package com.me.mdm.server.customer;

import java.util.Map;
import com.me.mdm.server.settings.MDAgentCommunicationModeData;
import java.util.Properties;
import com.me.mdm.server.apps.appupdatepolicy.ExistingStoreAppPolicyHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.emsalerts.notifications.core.MediumDAOUtil;
import java.util.LinkedHashMap;
import com.me.emsalerts.notifications.core.TemplatesDAOUtil;
import java.util.HashMap;
import com.me.mdm.server.alerts.AlertConstants;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import com.me.mdm.server.dep.DEPTechnicianUserListener;
import com.adventnet.sym.server.mdm.certificates.scep.ScepRootCAGenerator;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.me.mdm.server.settings.DownloadSettingsHandler;
import com.adventnet.sym.server.mdm.command.smscommand.SmsDbHandler;
import com.me.mdm.server.msp.sync.SyncConfigurationListeners;
import com.me.mdm.server.settings.MdComplianceRulesHandler;
import com.me.mdm.server.settings.MdAgentDownloadInfoData;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.agent.handlers.MacMDMAgentHandler;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.sym.server.mdm.android.AndroidAgentSettingsHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import java.util.logging.Level;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerListener;

public class GeneralCustomerListenerMDMImpl implements CustomerListener
{
    private static Logger logger;
    
