package org.apache.tomcat.util.security;

import java.security.PrivilegedAction;

public class PrivilegedSetTccl implements PrivilegedAction<Void>
{
    private final ClassLoader cl;
    private final Thread t;
    
    public PrivilegedSetTccl(final ClassLoader cl) {
        this(Thread.currentThread(), cl);
    }
    
    public PrivilegedSetTccl(final Thread t, final ClassLoader cl) {
        this.t = t;
        this.cl = cl;
    }
    
    @Override
    public Void run() {
        this.t.setContextClassLoader(this.cl);
        return null;
    }
}
