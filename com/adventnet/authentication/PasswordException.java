package com.adventnet.authentication;

import javax.security.auth.login.LoginException;

public class PasswordException extends LoginException
{
    public PasswordException(final String msg) {
        super(msg);
    }
}
