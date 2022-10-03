package org.apache.xerces.impl.xs.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSElementDeclHelper;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSOpenContentDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.XSElementDecl;

public class XS11AllCM implements XSCMValidator, XS11CMRestriction.XS11CM
{
    private static final short STATE_START = 0;
    private static final short STATE_CHILD = 1;
    private static final short STATE_SUFFIX = 2;
    private final boolean fHasOptionalContent;
    private final XSElementDecl[] fElements;
    private final XSWildcardDecl[] fWildcards;
    private final int[] fMinOccurs;
    private final int[] fMaxOccurs;
    private final int fNumElements;
    private final int fNumTotal;
    private final XSOpenContentDecl fOpenContent;
    
    public XS11AllCM(final boolean fHasOptionalContent, final int n, final XSParticleDecl[] array, final XSOpenContentDecl fOpenContent) {
        this.fHasOptionalContent = fHasOptionalContent;
        int fNumElements = 1;
        for (int i = 0; i < n; ++i) {
            if (array[i].fType == 1) {
                ++fNumElements;
            }
        }
        this.fNumElements = fNumElements;
        this.fNumTotal = n + 1;
        if (fNumElements > 1) {
            this.fElements = new XSElementDecl[fNumElements];
        }
        else {
            this.fElements = null;
        }
        if (this.fNumTotal > fNumElements) {
            this.fWildcards = new XSWildcardDecl[this.fNumTotal];
        }
        else {
            this.fWildcards = null;
        }
        if (this.fNumTotal > 1) {
            this.fMinOccurs = new int[this.fNumTotal];
            this.fMaxOccurs = new int[this.fNumTotal];
        }
        else {
            this.fMinOccurs = null;
            this.fMaxOccurs = null;
        }
        int n2 = fNumElements;
        int n3 = 1;
        for (final XSParticleDecl xsParticleDecl : array) {
            if (xsParticleDecl.fType == 1) {
                this.fElements[n3] = (XSElementDecl)xsParticleDecl.fValue;
                this.fMinOccurs[n3] = xsParticleDecl.fMinOccurs;
                this.fMaxOccurs[n3] = xsParticleDecl.fMaxOccurs;
                ++n3;
            }
            else {
                this.fWildcards[n2] = (XSWildcardDecl)xsParticleDecl.fValue;
                this.fMinOccurs[n2] = xsParticleDecl.fMinOccurs;
                this.fMaxOccurs[n2] = xsParticleDecl.fMaxOccurs;
                ++n2;
            }
        }
        this.fOpenContent = fOpenContent;
    }
    
    public int[] startContentModel() {
        final int[] array = new int[this.fNumTotal];
        for (int i = 0; i < this.fNumTotal; ++i) {
            array[i] = 0;
        }
        return array;
    }
    
    Object findMatchingDecl(final QName qName, final SubstitutionGroupHandler substitutionGroupHandler) {
        final XSElementDecl matchingElemDecl = this.findMatchingElemDecl(qName, substitutionGroupHandler);
        if (matchingElemDecl != null) {
            return matchingElemDecl;
        }
        for (int i = this.fNumElements; i < this.fNumTotal; ++i) {
            if (this.fWildcards[i].allowQName(qName)) {
                return this.fWildcards[i];
            }
        }
        return null;
    }
    
