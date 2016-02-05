package com.tc.websocket.junit.tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;

import com.tc.scriptrunner.guice.ScriptRunnerModule;
import com.tc.scriptrunner.runners.IScriptRunner;
import com.tc.websocket.embeded.clients.IScriptClient;
import com.tc.websocket.embeded.clients.RhinoClient;
import com.tc.websocket.embeded.clients.Script;
import com.tc.websocket.tests.config.TestConfig;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.User;

public class QuickTest {
	
	public static void main(String[] args) throws InterruptedException, IOException{
		//set this value to your local system's path.
				System.setProperty("java.library.path","C:/Domino");

				IScriptRunner runner = new ScriptRunnerModule().provideScriptRunner();
				
				String url = TestConfig.getInstance().getWebSocketUrl().replace("{uri}","/rhinotest/rhinoclient");
				
				System.out.println(url);

				URI uri = URI.create(url);
				RhinoClient client = new RhinoClient(uri);
				
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
				script.setEvent(IScriptClient.ON_MESSAGE);
				
				//add the script
				client.addScript(script);
				

				// connect the client.
				client.connect();
				
				
				while(true){
					Thread.sleep(1000);
				}
				
	}

}
