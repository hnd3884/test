package com.sun.media.sound;

import java.util.ServiceLoader;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.security.Permission;
import javax.sound.sampled.AudioPermission;

final class JSSecurityManager
{
    private JSSecurityManager() {
    }
    
    private static boolean hasSecurityManager() {
        return System.getSecurityManager() != null;
    }
    
    static void checkRecordPermission() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AudioPermission("record"));
        }
    }
    
    static void loadProperties(final Properties properties, final String s) {
        if (hasSecurityManager()) {
            try {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        loadPropertiesImpl(properties, s);
                        return null;
                    }
                });
            }
            catch (final Exception ex) {
                loadPropertiesImpl(properties, s);
            }
        }
        else {
            loadPropertiesImpl(properties, s);
        }
    }
    
    private static void loadPropertiesImpl(final Properties properties, final String s) {
        final String property = System.getProperty("java.home");
        try {
            if (property == null) {
                throw new Error("Can't find java.home ??");
            }
            final FileInputStream fileInputStream = new FileInputStream(new File(new File(property, "lib"), s).getCanonicalPath());
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            try {
                properties.load(bufferedInputStream);
            }
            finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
        }
        catch (final Throwable t) {}
    }
    
    static Thread createThread(final Runnable runnable, final String name, final boolean daemon, final int priority, final boolean b) {
        final Thread thread = new Thread(runnable);
        if (name != null) {
            thread.setName(name);
        }
        thread.setDaemon(daemon);
        if (priority >= 0) {
            thread.setPriority(priority);
        }
        if (b) {
            thread.start();
        }
        return thread;
    }
    
    static synchronized <T> List<T> getProviders(final Class<T> clazz) {
        final ArrayList list = new ArrayList(7);
        final Iterator iterator = AccessController.doPrivileged((PrivilegedAction<Iterator>)new PrivilegedAction<Iterator<T>>() {
            @Override
            public Iterator<T> run() {
                return ServiceLoader.load(clazz).iterator();
            }
        });
        while (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return iterator.hasNext();
            }
        })) {
            try {
                final Object next = iterator.next();
                if (!clazz.isInstance(next)) {
                    continue;
                }
                list.add(0, next);
            }
            catch (final Throwable t) {}
        }
        return list;
    }
}
