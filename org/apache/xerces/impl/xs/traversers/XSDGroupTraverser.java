package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDGroupTraverser extends XSDAbstractParticleTraverser
{
    XSDGroupTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
    }
    
    XSParticleDecl traverseLocal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final QName qName = (QName)checkAttributes[XSAttributeChecker.ATTIDX_REF];
        final XInt xInt = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_MINOCCURS];
        final XInt xInt2 = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_MAXOCCURS];
        XSGroupDecl xsGroupDecl = null;
        if (qName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { "group (local)", "ref" }, element);
        }
        else {
            xsGroupDecl = (XSGroupDecl)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 4, qName, element);
        }
        XSObject xsObject = null;
        Element element2 = DOMUtil.getFirstChildElement(element);
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
        if (element2 != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "group (local)", "(annotation?)", DOMUtil.getLocalName(element) }, element);
        }
        final int intValue = xInt.intValue();
        final int intValue2 = xInt2.intValue();
        XSParticleDecl xsParticleDecl = null;
        if (xsGroupDecl != null && xsGroupDecl.fModelGroup != null && (intValue != 0 || intValue2 != 0)) {
            if (this.fSchemaHandler.fDeclPool != null) {
                xsParticleDecl = this.fSchemaHandler.fDeclPool.getParticleDecl();
            }
            else {
                xsParticleDecl = new XSParticleDecl();
            }
            xsParticleDecl.fType = 3;
            xsParticleDecl.fValue = xsGroupDecl.fModelGroup;
            xsParticleDecl.fMinOccurs = intValue;
            xsParticleDecl.fMaxOccurs = intValue2;
            if (xsGroupDecl.fModelGroup.fCompositor == 103) {
                xsParticleDecl = this.checkOccurrences(xsParticleDecl, SchemaSymbols.ELT_GROUP, (Element)element.getParentNode(), 2, (long)checkAttributes[XSAttributeChecker.ATTIDX_FROMDEFAULT]);
            }
            if (qName != null) {
                XSObjectListImpl empty_LIST;
                if (xsObject != null) {
                    empty_LIST = new XSObjectListImpl();
                    empty_LIST.addXSObject(xsObject);
                }
                else {
                    empty_LIST = XSObjectListImpl.EMPTY_LIST;
                }
                xsParticleDecl.fAnnotations = empty_LIST;
            }
            else {
                xsParticleDecl.fAnnotations = xsGroupDecl.fAnnotations;
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return xsParticleDecl;
    }
    
    XSGroupDecl traverseGlobal(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, true, xsDocumentInfo);
        final String fName = (String)checkAttributes[XSAttributeChecker.ATTIDX_NAME];
        if (fName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { "group (global)", "name" }, element);
        }
        XSGroupDecl xsGroupDecl = new XSGroupDecl();
        XSParticleDecl xsParticleDecl = null;
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject xsObject = null;
        if (element2 == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[] { "group (global)", "(annotation?, (all | choice | sequence))" }, element);
        }
        else {
            String s = element2.getLocalName();
            if (s.equals(SchemaSymbols.ELT_ANNOTATION)) {
                xsObject = this.traverseAnnotationDecl(element2, checkAttributes, true, xsDocumentInfo);
                element2 = DOMUtil.getNextSiblingElement(element2);
                if (element2 != null) {
                    s = element2.getLocalName();
                }
            }
            else {
                final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
                if (syntheticAnnotation != null) {
                    xsObject = this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo);
                }
            }
            if (element2 == null) {
                this.reportSchemaError("s4s-elt-must-match.2", new Object[] { "group (global)", "(annotation?, (all | choice | sequence))" }, element);
            }
            else if (s.equals(SchemaSymbols.ELT_ALL)) {
                xsParticleDecl = this.traverseAll(element2, xsDocumentInfo, schemaGrammar, 4, xsGroupDecl);
            }
            else if (s.equals(SchemaSymbols.ELT_CHOICE)) {
                xsParticleDecl = this.traverseChoice(element2, xsDocumentInfo, schemaGrammar, 4, xsGroupDecl);
            }
            else if (s.equals(SchemaSymbols.ELT_SEQUENCE)) {
                xsParticleDecl = this.traverseSequence(element2, xsDocumentInfo, schemaGrammar, 4, xsGroupDecl);
            }
            else {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "group (global)", "(annotation?, (all | choice | sequence))", DOMUtil.getLocalName(element2) }, element2);
            }
            if (element2 != null && DOMUtil.getNextSiblingElement(element2) != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "group (global)", "(annotation?, (all | choice | sequence))", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(element2)) }, DOMUtil.getNextSiblingElement(element2));
            }
        }
        if (fName != null) {
            xsGroupDecl.fName = fName;
            xsGroupDecl.fTargetNamespace = xsDocumentInfo.fTargetNamespace;
            if (xsParticleDecl == null) {
                xsParticleDecl = XSConstraints.getEmptySequence();
            }
            xsGroupDecl.fModelGroup = (XSModelGroupImpl)xsParticleDecl.fValue;
            XSObjectListImpl empty_LIST;
            if (xsObject != null) {
                empty_LIST = new XSObjectListImpl();
                empty_LIST.addXSObject(xsObject);
            }
            else {
                empty_LIST = XSObjectListImpl.EMPTY_LIST;
            }
            xsGroupDecl.fAnnotations = empty_LIST;
            if (schemaGrammar.getGlobalGroupDecl(xsGroupDecl.fName) == null || DOMUtil.getLocalName(DOMUtil.getParent(element)).equals(SchemaSymbols.ELT_REDEFINE)) {
                schemaGrammar.addGlobalGroupDecl(xsGroupDecl);
            }
            final String schemaDocument2SystemId = this.fSchemaHandler.schemaDocument2SystemId(xsDocumentInfo);
            final XSGroupDecl globalGroupDecl = schemaGrammar.getGlobalGroupDecl(xsGroupDecl.fName, schemaDocument2SystemId);
            if (globalGroupDecl == null) {
                schemaGrammar.addGlobalGroupDecl(xsGroupDecl, schemaDocument2SystemId);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (globalGroupDecl != null) {
                    xsGroupDecl = globalGroupDecl;
                }
                this.fSchemaHandler.addGlobalGroupDecl(xsGroupDecl);
            }
        }
        else {
            xsGroupDecl = null;
        }
        if (xsGroupDecl != null) {
            final Object grpOrAttrGrpRedefinedByRestriction = this.fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(4, new QName(XMLSymbols.EMPTY_STRING, fName, fName, xsDocumentInfo.fTargetNamespace), xsDocumentInfo, element);
            if (grpOrAttrGrpRedefinedByRestriction != null) {
                schemaGrammar.addRedefinedGroupDecl(xsGroupDecl, (XSGroupDecl)grpOrAttrGrpRedefinedByRestriction, this.fSchemaHandler.element2Locator(element));
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return xsGroupDecl;
    }
}
