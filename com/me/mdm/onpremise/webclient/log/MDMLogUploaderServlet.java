package com.me.mdm.onpremise.webclient.log;

import org.apache.tika.Tika;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.Reader;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.webclient.support.SupportFileCreation;
import java.io.FileOutputStream;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.io.InputStream;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
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
    
    public MDMLogUploaderServlet() {
        this.logger = Logger.getLogger("MDMLogger");
        this.acceptedLogSize = 314572800L;
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        final Reader reader = null;
        final PrintWriter printWriter = null;
        this.logger.log(Level.WARNING, "Received Log from agent");
        final Long nDataLength = (Long)request.getContentLength();
        this.logger.log(Level.WARNING, "MDMLogUploaderServlet : file conentent lenght is {0}", nDataLength);
        this.logger.log(Level.WARNING, "MDMLogUploaderServlet :Acceptable file conentent lenght is {0}", this.acceptedLogSize);
        try {
            if (nDataLength > this.acceptedLogSize) {
                this.logger.log(Level.WARNING, "MDMLogUploaderServlet : Going to reject the file upload as the file conentent lenght is {0}", nDataLength);
                response.sendError(403, "Request Refused");
                return;
            }
            final String udid = request.getParameter("udid");
            if (udid != null && FileUploadUtil.hasVulnerabilityInFileName(udid)) {
                this.logger.log(Level.WARNING, "MDMLogUploaderServlet : Going to reject the file upload as path traversal vulnerability found in udid param {0}", udid);
                response.sendError(403, "Request Refused");
                return;
            }
            final String platform = request.getParameter("platform");
            String fileName = request.getParameter("filename");
            final HashMap deviceMap = MDMUtil.getInstance().getDeviceDetailsFromUDID(udid);
            if (deviceMap != null) {
                this.customerID = deviceMap.get("CUSTOMER_ID");
                this.deviceName = deviceMap.get("MANAGEDDEVICEEXTN.NAME");
                this.domainName = deviceMap.get("DOMAIN_NETBIOS_NAME");
                this.resourceID = deviceMap.get("RESOURCE_ID");
                this.platformType = deviceMap.get("PLATFORM_TYPE");
            }
            else {
                this.customerID = 0L;
                this.deviceName = "default";
                this.domainName = "default";
            }
            final String baseDir = System.getProperty("server.home");
            this.deviceName = this.removeInvalidCharactersInFileName(this.deviceName);
            final String localDirToStore = baseDir + File.separator + "mdm-logs" + File.separator + this.customerID + File.separator + this.deviceName + "_" + udid;
            final File file = new File(localDirToStore);
            if (!file.exists()) {
                file.mkdirs();
            }
            this.logger.log(Level.WARNING, "absolute Dir {0} ", new Object[] { localDirToStore });
            fileName = fileName.toLowerCase();
            if (fileName != null && FileUploadUtil.hasVulnerabilityInFileName(fileName, "log|txt|zip|7z")) {
                this.logger.log(Level.WARNING, "MDMLogUploaderServlet : Going to reject the file upload {0}", fileName);
                response.sendError(403, "Request Refused");
                return;
            }
            final String absoluteFileName = localDirToStore + File.separator + fileName;
            this.logger.log(Level.WARNING, "absolute File Name {0} ", new Object[] { fileName });
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
            catch (final Exception e1) {
                e1.printStackTrace();
            }
            finally {
                if (fout != null) {
                    fout.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            final SupportFileCreation supportFileCreation = SupportFileCreation.getInstance();
            supportFileCreation.incrementMDMLogUploadCount();
            final JSONObject deviceDetails = new JSONObject();
            deviceDetails.put("platformType", (Object)this.platformType);
            deviceDetails.put("dataId", (Object)this.resourceID);
            deviceDetails.put("dataValue", (Object)this.deviceName);
            supportFileCreation.removeDeviceFromList(deviceDetails);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception   ", e2);
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final Exception ex2) {
                    ex2.fillInStackTrace();
                }
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final Exception ex3) {
                    ex3.fillInStackTrace();
                }
            }
        }
    }
    
    private String removeInvalidCharactersInFileName(final String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
    
    private InputStream validateRequestStreamWithTika(final InputStream requestStream, final String fileName) throws SyMException {
        ByteArrayInputStream fileByteStream = null;
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            IOUtils.copy(requestStream, (OutputStream)byteStream);
            fileByteStream = new ByteArrayInputStream(byteStream.toByteArray());
            fileByteStream.mark(byteStream.size());
            final Tika tika = new Tika();
            final String streamContentType = tika.detect((InputStream)fileByteStream);
            final String fileNameContentType = tika.detect(fileName);
            if (!streamContentType.equals(fileNameContentType) || !streamContentType.equals("application/zip")) {
                this.logger.log(Level.SEVERE, "Detected content types of LogUploaderFile - fileNameContentType - {0}, streamContentType - {1}", new Object[] { fileNameContentType, streamContentType });
                throw new SyMException(140001, "File content type is not of zip type", (Throwable)null);
            }
            fileByteStream.reset();
        }
        catch (final IOException e) {
            this.logger.log(Level.SEVERE, "Exception while detecting content type for log upload file via Tika", e);
            try {
                if (requestStream != null) {
                    requestStream.close();
                }
                if (byteStream != null) {
                    byteStream.close();
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
                if (byteStream != null) {
                    byteStream.close();
                }
            }
            catch (final IOException e2) {
                this.logger.log(Level.SEVERE, "Exception while closing input stream", e2);
            }
        }
        return fileByteStream;
    }
}
