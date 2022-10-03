package org.apache.tomcat.util.net;

import org.apache.juli.logging.LogFactory;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.tomcat.util.net.jsse.JSSEImplementation;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public abstract class SSLImplementation
{
    private static final Log logger;
    private static final StringManager sm;
    
    public static SSLImplementation getInstance(final String className) throws ClassNotFoundException {
        if (className == null) {
            return new JSSEImplementation();
        }
        try {
            final Class<?> clazz = Class.forName(className);
            return (SSLImplementation)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final Exception e) {
            final String msg = SSLImplementation.sm.getString("sslImplementation.cnfe", new Object[] { className });
            if (SSLImplementation.logger.isDebugEnabled()) {
                SSLImplementation.logger.debug((Object)msg, (Throwable)e);
            }
            throw new ClassNotFoundException(msg, e);
        }
    }
    
    public SSLSupport getSSLSupport(final SSLSession session, final Map<String, List<String>> additionalAttributes) {
        return this.getSSLSupport(session);
    }
    
    @Deprecated
    public abstract SSLSupport getSSLSupport(final SSLSession p0);
    
    public abstract SSLUtil getSSLUtil(final SSLHostConfigCertificate p0);
    
    public abstract boolean isAlpnSupported();
    
    static {
        logger = LogFactory.getLog((Class)SSLImplementation.class);
        sm = StringManager.getManager((Class)SSLImplementation.class);
    }
}
