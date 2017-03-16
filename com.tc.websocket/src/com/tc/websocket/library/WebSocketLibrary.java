/*
 * � Copyright Tek Counsel LLC 2016
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


package com.tc.websocket.library;

import com.ibm.xsp.library.AbstractXspLibrary;


// TODO: Auto-generated Javadoc
/**
 * The Class WebSocketLibrary.
 */
public class WebSocketLibrary extends AbstractXspLibrary {


	/* (non-Javadoc)
	 * @see com.ibm.xsp.library.XspLibrary#getLibraryId()
	 */
	@Override
	public String getLibraryId() {
		return "com.tc.websocket.library";
	}
 

	/* (non-Javadoc)
	 * @see com.ibm.xsp.library.AbstractXspLibrary#getPluginId()
	 */
	@Override
	public String getPluginId() {
		return "com.tc.websocket";
	}

	

	/* (non-Javadoc)
	 * @see com.ibm.xsp.library.AbstractXspLibrary#getFacesConfigFiles()
	 */
	@Override
	public String[] getFacesConfigFiles() {
		return new String[]{
				"META-INF/socket-faces-config.xml",
		};
	}

}