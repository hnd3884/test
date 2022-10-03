package com.me.ems.onpremise.support.api.v1.service;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.ws.rs.core.Response;
import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.onpremise.webclient.dblock.CleanDbLockFiles;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.webclient.support.Compress;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.webclient.support.SupportFileCreation;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.me.ems.onpremise.support.utils.SupportTabUtils;
import com.me.ems.onpremise.support.factory.SupportFileService;

public class SupportFileServiceImpl implements SupportFileService
{
    private SupportTabUtils supportTabUtils;
    private Logger log;
    
    public SupportFileServiceImpl() {
        this.supportTabUtils = new SupportTabUtils();
        this.log = Logger.getLogger(SupportFileServiceImpl.class.getName());
    }
    
    @Override
    public Map getSupportFileDetails(final String supportAction, final String disableServerCheck) throws APIException {
        try {
            final Map supportFileDetails = new HashMap();
            final SupportFileCreation supportFileCreation = SupportFileCreation.getInstance();
            supportFileCreation.resetSFCstatus();
            supportFileDetails.put("emailID", this.supportTabUtils.getLogonUserEmailID());
            supportFileDetails.put("supportFile", supportFileCreation.getSupportFileName() + "." + supportFileCreation.fileExtention);
            supportFileDetails.put("failedComputerList", supportFileCreation.getComputerListAsList());
            supportFileDetails.put("failedDeviceList", supportFileCreation.getDeviceListAsList());
            supportFileDetails.put("serverLogUpload", true);
            if (disableServerCheck != null && disableServerCheck.equalsIgnoreCase("TRUE")) {
                supportFileDetails.put("serverCheckDisable", true);
            }
            if (supportAction != null && supportAction.equalsIgnoreCase("supportFileLayoutForDBlock")) {
                supportFileDetails.put("dbLockSupport", "true");
                supportFileDetails.put("userMessage", I18N.getMsg("dc.common.DBLOCK.MESSAGE", new Object[0]));
            }
            return supportFileDetails;
        }
        catch (final Exception e) {
            this.log.log(Level.SEVERE, "Exception caught while obtaining support file details ", e);
            throw new APIException("GENERIC0005");
        }
    }
    
    @Override
    public Map getProcessStatus() throws APIException {
        final String emailID = this.supportTabUtils.getLogonUserEmailID();
        final Map sfCstatus = SupportFileCreation.getInstance().getSFCstatusMap(emailID);
        final String message = sfCstatus.get("message");
        int statusCode = -1;
        Label_0333: {
            if (message != null && message.equalsIgnoreCase("Not Started")) {
                statusCode = 0;
            }
            else if (message != null && message.equalsIgnoreCase("Process started")) {
                statusCode = 1;
            }
            else if (message != null && message.equalsIgnoreCase("Creating support file")) {
                statusCode = 2;
            }
            else if (message != null && message.equalsIgnoreCase("Creating support file failed")) {
                statusCode = 3;
            }
            else if (message != null && message.equalsIgnoreCase("Uploading support file")) {
                statusCode = 4;
            }
            else if (message != null && message.equalsIgnoreCase("upload success")) {
                statusCode = 5;
            }
            else {
                if (message != null && message.equalsIgnoreCase("upload failed")) {
                    statusCode = 6;
                    try {
                        String filePath = SyMUtil.getInstallationDir();
                        final String fileName = SupportFileCreation.getInstance().getSupportFileName() + "." + SupportFileCreation.getInstance().getExtension();
                        if (filePath != null && fileName != null) {
                            filePath = filePath + File.separator + "logs" + File.separator + "supportLogs" + File.separator + fileName;
                            final File file = new File(filePath);
                            if (file.exists()) {
                                sfCstatus.put("logFilePath", filePath);
                            }
                        }
                        break Label_0333;
                    }
                    catch (final Exception e) {
                        this.log.severe("File not found in the directory");
                        throw new APIException("RESOURCE0004");
                    }
                }
                if (message != null && message.equalsIgnoreCase("FILESIZE HIGH during upload")) {
                    statusCode = 7;
                }
            }
        }
        sfCstatus.put("statusCode", statusCode);
        return sfCstatus;
    }
    
    @Override
    public void cancelSupportFileCreation() throws APIException {
        try {
            this.log.log(Level.INFO, "Deleting support folder on <cancel> button ..");
            SupportFileCreation.getInstance().resetSFCProcess();
            Compress.cancelOperation();
            SupportFileCreation.getInstance().cleanSupportFolder(false);
        }
        catch (final Exception e) {
            this.log.log(Level.WARNING, "Error while deleting support file" + e.toString(), e);
            throw new APIException("GENERIC0005");
        }
    }
    
