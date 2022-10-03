package org.apache.tomcat.util.digester;

import java.security.Permission;
import org.apache.tomcat.util.security.PermissionCheck;
import org.apache.tomcat.util.IntrospectionUtils;

public class EnvironmentPropertySource implements IntrospectionUtils.SecurePropertySource
{
    public String getProperty(final String key) {
        return null;
    }
    
    public String getProperty(final String key, final ClassLoader classLoader) {
        if (classLoader instanceof PermissionCheck) {
            final Permission p = new RuntimePermission("getenv." + key, null);
            if (!((PermissionCheck)classLoader).check(p)) {
                return null;
            }
        }
        return System.getenv(key);
    }
}
