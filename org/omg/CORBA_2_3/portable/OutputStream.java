package org.omg.CORBA_2_3.portable;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.NO_IMPLEMENT;
import java.io.Serializable;
import java.security.Permission;
import java.io.SerializablePermission;

public abstract class OutputStream extends org.omg.CORBA.portable.OutputStream
{
    private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowOutputStreamSubclass";
    private static final boolean allowSubclass;
    
    private static Void checkPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null && !OutputStream.allowSubclass) {
            securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
        }
        return null;
    }
    
    private OutputStream(final Void void1) {
    }
    
    public OutputStream() {
        this(checkPermission());
    }
    
    public void write_value(final Serializable s) {
        throw new NO_IMPLEMENT();
    }
    
    public void write_value(final Serializable s, final Class clazz) {
        throw new NO_IMPLEMENT();
    }
    
    public void write_value(final Serializable s, final String s2) {
        throw new NO_IMPLEMENT();
    }
    
    public void write_value(final Serializable s, final BoxedValueHelper boxedValueHelper) {
        throw new NO_IMPLEMENT();
    }
    
    public void write_abstract_interface(final Object o) {
        throw new NO_IMPLEMENT();
    }
    
    static {
        allowSubclass = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String property = System.getProperty("jdk.corba.allowOutputStreamSubclass");
                return property != null && !property.equalsIgnoreCase("false");
            }
        });
    }
}
