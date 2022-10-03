package org.apache.tomcat.util.net.openssl;

import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.net.jsse.JSSESupport;
import org.apache.tomcat.util.net.SSLSupport;
import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.net.SSLImplementation;

public class OpenSSLImplementation extends SSLImplementation
{
    @Deprecated
    @Override
    public SSLSupport getSSLSupport(final SSLSession session) {
        return new JSSESupport(session);
    }
    
    @Override
    public SSLSupport getSSLSupport(final SSLSession session, final Map<String, List<String>> additionalAttributes) {
        return new JSSESupport(session, additionalAttributes);
    }
    
    @Override
    public SSLUtil getSSLUtil(final SSLHostConfigCertificate certificate) {
        return new OpenSSLUtil(certificate);
    }
    
    @Override
    public boolean isAlpnSupported() {
        return true;
    }
}