    @Override
    public Map supportFileCreationForDBLock(final Map supportFileDetails) {
        final Map dbLockSupportFile = new HashMap();
        dbLockSupportFile.put("dbLockFileUpload", "true");
        dbLockSupportFile.put("automaticMail", supportFileDetails.get("automaticMail"));
        dbLockSupportFile.put("emailID", supportFileDetails.get("emailID"));
        dbLockSupportFile.put("userMessage", supportFileDetails.get("userMessage"));
        dbLockSupportFile.put("userAgent", supportFileDetails.get("userAgent"));
        final Map responseDetails = SupportFileCreation.getInstance().supportFileCreationProcess(dbLockSupportFile);
        this.log.log(Level.INFO, "support file creation api for dblock is called..");
        final Boolean automaticMail = Boolean.valueOf(dbLockSupportFile.get("automaticMail").toString());
        if (automaticMail != null) {
            this.log.log(Level.INFO, "automatic mail {0}", automaticMail);
            if (automaticMail) {
                try {
                    final DataObject data = SyMUtil.getPersistence().get("DbLockSettings", (Criteria)null);
                    final Row settingsRow = data.getRow("DbLockSettings");
                    settingsRow.set("IS_AUTOMATIC", (Object)"true");
                    data.updateRow(settingsRow);
                    SyMUtil.getPersistence().update(data);
                }
                catch (final DataAccessException ex) {
                    this.log.log(Level.SEVERE, "Exception while accessing dblocksettings table {0}", (Throwable)ex);
                }
            }
        }
        responseDetails.put("supportFile", SupportFileCreation.getInstance().getSupportFileName() + "." + SupportFileCreation.getInstance().getExtension());
        final Object uploaded = responseDetails.get("isFileUploaded");
        if (uploaded != null && uploaded.toString().equalsIgnoreCase("true")) {
            final CleanDbLockFiles cleanDbLockFilesObj = new CleanDbLockFiles();
            CleanDbLockFiles.SetNotificationOff();
            CleanDbLockFiles.deleteDblockFiles();
        }
        return responseDetails;
    }
    
    @Override
    public Long validateCustomer(final String customerIdStr, final Map supportFileDetails) throws APIException {
        Long customerID = null;
        if (!CustomerInfoUtil.getInstance().isMSP()) {
            customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            supportFileDetails.put("customerIdForMDM", customerID);
        }
        if (customerIdStr != null && !customerIdStr.isEmpty()) {
            if (customerIdStr.equalsIgnoreCase("all")) {
                try {
                    customerID = CustomerInfoUtil.getInstance().getCustomerIDForLoginUser();
                    supportFileDetails.put("customerIdForMDM", null);
                }
                catch (final Exception ex) {
                    if (!ex.getMessage().equalsIgnoreCase("Customer ID Not Available.")) {
                        throw new APIException("GENERIC0005");
                    }
                }
            }
            else {
                customerID = Long.parseLong(customerIdStr);
                supportFileDetails.put("customerIdForMDM", customerID);
            }
        }
        return customerID;
    }
    
    @Override
    public Map supportFileCreation(final Map supportFileDetails) {
        final Map responseDetails = SupportFileCreation.getInstance().supportFileCreationProcess(supportFileDetails);
        Object failedComputerListAlreadySet = supportFileDetails.get("failedComputerList");
        final Object failedDeviceListAlreadySet = supportFileDetails.get("failedDeviceList");
        Object failedDSList = supportFileDetails.get("failedDSList");
        if (failedComputerListAlreadySet == null || failedComputerListAlreadySet.toString().length() <= 0) {
            final List sFailedComputerList = SupportFileCreation.getInstance().getComputerListAsList();
            responseDetails.put("failedComputerList", sFailedComputerList);
            this.log.log(Level.INFO, "Final Failed Computer List .." + sFailedComputerList);
        }
        if (failedDeviceListAlreadySet == null || failedDeviceListAlreadySet.toString().length() <= 0) {
            final List sFailedDeviceList = SupportFileCreation.getInstance().getDeviceListAsList();
            responseDetails.put("failedDeviceList", sFailedDeviceList);
            this.log.log(Level.INFO, "Final Failed Computer List .." + sFailedDeviceList);
        }
        failedComputerListAlreadySet = supportFileDetails.get("failedComputerList");
        try {
            String failedResList = null;
            if (failedComputerListAlreadySet != null) {
                failedResList = I18N.getMsg("dc.common.Agent", new Object[0]) + ":" + failedComputerListAlreadySet;
            }
            if (failedDSList != null) {
                failedDSList = "Distribution Server:" + failedDSList;
                failedResList = ((failedResList != null) ? (failedResList + "&nbsp;" + I18N.getMsg("dc.common.AND", new Object[0]) + "&nbsp;" + failedDSList) : String.valueOf(failedDSList));
            }
            responseDetails.put("failedComputerList", failedResList);
        }
        catch (final Exception e) {
            this.log.log(Level.SEVERE, "Exception in Getting failed computer list..", e);
        }
        responseDetails.put("uploaded", supportFileDetails.get("uploaded"));
        responseDetails.put("supportFile", SupportFileCreation.getInstance().getSupportFileName() + "." + SupportFileCreation.getInstance().getExtension());
        return responseDetails;
    }
    
