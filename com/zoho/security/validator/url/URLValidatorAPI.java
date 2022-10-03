package com.zoho.security.validator.url;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.logging.Logger;

public class URLValidatorAPI
{
    private static final Logger LOGGER;
    static BitSet alphanumeric_hyphen_period;
    private int maxlength;
    int data_uri_maxlength;
    Charset url_charset_encoding;
    boolean allow_defaultValues;
    boolean allowRelativeURL;
    String mode;
    Map<String, Scheme> schemeMap;
    Map<String, CustomScheme> customSchemeMap;
    
    public URLValidatorAPI() {
        this(true, false);
    }
    
    public URLValidatorAPI(final boolean enableAPIModeDefaultConfig) {
        this(enableAPIModeDefaultConfig, false);
    }
    
    public URLValidatorAPI(final boolean enableAPIModeDefaultConfig, final boolean allowRelativeURL) {
        this.maxlength = 2000;
        this.data_uri_maxlength = 2000;
        this.url_charset_encoding = DefaultConfiguration.DEFAULT_URL_VALIDATOR_CONFIGURATION.urlCharsetEncoding;
        this.allow_defaultValues = true;
        this.allowRelativeURL = false;
        this.mode = "encode";
        this.allowRelativeURL = allowRelativeURL;
        if (enableAPIModeDefaultConfig) {
            this.initCustomizableProperties();
        }
        if (allowRelativeURL) {
            final DefaultConfiguration defaultConfig = DefaultConfiguration.DEFAULT_URL_VALIDATOR_CONFIGURATION;
            this.addScheme(new Scheme(SCHEMES.RELATIVE.name().toLowerCase(), null, defaultConfig.pathInfo, defaultConfig.queryString, defaultConfig.fragment));
        }
    }
    
    private void initCustomizableProperties() {
        final DefaultConfiguration defaultConfig = DefaultConfiguration.DEFAULT_URL_VALIDATOR_CONFIGURATION;
        for (final SCHEMES schemeEnum : SCHEMES.values()) {
            final String schemeName = schemeEnum.name();
            Scheme schemeObj = null;
            switch (schemeEnum) {
                case HTTP:
                case HTTPS:
                case FTP: {
                    schemeObj = new Scheme(schemeName, defaultConfig.domainAuthority, defaultConfig.pathInfo, defaultConfig.queryString, defaultConfig.fragment);
                    break;
                }
                case MAILTO: {
                    schemeObj = new Scheme(schemeName, defaultConfig.domainAuthority_mailto, null, defaultConfig.queryString, null);
                    break;
                }
                case TEL: {
                    schemeObj = new Scheme(schemeName, defaultConfig.domainAuthority_tel, null, null, null);
                    break;
                }
                case DATA: {
                    schemeObj = new Scheme(schemeName, null, null, null, defaultConfig.datauripart);
                    schemeObj.setDataURIComponents(defaultConfig.allowedMimetypes, defaultConfig.allowedCharsets, defaultConfig.allowedEncoding);
                    break;
                }
            }
            if (schemeObj != null) {
                this.addScheme(schemeObj);
            }
        }
    }
    
    private static void setAlphaNumericHyphenPeriod() {
        for (int i = 97; i <= 122; ++i) {
            URLValidatorAPI.alphanumeric_hyphen_period.set(i);
        }
        for (int i = 65; i <= 90; ++i) {
            URLValidatorAPI.alphanumeric_hyphen_period.set(i);
        }
        for (int i = 48; i <= 57; ++i) {
            URLValidatorAPI.alphanumeric_hyphen_period.set(i);
        }
        URLValidatorAPI.alphanumeric_hyphen_period.set(45);
        URLValidatorAPI.alphanumeric_hyphen_period.set(46);
    }
    
    public void setDataURIMaxLength(final int datauri_maxlen) {
        this.data_uri_maxlength = datauri_maxlen;
    }
    
