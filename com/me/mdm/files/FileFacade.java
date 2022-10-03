package com.me.mdm.files;

import com.zoho.security.validator.zip.ZSecZipSanitizer;
import com.adventnet.iam.security.ZSecConstants;
import java.util.regex.Pattern;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Collection;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MediaType;
import java.io.FileInputStream;
import org.apache.tika.Tika;
import java.io.FileOutputStream;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.i18n.I18N;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import java.util.UUID;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.io.IOException;
import com.me.mdm.api.error.APIHTTPException;
import java.io.InputStream;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FileFacade
{
    Logger logger;
    private static FileFacade instance;
    private static Map<String, List<String>> linkedContentType;
    
    public FileFacade() {
        this.logger = Logger.getLogger("MDMApiLogger");
    }
    
    public static FileFacade getInstance() {
        if (FileFacade.instance == null) {
            FileFacade.linkedContentType = new HashMap() {
                {
                    this.put("video/mp4", Arrays.asList("video/x-m4v"));
                    this.put("application/pkix-cert", Arrays.asList("application/x-x509-cert; format=pem", "application/x-x509-cert; format=der"));
                    this.put("application/x-x509-cert", Arrays.asList("application/x-x509-cert; format=pem", "application/x-x509-cert; format=der"));
                    this.put("application/x-pkcs12", Arrays.asList("application/x-x509-key; format=pem", "application/x-x509-key; format=der"));
                    this.put("application/x-x509-cert; format=pem", Arrays.asList("text/plain"));
                    this.put("application/x-x509-cert; format=der", Arrays.asList("text/plain"));
                    this.put("application/xml", Arrays.asList("application/x-plist"));
                }
            };
            FileFacade.instance = new FileFacade();
        }
        return FileFacade.instance;
    }
    
    public JSONObject addFile(final JSONObject requestJSON, final InputStream fileInStream) throws APIHTTPException, IOException {
        return this.addFile(requestJSON, fileInStream, false);
    }
    
    public JSONObject addFile(final JSONObject requestJSON, final InputStream fileInStream, final boolean fromAgent) throws APIHTTPException, IOException {
        try {
            final String[] fileNameSplit = String.valueOf(requestJSON.get("file_name")).split("\\.");
            final String strContentType = (fileNameSplit.length > 1) ? fileNameSplit[fileNameSplit.length - 1] : "";
            final String fileName = Long.toString(MDMUtil.getCurrentTime()) + "." + strContentType;
            final UUID randomid = UUID.randomUUID();
            final Long customerId = APIUtil.optCustomerID(requestJSON);
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            String folderPath = null;
            if (customerId < 0L) {
                folderPath = serverHome + File.separator + "api_temp_downloads" + File.separator + randomid;
            }
            else {
                folderPath = serverHome + File.separator + "api_temp_downloads" + File.separator + customerId + File.separator + randomid;
            }
            ApiFactoryProvider.getFileAccessAPI().createDirectory(folderPath);
            final String completedFileName = folderPath + File.separator + fileName;
            ApiFactoryProvider.getFileAccessAPI().writeFile(completedFileName, fileInStream);
            final JSONObject addDMFileJSON = new JSONObject();
            addDMFileJSON.put("file_availability_status", 2);
            addDMFileJSON.put("remarks", (Object)I18N.getMsg("dc.mdmod.file_upload_success", new Object[0]));
            addDMFileJSON.put("file_system_location", (Object)completedFileName);
            addDMFileJSON.put("expiry_offset", 600000L);
            if (!fromAgent) {
                this.validateRolesForMSPCustomer(requestJSON);
            }
            addDMFileJSON.put("CUSTOMER_ID", (Object)customerId);
            addDMFileJSON.put("content_type", (Object)String.valueOf(requestJSON.get("content_type")));
            addDMFileJSON.put("content_length", (Object)String.valueOf(requestJSON.get("content_length")));
            addDMFileJSON.put("file_name", (Object)requestJSON.optString("file_name", "--"));
            final Long fileId = new FileUploadManager().addOrUpdateDMFiles(addDMFileJSON);
            final Long expiryTime = MDMUtil.getCurrentTimeInMillis() + 600000L;
            requestJSON.remove("content");
            requestJSON.put("expiry_time", (Object)expiryTime);
            requestJSON.put("file_id", (Object)String.valueOf(fileId));
            requestJSON.put("file_path", (Object)completedFileName);
            return requestJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error while adding file", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (fileInStream != null) {
                fileInStream.close();
            }
        }
    }
    
    protected void validateRolesForMSPCustomer(final JSONObject requestJSON) throws Exception {
        final Long customerId = APIUtil.optCustomerID(requestJSON);
        final Long currentLoginId = APIUtil.getLoginID(requestJSON);
        final Boolean currentAdmin = DMUserHandler.isUserInRole(currentLoginId, "MDM_Settings_Write");
        if (customerId < 0L && (!CustomerInfoUtil.getInstance().isMSP() || !currentAdmin)) {
            throw new APIHTTPException("COM0022", new Object[0]);
        }
    }
    
    public void writeFile(final String fileName, final byte[] content) throws Exception {
        FileOutputStream fos = null;
        if (!this.testForPathTraversal(fileName)) {
            this.logger.log(Level.WARNING, "Attack Attempt, input file name was {0}", fileName);
            throw new Exception("Attack Attempt, not writing File");
        }
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
            fos.write(content);
        }
        catch (final IOException e) {
            this.logger.log(Level.WARNING, "Exception occurred while writing file", e);
            throw e;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e2) {
                    this.logger.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                }
            }
        }
    }
    
    public void writeFile(final String fileName, final InputStream in) throws Exception {
        FileOutputStream fos = null;
        if (!this.testForPathTraversal(fileName)) {
            this.logger.log(Level.WARNING, "Attack Attempt, input file name was {0}", fileName);
            throw new Exception("Attack Attempt, not writing File");
        }
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
            final byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
        catch (final IOException e) {
            this.logger.log(Level.WARNING, "Exception occurred while writing file", e);
            throw e;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e2) {
                    this.logger.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                }
                in.close();
            }
        }
    }
    
    public boolean deleteFile(final String pathName) {
        if (this.isSafePathToDelete(pathName)) {
            final File path = new File(pathName);
            try {
                if (path.isFile()) {
                    return path.delete();
                }
                if (path.exists()) {
                    final File[] files = path.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; ++i) {
                            if (files[i].isDirectory()) {
                                this.deleteFile(files[i].toString());
                            }
                            else {
                                files[i].delete();
                            }
                        }
                    }
                    return path.delete();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, e, () -> "Exception occurred while deleting parent of file " + file);
            }
        }
        return true;
    }
    
    public String getTempLocation(final String filePath) {
        String result = null;
        final int lastIndex = filePath.lastIndexOf(File.separator);
        result = filePath.substring(0, lastIndex) + File.separator + "temp" + filePath.substring(lastIndex);
        return result;
    }
    
    public String getModifiedTempLocation(final String filePath) {
        String result = null;
        final int lastIndex = filePath.lastIndexOf(File.separator);
        final String fileExt = this.getFileExtension(filePath);
        result = filePath.substring(0, lastIndex) + File.separator + "temp" + File.separator + System.currentTimeMillis() + "." + fileExt;
        return result;
    }
    
    public String getLocalPathForFileID(final Long fileID) throws APIHTTPException {
        try {
            final String filePath = FileUploadManager.getFilePath(fileID);
            this.writeFile(filePath, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath));
            return filePath;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception occurred in getLocalPathForFileID()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void verifyFileContentType(final JSONObject requestJSON, final File file) throws Exception {
        final Tika tika = new Tika();
        final FileInputStream inputStream = new FileInputStream(file);
        try {
            final String fileName = requestJSON.optString("file_name", "--");
            final String tikaContentType = tika.detect((InputStream)inputStream, fileName);
            final String strContentType = tika.detect(fileName);
            final String contentType = requestJSON.getString("content_type");
            this.logger.log(Level.INFO, "fileName:{0} tikaContentType:{1} strContentType:{2} contentType:{3}", new Object[] { fileName, tikaContentType, strContentType, contentType });
            if (contentType == null) {
                throw new APIHTTPException("FIL0001", new Object[] { "Content type not Allowed for upload" });
            }
            if (APIUtil.isAllowedZipMimeType(tikaContentType) && !this.isSafeZip(file, contentType, fileName)) {
                throw new APIHTTPException("FIL0001", new Object[] { "Corrupted File." });
            }
            if (!this.allowTikaUndetectedContent(fileName, tikaContentType, contentType) && !this.isValidFileContent(tikaContentType, strContentType, contentType)) {
                throw new APIHTTPException("FIL0001", new Object[] { "Corrupted File." });
            }
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    private boolean isValidFileContent(final String tikaContentType, final String strContentType, final String providedContentType) {
        final MediaType strMediaType = MediaType.parse(strContentType);
        final MediaType tikaMediaType = MediaType.parse(tikaContentType);
        return (tikaContentType.equalsIgnoreCase(providedContentType) && strContentType.equalsIgnoreCase(providedContentType)) || MediaTypeRegistry.getDefaultRegistry().isSpecializationOf(strMediaType, tikaMediaType) || strContentType.equalsIgnoreCase("application/octet-stream");
    }
    
    private boolean allowTikaUndetectedContent(final String fileName, final String tikacontentType, final String clientContentType) {
        for (final Map.Entry<String, List<String>> entry : FileFacade.linkedContentType.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(clientContentType) && entry.getValue().contains(tikacontentType)) {
                return true;
            }
        }
        return ((fileName.endsWith(".crt") || fileName.endsWith(".cer") || fileName.endsWith(".key") || fileName.endsWith(".p7m") || fileName.endsWith(".p7b") || fileName.endsWith(".ovpn")) && tikacontentType.equalsIgnoreCase("text/plain")) || fileName.endsWith(".pkg") || fileName.endsWith(".ini");
    }
    
    public String getFileExtension(final String filePath) {
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }
    
    public String getContentTypeFromFileExtension(String extension) {
        String contentType = "";
        extension = "." + extension;
        final Tika tika = new Tika();
        contentType = tika.detect(extension);
        return contentType;
    }
    
    public String validateIfExistsAndReturnFilePath(final Long fileId, final Long customerId) throws APIHTTPException {
        if (fileId == null || fileId == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            final SelectQuery selectQuery = this.getFileBaseQuery(customerId);
            Criteria criteria = new Criteria(new Column("DMFiles", "FILE_ID"), (Object)fileId, 0);
            if (selectQuery.getCriteria() != null) {
                criteria = criteria.and(selectQuery.getCriteria());
            }
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("DMFiles", "FILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DMFiles", "FILE_SYSTEM_LOCATION"));
            selectQuery.addSelectColumn(Column.getColumn("DMFiles", "FILE_STATUS"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { "file_id: " + fileId });
            }
            final int fileStatus = (int)dataObject.getFirstValue("DMFiles", "FILE_STATUS");
            if (fileStatus != 2) {
                throw new APIHTTPException("COM0014", new Object[] { (fileStatus == 2) ? I18NUtil.transformRemarks("mdm.file.unavailable.reason", (String)dataObject.getFirstValue("DMFiles", "FILE_STATUS_REMARKS")) : I18NUtil.transformRemarks("mdm.file.unavailabel.status", "") });
            }
            final String fileName = (String)dataObject.getFirstValue("DMFiles", "FILE_SYSTEM_LOCATION");
            return fileName;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while validating File", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public String validateContentTypeGetFilePath(final Long fileId, final Long customerId, final String contentType) throws APIHTTPException, IOException {
        InputStream inputStream = null;
        try {
            final String filePath = this.validateIfExistsAndReturnFilePath(fileId, customerId);
            final Tika tika = new Tika();
            inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            final String tikaContentType = tika.detect(inputStream);
            if (APIUtil.isAllowedContentType(tikaContentType) && !contentType.equalsIgnoreCase(tikaContentType)) {
                throw new APIHTTPException("COM0015", new Object[] { "File Id is rejected for unknown content type" });
            }
            return filePath;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception while validating File and Content type", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while validating File and Content type", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    public SelectQuery getFileBaseQuery(final Long customerId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMFiles"));
        final Criteria criteria = new Criteria(Column.getColumn("DMFiles", "CUSTOMER_ID"), (Object)customerId, 0).and(new Criteria(Column.getColumn("DMFiles", "EXPIRY_TIME"), (Object)MDMUtil.getCurrentTimeInMillis(), 5));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("DMFiles", "FILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DMFiles", "FILE_SYSTEM_LOCATION"));
        selectQuery.addSelectColumn(Column.getColumn("DMFiles", "FILE_STATUS"));
        return selectQuery;
    }
    
    public boolean isSafePathToDelete(final String path) {
        this.logger.log(Level.INFO, "Path for validation {0}", path);
        final List<String> safePaths = new ArrayList<String>();
        safePaths.add(File.separator + "MDM" + File.separator + "apprepository" + File.separator);
        safePaths.add(File.separator + "api_temp_downloads" + File.separator);
        safePaths.add(File.separator + "temp" + File.separator);
        safePaths.add(File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "server-data");
        for (final String safePath : safePaths) {
            if (APIUtil.isValidFileName(path) && path.contains(safePath) && this.testForPathTraversal(path)) {
                return true;
            }
        }
        return false;
    }
    
    public Boolean testForPathTraversal(final String filePath) {
        Boolean allow = Boolean.FALSE;
        if (filePath.contains("../") || filePath.contains("..\\")) {
            if (filePath.lastIndexOf("../") == 0 || filePath.lastIndexOf("..\\") == 0) {
                allow = Boolean.TRUE;
            }
        }
        else {
            allow = Boolean.TRUE;
        }
        return allow;
    }
    
    public void validateFileToUnzip(final String filePath, final String destFolder) throws IOException, SecurityException {
        if (!this.testForPathTraversal(filePath)) {
            this.logger.log(Level.SEVERE, "Path traversal found in file path {0}", new Object[] { filePath });
            throw new SecurityException();
        }
        final File file = new File(filePath);
        final File destPathDir = new File(destFolder);
        if (!file.getCanonicalPath().startsWith(destPathDir.getCanonicalPath())) {
            this.logger.log(Level.SEVERE, "Path traversal found in zip extract");
            throw new SecurityException();
        }
    }
    
    private ZipSanitizerRule getZipSanitizerRule(final String contentType, final String fileName) throws Exception {
        final String apkContentType = "application/vnd.android.package-archive";
        final String zipContentType = "application/zip";
        switch (contentType) {
            case "application/vnd.android.package-archive": {
                final String allowedExtensions = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("ZIPSANITISER_APK_ALLOWED_EXTENSIONS");
                final String[] allowedExtensionsArr = allowedExtensions.split(",");
                final List<String> extensionsTobeAllowed = new ArrayList<String>(Arrays.asList(allowedExtensionsArr));
                if (extensionsTobeAllowed.contains("dll")) {
                    extensionsTobeAllowed.remove("dll");
                }
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowDllInApkFile") && !extensionsTobeAllowed.contains("dll")) {
                    extensionsTobeAllowed.add("dll");
                }
                final ZipSanitizerRule zipSanitizerRule = new ZipSanitizerRule();
                zipSanitizerRule.getBlockedExtensions().removeAll(extensionsTobeAllowed);
                final ZipSanitizerRule apkSanitizer = new ZipSanitizerRule("apkSanitizer", (String)null, -1L, -1, 25000, (List)null, zipSanitizerRule.getBlockedExtensions(), (Pattern)null, (Pattern)null);
                return apkSanitizer;
            }
            case "application/zip": {
                ZipSanitizerRule zipSanitizer = new ZipSanitizerRule();
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ZipFileExtractLimit")) {
                    final long fileExtractSizeLimit = Long.parseLong(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("ZIP_SIZE_LIMIT"));
                    zipSanitizer = new ZipSanitizerRule("zipSanitizer", (String)null, fileExtractSizeLimit, -1, 25000, (List)null, zipSanitizer.getBlockedExtensions(), (Pattern)null, zipSanitizer.getBlockedContentTypes());
                }
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowExtnsInZipFile")) {
                    final String zipAllowedExtensions = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("ZIPSANITISER_ALLOWED_EXTENSIONS");
                    final String[] zipAllowedExtensionsArr = zipAllowedExtensions.split(",");
                    final List<String> extensionsTobeAllowed = new ArrayList<String>(Arrays.asList(zipAllowedExtensionsArr));
                    zipSanitizer.getBlockedExtensions().removeAll(extensionsTobeAllowed);
                }
                if (fileName.endsWith(".appx")) {
                    final List<String> allowedAppxExtensions = new ArrayList<String>();
                    allowedAppxExtensions.add("dll");
                    allowedAppxExtensions.add("exe");
                    final ZipSanitizerRule zipSanitizerRules = new ZipSanitizerRule();
                    zipSanitizerRules.getBlockedExtensions().removeAll(allowedAppxExtensions);
                    final ZipSanitizerRule appxSantizer = new ZipSanitizerRule("appxSanitizer", (String)null, -1L, -1, 25000, (List)null, zipSanitizerRules.getBlockedExtensions(), (Pattern)null, (Pattern)null);
                    return appxSantizer;
                }
                return zipSanitizer;
            }
            default: {
                return new ZipSanitizerRule();
            }
        }
    }
    
    private boolean isSafeZip(final File inputFile, final String contentType, final String fileName) {
        File tempDir = null;
        File destinationFile = null;
        String destinationFileName = null;
        String tempFolderPath = null;
        try {
            if (!contentType.equalsIgnoreCase("application/vnd.android.package-archive") || !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("IgnoreZipValidationForAPK")) {
                final UUID randomid = UUID.randomUUID();
                final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
                tempFolderPath = serverHome + File.separator + "api_temp_downloads" + File.separator + randomid;
                tempDir = new File(tempFolderPath);
                tempDir.mkdirs();
                destinationFileName = tempFolderPath + File.separator + System.currentTimeMillis() + ".zip";
                destinationFile = new File(destinationFileName);
                ZSecZipSanitizer.extract(this.getZipSanitizerRule(contentType, fileName), inputFile, destinationFile, ZSecConstants.DESTINATION_TYPE.ZIP);
            }
            return Boolean.TRUE;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Invalid apk/zip due to following reasons", e);
            return Boolean.FALSE;
        }
        finally {
            try {
                if (destinationFile != null && destinationFile.exists()) {
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(destinationFileName);
                }
                if (tempDir != null && tempDir.exists()) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(tempFolderPath);
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.WARNING, "Cannot delete temp files created during zip sanitizer on FileFacade", e2);
            }
        }
    }
    
    static {
        FileFacade.instance = null;
        FileFacade.linkedContentType = null;
    }
}
