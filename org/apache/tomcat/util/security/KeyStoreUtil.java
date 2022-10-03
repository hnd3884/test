package org.apache.tomcat.util.security;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class KeyStoreUtil
{
    private KeyStoreUtil() {
    }
    
    public static void load(final KeyStore keystore, final InputStream is, final char[] storePass) throws NoSuchAlgorithmException, CertificateException, IOException {
        if (keystore.getType().equals("PKCS12")) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] buf = new byte[8192];
            int numRead;
            while ((numRead = is.read(buf)) >= 0) {
                baos.write(buf, 0, numRead);
            }
            baos.close();
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            keystore.load(bais, storePass);
        }
        else {
            keystore.load(is, storePass);
        }
    }
}
