package org.eclipse.jdt.internal.compiler.lookup;

public class SyntheticFactoryMethodBinding extends MethodBinding
{
    private MethodBinding staticFactoryFor;
    private LookupEnvironment environment;
    private ReferenceBinding enclosingType;
    
    public SyntheticFactoryMethodBinding(final MethodBinding method, final LookupEnvironment environment, final ReferenceBinding enclosingType) {
        super(method.modifiers | 0x8, TypeConstants.SYNTHETIC_STATIC_FACTORY, null, null, null, method.declaringClass);
        this.environment = environment;
        this.staticFactoryFor = method;
        this.enclosingType = enclosingType;
    }
    
    public MethodBinding getConstructor() {
        return this.staticFactoryFor;
    }
    
    public ParameterizedMethodBinding applyTypeArgumentsOnConstructor(final TypeBinding[] typeArguments, final TypeBinding[] constructorTypeArguments, final boolean inferredWithUncheckedConversion) {
        final ReferenceBinding parameterizedType = this.environment.createParameterizedType(this.declaringClass, typeArguments, this.enclosingType);
        MethodBinding[] methods;
        for (int length = (methods = parameterizedType.methods()).length, i = 0; i < length; ++i) {
            final MethodBinding parameterizedMethod = methods[i];
            if (parameterizedMethod.original() == this.staticFactoryFor) {
                return (constructorTypeArguments.length > 0 || inferredWithUncheckedConversion) ? this.environment.createParameterizedGenericMethod(parameterizedMethod, constructorTypeArguments, inferredWithUncheckedConversion, false) : ((ParameterizedMethodBinding)parameterizedMethod);
            }
            if (parameterizedMethod instanceof ProblemMethodBinding) {
                final MethodBinding closestMatch = ((ProblemMethodBinding)parameterizedMethod).closestMatch;
                if (closestMatch instanceof ParameterizedMethodBinding && closestMatch.original() == this.staticFactoryFor) {
                    return (ParameterizedMethodBinding)closestMatch;
                }
            }
        }
        throw new IllegalArgumentException("Type doesn't have its own method?");
    }
}
