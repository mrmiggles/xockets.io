package com.tc.websocket.junit.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tc.rest.client.IJSONClient;
import com.tc.rest.client.JsonClient;
import com.tc.utils.JSONUtils;
import com.tc.websocket.tests.client.NettyTestClient;
import com.tc.websocket.tests.config.NettyClientFactory;
import com.tc.websocket.tests.config.TestConfig;
import com.tc.websocket.valueobjects.SocketMessage;

public class TestScriptingRuntimes {
	
	private IJSONClient client = new JsonClient();
	private TestConfig cfg = TestConfig.getInstance();
	private List<NettyTestClient> clients = new ArrayList<NettyTestClient>();
	NettyClientFactory factory = new NettyClientFactory();
	
	@Before
	public void setUp() throws Exception {
		
		cfg.overrideProperty("print.on.count", "10000"); //we don't want it to print
		cfg.overrideProperty("number.of.clients", "1");
		cfg.overrideProperty("compression.enabled", "false");
		cfg.overrideProperty("message.delay", "100");
		cfg.overrideProperty("print.data", "true");//we want to print data.
		
		
		this.clients.addAll(factory.buildClients(TestConfig.getInstance().getMaxPayload()));
		
		
		
		client.setCredentials(cfg.getHttpUsername(), cfg.getHttpPassword());
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testScriptingRuntimes() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
		
	
		
		String json = client.get(cfg.property("load.scripts.url"));
		Map<String,Object> map = JSONUtils.toMap(json);
		
		if(!map.get("scriptsLoaded").equals(new Boolean(true))){
			throw new RuntimeException("Scripts did not load.");
		}
		
		//give the scripts some time to load up.
		print("pausing to give scripts time to register...");
		java.lang.Thread.sleep(5000);

		
		NettyTestClient client = this.clients.get(0);
		
		//now lets start interacting.
		SocketMessage msg = new SocketMessage();
		msg.setFrom(client.getUsername());
		msg.setTo("/chat.nsf*");//make sure all the listener scripts fire.
		msg.setFrom(client.getUsername());
		msg.setText("Hi There");
		
		client.send(JSONUtils.toJson(msg));
		
		print("pausing to allow script responses...");
		java.lang.Thread.sleep(5000);
		
		factory.closeClients();
		
		print("pausing to allow client disconnect");
		java.lang.Thread.sleep(5000);
		
		print("\n\nRemember to review log.nsf to see intervaled scripts and observer activity");
		
		print("\n\nDone");
		
			
	}
	
	public void print(Object o){
		System.out.println(o);
	}

}
