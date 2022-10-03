package sun.security.ssl;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.ProviderException;
import sun.security.internal.spec.TlsMasterSecretParameterSpec;
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
            case TLS11: {
                return SSLMasterKeyDerivation.TLS10;
            }
            case TLS12: {
                return SSLMasterKeyDerivation.TLS12;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext, final SecretKey secretKey) throws IOException {
        return new LegacyMasterKeyDerivation(handshakeContext, secretKey);
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
        public SecretKey deriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
            final CipherSuite negotiatedCipherSuite = this.context.negotiatedCipherSuite;
            final ProtocolVersion negotiatedProtocol = this.context.negotiatedProtocol;
            final byte major = negotiatedProtocol.major;
            final byte minor = negotiatedProtocol.minor;
            String s2;
            CipherSuite.HashAlg hashAlg;
            if (negotiatedProtocol.id >= ProtocolVersion.TLS12.id) {
                s2 = "SunTls12MasterSecret";
                hashAlg = negotiatedCipherSuite.hashAlg;
            }
            else {
                s2 = "SunTlsMasterSecret";
                hashAlg = CipherSuite.HashAlg.H_NONE;
            }
            TlsMasterSecretParameterSpec tlsMasterSecretParameterSpec;
            if (this.context.handshakeSession.useExtendedMasterSecret) {
                s2 = "SunTlsExtendedMasterSecret";
                this.context.handshakeHash.utilize();
                tlsMasterSecretParameterSpec = new TlsMasterSecretParameterSpec(this.preMasterSecret, major & 0xFF, minor & 0xFF, this.context.handshakeHash.digest(), hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
            }
            else {
                tlsMasterSecretParameterSpec = new TlsMasterSecretParameterSpec(this.preMasterSecret, major & 0xFF, minor & 0xFF, this.context.clientHelloRandom.randomBytes, this.context.serverHelloRandom.randomBytes, hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
            }
            try {
                final KeyGenerator keyGenerator = JsseJce.getKeyGenerator(s2);
                keyGenerator.init(tlsMasterSecretParameterSpec);
                return keyGenerator.generateKey();
            }
            catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                    SSLLogger.fine("RSA master secret generation error.", ex);
                }
                throw new ProviderException((Throwable)ex);
            }
        }
    }
}
