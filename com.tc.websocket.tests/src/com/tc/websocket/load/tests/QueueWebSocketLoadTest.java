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

package com.tc.websocket.load.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.tc.websocket.tests.client.NettyTestClient;
import com.tc.websocket.tests.config.NettyClientFactory;
import com.tc.websocket.tests.config.TestConfig;

public class QueueWebSocketLoadTest{

	private static QueueWebSocketLoadTest loader;
	
	protected static final TestConfig cfg = TestConfig.getInstance();
	protected List<NettyTestClient> clients = new ArrayList<NettyTestClient>();
	private static Scanner scanner;
	
	/*
	 * make sure the xpages runtime on the target domino server has been initialized (just load an xpage app via browser)
	 * after this class loads, go back to the websocket.nsf and manually invoke the broadcast many agent
	 */
	public static void main(String[] args) throws InterruptedException{
		cfg.overrideProperty("number.of.clients", "2000");
		cfg.overrideProperty("print.on.count", "1000000");
	
		
		loader = new QueueWebSocketLoadTest();
		System.out.println("Loaded " + cfg.getNumberOfClients() + " . Run the test agents to broadcast from websocket.nsf");
		
		scanner = new Scanner(System.in);
		while(scanner.hasNext()){
			String cmd = scanner.next();
			if(cmd.equals("runningcount")){
				System.out.println("Running count is : " + NettyTestClient.counter.get());
			}
			else if(cmd.equals("stop")){
				NettyTestClient.printStats();
				loader.closeClients();
				System.exit(0);
			}else if(cmd.equals("resetcounter")){
				NettyTestClient.resetCounter();
			}else if("gc".equals(cmd)){
				System.gc();
			}
		}

	}


	public QueueWebSocketLoadTest(){

		try{
			NettyClientFactory factory = new NettyClientFactory();
			this.clients.addAll(factory.buildClients(TestConfig.getInstance().getMaxPayload()));
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public void closeClients() throws InterruptedException{
		for(NettyTestClient client : this.getClients()){
			client.disconnect();
			Thread.sleep(TestConfig.getInstance().getConnectionDelay());
		}
	}

	public List<NettyTestClient> getClients(){
		return clients;
	}
	
	

}
