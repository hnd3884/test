package com.adventnet.iam.security.antivirus.icap;

public interface IcapAvConfiguration
{
    default int getPort() {
        return 1344;
    }
    
    String getHost();
    
    String getService();
    
    default Integer getPreviewSize() {
        return null;
    }
    
    default boolean isPreviewModeSupported() {
        return false;
    }
    
    default Vendor getVendorName() {
        return Vendor.UNKNOWN_VENDOR;
    }
    
    default int getReadTimeout() {
        return 60000;
    }
    
    public enum Vendor
    {
        BITDEFENDER_SECURIY_FOR_STORAGE("BitDefender Security For Storage"), 
        ESET_FILE_SECURITY("ESET File Security"), 
        ESET_GATEWAY_SECURITY("ESET Gateway Security"), 
        KASPERSKY_SECURITY_FOR_WINDOWS_SERVER("Kaspersky Security for Windows Server"), 
        MCAFEE_VIRUSSCAN_ENTERPRISE_FOR_STORAGE("McAfee VirusScan Enterprise for Storage"), 
        MCAFEE_WEB_GATEWAY("McAfee Web GateWay"), 
        SYMANTEC_PROTECTION_ENGINE_FOR_CLOUD("Symantec Protection Engine For Cloud"), 
        CLAM_AV_WITH_SQUID("ClamAV with Squid"), 
        UNKNOWN_VENDOR("Unknow vendor");
        
        private String vendorName;
        
        private Vendor(final String vendorName) {
            this.vendorName = vendorName;
        }
        
        public String value() {
            return this.vendorName;
        }
    }
}
