package com.adventnet.authentication;

import javax.security.auth.login.LoginException;

public class PAMException extends LoginException
{
    String msg;
    Exception exception;
    
    public PAMException(final String message) {
        super(message);
        this.msg = "";
        this.exception = null;
        this.msg = message;
    }
    
    public PAMException(final String message, final Exception e) {
        this.msg = "";
        this.exception = null;
        this.initCause(new Throwable(message, e));
        this.msg = message;
        this.exception = e;
    }
}
