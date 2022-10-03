package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

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
import org.bouncycastle.jce.interfaces.ECPublicKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.signers.ECGOST3410_2012Signer;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.SignatureSpi;

public class ECGOST2012SignatureSpi512 extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    private Digest digest;
    private DSA signer;
    private int size;
    private int halfSize;
    
    public ECGOST2012SignatureSpi512() {
        this.size = 128;
        this.halfSize = 64;
        this.digest = new GOST3411_2012_512Digest();
        this.signer = new ECGOST3410_2012Signer();
    }
    
    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (publicKey instanceof ECPublicKey) {
            asymmetricKeyParameter = generatePublicKeyParameter(publicKey);
        }
        else {
            try {
                publicKey = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
                asymmetricKeyParameter = ECUtil.generatePublicKeyParameter(publicKey);
            }
            catch (final Exception ex) {
                throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-512 signer");
            }
        }
        this.digest.reset();
        this.signer.init(false, asymmetricKeyParameter);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof ECKey) {
            final AsymmetricKeyParameter generatePrivateKeyParameter = ECUtil.generatePrivateKeyParameter(privateKey);
            this.digest.reset();
            if (this.appRandom != null) {
                this.signer.init(true, new ParametersWithRandom(generatePrivateKeyParameter, this.appRandom));
            }
            else {
                this.signer.init(true, generatePrivateKeyParameter);
            }
            return;
        }
        throw new InvalidKeyException("cannot recognise key type in ECGOST-2012-512 signer");
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
            final byte[] array2 = new byte[this.size];
            final BigInteger[] generateSignature = this.signer.generateSignature(array);
            final byte[] byteArray = generateSignature[0].toByteArray();
            final byte[] byteArray2 = generateSignature[1].toByteArray();
            if (byteArray2[0] != 0) {
                System.arraycopy(byteArray2, 0, array2, this.halfSize - byteArray2.length, byteArray2.length);
            }
            else {
                System.arraycopy(byteArray2, 1, array2, this.halfSize - (byteArray2.length - 1), byteArray2.length - 1);
            }
            if (byteArray[0] != 0) {
                System.arraycopy(byteArray, 0, array2, this.size - byteArray.length, byteArray.length);
            }
            else {
                System.arraycopy(byteArray, 1, array2, this.size - (byteArray.length - 1), byteArray.length - 1);
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
            final byte[] array3 = new byte[this.halfSize];
            final byte[] array4 = new byte[this.halfSize];
            System.arraycopy(array, 0, array4, 0, this.halfSize);
            System.arraycopy(array, this.halfSize, array3, 0, this.halfSize);
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
        return (publicKey instanceof BCECGOST3410_2012PublicKey) ? ((BCECGOST3410_2012PublicKey)publicKey).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(publicKey);
    }
}
