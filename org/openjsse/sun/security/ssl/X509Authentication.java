package org.openjsse.sun.security.ssl;

import java.security.interfaces.ECPublicKey;
import javax.net.ssl.X509ExtendedKeyManager;
import org.openjsse.javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.SSLSocket;
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
        for (final X509Authentication au : values()) {
            if (au.keyType.equals(signatureScheme.keyAlgorithm)) {
                return au;
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
        public SSLPossession createPossession(final HandshakeContext context) {
            if (context.sslConfig.isClientMode) {
                for (final String keyType : this.keyTypes) {
                    final SSLPossession poss = this.createClientPossession((ClientHandshakeContext)context, keyType);
                    if (poss != null) {
                        return poss;
                    }
                }
            }
            else {
                for (final String keyType : this.keyTypes) {
                    final SSLPossession poss = this.createServerPossession((ServerHandshakeContext)context, keyType);
                    if (poss != null) {
                        return poss;
                    }
                }
            }
            return null;
        }
        
        private SSLPossession createClientPossession(final ClientHandshakeContext chc, final String keyType) {
            final X509ExtendedKeyManager km = chc.sslContext.getX509KeyManager();
            String clientAlias = null;
            if (chc.conContext.transport instanceof SSLSocketImpl) {
                clientAlias = km.chooseClientAlias(new String[] { keyType }, chc.peerSupportedAuthorities, (Socket)chc.conContext.transport);
            }
            else if (chc.conContext.transport instanceof SSLEngineImpl) {
                clientAlias = km.chooseEngineClientAlias(new String[] { keyType }, chc.peerSupportedAuthorities, (javax.net.ssl.SSLEngine)chc.conContext.transport);
            }
            if (clientAlias == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest("No X.509 cert selected for " + keyType, new Object[0]);
                }
                return null;
            }
            final PrivateKey clientPrivateKey = km.getPrivateKey(clientAlias);
            if (clientPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest(clientAlias + " is not a private key entry", new Object[0]);
                }
                return null;
            }
            final X509Certificate[] clientCerts = km.getCertificateChain(clientAlias);
            if (clientCerts == null || clientCerts.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest(clientAlias + " is a private key entry with no cert chain stored", new Object[0]);
                }
                return null;
            }
            final PublicKey clientPublicKey = clientCerts[0].getPublicKey();
            if (!clientPrivateKey.getAlgorithm().equals(keyType) || !clientPublicKey.getAlgorithm().equals(keyType)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine(clientAlias + " private or public key is not of " + keyType + " algorithm", new Object[0]);
                }
                return null;
            }
            return new X509Possession(clientPrivateKey, clientCerts);
        }
        
        private SSLPossession createServerPossession(final ServerHandshakeContext shc, final String keyType) {
            final X509ExtendedKeyManager km = shc.sslContext.getX509KeyManager();
            String serverAlias = null;
            if (shc.conContext.transport instanceof SSLSocketImpl) {
                serverAlias = km.chooseServerAlias(keyType, null, (Socket)shc.conContext.transport);
            }
            else if (shc.conContext.transport instanceof SSLEngineImpl) {
                serverAlias = km.chooseEngineServerAlias(keyType, null, (javax.net.ssl.SSLEngine)shc.conContext.transport);
            }
            if (serverAlias == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest("No X.509 cert selected for " + keyType, new Object[0]);
                }
                return null;
            }
            final PrivateKey serverPrivateKey = km.getPrivateKey(serverAlias);
            if (serverPrivateKey == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest(serverAlias + " is not a private key entry", new Object[0]);
                }
                return null;
            }
            final X509Certificate[] serverCerts = km.getCertificateChain(serverAlias);
            if (serverCerts == null || serverCerts.length == 0) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest(serverAlias + " is not a certificate entry", new Object[0]);
                }
                return null;
            }
            final PublicKey serverPublicKey = serverCerts[0].getPublicKey();
            if (!serverPrivateKey.getAlgorithm().equals(keyType) || !serverPublicKey.getAlgorithm().equals(keyType)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine(serverAlias + " private or public key is not of " + keyType + " algorithm", new Object[0]);
                }
                return null;
            }
            if (keyType.equals("EC")) {
                if (!(serverPublicKey instanceof ECPublicKey)) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.warning(serverAlias + " public key is not an instance of ECPublicKey", new Object[0]);
                    }
                    return null;
                }
                final ECParameterSpec params = ((ECPublicKey)serverPublicKey).getParams();
                final SupportedGroupsExtension.NamedGroup namedGroup = SupportedGroupsExtension.NamedGroup.valueOf(params);
                if (namedGroup == null || !SupportedGroupsExtension.SupportedGroups.isSupported(namedGroup) || (shc.clientRequestedNamedGroups != null && !shc.clientRequestedNamedGroups.contains(namedGroup))) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.warning("Unsupported named group (" + namedGroup + ") used in the " + serverAlias + " certificate", new Object[0]);
                    }
                    return null;
                }
            }
            return new X509Possession(serverPrivateKey, serverCerts);
        }
    }
}
