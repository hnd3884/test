package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyActive extends UserException
{
    public int serverId;
    
    public ServerAlreadyActive() {
        super(ServerAlreadyActiveHelper.id());
        this.serverId = 0;
    }
    
    public ServerAlreadyActive(final int serverId) {
        super(ServerAlreadyActiveHelper.id());
        this.serverId = 0;
        this.serverId = serverId;
    }
    
    public ServerAlreadyActive(final String s, final int serverId) {
        super(ServerAlreadyActiveHelper.id() + "  " + s);
        this.serverId = 0;
        this.serverId = serverId;
    }
}
