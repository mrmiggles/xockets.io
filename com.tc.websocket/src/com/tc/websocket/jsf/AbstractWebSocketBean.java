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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.google.inject.Inject;
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
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;

public abstract class AbstractWebSocketBean implements IWebSocketBean {

	private static final Logger logger = Logger.getLogger(AbstractWebSocketBean.class.getName());

	protected IDominoWebSocketServer server;


	@Inject
	protected IUserFactory userFactory;



	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#registerCurrentUser()
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
			logger.log(Level.SEVERE, null, e);
		}

	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#removeCurrentUser()
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



	/* (non-Javadoc)
	 * @see com.tc.websocket.jsf.IWebSocketBean#getWebSocketUrl()
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

	
	public void addObservers(String[] resources){
		for(String resource : resources){
			this.addEventObserver(resource);
		}
	}
	
	public void addEventObserver(String resource){
		for(String func : Const.ALL_EVENTS){
			this.addEventObserver(func, resource);
		}
	}

	
	public void addEventObserver(final String function, final String source){
		
		if(!this.containsObserver(function, source)){
			TaskRunner.getInstance().add(new Runnable(){

				@Override
				public void run() {
					Script script = Script.newScript(source)
							.source(source)
							.function(function)
							.creds("chatapp", "password");//testing.
					
					script.recompile(true);
					server.addEventObserver(script);
				}
			});
		}
	}

	public boolean containsObserver(final String function, final String source){
		Script script = Script.newScript(source);
		script.setSource(source);
		script.setFunction(function);
		return server.containsObserver(script);
	}


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



	@Override
	public boolean containsUriListener(String scriptPath){
		return this.containsScript(scriptPath);
	}

	@Override
	public boolean containsScript(String scriptPath){
		return server.findUriListener(scriptPath)!=null;
	}
	
	@Override
	public void addUriListeners(final String uri, final String[] sources){
		for(String source : sources){
			this.addUriListener(uri, source);
		}
	}

	@Override
	public void addUriListener(final String source){
		this.addUriListener(source, source, null, null);
	}

	@Override
	public void addUriListener(final String uri, final String source){
		this.addUriListener(uri, source, null, null);
	}

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
	

	public void removeUriListener(String source){
		server.removeUriListener(server.findUriListener(source));
	}


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
	

	@Override
	public void addIntervaledScripts(int interval, String[] sources) {
		for(String source : sources){
			this.addIntervaled(interval, source);
		}
	}

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

	@Override
	public void addIntervaled(int interval, String source) {
		this.addIntervaled(interval, source, null, null);
	}

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
	
	

}
