package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyInstalled extends UserException
{
    public int serverId;
    
    public ServerAlreadyInstalled() {
        super(ServerAlreadyInstalledHelper.id());
        this.serverId = 0;
    }
    
    public ServerAlreadyInstalled(final int serverId) {
        super(ServerAlreadyInstalledHelper.id());
        this.serverId = 0;
        this.serverId = serverId;
    }
    
    public ServerAlreadyInstalled(final String s, final int serverId) {
        super(ServerAlreadyInstalledHelper.id() + "  " + s);
        this.serverId = 0;
        this.serverId = serverId;
    }
}
