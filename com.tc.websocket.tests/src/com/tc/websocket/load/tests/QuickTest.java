package com.tc.websocket.load.tests;

public class QuickTest {
	
	public static void main(String[] args){
		String json = "{\"from\":\"system\",\"to\":\"broadcast\",\"text\":\"An agent sent me! 1. Adding more data to make it more real. More text to get closer to a reasonable amount of data.  All work and no play makes Mark a dull boy.\",\"date\":\"2016-05-12 12:00 AM\",\"durable\":\"false\",\"persisted\":\"true\"}";
		System.out.println(parseTo(json));

	}
	
	public static String parseTo(String json){
		String tokenStart = "\"to\":";
		String tokenEnd = ",\"text\":";
		int start = json.indexOf(tokenStart);
		int end = json.indexOf(tokenEnd);
		return json.substring(start + tokenStart.length(),end).replace("\"", "");
	}


}
