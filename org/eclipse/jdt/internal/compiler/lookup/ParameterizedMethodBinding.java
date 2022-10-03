package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;

public class ParameterizedMethodBinding extends MethodBinding
{
    protected MethodBinding originalMethod;
    
    public ParameterizedMethodBinding(final ParameterizedTypeBinding parameterizedDeclaringClass, final MethodBinding originalMethod) {
        super(originalMethod.modifiers, originalMethod.selector, originalMethod.returnType, originalMethod.parameters, originalMethod.thrownExceptions, parameterizedDeclaringClass);
        this.originalMethod = originalMethod;
        this.tagBits = (originalMethod.tagBits & 0xFFFFFFFFFFFFFF7FL);
        this.parameterNonNullness = originalMethod.parameterNonNullness;
        this.defaultNullness = originalMethod.defaultNullness;
        final TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
        Substitution substitution = null;
        final int length = originalVariables.length;
        final boolean isStatic = originalMethod.isStatic();
        if (length == 0) {
            this.typeVariables = Binding.NO_TYPE_VARIABLES;
            if (!isStatic) {
                substitution = parameterizedDeclaringClass;
            }
        }
        else {
            final TypeVariableBinding[] substitutedVariables = new TypeVariableBinding[length];
            for (int i = 0; i < length; ++i) {
                final TypeVariableBinding originalVariable = originalVariables[i];
                substitutedVariables[i] = new TypeVariableBinding(originalVariable.sourceName, this, originalVariable.rank, parameterizedDeclaringClass.environment);
                final TypeVariableBinding typeVariableBinding = substitutedVariables[i];
                typeVariableBinding.tagBits |= (originalVariable.tagBits & 0x180000000100000L);
            }
            this.typeVariables = substitutedVariables;
            substitution = new Substitution() {
                @Override
                public LookupEnvironment environment() {
                    return parameterizedDeclaringClass.environment;
                }
                
                @Override
                public boolean isRawSubstitution() {
                    return !isStatic && parameterizedDeclaringClass.isRawSubstitution();
                }
                
                @Override
                public TypeBinding substitute(final TypeVariableBinding typeVariable) {
                    if (typeVariable.rank < length && TypeBinding.equalsEquals(originalVariables[typeVariable.rank], typeVariable)) {
                        final TypeBinding substitute = substitutedVariables[typeVariable.rank];
                        return typeVariable.hasTypeAnnotations() ? this.environment().createAnnotatedType(substitute, typeVariable.getTypeAnnotations()) : substitute;
                    }
                    if (!isStatic) {
                        return parameterizedDeclaringClass.substitute(typeVariable);
                    }
                    return typeVariable;
                }
            };
            for (int i = 0; i < length; ++i) {
                final TypeVariableBinding originalVariable = originalVariables[i];
                final TypeVariableBinding substitutedVariable = substitutedVariables[i];
                final TypeBinding substitutedSuperclass = Scope.substitute(substitution, originalVariable.superclass);
                ReferenceBinding[] substitutedInterfaces = Scope.substitute(substitution, originalVariable.superInterfaces);
                if (originalVariable.firstBound != null) {
                    final TypeBinding firstBound = TypeBinding.equalsEquals(originalVariable.firstBound, originalVariable.superclass) ? substitutedSuperclass : substitutedInterfaces[0];
                    substitutedVariable.setFirstBound(firstBound);
                }
                switch (substitutedSuperclass.kind()) {
                    case 68: {
                        substitutedVariable.setSuperClass(parameterizedDeclaringClass.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
                        substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                        break;
                    }
                    default: {
                        if (substitutedSuperclass.isInterface()) {
                            substitutedVariable.setSuperClass(parameterizedDeclaringClass.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
                            final int interfaceCount = substitutedInterfaces.length;
                            System.arraycopy(substitutedInterfaces, 0, substitutedInterfaces = new ReferenceBinding[interfaceCount + 1], 1, interfaceCount);
                            substitutedInterfaces[0] = (ReferenceBinding)substitutedSuperclass;
                            substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                            break;
                        }
                        substitutedVariable.setSuperClass((ReferenceBinding)substitutedSuperclass);
                        substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                        break;
                    }
                }
            }
        }
        if (substitution != null) {
            this.returnType = Scope.substitute(substitution, this.returnType);
            this.parameters = Scope.substitute(substitution, this.parameters);
            this.thrownExceptions = Scope.substitute(substitution, this.thrownExceptions);
            if (this.thrownExceptions == null) {
                this.thrownExceptions = Binding.NO_EXCEPTIONS;
            }
            if (parameterizedDeclaringClass.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                final long returnNullBits = NullAnnotationMatching.validNullTagBits(this.returnType.tagBits);
                if (returnNullBits != 0L) {
                    this.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                    this.tagBits |= returnNullBits;
                }
                for (int parametersLen = this.parameters.length, j = 0; j < parametersLen; ++j) {
                    final long paramTagBits = NullAnnotationMatching.validNullTagBits(this.parameters[j].tagBits);
                    if (paramTagBits != 0L) {
                        if (this.parameterNonNullness == null) {
                            this.parameterNonNullness = new Boolean[parametersLen];
                        }
                        this.parameterNonNullness[j] = (paramTagBits == 72057594037927936L);
                    }
                }
            }
        }
        if ((this.tagBits & 0x80L) == 0x0L) {
            if ((this.returnType.tagBits & 0x80L) != 0x0L) {
                this.tagBits |= 0x80L;
            }
            else {
                for (int k = 0, max = this.parameters.length; k < max; ++k) {
                    if ((this.parameters[k].tagBits & 0x80L) != 0x0L) {
                        this.tagBits |= 0x80L;
                        return;
                    }
                }
                for (int k = 0, max = this.thrownExceptions.length; k < max; ++k) {
                    if ((this.thrownExceptions[k].tagBits & 0x80L) != 0x0L) {
                        this.tagBits |= 0x80L;
                        break;
                    }
                }
            }
        }
    }
    
    public ParameterizedMethodBinding(final ReferenceBinding declaringClass, final MethodBinding originalMethod, final char[][] alternateParamaterNames, final LookupEnvironment environment) {
        super(originalMethod.modifiers, originalMethod.selector, originalMethod.returnType, originalMethod.parameters, originalMethod.thrownExceptions, declaringClass);
        this.originalMethod = originalMethod;
        this.tagBits = (originalMethod.tagBits & 0xFFFFFFFFFFFFFF7FL);
        this.parameterNonNullness = originalMethod.parameterNonNullness;
        this.defaultNullness = originalMethod.defaultNullness;
        final TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
        Substitution substitution = null;
        final int length = originalVariables.length;
        if (length == 0) {
            this.typeVariables = Binding.NO_TYPE_VARIABLES;
        }
        else {
            final TypeVariableBinding[] substitutedVariables = new TypeVariableBinding[length];
            for (int i = 0; i < length; ++i) {
                final TypeVariableBinding originalVariable = originalVariables[i];
                substitutedVariables[i] = new TypeVariableBinding((alternateParamaterNames == null) ? originalVariable.sourceName : alternateParamaterNames[i], this, originalVariable.rank, environment);
                final TypeVariableBinding typeVariableBinding = substitutedVariables[i];
                typeVariableBinding.tagBits |= (originalVariable.tagBits & 0x180000000100000L);
            }
            this.typeVariables = substitutedVariables;
            substitution = new Substitution() {
                @Override
                public LookupEnvironment environment() {
                    return environment;
                }
                
                @Override
                public boolean isRawSubstitution() {
                    return false;
                }
                
                @Override
                public TypeBinding substitute(final TypeVariableBinding typeVariable) {
                    if (typeVariable.rank < length && TypeBinding.equalsEquals(originalVariables[typeVariable.rank], typeVariable)) {
                        final TypeBinding substitute = substitutedVariables[typeVariable.rank];
                        return typeVariable.hasTypeAnnotations() ? this.environment().createAnnotatedType(substitute, typeVariable.getTypeAnnotations()) : substitute;
                    }
                    return typeVariable;
                }
            };
            for (int i = 0; i < length; ++i) {
                final TypeVariableBinding originalVariable = originalVariables[i];
                final TypeVariableBinding substitutedVariable = substitutedVariables[i];
                final TypeBinding substitutedSuperclass = Scope.substitute(substitution, originalVariable.superclass);
                ReferenceBinding[] substitutedInterfaces = Scope.substitute(substitution, originalVariable.superInterfaces);
                if (originalVariable.firstBound != null) {
                    final TypeBinding firstBound = TypeBinding.equalsEquals(originalVariable.firstBound, originalVariable.superclass) ? substitutedSuperclass : substitutedInterfaces[0];
                    substitutedVariable.setFirstBound(firstBound);
                }
                switch (substitutedSuperclass.kind()) {
                    case 68: {
                        substitutedVariable.setSuperClass(environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
                        substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                        break;
                    }
                    default: {
                        if (substitutedSuperclass.isInterface()) {
                            substitutedVariable.setSuperClass(environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
                            final int interfaceCount = substitutedInterfaces.length;
                            System.arraycopy(substitutedInterfaces, 0, substitutedInterfaces = new ReferenceBinding[interfaceCount + 1], 1, interfaceCount);
                            substitutedInterfaces[0] = (ReferenceBinding)substitutedSuperclass;
                            substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                            break;
                        }
                        substitutedVariable.setSuperClass((ReferenceBinding)substitutedSuperclass);
                        substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                        break;
                    }
                }
            }
        }
        if (substitution != null) {
            this.returnType = Scope.substitute(substitution, this.returnType);
            this.parameters = Scope.substitute(substitution, this.parameters);
            this.thrownExceptions = Scope.substitute(substitution, this.thrownExceptions);
            if (this.thrownExceptions == null) {
                this.thrownExceptions = Binding.NO_EXCEPTIONS;
            }
        }
        if ((this.tagBits & 0x80L) == 0x0L) {
            if ((this.returnType.tagBits & 0x80L) != 0x0L) {
                this.tagBits |= 0x80L;
            }
            else {
                for (int j = 0, max = this.parameters.length; j < max; ++j) {
                    if ((this.parameters[j].tagBits & 0x80L) != 0x0L) {
                        this.tagBits |= 0x80L;
                        return;
                    }
                }
                for (int j = 0, max = this.thrownExceptions.length; j < max; ++j) {
                    if ((this.thrownExceptions[j].tagBits & 0x80L) != 0x0L) {
                        this.tagBits |= 0x80L;
                        break;
                    }
                }
            }
        }
    }
    
    public ParameterizedMethodBinding() {
    }
    
    public static ParameterizedMethodBinding instantiateGetClass(final TypeBinding receiverType, final MethodBinding originalMethod, final Scope scope) {
        final ParameterizedMethodBinding method = new ParameterizedMethodBinding();
        method.modifiers = originalMethod.modifiers;
        method.selector = originalMethod.selector;
        method.declaringClass = originalMethod.declaringClass;
        method.typeVariables = Binding.NO_TYPE_VARIABLES;
        method.originalMethod = originalMethod;
        method.parameters = originalMethod.parameters;
        method.thrownExceptions = originalMethod.thrownExceptions;
        method.tagBits = originalMethod.tagBits;
        final ReferenceBinding genericClassType = scope.getJavaLangClass();
        final LookupEnvironment environment = scope.environment();
        TypeBinding rawType = environment.convertToRawType(receiverType.erasure(), false);
        if (environment.usesNullTypeAnnotations()) {
            rawType = environment.createAnnotatedType(rawType, new AnnotationBinding[] { environment.getNonNullAnnotation() });
        }
        method.returnType = environment.createParameterizedType(genericClassType, new TypeBinding[] { environment.createWildcard(genericClassType, 0, rawType, null, 1) }, null);
        if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (environment.usesNullTypeAnnotations()) {
                method.returnType = environment.createAnnotatedType(method.returnType, new AnnotationBinding[] { environment.getNonNullAnnotation() });
            }
            else {
                final ParameterizedMethodBinding parameterizedMethodBinding = method;
                parameterizedMethodBinding.tagBits |= 0x100000000000000L;
            }
        }
        if ((method.returnType.tagBits & 0x80L) != 0x0L) {
            final ParameterizedMethodBinding parameterizedMethodBinding2 = method;
            parameterizedMethodBinding2.tagBits |= 0x80L;
        }
        return method;
    }
    
    @Override
    public boolean hasSubstitutedParameters() {
        return this.parameters != this.originalMethod.parameters;
    }
    
    @Override
    public boolean hasSubstitutedReturnType() {
        return this.returnType != this.originalMethod.returnType;
    }
    
    @Override
    public MethodBinding original() {
        return this.originalMethod.original();
    }
    
    @Override
    public MethodBinding shallowOriginal() {
        return this.originalMethod;
    }
}
