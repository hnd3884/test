package com.me.devicemanagement.framework.server.downloadmgr;

import HTTPClient.NVPair;
import HTTPClient.AuthorizationInfo;
import HTTPClient.AuthorizationPrompter;

public class DCAuthorizationPrompter implements AuthorizationPrompter
{
    private String proxyUser;
    private String proxyPass;
    
    public DCAuthorizationPrompter(final String userName, final String password) {
        this.proxyUser = null;
        this.proxyPass = null;
        this.proxyUser = userName;
        this.proxyPass = password;
    }
    
    public DCAuthorizationPrompter() {
        this.proxyUser = null;
        this.proxyPass = null;
    }
    
    public NVPair getUsernamePassword(final AuthorizationInfo challenge, final boolean forProxy) throws Exception {
        if (this.proxyUser != null && this.proxyPass != null) {
            return new NVPair(this.proxyUser, this.proxyPass);
        }
        return null;
    }
}
