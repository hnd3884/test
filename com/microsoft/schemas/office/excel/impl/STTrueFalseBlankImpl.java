package com.microsoft.schemas.office.excel.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.excel.STTrueFalseBlank;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STTrueFalseBlankImpl extends JavaStringEnumerationHolderEx implements STTrueFalseBlank
{
    private static final long serialVersionUID = 1L;
    
    public STTrueFalseBlankImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STTrueFalseBlankImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
