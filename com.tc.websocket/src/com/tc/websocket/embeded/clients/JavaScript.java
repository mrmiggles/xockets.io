package com.tc.websocket.embeded.clients;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptException;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tc.di.guicer.IGuicer;
import com.tc.guice.domino.module.SessionFactory;
import com.tc.scriptrunner.config.ScriptRunnerCfg;
import com.tc.scriptrunner.runners.IScriptRunner;
import com.tc.utils.BundleUtils;
import com.tc.utils.DxlUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.IConfig;
import com.tc.websocket.valueobjects.SocketMessage;

public class JavaScript extends Script implements Runnable{

	private IScriptRunner runner;
	private String function;
	private Object[] args;
	
	@Inject
	private IGuicer guicer;

	private static final Logger logger = Logger.getLogger(Script.class.getName());

	public String getFunction() {
		return function;
	}

	public void setFunction(String event) {
		this.function = event;
	}


	@Override
	public String toString(){
		return this.getFunction() + "." + this.getSource();
	}

	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		return this.toString().equals(o.toString());
	}


	public boolean recompile(){
		boolean b = false;
		try{
			if(this.getSource()!=null){
				this.setScript(this.extractScript(this.getSource()));
				this.setCompiled(runner.compile(this.getScript()));
				b = true;
			}
		}catch(ScriptException e){
			logger.log(Level.SEVERE, null, e);
		}


		return b;
	}
	
	private boolean isCallingItself(){
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

	public synchronized void execute() {
		
		if(this.isCallingItself()) return; //we don't want to accidentally recurse.

		Session session = null;
		IConfig cfg = Config.getInstance();
		session = SessionFactory.openSessionDefaultToTrusted(cfg.getUsername(),cfg.getPassword());
		try {
			
			this.runner.addToScope(this.getCompiled().getEngine(),
					Const.FUNCTION //name 
					,function//value
					,Const.RHINO_SESSION //name
					,session //value
					,Const.RHINO_BUNDLE_UTIL //name
					,new BundleUtils()//value
					,Const.RHINO_WEB_SOCKET_CLIENT,//name
					guicer.inject(new SimpleClient(this))//value
					);
			
			// execute the compiled script.
			if (getScript() == null){
				throw new IllegalArgumentException("Script cannot be null");
			}

			//eval the script.
			getCompiled().eval();
			
			Invocable invocable = (Invocable) getCompiled().getEngine();
			invocable.invokeFunction(function, args);
			

		} catch (Exception e) {
			logger.log(Level.SEVERE, null, e);
		} finally {

			SessionFactory.closeSession(session);
		}
	}
	
	

	private synchronized String extractScript(String path) {
		logger.log(Level.SEVERE,"extracting " + path);
		IConfig cfg = Config.getInstance();
		String script = null;

		String resource = path.substring(path.lastIndexOf(StringCache.FORWARD_SLASH) + 1,path.length());

		String finalpath = path.substring(1, path.lastIndexOf(StringCache.FORWARD_SLASH));

		Session session = SessionFactory.openSessionDefaultToTrusted(cfg.getUsername(),cfg.getPassword());
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

	@Inject
	public void setScriptRunner(@Named(ScriptRunnerCfg.RHINO_RUNNER) IScriptRunner runner) {
		this.runner = runner;
	}
	
	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public JavaScript copy(){
		JavaScript copy  = new JavaScript();
		copy.setArgs(this.getArgs());
		copy.setCompiled(this.getCompiled());
		copy.setEvent(this.getEvent());
		copy.setFunction(this.getFunction());
		copy.setScript(this.getScript());
		copy.setSource(this.getSource());
		copy.setScriptRunner(this.runner);
		return copy;
	}

	@Override
	public void run() {
		this.execute();
	}

}
