package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchCreatePrintersRequest extends GenericJson
{
    @Key
    private List<CreatePrinterRequest> requests;
    
    public List<CreatePrinterRequest> getRequests() {
        return this.requests;
    }
    
    public BatchCreatePrintersRequest setRequests(final List<CreatePrinterRequest> requests) {
        this.requests = requests;
        return this;
    }
    
    public BatchCreatePrintersRequest set(final String fieldName, final Object value) {
        return (BatchCreatePrintersRequest)super.set(fieldName, value);
    }
    
    public BatchCreatePrintersRequest clone() {
        return (BatchCreatePrintersRequest)super.clone();
    }
}
