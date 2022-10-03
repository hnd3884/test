package com.zoho.security.validator.url;

import java.util.Arrays;
import java.util.List;
import java.nio.charset.Charset;

public class DefaultConfiguration
{
    public static final DefaultConfiguration DEFAULT_URL_VALIDATOR_CONFIGURATION;
    public Charset urlCharsetEncoding;
    char[] domainAuthority;
    char[] pathInfo;
    char[] queryString;
    char[] fragment;
    char[] domainAuthority_mailto;
    char[] domainAuthority_tel;
    char[] datauripart;
    List<String> allowedSchemes;
    List<String> allowedMimetypes;
    List<String> allowedCharsets;
    List<String> allowedEncoding;
    int dataURIMaxLen;
    
    public DefaultConfiguration() {
        this.urlCharsetEncoding = Charset.forName("UTF-8");
        this.domainAuthority = new char[] { '_', '@', ':', '%' };
        this.pathInfo = new char[] { '_', '/', ';', '%', '=' };
        this.queryString = new char[] { '_', '%', '=', '&' };
        this.fragment = new char[] { '?', '/', '_', '$', '(', ')', '*', '+', '%', ' ' };
        this.domainAuthority_mailto = new char[] { '_', '@' };
        this.domainAuthority_tel = new char[] { '_', '+', ';', '=', '%' };
        this.datauripart = new char[] { '%', '=', '/', '+', ' ' };
        this.allowedSchemes = Arrays.asList("http", "https", "ftp", "mailto", "tel");
        this.allowedMimetypes = Arrays.asList("image/png", "image/jpeg", "image/gif");
        this.allowedCharsets = Arrays.asList("utf-8", "iso-8859-1");
        this.allowedEncoding = Arrays.asList("base64");
        this.dataURIMaxLen = 2000;
    }
    
    static {
        DEFAULT_URL_VALIDATOR_CONFIGURATION = new DefaultConfiguration();
    }
}