    public XSElementDecl findMatchingElemDecl(final QName qName, final SubstitutionGroupHandler substitutionGroupHandler) {
        for (int i = 1; i < this.fNumElements; ++i) {
            final XSElementDecl matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fElements[i], (short)4);
            if (matchingElemDecl != null) {
                return matchingElemDecl;
            }
        }
        return null;
    }
    
    public boolean allowExpandedName(final XSWildcardDecl xsWildcardDecl, final QName qName, final SubstitutionGroupHandler substitutionGroupHandler, final XSElementDeclHelper xsElementDeclHelper) {
        return xsWildcardDecl.allowQName(qName) && (!xsWildcardDecl.fDisallowedSibling || this.findMatchingElemDecl(qName, substitutionGroupHandler) == null) && (!xsWildcardDecl.fDisallowedDefined || xsElementDeclHelper.getGlobalElementDecl(qName) == null);
    }
    
    public Object oneTransition(final QName qName, final int[] array, final SubstitutionGroupHandler substitutionGroupHandler, final XSElementDeclHelper xsElementDeclHelper) {
        if (array[0] < 0) {
            array[0] = -2;
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        if (array[0] != 2) {
            array[0] = 1;
            for (int i = 1; i < this.fNumElements; ++i) {
                if (array[i] != this.fMaxOccurs[i]) {
                    final XSElementDecl matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fElements[i], (short)4);
                    if (matchingElemDecl != null) {
                        final int n = i;
                        ++array[n];
                        return matchingElemDecl;
                    }
                }
            }
            for (int j = this.fNumElements; j < this.fNumTotal; ++j) {
                if (array[j] != this.fMaxOccurs[j]) {
                    if (this.allowExpandedName(this.fWildcards[j], qName, substitutionGroupHandler, xsElementDeclHelper)) {
                        final int n2 = j;
                        ++array[n2];
                        return this.fWildcards[j];
                    }
                }
            }
            if (this.fOpenContent != null) {
                if (this.fOpenContent.fMode == 2) {
                    if (!this.isFinal(array)) {
                        array[0] = -1;
                        return this.findMatchingDecl(qName, substitutionGroupHandler);
                    }
                    array[0] = 2;
                }
                if (this.allowExpandedName(this.fOpenContent.fWildcard, qName, substitutionGroupHandler, xsElementDeclHelper)) {
                    return this.fOpenContent;
                }
            }
            array[0] = -1;
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        if (this.allowExpandedName(this.fOpenContent.fWildcard, qName, substitutionGroupHandler, xsElementDeclHelper)) {
            return this.fOpenContent;
        }
        array[0] = -1;
        return this.findMatchingDecl(qName, substitutionGroupHandler);
    }
    
    public boolean endContentModel(final int[] array) {
        final int n = array[0];
        return n != -1 && n != -2 && this.isFinal(array);
    }
    
    public boolean checkUniqueParticleAttribution(final SubstitutionGroupHandler substitutionGroupHandler, final XSConstraints xsConstraints) throws XMLSchemaException {
        for (int i = 1; i < this.fNumElements; ++i) {
            for (int j = i + 1; j < this.fNumElements; ++j) {
                if (xsConstraints.overlapUPA(this.fElements[i], (Object)this.fElements[j], substitutionGroupHandler)) {
                    throw new XMLSchemaException("cos-nonambig", new Object[] { this.fElements[i].toString(), this.fElements[j].toString() });
                }
            }
        }
        for (int k = this.fNumElements; k < this.fNumTotal; ++k) {
            for (int l = k + 1; l < this.fNumTotal; ++l) {
                if (xsConstraints.overlapUPA(this.fWildcards[k], this.fWildcards[l])) {
                    throw new XMLSchemaException("cos-nonambig", new Object[] { this.fWildcards[k].toString(), this.fWildcards[l].toString() });
                }
            }
        }
        return false;
    }
    
    public Vector whatCanGoHere(final int[] array) {
        final Vector vector = new Vector();
        for (int i = 1; i < this.fNumElements; ++i) {
            if (array[i] == 0 || array[i] < this.fMaxOccurs[i]) {
                vector.addElement(this.fElements[i]);
            }
        }
        if (vector.size() == 0) {
            for (int j = this.fNumElements; j < this.fNumTotal; ++j) {
                if (array[j] == 0 || array[j] < this.fMaxOccurs[j]) {
                    vector.addElement(this.fWildcards[j]);
                }
            }
        }
        if (vector.size() == 0 && this.fOpenContent != null) {
            vector.add(this.fOpenContent);
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
    
    private boolean isFinal(final int[] array) {
        if ((this.fHasOptionalContent && array[0] == 0) || array[0] == 2) {
            return true;
        }
        for (int i = 1; i < this.fNumTotal; ++i) {
            if (array[i] < this.fMinOccurs[i]) {
                return false;
            }
        }
        return true;
    }
    
    public XSElementDecl nextElementTransition(final int[] array, final int[] array2, final int[] array3) {
        for (int i = (array3[0] == -1) ? 1 : (array3[0] + 1); i < this.fNumElements; ++i) {
            if (this.isAllowedTransition(array, array2, i)) {
                array3[0] = i;
                return this.fElements[i];
            }
        }
        array3[0] = -1;
        return null;
    }
    
    public XSWildcardDecl nextWildcardTransition(final int[] array, final int[] array2, final int[] array3) {
        int i;
        for (i = ((array3[0] == -1) ? this.fNumElements : (array3[0] + 1)); i < this.fNumTotal; ++i) {
            if (this.isAllowedTransition(array, array2, i)) {
                array3[0] = i;
                return this.fWildcards[i];
            }
        }
        if (i == this.fNumTotal && this.isOpenContentAllowed(array, array2)) {
            array3[0] = this.fNumTotal;
            return this.fOpenContent.fWildcard;
        }
        array3[0] = -1;
        return null;
    }
    
    private boolean isAllowedTransition(final int[] array, final int[] array2, final int n) {
        if (array[0] == 2) {
            return false;
        }
        if (array[n] == this.fMaxOccurs[n]) {
            return false;
        }
        if (array2 != null) {
            System.arraycopy(array, 0, array2, 0, array.length);
            array2[0] = 1;
            if (array2[n] == 0 || array2[n] < this.fMinOccurs[n] || this.fMaxOccurs[n] != -1) {
                ++array2[n];
            }
        }
        return true;
    }
    
    private boolean isOpenContentAllowed(final int[] array, final int[] array2) {
        if (this.fOpenContent == null) {
            return false;
        }
        if (this.fOpenContent.fMode != 2) {
            System.arraycopy(array, 0, array2, 0, array.length);
            return true;
        }
        if (this.isFinal(array)) {
            array2[0] = 2;
            return true;
        }
        return false;
    }
    
    public boolean isOpenContent(final XSWildcardDecl xsWildcardDecl) {
        return this.fOpenContent != null && this.fOpenContent.fWildcard == xsWildcardDecl;
    }
    
    public List getDefinedNames(final SubstitutionGroupHandler substitutionGroupHandler) {
        final ArrayList list = new ArrayList();
        for (int i = 1; i < this.fNumElements; ++i) {
            final XSElementDecl xsElementDecl = this.fElements[i];
            list.add(xsElementDecl.fTargetNamespace);
            list.add(xsElementDecl.fName);
            if (xsElementDecl.fScope == 1) {
                final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, (short)4);
                for (int j = 0; j < substitutionGroup.length; ++j) {
                    list.add(substitutionGroup[j].fTargetNamespace);
                    list.add(substitutionGroup[j].fName);
                }
            }
        }
        return list;
    }
    
    public void optimizeStates(final XS11CMRestriction.XS11CM xs11CM, final int[] array, final int[] array2, final int n) {
    }
    
    private XS11AllCM(final boolean fHasOptionalContent, final XSElementDecl[] fElements, final XSWildcardDecl[] fWildcards, final int[] fMinOccurs, final int[] fMaxOccurs, final int fNumElements, final int fNumTotal, final XSOpenContentDecl fOpenContent) {
        this.fHasOptionalContent = fHasOptionalContent;
        this.fElements = fElements;
        this.fWildcards = fWildcards;
        this.fMinOccurs = fMinOccurs;
        this.fMaxOccurs = fMaxOccurs;
        this.fNumElements = fNumElements;
        this.fNumTotal = fNumTotal;
        this.fOpenContent = fOpenContent;
    }
    
    XS11AllCM copy() {
        int[] array;
        int[] array2;
        if (this.fNumTotal > 1) {
            array = new int[this.fNumTotal];
            array2 = new int[this.fNumTotal];
            System.arraycopy(this.fMinOccurs, 0, array, 0, this.fNumTotal);
            System.arraycopy(this.fMaxOccurs, 0, array2, 0, this.fNumTotal);
        }
        else {
            array = null;
            array2 = null;
        }
        return new XS11AllCM(this.fHasOptionalContent, this.fElements, this.fWildcards, array, array2, this.fNumElements, this.fNumTotal, this.fOpenContent);
    }
    
    void collectOccurs(final int[] array, final int[] array2, final int n, final int n2) {
        array[n] += this.fMinOccurs[n2];
        if (array2[n] != -1) {
            if (this.fMaxOccurs[n2] == -1) {
                array2[n] = -1;
            }
            else {
                array2[n] += this.fMaxOccurs[n2];
            }
        }
    }
    
    boolean removeAsBase(final int[] array, final int[] array2, final int[] array3) {
        for (int i = 1; i < this.fNumElements; ++i) {
            if (this.fMinOccurs[i] > array[i]) {
                return false;
            }
            this.fMinOccurs[i] = 0;
            if (this.fMaxOccurs[i] != -1) {
                if (array2[i] == -1 || this.fMaxOccurs[i] < array2[i]) {
                    if (array3[i] > 1) {
                        array3[i] = -1;
                    }
                    else {
                        if (array2[i] != -1) {
                            final int n = i;
                            array2[n] -= this.fMaxOccurs[i];
                        }
                        this.fMaxOccurs[i] = 0;
                    }
                }
                else {
                    final int[] fMaxOccurs = this.fMaxOccurs;
                    final int n2 = i;
                    fMaxOccurs[n2] -= array2[i];
                    array2[i] = 0;
                }
            }
            else {
                if (array2[i] == -1) {
                    this.fMaxOccurs[i] = 0;
                }
                array2[i] = 0;
            }
        }
        return true;
    }
    
    void removeAsDerived(final int[] array, final int[] array2, final int[] array3) {
        for (int i = 1; i < this.fNumElements; ++i) {
            final int n = array3[i];
            if (n >= 0) {
                if (array2[n] >= 0) {
                    this.fMinOccurs[i] = 0;
                    this.fMaxOccurs[i] = array[n];
                }
            }
        }
    }
    
    int minOccurs(final int n) {
        return this.fMinOccurs[n];
    }
    
    int maxOccurs(final int n) {
        return this.fMaxOccurs[n];
    }
    
    boolean isUnbounded(final int n) {
        return n >= this.fNumTotal || this.fMaxOccurs[n] == -1;
    }
    
    boolean hasOptionalContent() {
        return this.fHasOptionalContent;
    }
    
    int totalMin() {
        int n = 0;
        for (int i = 1; i < this.fNumTotal; ++i) {
            n += this.fMinOccurs[i];
        }
        return n;
    }
    
    int min(final int n) {
        return this.fMinOccurs[n];
    }
    
    XSOpenContentDecl getOpenContent() {
        return this.fOpenContent;
    }
    
    int calOccurs() {
        long n = 1L;
        for (int i = 1; i < this.fNumTotal; ++i) {
            int n2 = this.fMaxOccurs[i];
            if (n2 != 0) {
                if (n2 == -1) {
                    n2 = ((this.fMinOccurs[i] == 0) ? 1 : this.fMinOccurs[i]);
                }
                n *= n2 + 1;
                if (n > 2147483647L) {
                    return -1;
                }
            }
        }
        return (int)n;
    }
}
