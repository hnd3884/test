package com.me.devicemanagement.framework.server.license;

public interface MDMLicenseAPI
{
    public static final String MDM_STANDARD_EDITION = "Standard";
    public static final String MDM_PROFESSIONAL_EDITION = "Professional";
    public static final String MDM_ENTERPRISE_EDITION = "Enterprise";
    public static final String MDM_FREE_EDITION_LICENSE_COUNT = "25";
    
    boolean isMobileDeviceLicenseLimitExceed(final int p0);
    
    boolean isMobileDeviceLicenseReached(final int p0);
    
    String getNoOfMobileDevicesManaged();
    
    boolean isMDMLicenseLimitExceed();
    
    boolean isMDMLicenseLimitReached();
    
    int getActiveMobileDevices();
    
    boolean isNoOfMobileDevicesManagedIsEmptyString();
    
    boolean isProfessionalLicenseEdition();
    
    boolean isEnterpriseLicenseEdition();
    
    String getMDMLiceseEditionType();
    
    int getManagedDeviceCount();
    
    boolean isModernManagementCapable();
    
    String getPurchasedMobileCount();
}
