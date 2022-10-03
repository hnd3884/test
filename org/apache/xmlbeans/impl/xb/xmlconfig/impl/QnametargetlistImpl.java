package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnametargetlist;
import org.apache.xmlbeans.impl.values.XmlListImpl;

public class QnametargetlistImpl extends XmlListImpl implements Qnametargetlist
{
    private static final long serialVersionUID = 1L;
    
    public QnametargetlistImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected QnametargetlistImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
