package com.me.devicemanagement.framework.webclient.filter.security;

import java.io.IOException;
import com.adventnet.iam.security.ActionRule;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.iam.security.SecurityRequestWrapper;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class SecurityExtendedFilter implements Filter
{
    private static Logger logger;
    private String authorisationEnabled;
    private String excludeURLs;
    private boolean useSecurityExtendedFilter;
    
    public SecurityExtendedFilter() {
        this.authorisationEnabled = "true";
        this.excludeURLs = null;
        this.useSecurityExtendedFilter = false;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.useSecurityExtendedFilter = DMSecurityUtil.checkAndEnableSecurityFilter(filterConfig);
        final String authorisationParam = filterConfig.getInitParameter("authorisation");
        final String excludeParam = filterConfig.getInitParameter("exclude");
        if (authorisationParam != null) {
            this.authorisationEnabled = authorisationParam;
        }
        if (excludeParam != null) {
            this.excludeURLs = excludeParam;
        }
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)servletRequest;
        if (this.useSecurityExtendedFilter) {
            if (this.excludeURLs != null) {
                final String lookupURI = SecurityUtil.getNormalizedRequestURI(request);
                if (lookupURI != null && com.adventnet.iam.security.SecurityUtil.isMatches(this.excludeURLs, lookupURI)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }
            request.setAttribute("cookieName", (Object)com.adventnet.iam.security.SecurityUtil.getCSRFCookieName(request));
            request.setAttribute("csrfParamName", (Object)com.adventnet.iam.security.SecurityUtil.getCSRFParamName(request));
            request.setAttribute("isCsrfEnabled", (Object)true);
            final SecurityRequestWrapper secureRequest = (SecurityRequestWrapper)servletRequest.getAttribute("SecurityRequest");
            if (secureRequest != null) {
                final ActionRule actionrule = secureRequest.getURLActionRule();
                if (this.authorisationEnabled.equalsIgnoreCase("true")) {
                    boolean authorisedUser = false;
                    final String[] configuredRoles = actionrule.getRoles();
                    if (configuredRoles != null) {
                        try {
                            for (final String role : configuredRoles) {
                                if (authorisedUser) {
                                    break;
                                }
                                authorisedUser = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains(role.trim());
                            }
                        }
                        catch (final Exception e) {
                            SecurityExtendedFilter.logger.log(Level.SEVERE, "Exception while getting roles : {0}", e);
                        }
                    }
                    else {
                        authorisedUser = true;
                    }
                    if (!authorisedUser) {
                        throw new IAMSecurityException("UNAUTHORISED");
                    }
                }
                final String configuredEdition = actionrule.getCustomAttribute("edition");
                if (configuredEdition != null) {
                    final String plan = LicenseProvider.getInstance().getProductType();
                    if (!configuredEdition.contains(plan)) {
                        throw new IAMSecurityException("DC_EDITION_INVALID");
                    }
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    public void destroy() {
    }
    
    static {
        SecurityExtendedFilter.logger = Logger.getLogger(SecurityExtendedFilter.class.getName());
    }
}
