package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.Object;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.security.Permission;
import java.io.SerializablePermission;

public abstract class StubFactoryDynamicBase extends StubFactoryBase
{
    protected final ClassLoader loader;
    
    private static Void checkPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
        }
        return null;
    }
    
    private StubFactoryDynamicBase(final Void void1, final PresentationManager.ClassData classData, final ClassLoader loader) {
        super(classData);
        if (loader == null) {
            ClassLoader loader2 = Thread.currentThread().getContextClassLoader();
            if (loader2 == null) {
                loader2 = ClassLoader.getSystemClassLoader();
            }
            this.loader = loader2;
        }
        else {
            this.loader = loader;
        }
    }
    
    public StubFactoryDynamicBase(final PresentationManager.ClassData classData, final ClassLoader classLoader) {
        this(checkPermission(), classData, classLoader);
    }
    
    @Override
    public abstract org.omg.CORBA.Object makeStub();
}
