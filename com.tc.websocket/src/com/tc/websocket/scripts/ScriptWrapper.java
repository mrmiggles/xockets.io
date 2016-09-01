package com.tc.websocket.scripts;

/*
 * wrapper to disable mutability of the script class
 * from within the script execution
 */
public class ScriptWrapper extends Script {
	
	private Script script;
	
	public ScriptWrapper(Script script){
		this.script = script;
	}

	@Override
	public void run() {
		throw new UnsupportedOperationException("Execution not allowed from within the script");
	}

	@Override
	public Script copy(Object... args) {
		throw new UnsupportedOperationException("Copy not allowed from within the script");
	}

	@Override
	public boolean recompile(boolean reload) {
		throw new UnsupportedOperationException("Recompile not allowed from within the script");
	}
	
	public String getSource(){
		return this.script.getSource();
	}
	
	public String getFunction(){
		return this.script.getFunction();
	}
	
	public boolean isIntervaled(){
		return this.script.isIntervaled();
	}
	
	public String getUri(){
		return this.script.getUri();
	}

	public boolean isWild(){
		return this.script.isWild();
	}
}
