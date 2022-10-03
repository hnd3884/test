package com.sun.jndi.ldap;

import sun.misc.SharedSecrets;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

final class VersionHelper12 extends VersionHelper
{
    private static final String TRUST_URL_CODEBASE_PROPERTY = "com.sun.jndi.ldap.object.trustURLCodebase";
    private static final String trustURLCodebase;
    
    @Override
    ClassLoader getURLClassLoader(final String[] array) throws MalformedURLException {
        final ClassLoader contextClassLoader = this.getContextClassLoader();
        if (array != null && "true".equalsIgnoreCase(VersionHelper12.trustURLCodebase)) {
            return URLClassLoader.newInstance(VersionHelper.getUrlArray(array), contextClassLoader);
        }
        return contextClassLoader;
    }
    
    @Override
    Class<?> loadClass(final String s) throws ClassNotFoundException {
        return Class.forName(s, true, this.getContextClassLoader());
    }
    
    private ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    @Override
    Thread createThread(final Runnable runnable) {
        return AccessController.doPrivileged((PrivilegedAction<Thread>)new PrivilegedAction<Thread>() {
            final /* synthetic */ AccessControlContext val$acc = AccessController.getContext();
            
            @Override
            public Thread run() {
                return SharedSecrets.getJavaLangAccess().newThreadWithAcc(runnable, this.val$acc);
            }
        });
    }
    
    static {
        trustURLCodebase = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
            }
        });
    }
}
