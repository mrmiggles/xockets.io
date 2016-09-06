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
import java.util.Collections;
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
import com.tc.websocket.runners.Batch;
import com.tc.websocket.runners.BatchSend;
import com.tc.websocket.runners.EventQueueProcessor;
import com.tc.websocket.runners.QueueMessage;
import com.tc.websocket.runners.TaskRunner;
import com.tc.websocket.scripts.Script;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;
import com.tc.websocket.valueobjects.structures.IMultiMap;
import com.tc.websocket.valueobjects.structures.MultiMap;
import com.tc.websocket.valueobjects.structures.UriScriptMap;
import com.tc.websocket.valueobjects.structures.UriUserMap;
import com.tc.xpage.profiler.Stopwatch;




// TODO: Auto-generated Javadoc
/**
 * The Class DominoWebSocketServer.
 */
public class DominoWebSocketServer implements IDominoWebSocketServer, Runnable{

	/** The Constant cfg. */
	private static final IConfig cfg = Config.getInstance();
	
	/** The Constant URI_MAP. */
	private static final UriUserMap URI_MAP = new UriUserMap();
	
	/** The Constant SCRIPT_MAP. */
	private static final UriScriptMap SCRIPT_MAP = new UriScriptMap();
	
	/** The Constant VALID_USERS. */
	private static final IMultiMap<String,IUser> VALID_USERS=new MultiMap<String, IUser>(Config.getInstance().getMaxConnections() / 2);
	
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(DominoWebSocketServer.class.getName());
	
	/** The Constant OBSERVERS. */
	private static final Set<Script> OBSERVERS = Collections.synchronizedSet(new HashSet<Script>());
	
	/** The Constant INTERVALED. */
	private static final Set<Script> INTERVALED = Collections.synchronizedSet(new HashSet<Script>());

	
	/** The filter. */
	//set during server provisioning (see DominoWebSocketModule)
	private IWebsocketFilter filter;


	/** The boss group. */
	//netty boss thread, manages workers
	@Inject @Named(Const.GUICE_EVENTLOOP_BOSS)
	private EventLoopGroup bossGroup;

	/** The worker group. */
	//netty worker threads.
	@Inject @Named(Const.GUICE_EVENTLOOP_WORKER)
	private EventLoopGroup workerGroup;

	/** The init. */
	//netty WebSocketServerInitializer
	@Inject
	private WebSocketServerInitializer init;


	/** The guicer. */
	@Inject
	private IGuicer guicer;


	/** The user factory. */
	@Inject
	private IUserFactory userFactory;


	/** The on. */
	private AtomicBoolean on = new AtomicBoolean(false);
	
