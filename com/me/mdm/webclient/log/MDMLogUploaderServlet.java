package com.me.mdm.webclient.log;

import java.util.Hashtable;
import com.adventnet.iam.security.SecurityUtil;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.Properties;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import com.adventnet.i18n.I18N;
import java.io.DataOutputStream;
import java.net.URLConnection;
import com.me.devicemanagement.framework.webclient.support.DMUploadAction;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import java.io.OutputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.List;
import com.adventnet.sym.server.devicemanagement.framework.groupevent.GroupEventNotifier;
import com.me.mdm.server.support.SupportFileCreation;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.io.InputStream;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class MDMLogUploaderServlet extends DeviceAuthenticatedRequestServlet
{
    private Logger logger;
    private Long customerID;
    private String deviceName;
    private String domainName;
    private Long resourceID;
    private Integer platformType;
    private Long acceptedLogSize;
    private String fromUser;
    
    public MDMLogUploaderServlet() {
        this.logger = Logger.getLogger(MDMLogUploaderServlet.class.getName());
        this.acceptedLogSize = 314572800L;
        this.fromUser = "2";
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.FINE, "Received Log from agent");
        final Long nDataLength = (Long)request.getContentLength();
        this.logger.log(Level.FINE, "MDMLogUploaderServlet : file conentent lenght is {0}", nDataLength);
        this.logger.log(Level.FINE, "MDMLogUploaderServlet :Acceptable file conentent lenght is {0}", this.acceptedLogSize);
        final List allowedFileList = new ArrayList();
        try {
            if (nDataLength > this.acceptedLogSize) {
                this.logger.log(Level.WARNING, "MDMLogUploaderServlet : Going to reject the file upload as the file content length is {0}, acceptable content length is {1}", new Object[] { nDataLength, this.acceptedLogSize });
                response.sendError(403, "Request Refused");
                return;
            }
            final String initiatedBy = request.getParameter("initiatedBy");
            allowedFileList.add("logger.txt");
            allowedFileList.add("mdmlogs.zip");
            allowedFileList.add("logger.zip");
            allowedFileList.add("managedprofile_mdmlogs.zip");
            final String udid = request.getParameter("udid");
            if (udid != null && FileUploadUtil.hasVulnerabilityInFileName(udid)) {
                this.logger.log(Level.WARNING, "AgentLogUploadServlet : Going to reject the file upload as path traversal vulnerability found in udid param {0}", udid);
                response.sendError(403, "Request Refused");
                return;
            }
            String fileName = request.getParameter("filename");
            fileName = fileName.toLowerCase();
            if (fileName != null && FileUploadUtil.hasVulnerabilityInFileName(fileName, "log|txt|zip|7z") && !allowedFileList.contains(fileName)) {
                this.logger.log(Level.WARNING, "AgentLogUploadServlet : Going to reject the file upload {0}", fileName);
                response.sendError(403, "Request Refused");
                return;
            }
            InputStream fileStreamData = null;
            try {
                fileStreamData = this.validateRequestStreamWithTika((InputStream)request.getInputStream(), fileName);
            }
            catch (final SyMException ex) {
                if (ex.getErrorCode() == 140001) {
                    this.logger.log(Level.WARNING, "MDMLogUploaderServlet : Content type detected is not application/zip - Terminating log upload request with error code 403");
                    response.sendError(403, "Request Refused");
                    return;
                }
            }
            final HashMap deviceMap = MDMUtil.getInstance().getDeviceDetailsFromUDID(udid);
            if (deviceMap != null) {
                this.customerID = deviceMap.get("CUSTOMER_ID");
                this.deviceName = this.removeInvalidCharactersInFileName(deviceMap.get("MANAGEDDEVICEEXTN.NAME"));
                this.domainName = deviceMap.get("DOMAIN_NETBIOS_NAME");
                this.resourceID = deviceMap.get("RESOURCE_ID");
                this.platformType = deviceMap.get("PLATFORM_TYPE");
            }
            else {
                this.customerID = 0L;
                this.deviceName = "default";
                this.domainName = "default";
            }
            if (initiatedBy != null && initiatedBy.equals(this.fromUser)) {
                final JSONObject jsonObject = new JSONObject(request.getParameter("extraData"));
                final String issueType = String.valueOf(jsonObject.get("IssueType"));
                final String issueDescription = String.valueOf(jsonObject.get("IssueDescription"));
                final String fromAddress = jsonObject.optString("EmailId");
                final String outputFileName = MDMApiFactoryProvider.getMdmCompressAPI().getSupportFileName() + ".zip";
                final String toAddress = ProductUrlLoader.getInstance().getValue("supportmailid");
                String ticketID = jsonObject.optString("TicketId");
                ticketID = (ticketID.trim().equals("") ? "NA" : ticketID.trim());
                Boolean status = this.downloadUserSupportFile(fileName, udid, fileStreamData);
                if (status) {
                    status = this.zipProcess(fileName, outputFileName, udid);
                }
                if (status) {
                    this.doUpload(outputFileName, fromAddress, toAddress, issueType, issueDescription, ticketID);
                }
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    this.deleteLogsFile();
                }
                if (!status) {
                    this.logger.log(Level.WARNING, "MDMLogUploaderServlet : Going to reject the file upload due to failure to download/zipping/upload issue");
                    response.sendError(403, "Request Refused");
                }
            }
            else {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    this.downloadSupportFileCloud(fileName, udid, fileStreamData);
                }
                else {
                    this.downloadSupportFile(fileName, udid, fileStreamData);
                }
                final SupportFileCreation supportFileCreation = SupportFileCreation.getInstance();
                supportFileCreation.incrementMDMLogUploadCount();
                final JSONObject deviceDetails = new JSONObject();
                deviceDetails.put("platform_type_id", (Object)this.platformType);
                deviceDetails.put("device_id", (Object)this.resourceID);
                deviceDetails.put("device_name", (Object)this.deviceName);
                supportFileCreation.removeDeviceFromList(deviceDetails);
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    GroupEventNotifier.getInstance().actionCompleted(Long.valueOf(Long.parseLong(String.valueOf(this.resourceID))), "MDM_AGENT_LOG_UPLOAD");
                }
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception   ", ex2);
        }
    }
    
    public void downloadSupportFile(final String fileName, final String udid, final InputStream fileStreamData) throws Exception {
        final String baseDir = System.getProperty("server.home");
        this.deviceName = this.removeInvalidCharactersInFileName(this.deviceName);
        if (FileUploadUtil.hasVulnerabilityInFileName(this.deviceName) || FileUploadUtil.hasVulnerabilityInFileName(udid)) {
            throw new Exception("Unexpected character in file name");
        }
        final String localDirToStore = baseDir + File.separator + "mdm-logs" + File.separator + this.customerID + File.separator + this.deviceName + "_" + udid;
        final File file = new File(localDirToStore);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.logger.log(Level.WARNING, "absolute Dir {0} ", new Object[] { localDirToStore });
        final String absoluteFileName = localDirToStore + File.separator + fileName;
        this.logger.log(Level.WARNING, "absolute File Name {0} ", new Object[] { fileName });
        InputStream in = null;
        FileOutputStream fout = null;
        try {
            in = fileStreamData;
            fout = new FileOutputStream(absoluteFileName);
            final byte[] bytes = new byte[10000];
            int i;
            while ((i = in.read(bytes)) != -1) {
                fout.write(bytes, 0, i);
            }
            fout.flush();
        }
        catch (final Exception var37) {
            var37.printStackTrace();
        }
        finally {
            if (fout != null) {
                fout.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }
    
    public void downloadSupportFileCloud(final String fileName, final String udid, final InputStream fileStreamData) throws Exception {
        final String cloudBaseDir = "support";
        final String dfsDirToStore = cloudBaseDir + File.separator + "mdm-logs" + File.separator + this.customerID + File.separator + this.deviceName + "_" + udid;
        this.logger.log(Level.WARNING, "absolute Dir {0} ", new Object[] { dfsDirToStore });
        final String absoluteFileName = dfsDirToStore + File.separator + fileName;
        this.logger.log(Level.WARNING, "absolute File Name {0} ", new Object[] { fileName });
        InputStream in = null;
        final OutputStream fout = ApiFactoryProvider.getFileAccessAPI().writeFile(absoluteFileName);
        try {
            in = fileStreamData;
            final byte[] bytes = new byte[10000];
            int i;
            while ((i = in.read(bytes)) != -1) {
                fout.write(bytes, 0, i);
            }
            fout.flush();
        }
        catch (final Exception var21) {
            this.logger.log(Level.SEVERE, "Error : ", var21);
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
    
    public Boolean downloadUserSupportFile(final String fileName, final String udid, final InputStream fileStreamData) throws Exception {
        Boolean status = true;
        final String cloudBaseDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "logsFromUser" + File.separator + this.resourceID;
        if (FileUploadUtil.hasVulnerabilityInFileName(this.deviceName) || FileUploadUtil.hasVulnerabilityInFileName(udid) || FileUploadUtil.hasVulnerabilityInFileName(fileName)) {
            throw new Exception("Unexpected character in file name");
        }
        final String dirToStore = cloudBaseDir + File.separator + "support" + File.separator + "mdm-logs" + File.separator + this.customerID + File.separator + this.deviceName + "_" + udid + File.separator;
        this.logger.log(Level.WARNING, "absolute Dir {0} ", new Object[] { dirToStore });
        final File resourceDir = new File(cloudBaseDir);
        if (resourceDir.exists()) {
            FileUtils.deleteDirectory(resourceDir);
        }
        File file = null;
        InputStream in = null;
        OutputStream fout = null;
        try {
            final String absoluteFileName = dirToStore + File.separator + fileName;
            this.logger.log(Level.WARNING, "absolute File Name {0} ", new Object[] { fileName });
            in = fileStreamData;
            new File(dirToStore).mkdirs();
            file = new File(absoluteFileName);
            if (file.getCanonicalPath().startsWith(dirToStore)) {
                if (file.exists()) {
                    file.delete();
                }
                fout = new FileOutputStream(file);
                final byte[] bytes = new byte[10000];
                int i;
                while ((i = in.read(bytes)) != -1) {
                    fout.write(bytes, 0, i);
                }
                fout.flush();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error : ", e);
            status = false;
            try {
                if (fout != null) {
                    fout.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Error : ", e);
            }
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Error : ", e2);
            }
        }
        return status;
    }
    
    public Boolean zipProcess(final String inputFileName, final String outputFileName, final String udid) throws Exception {
        Boolean status = true;
        final String cloudBaseDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "logsFromUser" + File.separator + this.resourceID;
        if (FileUploadUtil.hasVulnerabilityInFileName(this.deviceName) || FileUploadUtil.hasVulnerabilityInFileName(udid) || FileUploadUtil.hasVulnerabilityInFileName(inputFileName)) {
            throw new Exception("Unexpected character in file name");
        }
        final String supportAbsPath = "support" + File.separator + "mdm-logs" + File.separator + this.customerID + File.separator + this.deviceName + "_" + udid + File.separator + inputFileName;
        final String inputFullFileName = cloudBaseDir + File.separator + supportAbsPath;
        final String dest = cloudBaseDir + File.separator + outputFileName;
        final File outputFile = new File(dest);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        ZipOutputStream targetZipOutputStream = null;
        InputStream fis = null;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputFile);
            targetZipOutputStream = new ZipOutputStream(outputStream);
            targetZipOutputStream.setLevel(9);
            fis = new FileInputStream(new File(inputFullFileName));
            targetZipOutputStream.putNextEntry(new ZipEntry(supportAbsPath));
            final byte[] bytes = new byte[1000];
            int length;
            while ((length = fis.read(bytes)) > 0) {
                targetZipOutputStream.write(bytes, 0, length);
            }
            targetZipOutputStream.closeEntry();
            outputStream.flush();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error : ", e);
            status = false;
            try {
                if (targetZipOutputStream != null) {
                    targetZipOutputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Error : ", e);
            }
        }
        finally {
            try {
                if (targetZipOutputStream != null) {
                    targetZipOutputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Error : ", e2);
            }
        }
        return status;
    }
    
    public void doUpload(final String inputFile, final String fromAddress, final String toAddress, final String issueType, final String issueDescription, final String ticketID) {
        DataOutputStream out = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream bos = null;
        try {
            final Properties productProps = MDMApiFactoryProvider.getUploadAction().getProductInfo();
            if (productProps != null) {
                final String somProps = ((Hashtable<K, String>)productProps).get("som");
                final String[] somPropsArray = somProps.split("metrId");
                ((Hashtable<String, String>)productProps).put("som", somPropsArray[0]);
            }
            URLConnection conn = null;
            final HashMap<String, Object> connectionData = DMUploadAction.getInstance().openBonitasConnection();
            conn = connectionData.get("URLConnection");
            final String boundary = connectionData.get("boundary");
            out = new DataOutputStream(conn.getOutputStream());
            this.logger.log(Level.INFO, "Writing Mail Related Information");
            out.writeBytes("--" + boundary + "\r\n");
            this.writeParam("fromAddress", fromAddress, out, boundary);
            final String sendToAddress = I18N.getMsg(toAddress, new Object[0]);
            this.writeParam("toAddress", sendToAddress, out, boundary);
            this.writeParam("todo", "upload", out, boundary);
            this.writeParam("Issue Type", issueType, out, boundary);
            this.writeParam("Issue Description", issueDescription, out, boundary);
            this.writeParam("Subject", MDMApiFactoryProvider.getUploadAction().getSubject(1), out, boundary);
            final String environment = MDMApiFactoryProvider.getUploadAction().getEnvironment();
            if (environment != null) {
                this.writeParam("Environment", environment, out, boundary);
            }
            this.writeParam("TicketID", ticketID, out, boundary);
            if (productProps != null) {
                final String productType = productProps.getProperty("productType");
                if (productType != null) {
                    this.writeParam("Product Type", productType, out, boundary);
                }
                final String lang = productProps.getProperty("lang");
                if (lang != null) {
                    this.writeParam("lang", lang, out, boundary);
                }
            }
            this.logger.log(Level.INFO, "Finished writing Mail Related Information, Now Uploading File Starts");
            final String cloudBaseDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "logsFromUser" + File.separator + this.resourceID;
            if (FileUploadUtil.hasVulnerabilityInFileName(this.deviceName) || FileUploadUtil.hasVulnerabilityInFileName(inputFile)) {
                throw new Exception("Unexpected character in file name");
            }
            final String inputFilePath = cloudBaseDir + File.separator + inputFile;
            this.writeFile("uploadfile", new File(inputFilePath), out, boundary);
            this.logger.log(Level.INFO, "Writing Customer Details Starts.");
            out.flush();
            this.logger.log(Level.INFO, "Finished writing Customer Related Info. Now Reading From Server");
            final InputStream stream = conn.getInputStream();
            in = new BufferedInputStream(stream);
            bos = new ByteArrayOutputStream();
            final boolean var42 = false;
            int i;
            while ((i = in.read()) != -1) {
                bos.write(i);
            }
            this.logger.log(Level.INFO, "Upload Completed in URLConnection Method");
            final ArrayList<String> deviceList = new ArrayList<String>();
            deviceList.add(this.deviceName);
            final ArrayList<String> attachments = new ArrayList<String>();
            attachments.add(inputFilePath);
            MDMMailNotificationHandler.getInstance().sendSupportLogMail(fromAddress, deviceList, attachments, false);
        }
        catch (final Exception var43) {
            this.deleteLogsFile();
            this.logger.log(Level.WARNING, "MDMUploadAction -> Problem occurred in doUpload ", var43);
            try {
                out.close();
                in.close();
                bos.close();
            }
            catch (final Exception var44) {
                this.logger.log(Level.SEVERE, "Error : ", var44);
            }
        }
        finally {
            try {
                out.close();
                in.close();
                bos.close();
            }
            catch (final Exception var45) {
                this.logger.log(Level.SEVERE, "Error : ", var45);
            }
        }
    }
    
    private void writeParam(final String name, final String value, final DataOutputStream out, final String boundary) {
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n");
            out.writeBytes(value);
            out.writeBytes("\r\n--" + boundary + "\r\n");
        }
        catch (final Exception var5) {
            this.logger.log(Level.WARNING, "SupportFileUploader -> Problem occurred in writeParam ", var5);
        }
    }
    
    private void writeFile(final String name, final File inputFile, final DataOutputStream out, final String boundary) throws Exception {
        InputStream fis = null;
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"; filename=\"" + inputFile.getAbsolutePath() + "\"\r\n");
            out.writeBytes("content-type: application/octet-stream\r\n\r\n");
            fis = new FileInputStream(inputFile);
            final byte[] buffer = new byte[1000];
            while (true) {
                final int amountRead = fis.read(buffer);
                if (amountRead == -1) {
                    break;
                }
                out.write(buffer, 0, amountRead);
            }
            out.writeBytes("\r\n--" + boundary + "\r\n");
            this.logger.log(Level.INFO, "Finished writing file content.");
        }
        catch (final Exception var18) {
            this.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred", var18);
            try {
                fis.close();
            }
            catch (final Exception var19) {
                this.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred", var19);
            }
        }
        finally {
            try {
                fis.close();
            }
            catch (final Exception var20) {
                this.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred", var20);
            }
        }
    }
    
    public boolean checkUploadAccess() throws Exception {
        try {
            final URL servlet = new URL(DMUploadAction.getInstance().getUploadUrl());
            final URLConnection conn = servlet.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            return true;
        }
        catch (final UnknownHostException var2) {
            this.logger.log(Level.SEVERE, "No access for uploading file");
            return false;
        }
        catch (final Exception var3) {
            this.logger.log(Level.SEVERE, "Unable to create connection to for uploading support file");
            return false;
        }
    }
    
    private static String getBrowserInfo(final String browser) {
        String browsername = "";
        String browserversion = "";
        try {
            if (browser.contains("MSIE")) {
                final String subsString = browser.substring(browser.indexOf("MSIE"));
                final String[] Info = subsString.split(";")[0].split(" ");
                browsername = Info[0];
                browserversion = Info[1];
            }
            else if (browser.contains("Firefox")) {
                final String subsString = browser.substring(browser.indexOf("Firefox"));
                final String[] Info = subsString.split(" ")[0].split("/");
                browsername = Info[0];
                browserversion = Info[1];
            }
            else if (browser.contains("Chrome")) {
                final String subsString = browser.substring(browser.indexOf("Chrome"));
                final String[] Info = subsString.split(" ")[0].split("/");
                browsername = Info[0];
                browserversion = Info[1];
            }
            else if (browser.contains("Opera")) {
                final String subsString = browser.substring(browser.indexOf("Opera"));
                final String[] Info = subsString.split(" ")[0].split("/");
                browsername = Info[0];
                browserversion = Info[1];
            }
            else if (browser.contains("Safari")) {
                final String subsString = browser.substring(browser.indexOf("Safari"));
                final String[] Info = subsString.split(" ")[0].split("/");
                browsername = Info[0];
                browserversion = Info[1];
            }
        }
        catch (final Exception var5) {
            return "";
        }
        return browsername + "-" + browserversion;
    }
    
    private String removeInvalidCharactersInFileName(final String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
    
    private InputStream validateRequestStreamWithTika(final InputStream requestStream, final String fileName) throws SyMException {
        ByteArrayInputStream fileInputStream = null;
        try {
            fileInputStream = new ByteArrayInputStream(IOUtils.toByteArray(requestStream));
            final String contentType = SecurityUtil.getMimeTypeUsingTika((InputStream)fileInputStream, fileName);
            if (!contentType.equals("application/zip")) {
                this.logger.log(Level.SEVERE, "Detected content types of LogUploaderFile - ContentType - {0}", new Object[] { contentType });
                throw new SyMException(140001, "File content type is not of zip type", (Throwable)null);
            }
        }
        catch (final IOException e) {
            this.logger.log(Level.SEVERE, "Exception while detecting content type for log upload file via Tika", e);
            try {
                if (requestStream != null) {
                    requestStream.close();
                }
            }
            catch (final IOException e) {
                this.logger.log(Level.SEVERE, "Exception while closing input stream", e);
            }
        }
        finally {
            try {
                if (requestStream != null) {
                    requestStream.close();
                }
            }
            catch (final IOException e2) {
                this.logger.log(Level.SEVERE, "Exception while closing input stream", e2);
            }
        }
        return fileInputStream;
    }
    
    private void deleteLogsFile() {
        try {
            final File resourceDir = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "logsFromUser" + File.separator + this.resourceID);
            if (resourceDir.exists()) {
                FileUtils.deleteDirectory(resourceDir);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error deleting log file", e);
        }
    }
}
