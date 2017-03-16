/*
* © Copyright LANL
*
*/

#pragma once

#define DLLEXPORT __declspec(dllexport)

// Windows Header Files:
#include <windows.h>

// TODO: reference additional headers your program requires here
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <time.h>
#include <direct.h>


/* Special dsapi include file */
#include "dsapi.h"

/* Notes SDK include files */
/* notes901 C API available for download at IBM */
#include "global.h"
#include "osmem.h"
#include "addin.h"
#include "lookup.h"
#include "xconstants.h"
#include "hexTobase64.h"
#include "fingerprint.h"


/*
Defined functions
*/

DLLEXPORT unsigned int FilterInit(FilterInitData* filterInitData);
DLLEXPORT unsigned int HttpFilterProc(FilterContext* context, unsigned int eventType, void* eventData);

unsigned int Authenticate(FilterContext* context, FilterAuthenticate* authData);
void parseCookie(char cookies[], char cookieName[], char result[]);
void readUserId(char sessionId[], char userId[], char username[], char fingerprint[]);
void xoc_logme(const char*message);


/* Retrieval of names from Notes name and address book */
int getUserNames(FilterContext* context, char *userName,
	char **pUserFullName, int  *pUserFullNameLen,
	char **pUserShortName, int  *pUserShortNameLen);

/* Retreival of certificate and public key from user*/
int getUserCerts(FilterContext* context, char *userName,
	char **pUserPulicKey, int *pUserPublicKeyLen,
	char **pUserCertificate, int *pUserCertificateLen);

int getLookupInfo(FilterContext* context, char *pMatch, int  itemNumber, char **pInfo, int  *pInfoLen);