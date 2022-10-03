package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchDeletePrintersResponse extends GenericJson
{
    @Key
    private List<FailureInfo> failedPrinters;
    @Key
    private List<String> printerIds;
    
    public List<FailureInfo> getFailedPrinters() {
        return this.failedPrinters;
    }
    
    public BatchDeletePrintersResponse setFailedPrinters(final List<FailureInfo> failedPrinters) {
        this.failedPrinters = failedPrinters;
        return this;
    }
    
    public List<String> getPrinterIds() {
        return this.printerIds;
    }
    
    public BatchDeletePrintersResponse setPrinterIds(final List<String> printerIds) {
        this.printerIds = printerIds;
        return this;
    }
    
    public BatchDeletePrintersResponse set(final String fieldName, final Object value) {
        return (BatchDeletePrintersResponse)super.set(fieldName, value);
    }
    
    public BatchDeletePrintersResponse clone() {
        return (BatchDeletePrintersResponse)super.clone();
    }
}
