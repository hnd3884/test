package com.me.devicemanagement.framework.server.factory;

import javax.resource.NotSupportedException;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.server.util.BackUpUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.api.MDMDCIntegrationUtilAPI;
import com.me.devicemanagement.framework.server.xml.SecureXml2DoConverterAPI;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecOneLineLoggerAccessAPI;
import com.me.ems.framework.securitysettings.api.core.SecuritySettingsAPI;
import com.me.devicemanagement.framework.server.authentication.DCUserHandlerAPI;
import com.me.ems.framework.security.breachnotification.core.BreachNotificationAPI;
import com.me.devicemanagement.framework.server.cache.RedisHashMapAPI;
import com.me.ems.framework.home.core.HomePageHandler;
import com.me.ems.framework.personalization.core.PersonalizationAPI;
import com.me.devicemanagement.framework.server.api.SupportTabAPI;
import com.me.ems.framework.common.core.BuildVersionAPI;
import com.me.devicemanagement.framework.server.api.WakeOnLANAPI;
import com.me.devicemanagement.framework.server.api.DCNotificationServiceAPI;
import com.me.devicemanagement.framework.server.api.UnifiedEndpointEditionAPI;
import com.me.devicemanagement.framework.server.license.LicenseKeyValidatorAPI;
import com.me.devicemanagement.framework.server.queue.RedisQueueAPI;
import com.me.devicemanagement.framework.server.api.IdPsAPI;
import com.me.devicemanagement.framework.server.search.AdvSearchProductSpecificHandler;
import com.me.devicemanagement.framework.webclient.search.SuggestSearchHandler;
import com.me.devicemanagement.framework.server.tinyurl.TinyURLAPI;
import com.me.devicemanagement.framework.server.api.AuthenticationKeyHandlerAPI;
import com.me.devicemanagement.framework.server.api.DemoUtilAPI;
import com.me.devicemanagement.framework.server.api.ADGeneralAPI;
import com.me.devicemanagement.framework.server.patch.EPMPatchUtilAPI;
import com.me.devicemanagement.framework.server.util.PatchDataBaseSettingAPI;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.security.CryptoAPI;
import com.me.devicemanagement.framework.webclient.zohoCharts.ZohoChartProperties;
import com.me.devicemanagement.framework.server.mailmanager.MailCallBackHandler;
import com.me.devicemanagement.framework.server.authentication.AuthHandlerAPI;
import com.me.devicemanagement.framework.server.persistence.DMPersistenceAPI;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import com.me.devicemanagement.framework.server.util.ZipUtilAPI;
import com.me.devicemanagement.framework.server.scheduler.SchedulerProviderInterface;
import com.me.devicemanagement.framework.server.api.ServiceAPI;
import com.me.devicemanagement.framework.server.search.SearchSuggestionAPI;
import com.me.devicemanagement.framework.server.eventlog.EventLogAPI;
import com.me.devicemanagement.framework.server.api.ADAccessAPI;
import com.me.devicemanagement.framework.server.security.FileServeAuthorizationAPI;
import com.me.devicemanagement.framework.server.api.MDMSupportAPI;
import com.me.devicemanagement.framework.server.api.SupportAPI;
import com.me.devicemanagement.framework.server.general.ServerSettingsAPI;
import com.me.devicemanagement.framework.server.mailmanager.MailSettingsAPI;
import com.me.devicemanagement.framework.server.authentication.AuthUtilAccessAPI;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;
import com.me.devicemanagement.framework.server.general.InstallationTrackingAPI;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;

