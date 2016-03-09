package com.tc.websocket.junit.tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

public class SocketTest {

	public static void main(String[] args){
		try{
			
			InputStream in = new FileInputStream("c:/temp/req.txt");
	
			Socket socket = new Socket("tekcounsel.net",80);
			socket.setTcpNoDelay(true);
			socket.setReceiveBufferSize(4096);
			socket.setSendBufferSize(4096 * 2);
		
			OutputStream out = new BufferedOutputStream(socket.getOutputStream());
			InputStream res = new BufferedInputStream(socket.getInputStream());
			
			IOUtils.copy(in, out);
			out.write("\n\r\n\r".getBytes());
			out.flush();
			
			
			System.out.println("start read");
			System.out.println(IOUtils.copy(res, System.out));
			System.out.println("end read");
			
			
			System.out.println("closing connection");
			in.close();
			out.close();
			res.close();
			res.close();
			socket.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
