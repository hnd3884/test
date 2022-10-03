package com.me.mdm.api.support;

import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.List;
import com.adventnet.sym.server.devicemanagement.framework.groupevent.GroupEventNotifier;
import java.util.logging.Level;
import com.me.mdm.server.support.SupportFileCreation;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public abstract class MDMSupportFacade
{
    Logger logger;
    
    public MDMSupportFacade() {
        this.logger = Logger.getLogger(MDMSupportFacade.class.getCanonicalName());
    }
    
    public abstract JSONObject getProductInfo() throws Exception;
    
    public JSONObject startUploadAction(final JSONObject requestJsonObject) throws Exception {
        final JSONObject msgBodyJsonObject = requestJsonObject.getJSONObject("msg_body");
        Long customerID;
        try {
            customerID = APIUtil.getCustomerID(requestJsonObject);
        }
        catch (final APIHTTPException e) {
            if (!ApiFactoryProvider.getUtilAccessAPI().isMSP() || e.toJSONObject().getString("error_code") != "COM0022") {
                throw e;
            }
            customerID = null;
        }
        final List deviceList = JSONUtil.getInstance().convertLongJSONArrayTOList(msgBodyJsonObject.getJSONArray("device_list"));
        if (msgBodyJsonObject.optBoolean("mdm_log_upload")) {
            if (deviceList.size() <= 0) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            final JSONArray device_list = new DeviceFacade().validateandGetDeviceDetails(deviceList, customerID, APIUtil.getUserID(requestJsonObject));
            msgBodyJsonObject.remove("device_list");
            msgBodyJsonObject.put("device_list", (Object)device_list);
        }
        try {
            final JSONObject jsonObject = new JSONObject();
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            SupportFileCreation.getInstance().supportFileCreationProcess(msgBodyJsonObject);
            return jsonObject;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in startUploadAction() : ", e2);
            ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)"Creating support file failed", 2);
            GroupEventNotifier.setGroupEventNotifierStatus("MDM_AGENT_LOG_UPLOAD", "failed");
            throw e2;
        }
    }
    
    public JSONObject getUploadProcessStatus() throws Exception {
        try {
            final JSONObject tempJsonObject = SupportFileCreation.getInstance().getSFCstatus();
            final JSONObject jsonObject = new JSONObject();
            final String status = String.valueOf(tempJsonObject.get("message"));
            jsonObject.put("status", (Object)status);
            JSONArray failedDeviceList = new JSONArray();
            try {
                failedDeviceList = tempJsonObject.getJSONArray("failedDeviceList");
            }
            catch (final Exception e) {
                this.logger.log(Level.INFO, "Exception while getting json array in getUploadProcessStatus()");
            }
            jsonObject.put("failed_device_list", (Object)failedDeviceList);
            return jsonObject;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in getUploadProcessStatus() : ", e2);
            throw e2;
        }
    }
    
    public JSONObject setSuccessResponse(final JSONObject jsonObject) throws Exception {
        final JSONObject responseJsonObject = new JSONObject();
        responseJsonObject.put("RESPONSE", (Object)jsonObject);
        responseJsonObject.put("status", 200);
        return responseJsonObject;
    }
    
    public JSONObject setFailedResponse(final Exception e) throws APIHTTPException {
        try {
            final JSONObject responseJsonObject = new JSONObject();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("error_code", (Object)"COM0004");
            jsonObject.put("error_description", (Object)e.getMessage());
            responseJsonObject.put("RESPONSE", (Object)jsonObject);
            responseJsonObject.put("status", 500);
            return responseJsonObject;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getChatDetails() throws APIHTTPException {
        JSONObject customerInfo = this.getCommonChatInfo();
        customerInfo = MDMApiFactoryProvider.getMDMChatAPI().getBasicChatInfo(customerInfo);
        return customerInfo;
    }
    
    private JSONObject getCommonChatInfo() {
        final JSONObject customerInfo = new JSONObject();
        try {
            String managedDevices = null;
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            if (licenseType != null && licenseType.equalsIgnoreCase("R")) {
                managedDevices = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
            }
            customerInfo.put("LICENSE_TYPE", (Object)((licenseType != null) ? licenseType : "--"));
            customerInfo.put("LICENSED_DEVICES", (Object)((managedDevices != null) ? managedDevices : "--"));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Couldn't fetch customer details for chat", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return customerInfo;
    }
}
