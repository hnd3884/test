package org.bouncycastle.operator.jcajce;

import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.util.HashMap;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.ASN1Sequence;
import java.security.Signature;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import java.security.MessageDigest;
import java.io.IOException;
import java.security.NoSuchProviderException;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.GeneralSecurityException;
import org.bouncycastle.operator.OperatorCreationException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.util.Map;

class OperatorHelper
{
    private static final Map oids;
    private static final Map asymmetricWrapperAlgNames;
    private static final Map symmetricWrapperAlgNames;
    private static final Map symmetricKeyAlgNames;
    private static final Map symmetricWrapperKeySizes;
    private JcaJceHelper helper;
    
    OperatorHelper(final JcaJceHelper helper) {
        this.helper = helper;
    }
    
    String getWrappingAlgorithmName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return OperatorHelper.symmetricWrapperAlgNames.get(asn1ObjectIdentifier);
    }
    
    int getKeySizeInBits(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return OperatorHelper.symmetricWrapperKeySizes.get(asn1ObjectIdentifier);
    }
    
    Cipher createAsymmetricWrapper(final ASN1ObjectIdentifier asn1ObjectIdentifier, final Map map) throws OperatorCreationException {
        try {
            String s = null;
            if (!map.isEmpty()) {
                s = map.get(asn1ObjectIdentifier);
            }
            if (s == null) {
                s = OperatorHelper.asymmetricWrapperAlgNames.get(asn1ObjectIdentifier);
            }
            if (s != null) {
                try {
                    return this.helper.createCipher(s);
                }
                catch (final NoSuchAlgorithmException ex) {
                    if (s.equals("RSA/ECB/PKCS1Padding")) {
                        try {
                            return this.helper.createCipher("RSA/NONE/PKCS1Padding");
                        }
                        catch (final NoSuchAlgorithmException ex2) {}
                    }
                }
            }
            return this.helper.createCipher(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex3) {
            throw new OperatorCreationException("cannot create cipher: " + ex3.getMessage(), ex3);
        }
    }
    
    Cipher createSymmetricWrapper(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws OperatorCreationException {
        try {
            final String s = OperatorHelper.symmetricWrapperAlgNames.get(asn1ObjectIdentifier);
            if (s != null) {
                try {
                    return this.helper.createCipher(s);
                }
                catch (final NoSuchAlgorithmException ex) {}
            }
            return this.helper.createCipher(asn1ObjectIdentifier.getId());
        }
        catch (final GeneralSecurityException ex2) {
            throw new OperatorCreationException("cannot create cipher: " + ex2.getMessage(), ex2);
        }
    }
    
    AlgorithmParameters createAlgorithmParameters(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        if (algorithmIdentifier.getAlgorithm().equals((Object)PKCSObjectIdentifiers.rsaEncryption)) {
            return null;
        }
        AlgorithmParameters algorithmParameters;
        try {
            algorithmParameters = this.helper.createAlgorithmParameters(algorithmIdentifier.getAlgorithm().getId());
        }
        catch (final NoSuchAlgorithmException ex) {
            return null;
        }
        catch (final NoSuchProviderException ex2) {
            throw new OperatorCreationException("cannot create algorithm parameters: " + ex2.getMessage(), ex2);
        }
        try {
            algorithmParameters.init(algorithmIdentifier.getParameters().toASN1Primitive().getEncoded());
        }
        catch (final IOException ex3) {
            throw new OperatorCreationException("cannot initialise algorithm parameters: " + ex3.getMessage(), ex3);
        }
        return algorithmParameters;
    }
    
    MessageDigest createDigest(final AlgorithmIdentifier algorithmIdentifier) throws GeneralSecurityException {
        MessageDigest messageDigest;
        try {
            messageDigest = this.helper.createDigest(MessageDigestUtils.getDigestName(algorithmIdentifier.getAlgorithm()));
        }
        catch (final NoSuchAlgorithmException ex) {
            if (OperatorHelper.oids.get(algorithmIdentifier.getAlgorithm()) == null) {
                throw ex;
            }
            messageDigest = this.helper.createDigest((String)OperatorHelper.oids.get(algorithmIdentifier.getAlgorithm()));
        }
        return messageDigest;
    }
    
    Signature createSignature(final AlgorithmIdentifier algorithmIdentifier) throws GeneralSecurityException {
        Signature signature;
        try {
            signature = this.helper.createSignature(getSignatureName(algorithmIdentifier));
        }
        catch (final NoSuchAlgorithmException ex) {
            if (OperatorHelper.oids.get(algorithmIdentifier.getAlgorithm()) == null) {
                throw ex;
            }
            signature = this.helper.createSignature((String)OperatorHelper.oids.get(algorithmIdentifier.getAlgorithm()));
        }
        if (algorithmIdentifier.getAlgorithm().equals((Object)PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            final ASN1Sequence instance = ASN1Sequence.getInstance((Object)algorithmIdentifier.getParameters());
            if (this.notDefaultPSSParams(instance)) {
                try {
                    final AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters("PSS");
                    algorithmParameters.init(instance.getEncoded());
                    signature.setParameter(algorithmParameters.getParameterSpec(PSSParameterSpec.class));
                }
                catch (final IOException ex2) {
                    throw new GeneralSecurityException("unable to process PSS parameters: " + ex2.getMessage());
                }
            }
        }
        return signature;
    }
    
    public Signature createRawSignature(final AlgorithmIdentifier algorithmIdentifier) {
        Signature signature;
        try {
            final String signatureName = getSignatureName(algorithmIdentifier);
            final String string = "NONE" + signatureName.substring(signatureName.indexOf("WITH"));
            signature = this.helper.createSignature(string);
            if (algorithmIdentifier.getAlgorithm().equals((Object)PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                final AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(string);
                AlgorithmParametersUtils.loadParameters(algorithmParameters, algorithmIdentifier.getParameters());
                signature.setParameter(algorithmParameters.getParameterSpec(PSSParameterSpec.class));
            }
        }
        catch (final Exception ex) {
            return null;
        }
        return signature;
    }
    
    private static String getSignatureName(final AlgorithmIdentifier algorithmIdentifier) {
        final ASN1Encodable parameters = algorithmIdentifier.getParameters();
        if (parameters != null && !DERNull.INSTANCE.equals((Object)parameters) && algorithmIdentifier.getAlgorithm().equals((Object)PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            return getDigestName(RSASSAPSSparams.getInstance((Object)parameters).getHashAlgorithm().getAlgorithm()) + "WITHRSAANDMGF1";
        }
        if (OperatorHelper.oids.containsKey(algorithmIdentifier.getAlgorithm())) {
            return OperatorHelper.oids.get(algorithmIdentifier.getAlgorithm());
        }
        return algorithmIdentifier.getAlgorithm().getId();
    }
    
    private static String getDigestName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String digestName = MessageDigestUtils.getDigestName(asn1ObjectIdentifier);
        final int index = digestName.indexOf(45);
        if (index > 0 && !digestName.startsWith("SHA3")) {
            return digestName.substring(0, index) + digestName.substring(index + 1);
        }
        return MessageDigestUtils.getDigestName(asn1ObjectIdentifier);
    }
    
    public X509Certificate convertCertificate(final X509CertificateHolder x509CertificateHolder) throws CertificateException {
        try {
            return (X509Certificate)this.helper.createCertificateFactory("X.509").generateCertificate(new ByteArrayInputStream(x509CertificateHolder.getEncoded()));
        }
        catch (final IOException ex) {
            throw new OpCertificateException("cannot get encoded form of certificate: " + ex.getMessage(), ex);
        }
        catch (final NoSuchProviderException ex2) {
            throw new OpCertificateException("cannot find factory provider: " + ex2.getMessage(), ex2);
        }
    }
    
    public PublicKey convertPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws OperatorCreationException {
        try {
            return this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        }
        catch (final IOException ex) {
            throw new OperatorCreationException("cannot get encoded form of key: " + ex.getMessage(), ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new OperatorCreationException("cannot create key factory: " + ex2.getMessage(), ex2);
        }
        catch (final NoSuchProviderException ex3) {
            throw new OperatorCreationException("cannot find factory provider: " + ex3.getMessage(), ex3);
        }
        catch (final InvalidKeySpecException ex4) {
            throw new OperatorCreationException("cannot create key factory: " + ex4.getMessage(), ex4);
        }
    }
    
    String getKeyAlgorithmName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String s = OperatorHelper.symmetricKeyAlgNames.get(asn1ObjectIdentifier);
        if (s != null) {
            return s;
        }
        return asn1ObjectIdentifier.getId();
    }
    
    private boolean notDefaultPSSParams(final ASN1Sequence asn1Sequence) throws GeneralSecurityException {
        if (asn1Sequence == null || asn1Sequence.size() == 0) {
            return false;
        }
        final RSASSAPSSparams instance = RSASSAPSSparams.getInstance((Object)asn1Sequence);
        return !instance.getMaskGenAlgorithm().getAlgorithm().equals((Object)PKCSObjectIdentifiers.id_mgf1) || !instance.getHashAlgorithm().equals((Object)AlgorithmIdentifier.getInstance((Object)instance.getMaskGenAlgorithm().getParameters())) || instance.getSaltLength().intValue() != this.createDigest(instance.getHashAlgorithm()).getDigestLength();
    }
    
    static {
        oids = new HashMap();
        asymmetricWrapperAlgNames = new HashMap();
        symmetricWrapperAlgNames = new HashMap();
        symmetricKeyAlgNames = new HashMap();
        symmetricWrapperKeySizes = new HashMap();
        OperatorHelper.oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
        OperatorHelper.oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
        OperatorHelper.oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
        OperatorHelper.oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
        OperatorHelper.oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
        OperatorHelper.oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
        OperatorHelper.oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
        OperatorHelper.oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411-2012-256WITHECGOST3410-2012-256");
        OperatorHelper.oids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411-2012-512WITHECGOST3410-2012-512");
        OperatorHelper.oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1WITHPLAIN-ECDSA");
        OperatorHelper.oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224WITHPLAIN-ECDSA");
        OperatorHelper.oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256WITHPLAIN-ECDSA");
        OperatorHelper.oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384WITHPLAIN-ECDSA");
        OperatorHelper.oids.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512WITHPLAIN-ECDSA");
        OperatorHelper.oids.put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160WITHPLAIN-ECDSA");
        OperatorHelper.oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1WITHCVC-ECDSA");
        OperatorHelper.oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224WITHCVC-ECDSA");
        OperatorHelper.oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256WITHCVC-ECDSA");
        OperatorHelper.oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384WITHCVC-ECDSA");
        OperatorHelper.oids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512WITHCVC-ECDSA");
        OperatorHelper.oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
        OperatorHelper.oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
        OperatorHelper.oids.put(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), "SHA1WITHDSA");
        OperatorHelper.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
        OperatorHelper.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
        OperatorHelper.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
        OperatorHelper.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
        OperatorHelper.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
        OperatorHelper.oids.put(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
        OperatorHelper.oids.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1WITHDSA");
        OperatorHelper.oids.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
        OperatorHelper.oids.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
        OperatorHelper.oids.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        OperatorHelper.oids.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        OperatorHelper.oids.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        OperatorHelper.oids.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        OperatorHelper.oids.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        OperatorHelper.oids.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        OperatorHelper.oids.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        OperatorHelper.oids.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        OperatorHelper.asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        OperatorHelper.asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        OperatorHelper.symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, "DESEDEWrap");
        OperatorHelper.symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap, "RC2Wrap");
        OperatorHelper.symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes128_wrap, "AESWrap");
        OperatorHelper.symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes192_wrap, "AESWrap");
        OperatorHelper.symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes256_wrap, "AESWrap");
        OperatorHelper.symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia128_wrap, "CamelliaWrap");
        OperatorHelper.symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia192_wrap, "CamelliaWrap");
        OperatorHelper.symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia256_wrap, "CamelliaWrap");
        OperatorHelper.symmetricWrapperAlgNames.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, "SEEDWrap");
        OperatorHelper.symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
        OperatorHelper.symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
        OperatorHelper.symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
        OperatorHelper.symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
        OperatorHelper.symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
        OperatorHelper.symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
        OperatorHelper.symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
        OperatorHelper.symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
        OperatorHelper.symmetricWrapperKeySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, Integers.valueOf(128));
        OperatorHelper.symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
        OperatorHelper.symmetricKeyAlgNames.put(NISTObjectIdentifiers.aes, "AES");
        OperatorHelper.symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
        OperatorHelper.symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
        OperatorHelper.symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
        OperatorHelper.symmetricKeyAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
        OperatorHelper.symmetricKeyAlgNames.put(PKCSObjectIdentifiers.RC2_CBC, "RC2");
    }
    
    private static class OpCertificateException extends CertificateException
    {
        private Throwable cause;
        
        public OpCertificateException(final String s, final Throwable cause) {
            super(s);
            this.cause = cause;
        }
        
        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}