public class ApiFactoryProvider
{
    private static FileAccessAPI fileAccessAPI;
    private static InstallationTrackingAPI installationTrackingAPI;
    private static CacheAccessAPI cacheAccessAPI;
    private static UtilAccessAPI utilAccessAPI;
    private static AuthUtilAccessAPI authUtilAccessAPI;
    private static MailSettingsAPI mailSettingsAPI;
    private static ServerSettingsAPI serverSettingsAPI;
    private static SupportAPI supportAPI;
    private static MDMSupportAPI mdmSupportAPI;
    private static FileServeAuthorizationAPI fileServeAuthorizationAPI;
    private static ADAccessAPI adaccessapi;
    private static EventLogAPI eventAPI;
    private static SearchSuggestionAPI searchAPI;
    private static ServiceAPI serviceAPI;
    private static SchedulerProviderInterface schedAPI;
    private static ZipUtilAPI zipUtilAPI;
    private static EvaluatorAPI evaluatorAPI;
    private static DMPersistenceAPI dmPersistenceAPI;
    private static AuthHandlerAPI authHandlerAPI;
    private static MailCallBackHandler mailCallBackAPI;
    private static ZohoChartProperties zohoChartProps;
    private static CryptoAPI cryptoAPI;
    private static final Logger LOGGER;
    private static PatchDataBaseSettingAPI patchDBAPI;
    private static EPMPatchUtilAPI epmPatchUtilAPI;
    private static ADGeneralAPI adGeneralAPI;
    private static DemoUtilAPI demoUtilAPI;
    private static AuthenticationKeyHandlerAPI authKeyHandlerAPI;
    private static PatchDataBaseSettingAPI proxyDBAPI;
    private static TinyURLAPI tinyURLAPI;
    private static SuggestSearchHandler suggestSearchHandler;
    private static AdvSearchProductSpecificHandler searchProductSpecificHandler;
    private static IdPsAPI idPsAPI;
    private static RedisQueueAPI redisQueueAPI;
    private static LicenseKeyValidatorAPI licenseKeyValidatorAPI;
    private static UnifiedEndpointEditionAPI unifiedEndpointEditionAPI;
    private static DCNotificationServiceAPI dcNotificationServiceAPI;
    private static WakeOnLANAPI wakeOnLANAPI;
    private static BuildVersionAPI buildVersionAPI;
    private static SupportTabAPI supportTabAPI;
    private static PersonalizationAPI personalizationAPI;
    private static HomePageHandler homePageHandler;
    private static RedisHashMapAPI redisHashMapAPI;
    private static BreachNotificationAPI breachNotificationAPI;
    private static DCUserHandlerAPI dcUserHandlerAPI;
    private static SecuritySettingsAPI fwSecuritySettingsAPI;
    private static SecuritySettingsAPI productSecuritySettingsAPI;
    private static SecOneLineLoggerAccessAPI secOnelineHanderAPI;
    private static SecureXml2DoConverterAPI secureXml2DoConverterAPI;
    private static MDMDCIntegrationUtilAPI mdmdcIntegrationUtilAPI;
    private static RestAPIUtil restAPIUtil;
    
    public static FileAccessAPI getFileAccessAPI() {
        if (ApiFactoryProvider.fileAccessAPI == null) {
            ApiFactoryProvider.fileAccessAPI = (FileAccessAPI)getImplClassInstance("DM_FILE_ACCESS_API_CLASS");
        }
        return ApiFactoryProvider.fileAccessAPI;
    }
    
    public static CacheAccessAPI getCacheAccessAPI() {
        if (ApiFactoryProvider.cacheAccessAPI == null) {
            ApiFactoryProvider.cacheAccessAPI = (CacheAccessAPI)getImplClassInstance("DM_CACHE_ACCESS_API_CLASS");
        }
        return ApiFactoryProvider.cacheAccessAPI;
    }
    
    public static UtilAccessAPI getUtilAccessAPI() {
        if (ApiFactoryProvider.utilAccessAPI == null) {
            ApiFactoryProvider.utilAccessAPI = (UtilAccessAPI)getImplClassInstance("DM_UTIL_ACCESS_API_CLASS");
        }
        return ApiFactoryProvider.utilAccessAPI;
    }
    
    public static AuthUtilAccessAPI getAuthUtilAccessAPI() {
        if (ApiFactoryProvider.authUtilAccessAPI == null) {
            ApiFactoryProvider.authUtilAccessAPI = (AuthUtilAccessAPI)getImplClassInstance("DM_AUTHUTIL_ACCESS_API_CLASS");
        }
        return ApiFactoryProvider.authUtilAccessAPI;
    }
    
    public static AuthHandlerAPI getAuthHandlerAPI() {
        if (ApiFactoryProvider.authHandlerAPI == null) {
            ApiFactoryProvider.authHandlerAPI = (AuthHandlerAPI)getImplClassInstance("DM_AUTHHANDLER_ACCESS_API_CLASS");
        }
        return ApiFactoryProvider.authHandlerAPI;
    }
    
    public static MailSettingsAPI getMailSettingAPI() {
        if (ApiFactoryProvider.mailSettingsAPI == null) {
            ApiFactoryProvider.mailSettingsAPI = (MailSettingsAPI)getImplClassInstance("DM_MAIL_SETTING_API_CLASS");
        }
        return ApiFactoryProvider.mailSettingsAPI;
    }
    
