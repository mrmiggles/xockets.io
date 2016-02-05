package com.tc.websocket.queue;

import java.util.ArrayList;
import java.util.List;

public class Batch implements Runnable {
	
	private List<Runnable> runners = new ArrayList<Runnable>(10);

	@Override
	public void run() {
		for(Runnable r : runners){
			r.run();
		}
	}
	
	public void addRunner(Runnable runner){
		this.runners.add(runner);
	}
	
	public void removeRunner(Runnable runner){
		runners.remove(runner);
	}
	
}
