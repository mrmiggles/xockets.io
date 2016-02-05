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


package com.tc.scriptrunner.guice;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.tc.scriptrunner.config.ScriptRunnerCfg;
import com.tc.scriptrunner.runners.IScriptRunner;
import com.tc.scriptrunner.runners.RhinoRunner;


public class ScriptRunnerModule extends AbstractModule {

	private static final Logger logger = Logger.getLogger(ScriptRunnerModule.class.getName());

	@Override
	protected void configure() {
		bind(IScriptRunnerFactory.class).to(ScriptRunnerFactory.class).in(Singleton.class);
	} 


	@Provides
	@Named(ScriptRunnerCfg.RHINO_RUNNER)
	public IScriptRunner provideScriptRunner(){

		RhinoRunner iscript =new RhinoRunner();

		ScriptEngineManager manager =new ScriptEngineManager();

		ScriptEngine engine=manager.getEngineByName("js");

		iscript.setManager(manager);

		iscript.setEngine(engine);

		iscript.setLogger(logger);

		return iscript;
	}



}
