package sun.security.ssl;

import java.security.interfaces.ECPublicKey;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import java.security.Principal;
import java.security.PublicKey;
import java.security.interfaces.ECKey;
import java.security.spec.ECParameterSpec;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.AbstractMap;
import java.util.Map;

enum X509Authentication implements SSLAuthentication
{
    RSA("RSA", (SSLPossessionGenerator)new X509PossessionGenerator(new String[] { "RSA" })), 
    RSASSA_PSS("RSASSA-PSS", (SSLPossessionGenerator)new X509PossessionGenerator(new String[] { "RSASSA-PSS" })), 
    RSA_OR_PSS("RSA_OR_PSS", (SSLPossessionGenerator)new X509PossessionGenerator(new String[] { "RSA", "RSASSA-PSS" })), 
    DSA("DSA", (SSLPossessionGenerator)new X509PossessionGenerator(new String[] { "DSA" })), 
    EC("EC", (SSLPossessionGenerator)new X509PossessionGenerator(new String[] { "EC" }));
    
    final String keyType;
    final SSLPossessionGenerator possessionGenerator;
    
    private X509Authentication(final String keyType, final SSLPossessionGenerator possessionGenerator) {
        this.keyType = keyType;
        this.possessionGenerator = possessionGenerator;
    }
    
    static X509Authentication valueOf(final SignatureScheme signatureScheme) {
        for (final X509Authentication x509Authentication : values()) {
            if (x509Authentication.keyType.equals(signatureScheme.keyAlgorithm)) {
                return x509Authentication;
            }
        }
        return null;
    }
    
    @Override
    public SSLPossession createPossession(final HandshakeContext handshakeContext) {
        return this.possessionGenerator.createPossession(handshakeContext);
    }
    
    @Override
    public SSLHandshake[] getRelatedHandshakers(final HandshakeContext handshakeContext) {
        if (!handshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
            return new SSLHandshake[] { SSLHandshake.CERTIFICATE, SSLHandshake.CERTIFICATE_REQUEST };
        }
        return new SSLHandshake[0];
    }
    
