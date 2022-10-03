package com.me.devicemanagement.framework.server.license;

public interface PMPLicenseAPI
{
    public static final String STANDARD_EDITION = "Standard";
    public static final String PROFESSIONAL_EDITION = "Professional";
    public static final String ENTERPRISE_EDITION = "Enterprise";
    
    boolean isProfessionalLicenseEdition();
    
    boolean isEnterpriseLicenseEdition();
    
    boolean isVMPEnabled();
    
    String getLicenseEditionType();
}
