package com.me.devicemanagement.onpremise.webclient.filter;

import java.util.Arrays;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;
import java.io.PrintWriter;
import javax.ws.rs.core.Response;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.ems.onpremise.uac.core.TFAUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.framework.common.api.utils.APIException;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.List;
import javax.servlet.Filter;

public class UIRestrictionFilter implements Filter
{
    private List restrictedHostNames;
    private List exclusionList;
    private Pattern excludedRegexURLs;
    private static Logger logger;
    
    public UIRestrictionFilter() {
        this.restrictedHostNames = new ArrayList();
        this.exclusionList = new ArrayList();
        this.excludedRegexURLs = null;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final UserManagementUtil userManagementUtil = new UserManagementUtil();
        final HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        final String currURI = SecurityUtil.getNormalizedRequestURI(httpServletRequest);
        final String authToken = httpServletRequest.getHeader("Authorization");
        if (authToken == null || authToken.trim().isEmpty()) {
            if (userManagementUtil.isPasswordChangeRequired()) {
                final boolean isExcluded = this.exclusionList.contains(currURI) || this.isExcludedAsRegexURL(currURI);
                if (!isExcluded) {
                    UIRestrictionFilter.logger.log(Level.FINE, "UIRestrictionFilter : default admin password change required. So blocking url {0}", currURI);
                    final HttpServletResponse httpServletResponse = (HttpServletResponse)response;
                    final APIException apiException = new APIException("USER006", "ems.admin.default_pwd", new String[0]);
                    httpServletResponse.setStatus(423);
                    httpServletResponse.setHeader("Content-Type", "application/json");
                    final PrintWriter printWriter = httpServletResponse.getWriter();
                    printWriter.write(apiException.toJSONObject().toString());
                    printWriter.close();
                }
            }
            final CacheAccessAPI cacheAccessAPI = ApiFactoryProvider.getCacheAccessAPI();
            Boolean isTFAToBeEnabled = (Boolean)cacheAccessAPI.getCache("isTFAToBeEnabled");
            if (isTFAToBeEnabled == null) {
                isTFAToBeEnabled = TFAUtil.isTFAToBeEnabled();
                cacheAccessAPI.putCache("isTFAToBeEnabled", (Object)isTFAToBeEnabled);
            }
            if (isTFAToBeEnabled) {
                final boolean isExcluded2 = this.exclusionList.contains(currURI) || this.isExcludedAsRegexURL(currURI);
                if (!isExcluded2) {
                    UIRestrictionFilter.logger.log(Level.FINE, "UIRestrictionFilter : TFA need to enabled. So blocking url {0}", currURI);
                    final Boolean isAdminUser = SYMClientUtil.isUserInAdminRole(httpServletRequest);
                    String errorCode = "TFA002";
                    if (isAdminUser) {
                        errorCode = "TFA001";
                    }
                    final HttpServletResponse httpResponse = (HttpServletResponse)response;
                    httpResponse.setStatus(412);
                    httpResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
                    final PrintWriter pout = httpResponse.getWriter();
                    pout.print(new APIException(Response.Status.PRECONDITION_FAILED, errorCode, "ems.tfa.tfa_concern").toJSONObject().toString());
                    pout.close();
                    return;
                }
            }
        }
        SYMClientUtil.returnRequestFromRestrictedHostName(this.restrictedHostNames, request, response);
        chain.doFilter(request, response);
    }
    
    public void init(final FilterConfig fc) throws ServletException {
        final String excludedURLs = fc.getInitParameter("excludedURLs");
        if (excludedURLs != null) {
            this.exclusionList = Arrays.asList(excludedURLs.split(","));
        }
        this.restrictedHostNames = SYMClientUtil.getRestrictedHostNames();
        final String regex = fc.getInitParameter("excludedRegexURLs");
        if (regex != null) {
            this.excludedRegexURLs = Pattern.compile(regex);
        }
    }
    
    private boolean isExcludedAsRegexURL(final String currURL) {
        return this.excludedRegexURLs != null && this.excludedRegexURLs.matcher(currURL).find();
    }
    
    public void destroy() {
        this.restrictedHostNames = null;
        this.exclusionList = null;
        this.excludedRegexURLs = null;
    }
    
    static {
        UIRestrictionFilter.logger = Logger.getLogger(UIRestrictionFilter.class.getName());
    }
}
