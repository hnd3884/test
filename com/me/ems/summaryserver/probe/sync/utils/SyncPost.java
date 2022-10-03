package com.me.ems.summaryserver.probe.sync.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.FileUtils;
import java.util.Base64;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.summaryserver.probe.util.ProbeAPIRedirectHandler;
import java.util.Properties;
import org.json.JSONObject;
import com.me.ems.summaryserver.probe.sync.factory.ProbeSyncAPI;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.ems.summaryserver.probe.sync.SyncData;
import java.util.logging.Logger;

public class SyncPost
{
    private static Logger logger;
    private static String sourceClass;
    
    public boolean postSyncDataToSummaryServer(final long moduleID, final SyncData syncData, final boolean isLastFile) {
        final String sourceMethod = "postSyncDataToSummaryServer";
        boolean postStatus = false;
        try {
            final ProbeDetailsAPI probeDetailsAPI = ProbeMgmtFactoryProvider.getProbeDetailsAPI();
            final String baseUrl = probeDetailsAPI.getSummaryServerBaseURL();
            final ProbeSyncAPI probeSyncAPI = ProbeMgmtFactoryProvider.getProbeSyncAPI();
            final String methodType = "POST";
            final String fileName = syncData.fileName;
            File fileToPost = new File(syncData.fileLocation);
            final String syncUrlParams = this.getSyncURLParams(moduleID, syncData, isLastFile);
            final URL syncUrl = new URL(baseUrl + syncUrlParams);
            final String contentType = (syncData.fileType == 2) ? "application/json" : "text/csv";
            final HttpURLConnection urlConnection = (HttpURLConnection)probeSyncAPI.createSummaryServerConnection(syncUrl, methodType, contentType, null, true, true);
            if (syncData.isCompressed) {
                urlConnection.setRequestProperty("Content-Encoding", "gzip");
                fileToPost = this.getCompressedFile(fileToPost.getAbsolutePath());
            }
            urlConnection.setRequestProperty("Content-Length", String.valueOf(fileToPost.length()));
            final DataOutputStream dataOutStream = new DataOutputStream(urlConnection.getOutputStream());
            postStatus = this.writeFile(fileToPost.getAbsolutePath(), dataOutStream);
            SyMLogger.debug(SyncPost.logger, SyncPost.sourceClass, sourceMethod, fileName + " file dataoutputstream write status : {0}", postStatus);
            if (postStatus) {
                final int responseCode = urlConnection.getResponseCode();
                postStatus = (responseCode >= 200 && responseCode <= 210);
            }
            SyMLogger.info(SyncPost.logger, SyncPost.sourceClass, sourceMethod, fileName + " file post status : {0}", postStatus);
        }
        catch (final Exception e) {
            SyMLogger.error(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "Caught exception while postSyncData to summary server", e);
        }
        return postStatus;
    }
    
    public boolean postSyncStatusToSummaryServer(final JSONObject syncStatusJSON) {
        final String sourceMethod = "postSyncStatusToSummaryServer";
        boolean isPosted = true;
        try {
            final String actionURL = "emsapi/summaryserver/sync/updateSyncStatus";
            final String contentType = "application/summaryUpdateSyncStatus.v1+json";
            final String accept = "application/summaryUpdateSyncStatusResult.v1+json";
            final String methodType = "POST";
            final Properties apiProperties = new Properties();
            apiProperties.setProperty("url", actionURL);
            apiProperties.setProperty("content-type", contentType);
            apiProperties.setProperty("requestMethod", methodType);
            apiProperties.setProperty("accept", accept);
            final ProbeAPIRedirectHandler probeAPIRedirectHandler = new ProbeAPIRedirectHandler();
            probeAPIRedirectHandler.doAPICall(apiProperties, syncStatusJSON);
        }
        catch (final Exception e) {
            SyMLogger.error(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "Caught exception while postSyncStatusToSummaryServer", e);
            isPosted = false;
        }
        return isPosted;
    }
    
