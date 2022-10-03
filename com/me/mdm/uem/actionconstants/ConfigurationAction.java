package com.me.mdm.uem.actionconstants;

public enum ConfigurationAction
{
    POST_CONFIGURATION_STATUS_UPDATE("postConfigStatusUpdate");
    
    private String methodName;
    public static final String CLASS_NAME = "com.me.dc.integration.mdm.ConfigurationActionListenerImpl";
    
    private ConfigurationAction(final String methodName) {
        this.methodName = methodName;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
}
