package com.adventnet.authentication;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

public class LoginHandlerImpl implements LoginHandler
{
    @Override
    public boolean authenticate(final HttpServletRequest request, final Subject subject, final Credential credential) {
        return true;
    }
}
