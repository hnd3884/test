package com.me.mdm.server.util;

import java.util.Iterator;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.logging.Level;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.UUID;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class UploadUtil
{
    private static Logger logger;
    
    public String getDestinationPath(final String fileType) throws Exception {
        String DestPath = "";
        if (fileType.equals("IOS_ENTERPRISE_APP") || fileType.equals("ANDROID_ENTERPRISE_APP") || fileType.equals("WINDOWS_XAP_ENTERPRISE_APP") || fileType.equals("WINDOWS_MSI_ENTERPRISE_APP")) {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            DestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(customerId);
        }
        else {
            final UUID randomid = UUID.randomUUID();
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            DestPath = serverHome + File.separator + "temp_downloads" + File.separator + randomid;
        }
        return DestPath;
    }
    
    public int getStatus(final Long uploadId) {
        int l;
        try {
            final JSONObject json = this.getUploadCache(uploadId + "");
            l = json.getInt("status");
        }
        catch (final Exception e) {
            l = -1;
            UploadUtil.logger.log(Level.SEVERE, "UploadID does not Exist in cache. File may not be uploaded properly.");
        }
        return l;
    }
    
    public void putUploadCache(final String uploadId, final JSONObject json) throws Exception {
        final HashMap<String, Object> additionalParamMap = new HashMap<String, Object>();
        additionalParamMap.put("upload-id", uploadId);
        json.put("expiryTime", System.currentTimeMillis() + 300000L);
        additionalParamMap.put("upload-id-json", json.toString());
        final CacheAccessAPI.CacheSynchronisedUpdateHandler cacheLockHandler = (CacheAccessAPI.CacheSynchronisedUpdateHandler)new CacheAccessAPI.CacheSynchronisedUpdateHandler() {
            public Object processMessage(final Object jsonObj, final HashMap<String, Object> additionalParams) {
                try {
                    JSONObject jsonCache;
                    if (jsonObj != null && !jsonObj.toString().equalsIgnoreCase("") && !jsonObj.toString().equalsIgnoreCase("null")) {
                        jsonCache = new JSONObject(jsonObj + "");
                    }
                    else {
                        jsonCache = new JSONObject();
                    }
                    jsonCache.put("" + additionalParams.get("upload-id"), (Object)("" + additionalParams.get("upload-id-json")));
                    return jsonCache + "";
                }
                catch (final JSONException ex) {
                    UploadUtil.logger.log(Level.SEVERE, "Exception occurred while updating upload cache", (Throwable)ex);
                    return jsonObj;
                }
            }
        };
        ApiFactoryProvider.getCacheAccessAPI().putCacheWithLock("uploadCache", cacheLockHandler, 2, (HashMap)additionalParamMap, (Integer)null, (Integer)null);
    }
    
    public JSONObject getUploadCache(final String uploadId) throws NullPointerException, JSONException {
        final CacheAccessAPI cacheAccessAPI = ApiFactoryProvider.getCacheAccessAPI();
        final String JSONCacheString = cacheAccessAPI.getCache("uploadCache", 2) + "";
        if (JSONCacheString == null || JSONCacheString.equalsIgnoreCase("") || !JSONCacheString.startsWith("{")) {
            cacheAccessAPI.putCache("uploadCache", (Object)new JSONObject(), 2);
            throw new NullPointerException();
        }
        final JSONObject jsonCache = new JSONObject(JSONCacheString);
        final String uploadCache = "" + jsonCache.get(uploadId);
        if (uploadCache != null) {
            final JSONObject uploadJSONCache = new JSONObject(uploadCache);
            return uploadJSONCache;
        }
        throw new NullPointerException();
    }
    
    public void deleteFile(final String filePath) throws Exception {
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        if (fileAccessAPI.isFileExists(filePath)) {
            fileAccessAPI.deleteFile(filePath);
        }
    }
    
    public void uploadFailed(final String uploadId) throws Exception {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "UPLOAD_MODULE", "UPLOAD_FAILURE_MDMSERVLET");
        final JSONObject json = this.getUploadCache(uploadId + "");
        json.put("status", 1);
        this.putUploadCache(uploadId + "", json);
    }
    
    public JSONObject convertKeysinJSONToUppercase(final JSONObject json) throws JSONException {
        final HashMap<String, String> documentUploadKeyString = new HashMap<String, String>();
        documentUploadKeyString.put("DOC-ID", "DOC_ID");
        documentUploadKeyString.put("DOC-NAME", "DOC_NAME");
        documentUploadKeyString.put("DOWNLOAD-DOC", "DOWNLOAD_DOC");
        documentUploadKeyString.put("REPOSITORY-TYPE", "REPOSITORY_TYPE");
        documentUploadKeyString.put("TAG-NAME", "TAG_NAME");
        final Iterator it = json.keys();
        final JSONObject jobj = new JSONObject();
        String key = "";
        String finalkey = "";
        while (it.hasNext()) {
            key = it.next();
            if (documentUploadKeyString.containsKey(key.toUpperCase())) {
                finalkey = documentUploadKeyString.get(key.toUpperCase());
            }
            else {
                finalkey = key;
            }
            jobj.put(finalkey.toUpperCase(), json.get(key));
        }
        return jobj;
    }
    
    public void deleteTemporaryFolder() throws Exception {
        final CacheAccessAPI cacheAccessAPI = ApiFactoryProvider.getCacheAccessAPI();
        final String uploadCacheString = cacheAccessAPI.getCache("uploadCache", 2) + "";
        if (uploadCacheString != null) {
            final JSONObject uploadCache = new JSONObject(uploadCacheString);
            String key = "";
            String tempPath = "";
            final Iterator jsonList = uploadCache.keys();
            while (jsonList.hasNext()) {
                key = jsonList.next() + "";
                if (uploadCache.get(key) != null) {
                    UploadUtil.logger.log(Level.FINE, "Deleting file while Server stop {0}", key);
                    final JSONObject json = new JSONObject(uploadCache.get(key) + "");
                    tempPath = json.get("temp_path") + "";
                    this.deleteFileifExists(tempPath);
                }
            }
        }
    }
    
    private void deleteFileifExists(final String path) {
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        try {
            if (fileAccessAPI.isFileExists(path)) {
                fileAccessAPI.deleteFile(path);
            }
        }
        catch (final Exception e) {
            UploadUtil.logger.log(Level.SEVERE, "Couldn''t delete file from path -----  {0}", path);
        }
    }
    
    static {
        UploadUtil.logger = Logger.getLogger("MDMLogger");
    }
}
