package org.omg.CORBA_2_3.portable;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.NO_IMPLEMENT;
import java.io.Serializable;
import java.security.Permission;
import java.io.SerializablePermission;

public abstract class InputStream extends org.omg.CORBA.portable.InputStream
{
    private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowInputStreamSubclass";
    private static final boolean allowSubclass;
    
    private static Void checkPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null && !InputStream.allowSubclass) {
            securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
        }
        return null;
    }
    
    private InputStream(final Void void1) {
    }
    
    public InputStream() {
        this(checkPermission());
    }
    
    public Serializable read_value() {
        throw new NO_IMPLEMENT();
    }
    
    public Serializable read_value(final Class clazz) {
        throw new NO_IMPLEMENT();
    }
    
    public Serializable read_value(final BoxedValueHelper boxedValueHelper) {
        throw new NO_IMPLEMENT();
    }
    
    public Serializable read_value(final String s) {
        throw new NO_IMPLEMENT();
    }
    
    public Serializable read_value(final Serializable s) {
        throw new NO_IMPLEMENT();
    }
    
    public Object read_abstract_interface() {
        throw new NO_IMPLEMENT();
    }
    
    public Object read_abstract_interface(final Class clazz) {
        throw new NO_IMPLEMENT();
    }
    
    static {
        allowSubclass = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String property = System.getProperty("jdk.corba.allowInputStreamSubclass");
                return property != null && !property.equalsIgnoreCase("false");
            }
        });
    }
}
