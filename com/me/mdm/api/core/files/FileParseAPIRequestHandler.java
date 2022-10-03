package com.me.mdm.api.core.files;

import java.io.InputStream;
import java.util.logging.Level;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.upload.FileUploadManager;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class FileParseAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public FileParseAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMApiLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject message = apiRequest.toJSONObject();
            final Long fileId = APIUtil.getResourceID(message, "file_id");
            new FileUploadManager();
            final JSONObject fileJSON = FileUploadManager.getFileDetails(fileId, APIUtil.getCustomerID(message));
            final String formatType = APIUtil.getStringFilter(apiRequest.toJSONObject(), "format");
            if (fileJSON != null) {
                final String filePath = String.valueOf(fileJSON.get("file_path"));
                InputStream in = null;
                try {
                    in = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
                    if (formatType == null || !formatType.equalsIgnoreCase("plist")) {
                        throw new APIHTTPException("COM0014", new Object[0]);
                    }
                    final JSONObject jsonObject = new JSONObject();
                    if (String.valueOf(fileJSON.get("content_type")).equalsIgnoreCase("application/xml")) {
                        final JSONObject jsonObject2 = jsonObject;
                        final String s = "RESPONSE";
                        MDMRestAPIFactoryProvider.getAPIUtil();
                        jsonObject2.put(s, (Object)APIUtil.parseIOSAppConfig(in));
                        jsonObject.put("status", 200);
                        return jsonObject;
                    }
                    throw new APIHTTPException("COM0015", new Object[] { "File should be xml" });
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Parsing the XML File", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
