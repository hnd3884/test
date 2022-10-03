package com.google.api.client.googleapis.json;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public class GoogleJsonErrorContainer extends GenericJson
{
    @Key
    private GoogleJsonError error;
    
    public final GoogleJsonError getError() {
        return this.error;
    }
    
    public final void setError(final GoogleJsonError error) {
        this.error = error;
    }
    
    public GoogleJsonErrorContainer set(final String fieldName, final Object value) {
        return (GoogleJsonErrorContainer)super.set(fieldName, value);
    }
    
    public GoogleJsonErrorContainer clone() {
        return (GoogleJsonErrorContainer)super.clone();
    }
}
