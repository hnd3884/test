package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ListPrintersResponse extends GenericJson
{
    @Key
    private String nextPageToken;
    @Key
    private List<Printer> printers;
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public ListPrintersResponse setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public List<Printer> getPrinters() {
        return this.printers;
    }
    
    public ListPrintersResponse setPrinters(final List<Printer> printers) {
        this.printers = printers;
        return this;
    }
    
    public ListPrintersResponse set(final String fieldName, final Object value) {
        return (ListPrintersResponse)super.set(fieldName, value);
    }
    
    public ListPrintersResponse clone() {
        return (ListPrintersResponse)super.clone();
    }
}
