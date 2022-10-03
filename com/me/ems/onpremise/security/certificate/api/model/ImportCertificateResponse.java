package com.me.ems.onpremise.security.certificate.api.model;

import java.util.List;

public class ImportCertificateResponse
{
    Long statusCode;
    String responseMessage;
    List placeHolderParams;
    
    public Long getStatusCode() {
        return this.statusCode;
    }
    
    public void setStatusCode(final Long statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getResponseMessage() {
        return this.responseMessage;
    }
    
    public void setResponseMessage(final String responseMessage) {
        this.responseMessage = responseMessage;
    }
    
    public List getPlaceHolderParams() {
        return this.placeHolderParams;
    }
    
    public void setPlaceHolderParams(final List placeHolderParams) {
        this.placeHolderParams = placeHolderParams;
    }
}
