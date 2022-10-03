package com.adventnet.persistence;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import com.adventnet.db.adapter.BackupDBParams;
import com.adventnet.sym.server.patch.util.CommandExecutor;
import java.util.Properties;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import org.apache.commons.io.FilenameUtils;
import java.util.Collection;
import java.io.FileFilter;
import org.apache.commons.io.FileUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import com.zoho.framework.utils.archive.SevenZipUtils;
import java.util.ArrayList;
import java.io.File;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.sym.server.admin.DMApplnHandlerForProductChange;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;

public class StandAlonePersistenceDCExtn extends StandAlonePersistenceExtn
{
    private static final String TEMP_BACKUP_PASSWORD = "Password123";
    
    public void startServer() throws Exception {
        int status = 0;
        try {
            this.startServerSettings();
            this.populatePatchMgmtData();
            if (!DMApplicationHandler.getInstance().getDesktopModuleState()) {
                DMApplnHandlerForProductChange.changeSchedulerStatusForMDM(false);
            }
            DMApplicationHandler.setDCModulesInExtn();
            Thread.sleep(3000L);
            if (System.getProperty("shrinkDB") != null && System.getProperty("shrinkDB").equals("false")) {
                ConsoleOut.println("shrink DB operation excluded..");
            }
            else {
                status = this.backup();
            }
        }
        finally {
            this.stopDB();
        }
        Thread.sleep(30000L);
        final String tempDir = StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "tempDirectory";
        if (status > 0) {
            boolean dataRenameStatus = false;
            if (new File(StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data").exists()) {
                ConsoleOut.println("Data Folder rename to Location :  " + new File(tempDir + File.separator + "data1").getAbsolutePath());
                dataRenameStatus = new File(StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data").renameTo(new File(tempDir + File.separator + "data_1"));
                ConsoleOut.println("Data Folder rename Status :  " + dataRenameStatus);
            }
            if (dataRenameStatus && new File(tempDir + File.separator + "backup_data" + File.separator + "backupdata.ezip").exists()) {
                if (!new File(StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data").exists()) {
                    ConsoleOut.println("Data Folder create status  " + new File(StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data").mkdirs());
                }
                final List<String> excludeFiles = new ArrayList<String>();
                excludeFiles.add("conf");
                final int unzipStatus = SevenZipUtils.unZip(new File(tempDir + File.separator + "backup_data" + File.separator + "backupdata.ezip"), new File(StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data"), (List)null, (List)excludeFiles, "Password123");
                ConsoleOut.println("unzipStatus :  " + unzipStatus);
                if (unzipStatus != 0) {
                    Thread.sleep(3000L);
                    ConsoleOut.println("Data Folder rename to Location :  " + new File(tempDir + File.separator + "partialrestore_data").getAbsolutePath());
                    dataRenameStatus = new File(StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data").renameTo(new File(tempDir + File.separator + "partialrestore_data"));
                    ConsoleOut.println("Data Folder rename Status :  " + dataRenameStatus);
                    ConsoleOut.println("Data Folder rename to Location :  " + new File(tempDir + File.separator + "partialrestore_data").getAbsolutePath());
                    dataRenameStatus = new File(tempDir + File.separator + "data_1").renameTo(new File(StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data"));
                    ConsoleOut.println("Data Folder rename Status :  " + dataRenameStatus);
                }
            }
        }
        if (new File(tempDir).exists()) {
            final boolean isDeleteDirectory = ApiFactoryProvider.getFileAccessAPI().deleteDirectory(tempDir);
            ConsoleOut.println("Delete Temp Directory Status :  " + isDeleteDirectory);
        }
        final File pgWal = new File(StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data" + File.separator + "pg_wal");
        ConsoleOut.println("pg_wal folder size: " + FileUtils.sizeOfDirectory(pgWal) / 1024L + " kb");
        final File[] pgWalFilesList = pgWal.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return pathname.isFile();
            }
        });
        ConsoleOut.println("pg_wal files count : " + pgWalFilesList.length);
    }
    
    public ArrayList<String> findLockedFileORDir(final File masterFile) {
        final ArrayList<String> lockedFileList = new ArrayList<String>();
        try {
            if (masterFile.isDirectory()) {
                if (!masterFile.renameTo(this.getNewFile(masterFile))) {
                    lockedFileList.add(masterFile.getAbsolutePath());
                    for (final File subFiles : masterFile.listFiles()) {
                        lockedFileList.addAll(this.findLockedFileORDir(subFiles));
                    }
                }
            }
            else {
                final boolean renameStatus = masterFile.renameTo(this.getNewFile(masterFile));
                if (!renameStatus) {
                    lockedFileList.add(masterFile.getAbsolutePath());
                }
            }
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception :: " + e);
        }
        return lockedFileList;
    }
    
    public File getNewFile(final File oldFile) {
        if (oldFile.isDirectory()) {
            return new File(oldFile.getParentFile() + File.separator + FilenameUtils.getBaseName(oldFile.getAbsolutePath()) + "_Renamed");
        }
        String newFilePath = oldFile.getParentFile() + File.separator + FilenameUtils.getBaseName(oldFile.getAbsolutePath()) + "_Renamed";
        newFilePath += ("".equalsIgnoreCase(FilenameUtils.getExtension(oldFile.getAbsolutePath())) ? ("." + FilenameUtils.getExtension(oldFile.getAbsolutePath())) : "");
        return new File(newFilePath);
    }
    
    private boolean copyFileOrDirectory(final File sourceLocation, final File targetLocation) {
        try {
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdirs();
                }
                final String[] children = sourceLocation.list();
                for (int i = 0; i < children.length; ++i) {
                    if (!this.copyFileOrDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]))) {
                        return false;
                    }
                }
                return true;
            }
            return this.copyFile(sourceLocation, targetLocation);
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception : " + e);
            return false;
        }
    }
    
    private boolean copyFile(final File srcFile, final File destFile) throws Exception {
        InputStream inFile = null;
        OutputStream outFile = null;
        ConsoleOut.println("Going to copy file : " + srcFile.getAbsolutePath());
        try {
            if (srcFile.exists()) {
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
            }
            return true;
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception : " + e);
            return false;
        }
        finally {
            if (inFile != null) {
                inFile.close();
            }
            if (outFile != null) {
                outFile.close();
            }
        }
    }
    
    private void populatePatchMgmtData() {
        FileInputStream fis = null;
        try {
            StandAlonePersistenceDCExtn.serverHome = System.getProperty("server.home");
            final String pmFolder = StandAlonePersistenceDCExtn.serverHome + File.separator + "pm";
            final String pmPopulationConfFile = pmFolder + File.separator + "population-data.conf";
            final File f = new File(pmPopulationConfFile);
            if (!f.exists()) {
                ConsoleOut.println("Unable to populate Patch Mgmt data. File:  " + pmPopulationConfFile + " is not found");
                return;
            }
            final Properties props = new Properties();
            fis = new FileInputStream(pmPopulationConfFile);
            props.load(fis);
            ConsoleOut.println("contents of " + pmPopulationConfFile + " is :" + props);
            String[] popOrder = null;
            final String populationOrderStr = props.getProperty("population-order");
            ConsoleOut.println("PM population order: " + populationOrderStr);
            if (populationOrderStr == null) {
                ConsoleOut.println("Unable to populate Patch Mgmt data.   Data population order is not found in " + pmPopulationConfFile);
                return;
            }
            popOrder = populationOrderStr.split(",");
            for (int s = 0; s < popOrder.length; ++s) {
                final String fname = pmFolder + File.separator + popOrder[s];
                ConsoleOut.println("populating the sql file: " + fname);
                CommandExecutor.dumpData(fname);
            }
        }
        catch (final Exception ex) {
            ConsoleOut.println("Caught exception while populating patch mgmt data. Exception is " + ex);
            ex.printStackTrace();
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception ex) {
                    ConsoleOut.println("Caught exception while clsoing the FileOutputStream");
                }
            }
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception ex2) {
                    ConsoleOut.println("Caught exception while clsoing the FileOutputStream");
                }
            }
        }
    }
    
    public static void main(final String[] args) throws Exception {
        try {
            final StandAlonePersistenceDCExtn standAloneObj = new StandAlonePersistenceDCExtn();
            standAloneObj.startServer();
            System.exit(0);
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public int backup() throws Exception {
        final String tempDir = StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "tempDirectory";
        if (!new File(tempDir).exists()) {
            new File(tempDir).mkdirs();
        }
        final String existPGSQLFolder = StandAlonePersistenceDCExtn.serverHome + File.separator + "pgsql" + File.separator + "data";
        final String renamePGSQLFolder = tempDir + File.separator + "existing_data";
        System.setProperty("tools.7zip.win.path", StandAlonePersistenceDCExtn.serverHome + File.separator + "bin" + File.separator + "7za");
        final boolean fileOrFolderCopyStatus = this.copyFileOrDirectory(new File(existPGSQLFolder), new File(renamePGSQLFolder));
        if (!fileOrFolderCopyStatus) {
            return 0;
        }
        final String backupDir = tempDir + File.separator + "backup_data";
        if (new File(backupDir).exists()) {
            new File(backupDir).delete();
        }
        ConsoleOut.println("Backup Dir created " + new File(backupDir).mkdirs());
        final String backupFileName = "backupdata.ezip";
        try {
            final BackupDBParams backupProps = new BackupDBParams();
            backupProps.zipFileName = backupFileName;
            backupProps.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY;
            backupProps.backupFolder = new File(backupDir);
            backupProps.archivePassword = "Password123";
            final RelationalAPI relAPI = RelationalAPI.getInstance();
            final Properties backupProperties = new Properties();
            backupProperties.setProperty("backup.content.type", "binary");
            relAPI.getDBAdapter().getBackupHandler().isValid(backupProperties);
            relAPI.getDBAdapter().getBackupHandler().doBackup(backupProps);
        }
        catch (final Exception ex) {
            ConsoleOut.println("Exception :  " + ex);
            this.stopDB();
            if (new File(renamePGSQLFolder).exists()) {
                if (new File(existPGSQLFolder).exists()) {
                    ConsoleOut.println("Rename partial data folder  " + new File(existPGSQLFolder).renameTo(new File(tempDir + File.separator + "partial_data")));
                }
                final boolean result = new File(renamePGSQLFolder).renameTo(new File(existPGSQLFolder));
                ConsoleOut.println("Rename data folder  : " + result);
            }
            return 0;
        }
        return 1;
    }
}
