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


package com.tc.websocket.factories;

import lotus.domino.Document;

import com.tc.websocket.valueobjects.IUser;


public interface IUserFactory {

	public abstract IUser createUser(String sessionId, String userId, String status);
	
	//public abstract IUser createUser(String sessionId, String userId, String appPath, String status);
	
	public abstract IUser createUser(Document doc);

}