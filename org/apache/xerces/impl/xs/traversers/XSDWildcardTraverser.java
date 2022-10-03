package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.xni.QName;
import java.util.Vector;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDWildcardTraverser extends XSDAbstractTraverser
{
    XSDWildcardTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
    }
    
    XSParticleDecl traverseAny(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final XSWildcardDecl traverseWildcardDecl = this.traverseWildcardDecl(element, checkAttributes, xsDocumentInfo, schemaGrammar);
        XSParticleDecl particleDecl = null;
        if (traverseWildcardDecl != null) {
            final int intValue = ((XInt)checkAttributes[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
            final int intValue2 = ((XInt)checkAttributes[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
            if (intValue2 != 0) {
                if (this.fSchemaHandler.fDeclPool != null) {
                    particleDecl = this.fSchemaHandler.fDeclPool.getParticleDecl();
                }
                else {
                    particleDecl = new XSParticleDecl();
                }
                particleDecl.fType = 2;
                particleDecl.fValue = traverseWildcardDecl;
                particleDecl.fMinOccurs = intValue;
                particleDecl.fMaxOccurs = intValue2;
                particleDecl.fAnnotations = traverseWildcardDecl.fAnnotations;
            }
        }
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return particleDecl;
    }
    
    XSWildcardDecl traverseAnyAttribute(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        final XSWildcardDecl traverseWildcardDecl = this.traverseWildcardDecl(element, checkAttributes, xsDocumentInfo, schemaGrammar);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return traverseWildcardDecl;
    }
    
    XSWildcardDecl traverseWildcardDecl(final Element element, final Object[] array, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar) {
        final XSWildcardDecl xsWildcardDecl = new XSWildcardDecl();
        final XInt xInt = (XInt)array[XSAttributeChecker.ATTIDX_NAMESPACE];
        xsWildcardDecl.fType = (short)((xInt != null) ? xInt.shortValue() : 1);
        xsWildcardDecl.fNamespaceList = (String[])array[XSAttributeChecker.ATTIDX_NAMESPACE_LIST];
        xsWildcardDecl.fProcessContents = ((XInt)array[XSAttributeChecker.ATTIDX_PROCESSCONTENTS]).shortValue();
        if (this.fSchemaHandler.fSchemaVersion == 4) {
            this.processExtraAttributes(element, array, xsWildcardDecl);
        }
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject xsObject = null;
        if (element2 != null) {
            if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                xsObject = this.traverseAnnotationDecl(element2, array, false, xsDocumentInfo);
                element2 = DOMUtil.getNextSiblingElement(element2);
            }
            else {
                final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
                if (syntheticAnnotation != null) {
                    xsObject = this.traverseSyntheticAnnotation(element, syntheticAnnotation, array, false, xsDocumentInfo);
                }
            }
            if (element2 != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "wildcard", "(annotation?)", DOMUtil.getLocalName(element2) }, element);
            }
        }
        else {
            final String syntheticAnnotation2 = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation2 != null) {
                xsObject = this.traverseSyntheticAnnotation(element, syntheticAnnotation2, array, false, xsDocumentInfo);
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
        xsWildcardDecl.fAnnotations = empty_LIST;
        return xsWildcardDecl;
    }
    
    private void processExtraAttributes(final Element element, final Object[] array, final XSWildcardDecl xsWildcardDecl) {
        final String[] fNamespaceList = (String[])array[XSAttributeChecker.ATTIDX_NOTNAMESPACE];
        if (fNamespaceList != null) {
            if (array[XSAttributeChecker.ATTIDX_NAMESPACE] != null) {
                this.reportSchemaError("src-wildcard.1", null, element);
            }
            else {
                xsWildcardDecl.fType = 2;
                xsWildcardDecl.fNamespaceList = fNamespaceList;
                if (fNamespaceList.length == 0) {
                    this.reportSchemaError("wc-props-correct.2", null, element);
                }
            }
        }
        final Vector vector = (Vector)array[XSAttributeChecker.ATTIDX_NOTQNAME];
        if (vector != null && vector.size() > 0) {
            xsWildcardDecl.fDisallowedNamesList = (QName[])vector.get(0);
            xsWildcardDecl.fDisallowedDefined = (boolean)vector.get(1);
            xsWildcardDecl.fDisallowedSibling = (boolean)vector.get(2);
            for (int i = 0; i < xsWildcardDecl.fDisallowedNamesList.length; ++i) {
                final QName qName = xsWildcardDecl.fDisallowedNamesList[i];
                if (!xsWildcardDecl.allowNamespace(qName.uri)) {
                    this.reportSchemaError("wc-props-correct.4", new Object[] { qName.uri, qName.localpart }, element);
                }
            }
        }
    }
}
