package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import java.util.List;
import java.util.Collections;
import java.util.Vector;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.xs.models.CMBuilder;
import java.util.Comparator;

class XS10Constraints extends XSConstraints
{
    private static final Comparator ELEMENT_PARTICLE_COMPARATOR;
    
    public XS10Constraints(final short n) {
        super(SchemaGrammar.getXSAnyType(n), n);
    }
    
    public boolean overlapUPA(final XSElementDecl xsElementDecl, final XSWildcardDecl xsWildcardDecl, final SubstitutionGroupHandler substitutionGroupHandler) {
        if (xsWildcardDecl.allowNamespace(xsElementDecl.fTargetNamespace)) {
            return true;
        }
        final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, this.fSchemaVersion);
        for (int i = substitutionGroup.length - 1; i >= 0; --i) {
            if (xsWildcardDecl.allowNamespace(substitutionGroup[i].fTargetNamespace)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSubsetOf(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2) {
        if (xsWildcardDecl2 == null) {
            return false;
        }
        if (xsWildcardDecl2.fType == 1) {
            return true;
        }
        if (xsWildcardDecl.fType == 2 && xsWildcardDecl2.fType == 2 && xsWildcardDecl.fNamespaceList[0] == xsWildcardDecl2.fNamespaceList[0]) {
            return true;
        }
        if (xsWildcardDecl.fType == 3) {
            if (xsWildcardDecl2.fType == 3 && this.subset2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList)) {
                return true;
            }
            if (xsWildcardDecl2.fType == 2 && !this.elementInSet(xsWildcardDecl2.fNamespaceList[0], xsWildcardDecl.fNamespaceList) && !this.elementInSet(XSWildcardDecl.ABSENT, xsWildcardDecl.fNamespaceList)) {
                return true;
            }
        }
        return false;
    }
    
    public XSWildcardDecl performUnionWith(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2, final short fProcessContents) {
        if (xsWildcardDecl2 == null) {
            return null;
        }
        final XSWildcardDecl xsWildcardDecl3 = new XSWildcardDecl();
        xsWildcardDecl3.fProcessContents = fProcessContents;
        if (this.areSame(xsWildcardDecl, xsWildcardDecl2)) {
            xsWildcardDecl3.fType = xsWildcardDecl.fType;
            xsWildcardDecl3.fNamespaceList = xsWildcardDecl.fNamespaceList;
        }
        else if (xsWildcardDecl.fType == 1 || xsWildcardDecl2.fType == 1) {
            xsWildcardDecl3.fType = 1;
        }
        else if (xsWildcardDecl.fType == 3 && xsWildcardDecl2.fType == 3) {
            xsWildcardDecl3.fType = 3;
            xsWildcardDecl3.fNamespaceList = this.union2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList);
        }
        else if (xsWildcardDecl.fType == 2 && xsWildcardDecl2.fType == 2) {
            xsWildcardDecl3.fType = 2;
            (xsWildcardDecl3.fNamespaceList = new String[2])[0] = XSWildcardDecl.ABSENT;
            xsWildcardDecl3.fNamespaceList[1] = XSWildcardDecl.ABSENT;
        }
        else if ((xsWildcardDecl.fType == 2 && xsWildcardDecl2.fType == 3) || (xsWildcardDecl.fType == 3 && xsWildcardDecl2.fType == 2)) {
            String[] array;
            String[] array2;
            if (xsWildcardDecl.fType == 2) {
                array = xsWildcardDecl.fNamespaceList;
                array2 = xsWildcardDecl2.fNamespaceList;
            }
            else {
                array = xsWildcardDecl2.fNamespaceList;
                array2 = xsWildcardDecl.fNamespaceList;
            }
            final boolean elementInSet = this.elementInSet(XSWildcardDecl.ABSENT, array2);
            if (array[0] != XSWildcardDecl.ABSENT) {
                final boolean elementInSet2 = this.elementInSet(array[0], array2);
                if (elementInSet2 && elementInSet) {
                    xsWildcardDecl3.fType = 1;
                }
                else if (elementInSet2 && !elementInSet) {
                    xsWildcardDecl3.fType = 2;
                    (xsWildcardDecl3.fNamespaceList = new String[2])[0] = XSWildcardDecl.ABSENT;
                    xsWildcardDecl3.fNamespaceList[1] = XSWildcardDecl.ABSENT;
                }
                else {
                    if (!elementInSet2 && elementInSet) {
                        return null;
                    }
                    xsWildcardDecl3.fType = 2;
                    xsWildcardDecl3.fNamespaceList = array;
                }
            }
            else if (elementInSet) {
                xsWildcardDecl3.fType = 1;
            }
            else {
                xsWildcardDecl3.fType = 2;
                xsWildcardDecl3.fNamespaceList = array;
            }
        }
        return xsWildcardDecl3;
    }
    
