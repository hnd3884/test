package com.google.chromedevicemanagement.v1;

import com.google.api.client.util.GenericData;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.util.Key;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;

public abstract class ChromeDeviceManagementRequest<T> extends AbstractGoogleJsonClientRequest<T>
{
    @Key("$.xgafv")
    private String $Xgafv;
    @Key("access_token")
    private String accessToken;
    @Key
    private String alt;
    @Key
    private String callback;
    @Key
    private String fields;
    @Key
    private String key;
    @Key("oauth_token")
    private String oauthToken;
    @Key
    private Boolean prettyPrint;
    @Key
    private String quotaUser;
    @Key
    private String uploadType;
    @Key("upload_protocol")
    private String uploadProtocol;
    
    public ChromeDeviceManagementRequest(final ChromeDeviceManagement chromeDeviceManagement, final String s, final String s2, final Object o, final Class<T> clazz) {
        super((AbstractGoogleJsonClient)chromeDeviceManagement, s, s2, o, (Class)clazz);
    }
    
    public String get$Xgafv() {
        return this.$Xgafv;
    }
    
    public ChromeDeviceManagementRequest<T> set$Xgafv(final String $Xgafv) {
        this.$Xgafv = $Xgafv;
        return this;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public ChromeDeviceManagementRequest<T> setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
    
    public String getAlt() {
        return this.alt;
    }
    
    public ChromeDeviceManagementRequest<T> setAlt(final String alt) {
        this.alt = alt;
        return this;
    }
    
    public String getCallback() {
        return this.callback;
    }
    
    public ChromeDeviceManagementRequest<T> setCallback(final String callback) {
        this.callback = callback;
        return this;
    }
    
    public String getFields() {
        return this.fields;
    }
    
    public ChromeDeviceManagementRequest<T> setFields(final String fields) {
        this.fields = fields;
        return this;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public ChromeDeviceManagementRequest<T> setKey(final String key) {
        this.key = key;
        return this;
    }
    
    public String getOauthToken() {
        return this.oauthToken;
    }
    
    public ChromeDeviceManagementRequest<T> setOauthToken(final String oauthToken) {
        this.oauthToken = oauthToken;
        return this;
    }
    
    public Boolean getPrettyPrint() {
        return this.prettyPrint;
    }
    
    public ChromeDeviceManagementRequest<T> setPrettyPrint(final Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }
    
    public String getQuotaUser() {
        return this.quotaUser;
    }
    
    public ChromeDeviceManagementRequest<T> setQuotaUser(final String quotaUser) {
        this.quotaUser = quotaUser;
        return this;
    }
    
    public String getUploadType() {
        return this.uploadType;
    }
    
    public ChromeDeviceManagementRequest<T> setUploadType(final String uploadType) {
        this.uploadType = uploadType;
        return this;
    }
    
    public String getUploadProtocol() {
        return this.uploadProtocol;
    }
    
    public ChromeDeviceManagementRequest<T> setUploadProtocol(final String uploadProtocol) {
        this.uploadProtocol = uploadProtocol;
        return this;
    }
    
    public final ChromeDeviceManagement getAbstractGoogleClient() {
        return (ChromeDeviceManagement)super.getAbstractGoogleClient();
    }
    
    public ChromeDeviceManagementRequest<T> setDisableGZipContent(final boolean disableGZipContent) {
        return (ChromeDeviceManagementRequest)super.setDisableGZipContent(disableGZipContent);
    }
    
    public ChromeDeviceManagementRequest<T> setRequestHeaders(final HttpHeaders requestHeaders) {
        return (ChromeDeviceManagementRequest)super.setRequestHeaders(requestHeaders);
    }
    
    public ChromeDeviceManagementRequest<T> set(final String s, final Object o) {
        return (ChromeDeviceManagementRequest)super.set(s, o);
    }
}
