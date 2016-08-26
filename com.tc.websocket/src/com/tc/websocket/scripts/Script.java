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


package com.tc.websocket.scripts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.DxlUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.IConfig;
import com.tc.websocket.valueobjects.SocketMessage;

public abstract class Script implements Runnable {
	
	private static final Logger logger = Logger.getLogger(Script.class.getName());
	
	public static final int OBSERVER=1;
	public static final int LISTENER=2;

	private String uri;
	private boolean wild;
	private String function;
	private String script;
	private String source;
	private String toString;
	protected String user, password;
	protected Object[] args;
	


	protected Session openSession(){
		Session session = null;
		IConfig cfg = Config.getInstance();
		
		if(hasCreds()){
			session = SessionFactory.openSessionDefaultToTrusted(user, password);
		}else{
			session = SessionFactory.openSessionDefaultToTrusted(cfg.getUsername(),cfg.getPassword());
		}
		return session;
	}
	
	public void setArts(Object ...args){
		this.args = args;
	}
	
	protected void closeSession(Session session){
		SessionFactory.closeSession(session);
	}
	



	public void setCreds(String user, String password){
		this.user = user;
		this.password = password;
	}
	
	public boolean hasCreds(){
		return this.user!=null && this.password!=null;
	}
	
	public String getFunction() {
		return function;
	}

	public void setFunction(String event) {
		this.function = event;
	}
	
	public String getScript() {
		return script;
	}
	
	public void setScript(String script) {
		this.script = script;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	
	@Override
	public String toString(){
		if(toString == null){
			toString = this.getUri() + "." + this.getFunction() + "." + this.getSource();
		}
		return toString;
	}
	
	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		return this.toString().equals(o.toString());
	}
	
	
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	

	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
		if(uri.endsWith(StringCache.STAR)){ //check for wildcard char.
			this.setWild(true);
			this.uri = this.uri.substring(0,this.uri.length() -1);
		}
	}
	
	public boolean isWild() {
		return wild;
	}

	public void setWild(boolean wild) {
		this.wild = wild;
	}
	
	
	protected String getResource(){
		String resource = this.getSource().replace("/" + dbPath()  + "/", "");
		return resource;
	}

	
	protected String dbPath(){
		String path = this.getSource();
		String dbpath = path.substring(1, path.lastIndexOf(StringCache.DOT_NSF)) + ".nsf";
		return dbpath;	
	}
	
	public synchronized String extractFile(){
		Session session = null;
		String script = null;
		try{
			session = this.openSession();
			Database db = session.getDatabase(StringCache.EMPTY, this.dbPath());
			byte[] byteMe = DxlUtils.findFileResource(db, this.getResource());
			script = new String(byteMe);
		}catch(Exception n){
			n.printStackTrace();
		}finally{
			this.closeSession(session);
		}
			
		return script;
	}
	
	public synchronized String extractScript() {
		String path = this.getSource();
		IConfig cfg = Config.getInstance();
		String script = null;

		String resource = path.substring(path.lastIndexOf(StringCache.FORWARD_SLASH) + 1,path.length());

		Session session = SessionFactory.openSessionDefaultToTrusted(cfg.getUsername(),cfg.getPassword());
		Database db = null;
		try {
			db = session.getDatabase(StringCache.EMPTY, dbPath());
			byte[] byteMe = DxlUtils.findSSJS(db, resource);
			script = new String(byteMe).trim();
		} catch (NotesException e) {
			logger.log(Level.SEVERE, null, e);

		} finally {
			SessionFactory.closeSession(session);
		}

		return script;
	}

	public abstract Script copy(Object ...args);
	
	public abstract boolean recompile(boolean reload);
	
	public void setArgs(Object ...args){
		this.args = args;
	}
	
	public Object[] getArgs(){
		return this.args;
	}
	

	public Script source(String source){
		this.source = source;
		return this;
	}
	
	public Script uri(String uri){
		this.setUri(uri);
		return this;
	}
	
	public Script function(String function){
		this.function = function;
		return this;
	}
	
	public Script creds(String user, String password){
		this.setCreds(user, password);
		return this;
	}
	

	
	
	public static Script newScript(String resource){
		Script script = null;
		String engine = SupportedEngine.findEngine(resource);
		
		if(SupportedEngine.AGENT.engine().equalsIgnoreCase(engine)){
			script = new AgentScript();
		
		}else if(SupportedEngine.PYTHON.engine().equalsIgnoreCase(engine)){
			script = new PythonScript();
		
		}else{
			script = new JSRScript(engine);
		}
		
		script.setSource(resource);
		
		return script;
	}
	
	public boolean isCallingItself(){
		boolean b= false;
		for(Object o : this.getArgs()){
			if(o instanceof SocketMessage){
				SocketMessage msg = (SocketMessage) o;
				if(this.getSource().equalsIgnoreCase(msg.getFrom())){
					b = true;
					break;
				}
			}
		}
		return b;
	}
	
	public static void printEngines(){
		ScriptEngineManager manager = new ScriptEngineManager();
        List<ScriptEngineFactory> engines = manager.getEngineFactories();
		for(ScriptEngineFactory engine : engines){
			System.out.println(engine.getEngineName());
		}
	}

}
