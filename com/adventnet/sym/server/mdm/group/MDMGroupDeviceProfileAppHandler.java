package com.adventnet.sym.server.mdm.group;

import java.util.Hashtable;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import java.util.Properties;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.HashMap;

public class MDMGroupDeviceProfileAppHandler
{
    private static MDMGroupDeviceProfileAppHandler mdmCgHandler;
    
    public static MDMGroupDeviceProfileAppHandler getInstance() {
        if (MDMGroupDeviceProfileAppHandler.mdmCgHandler == null) {
            MDMGroupDeviceProfileAppHandler.mdmCgHandler = new MDMGroupDeviceProfileAppHandler();
        }
        return MDMGroupDeviceProfileAppHandler.mdmCgHandler;
    }
    
    public HashMap getGroupAssociatedProfileAppDetails(final Long groupId) {
        HashMap groupDetails = null;
        try {
            groupDetails = MDMGroupHandler.getInstance().getGroupDetails(groupId);
            final HashMap groupSummaryMap = (HashMap)ProfileAssociateHandler.getInstance().getGroupSummary(new ArrayList(Arrays.asList(groupId)));
            final Properties groupSummary = groupSummaryMap.get(groupId);
            int docCount = 0;
            int memberCount = 0;
            int profileCount = 0;
            int appsDistributedCount = 0;
            if (groupSummary != null) {
                docCount = ((groupSummary.get("DOC_COUNT") != null) ? ((Hashtable<K, Integer>)groupSummary).get("DOC_COUNT") : 0);
                memberCount = ((groupSummary.get("MEMBER_COUNT") != null) ? ((Hashtable<K, Integer>)groupSummary).get("MEMBER_COUNT") : 0);
                profileCount = ((groupSummary.get("PROFILE_COUNT") != null) ? ((Hashtable<K, Integer>)groupSummary).get("PROFILE_COUNT") : 0);
                appsDistributedCount = ((groupSummary.get("APP_COUNT") != null) ? ((Hashtable<K, Integer>)groupSummary).get("APP_COUNT") : 0);
            }
            final int appInstalledCount = MDMCustomGroupUtil.getInstance().getAppGroupInstalledCount(groupId);
            final String createdBy = (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", groupDetails.get("CREATED_BY"), "FIRST_NAME");
            final String modifiedBy = (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", groupDetails.get("LAST_MODIFIED_BY"), "FIRST_NAME");
            groupDetails.put("CREATED_BY", createdBy);
            groupDetails.put("LAST_MODIFIED_BY", modifiedBy);
            final String createdTimeStr = Utils.getEventTime(Long.valueOf(groupDetails.get("DB_ADDED_TIME")));
            final String modifiedTimeStr = Utils.getEventTime(Long.valueOf(groupDetails.get("DB_UPDATED_TIME")));
            groupDetails.put("DB_ADDED_TIME", createdTimeStr);
            groupDetails.put("DB_UPDATED_TIME", modifiedTimeStr);
            groupDetails.put("MEMBER_COUNT", memberCount);
            groupDetails.put("PROFILE_COUNT", profileCount);
            groupDetails.put("APP_DISTRIBUTED_COUNT", appsDistributedCount);
            groupDetails.put("APP_INSTALLED_COUNT", appInstalledCount);
            groupDetails.put("DOC_DISTRIBUTED_COUNT", docCount);
        }
        catch (final Exception e) {
            MDMUtil.logger.log(Level.WARNING, "Exception occoured  in getGroupAssociatedProfileAppDetails....", e);
        }
        return groupDetails;
    }
    
    static {
        MDMGroupDeviceProfileAppHandler.mdmCgHandler = null;
    }
}
