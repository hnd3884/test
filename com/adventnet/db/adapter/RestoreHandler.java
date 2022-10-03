package com.adventnet.db.adapter;

public interface RestoreHandler
{
    RestoreResult restoreBackup(final String p0, final String p1) throws BackupRestoreException;
}
