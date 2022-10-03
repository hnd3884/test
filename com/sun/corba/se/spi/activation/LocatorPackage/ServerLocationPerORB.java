package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.EndPointInfo;
import org.omg.CORBA.portable.IDLEntity;

public final class ServerLocationPerORB implements IDLEntity
{
    public String hostname;
    public EndPointInfo[] ports;
    
    public ServerLocationPerORB() {
        this.hostname = null;
        this.ports = null;
    }
    
    public ServerLocationPerORB(final String hostname, final EndPointInfo[] ports) {
        this.hostname = null;
        this.ports = null;
        this.hostname = hostname;
        this.ports = ports;
    }
}
