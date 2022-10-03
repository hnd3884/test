package org.w3c.tidy;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;

public class DOMDocumentTypeImpl extends DOMNodeImpl implements DocumentType
{
    protected DOMDocumentTypeImpl(final org.w3c.tidy.Node node) {
        super(node);
    }
    
    public short getNodeType() {
        return 10;
    }
    
    public String getNodeName() {
        return this.getName();
    }
    
    public String getName() {
        String string = null;
        if (this.adaptee.type == 1 && this.adaptee.textarray != null && this.adaptee.start < this.adaptee.end) {
            string = TidyUtils.getString(this.adaptee.textarray, this.adaptee.start, this.adaptee.end - this.adaptee.start);
        }
        return string;
    }
    
    public NamedNodeMap getEntities() {
        return null;
    }
    
    public NamedNodeMap getNotations() {
        return null;
    }
    
    public String getPublicId() {
        return null;
    }
    
    public String getSystemId() {
        return null;
    }
    
    public String getInternalSubset() {
        return null;
    }
}
