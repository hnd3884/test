package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ListPrinterModelsResponse extends GenericJson
{
    @Key
    private String nextPageToken;
    @Key
    private List<PrinterModel> printerModels;
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public ListPrinterModelsResponse setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public List<PrinterModel> getPrinterModels() {
        return this.printerModels;
    }
    
    public ListPrinterModelsResponse setPrinterModels(final List<PrinterModel> printerModels) {
        this.printerModels = printerModels;
        return this;
    }
    
    public ListPrinterModelsResponse set(final String fieldName, final Object value) {
        return (ListPrinterModelsResponse)super.set(fieldName, value);
    }
    
    public ListPrinterModelsResponse clone() {
        return (ListPrinterModelsResponse)super.clone();
    }
}
