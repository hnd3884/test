package org.apache.xerces.impl.xs.models;

import java.util.Vector;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSElementDeclHelper;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSElementDecl;

public class XSAllCM implements XSCMValidator
{
    private static final short STATE_START = 0;
    private static final short STATE_VALID = 1;
    private static final short STATE_CHILD = 1;
    private final XSElementDecl[] fAllElements;
    private final boolean[] fIsOptionalElement;
    private final boolean fHasOptionalContent;
    private int fNumElements;
    private final short fSchemaVersion;
    
    public XSAllCM(final boolean fHasOptionalContent, final int n, final short fSchemaVersion) {
        this.fNumElements = 0;
        this.fHasOptionalContent = fHasOptionalContent;
        this.fAllElements = new XSElementDecl[n];
        this.fIsOptionalElement = new boolean[n];
        this.fSchemaVersion = fSchemaVersion;
    }
    
    public void addElement(final XSElementDecl xsElementDecl, final boolean b) {
        this.fAllElements[this.fNumElements] = xsElementDecl;
        this.fIsOptionalElement[this.fNumElements] = b;
        ++this.fNumElements;
    }
    
    public int[] startContentModel() {
        final int[] array = new int[this.fNumElements + 1];
        for (int i = 0; i <= this.fNumElements; ++i) {
            array[i] = 0;
        }
        return array;
    }
    
    Object findMatchingDecl(final QName qName, final SubstitutionGroupHandler substitutionGroupHandler) {
        Object matchingElemDecl = null;
        for (int i = 0; i < this.fNumElements; ++i) {
            matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fAllElements[i], this.fSchemaVersion);
            if (matchingElemDecl != null) {
                break;
            }
        }
        return matchingElemDecl;
    }
    
    public Object oneTransition(final QName qName, final int[] array, final SubstitutionGroupHandler substitutionGroupHandler, final XSElementDeclHelper xsElementDeclHelper) {
        if (array[0] < 0) {
            array[0] = -2;
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        array[0] = 1;
        for (int i = 0; i < this.fNumElements; ++i) {
            if (array[i + 1] == 0) {
                final XSElementDecl matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fAllElements[i], this.fSchemaVersion);
                if (matchingElemDecl != null) {
                    array[i + 1] = 1;
                    return matchingElemDecl;
                }
            }
        }
        array[0] = -1;
        return this.findMatchingDecl(qName, substitutionGroupHandler);
    }
    
    public boolean endContentModel(final int[] array) {
        final int n = array[0];
        if (n == -1 || n == -2) {
            return false;
        }
        if (this.fHasOptionalContent && n == 0) {
            return true;
        }
        for (int i = 0; i < this.fNumElements; ++i) {
            if (!this.fIsOptionalElement[i] && array[i + 1] == 0) {
                return false;
            }
        }
        return true;
    }
    
    public boolean checkUniqueParticleAttribution(final SubstitutionGroupHandler substitutionGroupHandler, final XSConstraints xsConstraints) throws XMLSchemaException {
        for (int i = 0; i < this.fNumElements; ++i) {
            for (int j = i + 1; j < this.fNumElements; ++j) {
                if (xsConstraints.overlapUPA(this.fAllElements[i], (Object)this.fAllElements[j], substitutionGroupHandler)) {
                    throw new XMLSchemaException("cos-nonambig", new Object[] { this.fAllElements[i].toString(), this.fAllElements[j].toString() });
                }
            }
        }
        return false;
    }
    
    public Vector whatCanGoHere(final int[] array) {
        final Vector vector = new Vector();
        for (int i = 0; i < this.fNumElements; ++i) {
            if (array[i + 1] == 0) {
                vector.addElement(this.fAllElements[i]);
            }
        }
        return vector;
    }
    
    public int[] occurenceInfo(final int[] array) {
        return null;
    }
    
    public String getTermName(final int n) {
        return null;
    }
    
    public boolean isCompactedForUPA() {
        return false;
    }
    
    public XSElementDecl findMatchingElemDecl(final QName qName, final SubstitutionGroupHandler substitutionGroupHandler) {
        for (int i = 1; i < this.fNumElements; ++i) {
            final XSElementDecl matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fAllElements[i], (short)1);
            if (matchingElemDecl != null) {
                return matchingElemDecl;
            }
        }
        return null;
    }
}
