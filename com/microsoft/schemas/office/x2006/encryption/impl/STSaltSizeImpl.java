package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.encryption.STSaltSize;
import org.apache.xmlbeans.impl.values.JavaIntHolderEx;

public class STSaltSizeImpl extends JavaIntHolderEx implements STSaltSize
{
    private static final long serialVersionUID = 1L;
    
    public STSaltSizeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STSaltSizeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
