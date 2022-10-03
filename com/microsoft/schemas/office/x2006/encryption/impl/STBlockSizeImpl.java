package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.encryption.STBlockSize;
import org.apache.xmlbeans.impl.values.JavaIntHolderEx;

public class STBlockSizeImpl extends JavaIntHolderEx implements STBlockSize
{
    private static final long serialVersionUID = 1L;
    
    public STBlockSizeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STBlockSizeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
