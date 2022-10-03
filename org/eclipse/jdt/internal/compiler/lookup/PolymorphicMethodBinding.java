package org.eclipse.jdt.internal.compiler.lookup;

public class PolymorphicMethodBinding extends MethodBinding
{
    protected MethodBinding polymorphicMethod;
    
    public PolymorphicMethodBinding(final MethodBinding polymorphicMethod, final TypeBinding[] parameterTypes) {
        super(polymorphicMethod.modifiers, polymorphicMethod.selector, polymorphicMethod.returnType, parameterTypes, polymorphicMethod.thrownExceptions, polymorphicMethod.declaringClass);
        this.polymorphicMethod = polymorphicMethod;
        this.tagBits = polymorphicMethod.tagBits;
    }
    
    public PolymorphicMethodBinding(final MethodBinding polymorphicMethod, final TypeBinding returnType, final TypeBinding[] parameterTypes) {
        super(polymorphicMethod.modifiers, polymorphicMethod.selector, returnType, parameterTypes, polymorphicMethod.thrownExceptions, polymorphicMethod.declaringClass);
        this.polymorphicMethod = polymorphicMethod;
        this.tagBits = polymorphicMethod.tagBits;
    }
    
    @Override
    public MethodBinding original() {
        return this.polymorphicMethod;
    }
    
    @Override
    public boolean isPolymorphic() {
        return true;
    }
    
    public boolean matches(final TypeBinding[] matchingParameters, final TypeBinding matchingReturnType) {
        final int cachedParametersLength = (this.parameters == null) ? 0 : this.parameters.length;
        final int matchingParametersLength = (matchingParameters == null) ? 0 : matchingParameters.length;
        if (matchingParametersLength != cachedParametersLength) {
            return false;
        }
        for (int j = 0; j < cachedParametersLength; ++j) {
            if (TypeBinding.notEquals(this.parameters[j], matchingParameters[j])) {
                return false;
            }
        }
        final TypeBinding cachedReturnType = this.returnType;
        if (matchingReturnType == null) {
            if (cachedReturnType != null) {
                return false;
            }
        }
        else {
            if (cachedReturnType == null) {
                return false;
            }
            if (TypeBinding.notEquals(matchingReturnType, cachedReturnType)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean isVarargs() {
        return false;
    }
}
