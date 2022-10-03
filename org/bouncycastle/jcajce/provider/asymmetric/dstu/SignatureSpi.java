package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import java.math.BigInteger;
import org.bouncycastle.asn1.DEROctetString;
import java.security.SignatureException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jce.interfaces.ECKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import java.security.PublicKey;
import org.bouncycastle.crypto.signers.DSTU4145Signer;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public class SignatureSpi extends java.security.SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    private Digest digest;
    private DSA signer;
    private static byte[] DEFAULT_SBOX;
    
    public SignatureSpi() {
        this.signer = new DSTU4145Signer();
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (publicKey instanceof BCDSTU4145PublicKey) {
            asymmetricKeyParameter = ((BCDSTU4145PublicKey)publicKey).engineGetKeyParameters();
        }
        else {
            asymmetricKeyParameter = ECUtil.generatePublicKeyParameter(publicKey);
        }
        this.digest = new GOST3411Digest(this.expandSbox(((BCDSTU4145PublicKey)publicKey).getSbox()));
        this.signer.init(false, asymmetricKeyParameter);
    }
    
    byte[] expandSbox(final byte[] array) {
        final byte[] array2 = new byte[128];
        for (int i = 0; i < array.length; ++i) {
            array2[i * 2] = (byte)(array[i] >> 4 & 0xF);
            array2[i * 2 + 1] = (byte)(array[i] & 0xF);
        }
        return array2;
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters generatePrivateKeyParameter = null;
        if (privateKey instanceof ECKey) {
            generatePrivateKeyParameter = ECUtil.generatePrivateKeyParameter(privateKey);
        }
        this.digest = new GOST3411Digest(SignatureSpi.DEFAULT_SBOX);
        if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(generatePrivateKeyParameter, this.appRandom));
        }
        else {
            this.signer.init(true, generatePrivateKeyParameter);
        }
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
            final byte[] byteArray = generateSignature[0].toByteArray();
            final byte[] byteArray2 = generateSignature[1].toByteArray();
            final byte[] array2 = new byte[(byteArray.length > byteArray2.length) ? (byteArray.length * 2) : (byteArray2.length * 2)];
            System.arraycopy(byteArray2, 0, array2, array2.length / 2 - byteArray2.length, byteArray2.length);
            System.arraycopy(byteArray, 0, array2, array2.length - byteArray.length, byteArray.length);
            return new DEROctetString(array2).getEncoded();
        }
        catch (final Exception ex) {
            throw new SignatureException(ex.toString());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        BigInteger[] array5;
        try {
            final byte[] octets = ((ASN1OctetString)ASN1Primitive.fromByteArray(array)).getOctets();
            final byte[] array3 = new byte[octets.length / 2];
            final byte[] array4 = new byte[octets.length / 2];
            System.arraycopy(octets, 0, array4, 0, octets.length / 2);
            System.arraycopy(octets, octets.length / 2, array3, 0, octets.length / 2);
            array5 = new BigInteger[] { new BigInteger(1, array3), new BigInteger(1, array4) };
        }
        catch (final Exception ex) {
            throw new SignatureException("error decoding signature bytes.");
        }
        return this.signer.verifySignature(array2, array5[0], array5[1]);
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
    
    static {
        SignatureSpi.DEFAULT_SBOX = new byte[] { 10, 9, 13, 6, 14, 11, 4, 5, 15, 1, 3, 12, 7, 0, 8, 2, 8, 0, 12, 4, 9, 6, 7, 11, 2, 3, 1, 15, 5, 14, 10, 13, 15, 6, 5, 8, 14, 11, 10, 4, 12, 0, 3, 7, 2, 9, 1, 13, 3, 8, 13, 9, 6, 11, 15, 0, 2, 5, 12, 10, 4, 14, 1, 7, 15, 8, 14, 9, 7, 2, 0, 13, 12, 6, 1, 5, 11, 4, 3, 10, 2, 8, 9, 7, 5, 15, 0, 11, 12, 1, 13, 14, 10, 3, 6, 4, 3, 8, 11, 5, 6, 4, 14, 10, 2, 12, 1, 7, 9, 15, 13, 0, 1, 2, 3, 14, 6, 13, 11, 8, 15, 10, 12, 5, 7, 9, 0, 4 };
    }
}
