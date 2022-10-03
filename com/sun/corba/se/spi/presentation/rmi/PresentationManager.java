package com.sun.corba.se.spi.presentation.rmi;

import org.omg.CORBA.Object;
import java.util.Map;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import javax.rmi.CORBA.Tie;
import java.lang.reflect.Method;

public interface PresentationManager
{
    ClassData getClassData(final Class p0);
    
    DynamicMethodMarshaller getDynamicMethodMarshaller(final Method p0);
    
    StubFactoryFactory getStubFactoryFactory(final boolean p0);
    
    void setStubFactoryFactory(final boolean p0, final StubFactoryFactory p1);
    
    Tie getTie();
    
    boolean useDynamicStubs();
    
    public interface ClassData
    {
        Class getMyClass();
        
        IDLNameTranslator getIDLNameTranslator();
        
        String[] getTypeIds();
        
        InvocationHandlerFactory getInvocationHandlerFactory();
        
        Map getDictionary();
    }
    
    public interface StubFactory
    {
        org.omg.CORBA.Object makeStub();
        
        String[] getTypeIds();
    }
    
    public interface StubFactoryFactory
    {
        String getStubName(final String p0);
        
        StubFactory createStubFactory(final String p0, final boolean p1, final String p2, final Class p3, final ClassLoader p4);
        
        Tie getTie(final Class p0);
        
        boolean createsDynamicStubs();
    }
}
