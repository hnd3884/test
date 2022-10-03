package org.apache.tomcat.util.net.jsse;

import java.util.Iterator;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import java.util.HashMap;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.StringUtils;
import java.util.Collection;
import java.security.cert.Certificate;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.IOException;
import java.util.List;
import javax.net.ssl.SSLSession;
import java.util.Map;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.SSLSessionManager;
import org.apache.tomcat.util.net.SSLSupport;

public class JSSESupport implements SSLSupport, SSLSessionManager
{
    private static final Log log;
    private static final StringManager sm;
    private static final Map<String, Integer> keySizeCache;
    private SSLSession session;
    private Map<String, List<String>> additionalAttributes;
    
    static void init() {
    }
    
    @Deprecated
    public JSSESupport(final SSLSession session) {
        this(session, null);
    }
    
    public JSSESupport(final SSLSession session, final Map<String, List<String>> additionalAttributes) {
        this.session = session;
        this.additionalAttributes = additionalAttributes;
    }
    
    @Override
    public String getCipherSuite() throws IOException {
        if (this.session == null) {
            return null;
        }
        return this.session.getCipherSuite();
    }
    
    @Override
    public X509Certificate[] getPeerCertificateChain() throws IOException {
        if (this.session == null) {
            return null;
        }
        Certificate[] certs = null;
        try {
            certs = this.session.getPeerCertificates();
        }
        catch (final Throwable t) {
            JSSESupport.log.debug((Object)JSSESupport.sm.getString("jsseSupport.clientCertError"), t);
            return null;
        }
        if (certs == null) {
            return null;
        }
        final X509Certificate[] x509Certs = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; ++i) {
            if (certs[i] instanceof X509Certificate) {
                x509Certs[i] = (X509Certificate)certs[i];
            }
            else {
                try {
                    final byte[] buffer = certs[i].getEncoded();
                    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    final ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
                    x509Certs[i] = (X509Certificate)cf.generateCertificate(stream);
                }
                catch (final Exception ex) {
                    JSSESupport.log.info((Object)JSSESupport.sm.getString("jsseSupport.certTranslationError", new Object[] { certs[i] }), (Throwable)ex);
                    return null;
                }
            }
            if (JSSESupport.log.isTraceEnabled()) {
                JSSESupport.log.trace((Object)("Cert #" + i + " = " + x509Certs[i]));
            }
        }
        if (x509Certs.length < 1) {
            return null;
        }
        return x509Certs;
    }
    
    @Override
    public Integer getKeySize() throws IOException {
        if (this.session == null) {
            return null;
        }
        return JSSESupport.keySizeCache.get(this.session.getCipherSuite());
    }
    
    @Override
    public String getSessionId() throws IOException {
        if (this.session == null) {
            return null;
        }
        final byte[] ssl_session = this.session.getId();
        if (ssl_session == null) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        for (final byte b : ssl_session) {
            String digit = Integer.toHexString(b);
            if (digit.length() < 2) {
                buf.append('0');
            }
            if (digit.length() > 2) {
                digit = digit.substring(digit.length() - 2);
            }
            buf.append(digit);
        }
        return buf.toString();
    }
    
    public void setSession(final SSLSession session) {
        this.session = session;
    }
    
    @Override
    public void invalidateSession() {
        this.session.invalidate();
    }
    
    @Override
    public String getProtocol() throws IOException {
        if (this.session == null) {
            return null;
        }
        return this.session.getProtocol();
    }
    
    @Override
    public String getRequestedProtocols() throws IOException {
        if (this.additionalAttributes == null) {
            return null;
        }
        return StringUtils.join((Collection)this.additionalAttributes.get("org.apache.tomcat.util.net.secure_requested_protocol_versions"));
    }
    
    @Override
    public String getRequestedCiphers() throws IOException {
        if (this.additionalAttributes == null) {
            return null;
        }
        return StringUtils.join((Collection)this.additionalAttributes.get("org.apache.tomcat.util.net.secure_requested_ciphers"));
    }
    
    static {
        log = LogFactory.getLog((Class)JSSESupport.class);
        sm = StringManager.getManager((Class)JSSESupport.class);
        keySizeCache = new HashMap<String, Integer>();
        for (final Cipher cipher : Cipher.values()) {
            for (final String jsseName : cipher.getJsseNames()) {
                JSSESupport.keySizeCache.put(jsseName, cipher.getStrength_bits());
            }
        }
    }
}
