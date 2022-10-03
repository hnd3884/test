package com.adventnet.iam.security;

import com.zoho.security.cache.CacheConfiguration;
import java.io.File;
import org.w3c.dom.Element;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface SecurityProvider
{
    public static final String ZOHO_CSRFTOKEN_HEADER = "X-ZCSRF-TOKEN";
    
    void init(final SecurityProvider p0);
    
    List<ParameterRule> getDynamicParameterRules(final HttpServletRequest p0);
    
    UploadFileRule getDynamicFileRule(final HttpServletRequest p0, final ActionRule p1, final UploadedFileItem p2);
    
    ParameterRule getDynamicInputStreamRule(final HttpServletRequest p0, final ActionRule p1);
    
    String getUserRoleForResource(final HttpServletRequest p0, final HttpServletResponse p1, final ActionRule p2);
    
    ActionRule getActionRule(final SecurityFilterProperties p0, final Element p1);
    
    void authorize(final HttpServletRequest p0, final HttpServletResponse p1, final ActionRule p2);
    
    boolean isAuthenticationRequired(final HttpServletRequest p0, final ActionRule p1);
    
    long getMaximumUploadSize(final HttpServletRequest p0, final ActionRule p1);
    
    String getCSRFParameter(final HttpServletRequest p0, final ActionRule p1);
    
    String getUploadedFileContent(final HttpServletRequest p0, final String p1, final File p2) throws Exception;
    
    String getCompleteContent(final HttpServletRequest p0, final String p1, final String p2);
    
    long getMaxMultiPartRequestSize(final HttpServletRequest p0, final ActionRule p1);
    
    @Deprecated
    String getURLUniqueString(final HttpServletRequest p0, final ActionRule p1);
    
    List<CacheConfiguration> getCacheConfigurations();
    
    List<ThrottlesRule> getDynamicThrottlesRuleList(final HttpServletRequest p0, final ActionRule p1);
    
    default void setAccessLogCustomFieldsForThrottledRequest(final SecurityRequestWrapper securityRequestWrapper) {
    }
    
    String getDynamicThrottlesKey(final HttpServletRequest p0, final ActionRule p1, final ThrottlesRule p2, final String p3);
    
    default String getDynamicThrottlesKey(final HttpServletRequest request, final ActionRule rule, final ThrottlesRule throttles, final String keyName, final ThrottlesRule.KeyType keyType) {
        return null;
    }
    
    List<ThrottleRule> getDynamicThrottleRuleList(final HttpServletRequest p0, final ActionRule p1, final ThrottlesRule p2, final String p3);
    
    boolean isTrusted(final HttpServletRequest p0, final String p1, final int p2);
    
    String decrypt(final HttpServletRequest p0, final String p1, final String p2, final String p3);
    
    List<String> getTrustedDomainList(final String p0);
    
    void setCORSResponseHeaders(final HttpServletRequest p0, final HttpServletResponse p1, final ActionRule p2);
    
    void verifyRequest(final HttpServletRequest p0, final ActionRule p1);
    
    boolean enableXFrameTrustedCheck(final HttpServletRequest p0);
}
