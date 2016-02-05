package com.tc.websocket.tests.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class TestConfig {

	private static TestConfig config = new TestConfig();

	private Properties props = new Properties();



	public static TestConfig getInstance(){
		return config;
	}

	private TestConfig(){
		InputStream in =null;
		try{
			in= TestConfig.class.getResourceAsStream("config.properties");
			props.load(in);
		}catch(IOException io){
			io.printStackTrace();
		}finally{
			IOUtils.closeQuietly(in);
		}
	}

	public int getNumberOfClients(){
		return Integer.parseInt(props.getProperty("number.of.clients"));
	}

	public String getWebSocketUrl(){
		return props.getProperty("websocket.url");
	}

	public String getWebSocketUrl2(){
		return props.getProperty("websocket.url2");
	}

	public int getConnectionDelay(){
		return Integer.parseInt(props.getProperty("connection.delay"));
	}


	public int getMaxPayload(){
		return Integer.parseInt(props.getProperty("max.frame.payload.length"));
	}
	
	
	public int getMessageDelay(){
		return Integer.parseInt(props.getProperty("message.delay"));
	}
	
	public String getSampleDataDir(){
		return props.getProperty("sample.data.dir");
	}
	
	
	public String getHttpUsername(){
		return props.getProperty("http.username");
	}

	public String getHttpPassword(){
		return props.getProperty("http.password");
	}
	
	public String getRegisterUserUrl(){
		return props.getProperty("rest.registeruser");
	}
	
	public String getRemoveUserUrl(){
		return props.getProperty("rest.removeuser");
	}
	
	public String getOnlineUsersUrl(){
		return props.getProperty("rest.onlineusers");
	}
	
	public String getRestWebSocketUrl(){
		return props.getProperty("rest.websocketurl");
	}
	
	public String getSendMessageUrl(){
		return props.getProperty("rest.sendmessage");
	}
	
	public String getSendSimpleUrl(){
		return props.getProperty("rest.sendsimple");
	}
	
	public String getLatestMessageUrl(){
		return props.getProperty("rest.latestmessage");
	}
	
	public String getMessagesUrl(){
		return props.getProperty("rest.messages");
	}
	
	
	public int getPrintOnCount(){
		return Integer.parseInt(props.getProperty("print.on.count"));
	}
	
	public int getGcOnCount(){
		return Integer.parseInt(props.getProperty("gc.on.count"));
	}
	
	private int stopOnCount = 0;
	public int getStopOnCount(){
		if(stopOnCount > 0) return stopOnCount;
		stopOnCount= Integer.parseInt(props.getProperty("stop.on.count"));
		return stopOnCount;
	}
	
	public boolean isCompressionEnabled(){
		return new Boolean(props.getProperty("compression.enabled"));
	}

}
