package com.me.devicemanagement.framework.server.license;

import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import java.util.Map;

public class License
{
    private static License oldLicenseObject;
    private static License newLicenseObject;
    private String emsLicenseVersion;
    private String productType;
    private String productName;
    private String productCode;
    private String licenseVersion;
    private String licenseType;
    private String productCategoryString;
    private String userName;
    private String companyName;
    private Map modulePropertiesMap;
    
    private License() {
        this.emsLicenseVersion = null;
        this.productType = null;
        this.productName = null;
        this.productCode = null;
        this.licenseVersion = null;
        this.licenseType = null;
        this.productCategoryString = null;
        this.userName = null;
        this.companyName = null;
        this.modulePropertiesMap = null;
        this.productType = LicenseProvider.getInstance().getProductType();
        this.productName = LicenseProvider.getInstance().getProductName();
        this.productCode = String.valueOf(EMSProductUtil.getEMSProductCode().get(0));
        this.licenseVersion = LicenseProvider.getInstance().getLicenseVersion();
        this.licenseType = LicenseProvider.getInstance().getLicenseType();
        this.productCategoryString = LicenseProvider.getInstance().getProductCategoryString();
        this.userName = LicenseProvider.getInstance().getUserName();
        this.companyName = LicenseProvider.getInstance().getCompanyName();
        this.modulePropertiesMap = LicenseProvider.getInstance().getmoduleProMap();
        if (!LicenseProvider.getInstance().getEMSLicenseVersion().equals("11") && EMSProductUtil.isEMSFlowSupportedForCurrentProduct()) {
            final Map map = EMSConstants.DEFAULT_PRODUCT_TO_COMPONENTS_MAP.get(String.valueOf(EMSProductUtil.getEMSProductCode().get(0)));
            if (LicenseProvider.getInstance().getLicenseType().equals("R")) {
                this.modulePropertiesMap.putAll(map.get("registered"));
            }
            else if (LicenseProvider.getInstance().getLicenseType().equals("T")) {
                this.modulePropertiesMap.putAll(map.get("free"));
            }
            else if (LicenseProvider.getInstance().getLicenseType().equals("F")) {
                this.modulePropertiesMap.putAll(map.get("trial"));
            }
        }
        LicenseProvider.getInstance().setmoduleProMap();
        this.emsLicenseVersion = LicenseProvider.getInstance().getEMSLicenseVersion();
    }
    
    public static void storeOldLicenseObject() {
        License.oldLicenseObject = new License();
    }
    
    public static void storeOldLicenseObject(License oldLicenseObject) {
        oldLicenseObject = oldLicenseObject;
    }
    
    public static License getOldLicenseObject() {
        return License.oldLicenseObject;
    }
    
    public static License getNewLicenseObject() {
        return new License();
    }
    
    public static License getNewLicenseStaticObject() {
        return License.newLicenseObject;
    }
    
    public String getEmsLicenseversion() {
        return this.emsLicenseVersion;
    }
    
    public String getProductType() {
        return this.productType;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public String getProductCode() {
        return this.productCode;
    }
    
    public String getLicenseVersion() {
        return this.licenseVersion;
    }
    
    public String getLicenseType() {
        return this.licenseType;
    }
    
    public String getProductCategoryString() {
        return this.productCategoryString;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getCompanyName() {
        return this.companyName;
    }
    
    public Map getModulePropertiesMap() {
        return this.modulePropertiesMap;
    }
    
    public static void setOldLicenseObject(final License oldLicense) {
        License.oldLicenseObject = oldLicense;
    }
    
    public static void setNewLicenseObject(final License licenseObject) {
        License.newLicenseObject = licenseObject;
    }
    
    public void setEmsLicenseversion(final String licenseVersion) {
        this.emsLicenseVersion = licenseVersion;
    }
    
    public void setProductType(final String type) {
        this.productType = type;
    }
    
    public void setProductName(final String name) {
        this.productName = name;
    }
    
    public void setProductCode(final String code) {
        this.productCode = code;
    }
    
    public void setLicenseVersion(final String version) {
        this.licenseVersion = version;
    }
    
    public void setLicenseType(final String type) {
        this.licenseType = type;
    }
    
    public void setProductCategoryString(final String categoryString) {
        this.productCategoryString = categoryString;
    }
    
    public void setUserName(final String name) {
        this.userName = name;
    }
    
    public void setCompanyName(final String name) {
        this.companyName = name;
    }
    
    public Map setModulePropertiesMap() {
        return this.modulePropertiesMap;
    }
    
    static {
        License.oldLicenseObject = null;
        License.newLicenseObject = null;
    }
}
