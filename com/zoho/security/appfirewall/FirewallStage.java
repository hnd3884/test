package com.zoho.security.appfirewall;

public enum FirewallStage
{
    PRE_STAGE("pre"), 
    POST_STAGE("post"), 
    POST_AUTHENTICATION_STAGE("iam");
    
    private String stage;
    
    private FirewallStage(final String val) {
        this.stage = null;
        this.stage = val;
    }
    
    public String value() {
        return this.stage;
    }
}
