package com.me.mdm.onpremise.util;

import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.core.auth.APIKey;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.factory.UploadDownloadAPI;

public class UploadDownloadApiImpl implements UploadDownloadAPI
{
    private Logger logger;
    private static String uploadUrl;
    private static JSONObject json;
    
    public UploadDownloadApiImpl() {
        this.logger = Logger.getLogger(UploadDownloadAPI.class.getName());
    }
    
    public JSONObject getUploadData() throws Exception {
        if (UploadDownloadApiImpl.json == null) {
            UploadDownloadApiImpl.json = new JSONObject().put("uploadurl", (Object)UploadDownloadApiImpl.uploadUrl);
        }
        return UploadDownloadApiImpl.json;
    }
    
    public void writeFileInAppServer(final String fromPath, final String toServerPath) throws Exception {
    }
    
    public void deleteDirectoryInAppServer(final String directoryPath) throws Exception {
    }
    
    public JSONObject isSpaceExists(final int fileSize, final String fileType) throws Exception {
        final JSONObject jObj = new JSONObject();
        jObj.put("isSpaceExists", true);
        return jObj;
    }
    
    public String constructFileURLwithDownloadServer(final HashMap hMap) throws Exception {
        return "";
    }
    
    public String replaceDeviceAPIKeyPlaceHolderForUDWindows(final String placeHolderURL, final APIKey key, final boolean encodeURL) throws Exception {
        return MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(placeHolderURL, key, encodeURL);
    }
    
    static {
        UploadDownloadApiImpl.uploadUrl = "/UploadServlet.do?actionToCall=uploadSingleFile";
        UploadDownloadApiImpl.json = null;
    }
}
