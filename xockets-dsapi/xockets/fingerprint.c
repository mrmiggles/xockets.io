#include "fingerprint.h"

#define SHA1LEN 20

void getFingerprint(char *base64, char strbuf[]) {

	char buf[SHA1LEN];
	//char strbuf[2 * SHA1LEN + 1];
	const EVP_MD *digest = EVP_sha1();
	unsigned len;

	BIO *certBio;
	X509 *cert = NULL;

	certBio = BIO_new_mem_buf(base64, -1);
	PEM_read_bio_X509(certBio, &cert, 0, NULL);
	if (cert == NULL) {
		AddInLogMessageText("unable to parse certificate in memory: \n", 0L);
	}
	else {

		int rc = X509_digest(cert, digest, (unsigned char*)buf, &len);
		if (rc == 0 || len != SHA1LEN) {
			AddInLogMessageText("SHA1 length incorrect \n", 0L);
		}
		else {
			hex_encode(buf, strbuf, SHA1LEN);
			//AddInLogMessageText("Fingerprint: %s\n", 0L, strbuf);
		}
	}

	// do stuff
	BIO_free(certBio);
	X509_free(cert);
}

void hex_encode(unsigned char* readbuf, void *writebuf, size_t len)
{
	for (size_t i = 0; i < len; i++) {
		char *l = (char*)(2 * i + ((intptr_t)writebuf));
		sprintf(l, "%02x", readbuf[i]);
	}
}
