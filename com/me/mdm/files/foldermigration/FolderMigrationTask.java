package com.me.mdm.files.foldermigration;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public abstract class FolderMigrationTask
{
    String sourcePath;
    String destPath;
    public Logger logger;
    
    FolderMigrationTask() {
        this.sourcePath = null;
        this.destPath = null;
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    FolderMigrationTask(final String sourcePath, final String destPath) {
        this.sourcePath = null;
        this.destPath = null;
        this.logger = Logger.getLogger("MDMLogger");
        this.destPath = destPath;
        this.sourcePath = sourcePath;
    }
    
    public void migrateFiles(final Long customerId) {
        if (this.copyFiles(customerId)) {
            this.updateDataBase(customerId);
            this.deleteFiles(customerId);
            this.clearCache(customerId);
        }
    }
    
    public boolean copyFiles(final Long customerId) {
        try {
            return ApiFactoryProvider.getFileAccessAPI().copyDirectory(this.sourcePath, this.destPath);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot copy files ", e);
            return false;
        }
    }
    
    public void updateDataBase(final Long customerId) {
    }
    
    public void clearCache(final Long customerId) {
    }
    
    public void deleteFiles(final Long customerId) {
        try {
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(this.sourcePath);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot delete source files ", e);
        }
    }
}
