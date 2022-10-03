package org.apache.tomcat.util.net.openssl;

import org.apache.juli.logging.LogFactory;
import java.io.IOException;
import java.security.KeyStoreException;
import org.apache.tomcat.util.net.jsse.JSSEKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.KeyManager;
import org.apache.tomcat.util.net.SSLContext;
import java.util.List;
import java.util.Set;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.SSLUtilBase;

public class OpenSSLUtil extends SSLUtilBase
{
    private static final Log log;
    private static final StringManager sm;
    
    public OpenSSLUtil(final SSLHostConfigCertificate certificate) {
        super(certificate);
    }
    
    @Override
    protected Log getLog() {
        return OpenSSLUtil.log;
    }
    
    @Override
    protected Set<String> getImplementedProtocols() {
        return OpenSSLEngine.IMPLEMENTED_PROTOCOLS_SET;
    }
    
    @Override
    protected Set<String> getImplementedCiphers() {
        return OpenSSLEngine.AVAILABLE_CIPHER_SUITES;
    }
    
    @Override
    protected boolean isTls13RenegAuthAvailable() {
        return true;
    }
    
    public SSLContext createSSLContextInternal(final List<String> negotiableProtocols) throws Exception {
        return new OpenSSLContext(this.certificate, negotiableProtocols);
    }
    
    public static X509KeyManager chooseKeyManager(final KeyManager[] managers) throws Exception {
        if (managers == null) {
            return null;
        }
        for (final KeyManager manager : managers) {
            if (manager instanceof JSSEKeyManager) {
                return (JSSEKeyManager)manager;
            }
        }
        for (final KeyManager manager : managers) {
            if (manager instanceof X509KeyManager) {
                return (X509KeyManager)manager;
            }
        }
        throw new IllegalStateException(OpenSSLUtil.sm.getString("openssl.keyManagerMissing"));
    }
    
    @Override
    public KeyManager[] getKeyManagers() throws Exception {
        try {
            return super.getKeyManagers();
        }
        catch (final IllegalArgumentException e) {
            final String msg = OpenSSLUtil.sm.getString("openssl.nonJsseChain", new Object[] { this.certificate.getCertificateChainFile() });
            if (OpenSSLUtil.log.isDebugEnabled()) {
                OpenSSLUtil.log.info((Object)msg, (Throwable)e);
            }
            else {
                OpenSSLUtil.log.info((Object)msg);
            }
            return null;
        }
        catch (final KeyStoreException | IOException e2) {
            if (this.certificate.getCertificateFile() != null) {
                final String msg = OpenSSLUtil.sm.getString("openssl.nonJsseCertificate", new Object[] { this.certificate.getCertificateFile(), this.certificate.getCertificateKeyFile() });
                if (OpenSSLUtil.log.isDebugEnabled()) {
                    OpenSSLUtil.log.info((Object)msg, (Throwable)e2);
                }
                else {
                    OpenSSLUtil.log.info((Object)msg);
                }
                return null;
            }
            throw e2;
        }
    }
    
    static {
        log = LogFactory.getLog((Class)OpenSSLUtil.class);
        sm = StringManager.getManager((Class)OpenSSLUtil.class);
    }
}
