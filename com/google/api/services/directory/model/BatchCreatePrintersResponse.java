package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchCreatePrintersResponse extends GenericJson
{
    @Key
    private List<FailureInfo> failures;
    @Key
    private List<Printer> printers;
    
    public List<FailureInfo> getFailures() {
        return this.failures;
    }
    
    public BatchCreatePrintersResponse setFailures(final List<FailureInfo> failures) {
        this.failures = failures;
        return this;
    }
    
    public List<Printer> getPrinters() {
        return this.printers;
    }
    
    public BatchCreatePrintersResponse setPrinters(final List<Printer> printers) {
        this.printers = printers;
        return this;
    }
    
    public BatchCreatePrintersResponse set(final String fieldName, final Object value) {
        return (BatchCreatePrintersResponse)super.set(fieldName, value);
    }
    
    public BatchCreatePrintersResponse clone() {
        return (BatchCreatePrintersResponse)super.clone();
    }
}
