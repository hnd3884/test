package com.me.devicemanagement.onpremise.server.metrack;

import com.me.devicemanagement.onpremise.server.service.DCServerBuildHistoryProvider;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import com.adventnet.tools.prevalent.Wield;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.Iterator;
import com.me.devicemanagement.onpremise.server.metrack.ondemand.ONDemandDataCollectorBean;
import com.me.devicemanagement.onpremise.server.metrack.ondemand.ONDemandDataCollectorApi;
import com.me.tools.zcutil.METrack;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.onpremise.server.metrack.ondemand.ONDemandDataCollector;
import com.me.devicemanagement.onpremise.server.metrack.ondemand.ONDemandDataCollectorUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.tools.zcutil.ApplicationData;
import java.util.Properties;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.logging.Logger;

public class METrackerHandler implements METrackerHandlerAPI
{
    private static Logger logger;
    private static String sourceClass;
    private boolean isMETrackingTrackerEnable;
    private static String meTrackingTrackerClassName;
    
    public METrackerHandler() {
        this.isMETrackingTrackerEnable = false;
    }
    
    @Override
    public void postTrackingData(final HashMap<String, String> trackerHash) {
        try {
            this.meTrackingUpdater();
            Hashtable<String, Properties> nonDefaultFormRecords = new Hashtable<String, Properties>();
            final Properties postedDiffApplicableData = new Properties();
            final ApplicationData appData = new ApplicationData();
            final JSONObject fullPostDetails = new JSONObject();
            final JSONArray fullPostForms = new JSONArray();
            final JSONObject dataGetTimeDur = new JSONObject();
            final Properties meTrackingTracker = new Properties();
            final Properties proxyProperties = getProxyProps();
            final ArrayList modules = new ArrayList((Collection<? extends E>)trackerHash.keySet());
            final String lastSuccessfullyFileData = METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "last_successfully_post_data.properties";
            boolean diffApplicable;
            if (METrackerDiffUtil.getInstance().ifDiffApplicable() && ApiFactoryProvider.getFileAccessAPI().isFileExists(lastSuccessfullyFileData)) {
                diffApplicable = true;
                fullPostDetails.put("Type", (Object)"1");
            }
            else {
                diffApplicable = false;
                fullPostDetails.put("Type", (Object)"0");
            }
            if (ONDemandDataCollectorUtil.getInstance().isONDemandDataCollectorEnabled()) {
                final long startTime = System.currentTimeMillis();
                final ONDemandDataCollectorApi onDemandDataCollector = new ONDemandDataCollector();
                final ONDemandDataCollectorBean onDemandDataCollectorBean = onDemandDataCollector.getAllOndemandProperties();
                dataGetTimeDur.put("ONDemandDataCollector", TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - startTime));
                ((Hashtable<String, String>)meTrackingTracker).put("ONDemand_ZCFailed_Requests", onDemandDataCollectorBean.getZCFailedRequests());
                final Hashtable<String, Vector<Properties>> defaultFormRecords = onDemandDataCollectorBean.getDefaultFormRecords();
                if (defaultFormRecords != null) {
                    for (final String zcFormName : defaultFormRecords.keySet()) {
                        appData.addRecords(zcFormName, (Vector)defaultFormRecords.get(zcFormName));
                    }
                }
                nonDefaultFormRecords = onDemandDataCollectorBean.getNonDefaultFormRecords();
            }
            for (final Object module : modules) {
                final String className = module.toString();
                final String formName = trackerHash.get(className);
                if (!"inputmetrackingtracker".equals(formName)) {
                    final long startTime2 = System.currentTimeMillis();
                    final MEDMTracker tracker = (MEDMTracker)Class.forName(className).newInstance();
                    Properties trackerProperties = tracker.getTrackerProperties();
                    dataGetTimeDur.put(formName, TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - startTime2));
                    if (trackerProperties == null || trackerProperties.isEmpty()) {
                        continue;
                    }
                    if (nonDefaultFormRecords.containsKey(formName)) {
                        trackerProperties = this.concatenate2Props(nonDefaultFormRecords.get(formName), trackerProperties);
                        nonDefaultFormRecords.remove(formName);
                    }
                    if (diffApplicable) {
                        final Properties diffTrackerProperties = METrackerDiffUtil.getInstance().getTrackerDiff(formName, trackerProperties);
                        if (diffTrackerProperties == null) {
                            appData.addRecord(formName, trackerProperties);
                            ((Hashtable<String, String>)postedDiffApplicableData).put(formName, new JSONObject((Map)trackerProperties).toString());
                            fullPostForms.put((Object)formName);
                        }
                        else {
                            if (diffTrackerProperties.isEmpty()) {
                                continue;
                            }
                            appData.addRecord(formName, diffTrackerProperties);
                            ((Hashtable<String, String>)postedDiffApplicableData).put(formName, new JSONObject((Map)trackerProperties).toString());
                        }
                    }
                    else {
                        appData.addRecord(formName, trackerProperties);
                        ((Hashtable<String, String>)postedDiffApplicableData).put(formName, new JSONObject((Map)trackerProperties).toString());
                    }
                }
                else {
                    this.isMETrackingTrackerEnable = true;
                    METrackerHandler.meTrackingTrackerClassName = className;
                }
            }
            if (ONDemandDataCollectorUtil.getInstance().isONDemandDataCollectorEnabled() && nonDefaultFormRecords != null) {
                for (final String formName2 : nonDefaultFormRecords.keySet()) {
                    final Properties properties = nonDefaultFormRecords.get(formName2);
                    if (properties != null && !properties.isEmpty()) {
                        if (diffApplicable) {
                            final Properties diffTrackerProperties2 = METrackerDiffUtil.getInstance().getTrackerDiff(formName2, properties);
                            if (diffTrackerProperties2 == null) {
                                appData.addRecord(formName2, properties);
                                ((Hashtable<String, String>)postedDiffApplicableData).put(formName2, new JSONObject((Map)properties).toString());
                                fullPostForms.put((Object)formName2);
                            }
                            else {
                                if (diffTrackerProperties2.isEmpty()) {
                                    continue;
                                }
                                appData.addRecord(formName2, diffTrackerProperties2);
                                ((Hashtable<String, String>)postedDiffApplicableData).put(formName2, new JSONObject((Map)properties).toString());
                            }
                        }
                        else {
                            appData.addRecord(formName2, properties);
                            ((Hashtable<String, String>)postedDiffApplicableData).put(formName2, new JSONObject((Map)properties).toString());
                        }
                    }
                }
            }
            if (this.isMETrackingTrackerEnable) {
                String remarks = "";
                if (!diffApplicable) {
                    remarks = this.getFullPostRemarks();
                }
                fullPostDetails.put("FP_Forms", (Object)fullPostForms);
                fullPostDetails.put("Remarks", (Object)remarks);
                ((Hashtable<String, String>)meTrackingTracker).put("Full_Post_Details", fullPostDetails.toString());
                ((Hashtable<String, String>)meTrackingTracker).put("Data_Get_Time_Dur", dataGetTimeDur.toString());
                appData.addRecord("inputmetrackingtracker", this.getMETrackingTrackerProps(meTrackingTracker));
            }
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "postTrackingData", "post details : " + fullPostDetails);
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "postTrackingData", "Application Data : " + appData.getUploadData());
            if (proxyProperties == null) {
                SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "postTrackingData", "Proxy Properties is Null");
            }
            else {
                SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "postTrackingData", "Proxy Properties : host - " + ((Hashtable<K, Object>)proxyProperties).get("host") + ", port - " + ((Hashtable<K, Object>)proxyProperties).get("port"));
            }
            final String response = METrack.updateMultiFormdData(appData, proxyProperties);
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "postTrackingData", "response received : " + response);
            final JSONObject status = METrackerDiffUtil.getInstance().getZCUpdateStatus(response);
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "postTrackingData", "ZC response status : " + status);
            if (!status.has("error")) {
                final JSONObject postFailedForms = METrackerDiffUtil.getInstance().updatePostSuccessValues(status, postedDiffApplicableData);
                METrackerDiffUtil.getInstance().updatePostFailedValues(postFailedForms);
            }
            else {
                METrackerDiffUtil.getInstance().updatePostFailedValues(status);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "postTrackingData", "Exception occurred : ", (Throwable)e);
        }
    }
    
    private Properties concatenate2Props(final Properties baseProps, final Properties overProps) {
        baseProps.putAll(overProps);
        return baseProps;
    }
    
    public static Properties getProxyProps() {
        Properties proxyProps = null;
        try {
            final Properties proxyProperties = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final String url = "http://creator.zoho.com";
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyProperties);
                proxyProps = new Properties();
                ((Hashtable<String, Object>)proxyProps).put("host", ((Hashtable<K, Object>)pacProps).get("proxyHost"));
                ((Hashtable<String, Object>)proxyProps).put("port", ((Hashtable<K, Object>)pacProps).get("proxyPort"));
                ((Hashtable<String, Object>)proxyProps).put("username", ((Hashtable<K, Object>)pacProps).get("proxyUser"));
                ((Hashtable<String, Object>)proxyProps).put("password", ((Hashtable<K, Object>)pacProps).get("proxyPass"));
            }
            else if (proxyProperties != null && !proxyProperties.isEmpty()) {
                proxyProps = new Properties();
                ((Hashtable<String, Object>)proxyProps).put("host", ((Hashtable<K, Object>)proxyProperties).get("proxyHost"));
                ((Hashtable<String, Object>)proxyProps).put("port", ((Hashtable<K, Object>)proxyProperties).get("proxyPort"));
                ((Hashtable<String, Object>)proxyProps).put("username", ((Hashtable<K, Object>)proxyProperties).get("proxyUser"));
                ((Hashtable<String, Object>)proxyProps).put("password", ((Hashtable<K, Object>)proxyProperties).get("proxyPass"));
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(METrackerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return proxyProps;
    }
    
    public static boolean getMETrackSettings() {
        boolean isTrackingEnabled = true;
        String setting = SyMUtil.getSyMParameter("ME_TRACK_SETTINGS");
        if (setting == null) {
            SyMUtil.updateSyMParameter("ME_TRACK_SETTINGS", "true");
            setting = "true";
        }
        isTrackingEnabled = Boolean.valueOf(setting);
        return isTrackingEnabled;
    }
    
    public static void enableTracking() {
        try {
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", "==================================");
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", " Enabling ME Tracking on request");
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", "==================================");
            METrack.enable(getProxyProps());
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "MEDCdata", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public static void disableTracking() {
        try {
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", "==================================");
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", " Disabling ME Tracking on request");
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", "==================================");
            METrack.disable(getProxyProps());
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "MEDCdata", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public static void startTracking() {
        try {
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", "======================");
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", " Starting ME Tracking");
            SyMLogger.info(METrackerHandler.logger, METrackerHandler.sourceClass, "stopTracking", "======================");
            final Properties proxyProps = getProxyProps();
            final String baseDir = System.getProperty("server.home");
            final String confDir = baseDir + File.separator + "conf";
            final METrack meTrack = new METrack(confDir, baseDir, "mickeylite", (String)null, (String)null, true, proxyProps, Wield.getInstance().getProductName());
            meTrack.start();
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "MEDCdata", "Exception occurred : ", (Throwable)e);
        }
    }
    
    public static void checkAndUpdateTrackingProps() {
        FileInputStream fin = null;
        try {
            final String baseDir = System.getProperty("server.home");
            final String confDir = baseDir + File.separator + "conf";
            final Properties prop = new Properties();
            final File myFile = new File(confDir + File.separator + "ZohoCreator.properties");
            fin = new FileInputStream(myFile.getCanonicalPath());
            prop.load(fin);
            final String isTrackingEnabled = SyMUtil.getSyMParameter("ME_TRACK_SETTINGS");
            final String propertyValue = ((Hashtable<K, Object>)prop).get("enabled").toString();
            if (isTrackingEnabled != null && !isTrackingEnabled.equalsIgnoreCase(propertyValue)) {
                prop.setProperty("enabled", isTrackingEnabled.toLowerCase());
                prop.store(new FileOutputStream(confDir + File.separator + "ZohoCreator.properties"), null);
                if (isTrackingEnabled.equalsIgnoreCase("false")) {
                    disableTracking();
                }
            }
        }
        catch (final IOException e) {
            SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "checkAndUpdateTrackingProps", "Exception occurred : ", (Throwable)e);
            try {
                if (fin != null) {
                    fin.close();
                }
            }
            catch (final IOException e) {
                SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "checkAndUpdateTrackingProps", "IOException occurred : ", (Throwable)e);
            }
        }
        finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            }
            catch (final IOException e2) {
                SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "checkAndUpdateTrackingProps", "IOException occurred : ", (Throwable)e2);
            }
        }
    }
    
    public Properties getMETrackingTrackerProps(final Properties meTrackingTracker) {
        try {
            final MEDMTracker tracker = (MEDMTracker)Class.forName(METrackerHandler.meTrackingTrackerClassName).newInstance();
            meTrackingTracker.putAll(tracker.getTrackerProperties());
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "getMETrackingTrackerProps", "Exception occurred : ", (Throwable)e);
        }
        return meTrackingTracker;
    }
    
    public String getFullPostRemarks() {
        try {
            final String meTrackDir = METrackerDiffUtil.getInstance().getMETrackDir();
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(meTrackDir + File.separator + "last_successfully_post_data.properties")) {
                return "last_successfully_post_data.properties file not found";
            }
            final Properties properties = FileAccessUtil.readProperties(meTrackDir + File.separator + "metrack_config.properties");
            if (properties.containsKey("LastDownloadStatus")) {
                final int lastDownloadStatus = Integer.valueOf(((Hashtable<K, Object>)properties).get("LastDownloadStatus").toString());
                if ((0 != lastDownloadStatus && 10010 != lastDownloadStatus) || !properties.containsKey("METrackDiffMinBuildNo")) {
                    return "lastDownloadStatus:(" + lastDownloadStatus + ")";
                }
                final Long applyBuildNum = Long.valueOf(((Hashtable<K, Object>)properties).get("METrackDiffMinBuildNo").toString());
                final Integer buildNumFromDB = DCServerBuildHistoryProvider.getInstance().getCurrentBuildNumberFromDB();
                if (buildNumFromDB < applyBuildNum) {
                    return "build not applicaple(" + buildNumFromDB + ")";
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "getFullPostRemarks", "Exception occurred : ", (Throwable)e);
        }
        return "--";
    }
    
    public void meTrackingUpdater() {
        try {
            final String metrackingConfDir = METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metracking.conf";
            final Properties metrackingConf = FileAccessUtil.readProperties(metrackingConfDir);
            if (metrackingConf.containsKey("metracking_updater_classname")) {
                final METrackDownloadHandler updater = (METrackDownloadHandler)Class.forName(((Hashtable<K, Object>)metrackingConf).get("metracking_updater_classname").toString()).newInstance();
                updater.updateMETrackConfiguration();
            }
            else {
                FileAccessUtil.storeProperties(new Properties(), METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metrack_config.properties", false);
                SyMLogger.warning(METrackerHandler.logger, METrackerHandler.sourceClass, "trackingUpdater", "metracking_updater_classname not found in metracking.conf");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(METrackerHandler.logger, METrackerHandler.sourceClass, "getFullPostRemarks", "Exception occurred : ", (Throwable)e);
        }
    }
    
    @Override
    public Properties getInstallationDetails() {
        final MEInstallationDetails meInstallationDetails = new MEInstallationDetails();
        final Properties installationDetails = meInstallationDetails.getInstallationDetails();
        return installationDetails;
    }
    
    static {
        METrackerHandler.logger = Logger.getLogger("METrackLog");
        METrackerHandler.sourceClass = "MEDCHandler";
        METrackerHandler.meTrackingTrackerClassName = null;
    }
}
