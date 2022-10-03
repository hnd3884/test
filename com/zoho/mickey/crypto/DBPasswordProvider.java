package com.zoho.mickey.crypto;

import com.zoho.mickey.exception.PasswordException;

public interface DBPasswordProvider extends PasswordProvider
{
    String getPassword(final Object p0) throws PasswordException;
    
    String getEncryptedPassword(final String p0) throws PasswordException;
}
