package com.adventnet.iam.security;

import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Element;
import java.util.List;

public class ResponseHeaderRule
{
    private String headerName;
    private String headerValue;
    private String defaultValue;
    private String scope;
    private List<String> allowedServices;
    private List<ResponseHeaderRule> innerResponseHeaderList;
    
    public ResponseHeaderRule(final Element headerElement) {
        this.innerResponseHeaderList = new LinkedList<ResponseHeaderRule>();
        this.headerName = headerElement.getAttribute("name");
        this.headerValue = headerElement.getAttribute("value");
        this.defaultValue = headerElement.getAttribute("default-value");
        this.scope = headerElement.getAttribute("scope");
        final String allowedServices = headerElement.getAttribute("allowed-services");
        if (SecurityUtil.isValid(allowedServices)) {
            this.allowedServices = SecurityUtil.getStringAsList(allowedServices, ",");
        }
        List<Element> innerResponseHeaderElements = null;
        final List<Element> coheaderNodeList = RuleSetParser.getChildNodesByTagName(headerElement, "coheader");
        if (coheaderNodeList.size() > 0 && coheaderNodeList.get(0).getNodeType() == 1) {
            final Element coheaderElement = coheaderNodeList.get(0);
            innerResponseHeaderElements = RuleSetParser.getChildNodesByTagName(coheaderElement, "header");
            for (final Element innerResponseHeaderElement : innerResponseHeaderElements) {
                final ResponseHeaderRule innerResponseHeaderRule = new ResponseHeaderRule(innerResponseHeaderElement);
                this.innerResponseHeaderList.add(innerResponseHeaderRule);
            }
        }
    }
    
    public ResponseHeaderRule(final String headerName, final String headerValue) {
        this.innerResponseHeaderList = new LinkedList<ResponseHeaderRule>();
        this.headerName = headerName;
        this.headerValue = headerValue;
        this.defaultValue = "";
        this.scope = "";
    }
    
    public String getHeaderName() {
        return this.headerName.trim();
    }
    
    public void setHeaderName(final String name) {
        this.headerName = name;
    }
    
    public String getHeaderValue() {
        return this.headerValue.trim();
    }
    
    public void setHeaderValue(final String value) {
        this.headerValue = value;
    }
    
    public List<ResponseHeaderRule> getInnerResponseHeaderList() {
        return this.innerResponseHeaderList;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public List<String> getAllowedServices() {
        return this.allowedServices;
    }
}
