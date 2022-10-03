package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSWildcard;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.xs.EqualityHelper;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSAttributeGroupDefinition;

public class XSAttributeGroupDecl implements XSAttributeGroupDefinition
{
    public String fName;
    public String fTargetNamespace;
    int fAttrUseNum;
    private static final int INITIAL_SIZE = 5;
    XSAttributeUseImpl[] fAttributeUses;
    public XSWildcardDecl fAttributeWC;
    public String fIDAttrName;
    public XSObjectList fAnnotations;
    protected XSObjectListImpl fAttrUses;
    private XSNamespaceItem fNamespaceItem;
    
    public XSAttributeGroupDecl() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fAttrUseNum = 0;
        this.fAttributeUses = new XSAttributeUseImpl[5];
        this.fAttributeWC = null;
        this.fIDAttrName = null;
        this.fAttrUses = null;
        this.fNamespaceItem = null;
    }
    
    public String addAttributeUse(final XSAttributeUseImpl xsAttributeUseImpl) {
        if (xsAttributeUseImpl.fUse != 2 && xsAttributeUseImpl.fAttrDecl.fType.isIDType()) {
            if (this.fIDAttrName != null) {
                return this.fIDAttrName;
            }
            this.fIDAttrName = xsAttributeUseImpl.fAttrDecl.fName;
        }
        if (this.fAttrUseNum == this.fAttributeUses.length) {
            this.fAttributeUses = resize(this.fAttributeUses, this.fAttrUseNum * 2);
        }
        this.fAttributeUses[this.fAttrUseNum++] = xsAttributeUseImpl;
        return null;
    }
    
    public String addAttributeUse(final XSAttributeUseImpl xsAttributeUseImpl, final boolean b) {
        if (xsAttributeUseImpl.fUse != 2 && !b && xsAttributeUseImpl.fAttrDecl.fType.isIDType()) {
            if (this.fIDAttrName != null) {
                return this.fIDAttrName;
            }
            this.fIDAttrName = xsAttributeUseImpl.fAttrDecl.fName;
        }
        if (this.fAttrUseNum == this.fAttributeUses.length) {
            this.fAttributeUses = resize(this.fAttributeUses, this.fAttrUseNum * 2);
        }
        this.fAttributeUses[this.fAttrUseNum++] = xsAttributeUseImpl;
        return null;
    }
    
    public void replaceAttributeUse(final XSAttributeUse xsAttributeUse, final XSAttributeUseImpl xsAttributeUseImpl) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i] == xsAttributeUse) {
                this.fAttributeUses[i] = xsAttributeUseImpl;
            }
        }
    }
    
    public XSAttributeUse getAttributeUse(final String s, final String s2) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fAttrDecl.fTargetNamespace == s && this.fAttributeUses[i].fAttrDecl.fName == s2) {
                return this.fAttributeUses[i];
            }
        }
        return null;
    }
    
    public XSAttributeUse getAttributeUseNoProhibited(final String s, final String s2) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fAttrDecl.fTargetNamespace == s && this.fAttributeUses[i].fAttrDecl.fName == s2 && this.fAttributeUses[i].fUse != 2) {
                return this.fAttributeUses[i];
            }
        }
        return null;
    }
    
    public void removeProhibitedAttrs() {
        if (this.fAttrUseNum == 0) {
            return;
        }
        int fAttrUseNum = 0;
        final XSAttributeUseImpl[] fAttributeUses = new XSAttributeUseImpl[this.fAttrUseNum];
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fUse != 2) {
                fAttributeUses[fAttrUseNum++] = this.fAttributeUses[i];
            }
        }
        this.fAttributeUses = fAttributeUses;
        this.fAttrUseNum = fAttrUseNum;
    }
    
    public Object[] validRestrictionOf(final String s, final XSAttributeGroupDecl xsAttributeGroupDecl, final XSConstraints xsConstraints) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            final XSAttributeUseImpl xsAttributeUseImpl = this.fAttributeUses[i];
            final XSAttributeDecl fAttrDecl = xsAttributeUseImpl.fAttrDecl;
            final XSAttributeUseImpl xsAttributeUseImpl2 = (XSAttributeUseImpl)xsAttributeGroupDecl.getAttributeUse(fAttrDecl.fTargetNamespace, fAttrDecl.fName);
            if (xsAttributeUseImpl2 != null) {
                if (xsAttributeUseImpl2.getRequired() && !xsAttributeUseImpl.getRequired()) {
                    return new Object[] { s, fAttrDecl.fName, (xsAttributeUseImpl.fUse == 0) ? "optional" : "prohibited", "derivation-ok-restriction.2.1.1" };
                }
                if (xsAttributeUseImpl.fUse != 2) {
                    final XSAttributeDecl fAttrDecl2 = xsAttributeUseImpl2.fAttrDecl;
                    if (!xsConstraints.checkSimpleDerivationOk(fAttrDecl.fType, fAttrDecl2.fType, fAttrDecl2.fType.getFinal())) {
                        return new Object[] { s, fAttrDecl.fName, fAttrDecl.fType.getName(), fAttrDecl2.fType.getName(), "derivation-ok-restriction.2.1.2" };
                    }
                    final short n = (xsAttributeUseImpl2.fConstraintType != 0) ? xsAttributeUseImpl2.fConstraintType : fAttrDecl2.getConstraintType();
                    final short n2 = (xsAttributeUseImpl.fConstraintType != 0) ? xsAttributeUseImpl.fConstraintType : fAttrDecl.getConstraintType();
                    if (n == 2) {
                        if (n2 != 2) {
                            return new Object[] { s, fAttrDecl.fName, "derivation-ok-restriction.2.1.3.a" };
                        }
                        final ValidatedInfo validatedInfo = (xsAttributeUseImpl2.fDefault != null) ? xsAttributeUseImpl2.fDefault : fAttrDecl2.fDefault;
                        final ValidatedInfo validatedInfo2 = (xsAttributeUseImpl.fDefault != null) ? xsAttributeUseImpl.fDefault : fAttrDecl.fDefault;
                        if (!EqualityHelper.isEqual(validatedInfo, validatedInfo2, xsConstraints.getSchemaVersion())) {
                            return new Object[] { s, fAttrDecl.fName, validatedInfo2.stringValue(), validatedInfo.stringValue(), "derivation-ok-restriction.2.1.3.b" };
                        }
                    }
                    if (xsAttributeUseImpl2.getInheritable() != xsAttributeUseImpl.getInheritable()) {
                        return new Object[] { s, fAttrDecl.fName, "cos-content-act-restrict.5.3" };
                    }
                }
            }
            else {
                if (xsAttributeGroupDecl.fAttributeWC == null) {
                    return new Object[] { s, fAttrDecl.fName, "derivation-ok-restriction.2.2.a" };
                }
                if (!xsAttributeGroupDecl.fAttributeWC.allowNamespace(fAttrDecl.fTargetNamespace)) {
                    return new Object[] { s, fAttrDecl.fName, (fAttrDecl.fTargetNamespace == null) ? "" : fAttrDecl.fTargetNamespace, "derivation-ok-restriction.2.2.b" };
                }
            }
        }
        for (int j = 0; j < xsAttributeGroupDecl.fAttrUseNum; ++j) {
            final XSAttributeUseImpl xsAttributeUseImpl3 = xsAttributeGroupDecl.fAttributeUses[j];
            if (xsAttributeUseImpl3.fUse == 1) {
                final XSAttributeDecl fAttrDecl3 = xsAttributeUseImpl3.fAttrDecl;
                if (this.getAttributeUse(fAttrDecl3.fTargetNamespace, fAttrDecl3.fName) == null) {
                    return new Object[] { s, xsAttributeUseImpl3.fAttrDecl.fName, "derivation-ok-restriction.3" };
                }
            }
        }
        if (this.fAttributeWC != null) {
            if (xsAttributeGroupDecl.fAttributeWC == null) {
                return new Object[] { s, "derivation-ok-restriction.4.1" };
            }
            if (!xsConstraints.isSubsetOf(this.fAttributeWC, xsAttributeGroupDecl.fAttributeWC)) {
                return new Object[] { s, "derivation-ok-restriction.4.2" };
            }
            if (this.fAttributeWC.weakerProcessContents(xsAttributeGroupDecl.fAttributeWC)) {
                return new Object[] { s, this.fAttributeWC.getProcessContentsAsString(), xsAttributeGroupDecl.fAttributeWC.getProcessContentsAsString(), "derivation-ok-restriction.4.3" };
            }
        }
        return null;
    }
    
    static final XSAttributeUseImpl[] resize(final XSAttributeUseImpl[] array, final int n) {
        final XSAttributeUseImpl[] array2 = new XSAttributeUseImpl[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    public void reset() {
        this.fName = null;
        this.fTargetNamespace = null;
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            this.fAttributeUses[i] = null;
        }
        this.fAttrUseNum = 0;
        this.fAttributeWC = null;
        this.fAnnotations = null;
        this.fIDAttrName = null;
    }
    
    public short getType() {
        return 5;
    }
    
    public String getName() {
        return this.fName;
    }
    
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    public XSObjectList getAttributeUses() {
        if (this.fAttrUses == null) {
            if (this.fAttrUseNum > 0) {
                this.fAttrUses = new XSObjectListImpl(this.fAttributeUses, this.fAttrUseNum);
            }
            else {
                this.fAttrUses = XSObjectListImpl.EMPTY_LIST;
            }
        }
        return this.fAttrUses;
    }
    
    public XSWildcard getAttributeWildcard() {
        return this.fAttributeWC;
    }
    
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }
    
    void setNamespaceItem(final XSNamespaceItem fNamespaceItem) {
        this.fNamespaceItem = fNamespaceItem;
    }
}
