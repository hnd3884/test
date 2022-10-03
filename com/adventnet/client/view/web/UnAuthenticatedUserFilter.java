package com.adventnet.client.view.web;

import com.adventnet.persistence.DataObject;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.Filter;

public class UnAuthenticatedUserFilter implements Filter, WebConstants, JavaScriptConstants
{
    private static Logger logger;
    
    public void init(final FilterConfig filterConfig) {
        UnAuthenticatedUserFilter.logger.log(Level.FINEST, "UnAuthenticatedUserFilter initialized");
        UnAuthenticatedUserFilter.logger.setLevel(Level.FINEST);
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        try {
            final HttpServletRequest request = (HttpServletRequest)req;
            if (WebClientUtil.isUserCredentialSet()) {
                UnAuthenticatedUserFilter.logger.log(Level.FINEST, "Already Authorized. Uri is {0} ", request.getRequestURI());
                chain.doFilter((ServletRequest)request, response);
                return;
            }
            final String redirect = this.needsAuthentication(request);
            if (redirect != null) {
                response.getWriter().println("<script>window.location.href='" + IAMEncoder.encodeJavaScript(redirect) + "';</script>");
                return;
            }
            chain.doFilter((ServletRequest)request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new ServletException((Throwable)e);
        }
    }
    
    public String needsAuthentication(final HttpServletRequest request) throws Exception {
        String uri = request.getRequestURI();
        final StringBuffer url = request.getRequestURL();
        final String pathInfo = request.getPathInfo();
        final String pathName = WebClientUtil.getRequestedPathName(request);
        final String pathNameExt = WebClientUtil.getRequestedPathWithExtension(request);
        String reqUri = null;
        uri = url.substring(url.indexOf(request.getContextPath()), url.length());
        boolean requiresAuthentication = false;
        if (pathNameExt.endsWith(".cc")) {
            final WebViewModel wvm = WebViewAPI.getConfigModel(pathName, false);
            if (wvm.getViewConfiguration().getFirstValue("ViewConfiguration", "ROLENAME") != null) {
                requiresAuthentication = true;
            }
        }
        else if (pathNameExt.endsWith(".ma")) {
            final DataObject dob = MenuVariablesGenerator.getCompleteMenuItemData(pathName);
            if (dob.getFirstValue("MenuItem", "ROLENAME") != null) {
                requiresAuthentication = true;
            }
        }
        else {
            if (!pathNameExt.endsWith(".ve")) {
                throw new RuntimeException("Unknown extension : " + reqUri);
            }
            final WebViewModel wvm = WebViewAPI.getConfigModel(pathName, false);
            if (wvm.getViewConfiguration().getFirstValue("ViewConfiguration", "ROLENAME") != null) {
                requiresAuthentication = true;
            }
        }
        if (requiresAuthentication) {
            if (uri.endsWith(".cc")) {
                reqUri = uri.replaceAll(".cc", ".acc");
            }
            else if (uri.endsWith(".ma")) {
                reqUri = uri.replaceAll(".ma", ".ama");
            }
            else if (uri.endsWith(".ve")) {
                reqUri = uri.replaceAll(".ve", ".ave");
            }
        }
        if (reqUri == null) {
            return null;
        }
        final String queryString = request.getQueryString();
        if (queryString != null) {
            reqUri = reqUri + "?" + queryString;
        }
        return reqUri;
    }
    
    public void destroy() {
    }
    
    static {
        UnAuthenticatedUserFilter.logger = Logger.getLogger(StateFilter.class.getName());
    }
}
