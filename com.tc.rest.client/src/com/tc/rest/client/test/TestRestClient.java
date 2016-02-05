package com.tc.rest.client.test;

import org.junit.Test;

import com.tc.rest.client.IJSONClient;
import com.tc.rest.client.JsonClient;

public class TestRestClient {

	@Test
	public void test() {
		IJSONClient client = new JsonClient();
		client.setCredentials("admin admin", "password");
		String response = client.post(null, "http://192.168.0.123/websocket.nsf/api/websocket/v1/registeruser");
		System.out.println(response);
	}

}
