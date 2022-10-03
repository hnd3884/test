package com.me.devicemanagement.framework.webclient.admin.certificate;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.security.auth.x500.X500Principal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import java.util.Iterator;
import java.util.Date;
import com.me.devicemanagement.framework.webclient.file.FormFile;
import java.util.ArrayList;
import java.io.File;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;

public class CertificateUtil
{
    private static CertificateUtil certificateUtil;
    private static String sourceClass;
    private static FileAccessAPI fileAccessAPI;
    private static String webServerLocation;
    private static Logger logger;
    public static final String BEGINSTRING = "BEGIN CERTIFICATE";
    public static final String ENDSTRING = "END CERTIFICATE";
    public static final String SERVERCRTLOC = "server.crt.loc";
    public static final String APACHESERVERCRT = "apache.crt.loc";
    
    public static CertificateUtil getInstance() {
        if (CertificateUtil.certificateUtil == null) {
            CertificateUtil.certificateUtil = new CertificateUtil();
        }
        return CertificateUtil.certificateUtil;
    }
    
    public X509Certificate generateCertificateFromFile(final String serverCertificateFile) {
        final String sourceMethod = "generateCertificateFromFile";
        X509Certificate cert = null;
        InputStream in = null;
        try {
            final byte[] certificateBytes = CertificateUtil.fileAccessAPI.readFileContentAsArray(serverCertificateFile);
            final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            in = new ByteArrayInputStream(certificateBytes);
            cert = (X509Certificate)certFactory.generateCertificate(in);
        }
        catch (final CertificateException ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Tool doesn't support this certificate..Given certificate is not proper/corrupted..Certificate generation failed..", ex);
        }
        catch (final IOException ex2) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Reading the file " + serverCertificateFile + " failed", ex2);
        }
        catch (final Exception ex3) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Couldn't read the file " + serverCertificateFile + ".. it may be in use/not accessible.." + "Certificate generation failed..", ex3);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex4) {
                CertificateUtil.logger.log(Level.INFO, "Exceptin in closing the stream..you canignore this..");
            }
        }
        return cert;
    }
    
    public HashMap downloadFile(final String theLink, final String downloadFilePath) {
        final HashMap map = new HashMap();
        if (theLink.startsWith("ldap")) {
            map.put("ldap", "true");
            map.put("downloadLink", theLink);
            map.put("isSuccess", Boolean.FALSE);
            return map;
        }
        Boolean isSuccess = Boolean.FALSE;
        final DownloadManager downloadMgr = DownloadManager.getInstance();
        try {
            CertificateUtil.fileAccessAPI.writeFile(downloadFilePath, "0".getBytes());
        }
        catch (final IOException ex) {
            CertificateUtil.logger.log(Level.SEVERE, downloadFilePath + " File creation failed..", ex);
        }
        catch (final Exception ex2) {
            CertificateUtil.logger.log(Level.SEVERE, downloadFilePath + " File creation failed..", ex2);
        }
        final DownloadStatus downloadStatus = downloadMgr.downloadFile(theLink, downloadFilePath, new SSLValidationType[0]);
        final int statusCode = downloadStatus.getStatus();
        if (statusCode == 0) {
            CertificateUtil.logger.log(Level.INFO, "Successfully Downloaded the  File intermediate.cer from the link: " + theLink);
            isSuccess = Boolean.TRUE;
        }
        else {
            CertificateUtil.logger.log(Level.INFO, " Download Failed for  File : intermediate.cer for the link " + theLink);
            CertificateUtil.logger.info("Error message " + downloadStatus.getErrorMessage());
            CertificateUtil.logger.info("Error message key " + downloadStatus.getErrorMessageKey());
            isSuccess = Boolean.FALSE;
            map.put("downloadLink", theLink);
        }
        map.put("isSuccess", isSuccess);
        map.put("downloadStatus", downloadStatus);
        return map;
    }
    
    public void deleteUploadDirectory() {
        final String sourceMethod = "deleteUploadDirectory";
        try {
            CertificateUtil.fileAccessAPI.deleteDirectory(CertificateUtil.webServerLocation + File.separator + "conf" + File.separator + "uploaded_files");
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "uploaded directory deletion failed..", ex);
        }
    }
    
    public boolean uploadListOfFiles(final String uploadFilePath, final ArrayList<FormFile> filesToUploadList) {
        CertificateUtil.logger.info("");
        CertificateUtil.logger.info("-----------------------START--------------------------");
        CertificateUtil.logger.info("Importing SSL Certificate Process started at " + new Date());
        if (!filesToUploadList.isEmpty()) {
            CertificateUtil.logger.info("Following are the entries provided by the customer..");
            for (final FormFile fileToUpload : filesToUploadList) {
                if (!this.uploadFile(uploadFilePath, fileToUpload)) {
                    return false;
                }
                CertificateUtil.logger.info(fileToUpload.getFileName());
            }
            return true;
        }
        return false;
    }
    
    public boolean uploadFile(final String uploadFilePath, final FormFile fileToUpload) {
        final String sourceMethod = "uploadFile";
        OutputStream fout = null;
        try {
            if (!CertificateUtil.fileAccessAPI.isFileExists(uploadFilePath)) {
                CertificateUtil.fileAccessAPI.createDirectory(uploadFilePath);
            }
            final byte[] fileBytes = IOUtils.toByteArray(fileToUpload.getInputStream());
            fout = CertificateUtil.fileAccessAPI.writeFile(uploadFilePath + File.separator + fileToUpload.getFileName());
            IOUtils.write(fileBytes, fout);
            fout.flush();
            IOUtils.closeQuietly(fout);
            fout.close();
            return true;
        }
        catch (final IOException ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, "uploadFile", "Failed to upload the file " + fileToUpload.getFileName() + " given", ex);
            return false;
        }
        catch (final Exception ex2) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, "uploadFile", "Couldn't upload the file..file may have restricted access ", ex2);
            return false;
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (final Exception ex3) {
                CertificateUtil.logger.severe("Failed to close the stream..you can ignore this..");
            }
        }
    }
    
    public String getFileFromUploadDirectory(final String fileName) throws IOException {
        final String sourceMethod = "getFileFromUploadDirectory";
        try {
            final String uploadedFile = CertificateUtil.webServerLocation + File.separator + "conf" + File.separator + "uploaded_files" + File.separator + fileName;
            if (CertificateUtil.fileAccessAPI.isFileExists(uploadedFile) && !CertificateUtil.fileAccessAPI.isDirectory(uploadedFile)) {
                return uploadedFile;
            }
            if (fileName != "") {
                CertificateUtil.logger.severe(fileName + " file not found in uploaded_files directory");
            }
            return null;
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Getting the file " + fileName + " from uploads directory failed..file may be in use/not available..", ex);
            return null;
        }
    }
    
    public String getExtension(final String certificateFile) {
        final String certificateName = this.getNameOfTheFile(certificateFile);
        if (certificateName.lastIndexOf(".") == -1 && certificateName.length() >= 5) {
            return certificateName.substring(certificateName.length() - 4, certificateName.length());
        }
        if (certificateName.contains(".")) {
            return certificateName.substring(certificateName.lastIndexOf("."));
        }
        return certificateName;
    }
    
    public String changeExtension(final String interOrRootFile) {
        final String sourceMethod = "changeExtension";
        final String parentDirectory = this.getParentDirectory(interOrRootFile);
        try {
            CertificateUtil.logger.info("Changing the extension of intermediate or root file");
            final String renamedFile = parentDirectory + File.separator + FilenameUtils.removeExtension(this.getNameOfTheFile(interOrRootFile)) + ".crt";
            if (CertificateUtil.fileAccessAPI.renameFolder(interOrRootFile, renamedFile)) {
                return renamedFile;
            }
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Changing extension failed..", ex);
        }
        return null;
    }
    
    boolean createDirectory(final String backUpDirectory) {
        boolean dirCreationStatus = false;
        if (!CertificateUtil.fileAccessAPI.isFileExists(backUpDirectory)) {
            dirCreationStatus = CertificateUtil.fileAccessAPI.createDirectory(backUpDirectory);
        }
        return dirCreationStatus;
    }
    
    void moveFilesToWorkingDirectory(final ArrayList<String> moveFiles, final String freshDirectory) {
        final String sourceMethod = "moveFilesToWorkingDirectory";
        try {
            if (!CertificateUtil.fileAccessAPI.isFileExists(freshDirectory) && !CertificateUtil.fileAccessAPI.isDirectory(freshDirectory) && this.createDirectory(freshDirectory)) {
                for (final String file : moveFiles) {
                    this.copyToFreshBackUpDirectory(file, freshDirectory);
                }
            }
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Unable to check working directory existence..", ex);
        }
    }
    
    public String getSubDirectory(final String backUpDirectory) {
        String subDirectory = null;
        subDirectory = backUpDirectory + File.separator + this.getSubDirectoryName(backUpDirectory);
        if (this.createDirectory(subDirectory)) {
            CertificateUtil.logger.log(Level.INFO, subDirectory + " created successfully..");
            return subDirectory;
        }
        CertificateUtil.logger.log(Level.INFO, subDirectory + " not created..");
        return null;
    }
    
    String copyToSubBackUpDirectory(final String fileFromProperty, final String backUpDirectory, final boolean dirCreationStatus) {
        final String sourceMethod = "copyToSubBackUpDirectory";
        if (this.checkExistenceOfFile(fileFromProperty)) {
            if (!CertificateUtil.fileAccessAPI.isFileExists(backUpDirectory)) {
                if (!dirCreationStatus) {
                    return null;
                }
            }
            try {
                this.copyFileSrcToDest(fileFromProperty, backUpDirectory + File.separator + this.getNameOfTheFile(fileFromProperty));
                this.backUpFiles(backUpDirectory, this.getNameOfTheFile(fileFromProperty));
                return fileFromProperty;
            }
            catch (final NullPointerException ex) {
                CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Copying files interrupted.. File may not exist/privileges not granted..", ex);
            }
        }
        return null;
    }
    
    public boolean deleteFiles(final ArrayList<String> fileToBeDeleted) {
        final String sourceMethod = "deleteFiles";
        boolean deletionFlag = true;
        if (!fileToBeDeleted.isEmpty()) {
            final Iterator<String> it = fileToBeDeleted.iterator();
            while (it.hasNext()) {
                try {
                    final String toDeleteFileName = it.next();
                    CertificateUtil.logger.info("Going to delete the file " + toDeleteFileName + " the file exist ." + CertificateUtil.fileAccessAPI.isFileExists(toDeleteFileName));
                    if (CertificateUtil.fileAccessAPI.deleteFile(toDeleteFileName)) {
                        continue;
                    }
                    deletionFlag = false;
                    continue;
                }
                catch (final Exception ex) {
                    CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "unable to delete the file " + fileToBeDeleted + " file may be in use/ not accessible..", ex);
                    return false;
                }
                break;
            }
        }
        CertificateUtil.logger.info("Result delete flag " + deletionFlag);
        return deletionFlag;
    }
    
    public void copySSLToConf(final String confDirectory, final String certificateFile) {
        final String sourceMethod = "copySSLToConf";
        try {
            this.copyFileSrcToDest(certificateFile, confDirectory + File.separator + this.getNameOfTheFile(certificateFile));
        }
        catch (final NullPointerException ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, certificateFile + " File doesn't exist..", ex);
        }
    }
    
    public Map<String, String> propertiesToMap(final Properties props) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        final Enumeration<Object> keyEnumerator = ((Hashtable<Object, V>)props).keys();
        while (keyEnumerator.hasMoreElements()) {
            final String s = keyEnumerator.nextElement();
            hashMap.put(s, props.getProperty(s));
        }
        return hashMap;
    }
    
    public Properties mapToProperties(final Map<String, String> map) {
        final Properties p = new Properties();
        final Set<Map.Entry<String, String>> set = map.entrySet();
        for (final Map.Entry<String, String> keyValuePair : set) {
            p.setProperty(keyValuePair.getKey(), keyValuePair.getValue());
        }
        return p;
    }
    
    private int extractNumber(final String fileName) {
        int i = 0;
        try {
            final int s = fileName.indexOf(95) + 1;
            final String number = fileName.substring(s, fileName.length());
            i = Integer.parseInt(number);
        }
        catch (final Exception e) {
            i = 0;
        }
        return i;
    }
    
    private String getSubDirectoryName(final String backUpDirectory) {
        List<String> fileList = new ArrayList<String>();
        fileList = this.getDirectoriesIn(backUpDirectory);
        String lastFileName = null;
        int extractedNumber = 0;
        int fileListLength = 0;
        Label_0118: {
            if (!fileList.isEmpty()) {
                Collections.sort(fileList, new Comparator<String>() {
                    @Override
                    public int compare(final String fileName, final String fileNameToCompare) {
                        final int n1 = CertificateUtil.this.extractNumber(CertificateUtil.this.getNameOfTheFile(fileName));
                        final int n2 = CertificateUtil.this.extractNumber(CertificateUtil.this.getNameOfTheFile(fileNameToCompare));
                        return n1 - n2;
                    }
                });
                boolean found = true;
                int lastIndexToSubtract = 1;
                fileListLength = fileList.size();
                while (lastIndexToSubtract != fileListLength) {
                    lastFileName = this.getNameOfTheFile(fileList.get(fileListLength - lastIndexToSubtract));
                    if (lastFileName.matches(".*\\d.*")) {
                        extractedNumber = this.extractNumber(lastFileName);
                        found = false;
                    }
                    else {
                        ++lastIndexToSubtract;
                    }
                    if (!found) {
                        break Label_0118;
                    }
                }
                lastFileName = null;
            }
        }
        int subFolderNumber = (lastFileName == null) ? 1 : (extractedNumber + 1);
        boolean isDirectoryExist = true;
        do {
            if (CertificateUtil.fileAccessAPI.isFileExists(backUpDirectory + File.separator + "backup_" + subFolderNumber)) {
                ++subFolderNumber;
            }
            else {
                isDirectoryExist = false;
            }
        } while (isDirectoryExist);
        return "backup_" + subFolderNumber;
    }
    
    public ArrayList<String> getDirectoriesIn(final String directory) {
        final String sourceMethod = "getDirectoriesIn";
        try {
            final ArrayList<String> allFilesInDirectory = CertificateUtil.fileAccessAPI.getAllFilesList(directory, null, null);
            final ArrayList<String> directoriesOnly = new ArrayList<String>();
            for (final String path : allFilesInDirectory) {
                final String parentPath = this.getParentDirectory(path);
                if (!directoriesOnly.contains(parentPath) || directoriesOnly.isEmpty()) {
                    directoriesOnly.add(parentPath);
                }
            }
            return directoriesOnly;
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "File list in the specified" + directory + " folder can't be retrieved..", ex);
            return null;
        }
    }
    
    private void copyToFreshBackUpDirectory(final String fileFromProperty, final String freshDirectory) {
        final String sourceMethod = "copyToFreshBackUpDirectory";
        if (this.checkExistenceOfFile(fileFromProperty)) {
            try {
                CertificateUtil.logger.log(Level.INFO, "Moving the Existing copy  of " + this.getNameOfTheFile(fileFromProperty) + " to freshcertificate directory initiated..");
                this.copyFileSrcToDest(fileFromProperty, freshDirectory + File.separator + this.getNameOfTheFile(fileFromProperty));
            }
            catch (final NullPointerException ex) {
                CertificateUtil.logger.info(" Null pointer exception in copying to the fresh back up directory.");
            }
        }
        else {
            CertificateUtil.logger.log(Level.SEVERE, fileFromProperty + " File not found to copy to the freshcertificate directory..Not a Big issue..");
        }
    }
    
    public void copyFileSrcToDest(final String source, final String dest) {
        final String sourceMethod = "copyFileSrcToDest";
        try {
            if (!CertificateUtil.fileAccessAPI.copyFile(source, dest)) {
                CertificateUtil.logger.info("copy failed from source " + source + " to " + dest);
            }
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "Copy failed.. file may be in use or not accessible", ex);
        }
    }
    
    private void backUpFiles(final String certificateBackUpDirectory, final String oldPropertyFileName) {
        final String oldPropertyFilePath = certificateBackUpDirectory + File.separator + oldPropertyFileName;
        if (this.checkExistenceOfFile(oldPropertyFilePath)) {
            final String extension = this.getExtension(oldPropertyFilePath);
            final String backUpName = oldPropertyFileName.substring(0, oldPropertyFileName.length() - 4);
            final String newBackUpFile = certificateBackUpDirectory + File.separator + backUpName + "_back" + extension;
            try {
                this.renameFile(oldPropertyFilePath, newBackUpFile);
            }
            catch (final IOException ex) {
                CertificateUtil.logger.log(Level.SEVERE, "Rename not done.. file may have been in use/not accessible..", ex);
            }
        }
    }
    
    public boolean checkExistenceOfFile(final String file) {
        final String sourceMethod = "checkExistenceOFFile";
        try {
            return !CertificateUtil.fileAccessAPI.isDirectory(file) && CertificateUtil.fileAccessAPI.isFileExists(file);
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, " checking existence of the file " + file + " failed..file may be in use/not available", ex);
            return false;
        }
    }
    
    public boolean renameFile(final String beforeFileName, final String afterFileName) throws IOException {
        final String sourceMethod = "renameFile";
        if (CertificateUtil.fileAccessAPI.isFileExists(afterFileName)) {
            throw new IOException("file already exists..can't be renamed.. Delete all the files from \\conf folder..");
        }
        boolean success = false;
        try {
            success = CertificateUtil.fileAccessAPI.renameFolder(beforeFileName, afterFileName);
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, "File renaming failed..file may be in use/not accessible", ex);
        }
        return success;
    }
    
    public static String trimCertificate(final String certificateFile) throws IOException {
        final String sourceMethod = "trimCertificate";
        CertificateUtil.logger.info("Trimming certificate process began..");
        BufferedReader certificateReader = null;
        InputStreamReader inputStreamReader = null;
        boolean beginCertificateLabelFound = false;
        StringBuilder certificateContent = null;
        try {
            certificateContent = new StringBuilder();
            String line = null;
            final String newLineChar = "\n";
            inputStreamReader = new InputStreamReader(CertificateUtil.fileAccessAPI.readFile(certificateFile));
            certificateReader = new BufferedReader(inputStreamReader);
            while ((line = certificateReader.readLine()) != null) {
                if (line.contains("BEGIN CERTIFICATE")) {
                    beginCertificateLabelFound = true;
                }
                if (beginCertificateLabelFound) {
                    if (line.contains("BEGIN CERTIFICATE")) {
                        certificateContent.append(line);
                        certificateContent.append(newLineChar);
                    }
                    else if (line.contains("END CERTIFICATE")) {
                        certificateContent.append(newLineChar);
                        certificateContent.append(line);
                    }
                    else {
                        line = line.replace("\n", "");
                        certificateContent.append(line);
                    }
                }
            }
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, certificateFile + " file may be in use/not available.." + "Trimming certificate failed..", ex);
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (certificateReader != null) {
                    certificateReader.close();
                }
            }
            catch (final IOException e) {
                CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, certificateFile + "file may be in " + "use/not available.. closing the stream failed..", e);
            }
            catch (final Exception e2) {
                CertificateUtil.logger.logp(Level.INFO, CertificateUtil.sourceClass, sourceMethod, "Exception in closing the stream");
            }
        }
        finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (certificateReader != null) {
                    certificateReader.close();
                }
            }
            catch (final IOException e3) {
                CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, certificateFile + "file may be in " + "use/not available.. closing the stream failed..", e3);
            }
            catch (final Exception e4) {
                CertificateUtil.logger.logp(Level.INFO, CertificateUtil.sourceClass, sourceMethod, "Exception in closing the stream");
            }
        }
        if (beginCertificateLabelFound) {
            return certificateContent.toString();
        }
        CertificateUtil.logger.info("Trimming certificate failed.. BEGIN CERTIFICATE LABEL NOT FOUND..");
        return null;
    }
    
    public String getNameOfTheFile(final String serverCertificateFile) {
        return serverCertificateFile.substring(serverCertificateFile.lastIndexOf(File.separator) + 1, serverCertificateFile.length());
    }
    
    public String getParentDirectory(final String interOrRootFile) {
        return interOrRootFile.substring(0, interOrRootFile.lastIndexOf(File.separator));
    }
    
    boolean moveExtractedPFXFilesToConfDirectory(final String uploadedDirectory) {
        final String sourceMethod = "moveExtractedPFXFilesToConfDirectory";
        try {
            final String confDirectory = CertificateUtil.webServerLocation + File.separator + "conf";
            this.checkAndMoveFileToConfDir(uploadedDirectory + File.separator + "server.crt", confDirectory);
            this.checkAndMoveFileToConfDir(uploadedDirectory + File.separator + "server.key", confDirectory);
            this.checkAndMoveFileToConfDir(uploadedDirectory + File.separator + "intermediate.crt", confDirectory);
            return true;
        }
        catch (final Exception ex) {
            CertificateUtil.logger.logp(Level.SEVERE, CertificateUtil.sourceClass, sourceMethod, " PFX server.crt server.key copying failed..", ex);
            return false;
        }
    }
    
    public Map getCertificateDetails(final String certificateFilePath) {
        final Map certificateDetails = new HashMap();
        final X509Certificate cert = this.generateCertificateFromFile(certificateFilePath);
        final Date notAfter = cert.getNotAfter();
        final Date notBefore = cert.getNotBefore();
        final Principal subjectPrincipal = cert.getSubjectDN();
        final String subjectName = subjectPrincipal.getName();
        if (cert.getBasicConstraints() != -1) {
            return null;
        }
        if (subjectName != null) {
            final StringTokenizer tokenizer = new StringTokenizer(subjectName, ", ");
            while (tokenizer.hasMoreElements()) {
                final String token = (String)tokenizer.nextElement();
                final String[] strArray = token.split("=");
                if (token.startsWith("CN=")) {
                    certificateDetails.put("CertificateName", strArray[1]);
                }
                else {
                    if (!token.startsWith("UID=")) {
                        continue;
                    }
                    certificateDetails.put("Topic", strArray[1]);
                }
            }
        }
        final X500Principal issuerPrincipal = cert.getIssuerX500Principal();
        final String issuerDistinguishedName = issuerPrincipal.getName();
        if (issuerDistinguishedName != null) {
            final String[] strIssuerNameArray = issuerDistinguishedName.split(",");
            for (int issuerNameIndex = 0; issuerNameIndex < strIssuerNameArray.length; ++issuerNameIndex) {
                final String issuerName = strIssuerNameArray[issuerNameIndex];
                final String[] strArray2 = issuerName.split("=");
                if (issuerName.startsWith("CN=")) {
                    certificateDetails.put("IssuerName", strArray2[1]);
                }
                else if (issuerName.startsWith("OU=")) {
                    certificateDetails.put("IssuerOrganizationalUnitName", strArray2[1]);
                }
                else if (issuerName.startsWith("O=")) {
                    certificateDetails.put("IssuerOrganizationName", strArray2[1]);
                }
            }
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        certificateDetails.put("CreationDate", sdf.format(notBefore));
        certificateDetails.put("ExpiryDate", sdf.format(notAfter));
        return certificateDetails;
    }
    
    private void checkAndMoveFileToConfDir(final String fileToCopy, final String confDirectory) throws Exception {
        if (ApiFactoryProvider.getFileAccessAPI().isFileExists(fileToCopy)) {
            CertificateUtil.fileAccessAPI.copyFile(fileToCopy, confDirectory + File.separator + this.getNameOfTheFile(fileToCopy));
        }
    }
    
    public Boolean copyFileListToDestinationFolder(final List<String> filesToBeCopied, final String destPath) {
        Boolean copiedSuccessfully = Boolean.TRUE;
        try {
            for (final String fileName : filesToBeCopied) {
                final String backUpFileName = destPath + File.separator + fileName.substring(fileName.lastIndexOf(File.separator) + 1);
                copiedSuccessfully = CertificateUtil.fileAccessAPI.copyFile(fileName, backUpFileName);
                if (!copiedSuccessfully) {
                    return copiedSuccessfully;
                }
            }
        }
        catch (final Exception exp) {
            CertificateUtil.logger.log(Level.SEVERE, "Error in CertificateUtil.copyFileListToDestinationFolder {0}", exp);
            copiedSuccessfully = Boolean.FALSE;
        }
        return copiedSuccessfully;
    }
    
    static {
        CertificateUtil.certificateUtil = null;
        CertificateUtil.sourceClass = "CertificateUtil";
        CertificateUtil.fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
        CertificateUtil.webServerLocation = System.getProperty("server.home") + File.separator + SSLCertificateUtil.webServerName;
        CertificateUtil.logger = Logger.getLogger("ImportCertificateLogger");
    }
}
