package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.SchemaType;
import org.w3.x2000.x09.xmldsig.DigestValueType;
import org.apache.xmlbeans.impl.values.JavaBase64HolderEx;

public class DigestValueTypeImpl extends JavaBase64HolderEx implements DigestValueType
{
    private static final long serialVersionUID = 1L;
    
    public DigestValueTypeImpl(final SchemaType schemaType) {
        super(schemaType, false);
    }
    
    protected DigestValueTypeImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
}
