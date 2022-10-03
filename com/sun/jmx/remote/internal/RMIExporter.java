package com.sun.jmx.remote.internal;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.Remote;

public interface RMIExporter
{
    public static final String EXPORTER_ATTRIBUTE = "com.sun.jmx.remote.rmi.exporter";
    
    Remote exportObject(final Remote p0, final int p1, final RMIClientSocketFactory p2, final RMIServerSocketFactory p3) throws RemoteException;
    
    boolean unexportObject(final Remote p0, final boolean p1) throws NoSuchObjectException;
}
