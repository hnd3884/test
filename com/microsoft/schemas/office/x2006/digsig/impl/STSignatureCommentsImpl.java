package com.microsoft.schemas.office.x2006.digsig.impl;

import org.apache.xmlbeans.SchemaType;
import com.microsoft.schemas.office.x2006.digsig.STSignatureComments;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class STSignatureCommentsImpl extends JavaStringHolderEx implements STSignatureComments
{
    private static final long serialVersionUID = 1L;
    
    public STSignatureCommentsImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected STSignatureCommentsImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
