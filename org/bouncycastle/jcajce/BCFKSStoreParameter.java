package org.bouncycastle.jcajce;

import java.io.OutputStream;
import org.bouncycastle.crypto.util.PBKDFConfig;
import java.security.KeyStore;

public class BCFKSStoreParameter implements KeyStore.LoadStoreParameter
{
    private final KeyStore.ProtectionParameter protectionParameter;
    private final PBKDFConfig storeConfig;
    private OutputStream out;
    
    public BCFKSStoreParameter(final OutputStream outputStream, final PBKDFConfig pbkdfConfig, final char[] array) {
        this(outputStream, pbkdfConfig, new KeyStore.PasswordProtection(array));
    }
    
    public BCFKSStoreParameter(final OutputStream out, final PBKDFConfig storeConfig, final KeyStore.ProtectionParameter protectionParameter) {
        this.out = out;
        this.storeConfig = storeConfig;
        this.protectionParameter = protectionParameter;
    }
    
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }
    
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    public PBKDFConfig getStorePBKDFConfig() {
        return this.storeConfig;
    }
}
