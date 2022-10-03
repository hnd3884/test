package org.openjsse.sun.security.ssl;

import java.security.GeneralSecurityException;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.ProviderException;
import org.openjsse.sun.security.internal.spec.TlsMasterSecretParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import javax.crypto.SecretKey;

enum SSLMasterKeyDerivation implements SSLKeyDerivationGenerator
{
    SSL30("kdf_ssl30"), 
    TLS10("kdf_tls10"), 
    TLS12("kdf_tls12");
    
    final String name;
    
    private SSLMasterKeyDerivation(final String name) {
        this.name = name;
    }
    
    static SSLMasterKeyDerivation valueOf(final ProtocolVersion protocolVersion) {
        switch (protocolVersion) {
            case SSL30: {
                return SSLMasterKeyDerivation.SSL30;
            }
            case TLS10:
            case TLS11:
            case DTLS10: {
                return SSLMasterKeyDerivation.TLS10;
            }
            case TLS12:
            case DTLS12: {
                return SSLMasterKeyDerivation.TLS12;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public SSLKeyDerivation createKeyDerivation(final HandshakeContext context, final SecretKey secretKey) throws IOException {
        return new LegacyMasterKeyDerivation(context, secretKey);
    }
    
    private static final class LegacyMasterKeyDerivation implements SSLKeyDerivation
    {
        final HandshakeContext context;
        final SecretKey preMasterSecret;
        
        LegacyMasterKeyDerivation(final HandshakeContext context, final SecretKey preMasterSecret) {
            this.context = context;
            this.preMasterSecret = preMasterSecret;
        }
        
        @Override
        public SecretKey deriveKey(final String algorithm, final AlgorithmParameterSpec params) throws IOException {
            final CipherSuite cipherSuite = this.context.negotiatedCipherSuite;
            final ProtocolVersion protocolVersion = this.context.negotiatedProtocol;
            byte majorVersion = protocolVersion.major;
            byte minorVersion = protocolVersion.minor;
            String masterAlg;
            CipherSuite.HashAlg hashAlg;
            if (protocolVersion.isDTLS) {
                if (protocolVersion.id == ProtocolVersion.DTLS10.id) {
                    majorVersion = ProtocolVersion.TLS11.major;
                    minorVersion = ProtocolVersion.TLS11.minor;
                    masterAlg = "SunTlsMasterSecret";
                    hashAlg = CipherSuite.HashAlg.H_NONE;
                }
                else {
                    majorVersion = ProtocolVersion.TLS12.major;
                    minorVersion = ProtocolVersion.TLS12.minor;
                    masterAlg = "SunTls12MasterSecret";
                    hashAlg = cipherSuite.hashAlg;
                }
            }
            else if (protocolVersion.id >= ProtocolVersion.TLS12.id) {
                masterAlg = "SunTls12MasterSecret";
                hashAlg = cipherSuite.hashAlg;
            }
            else {
                masterAlg = "SunTlsMasterSecret";
                hashAlg = CipherSuite.HashAlg.H_NONE;
            }
            TlsMasterSecretParameterSpec spec;
            if (this.context.handshakeSession.useExtendedMasterSecret) {
                masterAlg = "SunTlsExtendedMasterSecret";
                this.context.handshakeHash.utilize();
                final byte[] sessionHash = this.context.handshakeHash.digest();
                spec = new TlsMasterSecretParameterSpec(this.preMasterSecret, majorVersion & 0xFF, minorVersion & 0xFF, sessionHash, hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
            }
            else {
                spec = new TlsMasterSecretParameterSpec(this.preMasterSecret, majorVersion & 0xFF, minorVersion & 0xFF, this.context.clientHelloRandom.randomBytes, this.context.serverHelloRandom.randomBytes, hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
            }
            try {
                final KeyGenerator kg = JsseJce.getKeyGenerator(masterAlg);
                kg.init(spec);
                return kg.generateKey();
            }
            catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException iae) {
                if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                    SSLLogger.fine("RSA master secret generation error.", iae);
                }
                throw new ProviderException(iae);
            }
        }
    }
}
