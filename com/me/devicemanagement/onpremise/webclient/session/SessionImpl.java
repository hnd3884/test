package com.me.devicemanagement.onpremise.webclient.session;

import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.cache.SessionAPI;

public class SessionImpl implements SessionAPI
{
    private static final Logger LOGGER;
    
    public Object getSessionAttribute(final HttpServletRequest request, final String key) {
        return request.getSession().getAttribute(key);
    }
    
    public void addToSession(final HttpServletRequest request, final String key, final Object value) {
        request.getSession().setAttribute(key, value);
    }
    
    public void removeSessionAttribute(final HttpServletRequest request, final String key) {
        request.getSession().removeAttribute(key);
    }
    
    public Object getServletContext(final HttpServletRequest request, final String key) {
        final ServletContext context = request.getSession().getServletContext();
        return context.getAttribute(key);
    }
    
    public boolean checkCSRFAttack(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String csrfPreventionSaltFromSession = (String)request.getSession().getAttribute("rolePageCsrfPreventionSalt");
        final String csrfPreventionSaltFromRequest = request.getParameter("rolePageCsrfPreventionSalt");
        if (csrfPreventionSaltFromSession == null || csrfPreventionSaltFromRequest == null || !csrfPreventionSaltFromSession.equals(csrfPreventionSaltFromRequest)) {
            SessionImpl.LOGGER.log(Level.INFO, "CSRF attack. Going to reject the request.");
            response.sendError(403, "Request Refused");
            return true;
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger("UserManagementLogger");
    }
}
