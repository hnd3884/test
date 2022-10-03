package org.bouncycastle.jce.provider;

import java.io.OutputStream;
import java.security.KeyStore;

public class JDKPKCS12StoreParameter implements KeyStore.LoadStoreParameter
{
    private OutputStream outputStream;
    private KeyStore.ProtectionParameter protectionParameter;
    private boolean useDEREncoding;
    
    public OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }
    
    public boolean isUseDEREncoding() {
        return this.useDEREncoding;
    }
    
    public void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public void setPassword(final char[] array) {
        this.protectionParameter = new KeyStore.PasswordProtection(array);
    }
    
    public void setProtectionParameter(final KeyStore.ProtectionParameter protectionParameter) {
        this.protectionParameter = protectionParameter;
    }
    
    public void setUseDEREncoding(final boolean useDEREncoding) {
        this.useDEREncoding = useDEREncoding;
    }
}
