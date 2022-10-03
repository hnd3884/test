package com.sun.corba.se.impl.presentation.rmi;

import java.io.ObjectStreamException;
import java.lang.reflect.Proxy;
import java.io.Serializable;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl;
import java.security.AccessController;
import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandler;
import java.security.PrivilegedAction;
import com.sun.corba.se.spi.orbutil.proxy.DelegateInvocationHandlerImpl;
import java.lang.reflect.InvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;

public class InvocationHandlerFactoryImpl implements InvocationHandlerFactory
{
    private final PresentationManager.ClassData classData;
    private final PresentationManager pm;
    private Class[] proxyInterfaces;
    
    public InvocationHandlerFactoryImpl(final PresentationManager pm, final PresentationManager.ClassData classData) {
        this.classData = classData;
        this.pm = pm;
        final Class[] interfaces = classData.getIDLNameTranslator().getInterfaces();
        this.proxyInterfaces = new Class[interfaces.length + 1];
        for (int i = 0; i < interfaces.length; ++i) {
            this.proxyInterfaces[i] = interfaces[i];
        }
        this.proxyInterfaces[interfaces.length] = DynamicStub.class;
    }
    
    @Override
    public InvocationHandler getInvocationHandler() {
        return this.getInvocationHandler(new DynamicStubImpl(this.classData.getTypeIds()));
    }
    
    InvocationHandler getInvocationHandler(final DynamicStub dynamicStub) {
        final InvocationHandler create = DelegateInvocationHandlerImpl.create(dynamicStub);
        final StubInvocationHandlerImpl defaultHandler = new StubInvocationHandlerImpl(this.pm, this.classData, dynamicStub);
        final CustomCompositeInvocationHandlerImpl customCompositeInvocationHandlerImpl = new CustomCompositeInvocationHandlerImpl(dynamicStub);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                customCompositeInvocationHandlerImpl.addInvocationHandler(DynamicStub.class, create);
                customCompositeInvocationHandlerImpl.addInvocationHandler(org.omg.CORBA.Object.class, create);
                customCompositeInvocationHandlerImpl.addInvocationHandler(Object.class, create);
                return null;
            }
        });
        customCompositeInvocationHandlerImpl.setDefaultHandler(defaultHandler);
        return customCompositeInvocationHandlerImpl;
    }
    
    @Override
    public Class[] getProxyInterfaces() {
        return this.proxyInterfaces;
    }
    
    private class CustomCompositeInvocationHandlerImpl extends CompositeInvocationHandlerImpl implements LinkedInvocationHandler, Serializable
    {
        private transient DynamicStub stub;
        
        @Override
        public void setProxy(final Proxy proxy) {
            ((DynamicStubImpl)this.stub).setSelf((DynamicStub)proxy);
        }
        
        @Override
        public Proxy getProxy() {
            return (Proxy)((DynamicStubImpl)this.stub).getSelf();
        }
        
        public CustomCompositeInvocationHandlerImpl(final DynamicStub stub) {
            this.stub = stub;
        }
        
        public Object writeReplace() throws ObjectStreamException {
            return this.stub;
        }
    }
}
