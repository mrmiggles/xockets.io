
#include "hexTobase64.h"

const char binary[16][5] = { "0000", "0001", "0010", "0011", "0100", "0101","0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110","1111" };
const char digits[] = "0123456789abcdef";
const char dec_2_base64[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

void hex_to_base64(char *hex_string, int size, char **result) {

	//AddInLogMessageText("Hex Size Param:  %lu\n", 0L, size);
	int newSize = 0;
	//remove whitespaces from hex string
	int i, j;
	char *output = hex_string;
	for (i = 0, j = 0; i<size; i++, j++) {
		//if (!isspace(hex_string[i]) && hex_string[i] != '\0' ) {
		if(hex_string[i] != ' ' && hex_string[i] != '\0'){
			output[j] = hex_string[i];
			newSize++;
		}
		else {
			j--;
		}
			
	}
	output[j] = '\0';
	
	//debug statements
	//AddInLogMessageText("Hex String No Space Size: %lu\n", 0L, newSize);
	//AddInLogMessageText("Hex at 0:  %c\n", 0L, hex_string[0]);
	//AddInLogMessageText("Hex at length:  %c\n", 0L, output[newSize-1]);
	hex_to_bin(output, newSize, result);

}

void hex_to_bin(char *_hex, int size, char **result) {
	int i = 0;
	int value = 0;

	char *res;
	res = (char*)calloc(1 + (size * 4), sizeof(char)); //4 binary bits to every 1 char
	int len = 1 + (size * 4);

	for (i = 0; i < size; i++) {

		//while (_hex[i]) {
		char *v = strchr(digits, tolower(_hex[i]));
		if (v) {
			if (v[0] == 0) {
				value = 0;

			}
			else if (v[0] > 96) {
				value = v[0] - 87;
			}
			else {
				value = v[0] - 48;
			}
		}
		strcat(res, binary[value]);
	}
	res[len] = '\0';
	bin_to_base64(res, len-1, result);
	res = NULL;
	free(res);


}

void bin_to_base64(char *bin_string, int size, char **base64) {
	//char *base64 = NULL;
	char begincert[] = "-----BEGIN CERTIFICATE-----\n"; //"-----BEGIN RSA PUBLIC KEY-----";
	char end[] = "-----END CERTIFICATE-----\n"; //"-----END RSA PUBLIC KEY-----";
	*base64 = (char*)calloc(strlen(begincert) * strlen(end)*(4 + (size / 6)), sizeof(char));
	
	size_t binstring_size = size; //strlen(bin_string);
	if (!binstring_size) {
		//return base64;
	}
	if (binstring_size % 2 != 0) {
		//AddInLogMessageText("public key was odd sized: %lu\n", 0L, binstring_size);
	}
	else {
		strcat(*base64, begincert);
		int i = 0;
		int charcount = 0;
		int bin_to_dec = 0;
		char sixBits[7];
		char *v;
		v = malloc(2);
		v[1] = '\0';

		while (binstring_size >= 6) {
			++i;
			strncpy(sixBits, bin_string, 6);
			//r1 = strncpy_s(sixBits, 7, bin_string, 6);

			//sixBits[7] = '\0';
			bin_to_dec = (int)strtol(sixBits, NULL, 2);
			v[0] = dec_2_base64[bin_to_dec];
			strcat(*base64, v);
			binstring_size -= 6;
			bin_string += 6;
			charcount++;
			if (charcount == 76) {
				strcat(*base64, "\n");
				charcount = 0;
			}

			//fprintf(f, "%s\n", sixBits);
		}

		/* if hex string is not mod 3 pad some zeros at the end */
		if (binstring_size > 0) {
			char temphex[6] = { '0', '0', '0', '0', '0', '0' };
			memcpy(temphex, bin_string, binstring_size);
			bin_to_dec = (int)strtol(temphex, NULL, 2);
			v[0] = dec_2_base64[bin_to_dec];
			strcat(*base64, v);

			//fprintf(f, "%s\n", temphex);
			//and pad some = signs according to the algorithm
			int pad = 4 - ((i + 1) % 4);
			while (pad != 0) {
				strcat(*base64, "=");
				--pad;
			}
			strcat(*base64, "\n");
			//printf("Last of Hex: %s\n", temphex);
		}
		else {
			strcat(*base64, "\n");
		}

		strcat(*base64, end);
		//base64[strlen(base64)] = '\0';

		/*
		FILE *f = fopen("c:\\sessions\\base64.txt", "w");
		if (f == NULL) {
		printf("Error opening file!\n");
		}
		else {
		fprintf(f, "\n\n%s", *base64);
		fclose(f);
		}
		*/

		/* clean up*/
		free(v);

	}
	
}
