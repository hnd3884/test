package com.sun.jmx.remote.protocol.iiop;

import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.Permission;
import java.io.SerializablePermission;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import javax.rmi.PortableRemoteObject;
import java.util.Properties;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.Delegate;
import javax.rmi.CORBA.Stub;
import java.security.AccessControlContext;
import com.sun.jmx.remote.internal.IIOPProxy;

public class IIOPProxyImpl implements IIOPProxy
{
    private static final AccessControlContext STUB_ACC;
    
    @Override
    public boolean isStub(final Object o) {
        return o instanceof Stub;
    }
    
    @Override
    public Object getDelegate(final Object o) {
        return ((Stub)o)._get_delegate();
    }
    
    @Override
    public void setDelegate(final Object o, final Object o2) {
        ((Stub)o)._set_delegate((Delegate)o2);
    }
    
    @Override
    public Object getOrb(final Object o) {
        try {
            return ((Stub)o)._orb();
        }
        catch (final BAD_OPERATION bad_OPERATION) {
            throw new UnsupportedOperationException(bad_OPERATION);
        }
    }
    
    @Override
    public void connect(final Object o, final Object o2) throws RemoteException {
        ((Stub)o).connect((ORB)o2);
    }
    
    @Override
    public boolean isOrb(final Object o) {
        return o instanceof ORB;
    }
    
    @Override
    public Object createOrb(final String[] array, final Properties properties) {
        return ORB.init(array, properties);
    }
    
    @Override
    public Object stringToObject(final Object o, final String s) {
        return ((ORB)o).string_to_object(s);
    }
    
    @Override
    public String objectToString(final Object o, final Object o2) {
        return ((ORB)o).object_to_string((org.omg.CORBA.Object)o2);
    }
    
    @Override
    public <T> T narrow(final Object o, final Class<T> clazz) {
        return (T)PortableRemoteObject.narrow(o, clazz);
    }
    
    @Override
    public void exportObject(final Remote remote) throws RemoteException {
        PortableRemoteObject.exportObject(remote);
    }
    
    @Override
    public void unexportObject(final Remote remote) throws NoSuchObjectException {
        PortableRemoteObject.unexportObject(remote);
    }
    
    @Override
    public Remote toStub(final Remote remote) throws NoSuchObjectException {
        if (System.getSecurityManager() == null) {
            return PortableRemoteObject.toStub(remote);
        }
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Remote>)new PrivilegedExceptionAction<Remote>() {
                @Override
                public Remote run() throws Exception {
                    return PortableRemoteObject.toStub(remote);
                }
            }, IIOPProxyImpl.STUB_ACC);
        }
        catch (final PrivilegedActionException ex) {
            if (ex.getException() instanceof NoSuchObjectException) {
                throw (NoSuchObjectException)ex.getException();
            }
            throw new RuntimeException("Unexpected exception type", ex.getException());
        }
    }
    
    static {
        final Permissions permissions = new Permissions();
        permissions.add(new SerializablePermission("enableSubclassImplementation"));
        STUB_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) });
    }
}
