package sun.management;

import java.security.Permission;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;
import java.lang.management.ManagementPermission;

public class Util
{
    private static final String[] EMPTY_STRING_ARRAY;
    private static ManagementPermission monitorPermission;
    private static ManagementPermission controlPermission;
    
    private Util() {
    }
    
    static RuntimeException newException(final Exception ex) {
        throw new RuntimeException(ex);
    }
    
    static String[] toStringArray(final List<String> list) {
        return list.toArray(Util.EMPTY_STRING_ARRAY);
    }
    
    public static ObjectName newObjectName(final String s, final String s2) {
        return newObjectName(s + ",name=" + s2);
    }
    
    public static ObjectName newObjectName(final String s) {
        try {
            return ObjectName.getInstance(s);
        }
        catch (final MalformedObjectNameException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    static void checkAccess(final ManagementPermission managementPermission) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(managementPermission);
        }
    }
    
    static void checkMonitorAccess() throws SecurityException {
        checkAccess(Util.monitorPermission);
    }
    
    static void checkControlAccess() throws SecurityException {
        checkAccess(Util.controlPermission);
    }
    
    static {
        EMPTY_STRING_ARRAY = new String[0];
        Util.monitorPermission = new ManagementPermission("monitor");
        Util.controlPermission = new ManagementPermission("control");
    }
}
