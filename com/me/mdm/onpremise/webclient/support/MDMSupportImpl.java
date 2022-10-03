package com.me.mdm.onpremise.webclient.support;

import java.io.File;
import com.me.mdm.server.device.DeviceFacade;
import java.util.List;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.support.SupportFileCreation;
import com.me.mdm.webclient.support.MDMSupport;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.api.MDMSupportAPI;

public class MDMSupportImpl implements MDMSupportAPI
{
    private static String fs;
    private Logger out;
    
    public MDMSupportImpl() {
        this.out = Logger.getLogger(MDMSupportImpl.class.getName());
    }
    
    public boolean uploadAgentLogs(final JSONObject data, final int waitTimeInMinutes) throws Exception {
        ArrayList<Long> agentResIDs;
        if (data.has("device_list")) {
            agentResIDs = MDMSupport.getInstance().uploadAgentLogs(data.getJSONArray("device_list"));
        }
        else {
            agentResIDs = MDMSupport.getInstance().uploadAgentLogs(this.getCompatibleFormatDevices(data));
        }
        if (SupportFileCreation.getInstance().getMdmAgentLogInitiatedCount() <= 0) {
            return false;
        }
        if (agentResIDs.isEmpty()) {
            final String supportFileStatus = "Creating support file failed";
            ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)supportFileStatus, 2);
            return false;
        }
        this.out.log(Level.INFO, "Waiting started for Agent log to get uploaded..Wait Time {0} minute(s)", waitTimeInMinutes);
        final int waitTime = waitTimeInMinutes * 12;
        final SupportFileCreation supportObj = SupportFileCreation.getInstance();
        for (int i = 0; i < waitTime; ++i) {
            Thread.sleep(5000L);
            this.out.log(Level.INFO, "MDMAGENTLOGINITIATEDCOUNT : {0} MDMAGENTLOGUPLOADEDCOUNT : {1} for retry {2} waiting for device {3}: ", new Object[] { supportObj.getMdmAgentLogInitiatedCount(), supportObj.getMdmAgentLogUplodedCount(), i, supportObj.getDeviceNameList().toString() });
            if (supportObj.getMdmAgentLogInitiatedCount() == supportObj.getMdmAgentLogUplodedCount()) {
                this.out.log(Level.INFO, "ALL Device Log uploades sucessfully");
                break;
            }
        }
        data.put("failedDeviceList", (Object)supportObj.getDeviceNameList());
        return true;
    }
    
    private JSONArray getCompatibleFormatDevices(final JSONObject data) {
        final JSONArray jsonArray = new JSONArray();
        final JSONArray existingArray = data.getJSONArray("mdmDeviceLog");
        for (int i = 0; i < existingArray.length(); ++i) {
            final JSONObject existingDeviceDetails = existingArray.getJSONObject(i);
            final JSONObject deviceDetails = new JSONObject();
            deviceDetails.put("device_id", existingDeviceDetails.get("dataId"));
            deviceDetails.put("platform_type_id", existingDeviceDetails.get("platformType"));
            deviceDetails.put("device_name", existingDeviceDetails.get("dataValue"));
            deviceDetails.put("udid", existingDeviceDetails.get("udid"));
            deviceDetails.put("customer_id", existingDeviceDetails.get("customerId"));
            jsonArray.put((Object)deviceDetails);
        }
        return jsonArray;
    }
    
    public boolean isMDMDevicesSelectedValid(final List deviceIds, final Long customerId, final Long userId) throws Exception {
        final DeviceFacade deviceFacade = new DeviceFacade();
        JSONArray jsonArray = null;
        try {
            jsonArray = deviceFacade.validateandGetDeviceDetails(deviceIds, customerId, userId);
        }
        catch (final Exception e) {
            this.out.log(Level.SEVERE, "unknown ID", e);
        }
        return jsonArray != null;
    }
    
    static {
        MDMSupportImpl.fs = File.separator;
    }
}
