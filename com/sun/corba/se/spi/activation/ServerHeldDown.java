package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerHeldDown extends UserException
{
    public int serverId;
    
    public ServerHeldDown() {
        super(ServerHeldDownHelper.id());
        this.serverId = 0;
    }
    
    public ServerHeldDown(final int serverId) {
        super(ServerHeldDownHelper.id());
        this.serverId = 0;
        this.serverId = serverId;
    }
    
    public ServerHeldDown(final String s, final int serverId) {
        super(ServerHeldDownHelper.id() + "  " + s);
        this.serverId = 0;
        this.serverId = serverId;
    }
}
