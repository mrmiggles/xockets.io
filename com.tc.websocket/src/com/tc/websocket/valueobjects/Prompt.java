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


package com.tc.websocket.valueobjects;

import javax.xml.bind.annotation.XmlRootElement;




// TODO: Auto-generated Javadoc
/**
 * The Class Prompt.
 */
@XmlRootElement
public class Prompt implements IPrompt {
	
	/** The Constant EXCEPTION. */
	public static final String EXCEPTION="EXCEPTION";
	
	/** The Constant INFO. */
	public static final String INFO="INFO";
	
	/** The Constant WARNING. */
	public static final String WARNING="WARNING";
	
	/** The message. */
	private String message;
	
	/** The title. */
	private String title;
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IPrompt#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	

	public void setMessage(String message) {
		this.message = message;
	}
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IPrompt#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	

	public void setTitle(String title) {
		this.title = title;
	}
	
	

	/* (non-Javadoc)
	 * @see com.tc.websocket.valueobjects.IPrompt#setMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void setMessage(String title, String message){
		this.title=title;
		this.message = message;
	}

}
