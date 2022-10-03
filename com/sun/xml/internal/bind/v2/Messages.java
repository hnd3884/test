package com.sun.xml.internal.bind.v2;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public enum Messages
{
    ILLEGAL_ENTRY, 
    ERROR_LOADING_CLASS, 
    INVALID_PROPERTY_VALUE, 
    UNSUPPORTED_PROPERTY, 
    BROKEN_CONTEXTPATH, 
    NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS, 
    INVALID_TYPE_IN_MAP, 
    INVALID_JAXP_IMPLEMENTATION, 
    JAXP_SUPPORTED_PROPERTY, 
    JAXP_UNSUPPORTED_PROPERTY, 
    JAXP_XML_SECURITY_DISABLED, 
    JAXP_EXTERNAL_ACCESS_CONFIGURED;
    
    private static final ResourceBundle rb;
    
    @Override
    public String toString() {
        return this.format(new Object[0]);
    }
    
    public String format(final Object... args) {
        return MessageFormat.format(Messages.rb.getString(this.name()), args);
    }
    
    static {
        rb = ResourceBundle.getBundle(Messages.class.getName());
    }
}
