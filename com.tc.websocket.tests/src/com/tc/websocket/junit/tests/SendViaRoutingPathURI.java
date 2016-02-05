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

package com.tc.websocket.junit.tests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.tc.utils.JSONUtils;
import com.tc.websocket.tests.client.NettyTestClient;
import com.tc.websocket.tests.config.NettyClientFactory;
import com.tc.websocket.tests.config.TestConfig;
import com.tc.websocket.valueobjects.SocketMessage;

public class SendViaRoutingPathURI {

	private static final TestConfig cfg = TestConfig.getInstance();
	private List<NettyTestClient> clients = new ArrayList<NettyTestClient>();

	@Before
	public void setUp() throws Exception {

		NettyTestClient.printmessage=false;		
		NettyClientFactory factory = new NettyClientFactory();
		this.clients.addAll(factory.buildClients(cfg.getMaxPayload()));

	}



	@Test
	public void testRoutingPath() {
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
				
				msg.setDate(new Date());
				String json = JSONUtils.toJson(msg);
				c.send(json);
			}

			prior = c;
			cntr ++;

		}
		
	}
	
	
	
	
	

}
