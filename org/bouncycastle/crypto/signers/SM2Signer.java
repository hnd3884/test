package org.bouncycastle.crypto.signers;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.crypto.CryptoException;
import java.math.BigInteger;
import java.io.IOException;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.crypto.Signer;

public class SM2Signer implements Signer, ECConstants
{
    private final DSAKCalculator kCalculator;
    private final SM3Digest digest;
    private ECDomainParameters ecParams;
    private ECPoint pubPoint;
    private ECKeyParameters ecKey;
    private byte[] z;
    
    public SM2Signer() {
        this.kCalculator = new RandomDSAKCalculator();
        this.digest = new SM3Digest();
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        CipherParameters parameters;
        byte[] array;
        if (cipherParameters instanceof ParametersWithID) {
            parameters = ((ParametersWithID)cipherParameters).getParameters();
            array = ((ParametersWithID)cipherParameters).getID();
        }
        else {
            parameters = cipherParameters;
            array = Hex.decode("31323334353637383132333435363738");
        }
        if (b) {
            if (parameters instanceof ParametersWithRandom) {
                final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)parameters;
                this.ecKey = (ECKeyParameters)parametersWithRandom.getParameters();
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), parametersWithRandom.getRandom());
            }
            else {
                this.ecKey = (ECKeyParameters)parameters;
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), new SecureRandom());
            }
            this.pubPoint = this.createBasePointMultiplier().multiply(this.ecParams.getG(), ((ECPrivateKeyParameters)this.ecKey).getD()).normalize();
        }
        else {
            this.ecKey = (ECKeyParameters)parameters;
            this.ecParams = this.ecKey.getParameters();
            this.pubPoint = ((ECPublicKeyParameters)this.ecKey).getQ();
        }
        this.z = this.getZ(array);
        this.digest.update(this.z, 0, this.z.length);
    }
    
    public void update(final byte b) {
        this.digest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.digest.update(array, n, n2);
    }
    
    public boolean verifySignature(final byte[] array) {
        try {
            final BigInteger[] derDecode = this.derDecode(array);
            if (derDecode != null) {
                return this.verifySignature(derDecode[0], derDecode[1]);
            }
        }
        catch (final IOException ex) {}
        return false;
    }
    
    public void reset() {
        this.digest.reset();
        if (this.z != null) {
            this.digest.update(this.z, 0, this.z.length);
        }
    }
    
    public byte[] generateSignature() throws CryptoException {
        final byte[] digestDoFinal = this.digestDoFinal();
        final BigInteger n = this.ecParams.getN();
        final BigInteger calculateE = this.calculateE(digestDoFinal);
        final BigInteger d = ((ECPrivateKeyParameters)this.ecKey).getD();
        final ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        BigInteger mod;
        BigInteger mod2;
        while (true) {
            final BigInteger nextK = this.kCalculator.nextK();
            mod = calculateE.add(basePointMultiplier.multiply(this.ecParams.getG(), nextK).normalize().getAffineXCoord().toBigInteger()).mod(n);
            if (!mod.equals(SM2Signer.ZERO) && !mod.add(nextK).equals(n)) {
                mod2 = d.add(SM2Signer.ONE).modInverse(n).multiply(nextK.subtract(mod.multiply(d)).mod(n)).mod(n);
                if (!mod2.equals(SM2Signer.ZERO)) {
                    break;
                }
                continue;
            }
        }
        try {
            return this.derEncode(mod, mod2);
        }
        catch (final IOException ex) {
            throw new CryptoException("unable to encode signature: " + ex.getMessage(), ex);
        }
    }
    
    private boolean verifySignature(final BigInteger bigInteger, final BigInteger bigInteger2) {
        final BigInteger n = this.ecParams.getN();
        if (bigInteger.compareTo(SM2Signer.ONE) < 0 || bigInteger.compareTo(n) >= 0) {
            return false;
        }
        if (bigInteger2.compareTo(SM2Signer.ONE) < 0 || bigInteger2.compareTo(n) >= 0) {
            return false;
        }
        final BigInteger calculateE = this.calculateE(this.digestDoFinal());
        final BigInteger mod = bigInteger.add(bigInteger2).mod(n);
        if (mod.equals(SM2Signer.ZERO)) {
            return false;
        }
        final ECPoint normalize = ECAlgorithms.sumOfTwoMultiplies(this.ecParams.getG(), bigInteger2, ((ECPublicKeyParameters)this.ecKey).getQ(), mod).normalize();
        return !normalize.isInfinity() && calculateE.add(normalize.getAffineXCoord().toBigInteger()).mod(n).equals(bigInteger);
    }
    
    private byte[] digestDoFinal() {
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        this.reset();
        return array;
    }
    
    private byte[] getZ(final byte[] array) {
        this.digest.reset();
        this.addUserID(this.digest, array);
        this.addFieldElement(this.digest, this.ecParams.getCurve().getA());
        this.addFieldElement(this.digest, this.ecParams.getCurve().getB());
        this.addFieldElement(this.digest, this.ecParams.getG().getAffineXCoord());
        this.addFieldElement(this.digest, this.ecParams.getG().getAffineYCoord());
        this.addFieldElement(this.digest, this.pubPoint.getAffineXCoord());
        this.addFieldElement(this.digest, this.pubPoint.getAffineYCoord());
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        return array2;
    }
    
    private void addUserID(final Digest digest, final byte[] array) {
        final int n = array.length * 8;
        digest.update((byte)(n >> 8 & 0xFF));
        digest.update((byte)(n & 0xFF));
        digest.update(array, 0, array.length);
    }
    
    private void addFieldElement(final Digest digest, final ECFieldElement ecFieldElement) {
        final byte[] encoded = ecFieldElement.getEncoded();
        digest.update(encoded, 0, encoded.length);
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
    
    protected BigInteger calculateE(final byte[] array) {
        return new BigInteger(1, array);
    }
    
    protected BigInteger[] derDecode(final byte[] array) throws IOException {
        final ASN1Sequence instance = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(array));
        if (instance.size() != 2) {
            return null;
        }
        final BigInteger value = ASN1Integer.getInstance(instance.getObjectAt(0)).getValue();
        final BigInteger value2 = ASN1Integer.getInstance(instance.getObjectAt(1)).getValue();
        if (!Arrays.constantTimeAreEqual(this.derEncode(value, value2), array)) {
            return null;
        }
        return new BigInteger[] { value, value2 };
    }
    
    protected byte[] derEncode(final BigInteger bigInteger, final BigInteger bigInteger2) throws IOException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(bigInteger));
        asn1EncodableVector.add(new ASN1Integer(bigInteger2));
        return new DERSequence(asn1EncodableVector).getEncoded("DER");
    }
}
