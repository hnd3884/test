package com.me.mdm.uem;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import java.net.URLEncoder;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class ModernMgmtManagedDeviceListener extends ManagedDeviceListener
{
    private Logger logger;
    
    public ModernMgmtManagedDeviceListener() {
        this.logger = Logger.getLogger("MDMModernMgmtLogger");
    }
    
    @Override
    public void devicePreUserAssigned(final DeviceEvent preDeviceEvent) {
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable()) {
                final JSONObject resourceJSON = preDeviceEvent.resourceJSON;
                final Long customerID = preDeviceEvent.customerID;
                final Integer platformType = preDeviceEvent.platformType;
                final Integer templateType = resourceJSON.getInt("TEMPLATE_TYPE");
                final String templateToken = resourceJSON.get("TEMPLATE_TOKEN").toString();
                final String udid = resourceJSON.optString("UDID");
                final String imei = resourceJSON.optString("IMEI");
                final String serialNumber = resourceJSON.optString("SERIAL_NUMBER");
                final JSONObject managedUserDetails = resourceJSON.optJSONObject("MANAGED_USER_DETAILS");
                if (templateType.equals(31) && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("stopUEMPopulation")) {
                    final String zapiKey = resourceJSON.get("ZAPIKEY").toString();
                    if (managedUserDetails != null && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("stopUEMPopulation")) {
                        final String userName = managedUserDetails.get("NAME").toString();
                        final String domainName = managedUserDetails.get("DOMAIN_NETBIOS_NAME").toString();
                        final String emailAddress = managedUserDetails.get("EMAIL_ADDRESS").toString();
                        String natUrl = "";
                        try {
                            natUrl = ((Hashtable<K, String>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_ADDRESS");
                        }
                        catch (final Exception ex) {
                            this.logger.log(Level.SEVERE, "Exception while initializing NAT_URL variable in ModernMgmtManagedDeviceListener", ex);
                        }
                        final JSONObject apiRequestBodyJSON = new JSONObject();
                        apiRequestBodyJSON.put("NAME", (Object)userName);
                        apiRequestBodyJSON.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
                        apiRequestBodyJSON.put("EMAIL_ADDRESS", (Object)emailAddress);
                        apiRequestBodyJSON.put("TEMPLATE_TOKEN", (Object)templateToken);
                        apiRequestBodyJSON.put("ZAPIKEY", (Object)zapiKey);
                        apiRequestBodyJSON.put("CUSTOMER_ID", (Object)customerID);
                        apiRequestBodyJSON.put("MDM_CUSTOMER_ID", (Object)customerID);
                        apiRequestBodyJSON.put("PLATFORM_TYPE", (Object)platformType);
                        apiRequestBodyJSON.put("ServerUrl", (Object)natUrl);
                        if (!MDMStringUtils.isEmpty(udid)) {
                            apiRequestBodyJSON.put("UDID", (Object)udid);
                        }
                        if (!MDMStringUtils.isEmpty(imei)) {
                            apiRequestBodyJSON.put("IMEI", (Object)imei);
                        }
                        if (!MDMStringUtils.isEmpty(serialNumber)) {
                            apiRequestBodyJSON.put("SERIAL_NUMBER", (Object)serialNumber);
                        }
                        apiRequestBodyJSON.put("uemPlatformType", (Object)platformType);
                        final JSONObject responseJSON = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.RESOURCE_USER_ASSIGNED, apiRequestBodyJSON);
                        if (responseJSON != null && responseJSON.has("isSuccessfull") && responseJSON.getBoolean("isSuccessfull")) {
                            this.logger.log(Level.FINE, "Successfully posted the assign user details to DC to submit NSRequest for legacy agent with details {0}", apiRequestBodyJSON.toString());
                        }
                        else {
                            this.logger.log(Level.SEVERE, "Error while posting assign user details to DC to submit NSRequest for legacy agent with details {0}", apiRequestBodyJSON.toString());
                        }
                    }
                }
                else if (templateType.equals(12) && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("stopUEMPopulation") && managedUserDetails != null) {
                    final JSONObject apiRequestBodyJSON2 = new JSONObject();
                    apiRequestBodyJSON2.put("CUSTOMER_ID", (Object)customerID);
                    apiRequestBodyJSON2.put("PLATFORM_TYPE", (Object)platformType);
                    if (!MDMStringUtils.isEmpty(udid)) {
                        apiRequestBodyJSON2.put("UDID", (Object)udid);
                    }
                    if (!MDMStringUtils.isEmpty(imei)) {
                        apiRequestBodyJSON2.put("IMEI", (Object)imei);
                    }
                    if (!MDMStringUtils.isEmpty(serialNumber)) {
                        apiRequestBodyJSON2.put("SERIAL_NUMBER", (Object)serialNumber);
                    }
                    apiRequestBodyJSON2.put("TEMPLATE_TOKEN", (Object)templateToken);
                    final String sServerBaseURL = MDMEnrollmentUtil.getInstance().getServerBaseURL();
                    apiRequestBodyJSON2.put("ServerUrl", (Object)URLEncoder.encode(sServerBaseURL + resourceJSON.get("MDM_URL"), "UTF-8"));
                    apiRequestBodyJSON2.put("Authorization-header-password", resourceJSON.get("PASSWORD"));
                    apiRequestBodyJSON2.put("Authorization-header-username", resourceJSON.get("USERNAME"));
                    apiRequestBodyJSON2.put("uemPlatformType", (Object)platformType);
                    final JSONObject responseJSON2 = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.RESOURCE_USER_ASSIGNED, apiRequestBodyJSON2);
                    if (responseJSON2 != null && responseJSON2.has("isSuccessfull") && responseJSON2.getBoolean("isSuccessfull")) {
                        this.logger.log(Level.FINE, "Successfully posted the assign user details to DC to submit NSRequest for legacy agent with details {0}", apiRequestBodyJSON2.toString());
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Error while posting assign user details to DC to submit NSRequest for legacy agent with details {0}", apiRequestBodyJSON2.toString());
                    }
                }
            }
            else {
                this.logger.log(Level.INFO, "Skipping for non DC Instance");
            }
        }
        catch (final JSONException ex2) {
            this.logger.log(Level.SEVERE, "JSONException in ModernMgmtManagedDeviceListener", (Throwable)ex2);
        }
        catch (final Exception ex3) {
            this.logger.log(Level.SEVERE, "Exception in ModernMgmtManagedDeviceListener", ex3);
        }
    }
    
    @Override
    public void deviceManaged(final DeviceEvent managedDeviceEvent) {
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable()) {
                Long managedDeviceId = null;
                String serialNumber = null;
                String udid = null;
                Integer platformType = null;
                Integer resourceType = null;
                final JSONObject resourceDetailsJSON = managedDeviceEvent.resourceJSON;
                if (resourceDetailsJSON != null && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("stopUEMPopulation")) {
                    resourceType = resourceDetailsJSON.optInt("RESOURCE_TYPE");
                    platformType = resourceDetailsJSON.optInt("PLATFORM_TYPE");
                    if (resourceType.equals(121) && (platformType.equals(3) || platformType.equals(1))) {
                        managedDeviceId = resourceDetailsJSON.getLong("RESOURCE_ID");
                        serialNumber = resourceDetailsJSON.get("SerialNumber").toString();
                        udid = resourceDetailsJSON.get("UDID").toString();
                        final JSONObject apiRequestBodyJSON = new JSONObject();
                        final JSONObject deviceDetails = new JSONObject();
                        deviceDetails.put("SerialNumberList", (Object)new JSONArray().put((Object)serialNumber));
                        final JSONObject updateDetails = new JSONObject();
                        updateDetails.put("SERIAL_NUMBER", (Object)serialNumber);
                        updateDetails.put("RESOURCE_ID", (Object)managedDeviceId);
                        updateDetails.put("UDID", (Object)udid);
                        ModernMgmtDeviceForEnrollmentHandler.addModernDetails(updateDetails, managedDeviceId);
                        deviceDetails.put("DeviceUpdateDetails", (Object)new JSONArray().put((Object)updateDetails));
                        apiRequestBodyJSON.put("DeviceDetails", (Object)deviceDetails);
                        apiRequestBodyJSON.put("uemPlatformType", (Object)platformType);
                        this.logger.log(Level.FINE, "Data Being posted to Legacy management for device managed is {0}", apiRequestBodyJSON);
                        final JSONObject responseJSON = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.DEVICE_MANAGED, apiRequestBodyJSON);
                        if (responseJSON != null && responseJSON.has("isSuccessfull") && responseJSON.getBoolean("isSuccessfull")) {
                            this.logger.log(Level.FINE, "Successfully posted the assign user details to DC to submit NSRequest for legacy agent with details {0}", apiRequestBodyJSON.toString());
                            if (resourceType.equals(121)) {
                                final List resList = new ArrayList();
                                resList.add(resourceDetailsJSON.getLong("RESOURCE_ID"));
                                ModernMgmtHandler.getInstance(platformType).addAgentInstallationCommand(resList, managedDeviceEvent.customerID);
                            }
                        }
                        else {
                            this.logger.log(Level.SEVERE, "Error while posting assign user details to DC to submit NSRequest for legacy agent with details {0}", apiRequestBodyJSON.toString());
                        }
                    }
                }
            }
            else {
                this.logger.log(Level.INFO, "Skipping for non DC Instance");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while obtaining technician list for given event " + deviceEvent.udid);
        }
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent managedDeviceEvent) {
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable()) {
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("stopUEMPopulation")) {
                    final JSONObject apiRequestBodyJSON = new JSONObject().put("RESOURCE_ID", managedDeviceEvent.resourceJSON.getLong("RESOURCE_ID"));
                    final JSONObject responseJSON = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.ADDORUPDATE_MAPPINGTABLE, apiRequestBodyJSON);
                    if (responseJSON != null && responseJSON.has("isSuccessfull") && responseJSON.getBoolean("isSuccessfull")) {
                        this.logger.log(Level.FINE, "Successfully posted the assign user details to DC to submit NSRequest for legacy agent with details {0}", apiRequestBodyJSON.toString());
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Error while posting assign user details to DC to submit NSRequest for legacy agent with details {0}", apiRequestBodyJSON.toString());
                    }
                }
            }
            else {
                this.logger.log(Level.INFO, "Skipping for non DC Instance");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in ModernMgmtManagedDeviceListener. ManagedDeviceId {0}", new Object[] { managedDeviceEvent.resourceID });
        }
    }
    
    @Override
    public void deviceDeprovisioned(final DeviceEvent deviceEvent) {
        final JSONObject jsonObject = new JSONObject();
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable() && ModernDeviceUtil.isModernManagementCapableResource(deviceEvent.resourceID)) {
                ModernMgmtDeviceForEnrollmentHandler.addModernDetails(jsonObject, deviceEvent.resourceJSON.getLong("RESOURCE_ID"));
                jsonObject.put("RESOURCE_ID", (Object)deviceEvent.resourceID);
                jsonObject.put("LAST_CONTACT_TIME", System.currentTimeMillis());
                this.logger.log(Level.INFO, "deviceDeprovisioned listner for MM device called JSON posted is   {0}", jsonObject);
                MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.UPDATE_LAST_CONTACT, jsonObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in ModernMgmtManagedDeviceListener ManagedDeviceId {0}", new Object[] { deviceEvent.resourceID });
        }
    }
    
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        final JSONObject jsonObject = new JSONObject();
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable() && ModernDeviceUtil.isModernManagementCapableResource(deviceEvent.resourceID)) {
                ModernMgmtDeviceForEnrollmentHandler.addModernDetails(jsonObject, deviceEvent.resourceJSON.getLong("RESOURCE_ID"));
                jsonObject.put("RESOURCE_ID", (Object)deviceEvent.resourceID);
                jsonObject.put("LAST_CONTACT_TIME", System.currentTimeMillis());
                this.logger.log(Level.INFO, "deviceRegistered listner for MM device called JSON posted is   {0}", jsonObject);
                MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.UPDATE_LAST_CONTACT, jsonObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in ModernMgmtManagedDeviceListener ManagedDeviceId {0}", new Object[] { deviceEvent.resourceID });
        }
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent managedDeviceEvent) {
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isModernMgmtCapable() && ModernDeviceUtil.isModernManagementCapableResource(managedDeviceEvent.resourceID)) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("RESOURCE_ID", (Object)managedDeviceEvent.resourceID);
                ModernMgmtDeviceForEnrollmentHandler.addModernDetails(jsonObject, managedDeviceEvent.resourceID);
                jsonObject.put("LAST_CONTACT_TIME", System.currentTimeMillis());
                this.logger.log(Level.INFO, "Unmanaged listner for MM device called JSON posted is   {0}", jsonObject);
                final JSONObject responseJSON = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.UPDATE_LAST_CONTACT, jsonObject);
                if (responseJSON != null && responseJSON.has("isSuccessfull") && responseJSON.getBoolean("isSuccessfull")) {
                    this.logger.log(Level.FINE, "Successfully posted the assign user details to DC to submit NSRequest for legacy agent with details {0}", jsonObject.toString());
                }
                else {
                    this.logger.log(Level.SEVERE, "Error while posting assign user details to DC to submit NSRequest for legacy agent with details {0}", jsonObject.toString());
                }
                MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.ADDORUPDATE_MAPPINGTABLE, new JSONObject().put("RESOURCE_ID", (Object)managedDeviceEvent.resourceID));
            }
            else {
                this.logger.log(Level.INFO, "Skipping for non DC Instance");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in ModernMgmtManagedDeviceListener ManagedDeviceId {0}", new Object[] { managedDeviceEvent.resourceID });
        }
    }
}
