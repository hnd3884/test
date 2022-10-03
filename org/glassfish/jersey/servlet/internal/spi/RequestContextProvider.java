package org.glassfish.jersey.servlet.internal.spi;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface RequestContextProvider
{
    HttpServletRequest getHttpServletRequest();
    
    HttpServletResponse getHttpServletResponse();
}