    public XSWildcardDecl performIntersectionWith(final XSWildcardDecl xsWildcardDecl, final XSWildcardDecl xsWildcardDecl2, final short fProcessContents) {
        if (xsWildcardDecl2 == null) {
            return null;
        }
        final XSWildcardDecl xsWildcardDecl3 = new XSWildcardDecl();
        xsWildcardDecl3.fProcessContents = fProcessContents;
        if (this.areSame(xsWildcardDecl, xsWildcardDecl2)) {
            xsWildcardDecl3.fType = xsWildcardDecl.fType;
            xsWildcardDecl3.fNamespaceList = xsWildcardDecl.fNamespaceList;
        }
        else if (xsWildcardDecl.fType == 1 || xsWildcardDecl2.fType == 1) {
            XSWildcardDecl xsWildcardDecl4 = xsWildcardDecl;
            if (xsWildcardDecl.fType == 1) {
                xsWildcardDecl4 = xsWildcardDecl2;
            }
            xsWildcardDecl3.fType = xsWildcardDecl4.fType;
            xsWildcardDecl3.fNamespaceList = xsWildcardDecl4.fNamespaceList;
        }
        else if ((xsWildcardDecl.fType == 2 && xsWildcardDecl2.fType == 3) || (xsWildcardDecl.fType == 3 && xsWildcardDecl2.fType == 2)) {
            String[] array;
            String[] array2;
            if (xsWildcardDecl.fType == 2) {
                array = xsWildcardDecl.fNamespaceList;
                array2 = xsWildcardDecl2.fNamespaceList;
            }
            else {
                array = xsWildcardDecl2.fNamespaceList;
                array2 = xsWildcardDecl.fNamespaceList;
            }
            final int length = array2.length;
            final String[] array3 = new String[length];
            int n = 0;
            for (int i = 0; i < length; ++i) {
                if (array2[i] != array[0] && array2[i] != XSWildcardDecl.ABSENT) {
                    array3[n++] = array2[i];
                }
            }
            xsWildcardDecl3.fType = 3;
            System.arraycopy(array3, 0, xsWildcardDecl3.fNamespaceList = new String[n], 0, n);
        }
        else if (xsWildcardDecl.fType == 3 && xsWildcardDecl2.fType == 3) {
            xsWildcardDecl3.fType = 3;
            xsWildcardDecl3.fNamespaceList = this.intersect2sets(xsWildcardDecl.fNamespaceList, xsWildcardDecl2.fNamespaceList);
        }
        else if (xsWildcardDecl.fType == 2 && xsWildcardDecl2.fType == 2) {
            if (xsWildcardDecl.fNamespaceList[0] != XSWildcardDecl.ABSENT && xsWildcardDecl2.fNamespaceList[0] != XSWildcardDecl.ABSENT) {
                return null;
            }
            XSWildcardDecl xsWildcardDecl5 = xsWildcardDecl;
            if (xsWildcardDecl.fNamespaceList[0] == XSWildcardDecl.ABSENT) {
                xsWildcardDecl5 = xsWildcardDecl2;
            }
            xsWildcardDecl3.fType = xsWildcardDecl5.fType;
            xsWildcardDecl3.fNamespaceList = xsWildcardDecl5.fNamespaceList;
        }
        return xsWildcardDecl3;
    }
    
    protected void groupSubsumption(final XSParticleDecl xsParticleDecl, final XSParticleDecl xsParticleDecl2, final XSGrammarBucket xsGrammarBucket, final SubstitutionGroupHandler substitutionGroupHandler, final CMBuilder cmBuilder, final XMLErrorReporter xmlErrorReporter, final String s, final SimpleLocator simpleLocator) {
        try {
            this.particleValidRestriction(xsParticleDecl, substitutionGroupHandler, xsParticleDecl2, substitutionGroupHandler);
        }
        catch (final XMLSchemaException ex) {
            final String key = ex.getKey();
            this.reportSchemaError(xmlErrorReporter, simpleLocator, key, ex.getArgs());
            this.reportSchemaError(xmlErrorReporter, simpleLocator, "src-redefine.6.2.2", new Object[] { s, key });
        }
    }
    
