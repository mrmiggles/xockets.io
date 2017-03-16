#pragma once
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#include "global.h"
#include "osmem.h"
#include "addin.h"

#ifndef HEXTOBASE64_H_
#define HEXTOBASE64_H_

void hex_to_base64(char *hex, int size, char **result);
void hex_to_bin(char *input, int size, char** result);
void bin_to_base64(char *hex_string, int size, char** result);

#endif
