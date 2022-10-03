package com.zoho.security.appfirewall;

public class AppFirewallException extends RuntimeException
{
    private static final long serialVersionUID = -1922517033937353482L;
    public static final String INVALID_APPFIREWALL_CONFIGURATION = "INVALID_APPFIREWALL_CONFIGURATION";
    public static final String INVALID_REQUEST_COMPONENT = "INVALID_REQUEST_COMPONENT";
    public static final String APPFIREWALL_RESOURCE_WITH_INVALID_CONTENT = "APPFIREWALL_RESOURCE_WITH_INVALID_CONTENT";
    public static final String APPFIREWALL_RESOURCE_NOT_FOUND = "APPFIREWALL_RESOURCE_NOT_FOUND";
    public static final String BAD_REQUEST = "BAD REQUEST";
    
    public AppFirewallException() {
    }
    
    public AppFirewallException(final String errorMessage) {
        super(errorMessage);
    }
}
