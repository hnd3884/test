package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

public class StubFactoryProxyImpl extends StubFactoryDynamicBase
{
    public StubFactoryProxyImpl(final PresentationManager.ClassData classData, final ClassLoader classLoader) {
        super(classData, classLoader);
    }
    
    @Override
    public org.omg.CORBA.Object makeStub() {
        final InvocationHandlerFactory invocationHandlerFactory = this.classData.getInvocationHandlerFactory();
        final LinkedInvocationHandler linkedInvocationHandler = (LinkedInvocationHandler)invocationHandlerFactory.getInvocationHandler();
        final DynamicStub dynamicStub = (DynamicStub)Proxy.newProxyInstance(this.loader, invocationHandlerFactory.getProxyInterfaces(), linkedInvocationHandler);
        linkedInvocationHandler.setProxy((Proxy)dynamicStub);
        return dynamicStub;
    }
}
