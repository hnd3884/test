package org.apache.xerces.xs;

public interface XSAttributeDeclaration extends XSObject
{
    XSSimpleTypeDefinition getTypeDefinition();
    
    short getScope();
    
    XSComplexTypeDefinition getEnclosingCTDefinition();
    
    XSObject getParent();
    
    short getConstraintType();
    
    String getConstraintValue();
    
    Object getActualVC() throws XSException;
    
    short getActualVCType() throws XSException;
    
    ShortList getItemValueTypes() throws XSException;
    
    XSValue getValueConstraintValue();
    
    XSAnnotation getAnnotation();
    
    XSObjectList getAnnotations();
    
    boolean getInheritable();
}
