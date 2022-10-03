package sun.text.normalizer;

import java.util.MissingResourceException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;

public final class ICUData
{
    private static InputStream getStream(final Class<ICUData> clazz, final String s, final boolean b) {
        InputStream resourceAsStream;
        if (System.getSecurityManager() != null) {
            resourceAsStream = AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    return clazz.getResourceAsStream(s);
                }
            });
        }
        else {
            resourceAsStream = clazz.getResourceAsStream(s);
        }
        if (resourceAsStream == null && b) {
            throw new MissingResourceException("could not locate data", clazz.getPackage().getName(), s);
        }
        return resourceAsStream;
    }
    
    public static InputStream getStream(final String s) {
        return getStream(ICUData.class, s, false);
    }
    
    public static InputStream getRequiredStream(final String s) {
        return getStream(ICUData.class, s, true);
    }
}
