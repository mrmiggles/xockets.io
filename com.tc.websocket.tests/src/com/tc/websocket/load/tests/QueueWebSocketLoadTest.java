/*
 * � Copyright Tek Counsel LLC 2016
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
	
	private static final TestConfig cfg = TestConfig.getInstance();
	private List<NettyTestClient> clients = new ArrayList<NettyTestClient>();
	private static Scanner scanner;
	
	/*
	 * make sure the xpages runtime on the target domino server has been initialized (just load an xpage app via browser)
	 * after this class loads, go back to the websocket.nsf and manually invoke the broadcast many agent
	 */
	public static void main(String[] args) throws InterruptedException{
		loader = new QueueWebSocketLoadTest();
		System.out.println("Loaded " + cfg.getNumberOfClients() + " . Run the test agents to broadcast from websocket.nsf");
		
		scanner = new Scanner(System.in);
		while(scanner.hasNext()){
			String cmd = scanner.next();
			if(cmd.equals("stop")){
				System.out.println("Avg Seconds: " + NettyTestClient.calcAvg());
				System.out.println("msg/sec: " + NettyTestClient.messagesPerSecond());
				loader.closeClients();
				System.exit(0);
			}else if(cmd.equals("resetcounter")){
				NettyTestClient.responseCount.set(0);
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
			System.out.println("be sure to initialize the xpages run time, either manually, or setup XPagesPreload");
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