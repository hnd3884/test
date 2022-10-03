package org.openjsse.net.ssl;

import java.security.Provider;

public final class OpenJSSE extends org.openjsse.sun.security.ssl.OpenJSSE
{
    private static final long serialVersionUID = 3231825739635378733L;
    
    public OpenJSSE() {
    }
    
    public OpenJSSE(final Provider cryptoProvider) {
        super(cryptoProvider);
    }
    
    public OpenJSSE(final String cryptoProvider) {
        super(cryptoProvider);
    }
    
    public static synchronized boolean isFIPS() {
        return org.openjsse.sun.security.ssl.OpenJSSE.isFIPS();
    }
    
    public static synchronized void install() {
    }
}
