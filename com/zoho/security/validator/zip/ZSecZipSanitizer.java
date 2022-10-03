package com.zoho.security.validator.zip;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import com.zoho.security.util.CommonUtil;
import java.util.Enumeration;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.util.zip.ZipException;
import java.util.LinkedList;
import java.io.IOException;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityUtil;
import com.adventnet.iam.security.SecurityFilterProperties;
import com.adventnet.iam.security.ZSecConstants;
import java.io.File;
import java.util.logging.Logger;

public class ZSecZipSanitizer
{
    private static final Logger LOGGER;
    private static final int BUFFER_SIZE = 8096;
    private static final String ZIP_EXTENSION = ".zip";
    
    public static boolean isSafeZip(final ZipSanitizerRule zipSanitizerRule, final File inputFile) {
        try {
            extract(zipSanitizerRule, inputFile, null, ZSecConstants.DESTINATION_TYPE.NONE);
        }
        catch (final Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean isSafeZip(final SecurityFilterProperties securityFilterConfig, final String zipSanitizerName, final File inputFile) {
        try {
            final ZipSanitizerRule zipSanitizerRule = securityFilterConfig.getZipSanitizerRule(zipSanitizerName);
            extract(zipSanitizerRule, inputFile, null, ZSecConstants.DESTINATION_TYPE.NONE);
        }
        catch (final Exception e) {
            return false;
        }
        return true;
    }
    
    public static boolean isSafeZip(final ZipSanitizerRule zipSanitizerRule, final File inputFile, final boolean validate7ZipArchive) {
        try {
            extract(zipSanitizerRule, inputFile, null, ZSecConstants.DESTINATION_TYPE.NONE, validate7ZipArchive);
        }
        catch (final Exception e) {
            return false;
        }
        return true;
    }
    
    public static void extract(final SecurityFilterProperties securityFilterConfig, final String zipSanitizerName, final File inputFile, final File destinationFile) {
        final ZipSanitizerRule zipSanitizerRule = securityFilterConfig.getZipSanitizerRule(zipSanitizerName);
        extract(zipSanitizerRule, inputFile, destinationFile, (destinationFile == null || destinationFile.getName().endsWith(".zip")) ? ZSecConstants.DESTINATION_TYPE.ZIP : ZSecConstants.DESTINATION_TYPE.FOLDER);
    }
    
    public static void extract(final ZipSanitizerRule zipSanitizerRule, final File inputFile, final File destinationFile) {
        extract(zipSanitizerRule, inputFile, destinationFile, destinationFile.getName().endsWith(".zip") ? ZSecConstants.DESTINATION_TYPE.ZIP : ZSecConstants.DESTINATION_TYPE.FOLDER);
    }
    
    public static void extract(final ZipSanitizerRule zipSanitizerRule, final File inputFile, final ZSecConstants.DESTINATION_TYPE destinationType) {
        extract(zipSanitizerRule, inputFile, null, destinationType);
    }
    
    public static void extract(final ZipSanitizerRule zipSanitizerRule, final File inputFile, final ZSecConstants.DESTINATION_TYPE destinationType, final boolean is7ZipArchive) {
        extract(new ZipValidator(zipSanitizerRule, destinationType), inputFile, null, is7ZipArchive);
    }
    
    public static void extract(final ZipSanitizerRule zipSanitizerRule, final File inputFile, final File destinationFile, final ZSecConstants.DESTINATION_TYPE destinationType) {
        extract(zipSanitizerRule, inputFile, destinationFile, destinationType, false);
    }
    
    public static void extract(final ZipSanitizerRule zipSanitizerRule, final File inputFile, final File destinationFile, final ZSecConstants.DESTINATION_TYPE destinationType, final boolean validate7ZipArchive) {
        boolean is7ZipFile = false;
        try {
            is7ZipFile = (validate7ZipArchive && SecurityUtil.is7Zip(SecurityUtil.getMimeTypeUsingTika(inputFile, inputFile.getName())));
        }
        catch (final IOException e) {
            ZSecZipSanitizer.LOGGER.log(Level.SEVERE, "Exception occurred while ZIP sanitization, Message : {0}", e.getMessage());
            throw new IAMSecurityException("ZIPSANITIZER_INVALID_ZIP");
        }
        extract(new ZipValidator(zipSanitizerRule, destinationType), inputFile, destinationFile, is7ZipFile);
    }
    
    private static void extract(final ZipValidator zipValidator, final File inputFile, final File destinationFile, final boolean is7ZipArchive) {
        ZSecZipFile zipSourceFile = null;
        try {
            zipSourceFile = new ZSecZipFile(inputFile, is7ZipArchive);
            extract(zipValidator, zipSourceFile, destinationFile);
        }
        catch (final ZipException e) {
            ZSecZipSanitizer.LOGGER.log(Level.SEVERE, "ZipException thrown while Sanitization , exception  :  ", e);
            cleanUpFiles(zipValidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.ZIPFILES));
            throw new IAMSecurityException("ZIPSANITIZER_UNSUPPORTED_ZIP");
        }
        catch (final IOException e2) {
            ZSecZipSanitizer.LOGGER.log(Level.SEVERE, "Exception occurred while  ZIP sanitization , exception  : ", e2);
            cleanUpFiles(zipValidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.ZIPFILES));
            throw new IAMSecurityException("ZIPSANITIZER_INVALID_ZIP");
        }
        finally {
            if (zipSourceFile != null) {
                try {
                    zipSourceFile.close();
                }
                catch (final IOException e3) {
                    ZSecZipSanitizer.LOGGER.log(Level.SEVERE, "Unable to close the ZIP File, exception : ", e3.getMessage());
                }
            }
        }
    }
    
