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


// TODO: Auto-generated Javadoc
/**
 * The Class Const.
 */
public class Const {
	
	/** The Constant ON_LOGIN. */
	//websocket url constants.
	public static final String ON_LOGIN="ON_LOGIN";
	
	/** The Constant WS. */
	public static final String WS="ws://";
	
	/** The Constant WSS. */
	public static final String WSS="wss://";
	
	/** The Constant WEBSOCKET_PATH. */
	public static final String WEBSOCKET_PATH="websocket.nsf";
	
	/** The Constant WEBSOCKET_URI. */
	public static final String WEBSOCKET_URI="websocket";
	
	/** The Constant PROFILE_FORM. */
	public static final String PROFILE_FORM="fmConfig";
	
	/** The Constant PROFILE_KEY. */
	public static final String PROFILE_KEY="websocket";
	
	/** The Constant PROFILE_FIELD. */
	public static final String PROFILE_FIELD="config";
	
	/** The Constant UPGRADE_WEBSOCKET. */
	public static final String UPGRADE_WEBSOCKET="Upgrade: websocket";
	
	/** The Constant GET_WEBSOCKET. */
	public static final String GET_WEBSOCKET="GET /" + Const.WEBSOCKET_URI;
	
	/** The Constant PARTIAL_GET_WEBSOCKET. */
	public static final String PARTIAL_GET_WEBSOCKET="G";
	
	
	/** The Constant VIEW_MSG_QUEUE. */
	//view references
	public static final String VIEW_MSG_QUEUE="vSocketMessageQueue";
	
	/** The Constant VIEW_BROADCAST_QUEUE. */
	public static final String VIEW_BROADCAST_QUEUE="vBroadcastMessageQueue";
	
	/** The Constant VIEW_USERS_BY_STATUS. */
	public static final String VIEW_USERS_BY_STATUS="vUsersByStatus";
	
	/** The Constant VIEW_ON_OPEN_QUEUE. */
	public static final String VIEW_ON_OPEN_QUEUE="vOnOpenQueue";
	
	/** The Constant VIEW_ON_RECEIVE_MSG. */
	public static final String VIEW_ON_RECEIVE_MSG="vOnReceiveMessage";
	
	/** The Constant VIEW_ON_SEND_MSG. */
	public static final String VIEW_ON_SEND_MSG="vOnSendMessage";
	
	/** The Constant VIEW_USERS. */
	public static final String VIEW_USERS="vUsers";
	
	/** The Constant VIEW_SESSIONS. */
	public static final String VIEW_SESSIONS="vSessions";
	
	/** The Constant VIEW_SERVER_STATUS. */
	public static final String VIEW_SERVER_STATUS="vServerStatus";
	
	/** The Constant VIEW_MESSAGES_BY_USER. */
	public static final String VIEW_MESSAGES_BY_USER="vMessagesByUser";
	
	/** The Constant VIEW_SERVERS. */
	public static final String VIEW_SERVERS="($Servers)";
	
	/** The Constant VIEW_CLUSTERS. */
	public static final String VIEW_CLUSTERS="($Clusters)";
	

	/** The Constant WEBSOCKET_MAX_MSG_SIZE. */
	//default values if not present in notes.ini
	public static final int WEBSOCKET_MAX_MSG_SIZE=4096;
	
	/** The Constant WEBSOCKET_THREAD_COUNT. */
	public static final int WEBSOCKET_THREAD_COUNT = 1;
	
	/** The Constant WEBSOCKET_EVENT_LOOP_THREADS. */
	public static final int WEBSOCKET_EVENT_LOOP_THREADS=2;
	
	/** The Constant WEBSOCKET_PING_INTERVAL. */
	public static final int WEBSOCKET_PING_INTERVAL=60;
	
	/** The Constant WEBSOCKET_PURGE_INTERVAL. */
	public static final int WEBSOCKET_PURGE_INTERVAL=900;
	
	/** The Constant TEMP_FILE_PURGE_INTERVAL. */
	public static final int TEMP_FILE_PURGE_INTERVAL=1800;
	
	/** The Constant WEBSOCKET_PORT. */
	public static final int WEBSOCKET_PORT=8889;
	
	/** The Constant WEBSOCKET_MAX_CONNECTIONS. */
	public static final int WEBSOCKET_MAX_CONNECTIONS=200;
	
	/** The Constant WEBSOCKET_SEND_BUFFER. */
	public static final int WEBSOCKET_SEND_BUFFER=8192;
	
	/** The Constant WEBSOCKET_RECEIVE_BUFFER. */
	public static final int WEBSOCKET_RECEIVE_BUFFER=8192;
	
