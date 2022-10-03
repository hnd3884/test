package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;
import java.security.SignatureException;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.SignatureSpi;

public abstract class DSABase extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    protected Digest digest;
    protected DSA signer;
    protected DSAEncoder encoder;
    
    protected DSABase(final Digest digest, final DSA signer, final DSAEncoder encoder) {
        this.digest = digest;
        this.signer = signer;
        this.encoder = encoder;
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.digest.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.digest.update(array, n, n2);
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        try {
            final BigInteger[] generateSignature = this.signer.generateSignature(array);
            return this.encoder.encode(generateSignature[0], generateSignature[1]);
        }
        catch (final Exception ex) {
            throw new SignatureException(ex.toString());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        BigInteger[] decode;
        try {
            decode = this.encoder.decode(array);
        }
        catch (final Exception ex) {
            throw new SignatureException("error decoding signature bytes.");
        }
        return this.signer.verifySignature(array2, decode[0], decode[1]);
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    @Override
    @Deprecated
    protected void engineSetParameter(final String s, final Object o) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    @Override
    @Deprecated
    protected Object engineGetParameter(final String s) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
}
