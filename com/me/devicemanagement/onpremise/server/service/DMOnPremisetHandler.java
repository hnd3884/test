package com.me.devicemanagement.onpremise.server.service;

import com.me.devicemanagement.framework.server.admin.DomainListener;
import com.me.devicemanagement.framework.server.admin.DomainHandler;
import com.me.devicemanagement.onpremise.server.admin.MessageDomainListener;
import com.me.devicemanagement.onpremise.server.common.CommonUserListenerImpl;
import com.me.devicemanagement.framework.server.authentication.UserListener;
import com.me.devicemanagement.framework.server.authentication.UserListenerHandler;
import com.me.devicemanagement.onpremise.server.sdp.SDPListenerImpl;
import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredListener;
import com.me.devicemanagement.onpremise.server.general.ProxyListenerGeneralImpl;
import com.me.devicemanagement.onpremise.server.common.ProxyConfiguredHandler;
import com.adventnet.persistence.fos.FOS;
import java.util.Map;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.start.util.NSStartUpUtil;
import com.me.devicemanagement.onpremise.server.license.LicenseUtil;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.net.InetAddress;
import java.io.File;
import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.server.status.SysStatusHandler;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.scheduler.SchedulerTuning;
import com.me.devicemanagement.onpremise.server.settings.nat.NATListener;
import com.me.devicemanagement.onpremise.server.settings.nat.NATListenerCommonImpl;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class DMOnPremisetHandler
{
    private static Logger logger;
    private static final Logger COMMONLOGGER;
    private static final String UPLOAD_DEBUGFILE_PIDATA_EXCLUSION = "ConfFileKeysWithoutPI.conf";
    private static final String SERVER_INFO_FILENAME = "server_info.props";
    public static final String SERVER_INFO_WITHOUTPI_FILENAME = "server_info_withoutPI.props";
    
    public static void initiate() {
        firstInitialize();
        initializeCommonEnv();
        checkLicensedRoleWithDB();
        createServerInfoConf();
        registerProxyListener();
        ApiFactoryProvider.getDemoUtilAPI().loadDemoModeValFromConf();
        registerSDPListenerImpl();
        registerCommonUserListenerImpl();
        registerMessageDomainListener();
        sqlAutoRetryStatus();
        NATHandler.getInstance().addNATListener(new NATListenerCommonImpl());
        SchedulerTuning.getInstance().checkChangesInThreadPoolsConf();
        initiateCSVProcessingResumeTask();
    }
    
    private static void initiateCSVProcessingResumeTask() {
        DMOnPremisetHandler.logger.log(Level.INFO, "____________________________________");
        DMOnPremisetHandler.logger.log(Level.INFO, "Starting CSVProcessingResumeTask Service...");
        DMOnPremisetHandler.logger.log(Level.INFO, "____________________________________");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "CSVProcessingHaltTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        try {
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.devicemanagement.framework.server.csv.CSVProcessingResumeTask", taskInfoMap, new Properties());
        }
        catch (final Exception exp) {
            DMOnPremisetHandler.logger.log(Level.WARNING, "Exception occurred during the schdule mdm command : {0}", exp);
        }
    }
    
    private static void firstInitialize() {
        final boolean isClientDataAlreadyAvailable = createClientDataDir();
        DMOnPremisetHandler.logger.log(Level.INFO, "firstInitialize isClientDataAlreadyAvailable in onpremise: " + isClientDataAlreadyAvailable);
        if (SyMUtil.getServerParameter("last_db_migration_time") == null) {
            SyMUtil.updateServerParameter("last_db_migration_time", "0");
        }
    }
    
    public static synchronized boolean createClientDataDir() {
        Boolean clientDirectoryAlreadyAvailable = Boolean.TRUE;
        final String clientDataDirName = DCMetaDataUtil.getInstance().getClientDataDir();
        if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(clientDataDirName)) {
            final boolean result = ApiFactoryProvider.getFileAccessAPI().createDirectory(clientDataDirName);
            clientDirectoryAlreadyAvailable = Boolean.FALSE;
            DMOnPremisetHandler.logger.log(Level.INFO, "Client Data Directory has been created. Dir Name: " + clientDataDirName + "\t return value: " + result);
        }
        return clientDirectoryAlreadyAvailable;
    }
    
    private static void initializeCommonEnv() {
        SysStatusHandler.updateServerUptime("DesktopCentralService", System.currentTimeMillis(), null);
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        if (!isMSP) {
            SyMUtil.updateCustomerSegmentationInfo();
        }
        LicenseProvider.getInstance().setmoduleProMap();
    }
    
    private static void checkLicensedRoleWithDB() {
        try {
            DMOnPremisetHandler.logger.log(Level.INFO, "checkLicensedRoleWithDB");
            final String productTypeInDB = SyMUtil.getSyMParameter("productType");
            final String productTypeInFile = LicenseProvider.getInstance().getProductType();
            if (productTypeInDB != null && productTypeInFile != null && !productTypeInDB.equalsIgnoreCase(productTypeInFile)) {
                DMOnPremisetHandler.logger.log(Level.INFO, "Product Type in DB:{0}", productTypeInDB);
                DMOnPremisetHandler.logger.log(Level.INFO, "Product Type in License File:{0}", productTypeInFile);
                DMOnPremisetHandler.logger.log(Level.INFO, "License mismatch..recreate roles for corresponding licenseFile");
                LicenseProvider.getInstance().rolehandleForLicenseChage(productTypeInFile);
            }
        }
        catch (final Exception ex) {
            DMOnPremisetHandler.logger.log(Level.WARNING, "Caught exception while ", ex);
        }
    }
    
    private static void createServerInfoConf() {
        final Map props = new LinkedHashMap();
        try {
            final String serverDir = SyMUtil.getInstallationDir();
            final String serverInfoConf = serverDir + File.separator + "conf" + File.separator + "server_info.props";
            final String serverInfoLog = serverDir + File.separator + "logs" + File.separator + "server_info.props";
            final String hostName = InetAddress.getLocalHost().getHostName();
            if (hostName != null && hostName.trim().length() > 0) {
                props.put("server.host.name", hostName);
            }
            final String serverOS = System.getProperty("os.name");
            if (serverOS != null && serverOS.trim().length() > 0) {
                props.put("server.os", serverOS);
            }
            final String systemHWType = SyMUtil.getServerParameter("SYSTEM_HW_TYPE");
            DMOnPremisetHandler.logger.log(Level.INFO, "hardware type" + systemHWType);
            if (systemHWType != null) {
                props.put("server.hardware.type", systemHWType);
            }
            final String installDir = SyMUtil.getInstallationDir();
            if (installDir != null && installDir.trim().length() > 0) {
                props.put("installation.dir", installDir);
            }
            final String installDate = SyMUtil.getInstallationDate();
            if (installDate != null && installDate.trim().length() > 0) {
                props.put("installation.date", installDate);
            }
            final String dcProductArchitecture = StartupUtil.dcProductArch();
            if (dcProductArchitecture != null) {
                props.put("dc.architecture", dcProductArchitecture);
            }
            else {
                props.put("dc.architecture", "--");
            }
            final String buildNumber = SyMUtil.getProductProperty("buildnumber");
            if (buildNumber != null && buildNumber.trim().length() > 0) {
                props.put("build.number", buildNumber);
            }
            if (CustomerInfoUtil.isDC() || CustomerInfoUtil.isPMP() || CustomerInfoUtil.getInstance().isRAP()) {
                final String somSummary = ApiFactoryProvider.getServiceAPI(true).getTrackingSummary();
                if (somSummary != null && somSummary.trim().length() > 0) {
                    props.put("som.summary", somSummary);
                }
            }
            if (CustomerInfoUtil.isMDM()) {
                final String mdmSummary = ApiFactoryProvider.getServiceAPI(false).getTrackingSummary();
                if (mdmSummary != null && mdmSummary.trim().length() > 0) {
                    props.put("mdm.summary", mdmSummary);
                }
            }
            if (WebServerUtil.server_ip_props != null && !WebServerUtil.server_ip_props.equals("")) {
                DMOnPremisetHandler.logger.log(Level.INFO, "server IP in Properties : " + WebServerUtil.server_ip_props);
                props.put("user.specified.ip", WebServerUtil.server_ip_props);
            }
            final Map licenseProps = LicenseUtil.getLicenseProps();
            props.putAll(licenseProps);
            final Map wsProps = WebServerUtil.getWebServerSettings(Boolean.FALSE);
            props.putAll(wsProps);
            final Map nsProps = NSStartUpUtil.getNSSettings();
            props.putAll(nsProps);
            final Map dbProps = DBUtil.getDBServerProperties();
            props.putAll(dbProps);
            final Map fosProps = getFosProps();
            props.putAll(fosProps);
            final String currentTime = SyMUtil.getCurrentTimeWithDate();
            final String comments = "Generated at " + currentTime;
            FileAccessUtil.writeMapAsPropertiesIntoFile(props, serverInfoConf, comments);
            FileAccessUtil.writeMapAsPropertiesIntoFile(props, serverInfoLog, comments);
        }
        catch (final Exception ex) {
            DMOnPremisetHandler.logger.log(Level.WARNING, "Caught exception while creating serverinfo conf file", ex);
        }
    }
    
    public static void createServerInfoConfWithoutPI() {
        DMOnPremisetHandler.COMMONLOGGER.log(Level.INFO, "Generating server_info_withoutPI.props");
        final String inclusionKeysFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "ConfFileKeysWithoutPI.conf";
        final String logsDir = System.getProperty("server.home") + File.separator + "logs";
        final String serverInfoFilePath = logsDir + File.separator + "server_info.props";
        final String serverInfoWithoutPIFilePath = logsDir + File.separator + "server_info_withoutPI.props";
        final String keySeparator = ":";
        try {
            new File(serverInfoWithoutPIFilePath).createNewFile();
            final Properties inclusionKeysProps = FileAccessUtil.readProperties(inclusionKeysFile);
            final Properties serverInfoProps = FileAccessUtil.readProperties(serverInfoFilePath);
            final Properties serverInfoPropsWithoutPI = new Properties();
            final String serverInfoInclusionKeys = inclusionKeysProps.getProperty("server_info.props");
            if (serverInfoInclusionKeys != null) {
                final String[] split;
                final String[] propsWithoutPI = split = serverInfoInclusionKeys.split(",");
                for (final String key : split) {
                    if (key.contains(keySeparator)) {
                        final String[] keys = key.split(keySeparator);
                        if (keys.length > 1) {
                            final String mainKey = keys[0];
                            final String subKey = keys[1];
                            final String mainKeyValue = serverInfoProps.getProperty(mainKey);
                            if (mainKeyValue != null) {
                                final String[] split2;
                                final String[] mainKeyValues = split2 = mainKeyValue.split("[|]");
                                for (final String value : split2) {
                                    if (value.contains(subKey)) {
                                        serverInfoPropsWithoutPI.setProperty(subKey, value);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        final String value2 = serverInfoProps.getProperty(key);
                        if (value2 != null) {
                            serverInfoPropsWithoutPI.setProperty(key, value2.trim());
                        }
                    }
                }
                FileAccessUtil.storeProperties(serverInfoPropsWithoutPI, serverInfoWithoutPIFilePath, (boolean)Boolean.FALSE);
                DMOnPremisetHandler.COMMONLOGGER.log(Level.INFO, "server_info_withoutPI.propsfile generated successfully");
            }
        }
        catch (final Exception ex) {
            DMOnPremisetHandler.COMMONLOGGER.log(Level.WARNING, "Caught exception while generating server_info_withoutPI.props file : ", ex);
        }
    }
    
    public static boolean deleteServerInfoConfWithoutPI() {
        boolean deletionStatus = Boolean.FALSE;
        final String serverInfoWithoutPIFilePath = System.getProperty("server.home") + File.separator + "logs" + File.separator + "server_info_withoutPI.props";
        final File serverInfoFileWithoutPI = new File(serverInfoWithoutPIFilePath);
        if (serverInfoFileWithoutPI.exists()) {
            DMOnPremisetHandler.COMMONLOGGER.log(Level.INFO, "server_info_withoutPI.props file exists");
            deletionStatus = serverInfoFileWithoutPI.delete();
        }
        DMOnPremisetHandler.COMMONLOGGER.log(Level.INFO, "server_info_withoutPI.props file deletion status : " + deletionStatus);
        return deletionStatus;
    }
    
    private static Map getFosProps() {
        final Map props = new LinkedHashMap();
        try {
            if (FOS.isEnabled()) {
                final FOS fos = new FOS();
                fos.initialize();
                final String ipAddr = fos.getFOSConfig().ipaddr();
                final String otherIP = fos.getOtherNode();
                props.put("failover", "enabled");
                props.put("serving-ip", ipAddr);
                props.put("peer-ip", otherIP);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DMOnPremisetHandler.class.getName()).log(Level.SEVERE, "Caught Exception while getting failover properties..", ex);
        }
        return props;
    }
    
    private static void registerProxyListener() {
        ProxyConfiguredHandler.getInstance().addProxyConfiguredListener(new ProxyListenerGeneralImpl());
    }
    
    private static void registerSDPListenerImpl() {
        final SDPListenerImpl sdpListener = new SDPListenerImpl();
        UserListenerHandler.getInstance().addUserListener((UserListener)sdpListener);
    }
    
    private static synchronized void registerCommonUserListenerImpl() {
        final CommonUserListenerImpl commonUserListener = new CommonUserListenerImpl();
        UserListenerHandler.getInstance().addUserListener((UserListener)commonUserListener);
    }
    
    private static synchronized void registerMessageDomainListener() {
        final MessageDomainListener domainListener = new MessageDomainListener();
        DomainHandler.getInstance().addDomainListener((DomainListener)domainListener);
    }
    
    private static void sqlAutoRetryStatus() {
        final File sqlLock = new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "sqlAutoStarted.lock");
        if (sqlLock.exists()) {
            SyMUtil.updateServerParameter("ServerStartedBySqlRetry", "true");
            sqlLock.delete();
        }
    }
    
    static {
        DMOnPremisetHandler.logger = Logger.getLogger("DCServiceLogger");
        COMMONLOGGER = Logger.getLogger(DMOnPremisetHandler.class.getName());
    }
}