    public static FileServeAuthorizationAPI getFileServeAuthorizationAPI() {
        if (ApiFactoryProvider.fileServeAuthorizationAPI == null) {
            ApiFactoryProvider.fileServeAuthorizationAPI = (FileServeAuthorizationAPI)getImplClassInstance("FILE_SERVE_AUTHORIZATION_API_CLASS");
        }
        return ApiFactoryProvider.fileServeAuthorizationAPI;
    }
    
    public static ADAccessAPI getADAccessAPI() {
        if (ApiFactoryProvider.adaccessapi == null) {
            ApiFactoryProvider.adaccessapi = (ADAccessAPI)getImplClassInstance("DM_ADACCESS_CLASS");
        }
        return ApiFactoryProvider.adaccessapi;
    }
    
    public static SupportAPI getSupportAPI() {
        if (ApiFactoryProvider.supportAPI == null) {
            ApiFactoryProvider.supportAPI = (SupportAPI)getImplClassInstance("DM_SUPPORT_CLASS");
        }
        return ApiFactoryProvider.supportAPI;
    }
    
    public static MDMSupportAPI getMDMSupportAPI() {
        if (ApiFactoryProvider.mdmSupportAPI == null) {
            ApiFactoryProvider.mdmSupportAPI = (MDMSupportAPI)getImplClassInstance("DM_MDMLOG_CLASS");
        }
        return ApiFactoryProvider.mdmSupportAPI;
    }
    
    public static ServerSettingsAPI getServerSettingsAPI() {
        if (ApiFactoryProvider.serverSettingsAPI == null) {
            ApiFactoryProvider.serverSettingsAPI = (ServerSettingsAPI)getImplClassInstance("DM_SERVER_SETTINGS_API_CLASS");
        }
        return ApiFactoryProvider.serverSettingsAPI;
    }
    
    public static SearchSuggestionAPI getSearchSuggestion() {
        if (ApiFactoryProvider.searchAPI == null) {
            ApiFactoryProvider.searchAPI = (SearchSuggestionAPI)getImplClassInstance("DM_SEARCH_SUGGESTIONCRITERIA_CLASS");
        }
        return ApiFactoryProvider.searchAPI;
    }
    
    public static EventLogAPI getEventLoggerAPI() {
        if (ApiFactoryProvider.eventAPI == null) {
            ApiFactoryProvider.eventAPI = (EventLogAPI)getImplClassInstance("DM_EVENTLOGAPI_CLASS");
        }
        return ApiFactoryProvider.eventAPI;
    }
    
