package org.bouncycastle.jcajce.provider.asymmetric.ec;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import java.math.BigInteger;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import java.security.KeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import org.bouncycastle.util.Integers;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.util.Hashtable;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import java.security.KeyPairGenerator;

public abstract class KeyPairGeneratorSpi extends KeyPairGenerator
{
    public KeyPairGeneratorSpi(final String s) {
        super(s);
    }
    
    public static class EC extends KeyPairGeneratorSpi
    {
        ECKeyGenerationParameters param;
        ECKeyPairGenerator engine;
        Object ecParams;
        int strength;
        int certainty;
        SecureRandom random;
        boolean initialised;
        String algorithm;
        ProviderConfiguration configuration;
        private static Hashtable ecParameters;
        
        public EC() {
            super("EC");
            this.engine = new ECKeyPairGenerator();
            this.ecParams = null;
            this.strength = 239;
            this.certainty = 50;
            this.random = new SecureRandom();
            this.initialised = false;
            this.algorithm = "EC";
            this.configuration = BouncyCastleProvider.CONFIGURATION;
        }
        
        public EC(final String algorithm, final ProviderConfiguration configuration) {
            super(algorithm);
            this.engine = new ECKeyPairGenerator();
            this.ecParams = null;
            this.strength = 239;
            this.certainty = 50;
            this.random = new SecureRandom();
            this.initialised = false;
            this.algorithm = algorithm;
            this.configuration = configuration;
        }
        
        @Override
        public void initialize(final int strength, final SecureRandom random) {
            this.strength = strength;
            this.random = random;
            final ECGenParameterSpec ecGenParameterSpec = EC.ecParameters.get(Integers.valueOf(strength));
            if (ecGenParameterSpec == null) {
                throw new InvalidParameterException("unknown key size.");
            }
            try {
                this.initialize(ecGenParameterSpec, random);
            }
            catch (final InvalidAlgorithmParameterException ex) {
                throw new InvalidParameterException("key size not configurable.");
            }
        }
        
        @Override
        public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            if (algorithmParameterSpec == null) {
                final ECParameterSpec ecImplicitlyCa = this.configuration.getEcImplicitlyCa();
                if (ecImplicitlyCa == null) {
                    throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
                }
                this.ecParams = null;
                this.param = this.createKeyGenParamsBC(ecImplicitlyCa, secureRandom);
            }
            else if (algorithmParameterSpec instanceof ECParameterSpec) {
                this.ecParams = algorithmParameterSpec;
                this.param = this.createKeyGenParamsBC((ECParameterSpec)algorithmParameterSpec, secureRandom);
            }
            else if (algorithmParameterSpec instanceof java.security.spec.ECParameterSpec) {
                this.ecParams = algorithmParameterSpec;
                this.param = this.createKeyGenParamsJCE((java.security.spec.ECParameterSpec)algorithmParameterSpec, secureRandom);
            }
            else if (algorithmParameterSpec instanceof ECGenParameterSpec) {
                this.initializeNamedCurve(((ECGenParameterSpec)algorithmParameterSpec).getName(), secureRandom);
            }
            else {
                if (!(algorithmParameterSpec instanceof ECNamedCurveGenParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec");
                }
                this.initializeNamedCurve(((ECNamedCurveGenParameterSpec)algorithmParameterSpec).getName(), secureRandom);
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        
        @Override
        public KeyPair generateKeyPair() {
            if (!this.initialised) {
                this.initialize(this.strength, new SecureRandom());
            }
            final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
            final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)generateKeyPair.getPublic();
            final ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters)generateKeyPair.getPrivate();
            if (this.ecParams instanceof ECParameterSpec) {
                final ECParameterSpec ecParameterSpec = (ECParameterSpec)this.ecParams;
                final BCECPublicKey bcecPublicKey = new BCECPublicKey(this.algorithm, ecPublicKeyParameters, ecParameterSpec, this.configuration);
                return new KeyPair(bcecPublicKey, new BCECPrivateKey(this.algorithm, ecPrivateKeyParameters, bcecPublicKey, ecParameterSpec, this.configuration));
            }
            if (this.ecParams == null) {
                return new KeyPair(new BCECPublicKey(this.algorithm, ecPublicKeyParameters, this.configuration), new BCECPrivateKey(this.algorithm, ecPrivateKeyParameters, this.configuration));
            }
            final java.security.spec.ECParameterSpec ecParameterSpec2 = (java.security.spec.ECParameterSpec)this.ecParams;
            final BCECPublicKey bcecPublicKey2 = new BCECPublicKey(this.algorithm, ecPublicKeyParameters, ecParameterSpec2, this.configuration);
            return new KeyPair(bcecPublicKey2, new BCECPrivateKey(this.algorithm, ecPrivateKeyParameters, bcecPublicKey2, ecParameterSpec2, this.configuration));
        }
        