    public void customerAdded(final CustomerEvent customerEvent) {
        try {
            final Long customerId = customerEvent.customerID;
            final Long userId = MDMUtil.getAdminUserId();
            IosNativeAppHandler.getInstance().addIosAgentAsync(customerId, userId);
            AppsUtil.getInstance().addiOSSystemAppToAppGroup(customerId);
            AppsUtil.getInstance().addWindowSystemAppToAppGroup(customerId);
            final JSONObject json = new JSONObject();
            json.put("AUTH_MODE", 1);
            json.put("CUSTOMER_ID", (Object)customerEvent.customerID);
            try {
                EnrollmentSettingsHandler.getInstance().addOrUpdateInvitationEnrollmentSettings(json);
            }
            catch (final Exception ex) {
                if (ex.getMessage() == null || !ex.getMessage().contains("Device Token Could not be generated Contact Support")) {
                    throw ex;
                }
                GeneralCustomerListenerMDMImpl.logger.log(Level.SEVERE, "Could Not generate OrgAPIKEY !");
            }
            final Properties locationSettingsProp = LocationSettingsDataHandler.getInstance().getDefaultLocationSettingsProp();
            LocationSettingsDataHandler.getInstance().addorUpdateLocationSettings(customerId, locationSettingsProp);
            AndroidAgentSettingsHandler.getInstance().addDefaultAndroidSettings(customerId);
            MDMUtil.getInstance().adddefaultAppSettings(customerId);
            AppVersionDBUtil.getInstance().addDefaultAppReleaseLabels(customerId);
            final MacMDMAgentHandler macHanndler = new MacMDMAgentHandler();
            macHanndler.addMacMDMAgentAsynchronously(customerId);
            MessageProvider.getInstance().hideMessage("APPLE_CONFIG_UPDATE", customerId);
            MessageProvider.getInstance().hideMessage("VPP_ABOUT_TO_EXPIRE", customerId);
            MessageProvider.getInstance().hideMessage("VPP_EXPIRED", customerId);
            MessageProvider.getInstance().hideMessage("VPP_REVOKED", customerId);
            MessageProvider.getInstance().hideMessage("AET_ABOUT_TO_EXPIRE", customerId);
            MessageProvider.getInstance().hideMessage("AET_EXPIRED", customerId);
            MessageProvider.getInstance().hideMessage("CERT_ABOUT_TO_EXPIRE", customerId);
            MessageProvider.getInstance().hideMessage("CERT_EXPIRED", customerId);
            MessageProvider.getInstance().hideMessage("NEW_WINDOWS_APP", customerId);
            MessageProvider.getInstance().hideMessage("WP_APP_NOT_ADDED", customerId);
            MessageProvider.getInstance().hideMessage("WP_APP_NOT_PURCHASED", customerId);
            MessageProvider.getInstance().hideMessage("VPP_USED_IN_OTHER_MDM", customerId);
            MessageProvider.getInstance().hideMessage("ASSIST_AUTH_FAILED", customerId);
            MessageProvider.getInstance().hideMessage("DEP_EXPIRED_MSG", customerId);
            MessageProvider.getInstance().hideMessage("DEP_ABOUT_TO_EXPIRE_MSG", customerId);
            MessageProvider.getInstance().hideMessage("VPP_EXPIRED_OR_ABOUT_TO_EXPIRE", customerId);
            MessageProvider.getInstance().hideMessage("WIN_APP_MGMT_NOT_CONFIGURED", customerId);
            MessageProvider.getInstance().hideMessage("BUSINESS_STORE_NOT_CONFIGURED", customerId);
            MessageProvider.getInstance().hideMessage("BUSINESS_STORE_PROMO", customerId);
            MessageProvider.getInstance().hideMessage("MSP_DEVICE_ALLOCATION_LIMIT_REACHED", customerId);
            MessageProvider.getInstance().hideMessage("PROFILE_UPDATE_COUNT", customerId);
            MessageProvider.getInstance().hideMessage("MDM_DEVICE_LICENSE_PERCENT_ALERT", customerId);
            MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", customerId);
            MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", customerId);
            MessageProvider.getInstance().hideMessage("LOST_DEVICE_FOUND_MSG", customerId);
            MessageProvider.getInstance().hideMessage("REENROLL_AUTH_TOKEN_DEVICE", customerId);
            final MDMAgentSettingsHandler agentSettings = MDMAgentSettingsHandler.getInstance();
            final MdAgentDownloadInfoData androidData = new MdAgentDownloadInfoData();
            androidData.addCustomerID(customerId).addPlatform(2).addDownloadMode(agentSettings.getAndroidDefaultDownloadMode());
            final MdAgentDownloadInfoData windowsData = new MdAgentDownloadInfoData();
            windowsData.addCustomerID(customerId).addPlatform(3).addDownloadMode(2);
            final MdAgentDownloadInfoData[] mdAgentDataArr = { androidData, windowsData };
            agentSettings.addMDAgentDownloadInfo(mdAgentDataArr);
            final MDAgentCommunicationModeData[] notificationData = agentSettings.getDefaultAgentCommunicationMode(customerId);
            agentSettings.addOrUpdateCommunicationMode(notificationData);
            MdComplianceRulesHandler.getInstance().addDefaultComplianceRules(customerId);
            SyncConfigurationListeners.invokeListenersOnCustomerCreation(customerId);
            try {
                final SmsDbHandler smsDbHandler = new SmsDbHandler();
                smsDbHandler.generateAndPublishKeys(customerId);
            }
            catch (final Exception e) {
                GeneralCustomerListenerMDMImpl.logger.log(Level.SEVERE, "Error occured while generating the keys");
                GeneralCustomerListenerMDMImpl.logger.log(Level.SEVERE, e.toString());
            }
            DownloadSettingsHandler.getInstance().addDefaultDownloadSettingsForAgent(customerId);
            new UserAssignmentRuleHandler().postUserAssignmentSettingsforCustomer(customerId, Boolean.TRUE);
            try {
                Logger.getLogger("MDMIosEnrollmentClientCertificateLogger").log(Level.INFO, "GeneralCustomerListenerMDMImpl: Creating Root ca cert for customer: {0}", new Object[] { customerId });
                ScepRootCAGenerator.getInstance().generateRootCACertificateForCustomer(customerId);
                Logger.getLogger("MDMIosEnrollmentClientCertificateLogger").log(Level.INFO, "GeneralCustomerListenerMDMImpl: Root ca cert created successfully for customer: {0}", new Object[] { customerId });
            }
            catch (final Exception e) {
                GeneralCustomerListenerMDMImpl.logger.log(Level.SEVERE, e, () -> "GeneralCustomerListenerMDMImpl: Root CA Certificate generation failed for customer :" + n);
            }
            try {
                Logger.getLogger(DEPTechnicianUserListener.class.getName()).log(Level.INFO, "Entered DEPTechnicianUserListener:userAdded to add default license percent medium data");
                final TemplatesUtil templatesUtil = new TemplatesUtil();
                final Long mediumId = templatesUtil.getMediumIdByName("EMAIL");
                Long subCategoryID = templatesUtil.getSubCategoryIDForEventCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
                Map templateMap = new HashMap();
                templateMap.put("templateName", "Device license exceeded alert Template " + System.currentTimeMillis());
                templateMap.put("userID", userId);
                templateMap.put("description", "Template for sending alerts when Device license exceeds 100 percent " + AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
                templateMap.put("subCategoryID", subCategoryID);
                Long templateID = new TemplatesDAOUtil().addOrUpdateTemplateDetails(templateMap, (Long)null);
                LinkedHashMap mediumMap = new LinkedHashMap();
                mediumMap.put("mediumID", mediumId);
                mediumMap.put("mediumData", "{\"subject\": \"License Usage exceeded $mdm.specifiedpercent$% of the allocated limit for customer - $device.customername$ \",\"description\": \"Dear $mdm.user_name$, You have enrolled ($mdm.enrolled_count$) devices into MDM. This exceeds $mdm.specifiedpercent$% of the licenses purchased for the month of $mdm.month$. To enroll and managed additional devices, purchase more licenses from MDM. Contact our support team at mdm-support@manageengine.com\"}");
                new MediumDAOUtil().populateMediumData(templateID, mediumMap);
                subCategoryID = templatesUtil.getSubCategoryIDForEventCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
                templateMap = new HashMap();
                templateMap.put("templateName", "Device license reached alert Template " + System.currentTimeMillis());
                templateMap.put("userID", userId);
                templateMap.put("description", "Template for sending alerts when Device license drops below specified percent " + AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
                templateMap.put("subCategoryID", subCategoryID);
                templateID = new TemplatesDAOUtil().addOrUpdateTemplateDetails(templateMap, (Long)null);
                mediumMap = new LinkedHashMap();
                mediumMap.put("mediumID", mediumId);
                mediumMap.put("mediumData", "{\"subject\": \"License Usage reached $mdm.specifiedpercent$ of the allocated limit for customer - $device.customername$ \",\"description\": \"Dear $mdm.user_name$, You have enrolled $mdm.enrolled_count$ devices, and have purchased $mdm.license_count$ licenses for the month of $mdm.month$. Kindly purchase only the required number of licenses for the devices enrolled.\"}");
                new MediumDAOUtil().populateMediumData(templateID, mediumMap);
            }
            catch (final Exception e) {
                Logger.getLogger(GeneralCustomerListenerMDMImpl.class.getName()).log(Level.SEVERE, "Exception during adding medium data for newly added technician for license percent handling", e);
            }
            try {
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableScheduleAppUpdates")) {
                    ExistingStoreAppPolicyHandler.getInstance().addStoreAppPolicy(customerId);
                }
            }
            catch (final Exception ex2) {
                GeneralCustomerListenerMDMImpl.logger.log(Level.SEVERE, "Exception while creation default store app policy for customer {0}", customerId);
            }
        }
        catch (final Exception ex3) {
            Logger.getLogger(GeneralCustomerListenerMDMImpl.class.getName()).log(Level.SEVERE, null, ex3);
        }
    }
    
    public void customerDeleted(final CustomerEvent customerEvent) {
    }
    
    public void customerUpdated(final CustomerEvent customerEvent) {
    }
    
    public void firstCustomerAdded(final CustomerEvent customerEvent) {
    }
    
    static {
        GeneralCustomerListenerMDMImpl.logger = Logger.getLogger(GeneralCustomerListenerMDMImpl.class.getName());
    }
}
