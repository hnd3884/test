package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;

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
    
    public String addAttributeUse(final XSAttributeUseImpl attrUse) {
        if (attrUse.fUse != 2 && attrUse.fAttrDecl.fType.isIDType()) {
            if (this.fIDAttrName != null) {
                return this.fIDAttrName;
            }
            this.fIDAttrName = attrUse.fAttrDecl.fName;
        }
        if (this.fAttrUseNum == this.fAttributeUses.length) {
            this.fAttributeUses = resize(this.fAttributeUses, this.fAttrUseNum * 2);
        }
        this.fAttributeUses[this.fAttrUseNum++] = attrUse;
        return null;
    }
    
    public void replaceAttributeUse(final XSAttributeUse oldUse, final XSAttributeUseImpl newUse) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i] == oldUse) {
                this.fAttributeUses[i] = newUse;
            }
        }
    }
    
    public XSAttributeUse getAttributeUse(final String namespace, final String name) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fAttrDecl.fTargetNamespace == namespace && this.fAttributeUses[i].fAttrDecl.fName == name) {
                return this.fAttributeUses[i];
            }
        }
        return null;
    }
    
    public XSAttributeUse getAttributeUseNoProhibited(final String namespace, final String name) {
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fAttrDecl.fTargetNamespace == namespace && this.fAttributeUses[i].fAttrDecl.fName == name && this.fAttributeUses[i].fUse != 2) {
                return this.fAttributeUses[i];
            }
        }
        return null;
    }
    
    public void removeProhibitedAttrs() {
        if (this.fAttrUseNum == 0) {
            return;
        }
        int count = 0;
        final XSAttributeUseImpl[] uses = new XSAttributeUseImpl[this.fAttrUseNum];
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            if (this.fAttributeUses[i].fUse != 2) {
                uses[count++] = this.fAttributeUses[i];
            }
        }
        this.fAttributeUses = uses;
        this.fAttrUseNum = count;
    }
    
    public Object[] validRestrictionOf(final String typeName, final XSAttributeGroupDecl baseGroup) {
        Object[] errorArgs = null;
        XSAttributeUseImpl attrUse = null;
        XSAttributeDecl attrDecl = null;
        XSAttributeUseImpl baseAttrUse = null;
        XSAttributeDecl baseAttrDecl = null;
        for (int i = 0; i < this.fAttrUseNum; ++i) {
            attrUse = this.fAttributeUses[i];
            attrDecl = attrUse.fAttrDecl;
            baseAttrUse = (XSAttributeUseImpl)baseGroup.getAttributeUse(attrDecl.fTargetNamespace, attrDecl.fName);
            if (baseAttrUse != null) {
                if (baseAttrUse.getRequired() && !attrUse.getRequired()) {
                    errorArgs = new Object[] { typeName, attrDecl.fName, (attrUse.fUse == 0) ? "optional" : "prohibited", "derivation-ok-restriction.2.1.1" };
                    return errorArgs;
                }
                if (attrUse.fUse != 2) {
                    baseAttrDecl = baseAttrUse.fAttrDecl;
                    if (!XSConstraints.checkSimpleDerivationOk(attrDecl.fType, baseAttrDecl.fType, baseAttrDecl.fType.getFinal())) {
                        errorArgs = new Object[] { typeName, attrDecl.fName, attrDecl.fType.getName(), baseAttrDecl.fType.getName(), "derivation-ok-restriction.2.1.2" };
                        return errorArgs;
                    }
                    final int baseConsType = (baseAttrUse.fConstraintType != 0) ? baseAttrUse.fConstraintType : baseAttrDecl.getConstraintType();
                    final int thisConstType = (attrUse.fConstraintType != 0) ? attrUse.fConstraintType : attrDecl.getConstraintType();
                    if (baseConsType == 2) {
                        if (thisConstType != 2) {
                            errorArgs = new Object[] { typeName, attrDecl.fName, "derivation-ok-restriction.2.1.3.a" };
                            return errorArgs;
                        }
                        final ValidatedInfo baseFixedValue = (baseAttrUse.fDefault != null) ? baseAttrUse.fDefault : baseAttrDecl.fDefault;
                        final ValidatedInfo thisFixedValue = (attrUse.fDefault != null) ? attrUse.fDefault : attrDecl.fDefault;
                        if (!baseFixedValue.actualValue.equals(thisFixedValue.actualValue)) {
                            errorArgs = new Object[] { typeName, attrDecl.fName, thisFixedValue.stringValue(), baseFixedValue.stringValue(), "derivation-ok-restriction.2.1.3.b" };
                            return errorArgs;
                        }
                    }
                }
            }
            else {
                if (baseGroup.fAttributeWC == null) {
                    errorArgs = new Object[] { typeName, attrDecl.fName, "derivation-ok-restriction.2.2.a" };
                    return errorArgs;
                }
                if (!baseGroup.fAttributeWC.allowNamespace(attrDecl.fTargetNamespace)) {
                    errorArgs = new Object[] { typeName, attrDecl.fName, (attrDecl.fTargetNamespace == null) ? "" : attrDecl.fTargetNamespace, "derivation-ok-restriction.2.2.b" };
                    return errorArgs;
                }
            }
        }
        for (int i = 0; i < baseGroup.fAttrUseNum; ++i) {
            baseAttrUse = baseGroup.fAttributeUses[i];
            if (baseAttrUse.fUse == 1) {
                baseAttrDecl = baseAttrUse.fAttrDecl;
                if (this.getAttributeUse(baseAttrDecl.fTargetNamespace, baseAttrDecl.fName) == null) {
                    errorArgs = new Object[] { typeName, baseAttrUse.fAttrDecl.fName, "derivation-ok-restriction.3" };
                    return errorArgs;
                }
            }
        }
        if (this.fAttributeWC != null) {
            if (baseGroup.fAttributeWC == null) {
                errorArgs = new Object[] { typeName, "derivation-ok-restriction.4.1" };
                return errorArgs;
            }
            if (!this.fAttributeWC.isSubsetOf(baseGroup.fAttributeWC)) {
                errorArgs = new Object[] { typeName, "derivation-ok-restriction.4.2" };
                return errorArgs;
            }
            if (this.fAttributeWC.weakerProcessContents(baseGroup.fAttributeWC)) {
                errorArgs = new Object[] { typeName, this.fAttributeWC.getProcessContentsAsString(), baseGroup.fAttributeWC.getProcessContentsAsString(), "derivation-ok-restriction.4.3" };
                return errorArgs;
            }
        }
        return null;
    }
    
    static final XSAttributeUseImpl[] resize(final XSAttributeUseImpl[] oldArray, final int newSize) {
        final XSAttributeUseImpl[] newArray = new XSAttributeUseImpl[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
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
    
    @Override
    public short getType() {
        return 5;
    }
    
    @Override
    public String getName() {
        return this.fName;
    }
    
    @Override
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    @Override
    public XSObjectList getAttributeUses() {
        if (this.fAttrUses == null) {
            this.fAttrUses = new XSObjectListImpl(this.fAttributeUses, this.fAttrUseNum);
        }
        return this.fAttrUses;
    }
    
    @Override
    public XSWildcard getAttributeWildcard() {
        return this.fAttributeWC;
    }
    
    @Override
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    @Override
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }
    
    void setNamespaceItem(final XSNamespaceItem namespaceItem) {
        this.fNamespaceItem = namespaceItem;
    }
}
