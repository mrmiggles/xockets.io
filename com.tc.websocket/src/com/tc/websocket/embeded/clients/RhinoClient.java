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

import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tc.guice.domino.module.SessionFactory;
import com.tc.scriptrunner.config.ScriptRunnerCfg;
import com.tc.scriptrunner.runners.IScriptRunner;
import com.tc.utils.BundleUtils;
import com.tc.utils.DxlUtils;
import com.tc.utils.JSONUtils;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.RhinoClientMap;
import com.tc.websocket.valueobjects.SocketMessage;

public class RhinoClient extends AbstractClient implements IScriptClient {

	
	private static final Config cfg = Config.getInstance();
	private static final RhinoClientMap clients = new RhinoClientMap();
	
	private String username;
	private String password;
	private boolean useCreds = false;
 
	public static final Logger logger = Logger.getLogger(RhinoClient.class.getName());

	private boolean error;

	private Map<String, Script> scripts = new ConcurrentHashMap<String, Script>();
	private IUser user;
	private IScriptRunner runner;
	
	@Inject
	private IClientCache cache;


	public RhinoClient( URI uri ) throws InterruptedException {
		super(uri);
	}

	@Override
	public void setUser(IUser user) {
		this.user = user;
	}


	@Override
	public IUser getUser() {
		return this.user;
	}


	@Override
	@Inject
	public void setScriptRunner(@Named(ScriptRunnerCfg.RHINO_RUNNER) IScriptRunner runner) {
		this.runner = runner;
	}


	@Override
	public void addToScope(String varName, Object o) {
		runner.addToScope(varName, o);
	}


	@Override
	public void removeFromScope(String varName) {
		runner.removeFromScope(varName);
	}


	@Override
	public void addScript(Script script) {

		if (!StrUtils.isEmpty(script.getSource())) {
			String javascript = this.extractScript(script.getSource());
			script.setScript(javascript);
		}

		CompiledScript compiled;
		try {
			compiled = runner.compile(script.getScript());
			script.setCompiled(compiled);
			logger.log(Level.INFO, "putting compiled script");

			if (StringCache.STAR.equals(script.getEvent())) {
				scripts.put(ON_OPEN, script);
				scripts.put(ON_MESSAGE, script);
				scripts.put(ON_CLOSE, script);
				scripts.put(ON_ERROR, script);
			} else {
				scripts.put(script.getEvent(), script);
			}

		} catch (ScriptException e) {
			logger.log(Level.SEVERE, null, e);
		}

	}


	@Override
	public void removeScriptByEvent(String event) {
		if (StringCache.STAR.equals(event)) {
			scripts.remove(ON_CLOSE);
			scripts.remove(ON_ERROR);
			scripts.remove(ON_MESSAGE);
			scripts.remove(ON_OPEN);

		} else {
			scripts.remove(event);
		}

	}

	@Override
	public void removeScriptByPath(String path) {
		List<String> events = new ArrayList<String>();
		for (Script script : scripts.values()) {
			if (script.getSource().equals(path)) {
				events.add(script.getEvent());
			}
		}

		for (String event : events) {
			this.removeScriptByEvent(event);
		}

	}


	@Override
	public void onMessage(String message) {
		SocketMessage socketMessage = JSONUtils.toObject(message,SocketMessage.class);

		// lets not let the messages get resent over and over...
		if (socketMessage.getFrom().equals(this.getUser().getUserId())) {
			return;
		}

		socketMessage.setJson(message);
		if (scripts.containsKey(ON_MESSAGE)) {
			logger.log(Level.INFO, "executing onMessage script");
			this.addToScope("socketMessage", socketMessage);
			this.execute(ON_MESSAGE, scripts.get(ON_MESSAGE).getCompiled());
		}
	}

	
	@Override
	public void onClose() {
		if (scripts.containsKey(ON_CLOSE)) {
			this.execute(ON_CLOSE, scripts.get(ON_CLOSE).getCompiled());
		}
	}


	
	@Override
	public void onOpen(WebSocketClientHandshaker handShaker) {
		this.addToScope("handShake", handShaker);
		if (scripts.containsKey(ON_OPEN)) {
			this.execute(ON_OPEN, scripts.get(ON_OPEN).getCompiled());
		}
	}

	
	@Override
	public void onError(Throwable ex) {
		logger.log(Level.SEVERE, null, ex);
		if (scripts.containsKey(ON_ERROR)) {
			logger.log(Level.INFO, "executing onError script");
			this.addToScope("ex", ex);
			this.execute(ON_ERROR, scripts.get(ON_ERROR).getCompiled());
		}

	}

