package sun.security.x509;

import java.util.Enumeration;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.io.OutputStream;

public interface CertAttrSet<T>
{
    String toString();
    
    void encode(final OutputStream p0) throws CertificateException, IOException;
    
    void set(final String p0, final Object p1) throws CertificateException, IOException;
    
    Object get(final String p0) throws CertificateException, IOException;
    
    void delete(final String p0) throws CertificateException, IOException;
    
    Enumeration<T> getElements();
    
    String getName();
}
