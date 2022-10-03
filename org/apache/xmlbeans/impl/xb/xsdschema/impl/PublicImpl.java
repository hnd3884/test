package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class PublicImpl extends JavaStringHolderEx implements Public
{
    private static final long serialVersionUID = 1L;
    
    public PublicImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected PublicImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
