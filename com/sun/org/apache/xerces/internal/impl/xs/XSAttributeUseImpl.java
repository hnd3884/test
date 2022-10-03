package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;

public class XSAttributeUseImpl implements XSAttributeUse
{
    public XSAttributeDecl fAttrDecl;
    public short fUse;
    public short fConstraintType;
    public ValidatedInfo fDefault;
    public XSObjectList fAnnotations;
    
    public XSAttributeUseImpl() {
        this.fAttrDecl = null;
        this.fUse = 0;
        this.fConstraintType = 0;
        this.fDefault = null;
        this.fAnnotations = null;
    }
    
    public void reset() {
        this.fDefault = null;
        this.fAttrDecl = null;
        this.fUse = 0;
        this.fConstraintType = 0;
        this.fAnnotations = null;
    }
    
    @Override
    public short getType() {
        return 4;
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public String getNamespace() {
        return null;
    }
    
    @Override
    public boolean getRequired() {
        return this.fUse == 1;
    }
    
    @Override
    public XSAttributeDeclaration getAttrDeclaration() {
        return this.fAttrDecl;
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
    public XSNamespaceItem getNamespaceItem() {
        return null;
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
    
    @Override
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
}
