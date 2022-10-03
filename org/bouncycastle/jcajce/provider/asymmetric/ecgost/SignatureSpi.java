package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;
import java.security.SignatureException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jce.interfaces.ECKey;
import java.security.PrivateKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidKeyException;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.GOST3410Util;
import org.bouncycastle.jce.interfaces.GOST3410Key;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.signers.ECGOST3410Signer;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public class SignatureSpi extends java.security.SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    private Digest digest;
    private DSA signer;
    
    public SignatureSpi() {
        this.digest = new GOST3411Digest();
        this.signer = new ECGOST3410Signer();
    }
    
    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (publicKey instanceof ECPublicKey) {
            asymmetricKeyParameter = generatePublicKeyParameter(publicKey);
        }
        else if (publicKey instanceof GOST3410Key) {
            asymmetricKeyParameter = GOST3410Util.generatePublicKeyParameter(publicKey);
        }
        else {
            try {
                publicKey = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
                asymmetricKeyParameter = ECUtil.generatePublicKeyParameter(publicKey);
            }
            catch (final Exception ex) {
                throw new InvalidKeyException("can't recognise key type in DSA based signer");
            }
        }
        this.digest.reset();
        this.signer.init(false, asymmetricKeyParameter);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (privateKey instanceof ECKey) {
            asymmetricKeyParameter = ECUtil.generatePrivateKeyParameter(privateKey);
        }
        else {
            asymmetricKeyParameter = GOST3410Util.generatePrivateKeyParameter(privateKey);
        }
        this.digest.reset();
        if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(asymmetricKeyParameter, this.appRandom));
        }
        else {
            this.signer.init(true, asymmetricKeyParameter);
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
            final byte[] array2 = new byte[64];
            final BigInteger[] generateSignature = this.signer.generateSignature(array);
            final byte[] byteArray = generateSignature[0].toByteArray();
            final byte[] byteArray2 = generateSignature[1].toByteArray();
            if (byteArray2[0] != 0) {
                System.arraycopy(byteArray2, 0, array2, 32 - byteArray2.length, byteArray2.length);
            }
            else {
                System.arraycopy(byteArray2, 1, array2, 32 - (byteArray2.length - 1), byteArray2.length - 1);
            }
            if (byteArray[0] != 0) {
                System.arraycopy(byteArray, 0, array2, 64 - byteArray.length, byteArray.length);
            }
            else {
                System.arraycopy(byteArray, 1, array2, 64 - (byteArray.length - 1), byteArray.length - 1);
            }
            return array2;
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
            final byte[] array3 = new byte[32];
            final byte[] array4 = new byte[32];
            System.arraycopy(array, 0, array4, 0, 32);
            System.arraycopy(array, 32, array3, 0, 32);
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
    
    static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        return (publicKey instanceof BCECGOST3410PublicKey) ? ((BCECGOST3410PublicKey)publicKey).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(publicKey);
    }
}
