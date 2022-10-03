package com.me.devicemanagement.onpremise.server.util;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;
import java.util.logging.Logger;

public class ZipUtil
{
    static Logger logger;
    private Informable informable;
    
    public void createZipFile(final String sourceFolder, final String destFile, final boolean copyBeforeZip, String copyFolder) throws Exception {
        ZipUtil.logger.log(Level.INFO, "ZipUtil.createZipFile() is invoked with sourceFolder: " + sourceFolder + " destFile: " + destFile + "copyBeforeZip: " + copyBeforeZip + " tempFolder: " + copyFolder);
        if (sourceFolder == null || destFile == null) {
            ZipUtil.logger.log(Level.WARNING, "Source Folder or Destination is null.");
            return;
        }
        if (copyBeforeZip && copyFolder == null) {
            ZipUtil.logger.log(Level.WARNING, "Copy Folder cannot be null while copyBeforeZip is true");
            return;
        }
        File srcFile = new File(sourceFolder);
        if (!srcFile.isFile() && !srcFile.isDirectory()) {
            ZipUtil.logger.log(Level.WARNING, "Invalid Source file or directory!");
            return;
        }
        File copyFolderFile = null;
        String copyTmpFolder = null;
        try {
            String supressZipEntry = "";
            if (copyBeforeZip) {
                String tmpFolderName = new File(destFile).getName();
                tmpFolderName = tmpFolderName.substring(0, tmpFolderName.lastIndexOf(46));
                copyTmpFolder = copyFolder + File.separator + tmpFolderName;
                if (srcFile.isDirectory()) {
                    copyFolder = copyTmpFolder + File.separator + srcFile.getName();
                }
                else {
                    copyFolder = copyTmpFolder;
                }
                copyFolderFile = new File(copyFolder);
                if (!copyFolderFile.exists() && !copyFolderFile.mkdirs()) {
                    throw new IOException("copyFiles: Could not create direcotry: " + copyFolderFile.getAbsolutePath());
                }
                final File orgSrc = srcFile;
                this.copyFiles(srcFile, copyFolderFile);
                srcFile = copyFolderFile;
                if (orgSrc.isDirectory()) {
                    supressZipEntry = srcFile.getAbsoluteFile().getParent();
                }
                else {
                    supressZipEntry = srcFile.getAbsoluteFile().getAbsolutePath();
                }
            }
            else if (srcFile.isDirectory()) {
                supressZipEntry = srcFile.getAbsoluteFile().getParent();
            }
            ZipUtil.logger.log(Level.INFO, "suppressZipEntry: " + supressZipEntry);
            final ZipOutputStream targetZipOutputStream = new ZipOutputStream(new FileOutputStream(destFile));
            targetZipOutputStream.setLevel(9);
            this.zipFiles(srcFile, targetZipOutputStream, supressZipEntry);
            targetZipOutputStream.finish();
            targetZipOutputStream.close();
            ZipUtil.logger.log(Level.INFO, "Finished creating zip file " + destFile + " from source " + sourceFolder);
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            if (copyTmpFolder != null) {
                ZipUtil.logger.log(Level.INFO, "Going to delete folder and files: " + copyTmpFolder);
                this.deleteFileOrFolder(new File(copyTmpFolder));
            }
        }
    }
    
