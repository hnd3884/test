package com.microsoft.schemas.office.office.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.office.STConnectType;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STConnectTypeImpl extends JavaStringEnumerationHolderEx implements STConnectType
{
    private static final long serialVersionUID = 1L;
    
    public STConnectTypeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STConnectTypeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
