package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class FontManagerNativeLibrary
{
    public static void load() {
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                System.loadLibrary("awt");
                if (System.getProperty("os.name").startsWith("Windows")) {
                    System.loadLibrary("freetype");
                }
                System.loadLibrary("fontmanager");
                return null;
            }
        });
    }
}
