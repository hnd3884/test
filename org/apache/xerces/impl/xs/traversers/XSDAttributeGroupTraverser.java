package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDAttributeGroupTraverser extends XSDAbstractTraverser
{
    XSDAttributeGroupTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
    }
    
    XSAttributeGroupDecl traverseLocal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final QName qName = (QName)checkAttributes[XSAttributeChecker.ATTIDX_REF];
        if (qName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { "attributeGroup (local)", "ref" }, element);
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return null;
        }
        final XSAttributeGroupDecl xsAttributeGroupDecl = (XSAttributeGroupDecl)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 2, qName, element);
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 != null) {
            if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo);
                element2 = DOMUtil.getNextSiblingElement(element2);
            }
            else {
                final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element2);
                if (syntheticAnnotation != null) {
                    this.traverseSyntheticAnnotation(element2, syntheticAnnotation, checkAttributes, false, xsDocumentInfo);
                }
            }
            if (element2 != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { qName.rawname, "(annotation?)", DOMUtil.getLocalName(element2) }, element2);
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return xsAttributeGroupDecl;
    }
    
    XSAttributeGroupDecl traverseGlobal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        XSAttributeGroupDecl xsAttributeGroupDecl = new XSAttributeGroupDecl();
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, true, xsDocumentInfo);
        String fName = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAME];
        if (fName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { "attributeGroup (global)", "name" }, element);
            fName = "(no name)";
        }
        xsAttributeGroupDecl.fName = fName;
        xsAttributeGroupDecl.fTargetNamespace = xsDocumentInfo.fTargetNamespace;
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject xsObject = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xsObject = this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                xsObject = this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo);
            }
        }
        final Element traverseAttrsAndAttrGrps = this.traverseAttrsAndAttrGrps(element2, xsAttributeGroupDecl, xsDocumentInfo, schemaGrammar, xsAttributeGroupDecl);
        if (traverseAttrsAndAttrGrps != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { fName, "(annotation?, ((attribute | attributeGroup)*, anyAttribute?))", DOMUtil.getLocalName(traverseAttrsAndAttrGrps) }, traverseAttrsAndAttrGrps);
        }
        if (fName.equals("(no name)")) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return null;
        }
        xsAttributeGroupDecl.removeProhibitedAttrs();
        final XSAttributeGroupDecl xsAttributeGroupDecl2 = (XSAttributeGroupDecl)this.fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(2, new QName(XMLSymbols.EMPTY_STRING, fName, fName, xsDocumentInfo.fTargetNamespace), xsDocumentInfo, element);
        if (xsAttributeGroupDecl2 != null) {
            final Object[] validRestriction = xsAttributeGroupDecl.validRestrictionOf(fName, xsAttributeGroupDecl2, this.fSchemaHandler.fXSConstraints);
            if (validRestriction != null) {
                this.reportSchemaError((String)validRestriction[validRestriction.length - 1], validRestriction, element2);
                this.reportSchemaError("src-redefine.7.2.2", new Object[] { fName, validRestriction[validRestriction.length - 1] }, element2);
            }
        }
        XSObjectListImpl empty_LIST;
        if (xsObject != null) {
            empty_LIST = new XSObjectListImpl();
            empty_LIST.addXSObject(xsObject);
        }
        else {
            empty_LIST = XSObjectListImpl.EMPTY_LIST;
        }
        xsAttributeGroupDecl.fAnnotations = empty_LIST;
        if (schemaGrammar.getGlobalAttributeGroupDecl(xsAttributeGroupDecl.fName) == null || DOMUtil.getLocalName(DOMUtil.getParent(element)).equals(SchemaSymbols.ELT_REDEFINE)) {
            schemaGrammar.addGlobalAttributeGroupDecl(xsAttributeGroupDecl);
        }
        final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
        final XSAttributeGroupDecl globalAttributeGroupDecl = schemaGrammar.getGlobalAttributeGroupDecl(xsAttributeGroupDecl.fName, schemaDocument2SystemId);
        if (globalAttributeGroupDecl == null) {
            schemaGrammar.addGlobalAttributeGroupDecl(xsAttributeGroupDecl, schemaDocument2SystemId);
        }
        if (this.fSchemaHandler.fTolerateDuplicates) {
            if (globalAttributeGroupDecl != null) {
                xsAttributeGroupDecl = globalAttributeGroupDecl;
            }
            this.fSchemaHandler.addGlobalAttributeGroupDecl(xsAttributeGroupDecl);
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return xsAttributeGroupDecl;
    }
}
