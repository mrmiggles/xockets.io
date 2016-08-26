package com.tc.websocket.load.tests;

import java.io.IOException;
import java.io.InputStream;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;

import com.tc.websocket.valueobjects.SocketMessage;

public class QuickTest {
	
	public static void main(String[] args){
		
		SocketMessage msg = new SocketMessage().to("test").from("test").text("test");
		
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("python");
			InputStream in = QuickTest.class.getResourceAsStream("observer.py");
			String code = IOUtils.toString(in);
			
			
		
			engine.eval(code);
			Invocable inv = (Invocable) engine;
			inv.invokeFunction("onMessage", msg);
			
			
			IOUtils.closeQuietly(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String parseTo(String json){
		String tokenStart = "\"to\":";
		String tokenEnd = ",\"text\":";
		int start = json.indexOf(tokenStart);
		int end = json.indexOf(tokenEnd);
		return json.substring(start + tokenStart.length(),end).replace("\"", "");
	}


}
