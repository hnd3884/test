package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore;

public class PKCS12StoreParameter implements KeyStore.LoadStoreParameter
{
    private final OutputStream out;
    private final KeyStore.ProtectionParameter protectionParameter;
    private final boolean forDEREncoding;
    
    public PKCS12StoreParameter(final OutputStream outputStream, final char[] array) {
        this(outputStream, array, false);
    }
    
    public PKCS12StoreParameter(final OutputStream outputStream, final KeyStore.ProtectionParameter protectionParameter) {
        this(outputStream, protectionParameter, false);
    }
    
    public PKCS12StoreParameter(final OutputStream outputStream, final char[] array, final boolean b) {
        this(outputStream, new KeyStore.PasswordProtection(array), b);
    }
    
    public PKCS12StoreParameter(final OutputStream out, final KeyStore.ProtectionParameter protectionParameter, final boolean forDEREncoding) {
        this.out = out;
        this.protectionParameter = protectionParameter;
        this.forDEREncoding = forDEREncoding;
    }
    
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }
    
    public boolean isForDEREncoding() {
        return this.forDEREncoding;
    }
}
