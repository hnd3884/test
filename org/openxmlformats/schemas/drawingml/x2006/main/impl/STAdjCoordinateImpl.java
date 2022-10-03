package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;

public class STAdjCoordinateImpl extends XmlUnionImpl implements STAdjCoordinate, STCoordinate, STGeomGuideName
{
    private static final long serialVersionUID = 1L;
    
    public STAdjCoordinateImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STAdjCoordinateImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
