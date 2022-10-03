package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

abstract class XSDAbstractParticleTraverser extends XSDAbstractTraverser
{
    ParticleArray fPArray;
    
    XSDAbstractParticleTraverser(final XSDHandler xsdHandler, final XSAttributeChecker xsAttributeChecker) {
        super(xsdHandler, xsAttributeChecker);
        this.fPArray = new ParticleArray();
    }
    
    XSParticleDecl traverseAll(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final int n, final XSObject xsObject) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject xsObject2 = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xsObject2 = this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                xsObject2 = this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo);
            }
        }
        this.fPArray.pushContext();
        while (element2 != null) {
            final String localName = DOMUtil.getLocalName(element2);
            if (localName.equals(SchemaSymbols.ELT_ELEMENT)) {
                final XSParticleDecl traverseLocal = this.fSchemaHandler.fElementTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar, 1, xsObject);
                if (traverseLocal != null) {
                    this.fPArray.addParticle(traverseLocal);
                }
            }
            else if (this.fSchemaHandler.fSchemaVersion == 4) {
                if (localName.equals(SchemaSymbols.ELT_ANY)) {
                    final XSParticleDecl traverseAny = this.fSchemaHandler.fWildCardTraverser.traverseAny(element2, xsDocumentInfo, schemaGrammar);
                    if (traverseAny != null) {
                        this.fPArray.addParticle(traverseAny);
                    }
                }
                else if (localName.equals(SchemaSymbols.ELT_GROUP)) {
                    final Object[] checkAttributes2 = this.fAttrChecker.checkAttributes(element2, false, xsDocumentInfo);
                    final XInt xInt = (XInt)checkAttributes2[XSAttributeChecker.ATTIDX_MINOCCURS];
                    final XInt xInt2 = (XInt)checkAttributes2[XSAttributeChecker.ATTIDX_MAXOCCURS];
                    if (xInt.intValue() != 1 || xInt2.intValue() != 1) {
                        this.reportSchemaError("cos-all-limited.1.3", null, element2);
                    }
                    final XSParticleDecl traverseLocal2 = this.fSchemaHandler.fGroupTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar);
                    if (traverseLocal2 != null) {
                        this.expandGroupParticleForCompositorAll(traverseLocal2, element2);
                    }
                }
                else {
                    this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "all", "(annotation?, (element | any | group)*)", DOMUtil.getLocalName(element2) }, element2);
                }
            }
            else {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[] { "all", "(annotation?, element*)", DOMUtil.getLocalName(element2) }, element2);
            }
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        final XInt xInt3 = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_MINOCCURS];
        final XInt xInt4 = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_MAXOCCURS];
        final Long n2 = (Long)checkAttributes[XSAttributeChecker.ATTIDX_FROMDEFAULT];
        final XSModelGroupImpl fValue = new XSModelGroupImpl();
        fValue.fCompositor = 103;
        fValue.fParticleCount = this.fPArray.getParticleCount();
        fValue.fParticles = this.fPArray.popContext();
        XSObjectListImpl empty_LIST;
        if (xsObject2 != null) {
            empty_LIST = new XSObjectListImpl();
            empty_LIST.addXSObject(xsObject2);
        }
        else {
            empty_LIST = XSObjectListImpl.EMPTY_LIST;
        }
        fValue.fAnnotations = empty_LIST;
        final XSParticleDecl xsParticleDecl = new XSParticleDecl();
        xsParticleDecl.fType = 3;
        xsParticleDecl.fMinOccurs = xInt3.intValue();
        xsParticleDecl.fMaxOccurs = xInt4.intValue();
        xsParticleDecl.fValue = fValue;
        xsParticleDecl.fAnnotations = empty_LIST;
        final XSParticleDecl checkOccurrences = this.checkOccurrences(xsParticleDecl, SchemaSymbols.ELT_ALL, (Element)element.getParentNode(), n, n2);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return checkOccurrences;
    }
    
    private void expandGroupParticleForCompositorAll(final XSParticleDecl xsParticleDecl, final Element element) {
        final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
        if (xsModelGroupImpl.getCompositor() == 3) {
            final XSParticleDecl[] fParticles = xsModelGroupImpl.fParticles;
            for (int i = 0; i < xsModelGroupImpl.fParticleCount; ++i) {
                final short fType = fParticles[i].fType;
                if (fType == 1 || fType == 2) {
                    this.fPArray.addParticle(fParticles[i]);
                }
                else {
                    this.expandGroupParticleForCompositorAll(fParticles[i], element);
                }
            }
        }
        else {
            this.reportSchemaError("cos-all-limited.2-xs11", new Object[] { (xsModelGroupImpl.getCompositor() == 1) ? ("xs:" + SchemaSymbols.ELT_SEQUENCE) : ("xs:" + SchemaSymbols.ELT_CHOICE) }, element);
        }
    }
    
    XSParticleDecl traverseSequence(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final int n, final XSObject xsObject) {
        return this.traverseSeqChoice(element, xsDocumentInfo, schemaGrammar, n, false, xsObject);
    }
    
    XSParticleDecl traverseChoice(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final int n, final XSObject xsObject) {
        return this.traverseSeqChoice(element, xsDocumentInfo, schemaGrammar, n, true, xsObject);
    }
    
    private XSParticleDecl traverseSeqChoice(final Element element, final XSDocumentInfo xsDocumentInfo, final SchemaGrammar schemaGrammar, final int n, final boolean b, final XSObject xsObject) {
        final Object[] checkAttributes = this.fAttrChecker.checkAttributes(element, false, xsDocumentInfo);
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject xsObject2 = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xsObject2 = this.traverseAnnotationDecl(element2, checkAttributes, false, xsDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        else {
            final String syntheticAnnotation = DOMUtil.getSyntheticAnnotation(element);
            if (syntheticAnnotation != null) {
                xsObject2 = this.traverseSyntheticAnnotation(element, syntheticAnnotation, checkAttributes, false, xsDocumentInfo);
            }
        }
        this.fPArray.pushContext();
        while (element2 != null) {
            XSParticleDecl xsParticleDecl = null;
            final String localName = DOMUtil.getLocalName(element2);
            if (localName.equals(SchemaSymbols.ELT_ELEMENT)) {
                xsParticleDecl = this.fSchemaHandler.fElementTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar, 0, xsObject);
            }
            else if (localName.equals(SchemaSymbols.ELT_GROUP)) {
                xsParticleDecl = this.fSchemaHandler.fGroupTraverser.traverseLocal(element2, xsDocumentInfo, schemaGrammar);
                if (this.hasAllContent(xsParticleDecl)) {
                    xsParticleDecl = null;
                    this.reportSchemaError("cos-all-limited.1.2", null, element2);
                }
            }
            else if (localName.equals(SchemaSymbols.ELT_CHOICE)) {
                xsParticleDecl = this.traverseChoice(element2, xsDocumentInfo, schemaGrammar, 0, xsObject);
            }
            else if (localName.equals(SchemaSymbols.ELT_SEQUENCE)) {
                xsParticleDecl = this.traverseSequence(element2, xsDocumentInfo, schemaGrammar, 0, xsObject);
            }
            else if (localName.equals(SchemaSymbols.ELT_ANY)) {
                xsParticleDecl = this.fSchemaHandler.fWildCardTraverser.traverseAny(element2, xsDocumentInfo, schemaGrammar);
            }
            else {
                Object[] array;
                if (b) {
                    array = new Object[] { "choice", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(element2) };
                }
                else {
                    array = new Object[] { "sequence", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(element2) };
                }
                this.reportSchemaError("s4s-elt-must-match.1", array, element2);
            }
            if (xsParticleDecl != null) {
                this.fPArray.addParticle(xsParticleDecl);
            }
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        final XInt xInt = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_MINOCCURS];
        final XInt xInt2 = (XInt)checkAttributes[XSAttributeChecker.ATTIDX_MAXOCCURS];
        final Long n2 = (Long)checkAttributes[XSAttributeChecker.ATTIDX_FROMDEFAULT];
        final XSModelGroupImpl fValue = new XSModelGroupImpl();
        fValue.fCompositor = (short)(b ? 101 : 102);
        fValue.fParticleCount = this.fPArray.getParticleCount();
        fValue.fParticles = this.fPArray.popContext();
        XSObjectListImpl empty_LIST;
        if (xsObject2 != null) {
            empty_LIST = new XSObjectListImpl();
            empty_LIST.addXSObject(xsObject2);
        }
        else {
            empty_LIST = XSObjectListImpl.EMPTY_LIST;
        }
        fValue.fAnnotations = empty_LIST;
        final XSParticleDecl xsParticleDecl2 = new XSParticleDecl();
        xsParticleDecl2.fType = 3;
        xsParticleDecl2.fMinOccurs = xInt.intValue();
        xsParticleDecl2.fMaxOccurs = xInt2.intValue();
        xsParticleDecl2.fValue = fValue;
        xsParticleDecl2.fAnnotations = empty_LIST;
        final XSParticleDecl checkOccurrences = this.checkOccurrences(xsParticleDecl2, b ? SchemaSymbols.ELT_CHOICE : SchemaSymbols.ELT_SEQUENCE, (Element)element.getParentNode(), n, n2);
        this.fAttrChecker.returnAttrArray(checkAttributes, xsDocumentInfo);
        return checkOccurrences;
    }
    
    protected boolean hasAllContent(final XSParticleDecl xsParticleDecl) {
        return xsParticleDecl != null && xsParticleDecl.fType == 3 && ((XSModelGroupImpl)xsParticleDecl.fValue).fCompositor == 103;
    }
    
    static class ParticleArray
    {
        XSParticleDecl[] fParticles;
        int[] fPos;
        int fContextCount;
        
        ParticleArray() {
            this.fParticles = new XSParticleDecl[10];
            this.fPos = new int[5];
            this.fContextCount = 0;
        }
        
        void pushContext() {
            ++this.fContextCount;
            if (this.fContextCount == this.fPos.length) {
                final int[] fPos = new int[this.fContextCount * 2];
                System.arraycopy(this.fPos, 0, fPos, 0, this.fContextCount);
                this.fPos = fPos;
            }
            this.fPos[this.fContextCount] = this.fPos[this.fContextCount - 1];
        }
        
        int getParticleCount() {
            return this.fPos[this.fContextCount] - this.fPos[this.fContextCount - 1];
        }
        
        void addParticle(final XSParticleDecl xsParticleDecl) {
            if (this.fPos[this.fContextCount] == this.fParticles.length) {
                final XSParticleDecl[] fParticles = new XSParticleDecl[this.fPos[this.fContextCount] * 2];
                System.arraycopy(this.fParticles, 0, fParticles, 0, this.fPos[this.fContextCount]);
                this.fParticles = fParticles;
            }
            this.fParticles[this.fPos[this.fContextCount]++] = xsParticleDecl;
        }
        
        XSParticleDecl[] popContext() {
            final int n = this.fPos[this.fContextCount] - this.fPos[this.fContextCount - 1];
            Object o = null;
            if (n != 0) {
                o = new XSParticleDecl[n];
                System.arraycopy(this.fParticles, this.fPos[this.fContextCount - 1], o, 0, n);
                for (int i = this.fPos[this.fContextCount - 1]; i < this.fPos[this.fContextCount]; ++i) {
                    this.fParticles[i] = null;
                }
            }
            --this.fContextCount;
            return (XSParticleDecl[])o;
        }
    }
}
