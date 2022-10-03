package javax.naming.ldap;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;

public abstract class StartTlsResponse implements ExtendedResponse
{
    public static final String OID = "1.3.6.1.4.1.1466.20037";
    private static final long serialVersionUID = 8372842182579276418L;
    
    protected StartTlsResponse() {
    }
    
    @Override
    public String getID() {
        return "1.3.6.1.4.1.1466.20037";
    }
    
    @Override
    public byte[] getEncodedValue() {
        return null;
    }
    
    public abstract void setEnabledCipherSuites(final String[] p0);
    
    public abstract void setHostnameVerifier(final HostnameVerifier p0);
    
    public abstract SSLSession negotiate() throws IOException;
    
    public abstract SSLSession negotiate(final SSLSocketFactory p0) throws IOException;
    
    public abstract void close() throws IOException;
}
