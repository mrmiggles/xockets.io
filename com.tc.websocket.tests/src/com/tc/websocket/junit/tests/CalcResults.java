package com.tc.websocket.junit.tests;

import org.junit.Test;

import com.tc.websocket.tests.client.NettyTestClient;
import com.tc.websocket.tests.config.NettyClientFactory;

public class CalcResults {

	@Test
	public void test() throws InterruptedException {
		Thread.sleep(8000); //let last few messages make it through.
		System.out.println("Avg: " + NettyTestClient.calcAvg(NettyTestClient.seconds));
		System.out.println("Msg/Sec: " + NettyTestClient.messagesPerSecond());
		System.out.println("Total Time : " + NettyTestClient.elapsedSeconds() + " seconds");

		new NettyClientFactory().closeClients();
		System.out.println("Clients closed");

	}

}
