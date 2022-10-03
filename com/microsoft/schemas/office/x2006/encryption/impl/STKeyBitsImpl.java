package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.encryption.STKeyBits;
import org.apache.xmlbeans.impl.values.JavaLongHolderEx;

public class STKeyBitsImpl extends JavaLongHolderEx implements STKeyBits
{
    private static final long serialVersionUID = 1L;
    
    public STKeyBitsImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STKeyBitsImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
