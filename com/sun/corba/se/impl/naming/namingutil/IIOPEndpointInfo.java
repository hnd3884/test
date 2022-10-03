package com.sun.corba.se.impl.naming.namingutil;

public class IIOPEndpointInfo
{
    private int major;
    private int minor;
    private String host;
    private int port;
    
    IIOPEndpointInfo() {
        this.major = 1;
        this.minor = 0;
        this.host = "localhost";
        this.port = 2089;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setVersion(final int major, final int minor) {
        this.major = major;
        this.minor = minor;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public void dump() {
        System.out.println(" Major -> " + this.major + " Minor -> " + this.minor);
        System.out.println("host -> " + this.host);
        System.out.println("port -> " + this.port);
    }
}
