package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;
import java.util.logging.Logger;

public class FileUtil
{
    private static Logger logger;
    public static final long COPY_ERROR = -1L;
    public static final long INTERRUPTED = -2L;
    public static final long NATIVE_COPY_ERROR = -3L;
    private static final int BUFFER = 2048;
    
    public static boolean copy(final String baseDirectory, final String filePath, final String destination, final Informable informable) {
        boolean copyStatus = true;
        Process process = null;
        try {
            File sourceFile = null;
            File destFile = null;
            if (filePath != null) {
                sourceFile = new File(baseDirectory, filePath);
                destFile = new File(destination, filePath);
            }
            else {
                sourceFile = new File(baseDirectory);
                destFile = new File(destination);
            }
            if (!sourceFile.exists()) {
                return false;
            }
            if (sourceFile.isDirectory()) {
                final String[] command = { "xcopy", sourceFile.getAbsolutePath(), destFile.getAbsolutePath(), "/V", "/I", "/H", "/R", "/Y", "/E" };
                final ProcessBuilder builder = new ProcessBuilder(command);
                process = builder.start();
                final BufferedReader commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                final BufferedReader commandError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s = null;
                while ((s = commandOutput.readLine()) != null) {
                    if (informable != null) {
                        informable.messageRead(s);
                    }
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
                if ((s = commandError.readLine()) != null) {
                    FileUtil.logger.log(Level.WARNING, "Error in copying through x-copy : " + s);
                    return false;
                }
            }
            else {
                final File destParentFolder = destFile.getParentFile();
                if (!destParentFolder.exists()) {
                    destParentFolder.mkdirs();
                }
                final InputStream in = new FileInputStream(sourceFile);
                final OutputStream out = new FileOutputStream(destFile);
                copyInputStream(in, out);
                if (informable != null) {
                    informable.messageRead(sourceFile.getAbsolutePath());
                }
                out.close();
            }
            final long destSize = getFileOrFolderSize(destFile);
            copyStatus = (getFileOrFolderSize(sourceFile) == destSize);
        }
        catch (final InterruptedException e) {
            FileUtil.logger.log(Level.WARNING, "Interrupted while copying.");
            if (process != null) {
                process.destroy();
            }
            copyStatus = false;
        }
        catch (final Exception e2) {
            FileUtil.logger.log(Level.WARNING, "Exception while copying.", e2);
            copyStatus = false;
        }
        return copyStatus;
    }
    
    public static boolean deleteFileOrFolder(final File source) {
        if (source.isDirectory()) {
            final File[] listFiles;
            final File[] fileList = listFiles = source.listFiles();
            for (final File file : listFiles) {
                deleteFileOrFolder(file);
            }
        }
        return source.delete();
    }
    
    public void deleteFolderFiles(final File source) {
        if (source.isDirectory()) {
            final File[] listFiles;
            final File[] fileList = listFiles = source.listFiles();
            for (final File file : listFiles) {
                file.delete();
            }
        }
    }
    
    public static long getFileCount(final File source) {
        int count = 0;
        if (source.isDirectory()) {
            final File[] listFiles;
            final File[] fileList = listFiles = source.listFiles();
            for (final File file : listFiles) {
                count += (int)getFileCount(file);
            }
            return count;
        }
        return 1L;
    }
    
    public static long getFileOrFolderSize(final File source) {
        long size = 0L;
        if (source.isDirectory()) {
            final File[] fileList = source.listFiles();
            if (fileList != null) {
                for (final File file : fileList) {
                    size += getFileOrFolderSize(file);
                }
            }
            return size;
        }
        return source.length();
    }
    
    public static void copyInputStream(final InputStream in, final OutputStream out) throws Exception {
        final byte[] buffer = new byte[2048];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        out.flush();
        in.close();
    }
    
    public File createTempDir(final String prefix, final String directory) {
        File tempDir = null;
        try {
            tempDir = File.createTempFile(prefix, ".tmp", new File(directory));
            tempDir.delete();
            tempDir.mkdirs();
        }
        catch (final Exception e) {
            tempDir = new File(directory + File.separator + prefix + ".tmp");
            tempDir.mkdirs();
            FileUtil.logger.log(Level.WARNING, "Could not create temp file", e);
        }
        return tempDir;
    }
    
    public long getRequiredSpace(final ArrayList<File> fileList) {
        long space = 0L;
        for (int i = 0; i < fileList.size(); ++i) {
            space += getFileOrFolderSize(fileList.get(i));
        }
        return space;
    }
    
    public static long getNumberOfFiles(final HashMap<Integer, Properties> fileList) {
        final String serverHome = System.getProperty("server.home");
        long count = 0L;
        for (final Map.Entry<Integer, Properties> entry : fileList.entrySet()) {
            final Properties backupProps = entry.getValue();
            final String filePath = backupProps.getProperty("file_path");
            final File file = new File(serverHome, filePath);
            count += getFileCount(file);
        }
        return count;
    }
    
    public long getAvailableSpace(final String file) {
        final File currentFile = new File(file);
        if (!currentFile.exists()) {
            currentFile.mkdirs();
        }
        return currentFile.getUsableSpace();
    }
    
    public String convertBytesToGBorMB(final long bytes) {
        String result = null;
        final long bytesGBRounded = bytes / 1073741824L;
        if (bytesGBRounded > 0L) {
            final long bytesGBRemainder = bytes % 1073741824L;
            String bytesGBRemainderStr = String.valueOf(bytesGBRemainder);
            if (bytesGBRemainderStr.length() > 2) {
                bytesGBRemainderStr = bytesGBRemainderStr.substring(0, 2);
            }
            result = "" + bytesGBRounded + "." + bytesGBRemainderStr + " GB";
        }
        else {
            final long bytesMBRounded = bytes / 1048576L;
            result = "" + bytesMBRounded + " MB";
        }
        return result;
    }
    
    public String getErrorMessage(final long status) {
        String errorMessage = null;
        if (status == -1L) {
            errorMessage = "Run-time copy error (Thru streams)";
        }
        else if (status == -2L) {
            errorMessage = "Operation interrupted.";
        }
        else if (status == -3L) {
            errorMessage = "XCOPY error (Native)";
        }
        else {
            errorMessage = "Unknown Error";
        }
        return errorMessage;
    }
    
    public static boolean renameFolder(final String oldFolder, final String newFolder) {
        try {
            final File newFile = new File(newFolder);
            newFile.getParentFile().mkdirs();
            Files.move(Paths.get(oldFolder, new String[0]), Paths.get(newFolder, new String[0]), StandardCopyOption.ATOMIC_MOVE);
            FileUtil.logger.log(Level.INFO, "{0} Folder is successfully renamed to {1}", new Object[] { oldFolder, newFolder });
            return true;
        }
        catch (final Exception e) {
            FileUtil.logger.log(Level.SEVERE, "Exception while renaming the folder: ", e);
            return false;
        }
    }
    
    public static boolean isFileExists(final String fileName) {
        final File file = new File(fileName);
        return file.exists();
    }
    
    public static boolean isFolderRenamable(final String folderPath) throws Exception {
        File folder = null;
        File renamedFolder = null;
        folder = new File(folderPath);
        renamedFolder = new File(folderPath + System.currentTimeMillis());
        if (!renameFolderWithWaitingTime(folder, renamedFolder)) {
            return false;
        }
        if (!renameFolderWithWaitingTime(renamedFolder, folder)) {
            throw new Exception("Folder name revert failed in folder rename test");
        }
        return true;
    }
    
    public static boolean renameFolderWithWaitingTime(final File folder, final File renamedFolder) throws Exception {
        final int waitingTime = 10;
        int count = 0;
        renameFolder(folder.getAbsolutePath(), renamedFolder.getAbsolutePath());
        while (!renamedFolder.exists() && waitingTime > count) {
            Thread.sleep(1000L);
            ++count;
        }
        return count != waitingTime;
    }
    
    public static void copyFolder(final File srcFolder, final File destFolder) throws Exception {
        if (srcFolder.isDirectory()) {
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }
            final File[] listFiles;
            final File[] filesList = listFiles = srcFolder.listFiles();
            for (final File file : listFiles) {
                copyFolder(new File(srcFolder.getAbsolutePath(), file.getName()), new File(destFolder.getAbsolutePath(), file.getName()));
            }
        }
        else {
            Files.copy(srcFolder.toPath(), destFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    public static void swapFiles(final String filePathOne, final String filePathTwo) throws Exception {
        String temp = "";
        final String tempFile = new File(filePathOne).getParent() + File.separator + "temp.txt";
        BufferedReader br = new BufferedReader(new FileReader(filePathOne));
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
        while ((temp = br.readLine()) != null) {
            bw.write(temp);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();
        br = new BufferedReader(new FileReader(filePathTwo));
        bw = new BufferedWriter(new FileWriter(filePathOne));
        while ((temp = br.readLine()) != null) {
            bw.write(temp);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();
        br = new BufferedReader(new FileReader(tempFile));
        bw = new BufferedWriter(new FileWriter(filePathTwo));
        while ((temp = br.readLine()) != null) {
            bw.write(temp);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();
        new File(tempFile).delete();
    }
    
    static {
        FileUtil.logger = Logger.getLogger(FileUtil.class.getName());
    }
}
