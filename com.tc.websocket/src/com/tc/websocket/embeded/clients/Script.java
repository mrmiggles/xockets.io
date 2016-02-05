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

import javax.script.CompiledScript;

public class Script {
	
	private String event;
	private String script;
	private String source;
	private CompiledScript compiled;
	
	
	public String getEvent() {
		return event;
	}
	
	public void setEvent(String event) {
		this.event = event;
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
		return this.getEvent() + "." + this.getScript();
	}
	
	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		return this.toString().equals(o.toString());
	}
	
	@Override
	public int hashCode(){
		return this.toString().hashCode() * this.compiled.hashCode();
	}
	public CompiledScript getCompiled() {
		return compiled;
	}
	public void setCompiled(CompiledScript compiled) {
		this.compiled = compiled;
	}
	
	

}
