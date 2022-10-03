package com.me.mdm.uem.actionconstants;

public enum DeviceAction
{
    RESOURCE_USER_ASSIGNED("modernMgmtResourceUserAssigned"), 
    ADDORUPDATE_MAPPINGTABLE("addOrUpdateComputerDeviceMapping"), 
    GETLEGACY_AGENT_DETAILS("getLegacyAgentDetails"), 
    DEVICE_MANAGED("deviceManaged"), 
    UPDATE_LAST_CONTACT("updateLastContactTime"), 
    POST_USER_ASSIGNMENT_RULES("generateUserAssignmentRules");
    
    private String methodName;
    public static final String CLASS_NAME = "com.me.dc.integration.mdm.DeviceActionListenerImpl";
    
    private DeviceAction(final String methodName) {
        this.methodName = methodName;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
}
