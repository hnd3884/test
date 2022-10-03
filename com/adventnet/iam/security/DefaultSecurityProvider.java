package com.adventnet.iam.security;

import com.zoho.security.cache.CacheConfiguration;
import java.io.File;
import org.w3c.dom.Element;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class DefaultSecurityProvider implements SecurityProvider
{
    @Override
    public void init(final SecurityProvider defualtProvider) {
    }
    
    @Override
    public List<ParameterRule> getDynamicParameterRules(final HttpServletRequest request) {
        return null;
    }
    
    @Override
    public UploadFileRule getDynamicFileRule(final HttpServletRequest request, final ActionRule actionRule, final UploadedFileItem fileItem) {
        return null;
    }
    
    @Override
    public ParameterRule getDynamicInputStreamRule(final HttpServletRequest request, final ActionRule actionRule) {
        return null;
    }
    
    @Override
    public String getUserRoleForResource(final HttpServletRequest request, final HttpServletResponse response, final ActionRule rule) {
        return null;
    }
    
    @Override
    public ActionRule getActionRule(final SecurityFilterProperties securityFilterProperties, final Element el) {
        return new ActionRule(securityFilterProperties, el);
    }
    
    @Override
    public void authorize(final HttpServletRequest request, final HttpServletResponse response, final ActionRule rule) {
    }
    
    @Override
    public boolean isAuthenticationRequired(final HttpServletRequest request, final ActionRule rule) {
        return rule.isAuthenticationRequired();
    }
    
    @Override
    public long getMaximumUploadSize(final HttpServletRequest request, final ActionRule rule) {
        return -1L;
    }
    
    @Override
    public String getCSRFParameter(final HttpServletRequest request, final ActionRule rule) {
        if (SecurityUtil.isValid(((SecurityRequestWrapper)request).getParameterForValidation(SecurityUtil.getCSRFParamName(request)))) {
            return ((SecurityRequestWrapper)request).getParameterForValidation(SecurityUtil.getCSRFParamName(request));
        }
        return this.getCsrfHeaderParamValue(request);
    }
    
    private String getCsrfHeaderParamValue(final HttpServletRequest request) {
        final String csrfHeader = ((SecurityRequestWrapper)request).getHeader("X-ZCSRF-TOKEN");
        if (SecurityUtil.isValid(csrfHeader)) {
            final String[] csrfSubStrings = csrfHeader.split("=");
            if (csrfSubStrings != null && csrfSubStrings.length == 2) {
                final String csrfParamName = csrfSubStrings[0];
                final String csrfParamValue = csrfSubStrings[1];
                if (csrfParamName.equals(SecurityUtil.getCSRFParamName(request)) && SecurityUtil.isValid(csrfParamValue)) {
                    return csrfParamValue;
                }
            }
        }
        return null;
    }
    
    @Override
    public String getUploadedFileContent(final HttpServletRequest request, final String fileName, final File uploadedFile) throws Exception {
        return SecurityUtil.getFileAsString(uploadedFile);
    }
    
    @Override
    public String getCompleteContent(final HttpServletRequest request, final String paramName, final String paramValue) {
        return paramValue;
    }
    
    @Override
    public long getMaxMultiPartRequestSize(final HttpServletRequest request, final ActionRule rule) {
        return rule.getMaxRequestSize();
    }
    
    @Override
    public boolean isTrusted(final HttpServletRequest request, final String origin, final int type) {
        return false;
    }
    
    @Override
    public String getURLUniqueString(final HttpServletRequest request, final ActionRule rule) {
        return rule.getUrlUniquePath(SecurityUtil.getRequestPath(request));
    }
    
    @Override
    public String decrypt(final HttpServletRequest request, final String paramName, final String paramValue, final String decryptKeyLabel) {
        return null;
    }
    
    @Override
    public List<CacheConfiguration> getCacheConfigurations() {
        return null;
    }
    
    @Override
    public List<ThrottlesRule> getDynamicThrottlesRuleList(final HttpServletRequest request, final ActionRule rule) {
        return null;
    }
    
    @Override
    public String getDynamicThrottlesKey(final HttpServletRequest request, final ActionRule rule, final ThrottlesRule throttles, final String throttlesName) {
        return null;
    }
    
    @Override
    public List<ThrottleRule> getDynamicThrottleRuleList(final HttpServletRequest request, final ActionRule actionRule, final ThrottlesRule throttlesRule, final String throttlesName) {
        return null;
    }
    
    @Override
    public List<String> getTrustedDomainList(final String host) {
        return null;
    }
    
    @Override
    public void setCORSResponseHeaders(final HttpServletRequest request, final HttpServletResponse response, final ActionRule rule) {
    }
    
    @Override
    public void verifyRequest(final HttpServletRequest request, final ActionRule actionRule) {
    }
    
    @Override
    public boolean enableXFrameTrustedCheck(final HttpServletRequest request) {
        return true;
    }
}
