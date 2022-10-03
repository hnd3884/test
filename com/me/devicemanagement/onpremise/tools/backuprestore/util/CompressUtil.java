package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.onpremise.server.extensions.processbuilder.DMProcessBuilder;
import java.util.Collection;
import com.me.devicemanagement.onpremise.server.util.ZipUtil;
import java.util.Iterator;
import java.util.List;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMBackupAction;
import java.util.Map;
import java.util.ArrayList;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Level;
import java.io.File;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;
import java.util.logging.Logger;

public class CompressUtil
{
    private Logger logger;
    public static final int BUFFER = 2048;
    public static final int SUCCESS = 1;
    public static final int ZIP_ERROR = -1;
    public static final int INTERRUPTED = -2;
    private Informable informable;
    private String archivePassword;
    
    public CompressUtil() {
        this.logger = Logger.getLogger(CompressUtil.class.getName());
        this.archivePassword = null;
    }
    
    public CompressUtil(final String archivePassword) {
        this.logger = Logger.getLogger(CompressUtil.class.getName());
        this.archivePassword = null;
        this.archivePassword = archivePassword;
    }
    
    public CompressUtil(final Informable informable, final String archivePassword) {
        this.logger = Logger.getLogger(CompressUtil.class.getName());
        this.archivePassword = null;
        this.informable = informable;
        this.archivePassword = archivePassword;
        if (informable != null) {
            this.logger = Logger.getLogger("DCBackupRestoreUI");
        }
        else {
            this.logger = Logger.getLogger("ScheduleDBBackup");
        }
    }
    
    public int compress(final String source, final String destination) {
        int status = 1;
        try {
            final File sourceFolder = new File(source);
            final File file = new File(destination);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in zipping : " + source, e);
            status = -1;
        }
        return status;
    }
    
    public boolean compress(final HashMap<Integer, Properties> fileList, final String destFolder) throws Exception {
        String fileToAdd = "";
        final List<String> filesToExclude = new ArrayList<String>();
        final String destinationZip = destFolder + ".zip";
        this.logger.log(Level.INFO, "\tDestination zip\t::\t", destinationZip);
        final String serverHome = System.getProperty("server.home");
        for (final Map.Entry<Integer, Properties> entry : fileList.entrySet()) {
            final Properties backupProps = entry.getValue();
            final String includePath = backupProps.getProperty("file_path");
            final String excludePaths = backupProps.getProperty("files_to_exclude");
            final String backupType = backupProps.getProperty("backup_type");
            if (backupType.equalsIgnoreCase("copy")) {
                if (new File(serverHome, includePath).isDirectory()) {
                    fileToAdd = fileToAdd + includePath + "\\* ";
                    if (excludePaths == null) {
                        continue;
                    }
                    for (final String excludeFilePath : excludePaths.split(",")) {
                        if (new File(serverHome + File.separator + includePath + File.separator + excludeFilePath).exists() || excludeFilePath.contains("*") || excludeFilePath.contains(".")) {
                            filesToExclude.add("-x!" + includePath + File.separator + excludeFilePath);
                        }
                        else {
                            this.logger.log(Level.INFO, "No such file or folder :: " + excludeFilePath);
                        }
                    }
                }
                else {
                    fileToAdd = fileToAdd + includePath + " ";
                }
            }
        }
        this.logger.log(Level.INFO, "Files to add before: " + fileToAdd);
        fileToAdd = fileToAdd.substring(0, fileToAdd.length() - 1);
        fileToAdd = "\"" + fileToAdd + "\"";
        boolean status = this.addToArchive(destinationZip, fileToAdd, filesToExclude, serverHome);
        this.logger.log(Level.INFO, "Files to add after: " + fileToAdd);
        BackupRestoreUtil.checkZipTempFile(DMBackupAction.temporaryZip1);
        if (status) {
            status = this.updateToArchive(destinationZip, destFolder);
        }
        return status;
    }
    
