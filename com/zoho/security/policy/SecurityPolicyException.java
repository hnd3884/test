package com.zoho.security.policy;

public class SecurityPolicyException extends RuntimeException
{
    private static final long serialVersionUID = -6182585669802515319L;
    public static final String INVALID_JSON_FORMAT = "INVALID_JSON_FORMAT";
    public static final String INVALID_HANDLER = "INVALID_HANDLER";
    public static final String INVALID_HANDLER_NAME = "INVALID_HANDLER_NAME";
    public static final String INTERNAL_IP = "INTERNAL_IP";
    public static final String INVALID_IP_FORMAT = "INVALID_IP_FORMAT";
    public static final String INVALID_GEO_LOCATION_FORMAT = "INVALID_GEO_LOCATION_FORMAT";
    public static final String LOCATION_ALREADY_EXIST = "LOCATION_ALREADY_EXIST";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    private SecurityPolicyRule securityPolicyRule;
    private String uri;
    private String remoteAddr;
    
    public SecurityPolicyException(final String errorCode) {
        super(errorCode);
        this.securityPolicyRule = null;
        this.uri = null;
        this.remoteAddr = null;
    }
    
    public SecurityPolicyException(final String errorCode, final SecurityPolicyRule policyRule, final String requestURI, final String remoteAddr) {
        this(errorCode);
        this.securityPolicyRule = policyRule;
        this.uri = requestURI;
        this.remoteAddr = remoteAddr;
    }
    
    public SecurityPolicyRule getSecurityPolicyRule() {
        return this.securityPolicyRule;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public String getRemoteAddr() {
        return this.remoteAddr;
    }
}
