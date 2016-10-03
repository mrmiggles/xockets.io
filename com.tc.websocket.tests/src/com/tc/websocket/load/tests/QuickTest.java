package com.tc.websocket.load.tests;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class QuickTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String text = "Automático";
		String newText = new String(text.getBytes(), Charset.defaultCharset());
		System.out.println(text + " " + newText);
		System.out.println(Charset.defaultCharset());
	}

}
