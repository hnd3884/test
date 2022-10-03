package com.microsoft.schemas.vml.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.vml.STTrueFalse;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STTrueFalseImpl extends JavaStringEnumerationHolderEx implements STTrueFalse
{
    private static final long serialVersionUID = 1L;
    
    public STTrueFalseImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STTrueFalseImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
