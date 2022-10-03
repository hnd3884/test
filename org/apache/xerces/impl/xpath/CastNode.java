package org.apache.xerces.impl.xpath;

import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.dv.XSSimpleType;

class CastNode extends XPathSyntaxTreeNode
{
    private XSSimpleType castedType;
    private XPathSyntaxTreeNode child;
    
    public CastNode(final XPathSyntaxTreeNode child, final QName qName) throws XPathException {
        this.child = child;
        if (!"http://www.w3.org/2001/XMLSchema".equals(qName.uri)) {
            throw new XPathException("Casted type is not a built-in type");
        }
        this.castedType = CastNode.dvFactory.getBuiltInType(qName.localpart);
        if (this.castedType == null) {
            throw new XPathException("Casted type is not a built-in type");
        }
    }
    
    public boolean evaluate(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        final Object value = this.getValue(xmlAttributes, namespaceContext);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (boolean)value;
        }
        if (value instanceof String) {
            return ((String)value).length() > 0;
        }
        return !(value instanceof Double) || (double)value != 0.0;
    }
    
    public Object getValue(final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        final XSSimpleType simpleType = this.getSimpleType();
        Object o;
        if (this.child.getType() == 2) {
            o = simpleType.validate(this.child.getValue(xmlAttributes, namespaceContext).toString(), null, null);
        }
        else {
            o = simpleType.validate(this.child.getStringValue(), null, null);
        }
        if (simpleType.getNumeric()) {
            o = CastNode.dvFactory.getBuiltInType("double").validate(o, null, null);
        }
        return o;
    }
    
    public XSSimpleType getSimpleType() {
        return this.castedType;
    }
    
    public int getType() {
        if (this.castedType.getNumeric()) {
            return 0;
        }
        if (this.castedType.getName().equals("string")) {
            return 1;
        }
        return 3;
    }
}