    protected void typeSubsumption(final XSComplexTypeDecl xsComplexTypeDecl, final XSComplexTypeDecl xsComplexTypeDecl2, final XSGrammarBucket xsGrammarBucket, final SubstitutionGroupHandler substitutionGroupHandler, final CMBuilder cmBuilder, final XMLErrorReporter xmlErrorReporter, final SimpleLocator simpleLocator) {
        try {
            this.particleValidRestriction(xsComplexTypeDecl.fParticle, substitutionGroupHandler, xsComplexTypeDecl2.fParticle, substitutionGroupHandler);
        }
        catch (final XMLSchemaException ex) {
            this.reportSchemaError(xmlErrorReporter, simpleLocator, ex.getKey(), ex.getArgs());
            this.reportSchemaError(xmlErrorReporter, simpleLocator, "derivation-ok-restriction.5.4.2", new Object[] { xsComplexTypeDecl.fName });
        }
    }
    
    private boolean particleValidRestriction(final XSParticleDecl xsParticleDecl, final SubstitutionGroupHandler substitutionGroupHandler, final XSParticleDecl xsParticleDecl2, final SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        return this.particleValidRestriction(xsParticleDecl, substitutionGroupHandler, xsParticleDecl2, substitutionGroupHandler2, true);
    }
    
