package com.tc.websocket.tests.client;

import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.tc.utils.DateUtils;
import com.tc.websocket.clients.AbstractClient;
import com.tc.websocket.tests.config.TestConfig;

public class NettyTestClient extends AbstractClient implements Runnable{
	
	private String username;
	private String uuid;
	

	public static AtomicInteger counter = new AtomicInteger();
	public static List<Double> seconds = Collections.synchronizedList(new ArrayList<Double>());
	private static Date start=null;
	private static AtomicLong dataVolume = new AtomicLong();
	private static Date initDate = new Date();
	private int printOnCount = TestConfig.getInstance().getPrintOnCount();


	public NettyTestClient(URI uri) throws InterruptedException {
		super(uri);

	}

	@Override
	public void onOpen(WebSocketClientHandshaker handShaker) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onError(Throwable cause) {
		cause.printStackTrace();
	}

	@Override
	public synchronized void onMessage(String message) {
		
		if(start == null ) start = new Date();
		dataVolume.addAndGet(message.getBytes().length);
		long kb = message.length() / 1000;
		long mb = kb / 1000;
		String strdate = DateUtils.toISODateTime(new Date());
				
		if((counter.incrementAndGet() % printOnCount == 0)){
			double time = DateUtils.getTimeDiffSecDouble(start, new Date());
			seconds.add(time);
			start = new Date();
			System.out.println("datetime=" + strdate + ", total.msgs=" + counter.get() +  ", seconds=" + time + ", username=" + this.getUsername() + ", uri=" + this.getUri().getPath() + ", received: mb=" + mb + ", kb=" + kb + ", bytes=" + message.length() + ", host=" + this.getRemoteHost());
		}	
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public boolean isClosed(){
		return this.isOpen() == false;
	}

	public static double calcAvg(Collection<Double> values){
		double sum =0;
		for(Double i : values){
			sum = sum + i;
		}
		
		if(sum == 0) return 0;
		
		return round( sum / (double)values.size(),2);
	}
	
	public static double avgDropHigh(){
		List<Double> myseconds = new ArrayList<Double>();
		myseconds.addAll(seconds);
		Collections.sort(myseconds);
		myseconds.remove(myseconds.size() -1);
		return calcAvg(myseconds);
	}
	
	public static double avgDropLow(){
		List<Double> myseconds = new ArrayList<Double>();
		myseconds.addAll(seconds);
		Collections.sort(myseconds);
		myseconds.remove(0);
		return calcAvg(myseconds);
	}
	
	public static double messagesPerSecond(){
		double avg = calcAvg(seconds);
		if(avg == 0) return 0;
		return round(TestConfig.getInstance().getPrintOnCount() / calcAvg(seconds),2);
	}
	
	private static double round(double value, int places){
		BigDecimal bd = new BigDecimal(value);
    	bd = bd.setScale(places,BigDecimal.ROUND_HALF_UP);
    	return bd.doubleValue();
	}
	
	public static long elapsedSeconds(){
		return DateUtils.getTimeDiffSec(initDate, new Date());
	}
	
	public static long totalBytes(){
		return dataVolume.get();
	}
	
	public static void resetCounter(){
		counter.set(0);
		start = null;
	}
	
	public static void printDataVolume(){
		long kb = NettyTestClient.totalBytes() / 1000;
		long mb = kb / 1000;
		long gb = mb / 1000;
		System.out.println("Total data volume was kb=" + kb + ", mb=" + mb + ", gb=" + gb );
	}
	
	public static void printAvg(){
		System.out.println("Avg Sec: " + NettyTestClient.calcAvg(seconds));
		
	}
	
	public static void printMsgPerSec(){
		System.out.println("Msg/Sec: " + NettyTestClient.messagesPerSecond());
	}
	
	public static void printTotalMsgs(){
		System.out.println("Total Msgs: " + counter.get());
	}
	
	public static void printAvgDropHigh(){
		System.out.println("Avg remove highest: " + NettyTestClient.avgDropHigh());
	}
	
	public static void printAvgDropLow(){
		System.out.println("Avg remove lowest: " + NettyTestClient.avgDropLow());
	}
	
	public static void printStats(){
		
		printDataVolume();
		printAvg();
		printAvgDropHigh();
		printAvgDropLow();
		printMsgPerSec();
		printTotalMsgs();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
