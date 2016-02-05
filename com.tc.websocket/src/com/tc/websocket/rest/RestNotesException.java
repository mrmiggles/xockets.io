
/*
 * © Copyright Tek Counsel LLC 2013
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lotus.domino.NotesException;

import com.tc.websocket.valueobjects.IPrompt;
import com.tc.websocket.valueobjects.Prompt;


@Provider
public class RestNotesException implements ExceptionMapper<NotesException> {
    
	private static final Logger logger = Logger.getLogger(RestNotesException.class.getName());


    @Override
	public Response toResponse(NotesException exception) {
        IPrompt prompt = new Prompt();
        prompt.setMessage(IPrompt.EXCEPTION,exception.text);
        logger.log(Level.SEVERE,null, exception);
        return Response.status(Response.Status.OK).entity(prompt).type(MediaType.APPLICATION_JSON).build();
    }
}