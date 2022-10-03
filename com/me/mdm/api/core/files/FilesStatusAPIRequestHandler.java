package com.me.mdm.api.core.files;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.files.upload.FileUploadManager;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class FilesStatusAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject requestJson = apiRequest.toJSONObject();
            final Long fileId = APIUtil.getResourceID(requestJson, "file_id");
            MDMApiFactoryProvider.getUploadDownloadAPI().handleUploadTimeOut(fileId);
            responseDetails.put("RESPONSE", (Object)new FileUploadManager().getFileStatus(fileId, APIUtil.getCustomerID(requestJson)));
            return responseDetails;
        }
        catch (final Exception ex) {
            Logger.getLogger(FilesStatusAPIRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
