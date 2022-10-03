package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.XSValue;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.impl.xs.util.XSNamedMapImpl;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.impl.xs.alternative.XSTypeAlternativeImpl;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;

public class XSElementDecl implements XSElementDeclaration
{
    public static final short SCOPE_ABSENT = 0;
    public static final short SCOPE_GLOBAL = 1;
    public static final short SCOPE_LOCAL = 2;
    public String fName;
    public String fTargetNamespace;
    public XSTypeDefinition fType;
    public QName fUnresolvedTypeName;
    short fMiscFlags;
    public short fScope;
    XSObject fEnclosingParent;
    public short fBlock;
    public short fFinal;
    public XSObjectList fAnnotations;
    public ValidatedInfo fDefault;
    public XSElementDecl[] fSubGroup;
    static final int INITIAL_SIZE = 2;
    int fIDCPos;
    IdentityConstraint[] fIDConstraints;
    int fTypeAlternativePos;
    XSTypeAlternativeImpl[] fTypeAlternatives;
    XSTypeAlternativeImpl fDefaultTypeDef;
    private XSNamespaceItem fNamespaceItem;
    private static final short CONSTRAINT_MASK = 3;
    private static final short NILLABLE = 4;
    private static final short ABSTRACT = 8;
    private String fDescription;
    
