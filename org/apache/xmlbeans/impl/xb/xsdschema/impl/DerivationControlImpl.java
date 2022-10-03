package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.DerivationControl;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class DerivationControlImpl extends JavaStringEnumerationHolderEx implements DerivationControl
{
    private static final long serialVersionUID = 1L;
    
    public DerivationControlImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected DerivationControlImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
