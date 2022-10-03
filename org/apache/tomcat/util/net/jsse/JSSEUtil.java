package org.apache.tomcat.util.net.jsse;

import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Arrays;
import org.apache.tomcat.util.compat.JreVendor;
import java.util.Locale;
import java.util.HashSet;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import org.apache.juli.logging.LogFactory;
import java.security.NoSuchAlgorithmException;
import org.apache.tomcat.util.net.SSLContext;
import java.util.List;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.SSLUtilBase;

public class JSSEUtil extends SSLUtilBase
{
    private static final Log log;
    private static final StringManager sm;
    private static final Set<String> implementedProtocols;
    private static final Set<String> implementedCiphers;
    
    public JSSEUtil(final SSLHostConfigCertificate certificate) {
        this(certificate, true);
    }
    
    public JSSEUtil(final SSLHostConfigCertificate certificate, final boolean warnOnSkip) {
        super(certificate, warnOnSkip);
    }
    
    @Override
    protected Log getLog() {
        return JSSEUtil.log;
    }
    
    @Override
    protected Set<String> getImplementedProtocols() {
        return JSSEUtil.implementedProtocols;
    }
    
    @Override
    protected Set<String> getImplementedCiphers() {
        return JSSEUtil.implementedCiphers;
    }
    
    @Override
    protected boolean isTls13RenegAuthAvailable() {
        return false;
    }
    
    public SSLContext createSSLContextInternal(final List<String> negotiableProtocols) throws NoSuchAlgorithmException {
        return new JSSESSLContext(this.sslHostConfig.getSslProtocol());
    }
    
    static {
        log = LogFactory.getLog((Class)JSSEUtil.class);
        sm = StringManager.getManager((Class)JSSEUtil.class);
        SSLContext context;
        try {
            context = new JSSESSLContext("TLS");
            context.init(null, null, null);
        }
        catch (final NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalArgumentException(e);
        }
        final String[] implementedProtocolsArray = context.getSupportedSSLParameters().getProtocols();
        implementedProtocols = new HashSet<String>(implementedProtocolsArray.length);
        for (final String protocol : implementedProtocolsArray) {
            final String protocolUpper = protocol.toUpperCase(Locale.ENGLISH);
            if (!"SSLV2HELLO".equals(protocolUpper) && !"SSLV3".equals(protocolUpper) && protocolUpper.contains("SSL")) {
                JSSEUtil.log.debug((Object)JSSEUtil.sm.getString("jsseUtil.excludeProtocol", new Object[] { protocol }));
            }
            else {
                JSSEUtil.implementedProtocols.add(protocol);
            }
        }
        if (JSSEUtil.implementedProtocols.size() == 0) {
            JSSEUtil.log.warn((Object)JSSEUtil.sm.getString("jsseUtil.noDefaultProtocols"));
        }
        final String[] implementedCipherSuiteArray = context.getSupportedSSLParameters().getCipherSuites();
        if (JreVendor.IS_IBM_JVM) {
            implementedCiphers = new HashSet<String>(implementedCipherSuiteArray.length * 2);
            for (final String name : implementedCipherSuiteArray) {
                JSSEUtil.implementedCiphers.add(name);
                if (name.startsWith("SSL")) {
                    JSSEUtil.implementedCiphers.add("TLS" + name.substring(3));
                }
            }
        }
        else {
            (implementedCiphers = new HashSet<String>(implementedCipherSuiteArray.length)).addAll(Arrays.asList(implementedCipherSuiteArray));
        }
    }
}
