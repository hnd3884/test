package com.adventnet.iam.security;

import java.util.Arrays;
import java.util.List;

public class ResponseHeader
{
    static final List<String> XFRAME_OPTIONS_STD_VALUES;
    static final List<String> XFRAME_OPTIONS_ZSTD_VALUES;
    
    static {
        XFRAME_OPTIONS_STD_VALUES = Arrays.asList("sameorigin", "deny");
        XFRAME_OPTIONS_ZSTD_VALUES = Arrays.asList("trusted", "trusted-list", "trusted|trusted-list", "enableontrustedlist", "enableontrusted");
    }
    
    public enum NAME
    {
        CONTENT_TYPE("Content-Type"), 
        CONTENT_LENGTH("Content-Length"), 
        LOCATION("Location");
        
        private String name;
        
        private NAME(final String name) {
            this.name = null;
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
