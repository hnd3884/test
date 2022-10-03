package sun.security.util;

import java.security.Security;
import java.security.AccessController;

public class SecurityProperties
{
    public static String privilegedGetOverridable(final String s) {
        if (System.getSecurityManager() == null) {
            return getOverridableProperty(s);
        }
        return AccessController.doPrivileged(() -> getOverridableProperty(s2));
    }
    
    private static String getOverridableProperty(final String s) {
        final String property = System.getProperty(s);
        if (property == null) {
            return Security.getProperty(s);
        }
        return property;
    }
    
    public static boolean includedInExceptions(final String s) {
        final String privilegedGetOverridable = privilegedGetOverridable("jdk.includeInExceptions");
        if (privilegedGetOverridable == null) {
            return false;
        }
        final String[] split = privilegedGetOverridable.split(",");
        for (int length = split.length, i = 0; i < length; ++i) {
            if (split[i].trim().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
