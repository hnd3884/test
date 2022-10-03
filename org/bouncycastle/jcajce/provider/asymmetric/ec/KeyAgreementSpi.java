package org.bouncycastle.jcajce.provider.asymmetric.ec;

import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.crypto.params.ECDHUPrivateParameters;
import org.bouncycastle.crypto.params.MQVPrivateParameters;
import java.security.PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jce.interfaces.MQVPrivateKey;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidKeyException;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;
import org.bouncycastle.crypto.params.MQVPublicParameters;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.interfaces.MQVPublicKey;
import org.bouncycastle.crypto.agreement.ECMQVBasicAgreement;
import java.security.Key;
import java.math.BigInteger;
import org.bouncycastle.crypto.agreement.ECDHCUnifiedAgreement;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;

public class KeyAgreementSpi extends BaseAgreementSpi
{
    private static final X9IntegerConverter converter;
    private String kaAlgorithm;
    private ECDomainParameters parameters;
    private Object agreement;
    private MQVParameterSpec mqvParameters;
    private DHUParameterSpec dheParameters;
    private byte[] result;
    
    protected KeyAgreementSpi(final String kaAlgorithm, final BasicAgreement agreement, final DerivationFunction derivationFunction) {
        super(kaAlgorithm, derivationFunction);
        this.kaAlgorithm = kaAlgorithm;
        this.agreement = agreement;
    }
    
    protected KeyAgreementSpi(final String kaAlgorithm, final ECDHCUnifiedAgreement agreement, final DerivationFunction derivationFunction) {
        super(kaAlgorithm, derivationFunction);
        this.kaAlgorithm = kaAlgorithm;
        this.agreement = agreement;
    }
    
    protected byte[] bigIntToBytes(final BigInteger bigInteger) {
        return KeyAgreementSpi.converter.integerToBytes(bigInteger, KeyAgreementSpi.converter.getByteLength(this.parameters.getCurve()));
    }
    
