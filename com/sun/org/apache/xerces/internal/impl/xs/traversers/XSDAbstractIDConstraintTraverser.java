package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.identity.Field;
import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;

class XSDAbstractIDConstraintTraverser extends XSDAbstractTraverser
{
    public XSDAbstractIDConstraintTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }
    
    boolean traverseIdentityConstraint(final IdentityConstraint ic, final Element icElem, final XSDocumentInfo schemaDoc, final Object[] icElemAttrs) {
        Element sElem = DOMUtil.getFirstChildElement(icElem);
        if (sElem == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, icElem);
            return false;
        }
        if (DOMUtil.getLocalName(sElem).equals(SchemaSymbols.ELT_ANNOTATION)) {
            ic.addAnnotation(this.traverseAnnotationDecl(sElem, icElemAttrs, false, schemaDoc));
            sElem = DOMUtil.getNextSiblingElement(sElem);
            if (sElem == null) {
                this.reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, icElem);
                return false;
            }
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(icElem);
            if (text != null) {
                ic.addAnnotation(this.traverseSyntheticAnnotation(icElem, text, icElemAttrs, false, schemaDoc));
            }
        }
        if (!DOMUtil.getLocalName(sElem).equals(SchemaSymbols.ELT_SELECTOR)) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_SELECTOR }, sElem);
            return false;
        }
        Object[] attrValues = this.fAttrChecker.checkAttributes(sElem, false, schemaDoc);
        Element selChild = DOMUtil.getFirstChildElement(sElem);
        if (selChild != null) {
            if (DOMUtil.getLocalName(selChild).equals(SchemaSymbols.ELT_ANNOTATION)) {
                ic.addAnnotation(this.traverseAnnotationDecl(selChild, attrValues, false, schemaDoc));
                selChild = DOMUtil.getNextSiblingElement(selChild);
            }
            else {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(selChild) }, selChild);
            }
            if (selChild != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(selChild) }, selChild);
            }
        }
        else {
            final String text2 = DOMUtil.getSyntheticAnnotation(sElem);
            if (text2 != null) {
                ic.addAnnotation(this.traverseSyntheticAnnotation(icElem, text2, attrValues, false, schemaDoc));
            }
        }
        String sText = (String)attrValues[XSAttributeChecker.ATTIDX_XPATH];
        if (sText == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_SELECTOR, SchemaSymbols.ATT_XPATH }, sElem);
            return false;
        }
        sText = XMLChar.trim(sText);
        Selector.XPath sXpath = null;
        try {
            sXpath = new Selector.XPath(sText, this.fSymbolTable, schemaDoc.fNamespaceSupport);
            final Selector selector = new Selector(sXpath, ic);
            ic.setSelector(selector);
        }
        catch (final XPathException e) {
            this.reportSchemaError(e.getKey(), new Object[] { sText }, sElem);
            this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return false;
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        Element fElem = DOMUtil.getNextSiblingElement(sElem);
        if (fElem == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, sElem);
            return false;
        }
        while (fElem != null) {
            if (!DOMUtil.getLocalName(fElem).equals(SchemaSymbols.ELT_FIELD)) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_FIELD }, fElem);
                fElem = DOMUtil.getNextSiblingElement(fElem);
            }
            else {
                attrValues = this.fAttrChecker.checkAttributes(fElem, false, schemaDoc);
                Element fieldChild = DOMUtil.getFirstChildElement(fElem);
                if (fieldChild != null && DOMUtil.getLocalName(fieldChild).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    ic.addAnnotation(this.traverseAnnotationDecl(fieldChild, attrValues, false, schemaDoc));
                    fieldChild = DOMUtil.getNextSiblingElement(fieldChild);
                }
                if (fieldChild != null) {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_FIELD, "(annotation?)", DOMUtil.getLocalName(fieldChild) }, fieldChild);
                }
                else {
                    final String text3 = DOMUtil.getSyntheticAnnotation(fElem);
                    if (text3 != null) {
                        ic.addAnnotation(this.traverseSyntheticAnnotation(icElem, text3, attrValues, false, schemaDoc));
                    }
                }
                String fText = (String)attrValues[XSAttributeChecker.ATTIDX_XPATH];
                if (fText == null) {
                    this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_FIELD, SchemaSymbols.ATT_XPATH }, fElem);
                    this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
                    return false;
                }
                fText = XMLChar.trim(fText);
                try {
                    final Field.XPath fXpath = new Field.XPath(fText, this.fSymbolTable, schemaDoc.fNamespaceSupport);
                    final Field field = new Field(fXpath, ic);
                    ic.addField(field);
                }
                catch (final XPathException e2) {
                    this.reportSchemaError(e2.getKey(), new Object[] { fText }, fElem);
                    this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
                    return false;
                }
                fElem = DOMUtil.getNextSiblingElement(fElem);
                this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            }
        }
        return ic.getFieldCount() > 0;
    }
}
