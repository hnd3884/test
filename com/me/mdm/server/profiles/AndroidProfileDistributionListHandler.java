package com.me.mdm.server.profiles;

import java.util.Iterator;
import org.json.JSONObject;
import java.util.Map;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import java.util.HashMap;
import java.util.List;

public class AndroidProfileDistributionListHandler extends ProfileDistributionListHandler
{
    public AndroidProfileDistributionListHandler() {
        this.platformType = 2;
    }
    
    @Override
    public HashMap getRemainingLicenseCountMap(final Long customerId, final List businessStoreIDList) throws Exception {
        final HashMap licMap = new HashMap();
        try {
            final Map appLicenseData = new AppLicenseMgmtHandler().getStoreAppLicenseDetailsForCustomer(customerId);
            for (final Map.Entry data : appLicenseData.entrySet()) {
                final Long appGroupID = data.getKey();
                final JSONObject licInf = data.getValue();
                final JSONObject licenseSummaryJSON = new JSONObject();
                licenseSummaryJSON.put("TOTAL_LICENSE", 0);
                licenseSummaryJSON.put("AVAILABLE_LICENSE_COUNT", licInf.optInt("PROVISIONED_COUNT"));
                licenseSummaryJSON.put("ASSIGNED_LICENSE_COUNT", 0);
                licMap.put(appGroupID, licenseSummaryJSON);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return licMap;
    }
    
    @Override
    public HashMap getLicensesAssociatedToGroupsMap(final List groupResourceIds, final long customerId, final List businessStoreIDList) throws Exception {
        return this.geAssociatedAppLicenseInfo(customerId);
    }
    
    @Override
    public HashMap getLicensesAssociatedToResourcesMap(final List resourceIds, final long customerId, final List businessStoreIDList) throws Exception {
        return this.geAssociatedAppLicenseInfo(customerId);
    }
    
    private HashMap geAssociatedAppLicenseInfo(final long customerID) {
        final HashMap licMap = new HashMap();
        try {
            final Map appLicenseData = new AppLicenseMgmtHandler().getStoreAppLicenseDetailsForCustomer(customerID);
            for (final Map.Entry data : appLicenseData.entrySet()) {
                final Long appGroupID = data.getKey();
                final JSONObject licInf = data.getValue();
                final int totalLic = licInf.optInt("PURCHASED_COUNT");
                final int availableLic = licInf.optInt("PROVISIONED_COUNT");
                licMap.put(appGroupID, totalLic - availableLic);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return licMap;
    }
}
