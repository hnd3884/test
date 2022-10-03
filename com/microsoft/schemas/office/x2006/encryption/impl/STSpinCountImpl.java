package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.encryption.STSpinCount;
import org.apache.xmlbeans.impl.values.JavaIntHolderEx;

public class STSpinCountImpl extends JavaIntHolderEx implements STSpinCount
{
    private static final long serialVersionUID = 1L;
    
    public STSpinCountImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STSpinCountImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
