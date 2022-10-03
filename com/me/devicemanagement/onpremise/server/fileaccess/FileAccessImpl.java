package com.me.devicemanagement.onpremise.server.fileaccess;

import java.util.regex.Matcher;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.io.FileReader;
import org.apache.commons.io.FileUtils;
import java.io.FilenameFilter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.io.FileInputStream;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;

public class FileAccessImpl implements FileAccessAPI
{
    private static Logger logger;
    
    public FileInputStream createFile(final String fileName) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
        }
        catch (final IOException e) {
            FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while creating file", e);
            if (fis != null) {
                fis.close();
            }
            throw e;
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        return fis;
    }
    
    public InputStream readFile(final String fileName) throws Exception {
        InputStream fis = null;
        try {
            if (new File(fileName).exists()) {
                fis = new FileInputStream(fileName);
            }
        }
        catch (final Exception e) {
            FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while reading file", e);
            if (fis != null) {
                fis.close();
            }
            throw e;
        }
        return fis;
    }
    
    public String readFileIntoString(final InputStream inputStream, final boolean readOnlyAscii) {
        final StringBuffer fileContent = new StringBuffer();
        BufferedReader br = null;
        InputStreamReader ir = null;
        try {
            ir = new InputStreamReader(inputStream);
            br = new BufferedReader(ir);
            int ch;
            while ((ch = br.read()) != -1) {
                boolean condition = true;
                if (readOnlyAscii) {
                    condition = (ch >= 32 && ch <= 126);
                }
                if (condition) {
                    fileContent.append((char)ch);
                }
            }
        }
        catch (final IOException ex) {
            FileAccessImpl.logger.log(Level.SEVERE, null, ex);
            try {
                br.close();
            }
            catch (final IOException ex) {
                FileAccessImpl.logger.log(Level.SEVERE, null, ex);
            }
            try {
                ir.close();
            }
            catch (final IOException ex) {
                FileAccessImpl.logger.log(Level.SEVERE, null, ex);
            }
            try {
                inputStream.close();
            }
            catch (final IOException ex) {
                FileAccessImpl.logger.log(Level.SEVERE, null, ex);
            }
        }
        finally {
            try {
                br.close();
            }
            catch (final IOException ex2) {
                FileAccessImpl.logger.log(Level.SEVERE, null, ex2);
            }
            try {
                ir.close();
            }
            catch (final IOException ex2) {
                FileAccessImpl.logger.log(Level.SEVERE, null, ex2);
            }
            try {
                inputStream.close();
            }
            catch (final IOException ex2) {
                FileAccessImpl.logger.log(Level.SEVERE, null, ex2);
            }
        }
        return fileContent.toString();
    }
    
    public String readFileIntoString(final String fileAddress, final boolean readOnlyAscii) {
        try {
            final InputStream inputStream = this.readFile(fileAddress);
            return this.readFileIntoString(inputStream, readOnlyAscii);
        }
        catch (final Exception ex) {
            FileAccessImpl.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String readFileIntoString(final String fileAddress) {
        return this.readFileIntoString(fileAddress, false);
    }
    
    public byte[] readFileContentAsArray(final String fileName) throws Exception {
        FileInputStream fis = null;
        byte[] content = null;
        try {
            if (new File(fileName).exists()) {
                fis = new FileInputStream(fileName);
                content = new byte[fis.available()];
                fis.read(content);
            }
        }
        catch (final IOException e) {
            FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while reading file", e);
            throw e;
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception e2) {
                    FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                }
            }
        }
        return content;
    }
    
    public Long writeFile(final String fileName, final InputStream fileInput) throws IOException {
        FileOutputStream fos = null;
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
            final byte[] buf = new byte[8192];
            int len;
            while ((len = fileInput.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            FileAccessImpl.logger.log(Level.INFO, "Writing completed for the file : {0}", fileName);
        }
        catch (final IOException e) {
            FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while writing file", e);
            throw e;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e2) {
                    FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                }
            }
        }
        return null;
    }
    
    public Long writeFile(final String fileName, final byte[] content) throws IOException {
        FileOutputStream fos = null;
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
            fos.write(content);
        }
        catch (final IOException e) {
            FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while writing file", e);
            throw e;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e2) {
                    FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                }
            }
        }
        return null;
    }
    
    public Long writeFile(final String fileName, final byte[] fileContent, final boolean earRequired) throws Exception {
        return this.writeFile(fileName, fileContent);
    }
    
    public OutputStream writeFile(final String fileName) throws IOException {
        OutputStream fos = null;
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
        }
        catch (final IOException e) {
            FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while writing file", e);
            if (fos != null) {
                fos.close();
            }
            throw e;
        }
        return fos;
    }
    
    public boolean copyFile(final String srcFile, final String destFile) throws Exception {
        return FileAccessUtil.copyFileWithinServer(srcFile, destFile);
    }
    
    public HashMap copyFileWithResponse(final String srcFile, final String destFile) throws Exception {
        final HashMap details = new HashMap();
        details.put("status", this.copyFile(srcFile, destFile));
        return details;
    }
    
    public boolean deleteFile(final String fileName) throws Exception {
        return new File(fileName).delete();
    }
    
    public boolean deleteFile(final String fileName, final boolean isLocal) throws Exception {
        return this.deleteFile(fileName);
    }
    
    public boolean deleteDirectory(final String dirPath) throws Exception {
        return this.deleteFilesInDirectory(dirPath, false);
    }
    
    public Boolean deleteFilesInDirectory(final String dirPath, final boolean excludeDirectory) throws Exception {
        final File path = new File(dirPath);
        if (path.isFile()) {
            return path.delete();
        }
        if (path.exists()) {
            final File[] files = path.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    if (!excludeDirectory) {
                        this.deleteDirectory(files[i].toString());
                    }
                }
                else {
                    files[i].delete();
                }
            }
        }
        if (excludeDirectory) {
            return null;
        }
        return path.delete();
    }
    
    public long getFileSize(final String fileName) {
        return new File(fileName).length();
    }
    
    public String getFileName(final String filePath) {
        return new File(filePath).getName();
    }
    
    public String getFileNameFromFilePath(final String filePath) {
        return this.getFileName(filePath);
    }
    
    public boolean createDirectory(final String dirPath) {
        final File file = new File(dirPath);
        return file.mkdirs();
    }
    
    public boolean isFileExists(final String fileName) {
        final File file = new File(fileName);
        return file.exists();
    }
    
    public ArrayList getAllFilesList(final String absolutePath, String previousdir, final String includeExtn) throws Exception {
        final ArrayList fileList = new ArrayList();
        try {
            if (previousdir == null) {
                previousdir = absolutePath;
            }
            final File rootDir = new File(absolutePath);
            final String[] allFiles = rootDir.list();
            for (int i = 0; i < allFiles.length; ++i) {
                final String filePath = absolutePath + File.separator + allFiles[i];
                final File temp = new File(filePath);
                if (temp.isDirectory()) {
                    fileList.addAll(this.getAllFilesList(filePath, previousdir + File.separator + allFiles[i], includeExtn));
                }
                else if (includeExtn == null || (includeExtn != null && allFiles[i].endsWith("." + includeExtn))) {
                    fileList.add(previousdir + File.separator + allFiles[i]);
                }
            }
        }
        catch (final Exception e) {
            FileAccessImpl.logger.log(Level.WARNING, "Caught exception while getting data from directory " + absolutePath, e);
        }
        return fileList;
    }
    
    public boolean renameFolder(final String oldFolder, final String newFolder) throws Exception {
        return this.renameFile(oldFolder, newFolder);
    }
    
    public boolean copyDirectory(final String sourceLocation, final String targetLocation) throws Exception {
        return FileAccessUtil.copyDirectoryWithinServer(sourceLocation, targetLocation);
    }
    
    public boolean isDirectory(final String path) throws Exception {
        return new File(path).isDirectory();
    }
    
    public String getCanonicalPath(final String filePath) throws Exception {
        final String cononicalPath = new File(filePath).getCanonicalPath();
        return cononicalPath;
    }
    
    public String getParent(final String filePath) {
        return new File(filePath).getParent();
    }
    
    public boolean renameFile(final String srcFilePath, final String descFilePath) {
        try {
            final File newFile = new File(descFilePath);
            newFile.getParentFile().mkdirs();
            Files.move(Paths.get(srcFilePath, new String[0]), Paths.get(descFilePath, new String[0]), StandardCopyOption.ATOMIC_MOVE);
            FileAccessImpl.logger.log(Level.INFO, srcFilePath + " is successfully renamed to " + descFilePath);
            return true;
        }
        catch (final Exception e) {
            FileAccessImpl.logger.log(Level.SEVERE, "Exception while renaming the folder/file" + e);
            return false;
        }
    }
    
    public URL getURLFromFilePath(final String filePath) {
        URL url = null;
        try {
            final File file = new File(filePath);
            url = file.toURI().toURL();
        }
        catch (final Exception e) {
            FileAccessImpl.logger.log(Level.SEVERE, "Exception while getURL method" + e);
        }
        return url;
    }
    
    public String getAbsolutePath(final String filePath) {
        String absolutePath = null;
        try {
            absolutePath = new File(filePath).getAbsolutePath();
        }
        catch (final Exception e) {
            FileAccessImpl.logger.log(Level.SEVERE, "Exception while getAbsolutePath method " + e);
        }
        return absolutePath;
    }
    
    public long lastModified(final String filePath) {
        return new File(filePath).lastModified();
    }
    
    public boolean createNewFile(final String filePath) {
        final File file = new File(filePath);
        boolean flag;
        try {
            flag = file.createNewFile();
        }
        catch (final IOException ex) {
            FileAccessImpl.logger.log(Level.SEVERE, "Exception while createNewFile method " + ex);
            flag = false;
        }
        return flag;
    }
    
    public long getFreeSpace(final String filePath) {
        return new File(filePath).getFreeSpace();
    }
    
    public String[] list(final String filePath, final FilenameFilter filter) {
        return new File(filePath).list(filter);
    }
    
    public long getUsableSpace(final String filePath) {
        return new File(filePath).getUsableSpace();
    }
    
    public boolean exists(final String filePath) throws Exception {
        return new File(filePath).exists();
    }
    
    public HashMap getOutputStream(final String fileName) {
        OutputStream os = null;
        final HashMap hashMap = new HashMap();
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            os = new FileOutputStream(fileName);
            hashMap.put("outputstream", os);
        }
        catch (final IOException e) {
            FileAccessImpl.logger.log(Level.WARNING, "Exception occurred while writing file", e);
        }
        return hashMap;
    }
    
    public InputStream getInputStream(final String filePath) throws Exception {
        final InputStream inputStream = new FileInputStream(filePath);
        return inputStream;
    }
    
    public String constructFileURL(final HashMap hashMap) {
        return hashMap.get("path");
    }
    
    public byte[] readImageContentAsArray(final String fileName) throws Exception {
        return this.readFileContentAsArray(fileName);
    }
    
    public void forceDeleteDirectory(final String dirPath) throws Exception {
        FileAccessImpl.logger.log(Level.FINE, "Inside deleteFolder()");
        FileAccessImpl.logger.log(Level.INFO, "Folder to be Deleted  :  {0}", dirPath);
        try {
            final File file = new File(dirPath);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            }
        }
        catch (final Exception ex) {
            FileAccessImpl.logger.log(Level.WARNING, "Exception in deleteFolder...", ex);
        }
    }
    
    public boolean isFile(final String path) throws Exception {
        return new File(path).isFile();
    }
    
    public ArrayList<String> getAllOldFilesList(final String absolutePath, String previousdir, final Long time, final String includeExtn) throws Exception {
        final ArrayList<String> fileList = new ArrayList<String>();
        try {
            if (previousdir == null) {
                previousdir = absolutePath;
            }
            final File rootDir = new File(absolutePath);
            final String[] allFiles = rootDir.list();
            for (int i = 0; i < allFiles.length; ++i) {
                final String filePath = absolutePath + File.separator + allFiles[i];
                final File temp = new File(filePath);
                if (temp.isDirectory()) {
                    fileList.addAll(this.getAllOldFilesList(filePath, previousdir + File.separator + allFiles[i], time, includeExtn));
                }
                else if ((includeExtn == null || (includeExtn != null && allFiles[i].endsWith("." + includeExtn))) && time > 0L && temp.lastModified() < time) {
                    fileList.add(previousdir + File.separator + allFiles[i]);
                }
            }
        }
        catch (final Exception e) {
            FileAccessImpl.logger.log(Level.WARNING, "Caught exception while getting data from directory " + absolutePath, e);
        }
        return fileList;
    }
    
    public ArrayList<String> getAllFilesList(final String absolutePath, String previousdir, final Long startTime, final Long endTime, final String includeExtn) throws Exception {
        final ArrayList<String> fileList = new ArrayList<String>();
        try {
            if (previousdir == null) {
                previousdir = absolutePath;
            }
            final File rootDir = new File(absolutePath);
            final String[] allFiles = rootDir.list();
            for (int i = 0; i < allFiles.length; ++i) {
                final String filePath = absolutePath + File.separator + allFiles[i];
                final File temp = new File(filePath);
                if (temp.isDirectory()) {
                    fileList.addAll(this.getAllFilesList(filePath, previousdir + File.separator + allFiles[i], startTime, endTime, includeExtn));
                }
                else if (includeExtn == null || (includeExtn != null && allFiles[i].endsWith("." + includeExtn))) {
                    if (endTime > 0L && startTime > 0L) {
                        if (temp.lastModified() < endTime && temp.lastModified() > startTime) {
                            fileList.add(previousdir + File.separator + allFiles[i]);
                        }
                    }
                    else if (endTime > 0L) {
                        if (temp.lastModified() < endTime) {
                            fileList.add(previousdir + File.separator + allFiles[i]);
                        }
                    }
                    else if (startTime > 0L && temp.lastModified() > startTime) {
                        fileList.add(previousdir + File.separator + allFiles[i]);
                    }
                }
            }
        }
        catch (final Exception e) {
            FileAccessImpl.logger.log(Level.WARNING, "Caught exception while getting data from directory " + absolutePath, e);
        }
        return fileList;
    }
    
    public static File appendMultipleFiles(final String sourceFile, final String... additionalFiles) throws Exception {
        final File appendedFile = new File(sourceFile);
        final String sourceFileContent = FileUtils.readFileToString(new File(sourceFile), "UTF-8");
        final StringBuilder additionalFileContent = new StringBuilder(sourceFileContent);
        for (int i = 0; i < additionalFiles.length; ++i) {
            final File additionalFile = new File(additionalFiles[i]);
            additionalFileContent.append("\n\n");
            additionalFileContent.append(FileUtils.readFileToString(additionalFile, "UTF-8"));
        }
        final String finaalContent = additionalFileContent.toString();
        FileUtils.writeStringToFile(appendedFile, finaalContent, "UTF-8");
        return appendedFile;
    }
    
    public static void findAndReplaceStringInFile(final String fileName, final String findStr, final String replaceStr) {
        FileReader filereader = null;
        FileWriter filewriter = null;
        try {
            filereader = new FileReader(fileName);
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            while ((read = filereader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            String finalStr = strBuilder.toString();
            final Pattern findStrPattern = Pattern.compile(findStr);
            final Matcher matcher = findStrPattern.matcher(finalStr);
            if (matcher.find()) {
                finalStr = finalStr.replaceAll(findStr, replaceStr);
            }
            else {
                finalStr = finalStr.concat("\n\n" + replaceStr);
            }
            filewriter = new FileWriter(fileName, false);
            filewriter.write(finalStr, 0, finalStr.length());
        }
        catch (final Exception ex) {
            FileAccessImpl.logger.log(Level.WARNING, "Caught exception in findAndReplaceStringInFile() for fileName: " + fileName, ex);
            try {
                if (filereader != null) {
                    filereader.close();
                }
                if (filewriter != null) {
                    filewriter.close();
                }
            }
            catch (final Exception e) {
                FileAccessImpl.logger.log(Level.WARNING, "Exception while closing fileObjects ", e);
            }
        }
        finally {
            try {
                if (filereader != null) {
                    filereader.close();
                }
                if (filewriter != null) {
                    filewriter.close();
                }
            }
            catch (final Exception e2) {
                FileAccessImpl.logger.log(Level.WARNING, "Exception while closing fileObjects ", e2);
            }
        }
    }
    
    static {
        FileAccessImpl.logger = Logger.getLogger(FileAccessImpl.class.getName());
    }
}
