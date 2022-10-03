package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.Object;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

public class StubFactoryStaticImpl extends StubFactoryBase
{
    private Class stubClass;
    
    public StubFactoryStaticImpl(final Class stubClass) {
        super(null);
        this.stubClass = stubClass;
    }
    
    @Override
    public org.omg.CORBA.Object makeStub() {
        org.omg.CORBA.Object object;
        try {
            object = this.stubClass.newInstance();
        }
        catch (final InstantiationException ex) {
            throw new RuntimeException(ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new RuntimeException(ex2);
        }
        return object;
    }
}
