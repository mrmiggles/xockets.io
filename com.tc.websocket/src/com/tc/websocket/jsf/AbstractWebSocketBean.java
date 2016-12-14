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

package com.tc.websocket.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.ibm.xsp.model.domino.DominoUtils;
import com.tc.utils.JSONUtils;
import com.tc.utils.StringCache;
import com.tc.utils.XSPUtils;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.IConfig;
import com.tc.websocket.factories.IUserFactory;
import com.tc.websocket.runners.ApplyStatus;
import com.tc.websocket.runners.SendMessage;
import com.tc.websocket.runners.TaskRunner;
import com.tc.websocket.scripts.Script;
import com.tc.websocket.server.ContextWrapper;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.server.IMessageSender;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;
import com.tc.websocket.valueobjects.structures.UriUserMap;

import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;


// TODO: Auto-generated Javadoc
/**
 * The Class AbstractWebSocketBean.
 */
public abstract class AbstractWebSocketBean implements IWebSocketBean, IMessageSender {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(AbstractWebSocketBean.class.getName());

	/** The server. */
	protected IDominoWebSocketServer server;


	/** The user factory. */
	@Inject
	protected IUserFactory userFactory;



	/**
	 * Register current user.
	 *
	 * @param req the req
	 * @throws NotesException the notes exception
	 */
	public void registerCurrentUser(HttpServletRequest req) throws NotesException{


		try{
			Session session = XSPUtils.session();
			String userName = session.getEffectiveUserName(); 
			String sessionId = req.getSession().getId();


			//don't allow it...
			if(!Config.getInstance().isAllowAnonymous() && StringCache.ANONYMOUS.equals(userName)){
				return;
			}

			//create the user and add to the server's in memory map
			IUser user = userFactory.createUser(sessionId, userName, Const.STATUS_ONLINE);
			server.addUser(user);


			//update the status of the doc
			ApplyStatus status = new ApplyStatus(user);
			TaskRunner.getInstance().add(status);
		}catch(Exception e){
			LOG.log(Level.SEVERE, null, e);
		}

	}

	/**
	 * Removes the current user.
	 *
	 * @param req the req
	 */

