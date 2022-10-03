package com.me.devicemanagement.onpremise.server.silentupdate.ondemand;

import java.util.Hashtable;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import org.json.simple.JSONValue;
import java.util.List;
import com.zoho.framework.utils.archive.SevenZipUtils;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import java.io.File;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import com.me.devicemanagement.framework.server.certificate.verifier.CRTThumbPrintVerifier;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class SilentUpdateController
{
    private static Logger logger;
    private static String sourceClass;
    
    public JSONObject exportTaskDetailsFromZC() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final Properties properties = new Properties();
            final String zcApplicationName = SilentUpdateHelper.getInstance().productSpecificProps("ApplicationName");
            final String zcViewName = SilentUpdateHelper.getInstance().productSpecificProps("ViewName");
            final String zcKey = SilentUpdateHelper.getInstance().productSpecificProps("ZC_Key");
            final String zcOwnerName = SilentUpdateHelper.getInstance().productSpecificProps("OwnerName");
            final String zcCriteriaColumnName = SilentUpdateHelper.getInstance().productSpecificProps("CriteriaColumnName");
            if (zcApplicationName != null && zcViewName != null && zcKey != null && zcOwnerName != null && zcCriteriaColumnName != null) {
                ((Hashtable<String, String>)properties).put("ApplicationName", zcApplicationName);
                ((Hashtable<String, String>)properties).put("ViewName", zcViewName);
                ((Hashtable<String, String>)properties).put("ZC_Key", zcKey);
                ((Hashtable<String, String>)properties).put("OwnerName", zcOwnerName);
                ((Hashtable<String, String>)properties).put("CriteriaColumnName", zcCriteriaColumnName);
                final String zcCriteriaTime = SilentUpdateHelper.getInstance().customerSpecificProps("zc_last_taskdetails_fetch_time");
                if (zcCriteriaTime != null && !zcCriteriaTime.equalsIgnoreCase("")) {
                    ((Hashtable<String, String>)properties).put("zc_last_taskdetails_fetch_time", zcCriteriaTime);
                }
                ((Hashtable<String, String>)properties).put("BufferType", "json");
                return this.downloadJsonFromCreator(properties);
            }
            jsonObject.put((Object)"0InvSet:", (Object)"InvSet:zc");
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateController.logger, SilentUpdateController.sourceClass, "exportTaskDetailsFromZC", "Exception occurred : ", (Throwable)e);
            jsonObject.put((Object)"0ERROR:", (Object)String.valueOf("ERROR:" + e.getMessage()));
        }
        return jsonObject;
    }
    
    protected JSONObject exportJsonFromCRS() {
        final JSONObject jsonObject = new JSONObject();
        try {
            String crsExportUrl = SilentUpdateHelper.getInstance().productSpecificProps("crs_export_url");
            if (crsExportUrl != null && !crsExportUrl.equalsIgnoreCase("")) {
                crsExportUrl = ((SilentUpdateHelper.getInstance().getCurrentBuildNumber() == null) ? crsExportUrl : crsExportUrl.replace("<build_number>", String.valueOf(SilentUpdateHelper.getInstance().getCurrentBuildNumber())));
                crsExportUrl = ((SilentUpdateHelper.getInstance().getProductCode() == null) ? crsExportUrl : crsExportUrl.replace("<product_code>", String.valueOf(SilentUpdateHelper.getInstance().getProductCode())));
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "exportJsonFromCRS", "Meta located URL : " + crsExportUrl);
                final String destinationFile = SilentUpdateHelper.getInstance().getSilentUpdateMetaLocalPath();
                final Properties headers = new Properties();
                final String lastModifiedSince = SilentUpdateHelper.getInstance().customerSpecificProps("crs_export_last_modified_since");
                if (lastModifiedSince != null && !lastModifiedSince.equalsIgnoreCase("")) {
                    ((Hashtable<String, String>)headers).put("If-Modified-Since", lastModifiedSince);
                }
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "exportJsonFromCRS", "Headers for crs export : " + headers);
                final DownloadStatus downloadStatus = DownloadManager.getInstance().downloadFile(crsExportUrl, destinationFile, (Properties)null, headers, new SSLValidationType[] { SSLValidationType.DEFAULT_SSL_VALIDATION });
                if (downloadStatus.getStatus() == 0) {
                    if (ApiFactoryProvider.getFileAccessAPI().isFileExists(destinationFile)) {
                        final byte[] unSignedContent = new CRTThumbPrintVerifier().verifyAndUNSignFileContentWithMEMetaCrt(SilentUpdateHelper.getInstance().getSilentUpdateMetaLocalPath());
                        if (unSignedContent != null) {
                            final JSONObject response = (JSONObject)new JSONParser().parse(new String(unSignedContent, "UTF-8"));
                            final Object object = response.get(response.keySet().iterator().next());
                            if (object instanceof JSONArray) {
                                jsonObject.put((Object)downloadStatus.getLastModifiedTime(), (Object)object);
                            }
                            else {
                                jsonObject.put((Object)"NotAJsonArray", (Object)"crs:NotAJsonArray");
                            }
                        }
                        else {
                            jsonObject.put((Object)"SignVerificationFailed", (Object)"crs:SignVerificationFailed");
                        }
                    }
                    else {
                        jsonObject.put((Object)"FileNotFound", (Object)"crs:DownFileNotFound");
                    }
                }
                else {
                    if (downloadStatus.getStatus() == 10010) {
                        return jsonObject;
                    }
                    jsonObject.put((Object)"CRSDownloadFailed", (Object)("crs:" + downloadStatus.getStatus()));
                }
            }
            else {
                jsonObject.put((Object)"0InvSet:", (Object)"InvSet:crs");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateController.logger, SilentUpdateController.sourceClass, "exportJsonFromCRS", "Exception occurred : ", (Throwable)e);
            jsonObject.put((Object)"0ERROR:", (Object)String.valueOf("ERROR:" + e.getMessage()));
        }
        return jsonObject;
    }
    
    protected Row downloadQPPM(final Row entnRow, final String theQPPMUrl, final String qppmChecksum, final String qppmUniqueId) {
        try {
            final String destination = SilentUpdateHelper.getInstance().getSilentUpdateBinaryHome() + File.separator + "QPPMRepo" + File.separator + qppmUniqueId + ".qpm";
            SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadQPPM", "Start to download the QPPM.");
            SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadQPPM", "qppmChecksum : " + qppmChecksum);
            SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadQPPM", "Download url : " + theQPPMUrl);
            SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadQPPM", "Destination : " + destination);
            final DownloadStatus qppmDownloadStatus = DownloadManager.getInstance().downloadBinaryFile(theQPPMUrl, destination, qppmChecksum, new SSLValidationType[] { SSLValidationType.ENFORCE_HTTPS, SSLValidationType.DEFAULT_SSL_VALIDATION });
            if (qppmDownloadStatus.getStatus() == 0) {
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadQPPM", "QPPM has been downloaded(" + qppmDownloadStatus.getStatus() + ").");
                entnRow.set("TASK_STATUS", (Object)1);
            }
            else {
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadQPPM", "QPPM download failed(" + qppmDownloadStatus.getStatus() + ").");
                METrackerUtil.addOrUpdateMETrackParams("QPPMDownldFailDtls." + qppmUniqueId, String.valueOf(qppmDownloadStatus.getStatus()));
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadQPPM", "Exception occurred : ", (Throwable)e);
            METrackerUtil.addOrUpdateMETrackParams("QPPMDownldFailDtls." + qppmUniqueId, String.valueOf("ERROR:" + e.getMessage()));
        }
        return entnRow;
    }
    
    protected void downloadDynamicCheckerClass(final String dynamicCheckerUrl, final String dynamicCheckerChecksum, final String qppmUniqueId) {
        try {
            final String dynamicCheckerRepo = SilentUpdateHelper.getInstance().getSilentUpdateBinaryHome() + File.separator + "DynamicCheckerRepo" + File.separator + qppmUniqueId;
            Files.deleteIfExists(Paths.get(dynamicCheckerRepo + File.separator + "DynamicChecker" + File.separator + SilentUpdateDynamicChecker.class.getSimpleName() + ".class", new String[0]));
            final String destinationZip = SilentUpdateHelper.getInstance().getSilentUpdateBinaryHome() + File.separator + "Temp" + File.separator + qppmUniqueId + File.separator + "DynamicChecker.zip";
            final DownloadStatus dynamicCheckerZipDownloadStatus = DownloadManager.getInstance().downloadBinaryFile(dynamicCheckerUrl, destinationZip, dynamicCheckerChecksum, new SSLValidationType[] { SSLValidationType.ENFORCE_HTTPS, SSLValidationType.DEFAULT_SSL_VALIDATION });
            if (dynamicCheckerZipDownloadStatus.getStatus() == 0) {
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadDynamicCheckerClass", "dynamicCheckerZip has been downloaded(" + dynamicCheckerZipDownloadStatus.getStatus() + ").");
                final List<String> includeFileList = new ArrayList<String>();
                includeFileList.add("DynamicChecker" + File.separator + SilentUpdateDynamicChecker.class.getSimpleName() + ".class");
                System.setProperty("tools.7zip.win.path", System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za");
                SevenZipUtils.unZip(new File(destinationZip), new File(dynamicCheckerRepo), (List)includeFileList, (List)null);
            }
            else {
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadDynamicCheckerClass", "Dynamic checker download failed(" + dynamicCheckerZipDownloadStatus.getStatus() + ").");
                METrackerUtil.addOrUpdateMETrackParams("DynamicCheckerDownldFailDtls." + qppmUniqueId, String.valueOf(dynamicCheckerZipDownloadStatus.getStatus()));
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadDynamicCheckerClass", "Exception occurred : ", (Throwable)e);
            METrackerUtil.addOrUpdateMETrackParams("DynamicCheckerDownldFailDtls." + qppmUniqueId, String.valueOf("ERROR:" + e.getMessage()));
        }
    }
    
    public JSONObject downloadJsonFromCreator(final Properties properties) {
        final JSONObject outputJson = new JSONObject();
        final String sourceMethod = "downloadDeploymentTasDetailFromCreator";
        try {
            int fromIndex = 1;
            final int limit = properties.containsKey("ToIndex") ? Integer.valueOf(((Hashtable<K, Object>)properties).get("ToIndex").toString()) : 200;
            int failedRequestCount = 0;
            while (true) {
                final String currentFetchKey = String.valueOf(fromIndex) + "-" + String.valueOf(limit);
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, sourceMethod, "Start to fetch record from creator - limit " + currentFetchKey);
                ((Hashtable<String, Integer>)properties).put("FromIndex", fromIndex);
                ((Hashtable<String, Integer>)properties).put("ToIndex", limit);
                final DownloadStatus downloadTasksStatus = this.downloadBufferFromCreator(properties);
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, sourceMethod, "Current download status : " + downloadTasksStatus.getStatus());
                boolean currentDownloadIsSuccess = downloadTasksStatus.getStatus() == 0;
                if (currentDownloadIsSuccess) {
                    SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, sourceMethod, "Deployment tasks downloaded(" + downloadTasksStatus.getStatus() + ")");
                    final JSONObject tasksJson = (JSONObject)JSONValue.parse(downloadTasksStatus.getUrlDataBuffer());
                    if (tasksJson == null) {
                        outputJson.put((Object)"InvalidResponse", (Object)"zc:InvalidResponse");
                        return outputJson;
                    }
                    JSONArray rows = null;
                    final Object response = tasksJson.get(tasksJson.keySet().iterator().next());
                    if (response instanceof JSONArray) {
                        rows = (JSONArray)response;
                        SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, sourceMethod, "Current Download Count : " + rows.size());
                        outputJson.put((Object)currentFetchKey, (Object)rows);
                        if (limit > rows.size()) {
                            break;
                        }
                        fromIndex += limit;
                    }
                    else {
                        SyMLogger.warning(SilentUpdateController.logger, SilentUpdateController.sourceClass, sourceMethod, "Download failed : " + response);
                        currentDownloadIsSuccess = false;
                        downloadTasksStatus.setErrorMessage("Un parsable response");
                    }
                }
                else {
                    SyMLogger.warning(SilentUpdateController.logger, SilentUpdateController.sourceClass, sourceMethod, "Download Failed(" + downloadTasksStatus.getStatus() + ") : " + downloadTasksStatus.getErrorMessage());
                }
                if (!currentDownloadIsSuccess) {
                    outputJson.put((Object)currentFetchKey, (Object)String.valueOf(downloadTasksStatus.getStatus() + "|" + downloadTasksStatus.getErrorMessage()));
                    if (failedRequestCount > 1) {
                        break;
                    }
                    SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, sourceMethod, "Initialize to retry.");
                    ++failedRequestCount;
                }
                else {
                    failedRequestCount = 0;
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateController.logger, SilentUpdateController.sourceClass, sourceMethod, "Exception occurred : ", (Throwable)e);
            outputJson.put((Object)"0ERROR:", (Object)String.valueOf(e.getMessage()));
        }
        return outputJson;
    }
    
    public DownloadStatus downloadBufferFromCreator(final Properties properties) throws Exception {
        try {
            String theUrl = "https://creator.zoho.com/api/" + ((Hashtable<K, Object>)properties).get("BufferType") + "/" + ((Hashtable<K, Object>)properties).get("ApplicationName") + "/view/" + ((Hashtable<K, Object>)properties).get("ViewName");
            theUrl = theUrl + "?authtoken=" + ((Hashtable<K, Object>)properties).get("ZC_Key") + "&" + "raw=true&zc_ownername=" + ((Hashtable<K, Object>)properties).get("OwnerName") + "&scope=creatorapi";
            if (properties.containsKey("FromIndex") && properties.containsKey("ToIndex")) {
                theUrl = theUrl + "&startindex=" + String.valueOf(((Hashtable<K, Object>)properties).get("FromIndex")) + "&limit=" + ((Hashtable<K, Object>)properties).get("ToIndex");
            }
            else {
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "getZCViewUrl", "No limit applied.");
            }
            if (properties.containsKey("CriteriaColumnName") && properties.containsKey("zc_last_taskdetails_fetch_time")) {
                String lastTasksFetchTime = ((Hashtable<K, Object>)properties).get("zc_last_taskdetails_fetch_time").toString().trim();
                final Long lastTasksFetchTimeAsLong = Long.valueOf(lastTasksFetchTime);
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                lastTasksFetchTime = simpleDateFormat.format(lastTasksFetchTimeAsLong);
                theUrl = theUrl + "&criteria=(" + ((Hashtable<K, Object>)properties).get("CriteriaColumnName") + "%3E=%22" + lastTasksFetchTime + "%22)";
            }
            else {
                SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadBufferFromCreator", "No filters applied.");
            }
            theUrl = theUrl.replaceAll(" ", "%20");
            SyMLogger.info(SilentUpdateController.logger, SilentUpdateController.sourceClass, "updateTasksTODB", "Download Url : " + theUrl);
            return DownloadManager.getInstance().getURLResponseWithoutCookie(theUrl, (String)null, new SSLValidationType[] { SSLValidationType.DEFAULT_SSL_VALIDATION });
        }
        catch (final Exception e) {
            SyMLogger.error(SilentUpdateController.logger, SilentUpdateController.sourceClass, "downloadBufferFromCreator", "Exception occurred : ", (Throwable)e);
            throw e;
        }
    }
    
    static {
        SilentUpdateController.logger = Logger.getLogger("SilentUpdate");
        SilentUpdateController.sourceClass = "SilentUpdateController";
    }
}
