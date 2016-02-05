package com.tc.websocket.junit.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.ibm.commons.util.io.base64.Base64;
import com.tc.utils.StrUtils;

public class ExtractScript {

	@Test
	public void test() throws IOException {
		InputStream in = new FileInputStream("c:/temp/script.xml");
		String data = IOUtils.toString(in );
		IOUtils.closeQuietly(in);
		
		String script = StrUtils.middle(data, "<rawitemdata type='1'>", "</rawitemdata>");
		
		
		String decoded = Base64.decode(script);
		System.out.println(decoded.substring(50, decoded.length()));
		
	}

}
