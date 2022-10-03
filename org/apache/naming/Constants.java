package org.apache.naming;

@Deprecated
public final class Constants
{
    public static final String Package = "org.apache.naming";
    public static final boolean IS_SECURITY_ENABLED;
    
    static {
        IS_SECURITY_ENABLED = (System.getSecurityManager() != null);
    }
}
