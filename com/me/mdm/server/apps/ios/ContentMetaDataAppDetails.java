package com.me.mdm.server.apps.ios;

public class ContentMetaDataAppDetails
{
    private String appName;
    private String adamId;
    private String releaseDate;
    private String sellerName;
    private String primaryGenreName;
    private String appIconImageURL;
    private String appStoreURL;
    private String appDescription;
    private Double appPrice;
    private Boolean isPaidApp;
    private String appVersion;
    private Long externalAppVersionID;
    private String bundleId;
    private String minimumOSVersion;
    private Integer supportDevice;
    private Boolean isNewVersion;
    private Boolean isError;
    
    public ContentMetaDataAppDetails() {
        this.appName = null;
        this.adamId = null;
        this.releaseDate = null;
        this.sellerName = null;
        this.primaryGenreName = null;
        this.appIconImageURL = null;
        this.appStoreURL = null;
        this.appDescription = null;
        this.appPrice = null;
        this.appVersion = null;
        this.externalAppVersionID = 0L;
        this.bundleId = null;
        this.minimumOSVersion = null;
        this.supportDevice = null;
        this.isError = false;
    }
    
    public String getAppName() {
        return this.appName;
    }
    
    public void setAppName(final String appName) {
        this.appName = appName;
    }
    
    public String getAdamId() {
        return this.adamId;
    }
    
    public void setAdamId(final String adamId) {
        this.adamId = adamId;
    }
    
    public String getReleaseDate() {
        return this.releaseDate;
    }
    
    public void setReleaseDate(final String releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public String getSellerName() {
        return this.sellerName;
    }
    
    public void setSellerName(final String sellerName) {
        this.sellerName = sellerName;
    }
    
    public String getPrimaryGenreName() {
        return this.primaryGenreName;
    }
    
    public void setPrimaryGenreName(final String primaryGenreName) {
        this.primaryGenreName = primaryGenreName;
    }
    
    public String getAppIconImageURL() {
        return this.appIconImageURL;
    }
    
    public void setAppIconImageURL(final String appIconImageURL) {
        this.appIconImageURL = appIconImageURL;
    }
    
    public String getAppStoreURL() {
        return this.appStoreURL;
    }
    
    public void setAppStoreURL(final String appStoreUrl) {
        this.appStoreURL = appStoreUrl;
    }
    
    public String getAppDescription() {
        return this.appDescription;
    }
    
    public void setAppDescription(final String appDescription) {
        this.appDescription = appDescription;
    }
    
    public Double getAppPrice() {
        return this.appPrice;
    }
    
    public void setAppPrice(final Double appPrice) {
        this.appPrice = appPrice;
    }
    
    public Boolean getIsPaidApp() {
        return this.isPaidApp;
    }
    
    public void setIsPaidApp(final Boolean paidApp) {
        this.isPaidApp = paidApp;
    }
    
    public String getAppVersion() {
        return this.appVersion;
    }
    
    public void setAppVersion(final String appVersion) {
        this.appVersion = appVersion;
    }
    
    public Long getExternalAppVersionID() {
        return this.externalAppVersionID;
    }
    
    public void setExternalAppVersionID(final Long externalAppVersionID) {
        this.externalAppVersionID = externalAppVersionID;
    }
    
    public String getBundleId() {
        return this.bundleId;
    }
    
    public void setBundleId(final String bundleId) {
        this.bundleId = bundleId;
    }
    
    public String getMinimumOSVersion() {
        return this.minimumOSVersion;
    }
    
    public void setMinimumOSVersion(final String minimumOSVersion) {
        this.minimumOSVersion = minimumOSVersion;
    }
    
    public Integer getSupportDevice() {
        return this.supportDevice;
    }
    
    public void setSupportDevice(final Integer supportDevice) {
        this.supportDevice = supportDevice;
    }
    
    public Boolean getIsNewVersion() {
        return this.isNewVersion;
    }
    
    public void setIsNewVersion(final Boolean newVersion) {
        this.isNewVersion = newVersion;
    }
    
    public Boolean getIsError() {
        return this.isError;
    }
    
    public void setIsError(final Boolean isError) {
        this.isError = isError;
    }
}
