package com.sun.crypto.provider;

import sun.security.internal.interfaces.TlsMasterSecret;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.MessageDigest;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import sun.security.internal.spec.TlsMasterSecretParameterSpec;
import javax.crypto.KeyGeneratorSpi;

public final class TlsMasterSecretGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec";
    private TlsMasterSecretParameterSpec spec;
    private int protocolVersion;
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof TlsMasterSecretParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
        }
        this.spec = (TlsMasterSecretParameterSpec)algorithmParameterSpec;
        if (!"RAW".equals(this.spec.getPremasterSecret().getFormat())) {
            throw new InvalidAlgorithmParameterException("Key format must be RAW");
        }
        this.protocolVersion = (this.spec.getMajorVersion() << 8 | this.spec.getMinorVersion());
        if (this.protocolVersion < 768 || this.protocolVersion > 771) {
            throw new InvalidAlgorithmParameterException("Only SSL 3.0, TLS 1.0/1.1/1.2 supported");
        }
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsMasterSecretGenerator must be initialized");
        }
        final SecretKey premasterSecret = this.spec.getPremasterSecret();
        final byte[] encoded = premasterSecret.getEncoded();
        int n;
        int n2;
        if (premasterSecret.getAlgorithm().equals("TlsRsaPremasterSecret")) {
            n = (encoded[0] & 0xFF);
            n2 = (encoded[1] & 0xFF);
        }
        else {
            n = -1;
            n2 = -1;
        }
        try {
            byte[] array2;
            if (this.protocolVersion >= 769) {
                final byte[] extendedMasterSecretSessionHash = this.spec.getExtendedMasterSecretSessionHash();
                byte[] array;
                byte[] concat;
                if (extendedMasterSecretSessionHash.length != 0) {
                    array = TlsPrfGenerator.LABEL_EXTENDED_MASTER_SECRET;
                    concat = extendedMasterSecretSessionHash;
                }
                else {
                    final byte[] clientRandom = this.spec.getClientRandom();
                    final byte[] serverRandom = this.spec.getServerRandom();
                    array = TlsPrfGenerator.LABEL_MASTER_SECRET;
                    concat = TlsPrfGenerator.concat(clientRandom, serverRandom);
                }
                array2 = ((this.protocolVersion >= 771) ? TlsPrfGenerator.doTLS12PRF(encoded, array, concat, 48, this.spec.getPRFHashAlg(), this.spec.getPRFHashLength(), this.spec.getPRFBlockSize()) : TlsPrfGenerator.doTLS10PRF(encoded, array, concat, 48));
            }
            else {
                array2 = new byte[48];
                final MessageDigest instance = MessageDigest.getInstance("MD5");
                final MessageDigest instance2 = MessageDigest.getInstance("SHA");
                final byte[] clientRandom2 = this.spec.getClientRandom();
                final byte[] serverRandom2 = this.spec.getServerRandom();
                final byte[] array3 = new byte[20];
                for (int i = 0; i < 3; ++i) {
                    instance2.update(TlsPrfGenerator.SSL3_CONST[i]);
                    instance2.update(encoded);
                    instance2.update(clientRandom2);
                    instance2.update(serverRandom2);
                    instance2.digest(array3, 0, 20);
                    instance.update(encoded);
                    instance.update(array3);
                    instance.digest(array2, i << 4, 16);
                }
            }
            return new TlsMasterSecretKey(array2, n, n2);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new ProviderException(ex);
        }
        catch (final DigestException ex2) {
            throw new ProviderException(ex2);
        }
    }
    
    private static final class TlsMasterSecretKey implements TlsMasterSecret
    {
        private static final long serialVersionUID = 1019571680375368880L;
        private byte[] key;
        private final int majorVersion;
        private final int minorVersion;
        
        TlsMasterSecretKey(final byte[] key, final int majorVersion, final int minorVersion) {
            this.key = key;
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
        }
        
        @Override
        public int getMajorVersion() {
            return this.majorVersion;
        }
        
        @Override
        public int getMinorVersion() {
            return this.minorVersion;
        }
        
        @Override
        public String getAlgorithm() {
            return "TlsMasterSecret";
        }
        
        @Override
        public String getFormat() {
            return "RAW";
        }
        
        @Override
        public byte[] getEncoded() {
            return this.key.clone();
        }
    }
}
