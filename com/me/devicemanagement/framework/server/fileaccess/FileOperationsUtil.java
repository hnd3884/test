package com.me.devicemanagement.framework.server.fileaccess;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Arrays;
import java.io.FilenameFilter;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileOperationsUtil
{
    private Logger logger;
    private static FileOperationsUtil foUtil;
    
    public FileOperationsUtil() {
        this.logger = Logger.getLogger(FileOperationsUtil.class.getName());
    }
    
    public static synchronized FileOperationsUtil getInstance() {
        if (FileOperationsUtil.foUtil == null) {
            FileOperationsUtil.foUtil = new FileOperationsUtil();
        }
        return FileOperationsUtil.foUtil;
    }
    
    public void cleanupFiles(final String sourceFolder, final String fileNamesStartsWith, final int numOfLatestFilesToMaintain) throws Exception {
        try {
            this.logger.log(Level.INFO, "cleanupFiles() called with sourceFolder: " + sourceFolder + " fileNamesStartsWith: " + fileNamesStartsWith + " numOfLatestFilesToMaintain: " + numOfLatestFilesToMaintain);
            final File sFolder = new File(sourceFolder);
            if (!sFolder.exists()) {
                this.logger.log(Level.WARNING, "SourceFolder does not exist: " + sourceFolder);
                throw new SyMException(1001, "SourceFolder does not exist: " + sourceFolder, null);
            }
            if (!sFolder.isDirectory()) {
                this.logger.log(Level.WARNING, "SourceFolder is not a directory: " + sourceFolder);
                throw new SyMException(1001, "SourceFolder is not a directory: " + sourceFolder, null);
            }
            final FilenameFilterImpl fnl = new FilenameFilterImpl();
            fnl.startsWith = fileNamesStartsWith;
            File[] files = sFolder.listFiles(fnl);
            final int filesLen = files.length;
            this.logger.log(Level.INFO, "No. of files present: " + filesLen + ". No. of files to be maintained: " + numOfLatestFilesToMaintain);
            if (filesLen > numOfLatestFilesToMaintain) {
                final int numOfFilesToDel = filesLen - numOfLatestFilesToMaintain;
                files = this.sortFilesWithLastModifiedTime(files);
                for (int s = 0; s < numOfFilesToDel; ++s) {
                    final boolean res = files[s].delete();
                    this.logger.log(Level.INFO, "File is deleted: " + files[s] + " result: " + res);
                }
            }
            else {
                this.logger.log(Level.INFO, "No cleanup is needed. Number of files present in the folder is <= numOfLatestFilesToMaintain: " + numOfLatestFilesToMaintain);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught error from cleanupFiles() with sourceFolder: " + sourceFolder + " fileNamesStartsWith: " + fileNamesStartsWith + " numOfLatestFilesToMaintain: " + numOfLatestFilesToMaintain, ex);
            throw ex;
        }
    }
    
    public File[] sortFilesWithLastModifiedTime(final File[] files) throws Exception {
        try {
            for (int len = files.length, s = 0; s < len; ++s) {
                for (int k = s + 1; k < len; ++k) {
                    if (files[s].lastModified() > files[k].lastModified()) {
                        final File tmpFile = files[k];
                        files[k] = files[s];
                        files[s] = tmpFile;
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while sorting files: " + Arrays.toString(files), ex);
            throw ex;
        }
        return files;
    }
    
    public boolean deleteFileOrFolder(final File source) {
        if (source.isDirectory()) {
            final File[] listFiles;
            final File[] fileList = listFiles = source.listFiles();
            for (final File file : listFiles) {
                this.deleteFileOrFolder(file);
            }
        }
        return source.delete();
    }
    
    public Properties getPropertiesFromFile(final String filePath) throws Exception {
        final Properties fileContentProp = new Properties();
        FileInputStream fileStream = null;
        try {
            if (new File(filePath).exists()) {
                fileStream = new FileInputStream(filePath);
                fileContentProp.load(fileStream);
                fileStream.close();
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            if (fileStream != null) {
                fileStream.close();
            }
        }
        return fileContentProp;
    }
    
    public boolean deleteFilesOfExtensionUnderDirectory(final String directory, final String extensionName) {
        final FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String fileName) {
                return fileName.endsWith(extensionName);
            }
        };
        final File parentDir = new File(directory);
        final String[] listOfFilesWithGivenExtension = parentDir.list(fileNameFilter);
        if (listOfFilesWithGivenExtension == null || listOfFilesWithGivenExtension.length == 0) {
            this.logger.warning("There are no files of given extension " + extensionName + "in this direcotry!");
            return false;
        }
        for (final String file : listOfFilesWithGivenExtension) {
            final String absoluteFilePath = new StringBuffer(directory).append(File.separator).append(file).toString();
            final File fileToDelete = new File(absoluteFilePath);
            if (!fileToDelete.delete()) {
                this.logger.warning(file + " couldn't be deleted. May be in use / not available");
            }
        }
        return true;
    }
    
    public boolean copyDirectory(final File sourceLocation, final File targetLocation) throws Exception {
        try {
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdirs();
                }
                final String[] children = sourceLocation.list();
                for (int i = 0; i < children.length; ++i) {
                    if (!this.copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]))) {
                        return false;
                    }
                }
                return true;
            }
            return this.copyFile(sourceLocation, targetLocation);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Failed to copy folder " + sourceLocation + " to location " + targetLocation, e);
            return false;
        }
    }
    
    private boolean copyFile(final File srcFile, final File destFile) throws Exception {
        boolean retType = false;
        InputStream inFile = null;
        OutputStream outFile = null;
        this.logger.log(Level.INFO, "Going to copy file.......");
        try {
            final String parentLoc = destFile.getParent();
            if (parentLoc != null && !parentLoc.equals("") && !new File(parentLoc).exists()) {
                new File(parentLoc).mkdirs();
            }
            inFile = new FileInputStream(srcFile);
            outFile = new FileOutputStream(destFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inFile.read(buf)) > 0) {
                outFile.write(buf, 0, len);
            }
            retType = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while copying file.......", e);
        }
        finally {
            if (inFile != null) {
                inFile.close();
            }
            if (outFile != null) {
                outFile.close();
            }
        }
        return retType;
    }
    
    static {
        FileOperationsUtil.foUtil = null;
    }
    
    class FilenameFilterImpl implements FilenameFilter
    {
        String startsWith;
        
        FilenameFilterImpl() {
            this.startsWith = null;
        }
        
        @Override
        public boolean accept(final File dir, final String name) {
            boolean result = false;
            if (this.startsWith == null || this.startsWith.trim().length() == 0 || name.startsWith(this.startsWith)) {
                result = true;
            }
            return result;
        }
    }
}