    private boolean addToArchive(final String zipFile, final String fileToAdd, final List<String> excludeFileList, final String workingDirectory) {
        String sevenZipLocation = "";
        try {
            sevenZipLocation = this.getSevenZipEXELocation();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while retrieving the 7zip exe Path ", e);
        }
        final String destination = new File(zipFile).getParent();
        final List<String> argumentsList = new ArrayList<String>();
        argumentsList.add("\"" + sevenZipLocation + "\"");
        argumentsList.add("a");
        argumentsList.add("\"" + DMBackupAction.temporaryZip1 + "\"");
        argumentsList.add("-ssw");
        argumentsList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
        this.addPasswordArgument(argumentsList);
        final String includeFileList = "-ir!" + fileToAdd;
        final String workingDir = "-w" + destination;
        argumentsList.add(workingDir);
        argumentsList.add(includeFileList);
        argumentsList.addAll(excludeFileList);
        return this.SevenZipCommands(argumentsList, workingDirectory, "Compressing ");
    }
    
    private boolean updateToArchive(final String zipFile, final String folderToUpdate) {
        this.logger.log(Level.INFO, "Folder to update :: " + folderToUpdate);
        this.logger.log(Level.INFO, "Destination :: " + zipFile);
        String sevenZipLocation = "";
        try {
            sevenZipLocation = this.getSevenZipEXELocation();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while retrieving the 7zip exe Path ", e);
        }
        final String destination = new File(zipFile).getParent();
        final List argumentsList = new ArrayList();
        argumentsList.add("\"" + sevenZipLocation + "\"");
        argumentsList.add("u");
        argumentsList.add("\"" + DMBackupAction.temporaryZip1 + "\"");
        argumentsList.add("-ssw");
        argumentsList.add("-u-");
        argumentsList.add("-uq1r2!" + DMBackupAction.temporaryZip2);
        argumentsList.add("*");
        argumentsList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
        final String workingDir = "-w" + destination;
        argumentsList.add(workingDir);
        this.addPasswordArgument(argumentsList);
        return this.SevenZipCommands(argumentsList, folderToUpdate, "Compressing ");
    }
    
    public boolean extractFromArchive(final String zipFile, final String destination) {
        this.logger.log(Level.INFO, "Extracting from archive..");
        String sevenZipLocation = "";
        try {
            sevenZipLocation = this.getSevenZipEXELocation();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while retrieving the 7zip exe Path ", e);
        }
        final List argumentsList = new ArrayList();
        argumentsList.add("\"" + sevenZipLocation + "\"");
        argumentsList.add("x");
        argumentsList.add("\"" + zipFile + "\"");
        argumentsList.add("-aoa");
        argumentsList.add("-x!backup-files.xml");
        argumentsList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
        this.addPasswordArgument(argumentsList);
        return this.SevenZipCommands(argumentsList, destination, "Extracting ");
    }
    
    public boolean extractFileFromArchive(final String zipFile, final String filenameToBeExtracted, final String destination) {
        this.logger.log(Level.INFO, "Extracting {0} from archive..", filenameToBeExtracted);
        String sevenZipLocation = null;
        try {
            sevenZipLocation = this.getSevenZipEXELocation();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while retrieving the 7zip exe Path ", e);
        }
        final List argumentsList = new ArrayList();
        argumentsList.add("\"" + sevenZipLocation + "\"");
        argumentsList.add("x");
        argumentsList.add("\"" + zipFile + "\"");
        argumentsList.add("\"" + filenameToBeExtracted + "\"");
        argumentsList.add("-y");
        argumentsList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
        this.addPasswordArgument(argumentsList);
        return this.SevenZipCommands(argumentsList, destination, "Extracting ");
    }
    
    public boolean updateFileToArchive(final String zipFile, final String fileToAdd, final String workingDirectory) {
        String sevenZipLocation = "";
        try {
            sevenZipLocation = this.getSevenZipEXELocation();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while retrieving the 7zip exe Path ", e);
        }
        final List<String> argumentsList = new ArrayList<String>();
        argumentsList.add("\"" + sevenZipLocation + "\"");
        argumentsList.add("u");
        argumentsList.add("\"" + DMBackupAction.temporaryZip2 + "\"");
        argumentsList.add("-u-");
        argumentsList.add("-ssw");
        argumentsList.add("-uq1r2!" + DMBackupAction.finalZip);
        argumentsList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
        argumentsList.add(fileToAdd);
        this.addPasswordArgument(argumentsList);
        return this.SevenZipCommands(argumentsList, workingDirectory, "Compressing ");
    }
    
