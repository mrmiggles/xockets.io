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

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.guice.domino.module.SessionFactory;
import com.tc.utils.Base64;
import com.tc.utils.BundleUtils;
import com.tc.utils.ColUtils;
import com.tc.utils.DateUtils;
import com.tc.utils.DxlUtils;
import com.tc.utils.StopWatch;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.IConfig;
import com.tc.websocket.valueobjects.SocketMessage;


// TODO: Auto-generated Javadoc
/**
 * The Class Script.
 */
public abstract class Script implements Runnable {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(Script.class.getName());

	/** The uri. */
	private String uri;

	/** The wild. */
	private boolean wild;

	/** The function. */
	private String function;

	/** The script. */
	private String script;

	/** The source. */
	private String source;

	/** The to string. */
	private String toString;

	/** The interval. */
	private int interval;

	/** The last run. */
	private Date lastRun = new Date();

	/** The password. */
	protected String user, password;

	/** The args. */
	protected Object[] args;

	/** The guicer. */
	@Inject
	IGuicer guicer;


	/**
	 * Should run.
	 *
	 * @return true, if successful
	 */
	public boolean shouldRun(){
		boolean b = true;
		if(this.isIntervaled()){
			long secs = DateUtils.getTimeDiffSec(lastRun , new Date());
			b =  secs > interval;
		}
		return b;
	}

	/**
	 * Checks if is intervaled.
	 *
	 * @return true, if is intervaled
	 */
	public boolean isIntervaled(){
		return this.interval > 0;
	}


	/**
	 * Open session.
	 *
	 * @return the session
	 */
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

	/**
	 * Sets the arts.
	 *
	 * @param args the new arts
	 */
	public void setArts(Object ...args){
		this.args = args;
	}

	/**
	 * Close session.
	 *
	 * @param session the session
	 */
	protected void closeSession(Session session){
		SessionFactory.closeSession(session);
	}




	/**
	 * Sets the creds.
	 *
	 * @param user the user
	 * @param password the password
	 */
	public void setCreds(String user, String password){
		this.user = user;
		this.password = password;
	}

	/**
	 * Checks for creds.
	 *
	 * @return true, if successful
	 */
	public boolean hasCreds(){
		return this.user!=null && this.password!=null;
	}

	/**
	 * Gets the function.
	 *
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * Sets the function.
	 *
	 * @param event the new function
	 */
	public void setFunction(String event) {
		this.function = event;
	}

	/**
	 * Gets the script.
	 *
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * Sets the script.
	 *
	 * @param script the new script
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	public void setSource(String source) {
		this.source = source;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		if(toString == null){
			toString = (this.getUri() == null ? "" : this.getUri() + ".")  + this.getFunction() + "." + this.getSource();
		}
		return toString;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o){
		boolean b = false;
		if(o instanceof Script){
			b  = this.toString().equals(o.toString());
		}
		return b;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}



	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the uri.
	 *
	 * @param uri the new uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
		if(uri.endsWith(StringCache.STAR)){ //check for wildcard char.
			this.setWild(true);
			this.uri = this.uri.substring(0,this.uri.length() -1);
		}
	}

	/**
	 * Checks if is wild.
	 *
	 * @return true, if is wild
	 */
	public boolean isWild() {
		return wild;
	}

	/**
	 * Sets the wild.
	 *
	 * @param wild the new wild
	 */
	public void setWild(boolean wild) {
		this.wild = wild;
	}


	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	protected String getResource(){
		String resource = this.getSource().replace("/" + dbPath()  + "/", "");
		return resource;
	}


	/**
	 * Db path.
	 *
	 * @return the string
	 */
	protected String dbPath(){
		String path = this.getSource();
		String dbpath = path.substring(1, path.lastIndexOf(StringCache.DOT_NSF)) + ".nsf";
		return dbpath;	
	}

	/**
	 * Extract file.
	 *
	 * @return the string
	 */
	public synchronized String extractFile(){
		Session session = null;
		String script = null;
		try{
			session = this.openSession();
			Database db = session.getDatabase(StringCache.EMPTY, this.dbPath());

			if(!db.isOpen()){
				db.open();
			}

			byte[] byteMe = DxlUtils.findFileResource(db, this.getResource());
			script = new String(byteMe);

			//resolve dependencies.
			script = new ScriptAggregator(db).build(script);

		}catch(Exception n){
			LOG.log(Level.SEVERE, null, n);

		}finally{
			this.closeSession(session);
		}

		return script;
	}

	/**
	 * Extract script.
	 *
	 * @return the string
	 */
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

