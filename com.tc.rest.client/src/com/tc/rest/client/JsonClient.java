/*
 * © Copyright Tek Counsel LLC 2016
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.tc.rest.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import com.tc.utils.Base64;
import com.tc.utils.CompressionUtils;
import com.tc.utils.StrUtils;

/**
 *
 * @author mwambler
 */
public class JsonClient implements IJSONClient {

	private static final Logger logger = Logger.getLogger(JsonClient.class.getName());

	private String username;

	private String password;

	private boolean useCreds;


	@Override
	public void setCredentials(String username, String password) {
		this.username = username;
		this.password = password;
		this.useCreds = true;
	}

	@Override
	public void setCredentials(HttpServletRequest req) {
		try {
			String authorization = StrUtils.replace(req.getHeader("Authorization"), "Basic ", "");
			byte[] byteMe = Base64.decode(authorization);
			authorization = new String(byteMe);
			String[] credentials = authorization.split(":");
			this.username=credentials[0];
			this.password=credentials[1];
			this.useCreds=true;
		} catch (Exception ex) {
			logger.log(Level.SEVERE,null,ex);
		}
	}


	@Override
	public void applyCredentials(HttpURLConnection conn) {
		String userpassword = username + ":" + password;
		String encodedAuthorization = Base64.encodeBytes(userpassword.getBytes());
		conn.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
	}

	@Override
	public String get(String strurl) {
		StringBuilder builder = new StringBuilder();
		HttpURLConnection conn = null;

		try {
			URL url = new URL(strurl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (this.useCreds) {
				this.applyCredentials(conn);
			}

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "  + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				builder.append(output);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE,null,e);
		}finally{
			conn.disconnect();
		}


		return builder.toString();
	}

	@Override
	public String post(String json, String strurl) {
		StringBuilder builder = new StringBuilder();
		HttpURLConnection conn=null;
		OutputStream out = null;
		try {
			URL url = new URL(strurl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			if (this.useCreds) {
				this.applyCredentials(conn);
			}

			String input = json;

			if(input!=null){
				OutputStream os = conn.getOutputStream();
				os.write(input.getBytes());
				os.flush();
			}

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			logger.log(Level.INFO,"Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				builder.append(output);
			}


		} catch (IOException e) {
			logger.log(Level.SEVERE,null,"url that failed is " + strurl);
			logger.log(Level.SEVERE,null,e);

		}finally{
			IOUtils.closeQuietly(out);
			conn.disconnect();
		}

		return builder.toString();

	}

	@Override
	public String compressAndPost(String json, String strurl) {
		StringBuilder builder = new StringBuilder();
		HttpURLConnection conn = null;
		try {
			URL url = new URL(strurl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.addRequestProperty("Content-Encoding", "gzip");

			if (this.useCreds) {
				this.applyCredentials(conn);
			}


			byte[] byteMe = CompressionUtils.compress(json.getBytes());

			conn.setRequestProperty("Content-Length", String.valueOf(byteMe.length));


			OutputStream os = conn.getOutputStream();
			os.write(byteMe);
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			logger.log(Level.INFO,"Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				builder.append(output);
			}


		} catch (IOException e) {
			logger.log(Level.SEVERE,null,e);
		}finally{
			conn.disconnect();
		}

		return builder.toString();

	}

	@Override
	public String put(String json, String strurl) {
		StringBuilder builder = new StringBuilder();
		HttpURLConnection conn = null;
		OutputStream os = null;
		try {
			URL url = new URL(strurl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");

			if (this.useCreds) {
				this.applyCredentials(conn);
			}


			String input = json;

			os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			logger.log(Level.INFO,"Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				builder.append(output);
			}

			conn.disconnect();
		} catch (Exception e) {
			logger.log(Level.SEVERE,null,e);

		}finally{
			conn.disconnect();
			IOUtils.closeQuietly(os);
		}

		return builder.toString();

	}

	@Override
	public String delete(String json, String strurl) {
		StringBuilder builder = new StringBuilder();
		OutputStream os = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(strurl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("delete");
			conn.setRequestProperty("Content-Type", "application/json");

			if (this.useCreds) {
				this.applyCredentials(conn);
			}

			String input = json;

			os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			logger.log(Level.INFO,"Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				builder.append(output);
			}

			conn.disconnect();
		} catch (Exception e) {
			logger.log(Level.SEVERE,null, e);

		}finally{
			conn.disconnect();
			IOUtils.closeQuietly(os);
		}

		return builder.toString();

	}

}
