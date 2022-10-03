package org.apache.xerces.xs;

public interface XSComplexTypeDefinition extends XSTypeDefinition
{
    public static final short CONTENTTYPE_EMPTY = 0;
    public static final short CONTENTTYPE_SIMPLE = 1;
    public static final short CONTENTTYPE_ELEMENT = 2;
    public static final short CONTENTTYPE_MIXED = 3;
    
    short getDerivationMethod();
    
    boolean getAbstract();
    
    XSObjectList getAttributeUses();
    
    XSWildcard getAttributeWildcard();
    
    short getContentType();
    
    XSSimpleTypeDefinition getSimpleType();
    
    XSParticle getParticle();
    
    boolean isProhibitedSubstitution(final short p0);
    
    short getProhibitedSubstitutions();
    
    XSObjectList getAnnotations();
    
    XSOpenContent getOpenContent();
    
    XSObjectList getAssertions();
}
