package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.drawingml.x2006.chart.STShape;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class STShapeImpl extends JavaStringEnumerationHolderEx implements STShape
{
    private static final long serialVersionUID = 1L;
    
    public STShapeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STShapeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
