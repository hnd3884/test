package javax.validation.metadata;

import java.util.Set;
import java.util.List;

public interface ExecutableDescriptor extends ElementDescriptor
{
    String getName();
    
    List<ParameterDescriptor> getParameterDescriptors();
    
    CrossParameterDescriptor getCrossParameterDescriptor();
    
    ReturnValueDescriptor getReturnValueDescriptor();
    
    boolean hasConstrainedParameters();
    
    boolean hasConstrainedReturnValue();
    
    boolean hasConstraints();
    
    Set<ConstraintDescriptor<?>> getConstraintDescriptors();
    
    ConstraintFinder findConstraints();
}
