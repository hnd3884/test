package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerNotRegistered extends UserException
{
    public int serverId;
    
    public ServerNotRegistered() {
        super(ServerNotRegisteredHelper.id());
        this.serverId = 0;
    }
    
    public ServerNotRegistered(final int serverId) {
        super(ServerNotRegisteredHelper.id());
        this.serverId = 0;
        this.serverId = serverId;
    }
    
    public ServerNotRegistered(final String s, final int serverId) {
        super(ServerNotRegisteredHelper.id() + "  " + s);
        this.serverId = 0;
        this.serverId = serverId;
    }
}
