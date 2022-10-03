package org.apache.xerces.impl.xpath;

import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

class AttrNode extends XPathSyntaxTreeNode
{
    private QName name;
    
    public AttrNode(final QName name) {
        this.name = name;
    }
    
    public boolean evaluate(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        final String value = xmlAttributes.getValue(this.name.uri, this.name.localpart);
        return value != null && value.length() != 0;
    }
    
    public Object getValue(final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        final String value = xmlAttributes.getValue(this.name.uri, this.name.localpart);
        if (value == null) {
            throw new XPathException("Attribute value is null");
        }
        return value;
    }
    
    public int getType() {
        return 2;
    }
}
