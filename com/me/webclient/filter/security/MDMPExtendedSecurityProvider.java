package com.me.webclient.filter.security;

import com.adventnet.iam.security.ActionRule;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.filter.security.ExtendedSecurityProvider;

public class MDMPExtendedSecurityProvider extends ExtendedSecurityProvider
{
    public void authorize(final HttpServletRequest request, final HttpServletResponse response, final ActionRule actionRule) {
    }
}
