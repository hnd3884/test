package org.bouncycastle.cms.jcajce;

import java.security.KeyPairGenerator;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyAgreement;
import org.bouncycastle.asn1.DERSequence;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import java.security.cert.X509Certificate;
import java.security.Provider;
import org.bouncycastle.util.Arrays;
import java.util.ArrayList;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.cms.KeyAgreeRecipientInfoGenerator;

public class JceKeyAgreeRecipientInfoGenerator extends KeyAgreeRecipientInfoGenerator
{
    private SecretKeySizeProvider keySizeProvider;
    private List recipientIDs;
    private List recipientKeys;
    private PublicKey senderPublicKey;
    private PrivateKey senderPrivateKey;
    private EnvelopedDataHelper helper;
    private SecureRandom random;
    private KeyPair ephemeralKP;
    private byte[] userKeyingMaterial;
    private static KeyMaterialGenerator ecc_cms_Generator;
    
    public JceKeyAgreeRecipientInfoGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier, final PrivateKey senderPrivateKey, final PublicKey senderPublicKey, final ASN1ObjectIdentifier asn1ObjectIdentifier2) {
        super(asn1ObjectIdentifier, SubjectPublicKeyInfo.getInstance((Object)senderPublicKey.getEncoded()), asn1ObjectIdentifier2);
        this.keySizeProvider = new DefaultSecretKeySizeProvider();
        this.recipientIDs = new ArrayList();
        this.recipientKeys = new ArrayList();
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.senderPublicKey = senderPublicKey;
        this.senderPrivateKey = senderPrivateKey;
    }
    
    public JceKeyAgreeRecipientInfoGenerator setUserKeyingMaterial(final byte[] array) {
        this.userKeyingMaterial = Arrays.clone(array);
        return this;
    }
    
    public JceKeyAgreeRecipientInfoGenerator setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }
    
    public JceKeyAgreeRecipientInfoGenerator setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        return this;
    }
    
    public JceKeyAgreeRecipientInfoGenerator setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public JceKeyAgreeRecipientInfoGenerator addRecipient(final X509Certificate x509Certificate) throws CertificateEncodingException {
        this.recipientIDs.add(new KeyAgreeRecipientIdentifier(CMSUtils.getIssuerAndSerialNumber(x509Certificate)));
        this.recipientKeys.add(x509Certificate.getPublicKey());
        return this;
    }
    
    public JceKeyAgreeRecipientInfoGenerator addRecipient(final byte[] array, final PublicKey publicKey) throws CertificateEncodingException {
        this.recipientIDs.add(new KeyAgreeRecipientIdentifier(new RecipientKeyIdentifier(array)));
        this.recipientKeys.add(publicKey);
        return this;
    }
    
    public ASN1Sequence generateRecipientEncryptedKeys(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final GenericKey genericKey) throws CMSException {
        if (this.recipientIDs.isEmpty()) {
            throw new CMSException("No recipients associated with generator - use addRecipient()");
        }
        this.init(algorithmIdentifier.getAlgorithm());
        final PrivateKey senderPrivateKey = this.senderPrivateKey;
        final ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != this.recipientIDs.size(); ++i) {
            final PublicKey publicKey = this.recipientKeys.get(i);
            final KeyAgreeRecipientIdentifier keyAgreeRecipientIdentifier = this.recipientIDs.get(i);
            try {
                final ASN1ObjectIdentifier algorithm2 = algorithmIdentifier2.getAlgorithm();
                Object o;
                if (CMSUtils.isMQV(algorithm)) {
                    o = new MQVParameterSpec(this.ephemeralKP, publicKey, this.userKeyingMaterial);
                }
                else if (CMSUtils.isEC(algorithm)) {
                    o = new UserKeyingMaterialSpec(JceKeyAgreeRecipientInfoGenerator.ecc_cms_Generator.generateKDFMaterial(algorithmIdentifier2, this.keySizeProvider.getKeySize(algorithm2), this.userKeyingMaterial));
                }
                else if (CMSUtils.isRFC2631(algorithm)) {
                    if (this.userKeyingMaterial != null) {
                        o = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                    }
                    else {
                        if (algorithm.equals((Object)PKCSObjectIdentifiers.id_alg_SSDH)) {
                            throw new CMSException("User keying material must be set for static keys.");
                        }
                        o = null;
                    }
                }
                else {
                    if (!CMSUtils.isGOST(algorithm)) {
                        throw new CMSException("Unknown key agreement algorithm: " + algorithm);
                    }
                    if (this.userKeyingMaterial == null) {
                        throw new CMSException("User keying material must be set for static keys.");
                    }
                    o = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                }
                final KeyAgreement keyAgreement = this.helper.createKeyAgreement(algorithm);
                keyAgreement.init(senderPrivateKey, (AlgorithmParameterSpec)o, this.random);
                keyAgreement.doPhase(publicKey, true);
                final SecretKey generateSecret = keyAgreement.generateSecret(algorithm2.getId());
                final Cipher cipher = this.helper.createCipher(algorithm2);
                DEROctetString derOctetString;
                if (algorithm2.equals((Object)CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) || algorithm2.equals((Object)CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
                    cipher.init(3, generateSecret, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, this.userKeyingMaterial));
                    final byte[] wrap = cipher.wrap(this.helper.getJceKey(genericKey));
                    derOctetString = new DEROctetString(new Gost2814789EncryptedKey(Arrays.copyOfRange(wrap, 0, wrap.length - 4), Arrays.copyOfRange(wrap, wrap.length - 4, wrap.length)).getEncoded("DER"));
                }
                else {
                    cipher.init(3, generateSecret, this.random);
                    derOctetString = new DEROctetString(cipher.wrap(this.helper.getJceKey(genericKey)));
                }
                asn1EncodableVector.add((ASN1Encodable)new RecipientEncryptedKey(keyAgreeRecipientIdentifier, (ASN1OctetString)derOctetString));
            }
            catch (final GeneralSecurityException ex) {
                throw new CMSException("cannot perform agreement step: " + ex.getMessage(), ex);
            }
            catch (final IOException ex2) {
                throw new CMSException("unable to encode wrapped key: " + ex2.getMessage(), ex2);
            }
        }
        return (ASN1Sequence)new DERSequence(asn1EncodableVector);
    }
    
    @Override
    protected byte[] getUserKeyingMaterial(final AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        this.init(algorithmIdentifier.getAlgorithm());
        if (this.ephemeralKP != null) {
            final OriginatorPublicKey originatorPublicKey = this.createOriginatorPublicKey(SubjectPublicKeyInfo.getInstance((Object)this.ephemeralKP.getPublic().getEncoded()));
            try {
                if (this.userKeyingMaterial != null) {
                    return new MQVuserKeyingMaterial(originatorPublicKey, (ASN1OctetString)new DEROctetString(this.userKeyingMaterial)).getEncoded();
                }
                return new MQVuserKeyingMaterial(originatorPublicKey, (ASN1OctetString)null).getEncoded();
            }
            catch (final IOException ex) {
                throw new CMSException("unable to encode user keying material: " + ex.getMessage(), ex);
            }
        }
        return this.userKeyingMaterial;
    }
    
    private void init(final ASN1ObjectIdentifier asn1ObjectIdentifier) throws CMSException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        if (CMSUtils.isMQV(asn1ObjectIdentifier) && this.ephemeralKP == null) {
            try {
                final SubjectPublicKeyInfo instance = SubjectPublicKeyInfo.getInstance((Object)this.senderPublicKey.getEncoded());
                final AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(asn1ObjectIdentifier);
                algorithmParameters.init(instance.getAlgorithm().getParameters().toASN1Primitive().getEncoded());
                final KeyPairGenerator keyPairGenerator = this.helper.createKeyPairGenerator(asn1ObjectIdentifier);
                keyPairGenerator.initialize(algorithmParameters.getParameterSpec(AlgorithmParameterSpec.class), this.random);
                this.ephemeralKP = keyPairGenerator.generateKeyPair();
            }
            catch (final Exception ex) {
                throw new CMSException("cannot determine MQV ephemeral key pair parameters from public key: " + ex, ex);
            }
        }
    }
    
    static {
        JceKeyAgreeRecipientInfoGenerator.ecc_cms_Generator = new RFC5753KeyMaterialGenerator();
    }
}
