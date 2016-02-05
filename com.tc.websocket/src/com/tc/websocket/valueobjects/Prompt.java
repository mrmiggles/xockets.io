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



@XmlRootElement
public class Prompt implements IPrompt {
	
	public static final String EXCEPTION="EXCEPTION";
	public static final String INFO="INFO";
	public static final String WARNING="WARNING";
	
	private String message;
	
	private String title;
	
	/* (non-Javadoc)
	 * @see com.tc.apn.push.service.rest.IPrompt#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}
	/* (non-Javadoc)
	 * @see com.tc.apn.push.service.rest.IPrompt#setMessage(java.lang.String)
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see com.tc.apn.push.service.rest.IPrompt#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}
	/* (non-Javadoc)
	 * @see com.tc.apn.push.service.rest.IPrompt#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/* (non-Javadoc)
	 * @see com.tc.apn.push.service.rest.IPrompt#setMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void setMessage(String title, String message){
		this.title=title;
		this.message = message;
	}

}
