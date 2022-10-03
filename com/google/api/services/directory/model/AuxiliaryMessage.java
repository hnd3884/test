package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AuxiliaryMessage extends GenericJson
{
    @Key
    private String auxiliaryMessage;
    @Key
    private String fieldMask;
    @Key
    private String severity;
    
    public String getAuxiliaryMessage() {
        return this.auxiliaryMessage;
    }
    
    public AuxiliaryMessage setAuxiliaryMessage(final String auxiliaryMessage) {
        this.auxiliaryMessage = auxiliaryMessage;
        return this;
    }
    
    public String getFieldMask() {
        return this.fieldMask;
    }
    
    public AuxiliaryMessage setFieldMask(final String fieldMask) {
        this.fieldMask = fieldMask;
        return this;
    }
    
    public String getSeverity() {
        return this.severity;
    }
    
    public AuxiliaryMessage setSeverity(final String severity) {
        this.severity = severity;
        return this;
    }
    
    public AuxiliaryMessage set(final String fieldName, final Object value) {
        return (AuxiliaryMessage)super.set(fieldName, value);
    }
    
    public AuxiliaryMessage clone() {
        return (AuxiliaryMessage)super.clone();
    }
}
