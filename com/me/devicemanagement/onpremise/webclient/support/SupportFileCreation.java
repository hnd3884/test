package com.me.devicemanagement.onpremise.webclient.support;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.NativeException;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.fos.FOS;
import com.me.ems.framework.common.api.utils.AdminCommonUtil;
import org.json.JSONArray;
import java.net.InetAddress;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import com.me.devicemanagement.onpremise.server.util.FileFilterUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.io.File;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SupportFileCreation
{
    private static SupportFileCreation supportFileCreation;
    private static String sourceClass;
    protected Logger out;
    private String supportFileStatus;
    private String failoverSupport;
    private String userMESSAGE;
    private String emailID;
    private static String fs;
    private long supportFileCreationTime;
    private int wanAgentLogInitiatedCount;
    private int wanAgentLogUplodedCount;
    private int mdmAgentLogInitiatedCount;
    private int mdmAgentLogUplodedCount;
    private ArrayList arrAgentLogUploadCompList;
    private HashMap arrMDMAgentLogUploadList;
    private int wan_Agent_Log_Upload_wait_Time;
    public String fileExtention;
    private long maxSizeForBonitas;
    private ArrayList notUploadedDeviceLogs;
    private ArrayList allDeviceLogs;
    private int dsLogInitiatedCount;
    private int dsLogUploadedCount;
    private ArrayList arrDSLogUploadCompList;
    private static final String PRODUCT_DISP_NAME = "displayname";
    
    private SupportFileCreation() {
        this.out = Logger.getLogger("UserManagementLogger");
        this.supportFileStatus = "Not Started";
        this.failoverSupport = "";
        this.userMESSAGE = "--";
        this.emailID = "--";
        this.supportFileCreationTime = 0L;
        this.wanAgentLogInitiatedCount = 0;
        this.wanAgentLogUplodedCount = 0;
        this.mdmAgentLogInitiatedCount = 0;
        this.mdmAgentLogUplodedCount = 0;
        this.arrAgentLogUploadCompList = null;
        this.arrMDMAgentLogUploadList = null;
        this.wan_Agent_Log_Upload_wait_Time = 10;
        this.fileExtention = "7z";
        this.maxSizeForBonitas = 5368709120L;
        this.notUploadedDeviceLogs = null;
        this.allDeviceLogs = null;
        this.dsLogInitiatedCount = 0;
        this.dsLogUploadedCount = 0;
        this.arrDSLogUploadCompList = null;
        this.setNotUploadedDeviceLogs(new ArrayList());
        this.setAllDeviceLogs(new ArrayList());
    }
    
    public static synchronized SupportFileCreation getInstance() {
        if (SupportFileCreation.supportFileCreation == null) {
            SupportFileCreation.supportFileCreation = new SupportFileCreation();
        }
        return SupportFileCreation.supportFileCreation;
    }
    
    public synchronized void incrementAgentLogUploadCount() {
        this.setWanAgentLogUplodedCount(this.getWanAgentLogUplodedCount() + 1);
    }
    
    public synchronized void addComputerToTheList(final String sDomainName) {
        if (this.getArrAgentLogUploadCompList() == null) {
            this.setArrAgentLogUploadCompList(new ArrayList());
        }
        this.getArrAgentLogUploadCompList().add(sDomainName.toLowerCase());
    }
    
    public synchronized void removeComputerFromList(final String sDomainName) {
        if (this.getArrAgentLogUploadCompList() != null) {
            this.getArrAgentLogUploadCompList().remove(sDomainName.toLowerCase());
        }
    }
    
    public String getComputerListAsString() {
        if (this.getArrAgentLogUploadCompList() == null || this.getArrAgentLogUploadCompList().size() == 0) {
            return null;
        }
        StringBuilder sbComputerList = new StringBuilder("");
        final Iterator computerList = this.getArrAgentLogUploadCompList().iterator();
        while (computerList.hasNext()) {
            if (sbComputerList.equals(new StringBuilder(""))) {
                sbComputerList = sbComputerList.append(computerList.next());
            }
            else {
                sbComputerList = sbComputerList.append(computerList.next()).append(",");
            }
        }
        String sComputerList = sbComputerList.toString();
        sComputerList = sComputerList.substring(0, sComputerList.lastIndexOf(","));
        return sComputerList;
    }
    
    public synchronized void incrementMDMLogUploadCount() {
        this.setMdmAgentLogUplodedCount(this.getMdmAgentLogUplodedCount() + 1);
    }
    
    public synchronized void addDeviceToTheList(final JSONObject deviceDetails) {
        if (this.getArrMDMAgentLogUploadList() == null) {
            this.setArrMDMAgentLogUploadList(new HashMap());
        }
        try {
            this.getArrMDMAgentLogUploadList().put(deviceDetails.getLong("dataId"), deviceDetails);
        }
        catch (final Exception exp) {
            this.out.log(Level.SEVERE, "Exception in addDeviceToTheList..", exp);
        }
    }
    
    public synchronized void removeDeviceFromList(final JSONObject deviceDetails) {
        if (this.getArrMDMAgentLogUploadList() != null) {
            try {
                this.getArrMDMAgentLogUploadList().remove(deviceDetails.getLong("dataId"));
            }
            catch (final Exception exp) {
                this.out.log(Level.SEVERE, "Exception in removeDeviceList..", exp);
            }
        }
    }
    
    public String getDeviceListAsString() {
        if (this.getArrMDMAgentLogUploadList() == null || this.getArrMDMAgentLogUploadList().isEmpty()) {
            return null;
        }
        StringBuilder sbDeviceList = new StringBuilder("");
        final Iterator deviceList = this.getArrMDMAgentLogUploadList().entrySet().iterator();
        try {
            for (final Object currentKey : this.getArrMDMAgentLogUploadList().keySet()) {
                final JSONObject deviceDetail = this.getArrMDMAgentLogUploadList().get(currentKey);
                if (sbDeviceList.equals(new StringBuilder(""))) {
                    sbDeviceList = sbDeviceList.append((String)deviceDetail.get("dataValue"));
                }
                else {
                    sbDeviceList = sbDeviceList.append((String)deviceDetail.get("dataValue")).append(",");
                }
            }
        }
        catch (final Exception exp) {
            this.out.log(Level.SEVERE, "Exception in getting device name list..", exp);
        }
        String sDeviceList = sbDeviceList.toString();
        sDeviceList = sDeviceList.substring(0, sDeviceList.lastIndexOf(","));
        return sDeviceList;
    }
    
    public String getExtension() {
        return this.fileExtention;
    }
    
    public String getFailoverStatus() {
        return this.failoverSupport;
    }
    
    public Properties getSFCstatus(final String logonEmailID) {
        final Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("message", this.supportFileStatus);
        String inProgress = "true";
        String status = "failed";
        if (this.supportFileStatus.equals("Creating support file failed")) {
            inProgress = "false";
        }
        else if (this.supportFileStatus.contains("upload")) {
            inProgress = "false";
            if (this.supportFileStatus.equals("upload success")) {
                status = "success";
            }
        }
        if (!Boolean.parseBoolean(inProgress)) {
            Properties props = null;
            try {
                props = ApiFactoryProvider.getSupportAPI().getSupportUploadState();
            }
            catch (final Exception ex) {
                this.out.log(Level.SEVERE, "Exception in uploading the support file..", ex);
            }
            if (props != null && props.get("FailedResList") != null) {
                ((Hashtable<String, Object>)prop).put("failedResList", ((Hashtable<K, Object>)props).get("FailedResList"));
            }
        }
        if (this.emailID.equals("")) {
            this.emailID = logonEmailID;
        }
        ((Hashtable<String, String>)prop).put("inProgress", inProgress);
        ((Hashtable<String, String>)prop).put("status", status);
        ((Hashtable<String, String>)prop).put("emailID", this.emailID);
        ((Hashtable<String, String>)prop).put("userMESSAGE", this.userMESSAGE);
        ((Hashtable<String, String>)prop).put("failover-upload", this.failoverSupport);
        return prop;
    }
    
    public void resetSFCstatus() {
        final String timeOutStr = SyMUtil.getProductProperty("timeoutforcleaningoldlogs");
        final long timeOut = Long.parseLong(timeOutStr);
        final long timeDurration = timeOut * 60L * 1000L + this.supportFileCreationTime;
        final long currentTime = System.currentTimeMillis();
        if (currentTime > timeDurration) {
            this.resetSFCProcess();
        }
    }
    
    public void resetSFCProcess() {
        this.supportFileStatus = "Not Started";
        this.supportFileCreationTime = 0L;
        this.emailID = "";
        this.userMESSAGE = "";
        this.resetWanAgentCount();
    }
    
    public void resetWanAgentCount() {
        this.setWanAgentLogInitiatedCount(0);
        this.setWanAgentLogUplodedCount(0);
        this.setArrAgentLogUploadCompList(null);
        this.setNotUploadedDeviceLogs(new ArrayList());
        this.setAllDeviceLogs(new ArrayList());
        this.setDsLogInitiatedCount(0);
        this.setDsLogUploadedCount(0);
        this.setArrDSLogUploadCompList(null);
    }
    
    public void cleanSupportFolder(final boolean clear) {
        try {
            this.out.log(Level.INFO, "Clean support folder method is invoked.");
            final String serverDir = System.getProperty("server.home");
            final String desPath = serverDir + SupportFileCreation.fs + "logs" + SupportFileCreation.fs + "support";
            File dir = new File(desPath);
            if (!dir.exists()) {
                this.out.log(Level.INFO, "No support folder found!!");
            }
            else {
                final boolean deletionsuccess = this.deleteDir(dir, clear);
                if (deletionsuccess) {
                    if (clear) {
                        this.out.log(Level.INFO, "Support folder files other than desktopcentral zip are cleaned !!");
                    }
                    else {
                        this.out.log(Level.INFO, "Support folder is deleted !!");
                    }
                }
                else {
                    this.out.log(Level.INFO, "Support folder deletion failed !!");
                }
            }
            if (!clear) {
                final String agentLogDesPath = serverDir + SupportFileCreation.fs + "agent-logs";
                dir = new File(agentLogDesPath);
                if (!dir.exists()) {
                    this.out.log(Level.INFO, "No support folder found!!");
                }
                else {
                    final boolean deletionsuccess2 = this.deleteDir(dir, clear);
                    if (deletionsuccess2) {
                        if (clear) {
                            this.out.log(Level.INFO, "Support folder files other than desktopcentral zip are cleaned !!");
                        }
                        else {
                            this.out.log(Level.INFO, "Support folder is deleted !!");
                        }
                    }
                    else {
                        this.out.log(Level.INFO, "Support folder deletion failed !!");
                    }
                }
            }
            if (clear) {
                final String serverHome = System.getProperty("server.home");
                final String mdmAgentLogs = serverHome + SupportFileCreation.fs + "mdm-logs";
                final File agentDir = new File(mdmAgentLogs);
                if (!agentDir.exists()) {
                    this.out.log(Level.INFO, "No logs folder found!!");
                }
                else {
                    final boolean deletionsuccess3 = this.deleteDir(agentDir, !clear);
                    if (deletionsuccess3) {
                        this.out.log(Level.INFO, "MDM Agent files and folder deletion success!!");
                    }
                    else {
                        this.out.log(Level.INFO, "MDM Agent files and folder deletion failed !!");
                    }
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Error while deleting support file" + e.toString(), e);
        }
    }
    
    public void cleanSupportLogFolder(final boolean clear) {
        boolean deletionsuccess = false;
        final String serverDir = System.getProperty("server.home");
        final String desPath = serverDir + SupportFileCreation.fs + "logs" + SupportFileCreation.fs + "supportLogs";
        final File dir = new File(desPath);
        if (!dir.exists()) {
            this.out.log(Level.INFO, "No supportLogs folder found to delete!!");
        }
        else {
            deletionsuccess = this.deleteDir(dir, clear);
            if (deletionsuccess) {
                this.out.log(Level.INFO, "Support Log folder deleted!!");
            }
            else {
                this.out.log(Level.INFO, "SupportLog folder Not deleted!!");
            }
        }
    }
    
    public String getSupportFileName() {
        String prodDisplayName = ProductUrlLoader.getInstance().getGeneralProperites().getProperty("displayname");
        prodDisplayName = prodDisplayName.replace(" ", "");
        final String uniqueFileName = prodDisplayName + "_Logs";
        return uniqueFileName;
    }
    
    public boolean deleteDir(final File dir, final boolean clear) {
        if (dir.isDirectory()) {
            final String[] dirobject = dir.list();
            for (int i = 0; i < dirobject.length; ++i) {
                final boolean success = this.deleteDir(new File(dir, dirobject[i]), clear);
                if (!success) {
                    this.out.log(Level.WARNING, "This support folder file is not deleted : " + dirobject[i]);
                    return false;
                }
            }
        }
        boolean delAction = true;
        final String fileName = dir.getName();
        if (fileName.contains(".err") && clear) {
            delAction = dir.delete();
            return delAction;
        }
        if (clear) {
            final boolean isAllowed = FileFilterUtil.isAllowed(dir.getName(), 100);
            if (!isAllowed) {
                delAction = true;
            }
            else if (!dir.getName().equals("support")) {
                delAction = dir.delete();
            }
        }
        else {
            delAction = dir.delete();
        }
        return delAction;
    }
    
    public ArrayList getNotUploadedDeviceLogs() {
        return this.notUploadedDeviceLogs;
    }
    
    public void setNotUploadedDeviceLogs(final ArrayList notUploadedDeviceLogs) {
        this.notUploadedDeviceLogs = notUploadedDeviceLogs;
    }
    
    public ArrayList getAllDeviceLogs() {
        return this.allDeviceLogs;
    }
    
    public void setAllDeviceLogs(final ArrayList allDeviceLogs) {
        this.allDeviceLogs = allDeviceLogs;
    }
    
    public int getWanAgentLogInitiatedCount() {
        return this.wanAgentLogInitiatedCount;
    }
    
    public void setWanAgentLogInitiatedCount(final int wanAgentLogInitiatedCount) {
        this.wanAgentLogInitiatedCount = wanAgentLogInitiatedCount;
    }
    
    public int getMdmAgentLogInitiatedCount() {
        return this.mdmAgentLogInitiatedCount;
    }
    
    public void setMdmAgentLogInitiatedCount(final int mdmAgentLogInitiatedCount) {
        this.mdmAgentLogInitiatedCount = mdmAgentLogInitiatedCount;
    }
    
    public int getWanAgentLogUplodedCount() {
        return this.wanAgentLogUplodedCount;
    }
    
    public void setWanAgentLogUplodedCount(final int wanAgentLogUplodedCount) {
        this.wanAgentLogUplodedCount = wanAgentLogUplodedCount;
    }
    
    public int getMdmAgentLogUplodedCount() {
        return this.mdmAgentLogUplodedCount;
    }
    
    public void setMdmAgentLogUplodedCount(final int mdmAgentLogUplodedCount) {
        this.mdmAgentLogUplodedCount = mdmAgentLogUplodedCount;
    }
    
    public ArrayList getArrAgentLogUploadCompList() {
        return this.arrAgentLogUploadCompList;
    }
    
    public void setArrAgentLogUploadCompList(final ArrayList arrAgentLogUploadCompList) {
        this.arrAgentLogUploadCompList = arrAgentLogUploadCompList;
    }
    
    public HashMap getArrMDMAgentLogUploadList() {
        return this.arrMDMAgentLogUploadList;
    }
    
    public void setArrMDMAgentLogUploadList(final HashMap arrMDMAgentLogUploadList) {
        this.arrMDMAgentLogUploadList = arrMDMAgentLogUploadList;
    }
    
    public int getWan_Agent_Log_Upload_wait_Time() {
        return this.wan_Agent_Log_Upload_wait_Time;
    }
    
    public void setWan_Agent_Log_Upload_wait_Time(final int wan_Agent_Log_Upload_wait_Time) {
        this.wan_Agent_Log_Upload_wait_Time = wan_Agent_Log_Upload_wait_Time;
    }
    
    public int getDsLogInitiatedCount() {
        return this.dsLogInitiatedCount;
    }
    
    public void setDsLogInitiatedCount(final int dsLogInitiatedCount) {
        this.dsLogInitiatedCount = dsLogInitiatedCount;
    }
    
    public int getDsLogUploadedCount() {
        return this.dsLogUploadedCount;
    }
    
    public void setDsLogUploadedCount(final int dsLogUploadedCount) {
        this.dsLogUploadedCount = dsLogUploadedCount;
    }
    
    public ArrayList getArrDSLogUploadCompList() {
        return this.arrDSLogUploadCompList;
    }
    
    public void setArrDSLogUploadCompList(final ArrayList arrDSLogUploadCompList) {
        this.arrDSLogUploadCompList = arrDSLogUploadCompList;
    }
    
    public synchronized void incrementDSLogUploadCount() {
        this.setDsLogUploadedCount(this.getDsLogUploadedCount() + 1);
    }
    
    public synchronized void addDSToList(final Long branchId) {
        if (this.getArrDSLogUploadCompList() == null) {
            this.setArrDSLogUploadCompList(new ArrayList());
        }
        this.getArrDSLogUploadCompList().add(branchId);
    }
    
    public synchronized void removeDSFromList(final Long branchId) {
        if (this.getArrDSLogUploadCompList() != null) {
            this.getArrDSLogUploadCompList().remove(branchId);
        }
    }
    
    public List getDeviceListAsList() {
        try {
            final String deviceListAsString = this.getDeviceListAsString();
            if (deviceListAsString != null && !deviceListAsString.isEmpty()) {
                final String[] split = deviceListAsString.split(",");
                return Arrays.asList(split);
            }
        }
        catch (final Exception ex) {
            this.out.log(Level.SEVERE, "Exception while getDeviceListAsList", ex);
        }
        return new ArrayList();
    }
    
    public List getComputerListAsList() {
        try {
            final String computerListAsString = this.getComputerListAsString();
            if (computerListAsString != null && !computerListAsString.isEmpty()) {
                final String[] split = computerListAsString.split(",");
                return Arrays.asList(split);
            }
        }
        catch (final Exception ex) {
            this.out.log(Level.SEVERE, "Exception while getComputerListAsList", ex);
        }
        return new ArrayList();
    }
    
    private Map uploadSupportFile(final Map supportFileDetails) {
        final Map responseStatus = new HashMap();
        this.out.log(Level.INFO, "SUPPORT FILE UPLOADING - STARTS ---");
        this.supportFileStatus = "Uploading support file";
        final String fromAddress = supportFileDetails.get("emailID");
        final String userMESSAGE = supportFileDetails.get("userMessage");
        String supportFile = supportFileDetails.get("supportFile");
        responseStatus.put("supportFile", supportFile);
        try {
            final boolean accessAvailable = UploadAction.checkUploadAccess();
            this.out.log(Level.WARNING, "Connection got while uploading:" + accessAvailable);
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Error while obtaining Connection", e);
        }
        try {
            supportFile = ".." + File.separator + "logs" + File.separator + "supportLogs" + File.separator + supportFile;
            final boolean uploaded = UploadAction.uploadSupportFile(supportFileDetails.get("userAgent"), supportFile, fromAddress, userMESSAGE);
            this.out.log(Level.WARNING, "Uploaded:" + uploaded);
            responseStatus.put("isFileUploaded", uploaded);
            if (uploaded) {
                this.supportFileStatus = "upload success";
            }
            else {
                this.supportFileStatus = "upload failed";
            }
            this.out.log(Level.INFO, "SUPPORT FILE UPLOADING - ENDS ---");
        }
        catch (final Exception e) {
            this.out.log(Level.SEVERE, "Error while obtaining Connection" + e.toString(), e);
        }
        return responseStatus;
    }
    
    public Map getSFCstatusMap(final String logonEmailID) {
        final Map fileCreationStatus = new HashMap();
        fileCreationStatus.put("message", this.supportFileStatus);
        String inProgress = "true";
        String status = "false";
        if (this.supportFileStatus.equals("Creating support file failed")) {
            inProgress = "false";
        }
        else if (this.supportFileStatus.contains("upload")) {
            inProgress = "false";
            if (this.supportFileStatus.equals("upload success")) {
                status = "true";
            }
        }
        if (!Boolean.parseBoolean(inProgress)) {
            Properties props = null;
            try {
                props = ApiFactoryProvider.getSupportAPI().getSupportUploadState();
            }
            catch (final Exception ex) {
                this.out.log(Level.SEVERE, "Exception in uploading the support file..", ex);
            }
            if (props != null && props.get("FailedResList") != null) {
                fileCreationStatus.put("failedResList", ((Hashtable<K, Object>)props).get("FailedResList"));
            }
        }
        if (this.emailID != null && this.emailID.isEmpty()) {
            this.emailID = logonEmailID;
        }
        fileCreationStatus.put("inProgress", Boolean.parseBoolean(inProgress));
        fileCreationStatus.put("success", Boolean.parseBoolean(status));
        fileCreationStatus.put("emailID", this.emailID);
        fileCreationStatus.put("userMessage", this.userMESSAGE);
        fileCreationStatus.put("failoverUpload", this.failoverSupport);
        return fileCreationStatus;
    }
    
    public Map supportFileCreationProcess(final Map supportFileDetails) {
        Map responseDetails = new HashMap();
        this.resetWanAgentCount();
        if (this.supportFileStatus.equals("Not Started")) {
            this.supportFileStatus = "Process started";
            this.supportFileCreationTime = System.currentTimeMillis();
            this.emailID = supportFileDetails.get("emailID");
            this.userMESSAGE = supportFileDetails.get("userMessage");
            this.out.log(Level.INFO, "-------Support File Creation Process, User message---------" + this.userMESSAGE);
            responseDetails = this.createSupportFile(supportFileDetails);
            supportFileDetails.put("supportFile", responseDetails.get("supportFile"));
            this.fileExtention = responseDetails.get("extension");
            if (!this.supportFileStatus.equals("Creating support file failed") && !this.supportFileStatus.equals("FILESIZE HIGH during upload")) {
                try {
                    final String owner = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                    DCEventLogUtil.getInstance().addEvent(121, (String)supportFileDetails.get("userName"), (HashMap)null, "dc.support.event.log.sendsupport", (Object)owner, false, CustomerInfoUtil.getInstance().getCustomerId());
                    responseDetails.putAll(this.uploadSupportFile(supportFileDetails));
                }
                catch (final Exception ex) {
                    this.out.log(Level.SEVERE, "Exception in uploading the support file..", ex);
                }
            }
            if (!this.supportFileStatus.equalsIgnoreCase("upload failed")) {
                this.out.log(Level.INFO, "Deleting support folder, since support file creation process completed ..");
                this.cleanSupportFolder(false);
                this.cleanSupportLogFolder(false);
            }
        }
        return responseDetails;
    }
    
    private Map createSupportFile(final Map supportFileDetails) {
        final Map responseDetails = new HashMap();
        String extension = "";
        this.out.log(Level.INFO, "SUPPORT FILE CREATION - STARTS");
        try {
            this.supportFileStatus = "Creating support file";
            Compress.cancelOperation();
            this.out.log(Level.INFO, "Deleting previously created support file ..");
            this.cleanSupportFolder(false);
            this.cleanSupportLogFolder(false);
            final String fileName = "eventlog1.txt";
            String serverName = InetAddress.getLocalHost().getHostName();
            serverName = serverName.toUpperCase();
            final String folderPath = "\\\\logs\\\\" + fileName;
            final String serverDir = System.getProperty("server.home");
            final String destinationPath = serverDir + folderPath;
            this.out.log(Level.INFO, "Creating event log file..\nMachine (hosting server) :" + serverName + "  Destination path :" + destinationPath);
            ApiFactoryProvider.getSupportAPI().logComputerEvents(serverName, "Application", destinationPath);
            final Properties dmProps = ApiFactoryProvider.getSupportTabAPI().uploadAgentLogs(supportFileDetails);
            if (supportFileDetails.containsKey("uploadLogIDs") && supportFileDetails.get("uploadLogIDs") != null) {
                ((Hashtable<String, Object>)dmProps).put("uploadLogIDs", supportFileDetails.get("uploadLogIDs"));
            }
            else {
                ((Hashtable<String, String>)dmProps).put("uploadLogIDs", "");
            }
            final String isMDMUploadStr = supportFileDetails.get("mdmLogUpload");
            boolean isMDMLogUpload = false;
            ((Hashtable<String, Boolean>)dmProps).put("hasMDMLogs", isMDMLogUpload);
            if (isMDMUploadStr != null && isMDMUploadStr.equalsIgnoreCase("true")) {
                isMDMLogUpload = true;
            }
            boolean isMDMLogAvailable = false;
            if (isMDMLogUpload) {
                final JSONObject supportFileForMDM = new JSONObject(supportFileDetails);
                isMDMLogAvailable = ApiFactoryProvider.getMDMSupportAPI().uploadAgentLogs(supportFileForMDM, this.getWan_Agent_Log_Upload_wait_Time());
                final List failedList = AdminCommonUtil.toList((JSONArray)supportFileForMDM.get("failedDeviceList"));
                responseDetails.put("failedDeviceList", failedList);
                supportFileDetails.put("failedDeviceList", failedList);
                ((Hashtable<String, Boolean>)dmProps).put("hasMDMLogs", isMDMLogAvailable);
            }
            final String isServerLogUploadStr = supportFileDetails.get("serverLogUpload");
            boolean isServerLogUpload = false;
            if (isServerLogUploadStr != null && isServerLogUploadStr.equalsIgnoreCase("true")) {
                isServerLogUpload = true;
            }
            ((Hashtable<String, Boolean>)dmProps).put("hasServerLogs", isServerLogUpload);
            final String isDbLockFileUploadStr = supportFileDetails.get("dbLockFileUpload");
            boolean isDbLockFileUpload = false;
            if (isDbLockFileUploadStr != null && isDbLockFileUploadStr.equalsIgnoreCase("true")) {
                isDbLockFileUpload = true;
            }
            ((Hashtable<String, Boolean>)dmProps).put("hasDBLockLogs", isDbLockFileUpload);
            final String uniqueFileName = this.getSupportFileName();
            final String supportfile = Compress.createSupportFile(uniqueFileName, dmProps);
            final File logFile = new File(".." + File.separator + "logs" + SupportFileCreation.fs + "supportLogs" + SupportFileCreation.fs + supportfile);
            if (supportfile.contains(".7z")) {
                extension = "7z";
            }
            else if (supportfile.contains(".zip")) {
                extension = "zip";
            }
            if (logFile.length() > this.maxSizeForBonitas) {
                this.supportFileStatus = "FILESIZE HIGH during upload";
                this.out.log(Level.INFO, "Log size is high. So upload cannot be done");
            }
            else {
                try {
                    if (isServerLogUpload && FOS.isEnabled()) {
                        final FOS fos = new FOS();
                        fos.initialize();
                        final String otherIP = fos.getOtherNode();
                        if (otherIP == null) {
                            this.failoverSupport = I18N.getMsg("dc.admin.fos.support.ip_not_found", new Object[0]);
                        }
                        else {
                            final String dest = "logs" + SupportFileCreation.fs + "support" + SupportFileCreation.fs + otherIP + ".7z";
                            final String source = "\\\\" + otherIP + "\\" + SyMUtil.getInstallationDirName() + "\\" + "logs";
                            if (new File(source).exists()) {
                                final String FailOversupportFile = Compress.createFailoverSupportFile(source, dest, otherIP);
                                final File failoverSupportFile = new File(".." + File.separator + "logs" + File.separator + "support" + File.separator + FailOversupportFile);
                                if (logFile.length() + failoverSupportFile.length() > this.maxSizeForBonitas) {
                                    this.failoverSupport = I18N.getMsg("dc.admin.fos.support.size_exceeds", new Object[] { otherIP });
                                }
                                else {
                                    Compress.createFailoverSupportFile(dest, "logs" + SupportFileCreation.fs + "supportLogs" + SupportFileCreation.fs + supportfile, uniqueFileName);
                                    this.failoverSupport = "";
                                }
                            }
                            else {
                                this.failoverSupport = I18N.getMsg("dc.admin.fos.support.ip_not_reachable", new Object[] { otherIP });
                            }
                        }
                    }
                }
                catch (final Exception ex) {
                    this.failoverSupport = "";
                }
            }
            responseDetails.put("supportFile", supportfile);
            this.out.log(Level.INFO, "Deleting support folder files other than desktopcentral zip ..");
            this.cleanSupportFolder(true);
            this.out.log(Level.INFO, "SUPPORT FILE CREATION - ENDS ---");
        }
        catch (final NativeException e) {
            this.out.log(Level.WARNING, "NativeException while creating zip", (Throwable)e);
        }
        catch (final Exception e2) {
            this.out.log(Level.WARNING, "Error while creating zip", e2);
            this.supportFileStatus = "Creating support file failed";
        }
        responseDetails.put("extension", extension);
        return responseDetails;
    }
    
    static {
        SupportFileCreation.supportFileCreation = null;
        SupportFileCreation.sourceClass = SupportFileCreation.class.getName();
        SupportFileCreation.fs = File.separator;
    }
}
