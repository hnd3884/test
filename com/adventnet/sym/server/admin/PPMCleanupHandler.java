package com.adventnet.sym.server.admin;

import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.fileaccess.FileOperationsUtil;
import java.util.Arrays;
import java.io.File;
import com.adventnet.sym.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PPMCleanupHandler
{
    private Logger logger;
    private static final String PPM_BACKUP_FOLDER = "PatchBackup";
    private static final int NO_OF_BACKUPS_TO_BE_MAINTAINED = 4;
    private static PPMCleanupHandler ppmcHandler;
    
    private PPMCleanupHandler() {
        (this.logger = Logger.getLogger(PPMCleanupHandler.class.getName())).log(Level.INFO, "Creating instance of PPMCleanupHandler()");
    }
    
    public static synchronized PPMCleanupHandler getInstance() {
        if (PPMCleanupHandler.ppmcHandler == null) {
            PPMCleanupHandler.ppmcHandler = new PPMCleanupHandler();
        }
        return PPMCleanupHandler.ppmcHandler;
    }
    
    public void cleanupOldPPMBackups() {
        try {
            final String backupFolderStr = SyMUtil.getInstallationDir() + File.separator + "PatchBackup";
            this.logger.log(Level.INFO, "PPM Backup Folder: {0}", backupFolderStr);
            final File backupFolder = new File(backupFolderStr);
            if (!backupFolder.exists() || !backupFolder.isDirectory()) {
                this.logger.log(Level.INFO, "PPM Backup Folder: {0} is not a directory or the directory does not exist.", backupFolderStr);
                return;
            }
            final File[] backupList = backupFolder.listFiles();
            this.logger.log(Level.INFO, "Backup Folders: {0}", Arrays.toString(backupList));
            final List buildNos = this.getBuildNumbersToBeCleanedup(backupList);
            this.logger.log(Level.INFO, "Build numbers for backup cleanedup: {0}", buildNos);
            if (buildNos == null || buildNos.isEmpty()) {
                this.logger.log(Level.INFO, "Build numbers to cleanedup old backups is empty. Going to return.");
                return;
            }
            for (final File bkFile : backupList) {
                final String bkFileName = bkFile.getName();
                if (bkFileName != null) {
                    if (bkFileName.length() >= 5) {
                        final String bNumberStr = bkFileName.trim().substring(0, 5);
                        if (buildNos.contains(bNumberStr)) {
                            this.logger.log(Level.INFO, "Going to delete the folder: {0}", bkFile);
                            FileOperationsUtil.getInstance().deleteFileOrFolder(bkFile);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while deleting the old ppm backup folders.", ex);
        }
    }
    
    private List getBuildNumbersToBeCleanedup(final File[] backupList) {
        final List buildNosStrList = new ArrayList();
        try {
            final List buildNosIntList = new ArrayList();
            for (final File bkFile : backupList) {
                final String bkFileName = bkFile.getName();
                if (bkFileName != null && bkFileName.length() >= 5 && bkFile.isDirectory()) {
                    if (!bkFileName.equalsIgnoreCase("Cleanup")) {
                        final String bNumberStr = bkFileName.trim().substring(0, 5);
                        try {
                            final Integer bNumber = new Integer(bNumberStr);
                            if (!buildNosIntList.contains(bNumber)) {
                                buildNosIntList.add(bNumber);
                            }
                        }
                        catch (final Exception ee) {
                            this.logger.log(Level.WARNING, ee, () -> "Caught exception while parsing the build number string: " + bNumberStr);
                        }
                    }
                }
            }
            final Object[] buildNosArray = buildNosIntList.toArray();
            Arrays.sort(buildNosArray);
            final int existCnt = buildNosIntList.size();
            final int toBeMaintained = 4;
            final int toBeCleanedup = existCnt - toBeMaintained;
            if (toBeCleanedup > 0) {
                for (int k = 0; k < toBeCleanedup; ++k) {
                    buildNosStrList.add(String.valueOf(buildNosArray[k]));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while finding the build numbers for cleaning up old ppm backups.", ex);
        }
        return buildNosStrList;
    }
    
    static {
        PPMCleanupHandler.ppmcHandler = null;
    }
}
