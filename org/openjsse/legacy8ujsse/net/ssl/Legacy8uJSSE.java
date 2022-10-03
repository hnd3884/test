package org.openjsse.legacy8ujsse.net.ssl;

import java.security.Provider;

public final class Legacy8uJSSE extends org.openjsse.legacy8ujsse.sun.security.ssl.Legacy8uJSSE
{
    private static final long serialVersionUID = 3231825739635378733L;
    
    public Legacy8uJSSE() {
    }
    
    public Legacy8uJSSE(final Provider cryptoProvider) {
        super(cryptoProvider);
    }
    
    public Legacy8uJSSE(final String cryptoProvider) {
        super(cryptoProvider);
    }
    
    public static synchronized boolean isFIPS() {
        return org.openjsse.legacy8ujsse.sun.security.ssl.Legacy8uJSSE.isFIPS();
    }
    
    public static synchronized void install() {
    }
}
