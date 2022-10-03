package com.me.devicemanagement.framework.server.license;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Properties;

public interface LicenseAPI
{
    public static final String AMS_KEY = "AMS";
    public static final String LICENSE_DETAILS = "LicenseDetails";
    public static final String LICENSE_VERSION = "11";
    public static final String ADD_ONS = "AddOnModules";
    
    String getEMSLicenseVersion();
    
    String getCompanyName();
    
    long getEvaluationDays();
    
    @Deprecated
    String getProductType();
    
    String getProductExpiryDate();
    
    String getProductName();
    
    @Deprecated
    String getLicenseVersion();
    
    default String getMinimumSupportedBuild() {
        return null;
    }
    
    String getLicenseType();
    
    Properties getModuleProperties(final String p0);
    
    String getProductCategoryString();
    
    String getUserName();
    
    String getNoOfTechnicians();
    
    String getProductAMSExpiryDate();
    
    String getPrimaryContact();
    
    String getLicenseUserType();
    
    void setmoduleProMap();
    
    Map getmoduleProMap();
    
    Boolean isLanguagePackEnabled();
    
    boolean isUserLimitReached();
    
    String getStoreURL(final String p0);
    
    Boolean isFosEnabled();
    
    Boolean isFwsEnabled();
    
    boolean isLicenseExpiryMsgRequired();
    
    void setFreeEditionConfiguredStatus();
    
    boolean isClearedDetailsForFreeEdition(final HttpServletRequest p0);
    
    default boolean isClearedDetailsForFreeEdition(final Map request) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    boolean isFreeEditionForwardRequired();
    
    int getComplimentaryDevicesCount();
    
    int getUsageCount(final String p0);
    
    Boolean isEndpointServiceEnabled();
    
    default Integer getPurchasedStorage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    default Long getUsedStorage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    default boolean isFreeLicense() {
        return LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("F");
    }
    
    default boolean isEvaluationLicense() {
        return LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T");
    }
    
    default boolean isRegisteredLicense() {
        return LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("R");
    }
    
    default boolean isUEMEdition() {
        return LicenseProvider.getInstance().getProductCategoryString().equalsIgnoreCase("UEM");
    }
    
    default boolean isEnterpriseEdition() {
        return LicenseProvider.getInstance().getProductCategoryString().equalsIgnoreCase("Enterprise");
    }
    
    default boolean isProfessionalEdition() {
        return LicenseProvider.getInstance().getProductCategoryString().equalsIgnoreCase("Professional");
    }
}
