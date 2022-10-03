package org.bouncycastle.openssl.jcajce;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.operator.GenericKey;
import javax.crypto.CipherOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.GeneralSecurityException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.SecretKey;
import java.security.AlgorithmParameterGenerator;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.AlgorithmParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class JceOpenSSLPKCS8EncryptorBuilder
{
    public static final String AES_128_CBC;
    public static final String AES_192_CBC;
    public static final String AES_256_CBC;
    public static final String DES3_CBC;
    public static final String PBE_SHA1_RC4_128;
    public static final String PBE_SHA1_RC4_40;
    public static final String PBE_SHA1_3DES;
    public static final String PBE_SHA1_2DES;
    public static final String PBE_SHA1_RC2_128;
    public static final String PBE_SHA1_RC2_40;
    private JcaJceHelper helper;
    private AlgorithmParameters params;
    private ASN1ObjectIdentifier algOID;
    byte[] salt;
    int iterationCount;
    private Cipher cipher;
    private SecureRandom random;
    private AlgorithmParameterGenerator paramGen;
    private char[] password;
    private SecretKey key;
    private AlgorithmIdentifier prf;
    
    public JceOpenSSLPKCS8EncryptorBuilder(final ASN1ObjectIdentifier algOID) {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
        this.prf = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE);
        this.algOID = algOID;
        this.iterationCount = 2048;
    }
    
    public JceOpenSSLPKCS8EncryptorBuilder setRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public JceOpenSSLPKCS8EncryptorBuilder setPasssword(final char[] password) {
        this.password = password;
        return this;
    }
    
    public JceOpenSSLPKCS8EncryptorBuilder setPRF(final AlgorithmIdentifier prf) {
        this.prf = prf;
        return this;
    }
    
    public JceOpenSSLPKCS8EncryptorBuilder setIterationCount(final int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }
    
    public JceOpenSSLPKCS8EncryptorBuilder setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public JceOpenSSLPKCS8EncryptorBuilder setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public OutputEncryptor build() throws OperatorCreationException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            this.cipher = this.helper.createCipher(this.algOID.getId());
            if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
                this.paramGen = this.helper.createAlgorithmParameterGenerator(this.algOID.getId());
            }
        }
        catch (final GeneralSecurityException ex) {
            throw new OperatorCreationException(this.algOID + " not available: " + ex.getMessage(), ex);
        }
        if (PEMUtilities.isPKCS5Scheme2(this.algOID)) {
            this.salt = new byte[PEMUtilities.getSaltSize(this.prf.getAlgorithm())];
            this.random.nextBytes(this.salt);
            this.params = this.paramGen.generateParameters();
            AlgorithmIdentifier algorithmIdentifier;
            try {
                final EncryptionScheme encryptionScheme = new EncryptionScheme(this.algOID, (ASN1Encodable)ASN1Primitive.fromByteArray(this.params.getEncoded()));
                final KeyDerivationFunc keyDerivationFunc = new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(this.salt, this.iterationCount, this.prf));
                final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                asn1EncodableVector.add((ASN1Encodable)keyDerivationFunc);
                asn1EncodableVector.add((ASN1Encodable)encryptionScheme);
                algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, (ASN1Encodable)PBES2Parameters.getInstance((Object)new DERSequence(asn1EncodableVector)));
            }
            catch (final IOException ex2) {
                throw new OperatorCreationException(ex2.getMessage(), ex2);
            }
            try {
                if (PEMUtilities.isHmacSHA1(this.prf)) {
                    this.key = PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount);
                }
                else {
                    this.key = PEMUtilities.generateSecretKeyForPKCS5Scheme2(this.helper, this.algOID.getId(), this.password, this.salt, this.iterationCount, this.prf);
                }
                this.cipher.init(1, this.key, this.params);
                return new OutputEncryptor() {
                    final /* synthetic */ AlgorithmIdentifier val$algID;
                    
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return this.val$algID;
                    }
                    
                    public OutputStream getOutputStream(final OutputStream outputStream) {
                        return new CipherOutputStream(outputStream, JceOpenSSLPKCS8EncryptorBuilder.this.cipher);
                    }
                    
                    public GenericKey getKey() {
                        return new JceGenericKey(this.val$algID, JceOpenSSLPKCS8EncryptorBuilder.this.key);
                    }
                };
            }
            catch (final GeneralSecurityException ex3) {
                throw new OperatorCreationException(ex3.getMessage(), ex3);
            }
        }
        if (!PEMUtilities.isPKCS12(this.algOID)) {
            throw new OperatorCreationException("unknown algorithm: " + this.algOID, null);
        }
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        this.salt = new byte[20];
        this.random.nextBytes(this.salt);
        asn1EncodableVector2.add((ASN1Encodable)new DEROctetString(this.salt));
        asn1EncodableVector2.add((ASN1Encodable)new ASN1Integer((long)this.iterationCount));
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(this.algOID, (ASN1Encodable)PKCS12PBEParams.getInstance((Object)new DERSequence(asn1EncodableVector2)));
        try {
            this.cipher.init(1, (Key)new PKCS12KeyWithParameters(this.password, this.salt, this.iterationCount));
        }
        catch (final GeneralSecurityException ex4) {
            throw new OperatorCreationException(ex4.getMessage(), ex4);
        }
        return new OutputEncryptor() {
            final /* synthetic */ AlgorithmIdentifier val$algID;
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier;
            }
            
            public OutputStream getOutputStream(final OutputStream outputStream) {
                return new CipherOutputStream(outputStream, JceOpenSSLPKCS8EncryptorBuilder.this.cipher);
            }
            
            public GenericKey getKey() {
                return new JceGenericKey(algorithmIdentifier, JceOpenSSLPKCS8EncryptorBuilder.this.key);
            }
        };
    }
    
    static {
        AES_128_CBC = NISTObjectIdentifiers.id_aes128_CBC.getId();
        AES_192_CBC = NISTObjectIdentifiers.id_aes192_CBC.getId();
        AES_256_CBC = NISTObjectIdentifiers.id_aes256_CBC.getId();
        DES3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC.getId();
        PBE_SHA1_RC4_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId();
        PBE_SHA1_RC4_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4.getId();
        PBE_SHA1_3DES = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC.getId();
        PBE_SHA1_2DES = PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC.getId();
        PBE_SHA1_RC2_128 = PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC.getId();
        PBE_SHA1_RC2_40 = PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC.getId();
    }
}
