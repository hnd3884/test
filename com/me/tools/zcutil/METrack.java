package com.me.tools.zcutil;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class METrack extends Thread
{
    private static final Logger LOGGER;
    private static ZCUtil zcu;
    private static Properties confProp;
    private static String confDir;
    private static String homeDir;
    private static String frameworkName;
    private static String formDef;
    private static Properties proxyDetails;
    private static String licenseDir;
    private static boolean meLoadQry;
    private static String productName;
    private static int database;
    private static boolean loadUpdateManager;
    private static Properties additionalBaseFormData;
    private static boolean isOD;
    
    @Override
    public void run() {
        try {
            this.appStart();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public METrack(final String cDir, final String hDir, final String framework) {
        this.init(cDir, hDir, framework, null, null, null, true, null, -1, true, null);
    }
    
    public METrack(final String cDir, final String hDir, final String framework, final String formConf) {
        this.init(cDir, hDir, framework, formConf, null, null, true, null, -1, true, null);
    }
    
    public METrack(final String cDir, final String hDir, final String framework, final String formConf, final String licDir, final boolean loadQry) {
        this.init(cDir, hDir, framework, formConf, null, licDir, loadQry, null, -1, true, null);
    }
    
    public METrack(final String cDir, final String hDir, final String framework, final String formConf, final String licDir, final boolean loadQry, final Properties proxyProp) {
        this.init(cDir, hDir, framework, formConf, proxyProp, licDir, loadQry, null, -1, true, null);
    }
    
    public METrack(final String cDir, final String hDir, final String framework, final String formConf, final String licDir, final boolean loadQry, final Properties proxyProp, final String productName) {
        this.init(cDir, hDir, framework, formConf, proxyProp, licDir, loadQry, productName, -1, true, null);
    }
    
    public METrack(final String cDir, final String hDir, final String framework, final String formConf, final String licDir, final boolean loadQry, final Properties proxyProp, final String productName, final int mec) {
        this.init(cDir, hDir, framework, formConf, proxyProp, licDir, loadQry, productName, mec, true, null);
    }
    
    public METrack(final String cDir, final String hDir, final String framework, final String formConf, final String licDir, final boolean loadQry, final Properties proxyProp, final String productName, final int mec, final boolean loadUpdateManager, final Properties additionalBaseDetails) {
        this.init(cDir, hDir, framework, formConf, proxyProp, licDir, loadQry, productName, mec, loadUpdateManager, additionalBaseDetails);
    }
    
    private void init(final String cDir, final String hDir, final String framework, final String formConf, final Properties proxyProp, final String licDir, final boolean loadQry, final String prdName, final int mec, final boolean loadUpdateManager, final Properties additionalBaseDetails) {
        METrack.additionalBaseFormData = null;
        METrack.confDir = cDir;
        METrack.homeDir = hDir;
        METrack.frameworkName = framework;
        METrack.formDef = formConf;
        METrack.proxyDetails = proxyProp;
        METrack.licenseDir = licDir;
        METrack.meLoadQry = loadQry;
        METrack.productName = prdName;
        METrack.database = mec;
        METrack.loadUpdateManager = loadUpdateManager;
        METrack.additionalBaseFormData = additionalBaseDetails;
        METrack.zcu = new ZCUtil();
        METrack.confProp = METrack.zcu.getConfValue();
    }
    
    public void appStart() {
        try {
            if (METrack.zcu.getConnectStatus()) {
                final Properties prop = METrack.zcu.getCustomerDetails();
                String cid = null;
                if (METrack.zcu.isNewCustomer()) {
                    if (METrack.zcu.getOldInstallationID() != null) {
                        prop.setProperty("old_installation_id", METrack.zcu.getOldInstallationID().trim());
                        prop.setProperty("old_product", METrack.zcu.getOldProductNae());
                    }
                    cid = METrack.zcu.addNewCustomer(prop, METrack.confProp.getProperty("appname"), METrack.confProp.getProperty("dataform"), METrack.proxyDetails);
                }
                else {
                    cid = METrack.zcu.getCreatorId();
                }
                if (cid != null) {
                    if (METrack.frameworkName != null && METrack.meLoadQry) {
                        final RunSelectQuery rsq = new RunSelectQuery();
                        long delay = 0L;
                        if (METrack.confProp.getProperty("scheduledelay") != null) {
                            delay = Long.parseLong(METrack.confProp.getProperty("scheduledelay"));
                        }
                        if (METrack.frameworkName.equalsIgnoreCase("mickeylite") && rsq.getOneValue("select SCHEDULE_NAME from Schedule where SCHEDULE_NAME = 'METrack'") == null) {
                            METrack.LOGGER.log(Level.INFO, "Going to create scheduler for METrack");
                            final CreateMickeyLiteScheduler sch = new CreateMickeyLiteScheduler();
                            sch.addScheduler(delay);
                        }
                        else if (METrack.frameworkName.equalsIgnoreCase("mickey") && rsq.getOneValue("select NAME from Templates where NAME = 'METrackWorkFlow'") == null) {
                            METrack.LOGGER.log(Level.INFO, "Going to create scheduler for METrack");
                            final CreateMickeyScheduler sch2 = new CreateMickeyScheduler();
                            sch2.addScheduler(delay);
                        }
                    }
                    logStartUp();
                }
            }
            else {
                logStartUp();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void logStartUp() {
        try {
            if (METrack.zcu.getConnectStatus() && METrack.zcu.getProductConf().isActionLogFormEnabled()) {
                METrack.zcu.logStartTime();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void shutdown(final Properties proxyProp) {
        try {
            if (!METrack.zcu.isNewCustomer() && METrack.zcu.getConnectStatus() && METrack.zcu.getProductConf().isActionLogFormEnabled()) {
                final Properties aProp = new Properties();
                aProp.setProperty("customerid", METrack.zcu.getCreatorId());
                aProp.setProperty("starttime", METrack.zcu.getStartTime());
                aProp.setProperty("shutdowntime", METrack.zcu.getCurrentDateTime("GMT"));
                METrack.zcu.addRecord(METrack.confProp.getProperty("appname"), METrack.confProp.getProperty("actionlog"), aProp, proxyProp);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getHomeDir() {
        return METrack.homeDir;
    }
    
    public static String getConfDir() {
        return METrack.confDir;
    }
    
    public static void ZCScheduler() {
        ZCScheduler(METrack.proxyDetails, false);
    }
    
    public static void ZCScheduler(final Properties proxyProp) {
        ZCScheduler(proxyProp, false);
    }
    
    public static void ZCScheduler(final Properties proxyProp, final boolean pushAllBaseDetails) {
        if (METrack.zcu.getConnectStatus()) {
            final ZCScheduler zcs = new ZCScheduler();
            zcs.zcScheduler(proxyProp, pushAllBaseDetails);
        }
    }
    
    public static void enable(final Properties proxyProp) {
        METrack.zcu.updateConnectionStatus("true");
        if (METrack.zcu.getCreatorId() != null) {
            final Properties prop = new Properties();
            prop.setProperty("customerid", METrack.zcu.getCreatorId());
            prop.setProperty("Status", "Enabled");
            METrack.zcu.addRecord(METrack.confProp.getProperty("appname"), METrack.confProp.getProperty("datainputform"), prop, proxyProp);
        }
    }
    
    public static void disable(final Properties proxyProp) {
        METrack.zcu.updateConnectionStatus("false");
        if (METrack.zcu.getCreatorId() != null) {
            final Properties prop = new Properties();
            prop.setProperty("customerid", METrack.zcu.getCreatorId());
            prop.setProperty("Status", "Disabled");
            METrack.zcu.addRecord(METrack.confProp.getProperty("appname"), METrack.confProp.getProperty("datainputform"), prop, proxyProp);
        }
    }
    
    public static boolean isEnable() {
        return METrack.zcu.getConnectStatus();
    }
    
    public static String getFormConf() {
        return METrack.formDef;
    }
    
    public static String updateRecord(final Properties prop, final String formName, final Properties proxyProp) {
        String result = "failure";
        try {
            if (METrack.zcu.getConnectStatus() && formName != null && prop != null && prop.size() > 0 && METrack.zcu.getCreatorId() != null) {
                prop.setProperty("customerid", METrack.zcu.getCreatorId());
                result = METrack.zcu.getTagTextNode(METrack.zcu.addRecord(METrack.confProp.getProperty("appname"), formName, prop, proxyProp), "status");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    protected static String getLicenseDir() {
        return METrack.licenseDir;
    }
    
    protected static boolean loadQueryExc() {
        return METrack.meLoadQry;
    }
    
    public static HashMap getQueries(final ArrayList<String> keyList) {
        return getQueries(keyList, null);
    }
    
    public static HashMap getQueries(final ArrayList<String> keyList, final Properties proxyProp) {
        if (METrack.zcu.getConnectStatus()) {
            return METrack.zcu.getQueries(keyList, proxyProp);
        }
        return null;
    }
    
    public static String getProductName() {
        return METrack.productName;
    }
    
    public static void shutdown() {
        shutdown(null);
    }
    
    public static void enable() {
        enable(null);
    }
    
    public static void disable() {
        disable(null);
    }
    
    public static int getDB() {
        return METrack.database;
    }
    
    public static String updateUserDetails(final Properties proxyProp) {
        return updateUserDetails(proxyProp, null, false);
    }
    
    public static String updateUserDetails(final Properties proxyProp, final Properties additionalBaseDetails) {
        return updateUserDetails(proxyProp, additionalBaseDetails, false);
    }
    
    public static String updateUserDetails(final Properties proxyProp, final Properties additionalBaseDetails, final boolean pushAllBaseDetails) {
        if (METrack.zcu.getConnectStatus()) {
            METrack.additionalBaseFormData = null;
            METrack.additionalBaseFormData = additionalBaseDetails;
            return METrack.zcu.updateBaseForm(proxyProp, pushAllBaseDetails);
        }
        return "failure";
    }
    
    public static String getAppName() {
        return METrack.confProp.getProperty("appname");
    }
    
    public static String getCustomerID() {
        return METrack.zcu.getCreatorId();
    }
    
    public static String updateMultiFormdData(final ApplicationData appData, final Properties proxyDetails) {
        String response = null;
        if (METrack.zcu.getConnectStatus()) {
            final Properties queryString = new Properties();
            queryString.setProperty("zc_ownername", METrack.confProp.getProperty("zowner"));
            queryString.setProperty("XMLString", appData.getUploadData());
            queryString.setProperty("apikey", METrack.confProp.getProperty("key"));
            response = METrack.zcu.connect(METrack.confProp.getProperty("url") + "api/xml/write?", queryString, proxyDetails).toString();
        }
        return response;
    }
    
    public static Properties getAdditionalBaseFormDetails() {
        return METrack.additionalBaseFormData;
    }
    
    public static boolean loadUpdateManager() {
        return METrack.loadUpdateManager;
    }
    
    public static String addRecord(final Properties addRow, final String authToken, final String owner, final String appName, final String formName, final Properties proxyDetails) {
        return METrack.zcu.connect(METrack.confProp.getProperty("url") + "api/" + owner + "/json/" + appName + "/form/" + formName + "/record/add?authtoken=" + authToken + "&scope=creatorapi", addRow, proxyDetails).toString();
    }
    
    public static String viewRecord(final String authToken, final String owner, final String appName, final String viewName, final String criteria, final Properties proxyDetails) {
        final Properties viewProp = new Properties();
        final String url = METrack.confProp.getProperty("url") + "api/json/" + appName + "/view/" + viewName + "?authtoken=" + authToken + "&scope=creatorapi";
        viewProp.setProperty("zc_ownername", owner);
        if (criteria != null) {
            viewProp.setProperty("criteria", criteria);
        }
        return METrack.zcu.connect(url, viewProp, proxyDetails).toString();
    }
    
    public static String editRecord(final Properties editRow, final String authToken, final String owner, final String appName, final String viewName, final String criteria, final Properties proxyDetails) {
        final String url = METrack.confProp.getProperty("url") + "api/" + owner + "/json/" + appName + "/form/" + viewName + "/record/update?authtoken=" + authToken + "&scope=creatorapi";
        editRow.setProperty("criteria", criteria);
        return METrack.zcu.connect(url, editRow, proxyDetails).toString();
    }
    
    public Vector<Properties> getServicePacksAvailableForDownload(final Properties proxyProp) {
        if (METrack.zcu.getConnectStatus() && METrack.zcu.getCreatorId() != null) {
            return METrack.zcu.viewRecordAsVector(METrack.confProp.getProperty("sp_view"), "status==\"true\"", proxyProp);
        }
        return null;
    }
    
    public static boolean downLoadServicePack(final String servicePackID, final String downloadFromLive, final Properties proxyProp) {
        try {
            if (METrack.zcu.getConnectStatus() && METrack.zcu.getCreatorId() != null) {
                Properties viewEntries = null;
                viewEntries = new Properties();
                viewEntries.setProperty("servicepack_id", servicePackID);
                viewEntries.setProperty("installationid", METrack.zcu.getCreatorId());
                viewEntries.setProperty("download_from_live", downloadFromLive);
                final String response = METrack.zcu.addRecord(METrack.confProp.getProperty("appname"), METrack.confProp.getProperty("sp_form"), viewEntries, proxyProp);
                final String status = METrack.zcu.getTagTextNode(response, "status");
                if ("success".equalsIgnoreCase(status)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static void updateServicePackInstallationStatus(final String status, final String comment, final String spVersion, final Properties proxyProp) {
        try {
            if (METrack.zcu.getConnectStatus() && METrack.zcu.getCreatorId() != null) {
                Properties viewEntries = null;
                viewEntries = new Properties();
                viewEntries.setProperty("servicepack_version", spVersion);
                viewEntries.setProperty("installationid", METrack.zcu.getCreatorId());
                viewEntries.setProperty("request_for", status);
                if (comment != null) {
                    viewEntries.setProperty("comment", comment);
                }
                METrack.zcu.addRecord(METrack.confProp.getProperty("appname"), METrack.confProp.getProperty("sp_logger_updater"), viewEntries, proxyProp);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void storeProperties(final Properties migProp, final String metrackConfLocation) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(metrackConfLocation));
            migProp.store(fos, "");
        }
        catch (final Exception e) {
            e.printStackTrace();
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception e) {
                METrack.LOGGER.log(Level.INFO, "METRack Exception while storeProperties: " + e.toString());
            }
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception e2) {
                METrack.LOGGER.log(Level.INFO, "METRack Exception while storeProperties: " + e2.toString());
            }
        }
    }
    
    private static Properties loadPropertiesFile(final String fileLocation) {
        try {
            if (new File(fileLocation).exists()) {
                final Properties cProp = new Properties();
                cProp.load(new FileInputStream(fileLocation));
                return cProp;
            }
        }
        catch (final Exception e) {
            METrack.LOGGER.log(Level.INFO, "METRack Exception while loadPropertiesFile : " + e.toString());
        }
        return null;
    }
    
    public static ZCUtil getZCUtil() {
        return METrack.zcu;
    }
    
    public static String getFormKey(final String formName) {
        return METrack.zcu.getProductConf().getFormKey(formName);
    }
    
    public static void setZCUtil(final ZCUtil zcuUtil) {
        METrack.zcu = zcuUtil;
    }
    
    public static void setConfDir(final String dir) {
        METrack.confDir = dir;
    }
    
    protected static void setIsOd(final boolean isOD) {
        METrack.isOD = isOD;
    }
    
    protected static boolean getIsOd() {
        return METrack.isOD;
    }
    
    protected static void setConfProp(final Properties confProp) {
        METrack.confProp = confProp;
    }
    
    static {
        LOGGER = Logger.getLogger(METrack.class.getName());
        METrack.zcu = null;
        METrack.confProp = null;
        METrack.confDir = null;
        METrack.homeDir = null;
        METrack.frameworkName = null;
        METrack.formDef = null;
        METrack.proxyDetails = null;
        METrack.licenseDir = null;
        METrack.meLoadQry = true;
        METrack.productName = null;
        METrack.database = -1;
        METrack.loadUpdateManager = true;
        METrack.additionalBaseFormData = null;
        METrack.isOD = false;
    }
}
