package org.apache.tomcat.dbcp.pool2.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class SecurityManagerCallStack implements CallStack
{
    private final String messageFormat;
    private final DateFormat dateFormat;
    private final PrivateSecurityManager securityManager;
    private volatile Snapshot snapshot;
    
    public SecurityManagerCallStack(final String messageFormat, final boolean useTimestamp) {
        this.messageFormat = messageFormat;
        this.dateFormat = (useTimestamp ? new SimpleDateFormat(messageFormat) : null);
        this.securityManager = AccessController.doPrivileged((PrivilegedAction<PrivateSecurityManager>)new PrivilegedAction<PrivateSecurityManager>() {
            @Override
            public PrivateSecurityManager run() {
                return new PrivateSecurityManager();
            }
        });
    }
    
    @Override
    public boolean printStackTrace(final PrintWriter writer) {
        final Snapshot snapshotRef = this.snapshot;
        if (snapshotRef == null) {
            return false;
        }
        String message;
        if (this.dateFormat == null) {
            message = this.messageFormat;
        }
        else {
            synchronized (this.dateFormat) {
                message = this.dateFormat.format(snapshotRef.timestampMillis);
            }
        }
        writer.println(message);
        for (final WeakReference<Class<?>> reference : snapshotRef.stack) {
            writer.println(reference.get());
        }
        return true;
    }
    
    @Override
    public void fillInStackTrace() {
        this.snapshot = new Snapshot(this.securityManager.getCallStack());
    }
    
    @Override
    public void clear() {
        this.snapshot = null;
    }
    
    private static class PrivateSecurityManager extends SecurityManager
    {
        private List<WeakReference<Class<?>>> getCallStack() {
            final Class<?>[] classes = this.getClassContext();
            final List<WeakReference<Class<?>>> stack = new ArrayList<WeakReference<Class<?>>>(classes.length);
            for (final Class<?> klass : classes) {
                stack.add(new WeakReference<Class<?>>(klass));
            }
            return stack;
        }
    }
    
    private static class Snapshot
    {
        private final long timestampMillis;
        private final List<WeakReference<Class<?>>> stack;
        
        private Snapshot(final List<WeakReference<Class<?>>> stack) {
            this.timestampMillis = System.currentTimeMillis();
            this.stack = stack;
        }
    }
}
