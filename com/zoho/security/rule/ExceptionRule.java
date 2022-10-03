package com.zoho.security.rule;

import com.adventnet.iam.security.SecurityUtil;
import org.w3c.dom.Element;

public class ExceptionRule
{
    private final String exceptionName;
    private final short statusCode;
    private String statusMessage;
    
    public ExceptionRule(final String exceptionName, final short statusCode) {
        this.exceptionName = exceptionName;
        this.statusCode = statusCode;
        this.validateRuleConfiguration();
    }
    
    public ExceptionRule(final Element securityExceptionElement) {
        this.exceptionName = securityExceptionElement.getAttribute("name");
        this.statusCode = Short.valueOf(securityExceptionElement.getAttribute("status-code"));
        this.statusMessage = securityExceptionElement.getAttribute("status-message");
        this.validateRuleConfiguration();
    }
    
    private void validateRuleConfiguration() {
        if (!SecurityUtil.isValid(this.exceptionName)) {
            throw new RuntimeException("The exception name should not be null/empty");
        }
        if (this.statusCode < 100 || this.statusCode > 599) {
            throw new RuntimeException("The status-code value must be >= 100 and <= 599");
        }
    }
    
    public String getExceptionName() {
        return this.exceptionName;
    }
    
    public short getStatusCode() {
        return this.statusCode;
    }
    
    public void setStatusMessage(final String statusMessage) {
        this.statusMessage = statusMessage;
    }
    
    public String getStatusMessage() {
        return this.statusMessage;
    }
}
