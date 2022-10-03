package com.me.devicemanagement.onpremise.tools.backuprestore.handler;

import com.zoho.mickey.exception.PasswordException;
import com.zoho.mickey.crypto.PasswordProvider;

public class MickeyBackupPasswordProvider implements PasswordProvider
{
    public String getPassword(final Object context) throws PasswordException {
        return "Password123";
    }
}
