package com.google.api.services.directory;

import com.google.api.client.util.GenericData;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.util.Key;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;

public abstract class DirectoryRequest<T> extends AbstractGoogleJsonClientRequest<T>
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
    
    public DirectoryRequest(final Directory client, final String method, final String uriTemplate, final Object content, final Class<T> responseClass) {
        super((AbstractGoogleJsonClient)client, method, uriTemplate, content, (Class)responseClass);
    }
    
    public String get$Xgafv() {
        return this.$Xgafv;
    }
    
    public DirectoryRequest<T> set$Xgafv(final String $Xgafv) {
        this.$Xgafv = $Xgafv;
        return this;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public DirectoryRequest<T> setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
    
    public String getAlt() {
        return this.alt;
    }
    
    public DirectoryRequest<T> setAlt(final String alt) {
        this.alt = alt;
        return this;
    }
    
    public String getCallback() {
        return this.callback;
    }
    
    public DirectoryRequest<T> setCallback(final String callback) {
        this.callback = callback;
        return this;
    }
    
    public String getFields() {
        return this.fields;
    }
    
    public DirectoryRequest<T> setFields(final String fields) {
        this.fields = fields;
        return this;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public DirectoryRequest<T> setKey(final String key) {
        this.key = key;
        return this;
    }
    
    public String getOauthToken() {
        return this.oauthToken;
    }
    
    public DirectoryRequest<T> setOauthToken(final String oauthToken) {
        this.oauthToken = oauthToken;
        return this;
    }
    
    public Boolean getPrettyPrint() {
        return this.prettyPrint;
    }
    
    public DirectoryRequest<T> setPrettyPrint(final Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }
    
    public String getQuotaUser() {
        return this.quotaUser;
    }
    
    public DirectoryRequest<T> setQuotaUser(final String quotaUser) {
        this.quotaUser = quotaUser;
        return this;
    }
    
    public String getUploadType() {
        return this.uploadType;
    }
    
    public DirectoryRequest<T> setUploadType(final String uploadType) {
        this.uploadType = uploadType;
        return this;
    }
    
    public String getUploadProtocol() {
        return this.uploadProtocol;
    }
    
    public DirectoryRequest<T> setUploadProtocol(final String uploadProtocol) {
        this.uploadProtocol = uploadProtocol;
        return this;
    }
    
    public final Directory getAbstractGoogleClient() {
        return (Directory)super.getAbstractGoogleClient();
    }
    
    public DirectoryRequest<T> setDisableGZipContent(final boolean disableGZipContent) {
        return (DirectoryRequest)super.setDisableGZipContent(disableGZipContent);
    }
    
    public DirectoryRequest<T> setRequestHeaders(final HttpHeaders headers) {
        return (DirectoryRequest)super.setRequestHeaders(headers);
    }
    
    public DirectoryRequest<T> set(final String parameterName, final Object value) {
        return (DirectoryRequest)super.set(parameterName, value);
    }
}
