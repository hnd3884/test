package org.openjsse.sun.security.ssl;

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
            case TLS11:
            case DTLS10: {
                return SSLTrafficKeyDerivation.TLS10;
            }
            case TLS12:
            case DTLS12: {
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
    public SSLKeyDerivation createKeyDerivation(final HandshakeContext context, final SecretKey secretKey) throws IOException {
        return this.keyDerivationGenerator.createKeyDerivation(context, secretKey);
    }
    
    private static final class S30TrafficKeyDerivationGenerator implements SSLKeyDerivationGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context, final SecretKey secretKey) throws IOException {
            return new LegacyTrafficKeyDerivation(context, secretKey);
        }
    }
    
    private static final class T10TrafficKeyDerivationGenerator implements SSLKeyDerivationGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context, final SecretKey secretKey) throws IOException {
            return new LegacyTrafficKeyDerivation(context, secretKey);
        }
    }
    
    private static final class T12TrafficKeyDerivationGenerator implements SSLKeyDerivationGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context, final SecretKey secretKey) throws IOException {
            return new LegacyTrafficKeyDerivation(context, secretKey);
        }
    }
    
    private static final class T13TrafficKeyDerivationGenerator implements SSLKeyDerivationGenerator
    {
        @Override
        public SSLKeyDerivation createKeyDerivation(final HandshakeContext context, final SecretKey secretKey) throws IOException {
            return new T13TrafficKeyDerivation(context, secretKey);
        }
    }
    
    static final class T13TrafficKeyDerivation implements SSLKeyDerivation
    {
        private final CipherSuite cs;
        private final SecretKey secret;
        
        T13TrafficKeyDerivation(final HandshakeContext context, final SecretKey secret) {
            this.secret = secret;
            this.cs = context.negotiatedCipherSuite;
        }
        
        @Override
        public SecretKey deriveKey(final String algorithm, final AlgorithmParameterSpec params) throws IOException {
            final KeySchedule ks = KeySchedule.valueOf(algorithm);
            try {
                final HKDF hkdf = new HKDF(this.cs.hashAlg.name);
                final byte[] hkdfInfo = createHkdfInfo(ks.label, ks.getKeyLength(this.cs));
                return hkdf.expand(this.secret, hkdfInfo, ks.getKeyLength(this.cs), ks.getAlgorithm(this.cs, algorithm));
            }
            catch (final GeneralSecurityException gse) {
                throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(gse);
            }
        }
        
        private static byte[] createHkdfInfo(final byte[] label, final int length) throws IOException {
            final byte[] info = new byte[4 + label.length];
            final ByteBuffer m = ByteBuffer.wrap(info);
            try {
                Record.putInt16(m, length);
                Record.putBytes8(m, label);
                Record.putInt8(m, 0);
            }
            catch (final IOException ioe) {
                throw new RuntimeException("Unexpected exception", ioe);
            }
            return info;
        }
    }
    
    private enum KeySchedule
    {
        TlsKey("key", false), 
        TlsIv("iv", true), 
        TlsUpdateNplus1("traffic upd", false);
        
        private final byte[] label;
        private final boolean isIv;
        
        private KeySchedule(final String label, final boolean isIv) {
            this.label = ("tls13 " + label).getBytes();
            this.isIv = isIv;
        }
        
        int getKeyLength(final CipherSuite cs) {
            if (this == KeySchedule.TlsUpdateNplus1) {
                return cs.hashAlg.hashLength;
            }
            return this.isIv ? cs.bulkCipher.ivSize : cs.bulkCipher.keySize;
        }
        
        String getAlgorithm(final CipherSuite cs, final String algorithm) {
            return this.isIv ? algorithm : cs.bulkCipher.algorithm;
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
            final CipherSuite cipherSuite = context.negotiatedCipherSuite;
            final ProtocolVersion protocolVersion = context.negotiatedProtocol;
            final int hashSize = cipherSuite.macAlg.size;
            final boolean is_exportable = cipherSuite.exportable;
            final SSLCipher cipher = cipherSuite.bulkCipher;
            final int expandedKeySize = is_exportable ? cipher.expandedKeySize : 0;
            byte majorVersion = protocolVersion.major;
            byte minorVersion = protocolVersion.minor;
            String keyMaterialAlg;
            CipherSuite.HashAlg hashAlg;
            if (protocolVersion.isDTLS) {
                if (protocolVersion.id == ProtocolVersion.DTLS10.id) {
                    majorVersion = ProtocolVersion.TLS11.major;
                    minorVersion = ProtocolVersion.TLS11.minor;
                    keyMaterialAlg = "SunTlsKeyMaterial";
                    hashAlg = CipherSuite.HashAlg.H_NONE;
                }
                else {
                    majorVersion = ProtocolVersion.TLS12.major;
                    minorVersion = ProtocolVersion.TLS12.minor;
                    keyMaterialAlg = "SunTls12KeyMaterial";
                    hashAlg = cipherSuite.hashAlg;
                }
            }
            else if (protocolVersion.id >= ProtocolVersion.TLS12.id) {
                keyMaterialAlg = "SunTls12KeyMaterial";
                hashAlg = cipherSuite.hashAlg;
            }
            else {
                keyMaterialAlg = "SunTlsKeyMaterial";
                hashAlg = CipherSuite.HashAlg.H_NONE;
            }
            int ivSize = cipher.ivSize;
            if (cipher.cipherType == CipherType.AEAD_CIPHER) {
                ivSize = cipher.fixedIvSize;
            }
            else if (cipher.cipherType == CipherType.BLOCK_CIPHER && protocolVersion.useTLS11PlusSpec()) {
                ivSize = 0;
            }
            final TlsKeyMaterialParameterSpec spec = new TlsKeyMaterialParameterSpec(masterSecret, majorVersion & 0xFF, minorVersion & 0xFF, context.clientHelloRandom.randomBytes, context.serverHelloRandom.randomBytes, cipher.algorithm, cipher.keySize, expandedKeySize, ivSize, hashSize, hashAlg.name, hashAlg.hashLength, hashAlg.blockSize);
            try {
                final KeyGenerator kg = JsseJce.getKeyGenerator(keyMaterialAlg);
                kg.init(spec);
                this.keyMaterialSpec = (TlsKeyMaterialSpec)kg.generateKey();
            }
            catch (final GeneralSecurityException e) {
                throw new ProviderException(e);
            }
        }
        
        SecretKey getTrafficKey(final String algorithm) {
            switch (algorithm) {
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
                    final IvParameterSpec cliIvSpec = this.keyMaterialSpec.getClientIv();
                    return (cliIvSpec == null) ? null : new SecretKeySpec(cliIvSpec.getIV(), "TlsIv");
                }
                case "serverWriteIv": {
                    final IvParameterSpec srvIvSpec = this.keyMaterialSpec.getServerIv();
                    return (srvIvSpec == null) ? null : new SecretKeySpec(srvIvSpec.getIV(), "TlsIv");
                }
                default: {
                    return null;
                }
            }
        }
        
        @Override
        public SecretKey deriveKey(final String algorithm, final AlgorithmParameterSpec params) throws IOException {
            return this.getTrafficKey(algorithm);
        }
    }
}
