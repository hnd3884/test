package com.me.mdm.server.support;

import java.util.Hashtable;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import com.me.devicemanagement.framework.server.exception.NativeException;
import java.io.File;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Properties;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.devicemanagement.framework.groupevent.GroupEventNotifier;
import java.util.Iterator;
import org.json.JSONArray;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class SupportFileCreation
{
    protected Logger out;
    public String fileExtention;
    protected long maxSizeForBonitas;
    private String logUploadCacheConstant;
    private static String fs;
    protected String failoverSupport;
    protected long supportFileCreationTime;
    public static final String MDMAGENTLOGINITIATEDCOUNT = "MDMAGENTLOGINITIATEDCOUNT";
    public static final String MDMAGENTLOGUPLOADEDCOUNT = "MDMAGENTLOGUPLOADEDCOUNT";
    public static final String MDMAGENTLOGUPLOADLIST = "MDMAGENTLOGUPLOADLIST";
    public static final String AGENTLOGSTATUS = "AGENTLOGSTATUS";
    public static final String AGENTLOGSTATUSEMAIL = "AGENTLOGSTATUSEMAIL";
    public static final String MDMAGENTLOGUPLOADSUCCESSLIST = "MDMAGENTLOGUPLOADSUCCESSLIST";
    
    public SupportFileCreation() {
        this.out = Logger.getLogger(SupportFileCreation.class.getCanonicalName());
        this.fileExtention = "7z";
        this.maxSizeForBonitas = 5368709120L;
        this.logUploadCacheConstant = "LOG_UPLOAD_CACHE";
        this.failoverSupport = "";
        this.supportFileCreationTime = 0L;
    }
    
    public static SupportFileCreation getInstance() {
        return new SupportFileCreation();
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
        final String supportFileStatus = "Not Started";
        ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)supportFileStatus, 2);
        ApiFactoryProvider.getCacheAccessAPI().removeCache("MDMAGENTLOGUPLOADLIST", 2);
        ApiFactoryProvider.getCacheAccessAPI().removeCache("MDMAGENTLOGUPLOADSUCCESSLIST", 2);
        this.supportFileCreationTime = 0L;
    }
    
    public int getMdmAgentLogInitiatedCount() {
        return (int)ApiFactoryProvider.getCacheAccessAPI().getCache("MDMAGENTLOGINITIATEDCOUNT", 2);
    }
    
    public void setMdmAgentLogInitiatedCount(final int mdmAgentLogInitiatedCount) {
        ApiFactoryProvider.getCacheAccessAPI().putCache("MDMAGENTLOGINITIATEDCOUNT", (Object)mdmAgentLogInitiatedCount, 2);
    }
    
    public int getMdmAgentLogUplodedCount() {
        return (int)ApiFactoryProvider.getCacheAccessAPI().getCache("MDMAGENTLOGUPLOADEDCOUNT", 2);
    }
    
    public void setMdmAgentLogUplodedCount(final int mdmAgentLogUploadedCount) {
        ApiFactoryProvider.getCacheAccessAPI().putCache("MDMAGENTLOGUPLOADEDCOUNT", (Object)mdmAgentLogUploadedCount, 2);
    }
    
    public void setArrMDMAgentLogUploadList(final HashMap arrMDMAgentLogUploadList) {
        if (arrMDMAgentLogUploadList != null) {
            ApiFactoryProvider.getCacheAccessAPI().putCache("MDMAGENTLOGUPLOADLIST", (Object)arrMDMAgentLogUploadList, 2);
        }
    }
    
    public synchronized void addDeviceToTheList(final JSONObject deviceDetails) {
        if (this.getArrMDMAgentLogUploadList() == null) {
            this.setArrMDMAgentLogUploadList(new HashMap());
        }
        try {
            final HashMap arrMDMAgentLogUploadList = this.getArrMDMAgentLogUploadList();
            arrMDMAgentLogUploadList.put(deviceDetails.getLong("device_id"), deviceDetails.toString());
            this.setArrMDMAgentLogUploadList(arrMDMAgentLogUploadList);
        }
        catch (final Exception var3) {
            this.out.log(Level.SEVERE, "Exception in addDeviceToTheList..", var3);
        }
    }
    
    public JSONArray getDeviceNameList() {
        final JSONArray deviceList = new JSONArray();
        HashMap deviceDetails = null;
        deviceDetails = this.getArrMDMAgentLogUploadList();
        if (deviceDetails != null && !deviceDetails.isEmpty()) {
            try {
                for (final Object currentKey : deviceDetails.keySet()) {
                    final JSONObject deviceDetail = new JSONObject((String)this.getArrMDMAgentLogUploadList().get(currentKey));
                    deviceList.put(deviceDetail.get("device_name"));
                }
            }
            catch (final Exception e) {
                this.out.log(Level.SEVERE, "Exception in getting device name list..", e);
            }
        }
        return deviceList;
    }
    
    public HashMap getArrMDMAgentLogUploadList() {
        final HashMap arrMDMAgentLogUploadList = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("MDMAGENTLOGUPLOADLIST", 2);
        return arrMDMAgentLogUploadList;
    }
    
    public JSONObject getSFCstatus() throws Exception {
        final JSONObject jsonObject = new JSONObject();
        String supportFileStatus = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("AGENTLOGSTATUS", 2);
        if (supportFileStatus == null) {
            supportFileStatus = "Not Started";
        }
        jsonObject.put("message", (Object)supportFileStatus);
        String inProgress = "true";
        String status = "failed";
        final String groupEventNotifierStatus = GroupEventNotifier.getGroupEventNotifierStatus("MDM_AGENT_LOG_UPLOAD");
        if (supportFileStatus.equals("Creating support file failed") || (groupEventNotifierStatus != null && groupEventNotifierStatus.equals("failed"))) {
            inProgress = "false";
            GroupEventNotifier.removeGroupEventNotifierStatus("MDM_AGENT_LOG_UPLOAD");
            GroupEventNotifier.removeEventListenerForAction("MDM_AGENT_LOG_UPLOAD");
        }
        else if (supportFileStatus.contains("upload")) {
            inProgress = "false";
            if (supportFileStatus.equals("upload success")) {
                status = "success";
            }
            GroupEventNotifier.removeGroupEventNotifierStatus("MDM_AGENT_LOG_UPLOAD");
            GroupEventNotifier.removeEventListenerForAction("MDM_AGENT_LOG_UPLOAD");
        }
        if (!Boolean.parseBoolean(inProgress)) {
            JSONArray deviceList = new JSONArray();
            try {
                deviceList = this.getDeviceNameList();
            }
            catch (final Exception var7) {
                this.out.log(Level.SEVERE, "Exception in uploading the support file..", var7);
            }
            jsonObject.put("failedDeviceList", (Object)deviceList);
        }
        jsonObject.put("inProgress", (Object)inProgress);
        jsonObject.put("status", (Object)status);
        return jsonObject;
    }
    
    public void supportFileCreationProcess(final JSONObject requestJsonObject) throws Exception {
        this.resetSFCProcess();
        String supportFileStatus = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("AGENTLOGSTATUS", 2);
        if (supportFileStatus == null || supportFileStatus.equals("Not Started")) {
            supportFileStatus = "Process started";
            ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)supportFileStatus, 2);
            this.supportFileCreationTime = System.currentTimeMillis();
        }
        final String emailID = String.valueOf(requestJsonObject.get("from_address"));
        final String userMessage = String.valueOf(requestJsonObject.get("user_message"));
        final String ticketID = String.valueOf(requestJsonObject.get("ticket_id"));
        final HashMap userDetails = new HashMap();
        userDetails.put("emailID", emailID);
        userDetails.put("userMessage", userMessage);
        userDetails.put("ticketID", ticketID);
        ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUSEMAIL", (Object)userDetails, 2);
        this.preCreateSupportFile(requestJsonObject);
    }
    
    private void preCreateSupportFile(final JSONObject data) throws Exception {
        this.out.log(Level.INFO, "SUPPORT FILE CREATION - STARTS");
        final String supportFileStatus = "Creating support file";
        ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)supportFileStatus, 2);
        MDMApiFactoryProvider.getMdmCompressAPI().cancelOperation();
        this.out.log(Level.INFO, "Deleting previously created support file ..");
        MDMApiFactoryProvider.getMdmCompressAPI().cleanSupportFolder(true);
        MDMApiFactoryProvider.getMdmCompressAPI().cleanSupportLogFolder(false);
        final String fileName = "eventlog1.txt";
        final String folderPath = "\\\\logs\\\\" + fileName;
        final String serverDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String destinationPath = serverDir + folderPath;
        this.out.log(Level.INFO, "Creating event log file.. Destination path :{0}", destinationPath);
        final String isMDMUploadStr = String.valueOf(data.get("mdm_log_upload"));
        boolean isMDMLogUpload = false;
        final Properties dmProps = new Properties();
        ((Hashtable<String, Boolean>)dmProps).put("hasMDMLogs", isMDMLogUpload);
        if (isMDMUploadStr != null && isMDMUploadStr.equalsIgnoreCase("true")) {
            isMDMLogUpload = true;
        }
        boolean isMDMLogAvailable = false;
        if (isMDMLogUpload) {
            final int waitTime = 10;
            isMDMLogAvailable = ApiFactoryProvider.getMDMSupportAPI().uploadAgentLogs(data, waitTime);
            ((Hashtable<String, Boolean>)dmProps).put("hasMDMLogs", isMDMLogAvailable);
        }
        final String isServerLogUploadStr = String.valueOf(data.get("server_log_upload"));
        boolean isServerLogUpload = false;
        if (isServerLogUploadStr != null && isServerLogUploadStr.equalsIgnoreCase("true")) {
            isServerLogUpload = true;
        }
        ((Hashtable<String, Boolean>)dmProps).put("hasServerLogs", isServerLogUpload);
        final String isDbLockFileUploadStr = String.valueOf(data.get("db_lock_file_upload"));
        boolean isDbLockFileUpload = false;
        if (isDbLockFileUploadStr != null && isDbLockFileUploadStr.equalsIgnoreCase("true")) {
            isDbLockFileUpload = true;
        }
        ((Hashtable<String, Boolean>)dmProps).put("hasDBLockLogs", isDbLockFileUpload);
        if (data.has("send_mail_copy")) {
            ((Hashtable<String, Object>)dmProps).put("sendMail", data.get("send_mail_copy"));
        }
        ((Hashtable<String, String>)dmProps).put("user_agent", String.valueOf(data.get("user_agent")));
        ApiFactoryProvider.getCacheAccessAPI().putCache(this.logUploadCacheConstant, (Object)dmProps, 2);
        if (!CustomerInfoUtil.isSAS) {
            this.onUpload();
        }
    }
    
    public void createSupportFile(final Properties supportProps) throws Exception {
        MDMApiFactoryProvider.getMdmCompressAPI().createSupportFile(supportProps);
    }
    
    public void onUpload() throws Exception {
        String supportFileStatus = "";
        String supportfile = "";
        Properties dmProps = new Properties();
        try {
            dmProps = (Properties)ApiFactoryProvider.getCacheAccessAPI().getCache(this.logUploadCacheConstant, 2);
            supportFileStatus = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("AGENTLOGSTATUS", 2);
            this.createSupportFile(dmProps);
            final String uniqueFileName = MDMApiFactoryProvider.getMdmCompressAPI().getSupportFileName();
            supportfile = MDMApiFactoryProvider.getMdmCompressAPI().compressSupportFile(uniqueFileName);
            if (((Hashtable<K, Boolean>)dmProps).get("hasMDMLogs") && ((Hashtable<K, Boolean>)dmProps).getOrDefault("sendMail", false) && this.getMdmAgentLogUplodedCount() > 0) {
                this.sendSupportFileMail();
            }
            final File logFile = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "logs" + SupportFileCreation.fs + "supportLogs" + SupportFileCreation.fs + supportfile);
            if (!supportfile.contains(".7z") && !supportfile.contains(".zip")) {
                throw new Exception("File Name Error");
            }
            final long fileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(logFile.getAbsolutePath());
            if (fileSize > this.maxSizeForBonitas) {
                supportFileStatus = "FILESIZE HIGH during upload";
                this.out.log(Level.INFO, "Log size is high. So upload cannot be done");
            }
            else if (fileSize < 100L) {
                supportFileStatus = "Creating support file failed";
                this.out.log(Level.INFO, "Log is empty. So upload is not done");
            }
            else {
                boolean isServerLogUpload = false;
                if (((Hashtable<K, Boolean>)dmProps).get("hasServerLogs")) {
                    isServerLogUpload = ((Hashtable<K, Boolean>)dmProps).get("hasServerLogs");
                }
            }
        }
        catch (final NativeException var9) {
            this.out.log(Level.WARNING, "NativeException while creating zip", (Throwable)var9);
            supportFileStatus = "Creating support file failed";
            ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)supportFileStatus, 2);
        }
        catch (final Exception var10) {
            this.out.log(Level.WARNING, "Error while creating zip", var10);
            supportFileStatus = "Creating support file failed";
            ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)supportFileStatus, 2);
        }
        if (!supportFileStatus.equals("Creating support file failed") && !supportFileStatus.equals("FILESIZE HIGH during upload")) {
            try {
                final Properties uploadProps = new Properties();
                ((Hashtable<String, String>)uploadProps).put("user_agent", dmProps.getProperty("user_agent"));
                ((Hashtable<String, String>)uploadProps).put("supportfile", supportfile);
                supportFileStatus = this.uploadSupportFile(uploadProps);
            }
            catch (final Exception var11) {
                this.out.log(Level.SEVERE, "Exception in uploading the support file..", var11);
                supportFileStatus = "upload failed";
            }
        }
        if (!supportFileStatus.equalsIgnoreCase("upload failed")) {
            this.out.log(Level.INFO, "Deleting support folder, since support file creation process completed ..");
            MDMApiFactoryProvider.getMdmCompressAPI().cleanSupportFolder(false);
            MDMApiFactoryProvider.getMdmCompressAPI().cleanSupportLogFolder(false);
        }
        ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)supportFileStatus, 2);
    }
    
    private String uploadSupportFile(final Properties uploadProps) {
        this.out.log(Level.INFO, "SUPPORT FILE UPLOADING - STARTS ---");
        String supportFileStatus = "Uploading support file";
        ApiFactoryProvider.getCacheAccessAPI().putCache("AGENTLOGSTATUS", (Object)supportFileStatus, 2);
        final HashMap userDetails = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("AGENTLOGSTATUSEMAIL", 2);
        final String fromAddress = userDetails.get("emailID");
        final String userMESSAGE = userDetails.get("userMessage");
        try {
            final boolean uploaded = MDMApiFactoryProvider.getUploadAction().checkUploadAccess();
            this.out.log(Level.WARNING, "Connection got while uploading:{0}", uploaded);
        }
        catch (final Exception var11) {
            this.out.log(Level.WARNING, "Error while obtaining Connection", var11);
        }
        try {
            final String supportFile = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "logs" + File.separator + "supportLogs" + File.separator + uploadProps.getProperty("supportfile");
            final boolean uploaded = MDMApiFactoryProvider.getUploadAction().uploadSupportFile(uploadProps.getProperty("user_agent"), supportFile, fromAddress, userMESSAGE, userDetails.get("ticketID"));
            this.out.log(Level.WARNING, "Uploaded:{0}", uploaded);
            if (uploaded) {
                supportFileStatus = "upload success";
            }
            else {
                supportFileStatus = "upload failed";
            }
            this.out.log(Level.INFO, "SUPPORT FILE UPLOADING - ENDS ---");
            return supportFileStatus;
        }
        catch (final Exception var12) {
            this.out.log(Level.WARNING, var12, () -> "Error while obtaining Connection" + ex.toString());
            return "upload failed";
        }
    }
    
    public void sendSupportFileMail() {
        final HashMap userDetails = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("AGENTLOGSTATUSEMAIL", 2);
        String filePath;
        if (CustomerInfoUtil.isSAS()) {
            filePath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + SupportFileCreation.fs + "logs" + SupportFileCreation.fs + "supportLogs" + SupportFileCreation.fs + "MDMCloudLogsToSupportTeam" + ".zip";
        }
        else {
            filePath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + SupportFileCreation.fs + "mdmAgentLogs.zip";
            final String sourceFilePath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + SupportFileCreation.fs + "mdm-logs";
            final File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            ZipOutputStream zos = null;
            try {
                zos = new ZipOutputStream(new FileOutputStream(filePath));
                this.zipMdmAgentLogs(new File(sourceFilePath), "mdm-logs", zos);
            }
            catch (final Exception e) {
                this.out.log(Level.SEVERE, "ERROR ZIPPING FOLDER", e);
                if (zos != null) {
                    try {
                        zos.close();
                    }
                    catch (final IOException e2) {
                        this.out.log(Level.SEVERE, "ERROR CLOSING ZIP OUTPUT STREAM", e2);
                    }
                }
            }
            finally {
                if (zos != null) {
                    try {
                        zos.close();
                    }
                    catch (final IOException e3) {
                        this.out.log(Level.SEVERE, "ERROR CLOSING ZIP OUTPUT STREAM", e3);
                    }
                }
            }
        }
        try {
            final ArrayList<String> attachments = new ArrayList<String>();
            attachments.add(filePath);
            MDMMailNotificationHandler.getInstance().sendSupportLogMail(userDetails.get("emailID"), this.getSuccessDeviceList(), attachments, true);
            ApiFactoryProvider.getCacheAccessAPI().removeCache("MDMAGENTLOGUPLOADSUCCESSLIST", 2);
        }
        catch (final Exception e4) {
            this.out.log(Level.SEVERE, "ERROR SENDING MAIL", e4);
        }
    }
    
    private void zipMdmAgentLogs(final File folder, final String folderName, final ZipOutputStream zos) throws IOException {
        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                this.zipMdmAgentLogs(file, folderName + SupportFileCreation.fs + file.getName(), zos);
            }
            else {
                BufferedInputStream bis = null;
                try {
                    zos.putNextEntry(new ZipEntry(folderName + File.separator + file.getName()));
                    bis = new BufferedInputStream(new FileInputStream(file));
                    final byte[] bytesIn = new byte[4096];
                    int read = 0;
                    while ((read = bis.read(bytesIn)) != -1) {
                        zos.write(bytesIn, 0, read);
                    }
                }
                catch (final IOException e) {
                    throw e;
                }
                finally {
                    zos.closeEntry();
                    if (bis != null) {
                        bis.close();
                    }
                }
            }
        }
    }
    
    public synchronized void removeDeviceFromList(final JSONObject deviceDetails) {
        final HashMap arrMDMAgentLogUploadList = this.getArrMDMAgentLogUploadList();
        if (arrMDMAgentLogUploadList != null) {
            try {
                arrMDMAgentLogUploadList.remove(deviceDetails.getLong("device_id"));
                this.addToSuccessList(deviceDetails.getString("device_name"));
                this.setArrMDMAgentLogUploadList(arrMDMAgentLogUploadList);
            }
            catch (final Exception var3) {
                this.out.log(Level.SEVERE, "Exception in removeDeviceList..", var3);
            }
        }
    }
    
    public void addToSuccessList(final String deviceName) {
        ArrayList successList = this.getSuccessDeviceList();
        if (successList == null) {
            successList = new ArrayList();
        }
        successList.add(deviceName);
        ApiFactoryProvider.getCacheAccessAPI().putCache("MDMAGENTLOGUPLOADSUCCESSLIST", (Object)successList, 2);
    }
    
    public ArrayList getSuccessDeviceList() {
        final ArrayList successDeviceList = (ArrayList)ApiFactoryProvider.getCacheAccessAPI().getCache("MDMAGENTLOGUPLOADSUCCESSLIST", 2);
        return successDeviceList;
    }
    
    public synchronized void incrementMDMLogUploadCount() {
        this.setMdmAgentLogUplodedCount(this.getMdmAgentLogUplodedCount() + 1);
    }
    
    public String getFailoverStatus() {
        return this.failoverSupport;
    }
    
    public String getExtension() {
        return this.fileExtention;
    }
    
    static {
        SupportFileCreation.fs = File.separator;
    }
}
