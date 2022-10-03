package javax.validation.metadata;

import java.util.Set;

public interface BeanDescriptor extends ElementDescriptor
{
    boolean isBeanConstrained();
    
    PropertyDescriptor getConstraintsForProperty(final String p0);
    
    Set<PropertyDescriptor> getConstrainedProperties();
    
    MethodDescriptor getConstraintsForMethod(final String p0, final Class<?>... p1);
    
    Set<MethodDescriptor> getConstrainedMethods(final MethodType p0, final MethodType... p1);
    
    ConstructorDescriptor getConstraintsForConstructor(final Class<?>... p0);
    
    Set<ConstructorDescriptor> getConstrainedConstructors();
}
