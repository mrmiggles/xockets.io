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



package com.tc.websocket.rest;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;

import com.ibm.domino.das.service.RestService;
import com.tc.di.guicer.Guicer;
import com.tc.di.guicer.IGuicer;
import com.tc.websocket.Activator;



// TODO: Auto-generated Javadoc
/**
 * The Class WebSocketService.
 */
public class WebSocketService extends RestService {
	
	/** The singletons. */
	private HashSet<Object> singletons = new HashSet<Object>();
	
	/** The classes. */
	private Set<Class<?>> classes = new HashSet<Class<?>>();


	/**
	 * Instantiates a new web socket service.
	 */
	public WebSocketService(){

	}
	


	/* (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public HashSet<Object> getSingletons(){
		ObjectMapper mapper = new ObjectMapper();
		JacksonJaxbJsonProvider jaxbProvider = new JacksonJaxbJsonProvider();
	    jaxbProvider.setMapper(mapper);
	    singletons.add(jaxbProvider);

		if(Activator.bundle==null){
			throw new IllegalArgumentException("WebSocketService.BUNDLE is null!");
		}
		
		IGuicer guicer = Guicer.getInstance(Activator.bundle);
		
		//create the rest object make sure dependencies are injected via guice.
		IRestWebSocket restWebSocket = guicer.createObject(IRestWebSocket.class);
		
		
		if(restWebSocket==null){
			throw new IllegalArgumentException("restWebSocket is null!!!");
		}
		
		//add it
	    singletons.add(restWebSocket);
 
		return singletons;

	}



	/* (non-Javadoc)
	 * @see org.apache.wink.common.WinkApplication#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses(){
		classes.add(RestArgumentException.class);
		classes.add(RestNotesException.class);
		return classes;
	}



}
