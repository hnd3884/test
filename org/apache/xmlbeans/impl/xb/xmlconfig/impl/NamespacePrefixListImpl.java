package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xmlconfig.NamespacePrefixList;
import org.apache.xmlbeans.impl.values.XmlListImpl;

public class NamespacePrefixListImpl extends XmlListImpl implements NamespacePrefixList
{
    private static final long serialVersionUID = 1L;
    
    public NamespacePrefixListImpl(final SchemaType sType) {
        super(sType, false);
    }
    
    protected NamespacePrefixListImpl(final SchemaType sType, final boolean b) {
        super(sType, b);
    }
}