	/** The Constant WEBSOCKET_ENCRYPT. */
	public static final boolean WEBSOCKET_ENCRYPT=false;
	
	/** The Constant WEBSOCKET_CLUSTERED. */
	public static final boolean WEBSOCKET_CLUSTERED=false;
	
	/** The Constant WEBSOCKET_ALLOW_ANONYMOUS. */
	public static final boolean WEBSOCKET_ALLOW_ANONYMOUS=true;
	
	/** The Constant WEBSOCKET_TEST_MODE. */
	public static final boolean WEBSOCKET_TEST_MODE = false;
	
	/** The Constant WEBSOCKET_DEBUG. */
	public static final boolean WEBSOCKET_DEBUG=false;
	
	/** The Constant WEBSOCKET_COMPRESSION_ENABLED. */
	public static final boolean WEBSOCKET_COMPRESSION_ENABLED=false;
	
	/** The Constant WEBSOCKET_ALLOWED_ORIGINS. */
	public static final String WEBSOCKET_ALLOWED_ORIGINS="*";
	
	
	/** The Constant STATUS_ONLINE. */
	//user and server state constants.
	public static final String STATUS_ONLINE="ONLINE";
	
	/** The Constant STATUS_OFFLINE. */
	public static final String STATUS_OFFLINE="OFFLINE";
	
	/** The Constant WEBSOCKET_BEAN. */
	//other :)
	public static final String WEBSOCKET_BEAN="websocketBean";
	
	/** The Constant BROADCAST. */
	public static final String BROADCAST="BROADCAST";

	
	/** The Constant CLUSTERMATE_MONITOR_INTERVAL. */
	//thread execution interval in seconds.
	public static final int CLUSTERMATE_MONITOR_INTERVAL=10;
	
	/** The Constant QUEUE_PROCESSOR_INTERVAL. */
	public static final int QUEUE_PROCESSOR_INTERVAL=500;
	
	/** The Constant BROADCAST_QUEUE_PROCESSOR_INTERVAL. */
	public static final int BROADCAST_QUEUE_PROCESSOR_INTERVAL=500;
	
	/** The Constant USER_MONITOR_INTERVAL. */
	public static final int USER_MONITOR_INTERVAL=1;
	
	/** The Constant PING_INTERVAL_MINUTES. */
	public static final long PING_INTERVAL_MINUTES=1;
	
	/** The Constant USER_CLEANUP_INTERVAL. */
	public static final int USER_CLEANUP_INTERVAL=30;
	
	
	
	/** The Constant FIELD_STATUS. */
	//field definitions
	public static final String FIELD_STATUS="status";
	
	/** The Constant FIELD_HOST. */
	public static final String FIELD_HOST="host";
	
	/** The Constant FIELD_USERID. */
	public static final String FIELD_USERID="userId";
	
	/** The Constant FIELD_SESSIONID. */
	public static final String FIELD_SESSIONID="sessionId";
	
	/** The Constant FIELD_JSON. */
	public static final String FIELD_JSON="json";
	
	/** The Constant FIELD_URI. */
	public static final String FIELD_URI="uri";
	
	/** The Constant ATTACH_NAME. */
	public static final String ATTACH_NAME="message.json";
	
	/** The Constant FIELD_ERROR. */
	public static final String FIELD_ERROR="error";
	
	/** The Constant FIELD_SENTFLAG. */
	public static final String FIELD_SENTFLAG="sentFlag";
	
	/** The Constant FIELD_SENTFLAG_VALUE_ERROR. */
	public static final int FIELD_SENTFLAG_VALUE_ERROR=-1;
	
	/** The Constant FIELD_SENTFLAG_VALUE_SENT. */
	public static final int FIELD_SENTFLAG_VALUE_SENT=1;
	
	/** The Constant FIELD_TO. */
	public static final String FIELD_TO="to";
	
	/** The Constant FIELD_FROM. */
	public static final String FIELD_FROM="from";
	
	/** The Constant FIELD_FORM. */
	public static final String FIELD_FORM="Form";
	
	/** The Constant FIELD_VALUE_DELETE. */
	public static final String FIELD_VALUE_DELETE="delete";
	
	/** The Constant FIELD_VALUE_USER. */
	public static final String FIELD_VALUE_USER="fmUser";
	
	/** The Constant FIELD_CLUSTERNAME. */
	public static final String FIELD_CLUSTERNAME="ClusterName";
	
	/** The Constant FIELD_SERVERNAME. */
	public static final String FIELD_SERVERNAME="ServerName";
	
	
	/** The Constant EVENT_ONOPEN. */
	//message events.
	public static final String EVENT_ONOPEN="onopen";
	
