package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;

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
    XSComplexTypeDecl fEnclosingCT;
    XSObjectList fAnnotations;
    ValidatedInfo fDefault;
    private XSNamespaceItem fNamespaceItem;
    
    public XSAttributeDecl() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fType = null;
        this.fUnresolvedTypeName = null;
        this.fConstraintType = 0;
        this.fScope = 0;
        this.fEnclosingCT = null;
        this.fAnnotations = null;
        this.fDefault = null;
        this.fNamespaceItem = null;
    }
    
    public void setValues(final String name, final String targetNamespace, final XSSimpleType simpleType, final short constraintType, final short scope, final ValidatedInfo valInfo, final XSComplexTypeDecl enclosingCT, final XSObjectList annotations) {
        this.fName = name;
        this.fTargetNamespace = targetNamespace;
        this.fType = simpleType;
        this.fConstraintType = constraintType;
        this.fScope = scope;
        this.fDefault = valInfo;
        this.fEnclosingCT = enclosingCT;
        this.fAnnotations = annotations;
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
    }
    
    @Override
    public short getType() {
        return 1;
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
    public XSSimpleTypeDefinition getTypeDefinition() {
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
        return this.fConstraintType;
    }
    
    @Override
    public String getConstraintValue() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.stringValue();
    }
    
    @Override
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    @Override
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    public ValidatedInfo getValInfo() {
        return this.fDefault;
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
