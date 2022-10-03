package org.apache.xerces.impl.xpath;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.dv.SchemaDVFactory;

abstract class XPathSyntaxTreeNode
{
    public static final int TYPE_UNDEFINED = -1;
    public static final int TYPE_DOUBLE = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_UNTYPED = 2;
    public static final int TYPE_OTHER = 3;
    private static final String SCHEMA11_FACTORY_CLASS = "org.apache.xerces.impl.dv.xs.Schema11DVFactoryImpl";
    protected static SchemaDVFactory dvFactory;
    
    public abstract boolean evaluate(final QName p0, final XMLAttributes p1, final NamespaceContext p2) throws Exception;
    
    public Object getValue(final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        return null;
    }
    
    public String getStringValue() {
        return null;
    }
    
    public int getType() {
        return -1;
    }
    
    public XSSimpleType getSimpleType() {
        return null;
    }
    
    static {
        XPathSyntaxTreeNode.dvFactory = SchemaDVFactory.getInstance("org.apache.xerces.impl.dv.xs.Schema11DVFactoryImpl");
    }
}
