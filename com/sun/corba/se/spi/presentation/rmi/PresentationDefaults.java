package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.impl.presentation.rmi.StubFactoryStaticImpl;
import com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryProxyImpl;
import com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryStaticImpl;

public abstract class PresentationDefaults
{
    private static StubFactoryFactoryStaticImpl staticImpl;
    
    private PresentationDefaults() {
    }
    
    public static synchronized PresentationManager.StubFactoryFactory getStaticStubFactoryFactory() {
        if (PresentationDefaults.staticImpl == null) {
            PresentationDefaults.staticImpl = new StubFactoryFactoryStaticImpl();
        }
        return PresentationDefaults.staticImpl;
    }
    
    public static PresentationManager.StubFactoryFactory getProxyStubFactoryFactory() {
        return new StubFactoryFactoryProxyImpl();
    }
    
    public static PresentationManager.StubFactory makeStaticStubFactory(final Class clazz) {
        return new StubFactoryStaticImpl(clazz);
    }
    
    static {
        PresentationDefaults.staticImpl = null;
    }
}
