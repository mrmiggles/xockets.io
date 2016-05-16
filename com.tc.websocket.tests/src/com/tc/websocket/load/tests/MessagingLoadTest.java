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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.tc.utils.JSONUtils;
import com.tc.websocket.tests.client.NettyTestClient;
import com.tc.websocket.tests.config.NettyClientFactory;
import com.tc.websocket.tests.config.TestConfig;
import com.tc.websocket.valueobjects.SocketMessage;

public class MessagingLoadTest{

	private static MessagingLoadTest loader;
	
	protected static final TestConfig cfg = TestConfig.getInstance();
	protected List<NettyTestClient> clients = new ArrayList<NettyTestClient>();
	private static Scanner scanner;
	
	/*
	 * make sure the xpages runtime on the target domino server has been initialized (just load an xpage app via browser)
	 * after this class loads, go back to the websocket.nsf and manually invoke the broadcast many agent
	 */
	public static void main(String[] args) throws InterruptedException{
		
		cfg.overrideProperty("print.on.count", "100");
		cfg.overrideProperty("number.of.clients", "1000");
		cfg.overrideProperty("compression.enabled", "false");
		cfg.overrideProperty("message.delay", "100");
		
		loader = new MessagingLoadTest();
		System.out.println("Loaded " + cfg.getNumberOfClients() + ".  Pass start command to initiate test.");
		
		scanner = new Scanner(System.in);
		while(scanner.hasNext()){
			String cmd = scanner.next();
			if("start".equals(cmd)){
				loader.testMultipleTargets();
				loader.testDurability();
				loader.testSmallMessage();
				loader.testLargeMessage();
				loader.testRoutingPath();
				
			}
			if(cmd.equals("stop")){
				NettyTestClient.printStats();
				loader.closeClients();
				System.exit(0);
			}else if(cmd.equals("resetcounter")){
				NettyTestClient.counter.set(0);
			}else if("gc".equals(cmd)){
				System.gc();
			}
		}

	}


	public MessagingLoadTest(){

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
	
	
	

	public void testLargeMessage() throws InterruptedException {
		System.out.println("testing large message");
		cfg.overrideProperty("message.delay", "200");
		
		SocketMessage msg = new SocketMessage();	
		String text = "All work and no play makes Mark a dull boy.";
		Map<String,Object> data = new HashMap<String,Object>();
		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i< 100000 ; i++){
			sb.append(text);
		}
		data.put("bigMessage", sb.toString());
		msg.setData(data);
		msg.setText("Large Messge");


		NettyTestClient prior = null;
		for(NettyTestClient c: clients){

			if(prior!=null){
				msg.setFrom(c.getUuid());
				msg.setTo(prior.getUuid());
				msg.setDate(new Date());
				String json = JSONUtils.toJson(msg);
				c.send(json);
				json = null;
			}

			if(clients.indexOf(c)==(clients.size()-1)){
				prior = clients.get(0);
				msg.setFrom(c.getUuid());
				msg.setTo(prior.getUuid());
				msg.setDate(new Date());
				String json = JSONUtils.toJson(msg);
				c.send(json);
				json=null;
			}
			prior = c;
			Thread.sleep(TestConfig.getInstance().getMessageDelay());

		}

	}
	
	

	public void testSmallMessage() throws InterruptedException{
		System.out.println("testing small message");
		cfg.overrideProperty("message.delay", "1");
		SocketMessage msg = new SocketMessage();

		int cntr = 0;
		String text = "All work and no play makes Mark a dull boy\n\r.";

		NettyTestClient prior = null;
		for(NettyTestClient c: clients){

			if(prior!=null){
				msg.setText(text + " " + cntr);
				msg.setFrom(c.getUuid());
				msg.setTo(prior.getUuid());
				msg.setDate(new Date());
				msg.getData().put("test", "testing testing 123");
				msg.setText(text);
				String json = JSONUtils.toJson(msg);
				c.send(json);
				
			}

			prior = c;
			cntr ++;
			Thread.sleep(TestConfig.getInstance().getMessageDelay());
		}

	}
	
	public void testDurability() throws InterruptedException{
		System.out.println("testing durable message");
		cfg.overrideProperty("message.delay", "1");
		SocketMessage msg = new SocketMessage();

		int cntr = 0;
		String text = "Testing durable flag\n\r.";

		NettyTestClient prior = null;
		for(NettyTestClient c: clients){

			if(prior!=null){
				msg.setText(text + " " + cntr);
				msg.setFrom(c.getUuid());
				msg.setTo(prior.getUuid());
				msg.setDate(new Date());
				msg.setDurable(true);
				msg.setText(text);
				msg.getData().put("test", "testing testing 123");
				String json = JSONUtils.toJson(msg);
				c.send(json);
				
			}

			prior = c;
			cntr ++;
			Thread.sleep(TestConfig.getInstance().getMessageDelay());
		}

	}
	
	
	public void testRoutingPath() throws InterruptedException {
		System.out.println("test routing path");
		cfg.overrideProperty("message.delay", "1");
		
		Random random = new Random();
		SocketMessage msg = new SocketMessage();
		int cntr = 0;
		String text = "all work and no play makes mark a dull boy.all work and no play makes mark a dull boy.all work and no play makes mark a dull boy.all work and no play makes mark a dull boy.";
		
		NettyTestClient prior = null;
		for(NettyTestClient c: clients){

			if(prior!=null){
				String uri = "/uri" + random.nextInt(cfg.getNumberOfClients());
				msg.setTo(uri);
				msg.setText(text + " " + cntr);
				msg.setFrom(c.getUuid());
				msg.getData().put("testing", text);
				msg.setDate(new Date());
				String json = JSONUtils.toJson(msg);
				c.send(json);
				Thread.sleep(cfg.getMessageDelay());
			}
			prior = c;
			cntr ++;
		}
		
	}
	
	
	public void testMultipleTargets() throws InterruptedException {
		System.out.println("test multiple targets");
		cfg.overrideProperty("message.delay", "1");
		
		Random random = new Random();
		SocketMessage msg = new SocketMessage();
		int cntr = 0;
		String text = "all work and no play makes mark a dull boy.all work and no play makes mark a dull boy.all work and no play makes mark a dull boy.all work and no play makes mark a dull boy.";
		
		NettyTestClient prior = null;
		for(NettyTestClient c: clients){
			msg.getTargets().clear();
			if(prior!=null){
				
				for(int i=0;i<4;i++){
					String uri = "/uri" + random.nextInt(cfg.getNumberOfClients());
					msg.addTarget(uri);
				}
				msg.setText(text + " " + cntr);
				msg.setFrom(c.getUuid());
				msg.getData().put("testing", text);
				msg.setDate(new Date());
				String json = JSONUtils.toJson(msg);
				c.send(json);
				Thread.sleep(cfg.getMessageDelay());
			}
			prior = c;
			cntr ++;
		}
		
	}
	

}
