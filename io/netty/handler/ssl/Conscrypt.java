package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.InvocationTargetException;
import javax.net.ssl.SSLEngine;
import java.lang.reflect.Method;

final class Conscrypt
{
    private static final Method IS_CONSCRYPT_SSLENGINE;
    
    static boolean isAvailable() {
        return Conscrypt.IS_CONSCRYPT_SSLENGINE != null;
    }
    
    static boolean isEngineSupported(final SSLEngine engine) {
        try {
            return Conscrypt.IS_CONSCRYPT_SSLENGINE != null && (boolean)Conscrypt.IS_CONSCRYPT_SSLENGINE.invoke(null, engine);
        }
        catch (final IllegalAccessException ignore) {
            return false;
        }
        catch (final InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private Conscrypt() {
    }
    
    static {
        Method isConscryptSSLEngine = null;
        Label_0073: {
            if (PlatformDependent.javaVersion() < 8 || PlatformDependent.javaVersion() >= 15) {
                if (!PlatformDependent.isAndroid()) {
                    break Label_0073;
                }
            }
            try {
                final Class<?> providerClass = Class.forName("org.conscrypt.OpenSSLProvider", true, PlatformDependent.getClassLoader(ConscryptAlpnSslEngine.class));
                providerClass.newInstance();
                final Class<?> conscryptClass = Class.forName("org.conscrypt.Conscrypt", true, PlatformDependent.getClassLoader(ConscryptAlpnSslEngine.class));
                isConscryptSSLEngine = conscryptClass.getMethod("isConscrypt", SSLEngine.class);
            }
            catch (final Throwable t) {}
        }
        IS_CONSCRYPT_SSLENGINE = isConscryptSSLEngine;
    }
}
