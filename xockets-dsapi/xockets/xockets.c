/*
* © Copyright LANL
*/


#include "xockets.h"

static char b64table[64] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
"abcdefghijklmnopqrstuvwxyz"
"0123456789+/";

void xoc_logme(const char*message) {
	if (message) {
		AddInLogMessageText("%s: %s\n", NOERROR, filter_name, message);
	}
}


unsigned int FilterInit(FilterInitData* filterInitData) {
	filterInitData->appFilterVersion = kInterfaceVersion;
	filterInitData->eventFlags = kFilterAuthenticate;//kFilterAuthUser | kFilterAuthorized | ;


	/* Output sent to stdout and stderr is displayed on the
	* server console, but is not written to the server log file.
	*/
	xoc_logme("DSAPI Authentication filter initialized");
	return kFilterHandledEvent;
}

unsigned int TerminateFilter(unsigned int reserved) {
	if (xoc_debug) xoc_logme("filter terminated");
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
	sessionId[0] = '\0';

	parseCookie(cookies, COOKIE_SESSIONID, sessionId);
	
	char userId[USER_ID_BUFFER];
	char userName[USER_ID_BUFFER];
	char clientsidePrint[USER_ID_BUFFER];

	/* decs */
	unsigned char *fullName = NULL;
	int fullNameLen = 0;
	char *shortName = NULL;
	int shortNameLen = 0;

	char *publicKey = NULL;
	int publicKeyLen = 0;
	char *certificate = NULL;
	int certificateLen = 0;

	userId[0] = '\0';
	userName[0] = '\0';
	clientsidePrint[0] = '\0';

	readUserId(sessionId, userId, userName, clientsidePrint);


	if (strlen(userId) > 0 && strlen(userName) > 0 && strlen(clientsidePrint) > 0) {

		/* Lookup the user in the Name and Address book.  Get
		* the user's short name and get the user's fullname (which we
		* expect will be in the format to pass back to
		* dsapi).
		*/
		if (0 == getUserNames(context, (char *)userName, &fullName, &fullNameLen, &shortName, &shortNameLen)) {

			strncpy((char *)authData->authName, fullName, authData->authNameSize);
			if(xoc_debug) AddInLogMessageText("Found User %s in Address Book. Verifying Certificate...", NO_ERROR, authData->authName);

			if (0 == getUserCerts(context, (char *)authData->authName, &publicKey, &publicKeyLen, &certificate, &certificateLen)) {

				if (certificate && !*certificate) {

				}
				else {
					//chop off first 49 chars IBM tacks on to the Cert
					certificate += 49;
					certificateLen -= 49;

					char *base64cert;
					char strbuf[2 * 20 + 1];
					hex_to_base64(certificate, certificateLen, &base64cert); //change from hex to base64 Pem for OpenSSL 
					getFingerprint(base64cert, strbuf); //get fingerprint from stored cert

					//if(xoc_debug) AddInLogMessageText("Server Cert Fingerprint: %s\n", 0L, strbuf);

					/*clean up after*/
					base64cert = NULL;
					free(base64cert);
					certificate = NULL;
					free(certificate);

					//compare request print vs stored print
					if (strcmp(clientsidePrint, strbuf) == 0) {

						if (xoc_debug) AddInLogMessageText("DSAPI Filter authenticated %s (web user %s)", NO_ERROR, (char *)authData->authName, shortName);

						strbuf[0] = '\0';

						authData->authFlags = kAuthAllowBasic;
						authData->authType = kAuthenticBasic;
						authData->foundInCache = TRUE;
						return kFilterHandledEvent;
					}
					else {
						strbuf[0] = '\0';
						if(xoc_debug) AddInLogMessageText("Access Denied: Mismatching keys for -  %s.", NO_ERROR, (char *)authData->authName);

						return kFilterNotHandled;
						//authData->authType = kNotAuthentic;
						//authData->authFlags = kAuthAllowBasic;
					}
				}
			}
			else { //Database query for Cert failed...do not allow
				//authData->authType = kNotAuthentic;
				//authData->authFlags = kAuthAllowBasic;
				if (xoc_debug) AddInLogMessageText("NO CERTIFICATE for User: %s", NO_ERROR, (char *)userName);

				return kFilterNotHandled;
			}

			

		}
		else {
			if (xoc_debug) AddInLogMessageText("User %s was NOT found in the address book", NO_ERROR, userName);
			return kFilterNotHandled;
		}
	}

	//AddInLogMessageText("Could not read files for %s", NO_ERROR, (char *)authData->userName);
	if (xoc_debug) AddInLogMessageText("Either DN, Username or Fingerprint is missing or the session file was never created.", NO_ERROR);
	return kFilterNotHandled;
}