	private synchronized void execute(String event, CompiledScript script) {
		
		Session session = null;
		if(this.useCreds){
			session = SessionFactory.openSession(username, password);
		}else{
			session = SessionFactory.openSession(cfg.getUsername(),cfg.getPassword());
		}
		
		try {
			
			//cache for temporary storage between invocations
			this.addToScope("cache", cache);

			// add the server session to scope.
			this.addToScope("event", event);

			// Domino session to gain access to Domino data
			this.addToScope("session", session);

			// RhinoClient to send messages after server side processing.
			this.addToScope("websocketClient", this);

			// to load external osgi plugin
			this.addToScope("bundleUtils", new BundleUtils());

			// execute the compiled script.
			if (script == null)
				throw new IllegalArgumentException("Script cannot be null");

			// run the script
			script.eval();

			// reset the error flag
			error = false;

		} catch (Exception e) {
			error = true;
			logger.log(Level.SEVERE, null, e);
		} finally {
			this.removeFromScope("session");
			SessionFactory.closeSession(session);
		}
	}


	@Override
	public boolean hasError() {
		return error;
	}

	public static boolean containsClient(String uri) {
		List<RhinoClient> list = clients.get(uri);
		return !list.isEmpty();
	}

	public synchronized static void deregisterClients(String uri) {
		List<RhinoClient> list = clients.get(uri);
		for (RhinoClient client : list) {
			// client.close();
			clients.remove(client);
		}
	}

	private String extractScript(String path) {
		String script = null;
		String resource = path.substring(path.lastIndexOf('/') + 1,
				path.length());
		String finalpath = path.substring(1, path.lastIndexOf('/'));

		Session session = SessionFactory.openSession(cfg.getUsername(),cfg.getPassword());
		Database db = null;
		try {
			db = session.getDatabase(StringCache.EMPTY, finalpath);
			byte[] byteMe = DxlUtils.findSSJS(db, resource);
			script = new String(byteMe).trim();
		} catch (NotesException e) {
			logger.log(Level.SEVERE, null, e);

		} finally {
			SessionFactory.closeSession(session);
		}

		return script;
	}

	@Override
	public Collection<Script> getScripts() {
		List<Script> list = new ArrayList<Script>();
		for (Script script : scripts.values()) {
			if (!list.contains(script)) {
				list.add(script);
			}
		}
		return list;
	}


	@Override
	public void reloadScripts() {
		// store all the scripts in a new collection;
		List<Script> list = new ArrayList<Script>();
		list.addAll(scripts.values());

		// now reload them.
		for (Script script : list) {
			if (!StrUtils.isEmpty(script.getSource())) {
				String javascript = extractScript(script.getSource());
				script.setScript(javascript);

				// adding will rebuild the compiled script.
				this.addScript(script);
			}
		}
	}

	public static List<RhinoClient> getAllClients() {
		return clients.getAll();
	}

	public SocketMessage createMessage() {
		return new SocketMessage();
	}

	public void sendMsg(SocketMessage msg) {
		msg.setFrom(this.getUser().getUserId());
		super.send(JSONUtils.toJson(msg));
	}
	

	@Override
	public void runAsUser(String sessionUsername, String sessionPassword) {
		this.username=sessionUsername;
		this.password=sessionPassword;
		this.useCreds=true;
	}






}
