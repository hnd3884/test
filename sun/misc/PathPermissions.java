package sun.misc;

import java.util.Enumeration;
import java.security.AccessController;
import java.io.FilePermission;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.PropertyPermission;
import sun.security.util.SecurityConstants;
import java.security.Permission;
import java.net.URL;
import java.security.Permissions;
import java.io.File;
import java.security.PermissionCollection;

class PathPermissions extends PermissionCollection
{
    private static final long serialVersionUID = 8133287259134945693L;
    private File[] path;
    private Permissions perms;
    URL codeBase;
    
    PathPermissions(final File[] path) {
        this.path = path;
        this.perms = null;
        this.codeBase = null;
    }
    
    URL getCodeBase() {
        return this.codeBase;
    }
    
    @Override
    public void add(final Permission permission) {
        throw new SecurityException("attempt to add a permission");
    }
    
    private synchronized void init() {
        if (this.perms != null) {
            return;
        }
        (this.perms = new Permissions()).add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
        this.perms.add(new PropertyPermission("java.*", "read"));
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                for (int i = 0; i < PathPermissions.this.path.length; ++i) {
                    final File file = PathPermissions.this.path[i];
                    String s;
                    try {
                        s = file.getCanonicalPath();
                    }
                    catch (final IOException ex) {
                        s = file.getAbsolutePath();
                    }
                    if (i == 0) {
                        PathPermissions.this.codeBase = Launcher.getFileURL(new File(s));
                    }
                    if (file.isDirectory()) {
                        if (s.endsWith(File.separator)) {
                            PathPermissions.this.perms.add(new FilePermission(s + "-", "read"));
                        }
                        else {
                            PathPermissions.this.perms.add(new FilePermission(s + File.separator + "-", "read"));
                        }
                    }
                    else {
                        final int lastIndex = s.lastIndexOf(File.separatorChar);
                        if (lastIndex != -1) {
                            PathPermissions.this.perms.add(new FilePermission(s.substring(0, lastIndex + 1) + "-", "read"));
                        }
                    }
                }
                return null;
            }
        });
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (this.perms == null) {
            this.init();
        }
        return this.perms.implies(permission);
    }
    
    @Override
    public Enumeration<Permission> elements() {
        if (this.perms == null) {
            this.init();
        }
        synchronized (this.perms) {
            return this.perms.elements();
        }
    }
    
    @Override
    public String toString() {
        if (this.perms == null) {
            this.init();
        }
        return this.perms.toString();
    }
}
