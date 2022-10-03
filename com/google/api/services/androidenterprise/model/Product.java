package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class Product extends GenericJson
{
    @Key
    private List<TrackInfo> appTracks;
    @Key
    private List<AppVersion> appVersion;
    @Key
    private String authorName;
    @Key
    private List<String> availableCountries;
    @Key
    private List<String> availableTracks;
    @Key
    private String category;
    @Key
    private String contentRating;
    @Key
    private String description;
    @Key
    private String detailsUrl;
    @Key
    private String distributionChannel;
    @Key
    private List<String> features;
    @Key
    private String iconUrl;
    @Key
    @JsonString
    private Long lastUpdatedTimestampMillis;
    @Key
    private Integer minAndroidSdkVersion;
    @Key
    private List<ProductPermission> permissions;
    @Key
    private String productId;
    @Key
    private String productPricing;
    @Key
    private String recentChanges;
    @Key
    private Boolean requiresContainerApp;
    @Key
    private List<String> screenshotUrls;
    @Key
    private ProductSigningCertificate signingCertificate;
    @Key
    private String smallIconUrl;
    @Key
    private String title;
    @Key
    private String workDetailsUrl;
    
    public List<TrackInfo> getAppTracks() {
        return this.appTracks;
    }
    
    public Product setAppTracks(final List<TrackInfo> appTracks) {
        this.appTracks = appTracks;
        return this;
    }
    
    public List<AppVersion> getAppVersion() {
        return this.appVersion;
    }
    
    public Product setAppVersion(final List<AppVersion> appVersion) {
        this.appVersion = appVersion;
        return this;
    }
    
    public String getAuthorName() {
        return this.authorName;
    }
    
    public Product setAuthorName(final String authorName) {
        this.authorName = authorName;
        return this;
    }
    
    public List<String> getAvailableCountries() {
        return this.availableCountries;
    }
    
    public Product setAvailableCountries(final List<String> availableCountries) {
        this.availableCountries = availableCountries;
        return this;
    }
    
    public List<String> getAvailableTracks() {
        return this.availableTracks;
    }
    
    public Product setAvailableTracks(final List<String> availableTracks) {
        this.availableTracks = availableTracks;
        return this;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public Product setCategory(final String category) {
        this.category = category;
        return this;
    }
    
    public String getContentRating() {
        return this.contentRating;
    }
    
    public Product setContentRating(final String contentRating) {
        this.contentRating = contentRating;
        return this;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Product setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public String getDetailsUrl() {
        return this.detailsUrl;
    }
    
    public Product setDetailsUrl(final String detailsUrl) {
        this.detailsUrl = detailsUrl;
        return this;
    }
    
    public String getDistributionChannel() {
        return this.distributionChannel;
    }
    
    public Product setDistributionChannel(final String distributionChannel) {
        this.distributionChannel = distributionChannel;
        return this;
    }
    
    public List<String> getFeatures() {
        return this.features;
    }
    
    public Product setFeatures(final List<String> features) {
        this.features = features;
        return this;
    }
    
    public String getIconUrl() {
        return this.iconUrl;
    }
    
    public Product setIconUrl(final String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }
    
    public Long getLastUpdatedTimestampMillis() {
        return this.lastUpdatedTimestampMillis;
    }
    
    public Product setLastUpdatedTimestampMillis(final Long lastUpdatedTimestampMillis) {
        this.lastUpdatedTimestampMillis = lastUpdatedTimestampMillis;
        return this;
    }
    
    public Integer getMinAndroidSdkVersion() {
        return this.minAndroidSdkVersion;
    }
    
    public Product setMinAndroidSdkVersion(final Integer minAndroidSdkVersion) {
        this.minAndroidSdkVersion = minAndroidSdkVersion;
        return this;
    }
    
    public List<ProductPermission> getPermissions() {
        return this.permissions;
    }
    
    public Product setPermissions(final List<ProductPermission> permissions) {
        this.permissions = permissions;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public Product setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public String getProductPricing() {
        return this.productPricing;
    }
    
    public Product setProductPricing(final String productPricing) {
        this.productPricing = productPricing;
        return this;
    }
    
    public String getRecentChanges() {
        return this.recentChanges;
    }
    
    public Product setRecentChanges(final String recentChanges) {
        this.recentChanges = recentChanges;
        return this;
    }
    
    public Boolean getRequiresContainerApp() {
        return this.requiresContainerApp;
    }
    
    public Product setRequiresContainerApp(final Boolean requiresContainerApp) {
        this.requiresContainerApp = requiresContainerApp;
        return this;
    }
    
    public List<String> getScreenshotUrls() {
        return this.screenshotUrls;
    }
    
    public Product setScreenshotUrls(final List<String> screenshotUrls) {
        this.screenshotUrls = screenshotUrls;
        return this;
    }
    
    public ProductSigningCertificate getSigningCertificate() {
        return this.signingCertificate;
    }
    
    public Product setSigningCertificate(final ProductSigningCertificate signingCertificate) {
        this.signingCertificate = signingCertificate;
        return this;
    }
    
    public String getSmallIconUrl() {
        return this.smallIconUrl;
    }
    
    public Product setSmallIconUrl(final String smallIconUrl) {
        this.smallIconUrl = smallIconUrl;
        return this;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public Product setTitle(final String title) {
        this.title = title;
        return this;
    }
    
    public String getWorkDetailsUrl() {
        return this.workDetailsUrl;
    }
    
    public Product setWorkDetailsUrl(final String workDetailsUrl) {
        this.workDetailsUrl = workDetailsUrl;
        return this;
    }
    
    public Product set(final String fieldName, final Object value) {
        return (Product)super.set(fieldName, value);
    }
    
    public Product clone() {
        return (Product)super.clone();
    }
    
    static {
        Data.nullOf((Class)AppVersion.class);
    }
}
