package com.adventnet.db.adapter.postgres;

import java.nio.file.Files;
import java.io.IOException;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.io.File;
import com.adventnet.db.adapter.BackupRestoreException;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.DMProcessBuilderHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.List;
import java.util.logging.Logger;

public class DMPostgresBackupHandler extends PostgresBackupHandler
{
    private static final Logger LOGGER;
    
    protected void executeCommand(final List<String> cmds, final Properties envProps, final String errorMsgToIgnore) throws BackupRestoreException {
        DMPostgresBackupHandler.LOGGER.log(Level.INFO, "Calling executeCommand()");
        DMProcessBuilderHandler.getInstance().executeCommand((List)cmds, envProps, errorMsgToIgnore);
    }
    
    protected void zip(final File zipFolder, final String zipFileName, final File contentDirectory, final boolean includeContentDirectoryToo, final boolean includeFilesInContentDirectory, final List<String> includeFileList, final List<String> excludeFileList, final String archivePassword, final String encAlgo) throws BackupRestoreException {
        DMPostgresBackupHandler.LOGGER.log(Level.INFO, "Calling zip()");
        if (BackupRestoreUtil.getInstance().useNativeForExecution()) {
            DMProcessBuilderHandler.getInstance().zip(zipFolder, zipFileName, contentDirectory, includeContentDirectoryToo, includeFilesInContentDirectory, (List)includeFileList, (List)excludeFileList, archivePassword, encAlgo);
        }
        else {
            super.zip(zipFolder, zipFileName, contentDirectory, includeContentDirectoryToo, includeFilesInContentDirectory, (List)includeFileList, (List)excludeFileList, archivePassword, encAlgo);
        }
    }
    
    protected void appendInZip(final String zipFilePath, final List<String> includeFileList, final String archivePassword, final String encAlgo) throws BackupRestoreException {
        DMPostgresBackupHandler.LOGGER.log(Level.INFO, "Calling appendInZip()");
        if (BackupRestoreUtil.getInstance().useNativeForExecution()) {
            DMProcessBuilderHandler.getInstance().appendInZip(zipFilePath, (List)includeFileList, archivePassword, encAlgo);
        }
        else {
            super.appendInZip(zipFilePath, (List)includeFileList, archivePassword, encAlgo);
        }
    }
    
    protected void unZip(final File zipFile, final File destinationFolder, final List<String> includeFileList, final List<String> excludeFileList) throws IOException, InterruptedException {
        DMPostgresBackupHandler.LOGGER.log(Level.INFO, "Calling unZip()");
        if (BackupRestoreUtil.getInstance().useNativeForExecution()) {
            DMProcessBuilderHandler.getInstance().unZip(zipFile, destinationFolder, (List)includeFileList, (List)excludeFileList, (String)null);
        }
        else {
            super.unZip(zipFile, destinationFolder, (List)includeFileList, (List)excludeFileList);
        }
    }
    
    protected String getAllVersions(final File specsFile) throws IOException {
        final byte[] b = Files.readAllBytes(specsFile.toPath());
        final String strFromSpecsFile = new String(b);
        final String versionString = strFromSpecsFile.substring(strFromSpecsFile.indexOf(" AllVersions=") + 14, strFromSpecsFile.indexOf(" Versions=") - 1);
        return versionString;
    }
    
    static {
        LOGGER = Logger.getLogger(DMPostgresBackupHandler.class.getName());
    }
}
