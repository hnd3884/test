package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalElement;

public class LocalElementImpl extends ElementImpl implements LocalElement
{
    private static final long serialVersionUID = 1L;
    
    public LocalElementImpl(final SchemaType sType) {
        super(sType);
    }
}
