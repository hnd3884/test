package com.me.mdm.onpremise.server.service;

import java.util.Hashtable;
import com.me.ems.onpremise.productbanner.core.ProductBannerSyncTask;
import com.me.devicemanagement.framework.server.authorization.RoleListener;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.OnelineLoggerRoleListener;
import com.me.devicemanagement.framework.server.authorization.RoleListenerHandler;
import com.me.devicemanagement.framework.server.authentication.UserListener;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.OnelineLoggerUserListenerImpl;
import com.me.devicemanagement.framework.server.authentication.UserListenerHandler;
import com.me.mdm.onpremise.server.android.agent.AndroidAgentSecretsHandler;
import org.json.JSONObject;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Locale;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.onpremise.server.certificate.ServerSANCertificateGeneratorTask;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.onpremise.webclient.settings.SettingsUtil;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.Map;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.adventnet.sym.server.util.SyMUtil;
import com.me.ems.framework.common.api.utils.AdminCommonUtil;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.silentupdate.ondemand.SilentUpdateHandler;
import com.me.mdm.onpremise.server.util.MDMPFwsUtil;
import com.adventnet.sym.server.fos.FailoverServerUtil;
import com.me.mdm.onpremise.api.error.MDMPAPIErrorutil;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import com.me.devicemanagement.onpremise.server.service.DCServerBuildHistoryProvider;
import com.me.devicemanagement.onpremise.server.service.DMOnPremiseService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDMPHandler
{
    private static Logger logger;
    
    private MDMPHandler() {
    }
    
    public static void initialize() {
        MDMPHandler.logger.log(Level.INFO, " MDMPHandler : initialize() : In MDMPHandler initialize()");
        final boolean firstStartUp = isFirstStartUp();
        MDMPHandler.logger.log(Level.INFO, "MDMPHandler : initialize() : Is First StartUp :{0}", firstStartUp);
        MDMPHandler.logger.log(Level.INFO, "MDMPHandler : initialize() : isAllowToStart :{0}", DMOnPremiseService.isAllowToStartServer);
        if (DMOnPremiseService.isAllowToStartServer) {
            MDMPHandler.logger.log(Level.INFO, "MDMPHandler : initialize() : going to update server startup timing  :");
            DCServerBuildHistoryProvider.getInstance().updateDCServerStartup();
            DCServerBuildHistoryProvider.getInstance().updateServerStartupTimeIntoFileSystem();
        }
        checkAndGenerateServerCertificate();
        if (firstStartUp) {
            changeNATsettings();
            changeProxySettings();
            updateDefaultAdminTimeZone();
            updateELMKeys();
        }
        FwsUtil.generateserverKeystore();
        MDMPAPIErrorutil.initializeErrorConstants();
        createProductBannerSyncScheduler();
        FailoverServerUtil.UpdateFOSDetails();
        MDMPFwsUtil.showOrHideSgsIncompatibilityMessage();
        final SilentUpdateHandler silentUpdate = new SilentUpdateHandler();
        silentUpdate.startupHandling();
        checkAndStartDemoTask();
        registerOneLineLoggerListenerImpl();
    }
    
    public static void destroy() {
    }
    
    public static void checkAndStartDemoTask() {
        if (ApiFactoryProvider.getDemoUtilAPI().isDemoMode()) {
            MDMPHandler.logger.log(Level.INFO, " MDMPHandler : initialize() : checkAndStartDemoTask : Enabling readonly.mode for MDMP Demo");
            AdminCommonUtil.modifyReadOnlyModeInSecurityXML(Boolean.valueOf(true));
        }
        else {
            MDMPHandler.logger.log(Level.INFO, " MDMPHandler : initialize() : checkAndStartDemoTask : Disabling readonly.mode for MDMP Demo");
            AdminCommonUtil.modifyReadOnlyModeInSecurityXML(Boolean.valueOf(false));
        }
    }
    
    private static void changeNATsettings() {
        MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeNATsettings() is called");
        try {
            final HashMap dynaMap = getPortMap();
            final String systemHWType = SyMUtil.getServerParameter("SYSTEM_HW_TYPE");
            MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeNATsettings() systemHWTYpe :{0}", systemHWType);
            if (systemHWType != null && systemHWType.equalsIgnoreCase("amazon_virtual")) {
                final String awsInstancePublicDNS = getAWSpublicDNS();
                MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeNATsettings() : Amazon Machine public DNS {0}", awsInstancePublicDNS);
                if (awsInstancePublicDNS != null && !awsInstancePublicDNS.equals("")) {
                    dynaMap.put("NAT_ADDRESS", awsInstancePublicDNS);
                }
            }
            else {
                final Properties serverInfo = SyMUtil.getDCServerInfo();
                String secIP = ((Hashtable<K, String>)serverInfo).get("SERVER_SEC_IPADDR");
                if (secIP == null || secIP.equalsIgnoreCase("--") || secIP.trim().length() == 0) {
                    secIP = ((Hashtable<K, String>)serverInfo).get("SERVER_FQDN");
                }
                MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeNATsettings() : for HW type: {0} FQDN is :{1}", new Object[] { systemHWType, secIP });
                dynaMap.put("NAT_ADDRESS", secIP);
            }
            MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeNATsettings() NAT DynaForm {0}", dynaMap);
            NATHandler.getInstance().saveNATsettings((Map)dynaMap);
            MessageProvider.getInstance().unhideMessage("NAT_NOT_CONFIGURED");
            MessageProvider.getInstance().unhideMessage("NAT_RECOMMENDATION");
        }
        catch (final Exception ex) {
            MDMPHandler.logger.log(Level.SEVERE, "Exception in MDMPHandler : changeNATsettings() ", ex);
        }
    }
    
    private static void changeProxySettings() {
        final boolean validationStatus = validateProxyConnetion();
        final String vmbuild = SyMUtil.getProductProperty("vmbuild");
        MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeProxySettings () : is vmbuild {0}", vmbuild);
        if ((vmbuild != null && !vmbuild.isEmpty() && vmbuild.equalsIgnoreCase("true")) || validationStatus) {
            MDMPHandler.logger.log(Level.INFO, "is vmbuild is true so configuring proxy as direct connection");
            final HashMap proxyMap = new HashMap();
            final HashMap proxyConfigMap = new HashMap();
            proxyConfigMap.put("proxyType", 1);
            proxyMap.put("proxyF", proxyConfigMap);
            try {
                MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeProxySettings () : changing proxy settings");
                SettingsUtil.saveProxyAPI(proxyMap, false);
                MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeProxySettings () :  proxy settings saved successfully");
            }
            catch (final Exception ex) {
                MDMPHandler.logger.log(Level.SEVERE, "MDMPHandler : changeProxySettings () : Exeption while saveing Proxy ", ex);
            }
        }
        else {
            MDMPHandler.logger.log(Level.INFO, "MDMPHandler : changeProxySettings () :is vmbuild is not true so proxy is not saved");
        }
    }
    
    private static HashMap getPortMap() {
        final Properties rdsProperties = readPropertiesFromConf("conf" + File.separator + "rdssettings.conf");
        final Properties webProperties = readPropertiesFromConf("conf" + File.separator + "websettings.conf");
        final HashMap portMap = new HashMap();
        portMap.put("NAT_RDS_HTTPS_PORT", Integer.valueOf(((Hashtable<K, String>)rdsProperties).get("rds.default.https.port")));
        portMap.put("NAT_FT_HTTPS_PORT", Integer.valueOf(((Hashtable<K, String>)rdsProperties).get("ft.default.https.port")));
        portMap.put("NAT_NS_PORT", Integer.valueOf("0"));
        portMap.put("NAT_CHAT_PORT", Integer.valueOf(((Hashtable<K, String>)webProperties).get("httpnio.port")));
        portMap.put("NAT_HTTPS_PORT", Integer.valueOf(((Hashtable<K, String>)webProperties).get("https.port")));
        return portMap;
    }
    
    private static Properties readPropertiesFromConf(final String fileName) {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String fname = serverHome + File.separator + fileName;
            return FileAccessUtil.readProperties(fname);
        }
        catch (final Exception ex) {
            MDMPHandler.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static String getAWSMetaDataURL() {
        String url = "http://169.254.169.254/latest/meta-data";
        try {
            final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "general_properties.conf";
            MDMPHandler.logger.log(Level.INFO, "MDMPHandler : getAWSMetaDataURL() : Path of general_props.conf file: {0}", confFile);
            final Properties props = FileAccessUtil.readProperties(confFile);
            url = props.getProperty("AWSMetaDataUrl");
        }
        catch (final Exception e) {
            MDMPHandler.logger.log(Level.INFO, "MDMPHandler : getAWSMetaDataURL() : Path of general_properties.conf file: ", e);
        }
        return url;
    }
    
    private static String getAWSpublicDNS() {
        final String publicDNSmetaDataUrl = getAWSMetaDataURL() + "/public-hostname";
        final String publicDNS = responseForHTTPgetRequest(publicDNSmetaDataUrl);
        return publicDNS;
    }
    
    private static String responseForHTTPgetRequest(final String url) {
        String response = null;
        try {
            final DownloadStatus downloadStatus = DownloadManager.getInstance().getURLResponseWithoutCookie(url, (String)null, new SSLValidationType[0]);
            final int responseCode = downloadStatus.getStatus();
            if (responseCode == 0) {
                response = downloadStatus.getUrlDataBuffer();
            }
            else {
                MDMPHandler.logger.log(Level.INFO, "MDMPHandler : responseForHTTPgetRequest() :getAWSMetaDataURLErrorenous response code {0}", String.valueOf(responseCode));
            }
        }
        catch (final Exception ex) {
            MDMPHandler.logger.log(Level.SEVERE, "Exception in MDMPHandler : responseForHTTPgetRequest() ", ex);
        }
        return response;
    }
    
    public static boolean isFirstStartUp() {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(Table.getTable("DCServerBuildHistory"));
            query.addSelectColumn(Column.getColumn("DCServerBuildHistory", "*"));
            final DataObject buildHistoryDO = DataAccess.get((SelectQuery)query);
            MDMPHandler.logger.log(Level.INFO, "MDMPHandler : isFirstStartUp() : DCSERVERBUILDHISTORY table DO : {0}", buildHistoryDO);
            if (!buildHistoryDO.isEmpty()) {
                final int buildHistoryRowCount = buildHistoryDO.size("DCServerBuildHistory");
                MDMPHandler.logger.log(Level.INFO, "MDMPHandler : isFirstStartUp() : Build DO Count{0}", buildHistoryRowCount);
                if (buildHistoryRowCount == 1) {
                    final Row buildHistoryDOFirstRow = buildHistoryDO.getFirstRow("DCServerBuildHistory");
                    final Long build_detected_at = (Long)buildHistoryDOFirstRow.get("BUILD_DETECTED_AT");
                    final Integer build_no = (Integer)buildHistoryDOFirstRow.get("BUILD_NUMBER");
                    final int buildUpTimeHistoryRowCount = DBUtil.getRecordCount("DCServerUptimeHistory", "DC_UPTIME_RECORD_ID", (Criteria)null);
                    MDMPHandler.logger.log(Level.INFO, "MDMPHandler : isFirstStartUp() : Build Detected at :{0}", build_detected_at);
                    MDMPHandler.logger.log(Level.INFO, "MDMPHandler : isFirstStartUp() : Build up time count{0}", buildUpTimeHistoryRowCount);
                    if (build_no != null && build_detected_at == -1L && buildUpTimeHistoryRowCount == 0) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        catch (final Exception e) {
            MDMPHandler.logger.log(Level.INFO, "Exception in checking first startup", e);
            return Boolean.FALSE;
        }
        MDMPHandler.logger.log(Level.INFO, "MDMPHandler : isFirstStartUp() : DCSERVERBUILDHISTORY table entry is empty so it is not 1ft startup");
        return Boolean.FALSE;
    }
    
    private static void checkAndGenerateServerCertificate() {
        final Properties properties = new Properties();
        final HashMap taskInfo = new HashMap();
        taskInfo.put("taskName", "ServerSANCertificateGeneratorTask");
        taskInfo.put("schedulerTime", System.currentTimeMillis());
        new ServerSANCertificateGeneratorTask().executeTask(properties);
    }
    
    private static void updateDefaultAdminTimeZone() {
        try {
            final String defaultLanguage = "en_US";
            final String languageSelected = SyMUtil.getValueFromSystemLogFile("InstallerSelectedLang");
            final Locale userLocale = I18NUtil.getDefaultLocale();
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaUserProfile");
            updateQuery.setUpdateColumn("TIMEZONE", (Object)SyMUtil.getDefaultTimeZoneID());
            final String localLanguage = userLocale.getLanguage();
            final String localRegion = userLocale.getCountry();
            final String userLocaleLanguage = localLanguage + "_" + localRegion;
            if (defaultLanguage.equals(userLocaleLanguage) && !defaultLanguage.equals(languageSelected) && languageSelected != null && !languageSelected.equals("Undefined")) {
                final String[] userSelectedLanguage = languageSelected.split("_");
                final String selectedLanguage = userSelectedLanguage[0];
                final String selectedCountry = userSelectedLanguage[1];
                updateQuery.setUpdateColumn("LANGUAGE_CODE", (Object)selectedLanguage);
                updateQuery.setUpdateColumn("COUNTRY_CODE", (Object)selectedCountry);
            }
            else {
                updateQuery.setUpdateColumn("LANGUAGE_CODE", (Object)localLanguage);
                updateQuery.setUpdateColumn("COUNTRY_CODE", (Object)localRegion);
            }
            DataAccess.update(updateQuery);
        }
        catch (final Exception e) {
            MDMPHandler.logger.log(Level.SEVERE, "Exception while updatating default admin time zone value...", e);
        }
    }
    
    private static boolean validateProxyConnetion() {
        final JSONObject proxyJson = new JSONObject();
        boolean proxyStatus = false;
        try {
            proxyJson.put("proxyType", 1);
            final int resultCode = SettingsUtil.validateProxy(proxyJson);
            if (resultCode == 1504) {
                proxyStatus = true;
            }
        }
        catch (final Exception exception) {
            MDMPHandler.logger.log(Level.SEVERE, "Exception while validating proxy settings...", exception);
        }
        return proxyStatus;
    }
    
    private static void updateELMKeys() {
        final AndroidAgentSecretsHandler androidAgentSecretsHandler = new AndroidAgentSecretsHandler();
        androidAgentSecretsHandler.updateElmKeysInDB();
    }
    
    private static synchronized void registerOneLineLoggerListenerImpl() {
        UserListenerHandler.getInstance().addUserListener((UserListener)new OnelineLoggerUserListenerImpl());
        RoleListenerHandler.getInstance().addRoleListener((RoleListener)new OnelineLoggerRoleListener());
    }
    
    private static void createProductBannerSyncScheduler() {
        new ProductBannerSyncTask().initialiseTask();
    }
    
    static {
        MDMPHandler.logger = Logger.getLogger("DCServiceLogger");
    }
}
