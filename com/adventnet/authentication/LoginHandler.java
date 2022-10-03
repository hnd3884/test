package com.adventnet.authentication;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

public interface LoginHandler
{
    boolean authenticate(final HttpServletRequest p0, final Subject p1, final Credential p2);
}
