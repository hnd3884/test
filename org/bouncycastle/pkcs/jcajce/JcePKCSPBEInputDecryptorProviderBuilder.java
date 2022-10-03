package org.bouncycastle.pkcs.jcajce;

import org.bouncycastle.asn1.ASN1Encodable;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import javax.crypto.CipherInputStream;
import java.io.InputStream;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import java.security.spec.KeySpec;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.asn1.misc.ScryptParams;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import java.security.Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.Cipher;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class JcePKCSPBEInputDecryptorProviderBuilder
{
    private JcaJceHelper helper;
    private boolean wrongPKCS12Zero;
    private SecretKeySizeProvider keySizeProvider;
    
    public JcePKCSPBEInputDecryptorProviderBuilder() {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
        this.wrongPKCS12Zero = false;
        this.keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;
    }
    
    public JcePKCSPBEInputDecryptorProviderBuilder setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public JcePKCSPBEInputDecryptorProviderBuilder setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public JcePKCSPBEInputDecryptorProviderBuilder setTryWrongPKCS12Zero(final boolean wrongPKCS12Zero) {
        this.wrongPKCS12Zero = wrongPKCS12Zero;
        return this;
    }
    
    public JcePKCSPBEInputDecryptorProviderBuilder setKeySizeProvider(final SecretKeySizeProvider keySizeProvider) {
        this.keySizeProvider = keySizeProvider;
        return this;
    }
    
    public InputDecryptorProvider build(final char[] array) {
        return new InputDecryptorProvider() {
            private Cipher cipher;
            private AlgorithmIdentifier encryptionAlg;
            
            public InputDecryptor get(final AlgorithmIdentifier encryptionAlg) throws OperatorCreationException {
                final ASN1ObjectIdentifier algorithm = encryptionAlg.getAlgorithm();
                try {
                    if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                        final PKCS12PBEParams instance = PKCS12PBEParams.getInstance((Object)encryptionAlg.getParameters());
                        (this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(algorithm.getId())).init(2, (Key)new PKCS12KeyWithParameters(array, JcePKCSPBEInputDecryptorProviderBuilder.this.wrongPKCS12Zero, instance.getIV(), instance.getIterations().intValue()));
                        this.encryptionAlg = encryptionAlg;
                    }
                    else if (algorithm.equals((Object)PKCSObjectIdentifiers.id_PBES2)) {
                        final PBES2Parameters instance2 = PBES2Parameters.getInstance((Object)encryptionAlg.getParameters());
                        SecretKey secretKey;
                        if (MiscObjectIdentifiers.id_scrypt.equals((Object)instance2.getKeyDerivationFunc().getAlgorithm())) {
                            final ScryptParams instance3 = ScryptParams.getInstance((Object)instance2.getKeyDerivationFunc().getParameters());
                            secretKey = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createSecretKeyFactory("SCRYPT").generateSecret((KeySpec)new ScryptKeySpec(array, instance3.getSalt(), instance3.getCostParameter().intValue(), instance3.getBlockSize().intValue(), instance3.getParallelizationParameter().intValue(), JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(AlgorithmIdentifier.getInstance((Object)instance2.getEncryptionScheme()))));
                        }
                        else {
                            final SecretKeyFactory secretKeyFactory = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createSecretKeyFactory(instance2.getKeyDerivationFunc().getAlgorithm().getId());
                            final PBKDF2Params instance4 = PBKDF2Params.getInstance((Object)instance2.getKeyDerivationFunc().getParameters());
                            final AlgorithmIdentifier instance5 = AlgorithmIdentifier.getInstance((Object)instance2.getEncryptionScheme());
                            if (instance4.isDefaultPrf()) {
                                secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(array, instance4.getSalt(), instance4.getIterationCount().intValue(), JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(instance5)));
                            }
                            else {
                                secretKey = secretKeyFactory.generateSecret((KeySpec)new PBKDF2KeySpec(array, instance4.getSalt(), instance4.getIterationCount().intValue(), JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(instance5), instance4.getPrf()));
                            }
                        }
                        this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(instance2.getEncryptionScheme().getAlgorithm().getId());
                        this.encryptionAlg = AlgorithmIdentifier.getInstance((Object)instance2.getEncryptionScheme());
                        final ASN1Encodable parameters = instance2.getEncryptionScheme().getParameters();
                        if (parameters instanceof ASN1OctetString) {
                            this.cipher.init(2, secretKey, new IvParameterSpec(ASN1OctetString.getInstance((Object)parameters).getOctets()));
                        }
                        else {
                            final GOST28147Parameters instance6 = GOST28147Parameters.getInstance((Object)parameters);
                            this.cipher.init(2, secretKey, (AlgorithmParameterSpec)new GOST28147ParameterSpec(instance6.getEncryptionParamSet(), instance6.getIV()));
                        }
                    }
                    else {
                        if (!algorithm.equals((Object)PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC) && !algorithm.equals((Object)PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC)) {
                            throw new OperatorCreationException("unable to create InputDecryptor: algorithm " + algorithm + " unknown.");
                        }
                        final PBEParameter instance7 = PBEParameter.getInstance((Object)encryptionAlg.getParameters());
                        (this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(algorithm.getId())).init(2, (Key)new PBKDF1Key(array, (CharToByteConverter)PasswordConverter.ASCII), new PBEParameterSpec(instance7.getSalt(), instance7.getIterationCount().intValue()));
                    }
                }
                catch (final Exception ex) {
                    throw new OperatorCreationException("unable to create InputDecryptor: " + ex.getMessage(), ex);
                }
                return new InputDecryptor() {
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return InputDecryptorProvider.this.encryptionAlg;
                    }
                    
                    public InputStream getInputStream(final InputStream inputStream) {
                        return new CipherInputStream(inputStream, InputDecryptorProvider.this.cipher);
                    }
                };
            }
        };
    }
}
