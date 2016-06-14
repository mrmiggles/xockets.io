/*
 * © Copyright Tek Counsel LLC 2016
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */


package com.tc.websocket.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.ACL;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tc.di.guicer.IGuicer;
import com.tc.guice.domino.module.ServerInfo;
import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.BundleUtils;
import com.tc.utils.JSONUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Activator;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.IConfig;
import com.tc.websocket.factories.IUserFactory;
import com.tc.websocket.filter.IWebsocketFilter;
import com.tc.websocket.runners.ApplyStatus;
import com.tc.websocket.runners.BatchSend;
import com.tc.websocket.runners.EventQueueProcessor;
import com.tc.websocket.runners.QueueMessage;
import com.tc.websocket.runners.TaskRunner;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;
import com.tc.websocket.valueobjects.SocketMessageLite;
import com.tc.websocket.valueobjects.structures.IMultiMap;
import com.tc.websocket.valueobjects.structures.MultiMap;
import com.tc.websocket.valueobjects.structures.UriMap;
import com.tc.xpage.profiler.Stopwatch;



public class DominoWebSocketServer implements IDominoWebSocketServer, Runnable{

	private static final IConfig cfg = Config.getInstance();
	private static final UriMap URI_MAP = new UriMap();
	private static IMultiMap<String,IUser> VALID_USERS=new MultiMap<String, IUser>(Config.getInstance().getMaxConnections() / 2);
	private static final Logger logger = Logger.getLogger(DominoWebSocketServer.class.getName());
	


	//set during server provisioning (see DominoWebSocketModule)
	private IWebsocketFilter filter;


	//netty boss thread, manages workers
	@Inject @Named(Const.GUICE_EVENTLOOP_BOSS)
	private EventLoopGroup bossGroup;

	//netty worker threads.
	@Inject @Named(Const.GUICE_EVENTLOOP_WORKER)
	private EventLoopGroup workerGroup;

	//netty WebSocketServerInitializer
	@Inject
	private WebSocketServerInitializer init;
	

	@Inject
	private IGuicer guicer;


	@Inject
	private IUserFactory userFactory;


	private AtomicBoolean on = new AtomicBoolean(false);
	private AtomicInteger socket_count = new AtomicInteger(0);

	

	@Override
	public int getWebSocketCount(){
		return socket_count.get();
	}


	@Override
	public int decrementCount(){
		return socket_count.decrementAndGet();
	}


	@Override
	public void setFilter(IWebsocketFilter filter){
		this.filter=filter;
	}

	@Override
	public IWebsocketFilter getFilter(){
		return filter;
	}


	@Override
	public void addUser(IUser user){

		IUser storedUser = VALID_USERS.get(user.getSessionId());

		//lets only add the user if they aren't present...
		if(user.isValid() && storedUser == null){

			//clear out any closed connections.
			user.clear();

			//store a reference in the map for sessionId, and userId.
			VALID_USERS.putWithKeys(user, user.getSessionId(),user.getUserId());

		}else if(storedUser!=null && !user.isAnonymous()){
			VALID_USERS.removeWithKeys(storedUser.getUserId(),storedUser.getSessionId());
			storedUser.setUserId(user.getUserId());
			VALID_USERS.putWithKeys(storedUser, storedUser.getSessionId(),storedUser.getUserId());

		}
	}


	@Override
	public void removeUser(String key){
		IUser user = VALID_USERS.get(key);


		//be sure to close the socket connection as the user's Domino session expires.
		if(user!=null && user.count() == 0){
			user.setGoingOffline(true);
			user.clear();
		}

		//cleanup all references.
		if(user!=null){
			VALID_USERS.removeWithKeys(user.getSessionId(), user.getUserId());
			//cleanup urimap
			URI_MAP.remove(user);

		}
	}

	@Override
	public void removeUser(IUser user){
		if(VALID_USERS.containsKey(user.getUserId())){
			this.removeUser(user.getUserId());

		}else if(VALID_USERS.containsKey(user.getSessionId())){
			this.removeUser(user.getSessionId());
		}
	}

