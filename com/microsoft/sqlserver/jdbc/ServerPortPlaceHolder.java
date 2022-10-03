package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;

final class ServerPortPlaceHolder implements Serializable
{
    private static final long serialVersionUID = 7393779415545731523L;
    private final String serverName;
    private final int port;
    private final String instanceName;
    private final boolean checkLink;
    private final SQLServerConnectionSecurityManager securityManager;
    
    ServerPortPlaceHolder(final String name, final int conPort, final String instance, final boolean fLink) {
        this.serverName = name;
        this.port = conPort;
        this.instanceName = instance;
        this.checkLink = fLink;
        this.securityManager = new SQLServerConnectionSecurityManager(this.serverName, this.port);
        this.doSecurityCheck();
    }
    
    int getPortNumber() {
        return this.port;
    }
    
    String getServerName() {
        return this.serverName;
    }
    
    String getInstanceName() {
        return this.instanceName;
    }
    
    void doSecurityCheck() {
        this.securityManager.checkConnect();
        if (this.checkLink) {
            this.securityManager.checkLink();
        }
    }
}
