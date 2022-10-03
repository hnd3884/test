package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.identity.Field;
import org.apache.xerces.impl.xpath.XPathException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.xs.identity.Selector;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.w3c.dom.Element;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;

class XSDAbstractIDConstraintTraverser extends XSDAbstractTraverser
{
    public XSDAbstractIDConstraintTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
    }
    
    boolean traverseIdentityConstraint(final IdentityConstraint identityConstraint, final Element element, final XSDocumentInfo xsDocumentInfo, final Object[] array) {
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, element);
            return false;
        }
        if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            identityConstraint.addAnnotation(this.traverseAnnotationDecl(element2, array, false, xsDocumentInfo));
            element2 = DOMUtil.getNextSiblingElement(element2);
            if (element2 == null) {
                this.reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, element);
                return false;
            }
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                identityConstraint.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation, array, false, xsDocumentInfo));
            }
        }
        if (!DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_SELECTOR)) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_SELECTOR }, element2);
            return false;
        }
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element2, false, xsDocumentInfo);
        Element element3 = DOMUtil.getFirstChildElement(element2);
        if (element3 != null) {
            if (DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_ANNOTATION)) {
                identityConstraint.addAnnotation(this.traverseAnnotationDecl(element3, checkAttributes, false, xsDocumentInfo));
                element3 = DOMUtil.getNextSiblingElement(element3);
            }
            else {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(element3) }, element3);
            }
            if (element3 != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(element3) }, element3);
            }
        }
        else {
            final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element2);
            if (syntheticAnnotation2 != null) {
                identityConstraint.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation2, checkAttributes, false, xsDocumentInfo));
            }
        }
        final String s = (String)checkAttributes[XSAttributeChecker.ATTIDX_XPATH];
        if (s == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_SELECTOR, SchemaSymbols.ATT_XPATH }, element2);
            return false;
        }
        final String trim = XMLChar.trim(s);
        try {
            identityConstraint.setSelector(new Selector(new Selector.XPath(trim, this.fSymbolTable, xsDocumentInfo.fNamespaceSupport), identityConstraint, this.getXPathDefaultNamespace(checkAttributes, xsDocumentInfo)));
        }
        catch (final XPathException ex) {
            this.reportSchemaError(ex.getKey(), new Object[] { trim }, element2);
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return false;
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        Element element4 = DOMUtil.getNextSiblingElement(element2);
        if (element4 == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[] { "identity constraint", "(annotation?, selector, field+)" }, element2);
            return false;
        }
        while (element4 != null) {
            if (!DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_FIELD)) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_FIELD }, element4);
                element4 = DOMUtil.getNextSiblingElement(element4);
            }
            else {
                final Object[] checkAttributes2 = this.fAttrChecker.checkAttributes(element4, false, xsDocumentInfo);
                Element element5 = DOMUtil.getFirstChildElement(element4);
                if (element5 != null && DOMUtil.getLocalName(element5).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    identityConstraint.addAnnotation(this.traverseAnnotationDecl(element5, checkAttributes2, false, xsDocumentInfo));
                    element5 = DOMUtil.getNextSiblingElement(element5);
                }
                if (element5 != null) {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { SchemaSymbols.ELT_FIELD, "(annotation?)", DOMUtil.getLocalName(element5) }, element5);
                }
                else {
                    final String syntheticAnnotation3 = DOMUtil.getSyntheticAnnotation(element4);
                    if (syntheticAnnotation3 != null) {
                        identityConstraint.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation3, checkAttributes2, false, xsDocumentInfo));
                    }
                }
                final String s2 = (String)checkAttributes2[XSAttributeChecker.ATTIDX_XPATH];
                if (s2 == null) {
                    this.reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_FIELD, SchemaSymbols.ATT_XPATH }, element4);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    return false;
                }
                final String trim2 = XMLChar.trim(s2);
                try {
                    identityConstraint.addField(new Field(new Field.XPath(trim2, this.fSymbolTable, xsDocumentInfo.fNamespaceSupport), identityConstraint, this.getXPathDefaultNamespace(checkAttributes2, xsDocumentInfo)));
                }
                catch (final XPathException ex2) {
                    this.reportSchemaError(ex2.getKey(), new Object[] { trim2 }, element4);
                    this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
                    return false;
                }
                element4 = DOMUtil.getNextSiblingElement(element4);
                this.fAttrChecker.returnAttrArray(checkAttributes2, xsDocumentInfo);
            }
        }
        return identityConstraint.getFieldCount() > 0;
    }
    
    private String getXPathDefaultNamespace(final Object[] array, final XSDocumentInfo xsDocumentInfo) {
        String fXpathDefaultNamespace = (String)array[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS];
        if (fXpathDefaultNamespace == null) {
            fXpathDefaultNamespace = xsDocumentInfo.fXpathDefaultNamespace;
        }
        return fXpathDefaultNamespace;
    }
    
    void traverseIdentityConstraintReferral(final Element element, final XSElementDecl xsElementDecl, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final QName qName = (QName)checkAttributes[XSAttributeChecker.ATTIDX_REF];
        IdentityConstraint identityConstraint = null;
        if (qName != null) {
            identityConstraint = (IdentityConstraint)this.fSchemaHandler.getGlobalDecl(xsDocumentInfo, 5, qName, element);
        }
        if (identityConstraint == null) {
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return;
        }
        final String localName = DOMUtil.getLocalName(element);
        short n;
        if (localName.equals(SchemaSymbols.ELT_UNIQUE)) {
            n = 3;
        }
        else if (localName.equals(SchemaSymbols.ELT_KEY)) {
            n = 1;
        }
        else {
            n = 2;
        }
        if (identityConstraint.getCategory() != n) {
            this.reportSchemaError("src-identity-constraint.5", new Object[] { DOMUtil.getLocalName(element) }, element);
            this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
            return;
        }
        final Element firstChildElement = DOMUtil.getFirstChildElement(element);
        if (firstChildElement != null) {
            if (!DOMUtil.getLocalName(firstChildElement).equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { element.getLocalName(), "(annotation?)", DOMUtil.getLocalName(firstChildElement) }, firstChildElement);
                return;
            }
            xsElementDecl.addAnnotation(this.traverseAnnotationDecl(firstChildElement, checkAttributes, false, xsDocumentInfo));
            final Element nextSiblingElement = DOMUtil.getNextSiblingElement(firstChildElement);
            if (nextSiblingElement != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { element.getLocalName(), "(annotation?)", DOMUtil.getLocalName(nextSiblingElement) }, nextSiblingElement);
                return;
            }
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                xsElementDecl.addAnnotation(this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo));
            }
        }
        schemaGrammar.addIDConstraintDecl(xsElementDecl, identityConstraint);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
    }
}