    private boolean SevenZipCommands(final List<String> argumentsList, final String workingDirectory, final String displayString) {
        boolean executionStatus = true;
        try {
            this.logger.log(Level.INFO, "WDirectory " + workingDirectory);
            final File workingDir = new File(workingDirectory);
            Process process = null;
            if (BackupRestoreUtil.getInstance().useNativeForExecution()) {
                this.logger.log(Level.INFO, "executing in DMProcessBuilder");
                final DMProcessBuilder dpb = new DMProcessBuilder((List)argumentsList);
                dpb.redirectErrorStream(true);
                dpb.directory(workingDir);
                process = dpb.start();
            }
            else {
                this.logger.log(Level.INFO, "executing in ProcessBuilder");
                final ProcessBuilder pb = new ProcessBuilder(argumentsList);
                pb.redirectErrorStream(true);
                pb.directory(workingDir);
                process = pb.start();
            }
            final BufferedReader commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = null;
            final int len = displayString.length();
            this.logger.log(Level.INFO, "\t\t ******************* 7za.exe OUTPUT *******************");
            final StringBuilder sb = new StringBuilder();
            for (String string : argumentsList) {
                if (string.startsWith("-p")) {
                    string = "-p****";
                }
                sb.append(string).append(" ");
            }
            this.logger.log(Level.INFO, "\t\tCommand :: {0}", sb);
            while ((s = commandOutput.readLine()) != null) {
                this.showMessage(s, displayString, len);
            }
            final int exitCode = process.waitFor();
            this.logger.log(Level.INFO, "\t\tExit code :: {0}", exitCode);
            if (exitCode == 1073741502 && !BackupRestoreUtil.getInstance().useNativeForExecution()) {
                BackupRestoreUtil.getInstance().useDLLForBackupExe();
            }
            this.logger.log(Level.INFO, "\t\t ********************* 7za.exe OUTPUT **************************");
            if (exitCode != 0 && exitCode != 1) {
                this.logger.log(Level.WARNING, "Seems like fatal error. 7Zip failed!");
                executionStatus = false;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while executing 7zip command", e);
            executionStatus = false;
        }
        return executionStatus;
    }
    
    private String getSevenZipEXELocation() throws Exception {
        final String serverHome = System.getProperty("server.home");
        String binPath = serverHome + File.separator + "bin" + File.separator + "7za.exe";
        binPath = new File(binPath).getCanonicalPath();
        return binPath;
    }
    
    private void showMessage(final String message, final String status, final int index) {
        if (message.startsWith(status)) {
            if (this.informable != null) {
                this.informable.messageRead(message.substring(index));
            }
            else {
                this.logger.log(Level.FINEST, message);
            }
        }
        else if (message.length() > 0) {
            this.logger.log(Level.INFO, "\t" + message);
        }
    }
    
    public String getExtension() {
        return ".zip";
    }
    
    public boolean decompress(final String source, final String destination) {
        return this.extractFromArchive(source, destination);
    }
    
    public boolean decompress(final String source, final String filePath, final String destination) {
        final String arguments = "x \"" + source + "\" -aoa \"" + filePath + "\"";
        String sevenZipLocation = "";
        try {
            sevenZipLocation = this.getSevenZipEXELocation();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while retrieving the 7zip exe Path ", e);
        }
        final List argumentsList = new ArrayList();
        argumentsList.add("\"" + sevenZipLocation + "\"");
        argumentsList.add("x");
        argumentsList.add("\"" + source + "\"");
        argumentsList.add("-aoa");
        argumentsList.add("\"" + filePath + "\"");
        argumentsList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
        this.addPasswordArgument(argumentsList);
        return this.SevenZipCommands(argumentsList, destination, "Extracting ");
    }
    
    private void addPasswordArgument(final List commandList) {
        if (this.archivePassword != null) {
            commandList.add("-p\"" + this.archivePassword + "\"");
        }
    }
}
