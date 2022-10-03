package org.apache.xerces.impl.xpath;

import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

class FunctionNode extends XPathSyntaxTreeNode
{
    private QName name;
    private XPathSyntaxTreeNode child;
    
    public FunctionNode(final QName name, final XPathSyntaxTreeNode child) throws XPathException {
        if (!"not".equals(name.localpart) || !"http://www.w3.org/2005/xpath-functions".equals(name.uri)) {
            throw new XPathException("Only support fn:not function.");
        }
        this.name = name;
        this.child = child;
    }
    
    public boolean evaluate(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        return !this.child.evaluate(qName, xmlAttributes, namespaceContext);
    }
    
    public String getValue(final QName qName, final XMLAttributes xmlAttributes) {
        return null;
    }
}
