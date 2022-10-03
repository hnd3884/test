package com.me.devicemanagement.onpremise.server.metrack;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.dms.DMSDownloadUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Properties;
import java.io.File;
import java.util.logging.Logger;

public class METrackDownloadHandlerImpl implements METrackDownloadHandler
{
    private static Logger logger;
    private static String sourceClass;
    
    @Override
    public void updateMETrackConfiguration() {
        try {
            final String metrackingConfDir = METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metracking.conf";
            final Properties properties = new Properties();
            final Properties metrackingConf = FileAccessUtil.readProperties(metrackingConfDir);
            if (metrackingConf.containsKey("update_metrack_config_url") && !"".equals(((Hashtable<K, Object>)metrackingConf).get("update_metrack_config_url"))) {
                final String downloadUrl = ((Hashtable<K, Object>)metrackingConf).get("update_metrack_config_url").toString();
                final DownloadStatus downloadStatus = this.downloadFile(downloadUrl);
                ((Hashtable<String, String>)properties).put("LastDownloadStatus", String.valueOf(downloadStatus.getStatus()));
                if (0 == downloadStatus.getStatus()) {
                    ((Hashtable<String, String>)properties).put("metrack_config_last_modified_since", downloadStatus.getLastModifiedTime());
                    final String downloadPath = METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "METrackConfig.json";
                    SyMLogger.info(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "trackingUpdater", "download success : " + downloadStatus.getStatus());
                    this.updateDownloadFile(downloadPath, properties);
                }
                else if (10010 == downloadStatus.getStatus()) {
                    FileAccessUtil.storeProperties(properties, METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metrack_config.properties", true);
                    SyMLogger.info(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "trackingUpdater", "File Not Modified : " + downloadStatus.getStatus());
                }
                else {
                    FileAccessUtil.storeProperties(properties, METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metrack_config.properties", false);
                    SyMLogger.warning(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "trackingUpdater", "Download Failed : " + downloadStatus.getErrorMessage());
                }
            }
            else {
                FileAccessUtil.storeProperties(properties, METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metrack_config.properties", false);
                SyMLogger.warning(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "trackingUpdater", "update_metrack_config_url not found in metracking.properties");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "trackingUpdater", "Exception occurred : ", (Throwable)e);
        }
    }
    
    private DownloadStatus downloadFile(final String downloadUrl) {
        try {
            final String metrackDir = METrackerDiffUtil.getInstance().getMETrackDir();
            final String destinationFile = metrackDir + File.separator + "METrackConfig.json";
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(metrackDir)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(metrackDir);
            }
            final Properties headers = new Properties();
            final String lastModifiedSince = this.getLastModifiedSince();
            if (lastModifiedSince != null) {
                ((Hashtable<String, String>)headers).put("If-Modified-Since", lastModifiedSince);
            }
            SyMLogger.info(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "downloadFile", "METrackConfig.json headers : " + headers);
            final boolean enableChecksumValidation = Boolean.parseBoolean(ProductUrlLoader.getInstance().getValue("enableChecksumValidation", Boolean.FALSE.toString()));
            if (enableChecksumValidation) {
                return DMSDownloadUtil.getInstance().downloadRequestedFileForComponent("Server", "METrackConfig", destinationFile, (Properties)null, headers).getDownloadStatus();
            }
            return DownloadManager.getInstance().downloadFile(downloadUrl, destinationFile, (Properties)null, headers, new SSLValidationType[0]);
        }
        catch (final Exception e) {
            SyMLogger.error(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "downloadFile", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    private String getLastModifiedSince() {
        try {
            final String metrackConfigProps = METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metrack_config.properties";
            final Properties properties = FileAccessUtil.readProperties(metrackConfigProps);
            if (properties.containsKey("metrack_config_last_modified_since")) {
                return ((Hashtable<K, Object>)properties).get("metrack_config_last_modified_since").toString();
            }
        }
        catch (final NumberFormatException e) {
            SyMLogger.error(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "getLastModifiedSince", "NumberFormatException occurred : ", (Throwable)e);
        }
        catch (final Exception e2) {
            SyMLogger.error(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "getLastModifiedSince", "Exception occurred : ", (Throwable)e2);
        }
        return null;
    }
    
    private void updateDownloadFile(final String fileName, final Properties properties) {
        InputStreamReader inputStreamReader = null;
        try {
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(fileName)) {
                inputStreamReader = new InputStreamReader(ApiFactoryProvider.getFileAccessAPI().readFile(fileName));
                final JSONObject jsonObject = (JSONObject)new JSONParser().parse((Reader)inputStreamReader);
                final Set<String> keySet = jsonObject.keySet();
                for (final String key : keySet) {
                    ((Hashtable<String, String>)properties).put(key, jsonObject.get((Object)key).toString());
                }
                FileAccessUtil.storeProperties(properties, METrackerDiffUtil.getInstance().getMETrackDir() + File.separator + "metrack_config.properties", false);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "updater", "Exception occurred : ", (Throwable)e);
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "updater", "Unable to close  inputStreamReader : ", (Throwable)e);
            }
        }
        finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(METrackDownloadHandlerImpl.logger, METrackDownloadHandlerImpl.sourceClass, "updater", "Unable to close  inputStreamReader : ", (Throwable)e2);
            }
        }
    }
    
    static {
        METrackDownloadHandlerImpl.logger = Logger.getLogger("METrackLog");
        METrackDownloadHandlerImpl.sourceClass = "meTrackerConfigUpdateTask";
    }
}
