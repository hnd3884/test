package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.portable.ServantObject;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA.SystemException;
import javax.rmi.CORBA.Util;
import java.lang.reflect.Method;
import com.sun.corba.se.pept.transport.ContactInfoList;
import org.omg.CORBA.portable.Delegate;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.security.Permission;
import java.lang.reflect.Proxy;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;

public final class StubInvocationHandlerImpl implements LinkedInvocationHandler
{
    private transient PresentationManager.ClassData classData;
    private transient PresentationManager pm;
    private transient org.omg.CORBA.Object stub;
    private transient Proxy self;
    
    @Override
    public void setProxy(final Proxy self) {
        this.self = self;
    }
    
    @Override
    public Proxy getProxy() {
        return this.self;
    }
    
    public StubInvocationHandlerImpl(final PresentationManager pm, final PresentationManager.ClassData classData, final org.omg.CORBA.Object stub) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new DynamicAccessPermission("access"));
        }
        this.classData = classData;
        this.pm = pm;
        this.stub = stub;
    }
    
    private boolean isLocal() {
        boolean useLocalInvocation = false;
        final Delegate delegate = StubAdapter.getDelegate(this.stub);
        if (delegate instanceof CorbaClientDelegate) {
            final ContactInfoList contactInfoList = ((CorbaClientDelegate)delegate).getContactInfoList();
            if (contactInfoList instanceof CorbaContactInfoList) {
                useLocalInvocation = ((CorbaContactInfoList)contactInfoList).getLocalClientRequestDispatcher().useLocalInvocation(null);
            }
        }
        return useLocalInvocation;
    }
    
    @Override
    public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
        final String idlName = this.classData.getIDLNameTranslator().getIDLName(method);
        final DynamicMethodMarshaller dynamicMethodMarshaller = this.pm.getDynamicMethodMarshaller(method);
        Delegate delegate;
        try {
            delegate = StubAdapter.getDelegate(this.stub);
        }
        catch (final SystemException ex) {
            throw Util.mapSystemException(ex);
        }
        if (!this.isLocal()) {
            try {
                InputStream inputStream = null;
                try {
                    final OutputStream outputStream = (OutputStream)delegate.request(this.stub, idlName, true);
                    dynamicMethodMarshaller.writeArguments(outputStream, array);
                    inputStream = (InputStream)delegate.invoke(this.stub, outputStream);
                    return dynamicMethodMarshaller.readResult(inputStream);
                }
                catch (final ApplicationException ex2) {
                    throw dynamicMethodMarshaller.readException(ex2);
                }
                catch (final RemarshalException ex3) {
                    return this.invoke(o, method, array);
                }
                finally {
                    delegate.releaseReply(this.stub, inputStream);
                }
            }
            catch (final SystemException ex4) {
                throw Util.mapSystemException(ex4);
            }
        }
        final ORB orb = (ORB)delegate.orb(this.stub);
        final ServantObject servant_preinvoke = delegate.servant_preinvoke(this.stub, idlName, method.getDeclaringClass());
        if (servant_preinvoke == null) {
            return this.invoke(this.stub, method, array);
        }
        try {
            final Object[] copyArguments = dynamicMethodMarshaller.copyArguments(array, orb);
            if (!method.isAccessible()) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        method.setAccessible(true);
                        return null;
                    }
                });
            }
            return dynamicMethodMarshaller.copyResult(method.invoke(servant_preinvoke.servant, copyArguments), orb);
        }
        catch (final InvocationTargetException ex5) {
            final Throwable t = (Throwable)Util.copyObject(ex5.getCause(), orb);
            if (dynamicMethodMarshaller.isDeclaredException(t)) {
                throw t;
            }
            throw Util.wrapException(t);
        }
        catch (final Throwable t2) {
            if (t2 instanceof ThreadDeath) {
                throw (ThreadDeath)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            delegate.servant_postinvoke(this.stub, servant_preinvoke);
        }
    }
}
