package com.adventnet.iam.security;

import java.util.List;

public class XSDElementRule
{
    private String name;
    private String type;
    private String template;
    private List<XSDElementRule> attributeRules;
    boolean applyFilterToCDATA;
    
    XSDElementRule(final String name) {
        this.attributeRules = null;
        this.applyFilterToCDATA = false;
        this.name = name;
    }
    
    XSDElementRule(final String name, final String datatype) {
        this.attributeRules = null;
        this.applyFilterToCDATA = false;
        this.name = name;
        this.type = datatype;
    }
    
    public void setTemplate(final String templateName) {
        this.template = templateName;
    }
    
    public void setAttributeRules(final List<XSDElementRule> attributesList) {
        this.attributeRules = attributesList;
    }
    
    public void setIsApplyFilterToCDATA(final boolean isApplyFilterToCDATA) {
        this.applyFilterToCDATA = isApplyFilterToCDATA;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getTemplate() {
        return this.template;
    }
    
    public List<XSDElementRule> getAttributeRules() {
        return this.attributeRules;
    }
    
    public boolean getIsApplyFilterToCDATA() {
        return this.applyFilterToCDATA;
    }
    
    public void validateConfiguration() {
        if (("JSONObject".equals(this.type) || "JSONArray".equals(this.type)) && this.template == null) {
            throw new RuntimeException("Invalid element configuration in XSD - Template is not configured for JSON datatype. Element Name :" + this.name);
        }
    }
}
