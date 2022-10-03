package com.google.api.services.androidenterprise;

import com.google.api.client.util.GenericData;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.util.Key;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;

public abstract class AndroidEnterpriseRequest<T> extends AbstractGoogleJsonClientRequest<T>
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
    
    public AndroidEnterpriseRequest(final AndroidEnterprise client, final String method, final String uriTemplate, final Object content, final Class<T> responseClass) {
        super((AbstractGoogleJsonClient)client, method, uriTemplate, content, (Class)responseClass);
    }
    
    public String get$Xgafv() {
        return this.$Xgafv;
    }
    
    public AndroidEnterpriseRequest<T> set$Xgafv(final String $Xgafv) {
        this.$Xgafv = $Xgafv;
        return this;
    }
    
    public String getAccessToken() {
        return this.accessToken;
    }
    
    public AndroidEnterpriseRequest<T> setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
    
    public String getAlt() {
        return this.alt;
    }
    
    public AndroidEnterpriseRequest<T> setAlt(final String alt) {
        this.alt = alt;
        return this;
    }
    
    public String getCallback() {
        return this.callback;
    }
    
    public AndroidEnterpriseRequest<T> setCallback(final String callback) {
        this.callback = callback;
        return this;
    }
    
    public String getFields() {
        return this.fields;
    }
    
    public AndroidEnterpriseRequest<T> setFields(final String fields) {
        this.fields = fields;
        return this;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public AndroidEnterpriseRequest<T> setKey(final String key) {
        this.key = key;
        return this;
    }
    
    public String getOauthToken() {
        return this.oauthToken;
    }
    
    public AndroidEnterpriseRequest<T> setOauthToken(final String oauthToken) {
        this.oauthToken = oauthToken;
        return this;
    }
    
    public Boolean getPrettyPrint() {
        return this.prettyPrint;
    }
    
    public AndroidEnterpriseRequest<T> setPrettyPrint(final Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }
    
    public String getQuotaUser() {
        return this.quotaUser;
    }
    
    public AndroidEnterpriseRequest<T> setQuotaUser(final String quotaUser) {
        this.quotaUser = quotaUser;
        return this;
    }
    
    public String getUploadType() {
        return this.uploadType;
    }
    
    public AndroidEnterpriseRequest<T> setUploadType(final String uploadType) {
        this.uploadType = uploadType;
        return this;
    }
    
    public String getUploadProtocol() {
        return this.uploadProtocol;
    }
    
    public AndroidEnterpriseRequest<T> setUploadProtocol(final String uploadProtocol) {
        this.uploadProtocol = uploadProtocol;
        return this;
    }
    
    public final AndroidEnterprise getAbstractGoogleClient() {
        return (AndroidEnterprise)super.getAbstractGoogleClient();
    }
    
    public AndroidEnterpriseRequest<T> setDisableGZipContent(final boolean disableGZipContent) {
        return (AndroidEnterpriseRequest)super.setDisableGZipContent(disableGZipContent);
    }
    
    public AndroidEnterpriseRequest<T> setRequestHeaders(final HttpHeaders headers) {
        return (AndroidEnterpriseRequest)super.setRequestHeaders(headers);
    }
    
    public AndroidEnterpriseRequest<T> set(final String parameterName, final Object value) {
        return (AndroidEnterpriseRequest)super.set(parameterName, value);
    }
}
