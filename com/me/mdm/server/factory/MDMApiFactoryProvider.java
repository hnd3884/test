package com.me.mdm.server.factory;

import com.me.mdm.server.notification.MDMNotificationLimiter;
import javax.resource.NotSupportedException;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.server.alerts.sms.SMSAPI;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.enroll.MDMModelNameMappingHandler;
import com.me.mdm.server.util.CloudCSRAuthAPIInterface;
import com.me.mdm.server.adep.DepErrorsAPI;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import com.me.mdm.server.notification.NSWakeupAPI;
import com.me.mdm.server.tracker.mics.MICSMailerAPI;
import com.me.mdm.server.tracker.mics.MICSDataAPI;
import com.me.mdm.server.doc.DocViewApi;
import com.me.mdm.api.reports.integ.MDMAnalyticRequestHandler;
import com.me.mdm.webclient.filter.MDMURLTrackingAPI;
import com.me.mdm.server.ios.apns.APNSCommunicationErrorHandler;
import com.me.mdm.server.apps.CloudFileStorageInterface;
import com.me.mdm.server.support.MDMUploadAction;
import com.me.mdm.server.support.MDMCompressAPI;
import com.me.mdm.core.auth.MDMPurposeAPIKeyGenerator;
import com.me.mdm.server.enrollment.EnrollmentInvitationListener;
import com.adventnet.sym.server.mdm.enroll.ERDeviceListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class MDMApiFactoryProvider extends ApiFactoryProvider
{
    private static final Logger LOGGER;
    private static SDPIntegrationAPI sdpIntegrationAPI;
    private static ManagedDeviceListener sdpManagedDeviceListener;
    private static SecureKeyProviderAPI secureKeyProviderAPI;
    private static MDMAuthTokenUtilAPI mdmAuthTokenUtilAPI;
    private static MDMUtilAPI mdmUtilAPI;
    private static MDMChatAPI mdmChatAPI;
    private static MDMGDPRSettingAPI mdmGDPRSettingsAPI;
    private static MDMTrackerAPI mdmTrackerAPI;
    private static AssistAuthTokenHandlerAPI assistAuthTokenHandlerAPI;
    private static ERDeviceListener enrollmentRequestManagedDeviceListener;
    private static EnrollmentInvitationListener enrollmentInvitationListener;
    private static MDMWinAppExtractorAPI mdmWinAppExtractorAPI;
    private static MDMPurposeAPIKeyGenerator mdmPurposeAPIKeyGenerator;
    private static BusinessStoreAccessInterface businessStoreAccessInterface;
    private static APIUserDetailsUtil apiUserDetailsUtil;
    private static MDMAnonymousTrackingAPI trackingImpl;
    private static MDMLoginUserAPI userAPI;
    private static UploadDownloadAPI udApi;
    private static MDMRebrandAPI rebrandImpl;
    private static MDMLicenseDetailsAPI licesenAPIImpl;
    private static RedirectURLAPI redirecturlAPI;
    private static TwoFactorAuthenticationAPI twoFactorAuthenticationAPI;
    private static PasswordPolicyAPI passwordPolicyAPI;
    private static PersonalizationAPI personalizationAPI;
    private static MDMCompressAPI mdmCompressAPI;
    private static MDMUploadAction uploadAction;
    private static CloudFileStorageInterface cloudFileStorageInterface;
    private static APNSCommunicationErrorHandler apnsErrorHandler;
    private static MDMURLTrackingAPI urlTrackingAPI;
    private static MDMAnalyticRequestHandler mdmAnalyticRequestHandler;
    private static ConditionalExchangeAccessAPI conditionalExchangeAccessAPI;
    private static DocViewApi docViewApi;
    private static MICSDataAPI micsDataAPI;
    private static MICSMailerAPI micsMailerAPI;
    private static NSWakeupAPI nsWakeupAPI;
    private static MDMTableViewAPI mdmTableViewApi;
    private static DocsCleanupAPI docsCleanupAPI;
    private static InactiveDevicePolicyTask inactivePolicyTask;
    private static GoogleApiProductBasedHandler googleApiProductBasedHandler;
    private static MDMModernMgmtAPI mdmModernMgmtAPI;
    private static MdmIosScepEnrollmentAPI mdmIosScepEnrollmentAPI;
    private static DepErrorsAPI depErrorsAPI;
    private static CloudCSRAuthAPIInterface cloudCSRAuthAPI;
    private static MDMModelNameMappingHandler mdmModelNameMappingHandler;
    private static MDMAssociationQueueSerializerAPI asyncQueueSerializer;
    
    public static CloudFileStorageInterface getCloudFileStorageAPI() {
        if (MDMApiFactoryProvider.cloudFileStorageInterface == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    MDMApiFactoryProvider.cloudFileStorageInterface = (CloudFileStorageInterface)Class.forName("com.me.mdmcloud.server.cloudpickerintegration.CloudFileStorageCloudImpl").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for AddOrModifyAPIHandler... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for AddOrModifyAPIHandler...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for AddOrModifyAPIHandler...", ie2);
            }
            catch (final Exception ex) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for AddOrModifyAPIHandler...", ex);
            }
        }
        return MDMApiFactoryProvider.cloudFileStorageInterface;
    }
    
    public static AssistAuthTokenHandlerAPI getAssistAuthTokenHandler() {
        if (MDMApiFactoryProvider.assistAuthTokenHandlerAPI == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    MDMApiFactoryProvider.assistAuthTokenHandlerAPI = (AssistAuthTokenHandlerAPI)Class.forName("com.me.mdmcloud.server.remotesession.AssistIntegrationHandlerImpl").newInstance();
                }
                else {
                    MDMApiFactoryProvider.assistAuthTokenHandlerAPI = (AssistAuthTokenHandlerAPI)Class.forName("com.me.mdm.onpremise.remotesession.AuthTokenHandlerOnPremiseImpl").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for AssistAuthTokenHandlerAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for AssistAuthTokenHandlerAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for AssistAuthTokenHandlerAPI...", ie2);
            }
            catch (final Exception ex) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for AssistAuthTokenHandlerAPI...", ex);
            }
        }
        return MDMApiFactoryProvider.assistAuthTokenHandlerAPI;
    }
    
    public static SDPIntegrationAPI getSDPIntegrationAPI() {
        try {
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS() && MDMApiFactoryProvider.sdpIntegrationAPI == null) {
                MDMApiFactoryProvider.sdpIntegrationAPI = (SDPIntegrationAPI)Class.forName("com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationImpl").newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for getSDPIntegrationAPI... ", ce);
        }
        catch (final InstantiationException ie) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for getSDPIntegrationAPI...", ie);
        }
        catch (final IllegalAccessException ie2) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for getSDPIntegrationAPI...", ie2);
        }
        catch (final Exception ex) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for getSDPIntegrationAPI...", ex);
        }
        return MDMApiFactoryProvider.sdpIntegrationAPI;
    }
    
    public static ManagedDeviceListener getSDPIntegrationListenerAPI() {
        try {
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS() && MDMApiFactoryProvider.sdpManagedDeviceListener == null) {
                MDMApiFactoryProvider.sdpManagedDeviceListener = (ManagedDeviceListener)Class.forName("com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationManagedDeviceListener").newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for getSDPIntegrationListenerAPI... ", ce);
        }
        catch (final InstantiationException ie) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for getSDPIntegrationListenerAPI...", ie);
        }
        catch (final IllegalAccessException ie2) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for getSDPIntegrationListenerAPI...", ie2);
        }
        catch (final Exception ex) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for getSDPIntegrationListenerAPI...", ex);
        }
        return MDMApiFactoryProvider.sdpManagedDeviceListener;
    }
    
    public static SecureKeyProviderAPI getSecureKeyProviderAPI() {
        if (MDMApiFactoryProvider.secureKeyProviderAPI == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (!CustomerInfoUtil.isSAS()) {
                    MDMApiFactoryProvider.secureKeyProviderAPI = (SecureKeyProviderAPI)Class.forName("com.me.mdm.onpremise.util.SecureKeyProviderImpl").newInstance();
                }
                else {
                    MDMApiFactoryProvider.secureKeyProviderAPI = (SecureKeyProviderAPI)Class.forName("com.me.mdmcloud.server.util.SecureKeyProviderImpl").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for SecureKeyProviderAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for AppRepositoryAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for AppRepositoryAPI...", ie2);
            }
            catch (final Exception ex) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for AppRepositoryAPI...", ex);
            }
        }
        return MDMApiFactoryProvider.secureKeyProviderAPI;
    }
    
    public static MDMAuthTokenUtilAPI getMDMAuthTokenUtilAPI() {
        if (MDMApiFactoryProvider.mdmAuthTokenUtilAPI == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (!CustomerInfoUtil.isSAS()) {
                    MDMApiFactoryProvider.mdmAuthTokenUtilAPI = (MDMAuthTokenUtilAPI)Class.forName("com.me.mdm.onpremise.util.MDMAuthTokenUtilImpl").newInstance();
                }
                else {
                    MDMApiFactoryProvider.mdmAuthTokenUtilAPI = (MDMAuthTokenUtilAPI)Class.forName("com.me.mdmcloud.server.util.MDMAuthTokenUtilImpl").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for MDMAuthTokenUtilAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for MDMAuthTokenUtilAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for MDMAuthTokenUtilAPI...", ie2);
            }
            catch (final Exception ex) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for MDMAuthTokenUtilAPI...", ex);
            }
        }
        return MDMApiFactoryProvider.mdmAuthTokenUtilAPI;
    }
    
    public static MDMUtilAPI getMDMUtilAPI() {
        if (MDMApiFactoryProvider.mdmUtilAPI == null) {
            MDMApiFactoryProvider.mdmUtilAPI = (MDMUtilAPI)getImplClassInstance("MDM_UTIL_API_CLASS");
        }
        return MDMApiFactoryProvider.mdmUtilAPI;
    }
    
    public static MDMTrackerAPI getTrackerQueueAPI() {
        if (MDMApiFactoryProvider.mdmTrackerAPI == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    MDMApiFactoryProvider.mdmTrackerAPI = (MDMTrackerAPI)Class.forName("com.me.mdmcloud.server.tracker.MDMTrackerImpl").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for MDMTrackerAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for MDMTrackerAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for MDMTrackerAPI...", ie2);
            }
            catch (final Exception ex) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for MDMTrackerAPI...", ex);
            }
        }
        return MDMApiFactoryProvider.mdmTrackerAPI;
    }
    
    public static MDMChatAPI getMDMChatAPI() {
        if (MDMApiFactoryProvider.mdmChatAPI == null) {
            MDMApiFactoryProvider.mdmChatAPI = (MDMChatAPI)getImplClassInstance("MDM_CHAT_INFO_CLASSNAME");
        }
        return MDMApiFactoryProvider.mdmChatAPI;
    }
    
    public static MDMGDPRSettingAPI getMDMGDPRSettingsAPI() {
        if (MDMApiFactoryProvider.mdmGDPRSettingsAPI == null) {
            MDMApiFactoryProvider.mdmGDPRSettingsAPI = (MDMGDPRSettingAPI)getImplClassInstance("MDM_GDPR_SETTINGS_INFO_CLASSNAME");
        }
        return MDMApiFactoryProvider.mdmGDPRSettingsAPI;
    }
    
    public static ERDeviceListener getEnrollmentRequestManagedDeviceListener() {
        if (MDMApiFactoryProvider.enrollmentRequestManagedDeviceListener == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    MDMApiFactoryProvider.enrollmentRequestManagedDeviceListener = (ERDeviceListener)Class.forName("com.me.mdmcloud.server.tinyurl.ERDeviceCloudListener").newInstance();
                }
                else {
                    MDMApiFactoryProvider.enrollmentRequestManagedDeviceListener = (ERDeviceListener)Class.forName("com.me.mdm.onpremise.server.enrollment.ManagedDeviceListenerOnPremise").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for enrollmentRequestListner... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for enrollmentRequestListner...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for enrollmentRequestListner...", ie2);
            }
            catch (final Exception ex) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for enrollmentRequestListner...", ex);
            }
        }
        return MDMApiFactoryProvider.enrollmentRequestManagedDeviceListener;
    }
    
    public static EnrollmentInvitationListener getInvitationEnrollmentRequestListener() {
        if (MDMApiFactoryProvider.enrollmentInvitationListener == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    MDMApiFactoryProvider.enrollmentInvitationListener = (EnrollmentInvitationListener)Class.forName("com.me.mdmcloud.server.tinyurl.EnrollmentInvitationCloudListener").newInstance();
                }
                else {
                    MDMApiFactoryProvider.enrollmentInvitationListener = (EnrollmentInvitationListener)Class.forName("com.me.mdm.onpremise.server.enrollment.EnrollmentInvitationListenerOnPremise").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for getInvitationEnrollmentRequestListener... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for getInvitationEnrollmentRequestListener...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for getInvitationEnrollmentRequestListener...", ie2);
            }
            catch (final Exception ex) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for getInvitationEnrollmentRequestListener...", ex);
            }
        }
        return MDMApiFactoryProvider.enrollmentInvitationListener;
    }
    
    public static MDMWinAppExtractorAPI getMDMWinAppExtractorAPI() {
        if (MDMApiFactoryProvider.mdmWinAppExtractorAPI == null) {
            MDMApiFactoryProvider.mdmWinAppExtractorAPI = (MDMWinAppExtractorAPI)getImplClassInstance("MDM_WIN_MSI_EXTRACTOR_CLASS");
        }
        return MDMApiFactoryProvider.mdmWinAppExtractorAPI;
    }
    
    public static MDMPurposeAPIKeyGenerator getMdmPurposeAPIKeyGenerator() {
        if (MDMApiFactoryProvider.mdmPurposeAPIKeyGenerator == null) {
            MDMApiFactoryProvider.mdmPurposeAPIKeyGenerator = (MDMPurposeAPIKeyGenerator)getImplClassInstance("MDM_PURPOSE_KEY_GENERATOR_CLASS");
        }
        return MDMApiFactoryProvider.mdmPurposeAPIKeyGenerator;
    }
    
    public static BusinessStoreAccessInterface getBusinessStoreAccess() {
        try {
            if (MDMApiFactoryProvider.businessStoreAccessInterface == null) {
                MDMApiFactoryProvider.businessStoreAccessInterface = (BusinessStoreAccessInterface)getImplClassInstance("MDM_BUSINESS_STORE_API_CLASS");
            }
        }
        catch (final Exception e) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for getBusinessStoreAccess...", e);
        }
        return MDMApiFactoryProvider.businessStoreAccessInterface;
    }
    
    public static APIUserDetailsUtil getAPIUserDetailsUtil() {
        if (MDMApiFactoryProvider.apiUserDetailsUtil == null) {
            MDMApiFactoryProvider.apiUserDetailsUtil = (APIUserDetailsUtil)getImplClassInstance("MDM_API_USER_DETAILS_UTIL");
        }
        return MDMApiFactoryProvider.apiUserDetailsUtil;
    }
    
    public static MDMAnonymousTrackingAPI getMDMAnonymousTrackingImpl() {
        if (MDMApiFactoryProvider.trackingImpl == null) {
            MDMApiFactoryProvider.trackingImpl = (MDMAnonymousTrackingAPI)getImplClassInstance("MDM_ANONYMOUS_TRACKING_IMPL");
        }
        return MDMApiFactoryProvider.trackingImpl;
    }
    
    public static MDMLoginUserAPI getMDMLoginUserAPI() {
        if (MDMApiFactoryProvider.userAPI == null) {
            MDMApiFactoryProvider.userAPI = (MDMLoginUserAPI)getImplClassInstance("MDM_LOGIN_USER_API");
        }
        return MDMApiFactoryProvider.userAPI;
    }
    
    public static UploadDownloadAPI getUploadDownloadAPI() {
        if (MDMApiFactoryProvider.udApi == null) {
            MDMApiFactoryProvider.udApi = (UploadDownloadAPI)ApiFactoryProvider.getImplClassInstance("MDM_UDAPI_CLASS");
        }
        return MDMApiFactoryProvider.udApi;
    }
    
    public static SMSAPI getSMSAPI() {
        String classname = null;
        try {
            classname = ProductClassLoader.getSingleImplProductClass("DM_SMSSETTINGS_CLASS");
            if (classname != null && classname.trim().length() != 0) {
                return (SMSAPI)Class.forName(classname).newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for" + classname, ce);
        }
        catch (final InstantiationException ie) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for" + classname, ie);
        }
        catch (final IllegalAccessException ie2) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for" + classname, ie2);
        }
        catch (final NotSupportedException e) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception while trying to get class value forDM_SMSSETTINGS_CLASS", (Throwable)e);
        }
        catch (final Exception ex) {
            MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for" + classname, ex);
        }
        return null;
    }
    
    public static RedirectURLAPI getRedirectURLAPI() {
        if (MDMApiFactoryProvider.redirecturlAPI == null) {
            MDMApiFactoryProvider.redirecturlAPI = (RedirectURLAPI)getImplClassInstance("MDM_REDIRECT_URL_CLASSNAME");
        }
        return MDMApiFactoryProvider.redirecturlAPI;
    }
    
    public static MDMCompressAPI getMdmCompressAPI() {
        return MDMApiFactoryProvider.mdmCompressAPI = (MDMCompressAPI)getImplClassInstance("MDM_COMPRESS_API");
    }
    
    public static MDMAssociationQueueSerializerAPI getAssociationQueueSerializer() {
        if (MDMApiFactoryProvider.asyncQueueSerializer == null) {
            MDMApiFactoryProvider.asyncQueueSerializer = (MDMAssociationQueueSerializerAPI)getImplClassInstance("MDM_ASSOCIATION_QUEUE_SERIALIZER");
        }
        return MDMApiFactoryProvider.asyncQueueSerializer;
    }
    
    public static TwoFactorAuthenticationAPI getTwoFactorAuthenticationAPI() {
        if (MDMApiFactoryProvider.twoFactorAuthenticationAPI == null) {
            MDMApiFactoryProvider.twoFactorAuthenticationAPI = (TwoFactorAuthenticationAPI)getImplClassInstance("TFA_API_IMPL");
        }
        return MDMApiFactoryProvider.twoFactorAuthenticationAPI;
    }
    
    public static PersonalizationAPI getPersonalizationAPI() {
        if (MDMApiFactoryProvider.personalizationAPI == null) {
            MDMApiFactoryProvider.personalizationAPI = (PersonalizationAPI)getImplClassInstance("PERSONALIZATION_API_IMPL");
        }
        return MDMApiFactoryProvider.personalizationAPI;
    }
    
    public static PasswordPolicyAPI getPasswordPolicyAPI() {
        if (MDMApiFactoryProvider.passwordPolicyAPI == null) {
            MDMApiFactoryProvider.passwordPolicyAPI = (PasswordPolicyAPI)getImplClassInstance("PASSWORDPOLICY_API_IMPL");
        }
        return MDMApiFactoryProvider.passwordPolicyAPI;
    }
    
    public static MDMUploadAction getUploadAction() {
        if (MDMApiFactoryProvider.uploadAction == null) {
            MDMApiFactoryProvider.uploadAction = (MDMUploadAction)getImplClassInstance("MDM_UPLOAD_ACTION");
        }
        return MDMApiFactoryProvider.uploadAction;
    }
    
    public static MDMRebrandAPI getRebrandAPI() {
        if (MDMApiFactoryProvider.rebrandImpl == null) {
            MDMApiFactoryProvider.rebrandImpl = (MDMRebrandAPI)getImplClassInstance("MDM_REBRANDAPI_CLASSNAME");
        }
        return MDMApiFactoryProvider.rebrandImpl;
    }
    
    public static MDMLicenseDetailsAPI getLicenseDetailsAPI() {
        if (MDMApiFactoryProvider.licesenAPIImpl == null) {
            MDMApiFactoryProvider.licesenAPIImpl = (MDMLicenseDetailsAPI)getImplClassInstance("MDM_LICENSEDETAILSAPI_CLASSNAME");
        }
        return MDMApiFactoryProvider.licesenAPIImpl;
    }
    
    public static APNSCommunicationErrorHandler getAPNSErrorHandler() {
        if (MDMApiFactoryProvider.apnsErrorHandler == null) {
            MDMApiFactoryProvider.apnsErrorHandler = (APNSCommunicationErrorHandler)getImplClassInstance("MDM_APNS_ERROR_HANDLER");
        }
        return MDMApiFactoryProvider.apnsErrorHandler;
    }
    
    public static ConditionalExchangeAccessAPI getConditionalExchangeAccessApi() {
        if (MDMApiFactoryProvider.conditionalExchangeAccessAPI == null) {
            MDMApiFactoryProvider.conditionalExchangeAccessAPI = (ConditionalExchangeAccessAPI)getImplClassInstance("MDM_CEA_API_IMPL");
        }
        return MDMApiFactoryProvider.conditionalExchangeAccessAPI;
    }
    
    public static MDMURLTrackingAPI getUrlTrackingAPI() {
        if (MDMApiFactoryProvider.urlTrackingAPI == null) {
            MDMApiFactoryProvider.urlTrackingAPI = (MDMURLTrackingAPI)getImplClassInstance("MDM_URL_TRACKING_IMPL");
        }
        return MDMApiFactoryProvider.urlTrackingAPI;
    }
    
    public static InactiveDevicePolicyTask getInactivePolicyTask() {
        if (MDMApiFactoryProvider.inactivePolicyTask == null) {
            MDMApiFactoryProvider.inactivePolicyTask = (InactiveDevicePolicyTask)getImplClassInstance("INACTIVE_POLICY_TASK");
        }
        return MDMApiFactoryProvider.inactivePolicyTask;
    }
    
    public static MICSDataAPI getMicsTrackingAPI() {
        if (MDMApiFactoryProvider.micsDataAPI == null) {
            MDMApiFactoryProvider.micsDataAPI = (MICSDataAPI)getImplClassInstance("MICS_TRACKING_IMPL");
        }
        return MDMApiFactoryProvider.micsDataAPI;
    }
    
    public static MICSMailerAPI getMicsMailerAPI() {
        if (MDMApiFactoryProvider.micsMailerAPI == null) {
            MDMApiFactoryProvider.micsMailerAPI = (MICSMailerAPI)getImplClassInstance("MICS_MAILER_IMPL");
        }
        return MDMApiFactoryProvider.micsMailerAPI;
    }
    
    public static MDMAnalyticRequestHandler getAnalyticRequestHandler() {
        if (MDMApiFactoryProvider.mdmAnalyticRequestHandler == null) {
            MDMApiFactoryProvider.mdmAnalyticRequestHandler = (MDMAnalyticRequestHandler)getImplClassInstance("MDM_ANALYTIC_INTEG_IMPL");
        }
        return MDMApiFactoryProvider.mdmAnalyticRequestHandler;
    }
    
    public static DocViewApi getDocsApi() {
        if (MDMApiFactoryProvider.docViewApi == null) {
            MDMApiFactoryProvider.docViewApi = (DocViewApi)getImplClassInstance("MDM_DOC_VIEW_API");
        }
        return MDMApiFactoryProvider.docViewApi;
    }
    
    public static MDMTableViewAPI getMDMTableViewAPI() {
        if (MDMApiFactoryProvider.mdmTableViewApi == null) {
            MDMApiFactoryProvider.mdmTableViewApi = (MDMTableViewAPI)getImplClassInstance("MDM_TABLE_VIEW_IMPL");
        }
        return MDMApiFactoryProvider.mdmTableViewApi;
    }
    
    public static DocsCleanupAPI getDocsCleanupAPI() {
        if (MDMApiFactoryProvider.docsCleanupAPI == null) {
            MDMApiFactoryProvider.docsCleanupAPI = (DocsCleanupAPI)getImplClassInstance("MDM_DOC_CLEANUP_CLASSNAME");
        }
        return MDMApiFactoryProvider.docsCleanupAPI;
    }
    
    public static NSWakeupAPI getNSwakeupAPI() {
        if (MDMApiFactoryProvider.nsWakeupAPI == null) {
            MDMApiFactoryProvider.nsWakeupAPI = (NSWakeupAPI)getImplClassInstance("MDM_NS_WAKEUP_CLASSNAME");
        }
        return MDMApiFactoryProvider.nsWakeupAPI;
    }
    
    public static MDMNotificationLimiter getNotificationLimiter() {
        return (MDMNotificationLimiter)getImplClassInstance("MDM_NOTIFICATION_LIMITER");
    }
    
    public static MDMModernMgmtAPI getMDMModernMgmtAPI() {
        if (MDMApiFactoryProvider.mdmModernMgmtAPI == null) {
            MDMApiFactoryProvider.mdmModernMgmtAPI = (MDMModernMgmtAPI)getImplClassInstance("MODERNMGMT_API_IMPL");
        }
        return MDMApiFactoryProvider.mdmModernMgmtAPI;
    }
    
    public static GoogleApiProductBasedHandler getGoogleApiProductBasedHandler() {
        if (MDMApiFactoryProvider.googleApiProductBasedHandler == null) {
            MDMApiFactoryProvider.googleApiProductBasedHandler = (GoogleApiProductBasedHandler)getImplClassInstance("GOOGLE_API_PRODUCT_BASED_HANDLER");
        }
        return MDMApiFactoryProvider.googleApiProductBasedHandler;
    }
    
    public static CloudCSRAuthAPIInterface getCloudCSRSignAuthAPI() {
        if (MDMApiFactoryProvider.cloudCSRAuthAPI == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    MDMApiFactoryProvider.cloudCSRAuthAPI = (CloudCSRAuthAPIInterface)Class.forName("com.me.mdmcloud.server.util.MDMCSRCloudAuthProviderImpl").newInstance();
                }
                else {
                    MDMApiFactoryProvider.cloudCSRAuthAPI = (CloudCSRAuthAPIInterface)Class.forName("com.me.mdm.onpremise.util.MDMCSROPAuthProvider").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for CloudCSRAuthAPIInterface... ", ce);
            }
            catch (final InstantiationException ie) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for CloudCSRAuthAPIInterface...", ie);
            }
            catch (final IllegalAccessException ie2) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for CloudCSRAuthAPIInterface...", ie2);
            }
            catch (final Exception ex) {
                MDMApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for CloudCSRAuthAPIInterface...", ex);
            }
        }
        return MDMApiFactoryProvider.cloudCSRAuthAPI;
    }
    
    public static MdmIosScepEnrollmentAPI getIosScepEnrollmentAPI() {
        if (MDMApiFactoryProvider.mdmIosScepEnrollmentAPI == null) {
            MDMApiFactoryProvider.mdmIosScepEnrollmentAPI = (MdmIosScepEnrollmentAPI)getImplClassInstance("MDM_IOS_SCEP_ENROLLMENT_IMPL");
        }
        return MDMApiFactoryProvider.mdmIosScepEnrollmentAPI;
    }
    
    public static DepErrorsAPI getDepErrorsAPI() {
        if (MDMApiFactoryProvider.depErrorsAPI == null) {
            MDMApiFactoryProvider.depErrorsAPI = (DepErrorsAPI)getImplClassInstance("DEP_ERROR_IMPL");
        }
        return MDMApiFactoryProvider.depErrorsAPI;
    }
    
    public static MDMModelNameMappingHandler getMdmModelNameMappingHandlerAPI() {
        if (MDMApiFactoryProvider.mdmModelNameMappingHandler == null) {
            MDMApiFactoryProvider.mdmModelNameMappingHandler = (MDMModelNameMappingHandler)getImplClassInstance("MDM_MODEL_NAME_MAPPING_IMPL");
        }
        if (getMDMUtilAPI().isFeatureAllowedForUser("modelnamemapping.db.flow") && !MDMApiFactoryProvider.mdmModelNameMappingHandler.getClass().equals(MDMModelNameMappingHandler.class)) {
            MDMApiFactoryProvider.mdmModelNameMappingHandler = new MDMModelNameMappingHandler();
        }
        return MDMApiFactoryProvider.mdmModelNameMappingHandler;
    }
    
    static {
        LOGGER = Logger.getLogger(MDMApiFactoryProvider.class.getName());
        MDMApiFactoryProvider.sdpIntegrationAPI = null;
        MDMApiFactoryProvider.sdpManagedDeviceListener = null;
        MDMApiFactoryProvider.secureKeyProviderAPI = null;
        MDMApiFactoryProvider.mdmAuthTokenUtilAPI = null;
        MDMApiFactoryProvider.mdmUtilAPI = null;
        MDMApiFactoryProvider.mdmChatAPI = null;
        MDMApiFactoryProvider.mdmGDPRSettingsAPI = null;
        MDMApiFactoryProvider.mdmTrackerAPI = null;
        MDMApiFactoryProvider.assistAuthTokenHandlerAPI = null;
        MDMApiFactoryProvider.enrollmentRequestManagedDeviceListener = null;
        MDMApiFactoryProvider.enrollmentInvitationListener = null;
        MDMApiFactoryProvider.mdmWinAppExtractorAPI = null;
        MDMApiFactoryProvider.mdmPurposeAPIKeyGenerator = null;
        MDMApiFactoryProvider.businessStoreAccessInterface = null;
        MDMApiFactoryProvider.apiUserDetailsUtil = null;
        MDMApiFactoryProvider.trackingImpl = null;
        MDMApiFactoryProvider.userAPI = null;
        MDMApiFactoryProvider.udApi = null;
        MDMApiFactoryProvider.rebrandImpl = null;
        MDMApiFactoryProvider.licesenAPIImpl = null;
        MDMApiFactoryProvider.redirecturlAPI = null;
        MDMApiFactoryProvider.twoFactorAuthenticationAPI = null;
        MDMApiFactoryProvider.passwordPolicyAPI = null;
        MDMApiFactoryProvider.personalizationAPI = null;
        MDMApiFactoryProvider.mdmCompressAPI = null;
        MDMApiFactoryProvider.uploadAction = null;
        MDMApiFactoryProvider.cloudFileStorageInterface = null;
        MDMApiFactoryProvider.apnsErrorHandler = null;
        MDMApiFactoryProvider.urlTrackingAPI = null;
        MDMApiFactoryProvider.mdmAnalyticRequestHandler = null;
        MDMApiFactoryProvider.conditionalExchangeAccessAPI = null;
        MDMApiFactoryProvider.docViewApi = null;
        MDMApiFactoryProvider.micsDataAPI = null;
        MDMApiFactoryProvider.micsMailerAPI = null;
        MDMApiFactoryProvider.nsWakeupAPI = null;
        MDMApiFactoryProvider.mdmTableViewApi = null;
        MDMApiFactoryProvider.docsCleanupAPI = null;
        MDMApiFactoryProvider.inactivePolicyTask = null;
        MDMApiFactoryProvider.googleApiProductBasedHandler = null;
        MDMApiFactoryProvider.mdmModernMgmtAPI = null;
        MDMApiFactoryProvider.mdmIosScepEnrollmentAPI = null;
        MDMApiFactoryProvider.depErrorsAPI = null;
        MDMApiFactoryProvider.cloudCSRAuthAPI = null;
        MDMApiFactoryProvider.mdmModelNameMappingHandler = null;
        MDMApiFactoryProvider.asyncQueueSerializer = null;
    }
}
