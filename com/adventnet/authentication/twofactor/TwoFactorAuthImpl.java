package com.adventnet.authentication.twofactor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public class TwoFactorAuthImpl implements TwoFactorAuth
{
    @Override
    public boolean handle(final Long userId, final ServletRequest request, final ServletResponse response) throws Exception {
        return true;
    }
    
    @Override
    public boolean validate(final Long userId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        return false;
    }
}
