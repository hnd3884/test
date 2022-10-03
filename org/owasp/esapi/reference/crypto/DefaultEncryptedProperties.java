package org.owasp.esapi.reference.crypto;

import java.util.Hashtable;
import java.util.Iterator;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import java.util.Properties;
import org.owasp.esapi.EncryptedProperties;

public class DefaultEncryptedProperties implements EncryptedProperties
{
    private final Properties properties;
    private final Logger logger;
    
    public DefaultEncryptedProperties() {
        this.properties = new Properties();
        this.logger = ESAPI.getLogger("EncryptedProperties");
    }
    
    @Override
    public synchronized String getProperty(final String key) throws EncryptionException {
        final String[] errorMsgs = { ": failed decoding from base64", ": failed to deserialize properly", ": failed to decrypt properly" };
        int progressMark = 0;
        try {
            final String encryptedValue = this.properties.getProperty(key);
            if (encryptedValue == null) {
                return null;
            }
            progressMark = 0;
            final byte[] serializedCiphertext = ESAPI.encoder().decodeFromBase64(encryptedValue);
            ++progressMark;
            final CipherText restoredCipherText = CipherText.fromPortableSerializedBytes(serializedCiphertext);
            ++progressMark;
            final PlainText plaintext = ESAPI.encryptor().decrypt(restoredCipherText);
            return plaintext.toString();
        }
        catch (final Exception e) {
            throw new EncryptionException("Property retrieval failure", "Couldn't retrieve encrypted property for property " + key + errorMsgs[progressMark], e);
        }
    }
    
    @Override
    public synchronized String setProperty(final String key, final String value) throws EncryptionException {
        final String[] errorMsgs = { ": failed to encrypt properly", ": failed to serialize correctly", ": failed to base64-encode properly", ": failed to set base64-encoded value as property. Illegal key name?" };
        int progressMark = 0;
        try {
            if (key == null) {
                throw new NullPointerException("Property name may not be null.");
            }
            if (value == null) {
                throw new NullPointerException("Property value may not be null.");
            }
            final PlainText pt = new PlainText(value);
            final CipherText ct = ESAPI.encryptor().encrypt(pt);
            ++progressMark;
            final byte[] serializedCiphertext = ct.asPortableSerializedByteArray();
            ++progressMark;
            final String b64str = ESAPI.encoder().encodeForBase64(serializedCiphertext, false);
            ++progressMark;
            final String encryptedValue = (String)this.properties.setProperty(key, b64str);
            ++progressMark;
            return encryptedValue;
        }
        catch (final Exception e) {
            throw new EncryptionException("Property setting failure", "Couldn't set encrypted property " + key + errorMsgs[progressMark], e);
        }
    }
    
    @Override
    public Set<?> keySet() {
        return ((Hashtable<?, V>)this.properties).keySet();
    }
    
    @Override
    public void load(final InputStream in) throws IOException {
        this.properties.load(in);
        this.logger.trace(Logger.SECURITY_SUCCESS, "Encrypted properties loaded successfully");
    }
    
    @Override
    public void store(final OutputStream out, final String comments) throws IOException {
        this.properties.store(out, comments);
    }
    
    @Deprecated
    public static void main(final String[] args) throws Exception {
        final File f = new File(args[0]);
        ESAPI.getLogger("EncryptedProperties.main").debug(Logger.SECURITY_SUCCESS, "Loading encrypted properties from " + f.getAbsolutePath());
        if (!f.exists()) {
            throw new IOException("Properties file not found: " + f.getAbsolutePath());
        }
        ESAPI.getLogger("EncryptedProperties.main").debug(Logger.SECURITY_SUCCESS, "Encrypted properties found in " + f.getAbsolutePath());
        final DefaultEncryptedProperties ep = new DefaultEncryptedProperties();
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(f);
            out = new FileOutputStream(f);
            ep.load(in);
            final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String key = null;
            do {
                System.out.print("Enter key: ");
                key = br.readLine();
                System.out.print("Enter value: ");
                final String value = br.readLine();
                if (key != null && key.length() > 0 && value != null && value.length() > 0) {
                    ep.setProperty(key, value);
                }
            } while (key != null && key.length() > 0);
            ep.store(out, "Encrypted Properties File");
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex) {}
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (final Exception ex2) {}
        }
        for (final String k : ep.keySet()) {
            final String value = ep.getProperty(k);
            System.out.println("   " + k + "=" + value);
        }
    }
}
