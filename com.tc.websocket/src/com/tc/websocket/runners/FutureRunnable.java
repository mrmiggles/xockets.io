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

package com.tc.websocket.runners;


// TODO: Auto-generated Javadoc
/**
 * The Class FutureRunnable.
 */
public class FutureRunnable implements IFutureRunnable{
	
	/** The seconds. */
	private int seconds;
	
	/** The run me. */
	private Runnable runMe;
	
	
	/**
	 * Instantiates a new future runnable.
	 *
	 * @param runMe the run me
	 */
	public FutureRunnable(Runnable runMe){
		this.runMe = runMe;
	}
	


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		runMe.run();
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.runners.IFutureRunnable#getSeconds()
	 */
	@Override
	public int getSeconds() {
		return this.seconds;
	}


	/* (non-Javadoc)
	 * @see com.tc.websocket.runners.IFutureRunnable#setSeconds(int)
	 */
	@Override
	public void setSeconds(int seconds) {
		this.seconds = seconds;
		
	}

}
