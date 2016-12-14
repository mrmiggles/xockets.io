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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.tc.websocket.valueobjects.SocketMessage;

import lotus.domino.NotesException;


	
// TODO: Auto-generated Javadoc
/**
	 * The Interface IRestWebSocket.
	 */
	@Path("websocket/v1")
	public interface IRestWebSocket {
		
		
	    /**
    	 * Register.
    	 *
    	 * @return the response
    	 * @throws NotesException the notes exception
    	 */
    	@POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("registeruser")
	    public Response register() throws NotesException;

	    
	    
	    /**
    	 * Unregister.
    	 *
    	 * @return the response
    	 * @throws NotesException the notes exception
    	 */
    	@POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("removeuser")
	    public Response unregister() throws NotesException;

	    
	    
	    /**
    	 * Gets the online users.
    	 *
    	 * @return the online users
    	 * @throws NotesException the notes exception
    	 */
    	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("onlineusers")
	    public Response getOnlineUsers() throws NotesException;
	    
	    
	    /**
    	 * Gets the web socket url.
    	 *
    	 * @return the web socket url
    	 * @throws NotesException the notes exception
    	 */
    	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("websocketurl")
	    public Response getWebSocketUrl() throws NotesException;
	    
	    
	    
	    /**
    	 * Send message.
    	 *
    	 * @param msg the msg
    	 * @return the response
    	 * @throws NotesException the notes exception
    	 */
    	@POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Path("sendmessage")
	    public Response sendMessage(SocketMessage msg) throws NotesException;
	    
	    
	    /**
    	 * Send message.
    	 *
    	 * @param from the from
    	 * @param to the to
    	 * @param text the text
    	 * @return the response
    	 * @throws NotesException the notes exception
    	 */
    	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("sendsimple")
	    public Response sendMessage(
	    		@QueryParam("from") String from, 
	    		@QueryParam("to") String to, 
	    		@QueryParam("text") String text) throws NotesException;
	    
	    
	    /**
    	 * Gets the messages.
    	 *
    	 * @return the messages
    	 * @throws NotesException the notes exception
    	 */
    	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("messages")
	    public Response getMessages() throws NotesException;
	    
	    
	    /**
    	 * Gets the latest message.
    	 *
    	 * @return the latest message
    	 * @throws NotesException the notes exception
    	 */
    	@GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("latestmessage")
	    public Response getLatestMessage() throws NotesException;
	    

}
