package org.openjsse.sun.security.ssl;

import sun.security.util.AlgorithmDecomposer;
import sun.security.util.DisabledAlgorithmConstraints;
import java.security.Key;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.util.Set;
import org.openjsse.javax.net.ssl.SSLEngine;
import org.openjsse.javax.net.ssl.SSLSocket;
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
    
    SSLAlgorithmConstraints(final SSLSocket socket, final boolean withDefaultCertPathConstraints) {
        this.userSpecifiedConstraints = getUserSpecifiedConstraints(socket);
        this.peerSpecifiedConstraints = null;
        this.enabledX509DisabledAlgConstraints = withDefaultCertPathConstraints;
    }
    
    SSLAlgorithmConstraints(final SSLEngine engine, final boolean withDefaultCertPathConstraints) {
        this.userSpecifiedConstraints = getUserSpecifiedConstraints(engine);
        this.peerSpecifiedConstraints = null;
        this.enabledX509DisabledAlgConstraints = withDefaultCertPathConstraints;
    }
    
    SSLAlgorithmConstraints(final SSLSocket socket, final String[] supportedAlgorithms, final boolean withDefaultCertPathConstraints) {
        this.userSpecifiedConstraints = getUserSpecifiedConstraints(socket);
        this.peerSpecifiedConstraints = new SupportedSignatureAlgorithmConstraints(supportedAlgorithms);
        this.enabledX509DisabledAlgConstraints = withDefaultCertPathConstraints;
    }
    
    SSLAlgorithmConstraints(final SSLEngine engine, final String[] supportedAlgorithms, final boolean withDefaultCertPathConstraints) {
        this.userSpecifiedConstraints = getUserSpecifiedConstraints(engine);
        this.peerSpecifiedConstraints = new SupportedSignatureAlgorithmConstraints(supportedAlgorithms);
        this.enabledX509DisabledAlgConstraints = withDefaultCertPathConstraints;
    }
    
    private static AlgorithmConstraints getUserSpecifiedConstraints(final SSLEngine engine) {
        if (engine != null) {
            if (engine instanceof SSLEngineImpl) {
                final HandshakeContext hc = ((SSLEngineImpl)engine).conContext.handshakeContext;
                if (hc != null) {
                    return hc.sslConfig.userSpecifiedAlgorithmConstraints;
                }
            }
            return engine.getSSLParameters().getAlgorithmConstraints();
        }
        return null;
    }
    
    private static AlgorithmConstraints getUserSpecifiedConstraints(final SSLSocket socket) {
        if (socket != null) {
            if (socket instanceof SSLSocketImpl) {
                final HandshakeContext hc = ((SSLSocketImpl)socket).conContext.handshakeContext;
                if (hc != null) {
                    return hc.sslConfig.userSpecifiedAlgorithmConstraints;
                }
            }
            return socket.getSSLParameters().getAlgorithmConstraints();
        }
        return null;
    }
    
    @Override
    public boolean permits(final Set<CryptoPrimitive> primitives, final String algorithm, final AlgorithmParameters parameters) {
        boolean permitted = true;
        if (this.peerSpecifiedConstraints != null) {
            permitted = this.peerSpecifiedConstraints.permits(primitives, algorithm, parameters);
        }
        if (permitted && this.userSpecifiedConstraints != null) {
            permitted = this.userSpecifiedConstraints.permits(primitives, algorithm, parameters);
        }
        if (permitted) {
            permitted = SSLAlgorithmConstraints.tlsDisabledAlgConstraints.permits(primitives, algorithm, parameters);
        }
        if (permitted && this.enabledX509DisabledAlgConstraints) {
            permitted = SSLAlgorithmConstraints.x509DisabledAlgConstraints.permits(primitives, algorithm, parameters);
        }
        return permitted;
    }
    
    @Override
    public boolean permits(final Set<CryptoPrimitive> primitives, final Key key) {
        boolean permitted = true;
        if (this.peerSpecifiedConstraints != null) {
            permitted = this.peerSpecifiedConstraints.permits(primitives, key);
        }
        if (permitted && this.userSpecifiedConstraints != null) {
            permitted = this.userSpecifiedConstraints.permits(primitives, key);
        }
        if (permitted) {
            permitted = SSLAlgorithmConstraints.tlsDisabledAlgConstraints.permits(primitives, key);
        }
        if (permitted && this.enabledX509DisabledAlgConstraints) {
            permitted = SSLAlgorithmConstraints.x509DisabledAlgConstraints.permits(primitives, key);
        }
        return permitted;
    }
    
    @Override
    public boolean permits(final Set<CryptoPrimitive> primitives, final String algorithm, final Key key, final AlgorithmParameters parameters) {
        boolean permitted = true;
        if (this.peerSpecifiedConstraints != null) {
            permitted = this.peerSpecifiedConstraints.permits(primitives, algorithm, key, parameters);
        }
        if (permitted && this.userSpecifiedConstraints != null) {
            permitted = this.userSpecifiedConstraints.permits(primitives, algorithm, key, parameters);
        }
        if (permitted) {
            permitted = SSLAlgorithmConstraints.tlsDisabledAlgConstraints.permits(primitives, algorithm, key, parameters);
        }
        if (permitted && this.enabledX509DisabledAlgConstraints) {
            permitted = SSLAlgorithmConstraints.x509DisabledAlgConstraints.permits(primitives, algorithm, key, parameters);
        }
        return permitted;
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
        
        SupportedSignatureAlgorithmConstraints(final String[] supportedAlgorithms) {
            if (supportedAlgorithms != null) {
                this.supportedAlgorithms = supportedAlgorithms.clone();
            }
            else {
                this.supportedAlgorithms = null;
            }
        }
        
        @Override
        public boolean permits(final Set<CryptoPrimitive> primitives, String algorithm, final AlgorithmParameters parameters) {
            if (algorithm == null || algorithm.length() == 0) {
                throw new IllegalArgumentException("No algorithm name specified");
            }
            if (primitives == null || primitives.isEmpty()) {
                throw new IllegalArgumentException("No cryptographic primitive specified");
            }
            if (this.supportedAlgorithms == null || this.supportedAlgorithms.length == 0) {
                return false;
            }
            final int position = algorithm.indexOf("and");
            if (position > 0) {
                algorithm = algorithm.substring(0, position);
            }
            for (final String supportedAlgorithm : this.supportedAlgorithms) {
                if (algorithm.equalsIgnoreCase(supportedAlgorithm)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public final boolean permits(final Set<CryptoPrimitive> primitives, final Key key) {
            return true;
        }
        
        @Override
        public final boolean permits(final Set<CryptoPrimitive> primitives, final String algorithm, final Key key, final AlgorithmParameters parameters) {
            if (algorithm == null || algorithm.length() == 0) {
                throw new IllegalArgumentException("No algorithm name specified");
            }
            return this.permits(primitives, algorithm, parameters);
        }
    }
}
