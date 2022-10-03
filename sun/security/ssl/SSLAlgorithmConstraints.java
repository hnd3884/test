package sun.security.ssl;

import sun.security.util.AlgorithmDecomposer;
import sun.security.util.DisabledAlgorithmConstraints;
import java.security.Key;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocket;
import java.security.AlgorithmConstraints;

final class SSLAlgorithmConstraints implements AlgorithmConstraints
{
    private static final AlgorithmConstraints tlsDisabledAlgConstraints;
    private static final AlgorithmConstraints x509DisabledAlgConstraints;
    private final AlgorithmConstraints userSpecifiedConstraints;
    private final AlgorithmConstraints peerSpecifiedConstraints;
    private final boolean enabledX509DisabledAlgConstraints;
    static final AlgorithmConstraints DEFAULT;
    static final AlgorithmConstraints DEFAULT_SSL_ONLY;
    
    SSLAlgorithmConstraints(final AlgorithmConstraints userSpecifiedConstraints) {
        this.userSpecifiedConstraints = userSpecifiedConstraints;
        this.peerSpecifiedConstraints = null;
        this.enabledX509DisabledAlgConstraints = true;
    }
    
    SSLAlgorithmConstraints(final SSLSocket sslSocket, final boolean enabledX509DisabledAlgConstraints) {
        this.userSpecifiedConstraints = getUserSpecifiedConstraints(sslSocket);
        this.peerSpecifiedConstraints = null;
        this.enabledX509DisabledAlgConstraints = enabledX509DisabledAlgConstraints;
    }
    
    SSLAlgorithmConstraints(final SSLEngine sslEngine, final boolean enabledX509DisabledAlgConstraints) {
        this.userSpecifiedConstraints = getUserSpecifiedConstraints(sslEngine);
        this.peerSpecifiedConstraints = null;
        this.enabledX509DisabledAlgConstraints = enabledX509DisabledAlgConstraints;
    }
    
    SSLAlgorithmConstraints(final SSLSocket sslSocket, final String[] array, final boolean enabledX509DisabledAlgConstraints) {
        this.userSpecifiedConstraints = getUserSpecifiedConstraints(sslSocket);
        this.peerSpecifiedConstraints = new SupportedSignatureAlgorithmConstraints(array);
        this.enabledX509DisabledAlgConstraints = enabledX509DisabledAlgConstraints;
    }
    
    SSLAlgorithmConstraints(final SSLEngine sslEngine, final String[] array, final boolean enabledX509DisabledAlgConstraints) {
        this.userSpecifiedConstraints = getUserSpecifiedConstraints(sslEngine);
        this.peerSpecifiedConstraints = new SupportedSignatureAlgorithmConstraints(array);
        this.enabledX509DisabledAlgConstraints = enabledX509DisabledAlgConstraints;
    }
    
    private static AlgorithmConstraints getUserSpecifiedConstraints(final SSLEngine sslEngine) {
        if (sslEngine != null) {
            if (sslEngine instanceof SSLEngineImpl) {
                final HandshakeContext handshakeContext = ((SSLEngineImpl)sslEngine).conContext.handshakeContext;
                if (handshakeContext != null) {
                    return handshakeContext.sslConfig.userSpecifiedAlgorithmConstraints;
                }
            }
            return sslEngine.getSSLParameters().getAlgorithmConstraints();
        }
        return null;
    }
    
    private static AlgorithmConstraints getUserSpecifiedConstraints(final SSLSocket sslSocket) {
        if (sslSocket != null) {
            if (sslSocket instanceof SSLSocketImpl) {
                final HandshakeContext handshakeContext = ((SSLSocketImpl)sslSocket).conContext.handshakeContext;
                if (handshakeContext != null) {
                    return handshakeContext.sslConfig.userSpecifiedAlgorithmConstraints;
                }
            }
            return sslSocket.getSSLParameters().getAlgorithmConstraints();
        }
        return null;
    }
    
