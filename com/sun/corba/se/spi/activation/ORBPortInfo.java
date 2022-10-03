package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class ORBPortInfo implements IDLEntity
{
    public String orbId;
    public int port;
    
    public ORBPortInfo() {
        this.orbId = null;
        this.port = 0;
    }
    
    public ORBPortInfo(final String orbId, final int port) {
        this.orbId = null;
        this.port = 0;
        this.orbId = orbId;
        this.port = port;
    }
}
