package com.sun.corba.se.spi.activation;

public interface ActivatorOperations
{
    void active(final int p0, final Server p1) throws ServerNotRegistered;
    
    void registerEndpoints(final int p0, final String p1, final EndPointInfo[] p2) throws ServerNotRegistered, NoSuchEndPoint, ORBAlreadyRegistered;
    
    int[] getActiveServers();
    
    void activate(final int p0) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown;
    
    void shutdown(final int p0) throws ServerNotActive, ServerNotRegistered;
    
    void install(final int p0) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled;
    
    String[] getORBNames(final int p0) throws ServerNotRegistered;
    
    void uninstall(final int p0) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled;
}
