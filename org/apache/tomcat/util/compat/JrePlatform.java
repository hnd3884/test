package org.apache.tomcat.util.compat;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class JrePlatform
{
    private static final String OS_NAME_PROPERTY = "os.name";
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    public static final boolean IS_WINDOWS;
    
    static {
        String osName;
        if (System.getSecurityManager() == null) {
            osName = System.getProperty("os.name");
        }
        else {
            osName = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("os.name");
                }
            });
        }
        IS_WINDOWS = osName.startsWith("Windows");
    }
}
