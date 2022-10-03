package org.apache.xerces.impl.xpath;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

class LiteralNode extends XPathSyntaxTreeNode
{
    private String value;
    private boolean isNumeric;
    
    public LiteralNode(final String value, final boolean isNumeric) {
        this.value = value;
        this.isNumeric = isNumeric;
    }
    
    public boolean evaluate(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        final Object value = this.getValue(xmlAttributes, namespaceContext);
        if (this.isNumeric) {
            return value != null && 0.0 != (double)value;
        }
        return value != null;
    }
    
    public Object getValue(final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        XSSimpleType xsSimpleType;
        if (this.isNumeric) {
            xsSimpleType = LiteralNode.dvFactory.getBuiltInType("double");
        }
        else {
            xsSimpleType = LiteralNode.dvFactory.getBuiltInType("string");
        }
        return xsSimpleType.validate(this.value, null, null);
    }
    
    public String getStringValue() {
        return this.value;
    }
    
    public int getType() {
        return this.isNumeric ? 0 : 1;
    }
}
