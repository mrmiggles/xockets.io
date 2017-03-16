/*
 * Original Work: Copyright 2012 The Netty Project
 * 
 * Modified Work: Copyright 2016 Tek Counsel LLC
 *
 * Both licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.tc.websocket.server.handler;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateEncodingException;

import javax.security.cert.X509Certificate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.server.pipeline.IPipelineBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;


// TODO: Auto-generated Javadoc
/**
 * The Class ProxyFrontendHandler.
 */
public class ProxyFrontendHandler extends ChannelInboundHandlerAdapter {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(ProxyFrontendHandler.class.getName());	

	/** The remote host. */
	private final String remoteHost;

	/** The remote port. */
	private final int remotePort;

	/** The outbound channel. */
	private volatile Channel outboundChannel;

	/** The handler. */
	private ProxyBackendHandler handler;

	/** The builder. */
	@Inject
	@Named(Const.GUICE_WEBSOCKET_PIPELINE)
	private IPipelineBuilder builder;

	/** The guicer. */
	@Inject
	IGuicer guicer;

	/**
	 * Instantiates a new proxy frontend handler.
	 *
	 * @param remoteHost the remote host
	 * @param remotePort the remote port
	 */
	public ProxyFrontendHandler(String remoteHost, int remotePort) {
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}



	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		final Channel inboundChannel = ctx.channel();

		this.handler = new ProxyBackendHandler(inboundChannel);

		// Start the connection attempt.
		Bootstrap b = new Bootstrap();
		b.group(inboundChannel.eventLoop())
		.channel(ctx.channel().getClass())
		.handler(this.handler)
		.option(ChannelOption.AUTO_READ, false);

		ChannelFuture f = b.connect(remoteHost, remotePort);
		outboundChannel = f.channel();

		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					// connection complete start to read first data
					inboundChannel.read();

				} else {
					// Close the connection if the connection attempt has failed.
					inboundChannel.close();
				}
			}
		});

	}


	/** The proxy. */
	private AtomicBoolean proxy = new AtomicBoolean(true);



	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg){

		ByteBuf buf = (ByteBuf) msg;
		String data = new String(ByteBufUtil.getBytes(buf));

		if(data.contains(Const.UPGRADE_WEBSOCKET) || data.contains(Const.GET_WEBSOCKET)){
			proxy.set(false);

		}else if(data.contains(StringCache.HTTP_1_1)){
			proxy.set(true);
		}

		if(proxy.get()==false){
			writeToFile("frontend." + ctx.channel().id(), ByteBufUtil.getBytes(buf));
		}


		if(Config.getInstance().isCertAuth()){
			SslHandler sslhandler = (SslHandler) ctx.channel().pipeline().get("ssl");
			try {
				X509Certificate cert = sslhandler.engine().getSession().getPeerCertificateChain()[0];
				Principal p =cert.getSubjectDN();

				/* Added by Miguel */
				LdapName ldapDN = new LdapName(p.getName());
				String username = "";
		        String thumbprint = getThumbPrint(cert.getEncoded());
		
		        for(Rdn rdn: ldapDN.getRdns()) {                	
		            //System.out.println(rdn.getType() + " -> " + rdn.getValue());
		            if(rdn.getType().equals("CN")){
		            	username = rdn.getValue().toString();
		            	break;
		            }
		        }
		        /* End Added by Miguel*/
				
				String sessionId = parseSessionID(data);

				if(sessionId!=null){
			        String current = System.getProperty("user.dir");
			        //System.out.println("Current working directory in Java : " + current);

					//File sessionFile = new File("c:/sessions/" + sessionId + ".txt");
					File sessionFile = new File(current + "/data/sessions/" + sessionId + ".txt");

					//only write the file if it hasn't been written yet.
					if(sessionFile.createNewFile()){
						FileUtils.write(sessionFile, p.getName().replaceAll("\"","").replaceAll("\\+", ",") + "\n" + username + "\n" + thumbprint);	
					}				
				}

			} catch (Exception e) {
				LOG.log(Level.SEVERE,null, e);
			}
		}

		if(proxy.get()){
			ctx.channel().config().setAutoRead(false);
			if (outboundChannel.isActive()) {
				outboundChannel.writeAndFlush(buf).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) {
						if (future.isSuccess()) {
							// was able to flush out data, start to read the next chunk
							ctx.channel().read();
						} else {
							future.channel().close();
						}
					}
				});
			}
		}else{
			//make sure the backend handler knows its a websocket connection.
			this.handler.setWebsocket(true);

			//get handle on the pipeline.
			ChannelPipeline pipeline = ctx.pipeline();

			//apply the websocket handlers
			builder.apply(pipeline);

			//remove this handler.
			pipeline.remove(this);

			//fire the event to move on to the next handler.
			ctx.fireChannelRead(msg);
		}

	}
	
	public static String getThumbPrint(byte[] der) throws NoSuchAlgorithmException, CertificateEncodingException {
	    MessageDigest md = MessageDigest.getInstance("SHA-1");
	    md.update(der);
	    byte[] digest = md.digest();
	    return hexify(digest);
	
	}
	
	public static String hexify (byte bytes[]) {
	
	    char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', 
	            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	    StringBuffer buf = new StringBuffer(bytes.length * 2);
	
	    for (int i = 0; i < bytes.length; ++i) {
	        buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
	        buf.append(hexDigits[bytes[i] & 0x0f]);
	    }
	
	    return buf.toString();
	}		




	/**
	 * Write to file.
	 *
	 * @param prefix the prefix
	 * @param bytes the bytes
	 */
	public static void writeToFile(String prefix, byte[] bytes){
		/*
		if(Config.getInstance().isDebug()){
			OutputStream out = null;
			try {
				out = new FileOutputStream("c:/temp/" + prefix + "-" + new Date().getTime() + ".txt");
				out.write(bytes);
				out.close();

			} catch (Exception e) {
				LOG.log(Level.SEVERE, null,e);
			}finally{
				IOUtils.closeQuietly(out);
			}
		}
		 */
	}




	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		if (outboundChannel != null) {
			closeOnFlush(outboundChannel);
		}
	}



	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if(cause.getMessage().startsWith(Const.ESTABLISHED_CONN_ERR)){
			LOG.log(Level.FINE, null, cause);
		}else{
			LOG.log(Level.SEVERE, null, cause);
		}
		closeOnFlush(ctx.channel());
	}

	/**
	 * Closes the specified channel after all queued write requests are flushed.
	 *
	 * @param ch the ch
	 */
	static void closeOnFlush(Channel ch) {
		if (ch.isActive()) {
			ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static String parseSessionID(String data){
		//return StrUtils.parseCookie(data).get("SessionID");
		String[] req = data.split("\r\n");
		Map<String,String> cookies = new HashMap<String,String>();
		
		for(String str : req){
			if(str.toLowerCase().contains("cookie")){
				str = str.substring(str.indexOf(":") + 1, str.length());
				String[] pairs =str.split(";");
				for(String nv : pairs){
					String[] nameValue = nv.trim().split("=");
					cookies.put(nameValue[0], nameValue[1]);
				}				
			}
		}		
		return cookies.get("SessionID");		
	}


}