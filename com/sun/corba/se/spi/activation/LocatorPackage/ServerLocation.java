package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.ORBPortInfo;
import org.omg.CORBA.portable.IDLEntity;

public final class ServerLocation implements IDLEntity
{
    public String hostname;
    public ORBPortInfo[] ports;
    
    public ServerLocation() {
        this.hostname = null;
        this.ports = null;
    }
    
    public ServerLocation(final String hostname, final ORBPortInfo[] ports) {
        this.hostname = null;
        this.ports = null;
        this.hostname = hostname;
        this.ports = ports;
    }
}
