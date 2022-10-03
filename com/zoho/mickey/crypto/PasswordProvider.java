package com.zoho.mickey.crypto;

import com.zoho.mickey.exception.PasswordException;

public interface PasswordProvider
{
    String getPassword(final Object p0) throws PasswordException;
}
