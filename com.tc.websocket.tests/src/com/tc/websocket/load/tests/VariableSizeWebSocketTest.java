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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.tc.utils.DateUtils;
import com.tc.utils.JSONUtils;
import com.tc.websocket.tests.client.NettyTestClient;
import com.tc.websocket.tests.config.FileLoader;
import com.tc.websocket.tests.config.NettyClientFactory;
import com.tc.websocket.tests.config.TestConfig;
import com.tc.websocket.valueobjects.SocketMessage;

public class VariableSizeWebSocketTest implements Runnable{

	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1); 

	private List<NettyTestClient> clients = new ArrayList<NettyTestClient>();

	private FileLoader loader = new FileLoader();

	private List<SocketMessage> messages = new ArrayList<SocketMessage>();
	
	private Random random  = new Random();
	
	private static final int RUN_TIME_MIN=15;
	
	private final Date START=new Date();
	
	private static Scanner scanner;
	
	public static void main(String[] args){

		try {
			System.out.println("Test will run for " + RUN_TIME_MIN +  " minutes");
			TestConfig cfg = TestConfig.getInstance();
			cfg.overrideProperty("print.on.count", "1000");
			cfg.overrideProperty("number.of.clients", "4000");
			cfg.overrideProperty("enable.compression", "true");
			cfg.overrideProperty("message.delay", "100");
			
			
			VariableSizeWebSocketTest loader=new VariableSizeWebSocketTest();
			scheduled.scheduleAtFixedRate(loader, 0, 100, TimeUnit.MILLISECONDS);	
			
			
			scanner = new Scanner(System.in);
			while(scanner.hasNext()){
				String cmd = scanner.next();
				if(cmd.equals("stop")){
					loader.stop();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public VariableSizeWebSocketTest(){
		try{
			NettyClientFactory factory = new NettyClientFactory();
			clients.addAll(factory.buildClients(TestConfig.getInstance().getMaxPayload()));
			this.buildSocketMessages();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void buildSocketMessages(){
		if(this.messages.isEmpty()){
			for(File file : loader.getFiles()){
				SocketMessage msg = new SocketMessage();
				Map<String,Object> data = new HashMap<String,Object>();
				String filedata = loader.getData(file);
				data.put("sample.data", filedata);
				msg.setData(data);
				messages.add(msg);
			}
		}

	}

	public List<NettyTestClient> getClients(){
		return clients;
	}

	@Override
	public void run() {
		long elapsedTime = DateUtils.getTimeDiffMin(START, new Date());
		if(elapsedTime >= RUN_TIME_MIN){
			stop();
			return;
		}
		this.testWebSocketServer();
	}

	public void stop(){
		scheduled.shutdownNow();
		NettyTestClient.printStats();
		try {
			new NettyClientFactory().closeClients();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(0);
	}

	public void testWebSocketServer() {

		try{
			//randomly select two clients from the collection.
			for(int i =0;i<=1000;i++){
				NettyTestClient from = clients.get(random.nextInt(clients.size()));
				NettyTestClient to = clients.get(random.nextInt(clients.size()));
				
				//this needs to be here when testing max size or max connections.
				if(to.isClosed()){
					continue;
				}
				
				SocketMessage msg = messages.get(random.nextInt(messages.size()));
				msg.setText("Hola!");
				msg.setFrom(from.getUuid());
				msg.setTo(to.getUuid());
				msg.setDate(new Date());
				String json = JSONUtils.toJson(msg);
				from.send(json);
				
				Thread.sleep(TestConfig.getInstance().getMessageDelay());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
