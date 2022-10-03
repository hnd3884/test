package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.ReducedDerivationControl;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class ReducedDerivationControlImpl extends JavaStringEnumerationHolderEx implements ReducedDerivationControl
{
    private static final long serialVersionUID = 1L;
    
    public ReducedDerivationControlImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected ReducedDerivationControlImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
