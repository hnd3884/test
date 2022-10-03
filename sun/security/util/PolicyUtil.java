package sun.security.util;

import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.KeyStoreException;
import java.util.Arrays;
import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.io.IOException;
import java.io.FileInputStream;
import sun.net.www.ParseUtil;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class PolicyUtil
{
    private static final String P11KEYSTORE = "PKCS11";
    private static final String NONE = "NONE";
    
    public static InputStream getInputStream(final URL url) throws IOException {
        if ("file".equals(url.getProtocol())) {
            return new FileInputStream(ParseUtil.decode(url.getFile().replace('/', File.separatorChar)));
        }
        return url.openStream();
    }
    
    public static KeyStore getKeyStore(final URL url, final String s, String defaultType, final String s2, final String s3, final Debug debug) throws KeyStoreException, MalformedURLException, IOException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
        if (s == null) {
            throw new IllegalArgumentException("null KeyStore name");
        }
        char[] password = null;
        try {
            if (defaultType == null) {
                defaultType = KeyStore.getDefaultType();
            }
            if ("PKCS11".equalsIgnoreCase(defaultType) && !"NONE".equals(s)) {
                throw new IllegalArgumentException("Invalid value (" + s + ") for keystore URL.  If the keystore type is \"" + "PKCS11" + "\", the keystore url must be \"" + "NONE" + "\"");
            }
            KeyStore keyStore;
            if (s2 != null) {
                keyStore = KeyStore.getInstance(defaultType, s2);
            }
            else {
                keyStore = KeyStore.getInstance(defaultType);
            }
            if (s3 != null) {
                URL url2;
                try {
                    url2 = new URL(s3);
                }
                catch (final MalformedURLException ex) {
                    if (url == null) {
                        throw ex;
                    }
                    url2 = new URL(url, s3);
                }
                if (debug != null) {
                    debug.println("reading password" + url2);
                }
                InputStream openStream = null;
                try {
                    openStream = url2.openStream();
                    password = Password.readPassword(openStream);
                }
                finally {
                    if (openStream != null) {
                        openStream.close();
                    }
                }
            }
            if ("NONE".equals(s)) {
                keyStore.load(null, password);
                return keyStore;
            }
            URL url3;
            try {
                url3 = new URL(s);
            }
            catch (final MalformedURLException ex2) {
                if (url == null) {
                    throw ex2;
                }
                url3 = new URL(url, s);
            }
            if (debug != null) {
                debug.println("reading keystore" + url3);
            }
            InputStream inputStream = null;
            try {
                inputStream = new BufferedInputStream(getInputStream(url3));
                keyStore.load(inputStream, password);
            }
            finally {
                inputStream.close();
            }
            return keyStore;
        }
        finally {
            if (password != null) {
                Arrays.fill(password, ' ');
            }
        }
    }
}
