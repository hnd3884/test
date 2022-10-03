package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class CreatePrinterRequest extends GenericJson
{
    @Key
    private String parent;
    @Key
    private Printer printer;
    
    public String getParent() {
        return this.parent;
    }
    
    public CreatePrinterRequest setParent(final String parent) {
        this.parent = parent;
        return this;
    }
    
    public Printer getPrinter() {
        return this.printer;
    }
    
    public CreatePrinterRequest setPrinter(final Printer printer) {
        this.printer = printer;
        return this;
    }
    
    public CreatePrinterRequest set(final String fieldName, final Object value) {
        return (CreatePrinterRequest)super.set(fieldName, value);
    }
    
    public CreatePrinterRequest clone() {
        return (CreatePrinterRequest)super.clone();
    }
}
