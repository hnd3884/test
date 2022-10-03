package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PrinterModel extends GenericJson
{
    @Key
    private String displayName;
    @Key
    private String makeAndModel;
    @Key
    private String manufacturer;
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public PrinterModel setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public String getMakeAndModel() {
        return this.makeAndModel;
    }
    
    public PrinterModel setMakeAndModel(final String makeAndModel) {
        this.makeAndModel = makeAndModel;
        return this;
    }
    
    public String getManufacturer() {
        return this.manufacturer;
    }
    
    public PrinterModel setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }
    
    public PrinterModel set(final String fieldName, final Object value) {
        return (PrinterModel)super.set(fieldName, value);
    }
    
    public PrinterModel clone() {
        return (PrinterModel)super.clone();
    }
}
