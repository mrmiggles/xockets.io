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


package com.tc.scriptrunner.runners;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;



public class RhinoRunner implements IScriptRunner {

	private static final Logger logger = Logger.getLogger(RhinoRunner.class.getName());

	private ScriptEngineManager manager;
	private ScriptEngine engine;
	private String error;
	private String scriptKey;

	@Override
	public String getError() {
		return error;
	} 

	public void setError(String error) {
		this.error = error;
	}

	public ScriptEngineManager getManager() {
		return manager;
	}

	public void setManager(ScriptEngineManager manager) {
		this.manager = manager;
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}


	@Override
	public CompiledScript compile(String ssjs) throws ScriptException {
		CompiledScript compiled=null;

		Compilable compilingEngine = (Compilable)engine;
		compiled = compilingEngine.compile(ssjs);

		return compiled;
	}

	@Override
	public Object evaluate(String ssjs) {

		this.setError(null);
		Object o = null;
		try {

			o = this.engine.eval(ssjs);

		} catch (ScriptException e) {
			this.error=e.getMessage();
			logger.log(Level.SEVERE,null,e);
		} 
		return o;
	}

	@Override
	public boolean isValid(String ssjs) {
		try {
			this.compile(ssjs);
		} catch (ScriptException e) {
			this.error=e.getMessage();
			logger.log(Level.SEVERE,null, e);
		}
		return !this.hasError();
	}

	@Override
	public boolean hasError(){
		return this.getError()!=null;
	}

	@Override
	public void addToScope(String varName, Object o) {
		engine.getBindings(ScriptContext.ENGINE_SCOPE).put(varName, o);
	}
	
	
	public void addToScope(ScriptEngine engine, Object...args){
		int cntr =0;
		String name = null;
		for(Object o : args){
			if(cntr % 2 ==0){
				name = (String)o;
			}else{
				engine.getContext().setAttribute(name, o, ScriptContext.ENGINE_SCOPE);
			}
			cntr++;
		}
	}

	@Override
	public void removeFromScope(String varName) {
		//engine.getBindings(ScriptContext.ENGINE_SCOPE).remove(varName);
		engine.getContext().removeAttribute(varName, ScriptContext.ENGINE_SCOPE);
	}

	@Override
	public Object executeFunction(String ssjs, String functionName, Object[] args) {
		Object o = null;
		try {
			//use the ssjs as a key.
			CompiledScript comp = this.compile(ssjs);

			comp.eval();

			Invocable invocable = (Invocable) comp.getEngine();

			if(args==null){
				invocable.invokeFunction(functionName);
			}else{
				invocable.invokeFunction(functionName, args);
			}

		} catch (ScriptException e) {
			this.error=e.getMessage();
			logger.log(Level.SEVERE,null,e);
		} catch (NoSuchMethodException e) {
			logger.log(Level.SEVERE,null,e);
		} 
		return o;
	}

	@Override
	public void setLogger(Logger scriptLogger) {
		this.addToScope("logger", scriptLogger);
	}

	@Override
	public String getScriptKey() {
		return scriptKey;
	}

	@Override
	public void setScriptKey(String scriptKey) {
		this.scriptKey = scriptKey;
	}

	@Override
	public void addPrefix(String ssjs) {
		// TODO Auto-generated method stub

	}




}
