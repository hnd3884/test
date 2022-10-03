package com.zoho.security.validator.zip;

import java.util.regex.Matcher;
import com.adventnet.iam.security.IAMSecurityException;
import java.io.IOException;
import java.util.logging.Level;
import java.io.File;
import java.util.LinkedList;
import java.util.HashMap;
import java.io.BufferedInputStream;
import com.adventnet.iam.security.ZSecConstants;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class ZipValidator
{
    private static final Logger LOGGER;
    public static final Pattern DOTDOTSLASH_PATTERN;
    int filesCount;
    long filesSize;
    int level;
    ZipSanitizerRule zipSanitizerRule;
    ZSecConstants.DESTINATION_TYPE destinationType;
    BufferedInputStream bufferedIs;
    HashMap<FILE_TO_DELETE, LinkedList<File>> filesToCleanUp;
    boolean isValid;
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    
    public ZipValidator(final ZipSanitizerRule zipSanitizerRule, final ZSecConstants.DESTINATION_TYPE type) {
        this.filesCount = 1;
        this.level = 1;
        this.zipSanitizerRule = null;
        this.destinationType = null;
        this.bufferedIs = null;
        this.filesToCleanUp = new HashMap<FILE_TO_DELETE, LinkedList<File>>();
        this.isValid = true;
        this.zipSanitizerRule = zipSanitizerRule;
        this.destinationType = type;
        this.filesToCleanUp.put(FILE_TO_DELETE.TMPFILES, new LinkedList<File>());
        this.filesToCleanUp.put(FILE_TO_DELETE.ZIPFILES, new LinkedList<File>());
    }
    
    public boolean isValidZip(final ZipSanitizerRule zipSanitizerRule, final ZSecZipEntry zipEntry, final String sanitizedEntryName) throws IOException {
        final String entryName = zipEntry.getName();
        if (!this.validateZipEntryName(entryName, sanitizedEntryName)) {
            ZipValidator.LOGGER.log(Level.SEVERE, "ZIPSLIP_ATTACK IN ENTRY NAME : {0}, ACTION : {1} ", new Object[] { entryName, zipSanitizerRule.getAction() });
            return this.handleAction(zipSanitizerRule, "ZIPSANITIZER_ZIPSLIP_ATTACK");
        }
        if (!zipEntry.isDirectory()) {
            final String fileExtension = this.getFileExtension(sanitizedEntryName);
            if (zipSanitizerRule.getBlockedExtensions() != null && zipSanitizerRule.getBlockedExtensions().contains(fileExtension.toLowerCase())) {
                ZipValidator.LOGGER.log(Level.SEVERE, "BLACKLISTED EXTENSION FOUND {0} , ACTION : {1}", new Object[] { entryName, zipSanitizerRule.getAction() });
                return this.handleAction(zipSanitizerRule, "ZIPSANITIZER_INVALID_FILE_EXTENSION");
            }
            if (zipSanitizerRule.getAllowedExtensions() != null && !zipSanitizerRule.getAllowedExtensions().contains(fileExtension)) {
                ZipValidator.LOGGER.log(Level.SEVERE, "EXTENSION - {0} NOT MATCHED WITH ALLOWED EXTENSTIONS , ACTION : {1}", new Object[] { entryName, zipSanitizerRule.getAction() });
                return this.handleAction(zipSanitizerRule, "ZIPSANITIZER_INVALID_FILE_EXTENSION");
            }
        }
        return true;
    }
    
    private boolean validateZipEntryName(final String entryName, final String sanitizedEntryName) throws IOException {
        if (!entryName.equals(sanitizedEntryName)) {
            return false;
        }
        final File currentDir = new File(".");
        String currentDirPath = currentDir.getCanonicalPath();
        if (!currentDirPath.endsWith(File.separator)) {
            currentDirPath += File.separator;
        }
        final String zipEntryFilePath = new File(currentDir, entryName).getCanonicalPath();
        return zipEntryFilePath.startsWith(currentDirPath);
    }
    
    public boolean checkForSafeZipConditions(final ZipSanitizerRule zipSanitizerRule, final ZSecZipEntry zipEntry) {
        final String entryName = zipEntry.getName();
        if (this.level > zipSanitizerRule.getMax_level()) {
            ZipValidator.LOGGER.log(Level.SEVERE, "ZIP LEVEL (VERTICAL COUNT) EXCEEDED THE MAX-LEVEL {0} , EXCEEDED AT {1} , ACTION : {2}", new Object[] { zipSanitizerRule.getMax_level(), entryName, zipSanitizerRule.getAction() });
            return this.handleAction(zipSanitizerRule, "ZIPSANITIZER_LEVEL_EXCEEDED");
        }
        if (this.filesSize > zipSanitizerRule.getMax_extraction_size() || this.filesSize + zipEntry.getSize() > zipSanitizerRule.getMax_extraction_size()) {
            this.isValid = false;
            ZipValidator.LOGGER.log(Level.SEVERE, "EXTRACTED  FILES SIZE EXCEEDED THE MAX_SIZE {0}  EXCEEDED AT {1}, ACTION : {2} ", new Object[] { zipSanitizerRule.getMax_extraction_size(), entryName, zipSanitizerRule.getAction() });
            return this.handleAction(zipSanitizerRule, "ZIPSANITIZER_FILES_SIZE_EXCEEDED");
        }
        if (this.filesCount > zipSanitizerRule.getMax_files_count()) {
            this.isValid = false;
            ZipValidator.LOGGER.log(Level.SEVERE, "EXTRACTED FILES COUNT EXCEEDED THE MAX-COUNT {0} EXCEEDED AT {1},  ACTION : {2}", new Object[] { zipSanitizerRule.getMax_files_count(), entryName, zipSanitizerRule.getAction() });
            return this.handleAction(zipSanitizerRule, "ZIPSANITIZER_FILES_COUNT_EXCEEDED");
        }
        return true;
    }
    
    private boolean handleAction(final ZipSanitizerRule zipSanitizerRule, final String errorcode) {
        if (zipSanitizerRule.getAction().equals("error")) {
            throw new IAMSecurityException(errorcode);
        }
        return false;
    }
    
    public boolean checkValidContentTypes(final String contentType, final ZSecZipEntry zipEntry) {
        if (this.zipSanitizerRule.getBlockedContentTypes() != null && this.zipSanitizerRule.getBlockedContentTypes().matcher(contentType).matches()) {
            ZipValidator.LOGGER.log(Level.SEVERE, "BLACKLISTED CONTENT-TYPE FOUND {0} , Entry {1}", new Object[] { contentType, zipEntry.getName() });
            return this.handleAction(this.zipSanitizerRule, "ZIPSANITIZER_INVALID_CONTENT_TYPE_FOUND");
        }
        if (this.zipSanitizerRule.getAllowedContentTypes() != null && !this.zipSanitizerRule.getAllowedContentTypes().matcher(contentType).matches()) {
            ZipValidator.LOGGER.log(Level.SEVERE, "CONTENT TYPE - {0} NOT MATCHED WITH ALLOWED CONTENT_TYPES - Entry {1} ", new Object[] { contentType, zipEntry.getName() });
            return this.handleAction(this.zipSanitizerRule, "ZIPSANITIZER_INVALID_CONTENT_TYPE_FOUND");
        }
        return true;
    }
    
    String sanitizeZipEntryName(String entryName) {
        while (entryName.contains(File.separator + ".." + File.separator)) {
            final Matcher matcher = ZipValidator.DOTDOTSLASH_PATTERN.matcher(entryName);
            entryName = matcher.replaceAll("\\" + File.separator);
        }
        if (entryName.startsWith(".." + File.separator)) {
            entryName = entryName.substring(3, entryName.length());
        }
        if (entryName.endsWith(File.separator + "..")) {
            entryName = entryName.substring(0, entryName.length() - 2) + "_";
        }
        else if (entryName.equals("..")) {
            entryName = "_";
        }
        return entryName;
    }
    
    String getName(final String name) {
        final int unixSepIndex = name.lastIndexOf(47);
        final int windowsSepIndex = name.lastIndexOf(92);
        final int index = Math.max(unixSepIndex, windowsSepIndex);
        if (index > -1) {
            return name.substring(index + 1, name.length());
        }
        return name;
    }
    
    private String getFileExtension(String name) {
        name = this.getName(name);
        final int index = name.lastIndexOf(46);
        if (index > -1) {
            return name.substring(index + 1, name.length());
        }
        return "";
    }
    
    public void incrementFileCounters(final ZSecZipEntry zipEntry) {
        ++this.filesCount;
        this.filesSize += zipEntry.getSize();
    }
    
    public void resetBuffer() {
        this.bufferedIs = null;
    }
    
    public boolean isEmptyZip(final File tmpZip, final boolean is7ZipArchive) {
        ZSecZipFile tmpZipFile = null;
        try {
            tmpZipFile = new ZSecZipFile(tmpZip, is7ZipArchive);
            if (is7ZipArchive) {
                return tmpZipFile.get7ZipFile().getNextEntry() == null;
            }
            return tmpZipFile.getZipFile().size() == 0;
        }
        catch (final Exception ex) {
            ZipValidator.LOGGER.log(Level.SEVERE, null, ex.getMessage());
            return true;
        }
        finally {
            try {
                tmpZipFile.close();
            }
            catch (final IOException ioEx) {
                ZipValidator.LOGGER.log(Level.SEVERE, "Error occurred while closing the zip file : {0}", ioEx.getMessage());
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ZipValidator.class.getName());
        DOTDOTSLASH_PATTERN = Pattern.compile("(/\\.\\./)|(\\\\\\.\\.\\\\)");
    }
    
    enum FILE_TO_DELETE
    {
        TMPFILES, 
        ZIPFILES;
    }
}
