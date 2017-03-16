#pragma once

/* OpenSSL headears*/
#include <wchar.h>  
#include <openssl/pem.h>  
#include <openssl/bio.h>

#include "global.h"
#include "osmem.h"
#include "addin.h"

#ifndef FINGERPRINT_H_
#define FINGERPRINT_H_

void getFingerprint(char *base64, char strbuf[]);
void hex_encode(unsigned char* readbuf, void *writebuf, size_t len);

#endif