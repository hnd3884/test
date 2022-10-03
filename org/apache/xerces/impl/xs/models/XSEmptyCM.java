package org.apache.xerces.impl.xs.models;

import java.util.Collections;
import java.util.List;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSElementDeclHelper;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSOpenContentDecl;
import java.util.Vector;

public class XSEmptyCM implements XSCMValidator, XS11CMRestriction.XS11CM
{
    private static final short STATE_START = 0;
    private static final Vector EMPTY;
    private final XSOpenContentDecl fOpenContent;
    
    public XSEmptyCM() {
        this.fOpenContent = null;
    }
    
    public XSEmptyCM(final XSOpenContentDecl fOpenContent) {
        this.fOpenContent = fOpenContent;
    }
    
    public int[] startContentModel() {
        return new int[] { 0 };
    }
    
    public Object oneTransition(final QName qName, final int[] array, final SubstitutionGroupHandler substitutionGroupHandler, final XSElementDeclHelper xsElementDeclHelper) {
        if (array[0] < 0) {
            array[0] = -2;
            return null;
        }
        if (this.fOpenContent != null && this.allowExpandedName(this.fOpenContent.fWildcard, qName, substitutionGroupHandler, xsElementDeclHelper)) {
            return this.fOpenContent;
        }
        array[0] = -1;
        return null;
    }
    
    public boolean endContentModel(final int[] array) {
        return array[0] >= 0;
    }
    
    public boolean checkUniqueParticleAttribution(final SubstitutionGroupHandler substitutionGroupHandler, final XSConstraints xsConstraints) throws XMLSchemaException {
        return false;
    }
    
    public Vector whatCanGoHere(final int[] array) {
        return XSEmptyCM.EMPTY;
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
    
    public XSElementDecl nextElementTransition(final int[] array, final int[] array2, final int[] array3) {
        return null;
    }
    
    public XSWildcardDecl nextWildcardTransition(final int[] array, final int[] array2, final int[] array3) {
        array2[0] = array[0];
        if (this.fOpenContent == null) {
            return null;
        }
        if (array3[0] == -1) {
            array3[0] = 0;
            return this.fOpenContent.fWildcard;
        }
        array3[0] = -1;
        return null;
    }
    
    public boolean isOpenContent(final XSWildcardDecl xsWildcardDecl) {
        return this.fOpenContent != null && this.fOpenContent.fWildcard == xsWildcardDecl;
    }
    
    public boolean allowExpandedName(final XSWildcardDecl xsWildcardDecl, final QName qName, final SubstitutionGroupHandler substitutionGroupHandler, final XSElementDeclHelper xsElementDeclHelper) {
        return xsWildcardDecl.allowQName(qName) && (!xsWildcardDecl.fDisallowedDefined || xsElementDeclHelper.getGlobalElementDecl(qName) == null);
    }
    
    public List getDefinedNames(final SubstitutionGroupHandler substitutionGroupHandler) {
        return Collections.EMPTY_LIST;
    }
    
    public void optimizeStates(final XS11CMRestriction.XS11CM xs11CM, final int[] array, final int[] array2, final int n) {
    }
    
    public XSOpenContentDecl getOpenContent() {
        return this.fOpenContent;
    }
    
    public XSElementDecl findMatchingElemDecl(final QName qName, final SubstitutionGroupHandler substitutionGroupHandler) {
        return null;
    }
    
    static {
        EMPTY = new Vector(0);
    }
}
