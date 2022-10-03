package com.zoho.security.validator.url;

import java.util.Collection;
import java.util.Arrays;
import org.w3c.dom.Node;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import org.w3c.dom.Element;
import java.util.List;
import java.util.Map;

public class URLValidatorRule
{
    private String urlValidatorName;
    private String mode;
    private boolean allowRelativeURL;
    private int maxLen;
    private int datauri_maxLen;
    private Map<String, Scheme> schemeMap;
    private Map<String, CustomScheme> customSchemeMap;
    private static final List<String> DEFAULT_RESERVED_CHRACTERS;
    private static final List<String> PROTOCOL_SUPPORTED;
    public static final List<String> COMMON_PROTOCOL;
    URLValidatorAPI urlvalidator;
    
    public URLValidatorAPI getUrlvalidator() {
        return this.urlvalidator;
    }
    
    public URLValidatorRule(final Element urlValidatorElement) {
        this.urlValidatorName = null;
        this.mode = null;
        this.allowRelativeURL = false;
        this.maxLen = 2000;
        this.datauri_maxLen = 3000;
        this.urlvalidator = null;
        final String name = urlValidatorElement.getAttribute("name");
        if (isValid(name)) {
            this.urlValidatorName = name;
        }
        final String mode = urlValidatorElement.getAttribute("mode");
        if (isValid(mode)) {
            this.setMode(mode);
        }
        this.allowRelativeURL = "true".equalsIgnoreCase(urlValidatorElement.getAttribute("allow-relative-url"));
        final String len = urlValidatorElement.getAttribute("max-len");
        if (isValid(len)) {
            this.setMaxLen(Integer.parseInt(len));
        }
        final String dataURI_len = urlValidatorElement.getAttribute("datauri-max-len");
        if (isValid(dataURI_len)) {
            this.setDataUriMaxLen(Integer.parseInt(dataURI_len));
        }
        this.initializeURLValidatorRule(urlValidatorElement);
    }
    
    private void setMaxLen(final int len) {
        this.maxLen = len;
    }
    
    public int getMaxLen() {
        return this.maxLen;
    }
    
    public int getDatauriMaxLen() {
        return this.datauri_maxLen;
    }
    
    private void setDataUriMaxLen(final int len) {
        this.datauri_maxLen = len;
    }
    
    private void initializeURLValidatorRule(final Element urlValidatorElement) {
        final List<Scheme> schemeList = this.parseAndGetSchemes(urlValidatorElement);
        if (isValid(schemeList)) {
            this.schemeMap = new HashMap<String, Scheme>();
            for (final Scheme scheme : schemeList) {
                if (this.schemeMap.containsKey(scheme.getSchemeName())) {
                    throw new RuntimeException("The scheme name " + scheme.getSchemeName() + " is already defined in <url-validator name=\"" + this.urlValidatorName + "\">");
                }
                this.schemeMap.put(scheme.getSchemeName(), scheme);
            }
        }
        final List<CustomScheme> customSchemes = this.parseAndGetCustomSchemes(urlValidatorElement);
        if (isValid(customSchemes)) {
            this.customSchemeMap = new HashMap<String, CustomScheme>();
            for (final CustomScheme customScheme : customSchemes) {
                if (this.customSchemeMap.containsKey(customScheme.getSchemeName())) {
                    throw new RuntimeException("The custom-scheme name " + customScheme.getSchemeName() + " is already defined in <url-validator name=\"" + this.urlValidatorName + "\">");
                }
                this.customSchemeMap.put(customScheme.getSchemeName(), customScheme);
            }
        }
    }
    
    private List<CustomScheme> parseAndGetCustomSchemes(final Element urlValidatorElement) {
        final List<Element> customSchemeElements = getChildNodesByTagName(urlValidatorElement, TagName.CUSTOM_SCHEME.getValue());
        if (customSchemeElements.isEmpty()) {
            return null;
        }
        final List<CustomScheme> customSchemeList = new ArrayList<CustomScheme>();
        for (final Element customSchemeElement : customSchemeElements) {
            for (String customSchemeName : customSchemeElement.getAttribute("name").toLowerCase().split(",")) {
                customSchemeName = customSchemeName.trim();
                if (isValid(customSchemeName)) {
                    final CustomScheme customScheme = new CustomScheme(customSchemeName);
                    final List<Scheme> schemes = this.parseAndGetSchemes(customSchemeElement);
                    if (isValid(schemes)) {
                        for (final Scheme scheme : schemes) {
                            customScheme.addScheme(scheme);
                        }
                    }
                    customScheme.setURLComponents(this.getAllowedValues(customSchemeElement, TagName.DOMAIN_AUTHORITY), this.getAllowedValues(customSchemeElement, TagName.PATHINFO), this.getAllowedValues(customSchemeElement, TagName.QUERYSTRING), this.getAllowedValues(customSchemeElement, TagName.FRAGMENT));
                    customSchemeList.add(customScheme);
                }
            }
        }
        return customSchemeList;
    }
    
