/*
 * 
 */
package com.tc.websocket.runners;

import java.util.ArrayList;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class Batch.
 */
public class Batch implements Runnable {
	
	/** The runners. */
	private List<Runnable> runners = new ArrayList<Runnable>(10);


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for(Runnable r : runners){
			r.run();
		}
	}
	
	/**
	 * Adds the runner.
	 *
	 * @param runner the runner
	 */
	public void addRunner(Runnable runner){
		this.runners.add(runner);
	}
	
	/**
	 * Removes the runner.
	 *
	 * @param runner the runner
	 */
	public void removeRunner(Runnable runner){
		runners.remove(runner);
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty(){
		return runners.isEmpty();
	}
	
}
