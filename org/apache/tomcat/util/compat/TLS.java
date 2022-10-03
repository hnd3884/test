package org.apache.tomcat.util.compat;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

@Deprecated
public class TLS
{
    private static final boolean tlsv13Available;
    
    public static boolean isTlsv13Available() {
        return TLS.tlsv13Available;
    }
    
    static {
        boolean ok = false;
        try {
            SSLContext.getInstance("TLSv1.3");
            ok = true;
        }
        catch (final NoSuchAlgorithmException ex) {}
        tlsv13Available = ok;
    }
}
