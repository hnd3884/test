package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;

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
    XSComplexTypeDecl fEnclosingCT;
    public short fBlock;
    public short fFinal;
    public XSObjectList fAnnotations;
    public ValidatedInfo fDefault;
    public XSElementDecl fSubGroup;
    static final int INITIAL_SIZE = 2;
    int fIDCPos;
    IdentityConstraint[] fIDConstraints;
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
        this.fEnclosingCT = null;
        this.fBlock = 0;
        this.fFinal = 0;
        this.fAnnotations = null;
        this.fDefault = null;
        this.fSubGroup = null;
        this.fIDCPos = 0;
        this.fIDConstraints = new IdentityConstraint[2];
        this.fNamespaceItem = null;
        this.fDescription = null;
    }
    
    public void setConstraintType(final short constraintType) {
        this.fMiscFlags ^= (short)(this.fMiscFlags & 0x3);
        this.fMiscFlags |= (short)(constraintType & 0x3);
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
    
    public void setIsLocal(final XSComplexTypeDecl enclosingCT) {
        this.fScope = 2;
        this.fEnclosingCT = enclosingCT;
    }
    
    public void addIDConstraint(final IdentityConstraint idc) {
        if (this.fIDCPos == this.fIDConstraints.length) {
            this.fIDConstraints = resize(this.fIDConstraints, this.fIDCPos * 2);
        }
        this.fIDConstraints[this.fIDCPos++] = idc;
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
    
    static final IdentityConstraint[] resize(final IdentityConstraint[] oldArray, final int newSize) {
        final IdentityConstraint[] newArray = new IdentityConstraint[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }
    
    @Override
    public String toString() {
        if (this.fDescription == null) {
            if (this.fTargetNamespace != null) {
                final StringBuffer buffer = new StringBuffer(this.fTargetNamespace.length() + ((this.fName != null) ? this.fName.length() : 4) + 3);
                buffer.append('\"');
                buffer.append(this.fTargetNamespace);
                buffer.append('\"');
                buffer.append(':');
                buffer.append(this.fName);
                this.fDescription = buffer.toString();
            }
            else {
                this.fDescription = this.fName;
            }
        }
        return this.fDescription;
    }
    
    @Override
    public int hashCode() {
        int code = this.fName.hashCode();
        if (this.fTargetNamespace != null) {
            code = (code << 16) + this.fTargetNamespace.hashCode();
        }
        return code;
    }
    
    @Override
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
    
    @Override
    public short getType() {
        return 2;
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
    public XSTypeDefinition getTypeDefinition() {
        return this.fType;
    }
    
    @Override
    public short getScope() {
        return this.fScope;
    }
    
    @Override
    public XSComplexTypeDefinition getEnclosingCTDefinition() {
        return this.fEnclosingCT;
    }
    
    @Override
    public short getConstraintType() {
        return (short)(this.fMiscFlags & 0x3);
    }
    
    @Override
    public String getConstraintValue() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.stringValue();
    }
    
    @Override
    public boolean getNillable() {
        return (this.fMiscFlags & 0x4) != 0x0;
    }
    
    @Override
    public XSNamedMap getIdentityConstraints() {
        return new XSNamedMapImpl(this.fIDConstraints, this.fIDCPos);
    }
    
    @Override
    public XSElementDeclaration getSubstitutionGroupAffiliation() {
        return this.fSubGroup;
    }
    
    @Override
    public boolean isSubstitutionGroupExclusion(final short exclusion) {
        return (this.fFinal & exclusion) != 0x0;
    }
    
    @Override
    public short getSubstitutionGroupExclusions() {
        return this.fFinal;
    }
    
    @Override
    public boolean isDisallowedSubstitution(final short disallowed) {
        return (this.fBlock & disallowed) != 0x0;
    }
    
    @Override
    public short getDisallowedSubstitutions() {
        return this.fBlock;
    }
    
    @Override
    public boolean getAbstract() {
        return (this.fMiscFlags & 0x8) != 0x0;
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
    
    @Override
    public Object getActualVC() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.actualValue;
    }
    
    @Override
    public short getActualVCType() {
        return (short)((this.getConstraintType() == 0) ? 45 : this.fDefault.actualValueType);
    }
    
    @Override
    public ShortList getItemValueTypes() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.itemValueTypes;
    }
}
