package org.openxmlformats.schemas.xpackage.x2006.digitalSignature.impl;

import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.STValue;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class STValueImpl extends JavaStringHolderEx implements STValue
{
    private static final long serialVersionUID = 1L;
    
    public STValueImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STValueImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
