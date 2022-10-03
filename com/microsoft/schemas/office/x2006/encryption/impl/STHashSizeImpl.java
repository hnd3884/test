package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.encryption.STHashSize;
import org.apache.xmlbeans.impl.values.JavaIntHolderEx;

public class STHashSizeImpl extends JavaIntHolderEx implements STHashSize
{
    private static final long serialVersionUID = 1L;
    
    public STHashSizeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STHashSizeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
