package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class WebApp extends GenericJson
{
    @Key
    private String displayMode;
    @Key
    private List<WebAppIcon> icons;
    @Key
    private Boolean isPublished;
    @Key
    private String startUrl;
    @Key
    private String title;
    @Key
    @JsonString
    private Long versionCode;
    @Key
    private String webAppId;
    
    public String getDisplayMode() {
        return this.displayMode;
    }
    
    public WebApp setDisplayMode(final String displayMode) {
        this.displayMode = displayMode;
        return this;
    }
    
    public List<WebAppIcon> getIcons() {
        return this.icons;
    }
    
    public WebApp setIcons(final List<WebAppIcon> icons) {
        this.icons = icons;
        return this;
    }
    
    public Boolean getIsPublished() {
        return this.isPublished;
    }
    
    public WebApp setIsPublished(final Boolean isPublished) {
        this.isPublished = isPublished;
        return this;
    }
    
    public String getStartUrl() {
        return this.startUrl;
    }
    
    public WebApp setStartUrl(final String startUrl) {
        this.startUrl = startUrl;
        return this;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public WebApp setTitle(final String title) {
        this.title = title;
        return this;
    }
    
    public Long getVersionCode() {
        return this.versionCode;
    }
    
    public WebApp setVersionCode(final Long versionCode) {
        this.versionCode = versionCode;
        return this;
    }
    
    public String getWebAppId() {
        return this.webAppId;
    }
    
    public WebApp setWebAppId(final String webAppId) {
        this.webAppId = webAppId;
        return this;
    }
    
    public WebApp set(final String fieldName, final Object value) {
        return (WebApp)super.set(fieldName, value);
    }
    
    public WebApp clone() {
        return (WebApp)super.clone();
    }
}
