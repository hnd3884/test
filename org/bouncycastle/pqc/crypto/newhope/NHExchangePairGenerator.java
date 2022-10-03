package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.ExchangePairGenerator;

public class NHExchangePairGenerator implements ExchangePairGenerator
{
    private final SecureRandom random;
    
    public NHExchangePairGenerator(final SecureRandom random) {
        this.random = random;
    }
    
    public ExchangePair GenerateExchange(final AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.generateExchange(asymmetricKeyParameter);
    }
    
    public ExchangePair generateExchange(final AsymmetricKeyParameter asymmetricKeyParameter) {
        final NHPublicKeyParameters nhPublicKeyParameters = (NHPublicKeyParameters)asymmetricKeyParameter;
        final byte[] array = new byte[32];
        final byte[] array2 = new byte[2048];
        NewHope.sharedB(this.random, array, array2, nhPublicKeyParameters.pubData);
        return new ExchangePair(new NHPublicKeyParameters(array2), array);
    }
}