	/** The socket count. */
	private AtomicInteger socket_count = new AtomicInteger(0);



	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getWebSocketCount()
	 */
	@Override
	public int getWebSocketCount(){
		return socket_count.get();
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getWebSocketAndObserverCount()
	 */
	public int getWebSocketAndObserverCount(){
		return socket_count.get() + OBSERVERS.size();
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#decrementCount()
	 */
	@Override
	public int decrementCount(){
		return socket_count.decrementAndGet();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#setFilter(com.tc.websocket.filter.IWebsocketFilter)
	 */
	@Override
	public void setFilter(IWebsocketFilter filter){
		this.filter=filter;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getFilter()
	 */
	@Override
	public IWebsocketFilter getFilter(){
		return filter;
	}


	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#addUser(com.tc.websocket.valueobjects.IUser)
	 */
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


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#removeUser(java.lang.String)
	 */
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

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#removeUser(com.tc.websocket.valueobjects.IUser)
	 */
	@Override
	public void removeUser(IUser user){
		if(VALID_USERS.containsKey(user.getUserId())){
			this.removeUser(user.getUserId());

		}else if(VALID_USERS.containsKey(user.getSessionId())){
			this.removeUser(user.getSessionId());
		}
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getUsers()
	 */
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




	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getUsersOnThisServer()
	 */
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


	/**
	 * Instantiates a new domino web socket server.
	 */
	public DominoWebSocketServer() {
	}



	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#onOpen(com.tc.websocket.server.ContextWrapper, io.netty.handler.codec.http.FullHttpRequest)
	 */
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
			LOG.log(Level.SEVERE,"User is null.  Closing connection...");
			conn.channel().close();
			return;
		}

		String username = user.getUserId();

		//another anonymous check
		if(!cfg.isAllowAnonymous() && (username==null || user.isAnonymous())){
			LOG.log(Level.INFO,"anonymous not allowed");
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
		if(anonymousAdded){
			//this.notifyEventObservers(Const.ON_OPEN, user);
			return;
		}


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
		
		
		this.notifyEventObservers(Const.ON_OPEN, user);
		
		
	}


	/**
	 * Checks if is reader.
	 *
	 * @param user the user
	 * @return true, if is reader
	 */
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
				LOG.log(Level.INFO,"ACL level is " + level + " for user " + user.getUserId() +  " in database " + target.getFilePath());
				if(level<ACL.LEVEL_READER){
					LOG.log(Level.SEVERE, "User " + user.getUserId() + "  does not have permission to access " + target.getFilePath());
					b = false;
				}
				target.recycle();
			}
		}catch(NotesException n){
			LOG.log(Level.SEVERE,null , n);
		}finally{
			SessionFactory.closeSession(session);
		}
		return b;
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#onClose(com.tc.websocket.server.ContextWrapper)
	 */
	@Override
	public void onClose(ContextWrapper conn) {
		this.closeWithDelay(conn, 10);
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#closeWithDelay(com.tc.websocket.server.ContextWrapper, int)
	 */
	@Override
	public void closeWithDelay(ContextWrapper conn, int delay) {
		socket_count.decrementAndGet();
		final IUser user = this.resolveUser(conn);
		if(user!=null && ServerInfo.getInstance().isCurrentServer(user.getHost())){
			user.setGoingOffline(true);
			TaskRunner.getInstance().add(new ApplyStatus(user), delay);//mark user as offline
			
			
			
			TaskRunner.getInstance().add(new Runnable(){

				@Override
				public void run() {
					notifyEventObservers(Const.ON_CLOSE, user);
				}

			}, delay);
			
			
		}
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#onMessage(java.lang.String, java.lang.String)
	 */
	public boolean onMessage(String to, String json){
		boolean b = this.send(to, json);
		this.notifyEventObservers(Const.ON_MESSAGE, JSONUtils.toObject(json, SocketMessage.class));
		return b;
	}
	

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#onMessage(com.tc.websocket.server.ContextWrapper, java.lang.String)
	 */
	@Override
	public void onMessage(ContextWrapper conn, String json) {

		//we don't want to push a message that is too large
		if(!isValidSize(json)) return;

		String sessionId = this.resolveSessionId(conn);

		IUser user = VALID_USERS.get(sessionId);

		List<SocketMessage> messages = new ArrayList<SocketMessage>();

		if(this.isMessageCollection(json)){
			try {
				messages = JSONUtils.toList(json, SocketMessage.class);
				
			} catch (Exception e) {
				LOG.log(Level.SEVERE, null, e);
			} 
		}else{
			messages.add(JSONUtils.toObject(json, SocketMessage.class));
		}


		for(SocketMessage msg : messages){
			if(msg.hasMultipleTargets()){
				msg.addTarget(msg.getTo());
				List<String> targets = new ArrayList<String>();
				targets.addAll(msg.getTargets());
				for(String target : targets){
					msg.setTo(target);
					msg.getTargets().clear();
					this.processMessage(user, msg, JSONUtils.toJson(msg));
				}
			}else{
				this.processMessage(user, msg, JSONUtils.toJson(msg));
			}
		}
	}

	
	/**
	 * Checks if is message collection.
	 *
	 * @param json the json
	 * @return true, if is message collection
	 */
	private boolean isMessageCollection(String json){
		return json.startsWith(StringCache.OPEN_BRACKET) && json.endsWith(StringCache.CLOSE_BRACKET);
	}

	

	/**
	 * Process message.
	 *
	 * @param user the user
	 * @param msg the msg
	 * @param message the message
	 */
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
			LOG.log(Level.SEVERE,"Socket Message could not be created.");
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
		
		
		//notify the observers.
		this.notifyEventObservers(Const.ON_MESSAGE, msg);
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#onError(com.tc.websocket.server.ContextWrapper, java.lang.Exception)
	 */
	@Override
	public void onError(ContextWrapper conn, Exception ex) {
		LOG.log(Level.FINE,"***DominoWebSocketServer.onError***");
		if(conn!=null){
			Attribute<Object> att = conn.attr(AttributeKey.newInstance("resourceDescriptor"));
			LOG.log(Level.SEVERE,null,att.get().toString());
		}

		LOG.log(Level.SEVERE,null, ex);
		
		
		this.notifyEventObservers(Const.ON_ERROR, ex);
	}


	/**
	 * Resolve session id.
	 *
	 * @param conn the conn
	 * @return the string
	 */
	@Stopwatch
	private String resolveSessionId(ContextWrapper conn){
		int indexOf = conn.getResourceDescriptor().lastIndexOf(StringCache.FORWARD_SLASH);
		String token = conn.getResourceDescriptor().substring(indexOf + 1,conn.getResourceDescriptor().length());
		return token;
	}




	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#resolveUser(com.tc.websocket.server.ContextWrapper)
	 */
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


	/**
	 * On web socket pong.
	 *
	 * @param conn the conn
	 */
	public void onWebSocketPong(ContextWrapper conn){
		throw new UnsupportedOperationException("Unsupported");

	}


	/**
	 * Db.
	 *
	 * @param session the session
	 * @param path the path
	 * @return the database
	 */
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
			LOG.log(Level.SEVERE, null, n);
		}
		return db;
	}


	/**
	 * Resolve user id.
	 *
	 * @param user the user
	 * @return the string
	 */
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
			LOG.log(Level.SEVERE,null, n);
		}catch(Exception e){
			LOG.log(Level.SEVERE,null, e);
		}

		finally{
			SessionFactory.closeSession(session);
		}

		return set;
	}


	

