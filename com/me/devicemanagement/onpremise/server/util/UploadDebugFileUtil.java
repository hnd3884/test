package com.me.devicemanagement.onpremise.server.util;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.util.Calendar;
import java.util.Collection;
import java.io.FilenameFilter;
import com.me.devicemanagement.onpremise.start.util.NginxServerUtils;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.adventnet.ds.DSUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.webclient.support.UploadAction;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.text.SimpleDateFormat;
import org.apache.commons.io.FileUtils;
import com.me.devicemanagement.framework.server.fileaccess.FileOperationsUtil;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.ArrayList;

public class UploadDebugFileUtil
{
    public static final String TASK_NAME = "UploadDebugFileTask";
    public static final String FILE_LIST_XML = "uploadFileList.xml";
    public static final String FILE_LIST_WITHOUT_PI_XML = "uploadFileListForMETrack.xml";
    public static final String NO_MANAGED_CONNECTION = "no-managed-connection";
    public static final String JVM_THREAD_BLOCK = "jvm_thread_blocks";
    public static final Long FIRST_UPLOAD;
    public static final Long REACHED_UPLOAD_LIMIT;
    public static final int RETRY_COUNT = 3;
    public static final String DEFAULT_FILE = "DefaultFile";
    private static final String MAX_NUMBER_OF_FILES_KEY = "max_number_of_files";
    private static final String DEPENDENT_FILE_SEARCH_STRING_KEY = "dependent_file_search_string";
    public static ArrayList<String> userMessage;
    private static long maxSizeForBonitas;
    private static Properties uploadFileListDetails;
    private static Logger logger;
    private static Logger connectionDumpLog;
    
    public static ArrayList<Properties> mergeFileListProps(final ArrayList<Properties> piFileListProperties, final ArrayList<Properties> withoutPIFileListProperties) {
        UploadDebugFileUtil.logger.log(Level.FINEST, "Merging automatic file upload xml files...");
        final ArrayList<Properties> fileListProps = piFileListProperties;
        for (final Properties fileProp : withoutPIFileListProperties) {
            if (!fileProp.getProperty("fileType").equals("DefaultFile")) {
                fileListProps.add(fileProp);
            }
        }
        UploadDebugFileUtil.logger.log(Level.FINEST, "Upload count and upload interval are taken from uploadFileList.xml");
        return fileListProps;
    }
    
    private static void cleanSupportDebugFolder() {
        try {
            UploadDebugFileUtil.logger.log(Level.FINEST, "Clean support folder method is invoked.");
            final String serverDir = System.getProperty("server.home");
            final String desPath = serverDir + File.separator + "logs" + File.separator + "DebugFiles";
            final File dir = new File(desPath);
            if (!dir.exists()) {
                UploadDebugFileUtil.logger.log(Level.INFO, "No support debug directory found!!");
            }
            else {
                final boolean deletionSuccess = FileOperationsUtil.getInstance().deleteFileOrFolder(dir);
                if (deletionSuccess) {
                    UploadDebugFileUtil.logger.log(Level.INFO, "Support debug directory deleted !!");
                }
            }
        }
        catch (final Exception e) {
            UploadDebugFileUtil.logger.log(Level.WARNING, "Error while deleting support debug directory" + e.toString(), e);
        }
    }
    
    private static void cleanSupportDebugZipFolder() {
        boolean deletionsuccess = false;
        final String serverDir = System.getProperty("server.home");
        final String desPath = serverDir + File.separator + "logs" + File.separator + "uploadDebugFiles";
        final File dir = new File(desPath);
        if (!dir.exists()) {
            UploadDebugFileUtil.logger.log(Level.INFO, "No support debug Logs folder found to delete!!");
        }
        else {
            deletionsuccess = FileOperationsUtil.getInstance().deleteFileOrFolder(dir);
            if (deletionsuccess) {
                UploadDebugFileUtil.logger.log(Level.INFO, "Support debug Log folder deleted!!");
            }
            else {
                UploadDebugFileUtil.logger.log(Level.INFO, "Support debug Log folder Not deleted!!");
            }
        }
    }
    