    private boolean particleValidRestriction(XSParticleDecl xsParticleDecl, SubstitutionGroupHandler substitutionGroupHandler, XSParticleDecl xsParticleDecl2, SubstitutionGroupHandler substitutionGroupHandler2, final boolean b) throws XMLSchemaException {
        Vector removePointlessChildren = null;
        Vector removePointlessChildren2 = null;
        int n = -2;
        int n2 = -2;
        boolean b2 = false;
        if (xsParticleDecl.isEmpty() && !xsParticleDecl2.emptiable()) {
            throw new XMLSchemaException("cos-particle-restrict.a", (Object[])null);
        }
        if (!xsParticleDecl.isEmpty() && xsParticleDecl2.isEmpty()) {
            throw new XMLSchemaException("cos-particle-restrict.b", (Object[])null);
        }
        int n3 = xsParticleDecl.fType;
        if (n3 == 3) {
            n3 = ((XSModelGroupImpl)xsParticleDecl.fValue).fCompositor;
            final XSParticleDecl nonUnaryGroup = this.getNonUnaryGroup(xsParticleDecl);
            if (nonUnaryGroup != xsParticleDecl) {
                xsParticleDecl = nonUnaryGroup;
                n3 = xsParticleDecl.fType;
                if (n3 == 3) {
                    n3 = ((XSModelGroupImpl)xsParticleDecl.fValue).fCompositor;
                }
            }
            removePointlessChildren = removePointlessChildren(xsParticleDecl);
        }
        final int fMinOccurs = xsParticleDecl.fMinOccurs;
        final int fMaxOccurs = xsParticleDecl.fMaxOccurs;
        if (substitutionGroupHandler != null && n3 == 1) {
            final XSElementDecl xsElementDecl = (XSElementDecl)xsParticleDecl.fValue;
            if (xsElementDecl.fScope == 1) {
                final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, this.fSchemaVersion);
                if (substitutionGroup.length > 0) {
                    n3 = 101;
                    n = fMinOccurs;
                    n2 = fMaxOccurs;
                    removePointlessChildren = new Vector(substitutionGroup.length + 1);
                    for (int i = 0; i < substitutionGroup.length; ++i) {
                        this.addElementToParticleVector(removePointlessChildren, substitutionGroup[i]);
                    }
                    this.addElementToParticleVector(removePointlessChildren, xsElementDecl);
                    Collections.sort((List<Object>)removePointlessChildren, XS10Constraints.ELEMENT_PARTICLE_COMPARATOR);
                    substitutionGroupHandler = null;
                }
            }
        }
        int n4 = xsParticleDecl2.fType;
        if (n4 == 3) {
            n4 = ((XSModelGroupImpl)xsParticleDecl2.fValue).fCompositor;
            final XSParticleDecl nonUnaryGroup2 = this.getNonUnaryGroup(xsParticleDecl2);
            if (nonUnaryGroup2 != xsParticleDecl2) {
                xsParticleDecl2 = nonUnaryGroup2;
                n4 = xsParticleDecl2.fType;
                if (n4 == 3) {
                    n4 = ((XSModelGroupImpl)xsParticleDecl2.fValue).fCompositor;
                }
            }
            removePointlessChildren2 = removePointlessChildren(xsParticleDecl2);
        }
        final int fMinOccurs2 = xsParticleDecl2.fMinOccurs;
        final int fMaxOccurs2 = xsParticleDecl2.fMaxOccurs;
        if (substitutionGroupHandler2 != null && n4 == 1) {
            final XSElementDecl xsElementDecl2 = (XSElementDecl)xsParticleDecl2.fValue;
            if (xsElementDecl2.fScope == 1) {
                final XSElementDecl[] substitutionGroup2 = substitutionGroupHandler2.getSubstitutionGroup(xsElementDecl2, this.fSchemaVersion);
                if (substitutionGroup2.length > 0) {
                    n4 = 101;
                    removePointlessChildren2 = new Vector(substitutionGroup2.length + 1);
                    for (int j = 0; j < substitutionGroup2.length; ++j) {
                        this.addElementToParticleVector(removePointlessChildren2, substitutionGroup2[j]);
                    }
                    this.addElementToParticleVector(removePointlessChildren2, xsElementDecl2);
                    Collections.sort((List<Object>)removePointlessChildren2, XS10Constraints.ELEMENT_PARTICLE_COMPARATOR);
                    substitutionGroupHandler2 = null;
                    b2 = true;
                }
            }
        }
        switch (n3) {
            case 1: {
                switch (n4) {
                    case 1: {
                        this.checkNameAndTypeOK((XSElementDecl)xsParticleDecl.fValue, fMinOccurs, fMaxOccurs, (XSElementDecl)xsParticleDecl2.fValue, fMinOccurs2, fMaxOccurs2);
                        return b2;
                    }
                    case 2: {
                        this.checkNSCompat((XSElementDecl)xsParticleDecl.fValue, fMinOccurs, fMaxOccurs, (XSWildcardDecl)xsParticleDecl2.fValue, fMinOccurs2, fMaxOccurs2, b);
                        return b2;
                    }
                    case 101: {
                        final Vector vector = new Vector();
                        vector.addElement(xsParticleDecl);
                        this.checkRecurseLax(vector, 1, 1, substitutionGroupHandler, removePointlessChildren2, fMinOccurs2, fMaxOccurs2, substitutionGroupHandler2);
                        return b2;
                    }
                    case 102:
                    case 103: {
                        final Vector vector2 = new Vector();
                        vector2.addElement(xsParticleDecl);
                        this.checkRecurse(vector2, 1, 1, substitutionGroupHandler, removePointlessChildren2, fMinOccurs2, fMaxOccurs2, substitutionGroupHandler2);
                        return b2;
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            case 2: {
                switch (n4) {
                    case 2: {
                        this.checkNSSubset((XSWildcardDecl)xsParticleDecl.fValue, fMinOccurs, fMaxOccurs, (XSWildcardDecl)xsParticleDecl2.fValue, fMinOccurs2, fMaxOccurs2);
                        return b2;
                    }
                    case 1:
                    case 101:
                    case 102:
                    case 103: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "any:choice,sequence,all,elt" });
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            case 103: {
                switch (n4) {
                    case 2: {
                        if (n == -2) {
                            n = xsParticleDecl.minEffectiveTotalRange();
                        }
                        if (n2 == -2) {
                            n2 = xsParticleDecl.maxEffectiveTotalRange();
                        }
                        this.checkNSRecurseCheckCardinality(removePointlessChildren, n, n2, substitutionGroupHandler, xsParticleDecl2, fMinOccurs2, fMaxOccurs2, b);
                        return b2;
                    }
                    case 103: {
                        this.checkRecurse(removePointlessChildren, fMinOccurs, fMaxOccurs, substitutionGroupHandler, removePointlessChildren2, fMinOccurs2, fMaxOccurs2, substitutionGroupHandler2);
                        return b2;
                    }
                    case 1:
                    case 101:
                    case 102: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "all:choice,sequence,elt" });
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            case 101: {
                switch (n4) {
                    case 2: {
                        if (n == -2) {
                            n = xsParticleDecl.minEffectiveTotalRange();
                        }
                        if (n2 == -2) {
                            n2 = xsParticleDecl.maxEffectiveTotalRange();
                        }
                        this.checkNSRecurseCheckCardinality(removePointlessChildren, n, n2, substitutionGroupHandler, xsParticleDecl2, fMinOccurs2, fMaxOccurs2, b);
                        return b2;
                    }
                    case 101: {
                        this.checkRecurseLax(removePointlessChildren, fMinOccurs, fMaxOccurs, substitutionGroupHandler, removePointlessChildren2, fMinOccurs2, fMaxOccurs2, substitutionGroupHandler2);
                        return b2;
                    }
                    case 1:
                    case 102:
                    case 103: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "choice:all,sequence,elt" });
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            case 102: {
                switch (n4) {
                    case 2: {
                        if (n == -2) {
                            n = xsParticleDecl.minEffectiveTotalRange();
                        }
                        if (n2 == -2) {
                            n2 = xsParticleDecl.maxEffectiveTotalRange();
                        }
                        this.checkNSRecurseCheckCardinality(removePointlessChildren, n, n2, substitutionGroupHandler, xsParticleDecl2, fMinOccurs2, fMaxOccurs2, b);
                        return b2;
                    }
                    case 103: {
                        this.checkRecurseUnordered(removePointlessChildren, fMinOccurs, fMaxOccurs, substitutionGroupHandler, removePointlessChildren2, fMinOccurs2, fMaxOccurs2, substitutionGroupHandler2);
                        return b2;
                    }
                    case 102: {
                        this.checkRecurse(removePointlessChildren, fMinOccurs, fMaxOccurs, substitutionGroupHandler, removePointlessChildren2, fMinOccurs2, fMaxOccurs2, substitutionGroupHandler2);
                        return b2;
                    }
                    case 101: {
                        this.checkMapAndSum(removePointlessChildren, fMinOccurs * removePointlessChildren.size(), (fMaxOccurs == -1) ? fMaxOccurs : (fMaxOccurs * removePointlessChildren.size()), substitutionGroupHandler, removePointlessChildren2, fMinOccurs2, fMaxOccurs2, substitutionGroupHandler2);
                        return b2;
                    }
                    case 1: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[] { "seq:elt" });
                    }
                    default: {
                        throw new XMLSchemaException("Internal-Error", new Object[] { "in particleValidRestriction" });
                    }
                }
                break;
            }
            default: {
                return b2;
            }
        }
    }
    
