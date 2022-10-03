package com.me.devicemanagement.framework.webclient.common;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.io.FilenameUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.io.InputStream;
import java.util.logging.Logger;

public class FileUploadUtil
{
    String className;
    Logger out;
    
    public FileUploadUtil() {
        this.className = FileUploadUtil.class.getName();
        this.out = Logger.getLogger(this.className);
    }
    
    public boolean copyFileToServer(final InputStream fileInput, String fileName, final String directory) {
        OutputStream fout = null;
        try {
            this.out.log(Level.INFO, "Request to import new form file: " + fileName);
            ApiFactoryProvider.getFileAccessAPI().createDirectory(directory);
            try {
                if (directory.endsWith(File.separator)) {
                    fileName = directory + fileName;
                }
                else {
                    fileName = directory + File.separator + fileName;
                }
                fout = ApiFactoryProvider.getFileAccessAPI().writeFile(fileName);
                final byte[] file = new byte[1024];
                int len;
                while ((len = fileInput.read(file)) > 0) {
                    fout.write(file, 0, len);
                }
            }
            catch (final IOException ex) {
                this.out.log(Level.WARNING, "Exception while getting th file stream to write... ", ex);
            }
            finally {
                fout.close();
            }
            this.out.log(Level.INFO, "Successfully copied file - " + fileName + " to dir " + directory);
            return true;
        }
        catch (final Exception ex2) {
            this.out.log(Level.WARNING, "Import Form File Operation failed " + fileName + " " + ex2);
            return false;
        }
    }
    
    public boolean deleteFileFolder(final File fileFolder) {
        boolean returnType = true;
        this.out.log(Level.INFO, "Deleting file.....");
        try {
            if (fileFolder == null || !fileFolder.exists()) {
                this.out.log(Level.INFO, fileFolder + " :  does not exist.");
                return false;
            }
            if (fileFolder.isDirectory()) {
                final File[] files = fileFolder.listFiles();
                for (int size = files.length, i = 0; i < size; ++i) {
                    if (files[i].isDirectory()) {
                        this.deleteFileFolder(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
            fileFolder.delete();
            this.out.log(Level.INFO, "Deleted : " + fileFolder);
        }
        catch (final Exception e) {
            returnType = false;
            this.out.log(Level.WARNING, "Exception occured while deleting folder.", e);
        }
        return returnType;
    }
    
    public static boolean isValidFileExtension(String fileName, String fileExtPattern) {
        if (fileName.indexOf("\u0000") != -1) {
            fileName = fileName.substring(0, fileName.indexOf("\u0000"));
        }
        fileExtPattern = fileExtPattern.replaceAll("\\s+", "").toLowerCase();
        final String regexFileExtensionPattern = "([^\\s]+(\\.(?i)(" + fileExtPattern + "))$)";
        final Pattern pattern = Pattern.compile(regexFileExtensionPattern);
        final Matcher matcher = pattern.matcher(fileName.toLowerCase());
        return matcher.matches();
    }
    
    private static boolean isContainDirectoryTraversal(final String fileName) {
        return fileName.contains("/") || fileName.contains("\\");
    }
    
    private static boolean isCompletePath(final String fileName) {
        final String regexFileExtensionPattern = "([a-zA-Z]:[\\ \\\\ / //].*)";
        final Pattern pattern = Pattern.compile(regexFileExtensionPattern);
        final Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }
    
    private static boolean isContainExecutableFileExt(String fileName) {
        if (fileName.indexOf("\u0000") != -1) {
            fileName = fileName.substring(0, fileName.indexOf("\u0000"));
        }
        String fileExtension = FilenameUtils.getExtension(fileName).trim();
        if (!fileExtension.trim().equals("")) {
            fileExtension = fileExtension.toLowerCase();
            final ArrayList executableFileExts = new ArrayList((Collection<? extends E>)Arrays.asList("jsp", "js", "html", "htm", "shtml", "shtm", "hta", "asp"));
            if (executableFileExts.contains(fileExtension)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasVulnerabilityInFileName(final String fileName) {
        return isContainDirectoryTraversal(fileName) || isCompletePath(fileName) || isContainExecutableFileExt(fileName);
    }
    
    public static boolean hasVulnerabilityInFileName(final String fileName, final String allowedFileExt) {
        return isContainDirectoryTraversal(fileName) || isCompletePath(fileName) || !isValidFileExtension(fileName, allowedFileExt);
    }
}
