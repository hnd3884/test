package com.adventnet.authentication.twofactor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public interface TwoFactorAuth
{
    boolean handle(final Long p0, final ServletRequest p1, final ServletResponse p2) throws Exception;
    
    boolean validate(final Long p0, final HttpServletRequest p1, final HttpServletResponse p2) throws Exception;
}