    public static void createAndUploadDebugFiles(final ArrayList<File> uploadFileList, final String zipFileName) {
        String uploadStatus = "";
        UploadDebugFileUtil.userMessage = new ArrayList<String>();
        if (!uploadFileList.isEmpty()) {
            try {
                cleanSupportDebugFolder();
                cleanSupportDebugZipFolder();
                final String serverHome = System.getProperty("server.home");
                final String debugFileDirName = serverHome + File.separator + "logs" + File.separator + "DebugFiles";
                final File supportUploadDir = new File(serverHome + File.separator + "logs" + File.separator + "uploadDebugFiles");
                final File uploadDir = new File(debugFileDirName);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                for (final File f : uploadFileList) {
                    if (f.exists()) {
                        UploadDebugFileUtil.logger.log(Level.INFO, "File to be uploaded : " + f.getName());
                        FileUtils.copyFile(f, new File(uploadDir, f.getName()));
                        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                        final String fileType = UploadDebugFileUtil.uploadFileListDetails.getProperty(f.getName());
                        if (fileType == null) {
                            continue;
                        }
                        boolean update = true;
                        for (final String msg : UploadDebugFileUtil.userMessage) {
                            if (msg.startsWith(fileType)) {
                                update = false;
                            }
                        }
                        if (!update) {
                            continue;
                        }
                        UploadDebugFileUtil.userMessage.add(fileType + "-" + dateFormatter.format(f.lastModified()));
                    }
                }
                if (!supportUploadDir.exists()) {
                    supportUploadDir.mkdir();
                }
                final ZipUtil zipprocess = new ZipUtil();
                final String uploadFileName = ".." + File.separator + "logs" + File.separator + supportUploadDir.getName() + File.separator + zipFileName;
                final String dest = "logs" + File.separator + supportUploadDir.getName() + File.separator + zipFileName;
                final String source = "logs" + File.separator + uploadDir.getName() + File.separator;
                final String[] arguments = { System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za.exe", "a", dest, source, "-mmt=" + ZipUtil.get7ZipCoreCount() };
                final boolean iszipped = zipprocess.SevenZipCommand(arguments, "");
                UploadDebugFileUtil.logger.log(Level.INFO, "Zip success status : " + iszipped);
                if (iszipped) {
                    final File logFile = new File(uploadFileName);
                    UploadDebugFileUtil.logger.log(Level.INFO, "Log File size : " + logFile.length());
                    if (logFile.length() > UploadDebugFileUtil.maxSizeForBonitas) {
                        UploadDebugFileUtil.logger.log(Level.INFO, "Log size is high. So upload cannot be done");
                        uploadStatus = "Log size > 90MB";
                    }
                    else {
                        String fromAddress = null;
                        if (isAutomaticUploadEnable()) {
                            fromAddress = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails().get("mail.fromAddress");
                        }
                        else {
                            fromAddress = "anonymous@customer.com";
                        }
                        final String userMsg = "Logs content : " + UploadDebugFileUtil.userMessage.toString();
                        boolean isUploadSuccess = Boolean.FALSE;
                        int retryCountIterator = 0;
                        do {
                            ++retryCountIterator;
                            isUploadSuccess = UploadAction.doUploadBigSize(uploadFileName, fromAddress, userMsg, null, Boolean.FALSE, Boolean.TRUE);
                        } while (!isUploadSuccess && retryCountIterator < 3);
                        updateFileUploadDetailsDO(isUploadSuccess, uploadFileList);
                        uploadStatus = ((isUploadSuccess == Boolean.TRUE) ? "Upload success" : "Upload failed");
                        UploadDebugFileUtil.logger.log(Level.INFO, "Upload Status : " + uploadStatus);
                    }
                }
                else {
                    uploadStatus = "Compression failed";
                }
                cleanSupportDebugFolder();
                cleanSupportDebugZipFolder();
                SyMUtil.updateServerParameter("DebugLogUploadStatus", uploadStatus);
            }
            catch (final Exception e) {
                UploadDebugFileUtil.logger.log(Level.INFO, "Exception while uploading debug logs" + e);
            }
        }
        else {
            UploadDebugFileUtil.logger.log(Level.INFO, "NO FILES TO BE UPLOADED");
        }
    }
    
    private static void updateFileUploadDetailsDO(final boolean isUploadSuccess, final ArrayList<File> fileList) {
        try {
            final HashMap countBasedOnType = new HashMap();
            final Long currentTime = System.currentTimeMillis();
            final int buildNo = Integer.parseInt(SyMUtil.getProductProperty("buildnumber"));
            for (final File uploadFile : fileList) {
                final String fileType = UploadDebugFileUtil.uploadFileListDetails.getProperty(uploadFile.getName());
                if (fileType != null) {
                    UploadDebugFileUtil.logger.log(Level.FINE, "File type" + fileType);
                    final Boolean isUpdated = countBasedOnType.get(fileType);
                    if (isUpdated != null && isUpdated) {
                        continue;
                    }
                    final DataObject fileUploadDO = getFileUploadDetailsDO(fileType, String.valueOf(buildNo));
                    if (!fileUploadDO.isEmpty()) {
                        final Row fileUploadRow = fileUploadDO.getFirstRow("FileUploadDetails");
                        int count = (int)fileUploadRow.get("UPLOAD_COUNT_PER_BUILD_NUMBER");
                        ++count;
                        fileUploadRow.set("FILE_TYPE", (Object)fileType);
                        fileUploadRow.set("BUILD_NUMBER", (Object)buildNo);
                        fileUploadRow.set("UPLOAD_COUNT_PER_BUILD_NUMBER", (Object)count);
                        fileUploadRow.set("UPLOAD_SUCCESS_STATUS", (Object)isUploadSuccess);
                        if (isUploadSuccess) {
                            fileUploadRow.set("LAST_UPLOADED_TIME", (Object)currentTime);
                        }
                        fileUploadDO.updateRow(fileUploadRow);
                        UploadDebugFileUtil.logger.log(Level.FINEST, "Updated entry in file upload detail");
                    }
                    else {
                        final Row fileUploadRow = new Row("FileUploadDetails");
                        fileUploadRow.set("FILE_TYPE", (Object)fileType);
                        fileUploadRow.set("BUILD_NUMBER", (Object)buildNo);
                        fileUploadRow.set("UPLOAD_COUNT_PER_BUILD_NUMBER", (Object)1);
                        fileUploadRow.set("UPLOAD_SUCCESS_STATUS", (Object)isUploadSuccess);
                        if (isUploadSuccess) {
                            fileUploadRow.set("LAST_UPLOADED_TIME", (Object)currentTime);
                        }
                        fileUploadDO.addRow(fileUploadRow);
                        UploadDebugFileUtil.logger.log(Level.INFO, "Added entry in file upload detail");
                    }
                    SyMUtil.getPersistence().update(fileUploadDO);
                    countBasedOnType.put(fileType, Boolean.TRUE);
                }
            }
        }
        catch (final Exception ex) {
            UploadDebugFileUtil.logger.log(Level.WARNING, "Caught exception while updating fileUploadDetail DO ", ex);
        }
    }
    
    public static ArrayList<File> processUploadFileListXML(final ArrayList<Properties> fileListProperties) {
        final ArrayList<File> uploadFileList = new ArrayList<File>();
        final ArrayList<File> defaultFileList = new ArrayList<File>();
        try {
            UploadDebugFileUtil.logger.log(Level.FINEST, "Searching File list from xml...");
            final String serverHome = System.getProperty("server.home");
            for (final Properties prop : fileListProperties) {
                if (prop.getProperty("fileType").equals("DefaultFile")) {
                    final String fullFilePath = serverHome + File.separator + prop.getProperty("fileName");
                    defaultFileList.add(new File(fullFilePath));
                    UploadDebugFileUtil.logger.log(Level.INFO, "Added " + fullFilePath + " to Default file list.");
                }
                else {
                    final Long lastUploadTime = checkLastUploadStatus(prop);
                    if (lastUploadTime == UploadDebugFileUtil.REACHED_UPLOAD_LIMIT) {
                        continue;
                    }
                    final String searchName = prop.getProperty("fileSearchString");
                    final String dependentFileSearchName = prop.getProperty("dependentFileSearchString");
                    final String path = serverHome + File.separator + prop.getProperty("filePath");
                    UploadDebugFileUtil.logger.log(Level.INFO, "FILE PATH :" + path + "\t SEARCH STRING :" + searchName + "\t TYPE :" + prop.getProperty("fileType"));
                    Boolean fileBundle = Boolean.TRUE;
                    if (prop.getProperty("fileType").equals("no-managed-connection")) {
                        final int noManagedConnection = DSUtil.getInUseConnectionCount(10800L);
                        fileBundle = ((noManagedConnection > 0) ? Boolean.TRUE : Boolean.FALSE);
                        if (noManagedConnection > 0) {
                            UploadDebugFileUtil.logger.log(Level.INFO, "No-managed-connection found.Hence file bundled for upload. No of current  Live connections : " + noManagedConnection);
                            UploadDebugFileUtil.connectionDumpLog.log(Level.INFO, "--------------------*****************************--------------------");
                            UploadDebugFileUtil.connectionDumpLog.log(Level.INFO, "Automatic Diagnosis Log Upload Task");
                            UploadDebugFileUtil.connectionDumpLog.log(Level.INFO, "Number of connections which is opened before three hours : " + noManagedConnection);
                            UploadDebugFileUtil.connectionDumpLog.log(Level.INFO, "Number of opened connections : before three hours : " + DSUtil.getInUseConnectionInfo(10800L).toString());
                            UploadDebugFileUtil.connectionDumpLog.log(Level.INFO, "--------------------*****************************--------------------");
                        }
                        else {
                            UploadDebugFileUtil.logger.log(Level.INFO, "No-managed-connection not found");
                        }
                    }
                    if (prop.getProperty("fileType").equals("jvm_thread_blocks")) {
                        final long lastDeadLockOccurredTime = Long.parseLong(SyMUtil.getServerParameter("LastOccuredBlock"));
                        if (lastDeadLockOccurredTime > checkLastUploadStatus(prop)) {
                            fileBundle = Boolean.TRUE;
                            UploadDebugFileUtil.logger.log(Level.INFO, "Changes in jvm threads blocks found.Hence file bundled for upload.");
                        }
                        else {
                            fileBundle = Boolean.FALSE;
                            UploadDebugFileUtil.logger.log(Level.INFO, "No change in jvm threads occurrence log file");
                        }
                    }
                    if (prop.getProperty("fileType").equals("nginx-error")) {
                        if (WebServerUtil.getWebServerSettings().getProperty("webserver.name").equals("nginx")) {
                            if (!NginxServerUtils.isNginxServerRunning()) {
                                fileBundle = Boolean.TRUE;
                            }
                            else {
                                fileBundle = Boolean.FALSE;
                            }
                        }
                        else {
                            fileBundle = Boolean.FALSE;
                        }
                    }
                    if (!fileBundle) {
                        continue;
                    }
                    final File dir = new File(path);
                    if (!dir.exists()) {
                        continue;
                    }
                    final FilenameFilter filter = new FilenameFilter() {
                        @Override
                        public boolean accept(final File dir, final String name) {
                            return name.contains(searchName) && !name.contains(".lck");
                        }
                    };
                    final File[] files = dir.listFiles(filter);
                    if (files.length > 0) {
                        uploadFileList.addAll(getUploadFileList(files, prop));
                    }
                    if (dependentFileSearchName == null || dependentFileSearchName == "") {
                        continue;
                    }
                    final FilenameFilter dependentFileFilter = new FilenameFilter() {
                        @Override
                        public boolean accept(final File dir, final String name) {
                            return name.contains(dependentFileSearchName) && !name.contains(".lck");
                        }
                    };
                    final File[] dependentFiles = dir.listFiles(dependentFileFilter);
                    if (dependentFiles.length <= 0) {
                        continue;
                    }
                    uploadFileList.addAll(getUploadFileList(dependentFiles, prop));
                }
            }
            if (!uploadFileList.isEmpty()) {
                uploadFileList.addAll(defaultFileList);
            }
        }
        catch (final Exception e) {
            UploadDebugFileUtil.logger.log(Level.INFO, "Exception in proccessing input file list :", e);
        }
        return uploadFileList;
    }
    
    public static ArrayList<File> getUploadFileList(File[] files, final Properties prop) throws Exception {
        final Long lastUploadTime = checkLastUploadStatus(prop);
        final ArrayList<File> uploadFileList = new ArrayList<File>();
        files = FileOperationsUtil.getInstance().sortFilesWithLastModifiedTime(files);
        final Calendar calendar2015 = Calendar.getInstance();
        calendar2015.set(2015, 0, 1, 0, 0, 0);
        for (int maxNumberOfFiles = Math.min(Integer.parseInt(prop.getProperty("maxNumberOfFiles")), files.length), currentFileNumber = 1; currentFileNumber <= maxNumberOfFiles; ++currentFileNumber) {
            final File file = files[files.length - currentFileNumber];
            if (file.lastModified() > lastUploadTime && file.length() > 0L && file.lastModified() > calendar2015.getTimeInMillis()) {
                uploadFileList.add(file);
                UploadDebugFileUtil.uploadFileListDetails.setProperty(file.getName(), prop.getProperty("fileType"));
            }
        }
        return uploadFileList;
    }
    
    public static ArrayList<Properties> parseUploadFileListXML(final String uploadListXML) throws IOException, SAXException, ParserConfigurationException {
        final ArrayList<Properties> fileList = new ArrayList<Properties>();
        String defaultUploadCount = "";
        String defaultUploadInterval = "";
        String defaultMaxNumberOfFiles = "";
        try {
            final File uploadListXMLFile = new File(uploadListXML);
            final DocumentBuilder dBuilder = XMLUtils.getDocumentBuilderInstance();
            final Document doc = dBuilder.parse(uploadListXMLFile);
            doc.getDocumentElement().normalize();
            UploadDebugFileUtil.logger.log(Level.FINE, "Root element " + doc.getDocumentElement().getNodeName());
            UploadDebugFileUtil.logger.log(Level.FINE, "SERVER HOME " + uploadListXML);
            final Node root = doc.getDocumentElement();
            final NodeList uploadList = root.getChildNodes();
            for (int length = uploadList.getLength(), i = 0; i < length; ++i) {
                final Node uploadFile = uploadList.item(i);
                if (uploadFile.getNodeType() == 1) {
                    final Element fileElement = (Element)uploadFile;
                    if (fileElement.getNodeName().equals("general_settings")) {
                        defaultUploadCount = fileElement.getAttribute("uploadCountPerBuildNumber");
                        defaultUploadInterval = fileElement.getAttribute("uploadIntervalInDays");
                        defaultMaxNumberOfFiles = fileElement.getAttribute("max_number_of_files");
                    }
                    else if (fileElement.getNodeName().equals("DefaultFile")) {
                        final Properties defaultFileProps = new Properties();
                        defaultFileProps.setProperty("fileType", "DefaultFile");
                        final String filePath = fileElement.getAttribute("file_path") + File.separator + fileElement.getAttribute("file_name");
                        defaultFileProps.setProperty("fileName", filePath);
                        fileList.add(defaultFileProps);
                    }
                    else {
                        final String uploadCountPerBuildNumber = fileElement.getAttribute("uploadCountPerBuildNumber");
                        final String uploadIntervalInDays = fileElement.getAttribute("uploadIntervalInDays");
                        final String max_number_of_files_key = fileElement.getAttribute("max_number_of_files");
                        final String uploadCount = (uploadCountPerBuildNumber == null || uploadCountPerBuildNumber == "") ? defaultUploadCount : uploadCountPerBuildNumber;
                        final String uploadInterval = (uploadIntervalInDays == null || uploadIntervalInDays == "") ? defaultUploadInterval : uploadIntervalInDays;
                        final String maxNumberOfFiles = (max_number_of_files_key == null || max_number_of_files_key == "") ? defaultMaxNumberOfFiles : max_number_of_files_key;
                        final Properties fileProp = new Properties();
                        fileProp.setProperty("filePath", fileElement.getAttribute("file_path"));
                        fileProp.setProperty("fileType", fileElement.getAttribute("file_type"));
                        fileProp.setProperty("fileSearchString", fileElement.getAttribute("file_search_string"));
                        fileProp.setProperty("dependentFileSearchString", fileElement.getAttribute("dependent_file_search_string"));
                        fileProp.setProperty("uploadCountPerBuildNumber", uploadCount);
                        fileProp.setProperty("uploadIntervalInDays", uploadInterval);
                        fileProp.setProperty("maxNumberOfFiles", maxNumberOfFiles);
                        fileList.add(fileProp);
                    }
                }
            }
        }
        catch (final Exception ex) {
            UploadDebugFileUtil.logger.log(Level.WARNING, "Caught exception while parsing upload file list XML ", ex);
            throw ex;
        }
        return fileList;
    }
    
    public static Long checkLastUploadStatus(final Properties prop) {
        try {
            final int currentBuildNo = Integer.parseInt(SyMUtil.getProductProperty("buildnumber"));
            final String fileType = ((Hashtable<K, String>)prop).get("fileType");
            final int uploadInterval = Integer.parseInt(((Hashtable<K, String>)prop).get("uploadIntervalInDays"));
            final int maxUploadCount = Integer.parseInt(((Hashtable<K, String>)prop).get("uploadCountPerBuildNumber"));
            final DataObject fileUploadDetailsDO = getFileUploadDetailsDO(fileType);
            if (fileUploadDetailsDO.isEmpty()) {
                return UploadDebugFileUtil.FIRST_UPLOAD;
            }
            final Row fileDetailRow = fileUploadDetailsDO.getFirstRow("FileUploadDetails");
            final int count = (int)fileDetailRow.get("UPLOAD_COUNT_PER_BUILD_NUMBER");
            final long lastUploadTime = (long)fileDetailRow.get("LAST_UPLOADED_TIME");
            final int buildNo = (int)fileDetailRow.get("BUILD_NUMBER");
            if (buildNo != currentBuildNo) {
                UploadDebugFileUtil.logger.log(Level.INFO, "change in build number:check for last upload time : " + lastUploadTime);
                return lastUploadTime;
            }
            final Long currentTime = System.currentTimeMillis();
            final Long nextUploadTime = lastUploadTime + uploadInterval * 86400000L;
            if (count < maxUploadCount && currentTime > nextUploadTime) {
                UploadDebugFileUtil.logger.log(Level.FINE, "same build number:check for last upload time and count");
                return lastUploadTime;
            }
            UploadDebugFileUtil.logger.log(Level.INFO, "reached upload limit");
            return UploadDebugFileUtil.REACHED_UPLOAD_LIMIT;
        }
        catch (final Exception e) {
            UploadDebugFileUtil.logger.log(Level.WARNING, "Caught exception while checking is upload needed ", e);
            return UploadDebugFileUtil.REACHED_UPLOAD_LIMIT;
        }
    }
    
    public static boolean isAutomaticUploadEnable() throws Exception {
        Boolean isEnable = Boolean.TRUE;
        final DataObject dblocksettingsdo = SyMUtil.getPersistence().get("DbLockSettings", (Criteria)null);
        final Row settingsRow = dblocksettingsdo.getRow("DbLockSettings");
        if (settingsRow == null) {
            return isEnable;
        }
        isEnable = (Boolean)settingsRow.get("IS_AUTOMATIC");
        if (isEnable != null && isEnable == Boolean.FALSE) {
            UploadDebugFileUtil.logger.log(Level.INFO, "AUTOMATIC LOGS UPLOAD HAS BEEN DISABLED");
            return false;
        }
        UploadDebugFileUtil.logger.log(Level.INFO, "AUTOMATIC UPLOAD: ENABLE");
        return true;
    }
    
    private static DataObject getFileUploadDetailsDO(final String fileType, final String buildNumber) throws Exception {
        final SelectQueryImpl selectFileUploadDOQuery = new SelectQueryImpl(Table.getTable("FileUploadDetails"));
        selectFileUploadDOQuery.addSelectColumn(new Column((String)null, "*"));
        final Column fileTypecolumn = Column.getColumn("FileUploadDetails", "FILE_TYPE");
        Criteria fileTypecriteria = new Criteria(fileTypecolumn, (Object)fileType, 0, false);
        UploadDebugFileUtil.logger.log(Level.FINEST, "Build number" + buildNumber);
        final Column buildNumbercolumn = Column.getColumn("FileUploadDetails", "BUILD_NUMBER");
        final Criteria buildNumberCriteria = new Criteria(buildNumbercolumn, (Object)buildNumber, 0, false);
        fileTypecriteria = fileTypecriteria.and(buildNumberCriteria);
        selectFileUploadDOQuery.setCriteria(fileTypecriteria);
        return SyMUtil.getPersistence().get((SelectQuery)selectFileUploadDOQuery);
    }
    
    private static DataObject getFileUploadDetailsDO(final String fileType) throws Exception {
        final SelectQueryImpl selectFileUploadDOQuery = new SelectQueryImpl(Table.getTable("FileUploadDetails"));
        selectFileUploadDOQuery.addSelectColumn(new Column((String)null, "*"));
        final Column fileTypecolumn = Column.getColumn("FileUploadDetails", "FILE_TYPE");
        final Criteria fileTypecriteria = new Criteria(fileTypecolumn, (Object)fileType, 0, false);
        final SortColumn sortColumn = new SortColumn(Column.getColumn("FileUploadDetails", "BUILD_NUMBER"), false);
        selectFileUploadDOQuery.addSortColumn(sortColumn);
        selectFileUploadDOQuery.setCriteria(fileTypecriteria);
        return SyMUtil.getPersistence().get((SelectQuery)selectFileUploadDOQuery);
    }
    
    public void isChangeInBuildNumber() {
        try {
            final int buildNo = Integer.parseInt(SyMUtil.getProductProperty("buildnumber"));
            UploadDebugFileUtil.logger.log(Level.FINE, "current buildNo" + buildNo);
            final Long buildDetectedTime = (Long)DBUtil.getValueFromDB("DCServerBuildHistory", "BUILD_NUMBER", (Object)buildNo, "BUILD_DETECTED_AT");
            UploadDebugFileUtil.logger.log(Level.INFO, "buildDetectedTime" + buildDetectedTime);
            final Long lastUploadedFileTime = (Long)DBUtil.getMaxOfValue("FileUploadDetails", "LAST_UPLOADED_TIME", (Criteria)null);
            if (lastUploadedFileTime != null) {
                UploadDebugFileUtil.logger.log(Level.INFO, "last uploaded file time" + lastUploadedFileTime);
                if (buildDetectedTime > lastUploadedFileTime) {
                    UploadDebugFileUtil.logger.log(Level.FINE, "Change in build number.Clearing FileUploadDetails Table");
                    this.cleanupUploadDebugFile();
                }
            }
        }
        catch (final Exception e) {
            UploadDebugFileUtil.logger.log(Level.WARNING, "Caught exception while processing change in buildnumber : ", e);
        }
    }
    
    public void cleanupUploadDebugFile() throws DataAccessException {
        UploadDebugFileUtil.logger.log(Level.FINE, "FileUploadDetails table cleanup ");
        DataAccess.delete("FileUploadDetails", (Criteria)null);
    }
    
    static {
        FIRST_UPLOAD = 0L;
        REACHED_UPLOAD_LIMIT = -1L;
        UploadDebugFileUtil.userMessage = null;
        UploadDebugFileUtil.maxSizeForBonitas = 5000000000L;
        UploadDebugFileUtil.uploadFileListDetails = new Properties();
        UploadDebugFileUtil.logger = Logger.getLogger("UploadDebugFilesLogger");
        UploadDebugFileUtil.connectionDumpLog = Logger.getLogger("DMConnectionDump");
    }
}
