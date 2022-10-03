package sun.security.ssl;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import java.security.ProviderException;
import sun.security.internal.spec.TlsKeyMaterialParameterSpec;
import sun.security.internal.spec.TlsKeyMaterialSpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLHandshakeException;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import javax.crypto.SecretKey;

enum SSLTrafficKeyDerivation implements SSLKeyDerivationGenerator
{
    SSL30("kdf_ssl30", (SSLKeyDerivationGenerator)new S30TrafficKeyDerivationGenerator()), 
    TLS10("kdf_tls10", (SSLKeyDerivationGenerator)new T10TrafficKeyDerivationGenerator()), 
    TLS12("kdf_tls12", (SSLKeyDerivationGenerator)new T12TrafficKeyDerivationGenerator()), 
    TLS13("kdf_tls13", (SSLKeyDerivationGenerator)new T13TrafficKeyDerivationGenerator());
    
    final String name;
    final SSLKeyDerivationGenerator keyDerivationGenerator;
    
    private SSLTrafficKeyDerivation(final String name, final SSLKeyDerivationGenerator keyDerivationGenerator) {
        this.name = name;
        this.keyDerivationGenerator = keyDerivationGenerator;
    }
    
    static SSLTrafficKeyDerivation valueOf(final ProtocolVersion protocolVersion) {
        switch (protocolVersion) {
            case SSL30: {
                return SSLTrafficKeyDerivation.SSL30;
            }
            case TLS10:
            case TLS11: {
                return SSLTrafficKeyDerivation.TLS10;
            }
            case TLS12: {
                return SSLTrafficKeyDerivation.TLS12;
            }
            case TLS13: {
                return SSLTrafficKeyDerivation.TLS13;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext, final SecretKey secretKey) throws IOException {
        return this.keyDerivationGenerator.createKeyDerivation(handshakeContext, secretKey);
    }
    
    private static final class S30TrafficKeyDerivationGenerator implements SSLKeyDerivationGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext, final SecretKey secretKey) throws IOException {
            return new LegacyTrafficKeyDerivation(handshakeContext, secretKey);
        }
    }
    