    private List<Scheme> parseAndGetSchemes(final Element parentElement) {
        final List<Element> schemeElements = getChildNodesByTagName(parentElement, TagName.SCHEME.getValue());
        if (schemeElements.isEmpty()) {
            return null;
        }
        final List<Scheme> schemeList = new ArrayList<Scheme>();
        for (final Element schemeElement : schemeElements) {
            for (String schemeName : schemeElement.getAttribute("name").toLowerCase().split(",")) {
                schemeName = schemeName.trim();
                if (isValid(schemeName)) {
                    final Scheme scheme = this.parseAndGetScheme(schemeName, schemeElement);
                    if (scheme != null) {
                        schemeList.add(scheme);
                    }
                }
            }
        }
        return schemeList;
    }
    
    private Scheme parseAndGetScheme(final String schemeName, final Element schemeElem) {
        Scheme schemeObj = null;
        if (URLValidatorRule.PROTOCOL_SUPPORTED.contains(schemeName)) {
            schemeObj = new Scheme();
            schemeObj.setSchemeName(schemeName);
            switch (URLValidatorAPI.SCHEMES.valueOf(schemeName.toUpperCase())) {
                case HTTP:
                case HTTPS:
                case FTP: {
                    schemeObj.setURLComponents(this.getAllowedValues(schemeElem, TagName.DOMAIN_AUTHORITY), this.getAllowedValues(schemeElem, TagName.PATHINFO), this.getAllowedValues(schemeElem, TagName.QUERYSTRING), this.getAllowedValues(schemeElem, TagName.FRAGMENT));
                    break;
                }
                case MAILTO: {
                    schemeObj.setURLComponents(this.getAllowedValues(schemeElem, TagName.DOMAIN_AUTHORITY), null, this.getAllowedValues(schemeElem, TagName.QUERYSTRING), null);
                    break;
                }
                case TEL: {
                    schemeObj.setURLComponents(this.getAllowedValues(schemeElem, TagName.DOMAIN_AUTHORITY), null, null, null);
                    break;
                }
                case DATA: {
                    schemeObj.setURLComponents(null, null, null, this.getAllowedValues(schemeElem, TagName.DATAPART));
                    schemeObj.setDataURIComponents(this.getAllowedValuesList(schemeElem, TagName.MIMETYPES), this.getAllowedValuesList(schemeElem, TagName.CHARSETS), this.getAllowedValuesList(schemeElem, TagName.ENCODING));
                    break;
                }
            }
        }
        return schemeObj;
    }
    
    private List<String> getAllowedValuesList(final Element schemeElem, final TagName urlcomponentName) {
        final String[] allowedValues = this.getAllowedValuesArray(schemeElem, urlcomponentName);
        final List<String> allowedValuesList = new LinkedList<String>();
        if (allowedValues != null) {
            for (String value : allowedValues) {
                value = value.toLowerCase();
                if (!allowedValuesList.contains(value)) {
                    allowedValuesList.add(value);
                }
            }
        }
        return allowedValuesList;
    }
    
    private char[] getAllowedValues(final Element schemeElem, final TagName urlcomponentName) {
        final String[] allowedValues = this.getAllowedValuesArray(schemeElem, urlcomponentName);
        if (allowedValues != null) {
            final StringBuilder allowedValuesBuilder = new StringBuilder();
            for (final String value : allowedValues) {
                if (!URLValidatorRule.DEFAULT_RESERVED_CHRACTERS.contains(value) && value.length() == 1) {
                    allowedValuesBuilder.append(value);
                }
            }
            return (char[])((allowedValuesBuilder.length() > 0) ? allowedValuesBuilder.toString().toCharArray() : null);
        }
        return null;
    }
    
    private String[] getAllowedValuesArray(final Element schemeElem, final TagName urlcomponentName) {
        final Element urlComponentElement = getFirstChildNodeByTagName(schemeElem, urlcomponentName.getValue());
        if (urlComponentElement != null) {
            final String allowedValuesStr = urlComponentElement.getAttribute("allowed-values");
            if (isValid(allowedValuesStr)) {
                return allowedValuesStr.split("\\|");
            }
        }
        return null;
    }
    
