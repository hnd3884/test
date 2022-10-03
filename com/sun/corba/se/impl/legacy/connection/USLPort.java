package com.sun.corba.se.impl.legacy.connection;

public class USLPort
{
    private String type;
    private int port;
    
    public USLPort(final String type, final int port) {
        this.type = type;
        this.port = port;
    }
    
    public String getType() {
        return this.type;
    }
    
    public int getPort() {
        return this.port;
    }
    
    @Override
    public String toString() {
        return this.type + ":" + this.port;
    }
}
