package com.tc.websocket.junit.tests;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tc.rest.client.IJSONClient;
import com.tc.rest.client.JsonClient;
import com.tc.utils.JSONUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.tests.config.TestConfig;
import com.tc.websocket.valueobjects.SocketMessage;

public class TestRestServices {
	
	private IJSONClient client = new JsonClient();
	private TestConfig cfg = TestConfig.getInstance();


	@Before
	public void setUp() throws Exception {
		

		//make sure D omino allows basic authentication against the service (see domino website rules)
		print("setCredentials..." + cfg.getHttpUsername());
		client.setCredentials(cfg.getHttpUsername(), cfg.getHttpPassword());
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testRestApi() throws UnsupportedEncodingException {
		
	
		String json = client.post(StringCache.EMPTY, cfg.getRegisterUserUrl());
		print("registeruser=" + json);
		
		
		json = client.get(cfg.getRestWebSocketUrl());
		print("websocketurl=" + json);
		
		json = client.get(cfg.getOnlineUsersUrl());
		print("onlineusers=" + json);
		
		
		
		String simple = cfg.getSendSimpleUrl().replace("{to}", "broadcast").replace("{from}", "tester").replace("{text}", "test+message");
		json = client.get(simple);
		print("sendsimple=" + json);
		
		
		SocketMessage msg = new SocketMessage();
		msg.setFrom("systemtest");
		msg.setTo("broadcast");
		msg.setText("hi there");
		
		Map<String,Object> data = new HashMap<String,Object>();
		
		data.put("ibm","http://ibm.com");
		data.put("google", "http://google.com");
		data.put("microsoft","http://microsoft.com");
		data.put("apple","http://apple.com");
		msg.setData(data);
		
		json = client.post(JSONUtils.toJson(msg), cfg.getSendMessageUrl());
		print("sendmessage = " + json);
		
		
		
		//now lets send it to ourselves, and retrieve it
		msg.setTo(cfg.getHttpUsername());
		print("sendmessage again=" + client.post(JSONUtils.toJson(msg), cfg.getSendMessageUrl()));
		
		
		//now lets pull the message we sent to ourselves
		print("messages=" + client.get(cfg.getMessagesUrl()));
		
		
		//now lets remove the user.
		print("removeuser= " + client.post("",cfg.getRemoveUserUrl()));
		
		
		try {
			//let the queued up messages get caught up, you may want to term manually.
			Thread.sleep(1000 * 3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public void print(Object o){
		System.out.println(o);
	}

}
