package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnametargetenum;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;

public class QnametargetenumImpl extends JavaStringEnumerationHolderEx implements Qnametargetenum
{
    private static final long serialVersionUID = 1L;
    
    public QnametargetenumImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected QnametargetenumImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
