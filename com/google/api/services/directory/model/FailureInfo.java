package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class FailureInfo extends GenericJson
{
    @Key
    private String errorCode;
    @Key
    private String errorMessage;
    @Key
    private Printer printer;
    @Key
    private String printerId;
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public FailureInfo setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
        return this;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public FailureInfo setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
    
    public Printer getPrinter() {
        return this.printer;
    }
    
    public FailureInfo setPrinter(final Printer printer) {
        this.printer = printer;
        return this;
    }
    
    public String getPrinterId() {
        return this.printerId;
    }
    
    public FailureInfo setPrinterId(final String printerId) {
        this.printerId = printerId;
        return this;
    }
    
    public FailureInfo set(final String fieldName, final Object value) {
        return (FailureInfo)super.set(fieldName, value);
    }
    
    public FailureInfo clone() {
        return (FailureInfo)super.clone();
    }
}
