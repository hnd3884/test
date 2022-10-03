package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import java.security.KeyPair;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import java.math.BigInteger;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.spec.ECParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import java.security.KeyPairGenerator;

public class KeyPairGeneratorSpi extends KeyPairGenerator
{
    Object ecParams;
    ECKeyPairGenerator engine;
    String algorithm;
    ECKeyGenerationParameters param;
    int strength;
    SecureRandom random;
    boolean initialised;
    
    public KeyPairGeneratorSpi() {
        super("ECGOST3410");
        this.ecParams = null;
        this.engine = new ECKeyPairGenerator();
        this.algorithm = "ECGOST3410";
        this.strength = 239;
        this.random = null;
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
        if (this.ecParams != null) {
            try {
                this.initialize((AlgorithmParameterSpec)this.ecParams, random);
                return;
            }
            catch (final InvalidAlgorithmParameterException ex) {
                throw new InvalidParameterException("key size not configurable.");
            }
            throw new InvalidParameterException("unknown key size.");
        }
        throw new InvalidParameterException("unknown key size.");
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec ecParams, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (ecParams instanceof ECParameterSpec) {
            final ECParameterSpec ecParameterSpec = (ECParameterSpec)ecParams;
            this.ecParams = ecParams;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN(), ecParameterSpec.getH()), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
        }
        else if (ecParams instanceof java.security.spec.ECParameterSpec) {
            final java.security.spec.ECParameterSpec ecParameterSpec2 = (java.security.spec.ECParameterSpec)ecParams;
            this.ecParams = ecParams;
            final ECCurve convertCurve = EC5Util.convertCurve(ecParameterSpec2.getCurve());
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(convertCurve, EC5Util.convertPoint(convertCurve, ecParameterSpec2.getGenerator(), false), ecParameterSpec2.getOrder(), BigInteger.valueOf(ecParameterSpec2.getCofactor())), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
        }
        else if (ecParams instanceof ECGenParameterSpec || ecParams instanceof ECNamedCurveGenParameterSpec) {
            String s;
            if (ecParams instanceof ECGenParameterSpec) {
                s = ((ECGenParameterSpec)ecParams).getName();
            }
            else {
                s = ((ECNamedCurveGenParameterSpec)ecParams).getName();
            }
            final ECDomainParameters byName = ECGOST3410NamedCurves.getByName(s);
            if (byName == null) {
                throw new InvalidAlgorithmParameterException("unknown curve name: " + s);
            }
            this.ecParams = new ECNamedCurveSpec(s, byName.getCurve(), byName.getG(), byName.getN(), byName.getH(), byName.getSeed());
            final java.security.spec.ECParameterSpec ecParameterSpec3 = (java.security.spec.ECParameterSpec)this.ecParams;
            final ECCurve convertCurve2 = EC5Util.convertCurve(ecParameterSpec3.getCurve());
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(convertCurve2, EC5Util.convertPoint(convertCurve2, ecParameterSpec3.getGenerator(), false), ecParameterSpec3.getOrder(), BigInteger.valueOf(ecParameterSpec3.getCofactor())), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
        }
        else if (ecParams == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() != null) {
            final ECParameterSpec ecImplicitlyCa = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            this.ecParams = ecParams;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(ecImplicitlyCa.getCurve(), ecImplicitlyCa.getG(), ecImplicitlyCa.getN(), ecImplicitlyCa.getH()), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
        }
        else {
            if (ecParams == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
                throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
            }
            throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + ecParams.getClass().getName());
        }
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            throw new IllegalStateException("EC Key Pair Generator not initialised");
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)generateKeyPair.getPublic();
        final ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters)generateKeyPair.getPrivate();
        if (this.ecParams instanceof ECParameterSpec) {
            final ECParameterSpec ecParameterSpec = (ECParameterSpec)this.ecParams;
            final BCECGOST3410PublicKey bcecgost3410PublicKey = new BCECGOST3410PublicKey(this.algorithm, ecPublicKeyParameters, ecParameterSpec);
            return new KeyPair(bcecgost3410PublicKey, new BCECGOST3410PrivateKey(this.algorithm, ecPrivateKeyParameters, bcecgost3410PublicKey, ecParameterSpec));
        }
        if (this.ecParams == null) {
            return new KeyPair(new BCECGOST3410PublicKey(this.algorithm, ecPublicKeyParameters), new BCECGOST3410PrivateKey(this.algorithm, ecPrivateKeyParameters));
        }
        final java.security.spec.ECParameterSpec ecParameterSpec2 = (java.security.spec.ECParameterSpec)this.ecParams;
        final BCECGOST3410PublicKey bcecgost3410PublicKey2 = new BCECGOST3410PublicKey(this.algorithm, ecPublicKeyParameters, ecParameterSpec2);
        return new KeyPair(bcecgost3410PublicKey2, new BCECGOST3410PrivateKey(this.algorithm, ecPrivateKeyParameters, bcecgost3410PublicKey2, ecParameterSpec2));
    }
}
