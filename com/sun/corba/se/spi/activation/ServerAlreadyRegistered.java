package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyRegistered extends UserException
{
    public int serverId;
    
    public ServerAlreadyRegistered() {
        super(ServerAlreadyRegisteredHelper.id());
        this.serverId = 0;
    }
    
    public ServerAlreadyRegistered(final int serverId) {
        super(ServerAlreadyRegisteredHelper.id());
        this.serverId = 0;
        this.serverId = serverId;
    }
    
    public ServerAlreadyRegistered(final String s, final int serverId) {
        super(ServerAlreadyRegisteredHelper.id() + "  " + s);
        this.serverId = 0;
        this.serverId = serverId;
    }
}
