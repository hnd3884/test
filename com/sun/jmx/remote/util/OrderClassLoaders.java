package com.sun.jmx.remote.util;

import sun.reflect.misc.ReflectUtil;

public class OrderClassLoaders extends ClassLoader
{
    private ClassLoader cl2;
    
    public OrderClassLoaders(final ClassLoader classLoader, final ClassLoader cl2) {
        super(classLoader);
        this.cl2 = cl2;
    }
    
    @Override
    protected Class<?> loadClass(final String s, final boolean b) throws ClassNotFoundException {
        ReflectUtil.checkPackageAccess(s);
        try {
            return super.loadClass(s, b);
        }
        catch (final ClassNotFoundException ex) {
            if (this.cl2 != null) {
                return this.cl2.loadClass(s);
            }
            throw ex;
        }
    }
}
