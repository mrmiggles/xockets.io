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

import lotus.domino.NotesException;

import com.tc.websocket.valueobjects.SocketMessage;


	@Path("websocket/v1")
	public interface IRestWebSocket {
		
		
	    @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("registeruser")
	    public Response register() throws NotesException;

	    
	    
	    @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("removeuser")
	    public Response unregister() throws NotesException;

	    
	    
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("onlineusers")
	    public Response getOnlineUsers() throws NotesException;
	    
	    
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("websocketurl")
	    public Response getWebSocketUrl() throws NotesException;
	    
	    
	    
	    @POST
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Path("sendmessage")
	    public Response sendMessage(SocketMessage msg) throws NotesException;
	    
	    
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("sendsimple")
	    public Response sendMessage(
	    		@QueryParam("from") String from, 
	    		@QueryParam("to") String to, 
	    		@QueryParam("text") String text) throws NotesException;
	    
	    
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("messages")
	    public Response getMessages() throws NotesException;
	    
	    
	    @GET
	    @Produces(MediaType.APPLICATION_JSON)
	    @Path("latestmessage")
	    public Response getLatestMessage() throws NotesException;
	    

}