	@Override
	@Stopwatch
	public Collection<IUser> getUsers(){
		Map<String, IUser> map = new HashMap<String, IUser>(VALID_USERS.size());
		for(IUser user : VALID_USERS.values()){
			if(user.getStatus().equals(Const.STATUS_ONLINE)){
				map.put(user.getUserId(), user);
			}
		}
		return map.values();
	}

	
	

	@Override
	@Stopwatch(time=10)
	public Collection<IUser> getUsersOnThisServer(){

		final Map<String,IUser> map = new HashMap<String,IUser>(VALID_USERS.size());
		for(IUser user : VALID_USERS.values()){
			if(user.canReceive()){
				map.put(user.getUserId(), user);
			}
		}
		return map.values();
	}


	public DominoWebSocketServer() {
	}



	@Override
	public void onOpen(ContextWrapper conn, FullHttpRequest req) {

		conn.setResourceDescriptor(req.uri());

		this.socket_count.incrementAndGet();

		IUser user = this.resolveUser(conn);

		//make sure anonymous users aren't added twice to the websocket.nsf
		boolean anonymousAdded = false;

		//if we're running in test mode, or allowing anonymous connections, we create the user inside this method
		if((cfg.isTestMode() || cfg.isAllowAnonymous()) && user == null){
			String sessionId = this.resolveSessionId(conn);
			user = userFactory.createUser(sessionId, sessionId, Const.STATUS_ONLINE);
			TaskRunner.getInstance().add(new ApplyStatus(user));
			this.addUser(user);
			anonymousAdded = true;
		}


		//check for null.
		if (user == null){
			logger.log(Level.SEVERE,"User is null.  Closing connection...");
			conn.channel().close();
			return;
		}

		String username = user.getUserId();

		//another anonymous check
		if(!cfg.isAllowAnonymous() && (username==null || user.isAnonymous())){
			logger.log(Level.INFO,"anonymous not allowed");
			conn.channel().close();
			return;

		}



		//apply / overwrite the existing websocket reference
		user.setConn(conn);


		//set the status to online immediately  
		user.setStatus(Const.STATUS_ONLINE);


		//add the user to the UriMap (one to many map where URI is the key);
		URI_MAP.add(user);


		//now that we have the user fully initialized, make sure they have at least reader access
		if(!user.getUserId().startsWith(Const.RHINO_PREFIX) && !this.isReader(user)){
			user.getConn().close();
			return;
		}


		//break out since the anonymous user was already added previously.
		if(anonymousAdded) return;


		//update the user data in the corresponding doc.
		if(!user.getUserId().startsWith(Const.RHINO_PREFIX)){
			user.setGoingOffline(false);
			TaskRunner.getInstance().add(new ApplyStatus(user));
		}

		//check to see if there's any messages waiting on this event for this user.
		EventQueueProcessor openEvent = guicer.createObject(EventQueueProcessor.class);
		openEvent.setEventQueue(Const.VIEW_ON_OPEN_QUEUE);
		openEvent.setTarget(username);
		guicer.inject(openEvent);//inject dependencies.
		openEvent.run(); //run it in this thread
	}


	private boolean isReader(IUser user){
		boolean b = true;
		//make sure the user has access to the database referenced in the URI.
		Session session = SessionFactory.openSessionDefaultToTrusted(cfg.getUsername(),cfg.getPassword());
		try{
			if(user.getUri().toLowerCase().contains(StringCache.DOT_NSF)){
				Database target = this.db(session, new RoutingPath(user.getUri()));
				int level = -1;
				if(user.isAnonymous()){
					level = target.queryAccess(StringCache.ANONYMOUS);
				}else{
					level= target.queryAccess(user.getUserId());
				}
				logger.log(Level.INFO,"ACL level is " + level + " for user " + user.getUserId() +  " in database " + target.getFilePath());
				if(level<ACL.LEVEL_READER){
					logger.log(Level.SEVERE, "User " + user.getUserId() + "  does not have permission to access " + target.getFilePath());
					b = false;
				}
				target.recycle();
			}
		}catch(NotesException n){
			logger.log(Level.SEVERE,null , n);
		}finally{
			SessionFactory.closeSession(session);
		}
		return b;
	}


