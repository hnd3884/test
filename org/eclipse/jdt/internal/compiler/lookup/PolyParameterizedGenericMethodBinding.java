package org.eclipse.jdt.internal.compiler.lookup;

public class PolyParameterizedGenericMethodBinding extends ParameterizedGenericMethodBinding
{
    private ParameterizedGenericMethodBinding wrappedBinding;
    
    public PolyParameterizedGenericMethodBinding(final ParameterizedGenericMethodBinding applicableMethod) {
        super(applicableMethod.originalMethod, applicableMethod.typeArguments, applicableMethod.environment, false, false);
        this.wrappedBinding = applicableMethod;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof PolyParameterizedGenericMethodBinding) {
            final PolyParameterizedGenericMethodBinding ppgmb = (PolyParameterizedGenericMethodBinding)other;
            return this.wrappedBinding.equals(ppgmb.wrappedBinding);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.wrappedBinding.hashCode();
    }
}
