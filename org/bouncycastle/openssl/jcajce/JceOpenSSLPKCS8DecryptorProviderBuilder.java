package org.bouncycastle.openssl.jcajce;

import java.security.AlgorithmParameters;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import java.security.GeneralSecurityException;
import java.io.IOException;
import org.bouncycastle.operator.OperatorCreationException;
import javax.crypto.CipherInputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.util.Strings;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import java.security.Key;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class JceOpenSSLPKCS8DecryptorProviderBuilder
{
    private JcaJceHelper helper;
    
    public JceOpenSSLPKCS8DecryptorProviderBuilder() {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public InputDecryptorProvider build(final char[] array) throws OperatorCreationException {
        return new InputDecryptorProvider() {
            public InputDecryptor get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                try {
                    Cipher cipher;
                    if (PEMUtilities.isPKCS5Scheme2(algorithmIdentifier.getAlgorithm())) {
                        final PBES2Parameters instance = PBES2Parameters.getInstance((Object)algorithmIdentifier.getParameters());
                        final KeyDerivationFunc keyDerivationFunc = instance.getKeyDerivationFunc();
                        final EncryptionScheme encryptionScheme = instance.getEncryptionScheme();
                        final PBKDF2Params pbkdf2Params = (PBKDF2Params)keyDerivationFunc.getParameters();
                        final int intValue = pbkdf2Params.getIterationCount().intValue();
                        final byte[] salt = pbkdf2Params.getSalt();
                        final String id = encryptionScheme.getAlgorithm().getId();
                        SecretKey secretKey;
                        if (PEMUtilities.isHmacSHA1(pbkdf2Params.getPrf())) {
                            secretKey = PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, id, array, salt, intValue);
                        }
                        else {
                            secretKey = PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, id, array, salt, intValue, pbkdf2Params.getPrf());
                        }
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(id);
                        final AlgorithmParameters algorithmParameters = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createAlgorithmParameters(id);
                        algorithmParameters.init(encryptionScheme.getParameters().toASN1Primitive().getEncoded());
                        cipher.init(2, secretKey, algorithmParameters);
                    }
                    else if (PEMUtilities.isPKCS12(algorithmIdentifier.getAlgorithm())) {
                        final PKCS12PBEParams instance2 = PKCS12PBEParams.getInstance((Object)algorithmIdentifier.getParameters());
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(algorithmIdentifier.getAlgorithm().getId());
                        cipher.init(2, (Key)new PKCS12KeyWithParameters(array, instance2.getIV(), instance2.getIterations().intValue()));
                    }
                    else {
                        if (!PEMUtilities.isPKCS5Scheme1(algorithmIdentifier.getAlgorithm())) {
                            throw new PEMException("Unknown algorithm: " + algorithmIdentifier.getAlgorithm());
                        }
                        final PBEParameter instance3 = PBEParameter.getInstance((Object)algorithmIdentifier.getParameters());
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(algorithmIdentifier.getAlgorithm().getId());
                        cipher.init(2, (Key)new PBKDF1KeyWithParameters(array, (CharToByteConverter)new CharToByteConverter() {
                            public String getType() {
                                return "ASCII";
                            }
                            
                            public byte[] convert(final char[] array) {
                                return Strings.toByteArray(array);
                            }
                        }, instance3.getSalt(), instance3.getIterationCount().intValue()));
                    }
                    return new InputDecryptor() {
                        public AlgorithmIdentifier getAlgorithmIdentifier() {
                            return algorithmIdentifier;
                        }
                        
                        public InputStream getInputStream(final InputStream inputStream) {
                            return new CipherInputStream(inputStream, cipher);
                        }
                    };
                }
                catch (final IOException ex) {
                    throw new OperatorCreationException(algorithmIdentifier.getAlgorithm() + " not available: " + ex.getMessage(), ex);
                }
                catch (final GeneralSecurityException ex2) {
                    throw new OperatorCreationException(algorithmIdentifier.getAlgorithm() + " not available: " + ex2.getMessage(), ex2);
                }
            }
        };
    }
}
