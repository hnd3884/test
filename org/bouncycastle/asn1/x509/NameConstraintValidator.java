package org.bouncycastle.asn1.x509;

public interface NameConstraintValidator
{
    void checkPermitted(final GeneralName p0) throws NameConstraintValidatorException;
    
    void checkExcluded(final GeneralName p0) throws NameConstraintValidatorException;
    
    void intersectPermittedSubtree(final GeneralSubtree p0);
    
    void intersectPermittedSubtree(final GeneralSubtree[] p0);
    
    void intersectEmptyPermittedSubtree(final int p0);
    
    void addExcludedSubtree(final GeneralSubtree p0);
}
