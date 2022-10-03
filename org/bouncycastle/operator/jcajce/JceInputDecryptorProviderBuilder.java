package org.bouncycastle.operator.jcajce;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import javax.crypto.CipherInputStream;
import java.io.InputStream;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1OctetString;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.Cipher;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class JceInputDecryptorProviderBuilder
{
    private JcaJceHelper helper;
    
    public JceInputDecryptorProviderBuilder() {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JceInputDecryptorProviderBuilder setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public JceInputDecryptorProviderBuilder setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public InputDecryptorProvider build(final byte[] array) {
        return new InputDecryptorProvider() {
            private Cipher cipher;
            private AlgorithmIdentifier encryptionAlg;
            final /* synthetic */ byte[] val$encKeyBytes = Arrays.clone(array);
            
            public InputDecryptor get(final AlgorithmIdentifier encryptionAlg) throws OperatorCreationException {
                this.encryptionAlg = encryptionAlg;
                final ASN1ObjectIdentifier algorithm = encryptionAlg.getAlgorithm();
                try {
                    this.cipher = JceInputDecryptorProviderBuilder.this.helper.createCipher(algorithm.getId());
                    final SecretKeySpec secretKeySpec = new SecretKeySpec(this.val$encKeyBytes, algorithm.getId());
                    final ASN1Encodable parameters = encryptionAlg.getParameters();
                    if (parameters instanceof ASN1OctetString) {
                        this.cipher.init(2, secretKeySpec, new IvParameterSpec(ASN1OctetString.getInstance((Object)parameters).getOctets()));
                    }
                    else {
                        final GOST28147Parameters instance = GOST28147Parameters.getInstance((Object)parameters);
                        this.cipher.init(2, secretKeySpec, (AlgorithmParameterSpec)new GOST28147ParameterSpec(instance.getEncryptionParamSet(), instance.getIV()));
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
