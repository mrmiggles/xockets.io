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


#include "xockets.h"



void logme(const char*message) {
	if (message) {
		AddInLogMessageText("%s: %s\n", NOERROR, filter_name, message);
	}
}


unsigned int FilterInit(FilterInitData* filterInitData) {
	filterInitData->appFilterVersion = kInterfaceVersion;
	filterInitData->eventFlags = kFilterAuthenticate;
	logme("filter loaded");
	return kFilterHandledEvent;
}

unsigned int TerminateFilter(unsigned int reserved){
	if(debug) logme("filter terminated");
	return kFilterHandledEvent;
}

unsigned int Authenticate(FilterContext* context, FilterAuthenticate* authData) {

	FilterRequest requestInfo;

	unsigned int errid;

	context->GetRequest(context, &requestInfo, &errid);

	char cookies[BUFFER];

	int cookiesLength;

	cookiesLength = authData->GetHeader(context, "Cookie", cookies, BUFFER, &errid);

	if (cookiesLength == 0) { return kFilterNotHandled; }

	char sessionId[SESSION_ID_BUFFER];

	parseCookie(cookies, COOKIE_SESSIONID, sessionId);
		
	char userId[USER_ID_BUFFER];

	readUserId(sessionId, userId);
	
	if (strlen(userId) > 0) {
		strncpy((char *)authData->authName, userId, authData->authNameSize);
		authData->authType = kAuthenticBasic;
		authData->authFlags = kAuthAllowBasic;
		return kFilterHandledEvent;
	}

	return kFilterNotHandled;
}


DLLEXPORT unsigned int HttpFilterProc(FilterContext* context, unsigned int eventType, void *eventData) {

	switch (eventType) {
	case kFilterAuthenticate:
		return Authenticate(context, (FilterAuthenticate *)eventData);
		break;
	default:
		break;
	}
	return kFilterNotHandled;
}


void parseCookie(char cookies[], char cookieName[], char result[]) {
	char *value;
	char *parsed = strtok(cookies, "; ");
	while (parsed != NULL) {
		if (strcmp(parsed, cookieName) == 1) {
			value = strrchr(parsed, '=') + 1;
			strcpy(result, value);
			break;
		}
		parsed = strtok(NULL, "; ");
	}
}



void readUserId(char sessionId[], char userId[]) {
	char filePath[BUFFER];
	
	int chars = snprintf(filePath, sizeof(filePath), "%s%s%s", sessions_dir, sessionId, ".txt");

	if (debug) logme(filePath);

	if (chars > 0) {
		FILE *fp;

		fp = fopen(filePath, "r");

		fgets(userId, USER_ID_BUFFER, (FILE*)fp);

		if (debug) logme(userId);

		fclose(fp);
	}
}