    private void convertBooleanToString(final Map supportFileDetails) {
        if (supportFileDetails.containsKey("mdmLogUpload")) {
            supportFileDetails.put("mdmLogUpload", String.valueOf(supportFileDetails.get("mdmLogUpload")));
        }
        if (supportFileDetails.containsKey("serverLogUpload")) {
            supportFileDetails.put("serverLogUpload", String.valueOf(supportFileDetails.get("serverLogUpload")));
        }
        if (supportFileDetails.containsKey("agentLogUpload")) {
            supportFileDetails.put("agentLogUpload", String.valueOf(supportFileDetails.get("agentLogUpload")));
        }
        if (supportFileDetails.containsKey("dsLogUpload")) {
            supportFileDetails.put("dsLogUpload", String.valueOf(supportFileDetails.get("dsLogUpload")));
        }
        if (supportFileDetails.containsKey("dbLockFileUpload")) {
            supportFileDetails.put("dbLockFileUpload", String.valueOf(supportFileDetails.get("dbLockFileUpload")));
        }
    }
    
    @Override
    public Response downloadSupportFile() throws APIException {
        try {
            String filePath = SyMUtil.getInstallationDir();
            final String fileName = SupportFileCreation.getInstance().getSupportFileName() + "." + SupportFileCreation.getInstance().getExtension();
            if (filePath != null && fileName != null) {
                filePath = filePath + File.separator + "logs" + File.separator + "supportLogs" + File.separator + fileName;
                final File file = new File(filePath);
                if (file.exists()) {
                    return Response.ok().header("Content-Disposition", (Object)("attachment;filename=\"" + fileName + "\"")).header("X-FileName", (Object)fileName).entity((Object)ApiFactoryProvider.getFileAccessAPI().readFile(filePath)).build();
                }
            }
            else {
                this.log.severe("Requested File Not Found in the Directory");
            }
            throw new APIException("RESOURCE0004");
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.log.log(Level.SEVERE, "Exception while downloading File {0}", ex2);
            throw new APIException("GENERIC0005");
        }
    }
    
    @Override
    public void validateSupportFileData(final Map supportFileDetails) throws APIException {
        this.convertBooleanToString(supportFileDetails);
        CustomerInfoThreadLocal.setSkipCustomerFilter("true");
        try {
            boolean isMDMDevicesValid = true;
            final boolean isAgentsAndDSValid = ApiFactoryProvider.getSupportTabAPI().isAgentAndDSAvailableForUser(supportFileDetails);
            final String isMDMUploadStr = supportFileDetails.get("mdmLogUpload");
            if (isMDMUploadStr != null && isMDMUploadStr.equalsIgnoreCase("true")) {
                final List resourceIDsForMDMDevices = new ArrayList();
                final JSONArray mdmDeviceLog = new JSONArray((String)supportFileDetails.get("mdmDeviceLog"));
                supportFileDetails.put("mdmDeviceLog", mdmDeviceLog);
                for (int i = 0; i < mdmDeviceLog.length(); ++i) {
                    final JSONObject device = mdmDeviceLog.getJSONObject(i);
                    resourceIDsForMDMDevices.add(device.getLong("dataId"));
                }
                isMDMDevicesValid = ApiFactoryProvider.getMDMSupportAPI().isMDMDevicesSelectedValid(resourceIDsForMDMDevices, Long.valueOf(supportFileDetails.get("customerIdForMDM")), Long.valueOf(supportFileDetails.get("userID")));
            }
            if (!isMDMDevicesValid || !isAgentsAndDSValid) {
                throw new APIException(Response.Status.BAD_REQUEST, "RESOURCE0002", (String)null);
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIException) {
                throw (APIException)ex;
            }
            this.log.log(Level.SEVERE, "Exception while validating resource for support file ", ex);
            throw new APIException("GENERIC0005");
        }
    }
}