    @Override
    public Map.Entry<Byte, HandshakeProducer>[] getHandshakeProducers(final HandshakeContext handshakeContext) {
        if (!handshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
            return new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(SSLHandshake.CERTIFICATE.id, SSLHandshake.CERTIFICATE) };
        }
        return new Map.Entry[0];
    }
    
    static final class X509Possession implements SSLPossession
    {
        final X509Certificate[] popCerts;
        final PrivateKey popPrivateKey;
        
        X509Possession(final PrivateKey popPrivateKey, final X509Certificate[] popCerts) {
            this.popCerts = popCerts;
            this.popPrivateKey = popPrivateKey;
        }
        
        ECParameterSpec getECParameterSpec() {
            if (this.popPrivateKey == null || !"EC".equals(this.popPrivateKey.getAlgorithm())) {
                return null;
            }
            if (this.popPrivateKey instanceof ECKey) {
                return ((ECKey)this.popPrivateKey).getParams();
            }
            if (this.popCerts != null && this.popCerts.length != 0) {
                final PublicKey publicKey = this.popCerts[0].getPublicKey();
                if (publicKey instanceof ECKey) {
                    return ((ECKey)publicKey).getParams();
                }
            }
            return null;
        }
    }
    
    static final class X509Credentials implements SSLCredentials
    {
        final X509Certificate[] popCerts;
        final PublicKey popPublicKey;
        
        X509Credentials(final PublicKey popPublicKey, final X509Certificate[] popCerts) {
            this.popCerts = popCerts;
            this.popPublicKey = popPublicKey;
        }
    }
    
    private static final class X509PossessionGenerator implements SSLPossessionGenerator
    {
        private final String[] keyTypes;
        
        private X509PossessionGenerator(final String[] keyTypes) {
            this.keyTypes = keyTypes;
        }
        
        @Override
        public SSLPossession createPossession(final HandshakeContext handshakeContext) {
            if (handshakeContext.sslConfig.isClientMode) {
                final String[] keyTypes = this.keyTypes;
                for (int length = keyTypes.length, i = 0; i < length; ++i) {
                    final SSLPossession clientPossession = this.createClientPossession((ClientHandshakeContext)handshakeContext, keyTypes[i]);
                    if (clientPossession != null) {
                        return clientPossession;
                    }
                }
            }
            else {
                final String[] keyTypes2 = this.keyTypes;
                for (int length2 = keyTypes2.length, j = 0; j < length2; ++j) {
                    final SSLPossession serverPossession = this.createServerPossession((ServerHandshakeContext)handshakeContext, keyTypes2[j]);
                    if (serverPossession != null) {
                        return serverPossession;
                    }
                }
            }
            return null;
        }
        
        private SSLPossession createClientPossession(final ClientHandshakeContext clientHandshakeContext, final String s) {
            final X509ExtendedKeyManager x509KeyManager = clientHandshakeContext.sslContext.getX509KeyManager();
            String s2 = null;
            if (clientHandshakeContext.conContext.transport instanceof SSLSocketImpl) {
                s2 = x509KeyManager.chooseClientAlias(new String[] { s }, (Principal[])((clientHandshakeContext.peerSupportedAuthorities == null) ? null : ((Principal[])clientHandshakeContext.peerSupportedAuthorities.clone())), (Socket)clientHandshakeContext.conContext.transport);
            }
            else if (clientHandshakeContext.conContext.transport instanceof SSLEngineImpl) {
                s2 = x509KeyManager.chooseEngineClientAlias(new String[] { s }, (Principal[])((clientHandshakeContext.peerSupportedAuthorities == null) ? null : ((Principal[])clientHandshakeContext.peerSupportedAuthorities.clone())), (SSLEngine)clientHandshakeContext.conContext.transport);
            }
            if (s2 == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest("No X.509 cert selected for " + s, new Object[0]);
                }
                return null;
            }
            final PrivateKey privateKey = x509KeyManager.getPrivateKey(s2);
            if (privateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest(s2 + " is not a private key entry", new Object[0]);
                }
                return null;
            }
            final X509Certificate[] certificateChain = x509KeyManager.getCertificateChain(s2);
            if (certificateChain == null || certificateChain.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest(s2 + " is a private key entry with no cert chain stored", new Object[0]);
                }
                return null;
            }
            final PublicKey publicKey = certificateChain[0].getPublicKey();
            if (!privateKey.getAlgorithm().equals(s) || !publicKey.getAlgorithm().equals(s)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine(s2 + " private or public key is not of " + s + " algorithm", new Object[0]);
                }
                return null;
            }
            return new X509Possession(privateKey, certificateChain);
        }
        
        private SSLPossession createServerPossession(final ServerHandshakeContext serverHandshakeContext, final String s) {
            final X509ExtendedKeyManager x509KeyManager = serverHandshakeContext.sslContext.getX509KeyManager();
            String s2 = null;
            if (serverHandshakeContext.conContext.transport instanceof SSLSocketImpl) {
                s2 = x509KeyManager.chooseServerAlias(s, (Principal[])((serverHandshakeContext.peerSupportedAuthorities == null) ? null : ((Principal[])serverHandshakeContext.peerSupportedAuthorities.clone())), (Socket)serverHandshakeContext.conContext.transport);
            }
            else if (serverHandshakeContext.conContext.transport instanceof SSLEngineImpl) {
                s2 = x509KeyManager.chooseEngineServerAlias(s, (Principal[])((serverHandshakeContext.peerSupportedAuthorities == null) ? null : ((Principal[])serverHandshakeContext.peerSupportedAuthorities.clone())), (SSLEngine)serverHandshakeContext.conContext.transport);
            }
            if (s2 == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest("No X.509 cert selected for " + s, new Object[0]);
                }
                return null;
            }
            final PrivateKey privateKey = x509KeyManager.getPrivateKey(s2);
            if (privateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest(s2 + " is not a private key entry", new Object[0]);
                }
                return null;
            }
            final X509Certificate[] certificateChain = x509KeyManager.getCertificateChain(s2);
            if (certificateChain == null || certificateChain.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest(s2 + " is not a certificate entry", new Object[0]);
                }
                return null;
            }
            final PublicKey publicKey = certificateChain[0].getPublicKey();
            if (!privateKey.getAlgorithm().equals(s) || !publicKey.getAlgorithm().equals(s)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine(s2 + " private or public key is not of " + s + " algorithm", new Object[0]);
                }
                return null;
            }
            if (!serverHandshakeContext.negotiatedProtocol.useTLS13PlusSpec() && s.equals("EC")) {
                if (!(publicKey instanceof ECPublicKey)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.warning(s2 + " public key is not an instance of ECPublicKey", new Object[0]);
                    }
                    return null;
                }
                final SupportedGroupsExtension.NamedGroup value = SupportedGroupsExtension.NamedGroup.valueOf(((ECPublicKey)publicKey).getParams());
                if (value == null || !SupportedGroupsExtension.SupportedGroups.isSupported(value) || (serverHandshakeContext.clientRequestedNamedGroups != null && !serverHandshakeContext.clientRequestedNamedGroups.contains(value))) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.warning("Unsupported named group (" + value + ") used in the " + s2 + " certificate", new Object[0]);
                    }
                    return null;
                }
            }
            return new X509Possession(privateKey, certificateChain);
        }
    }
}
