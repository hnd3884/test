package com.sun.org.apache.xerces.internal.xs;

public interface XSElementDeclaration extends XSTerm
{
    XSTypeDefinition getTypeDefinition();
    
    short getScope();
    
    XSComplexTypeDefinition getEnclosingCTDefinition();
    
    short getConstraintType();
    
    String getConstraintValue();
    
    Object getActualVC() throws XSException;
    
    short getActualVCType() throws XSException;
    
    ShortList getItemValueTypes() throws XSException;
    
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
