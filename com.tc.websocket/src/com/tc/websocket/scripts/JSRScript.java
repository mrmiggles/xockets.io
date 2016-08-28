package com.tc.websocket.scripts;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import lotus.domino.Session;

import com.google.inject.Inject;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.BundleUtils;
import com.tc.websocket.Const;

public class JSRScript extends Script{


	@Inject
	private IGuicer guicer;

	private static final Logger logger = Logger.getLogger(JSRScript.class.getName());

	private ScriptEngineManager manager;
	private ScriptEngine engine;
	private String engineName;
	private CompiledScript compiled;


	public JSRScript(String engineName){
		
		this.engineName = engineName;
		this.manager =new ScriptEngineManager();
		this.engine=manager.getEngineByName(engineName);
		
	}
	
	private JSRScript(){
		
	}


	public ScriptEngine getEngine() {
		return engine;
	}

	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}

	public synchronized boolean recompile(boolean reload){
		boolean b = true;
		try{
			if(this.getSource()!=null){

				if("js".equals(this.engineName)){
					this.setScript(reload ? this.extractScript() : this.getScript());
				}else{
					this.setScript(reload ? this.extractFile() : this.getScript());
				}
				
				try{
					Compilable compilingEngine = (Compilable)engine;
					this.setCompiled(compilingEngine.compile(this.getScript()));
				}catch(final Throwable ex){
					logger.log(Level.SEVERE, null, ex);
					b = false;
				}
			}
		}catch(Exception e){
			logger.log(Level.SEVERE,null, e);
			b = false;
		}
		return b;
	}

	
	public CompiledScript getCompiled() {
		return this.compiled;
	}
	
	
	public void setCompiled(CompiledScript compiled) {
		this.compiled = compiled;
	}

	public synchronized JSRScript copy(Object ...args){
		JSRScript copy  = new JSRScript();
		copy.setArgs(args);
		copy.setFunction(this.getFunction());
		copy.setScript(this.getScript());
		copy.setSource(this.getSource());
		copy.setCompiled(this.getCompiled());
		copy.setEngine(this.getEngine());
		//copy.recompile(true);
		copy.setCreds(user, password);
		return copy;
	}
	
	public Bindings buildBindings(ScriptEngine engine, Object...args){
		Bindings bindings = engine.createBindings();
		
		int cntr =0;
		String name = null;
		for(Object o : args){
			if(cntr % 2 ==0){
				name = (String)o;
			}else{
				//engine.getContext().setAttribute(name, o, ScriptContext.ENGINE_SCOPE);
				bindings.put(name, o);
			}
			cntr++;
		}
		return bindings;
	}

	@Override
	public void run() {
		
		if(this.isCallingItself()) return; //we don't want to accidentally recurse.

		Session session = this.openSession();

		try {

			//make sure args have all their dependencies
			for(Object o : args) {
				guicer.inject(o);
			}

			Bindings bindings = this.buildBindings(this.engine,
					Const.FUNCTION //name 
					,getFunction()//value
					,Const.RHINO_SESSION //name
					,session //value
					,Const.RHINO_BUNDLE_UTIL //name
					,new BundleUtils()//value
					,Const.RHINO_WEB_SOCKET_CLIENT//name
					,guicer.inject(new SimpleClient(this))//value
					);

			// execute the compiled script.
			if (getScript() == null){
				throw new IllegalArgumentException("Script cannot be null");
			}

			
			//create the new context for the bindings.
			ScriptContext myContext = new SimpleScriptContext();
			myContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
			
			//eval the script
			this.getCompiled().getEngine().setContext(myContext);
			this.getCompiled().eval();
			
			//execute the function
			Invocable invocable = (Invocable) this.getCompiled().getEngine();
			invocable.invokeFunction(this.getFunction(), args);
		

		}catch(ScriptException se){
			logger.log(Level.SEVERE,null, se);
			
		
		}catch (Exception e) {
			logger.log(Level.SEVERE, null, e);
			
		} finally {
			this.closeSession(session);
		}
	}

}