    @Override
    public boolean permits(final Set<CryptoPrimitive> set, final String s, final AlgorithmParameters algorithmParameters) {
        boolean b = true;
        if (this.peerSpecifiedConstraints != null) {
            b = this.peerSpecifiedConstraints.permits(set, s, algorithmParameters);
        }
        if (b && this.userSpecifiedConstraints != null) {
            b = this.userSpecifiedConstraints.permits(set, s, algorithmParameters);
        }
        if (b) {
            b = SSLAlgorithmConstraints.tlsDisabledAlgConstraints.permits(set, s, algorithmParameters);
        }
        if (b && this.enabledX509DisabledAlgConstraints) {
            b = SSLAlgorithmConstraints.x509DisabledAlgConstraints.permits(set, s, algorithmParameters);
        }
        return b;
    }
    
    @Override
    public boolean permits(final Set<CryptoPrimitive> set, final Key key) {
        boolean b = true;
        if (this.peerSpecifiedConstraints != null) {
            b = this.peerSpecifiedConstraints.permits(set, key);
        }
        if (b && this.userSpecifiedConstraints != null) {
            b = this.userSpecifiedConstraints.permits(set, key);
        }
        if (b) {
            b = SSLAlgorithmConstraints.tlsDisabledAlgConstraints.permits(set, key);
        }
        if (b && this.enabledX509DisabledAlgConstraints) {
            b = SSLAlgorithmConstraints.x509DisabledAlgConstraints.permits(set, key);
        }
        return b;
    }
    
    @Override
    public boolean permits(final Set<CryptoPrimitive> set, final String s, final Key key, final AlgorithmParameters algorithmParameters) {
        boolean b = true;
        if (this.peerSpecifiedConstraints != null) {
            b = this.peerSpecifiedConstraints.permits(set, s, key, algorithmParameters);
        }
        if (b && this.userSpecifiedConstraints != null) {
            b = this.userSpecifiedConstraints.permits(set, s, key, algorithmParameters);
        }
        if (b) {
            b = SSLAlgorithmConstraints.tlsDisabledAlgConstraints.permits(set, s, key, algorithmParameters);
        }
        if (b && this.enabledX509DisabledAlgConstraints) {
            b = SSLAlgorithmConstraints.x509DisabledAlgConstraints.permits(set, s, key, algorithmParameters);
        }
        return b;
    }
    
    static {
        tlsDisabledAlgConstraints = new DisabledAlgorithmConstraints("jdk.tls.disabledAlgorithms", new SSLAlgorithmDecomposer());
        x509DisabledAlgConstraints = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms", new SSLAlgorithmDecomposer(true));
        DEFAULT = new SSLAlgorithmConstraints(null);
        DEFAULT_SSL_ONLY = new SSLAlgorithmConstraints((SSLSocket)null, false);
    }
    
    private static class SupportedSignatureAlgorithmConstraints implements AlgorithmConstraints
    {
        private String[] supportedAlgorithms;
        
        SupportedSignatureAlgorithmConstraints(final String[] array) {
            if (array != null) {
                this.supportedAlgorithms = array.clone();
            }
            else {
                this.supportedAlgorithms = null;
            }
        }
        
        @Override
        public boolean permits(final Set<CryptoPrimitive> set, String substring, final AlgorithmParameters algorithmParameters) {
            if (substring == null || substring.isEmpty()) {
                throw new IllegalArgumentException("No algorithm name specified");
            }
            if (set == null || set.isEmpty()) {
                throw new IllegalArgumentException("No cryptographic primitive specified");
            }
            if (this.supportedAlgorithms == null || this.supportedAlgorithms.length == 0) {
                return false;
            }
            final int index = substring.indexOf("and");
            if (index > 0) {
                substring = substring.substring(0, index);
            }
            final String[] supportedAlgorithms = this.supportedAlgorithms;
            for (int length = supportedAlgorithms.length, i = 0; i < length; ++i) {
                if (substring.equalsIgnoreCase(supportedAlgorithms[i])) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public final boolean permits(final Set<CryptoPrimitive> set, final Key key) {
            return true;
        }
        
        @Override
        public final boolean permits(final Set<CryptoPrimitive> set, final String s, final Key key, final AlgorithmParameters algorithmParameters) {
            if (s == null || s.isEmpty()) {
                throw new IllegalArgumentException("No algorithm name specified");
            }
            return this.permits(set, s, algorithmParameters);
        }
    }
}
