package com.sun.corba.se.impl.presentation.rmi;

import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

public class StubFactoryFactoryProxyImpl extends StubFactoryFactoryDynamicBase
{
    @Override
    public PresentationManager.StubFactory makeDynamicStubFactory(final PresentationManager presentationManager, final PresentationManager.ClassData classData, final ClassLoader classLoader) {
        return AccessController.doPrivileged((PrivilegedAction<PresentationManager.StubFactory>)new PrivilegedAction<StubFactoryProxyImpl>() {
            @Override
            public StubFactoryProxyImpl run() {
                return new StubFactoryProxyImpl(classData, classLoader);
            }
        });
    }
}
