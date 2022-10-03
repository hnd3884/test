package com.azul.crs.shared.models;

public enum SignificantSystemProperty
{
    INSTANCE_TAGS_PROPERTY("com.azul.crs.instance.tags"), 
    INSTANCE_OFFLINE_TIMEOUT_PROPERTY("com.azul.crs.instance.offline.timeout"), 
    CRS_IMAGE_ID("com.azul.crs.image.id");
    
    private final String key;
    
    private SignificantSystemProperty(final String key) {
        this.key = key;
    }
    
    public String getKey() {
        return this.key;
    }
}
