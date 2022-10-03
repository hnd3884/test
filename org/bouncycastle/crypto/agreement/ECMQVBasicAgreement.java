package org.bouncycastle.crypto.agreement;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import org.bouncycastle.util.Properties;
import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import org.bouncycastle.crypto.BasicAgreement;

public class ECMQVBasicAgreement implements BasicAgreement
{
    MQVPrivateParameters privParams;
    
    public void init(final CipherParameters cipherParameters) {
        this.privParams = (MQVPrivateParameters)cipherParameters;
    }
    
    public int getFieldSize() {
        return (this.privParams.getStaticPrivateKey().getParameters().getCurve().getFieldSize() + 7) / 8;
    }
    
    public BigInteger calculateAgreement(final CipherParameters cipherParameters) {
        if (Properties.isOverrideSet("org.bouncycastle.ec.disable_mqv")) {
            throw new IllegalStateException("ECMQV explicitly disabled");
        }
        final MQVPublicParameters mqvPublicParameters = (MQVPublicParameters)cipherParameters;
        final ECPrivateKeyParameters staticPrivateKey = this.privParams.getStaticPrivateKey();
        final ECDomainParameters parameters = staticPrivateKey.getParameters();
        if (!parameters.equals(mqvPublicParameters.getStaticPublicKey().getParameters())) {
            throw new IllegalStateException("ECMQV public key components have wrong domain parameters");
        }
        final ECPoint normalize = this.calculateMqvAgreement(parameters, staticPrivateKey, this.privParams.getEphemeralPrivateKey(), this.privParams.getEphemeralPublicKey(), mqvPublicParameters.getStaticPublicKey(), mqvPublicParameters.getEphemeralPublicKey()).normalize();
        if (normalize.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for MQV");
        }
        return normalize.getAffineXCoord().toBigInteger();
    }
    
    private ECPoint calculateMqvAgreement(final ECDomainParameters ecDomainParameters, final ECPrivateKeyParameters ecPrivateKeyParameters, final ECPrivateKeyParameters ecPrivateKeyParameters2, final ECPublicKeyParameters ecPublicKeyParameters, final ECPublicKeyParameters ecPublicKeyParameters2, final ECPublicKeyParameters ecPublicKeyParameters3) {
        final BigInteger n = ecDomainParameters.getN();
        final int n2 = (n.bitLength() + 1) / 2;
        final BigInteger shiftLeft = ECConstants.ONE.shiftLeft(n2);
        final ECCurve curve = ecDomainParameters.getCurve();
        final ECPoint[] array = { ECAlgorithms.importPoint(curve, ecPublicKeyParameters.getQ()), ECAlgorithms.importPoint(curve, ecPublicKeyParameters2.getQ()), ECAlgorithms.importPoint(curve, ecPublicKeyParameters3.getQ()) };
        curve.normalizeAll(array);
        final ECPoint ecPoint = array[0];
        final ECPoint ecPoint2 = array[1];
        final ECPoint ecPoint3 = array[2];
        final BigInteger mod = ecPrivateKeyParameters.getD().multiply(ecPoint.getAffineXCoord().toBigInteger().mod(shiftLeft).setBit(n2)).add(ecPrivateKeyParameters2.getD()).mod(n);
        final BigInteger setBit = ecPoint3.getAffineXCoord().toBigInteger().mod(shiftLeft).setBit(n2);
        final BigInteger mod2 = ecDomainParameters.getH().multiply(mod).mod(n);
        return ECAlgorithms.sumOfTwoMultiplies(ecPoint2, setBit.multiply(mod2).mod(n), ecPoint3, mod2);
    }
}
