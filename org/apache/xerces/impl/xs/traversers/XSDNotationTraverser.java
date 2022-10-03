package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSNotationDecl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDNotationTraverser extends XSDAbstractTraverser
{
    XSDNotationTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
    }
    
    XSNotationDecl traverse(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, true, xsDocumentInfo);
        final String fName = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAME];
        String fPublicId = (String)checkAttributes[XSAttributeChecker.ATTIDX_PUBLIC];
        final String fSystemId = (String)checkAttributes[XSAttributeChecker.ATTIDX_SYSTEM];
        if (fName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_NOTATION, SchemaSymbols.ATT_NAME }, element);
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return null;
        }
        if (fSystemId == null && fPublicId == null) {
            this.reportSchemaError("PublicSystemOnNotation", null, element);
            fPublicId = "missing";
        }
        XSNotationDecl xsNotationDecl = new XSNotationDecl();
        xsNotationDecl.fName = fName;
        xsNotationDecl.fTargetNamespace = xsDocumentInfo.fTargetNamespace;
        xsNotationDecl.fPublicId = fPublicId;
        xsNotationDecl.fSystemId = fSystemId;
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
        XSObjectListImpl empty_LIST;
        if (xsObject != null) {
            empty_LIST = new XSObjectListImpl();
            empty_LIST.addXSObject(xsObject);
        }
        else {
            empty_LIST = XSObjectListImpl.EMPTY_LIST;
        }
        xsNotationDecl.fAnnotations = empty_LIST;
        if (element2 != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_NOTATION, "(annotation?)", DOMUtil.getLocalName(element2) }, element2);
        }
        if (schemaGrammar.getGlobalNotationDecl(xsNotationDecl.fName) == null) {
            schemaGrammar.addGlobalNotationDecl(xsNotationDecl);
        }
        final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
        final XSNotationDecl globalNotationDecl = schemaGrammar.getGlobalNotationDecl(xsNotationDecl.fName, schemaDocument2SystemId);
        if (globalNotationDecl == null) {
            schemaGrammar.addGlobalNotationDecl(xsNotationDecl, schemaDocument2SystemId);
        }
        if (this.fSchemaHandler.fTolerateDuplicates) {
            if (globalNotationDecl != null) {
                xsNotationDecl = globalNotationDecl;
            }
            this.fSchemaHandler.addGlobalNotationDecl(xsNotationDecl);
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return xsNotationDecl;
    }
}
