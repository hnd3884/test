package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.XSValue;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xs.XSAttributeDeclaration;

public class XSAttributeDecl implements XSAttributeDeclaration
{
    public static final short SCOPE_ABSENT = 0;
    public static final short SCOPE_GLOBAL = 1;
    public static final short SCOPE_LOCAL = 2;
    String fName;
    String fTargetNamespace;
    XSSimpleType fType;
    public QName fUnresolvedTypeName;
    short fConstraintType;
    short fScope;
    XSObject fEnclosingParent;
    XSObjectList fAnnotations;
    ValidatedInfo fDefault;
    private XSNamespaceItem fNamespaceItem;
    boolean fInheritable;
    
    public XSAttributeDecl() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fType = null;
        this.fUnresolvedTypeName = null;
        this.fConstraintType = 0;
        this.fScope = 0;
        this.fEnclosingParent = null;
        this.fAnnotations = null;
        this.fDefault = null;
        this.fNamespaceItem = null;
        this.fInheritable = false;
    }
    
    public void setValues(final String fName, final String fTargetNamespace, final XSSimpleType fType, final short fConstraintType, final short fScope, final ValidatedInfo fDefault, final XSObject fEnclosingParent, final XSObjectList fAnnotations, final boolean fInheritable) {
        this.fName = fName;
        this.fTargetNamespace = fTargetNamespace;
        this.fType = fType;
        this.fConstraintType = fConstraintType;
        this.fScope = fScope;
        this.fDefault = fDefault;
        this.fEnclosingParent = fEnclosingParent;
        this.fAnnotations = fAnnotations;
        this.fInheritable = fInheritable;
    }
    
    public void reset() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fType = null;
        this.fUnresolvedTypeName = null;
        this.fConstraintType = 0;
        this.fScope = 0;
        this.fDefault = null;
        this.fAnnotations = null;
        this.fInheritable = false;
    }
    
    public short getType() {
        return 1;
    }
    
    public String getName() {
        return this.fName;
    }
    
    public String getNamespace() {
        return this.fTargetNamespace;
    }
    
    public XSSimpleTypeDefinition getTypeDefinition() {
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
        return this.fConstraintType;
    }
    
    public String getConstraintValue() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.stringValue();
    }
    
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    public ValidatedInfo getValInfo() {
        return this.fDefault;
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
    
    public boolean getInheritable() {
        return this.fInheritable;
    }
}
