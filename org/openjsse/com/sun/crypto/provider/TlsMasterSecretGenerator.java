package org.openjsse.com.sun.crypto.provider;

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
import org.openjsse.sun.security.internal.spec.TlsMasterSecretParameterSpec;
import javax.crypto.KeyGeneratorSpi;

public final class TlsMasterSecretGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec";
    private TlsMasterSecretParameterSpec spec;
    private int protocolVersion;
    
    @Override
    protected void engineInit(final SecureRandom random) {
        throw new InvalidParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof TlsMasterSecretParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
        }
        this.spec = (TlsMasterSecretParameterSpec)params;
        if (!"RAW".equals(this.spec.getPremasterSecret().getFormat())) {
            throw new InvalidAlgorithmParameterException("Key format must be RAW");
        }
        this.protocolVersion = (this.spec.getMajorVersion() << 8 | this.spec.getMinorVersion());
        if (this.protocolVersion < 768 || this.protocolVersion > 771) {
            throw new InvalidAlgorithmParameterException("Only SSL 3.0, TLS 1.0/1.1/1.2 supported");
        }
    }
    
    @Override
    protected void engineInit(final int keysize, final SecureRandom random) {
        throw new InvalidParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsMasterSecretGenerator must be initialized");
        }
        final SecretKey premasterKey = this.spec.getPremasterSecret();
        final byte[] premaster = premasterKey.getEncoded();
        int premasterMajor;
        int premasterMinor;
        if (premasterKey.getAlgorithm().equals("TlsRsaPremasterSecret")) {
            premasterMajor = (premaster[0] & 0xFF);
            premasterMinor = (premaster[1] & 0xFF);
        }
        else {
            premasterMajor = -1;
            premasterMinor = -1;
        }
        try {
            byte[] master;
            if (this.protocolVersion >= 769) {
                final byte[] extendedMasterSecretSessionHash = this.spec.getExtendedMasterSecretSessionHash();
                byte[] label;
                byte[] seed;
                if (extendedMasterSecretSessionHash.length != 0) {
                    label = TlsPrfGenerator.LABEL_EXTENDED_MASTER_SECRET;
                    seed = extendedMasterSecretSessionHash;
                }
                else {
                    final byte[] clientRandom = this.spec.getClientRandom();
                    final byte[] serverRandom = this.spec.getServerRandom();
                    label = TlsPrfGenerator.LABEL_MASTER_SECRET;
                    seed = TlsPrfGenerator.concat(clientRandom, serverRandom);
                }
                master = ((this.protocolVersion >= 771) ? TlsPrfGenerator.doTLS12PRF(premaster, label, seed, 48, this.spec.getPRFHashAlg(), this.spec.getPRFHashLength(), this.spec.getPRFBlockSize()) : TlsPrfGenerator.doTLS10PRF(premaster, label, seed, 48));
            }
            else {
                master = new byte[48];
                final MessageDigest md5 = MessageDigest.getInstance("MD5");
                final MessageDigest sha = MessageDigest.getInstance("SHA");
                final byte[] clientRandom2 = this.spec.getClientRandom();
                final byte[] serverRandom2 = this.spec.getServerRandom();
                final byte[] tmp = new byte[20];
                for (int i = 0; i < 3; ++i) {
                    sha.update(TlsPrfGenerator.SSL3_CONST[i]);
                    sha.update(premaster);
                    sha.update(clientRandom2);
                    sha.update(serverRandom2);
                    sha.digest(tmp, 0, 20);
                    md5.update(premaster);
                    md5.update(tmp);
                    md5.digest(master, i << 4, 16);
                }
            }
            return new TlsMasterSecretKey(master, premasterMajor, premasterMinor);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new ProviderException(e);
        }
        catch (final DigestException e2) {
            throw new ProviderException(e2);
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
