package com.adventnet.sym.server.mdm.certificates.scepserver.adcs;

import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;

public class AdcsScepServer extends ScepServer
{
    public final int challengeType;
    private String adminUrl;
    private String adminUsername;
    private String adminPassword;
    
    public AdcsScepServer(final int challengeType) {
        this.adminUrl = null;
        this.adminUsername = null;
        this.adminPassword = null;
        this.challengeType = challengeType;
    }
    
    public void setAdminUrl(final String adminUrl) {
        this.adminUrl = adminUrl;
    }
    
    public void setAdminUsername(final String adminUsername) {
        this.adminUsername = adminUsername;
    }
    
    public void setAdminPassword(final String adminPassword) {
        this.adminPassword = adminPassword;
    }
    
    public String getAdminUrl() {
        return this.adminUrl;
    }
    
    public String getAdminUsername() {
        return this.adminUsername;
    }
    
    public String getAdminPassword() {
        return this.adminPassword;
    }
}
