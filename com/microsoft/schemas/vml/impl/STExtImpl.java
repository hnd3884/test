package com.microsoft.schemas.vml.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.vml.STExt;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STExtImpl extends JavaStringEnumerationHolderEx implements STExt
{
    private static final long serialVersionUID = 1L;
    
    public STExtImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STExtImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
