package com.adventnet.iam.security;

import java.util.List;

public abstract class DataFormatValidator
{
    private static final String OPERATION_NOT_ALLOWED = "Operation Not Allowed";
    
    List<String> getKeySet() {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    String get(final String key) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    List<String> getList(final String key) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    void set(final String key, final String value) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    void setList(final String key, final List<String> value) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    boolean hasValidated(final String key) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    ZSecConstants.DataType getDataFormatType() {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
}