	public void removeCurrentUser(HttpServletRequest req){
		String sessionId = req.getSession().getId();
		server.removeUser(sessionId);
		ApplyStatus status = new ApplyStatus(userFactory.createUser(sessionId, StringCache.EMPTY, Const.STATUS_OFFLINE));
		TaskRunner.getInstance().add(status);

	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#getUsers()
	 */
	@Override
	public List<String> getUsers(){
		List<String> list = new ArrayList<String>();
		for(IUser user : server.getUsers()){
			list.add(user.getUserId());
		}
		return list;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#getOnlineUsers()
	 */
	@Override
	public List<SelectItem> getOnlineUsers() throws NotesException{
		List<SelectItem> list = new ArrayList<SelectItem>();
		for(String user : this.getUsers()){
			Name name = XSPUtils.session().createName(user);
			SelectItem select = new SelectItem(user,name.getAbbreviated());
			list.add(select);
			name.recycle();
		}
		return list;
	}
	
	
	@Override
	public List<SelectItem> getUsersByUri(String uri) throws NotesException{
		UriUserMap map = server.getUriUserMap();
		List<SelectItem> list = new ArrayList<SelectItem>();
		for(IUser user : map.get(uri)){
			ContextWrapper wrapper = user.findConnection(uri);
			if(wrapper!=null && wrapper.isOpen()){
				Name name = XSPUtils.session().createName(user.getUserId());
				SelectItem select = new SelectItem(user.getUserId(),name.getCommon());
				list.add(select);
				name.recycle();
			}
		}
		return list;
	}


	/**
	 * Gets the web socket url.
	 *
	 * @param req the req
	 * @return the web socket url
	 */
	public String getWebSocketUrl(HttpServletRequest req){

		IConfig cfg = Config.getInstance();

		//for ajax requests as we don't want the uri of the service call, but the calling page.
		String uri = req.getParameter(Const.SOURCE_URI);

		if(uri == null){
			uri = req.getRequestURI();
		}

		if(!uri.endsWith(StringCache.FORWARD_SLASH)){
			uri = uri + StringCache.FORWARD_SLASH;
		}

		String url = null;
		if(cfg.getPort() == 80 || cfg.getPort() == 443){
			url = req.getServerName() + StringCache.FORWARD_SLASH + Const.WEBSOCKET_URI + uri + req.getSession().getId();

		}else{
			url = req.getServerName() + StringCache.COLON + Config.getInstance().getPort() + StringCache.FORWARD_SLASH + Const.WEBSOCKET_URI + uri + req.getSession().getId();
		}

		if(Config.getInstance().isEncrypted()){
			url = Const.WSS + url;
		}else{
			url = Const.WS + url;
		}
		return url;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#getCustomWebSocketUrl(javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
	 */
	@Override
	public String getCustomWebSocketUrl(HttpServletRequest req, String sessionId, String sourceUri){
		return buildWebSocketUrl(req.getServerName(),sessionId, sourceUri);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#sendMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendMessage(String from, String to, String text){
		SocketMessage msg = new SocketMessage();
		msg.setDate(new Date());
		msg.setFrom(from);
		msg.setText(text);
		msg.setTo(to);
		this.sendMessage(msg);

	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#sendMessage(com.tc.websocket.valueobjects.SocketMessage)
	 */
	@Override
	public void sendMessage(SocketMessage msg){
		msg.setDate(new Date());
		TaskRunner.getInstance().add(new SendMessage(msg));
	}
	
	@Override
	public void sendMessageWithDelay(SocketMessage msg, int seconds){
		msg.setDate(new Date());
		TaskRunner.getInstance().add(new SendMessage(msg), seconds);
	}
	
	@Override
	public void sendMessage(String to, String text) {
		String from = null;
		try {
			from = DominoUtils.getCurrentSession().getEffectiveUserName();
		} catch (NotesException e) {
			LOG.log(Level.SEVERE, null, e);
		}
		this.sendMessage(new SocketMessage().to(to).text(text).from(from));
	}


	@Override
	public void sendMessage(String json) {
		this.sendMessage(JSONUtils.toObject(json, SocketMessage.class));
	}

	
	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addObservers(java.lang.String[])
	 */
	public void addObservers(String[] resources){
		for(String source : resources){
			this.addEventObserver(source);
		}
	}
	
	/**
	 * Adds the event observer.
	 *
	 * @param source the source
	 */
	public void addEventObserver(String source){
		for(String func : Const.ALL_EVENTS){
			this.addEventObserver(func, source);
		}
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addEventObserver(java.lang.String, java.lang.String)
	 */
	public void addEventObserver(final String function, final String source){
		
		if(!this.containsObserver(function, source)){
			TaskRunner.getInstance().add(new Runnable(){

				@Override
				public void run() {
					Script script = Script.newScript(source)
							.source(source)
							.function(function);
					
					script.recompile(true);
					server.addEventObserver(script);
				}
			});
		}
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#containsObserver(java.lang.String, java.lang.String)
	 */
	public boolean containsObserver(final String function, final String source){
		Script script = Script.newScript(source);
		script.setSource(source);
		script.setFunction(function);
		return server.containsObserver(script);
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#removeObserver(java.lang.String, java.lang.String)
	 */
	public void removeObserver(final String function, final String source){

		TaskRunner.getInstance().add(new Runnable(){

			@Override
			public void run() {
				Script script = Script.newScript(source);
				script.setSource(source);
				script.setFunction(function);
				server.removeEventObserver(script);
			}
		});

	}




	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#containsUriListener(java.lang.String)
	 */
	@Override
	public boolean containsUriListener(String scriptPath){
		return this.containsScript(scriptPath);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#containsScript(java.lang.String)
	 */
	@Override
	public boolean containsScript(String scriptPath){
		return server.findUriListener(scriptPath)!=null;
	}
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addUriListeners(java.lang.String, java.lang.String[])
	 */
	@Override
	public void addUriListeners(final String uri, final String[] sources){
		for(String source : sources){
			this.addUriListener(uri, source);
		}
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addUriListener(java.lang.String)
	 */
	@Override
	public void addUriListener(final String source){
		this.addUriListener(source, source, null, null);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addUriListener(java.lang.String, java.lang.String)
	 */
	@Override
	public void addUriListener(final String uri, final String source){
		this.addUriListener(uri, source, null, null);
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addUriListener(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addUriListener(final String uri, final String source, final String runAsUser, final String runAsPassword){
		
		TaskRunner.getInstance().add(new Runnable(){

			@Override
			public void run() {
				Script script = Script.newScript(source)
				.source(source)
				.function(Const.ON_MESSAGE)
				.creds(runAsUser, runAsPassword)
				.uri(uri);
				
				script.recompile(true);
				
				server.addUriListener(script);
				
			}
			
			
		});
		
		
	}
	


	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#removeUriListener(java.lang.String)
	 */
	public void removeUriListener(String source){
		server.removeUriListener(server.findUriListener(source));
	}


	/**
	 * Builds the web socket url.
	 *
	 * @param serverName the server name
	 * @param sessionId the session id
	 * @param sourceUri the source uri
	 * @return the string
	 */
	public static String buildWebSocketUrl(String serverName, String sessionId, String sourceUri){

		//for ajax requests as we don't want the uri of the service call, but the calling page.
		String uri = sourceUri;


		if(!uri.endsWith(StringCache.FORWARD_SLASH)){
			uri = uri + StringCache.FORWARD_SLASH;
		}

		String url = serverName + StringCache.COLON + Config.getInstance().getPort() + StringCache.FORWARD_SLASH + Const.WEBSOCKET_URI + uri + sessionId;
		if(Config.getInstance().isEncrypted()){
			url = Const.WSS + url;
		}else{
			url = Const.WS + url;
		}
		return url;
	}
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addIntervaledScripts(int, java.lang.String[])
	 */
	@Override
	public void addIntervaledScripts(int interval, String[] sources) {
		for(String source : sources){
			this.addIntervaled(interval, source);
		}
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addIntervaled(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void addIntervaled(final int interval, final String source,final String runAsUser,final String runAsPassword) {
		
		TaskRunner.getInstance().add(new Runnable(){

			@Override
			public void run() {
				Script script = Script.newScript(source)
				.source(source)
				.function(Const.ON_INTERVAL)
				.creds(runAsUser, runAsPassword)
				.interval(interval);
				script.recompile(true);
				server.addIntervaled(script);
			}
		});
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addIntervaled(int, java.lang.String)
	 */
	@Override
	public void addIntervaled(int interval, String source) {
		this.addIntervaled(interval, source, null, null);
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#removeIntervaled(java.lang.String)
	 */
	@Override
	public void removeIntervaled(final String source) {
		TaskRunner.getInstance().add(new Runnable(){
			@Override
			public void run() {
				Script script = Script.newScript(source)
				.source(source)
				.function(Const.ON_INTERVAL);
				server.removeIntervaled(script);
			}
		});
	}
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#addToScriptScope(java.lang.String, java.lang.Object)
	 */
	public <T> void addToScriptScope(String var){
		Object o = XSPUtils.getBean(var);
		o = XSPUtils.appScope().get(var);
		if(o!=null){
			Data.insta().put(XSPUtils.webPath(), var, o);
		}else{
			o = XSPUtils.sessionScope().get(var);
			String key = XSPUtils.webPath() + "/" + XSPUtils.getSessionId();
			Data.insta().put(key, var, o);
		}
	}


	
	@Deprecated
	public SocketMessage createSocketMessage(){
		return this.createMessage();
	}
	
	public SocketMessage createMessage(){
		return new SocketMessage().id(UUID.randomUUID().toString());
	}

}
