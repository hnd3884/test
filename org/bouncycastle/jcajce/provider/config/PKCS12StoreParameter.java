package org.bouncycastle.jcajce.provider.config;

import java.security.KeyStore;
import java.io.OutputStream;

public class PKCS12StoreParameter extends org.bouncycastle.jcajce.PKCS12StoreParameter
{
    public PKCS12StoreParameter(final OutputStream outputStream, final char[] array) {
        super(outputStream, array, false);
    }
    
    public PKCS12StoreParameter(final OutputStream outputStream, final KeyStore.ProtectionParameter protectionParameter) {
        super(outputStream, protectionParameter, false);
    }
    
    public PKCS12StoreParameter(final OutputStream outputStream, final char[] array, final boolean b) {
        super(outputStream, new KeyStore.PasswordProtection(array), b);
    }
    
    public PKCS12StoreParameter(final OutputStream outputStream, final KeyStore.ProtectionParameter protectionParameter, final boolean b) {
        super(outputStream, protectionParameter, b);
    }
}