    public static ServiceAPI getServiceAPI(final boolean isDC) {
        try {
            if (isDC) {
                ApiFactoryProvider.serviceAPI = (ServiceAPI)Class.forName("com.me.dc.server.util.ServiceImpl").newInstance();
            }
            else {
                ApiFactoryProvider.serviceAPI = (ServiceAPI)Class.forName("com.me.mdm.server.service.ServiceImpl").newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for serviceAPI... ", ce);
        }
        catch (final InstantiationException ie) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for serviceAPI...", ie);
        }
        catch (final IllegalAccessException ie2) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for serviceAPI...", ie2);
        }
        catch (final Exception ex) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for serviceAPI...", ex);
        }
        return ApiFactoryProvider.serviceAPI;
    }
    
    public static SchedulerProviderInterface getSchedulerAPI() {
        if (ApiFactoryProvider.schedAPI == null) {
            ApiFactoryProvider.schedAPI = (SchedulerProviderInterface)getImplClassInstance("DM_SCHED_API_CLASS");
        }
        return ApiFactoryProvider.schedAPI;
    }
    
    public static ZipUtilAPI getZipUtilAPI() {
        if (ApiFactoryProvider.zipUtilAPI == null) {
            ApiFactoryProvider.zipUtilAPI = (ZipUtilAPI)getImplClassInstance("DM_ZIPUTIL_API_CLASS");
        }
        return ApiFactoryProvider.zipUtilAPI;
    }
    
    public static DMPersistenceAPI getPersistenceAPI() {
        if (ApiFactoryProvider.dmPersistenceAPI == null) {
            ApiFactoryProvider.dmPersistenceAPI = (DMPersistenceAPI)getImplClassInstance("DM_PERSISTENCE_IMPL_CLASS");
        }
        return ApiFactoryProvider.dmPersistenceAPI;
    }
    
    public static InstallationTrackingAPI installationTrackingAPIImpl() {
        if (ApiFactoryProvider.installationTrackingAPI == null) {
            ApiFactoryProvider.installationTrackingAPI = (InstallationTrackingAPI)getImplClassInstance("DM_INSTALLATION_TRACKING_API_CLASS");
        }
        return ApiFactoryProvider.installationTrackingAPI;
    }
    
    public static EvaluatorAPI getEvaluatorAPI() {
        if (ApiFactoryProvider.evaluatorAPI == null) {
            ApiFactoryProvider.evaluatorAPI = (EvaluatorAPI)getImplClassInstance("DM_EVALUATOR_API_CLASS");
        }
        return ApiFactoryProvider.evaluatorAPI;
    }
    
    public static PatchDataBaseSettingAPI getPatchDBAPI() {
        if (ApiFactoryProvider.patchDBAPI == null) {
            ApiFactoryProvider.patchDBAPI = (PatchDataBaseSettingAPI)getImplClassInstance("DM_PATCH_DB_SETTING_CLASS");
        }
        return ApiFactoryProvider.patchDBAPI;
    }
    
    public static PatchDataBaseSettingAPI getProxyDBAPI() {
        if (ApiFactoryProvider.proxyDBAPI == null) {
            ApiFactoryProvider.proxyDBAPI = (PatchDataBaseSettingAPI)getImplClassInstance("DM_PROXY_DB_SETTING_CLASS");
        }
        return ApiFactoryProvider.proxyDBAPI;
    }
    
    public static MailCallBackHandler getMailCallBackHandler() {
        if (ApiFactoryProvider.mailCallBackAPI == null) {
            ApiFactoryProvider.mailCallBackAPI = (MailCallBackHandler)getImplClassInstance("DM_MAILCALLBACKHANDLER_CLASS");
        }
        return ApiFactoryProvider.mailCallBackAPI;
    }
    
    public static CryptoAPI getCryptoAPI() {
        if (ApiFactoryProvider.cryptoAPI == null) {
            ApiFactoryProvider.cryptoAPI = (CryptoAPI)getImplClassInstance("DM_CRYPTO_CLASS");
        }
        return ApiFactoryProvider.cryptoAPI;
    }
    
    public static ZohoChartProperties getZohoChartProps() {
        if (ApiFactoryProvider.zohoChartProps == null) {
            ApiFactoryProvider.zohoChartProps = (ZohoChartProperties)getImplClassInstance("DM_ZOHOCHARTS_CLASS");
        }
        return ApiFactoryProvider.zohoChartProps;
    }
    
    public static EPMPatchUtilAPI getEPMPatchUtilAPI() {
        if (ApiFactoryProvider.epmPatchUtilAPI == null) {
            ApiFactoryProvider.epmPatchUtilAPI = (EPMPatchUtilAPI)getImplClassInstance("DM_EPM_PATCH_UTIL_CLASS");
        }
        return ApiFactoryProvider.epmPatchUtilAPI;
    }
    
    public static ADGeneralAPI getADImpl() {
        if (ApiFactoryProvider.adGeneralAPI == null) {
            ApiFactoryProvider.adGeneralAPI = (ADGeneralAPI)getImplClassInstance("DM_AD_CLASS");
        }
        return ApiFactoryProvider.adGeneralAPI;
    }
    
    public static DemoUtilAPI getDemoUtilAPI() {
        if (ApiFactoryProvider.demoUtilAPI == null) {
            ApiFactoryProvider.demoUtilAPI = (DemoUtilAPI)getImplClassInstance("DM_DEMOUTIL_API_CLASS");
        }
        return ApiFactoryProvider.demoUtilAPI;
    }
    
    public static AuthenticationKeyHandlerAPI getAuthKeyHandlerAPI() {
        if (ApiFactoryProvider.authKeyHandlerAPI == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (!CustomerInfoUtil.isSAS()) {
                    if (CustomerInfoUtil.isDC()) {
                        ApiFactoryProvider.authKeyHandlerAPI = (AuthenticationKeyHandlerAPI)Class.forName("com.me.dc.server.admin.DCAuthenticationKeyHandlerImpl").newInstance();
                    }
                    else {
                        ApiFactoryProvider.authKeyHandlerAPI = (AuthenticationKeyHandlerAPI)Class.forName("com.me.mdm.onpremise.server.admin.MDMAuthenticationKeyHandlerImpl").newInstance();
                    }
                }
            }
            catch (final ClassNotFoundException ce) {
                ApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for getEPMPatchUtilAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                ApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for getEPMPatchUtilAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                ApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for getEPMPatchUtilAPI...", ie2);
            }
            catch (final Exception ex) {
                ApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for getEPMPatchUtilAPI...", ex);
            }
        }
        return ApiFactoryProvider.authKeyHandlerAPI;
    }
    
    public static BackUpUtil getBackUpUtil() {
        return (BackUpUtil)getImplClassInstance("DM_BACKUP_IMPL_CLASS");
    }
    
    public static Object getImplClassInstance(final String key) {
        String classname = null;
        try {
            classname = ProductClassLoader.getSingleImplProductClass(key);
            if (classname != null && classname.trim().length() != 0) {
                return Class.forName(classname).newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for" + classname, ce);
        }
        catch (final InstantiationException ie) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for" + classname, ie);
        }
        catch (final IllegalAccessException ie2) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for" + classname, ie2);
        }
        catch (final NotSupportedException e) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception while trying to get class value for" + key, (Throwable)e);
        }
        catch (final Exception ex) {
            ApiFactoryProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for" + classname, ex);
        }
        return null;
    }
    
    public static RestAPIUtil getRestApiUtil() {
        if (ApiFactoryProvider.restAPIUtil == null) {
            ApiFactoryProvider.restAPIUtil = (RestAPIUtil)getImplClassInstance("DM_REST_API_UTIL_CLASS");
        }
        return ApiFactoryProvider.restAPIUtil;
    }
    
    public static TinyURLAPI getTinyURLHandler() {
        if (ApiFactoryProvider.tinyURLAPI == null) {
            ApiFactoryProvider.tinyURLAPI = (TinyURLAPI)getImplClassInstance("DM_TINYURL_HANDLER");
        }
        return ApiFactoryProvider.tinyURLAPI;
    }
    
    public static SuggestSearchHandler getSuggestSearchHandler() {
        if (ApiFactoryProvider.suggestSearchHandler == null) {
            ApiFactoryProvider.suggestSearchHandler = (SuggestSearchHandler)getImplClassInstance("DM_ADVSEARCH_SUGGESSTION_CLASS");
        }
        return ApiFactoryProvider.suggestSearchHandler;
    }
    
    public static AdvSearchProductSpecificHandler getSearchProductSpecificHandler() {
        if (ApiFactoryProvider.searchProductSpecificHandler == null) {
            ApiFactoryProvider.searchProductSpecificHandler = (AdvSearchProductSpecificHandler)getImplClassInstance("DM_ADVSEARCH_PRODUCT_SPECIFIC_CLASS");
        }
        return ApiFactoryProvider.searchProductSpecificHandler;
    }
    
    public static IdPsAPI getIdPsAPI() {
        if (ApiFactoryProvider.idPsAPI == null) {
            ApiFactoryProvider.idPsAPI = (IdPsAPI)getImplClassInstance("IDPS_API");
        }
        return ApiFactoryProvider.idPsAPI;
    }
    
    public static RedisQueueAPI getRedisQueueAPI() {
        if (ApiFactoryProvider.redisQueueAPI == null) {
            ApiFactoryProvider.redisQueueAPI = (RedisQueueAPI)getImplClassInstance("DM_REDIS_QUEUE_API_CLASS");
        }
        return ApiFactoryProvider.redisQueueAPI;
    }
    
    public static UnifiedEndpointEditionAPI getUnifiedEndpointEditionAPI() {
        if (ApiFactoryProvider.unifiedEndpointEditionAPI == null) {
            ApiFactoryProvider.unifiedEndpointEditionAPI = (UnifiedEndpointEditionAPI)getImplClassInstance("DM_UNIFIED_ENDPOINT_EDITION_CLASS");
        }
        return ApiFactoryProvider.unifiedEndpointEditionAPI;
    }
    
    public static LicenseKeyValidatorAPI getLicenseKeyValidatorAPI() {
        if (ApiFactoryProvider.licenseKeyValidatorAPI == null) {
            ApiFactoryProvider.licenseKeyValidatorAPI = (LicenseKeyValidatorAPI)getImplClassInstance("DM_LICENSE_KEY_VALIDATOR_CLASS");
        }
        return ApiFactoryProvider.licenseKeyValidatorAPI;
    }
    
    public static DCNotificationServiceAPI getDCNotificationServiceAPI() {
        if (ApiFactoryProvider.dcNotificationServiceAPI == null) {
            ApiFactoryProvider.dcNotificationServiceAPI = (DCNotificationServiceAPI)getImplClassInstance("DC_NOTIFICATION_SERVICE_API_CLASS");
        }
        return ApiFactoryProvider.dcNotificationServiceAPI;
    }
    
    public static WakeOnLANAPI getWakeOnLANAPI() {
        if (ApiFactoryProvider.wakeOnLANAPI == null) {
            ApiFactoryProvider.wakeOnLANAPI = (WakeOnLANAPI)getImplClassInstance("WAKE_ON_LAN_API_CLASS");
        }
        return ApiFactoryProvider.wakeOnLANAPI;
    }
    
    public static BuildVersionAPI getBuildVersionAPI() {
        if (ApiFactoryProvider.buildVersionAPI == null) {
            ApiFactoryProvider.buildVersionAPI = (BuildVersionAPI)getImplClassInstance("DM_BUILD_VERSION_API_CLASS");
        }
        return ApiFactoryProvider.buildVersionAPI;
    }
    
    public static SupportTabAPI getSupportTabAPI() {
        if (ApiFactoryProvider.supportTabAPI == null) {
            ApiFactoryProvider.supportTabAPI = (SupportTabAPI)getImplClassInstance("SUPPORT_TAB_API_IMPL");
        }
        return ApiFactoryProvider.supportTabAPI;
    }
    
    public static PersonalizationAPI getPersonalizationAPIForRest() {
        if (ApiFactoryProvider.personalizationAPI == null) {
            ApiFactoryProvider.personalizationAPI = (PersonalizationAPI)getImplClassInstance("PERSONALIZATION_API_CLASS_NAME");
        }
        return ApiFactoryProvider.personalizationAPI;
    }
    
    public static HomePageHandler getHomePageHandler() {
        if (ApiFactoryProvider.homePageHandler == null) {
            ApiFactoryProvider.homePageHandler = (HomePageHandler)getImplClassInstance("DM_HOME_PAGE_HANDLER_CLASS");
        }
        return ApiFactoryProvider.homePageHandler;
    }
    
    public static RedisHashMapAPI getRedisHashMap() {
        if (ApiFactoryProvider.redisHashMapAPI == null) {
            ApiFactoryProvider.redisHashMapAPI = (RedisHashMapAPI)getImplClassInstance("REDIS_HASH_MAP_IMPL_CLASS");
        }
        return ApiFactoryProvider.redisHashMapAPI;
    }
    
    public static BreachNotificationAPI getBreachNotificationAPI() {
        if (ApiFactoryProvider.breachNotificationAPI == null) {
            ApiFactoryProvider.breachNotificationAPI = (BreachNotificationAPI)getImplClassInstance("DM_BREACH_NOTIFICATION_CLASS");
        }
        return ApiFactoryProvider.breachNotificationAPI;
    }
    
    public static DCUserHandlerAPI getDCUserHandler() {
        if (ApiFactoryProvider.dcUserHandlerAPI == null) {
            ApiFactoryProvider.dcUserHandlerAPI = (DCUserHandlerAPI)getImplClassInstance("DC_USER_HANDLER_API_CLASS");
        }
        return ApiFactoryProvider.dcUserHandlerAPI;
    }
    
    public static SecuritySettingsAPI getProductSecuritySettingsApi() {
        if (ApiFactoryProvider.productSecuritySettingsAPI == null) {
            ApiFactoryProvider.productSecuritySettingsAPI = (SecuritySettingsAPI)getImplClassInstance("PRODUCT_SECURE_SETTINGS_API_CLASS");
        }
        return ApiFactoryProvider.productSecuritySettingsAPI;
    }
    
    public static SecuritySettingsAPI getFwSecuritySettingsApi() {
        if (ApiFactoryProvider.fwSecuritySettingsAPI == null) {
            ApiFactoryProvider.fwSecuritySettingsAPI = (SecuritySettingsAPI)getImplClassInstance("FW_SECURE_SETTINGS_API_CLASS");
        }
        return ApiFactoryProvider.fwSecuritySettingsAPI;
    }
    
    public static SecOneLineLoggerAccessAPI getSecOnlineLogAPI() {
        if (ApiFactoryProvider.secOnelineHanderAPI == null) {
            ApiFactoryProvider.secOnelineHanderAPI = (SecOneLineLoggerAccessAPI)getImplClassInstance("DM_SEC_ONELINE_LOG_CLASS");
        }
        return ApiFactoryProvider.secOnelineHanderAPI;
    }
    
    public static SecureXml2DoConverterAPI getSecureXml2DoConverterAPI() {
        if (ApiFactoryProvider.secureXml2DoConverterAPI == null) {
            ApiFactoryProvider.secureXml2DoConverterAPI = (SecureXml2DoConverterAPI)getImplClassInstance("DM_SECURE_XML2DOCONVERTER_IMPL_CLASS");
        }
        return ApiFactoryProvider.secureXml2DoConverterAPI;
    }
    
    public static MDMDCIntegrationUtilAPI getMDMDCIntegrationUtilAPI() {
        if (ApiFactoryProvider.mdmdcIntegrationUtilAPI == null) {
            ApiFactoryProvider.mdmdcIntegrationUtilAPI = (MDMDCIntegrationUtilAPI)getImplClassInstance("MDM_DC_INTEGRATION_UTIL_API_CLASS");
        }
        return ApiFactoryProvider.mdmdcIntegrationUtilAPI;
    }
    
    static {
        ApiFactoryProvider.fileAccessAPI = null;
        ApiFactoryProvider.installationTrackingAPI = null;
        ApiFactoryProvider.cacheAccessAPI = null;
        ApiFactoryProvider.utilAccessAPI = null;
        ApiFactoryProvider.authUtilAccessAPI = null;
        ApiFactoryProvider.mailSettingsAPI = null;
        ApiFactoryProvider.serverSettingsAPI = null;
        ApiFactoryProvider.supportAPI = null;
        ApiFactoryProvider.mdmSupportAPI = null;
        ApiFactoryProvider.fileServeAuthorizationAPI = null;
        ApiFactoryProvider.adaccessapi = null;
        ApiFactoryProvider.eventAPI = null;
        ApiFactoryProvider.searchAPI = null;
        ApiFactoryProvider.serviceAPI = null;
        ApiFactoryProvider.schedAPI = null;
        ApiFactoryProvider.zipUtilAPI = null;
        ApiFactoryProvider.evaluatorAPI = null;
        ApiFactoryProvider.dmPersistenceAPI = null;
        ApiFactoryProvider.authHandlerAPI = null;
        ApiFactoryProvider.mailCallBackAPI = null;
        ApiFactoryProvider.zohoChartProps = null;
        ApiFactoryProvider.cryptoAPI = null;
        LOGGER = Logger.getLogger(ApiFactoryProvider.class.getName());
        ApiFactoryProvider.patchDBAPI = null;
        ApiFactoryProvider.epmPatchUtilAPI = null;
        ApiFactoryProvider.adGeneralAPI = null;
        ApiFactoryProvider.demoUtilAPI = null;
        ApiFactoryProvider.authKeyHandlerAPI = null;
        ApiFactoryProvider.proxyDBAPI = null;
        ApiFactoryProvider.tinyURLAPI = null;
        ApiFactoryProvider.suggestSearchHandler = null;
        ApiFactoryProvider.searchProductSpecificHandler = null;
        ApiFactoryProvider.idPsAPI = null;
        ApiFactoryProvider.redisQueueAPI = null;
        ApiFactoryProvider.licenseKeyValidatorAPI = null;
        ApiFactoryProvider.unifiedEndpointEditionAPI = null;
        ApiFactoryProvider.dcNotificationServiceAPI = null;
        ApiFactoryProvider.wakeOnLANAPI = null;
        ApiFactoryProvider.buildVersionAPI = null;
        ApiFactoryProvider.supportTabAPI = null;
        ApiFactoryProvider.personalizationAPI = null;
        ApiFactoryProvider.homePageHandler = null;
        ApiFactoryProvider.redisHashMapAPI = null;
        ApiFactoryProvider.breachNotificationAPI = null;
        ApiFactoryProvider.dcUserHandlerAPI = null;
        ApiFactoryProvider.fwSecuritySettingsAPI = null;
        ApiFactoryProvider.productSecuritySettingsAPI = null;
        ApiFactoryProvider.secOnelineHanderAPI = null;
        ApiFactoryProvider.secureXml2DoConverterAPI = null;
        ApiFactoryProvider.mdmdcIntegrationUtilAPI = null;
    }
}
