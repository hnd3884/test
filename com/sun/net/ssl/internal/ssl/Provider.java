package com.sun.net.ssl.internal.ssl;

import sun.security.ssl.SunJSSE;

public final class Provider extends SunJSSE
{
    private static final long serialVersionUID = 3231825739635378733L;
    
    public Provider() {
    }
    
    public Provider(final java.security.Provider provider) {
        super(provider);
    }
    
    public Provider(final String s) {
        super(s);
    }
    
    public static synchronized boolean isFIPS() {
        return SunJSSE.isFIPS();
    }
    
    public static synchronized void install() {
    }
}