    public int getDataURIMaxlength() {
        return this.data_uri_maxlength;
    }
    
    public void setSchemeMap(final Map<String, Scheme> schemeMap) {
        this.schemeMap = schemeMap;
    }
    
    public void addScheme(final Scheme scheme) {
        if (scheme == null) {
            return;
        }
        if (this.schemeMap == null) {
            this.schemeMap = new HashMap<String, Scheme>();
        }
        this.schemeMap.put(scheme.getSchemeName(), scheme);
    }
    
    public Scheme getScheme(final String schemeName) {
        return (this.schemeMap == null) ? null : this.schemeMap.get(schemeName);
    }
    
    public void setCustomSchemeMap(final Map<String, CustomScheme> customSchemeMap) {
        this.customSchemeMap = customSchemeMap;
    }
    
    public void addCustomScheme(final CustomScheme customScheme) {
        if (this.customSchemeMap == null) {
            this.customSchemeMap = new HashMap<String, CustomScheme>();
        }
        this.customSchemeMap.put(customScheme.getSchemeName(), customScheme);
    }
    
    public CustomScheme getCustomScheme(final String customSchemeName) {
        return (this.customSchemeMap == null) ? null : this.customSchemeMap.get(customSchemeName);
    }
    
    public Map<String, CustomScheme> getCustomSchemeMap() {
        return this.customSchemeMap;
    }
    
    public void setMaxlength(final int maxlen) {
        this.maxlength = maxlen;
    }
    
    public int getMaxlength() {
        return this.maxlength;
    }
    
    public ZSecURL getValidatedURLObject(final String url) throws MalformedURLException {
        final String urltext = url.trim();
        final int maxlength = urltext.startsWith("data:") ? this.data_uri_maxlength : this.maxlength;
        if (urltext.length() > maxlength) {
            URLValidatorAPI.LOGGER.log(Level.SEVERE, "\n Exception occured while validating the URL. URL length: \"{0}\" exceeded the maximum length configured: \"{1}\"", new Object[] { urltext.length(), maxlength });
            throw new MalformedURLException("URL_MAX_LENGTH_EXCEEDED");
        }
        final URLParser urlparser = new URLParser(this, urltext);
        return urlparser.safeurl_obj;
    }
    
    public void setMode(final String errorMode) {
        if (errorMode != null) {
            this.mode = errorMode;
        }
    }
    
    public String getMode() {
        return this.mode;
    }
    
    static {
        LOGGER = Logger.getLogger(URLValidatorAPI.class.getName());
        URLValidatorAPI.alphanumeric_hyphen_period = new BitSet(256);
        setAlphaNumericHyphenPeriod();
    }
    
    public enum SCHEMES
    {
        HTTP(0), 
        HTTPS(1), 
        FTP(2), 
        MAILTO(3), 
        TEL(4), 
        RELATIVE(5), 
        DATA(6);
        
        public int value;
        private static final List<SCHEMES> TRANSPORT_PROTOCOL;
        
        private SCHEMES(final int value) {
            this.value = value;
        }
        
        public static List<SCHEMES> getTransportProtocol() {
            return SCHEMES.TRANSPORT_PROTOCOL;
        }
        
        static {
            TRANSPORT_PROTOCOL = new LinkedList<SCHEMES>(Arrays.asList(SCHEMES.HTTP, SCHEMES.HTTPS, SCHEMES.FTP, SCHEMES.MAILTO));
        }
    }
    
    public enum URLCOMPONENTS
    {
        DOMAINAUTHORITY(0), 
        PATHINFO(1), 
        QUERYSTRING(2), 
        FRAGMENT(3);
        
        public int value;
        
        private URLCOMPONENTS(final int value) {
            this.value = value;
        }
    }
    
    public enum DATAURIHEADERS
    {
        MIMETYPES(0), 
        CHARSET(1), 
        ENCODING(2);
        
        public int value;
        
        private DATAURIHEADERS(final int value) {
            this.value = value;
        }
    }
}
