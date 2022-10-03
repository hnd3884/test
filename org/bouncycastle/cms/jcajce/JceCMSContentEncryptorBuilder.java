package org.bouncycastle.cms.jcajce;

import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.operator.GenericKey;
import javax.crypto.CipherOutputStream;
import java.io.OutputStream;
import javax.crypto.KeyGenerator;
import java.security.GeneralSecurityException;
import java.security.Key;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.SecretKey;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OutputEncryptor;
import java.security.Provider;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class JceCMSContentEncryptorBuilder
{
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER;
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private EnvelopedDataHelper helper;
    private SecureRandom random;
    private AlgorithmParameters algorithmParameters;
    
    public JceCMSContentEncryptorBuilder(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this(asn1ObjectIdentifier, JceCMSContentEncryptorBuilder.KEY_SIZE_PROVIDER.getKeySize(asn1ObjectIdentifier));
    }
    
    public JceCMSContentEncryptorBuilder(final ASN1ObjectIdentifier encryptionOID, final int keySize) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.encryptionOID = encryptionOID;
        final int keySize2 = JceCMSContentEncryptorBuilder.KEY_SIZE_PROVIDER.getKeySize(encryptionOID);
        if (encryptionOID.equals((Object)PKCSObjectIdentifiers.des_EDE3_CBC)) {
            if (keySize != 168 && keySize != keySize2) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 168;
        }
        else if (encryptionOID.equals((Object)OIWObjectIdentifiers.desCBC)) {
            if (keySize != 56 && keySize != keySize2) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = 56;
        }
        else {
            if (keySize2 > 0 && keySize2 != keySize) {
                throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
            }
            this.keySize = keySize;
        }
    }
    
    public JceCMSContentEncryptorBuilder setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }
    
    public JceCMSContentEncryptorBuilder setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        return this;
    }
    
    public JceCMSContentEncryptorBuilder setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public JceCMSContentEncryptorBuilder setAlgorithmParameters(final AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }
    
    public OutputEncryptor build() throws CMSException {
        return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.algorithmParameters, this.random);
    }
    
    static {
        KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    }
    
    private class CMSOutputEncryptor implements OutputEncryptor
    {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Cipher cipher;
        
        CMSOutputEncryptor(final ASN1ObjectIdentifier asn1ObjectIdentifier, final int n, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws CMSException {
            final KeyGenerator keyGenerator = JceCMSContentEncryptorBuilder.this.helper.createKeyGenerator(asn1ObjectIdentifier);
            if (secureRandom == null) {
                secureRandom = new SecureRandom();
            }
            if (n < 0) {
                keyGenerator.init(secureRandom);
            }
            else {
                keyGenerator.init(n, secureRandom);
            }
            this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(asn1ObjectIdentifier);
            this.encKey = keyGenerator.generateKey();
            if (algorithmParameters == null) {
                algorithmParameters = JceCMSContentEncryptorBuilder.this.helper.generateParameters(asn1ObjectIdentifier, this.encKey, secureRandom);
            }
            try {
                this.cipher.init(1, this.encKey, algorithmParameters, secureRandom);
            }
            catch (final GeneralSecurityException ex) {
                throw new CMSException("unable to initialize cipher: " + ex.getMessage(), ex);
            }
            if (algorithmParameters == null) {
                algorithmParameters = this.cipher.getParameters();
            }
            this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(asn1ObjectIdentifier, algorithmParameters);
        }
        
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }
        
        public OutputStream getOutputStream(final OutputStream outputStream) {
            return new CipherOutputStream(outputStream, this.cipher);
        }
        
        public GenericKey getKey() {
            return new JceGenericKey(this.algorithmIdentifier, this.encKey);
        }
    }
}
