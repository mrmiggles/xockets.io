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

import java.util.logging.Logger;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


public interface IScriptRunner {

	public void addToScope(String varName, Object o);
	public void addToScope(ScriptEngine engine, Object...args);
	
	public void removeFromScope(String varName);
	
	public void setLogger(Logger logger);
	
	public String getScriptKey();
	public void setScriptKey(String key);
	
	public void addPrefix(String ssjs);
	public Object evaluate(String ssjs);
	public Object executeFunction(String ssjs, String functionName, Object[] args);
	public CompiledScript compile(String ssjs) throws ScriptException;
	public boolean isValid(String ssjs);
	public String getError();
	public boolean hasError();

}