        protected ECKeyGenerationParameters createKeyGenParamsBC(final ECParameterSpec ecParameterSpec, final SecureRandom secureRandom) {
            return new ECKeyGenerationParameters(new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN(), ecParameterSpec.getH()), secureRandom);
        }
        
        protected ECKeyGenerationParameters createKeyGenParamsJCE(final java.security.spec.ECParameterSpec ecParameterSpec, final SecureRandom secureRandom) {
            final ECCurve convertCurve = EC5Util.convertCurve(ecParameterSpec.getCurve());
            return new ECKeyGenerationParameters(new ECDomainParameters(convertCurve, EC5Util.convertPoint(convertCurve, ecParameterSpec.getGenerator(), false), ecParameterSpec.getOrder(), BigInteger.valueOf(ecParameterSpec.getCofactor())), secureRandom);
        }
        
        protected ECNamedCurveSpec createNamedCurveSpec(final String s) throws InvalidAlgorithmParameterException {
            X9ECParameters x9ECParameters = ECUtils.getDomainParametersFromName(s);
            if (x9ECParameters == null) {
                try {
                    x9ECParameters = ECNamedCurveTable.getByOID(new ASN1ObjectIdentifier(s));
                    if (x9ECParameters == null) {
                        x9ECParameters = this.configuration.getAdditionalECParameters().get(new ASN1ObjectIdentifier(s));
                        if (x9ECParameters == null) {
                            throw new InvalidAlgorithmParameterException("unknown curve OID: " + s);
                        }
                    }
                }
                catch (final IllegalArgumentException ex) {
                    throw new InvalidAlgorithmParameterException("unknown curve name: " + s);
                }
            }
            return new ECNamedCurveSpec(s, x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), null);
        }
        
        protected void initializeNamedCurve(final String s, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            final ECNamedCurveSpec namedCurveSpec = this.createNamedCurveSpec(s);
            this.ecParams = namedCurveSpec;
            this.param = this.createKeyGenParamsJCE(namedCurveSpec, secureRandom);
        }
        
        static {
            (EC.ecParameters = new Hashtable()).put(Integers.valueOf(192), new ECGenParameterSpec("prime192v1"));
            EC.ecParameters.put(Integers.valueOf(239), new ECGenParameterSpec("prime239v1"));
            EC.ecParameters.put(Integers.valueOf(256), new ECGenParameterSpec("prime256v1"));
            EC.ecParameters.put(Integers.valueOf(224), new ECGenParameterSpec("P-224"));
            EC.ecParameters.put(Integers.valueOf(384), new ECGenParameterSpec("P-384"));
            EC.ecParameters.put(Integers.valueOf(521), new ECGenParameterSpec("P-521"));
        }
    }
    
    public static class ECDH extends EC
    {
        public ECDH() {
            super("ECDH", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECDHC extends EC
    {
        public ECDHC() {
            super("ECDHC", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECDSA extends EC
    {
        public ECDSA() {
            super("ECDSA", BouncyCastleProvider.CONFIGURATION);
        }
    }
    
    public static class ECMQV extends EC
    {
        public ECMQV() {
            super("ECMQV", BouncyCastleProvider.CONFIGURATION);
        }
    }
}
