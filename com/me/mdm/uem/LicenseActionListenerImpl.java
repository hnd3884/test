package com.me.mdm.uem;

import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public class LicenseActionListenerImpl
{
    private static final Logger LOGGER;
    
    public static JSONObject getMDMUsageDetails() {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final int managedComputerCount = LicenseProvider.getInstance().getMDMLicenseAPI().getManagedDeviceCount();
            final String planType = LicenseProvider.getInstance().getLicenseType();
            returnObj.put("managedCount", managedComputerCount);
            returnObj.put("planType", (Object)planType);
            isSuccessful = true;
        }
        catch (final Exception e) {
            LicenseActionListenerImpl.LOGGER.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                LicenseActionListenerImpl.LOGGER.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                LicenseActionListenerImpl.LOGGER.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public static void moveMDMDevicesForAwaitingForLicense() {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedDevice");
            updateQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            updateQuery.setUpdateColumn("MANAGED_STATUS", (Object)6);
            SyMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception e) {
            LicenseActionListenerImpl.LOGGER.log(Level.SEVERE, "Error while moving device to awaiting to license", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMModernMgmtLogger");
    }
}
