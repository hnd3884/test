package com.zoho.security.validator.url;

public class ZSecURL
{
    String safeURL;
    String original_url;
    String scheme;
    String customScheme;
    String domainAuthority;
    String pathInfo;
    String queryString;
    String fragment;
    Object importedDataAsFile;
    String dataURIMimeType;
    String dataURIEncoding;
    String dataURICharset;
    String dataURIDatapart;
    
    public ZSecURL(final String urltext) {
        this.safeURL = null;
        this.original_url = null;
        this.scheme = null;
        this.domainAuthority = null;
        this.pathInfo = null;
        this.queryString = null;
        this.fragment = null;
        this.importedDataAsFile = null;
        this.dataURIMimeType = null;
        this.dataURIEncoding = null;
        this.dataURICharset = null;
        this.dataURIDatapart = null;
        this.original_url = urltext;
    }
    
    public String getSafeURL() {
        return this.safeURL;
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public String getCustomScheme() {
        return this.customScheme;
    }
    
    public String getDomainAuthority() {
        return this.domainAuthority;
    }
    
    public String getPathInfo() {
        return this.pathInfo;
    }
    
    public String getQueryString() {
        return this.queryString;
    }
    
    public String getFragment() {
        return this.fragment;
    }
    
    public String getDataURIMimeType() {
        return this.dataURIMimeType;
    }
    
    public String getDataURIEncoding() {
        return this.dataURIEncoding;
    }
    
    public String getDataURICharset() {
        return this.dataURICharset;
    }
    
    public String getDataURIDatapart() {
        return this.dataURIDatapart;
    }
    
    public Object getImportedDataAsFile() {
        return this.importedDataAsFile;
    }
    
    public void setImportedDataAsFile(final Object fileItem) {
        this.importedDataAsFile = fileItem;
    }
    
    @Override
    public String toString() {
        return this.safeURL;
    }
}
