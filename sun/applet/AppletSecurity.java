package sun.applet;

import sun.awt.AppContext;
import java.util.Iterator;
import java.security.Permission;
import sun.security.util.SecurityConstants;
import java.security.ProtectionDomain;
import java.security.AccessControlContext;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.util.Enumeration;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.lang.reflect.Field;
import sun.awt.AWTSecurityManager;

public class AppletSecurity extends AWTSecurityManager
{
    private static Field facc;
    private static Field fcontext;
    private HashSet restrictedPackages;
    private boolean inThreadGroupCheck;
    
    public AppletSecurity() {
        this.restrictedPackages = new HashSet();
        this.inThreadGroupCheck = false;
        this.reset();
    }
    
    public void reset() {
        this.restrictedPackages.clear();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                final Enumeration<?> propertyNames = System.getProperties().propertyNames();
                while (propertyNames.hasMoreElements()) {
                    final String s = (String)propertyNames.nextElement();
                    if (s != null && s.startsWith("package.restrict.access.")) {
                        final String property = System.getProperty(s);
                        if (property == null || !property.equalsIgnoreCase("true")) {
                            continue;
                        }
                        AppletSecurity.this.restrictedPackages.add(s.substring(24));
                    }
                }
                return null;
            }
        });
    }
    
    private AppletClassLoader currentAppletClassLoader() {
        final ClassLoader currentClassLoader = this.currentClassLoader();
        if (currentClassLoader == null || currentClassLoader instanceof AppletClassLoader) {
            return (AppletClassLoader)currentClassLoader;
        }
        final Class[] classContext = this.getClassContext();
        for (int i = 0; i < classContext.length; ++i) {
            final ClassLoader classLoader = classContext[i].getClassLoader();
            if (classLoader instanceof AppletClassLoader) {
                return (AppletClassLoader)classLoader;
            }
        }
        for (int j = 0; j < classContext.length; ++j) {
            final ClassLoader classLoader2 = classContext[j].getClassLoader();
            if (classLoader2 instanceof URLClassLoader) {
                final ClassLoader classLoader3 = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        ProtectionDomain[] array;
                        try {
                            final AccessControlContext accessControlContext = (AccessControlContext)AppletSecurity.facc.get(classLoader2);
                            if (accessControlContext == null) {
                                return null;
                            }
                            array = (ProtectionDomain[])AppletSecurity.fcontext.get(accessControlContext);
                            if (array == null) {
                                return null;
                            }
                        }
                        catch (final Exception ex) {
                            throw new UnsupportedOperationException(ex);
                        }
                        for (int i = 0; i < array.length; ++i) {
                            final ClassLoader classLoader = array[i].getClassLoader();
                            if (classLoader instanceof AppletClassLoader) {
                                return classLoader;
                            }
                        }
                        return null;
                    }
                });
                if (classLoader3 != null) {
                    return (AppletClassLoader)classLoader3;
                }
            }
        }
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader instanceof AppletClassLoader) {
            return (AppletClassLoader)contextClassLoader;
        }
        return null;
    }
    
    protected boolean inThreadGroup(final ThreadGroup threadGroup) {
        return this.currentAppletClassLoader() != null && this.getThreadGroup().parentOf(threadGroup);
    }
    
    protected boolean inThreadGroup(final Thread thread) {
        return this.inThreadGroup(thread.getThreadGroup());
    }
    
    @Override
    public void checkAccess(final Thread thread) {
        if (thread.getState() != Thread.State.TERMINATED && !this.inThreadGroup(thread)) {
            this.checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
        }
    }
    
    @Override
    public synchronized void checkAccess(final ThreadGroup threadGroup) {
        if (this.inThreadGroupCheck) {
            this.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
        }
        else {
            try {
                this.inThreadGroupCheck = true;
                if (!this.inThreadGroup(threadGroup)) {
                    this.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
                }
            }
            finally {
                this.inThreadGroupCheck = false;
            }
        }
    }
    
    @Override
    public void checkPackageAccess(final String s) {
        super.checkPackageAccess(s);
        for (final String s2 : this.restrictedPackages) {
            if (s.equals(s2) || s.startsWith(s2 + ".")) {
                this.checkPermission(new RuntimePermission("accessClassInPackage." + s));
            }
        }
    }
    
    @Override
    public void checkAwtEventQueueAccess() {
        final AppContext appContext = AppContext.getAppContext();
        final AppletClassLoader currentAppletClassLoader = this.currentAppletClassLoader();
        if (AppContext.isMainContext(appContext) && currentAppletClassLoader != null) {
            super.checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION);
        }
    }
    
    @Override
    public ThreadGroup getThreadGroup() {
        final AppletClassLoader currentAppletClassLoader = this.currentAppletClassLoader();
        final ThreadGroup threadGroup = (currentAppletClassLoader == null) ? null : currentAppletClassLoader.getThreadGroup();
        if (threadGroup != null) {
            return threadGroup;
        }
        return super.getThreadGroup();
    }
    
    @Override
    public AppContext getAppContext() {
        final AppletClassLoader currentAppletClassLoader = this.currentAppletClassLoader();
        if (currentAppletClassLoader == null) {
            return null;
        }
        final AppContext appContext = currentAppletClassLoader.getAppContext();
        if (appContext == null) {
            throw new SecurityException("Applet classloader has invalid AppContext");
        }
        return appContext;
    }
    
    static {
        AppletSecurity.facc = null;
        AppletSecurity.fcontext = null;
        try {
            (AppletSecurity.facc = URLClassLoader.class.getDeclaredField("acc")).setAccessible(true);
            (AppletSecurity.fcontext = AccessControlContext.class.getDeclaredField("context")).setAccessible(true);
        }
        catch (final NoSuchFieldException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }
}
