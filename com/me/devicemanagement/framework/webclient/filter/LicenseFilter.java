package com.me.devicemanagement.framework.webclient.filter;

import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.IOException;
import java.io.PrintWriter;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.Arrays;
import javax.servlet.FilterConfig;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.Filter;

public class LicenseFilter implements Filter
{
    private RequestDispatcher dispatcher;
    private static Logger logger;
    private List<String> exclusionList;
    private Pattern excludedRegexURLs;
    
    public LicenseFilter() {
        this.exclusionList = new ArrayList<String>();
        this.excludedRegexURLs = null;
    }
    
    public void init(final FilterConfig fc) throws ServletException {
        this.exclusionList = new ArrayList<String>();
        final String excludedURLs = fc.getInitParameter("excludedURLs");
        if (excludedURLs != null) {
            this.exclusionList = Arrays.asList(excludedURLs.split(","));
        }
        final String regex = fc.getInitParameter("excludedRegexURLs");
        if (regex != null) {
            this.excludedRegexURLs = Pattern.compile(regex);
        }
    }
    
    public void doFilter(final ServletRequest servletrequest, final ServletResponse servletresponse, final FilterChain fc) throws IOException, ServletException {
        try {
            if (this.isLicenseExpired()) {
                final HttpServletRequest request = (HttpServletRequest)servletrequest;
                if (!this.isURLWhitelisted(request)) {
                    final HttpServletResponse servletResponse = (HttpServletResponse)servletresponse;
                    LicenseFilter.logger.log(Level.FINE, "LicenseFilter : This url {0} is blocked ", SecurityUtil.getNormalizedRequestURI(request));
                    final APIException apiException = new APIException("LIC001", "ems.license.license_expired", new String[0]);
                    servletResponse.setStatus(423);
                    servletResponse.setHeader("Content-Type", "application/json");
                    final PrintWriter printWriter = servletResponse.getWriter();
                    printWriter.write(apiException.toJSONObject().toString());
                    printWriter.close();
                }
            }
            fc.doFilter(servletrequest, servletresponse);
        }
        catch (final Exception ex) {
            fc.doFilter(servletrequest, servletresponse);
            LicenseFilter.logger.log(Level.SEVERE, "Exception: {0}", ex);
        }
    }
    
    private boolean isLicenseExpired() {
        Boolean licenseExpired = (Boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("FREE_LICENSE_NOT_CONFIGURED", 2);
        if (licenseExpired == null) {
            LicenseProvider.getInstance().setFreeEditionConfiguredStatus();
        }
        licenseExpired = (Boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("FREE_LICENSE_NOT_CONFIGURED", 2);
        return licenseExpired != null && licenseExpired;
    }
    
    private String getBaseURL(final HttpServletRequest request) {
        final String uri = SecurityUtil.getNormalizedRequestURI(request);
        final String queryString = request.getQueryString();
        String url = (queryString == null) ? uri : (uri + "?" + queryString);
        if (url.indexOf("&") != -1) {
            url = url.substring(0, url.indexOf("&"));
        }
        return url;
    }
    
    private boolean isURLWhitelisted(final HttpServletRequest request) {
        final String uri = SecurityUtil.getNormalizedRequestURI(request);
        final String url = this.getBaseURL(request);
        return this.exclusionList.contains(url) || this.exclusionList.contains(uri) || this.isExcludedAsRegexURL(url);
    }
    
    private boolean isExcludedAsRegexURL(final String currURL) {
        return this.excludedRegexURLs != null && this.excludedRegexURLs.matcher(currURL).find();
    }
    
    public void destroy() {
        this.exclusionList = null;
    }
    
    static {
        LicenseFilter.logger = Logger.getLogger(LicenseFilter.class.getName());
    }
}
