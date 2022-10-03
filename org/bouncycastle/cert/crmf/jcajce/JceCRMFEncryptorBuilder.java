package org.bouncycastle.cert.crmf.jcajce;

import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.operator.GenericKey;
import javax.crypto.CipherOutputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import javax.crypto.KeyGenerator;
import java.security.GeneralSecurityException;
import java.security.Key;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.SecretKey;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class JceCRMFEncryptorBuilder
{
    private static final SecretKeySizeProvider KEY_SIZE_PROVIDER;
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private CRMFHelper helper;
    private SecureRandom random;
    
    public JceCRMFEncryptorBuilder(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this(asn1ObjectIdentifier, -1);
    }
    
    public JceCRMFEncryptorBuilder(final ASN1ObjectIdentifier encryptionOID, final int keySize) {
        this.helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.encryptionOID = encryptionOID;
        this.keySize = keySize;
    }
    
    public JceCRMFEncryptorBuilder setProvider(final Provider provider) {
        this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JceCRMFEncryptorBuilder setProvider(final String s) {
        this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public JceCRMFEncryptorBuilder setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public OutputEncryptor build() throws CRMFException {
        return new CRMFOutputEncryptor(this.encryptionOID, this.keySize, this.random);
    }
    
    static {
        KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
    }
    
    private class CRMFOutputEncryptor implements OutputEncryptor
    {
        private SecretKey encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Cipher cipher;
        
        CRMFOutputEncryptor(final ASN1ObjectIdentifier asn1ObjectIdentifier, int keySize, SecureRandom secureRandom) throws CRMFException {
            final KeyGenerator keyGenerator = JceCRMFEncryptorBuilder.this.helper.createKeyGenerator(asn1ObjectIdentifier);
            if (secureRandom == null) {
                secureRandom = new SecureRandom();
            }
            if (keySize < 0) {
                keySize = JceCRMFEncryptorBuilder.KEY_SIZE_PROVIDER.getKeySize(asn1ObjectIdentifier);
            }
            if (keySize < 0) {
                keyGenerator.init(secureRandom);
            }
            else {
                keyGenerator.init(keySize, secureRandom);
            }
            this.cipher = JceCRMFEncryptorBuilder.this.helper.createCipher(asn1ObjectIdentifier);
            this.encKey = keyGenerator.generateKey();
            AlgorithmParameters algorithmParameters = JceCRMFEncryptorBuilder.this.helper.generateParameters(asn1ObjectIdentifier, this.encKey, secureRandom);
            try {
                this.cipher.init(1, this.encKey, algorithmParameters, secureRandom);
            }
            catch (final GeneralSecurityException ex) {
                throw new CRMFException("unable to initialize cipher: " + ex.getMessage(), ex);
            }
            if (algorithmParameters == null) {
                algorithmParameters = this.cipher.getParameters();
            }
            this.algorithmIdentifier = JceCRMFEncryptorBuilder.this.helper.getAlgorithmIdentifier(asn1ObjectIdentifier, algorithmParameters);
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
