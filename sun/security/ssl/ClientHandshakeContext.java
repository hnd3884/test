package sun.security.ssl;

import java.io.IOException;
import java.security.cert.X509Certificate;

class ClientHandshakeContext extends HandshakeContext
{
    static final boolean allowUnsafeServerCertChange;
    X509Certificate[] reservedServerCerts;
    X509Certificate[] deferredCerts;
    ClientHello.ClientHelloMessage initialClientHelloMsg;
    byte[] pskIdentity;
    
    ClientHandshakeContext(final SSLContextImpl sslContextImpl, final TransportContext transportContext) throws IOException {
        super(sslContextImpl, transportContext);
        this.reservedServerCerts = null;
        this.initialClientHelloMsg = null;
    }
    
    @Override
    void kickstart() throws IOException {
        if (this.kickstartMessageDelivered) {
            return;
        }
        SSLHandshake.kickstart(this);
        this.kickstartMessageDelivered = true;
    }
    
    static {
        allowUnsafeServerCertChange = Utilities.getBooleanProperty("jdk.tls.allowUnsafeServerCertChange", false);
    }
}
