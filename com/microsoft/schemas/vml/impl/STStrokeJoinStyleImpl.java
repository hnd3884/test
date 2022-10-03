package com.microsoft.schemas.vml.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.vml.STStrokeJoinStyle;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STStrokeJoinStyleImpl extends JavaStringEnumerationHolderEx implements STStrokeJoinStyle
{
    private static final long serialVersionUID = 1L;
    
    public STStrokeJoinStyleImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STStrokeJoinStyleImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
