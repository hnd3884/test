package org.bouncycastle.openssl.jcajce;

import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class JcePEMEncryptorBuilder
{
    private final String algorithm;
    private JcaJceHelper helper;
    private SecureRandom random;
    
    public JcePEMEncryptorBuilder(final String algorithm) {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
        this.algorithm = algorithm;
    }
    
    public JcePEMEncryptorBuilder setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public JcePEMEncryptorBuilder setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public JcePEMEncryptorBuilder setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public PEMEncryptor build(final char[] array) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        final byte[] array2 = new byte[this.algorithm.startsWith("AES-") ? 16 : 8];
        this.random.nextBytes(array2);
        return new PEMEncryptor() {
            public String getAlgorithm() {
                return JcePEMEncryptorBuilder.this.algorithm;
            }
            
            public byte[] getIV() {
                return array2;
            }
            
            public byte[] encrypt(final byte[] array) throws PEMException {
                return PEMUtilities.crypt(true, JcePEMEncryptorBuilder.this.helper, array, array, JcePEMEncryptorBuilder.this.algorithm, array2);
            }
        };
    }
}
