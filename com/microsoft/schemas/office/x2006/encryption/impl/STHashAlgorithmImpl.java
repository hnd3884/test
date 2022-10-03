package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.encryption.STHashAlgorithm;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STHashAlgorithmImpl extends JavaStringEnumerationHolderEx implements STHashAlgorithm
{
    private static final long serialVersionUID = 1L;
    
    public STHashAlgorithmImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STHashAlgorithmImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
