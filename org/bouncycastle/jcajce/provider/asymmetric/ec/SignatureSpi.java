package org.bouncycastle.jcajce.provider.asymmetric.ec;

import org.bouncycastle.crypto.signers.ECNRSigner;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import java.security.PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSAEncoder;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSABase;

public class SignatureSpi extends DSABase
{
    SignatureSpi(final Digest digest, final DSA dsa, final DSAEncoder dsaEncoder) {
        super(digest, dsa, dsaEncoder);
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        final AsymmetricKeyParameter generatePublicKeyParameter = ECUtils.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, generatePublicKeyParameter);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        final AsymmetricKeyParameter generatePrivateKeyParameter = ECUtil.generatePrivateKeyParameter(privateKey);
        this.digest.reset();
        if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(generatePrivateKeyParameter, this.appRandom));
        }
        else {
            this.signer.init(true, generatePrivateKeyParameter);
        }
    }
    
    private static class PlainDSAEncoder implements DSAEncoder
    {
        public byte[] encode(final BigInteger bigInteger, final BigInteger bigInteger2) throws IOException {
            final byte[] unsigned = this.makeUnsigned(bigInteger);
            final byte[] unsigned2 = this.makeUnsigned(bigInteger2);
            byte[] array;
            if (unsigned.length > unsigned2.length) {
                array = new byte[unsigned.length * 2];
            }
            else {
                array = new byte[unsigned2.length * 2];
            }
            System.arraycopy(unsigned, 0, array, array.length / 2 - unsigned.length, unsigned.length);
            System.arraycopy(unsigned2, 0, array, array.length - unsigned2.length, unsigned2.length);
            return array;
        }
        
        private byte[] makeUnsigned(final BigInteger bigInteger) {
            final byte[] byteArray = bigInteger.toByteArray();
            if (byteArray[0] == 0) {
                final byte[] array = new byte[byteArray.length - 1];
                System.arraycopy(byteArray, 1, array, 0, array.length);
                return array;
            }
            return byteArray;
        }
        
        public BigInteger[] decode(final byte[] array) throws IOException {
            final BigInteger[] array2 = new BigInteger[2];
            final byte[] array3 = new byte[array.length / 2];
            final byte[] array4 = new byte[array.length / 2];
            System.arraycopy(array, 0, array3, 0, array3.length);
            System.arraycopy(array, array3.length, array4, 0, array4.length);
            array2[0] = new BigInteger(1, array3);
            array2[1] = new BigInteger(1, array4);
            return array2;
        }
    }
    
    private static class StdDSAEncoder implements DSAEncoder
    {
        public byte[] encode(final BigInteger bigInteger, final BigInteger bigInteger2) throws IOException {
            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            asn1EncodableVector.add(new ASN1Integer(bigInteger));
            asn1EncodableVector.add(new ASN1Integer(bigInteger2));
            return new DERSequence(asn1EncodableVector).getEncoded("DER");
        }
        
        public BigInteger[] decode(final byte[] array) throws IOException {
            final ASN1Sequence asn1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(array);
            if (asn1Sequence.size() != 2) {
                throw new IOException("malformed signature");
            }
            if (!Arrays.areEqual(array, asn1Sequence.getEncoded("DER"))) {
                throw new IOException("malformed signature");
            }
            return new BigInteger[] { ASN1Integer.getInstance(asn1Sequence.getObjectAt(0)).getValue(), ASN1Integer.getInstance(asn1Sequence.getObjectAt(1)).getValue() };
        }
    }
    
    public static class ecCVCDSA extends SignatureSpi
    {
        public ecCVCDSA() {
            super(DigestFactory.createSHA1(), new ECDSASigner(), new PlainDSAEncoder());
        }
    }
    
    public static class ecCVCDSA224 extends SignatureSpi
    {
        public ecCVCDSA224() {
            super(DigestFactory.createSHA224(), new ECDSASigner(), new PlainDSAEncoder());
        }
    }
    
    public static class ecCVCDSA256 extends SignatureSpi
    {
        public ecCVCDSA256() {
            super(DigestFactory.createSHA256(), new ECDSASigner(), new PlainDSAEncoder());
        }
    }
    
    public static class ecCVCDSA384 extends SignatureSpi
    {
        public ecCVCDSA384() {
            super(DigestFactory.createSHA384(), new ECDSASigner(), new PlainDSAEncoder());
        }
    }
    
    public static class ecCVCDSA512 extends SignatureSpi
    {
        public ecCVCDSA512() {
            super(DigestFactory.createSHA512(), new ECDSASigner(), new PlainDSAEncoder());
        }
    }
    
    public static class ecDSA extends SignatureSpi
    {
        public ecDSA() {
            super(DigestFactory.createSHA1(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSA224 extends SignatureSpi
    {
        public ecDSA224() {
            super(DigestFactory.createSHA224(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSA256 extends SignatureSpi
    {
        public ecDSA256() {
            super(DigestFactory.createSHA256(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSA384 extends SignatureSpi
    {
        public ecDSA384() {
            super(DigestFactory.createSHA384(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSA512 extends SignatureSpi
    {
        public ecDSA512() {
            super(DigestFactory.createSHA512(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSARipeMD160 extends SignatureSpi
    {
        public ecDSARipeMD160() {
            super(new RIPEMD160Digest(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSASha3_224 extends SignatureSpi
    {
        public ecDSASha3_224() {
            super(DigestFactory.createSHA3_224(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSASha3_256 extends SignatureSpi
    {
        public ecDSASha3_256() {
            super(DigestFactory.createSHA3_256(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSASha3_384 extends SignatureSpi
    {
        public ecDSASha3_384() {
            super(DigestFactory.createSHA3_384(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSASha3_512 extends SignatureSpi
    {
        public ecDSASha3_512() {
            super(DigestFactory.createSHA3_512(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDSAnone extends SignatureSpi
    {
        public ecDSAnone() {
            super(new NullDigest(), new ECDSASigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSA extends SignatureSpi
    {
        public ecDetDSA() {
            super(DigestFactory.createSHA1(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA1())), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSA224 extends SignatureSpi
    {
        public ecDetDSA224() {
            super(DigestFactory.createSHA224(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA224())), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSA256 extends SignatureSpi
    {
        public ecDetDSA256() {
            super(DigestFactory.createSHA256(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA256())), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSA384 extends SignatureSpi
    {
        public ecDetDSA384() {
            super(DigestFactory.createSHA384(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA384())), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSA512 extends SignatureSpi
    {
        public ecDetDSA512() {
            super(DigestFactory.createSHA512(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA512())), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSASha3_224 extends SignatureSpi
    {
        public ecDetDSASha3_224() {
            super(DigestFactory.createSHA3_224(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_224())), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSASha3_256 extends SignatureSpi
    {
        public ecDetDSASha3_256() {
            super(DigestFactory.createSHA3_256(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_256())), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSASha3_384 extends SignatureSpi
    {
        public ecDetDSASha3_384() {
            super(DigestFactory.createSHA3_384(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_384())), new StdDSAEncoder());
        }
    }
    
    public static class ecDetDSASha3_512 extends SignatureSpi
    {
        public ecDetDSASha3_512() {
            super(DigestFactory.createSHA3_512(), new ECDSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_512())), new StdDSAEncoder());
        }
    }
    
    public static class ecNR extends SignatureSpi
    {
        public ecNR() {
            super(DigestFactory.createSHA1(), new ECNRSigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecNR224 extends SignatureSpi
    {
        public ecNR224() {
            super(DigestFactory.createSHA224(), new ECNRSigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecNR256 extends SignatureSpi
    {
        public ecNR256() {
            super(DigestFactory.createSHA256(), new ECNRSigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecNR384 extends SignatureSpi
    {
        public ecNR384() {
            super(DigestFactory.createSHA384(), new ECNRSigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecNR512 extends SignatureSpi
    {
        public ecNR512() {
            super(DigestFactory.createSHA512(), new ECNRSigner(), new StdDSAEncoder());
        }
    }
    
    public static class ecPlainDSARP160 extends SignatureSpi
    {
        public ecPlainDSARP160() {
            super(new RIPEMD160Digest(), new ECDSASigner(), new PlainDSAEncoder());
        }
    }
}
