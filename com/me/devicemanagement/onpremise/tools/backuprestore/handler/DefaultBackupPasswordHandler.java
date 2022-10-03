package com.me.devicemanagement.onpremise.tools.backuprestore.handler;

import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordProvider;

public class DefaultBackupPasswordHandler implements DMBackupPasswordProvider
{
    @Override
    public String getPassword() {
        String password = BackupRestoreUtil.getInstance().getBuildNumber();
        if (password == null || password.length() == 0) {
            password = "Password123";
        }
        return password;
    }
    
    @Override
    public String getPasswordHint() {
        return "Default Password";
    }
}
