package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.transport.SocketInfo;

public class EndPointInfoImpl implements SocketInfo, LegacyServerSocketEndPointInfo
{
    protected String type;
    protected String hostname;
    protected int port;
    protected int locatorPort;
    protected String name;
    
    public EndPointInfoImpl(final String type, final int port, final String hostname) {
        this.type = type;
        this.port = port;
        this.hostname = hostname;
        this.locatorPort = -1;
        this.name = "NO_NAME";
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    @Override
    public String getHost() {
        return this.hostname;
    }
    
    @Override
    public String getHostName() {
        return this.hostname;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    @Override
    public int getLocatorPort() {
        return this.locatorPort;
    }
    
    @Override
    public void setLocatorPort(final int locatorPort) {
        this.locatorPort = locatorPort;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return this.type.hashCode() ^ this.hostname.hashCode() ^ this.port;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof EndPointInfoImpl)) {
            return false;
        }
        final EndPointInfoImpl endPointInfoImpl = (EndPointInfoImpl)o;
        if (this.type == null) {
            if (endPointInfoImpl.type != null) {
                return false;
            }
        }
        else if (!this.type.equals(endPointInfoImpl.type)) {
            return false;
        }
        return this.port == endPointInfoImpl.port && this.hostname.equals(endPointInfoImpl.hostname);
    }
    
    @Override
    public String toString() {
        return this.type + " " + this.name + " " + this.hostname + " " + this.port;
    }
}
