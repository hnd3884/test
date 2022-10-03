package com.me.devicemanagement.framework.webclient.cache;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface SessionAPI
{
    void addToSession(final HttpServletRequest p0, final String p1, final Object p2);
    
    Object getSessionAttribute(final HttpServletRequest p0, final String p1);
    
    void removeSessionAttribute(final HttpServletRequest p0, final String p1);
    
    Object getServletContext(final HttpServletRequest p0, final String p1);
    
    boolean checkCSRFAttack(final HttpServletRequest p0, final HttpServletResponse p1) throws Exception;
}
