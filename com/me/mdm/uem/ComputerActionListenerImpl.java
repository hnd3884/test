package com.me.mdm.uem;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.NoSuchElementException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.dd.plist.Base64;
import com.me.mdm.server.dep.AdminEnrollmentHandler;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.enroll.DeviceManagedDetailsHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.enrollment.deprovision.DeprovisionRequest;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.stageddevice.ModernMgmtStagedDeviceFacade;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ComputerActionListenerImpl
{
    private Logger logger;
    
    public ComputerActionListenerImpl() {
        this.logger = Logger.getLogger("MDMModernMgmtLogger");
    }
    
    public JSONObject addOrUpdateDeviceStagedForModernMgmt(final JSONObject params) {
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final int platformType = params.getInt("uemPlatformType");
            returnObj = ModernMgmtStagedDeviceFacade.getInstance(platformType).addOrUpdateDeviceStagedForModernMgmt(params);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject deprovisionDevice(final JSONObject params) {
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            Long custId = params.has("CUSTOMER_ID") ? Long.valueOf(params.getLong("CUSTOMER_ID")) : null;
            if (custId == null) {
                custId = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(params.getLong("managedDeviceId")));
            }
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(params.getLong("managedDeviceId"));
            final DeprovisionRequest deprovisionRequest = new DeprovisionRequest(custId, null, params.getInt("wipeType"), params.getInt("wipeReason"), params.get("otherReason").toString(), resourceList);
            returnObj = ManagedDeviceHandler.getInstance().deprovisionDevice(deprovisionRequest);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject deleteDeviceStagedForModernMgmt(final JSONObject params) {
        JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final int platformType = params.getInt("uemPlatformType");
            returnObj = ModernMgmtStagedDeviceFacade.getInstance(platformType).deleteDeviceStagedForModernMgmt(params);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject addOrUpdateStatus(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            new DeviceForEnrollmentHandler().addOrUpdateStatus(params);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject getPreRequisites(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final Boolean apnsConfigured = MDMEnrollmentUtil.getInstance().isAPNsConfigured();
            returnObj.put("apns_configured", (Object)apnsConfigured);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, null, e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, null, (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, null, (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject updateDeviceHistoryTable(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            new DeviceManagedDetailsHandler().updateDeviceManagedHistoryTable(params);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in ComputerActionListenerImpl:updateDeviceHistoryTable", e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, "Exception while forming returnObj in ComputerActionListenerImpl:updateDeviceHistoryTable", (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, "Exception while forming returnObj in ComputerActionListenerImpl:updateDeviceHistoryTable", (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject getNoOfDevicesConsumedBetweenMillis(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final Long noOfDevices = new DeviceManagedDetailsHandler().getNoOfDevicesConsumedBetweenMillis(params);
            returnObj.put("noOfDevices", (Object)noOfDevices);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in ComputerActionListenerImpl:getNoOfDevicesConsumedBetweenMillis", e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, "Exception while updating returnObj in ComputerActionListenerImpl:getNoOfDevicesConsumedBetweenMillis", (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, "Exception while updating returnObj in ComputerActionListenerImpl:getNoOfDevicesConsumedBetweenMillis", (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject getDeviceDetailsForBilling(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            final JSONArray devicesArray = new DeviceManagedDetailsHandler().getDevicesHistoryForBilling(params);
            returnObj.put("devicesArray", (Object)devicesArray);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in ComputerActionListenerImpl:getDeviceDetailsForBilling", e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, "Exception while updating returnObj in ComputerActionListenerImpl:getDeviceDetailsForBilling", (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, "Exception while updating returnObj in ComputerActionListenerImpl:getDeviceDetailsForBilling", (Throwable)e3);
            }
        }
        return returnObj;
    }
    
    public JSONObject getMacMobileConfig(final JSONObject requestParams) {
        final JSONObject responseJson = new JSONObject();
        try {
            final String serialNumber = requestParams.getString("SERIAL_NUMBER");
            final String udid = requestParams.getString("UDID");
            final long customerId = requestParams.getLong("CUSTOMER_ID");
            this.logger.log(Level.INFO, "Incoming request to obtain mobile config: {0}, {1}, {2}", new Object[] { serialNumber, udid, customerId });
            final String templateToken = this.getTemplateToken(customerId);
            if (MDMEnrollmentUtil.getInstance().isAPNsConfigured() && MDMEnrollmentUtil.getInstance().isNATConfigured()) {
                this.logger.log(Level.INFO, "Prerequisites fulfilled: {0}, {1}, {2}", new Object[] { serialNumber, udid, customerId });
                final JSONObject requestObj = this.createDepRequest(serialNumber, udid, templateToken);
                this.logger.log(Level.INFO, "ComputerActionListenerImpl : Request Object:{0}", requestObj);
                final JSONObject responseJSON = new AdminEnrollmentHandler().processDeviceProvisioningMessageForMacModernMgmt(requestObj);
                final String status = String.valueOf(responseJSON.get("Status"));
                if (status.equals("Acknowledged")) {
                    this.logger.log(Level.INFO, "Mobile config successfully created for: {0}, {1}, {2}", new Object[] { serialNumber, udid, customerId });
                    final JSONObject msgResponseJSON = responseJSON.getJSONObject("MsgResponse");
                    final String mobileConfigEncodedContent = String.valueOf(msgResponseJSON.get("MobileConfigContent"));
                    final byte[] mobileConfigDecodedContent = Base64.decode(mobileConfigEncodedContent);
                    responseJson.put("IS_SUCCESS", true);
                    responseJson.put("MOBILE_CONFIG", (Object)mobileConfigDecodedContent);
                    this.logger.log(Level.INFO, "ComputerActionListenerImpl : Mobile config sent as response for: {0}, {1}, {2}", new Object[] { serialNumber, udid, customerId });
                }
            }
            else {
                responseJson.put("IS_SUCCESS", false);
                this.logger.log(Level.SEVERE, "MacModernMgmt : Pre-requisites are not yet fulfilled. But agent is trying to enroll: {0}, {1}, {2}", new Object[] { serialNumber, udid, customerId });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MacModernMgmt : Exception while sending MDM mobile config.", e);
            responseJson.put("IS_SUCCESS", false);
        }
        return responseJson;
    }
    
    private JSONObject createDepRequest(final String serialNumber, final String udid, final String templateToken) {
        final JSONObject messageRequest = this.createMessageRequest(serialNumber, udid, templateToken);
        final JSONObject requestObj = new JSONObject();
        requestObj.put("MsgRequest", (Object)messageRequest);
        requestObj.put("DevicePlatform", (Object)"iOS");
        requestObj.put("isUntrustedDeviceAllowed", false);
        this.logger.log(Level.INFO, "Dep request created: {0}, {1}, {2}", new Object[] { serialNumber, udid });
        return requestObj;
    }
    
    private JSONObject createMessageRequest(final String serialNumber, final String udid, final String templateToken) {
        final JSONObject messageRequest = new JSONObject();
        messageRequest.put("SerialNumber", (Object)serialNumber);
        messageRequest.put("TemplateToken", (Object)templateToken);
        messageRequest.put("UDID", (Object)udid);
        messageRequest.put("DeviceType", MDMInvDataPopulator.getModelType("Mac"));
        messageRequest.put("IMEI", (Object)"--");
        return messageRequest;
    }
    
    private String getTemplateToken(final long customerId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        selectQuery.addSelectColumn(new Column("EnrollmentTemplate", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(new Column("EnrollmentTemplate", "TEMPLATE_TOKEN"));
        final Criteria customerIdCriteria = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria templateTypeCriteria = new Criteria(new Column("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)12, 0);
        selectQuery.setCriteria(customerIdCriteria.and(templateTypeCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            throw new NoSuchElementException();
        }
        return (String)dataObject.getFirstRow("EnrollmentTemplate").get("TEMPLATE_TOKEN");
    }
    
    public JSONObject getModernComputerDetails(final JSONObject params) {
        final JSONObject returnObj = new JSONObject();
        Boolean isSuccessful = false;
        try {
            new ModernMgmtDeviceForEnrollmentHandler();
            final JSONArray devicesArray = ModernMgmtDeviceForEnrollmentHandler.getModernDetails(params.getJSONArray("device_list"));
            returnObj.put("devicesArray", (Object)devicesArray);
            isSuccessful = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in ComputerActionListenerImpl:getDeviceDetailsForBilling", e);
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e2) {
                this.logger.log(Level.SEVERE, "Exception while updating returnObj in ComputerActionListenerImpl:getDeviceDetailsForBilling", (Throwable)e2);
            }
        }
        finally {
            try {
                returnObj.put("isSuccessfull", (Object)isSuccessful);
            }
            catch (final JSONException e3) {
                this.logger.log(Level.SEVERE, "Exception while updating returnObj in ComputerActionListenerImpl:getDeviceDetailsForBilling", (Throwable)e3);
            }
        }
        return returnObj;
    }
}
