package org.bouncycastle.cert.dane;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;

public class TruncatingDigestCalculator implements DigestCalculator
{
    private final DigestCalculator baseCalculator;
    private final int length;
    
    public TruncatingDigestCalculator(final DigestCalculator digestCalculator) {
        this(digestCalculator, 28);
    }
    
    public TruncatingDigestCalculator(final DigestCalculator baseCalculator, final int length) {
        this.baseCalculator = baseCalculator;
        this.length = length;
    }
    
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.baseCalculator.getAlgorithmIdentifier();
    }
    
    public OutputStream getOutputStream() {
        return this.baseCalculator.getOutputStream();
    }
    
    public byte[] getDigest() {
        final byte[] array = new byte[this.length];
        System.arraycopy(this.baseCalculator.getDigest(), 0, array, 0, array.length);
        return array;
    }
}