    private static final class T10TrafficKeyDerivationGenerator implements SSLKeyDerivationGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext, final SecretKey secretKey) throws IOException {
            return new LegacyTrafficKeyDerivation(handshakeContext, secretKey);
        }
    }
    
    private static final class T12TrafficKeyDerivationGenerator implements SSLKeyDerivationGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext, final SecretKey secretKey) throws IOException {
            return new LegacyTrafficKeyDerivation(handshakeContext, secretKey);
        }
    }
    
    private static final class T13TrafficKeyDerivationGenerator implements SSLKeyDerivationGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext handshakeContext, final SecretKey secretKey) throws IOException {
            return new T13TrafficKeyDerivation(handshakeContext, secretKey);
        }
    }
    
    static final class T13TrafficKeyDerivation implements SSLKeyDerivation
    {
        private final CipherSuite cs;
        private final SecretKey secret;
        
        T13TrafficKeyDerivation(final HandshakeContext handshakeContext, final SecretKey secret) {
            this.secret = secret;
            this.cs = handshakeContext.negotiatedCipherSuite;
        }
        
        @Override
        public SecretKey deriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
            final KeySchedule value = KeySchedule.valueOf(s);
            try {
                return new HKDF(this.cs.hashAlg.name).expand(this.secret, createHkdfInfo(value.label, value.getKeyLength(this.cs)), value.getKeyLength(this.cs), value.getAlgorithm(this.cs, s));
            }
            catch (final GeneralSecurityException ex) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(ex);
            }
        }
        
        private static byte[] createHkdfInfo(final byte[] array, final int n) throws IOException {
            final byte[] array2 = new byte[4 + array.length];
            final ByteBuffer wrap = ByteBuffer.wrap(array2);
            try {
                Record.putInt16(wrap, n);
                Record.putBytes8(wrap, array);
                Record.putInt8(wrap, 0);
            }
            catch (final IOException ex) {
                throw new RuntimeException("Unexpected exception", ex);
            }
            return array2;
        }
    }
    
    private enum KeySchedule
    {
        TlsKey("key", false), 
        TlsIv("iv", true), 
        TlsUpdateNplus1("traffic upd", false);
        
        private final byte[] label;
        private final boolean isIv;
        
        private KeySchedule(final String s2, final boolean isIv) {
            this.label = ("tls13 " + s2).getBytes();
            this.isIv = isIv;
        }
        
        int getKeyLength(final CipherSuite cipherSuite) {
            if (this == KeySchedule.TlsUpdateNplus1) {
                return cipherSuite.hashAlg.hashLength;
            }
            return this.isIv ? cipherSuite.bulkCipher.ivSize : cipherSuite.bulkCipher.keySize;
        }
        
        String getAlgorithm(final CipherSuite cipherSuite, final String s) {
            return this.isIv ? s : cipherSuite.bulkCipher.algorithm;
        }
    }
    
    static final class LegacyTrafficKeyDerivation implements SSLKeyDerivation
    {
        private final HandshakeContext context;
        private final SecretKey masterSecret;
        private final TlsKeyMaterialSpec keyMaterialSpec;
        
        LegacyTrafficKeyDerivation(final HandshakeContext context, final SecretKey masterSecret) {
            this.context = context;
            this.masterSecret = masterSecret;
            final CipherSuite negotiatedCipherSuite = context.negotiatedCipherSuite;
            final ProtocolVersion negotiatedProtocol = context.negotiatedProtocol;
            final int size = negotiatedCipherSuite.macAlg.size;
            final boolean exportable = negotiatedCipherSuite.exportable;
            final SSLCipher bulkCipher = negotiatedCipherSuite.bulkCipher;
            final int n = exportable ? bulkCipher.expandedKeySize : 0;
            final byte major = negotiatedProtocol.major;
            final byte minor = negotiatedProtocol.minor;
            String s;
            CipherSuite.HashAlg hashAlg;
            if (negotiatedProtocol.id >= ProtocolVersion.TLS12.id) {
                s = "SunTls12KeyMaterial";
                hashAlg = negotiatedCipherSuite.hashAlg;
            }
            else {
                s = "SunTlsKeyMaterial";
                hashAlg = CipherSuite.HashAlg.H_NONE;
            }
            int n2 = bulkCipher.ivSize;
            if (bulkCipher.cipherType == CipherType.AEAD_CIPHER) {
                n2 = bulkCipher.fixedIvSize;
            }
            else if (bulkCipher.cipherType == CipherType.BLOCK_CIPHER && negotiatedProtocol.useTLS11PlusSpec()) {
                n2 = 0;
            }
            final TlsKeyMaterialParameterSpec tlsKeyMaterialParameterSpec = new TlsKeyMaterialParameterSpec(masterSecret, major & 0xFF, minor & 0xFF, context.clientHelloRandom.randomBytes, context.serverHelloRandom.randomBytes, bulkCipher.algorithm, bulkCipher.keySize, n, n2, size, hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
            try {
                final KeyGenerator keyGenerator = JsseJce.getKeyGenerator(s);
                keyGenerator.init(tlsKeyMaterialParameterSpec);
                this.keyMaterialSpec = (TlsKeyMaterialSpec)keyGenerator.generateKey();
            }
            catch (final GeneralSecurityException ex) {
                throw new ProviderException(ex);
            }
        }
        
        SecretKey getTrafficKey(final String s) {
            switch (s) {
                case "clientMacKey": {
                    return this.keyMaterialSpec.getClientMacKey();
                }
                case "serverMacKey": {
                    return this.keyMaterialSpec.getServerMacKey();
                }
                case "clientWriteKey": {
                    return this.keyMaterialSpec.getClientCipherKey();
                }
                case "serverWriteKey": {
                    return this.keyMaterialSpec.getServerCipherKey();
                }
                case "clientWriteIv": {
                    final IvParameterSpec clientIv = this.keyMaterialSpec.getClientIv();
                    return (clientIv == null) ? null : new SecretKeySpec(clientIv.getIV(), "TlsIv");
                }
                case "serverWriteIv": {
                    final IvParameterSpec serverIv = this.keyMaterialSpec.getServerIv();
                    return (serverIv == null) ? null : new SecretKeySpec(serverIv.getIV(), "TlsIv");
                }
                default: {
                    return null;
                }
            }
        }
        
        @Override
        public SecretKey deriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
            return this.getTrafficKey(s);
        }
    }
}
