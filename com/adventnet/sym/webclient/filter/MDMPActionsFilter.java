package com.adventnet.sym.webclient.filter;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.iam.security.SecurityUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class MDMPActionsFilter implements Filter
{
    public void init(final FilterConfig fc) throws ServletException {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest servletRequest = (HttpServletRequest)request;
        final HttpServletResponse servletResponse = (HttpServletResponse)response;
        try {
            final HttpSession session = servletRequest.getSession();
            session.setAttribute("cookieName", (Object)SecurityUtil.getCSRFCookieName((HttpServletRequest)request));
            session.setAttribute("csrfParamName", (Object)SecurityUtil.getCSRFParamName((HttpServletRequest)request));
            if (!LicenseProvider.getInstance().getProductExpiryDate().equals("never")) {
                request.setAttribute("licenseExpiryDays", (Object)MDMUtil.getSyMParameter("licenseExpiryDays"));
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMPActionsFilter.class.getName()).log(Level.SEVERE, "Exception: {0}", ex);
            new APIHTTPException("COM0004", new Object[0]).setErrorResponse(servletResponse);
            return;
        }
        chain.doFilter(request, response);
    }
    
    public void destroy() {
    }
}
