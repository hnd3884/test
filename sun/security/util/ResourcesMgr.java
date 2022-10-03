package sun.security.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;

public class ResourcesMgr
{
    private static ResourceBundle bundle;
    private static ResourceBundle altBundle;
    
    public static String getString(final String s) {
        if (ResourcesMgr.bundle == null) {
            ResourcesMgr.bundle = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
                @Override
                public ResourceBundle run() {
                    return ResourceBundle.getBundle("sun.security.util.Resources");
                }
            });
        }
        return ResourcesMgr.bundle.getString(s);
    }
    
    public static String getString(final String s, final String s2) {
        if (ResourcesMgr.altBundle == null) {
            ResourcesMgr.altBundle = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
                @Override
                public ResourceBundle run() {
                    return ResourceBundle.getBundle(s2);
                }
            });
        }
        return ResourcesMgr.altBundle.getString(s);
    }
}
