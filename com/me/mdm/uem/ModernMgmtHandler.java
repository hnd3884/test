package com.me.mdm.uem;

import com.me.mdm.uem.mac.MacModernMgmtHandler;
import com.me.mdm.uem.windows.WindowsModernMgmtHandler;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collection;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONObject;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public abstract class ModernMgmtHandler
{
    public static Logger mdmEnrollmentLogger;
    public static final String IS_MANAGED_ENDPOINT = "isManagedEndPoint";
    
    public List addAgentInstallationCommand(final List resourceList, final Long customerID) throws Exception {
        ModernMgmtHandler.mdmEnrollmentLogger.log(Level.INFO, "Going to Add Legacy agent Installtion Command For the Resources {0}", resourceList);
        final HashMap deviceUniqueProps = ManagedDeviceHandler.getUniquePropsFromResource(resourceList);
        ModernMgmtHandler.mdmEnrollmentLogger.log(Level.INFO, "Unique props for the resource are  {0}", deviceUniqueProps);
        final Iterator iterator = deviceUniqueProps.keySet().iterator();
        final List moveToWaitingList = new ArrayList();
        while (iterator.hasNext()) {
            final Long resourceID = iterator.next();
            final JSONObject commandJSON = deviceUniqueProps.get(resourceID);
            commandJSON.put("CUSTOMER_ID", (Object)customerID);
            this.prepareCommandJSON(commandJSON);
            final JSONObject response = this.getAgentProps(commandJSON);
            ModernMgmtHandler.mdmEnrollmentLogger.log(Level.WARNING, "DC Props for installing legacy agent in device:{0}", response);
            ModernMgmtHandler.mdmEnrollmentLogger.log(Level.INFO, "Obtained the agent installtion props for the resource  {0}", commandJSON);
            if (response != null) {
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("StopCoMgmt") || response.optBoolean("isManagedEndPoint", (boolean)Boolean.FALSE)) {
                    continue;
                }
                if (!LicenseProvider.getInstance().getMDMLicenseAPI().isMDMLicenseLimitExceed()) {
                    response.put("CUSTOMER_ID", (Object)customerID);
                    this.addAgentInstallCommand(resourceID, response);
                    ModernMgmtHandler.mdmEnrollmentLogger.log(Level.INFO, "command added for the resource, DC agent installation will begin in the next device check-in {0}", resourceID);
                }
                else {
                    moveToWaitingList.add(resourceID);
                }
                final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
                if (isMsp) {
                    continue;
                }
                final boolean isEndpointServiceEnabled = LicenseProvider.getInstance().isEndpointServiceEnabled();
                final String licenseType = LicenseProvider.getInstance().getLicenseType();
                final boolean isTrialCustomer = licenseType.equalsIgnoreCase("T");
                final boolean isFreeCustomer = licenseType.equalsIgnoreCase("F");
                final boolean isUemLicenseSatisfied = isEndpointServiceEnabled || isTrialCustomer || isFreeCustomer;
                if (isUemLicenseSatisfied) {
                    continue;
                }
                moveToWaitingList.add(resourceID);
            }
            else {
                ModernMgmtHandler.mdmEnrollmentLogger.log(Level.WARNING, "Empty response received from DC, probably because agent is not saved");
            }
        }
        this.preMoveToWaitingForLicense(moveToWaitingList);
        if (moveToWaitingList.size() > 0) {
            ManagedDeviceHandler.getInstance().updateManagedDeviceStatus(moveToWaitingList, 6);
            ModernMgmtHandler.mdmEnrollmentLogger.log(Level.INFO, "The list of devices were Moved to Waiting for license state :  {0} , reason : UEM License limit exceeded", moveToWaitingList);
        }
        final List returnList = new ArrayList(resourceList);
        returnList.removeAll(moveToWaitingList);
        return returnList;
    }
    
    protected void prepareCommandJSON(final JSONObject commandJSON) throws Exception {
        if (commandJSON.has("SerialNumber")) {
            commandJSON.put("ServiceTag", commandJSON.get("SerialNumber"));
        }
    }
    
    protected abstract JSONObject getAgentProps(final JSONObject p0);
    
    protected abstract void addAgentInstallCommand(final Long p0, final JSONObject p1);
    
    protected void preMoveToWaitingForLicense(final List resList) {
        ModernMgmtHandler.mdmEnrollmentLogger.log(Level.INFO, "Going to move the following resources to waiting for license State : {0}", resList);
    }
    
    public static ModernMgmtHandler getInstance(final int platform) {
        ModernMgmtHandler modernMgmtHandler = null;
        if (platform == 3) {
            modernMgmtHandler = new WindowsModernMgmtHandler();
        }
        else if (platform == 1) {
            modernMgmtHandler = new MacModernMgmtHandler();
        }
        return modernMgmtHandler;
    }
    
    static {
        ModernMgmtHandler.mdmEnrollmentLogger = Logger.getLogger("MDMEnrollment");
    }
}
