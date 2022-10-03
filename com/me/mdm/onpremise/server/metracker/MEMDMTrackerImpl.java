package com.me.mdm.onpremise.server.metracker;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import java.util.Enumeration;
import org.json.simple.JSONObject;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Locale;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.METrackerHandler;
import java.util.logging.Logger;
import java.util.HashMap;
import com.me.tools.zcutil.ZCDataHandler;

public class MEMDMTrackerImpl implements ZCDataHandler
{
    private static HashMap<String, String> trackerHash;
    private static Logger logger;
    
    public MEMDMTrackerImpl() {
        this.addTrackers();
    }
    
    private HashMap addTrackers() {
        (MEMDMTrackerImpl.trackerHash = new HashMap<String, String>()).put("com.adventnet.sym.server.medc.MEDCTrackerCommonImpl", "inputcommon");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerAndroidImpl", "inputmdmpandroid");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerIosImpl", "inputmdmpios");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerWPImpl", "inputmdmpwp");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerProfileImpl", "inputmdmpprofile");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerAppsImpl", "inputmdmpapps");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerAssetsImpl", "inputmdmpasset");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerGroupsImpl", "inputmdmpgroup");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerEnrollmentImpl", "inputmdmpenrollment");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMPTrackerSettingsImpl", "inputmdmpsettings");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerUACImpl", "inputusermgmt");
        MEMDMTrackerImpl.trackerHash.put("com.adventnet.sym.server.medc.MEDCTrackerI18NImpl", "inputi18n");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEDCTrackerMDMEvaluatorImpl", "inputmdmpevaluator");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackParamsImpl", "inputmdmtrackparams");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackRemoteImpl", "inputmdmpremotemgmt");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerOSUpdateMgmtImpl", "inputmdmposupdatemgmt");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackAgentDeploymentImpl", "inputmdmagenttracker");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerBulkActionsImpl", "inputmdmpgroup");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMMigrationTrackerImpl", "inputmdmmigrationtracker");
        MEMDMTrackerImpl.trackerHash.put("com.me.devicemanagement.onpremise.server.metrack.METrackerTrackingImpl", "inputmetrackingtracker");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerChromeImpl", "inputmdmpchrome");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerIntegrationImpl", "inputmdmpintegration");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerComplianceImpl", "inputmdmpcompliance");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerFenceRepositoryImpl", "inputmdmpfencerepository");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEDCTrackerAzureMamImpl", "inputmdmtrackparams");
        MEMDMTrackerImpl.trackerHash.put("com.me.mdm.onpremise.server.metracker.MEMDMTrackerAnnouncementImpl", "inputmdmpprofile");
        return MEMDMTrackerImpl.trackerHash;
    }
    
    public void uploadData() {
        new METrackerHandler().postTrackingData((HashMap)MEMDMTrackerImpl.trackerHash);
    }
    
    public void uploadODData(final long jobid) {
    }
    
    public Properties getInstallationDetails() {
        try {
            final Properties installationDetails = new METrackerHandler().getInstallationDetails();
            setManagedDeviceCountInInstallationProps(installationDetails);
            this.setInstallationTime(installationDetails);
            this.setLanguageDetails(installationDetails);
            this.setStartupFailureDetails(installationDetails);
            this.setProductName(installationDetails);
            MEMDMTrackerImpl.logger.log(Level.SEVERE, "Installation details from log {0} ", installationDetails);
            return installationDetails;
        }
        catch (final Exception e) {
            MEMDMTrackerImpl.logger.log(Level.SEVERE, "getInstallationDetails() Exception occurred : ", e);
            return new Properties();
        }
    }
    
    private void setProductName(final Properties installationDetails) {
        try {
            final String confFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "general_properties.conf";
            final Properties generalProps = StartupUtil.getProperties(confFilePath);
            String productName = generalProps.getProperty("productname");
            productName = productName.replaceAll("\\d", "");
            ((Hashtable<String, String>)installationDetails).put("Product", productName);
        }
        catch (final Exception e) {
            MEMDMTrackerImpl.logger.log(Level.SEVERE, "setProductName() Exception occurred : ", e);
        }
    }
    
    private void setLanguageDetails(final Properties installationDetails) {
        try {
            final String installerSelectedLang = this.getValueFromSystemLogFile("InstallerSelectedLang");
            if (installerSelectedLang != null && !installerSelectedLang.equalsIgnoreCase("")) {
                ((Hashtable<String, String>)installationDetails).put("InstallerSelectedLang", installerSelectedLang);
            }
            final Locale userLocale = DMUserHandler.getUserLocaleFromDB(DBUtil.getUVHValue("AaaUser:user_id:0"));
            ((Hashtable<String, String>)installationDetails).put("AdminUserLang", userLocale.getLanguage());
        }
        catch (final Exception e) {
            MEMDMTrackerImpl.logger.log(Level.SEVERE, "setLanguageDetails() Exception occurred : ", e);
        }
    }
    
    private void setInstallationTime(final Properties installationDetails) {
        try {
            final String installConfPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "install.conf";
            if (new File(installConfPath).exists()) {
                final Properties installProps = FileAccessUtil.readProperties(installConfPath);
                if (installProps.containsKey("it")) {
                    ((Hashtable<String, String>)installationDetails).put("it", String.valueOf(installProps.getProperty("it")));
                }
            }
        }
        catch (final Exception e) {
            MEMDMTrackerImpl.logger.log(Level.SEVERE, "setInstallationTime() Exception occurred : ", e);
        }
    }
    
    public String getValueFromSystemLogFile(final String key) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            final File file = new File(System.getProperty("server.home") + File.separator + "logs" + File.separator + "systemlog.txt");
            if (file.exists()) {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.trim().length() > 0 && line.trim().startsWith(key)) {
                        int index = 0;
                        if (line.indexOf(": ") != -1) {
                            index = line.indexOf(": ") + 1;
                        }
                        else {
                            index = line.indexOf("=") + 1;
                        }
                        return line.substring(index);
                    }
                }
            }
        }
        catch (final Exception e) {
            MEMDMTrackerImpl.logger.log(Level.SEVERE, "getValueFromSystemLogFile() Exception occurred : ", e);
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (final Exception e) {
                MEMDMTrackerImpl.logger.log(Level.SEVERE, "getValueFromSystemLogFile() Exception occurred while closing reader: ", e);
            }
        }
        finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (final Exception e2) {
                MEMDMTrackerImpl.logger.log(Level.SEVERE, "getValueFromSystemLogFile() Exception occurred while closing reader: ", e2);
            }
        }
        return null;
    }
    
    public Properties getStartupDetails() {
        final String confFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "METracking" + File.separator + "startupinfo.conf";
        final JSONObject startupObj = new JSONObject();
        final JSONObject loginObj = new JSONObject();
        final Properties startupProps = StartupUtil.getProperties(confFilePath);
        final Enumeration e = startupProps.propertyNames();
        while (e.hasMoreElements()) {
            final String keyName = e.nextElement();
            if (keyName.startsWith("LA_")) {
                loginObj.put((Object)keyName, (Object)startupProps.getProperty(keyName));
            }
            else {
                startupObj.put((Object)keyName, (Object)startupProps.getProperty(keyName));
            }
        }
        final Properties failureProps = new Properties();
        ((Hashtable<String, String>)failureProps).put("Startup_Details", startupObj.toString());
        ((Hashtable<String, String>)failureProps).put("Login_Details", loginObj.toString());
        return failureProps;
    }
    
    private void setStartupFailureDetails(final Properties installationDetails) {
        try {
            final Properties startUpDetails = this.getStartupDetails();
            if (startUpDetails != null && !startUpDetails.isEmpty()) {
                installationDetails.putAll(startUpDetails);
            }
        }
        catch (final Exception e) {
            MEMDMTrackerImpl.logger.log(Level.SEVERE, "setStartupFailureDetails() Exception occurred : ", e);
        }
    }
    
    public static void setManagedDeviceCountInInstallationProps(final Properties installationDetails) {
        try {
            final String latestEnrollmentTime = SyMUtil.getSyMParameter("LATESTENROLLTIME");
            if (latestEnrollmentTime != null) {
                installationDetails.setProperty("MDM_Latest_Device_Enrollment_Time", latestEnrollmentTime);
            }
            final String firstDeviceEnrollmetTime = SyMUtil.getSyMParameter("FIRSTENROLLTIME");
            if (firstDeviceEnrollmetTime != null) {
                installationDetails.setProperty("MDM_First_Device_Enrollment_Time", firstDeviceEnrollmetTime);
            }
            final String totalManagedDeviceHistory = SyMUtil.getSyMParameter("MDM_DEVICE_HISTORY_COUNT");
            if (totalManagedDeviceHistory != null) {
                installationDetails.setProperty("MDM_Device_Enrolled_History_count", totalManagedDeviceHistory);
            }
            try {
                final int totalLiveDevices = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
                installationDetails.setProperty("MDM_Device_Enrolled_Live_count", String.valueOf(totalLiveDevices));
            }
            catch (final Exception ex) {
                MEMDMTrackerImpl.logger.log(Level.SEVERE, "Exception while getManagedDeviceCount ", ex);
            }
        }
        catch (final Exception ex2) {
            MEMDMTrackerImpl.logger.log(Level.SEVERE, "Exception in setManagedDeviceCountInInstallationProps", ex2);
        }
    }
    
    static {
        MEMDMTrackerImpl.trackerHash = null;
        MEMDMTrackerImpl.logger = Logger.getLogger("METrackLog");
    }
}
