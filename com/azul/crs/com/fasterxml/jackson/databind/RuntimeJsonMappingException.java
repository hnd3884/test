package com.azul.crs.com.fasterxml.jackson.databind;

public class RuntimeJsonMappingException extends RuntimeException
{
    public RuntimeJsonMappingException(final JsonMappingException cause) {
        super(cause);
    }
    
    public RuntimeJsonMappingException(final String message) {
        super(message);
    }
    
    public RuntimeJsonMappingException(final String message, final JsonMappingException cause) {
        super(message, cause);
    }
}
