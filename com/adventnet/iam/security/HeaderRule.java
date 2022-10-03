package com.adventnet.iam.security;

import org.w3c.dom.Element;

public abstract class HeaderRule
{
    private ParameterRule headerRule;
    
    public HeaderRule() {
    }
    
    public HeaderRule(final Element headerElement) {
        this.headerRule = new ParameterRule(headerElement);
        this.headerRule.storeParameterValue = false;
    }
    
    public ParameterRule getHeaderRule() {
        return this.headerRule;
    }
    
    public void setHeaderRule(final ParameterRule headerRule) {
        this.headerRule = headerRule;
    }
    
    public String validate(final SecurityRequestWrapper securedRequest, final String headerValue) {
        String validated_header_value = null;
        if (this.headerRule != null) {
            if (this.headerRule.isSplitDefined()) {
                for (final String value : this.headerRule.getSplitPattern().split(headerValue)) {
                    validated_header_value = this.headerRule.validateParamValue(securedRequest, this.getHeaderName(), value, null);
                }
            }
            else {
                validated_header_value = this.headerRule.validateParamValue(securedRequest, this.getHeaderName(), headerValue, null);
            }
        }
        return validated_header_value;
    }
    
    public String getHeaderName() {
        return this.getHeaderRule().getParamName();
    }
    
    public String getHeaderValue() {
        return this.getHeaderValue();
    }
    
    @Override
    public String toString() {
        return this.headerRule.toString();
    }
}
