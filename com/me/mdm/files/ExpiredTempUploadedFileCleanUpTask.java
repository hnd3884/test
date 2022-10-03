package com.me.mdm.files;

import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import org.json.JSONException;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpiredTempUploadedFileCleanUpTask
{
    private Logger logger;
    
    public ExpiredTempUploadedFileCleanUpTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask() throws Exception {
        this.logger.log(Level.FINE, "Global Task For deleting Temp Uploaded File");
        final CacheAccessAPI.CacheSynchronisedUpdateHandler cacheLockHandler = (CacheAccessAPI.CacheSynchronisedUpdateHandler)new CacheAccessAPI.CacheSynchronisedUpdateHandler() {
            public Object processMessage(final Object uploadCacheObj, final HashMap<String, Object> additionalParams) {
                try {
                    final String uploadCacheString = uploadCacheObj + "";
                    if (uploadCacheString != null && !uploadCacheString.equalsIgnoreCase("") && !uploadCacheString.equalsIgnoreCase("null")) {
                        final JSONObject uploadCache = new JSONObject(uploadCacheString);
                        String key = "";
                        String tempPath = "";
                        final Long currentMillis = System.currentTimeMillis();
                        final Iterator<String> jsonKey = uploadCache.keys();
                        final ArrayList<String> keyList = new ArrayList<String>();
                        int status = -1;
                        while (jsonKey.hasNext()) {
                            try {
                                key = jsonKey.next();
                                if (uploadCache.get(key) == null) {
                                    continue;
                                }
                                ExpiredTempUploadedFileCleanUpTask.this.logger.log(Level.FINE, "Global Task Deleting for upload id {0}", key);
                                final JSONObject json = new JSONObject(uploadCache.get(key) + "");
                                tempPath = json.get("temp_path") + "";
                                status = json.getInt("status");
                                if (status == 2 || status == 1) {
                                    if (json.has("isTempPath") && !String.valueOf(json.get("isTempPath")).equalsIgnoreCase("false")) {
                                        ExpiredTempUploadedFileCleanUpTask.this.deleteFileifExists(tempPath);
                                    }
                                    else if (!json.has("isTempPath")) {
                                        ExpiredTempUploadedFileCleanUpTask.this.deleteFileifExists(tempPath);
                                    }
                                    keyList.add(key);
                                }
                                else {
                                    if (status != 0 || currentMillis + 30000L <= json.getLong("uploadStartTime")) {
                                        continue;
                                    }
                                    ExpiredTempUploadedFileCleanUpTask.this.deleteFileifExists(tempPath);
                                    keyList.add(key);
                                }
                            }
                            catch (final JSONException ex) {
                                ExpiredTempUploadedFileCleanUpTask.this.logger.log(Level.SEVERE, ex.getMessage() + "\n\nException in ExpiredTempUploadedFileCleanUpTask  -- for upload key  " + key + "\n", (Throwable)ex);
                            }
                        }
                        String keyString = "";
                        final Iterator<String> keyIterator = keyList.iterator();
                        while (keyIterator.hasNext()) {
                            keyString = keyIterator.next();
                            uploadCache.remove(keyString);
                        }
                        return uploadCache.toString();
                    }
                }
                catch (final JSONException ex2) {
                    ExpiredTempUploadedFileCleanUpTask.this.logger.log(Level.SEVERE, "Exception in getting the cache as JSON --- ExpiredTempUploadedFileCleanUpTask --- Global Scheduler \n", (Throwable)ex2);
                }
                return uploadCacheObj + "";
            }
        };
        ApiFactoryProvider.getCacheAccessAPI().putCacheWithLock("uploadCache", cacheLockHandler, 2, (HashMap)null, (Integer)null, (Integer)null);
    }
    
    private void deleteFileifExists(final String path) {
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        try {
            if (fileAccessAPI.isFileExists(path)) {
                fileAccessAPI.deleteFile(path);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "GlobalScheduler : Couldn''t delete file from DFS path -----  {0}", path);
        }
    }
}