    public XSElementDecl() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fType = null;
        this.fUnresolvedTypeName = null;
        this.fMiscFlags = 0;
        this.fScope = 0;
        this.fEnclosingParent = null;
        this.fBlock = 0;
        this.fFinal = 0;
        this.fAnnotations = null;
        this.fDefault = null;
        this.fSubGroup = null;
        this.fIDCPos = 0;
        this.fIDConstraints = new IdentityConstraint[2];
        this.fTypeAlternativePos = 0;
        this.fTypeAlternatives = new XSTypeAlternativeImpl[2];
        this.fDefaultTypeDef = null;
        this.fNamespaceItem = null;
        this.fDescription = null;
    }
    
    public void setConstraintType(final short n) {
        this.fMiscFlags ^= (short)(this.fMiscFlags & 0x3);
        this.fMiscFlags |= (short)(n & 0x3);
    }
    
    public void setIsNillable() {
        this.fMiscFlags |= 0x4;
    }
    
    public void setIsAbstract() {
        this.fMiscFlags |= 0x8;
    }
    
    public void setIsGlobal() {
        this.fScope = 1;
    }
    
    public void setIsLocal(final XSObject fEnclosingParent) {
        this.fScope = 2;
        this.fEnclosingParent = fEnclosingParent;
    }
    
    public void addIDConstraint(final IdentityConstraint identityConstraint) {
        if (this.fIDCPos == this.fIDConstraints.length) {
            this.fIDConstraints = resize(this.fIDConstraints, this.fIDCPos * 2);
        }
        this.fIDConstraints[this.fIDCPos++] = identityConstraint;
    }
    
    public IdentityConstraint[] getIDConstraints() {
        if (this.fIDCPos == 0) {
            return null;
        }
        if (this.fIDCPos < this.fIDConstraints.length) {
            this.fIDConstraints = resize(this.fIDConstraints, this.fIDCPos);
        }
        return this.fIDConstraints;
    }
    
    static final IdentityConstraint[] resize(final IdentityConstraint[] array, final int n) {
        final IdentityConstraint[] array2 = new IdentityConstraint[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    public boolean isTypeTableOK() {
        if (this.fTypeAlternativePos > 1) {
            for (int i = 0; i < this.fTypeAlternativePos - 1; ++i) {
                if (this.fTypeAlternatives[i].getTest() == null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void addTypeAlternative(final XSTypeAlternativeImpl xsTypeAlternativeImpl) {
        if (this.fTypeAlternativePos == this.fTypeAlternatives.length) {
            this.fTypeAlternatives = resize(this.fTypeAlternatives, this.fTypeAlternativePos * 2);
        }
        this.fTypeAlternatives[this.fTypeAlternativePos++] = xsTypeAlternativeImpl;
    }
    
    public XSTypeAlternativeImpl[] getTypeAlternatives() {
        if (this.fTypeAlternativePos == 0) {
            return null;
        }
        if (this.fTypeAlternativePos < this.fTypeAlternatives.length) {
            this.fTypeAlternatives = resize(this.fTypeAlternatives, this.fTypeAlternativePos);
        }
        return this.fTypeAlternatives;
    }
    
    public XSTypeAlternativeImpl getDefaultTypeDefinition() {
        return this.fDefaultTypeDef;
    }
    
    public void setDefaultTypeDefinition() {
        if (this.fTypeAlternativePos == 0) {
            this.fDefaultTypeDef = null;
        }
        else if (this.fTypeAlternatives[this.fTypeAlternativePos - 1].getTest() == null) {
            this.fDefaultTypeDef = this.fTypeAlternatives[this.fTypeAlternativePos - 1];
        }
        else {
            this.fDefaultTypeDef = new XSTypeAlternativeImpl(this.fName, this.fType, XSObjectListImpl.EMPTY_LIST);
        }
    }
    
    static final XSTypeAlternativeImpl[] resize(final XSTypeAlternativeImpl[] array, final int n) {
        final XSTypeAlternativeImpl[] array2 = new XSTypeAlternativeImpl[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    public String toString() {
        if (this.fDescription == null) {
            if (this.fTargetNamespace != null) {
                final StringBuffer sb = new StringBuffer(this.fTargetNamespace.length() + ((this.fName != null) ? this.fName.length() : 4) + 3);
                sb.append('\"');
                sb.append(this.fTargetNamespace);
                sb.append('\"');
                sb.append(':');
                sb.append(this.fName);
                this.fDescription = sb.toString();
            }
            else {
                this.fDescription = this.fName;
            }
        }
        return this.fDescription;
    }
    
    public int hashCode() {
        int hashCode = this.fName.hashCode();
        if (this.fTargetNamespace != null) {
            hashCode = (hashCode << 16) + this.fTargetNamespace.hashCode();
        }
        return hashCode;
    }
    
    public boolean equals(final Object o) {
        return o == this;
    }
    
    public void reset() {
        this.fScope = 0;
        this.fName = null;
        this.fTargetNamespace = null;
        this.fType = null;
        this.fUnresolvedTypeName = null;
        this.fMiscFlags = 0;
        this.fBlock = 0;
        this.fFinal = 0;
        this.fDefault = null;
        this.fAnnotations = null;
        this.fSubGroup = null;
        for (int i = 0; i < this.fIDCPos; ++i) {
            this.fIDConstraints[i] = null;
        }
        this.fIDCPos = 0;
    }
    
    public short getType() {
        return 2;
    }
    
    public String getName() {
        return this.fName;
    }
    
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    public XSTypeDefinition getTypeDefinition() {
        return this.fType;
    }
    
    public short getScope() {
        return this.fScope;
    }
    
    public XSComplexTypeDefinition getEnclosingCTDefinition() {
        return (this.fEnclosingParent instanceof XSComplexTypeDecl) ? ((XSComplexTypeDecl)this.fEnclosingParent) : null;
    }
    
    public XSObject getParent() {
        return this.fEnclosingParent;
    }
    
    public short getConstraintType() {
        return (short)(this.fMiscFlags & 0x3);
    }
    
    public String getConstraintValue() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.stringValue();
    }
    
    public boolean getNillable() {
        return (this.fMiscFlags & 0x4) != 0x0;
    }
    
    public XSNamedMap getIdentityConstraints() {
        return new XSNamedMapImpl(this.fIDConstraints, this.fIDCPos);
    }
    
    public XSElementDeclaration getSubstitutionGroupAffiliation() {
        return (this.fSubGroup != null && this.fSubGroup.length > 0) ? this.fSubGroup[0] : null;
    }
    
    public boolean isSubstitutionGroupExclusion(final short n) {
        return (this.fFinal & n) != 0x0;
    }
    
    public short getSubstitutionGroupExclusions() {
        return this.fFinal;
    }
    
    public boolean isDisallowedSubstitution(final short n) {
        return (this.fBlock & n) != 0x0;
    }
    
    public short getDisallowedSubstitutions() {
        return this.fBlock;
    }
    
    public boolean getAbstract() {
        return (this.fMiscFlags & 0x8) != 0x0;
    }
    
    public void addAnnotation(final XSAnnotation xsAnnotation) {
        if (this.fAnnotations == XSObjectListImpl.EMPTY_LIST) {
            this.fAnnotations = new XSObjectListImpl();
        }
        ((XSObjectListImpl)this.fAnnotations).addXSObject(xsAnnotation);
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
    
    public Object getActualVC() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.actualValue;
    }
    
    public short getActualVCType() {
        return (short)((this.getConstraintType() == 0) ? 45 : this.fDefault.actualValueType);
    }
    
    public ShortList getItemValueTypes() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.itemValueTypes;
    }
    
    public XSValue getValueConstraintValue() {
        return this.fDefault;
    }
}
