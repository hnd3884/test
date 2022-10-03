package com.adventnet.cli.rmi;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface CLIResourceManager extends Remote
{
    void setSystemWideMaxConnections(final int p0) throws RemoteException;
    
    int getSystemWideMaxConnections() throws RemoteException;
    
    void setKeepAliveTimeout(final int p0) throws RemoteException;
    
    int getKeepAliveTimeout() throws RemoteException;
    
    void setMaxConnections(final int p0) throws RemoteException;
    
    int getMaxConnections() throws RemoteException;
    
    void setPooling(final boolean p0) throws RemoteException;
    
    boolean isSetPooling() throws RemoteException;
    
    void closeAllConnections() throws RemoteException;
}
