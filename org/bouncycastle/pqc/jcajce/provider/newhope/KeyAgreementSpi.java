package org.bouncycastle.pqc.jcajce.provider.newhope;

import javax.crypto.ShortBufferException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.pqc.crypto.newhope.NHExchangePairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHAgreement;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;

public class KeyAgreementSpi extends BaseAgreementSpi
{
    private NHAgreement agreement;
    private BCNHPublicKey otherPartyKey;
    private NHExchangePairGenerator exchangePairGenerator;
    private byte[] shared;
    
    public KeyAgreementSpi() {
        super("NH", null);
    }
    
    @Override
    protected void engineInit(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        if (key != null) {
            (this.agreement = new NHAgreement()).init(((BCNHPrivateKey)key).getKeyParams());
        }
        else {
            this.exchangePairGenerator = new NHExchangePairGenerator(secureRandom);
        }
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("NewHope does not require parameters");
    }
    
    @Override
    protected Key engineDoPhase(final Key key, final boolean b) throws InvalidKeyException, IllegalStateException {
        if (!b) {
            throw new IllegalStateException("NewHope can only be between two parties.");
        }
        this.otherPartyKey = (BCNHPublicKey)key;
        if (this.exchangePairGenerator != null) {
            final ExchangePair generateExchange = this.exchangePairGenerator.generateExchange((AsymmetricKeyParameter)this.otherPartyKey.getKeyParams());
            this.shared = generateExchange.getSharedValue();
            return new BCNHPublicKey((NHPublicKeyParameters)generateExchange.getPublicKey());
        }
        this.shared = this.agreement.calculateAgreement(this.otherPartyKey.getKeyParams());
        return null;
    }
    
    @Override
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        final byte[] clone = Arrays.clone(this.shared);
        Arrays.fill(this.shared, (byte)0);
        return clone;
    }
    
    @Override
    protected int engineGenerateSecret(final byte[] array, final int n) throws IllegalStateException, ShortBufferException {
        System.arraycopy(this.shared, 0, array, n, this.shared.length);
        Arrays.fill(this.shared, (byte)0);
        return this.shared.length;
    }
    
    @Override
    protected byte[] calcSecret() {
        return this.engineGenerateSecret();
    }
}
