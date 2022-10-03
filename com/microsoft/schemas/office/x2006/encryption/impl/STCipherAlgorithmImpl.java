package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.encryption.STCipherAlgorithm;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STCipherAlgorithmImpl extends JavaStringEnumerationHolderEx implements STCipherAlgorithm
{
    private static final long serialVersionUID = 1L;
    
    public STCipherAlgorithmImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STCipherAlgorithmImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