    private void zipFiles(final File cpFile, final ZipOutputStream targetZipOutputStream, final String suppressZipEntry) throws Exception {
        if (cpFile.isDirectory()) {
            final File[] fList = cpFile.listFiles();
            for (int i = 0; i < fList.length; ++i) {
                this.zipFiles(fList[i], targetZipOutputStream, suppressZipEntry);
            }
        }
        else {
            FileInputStream cpFileInputStream = null;
            try {
                final String strAbsPath = cpFile.getAbsolutePath();
                String strZipEntryName = "";
                if (!suppressZipEntry.equals("")) {
                    strZipEntryName = strAbsPath.substring(suppressZipEntry.length() + 1, strAbsPath.length());
                }
                else {
                    strZipEntryName = cpFile.getName();
                }
                final byte[] b = new byte[(int)cpFile.length()];
                cpFileInputStream = new FileInputStream(cpFile);
                final int j = cpFileInputStream.read(b, 0, (int)cpFile.length());
                final ZipEntry cpZipEntry = new ZipEntry(strZipEntryName);
                targetZipOutputStream.putNextEntry(cpZipEntry);
                targetZipOutputStream.write(b, 0, (int)cpFile.length());
                targetZipOutputStream.closeEntry();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            finally {
                if (cpFileInputStream != null) {
                    cpFileInputStream.close();
                }
            }
        }
    }
    
    public void copyFiles(final File src, File dest) throws Exception {
        if (!src.exists()) {
            ZipUtil.logger.log(Level.WARNING, "copyFiles: Unable to copy file. File not found. Source: " + src.getAbsolutePath());
            return;
        }
        if (!src.canRead()) {
            throw new IOException("copyFiles: No rights to read the file: " + src.getAbsolutePath());
        }
        if (src.isDirectory()) {
            if (!dest.exists() && !dest.mkdirs()) {
                throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath());
            }
            final String[] list = src.list();
            for (int i = 0; i < list.length; ++i) {
                final File dest2 = new File(dest, list[i]);
                final File src2 = new File(src, list[i]);
                this.copyFiles(src2, dest2);
            }
        }
        else {
            FileInputStream fin = null;
            FileOutputStream fout = null;
            final byte[] buffer = new byte[4096];
            try {
                if (dest.isDirectory()) {
                    dest = new File(dest.getAbsolutePath() + File.separator + src.getName());
                }
                try {
                    fin = new FileInputStream(src);
                }
                catch (final IOException ioex) {
                    ZipUtil.logger.log(Level.WARNING, "copyFiles: Error while reading File: " + src.getAbsolutePath(), ioex);
                    if (!src.exists()) {
                        ZipUtil.logger.log(Level.WARNING, "copyFiles: Unable to copy file. File not found. Source: " + src.getAbsolutePath());
                        return;
                    }
                    throw ioex;
                }
                fout = new FileOutputStream(dest);
                int bytesRead;
                while ((bytesRead = fin.read(buffer)) >= 0) {
                    fout.write(buffer, 0, bytesRead);
                }
            }
            catch (final Exception ex) {
                ZipUtil.logger.log(Level.WARNING, "copyFiles: Unable to copy file. Source: " + src.getAbsolutePath() + " Dest: " + dest.getAbsolutePath(), ex);
                throw ex;
            }
            finally {
                if (fin != null) {
                    fin.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }
        }
    }
    
    public void deleteFileOrFolder(final File src) {
        if (src.isDirectory()) {
            final File[] fileList = src.listFiles();
            for (int i = 0; i < fileList.length; ++i) {
                this.deleteFileOrFolder(fileList[i]);
            }
            src.delete();
        }
        else {
            src.delete();
        }
    }
    
    public boolean SevenZipCommand(final String[] arguments, final String displayString) {
        boolean executionStatus = true;
        try {
            final String sevenZipExeLoc = this.getSevenZipEXELocation();
            arguments[0] = sevenZipExeLoc;
            String command = "";
            for (final String argument : arguments) {
                command = command + argument + " ";
            }
            final File dir = new File(System.getProperty("server.home"));
            final ProcessBuilder builder = new ProcessBuilder(arguments);
            builder.directory(dir.getCanonicalFile());
            final Process process = builder.start();
            final BufferedReader commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            final BufferedReader commandErrorOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s = null;
            final int len = displayString.length() + 1;
            ZipUtil.logger.log(Level.INFO, "\t\t ################ 7za.exe OUTPUT ################");
            ZipUtil.logger.log(Level.INFO, "\t\tCommand :: {0}", command);
            while ((s = commandOutput.readLine()) != null) {
                this.showMessage(s, displayString, len);
            }
            while ((s = commandErrorOutput.readLine()) != null) {
                this.showMessage(s, displayString, len);
            }
            final int exitCode = process.exitValue();
            ZipUtil.logger.log(Level.INFO, "\t\tExit code :: {0}", exitCode);
            ZipUtil.logger.log(Level.INFO, "\t\t ################ 7za.exe OUTPUT ################");
            if (exitCode != 0 && exitCode != 1) {
                ZipUtil.logger.log(Level.WARNING, "Seems like fatal error. 7Zip failed!");
                executionStatus = false;
            }
        }
        catch (final Exception e) {
            ZipUtil.logger.log(Level.WARNING, "Exception while executing 7zip command", e);
            executionStatus = false;
        }
        return executionStatus;
    }
    
    private String getSevenZipEXELocation() throws Exception {
        final String serverHome = System.getProperty("server.home");
        String binPath = serverHome + File.separator + "bin\\7za.exe";
        binPath = new File(binPath).getCanonicalPath();
        return binPath;
    }
    
    private void showMessage(final String message, final String status, final int index) {
        if (message.startsWith(status)) {
            if (this.informable != null) {
                this.informable.messageRead(message.substring(index));
            }
            else {
                ZipUtil.logger.log(Level.FINEST, message);
            }
        }
        else {
            ZipUtil.logger.log(Level.INFO, "\t\t{0}", message);
        }
    }
    
    public static boolean extract7zFile(final String source, final String destination) {
        final String[] arguments = { "7za.exe", "x", source, "-o" + destination, "-mmt=" + get7ZipCoreCount() };
        return new ZipUtil().SevenZipCommand(arguments, "");
    }
    
    public static int get7ZipCoreCount() {
        int coreCount = 1;
        try {
            coreCount = ServerProfileUtil.getProfileParameter().get("sevenZipCore");
            coreCount = ((coreCount > 0) ? coreCount : 1);
        }
        catch (final Exception e) {
            ZipUtil.logger.log(Level.WARNING, "Exception while retrieving the 7zip core usage count ", e);
        }
        return coreCount;
    }
    
    public static void main(final String[] args) throws Exception {
        if (args == null || args.length < 2) {
            ZipUtil.logger.log(Level.INFO, "Usage: java ZipUtil <directory or file to be zipped> <name of zip file to be created>");
            return;
        }
        final ZipUtil zUtil = new ZipUtil();
        zUtil.createZipFile(args[0], args[1], false, null);
    }
    
    static {
        ZipUtil.logger = Logger.getLogger(ZipUtil.class.getName());
    }
}
