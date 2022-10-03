package com.sun.jndi.rmi.registry;

import java.rmi.UnexpectedException;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import javax.naming.Reference;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;

public final class ReferenceWrapper_Stub extends RemoteStub implements RemoteReference, Remote
{
    private static final long serialVersionUID = 2L;
    private static Method $method_getReference_0;
    static /* synthetic */ Class class$com$sun$jndi$rmi$registry$RemoteReference;
    
    static {
        try {
            ReferenceWrapper_Stub.$method_getReference_0 = ((ReferenceWrapper_Stub.class$com$sun$jndi$rmi$registry$RemoteReference != null) ? ReferenceWrapper_Stub.class$com$sun$jndi$rmi$registry$RemoteReference : (ReferenceWrapper_Stub.class$com$sun$jndi$rmi$registry$RemoteReference = class$("com.sun.jndi.rmi.registry.RemoteReference"))).getMethod("getReference", (Class[])new Class[0]);
        }
        catch (final NoSuchMethodException ex) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }
    
    public ReferenceWrapper_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public Reference getReference() throws RemoteException, NamingException {
        try {
            return (Reference)super.ref.invoke(this, ReferenceWrapper_Stub.$method_getReference_0, null, 3529874867989176284L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final NamingException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
}
