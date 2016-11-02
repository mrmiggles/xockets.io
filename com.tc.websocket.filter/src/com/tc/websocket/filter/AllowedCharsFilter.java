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

package com.tc.websocket.filter;

import com.tc.utils.JSONUtils;
import com.tc.utils.StrUtils;
import com.tc.websocket.valueobjects.SocketMessage;

public class AllowedCharsFilter implements IWebsocketFilter {

	//just an example...
	private static final char[] ALLOWED_CHARS="=.{}'\"/,;:ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@_-|()?! \n\r".toCharArray();


	@Override
	public SocketMessage applyFilter(SocketMessage msg) {
		String json =  StrUtils.whiteList(JSONUtils.toJson(msg), ALLOWED_CHARS);
		return JSONUtils.toObject(json, SocketMessage.class);
	}
}
