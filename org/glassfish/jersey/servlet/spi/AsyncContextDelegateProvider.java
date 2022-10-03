package org.glassfish.jersey.servlet.spi;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface AsyncContextDelegateProvider
{
    AsyncContextDelegate createDelegate(final HttpServletRequest p0, final HttpServletResponse p1);
}