    private void addElementToParticleVector(final Vector vector, final XSElementDecl fValue) {
        final XSParticleDecl xsParticleDecl = new XSParticleDecl();
        xsParticleDecl.fValue = fValue;
        xsParticleDecl.fType = 1;
        vector.addElement(xsParticleDecl);
    }
    
    private XSParticleDecl getNonUnaryGroup(final XSParticleDecl xsParticleDecl) {
        if (xsParticleDecl.fType == 1 || xsParticleDecl.fType == 2) {
            return xsParticleDecl;
        }
        if (xsParticleDecl.fMinOccurs == 1 && xsParticleDecl.fMaxOccurs == 1 && xsParticleDecl.fValue != null && ((XSModelGroupImpl)xsParticleDecl.fValue).fParticleCount == 1) {
            return this.getNonUnaryGroup(((XSModelGroupImpl)xsParticleDecl.fValue).fParticles[0]);
        }
        return xsParticleDecl;
    }
    
    private static Vector removePointlessChildren(final XSParticleDecl xsParticleDecl) {
        if (xsParticleDecl.fType == 1 || xsParticleDecl.fType == 2) {
            return null;
        }
        final Vector vector = new Vector();
        final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
        for (int i = 0; i < xsModelGroupImpl.fParticleCount; ++i) {
            gatherChildren(xsModelGroupImpl.fCompositor, xsModelGroupImpl.fParticles[i], vector);
        }
        return vector;
    }
    
