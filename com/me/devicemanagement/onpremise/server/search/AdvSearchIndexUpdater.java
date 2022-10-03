package com.me.devicemanagement.onpremise.server.search;

import java.util.Hashtable;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import org.apache.commons.io.FilenameUtils;
import com.adventnet.mfw.ConsoleOut;
import java.io.FileWriter;
import java.util.Arrays;
import java.io.FilenameFilter;
import com.me.devicemanagement.onpremise.server.util.ZipUtil;
import java.util.Iterator;
import java.io.IOException;
import java.util.List;
import com.me.devicemanagement.framework.server.search.AdvSearchProductSpecificHandler;
import com.me.devicemanagement.framework.server.search.SearchConfiguration;
import com.me.devicemanagement.framework.server.dms.DMSDownloadUtil;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Map;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.io.FileNotFoundException;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.search.AdvSearchCommonUtil;
import com.me.devicemanagement.framework.server.search.CompleteSearchUtil;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class AdvSearchIndexUpdater
{
    private static Logger logger;
    private static AdvSearchIndexUpdater indexUpdater;
    private String searchTempDir;
    
    public AdvSearchIndexUpdater() throws Exception {
        this.searchTempDir = SyMUtil.getInstallationDir() + File.separator + "SearchTemp";
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        if (!fileAccessAPI.isDirectory(this.searchTempDir)) {
            AdvSearchIndexUpdater.logger.log(Level.WARNING, "Is Search Temp Directory is created : " + fileAccessAPI.createDirectory(this.searchTempDir));
        }
    }
    
    public static synchronized AdvSearchIndexUpdater getInstance() throws Exception {
        if (AdvSearchIndexUpdater.indexUpdater == null) {
            AdvSearchIndexUpdater.indexUpdater = new AdvSearchIndexUpdater();
        }
        return AdvSearchIndexUpdater.indexUpdater;
    }
    
    public String docCRSupdate() {
        String message = null;
        boolean isVaildCheckSum = Boolean.FALSE;
        boolean isCopyDocIndex = Boolean.FALSE;
        try {
            final int indexUpdateStatusCode = this.searchIndexCSRChecker();
            if (indexUpdateStatusCode == 0) {
                final JSONObject updateJson = CompleteSearchUtil.getJsonObjectFromFile(this.getSearchIndexUpdateFile());
                AdvSearchIndexUpdater.logger.log(Level.INFO, "CRS document version: " + updateJson.toString());
                final long latestDocIndexVersionInSetup = this.getLastestVersionFromDir(AdvSearchCommonUtil.doc_index_dir);
                long docIndexVersionInCRS = updateJson.getLong("documentIndexVersion");
                final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
                if (isMsp) {
                    docIndexVersionInCRS = updateJson.getLong("mspDocumentIndexVersion");
                }
                AdvSearchIndexUpdater.logger.log(Level.INFO, "docIndexVersionInCRS > latestDocIndexVersionInSetup => " + docIndexVersionInCRS + " > " + latestDocIndexVersionInSetup);
                if (docIndexVersionInCRS > latestDocIndexVersionInSetup) {
                    final DownloadManager downloadMgr = DownloadManager.getInstance();
                    final String docIdxURL = ProductUrlLoader.getInstance().getValue("advsearch_doc_index_csr_url");
                    final String extractedDir = this.searchTempDir + File.separator + docIndexVersionInCRS;
                    final String docIndexCRSUpdateFile = extractedDir + ".7z";
                    final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
                    fileAccessAPI.forceDeleteDirectory(docIndexCRSUpdateFile);
                    final DownloadStatus downloadStatus = downloadMgr.downloadFile(docIdxURL, docIndexCRSUpdateFile, new SSLValidationType[0]);
                    if (downloadStatus.getStatus() == 0) {
                        AdvSearchIndexUpdater.logger.log(Level.INFO, "Successfully Downloaded the Latest Document Index URL : " + docIdxURL);
                        fileAccessAPI.forceDeleteDirectory(extractedDir);
                        final boolean isExtracted = this.extract7zFile(docIndexCRSUpdateFile, this.searchTempDir);
                        if (isExtracted) {
                            final String indexChecksumFile = extractedDir + File.separator + "indexChecksum.json";
                            final JSONObject indexChecksumJson = CompleteSearchUtil.getJsonObjectFromFile(indexChecksumFile);
                            fileAccessAPI.forceDeleteDirectory(indexChecksumFile);
                            final JSONObject downloadCheckSumJson = new JSONObject();
                            getMD5Hash(extractedDir, downloadCheckSumJson, "");
                            final String checkSumFile = this.searchTempDir + File.separator + "downloadChecksum.json";
                            final Boolean isWriteSuccess = this.writeJsonToFile(checkSumFile, downloadCheckSumJson);
                            if (isWriteSuccess) {
                                final Map<String, Object> oldchecksumMap = SyMUtil.jsonToMap(indexChecksumJson, (boolean)Boolean.TRUE);
                                final Map<String, Object> newchecksumMap = SyMUtil.jsonToMap(downloadCheckSumJson, (boolean)Boolean.TRUE);
                                isVaildCheckSum = this.mapsAreEqual(oldchecksumMap, newchecksumMap);
                                if (isVaildCheckSum) {
                                    isCopyDocIndex = fileAccessAPI.copyDirectory(extractedDir, AdvSearchCommonUtil.doc_index_dir + File.separator + docIndexVersionInCRS);
                                    AdvSearchIndexUpdater.logger.log(Level.WARNING, "Successfully Sanitizer CheckSum check docIndexCRSVersion and copied to searchIndex directory : {0} ", isCopyDocIndex);
                                    if (!isCopyDocIndex) {
                                        message = "Failed copied to searchIndex directory";
                                        AdvSearchIndexUpdater.logger.log(Level.WARNING, message);
                                    }
                                }
                                else {
                                    AdvSearchIndexUpdater.logger.log(Level.INFO, "Download  checksum :" + oldchecksumMap.toString());
                                    AdvSearchIndexUpdater.logger.log(Level.INFO, "Extracted checksum :" + newchecksumMap.toString());
                                    message = "Failed in Sanitizer CheckSum check";
                                    AdvSearchIndexUpdater.logger.log(Level.WARNING, "Failed in Sanitizer CheckSum check docIndexCRSVersion :  " + docIndexVersionInCRS);
                                }
                            }
                        }
                    }
                    else {
                        message = " Download Failed for File : " + docIdxURL + " with ErrorCode:" + downloadStatus.getStatus() + "  -  " + downloadStatus.getErrorMessage();
                        AdvSearchIndexUpdater.logger.log(Level.SEVERE, message);
                    }
                }
            }
            if ((indexUpdateStatusCode == 0 && isVaildCheckSum) || indexUpdateStatusCode == 10010) {
                message = "SUCCESS";
                SyMUtil.updateServerParameter("ADVSEARCH_LAST_MODIFIED_TIME", System.currentTimeMillis() + "");
            }
        }
        catch (final FileNotFoundException ex) {
            AdvSearchIndexUpdater.logger.log(Level.SEVERE, "Mismatch version : Exception occured - getIndexCRSUpdate", ex);
        }
        catch (final Exception ex2) {
            AdvSearchIndexUpdater.logger.log(Level.SEVERE, "Advanced Search : Exception occured - getIndexCRSUpdate", ex2);
            message = ex2.getMessage();
        }
        this.deletedSearchTempDir();
        return message;
    }
    
    private void deletedSearchTempDir() {
        try {
            ApiFactoryProvider.getFileAccessAPI().forceDeleteDirectory(this.searchTempDir);
        }
        catch (final Exception e) {
            AdvSearchIndexUpdater.logger.log(Level.WARNING, "Failed to deleted the Temp files of AdvSearch : {0} ", this.searchTempDir);
            AdvSearchIndexUpdater.logger.log(Level.SEVERE, "Exception While deleting Search temp files ", e);
        }
    }
    
    public int searchIndexCSRChecker() {
        try {
            AdvSearchIndexUpdater.logger.log(Level.INFO, "Invoke searchIndexCSRChecker");
            int updatesJSONDownloadStatus = 10008;
            final String indexUpdateCheckerURL = ProductUrlLoader.getInstance().getValue("advsearch_index_check_url");
            if (indexUpdateCheckerURL == null || indexUpdateCheckerURL.trim().length() == 0) {
                AdvSearchIndexUpdater.logger.log(Level.SEVERE, "ADVSEARCH_DOC_INDEX_CSR_URL is NULL, Check in general_properties.conf");
                return 10001;
            }
            final String searchIndexUpdateFile = this.getSearchIndexUpdateFile();
            final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
            if (fileAccessAPI.isFileExists(searchIndexUpdateFile)) {
                fileAccessAPI.deleteFile(searchIndexUpdateFile);
            }
            final String proxyDefined = SyMUtil.getSyMParameter("proxy_defined");
            if (proxyDefined != null && proxyDefined.equalsIgnoreCase("true")) {
                final boolean isUpdatesJSON = Boolean.TRUE;
                updatesJSONDownloadStatus = this.downloadCRSCheckerFileUsingHTTP(indexUpdateCheckerURL, searchIndexUpdateFile, isUpdatesJSON);
            }
            else {
                AdvSearchIndexUpdater.logger.log(Level.INFO, "Proxy is Not Defined, Hence couldn't download " + indexUpdateCheckerURL);
            }
            return updatesJSONDownloadStatus;
        }
        catch (final Exception e) {
            AdvSearchIndexUpdater.logger.log(Level.SEVERE, "Exception while Downloading Update json file", e);
            return 10008;
        }
    }
    
    private String getSearchIndexUpdateFile() {
        return this.searchTempDir + File.separator + "searchIndexUpdate" + ".json";
    }
    
    private int downloadCRSCheckerFileUsingHTTP(final String idxUpdChkURL, final String searchIndexUpdateFile, final boolean isUpdatesJSON) throws Exception {
        try {
            AdvSearchIndexUpdater.logger.log(Level.INFO, "Going to access the Search Index CRS Update URL : " + idxUpdChkURL + " to find out the latest indexing for AdvSearch.");
            AdvSearchIndexUpdater.logger.log(Level.INFO, "Destination Indexing File : " + searchIndexUpdateFile);
            final Properties headers = new Properties();
            ((Hashtable<String, String>)headers).put("Pragma", "no-cache");
            ((Hashtable<String, String>)headers).put("Cache-Control", "no-cache");
            final String lastModifiedTime = SyMUtil.getServerParameter("ADVSEARCH_LAST_MODIFIED_TIME");
            if (isUpdatesJSON && lastModifiedTime != null) {
                final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                ((Hashtable<String, String>)headers).put("If-Modified-Since", sdf.format(new Date(Long.valueOf(lastModifiedTime))));
            }
            final boolean enableChecksumValidation = Boolean.parseBoolean(ProductUrlLoader.getInstance().getValue("enableChecksumValidation", Boolean.FALSE.toString()));
            DownloadStatus downloadStatus;
            if (enableChecksumValidation) {
                final DMSDownloadUtil downloadUtil = DMSDownloadUtil.getInstance();
                downloadStatus = downloadUtil.downloadRequestedFileForComponent("Framework", "advSearchIndexUpdate", searchIndexUpdateFile, (Properties)null, headers).getDownloadStatus();
            }
            else {
                final DownloadManager downloadMgr = DownloadManager.getInstance();
                downloadStatus = downloadMgr.downloadFile(idxUpdChkURL, searchIndexUpdateFile, (Properties)null, headers, new SSLValidationType[0]);
            }
            final int statusCode = downloadStatus.getStatus();
            if (statusCode == 0) {
                AdvSearchIndexUpdater.logger.log(Level.INFO, "Successfully Downloaded the searchIndexUpdate.json URL : " + idxUpdChkURL);
            }
            else if (statusCode == 10010) {
                AdvSearchIndexUpdater.logger.log(Level.INFO, "FILE NOT MODIFIED : " + idxUpdChkURL);
            }
            else {
                AdvSearchIndexUpdater.logger.log(Level.SEVERE, " Download Failed for File : " + idxUpdChkURL + " with ErrorCode: " + statusCode + "  -  " + downloadStatus.getErrorMessage());
            }
            return statusCode;
        }
        catch (final Exception e) {
            AdvSearchIndexUpdater.logger.log(Level.SEVERE, "Exception while Downloading File From" + idxUpdChkURL, e);
            return 10008;
        }
    }
    
    public Boolean checkSearchIndexDir(final String key, final String indexDir) throws Exception {
        final AdvSearchProductSpecificHandler searchProductSpecificHandler = ApiFactoryProvider.getSearchProductSpecificHandler();
        if (ApiFactoryProvider.getFileAccessAPI().isFileExists(indexDir)) {
            final List<String> subDirectoriesList = this.getSubDirectoriesList(indexDir);
            if (subDirectoriesList.isEmpty()) {
                AdvSearchIndexUpdater.logger.log(Level.WARNING, "Available sub directories list is Empty  :  " + indexDir);
                searchProductSpecificHandler.updateMainIndexFile(Boolean.FALSE, "Available sub directories list is Empty for " + key);
            }
            else {
                AdvSearchIndexUpdater.logger.log(Level.INFO, "Available sub directories list : " + subDirectoriesList.toArray() + "  :  " + indexDir);
            }
        }
        else {
            searchProductSpecificHandler.updateMainIndexFile(Boolean.FALSE, "File Not Found");
        }
        boolean isUpdated = Boolean.FALSE;
        if (SearchConfiguration.getConfiguration().isSearchEnabled()) {
            isUpdated = this.updateSearchMainIndexFile(key, indexDir);
        }
        return isUpdated;
    }
    
    public boolean updateSearchMainIndexFile(final String key, final String indexDir) throws Exception {
        AdvSearchIndexUpdater.logger.log(Level.INFO, "Invoke updateSearchMainIndexFile : " + key + "  :  " + indexDir);
        final JSONObject searchIndexMainJson = CompleteSearchUtil.getMainIndexDirJson();
        Boolean isWriteMainIndexSuccess = Boolean.FALSE;
        final Long currentDocVersion = searchIndexMainJson.getLong(key);
        final Long latestDocVersionAvailble = this.getLastestVersionFromDir(indexDir);
        AdvSearchIndexUpdater.logger.log(Level.INFO, "latestDocVersionAvailble > currentDocVersion => " + latestDocVersionAvailble + " > " + currentDocVersion);
        if (latestDocVersionAvailble > currentDocVersion) {
            AdvSearchIndexUpdater.logger.log(Level.INFO, "Changing Document Index to Latest Version : {0}", latestDocVersionAvailble);
            final String latestDocVersion = latestDocVersionAvailble.toString();
            searchIndexMainJson.put(key, (Object)latestDocVersion);
            isWriteMainIndexSuccess = this.writeJsonToFile(AdvSearchCommonUtil.search_main_index_file_name, searchIndexMainJson);
            AdvSearchIndexUpdater.logger.log(Level.INFO, "Updated Main SearchIndex Information in File :   " + isWriteMainIndexSuccess);
            this.deletedIndexingVersionDirTill(indexDir, latestDocVersion);
        }
        return isWriteMainIndexSuccess;
    }
    
    private boolean deletedIndexingVersionDirTill(final String dirPath, final String version) {
        boolean isDeleted = Boolean.FALSE;
        final List<String> directories = this.getSubDirectoriesList(dirPath);
        final int index = directories.indexOf(version);
        AdvSearchIndexUpdater.logger.log(Level.WARNING, "Deleted Indexing directories till {0} version", version);
        if (index >= 0) {
            for (int i = 0; i < index - 1; ++i) {
                final String dir = directories.get(i);
                final String file = dirPath + File.separator + dir;
                try {
                    ApiFactoryProvider.getFileAccessAPI().forceDeleteDirectory(file);
                    AdvSearchIndexUpdater.logger.log(Level.WARNING, "{0} Search Indexing Directory has been deleted recursively !", dir);
                }
                catch (final IOException e) {
                    AdvSearchIndexUpdater.logger.log(Level.SEVERE, "Problem occurs when deleting the Search Indexing Directory : {0}", dir);
                    AdvSearchIndexUpdater.logger.log(Level.SEVERE, "IOException :", e);
                }
                catch (final Exception e2) {
                    AdvSearchIndexUpdater.logger.log(Level.SEVERE, "Exception :", e2);
                }
            }
            isDeleted = Boolean.TRUE;
        }
        return isDeleted;
    }
    
    public Long getLastestVersionFromDir(final String dirPath) {
        String lastestVersion = null;
        final List<String> directories = this.getSubDirectoriesList(dirPath);
        lastestVersion = directories.get(directories.size() - 1);
        return Long.parseLong(lastestVersion);
    }
    
    private boolean mapsAreEqual(final Map<String, Object> mapA, final Map<String, Object> mapB) {
        try {
            for (final String key : mapB.keySet()) {
                if (key.equalsIgnoreCase("indexChecksum.json")) {
                    continue;
                }
                if (!mapA.get(key).equals(mapB.get(key))) {
                    AdvSearchIndexUpdater.logger.log(Level.WARNING, "Download CSR Checksum verification failed for {0} file", key);
                    return false;
                }
            }
            for (final String key : mapA.keySet()) {
                if (key.equalsIgnoreCase("indexChecksum.json")) {
                    continue;
                }
                if (!mapB.containsKey(key)) {
                    AdvSearchIndexUpdater.logger.log(Level.WARNING, "CSR Checksum verification failed because {0} file is not found", key);
                    return false;
                }
            }
        }
        catch (final NullPointerException np) {
            AdvSearchIndexUpdater.logger.log(Level.SEVERE, "CSR Checksum verification failed due to NullPointerException", np);
            return false;
        }
        return true;
    }
    
    private boolean extract7zFile(final String source, final String destination) {
        final ZipUtil zipUtil = new ZipUtil();
        final String[] arguments = { "7za.exe", "x", source, "-o" + destination, "-mmt=" + ZipUtil.get7ZipCoreCount() };
        return zipUtil.SevenZipCommand(arguments, "Successfully extracted the download Document Index 7z file.");
    }
    
    public List<String> getSubDirectoriesList(final String dirPath) {
        final File file = new File(dirPath);
        final String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(final File current, final String name) {
                return new File(current, name).isDirectory();
            }
        });
        Arrays.sort(directories);
        return Arrays.asList(directories);
    }
    
    public Boolean writeJsonToFile(final String filePath, final JSONObject indexedJsonObj) throws Exception {
        final FileWriter fileWriter = new FileWriter(filePath);
        try {
            fileWriter.write(indexedJsonObj.toString());
            fileWriter.flush();
            return true;
        }
        catch (final Exception ex) {
            ConsoleOut.println("Got Exception in writeJsonToFile(): " + ex);
            return false;
        }
        finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }
    
    public static void getMD5Hash(final String indexDataFile, final JSONObject jsonMd5CheckSum, String filePrefix) throws Exception {
        InputStream input = null;
        final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        try {
            if (fileAccessAPI.isFileExists(indexDataFile)) {
                final List<String> fileList = fileAccessAPI.getAllFilesList(indexDataFile, (String)null, (String)null);
                for (final String file : fileList) {
                    final String fileName = FilenameUtils.getName(file);
                    if (fileAccessAPI.isDirectory(file)) {
                        if (!filePrefix.equals("")) {
                            filePrefix = filePrefix + File.separator + fileName;
                        }
                        else {
                            filePrefix = fileName;
                        }
                        ConsoleOut.println("Directory Name :   " + filePrefix);
                        getMD5Hash(file, jsonMd5CheckSum, filePrefix);
                        filePrefix = "";
                    }
                    else {
                        String fileCheckName = null;
                        if (!filePrefix.equals("")) {
                            fileCheckName = filePrefix + File.separator + fileName;
                        }
                        else {
                            fileCheckName = fileName;
                        }
                        if (!fileAccessAPI.isFileExists(file)) {
                            continue;
                        }
                        final String canonicalPath = fileAccessAPI.getCanonicalPath(file);
                        AdvSearchIndexUpdater.logger.info("Indexing file " + canonicalPath);
                        input = fileAccessAPI.readFile(canonicalPath);
                        final String output = ChecksumProvider.getInstance().getMD5HashFromInputStream(input);
                        jsonMd5CheckSum.put(fileCheckName, (Object)output);
                        AdvSearchIndexUpdater.logger.info("Md5CheckSum for file " + canonicalPath + ".... is: " + output);
                    }
                }
            }
        }
        catch (final Exception exp) {
            ConsoleOut.println("Got Exception in getMD5HashFromDirectFileRead(): " + exp);
            throw exp;
        }
        finally {
            if (input != null) {
                input.close();
            }
        }
    }
    
    static {
        AdvSearchIndexUpdater.logger = Logger.getLogger(AdvSearchIndexUpdater.class.getName());
    }
}
