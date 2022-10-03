package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

class XSDWildcardTraverser extends XSDAbstractTraverser
{
    XSDWildcardTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }
    
    XSParticleDecl traverseAny(final Element elmNode, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        final XSWildcardDecl wildcard = this.traverseWildcardDecl(elmNode, attrValues, schemaDoc, grammar);
        XSParticleDecl particle = null;
        if (wildcard != null) {
            final int min = ((XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
            final int max = ((XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
            if (max != 0) {
                if (this.fSchemaHandler.fDeclPool != null) {
                    particle = this.fSchemaHandler.fDeclPool.getParticleDecl();
                }
                else {
                    particle = new XSParticleDecl();
                }
                particle.fType = 2;
                particle.fValue = wildcard;
                particle.fMinOccurs = min;
                particle.fMaxOccurs = max;
                particle.fAnnotations = wildcard.fAnnotations;
            }
        }
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return particle;
    }
    
    XSWildcardDecl traverseAnyAttribute(final Element elmNode, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        final XSWildcardDecl wildcard = this.traverseWildcardDecl(elmNode, attrValues, schemaDoc, grammar);
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return wildcard;
    }
    
    XSWildcardDecl traverseWildcardDecl(final Element elmNode, final Object[] attrValues, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar) {
        final XSWildcardDecl wildcard = new XSWildcardDecl();
        final XInt namespaceTypeAttr = (XInt)attrValues[XSAttributeChecker.ATTIDX_NAMESPACE];
        wildcard.fType = namespaceTypeAttr.shortValue();
        wildcard.fNamespaceList = (String[])attrValues[XSAttributeChecker.ATTIDX_NAMESPACE_LIST];
        final XInt processContentsAttr = (XInt)attrValues[XSAttributeChecker.ATTIDX_PROCESSCONTENTS];
        wildcard.fProcessContents = processContentsAttr.shortValue();
        Element child = DOMUtil.getFirstChildElement(elmNode);
        XSAnnotationImpl annotation = null;
        if (child != null) {
            if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            }
            else {
                final String text = DOMUtil.getSyntheticAnnotation(elmNode);
                if (text != null) {
                    annotation = this.traverseSyntheticAnnotation(elmNode, text, attrValues, false, schemaDoc);
                }
            }
            if (child != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "wildcard", "(annotation?)", DOMUtil.getLocalName(child) }, elmNode);
            }
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(elmNode);
            if (text != null) {
                annotation = this.traverseSyntheticAnnotation(elmNode, text, attrValues, false, schemaDoc);
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
        wildcard.fAnnotations = annotations;
        return wildcard;
    }
}