	/** The Constant EVENT_ONSEND. */
	public static final String EVENT_ONSEND="onsend";
	
	/** The Constant EVENT_ONRECEIVE. */
	public static final String EVENT_ONRECEIVE="onreceive";
	
	
	/** The Constant ON_OPEN. */
	//server-side events
	public static final String ON_OPEN="onOpen";
	
	/** The Constant ON_CLOSE. */
	public static final String ON_CLOSE="onClose";
	
	/** The Constant ON_MESSAGE. */
	public static final String ON_MESSAGE="onMessage";
	
	/** The Constant ON_ERROR. */
	public static final String ON_ERROR="onError";
	
	/** The Constant ON_INTERVAL. */
	public static final String ON_INTERVAL="onInterval";
	
	/** The Constant ALL_EVENTS. */
	public static final String[] ALL_EVENTS={ON_OPEN,ON_CLOSE,ON_MESSAGE,ON_ERROR,ON_INTERVAL};
	
	
	/** The Constant GUICE_REST_WEBSOCKET. */
	//guice keys
	public static final String GUICE_REST_WEBSOCKET="GUICE_REST_WEBSOCKET";
	
	/** The Constant GUICE_JSF_WEBSOCKET. */
	public static final String GUICE_JSF_WEBSOCKET="GUICE_JSF_WEBSOCKET";
	
	/** The Constant GUICE_EVENTLOOP_BOSS. */
	public static final String GUICE_EVENTLOOP_BOSS="GUICE_EVENTLOOP_BOSS";
	
	/** The Constant GUICE_EVENTLOOP_WORKER. */
	public static final String GUICE_EVENTLOOP_WORKER="GUICE_EVENTLOOP_WORKER";
	
	/** The Constant GUICE_WEBSOCKET_PIPELINE. */
	public static final String GUICE_WEBSOCKET_PIPELINE="GUICE_WEBSOCKET_PIPELINE";
	
	/** The Constant GUICE_REDIRECT_PIPELINE. */
	public static final String GUICE_REDIRECT_PIPELINE="GUICE_REDIRECT_PIPELINE";
	
	
	/** The Constant RHINO_PREFIX. */
	//random
	public static final String RHINO_PREFIX= "rhino";
	
	/** The Constant ESTABLISHED_CONN_ERR. */
	public static final String ESTABLISHED_CONN_ERR="An established connection was aborted";
	
	/** The Constant TLS_PROTOCOLS. */
	public static final String[] TLS_PROTOCOLS=new String[]{"TLS","TLSv1","TLSv1.1","TLSv1.2"};
	
	/** The Constant FROM_SERVER. */
	public static final String FROM_SERVER="server";
	
	/** The Constant DATE_FORMAT. */
	public static final String DATE_FORMAT="yyyy-MM-dd hh:mm a";
	
	/** The Constant EXCEPTION. */
	public static final String EXCEPTION="EXCEPTION";
	
	/** The Constant INFO. */
	public static final String INFO = "INFO";
	
	/** The Constant FUNCTION. */
	public static final String FUNCTION="function";
	
	
	
	/** The Constant VAR_EVENT. */
	//scoped variable names for scripting runtimes
	public static final String VAR_EVENT="event";
	
	/** The Constant VAR_SOCKET_MESSAGE. */
	public static final String VAR_SOCKET_MESSAGE="socketMessage";
	
	/** The Constant VAR_SESSION. */
	public static final String VAR_SESSION="session";
	
	public static final String VAR_DB="db";
	
	/** The Constant VAR_WEBSOCKET_CLIENT. */
	public static final String VAR_WEBSOCKET_CLIENT="websocketClient";
	
	/** The Constant VAR_BUNDLE_UTILS. */
	public static final String VAR_BUNDLE_UTILS="bundleUtils";
	
	/** The Constant VAR_CACHE. */
	public static final String VAR_CACHE="cache";
	
	/** The Constant VAR_TERM_SIGNAL. */
	public static final String VAR_TERM_SIGNAL="termSignal";
	
	/** The Constant VAR_SCRIPT. */
	public static final String VAR_SCRIPT="script";
	
	public static final String VAR_B64="b64Utils";
	
	public static final String VAR_STRUTILS="strUtils";
	
	public static final String VAR_COLUTILS="colUtils";
	
	public static final String VAR_STOPWATCH="stopWatch";
	
	public static final String VAR_IOUTILS = "ioUtils";
	
	public static final String VAR_FILEUTILS="fileUtils";
	
	public static final String VAR_ATTACHUTILS="attachUtils";
	
	
	/** The Constant SOURCE_URI. */
	//jsf object keys
	public static final String SOURCE_URI="sourceUri";
	
}
