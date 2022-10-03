package com.me.devicemanagement.onpremise.tools.backuprestore.handler;

import java.util.Hashtable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.io.IOException;
import com.me.devicemanagement.onpremise.server.util.ZipUtil;
import java.util.ArrayList;
import java.io.File;
import java.util.Enumeration;
import java.util.Map;
import com.adventnet.db.adapter.BackupErrors;
import com.adventnet.db.adapter.BackupRestoreException;
import com.adventnet.db.adapter.RestoreErrors;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.onpremise.server.extensions.processbuilder.DMProcessBuilder;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.util.Locale;
import java.util.Properties;
import java.util.List;
import java.util.logging.Logger;

public class DMProcessBuilderHandler
{
    private static DMProcessBuilderHandler ins;
    private static final Logger LOGGER;
    
    private DMProcessBuilderHandler() {
    }
    
    public static DMProcessBuilderHandler getInstance() {
        if (DMProcessBuilderHandler.ins == null) {
            DMProcessBuilderHandler.ins = new DMProcessBuilderHandler();
        }
        return DMProcessBuilderHandler.ins;
    }
    
    public void executeCommand(final List<String> cmds, final Properties envProps, String errorMsgToIgnore) throws BackupRestoreException {
        if (errorMsgToIgnore != null) {
            errorMsgToIgnore = errorMsgToIgnore.toLowerCase(Locale.ENGLISH);
        }
        Process process = null;
        try {
            if (BackupRestoreUtil.getInstance().useNativeForExecution()) {
                DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Executing command in DMProcessBuilder");
                final DMProcessBuilder processBuilder = new DMProcessBuilder((List)cmds);
                final Map<String, String> environment = processBuilder.environment();
                if (envProps != null) {
                    final Enumeration<Object> keys = ((Hashtable<Object, V>)envProps).keys();
                    while (keys.hasMoreElements()) {
                        final String key = keys.nextElement();
                        environment.put(key, envProps.getProperty(key));
                    }
                }
                process = processBuilder.start();
            }
            else {
                DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Executing command in ProcessBuilder");
                final ProcessBuilder processBuilder2 = new ProcessBuilder(cmds);
                final Map<String, String> environment = processBuilder2.environment();
                if (envProps != null) {
                    final Enumeration<Object> keys = ((Hashtable<Object, V>)envProps).keys();
                    while (keys.hasMoreElements()) {
                        final String key = keys.nextElement();
                        environment.put(key, envProps.getProperty(key));
                    }
                }
                process = processBuilder2.start();
            }
            try (final BufferedReader inputStreamBuff = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                while ((line = inputStreamBuff.readLine()) != null) {
                    DMProcessBuilderHandler.LOGGER.info(line);
                }
            }
            try (final BufferedReader errStreamBuff = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errStreamString = null;
                while ((errStreamString = errStreamBuff.readLine()) != null) {
                    DMProcessBuilderHandler.LOGGER.warning(errStreamString);
                    errStreamString = errStreamString.toLowerCase(Locale.ENGLISH);
                    if (errorMsgToIgnore != null && !errStreamString.contains(errorMsgToIgnore)) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND);
                    }
                }
            }
            final int waitFor = process.waitFor();
            final int exitValue = process.exitValue();
            DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Wait For returns :: [{0}] Process exitValue :: [{1}]", new Object[] { waitFor, exitValue });
            if (exitValue != 0) {
                BackupRestoreUtil.getInstance().addBackupTrackingDetails(6, String.valueOf(exitValue));
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND);
            }
        }
        catch (final Exception e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_EXECUTING_COMMAND, (Throwable)e);
        }
        finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    
    public void zip(final File zipFolder, final String zipFileName, final File contentDirectory, final boolean includeContentDirectoryToo, final boolean includeFilesInContentDirectory, final List<String> includeFileList, final List<String> excludeFileList, final String archivePassword, final String encAlgo) throws BackupRestoreException {
        DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Entered createZip(), backupDir :: [{0}], zipFileName :: [{1}], contentDirectory :: [{2}], includeContentDirectoryToo :: [{3}], includeFileList :: [{4}], excludeFileList :: [{5}]", new Object[] { zipFolder, zipFileName, contentDirectory, includeContentDirectoryToo, includeFileList, excludeFileList });
        try {
            if (!zipFolder.exists()) {
                zipFolder.mkdirs();
            }
            DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Going to create the Incremental/Full BackUpFile :: [{0}]", zipFileName);
            final List<String> commandList = new ArrayList<String>();
            commandList.add(this.get7zPath());
            commandList.add("u");
            commandList.add(zipFolder.getAbsolutePath() + File.separator + zipFileName);
            commandList.add("-tzip");
            commandList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
            if (includeContentDirectoryToo) {
                commandList.add(contentDirectory.getAbsolutePath());
            }
            if (includeFilesInContentDirectory) {
                commandList.add(contentDirectory.getAbsolutePath() + File.separator + "*");
            }
            if (includeFileList != null) {
                for (final String file : includeFileList) {
                    commandList.add("-i!" + file);
                }
            }
            if (excludeFileList != null) {
                for (final String file : excludeFileList) {
                    commandList.add("-x!" + file);
                }
            }
            this.addEncryptionToCommand(commandList, archivePassword, encAlgo);
            final int exitValue = this.executeProcess(commandList);
            DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Exiting createZip() with value :: [{0}]", exitValue);
            if (exitValue != 0) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING);
            }
        }
        catch (final IOException | InterruptedException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING, (Throwable)e);
        }
    }
    
    public void appendInZip(final String zipFilePath, final List<String> includeFileList, final String archivePassword, final String encAlgo) throws BackupRestoreException {
        DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Entered DMProcessBuilderHandler#appendInZip(), zipFileName :: [{0}], includeFileList :: [{1}],", new Object[] { zipFilePath, includeFileList });
        try {
            if (!new File(zipFilePath).exists()) {
                throw new IOException("Zip File does not exists :: " + zipFilePath);
            }
            final File zipFile = new File(zipFilePath);
            final String temporaryZip = zipFile.getParentFile().getPath() + File.separator + zipFile.getName().replace(".zip", "_db_backup_temp.zip").replace(".ezip", "_db_backup_temp.ezip");
            DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Going to append files to zip :: [{0}]", zipFilePath);
            final List<String> commandList = new ArrayList<String>();
            commandList.add(this.get7zPath());
            commandList.add("u");
            commandList.add("\"" + zipFilePath + "\"");
            commandList.add("-u-");
            commandList.add("-uq1r2!" + temporaryZip);
            commandList.add("-tzip");
            commandList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
            if (includeFileList != null) {
                for (final String file : includeFileList) {
                    commandList.add("-i!" + file);
                }
            }
            this.addEncryptionToCommand(commandList, archivePassword, encAlgo);
            final int exitValue = this.executeProcess(commandList);
            DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Exiting appendInZip() with value :: [{0}]", exitValue);
            if (exitValue != 0) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING);
            }
            Files.delete(Paths.get(zipFilePath, new String[0]));
            new File(temporaryZip).renameTo(new File(zipFilePath));
        }
        catch (final IOException | InterruptedException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING, (Throwable)e);
        }
    }
    
    public int unZip(final File zipFile, final File destinationFolder, final List<String> includeFileList, final List<String> excludeFileList, final String archivePassword) throws IOException, InterruptedException {
        DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Entered extract7zipFile() :: zipFileName :: [{0}]", zipFile);
        final List<String> commandList = new ArrayList<String>();
        commandList.add(this.get7zPath());
        commandList.add("x");
        commandList.add(zipFile.getCanonicalPath());
        commandList.add("-y");
        commandList.add("-o" + ((destinationFolder == null) ? "tmp" : destinationFolder.getCanonicalPath()));
        commandList.add("-mmt=" + ZipUtil.get7ZipCoreCount());
        if (includeFileList != null) {
            for (final String file : includeFileList) {
                commandList.add("-i!" + file);
            }
        }
        if (excludeFileList != null) {
            for (final String file : excludeFileList) {
                commandList.add("-x!" + file);
            }
        }
        if (archivePassword != null && archivePassword.length() > 0) {
            commandList.add("-p\"" + archivePassword + '\"');
        }
        final int exitValue = this.executeProcess(commandList);
        DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Exiting extract7zipFile() with value :: [{0}]", exitValue);
        return exitValue;
    }
    
    private void addEncryptionToCommand(final List<String> commandList, final String archivePassword, final String encAlgo) {
        if (archivePassword != null && archivePassword.length() > 0) {
            commandList.add("-p\"" + archivePassword + '\"');
            if (encAlgo != null) {
                commandList.add("-mem=" + encAlgo);
            }
        }
    }
    
    private int executeProcess(final List<String> commands) throws IOException, InterruptedException {
        int exitValue = -1;
        DMProcessBuilder processBuilder = null;
        Process start = null;
        try {
            final StringBuilder sb = new StringBuilder("[");
            for (String string : commands) {
                if (string.startsWith("-p")) {
                    string = "-p*****";
                }
                sb.append(string).append(", ");
            }
            sb.append("]");
            DMProcessBuilderHandler.LOGGER.log(Level.INFO, "Command to be executed ::: {0}", sb);
            processBuilder = new DMProcessBuilder((List)commands);
            processBuilder.redirectErrorStream(true);
            start = processBuilder.start();
            DMProcessBuilderHandler.LOGGER.log(Level.INFO, "7za.exe output:");
            try (final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(start.getInputStream()))) {
                String line = null;
                while ((line = ipBuf.readLine()) != null) {
                    DMProcessBuilderHandler.LOGGER.info(line);
                }
            }
            start.waitFor();
            exitValue = start.exitValue();
            DMProcessBuilderHandler.LOGGER.log(Level.INFO, "7za.exe execution completed. Exit code : " + exitValue);
        }
        finally {
            if (start != null) {
                start.destroy();
            }
        }
        return exitValue;
    }
    
    private String get7zPath() {
        final String sevenZipPath = System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za.exe";
        return sevenZipPath;
    }
    
    static {
        DMProcessBuilderHandler.ins = null;
        LOGGER = Logger.getLogger(DMProcessBuilderHandler.class.getName());
    }
}
