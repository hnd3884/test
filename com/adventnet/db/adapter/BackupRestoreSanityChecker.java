package com.adventnet.db.adapter;

public interface BackupRestoreSanityChecker
{
    boolean checkBackup(final BackupResult p0);
    
    boolean checkRestore(final RestoreResult p0);
}
