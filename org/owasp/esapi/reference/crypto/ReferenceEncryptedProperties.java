package org.owasp.esapi.reference.crypto;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Set;
import java.util.Collection;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.EncryptionRuntimeException;
import org.owasp.esapi.crypto.CipherText;
import java.util.Iterator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.EncryptedProperties;
import java.util.Properties;

public class ReferenceEncryptedProperties extends Properties implements EncryptedProperties
{
    private static final long serialVersionUID = 20120718L;
    private final Logger logger;
    private static final String[] GET_ERROR_MESSAGES;
    private static final String[] SET_ERROR_MESSAGES;
    
    public ReferenceEncryptedProperties() {
        this.logger = ESAPI.getLogger(this.getClass());
    }
    
    public ReferenceEncryptedProperties(final Properties defaults) {
        this.logger = ESAPI.getLogger(this.getClass());
        for (final Object oKey : ((Hashtable<Object, V>)defaults).keySet()) {
            final String key = (String)((oKey instanceof String) ? oKey : oKey.toString());
            final String value = defaults.getProperty(key);
            this.setProperty(key, value);
        }
    }
    
    @Override
    public synchronized String getProperty(final String key) throws EncryptionRuntimeException {
        int progressMark = 0;
        try {
            final String encryptedValue = super.getProperty(key);
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
            throw new EncryptionRuntimeException("Property retrieval failure", "Couldn't retrieve encrypted property for property " + key + ReferenceEncryptedProperties.GET_ERROR_MESSAGES[progressMark], e);
        }
    }
    
    @Override
    public synchronized String getProperty(final String key, final String defaultValue) throws EncryptionRuntimeException {
        final String value = this.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
    
    @Override
    public synchronized String setProperty(final String key, final String value) throws EncryptionRuntimeException {
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
            return super.put(key, b64str);
        }
        catch (final Exception e) {
            throw new EncryptionRuntimeException("Property setting failure", "Couldn't set encrypted property " + key + ReferenceEncryptedProperties.SET_ERROR_MESSAGES[progressMark], e);
        }
    }
    
    @Override
    public void load(final InputStream in) throws IOException {
        super.load(in);
        this.logger.trace(Logger.SECURITY_SUCCESS, "Encrypted properties loaded successfully");
    }
    
    @Override
    public void load(final Reader in) throws IOException {
        if (in == null) {
            return;
        }
        final char[] cbuf = new char[65536];
        final BufferedReader buff = new BufferedReader(in);
        final StringBuilder contents = new StringBuilder();
        int read_this_time = 0;
        while (read_this_time != -1) {
            read_this_time = buff.read(cbuf, 0, 65536);
            if (read_this_time > 0) {
                contents.append(cbuf, 0, read_this_time);
            }
        }
        final InputStream is = new ByteArrayInputStream(contents.toString().getBytes());
        super.load(is);
        this.logger.trace(Logger.SECURITY_SUCCESS, "Encrypted properties loaded successfully");
    }
    
    @Override
    public void list(final PrintStream out) {
        throw new UnsupportedOperationException("This method has been removed for security.");
    }
    
    @Override
    public void list(final PrintWriter out) {
        throw new UnsupportedOperationException("This method has been removed for security.");
    }
    
    @Override
    public Collection values() {
        throw new UnsupportedOperationException("This method has been removed for security.");
    }
    
    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException("This method has been removed for security.");
    }
    
    @Override
    public Enumeration elements() {
        throw new UnsupportedOperationException("This method has been removed for security.");
    }
    
    @Override
    public synchronized Object put(final Object key, final Object value) {
        final Throwable t = new Throwable();
        for (final StackTraceElement trace : t.getStackTrace()) {
            if ("java.util.Properties".equals(trace.getClassName())) {
                return super.put(key, value);
            }
        }
        if (key instanceof String && value instanceof String) {
            return this.setProperty((String)key, (String)value);
        }
        throw new IllegalArgumentException("This method has been overridden to only accept Strings for key and value.");
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
    }
    
    static {
        GET_ERROR_MESSAGES = new String[] { ": failed decoding from base64", ": failed to deserialize properly", ": failed to decrypt properly" };
        SET_ERROR_MESSAGES = new String[] { ": failed to encrypt properly", ": failed to serialize correctly", ": failed to base64-encode properly", ": failed to set base64-encoded value as property. Illegal key name?" };
    }
}
