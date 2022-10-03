package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;

public class DSTU4145KeyPairGenerator extends ECKeyPairGenerator
{
    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        final AsymmetricCipherKeyPair generateKeyPair = super.generateKeyPair();
        final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)generateKeyPair.getPublic();
        return new AsymmetricCipherKeyPair(new ECPublicKeyParameters(ecPublicKeyParameters.getQ().negate(), ecPublicKeyParameters.getParameters()), generateKeyPair.getPrivate());
    }
}
