package com.tc.websocket.tests.client;

import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.tc.utils.Base64;
import com.tc.utils.DateUtils;
import com.tc.utils.JSONUtils;
import com.tc.websocket.embeded.clients.AbstractClient;
import com.tc.websocket.tests.config.TestConfig;
import com.tc.websocket.valueobjects.SocketMessage;

public class NettyTestClient extends AbstractClient{
	
	private String username;
	private String uuid;
	

	public static AtomicInteger responseCount = new AtomicInteger();
	public static boolean printmessage=false;
	public static List<Double> seconds = Collections.synchronizedList(new ArrayList<Double>());
	private static Date start = new Date();
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
	public void onMessage(String message) {
		int count = responseCount.getAndIncrement();
		if(count == 1){
			start = new Date();
		}
		long kb = message.length() / 1000;
		long mb = kb / 1000;
		String strdate = DateUtils.toISODateTime(new Date());
		
		if(printmessage){
			try {
				SocketMessage msg = JSONUtils.toObject(message, SocketMessage.class);
				byte[] data;
				if(msg.getData().containsKey("gzip")){
					String zipdata = (String) msg.getData().get("gzip");
					data = Base64.decode(String.valueOf(zipdata));
					
					
					int savings = data.length - zipdata.length();
					double ratio= (double)zipdata.length() / (double)data.length;
					System.out.println("before compression = " + data.length + " after=" + zipdata.length() + " savings of " + savings + " bytes, ratio=" + ratio);
					
					
				}else {
					data = msg.getData().toString().getBytes();
				}
				
				File file = new File("c:/temp/" + UUID.randomUUID().toString());
				FileOutputStream out = new FileOutputStream(file);
				out.write(data);
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if((count % printOnCount == 0)){
			
			if(count == 0) return; //omit the first entry.
			double time = DateUtils.getTimeDiffSecDouble(start, new Date());
			seconds.add(time);
			start = new Date();
			System.out.println("datetime=" + strdate + ", msgcount=" + count +  ", seconds=" + time + ", username=" + this.getUsername() + ", uri=" + this.getUri().getPath() + ", received: mb=" + mb + ", kb=" + kb + ", bytes=" + message.length() + ", host=" + this.getRemoteHost());
			System.gc();
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

	public static double calcAvg(){
		double sum =0;
		for(Double i : seconds){
			sum = sum + i;
		}
		
		return round( sum / (double)seconds.size(),2);
	}
	
	public static double messagesPerSecond(){
		return round(TestConfig.getInstance().getPrintOnCount() / calcAvg(),2);
	}
	
	private static double round(double value, int places){
		BigDecimal bd = new BigDecimal(value);
    	bd = bd.setScale(places,BigDecimal.ROUND_HALF_UP);
    	return bd.doubleValue();
	}
	
	public static long elapsedSeconds(){
		return DateUtils.getTimeDiffSec(initDate, new Date());
	}
	
}
