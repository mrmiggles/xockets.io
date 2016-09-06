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


// TODO: Auto-generated Javadoc
/**
 * The Interface IPrompt.
 */
public interface IPrompt {
	
	/** The Constant EXCEPTION. */
	public static final String EXCEPTION="EXCEPTION";

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public abstract String getMessage();

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public abstract String getTitle();

	/**
	 * Sets the message.
	 *
	 * @param title the title
	 * @param message the message
	 */
	public abstract void setMessage(String title, String message);

}