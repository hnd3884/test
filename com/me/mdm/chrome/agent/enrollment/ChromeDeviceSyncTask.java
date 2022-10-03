package com.me.mdm.chrome.agent.enrollment;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleUsersDirectory;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import java.util.Properties;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ChromeDeviceSyncTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    private void syncChromeDevices(final Long customerID, final JSONObject googleESAJSON) throws Exception {
        CustomerParamsHandler.getInstance().addOrUpdateParameter("ChromeDeviceSyncStatus", "InProgress", (long)customerID);
        googleESAJSON.put("TEMPLATE_TOKEN", (Object)new EnrollmentTemplateHandler().getTemplateTokenForUserId(googleESAJSON.getLong("ADDED_BY"), 40, customerID));
        new ChromeDeviceEnrollmentHandler(googleESAJSON).syncChromeDevices();
        CustomerParamsHandler.getInstance().addOrUpdateParameter("ChromeDeviceSyncStatus", "Completed", (long)customerID);
    }
    
    public void executeTask(final Properties taskProps) {
        final Long customerID = ((Hashtable<K, Long>)taskProps).get("CUSTOMER_ID");
        try {
            final JSONObject googleESAJSON = GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
            final Boolean isConfigured = googleESAJSON.getBoolean("isConfigured");
            final String enterpriseID = googleESAJSON.getString("ENTERPRISE_ID");
            final String curStatus = CustomerParamsHandler.getInstance().getParameterValue("ChromeDeviceSyncStatus", (long)customerID);
            if (isConfigured && (MDMStringUtils.isEmpty(curStatus) || !curStatus.equals("InProgress"))) {
                if (!SyMUtil.isStringEmpty(enterpriseID)) {
                    this.syncChromeDevices(customerID, googleESAJSON);
                }
                else if (taskProps.containsKey("RE_INTEG")) {
                    final boolean reInteg = Boolean.valueOf(String.valueOf(((Hashtable<K, Object>)taskProps).get("RE_INTEG")));
                    if (reInteg) {
                        final GoogleUsersDirectory userDirectory = new GoogleUsersDirectory();
                        userDirectory.initialize(googleESAJSON);
                        final boolean updated = userDirectory.updateEnterpriseID(googleESAJSON);
                        if (updated) {
                            this.syncChromeDevices(customerID, googleESAJSON);
                        }
                        else {
                            CustomerParamsHandler.getInstance().addOrUpdateParameter("ChromeDeviceSyncStatus", "Failed", (long)customerID);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            try {
                CustomerParamsHandler.getInstance().addOrUpdateParameter("ChromeDeviceSyncStatus", "Failed", (long)customerID);
            }
            catch (final Exception e2) {
                ChromeDeviceSyncTask.logger.log(Level.SEVERE, "Exception While syncing chrome devices (Exception in updating params)", e2);
            }
            ChromeDeviceSyncTask.logger.log(Level.SEVERE, "Exception While syncing chrome devices", e);
        }
    }
    
    static {
        ChromeDeviceSyncTask.logger = Logger.getLogger("MDMEnrollment");
    }
}
