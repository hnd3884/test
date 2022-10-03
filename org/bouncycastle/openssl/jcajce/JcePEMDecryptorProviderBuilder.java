package org.bouncycastle.openssl.jcajce;

import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PasswordException;
import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class JcePEMDecryptorProviderBuilder
{
    private JcaJceHelper helper;
    
    public JcePEMDecryptorProviderBuilder() {
        this.helper = (JcaJceHelper)new DefaultJcaJceHelper();
    }
    
    public JcePEMDecryptorProviderBuilder setProvider(final Provider provider) {
        this.helper = (JcaJceHelper)new ProviderJcaJceHelper(provider);
        return this;
    }
    
    public JcePEMDecryptorProviderBuilder setProvider(final String s) {
        this.helper = (JcaJceHelper)new NamedJcaJceHelper(s);
        return this;
    }
    
    public PEMDecryptorProvider build(final char[] array) {
        return new PEMDecryptorProvider() {
            public PEMDecryptor get(final String s) {
                return new PEMDecryptor() {
                    public byte[] decrypt(final byte[] array, final byte[] array2) throws PEMException {
                        if (array == null) {
                            throw new PasswordException("Password is null, but a password is required");
                        }
                        return PEMUtilities.crypt(false, JcePEMDecryptorProviderBuilder.this.helper, array, array, s, array2);
                    }
                };
            }
        };
    }
}
