/*
 * 
 */
package com.tc.websocket.scripts;


// TODO: Auto-generated Javadoc
/**
 * The Class ScriptWrapper.
 */
/*
 * wrapper to disable mutability of the script class
 * from within the script execution
 */
public class ScriptWrapper extends Script {
	
	/** The script. */
	private Script script;
	
	/**
	 * Instantiates a new script wrapper.
	 *
	 * @param script the script
	 */
	public ScriptWrapper(Script script){
		this.script = script;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		throw new UnsupportedOperationException("Execution not allowed from within the script");
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#copy(java.lang.Object[])
	 */
	@Override
	public Script copy(Object... args) {
		throw new UnsupportedOperationException("Copy not allowed from within the script");
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#recompile(boolean)
	 */
	@Override
	public boolean recompile(boolean reload) {
		throw new UnsupportedOperationException("Recompile not allowed from within the script");
	}
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#getSource()
	 */
	public String getSource(){
		return this.script.getSource();
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#getFunction()
	 */
	public String getFunction(){
		return this.script.getFunction();
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#isIntervaled()
	 */
	public boolean isIntervaled(){
		return this.script.isIntervaled();
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#getUri()
	 */
	public String getUri(){
		return this.script.getUri();
	}

	

	/* (non-Javadoc)
	 * @see com.tc.websocket.scripts.Script#isWild()
	 */
	public boolean isWild(){
		return this.script.isWild();
	}
	
	
	
	public void updateInterval(int interval){
		if(!this.script.isIntervaled()){
			throw new IllegalArgumentException("setInterval can only be called on intervaled scripts.");
		}
		this.script.setInterval(interval);
	}
	
	
	public int getInterval(){
		return this.script.getInterval();
	}
	
}
