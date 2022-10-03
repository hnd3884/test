package org.w3c.tidy;

import org.w3c.dom.CDATASection;

public class DOMCDATASectionImpl extends DOMTextImpl implements CDATASection
{
    protected DOMCDATASectionImpl(final org.w3c.tidy.Node node) {
        super(node);
    }
    
    public String getNodeName() {
        return "#cdata-section";
    }
    
    public short getNodeType() {
        return 4;
    }
}
