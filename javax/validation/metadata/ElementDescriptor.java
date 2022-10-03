package javax.validation.metadata;

import java.lang.annotation.ElementType;
import java.util.Set;

public interface ElementDescriptor
{
    boolean hasConstraints();
    
    Class<?> getElementClass();
    
    Set<ConstraintDescriptor<?>> getConstraintDescriptors();
    
    ConstraintFinder findConstraints();
    
    public interface ConstraintFinder
    {
        ConstraintFinder unorderedAndMatchingGroups(final Class<?>... p0);
        
        ConstraintFinder lookingAt(final Scope p0);
        
        ConstraintFinder declaredOn(final ElementType... p0);
        
        Set<ConstraintDescriptor<?>> getConstraintDescriptors();
        
        boolean hasConstraints();
    }
}
