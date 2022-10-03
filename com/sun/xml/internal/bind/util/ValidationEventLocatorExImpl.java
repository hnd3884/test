package com.sun.xml.internal.bind.util;

import com.sun.xml.internal.bind.ValidationEventLocatorEx;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;

public class ValidationEventLocatorExImpl extends ValidationEventLocatorImpl implements ValidationEventLocatorEx
{
    private final String fieldName;
    
    public ValidationEventLocatorExImpl(final Object target, final String fieldName) {
        super(target);
        this.fieldName = fieldName;
    }
    
    @Override
    public String getFieldName() {
        return this.fieldName;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("[url=");
        buf.append(this.getURL());
        buf.append(",line=");
        buf.append(this.getLineNumber());
        buf.append(",column=");
        buf.append(this.getColumnNumber());
        buf.append(",node=");
        buf.append(this.getNode());
        buf.append(",object=");
        buf.append(this.getObject());
        buf.append(",field=");
        buf.append(this.getFieldName());
        buf.append("]");
        return buf.toString();
    }
}
