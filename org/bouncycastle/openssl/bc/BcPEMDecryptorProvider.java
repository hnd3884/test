package org.bouncycastle.openssl.bc;

import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PasswordException;
import org.bouncycastle.openssl.PEMDecryptor;
import org.bouncycastle.openssl.PEMDecryptorProvider;

public class BcPEMDecryptorProvider implements PEMDecryptorProvider
{
    private final char[] password;
    
    public BcPEMDecryptorProvider(final char[] password) {
        this.password = password;
    }
    
    public PEMDecryptor get(final String s) {
        return new PEMDecryptor() {
            public byte[] decrypt(final byte[] array, final byte[] array2) throws PEMException {
                if (BcPEMDecryptorProvider.this.password == null) {
                    throw new PasswordException("Password is null, but a password is required");
                }
                return PEMUtilities.crypt(false, array, BcPEMDecryptorProvider.this.password, s, array2);
            }
        };
    }
}
