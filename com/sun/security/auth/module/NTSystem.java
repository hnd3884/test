package com.sun.security.auth.module;

import jdk.Exported;

@Exported
public class NTSystem
{
    private String userName;
    private String domain;
    private String domainSID;
    private String userSID;
    private String[] groupIDs;
    private String primaryGroupID;
    private long impersonationToken;
    
    private native void getCurrent(final boolean p0);
    
    private native long getImpersonationToken0();
    
    public NTSystem() {
        this(false);
    }
    
    NTSystem(final boolean b) {
        this.loadNative();
        this.getCurrent(b);
    }
    
    public String getName() {
        return this.userName;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public String getDomainSID() {
        return this.domainSID;
    }
    
    public String getUserSID() {
        return this.userSID;
    }
    
    public String getPrimaryGroupID() {
        return this.primaryGroupID;
    }
    
    public String[] getGroupIDs() {
        return (String[])((this.groupIDs == null) ? null : ((String[])this.groupIDs.clone()));
    }
    
    public synchronized long getImpersonationToken() {
        if (this.impersonationToken == 0L) {
            this.impersonationToken = this.getImpersonationToken0();
        }
        return this.impersonationToken;
    }
    
    private void loadNative() {
        System.loadLibrary("jaas_nt");
    }
}
