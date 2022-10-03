package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class WebAppsListResponse extends GenericJson
{
    @Key
    private List<WebApp> webApp;
    
    public List<WebApp> getWebApp() {
        return this.webApp;
    }
    
    public WebAppsListResponse setWebApp(final List<WebApp> webApp) {
        this.webApp = webApp;
        return this;
    }
    
    public WebAppsListResponse set(final String fieldName, final Object value) {
        return (WebAppsListResponse)super.set(fieldName, value);
    }
    
    public WebAppsListResponse clone() {
        return (WebAppsListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)WebApp.class);
    }
}
