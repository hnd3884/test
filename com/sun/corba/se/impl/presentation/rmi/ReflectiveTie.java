package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;
import java.security.Permission;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import org.omg.PortableServer.Servant;

public final class ReflectiveTie extends Servant implements Tie
{
    private Remote target;
    private PresentationManager pm;
    private PresentationManager.ClassData classData;
    private ORBUtilSystemException wrapper;
    
    public ReflectiveTie(final PresentationManager pm, final ORBUtilSystemException wrapper) {
        this.target = null;
        this.classData = null;
        this.wrapper = null;
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new DynamicAccessPermission("access"));
        }
        this.pm = pm;
        this.wrapper = wrapper;
    }
    
    @Override
    public String[] _all_interfaces(final POA poa, final byte[] array) {
        return this.classData.getTypeIds();
    }
    
    @Override
    public void setTarget(final Remote target) {
        this.target = target;
        if (target == null) {
            this.classData = null;
        }
        else {
            this.classData = this.pm.getClassData(target.getClass());
        }
    }
    
    @Override
    public Remote getTarget() {
        return this.target;
    }
    
    @Override
    public org.omg.CORBA.Object thisObject() {
        return this._this_object();
    }
    
    @Override
    public void deactivate() {
        try {
            this._poa().deactivate_object(this._poa().servant_to_id(this));
        }
        catch (final WrongPolicy wrongPolicy) {}
        catch (final ObjectNotActive objectNotActive) {}
        catch (final ServantNotActive servantNotActive) {}
    }
    
    @Override
    public ORB orb() {
        return this._orb();
    }
    
    @Override
    public void orb(final ORB orb) {
        try {
            final com.sun.corba.se.spi.orb.ORB orb2 = (com.sun.corba.se.spi.orb.ORB)orb;
            ((org.omg.CORBA_2_3.ORB)orb).set_delegate(this);
        }
        catch (final ClassCastException ex) {
            throw this.wrapper.badOrbForServant(ex);
        }
    }
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        Method method = null;
        DynamicMethodMarshaller dynamicMethodMarshaller = null;
        try {
            final org.omg.CORBA_2_3.portable.InputStream inputStream2 = (org.omg.CORBA_2_3.portable.InputStream)inputStream;
            method = this.classData.getIDLNameTranslator().getMethod(s);
            if (method == null) {
                throw this.wrapper.methodNotFoundInTie(s, this.target.getClass().getName());
            }
            dynamicMethodMarshaller = this.pm.getDynamicMethodMarshaller(method);
            final Object invoke = method.invoke(this.target, dynamicMethodMarshaller.readArguments(inputStream2));
            final org.omg.CORBA_2_3.portable.OutputStream outputStream = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
            dynamicMethodMarshaller.writeResult(outputStream, invoke);
            return outputStream;
        }
        catch (final IllegalAccessException ex) {
            throw this.wrapper.invocationErrorInReflectiveTie(ex, method.getName(), method.getDeclaringClass().getName());
        }
        catch (final IllegalArgumentException ex2) {
            throw this.wrapper.invocationErrorInReflectiveTie(ex2, method.getName(), method.getDeclaringClass().getName());
        }
        catch (final InvocationTargetException ex3) {
            final Throwable cause = ex3.getCause();
            if (cause instanceof SystemException) {
                throw (SystemException)cause;
            }
            if (cause instanceof Exception && dynamicMethodMarshaller.isDeclaredException(cause)) {
                final org.omg.CORBA_2_3.portable.OutputStream outputStream2 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                dynamicMethodMarshaller.writeException(outputStream2, (Exception)cause);
                return outputStream2;
            }
            throw new UnknownException(cause);
        }
    }
}
