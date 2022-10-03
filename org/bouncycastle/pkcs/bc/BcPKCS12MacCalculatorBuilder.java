package org.bouncycastle.pkcs.bc;

import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.crypto.digests.SHA1Digest;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;

public class BcPKCS12MacCalculatorBuilder implements PKCS12MacCalculatorBuilder
{
    private ExtendedDigest digest;
    private AlgorithmIdentifier algorithmIdentifier;
    private SecureRandom random;
    private int saltLength;
    private int iterationCount;
    
    public BcPKCS12MacCalculatorBuilder() {
        this((ExtendedDigest)new SHA1Digest(), new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
    }
    
    public BcPKCS12MacCalculatorBuilder(final ExtendedDigest digest, final AlgorithmIdentifier algorithmIdentifier) {
        this.iterationCount = 1024;
        this.digest = digest;
        this.algorithmIdentifier = algorithmIdentifier;
        this.saltLength = digest.getDigestSize();
    }
    
    public BcPKCS12MacCalculatorBuilder setIterationCount(final int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }
    
    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return this.algorithmIdentifier;
    }
    
    public MacCalculator build(final char[] array) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        final byte[] array2 = new byte[this.saltLength];
        this.random.nextBytes(array2);
        return PKCS12PBEUtils.createMacCalculator(this.algorithmIdentifier.getAlgorithm(), this.digest, new PKCS12PBEParams(array2, this.iterationCount), array);
    }
}
