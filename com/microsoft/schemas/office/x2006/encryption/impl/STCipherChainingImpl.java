package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.encryption.STCipherChaining;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STCipherChainingImpl extends JavaStringEnumerationHolderEx implements STCipherChaining
{
    private static final long serialVersionUID = 1L;
    
    public STCipherChainingImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STCipherChainingImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
