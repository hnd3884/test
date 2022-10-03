package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.portable.IDLEntity;

public final class ServerDef implements IDLEntity
{
    public String applicationName;
    public String serverName;
    public String serverClassPath;
    public String serverArgs;
    public String serverVmArgs;
    
    public ServerDef() {
        this.applicationName = null;
        this.serverName = null;
        this.serverClassPath = null;
        this.serverArgs = null;
        this.serverVmArgs = null;
    }
    
    public ServerDef(final String applicationName, final String serverName, final String serverClassPath, final String serverArgs, final String serverVmArgs) {
        this.applicationName = null;
        this.serverName = null;
        this.serverClassPath = null;
        this.serverArgs = null;
        this.serverVmArgs = null;
        this.applicationName = applicationName;
        this.serverName = serverName;
        this.serverClassPath = serverClassPath;
        this.serverArgs = serverArgs;
        this.serverVmArgs = serverVmArgs;
    }
}
