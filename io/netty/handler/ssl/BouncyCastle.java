package io.netty.handler.ssl;

import javax.net.ssl.SSLEngine;

final class BouncyCastle
{
    private static final boolean BOUNCY_CASTLE_ON_CLASSPATH;
    
    static boolean isAvailable() {
        return BouncyCastle.BOUNCY_CASTLE_ON_CLASSPATH;
    }
    
    static boolean isInUse(final SSLEngine engine) {
        return engine.getClass().getPackage().getName().startsWith("org.bouncycastle.jsse.provider");
    }
    
    private BouncyCastle() {
    }
    
    static {
        boolean bcOnClasspath = false;
        try {
            Class.forName("org.bouncycastle.jsse.provider.BouncyCastleJsseProvider");
            bcOnClasspath = true;
        }
        catch (final Throwable t) {}
        BOUNCY_CASTLE_ON_CLASSPATH = bcOnClasspath;
    }
}
