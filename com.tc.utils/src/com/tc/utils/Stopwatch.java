package com.tc.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Stopwatch {
	
	private Date start;
	private Date end;
	
	public Stopwatch(){
		this.start();
	}
	
	public void start(){
		start = new Date();
	}
	
	public void end(){
		end = new Date();
	}
	
	
	public long elapsedTime(TimeUnit timeUnit){
		
		if(end==null) this.end();
		
		long elapsed = 0;
		
		if(TimeUnit.MILLISECONDS.equals(timeUnit)){
			elapsed =DateUtils.getTimeDiffMiliSec(start, end);
			
		}else if(TimeUnit.SECONDS.equals(timeUnit)){
			elapsed = DateUtils.getTimeDiffSec(start, end);
			
		}else if(TimeUnit.MINUTES.equals(timeUnit)){
			elapsed = DateUtils.getTimeDiffMin(start, end);
		}
		
		
		return elapsed;
	}
	

}