    @Override
    protected Key engineDoPhase(final Key key, final boolean b) throws InvalidKeyException, IllegalStateException {
        if (this.parameters == null) {
            throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
        }
        if (!b) {
            throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
        }
        CipherParameters generatePublicKeyParameter;
        if (this.agreement instanceof ECMQVBasicAgreement) {
            if (!(key instanceof MQVPublicKey)) {
                generatePublicKeyParameter = new MQVPublicParameters((ECPublicKeyParameters)ECUtils.generatePublicKeyParameter((PublicKey)key), (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(this.mqvParameters.getOtherPartyEphemeralKey()));
            }
            else {
                final MQVPublicKey mqvPublicKey = (MQVPublicKey)key;
                generatePublicKeyParameter = new MQVPublicParameters((ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(mqvPublicKey.getStaticKey()), (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(mqvPublicKey.getEphemeralKey()));
            }
        }
        else if (this.agreement instanceof ECDHCUnifiedAgreement) {
            generatePublicKeyParameter = new ECDHUPublicParameters((ECPublicKeyParameters)ECUtils.generatePublicKeyParameter((PublicKey)key), (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(this.dheParameters.getOtherPartyEphemeralKey()));
        }
        else {
            if (!(key instanceof PublicKey)) {
                throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPublicKey.class) + " for doPhase");
            }
            generatePublicKeyParameter = ECUtils.generatePublicKeyParameter((PublicKey)key);
        }
        try {
            if (this.agreement instanceof BasicAgreement) {
                this.result = this.bigIntToBytes(((BasicAgreement)this.agreement).calculateAgreement(generatePublicKeyParameter));
            }
            else {
                this.result = ((ECDHCUnifiedAgreement)this.agreement).calculateAgreement(generatePublicKeyParameter);
            }
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
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof MQVParameterSpec) && !(algorithmParameterSpec instanceof UserKeyingMaterialSpec) && !(algorithmParameterSpec instanceof DHUParameterSpec)) {
            throw new InvalidAlgorithmParameterException("No algorithm parameters supported");
        }
        this.initFromKey(key, algorithmParameterSpec);
    }
    
    @Override
    protected void engineInit(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.initFromKey(key, null);
    }
    
    private void initFromKey(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException {
        if (this.agreement instanceof ECMQVBasicAgreement) {
            this.mqvParameters = null;
            if (!(key instanceof MQVPrivateKey) && !(algorithmParameterSpec instanceof MQVParameterSpec)) {
                throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(MQVParameterSpec.class) + " for initialisation");
            }
            ECPrivateKeyParameters ecPrivateKeyParameters;
            ECPrivateKeyParameters ecPrivateKeyParameters2;
            ECPublicKeyParameters ecPublicKeyParameters;
            if (key instanceof MQVPrivateKey) {
                final MQVPrivateKey mqvPrivateKey = (MQVPrivateKey)key;
                ecPrivateKeyParameters = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter(mqvPrivateKey.getStaticPrivateKey());
                ecPrivateKeyParameters2 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter(mqvPrivateKey.getEphemeralPrivateKey());
                ecPublicKeyParameters = null;
                if (mqvPrivateKey.getEphemeralPublicKey() != null) {
                    ecPublicKeyParameters = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(mqvPrivateKey.getEphemeralPublicKey());
                }
            }
            else {
                final MQVParameterSpec mqvParameters = (MQVParameterSpec)algorithmParameterSpec;
                ecPrivateKeyParameters = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)key);
                ecPrivateKeyParameters2 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter(mqvParameters.getEphemeralPrivateKey());
                ecPublicKeyParameters = null;
                if (mqvParameters.getEphemeralPublicKey() != null) {
                    ecPublicKeyParameters = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(mqvParameters.getEphemeralPublicKey());
                }
                this.mqvParameters = mqvParameters;
                this.ukmParameters = mqvParameters.getUserKeyingMaterial();
            }
            final MQVPrivateParameters mqvPrivateParameters = new MQVPrivateParameters(ecPrivateKeyParameters, ecPrivateKeyParameters2, ecPublicKeyParameters);
            this.parameters = ecPrivateKeyParameters.getParameters();
            ((ECMQVBasicAgreement)this.agreement).init(mqvPrivateParameters);
        }
        else if (algorithmParameterSpec instanceof DHUParameterSpec) {
            if (!(this.agreement instanceof ECDHCUnifiedAgreement)) {
                throw new InvalidKeyException(this.kaAlgorithm + " key agreement cannot be used with " + getSimpleName(DHUParameterSpec.class));
            }
            final DHUParameterSpec dheParameters = (DHUParameterSpec)algorithmParameterSpec;
            final ECPrivateKeyParameters ecPrivateKeyParameters3 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)key);
            final ECPrivateKeyParameters ecPrivateKeyParameters4 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter(dheParameters.getEphemeralPrivateKey());
            ECPublicKeyParameters ecPublicKeyParameters2 = null;
            if (dheParameters.getEphemeralPublicKey() != null) {
                ecPublicKeyParameters2 = (ECPublicKeyParameters)ECUtils.generatePublicKeyParameter(dheParameters.getEphemeralPublicKey());
            }
            this.dheParameters = dheParameters;
            this.ukmParameters = dheParameters.getUserKeyingMaterial();
            final ECDHUPrivateParameters ecdhuPrivateParameters = new ECDHUPrivateParameters(ecPrivateKeyParameters3, ecPrivateKeyParameters4, ecPublicKeyParameters2);
            this.parameters = ecPrivateKeyParameters3.getParameters();
            ((ECDHCUnifiedAgreement)this.agreement).init(ecdhuPrivateParameters);
        }
        else {
            if (!(key instanceof PrivateKey)) {
                throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPrivateKey.class) + " for initialisation");
            }
            final ECPrivateKeyParameters ecPrivateKeyParameters5 = (ECPrivateKeyParameters)ECUtil.generatePrivateKeyParameter((PrivateKey)key);
            this.parameters = ecPrivateKeyParameters5.getParameters();
            this.ukmParameters = ((algorithmParameterSpec instanceof UserKeyingMaterialSpec) ? ((UserKeyingMaterialSpec)algorithmParameterSpec).getUserKeyingMaterial() : null);
            ((BasicAgreement)this.agreement).init(ecPrivateKeyParameters5);
        }
    }
    
    private static String getSimpleName(final Class clazz) {
        final String name = clazz.getName();
        return name.substring(name.lastIndexOf(46) + 1);
    }
    
    @Override
    protected byte[] calcSecret() {
        return Arrays.clone(this.result);
    }
    
    static {
        converter = new X9IntegerConverter();
    }
    
    public static class CDHwithSHA1KDFAndSharedInfo extends KeyAgreementSpi
    {
        public CDHwithSHA1KDFAndSharedInfo() {
            super("ECCDHwithSHA1KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
        }
    }
    
    public static class CDHwithSHA224KDFAndSharedInfo extends KeyAgreementSpi
    {
        public CDHwithSHA224KDFAndSharedInfo() {
            super("ECCDHwithSHA224KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
        }
    }
    
    public static class CDHwithSHA256KDFAndSharedInfo extends KeyAgreementSpi
    {
        public CDHwithSHA256KDFAndSharedInfo() {
            super("ECCDHwithSHA256KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }
    
    public static class CDHwithSHA384KDFAndSharedInfo extends KeyAgreementSpi
    {
        public CDHwithSHA384KDFAndSharedInfo() {
            super("ECCDHwithSHA384KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
        }
    }
    
    public static class CDHwithSHA512KDFAndSharedInfo extends KeyAgreementSpi
    {
        public CDHwithSHA512KDFAndSharedInfo() {
            super("ECCDHwithSHA512KDF", new ECDHCBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }
    
    public static class DH extends KeyAgreementSpi
    {
        public DH() {
            super("ECDH", new ECDHBasicAgreement(), null);
        }
    }
    
    public static class DHC extends KeyAgreementSpi
    {
        public DHC() {
            super("ECDHC", new ECDHCBasicAgreement(), null);
        }
    }
    
    public static class DHUC extends KeyAgreementSpi
    {
        public DHUC() {
            super("ECCDHU", new ECDHCUnifiedAgreement(), null);
        }
    }
    
    public static class DHUwithSHA1CKDF extends KeyAgreementSpi
    {
        public DHUwithSHA1CKDF() {
            super("ECCDHUwithSHA1CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
        }
    }
    
    public static class DHUwithSHA224CKDF extends KeyAgreementSpi
    {
        public DHUwithSHA224CKDF() {
            super("ECCDHUwithSHA224CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA224()));
        }
    }
    
    public static class DHUwithSHA256CKDF extends KeyAgreementSpi
    {
        public DHUwithSHA256CKDF() {
            super("ECCDHUwithSHA256CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }
    
    public static class DHUwithSHA384CKDF extends KeyAgreementSpi
    {
        public DHUwithSHA384CKDF() {
            super("ECCDHUwithSHA384CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }
    
    public static class DHUwithSHA512CKDF extends KeyAgreementSpi
    {
        public DHUwithSHA512CKDF() {
            super("ECCDHUwithSHA512CKDF", new ECDHCUnifiedAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }
    
    public static class DHwithSHA1CKDF extends KeyAgreementSpi
    {
        public DHwithSHA1CKDF() {
            super("ECDHwithSHA1CKDF", new ECDHCBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
        }
    }
    
    public static class DHwithSHA1KDF extends KeyAgreementSpi
    {
        public DHwithSHA1KDF() {
            super("ECDHwithSHA1KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
        }
    }
    
    public static class DHwithSHA1KDFAndSharedInfo extends KeyAgreementSpi
    {
        public DHwithSHA1KDFAndSharedInfo() {
            super("ECDHwithSHA1KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
        }
    }
    
    public static class DHwithSHA224KDFAndSharedInfo extends KeyAgreementSpi
    {
        public DHwithSHA224KDFAndSharedInfo() {
            super("ECDHwithSHA224KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
        }
    }
    
    public static class DHwithSHA256CKDF extends KeyAgreementSpi
    {
        public DHwithSHA256CKDF() {
            super("ECDHwithSHA256CKDF", new ECDHCBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }
    
    public static class DHwithSHA256KDFAndSharedInfo extends KeyAgreementSpi
    {
        public DHwithSHA256KDFAndSharedInfo() {
            super("ECDHwithSHA256KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }
    
    public static class DHwithSHA384CKDF extends KeyAgreementSpi
    {
        public DHwithSHA384CKDF() {
            super("ECDHwithSHA384CKDF", new ECDHCBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }
    
    public static class DHwithSHA384KDFAndSharedInfo extends KeyAgreementSpi
    {
        public DHwithSHA384KDFAndSharedInfo() {
            super("ECDHwithSHA384KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
        }
    }
    
    public static class DHwithSHA512CKDF extends KeyAgreementSpi
    {
        public DHwithSHA512CKDF() {
            super("ECDHwithSHA512CKDF", new ECDHCBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }
    
    public static class DHwithSHA512KDFAndSharedInfo extends KeyAgreementSpi
    {
        public DHwithSHA512KDFAndSharedInfo() {
            super("ECDHwithSHA512KDF", new ECDHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }
    
    public static class MQV extends KeyAgreementSpi
    {
        public MQV() {
            super("ECMQV", new ECMQVBasicAgreement(), null);
        }
    }
    
    public static class MQVwithSHA1CKDF extends KeyAgreementSpi
    {
        public MQVwithSHA1CKDF() {
            super("ECMQVwithSHA1CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA1()));
        }
    }
    
    public static class MQVwithSHA1KDFAndSharedInfo extends KeyAgreementSpi
    {
        public MQVwithSHA1KDFAndSharedInfo() {
            super("ECMQVwithSHA1KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()));
        }
    }
    
    public static class MQVwithSHA224CKDF extends KeyAgreementSpi
    {
        public MQVwithSHA224CKDF() {
            super("ECMQVwithSHA224CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA224()));
        }
    }
    
    public static class MQVwithSHA224KDFAndSharedInfo extends KeyAgreementSpi
    {
        public MQVwithSHA224KDFAndSharedInfo() {
            super("ECMQVwithSHA224KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA224()));
        }
    }
    
    public static class MQVwithSHA256CKDF extends KeyAgreementSpi
    {
        public MQVwithSHA256CKDF() {
            super("ECMQVwithSHA256CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }
    
    public static class MQVwithSHA256KDFAndSharedInfo extends KeyAgreementSpi
    {
        public MQVwithSHA256KDFAndSharedInfo() {
            super("ECMQVwithSHA256KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }
    
    public static class MQVwithSHA384CKDF extends KeyAgreementSpi
    {
        public MQVwithSHA384CKDF() {
            super("ECMQVwithSHA384CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }
    
    public static class MQVwithSHA384KDFAndSharedInfo extends KeyAgreementSpi
    {
        public MQVwithSHA384KDFAndSharedInfo() {
            super("ECMQVwithSHA384KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA384()));
        }
    }
    
    public static class MQVwithSHA512CKDF extends KeyAgreementSpi
    {
        public MQVwithSHA512CKDF() {
            super("ECDHUwithSHA512CKDF", new ECMQVBasicAgreement(), new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }
    
    public static class MQVwithSHA512KDFAndSharedInfo extends KeyAgreementSpi
    {
        public MQVwithSHA512KDFAndSharedInfo() {
            super("ECMQVwithSHA512KDF", new ECMQVBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }
}
