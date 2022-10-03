package javapns.communication;

import javapns.communication.exceptions.InvalidKeystoreReferenceException;
import java.io.InputStream;

public interface AppleServer
{
    InputStream getKeystoreStream() throws InvalidKeystoreReferenceException;
    
    String getKeystorePassword();
    
    String getKeystoreType();
    
    String getProxyHost();
    
    int getProxyPort();
    
    void setProxy(final String p0, final int p1);
    
    String getProxyAuthorization();
    
    void setProxyAuthorization(final String p0);
}
