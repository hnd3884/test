package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;

public interface LocatorOperations
{
    ServerLocation locateServer(final int p0, final String p1) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown;
    
    ServerLocationPerORB locateServerForORB(final int p0, final String p1) throws InvalidORBid, ServerNotRegistered, ServerHeldDown;
    
    int getEndpoint(final String p0) throws NoSuchEndPoint;
    
    int getServerPortForType(final ServerLocationPerORB p0, final String p1) throws NoSuchEndPoint;
}
