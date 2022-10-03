package com.azul.crs.shared.models;

public enum SignificantEnvironmentVariable
{
    CRS_IMAGE_ID("CRS_IMAGE_ID");
    
    private final String key;
    
    private SignificantEnvironmentVariable(final String key) {
        this.key = key;
    }
    
    public String getKey() {
        return this.key;
    }
}
