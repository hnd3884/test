package org.bouncycastle.pkcs.jcajce;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import javax.crypto.SecretKey;
import org.bouncycastle.operator.GenericKey;
import javax.crypto.CipherOutputStream;
import java.io.OutputStream;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.crypto.util.ScryptConfig;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import java.security.Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.crypto.util.PBKDF2Config;
import org.bouncycastle.operator.SecretKeySizeProvider;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.crypto.util.PBKDFConfig;

public class JcePKCSPBEOutputEncryptorBuilder
{
    private final PBKDFConfig pbkdf;
    private JcaJceHelper helper;
    private ASN1ObjectIdentifier algorithm;
    private ASN1ObjectIdentifier keyEncAlgorithm;
    private SecureRandom random;
    private SecretKeySizeProvider keySizeProvider;
    private int iterationCount;
    private PBKDF2Config.Builder pbkdfBuilder;
    
    public JcePKCSPBEOutputEncryptorBuilder(final ASN1ObjectIdentifier keyEncAlgorithm) {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
        this.keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
        this.iterationCount = 1024;
        this.pbkdfBuilder = new PBKDF2Config.Builder();
        this.pbkdf = null;
        if (this.isPKCS12(keyEncAlgorithm)) {
            this.algorithm = keyEncAlgorithm;
            this.keyEncAlgorithm = keyEncAlgorithm;
        }
        else {
            this.algorithm = PKCSObjectIdentifiers.id_PBES2;
            this.keyEncAlgorithm = keyEncAlgorithm;
        }
    }
    
    public JcePKCSPBEOutputEncryptorBuilder(final PBKDFConfig pbkdf, final ASN1ObjectIdentifier keyEncAlgorithm) {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
        this.keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
        this.iterationCount = 1024;
        this.pbkdfBuilder = new PBKDF2Config.Builder();
        this.algorithm = PKCSObjectIdentifiers.id_PBES2;
        this.pbkdf = pbkdf;
        this.keyEncAlgorithm = keyEncAlgorithm;
    }
    
    public JcePKCSPBEOutputEncryptorBuilder setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public JcePKCSPBEOutputEncryptorBuilder setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public JcePKCSPBEOutputEncryptorBuilder setRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public JcePKCSPBEOutputEncryptorBuilder setKeySizeProvider(final SecretKeySizeProvider keySizeProvider) {
        this.keySizeProvider = keySizeProvider;
        return this;
    }
    
    public JcePKCSPBEOutputEncryptorBuilder setPRF(final AlgorithmIdentifier algorithmIdentifier) {
        if (this.pbkdf != null) {
            throw new IllegalStateException("set PRF count using PBKDFDef");
        }
        this.pbkdfBuilder.withPRF(algorithmIdentifier);
        return this;
    }
    
    public JcePKCSPBEOutputEncryptorBuilder setIterationCount(final int iterationCount) {
        if (this.pbkdf != null) {
            throw new IllegalStateException("set iteration count using PBKDFDef");
        }
        this.iterationCount = iterationCount;
        this.pbkdfBuilder.withIterationCount(iterationCount);
        return this;
    }
    
