package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSValue;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.XSAttributeUse;

public class XSAttributeUseImpl implements XSAttributeUse
{
    public XSAttributeDecl fAttrDecl;
    public short fUse;
    public short fConstraintType;
    public ValidatedInfo fDefault;
    public XSObjectList fAnnotations;
    public boolean fInheritable;
    
    public XSAttributeUseImpl() {
        this.fAttrDecl = null;
        this.fUse = 0;
        this.fConstraintType = 0;
        this.fDefault = null;
        this.fAnnotations = null;
        this.fInheritable = false;
    }
    
    public void reset() {
        this.fDefault = null;
        this.fAttrDecl = null;
        this.fUse = 0;
        this.fConstraintType = 0;
        this.fAnnotations = null;
        this.fInheritable = false;
    }
    
    public short getType() {
        return 4;
    }
    
    public String getName() {
        return null;
    }
    
    public String getNamespace() {
        return null;
    }
    
    public boolean getRequired() {
        return this.fUse == 1;
    }
    
    public XSAttributeDeclaration getAttrDeclaration() {
        return this.fAttrDecl;
    }
    
    public short getConstraintType() {
        return this.fConstraintType;
    }
    
    public String getConstraintValue() {
        return (this.getConstraintType() == 0) ? null : this.fDefault.stringValue();
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
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
    
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    public boolean getInheritable() {
        return this.fInheritable;
    }
}