	@Override
	public void onClose(ContextWrapper conn) {
		this.closeWithDelay(conn, 10);
	}


	@Override
	public void closeWithDelay(ContextWrapper conn, int delay) {
		socket_count.decrementAndGet();
		IUser user = this.resolveUser(conn);
		if(user!=null && ServerInfo.getInstance().isCurrentServer(user.getHost())){
			user.setGoingOffline(true);
			TaskRunner.getInstance().add(new ApplyStatus(user), delay);//mark user as offline
		}
	}


	@Override
	public void onMessage(ContextWrapper conn, String message) {

		//we don't want to push a message that is too large
		if(!isValidSize(message)) return;

		String sessionId = this.resolveSessionId(conn);

		IUser user = VALID_USERS.get(sessionId);

		SocketMessage msg =JSONUtils.toObject(message, SocketMessageLite.class);
		
		if(msg.hasMultipleTargets()){
			//just in case use also set the to field
			msg.addTarget(msg.getTo());
			
			SocketMessage fullMsg =JSONUtils.toObject(message, SocketMessage.class);
			List<String> targets = new ArrayList<String>();
			targets.addAll(fullMsg.getTargets());
			for(String target : targets){
				fullMsg.setTo(target);
				fullMsg.getTargets().clear();
				this.processMessage(user, fullMsg, JSONUtils.toJson(fullMsg));
			}
		}else{
			this.processMessage(user, msg, message);
		}

	}
	
	
	private void processMessage(IUser user, SocketMessage msg , String message){
		if(msg!=null && !msg.getTo().startsWith(StringCache.FORWARD_SLASH)){
			if(user == null || user.isAnonymous()) {
				if(!cfg.isAllowAnonymous()){
					user.close();
					return;
				}
			}
		}



		//if msg fails to be created by json parser handle the null object
		if(msg == null){
			logger.log(Level.SEVERE,"Socket Message could not be created.");
			return;
		}
		
		if(msg.isDurable()){
			this.queueMessage(JSONUtils.toObject(message, SocketMessage.class));
			return;
		}


		if(user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}

		if(!user.getUserId().equals(msg.getFrom())){
			throw new IllegalArgumentException("Invalid value in from. from must equal current user's Id " + user.getUserId() + " " + msg.getFrom());
		}


		//added for page / uri routing.
		if(msg.getTo().startsWith(StringCache.FORWARD_SLASH)){
			this.sendToUri(msg.getTo(), message);

		}else{
			//only send direct if on the same host, else queue it up.
			IUser targetUser = this.resolveUser(msg.getTo());
			if(targetUser!=null && ServerInfo.getInstance().isCurrentServer(targetUser.getHost())){
				this.send(msg.getTo(), message);
			}else{
				//make sure we commit the full message.
				this.queueMessage(JSONUtils.toObject(message, SocketMessage.class));
			}
		}




		//check to see if there's any messages waiting on this event for the sender.

		EventQueueProcessor sendEvent = guicer.createObject(EventQueueProcessor.class);
		sendEvent.setEventQueue(Const.VIEW_ON_SEND_MSG);
		sendEvent.setTarget(msg.getFrom());
		guicer.inject(sendEvent);//inject dependencies.
		sendEvent.run(); //we don't want to execute in a separate thread.


		//check to see if there's any messages waiting on this event for receiver
		EventQueueProcessor receiveEvent = guicer.createObject(EventQueueProcessor.class);
		receiveEvent.setEventQueue(Const.VIEW_ON_RECEIVE_MSG);
		receiveEvent.setTarget(msg.getTo());
		guicer.inject(receiveEvent);//inject dependencies.
		receiveEvent.run(); //we don't want to execute in a separate thread.
	}



	@Override
	public void onError(ContextWrapper conn, Exception ex) {
		logger.log(Level.FINE,"***DominoWebSocketServer.onError***");
		if(conn!=null){
			Attribute<Object> att = conn.attr(AttributeKey.newInstance("resourceDescriptor"));
			logger.log(Level.SEVERE,null,att.get().toString());
		}

		logger.log(Level.SEVERE,null, ex);
	}