	/**
	 * Calc batch size.
	 *
	 * @param users the users
	 * @return the int
	 */
	private int calcBatchSize(int users){
		int batch = users / 2;
		return batch < 1000 ? 500 : 1000;
	}

	/**
	 * Send.
	 *
	 * @param target the target
	 * @param json the json
	 * @return true, if successful
	 */
	private boolean send(String target, String json){

		boolean sent = false;

		if(target.startsWith(StringCache.FORWARD_SLASH)){
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



	/**
	 * Send to uri.
	 *
	 * @param uri the uri
	 * @param json the json
	 * @return true, if successful
	 */
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

		//now lets notify the scripts
		try{
			Collection<Script> scripts = SCRIPT_MAP.get(new RoutingPath(uri));
			for(Script script : scripts){
				TaskRunner.getInstance().add(script.copy(JSONUtils.toObject(json, SocketMessage.class)));
				b = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}


		return b;
	}



	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#pingUsers()
	 */
	@Override
	public void pingUsers(){
		//with netty may no longer be required.
	}



	
	
	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#broadcast(com.tc.websocket.valueobjects.SocketMessage)
	 */
	@Override
	public void broadcast( SocketMessage msg ) {
		this.queueMessage(msg);
		this.notifyEventObservers(Const.ON_MESSAGE, msg);
	}




	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#queueMessage(com.tc.websocket.valueobjects.SocketMessage)
	 */
	@Override
	@Stopwatch
	public void queueMessage(SocketMessage msg) {
		QueueMessage queueMessage = guicer.createObject(QueueMessage.class);
		queueMessage.setMsg(msg);
		TaskRunner.getInstance().add(queueMessage);
	}




	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#isValidSize(java.lang.String)
	 */
	@Override
	@Stopwatch
	public boolean isValidSize(String json){
		boolean b = true;
		if(json.length() > cfg.getMaxSize()){
			LOG.log(Level.SEVERE,"Message is larger than " + cfg.getMaxSize() + " bytes.  It will not be sent.");
			b = false;
		}
		return b;
	}




	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#removeAllUsers()
	 */
	@Override
	public void removeAllUsers() {
		VALID_USERS.clear();
	}




	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#setOn(boolean)
	 */
	@Override
	public void setOn(boolean b){
		this.on.set(b);
	}




	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#isOn()
	 */
	@Override
	public boolean isOn(){
		return this.on.get();
	}


	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.start();
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#start()
	 */
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
			LOG.log(Level.SEVERE,null, e);
		}
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#stop()
	 */
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
		OBSERVERS.clear();

		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();

		this.on.set(false);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#addEventObserver(com.tc.websocket.scripts.Script)
	 */
	@Override
	public void addEventObserver(Script script) {
		if(!OBSERVERS.contains(script)){
			OBSERVERS.add(script);
		}
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#removeEventObserver(com.tc.websocket.scripts.Script)
	 */
	public void removeEventObserver(Script script) {
		OBSERVERS.remove(script);
	}
	
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#containsObserver(com.tc.websocket.scripts.Script)
	 */
	public boolean containsObserver(Script script){
		return OBSERVERS.contains(script);
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#notifyEventObservers(java.lang.String, java.lang.Object[])
	 */
	@Override
	public synchronized void notifyEventObservers(String event, Object ...args) {
		Batch batch = new Batch();
		for(Script script : OBSERVERS){
			if(script.getFunction().equalsIgnoreCase(event)){
				batch.addRunner(guicer.inject(script.copy(args)));
			}
		}
		TaskRunner.getInstance().add(batch);
		
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#reloadScripts()
	 */
	@Override
	public void reloadScripts() {
		for(Script script : this.getAllScripts()){script.recompile(true);}
	}


	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getEventObservers()
	 */
	@Override
	public Collection<Script> getEventObservers() {
		return OBSERVERS;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#addUriListener(com.tc.websocket.scripts.Script)
	 */
	@Override
	public void addUriListener(Script script) {
		SCRIPT_MAP.add(script);
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#findUriListener(java.lang.String)
	 */
	@Override
	public Script findUriListener(String source) {
		Script script = null;
		for(Script s : SCRIPT_MAP.all()){
			if(source.equalsIgnoreCase(s.getSource())){
				script = s;
				break;
			}
		}
		return script;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#removeUriListener(com.tc.websocket.scripts.Script)
	 */
	@Override
	public void removeUriListener(Script script) {
		SCRIPT_MAP.remove(script);
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getUriListeners()
	 */
	@Override
	public Collection<Script> getUriListeners() {
		return SCRIPT_MAP.all();
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getAllScripts()
	 */
	@Override
	public Collection<Script> getAllScripts() {
		List<Script> list = new ArrayList<Script>(100);
		list.addAll(OBSERVERS);
		list.addAll(getUriListeners());
		list.addAll(getIntervaled());
		return list;
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#addIntervaled(com.tc.websocket.scripts.Script)
	 */
	@Override
	public void addIntervaled(Script script) {
		INTERVALED.add(guicer.inject(script));
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#removeIntervaled(com.tc.websocket.scripts.Script)
	 */
	@Override
	public void removeIntervaled(Script script) {
		INTERVALED.remove(script);
	}

	
	/* (non-Javadoc)
	 * @see com.tc.websocket.server.IDominoWebSocketServer#getIntervaled()
	 */
	@Override
	public Collection<Script> getIntervaled() {
		return INTERVALED;
	}


}
