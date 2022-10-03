package org.w3c.tidy;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class DOMTextImpl extends DOMCharacterDataImpl implements Text
{
    protected DOMTextImpl(final org.w3c.tidy.Node node) {
        super(node);
    }
    
    public String getNodeName() {
        return "#text";
    }
    
    public short getNodeType() {
        return 3;
    }
    
    public Text splitText(final int n) throws DOMException {
        throw new DOMException((short)7, "Not supported");
    }
    
    public String getWholeText() {
        return null;
    }
    
    public boolean isElementContentWhitespace() {
        return false;
    }
    
    public Text replaceWholeText(final String s) throws DOMException {
        return this;
    }
}
