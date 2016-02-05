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

import java.io.InputStream;
import java.net.URI;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.tc.scriptrunner.guice.ScriptRunnerModule;
import com.tc.scriptrunner.runners.IScriptRunner;
import com.tc.websocket.embeded.clients.RhinoClient;
import com.tc.websocket.embeded.clients.Script;
import com.tc.websocket.tests.config.TestConfig;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.SocketMessage;
import com.tc.websocket.valueobjects.User;

public class RhinoClientTest {


	private RhinoClient client;

	@Before
	public void setUp() throws Exception {
		
		//set this value to your local system's path.
		System.setProperty("java.library.path","C:/Domino");

		IScriptRunner runner = new ScriptRunnerModule().provideScriptRunner();
		
		String url = TestConfig.getInstance().getWebSocketUrl().replace("{uri}","rhinotest/rhinoclient");
		
		System.out.println(url);

		URI uri = URI.create(url);
		client = new RhinoClient(uri);
		
		IUser user = new User();
		user.setUserId("rhinoclient");
		user.setSessionId("rhinoclient");
		client.setUser(user);
		
		// set the runner
		client.setScriptRunner(runner);

		//load the test script
		InputStream in = RhinoClientTest.class.getResourceAsStream("rhinotest.js");
		String javascript = IOUtils.toString(in);
		IOUtils.closeQuietly(in);
		
		//build the script with appropriate target event.
		Script script = new Script();
		script.setScript(javascript);
		script.setEvent("*");
		
		//add the script
		client.addScript(script);
		

		// connect the client.
		client.connect();
		
	}

	@Test
	public void testRhinoClient() throws InterruptedException {

		System.out.println("testRhinoClient...");

		SocketMessage msg = new SocketMessage();


		String text = "all work and no play makes mark a dull boy.";

		for (int i = 0; i < 1; i++) {
			
			msg.setTo("broadcast");
			
			msg.setText(text + " " + i);
			msg.setFrom("JUnit");

			msg.setDate(new Date());
		
			
			//client will echo the message sent.
			client.sendMsg(msg);
			
			Thread.sleep(4000);
			
			if(client.hasError()){
				throw new RuntimeException("RhinoClient has an internal error.  Please check the log.");
			}
			
			client.disconnect();
	
			Thread.sleep(1000);
			
		}
	}

}
