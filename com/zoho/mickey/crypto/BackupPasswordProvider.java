package com.zoho.mickey.crypto;

import com.zoho.mickey.exception.PasswordException;
import com.adventnet.db.adapter.RestoreDBParams;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import com.adventnet.db.adapter.BackupDBParams;

public class BackupPasswordProvider implements PasswordProvider
{
    @Override
    public String getPassword(final Object context) throws PasswordException {
        if (context instanceof BackupDBParams) {
            final BackupDBParams backupDBParams = (BackupDBParams)context;
            if (backupDBParams.archivePassword == null) {
                String password = (backupDBParams.backupType == BackupRestoreConfigurations.BACKUP_TYPE.INCREMENTAL_BACKUP) ? backupDBParams.fullbackup_zipname : backupDBParams.zipFileName;
                password = password.substring(0, password.indexOf(46));
                password = new StringBuilder(password).reverse().toString();
                return password;
            }
            return backupDBParams.archivePassword;
        }
        else {
            if (context instanceof RestoreDBParams) {
                final RestoreDBParams restoreDBParams = (RestoreDBParams)context;
                return restoreDBParams.getArchivePassword();
            }
            if (context == null) {
                throw new PasswordException("Context should not be empty");
            }
            throw new PasswordException("Unknown content [" + context.getClass() + "] provided");
        }
    }
}