	@Stopwatch
	private String resolveSessionId(ContextWrapper conn){
		int indexOf = conn.getResourceDescriptor().lastIndexOf(StringCache.FORWARD_SLASH);
		String token = conn.getResourceDescriptor().substring(indexOf + 1,conn.getResourceDescriptor().length());
		return token;
	}



	@Override
	@Stopwatch
	public IUser resolveUser(ContextWrapper conn){
		return VALID_USERS.get(this.resolveSessionId(conn));
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#resolveUser(java.lang.String)
	 */

	@Override
	@Stopwatch
	public IUser resolveUser(String key){
		return VALID_USERS.get(key);
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#containsUser(java.lang.String)
	 */

	@Override
	@Stopwatch
	public boolean containsUser(String key){
		return VALID_USERS.containsKey(key);
	}


	public void onWebSocketPong(ContextWrapper conn){
		throw new UnsupportedOperationException("Unsupported");

	}


	private Database db(Session session, RoutingPath path) {
		if(path.getDbPath()==null) return null;
		Database db = null;
		try{
			String dbPath = path.getDbPath();
			int start = dbPath.indexOf('/');
			if(start==0){
				dbPath = dbPath.substring(1,dbPath.length());
			}

			db = session.getDatabase(StringCache.EMPTY,dbPath);
			if(!db.isOpen()){
				db.open();
			}
		}catch(NotesException n){
			logger.log(Level.SEVERE, null, n);
		}
		return db;
	}


	private String resolveUserId(IUser user){
		if(user.getSessionId().startsWith(Const.RHINO_PREFIX)){
			return ServerInfo.getInstance().getServerName();
		}
		return user.getUserId();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getUsersByUri(java.lang.String)
	 */

	@Override
	@SuppressWarnings("unchecked")
	@Stopwatch
	public Collection<IUser> getUsersByUri(String uri){
		Set<IUser> set = new HashSet<IUser>();

		RoutingPath path = new RoutingPath(uri);

		//lets try to reduce the initial collection size.
		Collection<IUser> col = URI_MAP.get(path);
		if(col.isEmpty()) {
			//pull in everybody in case we have a wildcard i.e. /*
			col = this.getUsers();
		}

		Database db = null;
		Session session = SessionFactory.openSession(cfg.getUsername(),cfg.getPassword());
		try{
			db = this.db(session, path);
			uri = uri.replace(StringCache.STAR, StringCache.EMPTY);

			//probably not the fastest approach, need to re-visit.
			for(IUser user : col){

				if(user.isGoingOffline() || user.isOpen()==false) continue;

				if(path.isWild() && user.startsWith(path.getUri())){
					if(path.hasRoles()){
						//role filters were supplied and the user is part of one of the roles
						if(path.isMember(db.queryAccessRoles(this.resolveUserId(user)))){
							set.add(user);
						}

					}else{
						//no role filters were supplied, just send to the uri
						set.add(user);
					}

				}else if(user.containsUri(path.getUri())){
					if(path.hasRoles()){
						if(path.isMember(db.queryAccessRoles(this.resolveUserId(user)))){
							set.add(user);
						}

					}else{
						//just use the uri
						set.add(user);
					}
				}
			}
		}catch(NotesException n){
			logger.log(Level.SEVERE,null, n);
		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
		}

		finally{
			SessionFactory.closeSession(session);
		}

		return set;
	}


	private int calcBatchSize(int users){
		int size = users / 2;
		if (size < 500){
			size = 500;
		}
		return size;
	}

	@Override
	public boolean send(String target, String json){

		boolean sent = false;

		if(target.startsWith(StringCache.FORWARD_SLASH)){
			sent = true;
			return this.sendToUri(target, json);
		}

		if(!isValidSize(json)) return sent;

		//apply the json data filter
		if(this.filter!=null){
			json = this.filter.applyFilter(json);
		}

		//send to all users on this server if target === current server.
		if(ServerInfo.getInstance().isCurrentServer(target)){
			int cntr = 0;
			Collection<IUser> col = this.getUsersOnThisServer();
			int batchSize = this.calcBatchSize(col.size());
			BatchSend batchSend = new BatchSend();
			batchSend.setMessage(json);
			for(IUser user : col){
				if(user!=null && user.canReceive()){
					batchSend.addUser(user);
					if(cntr % batchSize ==0){
						TaskRunner.getInstance().add(batchSend);
						batchSend =new BatchSend();
						batchSend.setMessage(json);
					}

					//user.send(json);
					sent = true;
					cntr ++;
				}//end if
				
			}//end of for loop.
			
			if(batchSend.count() < batchSize){
				TaskRunner.getInstance().add(batchSend);
			}//end if
		}
		else{
			IUser user = VALID_USERS.get(target);
			if(user!=null && user.canReceive()){
				sent = true;
				user.send(json);

			}else if(user!=null && user.getConn()==null && !user.isGoingOffline()){
				//queue up for REST service
				sent = true;
				this.queueMessage(JSONUtils.toObject(json, SocketMessage.class));
			}
		}
		
		
		return sent;
	}



	@Stopwatch
	private boolean sendToUri(String uri, String json){
		boolean b = false;
		Collection<IUser> list = this.getUsersByUri(uri);
		if(list!=null && !list.isEmpty()){
			for(IUser user : list){
				//make sure we don't recurse forever
				if(!user.getUserId().startsWith(StringCache.FORWARD_SLASH)){
					if(this.send(user.getUserId(), json)){
						b = true; //if the message was sent to anyone in the uri we're consider it sent.
					}
				}
			}
		}
		return b;
	}



	@Override
	public void pingUsers(){
		//with netty may no longer be required.
	}



	@Override
	public void broadcast( SocketMessage msg ) {
		this.queueMessage(msg);
	}




	@Override
	@Stopwatch
	public void queueMessage(SocketMessage msg) {
		QueueMessage queueMessage = guicer.createObject(QueueMessage.class);
		queueMessage.setMsg(msg);
		TaskRunner.getInstance().add(queueMessage);
	}




	@Override
	@Stopwatch
	public boolean isValidSize(String json){
		boolean b = true;
		if(json.length() > cfg.getMaxSize()){
			logger.log(Level.SEVERE,"Message is larger than " + cfg.getMaxSize() + " bytes.  It will not be sent.");
			b = false;
		}
		return b;
	}




	@Override
	public void removeAllUsers() {
		VALID_USERS.clear();
	}



	@Override
	public void setOn(boolean b){
		this.on.set(b);
	}



	@Override
	public boolean isOn(){
		return this.on.get();
	}


	@Override
	public void run() {
		this.start();
	}


	@Override
	public synchronized void start(){

		if(this.isOn()) return;

		try{
			try {
				ServerBootstrap boot = new ServerBootstrap();
				
				if(cfg.isNativeTransport()){
					 boot.channel(EpollServerSocketChannel.class);
				}else{
					boot.channel(NioServerSocketChannel.class);
				}
				
	
				boot.group(bossGroup, workerGroup)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024,  32 * 1024))
				.childOption(ChannelOption.SO_SNDBUF, cfg.getSendBuffer())
				.childOption(ChannelOption.SO_RCVBUF, cfg.getReceiveBuffer())
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childHandler(init);


				//bind to the main port
				boot.bind(cfg.getPort()).sync();
				
				//bind to the redirect port (e.g. 80 will redirect to 443)
				for(Integer port : cfg.getRedirectPorts()){
					ChannelFuture f = boot.bind(port);
					f.sync();
				}
				
				this.on.set(true);

				String version = BundleUtils.getVersion(Activator.bundle);
				String name = BundleUtils.getName(Activator.bundle);
				cfg.print(name + " ready and listening on " + cfg.getPort() + " running version " + version);

			} finally {


			}
		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
		}
	}



	@Override
	public synchronized void stop(){
		for(IUser user : this.getUsers()){
			if(user.isOpen()){
				user.getConn().channel().close();
				user.getConn().channel().parent().close();
			}
		}


		VALID_USERS.clear();
		URI_MAP.clear();

		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		this.on.set(false);
	}


}