    public void initializeParentVariables(final URLValidatorRule parentUrlValidatorsRule) {
        if (this.mode == null) {
            this.mode = parentUrlValidatorsRule.mode;
        }
        if (this.schemeMap == null) {
            this.schemeMap = parentUrlValidatorsRule.schemeMap;
        }
        else if (parentUrlValidatorsRule.schemeMap != null) {
            for (final Scheme schemeObj : parentUrlValidatorsRule.schemeMap.values()) {
                if (!this.schemeMap.containsKey(schemeObj.getSchemeName())) {
                    this.schemeMap.put(schemeObj.getSchemeName(), schemeObj);
                }
            }
        }
        if (this.customSchemeMap == null) {
            this.customSchemeMap = parentUrlValidatorsRule.customSchemeMap;
        }
        else if (parentUrlValidatorsRule.customSchemeMap != null) {
            for (final CustomScheme parentCustomScheme : parentUrlValidatorsRule.customSchemeMap.values()) {
                if (!this.customSchemeMap.containsKey(parentCustomScheme.getSchemeName())) {
                    this.customSchemeMap.put(parentCustomScheme.getSchemeName(), parentCustomScheme);
                }
            }
        }
    }
    
    public String getName() {
        return this.urlValidatorName;
    }
    
    public String getMode() {
        return this.mode;
    }
    
    public void setMode(final String mode) {
        this.mode = mode;
    }
    
    public Map<String, Scheme> getSchemeMap() {
        return this.schemeMap;
    }
    
    public void setSchemeMap(final Map<String, Scheme> scheme) {
        this.schemeMap = scheme;
    }
    
    public Map<String, CustomScheme> getCustomSchemeMap() {
        return this.customSchemeMap;
    }
    
    public boolean isAllowRelativeURL() {
        return this.allowRelativeURL;
    }
    
    public void createZsecURLValidatorInstance() {
        (this.urlvalidator = new URLValidatorAPI(false, this.allowRelativeURL)).setMode(this.mode);
        this.urlvalidator.setMaxlength(this.maxLen);
        this.urlvalidator.setDataURIMaxLength(this.datauri_maxLen);
        if (this.schemeMap != null) {
            for (final Scheme scheme : this.schemeMap.values()) {
                this.urlvalidator.addScheme(scheme);
            }
        }
        if (this.customSchemeMap != null) {
            for (final CustomScheme customScheme : this.customSchemeMap.values()) {
                this.urlvalidator.addCustomScheme(customScheme);
            }
        }
    }
    
    public static List<Element> getChildNodesByTagName(final Element element, final String tagName) {
        final List<Element> nodeList = new ArrayList<Element>();
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if (childNode.getNodeType() == 1 && childNode.getNodeName().equalsIgnoreCase(tagName)) {
                final Element childElement = (Element)childNode;
                nodeList.add(childElement);
            }
        }
        return nodeList;
    }
    
    public static boolean isValid(final Object value) {
        return value != null && !value.equals("null") && !value.equals("");
    }
    
    public static Element getFirstChildNodeByTagName(final Element element, final String tagName) {
        for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            if (childNode.getNodeType() == 1 && childNode.getNodeName().equalsIgnoreCase(tagName)) {
                return (Element)childNode;
            }
        }
        return null;
    }
    
    static {
        DEFAULT_RESERVED_CHRACTERS = new LinkedList<String>(Arrays.asList("alpha", "numeric", "-", "."));
        PROTOCOL_SUPPORTED = new LinkedList<String>(Arrays.asList("http", "https", "ftp", "mailto", "tel", "data"));
        COMMON_PROTOCOL = new LinkedList<String>(Arrays.asList("http", "https"));
    }
    
    public enum TagName
    {
        URL_VALIDATOR("url-validator"), 
        URL_VALIDATORS("url-validators"), 
        SCHEME("scheme"), 
        CUSTOM_SCHEME("custom-scheme"), 
        DOMAIN_AUTHORITY("domainauthority"), 
        PATHINFO("pathinfo"), 
        QUERYSTRING("querystring"), 
        FRAGMENT("fragment"), 
        DATAPART("datapart"), 
        MIMETYPES("mimetypes"), 
        CHARSETS("charsets"), 
        ENCODING("encoding");
        
        private String value;
        
        private TagName(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
