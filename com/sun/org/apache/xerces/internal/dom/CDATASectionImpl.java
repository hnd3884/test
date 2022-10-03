package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CDATASection;

public class CDATASectionImpl extends TextImpl implements CDATASection
{
    static final long serialVersionUID = 2372071297878177780L;
    
    public CDATASectionImpl(final CoreDocumentImpl ownerDoc, final String data) {
        super(ownerDoc, data);
    }
    
    @Override
    public short getNodeType() {
        return 4;
    }
    
    @Override
    public String getNodeName() {
        return "#cdata-section";
    }
}
