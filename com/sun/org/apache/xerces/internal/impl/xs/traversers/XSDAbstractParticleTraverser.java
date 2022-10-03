package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import org.w3c.dom.Node;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import org.w3c.dom.Element;

abstract class XSDAbstractParticleTraverser extends XSDAbstractTraverser
{
    ParticleArray fPArray;
    
    XSDAbstractParticleTraverser(final XSDHandler handler, final XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
        this.fPArray = new ParticleArray();
    }
    
    XSParticleDecl traverseAll(final Element allDecl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final int allContextFlags, final XSObject parent) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(allDecl, false, schemaDoc);
        Element child = DOMUtil.getFirstChildElement(allDecl);
        XSAnnotationImpl annotation = null;
        if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            child = DOMUtil.getNextSiblingElement(child);
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(allDecl);
            if (text != null) {
                annotation = this.traverseSyntheticAnnotation(allDecl, text, attrValues, false, schemaDoc);
            }
        }
        String childName = null;
        this.fPArray.pushContext();
        while (child != null) {
            XSParticleDecl particle = null;
            childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ELEMENT)) {
                particle = this.fSchemaHandler.fElementTraverser.traverseLocal(child, schemaDoc, grammar, 1, parent);
            }
            else {
                final Object[] args = { "all", "(annotation?, element*)", DOMUtil.getLocalName(child) };
                this.reportSchemaError("s4s-elt-must-match.1", args, child);
            }
            if (particle != null) {
                this.fPArray.addParticle(particle);
            }
            child = DOMUtil.getNextSiblingElement(child);
        }
        XSParticleDecl particle = null;
        final XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
        final XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
        final Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];
        final XSModelGroupImpl group = new XSModelGroupImpl();
        group.fCompositor = 103;
        group.fParticleCount = this.fPArray.getParticleCount();
        group.fParticles = this.fPArray.popContext();
        XSObjectList annotations;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject(annotation);
        }
        else {
            annotations = XSObjectListImpl.EMPTY_LIST;
        }
        group.fAnnotations = annotations;
        particle = new XSParticleDecl();
        particle.fType = 3;
        particle.fMinOccurs = minAtt.intValue();
        particle.fMaxOccurs = maxAtt.intValue();
        particle.fValue = group;
        particle.fAnnotations = annotations;
        particle = this.checkOccurrences(particle, SchemaSymbols.ELT_ALL, (Element)allDecl.getParentNode(), allContextFlags, defaultVals);
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return particle;
    }
    
    XSParticleDecl traverseSequence(final Element seqDecl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final int allContextFlags, final XSObject parent) {
        return this.traverseSeqChoice(seqDecl, schemaDoc, grammar, allContextFlags, false, parent);
    }
    
    XSParticleDecl traverseChoice(final Element choiceDecl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final int allContextFlags, final XSObject parent) {
        return this.traverseSeqChoice(choiceDecl, schemaDoc, grammar, allContextFlags, true, parent);
    }
    
    private XSParticleDecl traverseSeqChoice(final Element decl, final XSDocumentInfo schemaDoc, final SchemaGrammar grammar, final int allContextFlags, final boolean choice, final XSObject parent) {
        final Object[] attrValues = this.fAttrChecker.checkAttributes(decl, false, schemaDoc);
        Element child = DOMUtil.getFirstChildElement(decl);
        XSAnnotationImpl annotation = null;
        if (child != null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = this.traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            child = DOMUtil.getNextSiblingElement(child);
        }
        else {
            final String text = DOMUtil.getSyntheticAnnotation(decl);
            if (text != null) {
                annotation = this.traverseSyntheticAnnotation(decl, text, attrValues, false, schemaDoc);
            }
        }
        String childName = null;
        this.fPArray.pushContext();
        while (child != null) {
            XSParticleDecl particle = null;
            childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ELEMENT)) {
                particle = this.fSchemaHandler.fElementTraverser.traverseLocal(child, schemaDoc, grammar, 0, parent);
            }
            else if (childName.equals(SchemaSymbols.ELT_GROUP)) {
                particle = this.fSchemaHandler.fGroupTraverser.traverseLocal(child, schemaDoc, grammar);
                if (this.hasAllContent(particle)) {
                    particle = null;
                    this.reportSchemaError("cos-all-limited.1.2", null, child);
                }
            }
            else if (childName.equals(SchemaSymbols.ELT_CHOICE)) {
                particle = this.traverseChoice(child, schemaDoc, grammar, 0, parent);
            }
            else if (childName.equals(SchemaSymbols.ELT_SEQUENCE)) {
                particle = this.traverseSequence(child, schemaDoc, grammar, 0, parent);
            }
            else if (childName.equals(SchemaSymbols.ELT_ANY)) {
                particle = this.fSchemaHandler.fWildCardTraverser.traverseAny(child, schemaDoc, grammar);
            }
            else {
                Object[] args;
                if (choice) {
                    args = new Object[] { "choice", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(child) };
                }
                else {
                    args = new Object[] { "sequence", "(annotation?, (element | group | choice | sequence | any)*)", DOMUtil.getLocalName(child) };
                }
                this.reportSchemaError("s4s-elt-must-match.1", args, child);
            }
            if (particle != null) {
                this.fPArray.addParticle(particle);
            }
            child = DOMUtil.getNextSiblingElement(child);
        }
        XSParticleDecl particle = null;
        final XInt minAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS];
        final XInt maxAtt = (XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS];
        final Long defaultVals = (Long)attrValues[XSAttributeChecker.ATTIDX_FROMDEFAULT];
        final XSModelGroupImpl group = new XSModelGroupImpl();
        group.fCompositor = (short)(choice ? 101 : 102);
        group.fParticleCount = this.fPArray.getParticleCount();
        group.fParticles = this.fPArray.popContext();
        XSObjectList annotations;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject(annotation);
        }
        else {
            annotations = XSObjectListImpl.EMPTY_LIST;
        }
        group.fAnnotations = annotations;
        particle = new XSParticleDecl();
        particle.fType = 3;
        particle.fMinOccurs = minAtt.intValue();
        particle.fMaxOccurs = maxAtt.intValue();
        particle.fValue = group;
        particle.fAnnotations = annotations;
        particle = this.checkOccurrences(particle, choice ? SchemaSymbols.ELT_CHOICE : SchemaSymbols.ELT_SEQUENCE, (Element)decl.getParentNode(), allContextFlags, defaultVals);
        this.fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return particle;
    }
    
    protected boolean hasAllContent(final XSParticleDecl particle) {
        return particle != null && particle.fType == 3 && ((XSModelGroupImpl)particle.fValue).fCompositor == 103;
    }
    
    protected static class ParticleArray
    {
        XSParticleDecl[] fParticles;
        int[] fPos;
        int fContextCount;
        
        protected ParticleArray() {
            this.fParticles = new XSParticleDecl[10];
            this.fPos = new int[5];
            this.fContextCount = 0;
        }
        
        void pushContext() {
            ++this.fContextCount;
            if (this.fContextCount == this.fPos.length) {
                final int newSize = this.fContextCount * 2;
                final int[] newArray = new int[newSize];
                System.arraycopy(this.fPos, 0, newArray, 0, this.fContextCount);
                this.fPos = newArray;
            }
            this.fPos[this.fContextCount] = this.fPos[this.fContextCount - 1];
        }
        
        int getParticleCount() {
            return this.fPos[this.fContextCount] - this.fPos[this.fContextCount - 1];
        }
        
        void addParticle(final XSParticleDecl particle) {
            if (this.fPos[this.fContextCount] == this.fParticles.length) {
                final int newSize = this.fPos[this.fContextCount] * 2;
                final XSParticleDecl[] newArray = new XSParticleDecl[newSize];
                System.arraycopy(this.fParticles, 0, newArray, 0, this.fPos[this.fContextCount]);
                this.fParticles = newArray;
            }
            this.fParticles[this.fPos[this.fContextCount]++] = particle;
        }
        
        XSParticleDecl[] popContext() {
            final int count = this.fPos[this.fContextCount] - this.fPos[this.fContextCount - 1];
            XSParticleDecl[] array = null;
            if (count != 0) {
                array = new XSParticleDecl[count];
                System.arraycopy(this.fParticles, this.fPos[this.fContextCount - 1], array, 0, count);
                for (int i = this.fPos[this.fContextCount - 1]; i < this.fPos[this.fContextCount]; ++i) {
                    this.fParticles[i] = null;
                }
            }
            --this.fContextCount;
            return array;
        }
    }
}
