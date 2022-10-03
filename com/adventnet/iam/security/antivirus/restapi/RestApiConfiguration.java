package com.adventnet.iam.security.antivirus.restapi;

import java.net.Proxy;

public interface RestApiConfiguration
{
    public static final String METADEFENDER_URL = "https://api.metadefender.com/v4";
    public static final String VIRUSTOTAL_URL = "https://www.virustotal.com/vtapi/v2";
    
    String getAPIKey();
    
    Vendor getVendorName();
    
    default String getURL() {
        if (this.getVendorName() != null) {
            switch (this.getVendorName()) {
                case METADEFENDER: {
                    return "https://api.metadefender.com/v4";
                }
                case VIRUSTOTAL: {
                    return "https://www.virustotal.com/vtapi/v2";
                }
            }
        }
        throw new IllegalStateException(String.format("Invalid Rest API vendor name: %s", this.getVendorName()));
    }
    
    default Proxy getProxy() {
        return null;
    }
    
    public enum Vendor
    {
        VIRUSTOTAL("VirusTotal"), 
        METADEFENDER("MetaDefender");
        
        private String vendorName;
        
        private Vendor(final String vendorName) {
            this.vendorName = vendorName;
        }
        
        public String value() {
            return this.vendorName;
        }
    }
}
