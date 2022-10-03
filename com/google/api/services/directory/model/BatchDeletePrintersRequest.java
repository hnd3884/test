package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchDeletePrintersRequest extends GenericJson
{
    @Key
    private List<String> printerIds;
    
    public List<String> getPrinterIds() {
        return this.printerIds;
    }
    
    public BatchDeletePrintersRequest setPrinterIds(final List<String> printerIds) {
        this.printerIds = printerIds;
        return this;
    }
    
    public BatchDeletePrintersRequest set(final String fieldName, final Object value) {
        return (BatchDeletePrintersRequest)super.set(fieldName, value);
    }
    
    public BatchDeletePrintersRequest clone() {
        return (BatchDeletePrintersRequest)super.clone();
    }
}