    public static void extract(final ZipSanitizerRule zipSanitizerRule, final ZSecZipFile inputFile, final File destinationFile, final ZSecConstants.DESTINATION_TYPE destinationType) {
        extract(new ZipValidator(zipSanitizerRule, destinationType), inputFile, destinationFile);
    }
    
    private static void extract(final ZipValidator zipValidator, final ZSecZipFile inputFile, final File destinationFile) {
        if ("sanitize".equals(zipValidator.zipSanitizerRule.getAction()) && zipValidator.destinationType == ZSecConstants.DESTINATION_TYPE.NONE) {
            ZSecZipSanitizer.LOGGER.log(Level.SEVERE, "INVALID ARGUMENTS PASSED : destination type must be either ZIP/FOLDER for action = sanitize");
            throw new IAMSecurityException("INVALID_ARGUMENT(S)_PASSED");
        }
        try {
            switch (zipValidator.destinationType) {
                case FOLDER: {
                    validateAndSanitize(inputFile, zipValidator, destinationFile);
                    break;
                }
                case ZIP: {
                    final File tmpZip = createTmpZip(inputFile.getName(), "SFZEW", zipValidator, destinationFile);
                    validateAndSanitize(inputFile, zipValidator, tmpZip);
                    if (!zipValidator.isEmptyZip(tmpZip, inputFile.is7Zip)) {
                        String finalDestPath = null;
                        if (destinationFile == null) {
                            inputFile.close();
                            finalDestPath = inputFile.getName();
                        }
                        else {
                            finalDestPath = destinationFile.getPath();
                        }
                        Files.move(tmpZip.toPath(), Paths.get(finalDestPath, new String[0]), StandardCopyOption.REPLACE_EXISTING);
                        break;
                    }
                    ZSecZipSanitizer.LOGGER.log(Level.SEVERE, "SANITIZED ZIP  {0} ,FOUND TO BE UNSAFE. ZIP EMPTY", new Object[] { inputFile.getName() });
                    final String exception = "ZIPSANITIZER_UNSAFE_ZIP";
                    throw new IAMSecurityException(exception);
                }
                case NONE: {
                    validateAndSanitize(inputFile, zipValidator, null);
                    break;
                }
            }
        }
        catch (final IAMSecurityException e) {
            cleanUpFiles(zipValidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.ZIPFILES));
            throw e;
        }
        catch (final ZipException e2) {
            ZSecZipSanitizer.LOGGER.log(Level.SEVERE, "ZipException thrown while Sanitization , exception  :  ", e2);
            cleanUpFiles(zipValidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.ZIPFILES));
            throw new IAMSecurityException("ZIPSANITIZER_UNSUPPORTED_ZIP");
        }
        catch (final Exception e3) {
            ZSecZipSanitizer.LOGGER.log(Level.SEVERE, "Exception occurred while  ZIP sanitization , exception  : ", e3);
            cleanUpFiles(zipValidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.ZIPFILES));
            throw new IAMSecurityException("ZIPSANITIZER_INVALID_ZIP");
        }
        finally {
            cleanUpFiles(zipValidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.TMPFILES));
        }
    }
    
    static void cleanUpFiles(final LinkedList<File> filesToCleanUp) {
        for (final File file : filesToCleanUp) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
    
    private static File createTmpZip(final String entryName, final String prefix, final ZipValidator zipvalidator, final File destinationFile) throws IOException {
        File tmp = null;
        final String name = zipvalidator.getName(entryName);
        if (destinationFile == null) {
            tmp = File.createTempFile(prefix + System.currentTimeMillis(), name);
        }
        else {
            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }
            tmp = new File(destinationFile.getParent() + File.separator + prefix + System.currentTimeMillis() + name);
            if (tmp.exists()) {
                tmp.delete();
            }
            tmp.createNewFile();
        }
        zipvalidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.TMPFILES).add(tmp);
        return tmp;
    }
    
    private static void validateAndSanitize(final ZSecZipFile zipSourceFile, final ZipValidator zipValidator, final File destination) throws IOException {
        ZSecZipOutputFile zipOutputFile = null;
        try {
            zipOutputFile = ((ZSecConstants.DESTINATION_TYPE.ZIP == zipValidator.destinationType) ? new ZSecZipOutputFile(destination, zipSourceFile.is7ZipFile()) : null);
            final Enumeration zipFileEntries = zipSourceFile.entries();
            while (zipFileEntries.hasMoreElements() && zipValidator.isValid) {
                final ZSecZipEntry zipEntry = new ZSecZipEntry(zipFileEntries.nextElement(), zipSourceFile.is7ZipFile());
                if (!zipValidator.checkForSafeZipConditions(zipValidator.zipSanitizerRule, zipEntry)) {
                    break;
                }
                final String sanitizedEntryName = zipValidator.sanitizeZipEntryName(zipEntry.getName());
                if (zipValidator.isValidZip(zipValidator.zipSanitizerRule, zipEntry, sanitizedEntryName)) {
                    switch (zipValidator.destinationType) {
                        case FOLDER: {
                            sanitizeToFolder(zipEntry, zipValidator, zipSourceFile, destination, sanitizedEntryName);
                            break;
                        }
                        case ZIP: {
                            sanitizeToZip(zipEntry, zipValidator, zipSourceFile, zipOutputFile, destination, sanitizedEntryName);
                            break;
                        }
                        case NONE: {
                            validateOnly(zipEntry, zipValidator, zipSourceFile, destination, sanitizedEntryName);
                            break;
                        }
                    }
                }
                zipSourceFile.closeEntry(zipEntry);
            }
        }
        catch (final IAMSecurityException e) {
            throw e;
        }
        finally {
            if (zipOutputFile != null) {
                zipOutputFile.close();
            }
        }
    }
    
    private static void validateOnly(final ZSecZipEntry zipEntry, final ZipValidator zipValidator, final ZSecZipFile zipSourceFile, final File destination, final String sanitizedEntryName) throws IOException {
        InputStream entryStream = null;
        if (!zipEntry.isDirectory()) {
            entryStream = zipSourceFile.getInputStream(zipEntry);
            final String contentType = SecurityUtil.getMimeTypeUsingTika(zipSourceFile.getInputStream(zipEntry), zipEntry.getName());
            entryStream.close();
            if (isZip(zipEntry.getName(), contentType)) {
                ++zipValidator.level;
                final File originalZip = createTmpZip(sanitizedEntryName, "SFZEW_ORIG" + CommonUtil.getSecureRandomNumber(), zipValidator, destination);
                entryStream = zipSourceFile.getInputStream(zipEntry);
                copyStreamToFile(originalZip, entryStream);
                validateAndSanitize(new ZSecZipFile(originalZip, 5, SecurityUtil.is7Zip(contentType)), zipValidator, destination);
                --zipValidator.level;
            }
            else {
                zipValidator.incrementFileCounters(zipEntry);
                zipValidator.checkValidContentTypes(contentType, zipEntry);
            }
        }
    }
    
    static void sanitizeToZip(final ZSecZipEntry zipEntry, final ZipValidator zipValidator, final ZSecZipFile zipSourceFile, final ZSecZipOutputFile zipOutputFile, File destination, final String sanitizedEntryName) throws IOException {
        InputStream entryStream = null;
        if (!zipEntry.isDirectory()) {
            entryStream = zipSourceFile.getInputStream(zipEntry);
            final String contentType = SecurityUtil.getMimeTypeUsingTika(entryStream, zipEntry.getName());
            entryStream.close();
            if (isZip(zipEntry.getName(), contentType)) {
                ++zipValidator.level;
                final int rand_no = CommonUtil.getSecureRandomNumber();
                final File originalZip = createTmpZip(sanitizedEntryName, "SFZEW_ORIG" + rand_no, zipValidator, destination);
                entryStream = zipSourceFile.getInputStream(zipEntry);
                copyStreamToFile(originalZip, entryStream);
                destination = createTmpZip(sanitizedEntryName, "SFZEW" + rand_no, zipValidator, destination);
                final boolean is7ZipEntry = SecurityUtil.is7Zip(contentType);
                validateAndSanitize(new ZSecZipFile(originalZip, 5, is7ZipEntry), zipValidator, destination);
                if (!zipValidator.isEmptyZip(destination, is7ZipEntry)) {
                    addFileToZipUsingZOS(zipOutputFile, sanitizedEntryName, new FileInputStream(destination));
                }
                --zipValidator.level;
            }
            else {
                zipValidator.incrementFileCounters(zipEntry);
                if (zipValidator.checkValidContentTypes(contentType, zipEntry)) {
                    entryStream = zipSourceFile.getInputStream(zipEntry);
                    addFileToZipUsingZOS(zipOutputFile, sanitizedEntryName, entryStream);
                    entryStream.close();
                }
            }
        }
        else {
            addFileToZipUsingZOS(zipOutputFile, sanitizedEntryName, null);
        }
    }
    
    private static void sanitizeToFolder(final ZSecZipEntry zipEntry, final ZipValidator zipValidator, final ZSecZipFile zipSourceFile, final File destination, final String sanitizedEntryName) throws IOException {
        InputStream entryStream = null;
        final File unCompressedFilePath = new File(destination + File.separator + sanitizedEntryName);
        zipValidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.ZIPFILES).addFirst(unCompressedFilePath);
        if (!zipEntry.isDirectory()) {
            entryStream = zipSourceFile.getInputStream(zipEntry);
            final String contentType = SecurityUtil.getMimeTypeUsingTika(entryStream, zipEntry.getName());
            entryStream.close();
            if (zipValidator.checkValidContentTypes(contentType, zipEntry)) {
                makeDirectory(unCompressedFilePath.getParentFile(), zipValidator);
                entryStream = zipSourceFile.getInputStream(zipEntry);
                writeFileToDisk(entryStream, unCompressedFilePath);
                if (isZip(zipEntry.getName(), contentType)) {
                    ++zipValidator.level;
                    validateAndSanitize(new ZSecZipFile(unCompressedFilePath, 5, SecurityUtil.is7Zip(contentType)), zipValidator, unCompressedFilePath.getParentFile());
                    --zipValidator.level;
                }
                else {
                    zipValidator.incrementFileCounters(zipEntry);
                }
            }
        }
        else {
            makeDirectory(unCompressedFilePath, zipValidator);
        }
    }
    
    private static boolean isZip(final String name, final String contentType) {
        return (name.endsWith(".zip") && "application/zip".equals(contentType)) || SecurityUtil.is7Zip(contentType);
    }
    
    private static void makeDirectory(final File directory, final ZipValidator zipValidator) {
        if (!directory.exists()) {
            directory.mkdirs();
            zipValidator.filesToCleanUp.get(ZipValidator.FILE_TO_DELETE.ZIPFILES).addFirst(directory);
        }
    }
    
    private static void writeFileToDisk(final InputStream inputStream, final File destFile) throws IOException {
        final byte[] data = new byte[8096];
        BufferedOutputStream bufOS = null;
        try {
            bufOS = new BufferedOutputStream(new FileOutputStream(destFile), 8096);
            int currentByte;
            while ((currentByte = inputStream.read(data, 0, 8096)) != -1) {
                bufOS.write(data, 0, currentByte);
            }
            bufOS.flush();
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (bufOS != null) {
                bufOS.close();
            }
        }
    }
    
    private static void addFileToZipUsingZOS(final ZSecZipOutputFile zipOutputFile, final String zipEntry, final InputStream inputStream) throws IOException {
        try {
            zipOutputFile.putEntry(zipEntry);
            if (inputStream != null) {
                final byte[] buffer = new byte[8096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) > 0) {
                    zipOutputFile.write(buffer, 0, bytesRead);
                }
            }
            zipOutputFile.closeEntry();
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    public static void copyStreamToFile(final File originalZip, final InputStream inputStream) throws IOException {
        BufferedOutputStream bufOS = null;
        FileOutputStream fOS = null;
        BufferedInputStream bufIS = null;
        try {
            bufIS = new BufferedInputStream(inputStream);
            fOS = new FileOutputStream(originalZip);
            final byte[] data = new byte[8096];
            bufOS = new BufferedOutputStream(fOS, 8096);
            int currentByte;
            while ((currentByte = bufIS.read(data, 0, 8096)) != -1) {
                bufOS.write(data, 0, currentByte);
            }
            bufOS.flush();
        }
        finally {
            if (bufIS != null) {
                bufIS.close();
            }
            if (bufOS != null) {
                bufOS.close();
            }
            if (fOS != null) {
                fOS.close();
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ZSecZipSanitizer.class.getName());
    }
}
