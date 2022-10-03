package com.google.api.client.json;

import java.io.IOException;
import com.google.api.client.util.Throwables;
import com.google.api.client.util.GenericData;

public class GenericJson extends GenericData implements Cloneable
{
    private JsonFactory jsonFactory;
    
    public final JsonFactory getFactory() {
        return this.jsonFactory;
    }
    
    public final void setFactory(final JsonFactory factory) {
        this.jsonFactory = factory;
    }
    
    @Override
    public String toString() {
        if (this.jsonFactory != null) {
            try {
                return this.jsonFactory.toString(this);
            }
            catch (final IOException e) {
                throw Throwables.propagate(e);
            }
        }
        return super.toString();
    }
    
    public String toPrettyString() throws IOException {
        if (this.jsonFactory != null) {
            return this.jsonFactory.toPrettyString(this);
        }
        return super.toString();
    }
    
    @Override
    public GenericJson clone() {
        return (GenericJson)super.clone();
    }
    
    @Override
    public GenericJson set(final String fieldName, final Object value) {
        return (GenericJson)super.set(fieldName, value);
    }
}
