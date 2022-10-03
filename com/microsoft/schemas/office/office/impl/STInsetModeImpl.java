package com.microsoft.schemas.office.office.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.office.STInsetMode;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STInsetModeImpl extends JavaStringEnumerationHolderEx implements STInsetMode
{
    private static final long serialVersionUID = 1L;
    
    public STInsetModeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STInsetModeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
