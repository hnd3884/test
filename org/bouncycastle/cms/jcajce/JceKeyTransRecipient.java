package org.bouncycastle.cms.jcajce;

import java.util.Iterator;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyAgreement;
import java.security.PublicKey;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport;
import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import java.security.PrivateKey;
import org.bouncycastle.cms.KeyTransRecipient;

public abstract class JceKeyTransRecipient implements KeyTransRecipient
{
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    protected Map extraMappings;
    protected boolean validateKeySize;
    protected boolean unwrappedKeyMustBeEncodable;
    
    public JceKeyTransRecipient(final PrivateKey recipientKey) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.contentHelper = this.helper;
        this.extraMappings = new HashMap();
        this.validateKeySize = false;
        this.recipientKey = recipientKey;
    }
    
    public JceKeyTransRecipient setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }
    
    public JceKeyTransRecipient setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        this.contentHelper = this.helper;
        return this;
    }
    
    public JceKeyTransRecipient setAlgorithmMapping(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        this.extraMappings.put(asn1ObjectIdentifier, s);
        return this;
    }
    
    public JceKeyTransRecipient setContentProvider(final Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }
    
    public JceKeyTransRecipient setMustProduceEncodableUnwrappedKey(final boolean unwrappedKeyMustBeEncodable) {
        this.unwrappedKeyMustBeEncodable = unwrappedKeyMustBeEncodable;
        return this;
    }
    
    public JceKeyTransRecipient setContentProvider(final String s) {
        this.contentHelper = CMSUtils.createContentHelper(s);
        return this;
    }
    
    public JceKeyTransRecipient setKeySizeValidation(final boolean validateKeySize) {
        this.validateKeySize = validateKeySize;
        return this;
    }
    
    protected Key extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        if (CMSUtils.isGOST(algorithmIdentifier.getAlgorithm())) {
            try {
                final GostR3410KeyTransport instance = GostR3410KeyTransport.getInstance((Object)array);
                final GostR3410TransportParameters transportParameters = instance.getTransportParameters();
                final PublicKey generatePublic = this.helper.createKeyFactory(algorithmIdentifier.getAlgorithm()).generatePublic(new X509EncodedKeySpec(transportParameters.getEphemeralPublicKey().getEncoded()));
                final KeyAgreement keyAgreement = this.helper.createKeyAgreement(algorithmIdentifier.getAlgorithm());
                keyAgreement.init(this.recipientKey, (AlgorithmParameterSpec)new UserKeyingMaterialSpec(transportParameters.getUkm()));
                keyAgreement.doPhase(generatePublic, true);
                final SecretKey generateSecret = keyAgreement.generateSecret("GOST28147");
                final Cipher cipher = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
                cipher.init(4, generateSecret, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(transportParameters.getEncryptionParamSet(), transportParameters.getUkm()));
                final Gost2814789EncryptedKey sessionEncryptedKey = instance.getSessionEncryptedKey();
                return cipher.unwrap(Arrays.concatenate(sessionEncryptedKey.getEncryptedKey(), sessionEncryptedKey.getMacKey()), this.helper.getBaseCipherName(algorithmIdentifier2.getAlgorithm()), 3);
            }
            catch (final Exception ex) {
                throw new CMSException("exception unwrapping key: " + ex.getMessage(), ex);
            }
        }
        final JceAsymmetricKeyUnwrapper setMustProduceEncodableUnwrappedKey = this.helper.createAsymmetricUnwrapper(algorithmIdentifier, this.recipientKey).setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
        if (!this.extraMappings.isEmpty()) {
            for (final ASN1ObjectIdentifier asn1ObjectIdentifier : this.extraMappings.keySet()) {
                setMustProduceEncodableUnwrappedKey.setAlgorithmMapping(asn1ObjectIdentifier, (String)this.extraMappings.get(asn1ObjectIdentifier));
            }
        }
        try {
            final Key jceKey = this.helper.getJceKey(algorithmIdentifier2.getAlgorithm(), setMustProduceEncodableUnwrappedKey.generateUnwrappedKey(algorithmIdentifier2, array));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(algorithmIdentifier2, jceKey);
            }
            return jceKey;
        }
        catch (final OperatorException ex2) {
            throw new CMSException("exception unwrapping key: " + ex2.getMessage(), ex2);
        }
    }
}
