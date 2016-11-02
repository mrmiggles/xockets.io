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

#pragma once

#define DLLEXPORT __declspec(dllexport)

// Windows Header Files:
#include <windows.h>

// TODO: reference additional headers your program requires here
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

/* Special dsapi include file */
#include <dsapi.h>

/* Notes SDK include files */
#include "global.h"
#include "osmem.h"
#include <addin.h>
#include "xconstants.h"


/*
Defined functions
*/

DLLEXPORT unsigned int FilterInit(FilterInitData* filterInitData);
DLLEXPORT unsigned int HttpFilterProc(FilterContext* context, unsigned int eventType, void* eventData);


unsigned int Authenticate(FilterContext* context, FilterAuthenticate* authData);
void parseCookie(char cookies[], char cookieName[], char result[]);
void readUserId(char sessionId[], char userId[]);
void logme(const char*message);



