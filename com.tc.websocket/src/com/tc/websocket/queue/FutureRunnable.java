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

package com.tc.websocket.queue;

public class FutureRunnable implements IFutureRunnable{
	
	private int seconds=0;
	private Runnable runMe;
	
	
	public FutureRunnable(Runnable runMe){
		this.runMe = runMe;
	}
	

	@Override
	public void run() {
		runMe.run();
	}

	@Override
	public int getSeconds() {
		return this.seconds;
	}

	@Override
	public void setSeconds(int seconds) {
		this.seconds = seconds;
		
	}

}
