package org.owasp.esapi;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.owasp.esapi.errors.EncryptionException;

public interface EncryptedProperties
{
    String getProperty(final String p0) throws EncryptionException;
    
    String setProperty(final String p0, final String p1) throws EncryptionException;
    
    Set<?> keySet();
    
    void load(final InputStream p0) throws IOException;
    
    void store(final OutputStream p0, final String p1) throws IOException;
}
