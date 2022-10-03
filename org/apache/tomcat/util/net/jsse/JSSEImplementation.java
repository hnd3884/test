package org.apache.tomcat.util.net.jsse;

import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.net.SSLSupport;
import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.net.SSLImplementation;

public class JSSEImplementation extends SSLImplementation
{
    public JSSEImplementation() {
        JSSESupport.init();
    }
    
    @Deprecated
    @Override
    public SSLSupport getSSLSupport(final SSLSession session) {
        return this.getSSLSupport(session, null);
    }
    
    @Override
    public SSLSupport getSSLSupport(final SSLSession session, final Map<String, List<String>> additionalAttributes) {
        return new JSSESupport(session, additionalAttributes);
    }
    
    @Override
    public SSLUtil getSSLUtil(final SSLHostConfigCertificate certificate) {
        return new JSSEUtil(certificate);
    }
    
    @Override
    public boolean isAlpnSupported() {
        return JreCompat.isAlpnSupported();
    }
}
