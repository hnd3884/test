package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages
{
    UNRESOLVED_IDREF, 
    UNEXPECTED_ELEMENT, 
    UNEXPECTED_TEXT, 
    NOT_A_QNAME, 
    UNRECOGNIZED_TYPE_NAME, 
    UNRECOGNIZED_TYPE_NAME_MAYBE, 
    UNABLE_TO_CREATE_MAP, 
    UNINTERNED_STRINGS, 
    ERRORS_LIMIT_EXCEEDED;
    
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
