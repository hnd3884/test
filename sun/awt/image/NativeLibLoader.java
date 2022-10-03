package sun.awt.image;

import java.security.AccessController;
import java.security.PrivilegedAction;

class NativeLibLoader
{
    static void loadLibraries() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("awt");
                return null;
            }
        });
    }
}
