package com.me.ems.framework.common.factory;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface UnifiedAuthenticationService
{
    void init();
    
    boolean authentication(final HttpServletRequest p0, final HttpServletResponse p1) throws ServletException, IOException;
    
    boolean authorization(final HttpServletRequest p0, final HttpServletResponse p1) throws ServletException, IOException;
}
