package com.tc.websocket.scripts;

import com.tc.websocket.runners.TaskRunner;

public class TermSignal {
	
	private static final TermSignal signal = new TermSignal();
	
	
	private TermSignal(){}
	
	public boolean isClosing(){
		return TaskRunner.getInstance().isClosing();
	}
	
	public static TermSignal insta(){
		return signal;
	}

}