    public Map<String, Object> fetchSyncStatusFromSummaryServer(final long probeID, final long moduleID, final long syncTime) {
        final String sourceMethod = "postSyncStatusToSummaryServer";
        Map<String, Object> syncStatusMap = new HashMap<String, Object>();
        try {
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final String moduleName = syncModuleMetaDAOUtil.getModuleName(moduleID);
            final String queryParam = "?probe_id=" + probeID + "&" + "sync_module_name" + "=" + moduleName + "&" + "sync_time" + "=" + syncTime;
            final String actionURL = "emsapi/summaryserver/sync/fetchSyncStatus" + queryParam;
            final String methodType = "GET";
            final Properties apiProperties = new Properties();
            apiProperties.setProperty("url", actionURL);
            apiProperties.setProperty("requestMethod", methodType);
            final ProbeAPIRedirectHandler probeAPIRedirectHandler = new ProbeAPIRedirectHandler();
            final String response = probeAPIRedirectHandler.doAPICall(apiProperties, null);
            syncStatusMap = probeAPIRedirectHandler.extractResponse(response, syncStatusMap);
        }
        catch (final Exception e) {
            SyMLogger.error(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "Caught exception while postSyncStatusToSummaryServer", e);
        }
        return syncStatusMap;
    }
    
    private String getSyncURLParams(final long moduleId, final SyncData syncData, final boolean isLastFile) throws Exception {
        final String sourceMethod = "getSyncURLParams";
        String urlParams = "";
        final ProbeSyncModuleMetaDAOUtil probeSyncModuleMetaDAOUtil = new ProbeSyncModuleMetaDAOUtil();
        final String moduleApi = probeSyncModuleMetaDAOUtil.getModuleSyncUrlPath(moduleId);
        if (moduleApi == null) {
            SyMLogger.warning(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "URL Construction error for moduleId: {0}", moduleId);
            throw new Exception("Unknown module");
        }
        String params = "?syncTime=" + syncData.syncTime + "&fileName=" + syncData.fileName + "&tableName=" + syncData.tableName;
        params += (syncData.isCompressed ? "&isCompressed=true" : "");
        params += (syncData.priority ? "&priority=true" : "");
        params += (isLastFile ? "&isLastFile=true" : "");
        urlParams = urlParams + moduleApi + params;
        return urlParams;
    }
    
    private File getCompressedFile(final String filePath) {
        final String sourceMethod = "getCompressedFile";
        final byte[] buffer = new byte[1024];
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        final StringBuilder contentBuilder = new StringBuilder();
        File compressedFile = null;
        try {
            final File file = new File(filePath);
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            while (true) {
                synchronized (buffer) {
                    final String sCurrentLine;
                    if ((sCurrentLine = bufferedReader.readLine()) == null) {
                        break;
                    }
                    contentBuilder.append(sCurrentLine);
                    contentBuilder.append("\n");
                }
            }
            final byte[] input = contentBuilder.toString().getBytes(StandardCharsets.UTF_8);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
            final DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(baos);
            for (final byte b : input) {
                deflaterOutputStream.write(b);
            }
            deflaterOutputStream.close();
            final String str = Base64.getEncoder().encodeToString(baos.toByteArray());
            final String compressedFileName = file.getName() + ".gz";
            compressedFile = new File(file.getParentFile() + File.separator + compressedFileName);
            FileUtils.writeStringToFile(compressedFile, str, "UTF-8");
            SyMLogger.debug(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "Original File Size : {0}, Compressed File size : {1}", new Object[] { file.length(), compressedFile.length() });
        }
        catch (final UnsupportedEncodingException e) {
            SyMLogger.error(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "UnSupportedEncodingException :", e);
        }
        catch (final IOException e2) {
            SyMLogger.error(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "IOException :", e2);
        }
        finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (final Exception e3) {
                SyMLogger.error(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "Exception while closing reader ", e3);
            }
        }
        return compressedFile;
    }
    
    private boolean writeFile(final String filePath, final DataOutputStream dataOutStream) {
        final String sourceMethod = "writeFile";
        final byte[] buffer = new byte[1024];
        FileInputStream fileInStream = null;
        try {
            fileInStream = new FileInputStream(filePath);
            while (true) {
                synchronized (buffer) {
                    final int amountRead = fileInStream.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    dataOutStream.write(buffer, 0, amountRead);
                }
            }
            dataOutStream.flush();
            return true;
        }
        catch (final Exception e) {
            SyMLogger.error(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "Exception in writing file content", e);
            return false;
        }
        finally {
            try {
                if (fileInStream != null) {
                    fileInStream.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(SyncPost.logger, SyncPost.sourceClass, sourceMethod, "Exception in finally block", e2);
            }
        }
    }
    
    static {
        SyncPost.logger = Logger.getLogger("ProbeDataPostLogger");
        SyncPost.sourceClass = "SyncPost";
    }
}
