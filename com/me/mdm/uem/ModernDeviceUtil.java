package com.me.mdm.uem;

import com.me.devicemanagement.framework.server.util.DBUtil;

public class ModernDeviceUtil
{
    public static Boolean isModernManagementCapableResource(final Long resourceID) throws Exception {
        return (int)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)resourceID, "RESOURCE_TYPE") == 121;
    }
}
