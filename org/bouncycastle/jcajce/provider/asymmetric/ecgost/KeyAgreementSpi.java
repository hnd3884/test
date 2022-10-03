package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import java.security.PrivateKey;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidKeyException;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import java.security.PublicKey;
import java.security.Key;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.ECVKOAgreement;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;

public class KeyAgreementSpi extends BaseAgreementSpi
{
    private static final X9IntegerConverter converter;
    private String kaAlgorithm;
    private ECDomainParameters parameters;
    private ECVKOAgreement agreement;
    private byte[] result;
    
    protected KeyAgreementSpi(final String kaAlgorithm, final ECVKOAgreement agreement, final DerivationFunction derivationFunction) {
        super(kaAlgorithm, derivationFunction);
        this.kaAlgorithm = kaAlgorithm;
        this.agreement = agreement;
    }
    
    @Override
    protected Key engineDoPhase(final Key key, final boolean b) throws InvalidKeyException, IllegalStateException {
        if (this.parameters == null) {
            throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
        }
        if (!b) {
            throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPublicKey.class) + " for doPhase");
        }
        final AsymmetricKeyParameter generatePublicKeyParameter = generatePublicKeyParameter((PublicKey)key);
        try {
            this.result = this.agreement.calculateAgreement(generatePublicKeyParameter);
        }
        catch (final Exception ex) {
            throw new InvalidKeyException("calculation failed: " + ex.getMessage()) {
                @Override
                public Throwable getCause() {
                    return ex;
                }
            };
        }
        return null;
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof UserKeyingMaterialSpec)) {
            throw new InvalidAlgorithmParameterException("No algorithm parameters supported");
        }
        this.initFromKey(key, algorithmParameterSpec);
    }
    
    @Override
    protected void engineInit(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.initFromKey(key, null);
    }
    
    private void initFromKey(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException {
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPrivateKey.class) + " for initialisation");
        }
        final ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)key);
        this.parameters = ecPrivateKeyParameters.getParameters();
        this.ukmParameters = ((algorithmParameterSpec instanceof UserKeyingMaterialSpec) ? ((UserKeyingMaterialSpec)algorithmParameterSpec).getUserKeyingMaterial() : null);
        this.agreement.init(new ParametersWithUKM(ecPrivateKeyParameters, this.ukmParameters));
    }
    
    private static String getSimpleName(final Class clazz) {
        final String name = clazz.getName();
        return name.substring(name.lastIndexOf(46) + 1);
    }
    
    @Override
    protected byte[] calcSecret() {
        return this.result;
    }
    
    static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        return (publicKey instanceof BCECPublicKey) ? ((BCECGOST3410PublicKey)publicKey).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(publicKey);
    }
    
    static {
        converter = new X9IntegerConverter();
    }
    
    public static class ECVKO extends KeyAgreementSpi
    {
        public ECVKO() {
            super("ECGOST3410", new ECVKOAgreement(new GOST3411Digest()), null);
        }
    }
}
