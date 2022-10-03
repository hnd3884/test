package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDAttributeGroupTraverser extends XSDAbstractTraverser
{
    XSDAttributeGroupTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }
    
    XSAttributeGroupDecl traverseLocal(final Element elmNode, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        final QName refAttr = (QName)attrValues[XSAttributeChecker.ATTIDX_REF];
        XSAttributeGroupDecl attrGrp = null;
        if (refAttr == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { "attributeGroup (local)", "ref" }, elmNode);
            this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return null;
        }
        attrGrp = (XSAttributeGroupDecl)this.fSchemaHandler.getGlobalDecl(schemaDoc, 2, refAttr, elmNode);
        Element child = DOMUtil.getFirstChildElement(elmNode);
        if (child != null) {
            final String childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            }
            else {
                final String text = DOMUtil.getSyntheticAnnotation(child);
                if (text != null) {
                    this.traverseSyntheticAnnotation(child, text, attrValues, false, schemaDoc);
                }
            }
            if (child != null) {
                final Object[] args = { refAttr.rawname, "(annotation?)", DOMUtil.getLocalName(child) };
                this.reportSchemaError("s4s-elt-must-match.1", args, child);
            }
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return attrGrp;
    }
    
    XSAttributeGroupDecl traverseGlobal(final Element elmNode, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        XSAttributeGroupDecl attrGrp = new XSAttributeGroupDecl();
        final Object[] attrValues = this.fAttrChecker.checkAttributes(elmNode, true, schemaDoc);
        String nameAttr = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        if (nameAttr == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { "attributeGroup (global)", "name" }, elmNode);
            nameAttr = "(no name)";
        }
        attrGrp.fName = nameAttr;
        attrGrp.fTargetNamespace = schemaDoc.fTargetNamespace;
        Element child = DOMUtil.getFirstChildElement(elmNode);
        XSAnnotationImpl annotation = null;
        if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            child = DOMUtil.getNextSiblingElement(child);
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(elmNode);
            if (text != null) {
                annotation = this.traverseSyntheticAnnotation(elmNode, text, attrValues, false, schemaDoc);
            }
        }
        final Element nextNode = this.traverseAttrsAndAttrGrps(child, attrGrp, schemaDoc, grammar, null);
        if (nextNode != null) {
            final Object[] args = { nameAttr, "(annotation?, ((attribute | attributeGroup)*, anyAttribute?))", DOMUtil.getLocalName(nextNode) };
            this.reportSchemaError("s4s-elt-must-match.1", args, nextNode);
        }
        if (nameAttr.equals("(no name)")) {
            this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return null;
        }
        attrGrp.removeProhibitedAttrs();
        final XSAttributeGroupDecl redefinedAttrGrp = (XSAttributeGroupDecl)this.fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(2, new QName(XMLSymbols.EMPTY_STRING, nameAttr, nameAttr, schemaDoc.fTargetNamespace), schemaDoc, elmNode);
        if (redefinedAttrGrp != null) {
            final Object[] errArgs = attrGrp.validRestrictionOf(nameAttr, redefinedAttrGrp);
            if (errArgs != null) {
                this.reportSchemaError((String)errArgs[errArgs.length - 1], errArgs, child);
                this.reportSchemaError("src-redefine.7.2.2", new Object[] { nameAttr, errArgs[errArgs.length - 1] }, child);
            }
        }
        XSObjectList annotations;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject(annotation);
        }
        else {
            annotations = XSObjectListImpl.EMPTY_LIST;
        }
        attrGrp.fAnnotations = annotations;
        if (grammar.getGlobalAttributeGroupDecl(attrGrp.fName) == null) {
            grammar.addGlobalAttributeGroupDecl(attrGrp);
        }
        final String loc = this.fSchemaHandler.schemaDocument2SystemId(schemaDoc);
        final XSAttributeGroupDecl attrGrp2 = grammar.getGlobalAttributeGroupDecl(attrGrp.fName, loc);
        if (attrGrp2 == null) {
            grammar.addGlobalAttributeGroupDecl(attrGrp, loc);
        }
        if (this.fSchemaHandler.fTolerateDuplicates) {
            if (attrGrp2 != null) {
                attrGrp = attrGrp2;
            }
            this.fSchemaHandler.addGlobalAttributeGroupDecl(attrGrp);
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return attrGrp;
    }
}
