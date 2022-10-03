package com.adventnet.sym.server.mdm.inv.chrome;

import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.mdm.server.privacy.PrivacyDeviceMessageHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class SystemActivityMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    public static final String CPU_PERFORMANCE = "CpuPerformance";
    public static final String RECENT_USERS = "RecentUsers";
    public static final String USER_TYPE = "type";
    public static final String USER_EMAIL = "email";
    public static final String LAST_SYNC_TIME = "LastSyncTime";
    public static final String ACTIVE_TIME_RANGE = "ActiveTimeRanges";
    public static final String DATE = "date";
    public static final String ACTIVE_TIME = "activeTime";
    public static final String USER_TYPE_MANAGED = "USER_TYPE_MANAGED";
    public static final String USER_TYPE_UNMANAGED = "USER_TYPE_UNMANAGED";
    public static final int USER_TYPE_MANAGED_CONSTANT = 1;
    public static final int USER_TYPE_UNMANAGED_CONSTANT = 2;
    
    public SystemActivityMDMInventoryImpl() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> systemActivityInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            final Long resourceId = inventoryObject.resourceId;
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            this.logger.log(Level.INFO, "Populating system activity data for resource ID {0}", resourceId);
            final JSONObject chromePrivacyData = PrivacyDeviceMessageHandler.getInstance().getChromePrivacyData(customerId);
            final boolean isDeviceReportingEnabled = chromePrivacyData.optBoolean("DeviceReportingEnabled", false);
            final boolean isUserReportingEnabled = chromePrivacyData.optBoolean("RecentUserReporting", false);
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            systemActivityInfo.put("CpuPerformance", inventoryData.optString("CpuPerformance", "-1"));
            systemActivityInfo.put("LastSyncTime", inventoryData.optString("LastSyncTime", "-1"));
            invDataPopulator.addOrUpdateDeviceSummaryData(resourceId, systemActivityInfo);
            if (isUserReportingEnabled && inventoryData.optJSONArray("RecentUsers") != null) {
                systemActivityInfo.put("RecentUsers", inventoryData.optJSONArray("RecentUsers").toString());
                invDataPopulator.addOrUpdateRecentUsersData(customerId, resourceId, systemActivityInfo);
            }
            if (isDeviceReportingEnabled && inventoryData.optJSONArray("ActiveTimeRanges") != null) {
                systemActivityInfo.put("ActiveTimeRanges", inventoryData.optJSONArray("ActiveTimeRanges").toString());
                invDataPopulator.addOrUpdateACtiveTimeRange(resourceId, systemActivityInfo);
            }
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred on populating system activity data from response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
