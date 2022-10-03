package sun.security.ssl;

import sun.security.util.AlgorithmDecomposer;
import sun.security.util.LegacyAlgorithmConstraints;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetLongAction;
import java.security.AlgorithmConstraints;

class ServerHandshakeContext extends HandshakeContext
{
    static final boolean rejectClientInitiatedRenego;
    static final AlgorithmConstraints legacyAlgorithmConstraints;
    SSLPossession interimAuthn;
    StatusResponseManager.StaplingParameters stapleParams;
    CertificateMessage.CertificateEntry currentCertEntry;
    private static final long DEFAULT_STATUS_RESP_DELAY = 5000L;
    final long statusRespTimeout;
    
    ServerHandshakeContext(final SSLContextImpl sslContextImpl, final TransportContext transportContext) throws IOException {
        super(sslContextImpl, transportContext);
        final long longValue = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("jdk.tls.stapling.responseTimeout", 5000L));
        this.statusRespTimeout = ((longValue >= 0L) ? longValue : 5000L);
        this.handshakeConsumers.put(SSLHandshake.CLIENT_HELLO.id, SSLHandshake.CLIENT_HELLO);
    }
    
    @Override
    void kickstart() throws IOException {
        if (!this.conContext.isNegotiated || this.kickstartMessageDelivered) {
            return;
        }
        SSLHandshake.kickstart(this);
        this.kickstartMessageDelivered = true;
    }
    
    static {
        rejectClientInitiatedRenego = Utilities.getBooleanProperty("jdk.tls.rejectClientInitiatedRenegotiation", false);
        legacyAlgorithmConstraints = new LegacyAlgorithmConstraints("jdk.tls.legacyAlgorithms", new SSLAlgorithmDecomposer());
    }
}
