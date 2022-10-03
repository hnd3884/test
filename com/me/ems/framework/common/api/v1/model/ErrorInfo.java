package com.me.ems.framework.common.api.v1.model;

import java.util.logging.Logger;

public class ErrorInfo
{
    Logger logger;
    private String errorCode;
    private String i18nKey;
    private int httpStatus;
    private String referenceUri;
    
    public ErrorInfo() {
        this.logger = Logger.getLogger(ErrorInfo.class.getName());
    }
    
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }
    
    public void setI18nKey(final String i18nKey) {
        this.i18nKey = i18nKey;
    }
    
    public void setHttpStatus(final Integer httpStatus) {
        this.httpStatus = httpStatus;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public String getI18nKey() {
        return this.i18nKey;
    }
    
    public Integer getHttpStatus() {
        return this.httpStatus;
    }
    
    public String getReferenceUri() {
        return this.referenceUri;
    }
    
    public void setReferenceUri(final String referenceUri) {
        this.referenceUri = referenceUri;
    }
}
