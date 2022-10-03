package com.me.mdm.api.core.files;

import com.me.mdm.api.APIUtil;
import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.net.URLDecoder;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;

public class FileWrapper
{
    public static JSONObject getFileDetails(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject requestJSON = apiRequest.toJSONObject();
        final StringBuilder errorMsg = null;
        String contentDispos = null;
        try {
            contentDispos = apiRequest.getHeader("content-disposition");
            contentDispos = URLDecoder.decode(contentDispos, "UTF-8");
        }
        catch (final JSONException | UnsupportedEncodingException e) {
            throw new APIHTTPException("COM0011", new Object[] { "Content-Disposition" });
        }
        String fileName = null;
        if (contentDispos != null) {
            if (contentDispos.contains("\"")) {
                fileName = contentDispos.substring(contentDispos.indexOf("filename=") + "filename=\"".length(), contentDispos.lastIndexOf("\"") + 1);
            }
            else {
                fileName = contentDispos.substring(contentDispos.indexOf("filename=") + "filename=".length());
            }
        }
        if (fileName == null || !APIUtil.isValidFileName(fileName)) {
            throw new APIHTTPException("FIL0001", new Object[] { "Invalid File Name." });
        }
        String contentType = null;
        try {
            contentType = apiRequest.getHeader("content-type");
        }
        catch (final JSONException e2) {
            throw new APIHTTPException("COM0011", new Object[] { "Content-Type" });
        }
        String contentLength = null;
        try {
            contentLength = apiRequest.getHeader("content-length");
        }
        catch (final JSONException e3) {
            throw new APIHTTPException("COM0011", new Object[] { "Content-Length" });
        }
        if (Integer.valueOf(contentLength) == 0) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
        try {
            requestJSON.put("content_type", (Object)contentType);
            requestJSON.put("file_name", (Object)fileName);
            if (contentLength != null) {
                requestJSON.put("content_length", (Object)contentLength);
            }
        }
        catch (final JSONException e3) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return requestJSON;
    }
    
    private boolean isValidFileContent(final String tikaContentType, final String strContentType, final String providedContentType) {
        return tikaContentType.equalsIgnoreCase(providedContentType) && (strContentType.equalsIgnoreCase(providedContentType) || strContentType.equalsIgnoreCase("application/octet-stream"));
    }
    
    private boolean allowTikaUndetectedContect(final String fileName, final String contentType) {
        return (fileName.contains(".crt") && contentType.equalsIgnoreCase("text/plain")) || (fileName.contains(".key") && contentType.equalsIgnoreCase("text/plain"));
    }
}
