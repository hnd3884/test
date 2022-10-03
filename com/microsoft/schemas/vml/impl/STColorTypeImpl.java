package com.microsoft.schemas.vml.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.vml.STColorType;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class STColorTypeImpl extends JavaStringHolderEx implements STColorType
{
    private static final long serialVersionUID = 1L;
    
    public STColorTypeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STColorTypeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