			//resolve all dependencies.
			script = new ScriptAggregator(db).build(script);

		} catch (NotesException e) {
			LOG.log(Level.SEVERE, null, e);

		}catch(Exception e){
			LOG.log(Level.SEVERE,null, e);

		}finally {
			SessionFactory.closeSession(session);
		}

		return script.replace("\0", "");
	}

	/**
	 * Copy.
	 *
	 * @param args the args
	 * @return the script
	 */
	public abstract Script copy(Object ...args);

	/**
	 * Recompile.
	 *
	 * @param reload the reload
	 * @return true, if successful
	 */
	public abstract boolean recompile(boolean reload);

	/**
	 * Sets the args.
	 *
	 * @param args the new args
	 */
	public void setArgs(Object ...args){
		this.args = args;
	}

	/**
	 * Gets the args.
	 *
	 * @return the args
	 */
	public Object[] getArgs(){
		return this.args;
	}


	/**
	 * Source.
	 *
	 * @param source the source
	 * @return the script
	 */
	public Script source(String source){
		this.source = source;
		return this;
	}

	/**
	 * Uri.
	 *
	 * @param uri the uri
	 * @return the script
	 */
	public Script uri(String uri){
		this.setUri(uri);
		return this;
	}

	/**
	 * Function.
	 *
	 * @param function the function
	 * @return the script
	 */
	public Script function(String function){
		this.function = function;
		return this;
	}

	/**
	 * Creds.
	 *
	 * @param user the user
	 * @param password the password
	 * @return the script
	 */
	public Script creds(String user, String password){
		this.setCreds(user, password);
		return this;
	}

	/**
	 * Interval.
	 *
	 * @param interval the interval
	 * @return the script
	 */
	public Script interval(int interval){
		this.interval = interval;
		return this;
	}



	/**
	 * New script.
	 *
	 * @param resource the resource
	 * @return the script
	 */
	public static Script newScript(String resource){
		Script script = null;
		String engine = SupportedEngine.findEngine(resource);

		if(SupportedEngine.AGENT.engine().equalsIgnoreCase(engine)){
			script = new AgentScript();

		}else if(SupportedEngine.PYTHON.engine().equalsIgnoreCase(engine)){
			script = new PythonScript();

		}else if(SupportedEngine.BEANSHELL.engine().equalsIgnoreCase(engine)){
			script= new BSHScript();
		}
		else{
			script = new JSRScript(engine);
		}

		script.setSource(resource);

		return script;
	}

	/**
	 * Checks if is calling itself.
	 *
	 * @return true, if is calling itself
	 */
	public boolean isCallingItself(){
		boolean b= false;
		if(this.isIntervaled() == false){
			for(Object o : this.getArgs()){
				if(o instanceof SocketMessage){
					SocketMessage msg = (SocketMessage) o;
					if(this.getSource().equalsIgnoreCase(msg.getFrom())){
						b = true;
						break;
					}
				}
			}
		}
		return b;
	}

	/**
	 * Prints the engines.
	 */
	public static void printEngines(){
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> engines = manager.getEngineFactories();
		for(ScriptEngineFactory engine : engines){
			System.out.println(engine.getEngineName());
		}
	}

	/**
	 * Gets the interval.
	 *
	 * @return the interval
	 */
	public synchronized int getInterval() {
		return interval;
	}

	/**
	 * Sets the interval.
	 *
	 * @param interval the new interval
	 */
	public synchronized void setInterval(int interval) {
		if(interval<=0) throw new IllegalArgumentException("interval must be greater than zero.");
		this.interval = interval;
	}

	/**
	 * Gets the last run.
	 *
	 * @return the last run
	 */
	public Date getLastRun() {
		return lastRun;
	}

	/**
	 * Sets the last run.
	 *
	 * @param lastRun the new last run
	 */
	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	/**
	 * Gets the common vars.
	 *
	 * @param session the session
	 * @return the common vars
	 */
	public Map<String,Object> getCommonVars(Session session){
		Map<String,Object> vars = new HashMap<String,Object>();
		vars.put(Const.FUNCTION, this.getFunction());
		vars.put(Const.VAR_SESSION, session);
		vars.put(Const.VAR_BUNDLE_UTILS, new BundleUtils());
		vars.put(Const.VAR_WEBSOCKET_CLIENT, guicer.inject(new SimpleClient(this)));
		vars.put(Const.VAR_TERM_SIGNAL, TermSignal.insta());
		vars.put(Const.VAR_CACHE, ScriptCache.insta());
		vars.put(Const.VAR_SCRIPT,new ScriptWrapper(this));
		vars.put(Const.VAR_B64,Base64.insta());
		vars.put(Const.VAR_STRUTILS,StrUtils.insta());
		vars.put(Const.VAR_COLUTILS, ColUtils.insta());
		vars.put(Const.VAR_STOPWATCH, new StopWatch());
		
		
		try{
			vars.put(Const.VAR_DB, session.getDatabase("", this.dbPath()));
		}catch(NotesException n){
			LOG.log(Level.SEVERE, null, n);
		}
		
		return vars;
	}


	public void toFile(){
		try{
			File file = File.createTempFile("tmp", ".txt");
			FileUtils.write(file, this.getScript());
			LOG.log(Level.SEVERE,"aggregate file has been created for debug");
			LOG.log(Level.SEVERE, file.getPath());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
