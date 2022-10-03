package org.apache.xerces.xs;

public interface XSElementDeclaration extends XSTerm
{
    XSTypeDefinition getTypeDefinition();
    
    short getScope();
    
    XSComplexTypeDefinition getEnclosingCTDefinition();
    
    XSObject getParent();
    
    short getConstraintType();
    
    String getConstraintValue();
    
    Object getActualVC() throws XSException;
    
    short getActualVCType() throws XSException;
    
    ShortList getItemValueTypes() throws XSException;
    
    XSValue getValueConstraintValue();
    
    boolean getNillable();
    
    XSNamedMap getIdentityConstraints();
    
    XSElementDeclaration getSubstitutionGroupAffiliation();
    
    boolean isSubstitutionGroupExclusion(final short p0);
    
    short getSubstitutionGroupExclusions();
    
    boolean isDisallowedSubstitution(final short p0);
    
    short getDisallowedSubstitutions();
    
    boolean getAbstract();
    
    XSAnnotation getAnnotation();
    
    XSObjectList getAnnotations();
}