DLLEXPORT unsigned int HttpFilterProc(FilterContext* context, unsigned int eventType, void *eventData) {

	switch (eventType) {
	case kFilterAuthenticate:
		return Authenticate(context, (FilterAuthenticate *)eventData);
		break;
	case kFilterAuthorized:
		return Authenticate(context, (FilterAuthenticate *)eventData);
		break;
	case kFilterAuthUser:
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
	char *cmp;
	result[0] = '\0';
	while (parsed != NULL) {
		//if (strcmp(parsed, cookieName) > 0) {
		cmp = strstr(parsed, cookieName);
		if( cmp != NULL) {
			value = strrchr(parsed, '=') + 1;
			strcpy(result, value);
			break;
		}
		parsed = strtok(NULL, "; ");
	}
}



void readUserId(char sessionId[], char userId[], char userName[], char clientsidePrint[]) {
	char filePath[BUFFER];
	char cCurrentPath[FILENAME_MAX];

	if (!GetCurrentDir(cCurrentPath, sizeof(cCurrentPath))) {
		xoc_logme("Could not find working directory");
		return;
	}

	//printf("The current working directory is %s", cCurrentPath);
		
		//int chars = snprintf(filePath, sizeof(filePath), "%s%s%s", sessions_dir, sessionId, ".txt");
		int chars = snprintf(filePath, sizeof(filePath), "%s%s%s%s", cCurrentPath, sessions_dir, sessionId, ".txt");
		if (xoc_debug) xoc_logme(filePath);

		if (chars > 0) {
			FILE *fp;
			fp = fopen(filePath, "a+");
			if (fp != NULL) {
				fgets(userId, USER_ID_BUFFER, (FILE*)fp);
				fgets(userName, USER_ID_BUFFER, (FILE*)fp);
				fgets(clientsidePrint, USER_ID_BUFFER, (FILE*)fp);
				if (strlen(userName) > 1) {
					userName[strlen(userName) - 1] = '\0';
				}

				if (xoc_debug && (userId == NULL || userName == NULL)) {
					xoc_logme("\nNo DN Name or Username or Thumprint\n");
				}

				if (xoc_debug) {
					xoc_logme(userId);
					xoc_logme(userName);
					xoc_logme(clientsidePrint);
				}

				fclose(fp);
			}

		}

}

/*===========================================================================*/
/*
* Description:  Lookup the user and return the user's full name and
*               short name.
*
* Input:  context            context we'll use for allocating memory
*         userName           the name of the user to lookup
* Output: pUserFullName      location of the user's full name
*         pUserFullNameLen   location to store the length of fullname
*         pUserShortName     location of the user's shortname
*         pUserShortNameLen  location to store the length of
*                            shortname
*
* Return: -1 on error, 0 on success
*/

int getUserNames(FilterContext* context,
	char *userName,
	char **pUserFullName,
	int  *pUserFullNameLen,
	char **pUserShortName,
	int  *pUserShortNameLen) {

	STATUS	error = NOERROR;
	HANDLE	hLookup = NULLHANDLE;
	WORD	Matches = 0;
	char	*pLookup;
	char	*pName = NULL;
	char	*pMatch = NULL;
	int     rc = -1;

	if (!userName || !pUserFullName || !pUserFullNameLen
		|| !pUserShortName || !pUserShortNameLen)
		return rc;

	/* Initialize output */
	*pUserFullName = NULL;
	*pUserFullNameLen = 0;
	*pUserShortName = NULL;
	*pUserShortNameLen = 0;


	/*
	do the name lookup
	* NULL means look locally
	* flags
	* number of namespaces
	* namespace list
	* number of names to lookup
	* list of names to lookup
	* number of items to return
	* list of items to return
	* place to receive handle of return buffer
	*/
	error = NAMELookup(NULL, 0, 1, "$Users", 1, userName, 4, "FullName\0ShortName", &hLookup);

	if (error || (NULLHANDLE == hLookup))
		goto NoUnlockExit;

	pLookup = (char *)OSLockObject(hLookup);

	/*	Get a pointer to our entry.
	* plookup - name of lookup buffer
	* NULL
	* &Matches - start at beginning of lookup buffer
	*/
	pName = (char *)NAMELocateNextName(pLookup, NULL, &Matches);
	/* Receives number
	* of times we
	* found the entry
	* (should be 1)
	*/

	/* If we didn't find the entry, then quit */
	if ((pName == NULL) || (Matches <= 0)) {
		goto Exit;
	}

	/*plookup - name of lookup buffer
	* pName - entry that we found
	* no previous match */
	pMatch = (char *)NAMELocateNextMatch(pLookup, pName, NULL);

	if (NULL == pMatch) {
		goto Exit;
	}
	/* Get the full name from the info we got back */
	if (getLookupInfo(context, pMatch, 0, pUserFullName, pUserFullNameLen)) {
		goto Exit;
	}

	/* Get the short name from the info we got back */
	if (getLookupInfo(context, pMatch, 1, pUserShortName, pUserShortNameLen)) {
		goto Exit;
	}
	else {
		/* Success in all things */
		rc = 0;
	}


Exit:
	if (pLookup && hLookup)
		OSUnlock(hLookup);
NoUnlockExit:
	if (NULLHANDLE != hLookup)
		OSMemFree(hLookup);
	return rc;
}

/*===========================================================================*/
/*
* Description:  Lookup the user and return the user's public key and
*               certificate.
*
* Input:  context              context we'll use for allocating memory
*         userName             the name of the user to lookup
* Output: pUserPublicKey       location of the user's public key
*         pUserPublicKeyLen    location to store the length of public key
*         pUserCertificate     location of the user's certificate
*         pUserCertificateLen  location to store the length of
*                              certificate
*
* Return: -1 on error, 0 on success
*/

int getUserCerts(FilterContext* context,
	char *userName,
	char **pUserPublicKey,
	int *pUserPublicKeyLen,
	char **pUserCertificate,
	int *pUserCertificateLen) {

	STATUS	error = NOERROR;
	HANDLE	hLookup = NULLHANDLE;
	WORD	Matches = 0;
	char	*pLookup;
	char	*pName = NULL;
	char	*pMatch = NULL;
	int     rc = -1;

	if (!userName || !pUserPublicKey || !pUserPublicKeyLen
		|| !pUserCertificate || !pUserCertificateLen)
		return rc;

	*pUserPublicKey = NULL;
	*pUserPublicKeyLen = 0;
	*pUserCertificate = NULL;
	*pUserCertificateLen = 0;
	/*
	do the name lookup
	* NULL means look locally
	* flags
	* number of namespaces
	* namespace list
	* number of names to lookup
	* list of names to lookup
	* number of items to return
	* list of items to return
	* place to receive handle of return buffer
	*/
	error = NAMELookup(NULL, 0, 1, "$Users", 1, userName, 2, "PublicKey\0UserCertificate", &hLookup);

	if (error || (NULLHANDLE == hLookup))
		goto NoUnlockExit;

	pLookup = (char *)OSLockObject(hLookup);

	/*	Get a pointer to our entry.
	* plookup - name of lookup buffer
	* NULL
	* &Matches - start at beginning of lookup buffer
	*/
	pName = (char *)NAMELocateNextName(pLookup, NULL, &Matches);
	/* Receives number
	* of times we
	* found the entry
	* (should be 1)
	*/

	/* If we didn't find the entry, then quit */
	if ((pName == NULL) || (Matches <= 0)) {
		goto Exit;
	}

	/*plookup - name of lookup buffer
	* pName - entry that we found
	* no previous match */
	pMatch = (char *)NAMELocateNextMatch(pLookup, pName, NULL);

	if (NULL == pMatch) {
		goto Exit;
	}

	/* Get the publicKey from the info we got back */
	if (getLookupInfo(context, pMatch, 0, pUserPublicKey, pUserPublicKeyLen)) {
		goto Exit;
	}

	/* Get the certificate from the info we got back */
	if (getLookupInfo(context, pMatch, 1, pUserCertificate, pUserCertificateLen)) {
		goto Exit;
	}
	else {
		/* Success in all things */
		rc = 0;
	}


Exit:
	if (pLookup && hLookup)
		OSUnlock(hLookup);
NoUnlockExit:
	if (NULLHANDLE != hLookup)
		OSMemFree(hLookup);
	return rc;
}

/*
* Description:  Get the info from the lookup buffer
*
* Input:  context            context we'll use for allocating memory
*         pMatch             the name of the lookup buffer
*         itemNumber         where the info is stored in the lookup
*                            buffer
* Output: pInfo              location of the info buffer
*         pInfoLen           location to store the info length
*
* Return: -1 on error, 0 on success
*/

int getLookupInfo(FilterContext* context,
	char *pMatch,
	int  itemNumber,
	char **pInfo,
	int  *pInfoLen) {

	unsigned int reserved = 0;
	unsigned int errID;
	char	*ValuePtr = NULL;
	WORD	ValueLength, DataType;
	STATUS	error;
	void	*newSpace = NULL;

	if (!pMatch || !pInfo || !pInfoLen || (itemNumber < 0))
		return -1;

	/* Initialize output */
	*pInfo = NULL;
	*pInfoLen = 0;

	/* Check the type and length of the info */
	/* match that we found */
	/* item # in order of item on lookup */
	/* return the datatype of item value */
	/* size of rtn value */
	ValuePtr = (char *)NAMELocateItem(pMatch, itemNumber, &DataType, &ValueLength);

	/* there is no info */
	if (NULL == ValuePtr || ValueLength == 0) {
		return -1;
	}

	/* remove datatype word included in the list length */
	ValueLength -= sizeof(WORD);

	/* check the value DataType */
	switch (DataType) {
	case TYPE_TEXT_LIST:
		break;

	case TYPE_TEXT:
		break;

	default:
		return -1;
	}

	/* Allocate space for the info.  This memory will be freed
	* automatically when the thread terminates.
	*/
	newSpace = (context->AllocMem)(context, ValueLength + 1, reserved, &errID);
	*pInfo = (char *)newSpace;

	if (NULL == *pInfo) {
		xoc_logme("Out of memory\n");
		return -1;
	}

	/* Get the info */
	/* match that we found */
	/* item # in order of item on lookup */
	/* Member # of item in text lists */
	/* buffer to copy result into */
	/* Length of buffer */
	error = NAMEGetTextItem(pMatch, itemNumber, 0, *pInfo, ValueLength + 1);

	if (!error) {
		*pInfoLen = ValueLength + 1;
		return 0;
	}
	return -1;
}
