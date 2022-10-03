package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class FormChoiceImpl extends JavaStringEnumerationHolderEx implements FormChoice
{
    private static final long serialVersionUID = 1L;
    
    public FormChoiceImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected FormChoiceImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
