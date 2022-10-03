package org.bouncycastle.cms.bc;

import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import java.io.OutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Integers;
import org.bouncycastle.cms.CMSAlgorithm;
import java.util.HashMap;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OutputEncryptor;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;

public class BcCMSContentEncryptorBuilder
{
    private static Map keySizes;
    private final ASN1ObjectIdentifier encryptionOID;
    private final int keySize;
    private EnvelopedDataHelper helper;
    private SecureRandom random;
    
    private static int getKeySize(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final Integer n = BcCMSContentEncryptorBuilder.keySizes.get(asn1ObjectIdentifier);
        if (n != null) {
            return n;
        }
        return -1;
    }
    
    public BcCMSContentEncryptorBuilder(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this(asn1ObjectIdentifier, getKeySize(asn1ObjectIdentifier));
    }
    
    public BcCMSContentEncryptorBuilder(final ASN1ObjectIdentifier encryptionOID, final int keySize) {
        this.helper = new EnvelopedDataHelper();
        this.encryptionOID = encryptionOID;
        this.keySize = keySize;
    }
    
    public BcCMSContentEncryptorBuilder setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public OutputEncryptor build() throws CMSException {
        return new CMSOutputEncryptor(this.encryptionOID, this.keySize, this.random);
    }
    
    static {
        (BcCMSContentEncryptorBuilder.keySizes = new HashMap()).put(CMSAlgorithm.AES128_CBC, Integers.valueOf(128));
        BcCMSContentEncryptorBuilder.keySizes.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(192));
        BcCMSContentEncryptorBuilder.keySizes.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(256));
        BcCMSContentEncryptorBuilder.keySizes.put(CMSAlgorithm.CAMELLIA128_CBC, Integers.valueOf(128));
        BcCMSContentEncryptorBuilder.keySizes.put(CMSAlgorithm.CAMELLIA192_CBC, Integers.valueOf(192));
        BcCMSContentEncryptorBuilder.keySizes.put(CMSAlgorithm.CAMELLIA256_CBC, Integers.valueOf(256));
    }
    
    private class CMSOutputEncryptor implements OutputEncryptor
    {
        private KeyParameter encKey;
        private AlgorithmIdentifier algorithmIdentifier;
        private Object cipher;
        
        CMSOutputEncryptor(final ASN1ObjectIdentifier asn1ObjectIdentifier, final int n, SecureRandom secureRandom) throws CMSException {
            if (secureRandom == null) {
                secureRandom = new SecureRandom();
            }
            this.encKey = new KeyParameter(BcCMSContentEncryptorBuilder.this.helper.createKeyGenerator(asn1ObjectIdentifier, secureRandom).generateKey());
            this.algorithmIdentifier = BcCMSContentEncryptorBuilder.this.helper.generateAlgorithmIdentifier(asn1ObjectIdentifier, (CipherParameters)this.encKey, secureRandom);
            BcCMSContentEncryptorBuilder.this.helper;
            this.cipher = EnvelopedDataHelper.createContentCipher(true, (CipherParameters)this.encKey, this.algorithmIdentifier);
        }
        
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithmIdentifier;
        }
        
        public OutputStream getOutputStream(final OutputStream outputStream) {
            if (this.cipher instanceof BufferedBlockCipher) {
                return (OutputStream)new CipherOutputStream(outputStream, (BufferedBlockCipher)this.cipher);
            }
            return (OutputStream)new CipherOutputStream(outputStream, (StreamCipher)this.cipher);
        }
        
        public GenericKey getKey() {
            return new GenericKey(this.algorithmIdentifier, this.encKey.getKey());
        }
    }
}
