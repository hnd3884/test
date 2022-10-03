package com.me.mdm.server.drp;

import java.util.Hashtable;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.settings.MdComplianceRulesHandler;
import org.json.JSONException;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import com.me.mdm.server.enrollment.MDMEnrollmentDeviceHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;
import com.me.mdm.server.apps.android.afw.usermgmt.GooglePlayDevicesSyncRequestHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;

public class AndroidMDMRegistrationHandler extends MDMRegistrationHandler
{
    @Override
    protected JSONObject processDiscoverMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String email = msgRequestJSON.optString("EmailAddress");
        final DataObject dataObject = this.getEnrollmentRequestDataObject(requestJSON, msgRequestJSON);
        final boolean enrollReqFound = !dataObject.isEmpty();
        if (!enrollReqFound) {
            final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final boolean isSelfEnroll = EnrollmentSettingsHandler.getInstance().isSelfEnrollmentEnabled(customerId);
            if (!isSelfEnroll && GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
                Long userId = (Long)DBUtil.getValueFromDB("BusinessStoreUsers", "BS_MDM_ID", (Object)email, "BS_USER_ID");
                if (userId == null) {
                    final String domainName = email.substring(email.indexOf("@") + 1);
                    final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
                    if (domainName.equalsIgnoreCase((String)playStoreDetails.get("MANAGED_DOMAIN_NAME"))) {
                        this.logger.log(Level.INFO, "If Domain name matches then we must perform one sync and test again for User");
                        new GooglePlayDevicesSyncRequestHandler(customerId).syncUser(email);
                        userId = (Long)DBUtil.getValueFromDB("BusinessStoreUsers", "BS_MDM_ID", (Object)email, "BS_USER_ID");
                    }
                }
                Logger.getLogger("MDMEnrollment").log(Level.INFO, "[Discovery] Enrolling user account is a managed GoogleForWork account, userId={0}", userId);
                String ownedBy = String.valueOf(2);
                final String doEnrollAsDeviceOwnerOnAccAdd = SyMUtil.getSyMParameter("DoEnrollAsDeviceOwnerOnAccAdd");
                if (doEnrollAsDeviceOwnerOnAccAdd != null && Boolean.getBoolean(doEnrollAsDeviceOwnerOnAccAdd)) {
                    ownedBy = String.valueOf(1);
                }
                if (userId != null) {
                    final String userName = (String)DBUtil.getValueFromDB("BusinessStoreUsers", "BS_USER_ID", (Object)userId, "BS_MDM_ID");
                    final Properties enrollProp = MDMEnrollmentUtil.getInstance().buildEnrollmentProperties("MDM", userName, MDMGroupHandler.getInstance().getDefaultMDMSelfEnrollGroupId(customerId, 2, 2), email, ownedBy, customerId, true, String.valueOf(2), false);
                    final Properties requestProperties = MDMEnrollmentRequestHandler.getInstance().addEnrollmentRequest(enrollProp);
                    final Long enrollmentRequestID = ((Hashtable<K, Long>)requestProperties).get("ENROLLMENT_REQUEST_ID");
                    final int enrollStatus = ((Hashtable<K, Integer>)requestProperties).get("ENROLL_STATUS");
                    if (enrollStatus == 1) {
                        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Enrollment request successfully added! ERID={0}", enrollmentRequestID);
                    }
                    else {
                        Logger.getLogger("MDMEnrollment").log(Level.INFO, "Enrollment request failed to add!! ERID={0}", enrollmentRequestID);
                    }
                }
            }
        }
        return super.processDiscoverMessage(requestJSON);
    }
    
    @Override
    protected JSONObject processRegistrationStatusUpdateMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String registrationType = msgRequestJSON.optString("RegistrationType", "AppRegistration");
        if (registrationType.equalsIgnoreCase("AppRegistration")) {
            final String deviceUDID = String.valueOf(msgRequestJSON.get("UDID"));
            final int platformType = 2;
            final String deviceName = String.valueOf(msgRequestJSON.get("DeviceName"));
            final Long enrollmentRequestID = msgRequestJSON.optLong("EnrollmentReqID");
            final Long customerID = MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(enrollmentRequestID);
            JSONObject hsDeviceInfoJson = null;
            if (msgRequestJSON.has("DeviceInfo")) {
                hsDeviceInfoJson = msgRequestJSON.getJSONObject("DeviceInfo");
            }
            final int agentType = 2;
            final String agentVersion = String.valueOf(msgRequestJSON.get("AgentVersion"));
            final String versionCode = String.valueOf(msgRequestJSON.get("AgentVersionCode"));
            final Long agentVersionCode = (versionCode != null) ? Long.parseLong(versionCode) : -1L;
            final JSONObject enrollJSON = new JSONObject();
            enrollJSON.put("CUSTOMER_ID", (Object)customerID);
            enrollJSON.put("UDID", (Object)deviceUDID);
            enrollJSON.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            enrollJSON.put("NAME", (Object)deviceName);
            enrollJSON.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            enrollJSON.put("MANAGED_STATUS", (Object)new Integer(1));
            enrollJSON.put("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
            enrollJSON.put("AGENT_TYPE", agentType);
            enrollJSON.put("PLATFORM_TYPE", platformType);
            enrollJSON.put("AGENT_VERSION", (Object)agentVersion);
            enrollJSON.put("AGENT_VERSION_CODE", (Object)agentVersionCode);
            final Integer enrollmentType = (Integer)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID, "ENROLLMENT_TYPE");
            if (hsDeviceInfoJson != null) {
                try {
                    enrollJSON.put("MODEL", (Object)String.valueOf(hsDeviceInfoJson.get("Model")));
                    enrollJSON.put("MODEL_NAME", (Object)String.valueOf(hsDeviceInfoJson.get("ModelName")));
                    enrollJSON.put("MODEL_TYPE", (Object)String.valueOf(hsDeviceInfoJson.get("DeviceType")));
                    enrollJSON.put("PRODUCT_NAME", (Object)String.valueOf(hsDeviceInfoJson.get("ProductName")));
                    enrollJSON.put("OS_VERSION", (Object)String.valueOf(hsDeviceInfoJson.get("OSVersion")));
                    enrollJSON.put("EAS_DEVICE_IDENTIFIER", (Object)hsDeviceInfoJson.optString("EASDeviceIdentifier", (String)null));
                    enrollJSON.put("IS_SUPERVISED", (Object)hsDeviceInfoJson.optString("IsDeviceOwner", (String)null));
                    enrollJSON.put("IS_PROFILEOWNER", (Object)hsDeviceInfoJson.optString("IsProfileOwner", (String)null));
                    enrollJSON.put("GOOGLE_PLAY_SERVICE_ID", (Object)hsDeviceInfoJson.optString("GSFAndroidID", (String)null));
                    enrollJSON.put("SERIAL_NUMBER", (Object)hsDeviceInfoJson.optString("SerialNumber", (String)null));
                    enrollJSON.put("IMEI", (Object)hsDeviceInfoJson.optString("IMEI", (String)null));
                }
                catch (final Exception ex) {}
            }
            DeviceCommandRepository.getInstance().removeAllCommandsForResource(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID), deviceUDID);
            MDMEnrollmentDeviceHandler.getInstance(enrollmentType).handlePreEnrollment(enrollJSON);
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
            if (resourceId == null) {
                ManagedDeviceHandler.getInstance().addOrUpdateManagedDevice(enrollJSON);
            }
        }
        return super.processRegistrationStatusUpdateMessage(requestJSON);
    }
    
    @Override
    protected void processPostAppRegistration(final JSONObject requestJSON) throws JSONException {
        super.processPostAppRegistration(requestJSON);
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String deviceUDID = String.valueOf(msgRequestJSON.get("UDID"));
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(resourceList);
        final String scanPersonalApps = MDMUtil.getSyMParameter("DoNotScanPersonalApps");
        if ((scanPersonalApps == null || !scanPersonalApps.equalsIgnoreCase("true")) && ManagedDeviceHandler.getInstance().isPersonalProfileManaged(resourceID)) {
            DeviceCommandRepository.getInstance().addAndAssignCommand(resourceID, "PersonalAppsInfo", 2);
            DeviceCommandRepository.getInstance().addSystemAppCommand(resourceID);
            try {
                NotificationHandler.getInstance().SendNotification(resourceList, 201);
            }
            catch (final Exception ex) {
                Logger.getLogger(AndroidMDMRegistrationHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    protected JSONObject processDeviceProvisioningMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final JSONObject messageResponseJSON = new JSONObject();
        final Long customerId = requestJSON.getJSONObject("MsgRequest").getLong("CustomerId");
        responseJSON.put("MsgResponseType", (Object)"DeviceProvisioningSettingsResponse");
        final String LAGUAGE_SETTINGS = "LanguageSettings";
        final String WAKEUP_SETTINGS = "WakeupSettings";
        final String ENROLLMENT_SETTINGS = "EnrollmentSettings";
        try {
            messageResponseJSON.put(LAGUAGE_SETTINGS, (Object)this.getLanguageSettings());
            final JSONObject wakeupJson = this.getWakeupSettings(customerId);
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            final String deviceUDID = String.valueOf(msgRequestJSON.get("UDID"));
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
            wakeupJson.put("ResourceID", (Object)resourceID);
            messageResponseJSON.put(WAKEUP_SETTINGS, (Object)wakeupJson);
            messageResponseJSON.put(ENROLLMENT_SETTINGS, (Object)this.getEnrollmentPolicy(customerId));
            final JSONObject complianceRules = MdComplianceRulesHandler.getInstance().getAndroidComplianceRules(customerId);
            final JSONObject complianceRulesConfig = new JSONObject();
            complianceRulesConfig.put("CorporateWipeOnRootedDevice", complianceRules.get("CORPORATE_WIPE_ROOTED_DEVICES"));
            messageResponseJSON.put("ComplianceRules", (Object)complianceRulesConfig);
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLogger").info("Exception while Generating DeviceProvisioning Message Response" + ex);
        }
        responseJSON.put("Status", (Object)"Acknowledged");
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        return responseJSON;
    }
    
    private JSONObject getLanguageSettings() throws JSONException {
        final JSONObject data = new JSONObject();
        final Boolean isLangPackEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
        data.put("IsLanguagePackEnabled", (Object)isLangPackEnabled);
        return data;
    }
    
    private JSONObject getWakeupSettings(final Long customerId) throws Exception {
        final JSONObject data = MDMAgentSettingsHandler.getInstance().getAndroidPushNotificationConfig(customerId);
        return data;
    }
    
    private JSONObject getEnrollmentPolicy(final Long customerId) throws Exception {
        final JSONObject data = new JSONObject();
        final JSONObject personalData = new JSONObject();
        final JSONObject workspaceData = new JSONObject();
        personalData.put("IsManageable", true);
        workspaceData.put("IsManageable", true);
        workspaceData.put("IsMandatory", false);
        workspaceData.put("DeleteProfileOnAdminDeactivation", true);
        final String doNotManagePersonal = SyMUtil.getSyMParameter("DoNotManagePersonal");
        final String doNotCreateWork = SyMUtil.getSyMParameter("DoNotCreateWorkProfile");
        final String forceWorkProfile = SyMUtil.getSyMParameter("ForceWorkProfile");
        final String doNotDeleteProfileOnUnmanage = SyMUtil.getSyMParameter("DoNotDeleteProfileOnUnmanage");
        final String createWorkProfileInSamsung = SyMUtil.getSyMParameter("CreateWorkProfileInSamsung");
        if (doNotManagePersonal != null && Boolean.parseBoolean(doNotManagePersonal)) {
            personalData.put("IsManageable", false);
        }
        if (doNotCreateWork != null && Boolean.parseBoolean(doNotCreateWork)) {
            workspaceData.put("IsManageable", false);
        }
        if (forceWorkProfile != null && Boolean.parseBoolean(forceWorkProfile)) {
            workspaceData.put("IsMandatory", true);
        }
        if (doNotDeleteProfileOnUnmanage != null && Boolean.parseBoolean(doNotDeleteProfileOnUnmanage)) {
            workspaceData.put("DeleteProfileOnAdminDeactivation", false);
        }
        if (createWorkProfileInSamsung != null) {
            workspaceData.put("CreateWorkProfileInSamsung", Boolean.parseBoolean(createWorkProfileInSamsung));
            workspaceData.put("CreateSamsungWorkProfileForPersonalOwned", (Object)MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ForceSamsungWorkProfileInPersonalOwned"));
        }
        data.put("Personalspace", (Object)personalData);
        data.put("Workspace", (Object)workspaceData);
        data.put("IsAFWIntegrated", GoogleForWorkSettings.isAFWSettingsConfigured(customerId));
        return data;
    }
}
