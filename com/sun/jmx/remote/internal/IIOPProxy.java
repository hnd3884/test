package com.sun.jmx.remote.internal;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.util.Properties;
import java.rmi.RemoteException;

public interface IIOPProxy
{
    boolean isStub(final Object p0);
    
    Object getDelegate(final Object p0);
    
    void setDelegate(final Object p0, final Object p1);
    
    Object getOrb(final Object p0);
    
    void connect(final Object p0, final Object p1) throws RemoteException;
    
    boolean isOrb(final Object p0);
    
    Object createOrb(final String[] p0, final Properties p1);
    
    Object stringToObject(final Object p0, final String p1);
    
    String objectToString(final Object p0, final Object p1);
    
     <T> T narrow(final Object p0, final Class<T> p1);
    
    void exportObject(final Remote p0) throws RemoteException;
    
    void unexportObject(final Remote p0) throws NoSuchObjectException;
    
    Remote toStub(final Remote p0) throws NoSuchObjectException;
}
