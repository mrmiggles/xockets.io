/*
 * 
 */
package com.tc.websocket.scripts;

import com.tc.websocket.runners.TaskRunner;


// TODO: Auto-generated Javadoc
/**
 * The Class TermSignal.
 */
public class TermSignal {
	
	/** The Constant signal. */
	private static final TermSignal signal = new TermSignal();
	
	
	/**
	 * Instantiates a new term signal.
	 */
	private TermSignal(){}
	
	/**
	 * Checks if is closing.
	 *
	 * @return true, if is closing
	 */
	public boolean isClosing(){
		return TaskRunner.getInstance().isClosing();
	}
	
	/**
	 * Insta.
	 *
	 * @return the term signal
	 */
	public static TermSignal insta(){
		return signal;
	}

}
