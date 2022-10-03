package com.me.mdm.api.core.files;

import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.upload.FileUploadManager;
import com.me.mdm.api.APIUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.files.FileFacade;
import java.io.InputStream;
import java.io.FileInputStream;
import mdm.mdm.source.server.com.me.mdm.api.enrollment.apple.apns.file.ApnsFileFacade;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class FilesAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final File file = new File(apiRequest.tempFilePath);
        try {
            MDMApiFactoryProvider.getUploadDownloadAPI().getAntivirusScanResult(file, apiRequest.httpServletRequest);
            if (apiRequest.headers.has("upload-from") && (apiRequest.headers.getString("upload-from").equalsIgnoreCase("APPS") || apiRequest.headers.getString("upload-from").equalsIgnoreCase("CONTENT_MANAGEMENT"))) {
                final JSONObject fileStatus = MDMApiFactoryProvider.getUploadDownloadAPI().isSpaceExists(apiRequest.httpServletRequest.getContentLength(), apiRequest.headers.getString("upload-from"));
                if (fileStatus.has("isSpaceExists") && !fileStatus.getBoolean("isSpaceExists")) {
                    final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
                    MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "UPLOAD_MODULE", "UPLOAD_FAIL_FILE_SIZE_EXCEEDED_MDMSERVLET");
                    throw new APIHTTPException("FIL0001", new Object[] { fileStatus.optString("error", I18N.getMsg("dc.mdm.upload.filesize.exceeded", new Object[0])) });
                }
            }
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject requestJSON = FileWrapper.getFileDetails(apiRequest);
            if (apiRequest.tempFilePath == null) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            JSONObject response = null;
            if (apiRequest.pathInfo.equals("/upload_apns_pem_temp_file")) {
                final ApnsFileFacade fileInstance = new ApnsFileFacade();
                fileInstance.verifyFileContentType(requestJSON, new File(apiRequest.tempFilePath));
                response = fileInstance.addFile(requestJSON, new FileInputStream(apiRequest.tempFilePath));
            }
            else {
                final FileFacade fileInstance2 = new FileFacade();
                fileInstance2.verifyFileContentType(requestJSON, new File(apiRequest.tempFilePath));
                response = fileInstance2.addFile(requestJSON, new FileInputStream(apiRequest.tempFilePath));
            }
            response.remove("msg_header");
            response.remove("file_path");
            responseDetails.put("RESPONSE", (Object)response);
            return responseDetails;
        }
        catch (final Exception ex) {
            Logger.getLogger(FilesAPIRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (file != null) {
                final File parentFolder = new File(file.getParent());
                if (file.exists()) {
                    if (file.delete()) {
                        this.logger.log(Level.INFO, "Temporary file deleted successfully");
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Temporary file uploaded for /files endpoint not deleted");
                    }
                }
                if (parentFolder.delete()) {
                    if (file.delete()) {
                        this.logger.log(Level.INFO, "Temporary folder deleted successfully");
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Temporary folder created by /files endpoint not deleted");
                    }
                }
            }
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject message = apiRequest.toJSONObject();
            final Long fileId = APIUtil.getResourceID(message, "file_id");
            new FileUploadManager();
            final JSONObject fileJSON = FileUploadManager.getFileDetails(fileId, APIUtil.getCustomerID(message));
            if (fileJSON == null) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final String filePath = String.valueOf(fileJSON.get("file_path"));
            final HttpServletResponse response = apiRequest.httpServletResponse;
            response.setContentType(String.valueOf(fileJSON.get("content_type")));
            response.setHeader("Content-Disposition", "inline");
            InputStream in = null;
            OutputStream os = null;
            try {
                in = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
                int read = 0;
                final byte[] bytes = new byte[2048];
                os = (OutputStream)response.getOutputStream();
                while ((read = in.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }
                os.flush();
            }
            catch (final Exception e) {
                throw e;
            }
            finally {
                if (os != null) {
                    os.close();
                }
                if (in != null) {
                    in.close();
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(FilesAPIRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
}
