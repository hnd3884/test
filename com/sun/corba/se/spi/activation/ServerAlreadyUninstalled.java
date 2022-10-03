package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyUninstalled extends UserException
{
    public int serverId;
    
    public ServerAlreadyUninstalled() {
        super(ServerAlreadyUninstalledHelper.id());
        this.serverId = 0;
    }
    
    public ServerAlreadyUninstalled(final int serverId) {
        super(ServerAlreadyUninstalledHelper.id());
        this.serverId = 0;
        this.serverId = serverId;
    }
    
    public ServerAlreadyUninstalled(final String s, final int serverId) {
        super(ServerAlreadyUninstalledHelper.id() + "  " + s);
        this.serverId = 0;
        this.serverId = serverId;
    }
}
