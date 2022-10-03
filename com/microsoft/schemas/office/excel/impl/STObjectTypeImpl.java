package com.microsoft.schemas.office.excel.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.excel.STObjectType;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STObjectTypeImpl extends JavaStringEnumerationHolderEx implements STObjectType
{
    private static final long serialVersionUID = 1L;
    
    public STObjectTypeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STObjectTypeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
