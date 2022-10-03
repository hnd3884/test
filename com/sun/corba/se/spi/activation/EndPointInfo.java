package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class EndPointInfo implements IDLEntity
{
    public String endpointType;
    public int port;
    
    public EndPointInfo() {
        this.endpointType = null;
        this.port = 0;
    }
    
    public EndPointInfo(final String endpointType, final int port) {
        this.endpointType = null;
        this.port = 0;
        this.endpointType = endpointType;
        this.port = port;
    }
}
