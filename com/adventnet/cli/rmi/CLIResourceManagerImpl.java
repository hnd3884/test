package com.adventnet.cli.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class CLIResourceManagerImpl extends UnicastRemoteObject implements CLIResourceManager
{
    protected static com.adventnet.cli.CLIResourceManager cliResourceManager;
    
    com.adventnet.cli.CLIResourceManager getCLIResourceManager() {
        return CLIResourceManagerImpl.cliResourceManager;
    }
    
    CLIResourceManagerImpl() throws RemoteException {
        if (CLIResourceManagerImpl.cliResourceManager == null) {
            CLIResourceManagerImpl.cliResourceManager = com.adventnet.cli.CLIResourceManager.getInstance();
        }
    }
    
    public void setSystemWideMaxConnections(final int systemWideMaxConnections) throws RemoteException {
        CLIResourceManagerImpl.cliResourceManager.setSystemWideMaxConnections(systemWideMaxConnections);
    }
    
    public int getSystemWideMaxConnections() throws RemoteException {
        return CLIResourceManagerImpl.cliResourceManager.getSystemWideMaxConnections();
    }
    
    public void setKeepAliveTimeout(final int keepAliveTimeout) throws RemoteException {
        CLIResourceManagerImpl.cliResourceManager.setKeepAliveTimeout(keepAliveTimeout);
    }
    
    public int getKeepAliveTimeout() throws RemoteException {
        return CLIResourceManagerImpl.cliResourceManager.getKeepAliveTimeout();
    }
    
    public void setMaxConnections(final int maxConnections) throws RemoteException {
        CLIResourceManagerImpl.cliResourceManager.setMaxConnections(maxConnections);
    }
    
    public int getMaxConnections() throws RemoteException {
        return CLIResourceManagerImpl.cliResourceManager.getMaxConnections();
    }
    
    public void setPooling(final boolean pooling) throws RemoteException {
        CLIResourceManagerImpl.cliResourceManager.setPooling(pooling);
    }
    
    public boolean isSetPooling() throws RemoteException {
        return CLIResourceManagerImpl.cliResourceManager.isSetPooling();
    }
    
    public synchronized void closeAllConnections() throws RemoteException {
        CLIResourceManagerImpl.cliResourceManager.closeAllConnections();
    }
    
    static {
        CLIResourceManagerImpl.cliResourceManager = null;
    }
}
