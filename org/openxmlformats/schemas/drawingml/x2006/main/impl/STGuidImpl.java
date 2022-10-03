package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.drawingml.x2006.main.STGuid;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class STGuidImpl extends JavaStringHolderEx implements STGuid
{
    private static final long serialVersionUID = 1L;
    
    public STGuidImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STGuidImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
