package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.TypeDerivationControl;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class TypeDerivationControlImpl extends JavaStringEnumerationHolderEx implements TypeDerivationControl
{
    private static final long serialVersionUID = 1L;
    
    public TypeDerivationControlImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected TypeDerivationControlImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
