package org.bouncycastle.cms.jcajce;

import org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.bouncycastle.util.Pack;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import java.util.HashSet;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.crypto.KeyAgreement;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1OctetString;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.Provider;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.SecretKeySizeProvider;
import java.security.PrivateKey;
import java.util.Set;
import org.bouncycastle.cms.KeyAgreeRecipient;

public abstract class JceKeyAgreeRecipient implements KeyAgreeRecipient
{
    private static final Set possibleOldMessages;
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    private SecretKeySizeProvider keySizeProvider;
    private static KeyMaterialGenerator old_ecc_cms_Generator;
    private static KeyMaterialGenerator ecc_cms_Generator;
    
    public JceKeyAgreeRecipient(final PrivateKey recipientKey) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.contentHelper = this.helper;
        this.keySizeProvider = new DefaultSecretKeySizeProvider();
        this.recipientKey = recipientKey;
    }
    
    public JceKeyAgreeRecipient setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }
    
    public JceKeyAgreeRecipient setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        this.contentHelper = this.helper;
        return this;
    }
    
    public JceKeyAgreeRecipient setContentProvider(final Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }
    
    public JceKeyAgreeRecipient setContentProvider(final String s) {
        this.contentHelper = CMSUtils.createContentHelper(s);
        return this;
    }
    
    private SecretKey calculateAgreedWrapKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final PublicKey publicKey, final ASN1OctetString asn1OctetString, final PrivateKey privateKey, final KeyMaterialGenerator keyMaterialGenerator) throws CMSException, GeneralSecurityException, IOException {
        if (CMSUtils.isMQV(algorithmIdentifier.getAlgorithm())) {
            final MQVuserKeyingMaterial instance = MQVuserKeyingMaterial.getInstance((Object)asn1OctetString.getOctets());
            final PublicKey generatePublic = this.helper.createKeyFactory(algorithmIdentifier.getAlgorithm()).generatePublic(new X509EncodedKeySpec(new SubjectPublicKeyInfo(this.getPrivateKeyAlgorithmIdentifier(), instance.getEphemeralPublicKey().getPublicKey().getBytes()).getEncoded()));
            final KeyAgreement keyAgreement = this.helper.createKeyAgreement(algorithmIdentifier.getAlgorithm());
            byte[] generateKDFMaterial = (byte[])((instance.getAddedukm() != null) ? instance.getAddedukm().getOctets() : null);
            if (keyMaterialGenerator == JceKeyAgreeRecipient.old_ecc_cms_Generator) {
                generateKDFMaterial = JceKeyAgreeRecipient.old_ecc_cms_Generator.generateKDFMaterial(algorithmIdentifier2, this.keySizeProvider.getKeySize(algorithmIdentifier2), generateKDFMaterial);
            }
            keyAgreement.init(privateKey, (AlgorithmParameterSpec)new MQVParameterSpec(privateKey, generatePublic, generateKDFMaterial));
            keyAgreement.doPhase(publicKey, true);
            return keyAgreement.generateSecret(algorithmIdentifier2.getAlgorithm().getId());
        }
        final KeyAgreement keyAgreement2 = this.helper.createKeyAgreement(algorithmIdentifier.getAlgorithm());
        Object o = null;
        if (CMSUtils.isEC(algorithmIdentifier.getAlgorithm())) {
            if (asn1OctetString != null) {
                o = new UserKeyingMaterialSpec(keyMaterialGenerator.generateKDFMaterial(algorithmIdentifier2, this.keySizeProvider.getKeySize(algorithmIdentifier2), asn1OctetString.getOctets()));
            }
            else {
                o = new UserKeyingMaterialSpec(keyMaterialGenerator.generateKDFMaterial(algorithmIdentifier2, this.keySizeProvider.getKeySize(algorithmIdentifier2), null));
            }
        }
        else if (CMSUtils.isRFC2631(algorithmIdentifier.getAlgorithm())) {
            if (asn1OctetString != null) {
                o = new UserKeyingMaterialSpec(asn1OctetString.getOctets());
            }
        }
        else {
            if (!CMSUtils.isGOST(algorithmIdentifier.getAlgorithm())) {
                throw new CMSException("Unknown key agreement algorithm: " + algorithmIdentifier.getAlgorithm());
            }
            if (asn1OctetString != null) {
                o = new UserKeyingMaterialSpec(asn1OctetString.getOctets());
            }
        }
        keyAgreement2.init(privateKey, (AlgorithmParameterSpec)o);
        keyAgreement2.doPhase(publicKey, true);
        return keyAgreement2.generateSecret(algorithmIdentifier2.getAlgorithm().getId());
    }
    
    private Key unwrapSessionKey(final ASN1ObjectIdentifier asn1ObjectIdentifier, final SecretKey secretKey, final ASN1ObjectIdentifier asn1ObjectIdentifier2, final byte[] array) throws CMSException, InvalidKeyException, NoSuchAlgorithmException {
        final Cipher cipher = this.helper.createCipher(asn1ObjectIdentifier);
        cipher.init(4, secretKey);
        return cipher.unwrap(array, this.helper.getBaseCipherName(asn1ObjectIdentifier2), 3);
    }
    
    protected Key extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final SubjectPublicKeyInfo subjectPublicKeyInfo, final ASN1OctetString asn1OctetString, final byte[] array) throws CMSException {
        try {
            final AlgorithmIdentifier instance = AlgorithmIdentifier.getInstance((Object)algorithmIdentifier.getParameters());
            final PublicKey generatePublic = this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
            try {
                final SecretKey calculateAgreedWrapKey = this.calculateAgreedWrapKey(algorithmIdentifier, instance, generatePublic, asn1OctetString, this.recipientKey, JceKeyAgreeRecipient.ecc_cms_Generator);
                if (instance.getAlgorithm().equals((Object)CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) || instance.getAlgorithm().equals((Object)CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
                    final Gost2814789EncryptedKey instance2 = Gost2814789EncryptedKey.getInstance((Object)array);
                    final Gost2814789KeyWrapParameters instance3 = Gost2814789KeyWrapParameters.getInstance((Object)instance.getParameters());
                    final Cipher cipher = this.helper.createCipher(instance.getAlgorithm());
                    cipher.init(4, calculateAgreedWrapKey, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(instance3.getEncryptionParamSet(), asn1OctetString.getOctets()));
                    return cipher.unwrap(Arrays.concatenate(instance2.getEncryptedKey(), instance2.getMacKey()), this.helper.getBaseCipherName(algorithmIdentifier2.getAlgorithm()), 3);
                }
                return this.unwrapSessionKey(instance.getAlgorithm(), calculateAgreedWrapKey, algorithmIdentifier2.getAlgorithm(), array);
            }
            catch (final InvalidKeyException ex) {
                if (JceKeyAgreeRecipient.possibleOldMessages.contains(algorithmIdentifier.getAlgorithm())) {
                    return this.unwrapSessionKey(instance.getAlgorithm(), this.calculateAgreedWrapKey(algorithmIdentifier, instance, generatePublic, asn1OctetString, this.recipientKey, JceKeyAgreeRecipient.old_ecc_cms_Generator), algorithmIdentifier2.getAlgorithm(), array);
                }
                throw ex;
            }
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new CMSException("can't find algorithm.", ex2);
        }
        catch (final InvalidKeyException ex3) {
            throw new CMSException("key invalid in message.", ex3);
        }
        catch (final InvalidKeySpecException ex4) {
            throw new CMSException("originator key spec invalid.", ex4);
        }
        catch (final NoSuchPaddingException ex5) {
            throw new CMSException("required padding not supported.", ex5);
        }
        catch (final Exception ex6) {
            throw new CMSException("originator key invalid.", ex6);
        }
    }
    
    public AlgorithmIdentifier getPrivateKeyAlgorithmIdentifier() {
        return PrivateKeyInfo.getInstance((Object)this.recipientKey.getEncoded()).getPrivateKeyAlgorithm();
    }
    
    static {
        (possibleOldMessages = new HashSet()).add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        JceKeyAgreeRecipient.possibleOldMessages.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        JceKeyAgreeRecipient.old_ecc_cms_Generator = new KeyMaterialGenerator() {
            public byte[] generateKDFMaterial(final AlgorithmIdentifier algorithmIdentifier, final int n, final byte[] array) {
                final ECCCMSSharedInfo ecccmsSharedInfo = new ECCCMSSharedInfo(new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE), array, Pack.intToBigEndian(n));
                try {
                    return ecccmsSharedInfo.getEncoded("DER");
                }
                catch (final IOException ex) {
                    throw new IllegalStateException("Unable to create KDF material: " + ex);
                }
            }
        };
        JceKeyAgreeRecipient.ecc_cms_Generator = new RFC5753KeyMaterialGenerator();
    }
}
