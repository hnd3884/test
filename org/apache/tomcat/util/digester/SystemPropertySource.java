package org.apache.tomcat.util.digester;

import java.security.Permission;
import java.util.PropertyPermission;
import org.apache.tomcat.util.security.PermissionCheck;
import org.apache.tomcat.util.IntrospectionUtils;

public class SystemPropertySource implements IntrospectionUtils.SecurePropertySource
{
    public String getProperty(final String key) {
        return this.getProperty(key, null);
    }
    
    public String getProperty(final String key, final ClassLoader classLoader) {
        if (classLoader instanceof PermissionCheck) {
            final Permission p = new PropertyPermission(key, "read");
            if (!((PermissionCheck)classLoader).check(p)) {
                return null;
            }
        }
        return System.getProperty(key);
    }
}
