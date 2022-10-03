package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerNotActive extends UserException
{
    public int serverId;
    
    public ServerNotActive() {
        super(ServerNotActiveHelper.id());
        this.serverId = 0;
    }
    
    public ServerNotActive(final int serverId) {
        super(ServerNotActiveHelper.id());
        this.serverId = 0;
        this.serverId = serverId;
    }
    
    public ServerNotActive(final String s, final int serverId) {
        super(ServerNotActiveHelper.id() + "  " + s);
        this.serverId = 0;
        this.serverId = serverId;
    }
}
