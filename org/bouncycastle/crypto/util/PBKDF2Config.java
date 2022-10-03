package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.util.Integers;
import java.util.HashMap;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PBKDF2Config extends PBKDFConfig
{
    public static final AlgorithmIdentifier PRF_SHA1;
    public static final AlgorithmIdentifier PRF_SHA256;
    public static final AlgorithmIdentifier PRF_SHA512;
    public static final AlgorithmIdentifier PRF_SHA3_256;
    public static final AlgorithmIdentifier PRF_SHA3_512;
    private static final Map PRFS_SALT;
    private final int iterationCount;
    private final int saltLength;
    private final AlgorithmIdentifier prf;
    
    static int getSaltSize(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (!PBKDF2Config.PRFS_SALT.containsKey(asn1ObjectIdentifier)) {
            throw new IllegalStateException("no salt size for algorithm: " + asn1ObjectIdentifier);
        }
        return PBKDF2Config.PRFS_SALT.get(asn1ObjectIdentifier);
    }
    
    private PBKDF2Config(final Builder builder) {
        super(PKCSObjectIdentifiers.id_PBKDF2);
        this.iterationCount = builder.iterationCount;
        this.prf = builder.prf;
        if (builder.saltLength < 0) {
            this.saltLength = getSaltSize(this.prf.getAlgorithm());
        }
        else {
            this.saltLength = builder.saltLength;
        }
    }
    
    public int getIterationCount() {
        return this.iterationCount;
    }
    
    public AlgorithmIdentifier getPRF() {
        return this.prf;
    }
    
    public int getSaltLength() {
        return this.saltLength;
    }
    
    static {
        PRF_SHA1 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);
        PRF_SHA256 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE);
        PRF_SHA512 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
        PRF_SHA3_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_256, DERNull.INSTANCE);
        PRF_SHA3_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE);
        (PRFS_SALT = new HashMap()).put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(20));
        PBKDF2Config.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(32));
        PBKDF2Config.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(64));
        PBKDF2Config.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(28));
        PBKDF2Config.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(48));
        PBKDF2Config.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(28));
        PBKDF2Config.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(32));
        PBKDF2Config.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(48));
        PBKDF2Config.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(64));
        PBKDF2Config.PRFS_SALT.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(32));
        PBKDF2Config.PRFS_SALT.put(RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_256, Integers.valueOf(32));
        PBKDF2Config.PRFS_SALT.put(RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_512, Integers.valueOf(64));
        PBKDF2Config.PRFS_SALT.put(GMObjectIdentifiers.hmac_sm3, Integers.valueOf(32));
    }
    
    public static class Builder
    {
        private int iterationCount;
        private int saltLength;
        private AlgorithmIdentifier prf;
        
        public Builder() {
            this.iterationCount = 1024;
            this.saltLength = -1;
            this.prf = PBKDF2Config.PRF_SHA1;
        }
        
        public Builder withIterationCount(final int iterationCount) {
            this.iterationCount = iterationCount;
            return this;
        }
        
        public Builder withPRF(final AlgorithmIdentifier prf) {
            this.prf = prf;
            return this;
        }
        
        public Builder withSaltLength(final int saltLength) {
            this.saltLength = saltLength;
            return this;
        }
        
        public PBKDF2Config build() {
            return new PBKDF2Config(this, null);
        }
    }
}
