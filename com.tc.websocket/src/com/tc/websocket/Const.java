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
	public static final String UPGRADE_WEBSOCKET="Upgrade: websocket";
	public static final String GET_WEBSOCKET="GET /" + Const.WEBSOCKET_URI;
	public static final String PARTIAL_GET_WEBSOCKET="G";
	
	
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
	public static final String VIEW_SERVERS="($Servers)";
	public static final String VIEW_CLUSTERS="($Clusters)";
	

	//default values if not present in notes.ini
	public static final int WEBSOCKET_MAX_MSG_SIZE=4096;
	public static final int WEBSOCKET_THREAD_COUNT = 1;
	public static final int WEBSOCKET_EVENT_LOOP_THREADS=2;
	public static final int WEBSOCKET_PING_INTERVAL=60;
	public static final int WEBSOCKET_PURGE_INTERVAL=900;
	public static final int TEMP_FILE_PURGE_INTERVAL=1800;
	public static final int WEBSOCKET_PORT=8889;
	public static final int WEBSOCKET_MAX_CONNECTIONS=200;
	public static final int WEBSOCKET_SEND_BUFFER=8192;
	public static final int WEBSOCKET_RECEIVE_BUFFER=8192;
	public static final boolean WEBSOCKET_ENCRYPT=false;
	public static final boolean WEBSOCKET_CLUSTERED=false;
	public static final boolean WEBSOCKET_ALLOW_ANONYMOUS=true;
	public static final boolean WEBSOCKET_TEST_MODE = false;
	public static final boolean WEBSOCKET_DEBUG=false;
	public static final boolean WEBSOCKET_COMPRESSION_ENABLED=false;
	public static final String WEBSOCKET_ALLOWED_ORIGINS="*";
	
	
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
	public static final String FIELD_ERROR="error";
	public static final String FIELD_SENTFLAG="sentFlag";
	public static final int FIELD_SENTFLAG_VALUE_ERROR=-1;
	public static final int FIELD_SENTFLAG_VALUE_SENT=1;
	
	public static final String FIELD_TO="to";
	public static final String FIELD_FROM="from";
	public static final String FIELD_FORM="Form";
	public static final String FIELD_VALUE_DELETE="delete";
	public static final String FIELD_VALUE_USER="fmUser";
	public static final String FIELD_CLUSTERNAME="ClusterName";
	public static final String FIELD_SERVERNAME="ServerName";
	
	
	//message events.
	public static final String EVENT_ONOPEN="onopen";
	public static final String EVENT_ONSEND="onsend";
	public static final String EVENT_ONRECEIVE="onreceive";
	
	//guice keys
	public static final String GUICE_REST_WEBSOCKET="GUICE_REST_WEBSOCKET";
	public static final String GUICE_JSF_WEBSOCKET="GUICE_JSF_WEBSOCKET";
	public static final String GUICE_EVENTLOOP_BOSS="GUICE_EVENTLOOP_BOSS";
	public static final String GUICE_EVENTLOOP_WORKER="GUICE_EVENTLOOP_WORKER";
	public static final String GUICE_WEBSOCKET_PIPELINE="GUICE_WEBSOCKET_PIPELINE";
	public static final String GUICE_REDIRECT_PIPELINE="GUICE_REDIRECT_PIPELINE";
	
	
	//random
	public static final String RHINO_PREFIX= "rhino";
	public static final String ESTABLISHED_CONN_ERR="An established connection was aborted";
	public static final String[] TLS_PROTOCOLS=new String[]{"TLS","TLSv1","TLSv1.1","TLSv1.2"};
	public static final String FROM_SERVER="server";
	public static final String DATE_FORMAT="yyyy-MM-dd hh:mm a";
	public static final String EXCEPTION="EXCEPTION";
	public static final String INFO = "INFO";
	
	
	//RhinoClient scoped variable names.
	public static final String RHINO_EVENT="event";
	public static final String RHINO_SOCKET_MESSAGE="socketMessage";
	public static final String RHINO_EX="EX";
	public static final String RHINO_SESSION="session";
	public static final String RHINO_WEB_SOCKET_CLIENT="websocketClient";
	public static final String RHINO_BUNDLE_UTIL="bundleUtils";
	public static final String RHINO_CACHE="cache";
	public static final String RHINO_HANDSHAKE="handShake";
	public static final String RHINO_DOC_SRCH="@Contains(sessionId;\"rhino\");";
	
	
	//jsf object keys
	public static final String SOURCE_URI="sourceUri";
	
	
	
	
}
