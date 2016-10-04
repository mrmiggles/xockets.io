package com.tc.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StopWatch {
	
	private Date start;
	private Date end;
	private String label;
	
	public StopWatch(){
		this.start();
	}
	
	public StopWatch(String label){
		this.label = label;
		this.start();
	}
	 
	public void start(){
		start = new Date();
	}
	
	public void end(){
		end = new Date();
	}
	
	
	public long elapsedSeconds(){
		return this.elapsedTime(TimeUnit.SECONDS);
	}
	
	public long elapsedMilli(){
		return this.elapsedTime(TimeUnit.MILLISECONDS);
	}
	
	public long elapsedMin(){
		return this.elapsedTime(TimeUnit.MINUTES);
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
	
	public void print(TimeUnit timeUnit, long max){
		long time = this.elapsedTime(timeUnit);
		if(time >=max){
			System.out.println(this.label + " in " + this.elapsedTime(timeUnit) + " " + timeUnit.name());
		}
	}

}
