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


package com.tc.websocket;

public class Const {
	
	//websocket url constants.
	public static final String ON_LOGIN="ON_LOGIN";
	public static final String WS="ws://";
	public static final String WSS="wss://";
	public static final String WEBSOCKET_PATH="websocket.nsf";
	public static final String WEBSOCKET_URI="websocket";
	public static final String PROFILE_FORM="fmConfig";
	public static final String PROFILE_KEY="websocket";
	public static final String PROFILE_FIELD="config";
	
	//view references
	public static final String VIEW_MSG_QUEUE="vSocketMessageQueue";
	public static final String VIEW_BROADCAST_QUEUE="vBroadcastMessageQueue";
	public static final String VIEW_USERS_BY_STATUS="vUsersByStatus";
	public static final String VIEW_ON_OPEN_QUEUE="vOnOpenQueue";
	public static final String VIEW_ON_RECEIVE_MSG="vOnReceiveMessage";
	public static final String VIEW_ON_SEND_MSG="vOnSendMessage";
	public static final String VIEW_USERS="vUsers";
	public static final String VIEW_SESSIONS="vSessions";
	public static final String VIEW_SERVER_STATUS="vServerStatus";
	public static final String VIEW_MESSAGES_BY_USER="vMessagesByUser";
	

	//default values if not present in notes.ini
	public static final int WEBSOCKET_MAX_MSG_SIZE=4096;
	public static final int WEBSOCKET_THREAD_COUNT = 1;
	public static final int WEBSOCKET_EVENT_LOOP_THREADS= 1;
	public static final int WEBSOCKET_PING_INTERVAL=60;
	public static final int WEBSOCKET_PURGE_INTERVAL=900;
	public static final int TEMP_FILE_PURGE_INTERVAL=1800;
	public static final int WEBSOCKET_PORT=8889;
	public static final int WEBSOCKET_MAX_CONNECTIONS=200;
	public static final int WEBSOCKET_SEND_BUFFER=8192;
	public static final int WEBSOCKET_RECEIVE_BUFFER=8192;
	public static final boolean WEBSOCKET_ENCRYPT=false;
	public static final boolean WEBSOCKET_CLUSTERED=false;
	public static final boolean WEBSOCKET_ALLOW_ANONYMOUS=false;
	public static final boolean WEBSOCKET_TEST_MODE = false;
	public static final boolean WEBSOCKET_DEBUG=false;
	public static final boolean WEBSOCKET_COMPRESSION_ENABLED=false;
	
	
	//user and server state constants.
	public static final String STATUS_ONLINE="ONLINE";
	public static final String STATUS_OFFLINE="OFFLINE";
	
	//other :)
	public static final String WEBSOCKET_BEAN="websocketBean";
	public static final String BROADCAST="BROADCAST";

	
	//thread execution interval in seconds.
	public static final int CLUSTERMATE_MONITOR_INTERVAL=10;
	public static final int QUEUE_PROCESSOR_INTERVAL=500;
	public static final int BROADCAST_QUEUE_PROCESSOR_INTERVAL=500;
	public static final int USER_MONITOR_INTERVAL=1;
	public static final long PING_INTERVAL_MINUTES=1;
	public static final int USER_CLEANUP_INTERVAL=30;
	
	
	
	//field definitions
	public static final String FIELD_STATUS="status";
	public static final String FIELD_HOST="host";
	public static final String FIELD_USERID="userId";
	public static final String FIELD_SESSIONID="sessionId";
	public static final String FIELD_JSON="json";
	public static final String FIELD_URI="uri";
	public static final String ATTACH_NAME="message.json";
	
	
	//message events.
	public static final String EVENT_ONOPEN="onopen";
	public static final String EVENT_ONSEND="onsend";
	public static final String EVENT_ONRECEIVE="onreceive";
	
	//guice keys
	public static final String GUICE_REST_WEBSOCKET="GUICE_REST_WEBSOCKET";
	public static final String GUICE_JSF_WEBSOCKET="GUICE_JSF_WEBSOCKET";
	public static final String GUICE_EVENTLOOP_BOSS="GUICE_EVENTLOOP_BOSS";
	public static final String GUICE_EVENTLOOP_WORKER="GUICE_EVENTLOOP_WORKER";
	
	
	public static final String RHINO_PREFIX= "rhino.";
	

}
