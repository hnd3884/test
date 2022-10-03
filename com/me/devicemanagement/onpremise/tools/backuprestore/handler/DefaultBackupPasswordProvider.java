package com.me.devicemanagement.onpremise.tools.backuprestore.handler;

import com.adventnet.persistence.PersistenceUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordProvider;

public class DefaultBackupPasswordProvider implements DMBackupPasswordProvider
{
    private String defaultPassword;
    
    public DefaultBackupPasswordProvider() {
        this.defaultPassword = null;
    }
    
    @Override
    public String getPassword() {
        if (this.defaultPassword == null) {
            this.defaultPassword = PersistenceUtil.generateRandomPassword();
        }
        return this.defaultPassword;
    }
    
    @Override
    public String getPasswordHint() {
        return null;
    }
}
