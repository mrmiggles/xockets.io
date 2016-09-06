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

package com.tc.websocket.guice;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.tc.di.guicer.Guicer;
import com.tc.di.guicer.IGuicer;
import com.tc.utils.BundleUtils;
import com.tc.utils.StrUtils;
import com.tc.utils.StringCache;
import com.tc.websocket.Activator;
import com.tc.websocket.Config;
import com.tc.websocket.Const;
import com.tc.websocket.IConfig;
import com.tc.websocket.ISSLFactory;
import com.tc.websocket.SSLFactory;
import com.tc.websocket.factories.ISocketMessageFactory;
import com.tc.websocket.factories.IUserFactory;
import com.tc.websocket.factories.SocketMessageFactory;
import com.tc.websocket.factories.UserFactory;
import com.tc.websocket.filter.IWebsocketFilter;
import com.tc.websocket.jsf.IWebSocketBean;
import com.tc.websocket.jsf.WebSocketBean;
import com.tc.websocket.rest.IRestWebSocket;
import com.tc.websocket.rest.RestWebSocket;
import com.tc.websocket.rest.RestWebSocketBean;
import com.tc.websocket.runners.TaskRunner;
import com.tc.websocket.server.DominoWebSocketServer;
import com.tc.websocket.server.IDominoWebSocketServer;
import com.tc.websocket.valueobjects.IUser;
import com.tc.websocket.valueobjects.User;



// TODO: Auto-generated Javadoc
/**
 * The Class DominoWebSocketModule.
 */
public class DominoWebSocketModule extends AbstractModule {

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(DominoWebSocketModule.class.getName());

	/** The server. */
	private static IDominoWebSocketServer server;
	
	/** The config. */
	private static IConfig config = Config.getInstance();
	
	/** The filter. */
	private static IWebsocketFilter filter;



	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {

		try{
	        
			bind(IUserFactory.class).to(UserFactory.class).in(Singleton.class);
			bind(ISocketMessageFactory.class).to(SocketMessageFactory.class).in(Singleton.class);

			bind(DominoWebSocketServer.class);

			bind(IUser.class).to(User.class);

			//bind the xpage/jsf bean
			bind(IWebSocketBean.class).annotatedWith(Names.named(Const.GUICE_JSF_WEBSOCKET)).to(WebSocketBean.class).in(Singleton.class);

			//bind the rest bean (not singleton... per request);
			bind(IWebSocketBean.class).annotatedWith(Names.named(Const.GUICE_REST_WEBSOCKET)).to(RestWebSocketBean.class);

			//setup the restful bindings
			bind(IRestWebSocket.class).to(RestWebSocket.class);

			bind(ISSLFactory.class).to(SSLFactory.class).in(Singleton.class);

		}catch(Exception e){
			LOG.log(Level.SEVERE,null,e);
		}

	}



	/**
	 * Provide guicer.
	 *
	 * @return the i guicer
	 */
	@Provides
	public IGuicer provideGuicer(){
		return Guicer.getInstance(Activator.bundle);
	}


	/**
	 * Provide config.
	 *
	 * @return the i config
	 */
	@Provides
	public IConfig provideConfig(){
		return config;
	}


	/**
	 * Provide filter.
	 *
	 * @return the i websocket filter
	 */
	@Provides
	public synchronized IWebsocketFilter provideFilter(){

		if(filter==null){
			String websocketFilter = config.getWebsocketFilter();
			if(!StrUtils.isEmpty(websocketFilter)){
				if(websocketFilter.contains(StringCache.COMMA)){
					String[] arr  =  websocketFilter.split(StringCache.COMMA);
					String bundle  = arr[0];
					String className = arr[1];
					filter = BundleUtils.load(bundle, className);
				}else{
					try {
						filter = (IWebsocketFilter) Class.forName(websocketFilter).newInstance();
					} catch (Exception e) {
						LOG.log(Level.SEVERE,null, e);
					} 
				}
			}
		}


		return filter;
	}



	/**
	 * Nullify server.
	 */
	public static void nullifyServer(){
		if(server.isOn()){
			throw new RuntimeException("server must be turned off first.");
		}
		server = null;
	}


	/**
	 * Provide web socket server.
	 *
	 * @param cfg the cfg
	 * @param filter the filter
	 * @return the i domino web socket server
	 */
	@Provides
	public synchronized IDominoWebSocketServer provideWebSocketServer(IConfig cfg, IWebsocketFilter filter){
		try{
			if(server==null){
				IDominoWebSocketServer domserver = Guicer.getInstance(Activator.bundle).createObject(DominoWebSocketServer.class);


				//apply the filter
				domserver.setFilter(filter);


				if(config.isEncrypted()){
					startSSLServer(domserver);
				}else{
					startServer(domserver);
				}


				//now lets init the taskrunner
				TaskRunner.getInstance();

				server = domserver;
			}
		}catch(Exception e){
			LOG.log(Level.SEVERE,null, e);
		}
		return server;
	}


	/**
	 * Start server.
	 *
	 * @param server the server
	 * @throws Exception the exception
	 */
	private void startServer(IDominoWebSocketServer server) throws Exception{
		if(config.getPort()<=0){
			throw new IllegalArgumentException("WEBSOCKET_PORT not configured in notes.ini");	
		}
		server.start();
	}


	/**
	 * Start SSL server.
	 *
	 * @param server the server
	 */
	private void startSSLServer(IDominoWebSocketServer server){


		if(config.getPort()==0){
			throw new IllegalArgumentException("WEBSOCKET_PORT not configured in notes.ini");	
		}

		try{
			if(config.isKeyStore()){
				if (StrUtils.isEmpty(config.getKeyStoreType())){
					throw new IllegalArgumentException("WEBSOCKET_KEYSTORE_TYPE not configured in notes.ini");

				}else if(StrUtils.isEmpty(config.getKeyStore())){
					throw new IllegalArgumentException("WEBSOCKET_KEYSTORE not configured in notes.ini. Please add the full path the key store file.");

				}else if(StrUtils.isEmpty(config.getKeyStorePassword())){
					throw new IllegalArgumentException("WEBSOCKET_KEYSTORE_PASSWORD not configured in notes.ini.");

				}else if(StrUtils.isEmpty(config.getKeyPassword())){
					throw new IllegalArgumentException("WEBSOCKET_KEY_PASSWORD not configured in notes.ini.");
				}
			}
			server.start();

		}catch(Exception e){
			LOG.log(Level.SEVERE,null, e);
		}
	}

}
