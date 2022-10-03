package com.adventnet.db.adapter;

import java.util.Properties;
import java.util.List;

public interface BackupHandler
{
    BackupResult doBackup(final BackupDBParams p0) throws BackupRestoreException;
    
    BackupResult doFileBackup(final String p0, final String p1, final List<String> p2, final String p3, final Properties p4) throws BackupRestoreException;
    
    void doCleanup(final List<String> p0);
    
    boolean abortBackup() throws BackupRestoreException;
    
    boolean isValid(final Properties p0) throws BackupRestoreException;
    
    void enableIncrementalBackup() throws BackupRestoreException;
    
    void disableIncrementalBackup() throws BackupRestoreException;
    
    void cleanBackupConfigFiles() throws BackupRestoreException;
    
    boolean isIncrementalBackupValid() throws BackupRestoreException;
}
