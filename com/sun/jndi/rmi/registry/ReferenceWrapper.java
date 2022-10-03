package com.sun.jndi.rmi.registry;

import java.rmi.RemoteException;
import javax.naming.NamingException;
import javax.naming.Reference;
import java.rmi.server.UnicastRemoteObject;

public class ReferenceWrapper extends UnicastRemoteObject implements RemoteReference
{
    protected Reference wrappee;
    private static final long serialVersionUID = 6078186197417641456L;
    
    public ReferenceWrapper(final Reference wrappee) throws NamingException, RemoteException {
        this.wrappee = wrappee;
    }
    
    @Override
    public Reference getReference() throws RemoteException {
        return this.wrappee;
    }
}