    private static void gatherChildren(final int n, final XSParticleDecl xsParticleDecl, final Vector vector) {
        final int fMinOccurs = xsParticleDecl.fMinOccurs;
        final int fMaxOccurs = xsParticleDecl.fMaxOccurs;
        short n2 = xsParticleDecl.fType;
        if (n2 == 3) {
            n2 = ((XSModelGroupImpl)xsParticleDecl.fValue).fCompositor;
        }
        if (n2 == 1 || n2 == 2) {
            vector.addElement(xsParticleDecl);
            return;
        }
        if (fMinOccurs != 1 || fMaxOccurs != 1) {
            vector.addElement(xsParticleDecl);
        }
        else if (n == n2) {
            final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
            for (int i = 0; i < xsModelGroupImpl.fParticleCount; ++i) {
                gatherChildren(n2, xsModelGroupImpl.fParticles[i], vector);
            }
        }
        else if (!xsParticleDecl.isEmpty()) {
            vector.addElement(xsParticleDecl);
        }
    }
    
    private void checkNameAndTypeOK(final XSElementDecl xsElementDecl, final int n, final int n2, final XSElementDecl xsElementDecl2, final int n3, final int n4) throws XMLSchemaException {
        if (xsElementDecl.fName != xsElementDecl2.fName || xsElementDecl.fTargetNamespace != xsElementDecl2.fTargetNamespace) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.1", new Object[] { xsElementDecl.fName, xsElementDecl.fTargetNamespace, xsElementDecl2.fName, xsElementDecl2.fTargetNamespace });
        }
        if (!xsElementDecl2.getNillable() && xsElementDecl.getNillable()) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.2", new Object[] { xsElementDecl.fName });
        }
        if (!this.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.3", new Object[] { xsElementDecl.fName, Integer.toString(n), (n2 == -1) ? "unbounded" : Integer.toString(n2), Integer.toString(n3), (n4 == -1) ? "unbounded" : Integer.toString(n4) });
        }
        if (xsElementDecl2.getConstraintType() == 2) {
            if (xsElementDecl.getConstraintType() != 2) {
                throw new XMLSchemaException("rcase-NameAndTypeOK.4.a", new Object[] { xsElementDecl.fName, xsElementDecl2.fDefault.stringValue() });
            }
            boolean b = false;
            if (xsElementDecl.fType.getTypeCategory() == 16 || ((XSComplexTypeDecl)xsElementDecl.fType).fContentType == 1) {
                b = true;
            }
            if ((!b && !xsElementDecl2.fDefault.normalizedValue.equals(xsElementDecl.fDefault.normalizedValue)) || (b && !xsElementDecl2.fDefault.actualValue.equals(xsElementDecl.fDefault.actualValue))) {
                throw new XMLSchemaException("rcase-NameAndTypeOK.4.b", new Object[] { xsElementDecl.fName, xsElementDecl.fDefault.stringValue(), xsElementDecl2.fDefault.stringValue() });
            }
        }
        this.checkIDConstraintRestriction(xsElementDecl, xsElementDecl2);
        final short fBlock = xsElementDecl.fBlock;
        final short fBlock2 = xsElementDecl2.fBlock;
        if ((fBlock & fBlock2) != fBlock2 || (fBlock == 0 && fBlock2 != 0)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.6", new Object[] { xsElementDecl.fName });
        }
        if (!this.checkTypeDerivationOk(xsElementDecl.fType, xsElementDecl2.fType, (short)25)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.7", new Object[] { xsElementDecl.fName, xsElementDecl.fType.getName(), xsElementDecl2.fType.getName() });
        }
    }
    
    private void checkIDConstraintRestriction(final XSElementDecl xsElementDecl, final XSElementDecl xsElementDecl2) throws XMLSchemaException {
    }
    
    private boolean checkOccurrenceRange(final int n, final int n2, final int n3, final int n4) {
        return n >= n3 && (n4 == -1 || (n2 != -1 && n2 <= n4));
    }
    
    private void checkNSCompat(final XSElementDecl xsElementDecl, final int n, final int n2, final XSWildcardDecl xsWildcardDecl, final int n3, final int n4, final boolean b) throws XMLSchemaException {
        if (b && !this.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-NSCompat.2", new Object[] { xsElementDecl.fName, Integer.toString(n), (n2 == -1) ? "unbounded" : Integer.toString(n2), Integer.toString(n3), (n4 == -1) ? "unbounded" : Integer.toString(n4) });
        }
        if (!xsWildcardDecl.allowNamespace(xsElementDecl.fTargetNamespace)) {
            throw new XMLSchemaException("rcase-NSCompat.1", new Object[] { xsElementDecl.fName, xsElementDecl.fTargetNamespace });
        }
    }
    
    private void checkNSSubset(final XSWildcardDecl xsWildcardDecl, final int n, final int n2, final XSWildcardDecl xsWildcardDecl2, final int n3, final int n4) throws XMLSchemaException {
        if (!this.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-NSSubset.2", new Object[] { Integer.toString(n), (n2 == -1) ? "unbounded" : Integer.toString(n2), Integer.toString(n3), (n4 == -1) ? "unbounded" : Integer.toString(n4) });
        }
        if (!this.isSubsetOf(xsWildcardDecl, xsWildcardDecl2)) {
            throw new XMLSchemaException("rcase-NSSubset.1", (Object[])null);
        }
        if (xsWildcardDecl.weakerProcessContents(xsWildcardDecl2)) {
            throw new XMLSchemaException("rcase-NSSubset.3", new Object[] { xsWildcardDecl.getProcessContentsAsString(), xsWildcardDecl2.getProcessContentsAsString() });
        }
    }
    
    private void checkNSRecurseCheckCardinality(final Vector vector, final int n, final int n2, final SubstitutionGroupHandler substitutionGroupHandler, final XSParticleDecl xsParticleDecl, final int n3, final int n4, final boolean b) throws XMLSchemaException {
        if (b && !this.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.2", new Object[] { Integer.toString(n), (n2 == -1) ? "unbounded" : Integer.toString(n2), Integer.toString(n3), (n4 == -1) ? "unbounded" : Integer.toString(n4) });
        }
        final int size = vector.size();
        try {
            for (int i = 0; i < size; ++i) {
                this.particleValidRestriction((XSParticleDecl)vector.elementAt(i), substitutionGroupHandler, xsParticleDecl, null, false);
            }
        }
        catch (final XMLSchemaException ex) {
            throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.1", (Object[])null);
        }
    }
    
    private void checkRecurse(final Vector vector, final int n, final int n2, final SubstitutionGroupHandler substitutionGroupHandler, final Vector vector2, final int n3, final int n4, final SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        if (!this.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-Recurse.1", new Object[] { Integer.toString(n), (n2 == -1) ? "unbounded" : Integer.toString(n2), Integer.toString(n3), (n4 == -1) ? "unbounded" : Integer.toString(n4) });
        }
        final int size = vector.size();
        final int size2 = vector2.size();
        int n5 = 0;
        int i = 0;
    Label_0097:
        while (i < size) {
            final XSParticleDecl xsParticleDecl = vector.elementAt(i);
            int j = n5;
            while (j < size2) {
                final XSParticleDecl xsParticleDecl2 = vector2.elementAt(j);
                ++n5;
                Label_0195: {
                    try {
                        this.particleValidRestriction(xsParticleDecl, substitutionGroupHandler, xsParticleDecl2, substitutionGroupHandler2);
                        break Label_0195;
                    }
                    catch (final XMLSchemaException ex) {
                        if (!xsParticleDecl2.emptiable()) {
                            throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
                        }
                        ++j;
                        continue;
                    }
                    break;
                }
                ++i;
                continue Label_0097;
            }
            throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
        }
        for (int k = n5; k < size2; ++k) {
            if (!((XSParticleDecl)vector2.elementAt(k)).emptiable()) {
                throw new XMLSchemaException("rcase-Recurse.2", (Object[])null);
            }
        }
    }
    
    private void checkRecurseUnordered(final Vector vector, final int n, final int n2, final SubstitutionGroupHandler substitutionGroupHandler, final Vector vector2, final int n3, final int n4, final SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        if (!this.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-RecurseUnordered.1", new Object[] { Integer.toString(n), (n2 == -1) ? "unbounded" : Integer.toString(n2), Integer.toString(n3), (n4 == -1) ? "unbounded" : Integer.toString(n4) });
        }
        final int size = vector.size();
        final int size2 = vector2.size();
        final boolean[] array = new boolean[size2];
        int i = 0;
    Label_0100:
        while (i < size) {
            final XSParticleDecl xsParticleDecl = vector.elementAt(i);
            int j = 0;
            while (j < size2) {
                final XSParticleDecl xsParticleDecl2 = vector2.elementAt(j);
                Label_0200: {
                    try {
                        this.particleValidRestriction(xsParticleDecl, substitutionGroupHandler, xsParticleDecl2, substitutionGroupHandler2);
                        if (array[j]) {
                            throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
                        }
                        array[j] = true;
                        break Label_0200;
                    }
                    catch (final XMLSchemaException ex) {
                        ++j;
                        continue;
                    }
                    break;
                }
                ++i;
                continue Label_0100;
            }
            throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
        }
        for (int k = 0; k < size2; ++k) {
            final XSParticleDecl xsParticleDecl3 = vector2.elementAt(k);
            if (!array[k] && !xsParticleDecl3.emptiable()) {
                throw new XMLSchemaException("rcase-RecurseUnordered.2", (Object[])null);
            }
        }
    }
    
    private void checkRecurseLax(final Vector vector, final int n, final int n2, final SubstitutionGroupHandler substitutionGroupHandler, final Vector vector2, final int n3, final int n4, final SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        if (!this.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-RecurseLax.1", new Object[] { Integer.toString(n), (n2 == -1) ? "unbounded" : Integer.toString(n2), Integer.toString(n3), (n4 == -1) ? "unbounded" : Integer.toString(n4) });
        }
        final int size = vector.size();
        final int size2 = vector2.size();
        int n5 = 0;
        int i = 0;
    Label_0097:
        while (i < size) {
            final XSParticleDecl xsParticleDecl = vector.elementAt(i);
            int j = n5;
            while (j < size2) {
                final XSParticleDecl xsParticleDecl2 = vector2.elementAt(j);
                ++n5;
                Label_0181: {
                    try {
                        if (this.particleValidRestriction(xsParticleDecl, substitutionGroupHandler, xsParticleDecl2, substitutionGroupHandler2)) {
                            --n5;
                        }
                        break Label_0181;
                    }
                    catch (final XMLSchemaException ex) {
                        ++j;
                        continue;
                    }
                    break;
                }
                ++i;
                continue Label_0097;
            }
            throw new XMLSchemaException("rcase-RecurseLax.2", (Object[])null);
        }
    }
    
    private void checkMapAndSum(final Vector vector, final int n, final int n2, final SubstitutionGroupHandler substitutionGroupHandler, final Vector vector2, final int n3, final int n4, final SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        if (!this.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-MapAndSum.2", new Object[] { Integer.toString(n), (n2 == -1) ? "unbounded" : Integer.toString(n2), Integer.toString(n3), (n4 == -1) ? "unbounded" : Integer.toString(n4) });
        }
        final int size = vector.size();
        final int size2 = vector2.size();
        int i = 0;
    Label_0094:
        while (i < size) {
            final XSParticleDecl xsParticleDecl = vector.elementAt(i);
            int j = 0;
            while (j < size2) {
                final XSParticleDecl xsParticleDecl2 = vector2.elementAt(j);
                Label_0169: {
                    try {
                        this.particleValidRestriction(xsParticleDecl, substitutionGroupHandler, xsParticleDecl2, substitutionGroupHandler2);
                        break Label_0169;
                    }
                    catch (final XMLSchemaException ex) {
                        ++j;
                        continue;
                    }
                    break;
                }
                ++i;
                continue Label_0094;
            }
            throw new XMLSchemaException("rcase-MapAndSum.1", (Object[])null);
        }
    }
    
    protected final boolean checkEmptyFacets(final XSSimpleType xsSimpleType) {
        return true;
    }
    
    static {
        ELEMENT_PARTICLE_COMPARATOR = new Comparator() {
            public int compare(final Object o, final Object o2) {
                final XSParticleDecl xsParticleDecl = (XSParticleDecl)o;
                final XSParticleDecl xsParticleDecl2 = (XSParticleDecl)o2;
                final XSElementDecl xsElementDecl = (XSElementDecl)xsParticleDecl.fValue;
                final XSElementDecl xsElementDecl2 = (XSElementDecl)xsParticleDecl2.fValue;
                final String namespace = xsElementDecl.getNamespace();
                final String namespace2 = xsElementDecl2.getNamespace();
                final String name = xsElementDecl.getName();
                final String name2 = xsElementDecl2.getName();
                final boolean b = namespace == namespace2;
                int compareTo = 0;
                if (!b) {
                    if (namespace != null) {
                        if (namespace2 != null) {
                            compareTo = namespace.compareTo(namespace2);
                        }
                        else {
                            compareTo = 1;
                        }
                    }
                    else {
                        compareTo = -1;
                    }
                }
                return (compareTo != 0) ? compareTo : name.compareTo(name2);
            }
        };
    }
}
