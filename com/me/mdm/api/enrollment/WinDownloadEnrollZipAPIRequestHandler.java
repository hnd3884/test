package com.me.mdm.api.enrollment;

import java.io.InputStream;
import java.io.OutputStream;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.apache.commons.codec.binary.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class WinDownloadEnrollZipAPIRequestHandler extends ApiRequestHandler
{
    AdminEnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public WinDownloadEnrollZipAPIRequestHandler() {
        this.enrollmentFacade = new AdminEnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final OutputStream os = null;
        final JSONObject responseJSON = new JSONObject();
        try {
            final String fileName = new AdminEnrollmentFacade().getDownloadFile(apiRequest.toJSONObject());
            if (fileName == null) {
                this.logger.info("Requested File Not Found in the Directory");
                throw new IOException("Requested File " + fileName + " Not Found");
            }
            final InputStream is = new FileInputStream(fileName);
            final File file = new File(fileName);
            final long length = file.length();
            final byte[] bytes = new byte[(int)length];
            int offset = 0;
            for (int numRead = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {}
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            is.close();
            responseJSON.put("status", 200);
            final byte[] encoded = Base64.encodeBase64(bytes);
            final String encodedString = new String(encoded);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("file_contents", (Object)encodedString);
            responseJSON.put("RESPONSE", (Object)jsonObject);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in doGet ", e);
            throw new APIHTTPException("COM0009", new Object[0]);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in doGet ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return responseJSON;
    }
}
