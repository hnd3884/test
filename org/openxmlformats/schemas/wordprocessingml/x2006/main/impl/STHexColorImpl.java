package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColorRGB;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColorAuto;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;

public class STHexColorImpl extends XmlUnionImpl implements STHexColor, STHexColorAuto, STHexColorRGB
{
    private static final long serialVersionUID = 1L;
    
    public STHexColorImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STHexColorImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