    public OutputEncryptor build(final char[] array) throws OperatorCreationException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            Cipher cipher;
            AlgorithmIdentifier algorithmIdentifier;
            if (this.isPKCS12(this.algorithm)) {
                final byte[] array2 = new byte[20];
                this.random.nextBytes(array2);
                cipher = this.helper.createCipher(this.algorithm.getId());
                cipher.init(1, (Key)new PKCS12KeyWithParameters(array, array2, this.iterationCount));
                algorithmIdentifier = new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)new PKCS12PBEParams(array2, this.iterationCount));
            }
            else {
                if (!this.algorithm.equals((Object)PKCSObjectIdentifiers.id_PBES2)) {
                    throw new OperatorCreationException("unrecognised algorithm");
                }
                final Object o = (this.pbkdf == null) ? this.pbkdfBuilder.build() : this.pbkdf;
                if (MiscObjectIdentifiers.id_scrypt.equals((Object)((PBKDFConfig)o).getAlgorithm())) {
                    final ScryptConfig scryptConfig = (ScryptConfig)o;
                    final byte[] array3 = new byte[scryptConfig.getSaltLength()];
                    this.random.nextBytes(array3);
                    final ScryptParams scryptParams = new ScryptParams(array3, scryptConfig.getCostParameter(), scryptConfig.getBlockSize(), scryptConfig.getParallelizationParameter());
                    final SecretKey generateSecret = this.helper.createSecretKeyFactory("SCRYPT").generateSecret((KeySpec)new ScryptKeySpec(array, array3, scryptConfig.getCostParameter(), scryptConfig.getBlockSize(), scryptConfig.getParallelizationParameter(), this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))));
                    cipher = this.helper.createCipher(this.keyEncAlgorithm.getId());
                    cipher.init(1, generateSecret, this.random);
                    algorithmIdentifier = new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)new PBES2Parameters(new KeyDerivationFunc(MiscObjectIdentifiers.id_scrypt, (ASN1Encodable)scryptParams), new EncryptionScheme(this.keyEncAlgorithm, (ASN1Encodable)ASN1Primitive.fromByteArray(cipher.getParameters().getEncoded()))));
                }
                else {
                    final PBKDF2Config pbkdf2Config = (PBKDF2Config)o;
                    final byte[] array4 = new byte[pbkdf2Config.getSaltLength()];
                    this.random.nextBytes(array4);
                    final SecretKey generateSecret2 = this.helper.createSecretKeyFactory(JceUtils.getAlgorithm(pbkdf2Config.getPRF().getAlgorithm())).generateSecret(new PBEKeySpec(array, array4, pbkdf2Config.getIterationCount(), this.keySizeProvider.getKeySize(new AlgorithmIdentifier(this.keyEncAlgorithm))));
                    cipher = this.helper.createCipher(this.keyEncAlgorithm.getId());
                    cipher.init(1, generateSecret2, this.random);
                    algorithmIdentifier = new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(array4, pbkdf2Config.getIterationCount(), pbkdf2Config.getPRF())), new EncryptionScheme(this.keyEncAlgorithm, (ASN1Encodable)ASN1Primitive.fromByteArray(cipher.getParameters().getEncoded()))));
                }
            }
            return new OutputEncryptor() {
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return algorithmIdentifier;
                }
                
                public OutputStream getOutputStream(final OutputStream outputStream) {
                    return new CipherOutputStream(outputStream, cipher);
                }
                
                public GenericKey getKey() {
                    if (JcePKCSPBEOutputEncryptorBuilder.this.isPKCS12(algorithmIdentifier.getAlgorithm())) {
                        return new GenericKey(algorithmIdentifier, PKCS12PasswordToBytes(array));
                    }
                    return new GenericKey(algorithmIdentifier, PKCS5PasswordToBytes(array));
                }
            };
        }
        catch (final Exception ex) {
            throw new OperatorCreationException("unable to create OutputEncryptor: " + ex.getMessage(), ex);
        }
    }
    
    private boolean isPKCS12(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return asn1ObjectIdentifier.on(PKCSObjectIdentifiers.pkcs_12PbeIds) || asn1ObjectIdentifier.on(BCObjectIdentifiers.bc_pbe_sha1_pkcs12) || asn1ObjectIdentifier.on(BCObjectIdentifiers.bc_pbe_sha256_pkcs12);
    }
    
    private static byte[] PKCS5PasswordToBytes(final char[] array) {
        if (array != null) {
            final byte[] array2 = new byte[array.length];
            for (int i = 0; i != array2.length; ++i) {
                array2[i] = (byte)array[i];
            }
            return array2;
        }
        return new byte[0];
    }
    
    private static byte[] PKCS12PasswordToBytes(final char[] array) {
        if (array != null && array.length > 0) {
            final byte[] array2 = new byte[(array.length + 1) * 2];
            for (int i = 0; i != array.length; ++i) {
                array2[i * 2] = (byte)(array[i] >>> 8);
                array2[i * 2 + 1] = (byte)array[i];
            }
            return array2;
        }
        return new byte[0];
    }
}
