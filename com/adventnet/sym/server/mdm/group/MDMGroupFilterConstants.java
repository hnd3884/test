package com.adventnet.sym.server.mdm.group;

import com.me.mdm.server.tree.MDMTreeFilterConstants;

public class MDMGroupFilterConstants extends MDMTreeFilterConstants
{
    public static final String GROUP_ID = "GROUP_ID";
    public static final String GROUP_MEMBERS = "GROUP_MEMBERS";
    public static final Long NEW_GROUP_ID;
    public static final int DEVICE_GROUP_ASSIGNED_FILTER_TYPE = 100;
    public static final int DEVICE_GROUP_UN_ASSIGNED = 101;
    public static final int LOCATION_FILTER_TYPE = 5;
    
    static {
        NEW_GROUP_ID = -1L;
    }
}
