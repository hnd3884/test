package com.me.mdm.agent.handlers.chromeos;

import java.util.Hashtable;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;
import com.me.mdm.chrome.agent.enrollment.ChromeEnrollmentUtil;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.agent.handlers.BaseAppMessageQueueProcessor;

public class ChromeInServerAgentMsgResponseQueueProcessor extends BaseAppMessageQueueProcessor
{
    @Override
    protected void processMessage() throws Exception {
        final String messageType = this.messageRequest.messageType;
        final String udid = this.messageRequest.udid;
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(this.queueData);
        hmap.put("CUSTOMER_ID", this.queueDataObject.customerID + "");
        hmap.put("PLATFORM_TYPE", String.valueOf(4));
        if (messageType.equalsIgnoreCase("Enrollment")) {
            if (this.queueData.optString("Status") != null && this.queueData.optString("Status").equalsIgnoreCase("Acknowledged")) {
                this.logger.log(Level.SEVERE, "Enrollment success. Going to populate tables with managed status Enrolled");
                MDMEnrollmentUtil.getInstance().updateChromeResourceDetails(hmap);
                final List<String> deviceUDIDList = new ArrayList<String>();
                deviceUDIDList.add(udid);
                ChromeEnrollmentUtil.getInstance().addOrUpdateChromeDeviceStatus(this.queueDataObject.customerID, deviceUDIDList, ChromeEnrollmentUtil.CHROME_STATUS_ENROLLED);
            }
            else {
                this.logger.log(Level.SEVERE, "Enrollment failed. Going to update CHROMEDEVICEMANAGEDSTATUS table with status Unenrolled");
                final List<String> deviceUDIDList = new ArrayList<String>();
                deviceUDIDList.add(udid);
                ChromeEnrollmentUtil.getInstance().addOrUpdateChromeDeviceStatus(this.queueDataObject.customerID, deviceUDIDList, ChromeEnrollmentUtil.CHROME_STATUS_UNENROLLED);
            }
        }
        else if (messageType.equalsIgnoreCase("RemoveDevice")) {
            this.handleUnmanagedMessage(resourceID, hmap);
        }
    }
    
    private void handleUnmanagedMessage(final Long resourceID, final HashMap<String, String> hmap) throws Exception {
        String sRemarks = "Device Unmanaged";
        if (hmap.containsKey("Remarks")) {
            sRemarks = hmap.get("Remarks");
        }
        final String strUDID = hmap.get("UDID");
        final Properties properties = new Properties();
        ((Hashtable<String, String>)properties).put("UDID", strUDID);
        Integer managedstatus = 4;
        final int ownedby = ManagedDeviceHandler.getInstance().getDeviceOwnership(resourceID);
        if (InventoryUtil.getInstance().isWipedFromServer(strUDID)) {
            if (ownedby == 1) {
                sRemarks = "mdm.deprovision.old_remark";
                managedstatus = 10;
            }
            else {
                sRemarks = "mdm.deprovision.retire_remark";
                managedstatus = 11;
            }
            ((Hashtable<String, Boolean>)properties).put("WipeCmdFromServer", true);
        }
        ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedstatus);
        ((Hashtable<String, String>)properties).put("REMARKS", sRemarks);
        ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 2);
        if (ManagedDeviceHandler.getInstance().isDeviceRemoved(resourceID)) {
            DeviceCommandRepository.getInstance().clearCommandFromDevice(strUDID, resourceID, "RemoveDevice", 1);
            if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(strUDID)) {
                ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID));
            }
        }
        else {
            ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
            final JSONObject deprovisionJson = new JSONObject();
            deprovisionJson.put("RESOURCE_ID", (Object)resourceID);
            deprovisionJson.put("WIPE_PENDING", (Object)Boolean.FALSE);
            ManagedDeviceHandler.getInstance().updatedeprovisionhistory(deprovisionJson);
        }
    }
}
