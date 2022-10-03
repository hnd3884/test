package com.me.mdm.uem.actionconstants;

public enum LicenseAction
{
    GET_LICENSE_DETAILS("getDCLicenseDetails"), 
    GET_USAGE_DETAILS("getDCUsageDetails");
    
    private String methodName;
    public static final String CLASS_NAME = "com.me.dc.integration.mdm.LicenseActionListenerImpl";
    
    private LicenseAction(final String methodName) {
        this.methodName = methodName;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
}
