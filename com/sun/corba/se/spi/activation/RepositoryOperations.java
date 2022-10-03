package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;

public interface RepositoryOperations
{
    int registerServer(final ServerDef p0) throws ServerAlreadyRegistered, BadServerDefinition;
    
    void unregisterServer(final int p0) throws ServerNotRegistered;
    
    ServerDef getServer(final int p0) throws ServerNotRegistered;
    
    boolean isInstalled(final int p0) throws ServerNotRegistered;
    
    void install(final int p0) throws ServerNotRegistered, ServerAlreadyInstalled;
    
    void uninstall(final int p0) throws ServerNotRegistered, ServerAlreadyUninstalled;
    
    int[] listRegisteredServers();
    
    String[] getApplicationNames();
    
    int getServerID(final String p0) throws ServerNotRegistered;
}
