package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.drawingml.x2006.chart.STXstring;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class STXstringImpl extends JavaStringHolderEx implements STXstring
{
    private static final long serialVersionUID = 1L;
    
    public STXstringImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STXstringImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
