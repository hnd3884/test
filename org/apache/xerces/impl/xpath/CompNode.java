package org.apache.xerces.impl.xpath;

import org.apache.xerces.xs.ShortList;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

class CompNode extends XPathSyntaxTreeNode
{
    private int comp;
    private XPathSyntaxTreeNode child1;
    private XPathSyntaxTreeNode child2;
    public static final int EQ = 0;
    public static final int NE = 1;
    public static final int LT = 2;
    public static final int GT = 3;
    public static final int LE = 4;
    public static final int GE = 5;
    
    public CompNode(final int comp, final XPathSyntaxTreeNode child1, final XPathSyntaxTreeNode child2) {
        this.comp = comp;
        this.child1 = child1;
        this.child2 = child2;
    }
    
    public boolean evaluate(final QName qName, final XMLAttributes xmlAttributes, final NamespaceContext namespaceContext) throws Exception {
        final int type = this.child1.getType();
        final int type2 = this.child2.getType();
        if (type == 2 && type2 == 0) {
            final String string = this.child1.getValue(xmlAttributes, namespaceContext).toString();
            final XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)CompNode.dvFactory.getBuiltInType("double");
            return DataMatcher.compareActualValues(xsSimpleTypeDecl.validate(string, null, null), this.child2.getValue(xmlAttributes, namespaceContext), this.comp, xsSimpleTypeDecl);
        }
        if (type == 2 && type2 == 1) {
            final String string2 = this.child1.getValue(xmlAttributes, namespaceContext).toString();
            final XSSimpleTypeDecl xsSimpleTypeDecl2 = (XSSimpleTypeDecl)CompNode.dvFactory.getBuiltInType("string");
            return DataMatcher.compareActualValues(xsSimpleTypeDecl2.validate(string2, null, null), this.child2.getValue(xmlAttributes, namespaceContext), this.comp, xsSimpleTypeDecl2);
        }
        if (type == 0 && type2 == 2) {
            final String string3 = this.child2.getValue(xmlAttributes, namespaceContext).toString();
            final XSSimpleTypeDecl xsSimpleTypeDecl3 = (XSSimpleTypeDecl)CompNode.dvFactory.getBuiltInType("double");
            return DataMatcher.compareActualValues(this.child1.getValue(xmlAttributes, namespaceContext), xsSimpleTypeDecl3.validate(string3, null, null), this.comp, xsSimpleTypeDecl3);
        }
        if (type == 1 && type2 == 2) {
            final String string4 = this.child2.getValue(xmlAttributes, namespaceContext).toString();
            final XSSimpleTypeDecl xsSimpleTypeDecl4 = (XSSimpleTypeDecl)CompNode.dvFactory.getBuiltInType("string");
            return DataMatcher.compareActualValues(this.child1.getValue(xmlAttributes, namespaceContext), xsSimpleTypeDecl4.validate(string4, null, null), this.comp, xsSimpleTypeDecl4);
        }
        if (type == 2 && type2 == 2) {
            final String string5 = this.child1.getValue(xmlAttributes, namespaceContext).toString();
            final String string6 = this.child2.getValue(xmlAttributes, namespaceContext).toString();
            final XSSimpleTypeDecl xsSimpleTypeDecl5 = (XSSimpleTypeDecl)CompNode.dvFactory.getBuiltInType("string");
            return DataMatcher.compareActualValues(xsSimpleTypeDecl5.validate(string5, null, null), xsSimpleTypeDecl5.validate(string6, null, null), this.comp, xsSimpleTypeDecl5);
        }
        if (type == 2 && type2 == 3) {
            final String string7 = this.child1.getValue(xmlAttributes, namespaceContext).toString();
            final XSSimpleTypeDecl xsSimpleTypeDecl6 = (XSSimpleTypeDecl)this.child2.getSimpleType();
            if (xsSimpleTypeDecl6 == null) {
                throw new XPathException("Casted type is not a built-in type");
            }
            return DataMatcher.compareActualValues(xsSimpleTypeDecl6.validate(string7, null, null), this.child2.getValue(xmlAttributes, namespaceContext), this.comp, xsSimpleTypeDecl6);
        }
        else if (type == 3 && type2 == 2) {
            final String string8 = this.child2.getValue(xmlAttributes, namespaceContext).toString();
            final XSSimpleTypeDecl xsSimpleTypeDecl7 = (XSSimpleTypeDecl)this.child1.getSimpleType();
            if (xsSimpleTypeDecl7 == null) {
                throw new XPathException("Casted type is not a built-in type");
            }
            return DataMatcher.compareActualValues(this.child1.getValue(xmlAttributes, namespaceContext), xsSimpleTypeDecl7.validate(string8, null, null), this.comp, xsSimpleTypeDecl7);
        }
        else if (type == 3 && type2 == 3) {
            final XSSimpleTypeDecl xsSimpleTypeDecl8 = (XSSimpleTypeDecl)this.child1.getSimpleType();
            if (xsSimpleTypeDecl8 == null) {
                throw new XPathException("Casted type is not a built-in type");
            }
            final short builtInKind = xsSimpleTypeDecl8.getBuiltInKind();
            final XSSimpleTypeDecl xsSimpleTypeDecl9 = (XSSimpleTypeDecl)this.child2.getSimpleType();
            if (xsSimpleTypeDecl9 == null) {
                throw new XPathException("Casted type is not a built-in type");
            }
            if (DataMatcher.isComparable(builtInKind, xsSimpleTypeDecl9.getBuiltInKind(), null, null)) {
                return DataMatcher.compareActualValues(xsSimpleTypeDecl9.validate(this.child1.getValue(xmlAttributes, namespaceContext), null, null), this.child2.getValue(xmlAttributes, namespaceContext), this.comp, xsSimpleTypeDecl9);
            }
            throw new XPathException("Invalid comparison between incompatible types");
        }
        else {
            if (type == 0 && type2 == 0) {
                return DataMatcher.compareActualValues(this.child1.getValue(xmlAttributes, namespaceContext), this.child2.getValue(xmlAttributes, namespaceContext), this.comp, (XSSimpleTypeDecl)CompNode.dvFactory.getBuiltInType("double"));
            }
            if (type == 1 && type2 == 1) {
                return DataMatcher.compareActualValues(this.child1.getValue(xmlAttributes, namespaceContext), this.child2.getValue(xmlAttributes, namespaceContext), this.comp, (XSSimpleTypeDecl)CompNode.dvFactory.getBuiltInType("string"));
            }
            throw new XPathException("Invalid comparison");
        }
    }
}
