package org.openxmlformats.schemas.xpackage.x2006.digitalSignature.impl;

import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.STFormat;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class STFormatImpl extends JavaStringHolderEx implements STFormat
{
    private static final long serialVersionUID = 1L;
    
    public STFormatImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STFormatImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
