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


package com.tc.websocket.embeded.clients;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.tc.di.guicer.Guicer;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Activator;
import com.tc.websocket.Const;
import com.tc.websocket.ISSLFactory;
import com.tc.websocket.factories.IUserFactory;
import com.tc.websocket.queue.ApplyStatus;
import com.tc.websocket.queue.TaskRunner;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;

public class ScriptBuilder implements Runnable, IScriptBuilder {

	private static final Logger logger = Logger.getLogger(ScriptBuilder.class.getName());

	private String sessionId;
	private String userId;
	private String websocketUrl;
	private String runAsUser;
	private String runAsPassword;
	



	private RhinoClient client;
	private Map<String,String> scripts = new HashMap<String,String>();


	@Inject
	IDominoWebSocketServer server;

	@Inject
	IUserFactory factory;

	@Inject
	ISSLFactory sslFactory;



	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#addScript(java.lang.String, java.lang.String)
	 */
	@Override
	public void addScript(String event, String script){
		if(StrUtils.isEmpty(event) || StrUtils.isEmpty(script)){
			throw new IllegalArgumentException("Event and script cannot be null.");
		}
		scripts.put(event, script);
	}



	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#run()
	 */
	@Override
	public void run() {
		this.client = this.buildClient(this);
		try {
			client.connect();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE,null,e);
		}
	}

	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#getClient()
	 */
	@Override
	public IScriptClient getClient(){
		return this.client;
	}




	private RhinoClient buildClient(IScriptBuilder values){

		try{
			client = new RhinoClient(URI.create(values.getWebsocketUrl()));

			//inject dependencies.
			IGuicer guicer = Guicer.getInstance(Activator.BUNDLE);
			
			guicer.inject(this);
			
			guicer.inject(client);
			
			
			if(!StrUtils.isEmpty(this.runAsUser) && !StrUtils.isEmpty(this.runAsPassword)){
				logger.log(Level.INFO, values.getUserId() + " will run as " + this.runAsUser);
				client.runAsUser(this.runAsUser, this.runAsPassword);
			}


			//apply the user to the RhinoClient and to the server.
			IUser user = factory.createUser(sessionId, userId, Const.STATUS_ONLINE);
			client.setUser(user);

			//apply the user to the server.
			IUser copy = factory.createUser(user.getSessionId(), user.getUserId(), Const.STATUS_ONLINE);
			server.addUser(copy);

			//update the status of the doc
			ApplyStatus status = new ApplyStatus(copy);
			TaskRunner.getInstance().add(status);


			//add the scriptable events.
			for(String key : scripts.keySet()){
				Script script = new Script();
				script.setEvent(key);
				String javascript = scripts.get(key);

				//grab the script library
				if(javascript.startsWith(StringCache.FORWARD_SLASH)){
					script.setSource(javascript);
				}else{
					script.setSource(StringCache.EMPTY);
					script.setScript(javascript);
				}

				//add the script (client will pull in the javascript if its a path to a lib)
				client.addScript(script);
			}


		}catch(Exception e){
			logger.log(Level.SEVERE, null,e);
		}

		return client;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#getWebsocketUrl()
	 */
	@Override
	public String getWebsocketUrl() {
		return websocketUrl;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#setWebsocketUrl(java.lang.String)
	 */
	@Override
	public void setWebsocketUrl(String websocketUrl) {
		this.websocketUrl = websocketUrl;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#getSessionId()
	 */
	@Override
	public String getSessionId() {
		return sessionId;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#setSessionId(java.lang.String)
	 */
	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#getUserId()
	 */
	@Override
	public String getUserId() {
		return userId;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.embeded.clients.IScriptConfig#setUserId(java.lang.String)
	 */
	@Override
	public void setUserId(String userId) {
		this.userId = userId;
	}



	@Override
	public void setRunAsCreds(String runAsUser, String runAsPassword) {
		this.runAsUser=runAsUser;
		this.runAsPassword=runAsPassword;
	}

	


}
