package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public class ParameterizedGenericMethodBinding extends ParameterizedMethodBinding implements Substitution
{
    public TypeBinding[] typeArguments;
    protected LookupEnvironment environment;
    public boolean inferredReturnType;
    public boolean wasInferred;
    public boolean isRaw;
    private MethodBinding tiebreakMethod;
    public boolean inferredWithUncheckedConversion;
    
    public static MethodBinding computeCompatibleMethod(final MethodBinding originalMethod, final TypeBinding[] arguments, final Scope scope, final InvocationSite invocationSite) {
        final TypeVariableBinding[] typeVariables = originalMethod.typeVariables;
        final TypeBinding[] substitutes = invocationSite.genericTypeArguments();
        InferenceContext inferenceContext = null;
        TypeBinding[] uncheckedArguments = null;
        ParameterizedGenericMethodBinding methodSubstitute;
        if (substitutes != null) {
            if (substitutes.length != typeVariables.length) {
                return new ProblemMethodBinding(originalMethod, originalMethod.selector, substitutes, 11);
            }
            methodSubstitute = scope.environment().createParameterizedGenericMethod(originalMethod, substitutes);
        }
        else {
            final TypeBinding[] parameters = originalMethod.parameters;
            final CompilerOptions compilerOptions = scope.compilerOptions();
            if (compilerOptions.sourceLevel >= 3407872L) {
                return computeCompatibleMethod18(originalMethod, arguments, scope, invocationSite);
            }
            inferenceContext = new InferenceContext(originalMethod);
            methodSubstitute = inferFromArgumentTypes(scope, originalMethod, arguments, parameters, inferenceContext);
            if (methodSubstitute == null) {
                return null;
            }
            if (inferenceContext.hasUnresolvedTypeArgument()) {
                if (inferenceContext.isUnchecked) {
                    final int length = inferenceContext.substitutes.length;
                    System.arraycopy(inferenceContext.substitutes, 0, uncheckedArguments = new TypeBinding[length], 0, length);
                }
                if (methodSubstitute.returnType != TypeBinding.VOID) {
                    TypeBinding expectedType = invocationSite.invocationTargetType();
                    if (expectedType != null) {
                        inferenceContext.hasExplicitExpectedType = true;
                    }
                    else {
                        expectedType = scope.getJavaLangObject();
                    }
                    inferenceContext.expectedType = expectedType;
                }
                methodSubstitute = methodSubstitute.inferFromExpectedType(scope, inferenceContext);
                if (methodSubstitute == null) {
                    return null;
                }
            }
            else if (compilerOptions.sourceLevel == 3342336L && methodSubstitute.returnType != TypeBinding.VOID) {
                final TypeBinding expectedType = invocationSite.invocationTargetType();
                if (expectedType != null && !originalMethod.returnType.mentionsAny(originalMethod.parameters, -1)) {
                    final TypeBinding uncaptured = methodSubstitute.returnType.uncapture(scope);
                    if (!methodSubstitute.returnType.isCompatibleWith(expectedType) && expectedType.isCompatibleWith(uncaptured)) {
                        final InferenceContext oldContext = inferenceContext;
                        inferenceContext = new InferenceContext(originalMethod);
                        originalMethod.returnType.collectSubstitutes(scope, expectedType, inferenceContext, 1);
                        final ParameterizedGenericMethodBinding substitute = inferFromArgumentTypes(scope, originalMethod, arguments, parameters, inferenceContext);
                        if (substitute != null && substitute.returnType.isCompatibleWith(expectedType)) {
                            if (scope.parameterCompatibilityLevel(substitute, arguments, false) > -1) {
                                methodSubstitute = substitute;
                            }
                            else {
                                inferenceContext = oldContext;
                            }
                        }
                        else {
                            inferenceContext = oldContext;
                        }
                    }
                }
            }
        }
        Substitution substitution = null;
        if (inferenceContext != null) {
            substitution = new LingeringTypeVariableEliminator(typeVariables, inferenceContext.substitutes, scope);
        }
        else {
            substitution = methodSubstitute;
        }
        int i;
        for (i = 0, final int length = typeVariables.length; i < length; ++i) {
            final TypeVariableBinding typeVariable = typeVariables[i];
            final TypeBinding substitute2 = methodSubstitute.typeArguments[i];
            TypeBinding substituteForChecks;
            if (substitute2 instanceof TypeVariableBinding) {
                substituteForChecks = substitute2;
            }
            else {
                substituteForChecks = Scope.substitute(new LingeringTypeVariableEliminator(typeVariables, null, scope), substitute2);
            }
            if (uncheckedArguments == null || uncheckedArguments[i] != null) {
                switch (typeVariable.boundCheck(substitution, substituteForChecks, scope, null)) {
                    case MISMATCH: {
                        final int argLength = arguments.length;
                        final TypeBinding[] augmentedArguments = new TypeBinding[argLength + 2];
                        System.arraycopy(arguments, 0, augmentedArguments, 0, argLength);
                        augmentedArguments[argLength] = substitute2;
                        augmentedArguments[argLength + 1] = typeVariable;
                        return new ProblemMethodBinding(methodSubstitute, originalMethod.selector, augmentedArguments, 10);
                    }
                    case UNCHECKED: {
                        final ParameterizedGenericMethodBinding parameterizedGenericMethodBinding = methodSubstitute;
                        parameterizedGenericMethodBinding.tagBits |= 0x100L;
                        break;
                    }
                }
            }
        }
        return methodSubstitute;
    }
    
    public static MethodBinding computeCompatibleMethod18(final MethodBinding originalMethod, TypeBinding[] arguments, final Scope scope, final InvocationSite invocationSite) {
        final TypeVariableBinding[] typeVariables = originalMethod.typeVariables;
        if (invocationSite.checkingPotentialCompatibility()) {
            return scope.environment().createParameterizedGenericMethod(originalMethod, typeVariables);
        }
        ParameterizedGenericMethodBinding methodSubstitute = null;
        InferenceContext18 infCtx18 = invocationSite.freshInferenceContext(scope);
        if (infCtx18 == null) {
            return originalMethod;
        }
        final TypeBinding[] parameters = originalMethod.parameters;
        final CompilerOptions compilerOptions = scope.compilerOptions();
        boolean invocationTypeInferred = false;
        boolean requireBoxing = false;
        final TypeBinding[] argumentsCopy = new TypeBinding[arguments.length];
        int i = 0;
        final int length = arguments.length;
        final int parametersLength = parameters.length;
        while (i < length) {
            final TypeBinding parameter = (i < parametersLength) ? parameters[i] : parameters[parametersLength - 1];
            final TypeBinding argument = arguments[i];
            if (argument.isPrimitiveType() != parameter.isPrimitiveType()) {
                argumentsCopy[i] = scope.environment().computeBoxingType(argument);
                requireBoxing = true;
            }
            else {
                argumentsCopy[i] = argument;
            }
            ++i;
        }
        arguments = argumentsCopy;
        final LookupEnvironment environment = scope.environment();
        final InferenceContext18 previousContext = environment.currentInferenceContext;
        if (previousContext == null) {
            environment.currentInferenceContext = infCtx18;
        }
        try {
            BoundSet provisionalResult = null;
            BoundSet result = null;
            final boolean isPolyExpression = invocationSite instanceof Expression && ((Expression)invocationSite).isPolyExpression(originalMethod);
            final boolean isDiamond = isPolyExpression && originalMethod.isConstructor();
            if (arguments.length == parameters.length) {
                infCtx18.inferenceKind = (requireBoxing ? 2 : 1);
                infCtx18.inferInvocationApplicability(originalMethod, arguments, isDiamond);
                result = infCtx18.solve(true);
            }
            if (result == null && originalMethod.isVarargs()) {
                infCtx18 = invocationSite.freshInferenceContext(scope);
                infCtx18.inferenceKind = 3;
                infCtx18.inferInvocationApplicability(originalMethod, arguments, isDiamond);
                result = infCtx18.solve(true);
            }
            if (result == null) {
                return null;
            }
            if (infCtx18.isResolved(result)) {
                infCtx18.stepCompleted = 1;
                final TypeBinding expectedType = invocationSite.invocationTargetType();
                boolean hasReturnProblem = false;
                if (expectedType != null || !invocationSite.getExpressionContext().definesTargetType() || !isPolyExpression) {
                    provisionalResult = result;
                    result = infCtx18.inferInvocationType(expectedType, invocationSite, originalMethod);
                    invocationTypeInferred = true;
                    hasReturnProblem |= (result == null);
                    if (hasReturnProblem) {
                        result = provisionalResult;
                    }
                }
                if (result != null) {
                    final TypeBinding[] solutions = infCtx18.getSolutions(typeVariables, invocationSite, result);
                    if (solutions != null) {
                        methodSubstitute = scope.environment().createParameterizedGenericMethod(originalMethod, solutions, infCtx18.usesUncheckedConversion, hasReturnProblem);
                        if (invocationSite instanceof Invocation) {
                            infCtx18.forwardResults(result, (Invocation)invocationSite, methodSubstitute, expectedType);
                        }
                        try {
                            if (hasReturnProblem) {
                                final MethodBinding problemMethod = infCtx18.getReturnProblemMethodIfNeeded(expectedType, methodSubstitute);
                                if (problemMethod instanceof ProblemMethodBinding) {
                                    return problemMethod;
                                }
                            }
                            if (invocationTypeInferred) {
                                if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
                                    NullAnnotationMatching.checkForContradictions(methodSubstitute, invocationSite, scope);
                                }
                                final MethodBinding problemMethod = methodSubstitute.boundCheck18(scope, arguments, invocationSite);
                                if (problemMethod != null) {
                                    return problemMethod;
                                }
                            }
                            else {
                                methodSubstitute = new PolyParameterizedGenericMethodBinding(methodSubstitute);
                            }
                        }
                        finally {
                            if (invocationSite instanceof Invocation) {
                                ((Invocation)invocationSite).registerInferenceContext(methodSubstitute, infCtx18);
                            }
                            else if (invocationSite instanceof ReferenceExpression) {
                                ((ReferenceExpression)invocationSite).registerInferenceContext(methodSubstitute, infCtx18);
                            }
                        }
                        if (invocationSite instanceof Invocation) {
                            ((Invocation)invocationSite).registerInferenceContext(methodSubstitute, infCtx18);
                        }
                        else if (invocationSite instanceof ReferenceExpression) {
                            ((ReferenceExpression)invocationSite).registerInferenceContext(methodSubstitute, infCtx18);
                        }
                        return methodSubstitute;
                    }
                }
                return null;
            }
            return null;
        }
        catch (final InferenceFailureException e) {
            scope.problemReporter().genericInferenceError(e.getMessage(), invocationSite);
            return null;
        }
        finally {
            environment.currentInferenceContext = previousContext;
        }
    }
    
    MethodBinding boundCheck18(final Scope scope, final TypeBinding[] arguments, final InvocationSite site) {
        final Substitution substitution = this;
        final ParameterizedGenericMethodBinding methodSubstitute = this;
        final TypeVariableBinding[] originalTypeVariables = this.originalMethod.typeVariables;
        for (int i = 0, length = originalTypeVariables.length; i < length; ++i) {
            final TypeVariableBinding typeVariable = originalTypeVariables[i];
            final TypeBinding substitute = methodSubstitute.typeArguments[i];
            final ASTNode location = (site instanceof ASTNode) ? site : null;
            switch (typeVariable.boundCheck(substitution, substitute, scope, location)) {
                case MISMATCH: {
                    final int argLength = arguments.length;
                    final TypeBinding[] augmentedArguments = new TypeBinding[argLength + 2];
                    System.arraycopy(arguments, 0, augmentedArguments, 0, argLength);
                    augmentedArguments[argLength] = substitute;
                    augmentedArguments[argLength + 1] = typeVariable;
                    return new ProblemMethodBinding(methodSubstitute, this.originalMethod.selector, augmentedArguments, 10);
                }
                case UNCHECKED: {
                    final ParameterizedGenericMethodBinding parameterizedGenericMethodBinding = methodSubstitute;
                    parameterizedGenericMethodBinding.tagBits |= 0x100L;
                    break;
                }
            }
        }
        return null;
    }
    
    private static ParameterizedGenericMethodBinding inferFromArgumentTypes(final Scope scope, final MethodBinding originalMethod, final TypeBinding[] arguments, final TypeBinding[] parameters, final InferenceContext inferenceContext) {
        if (originalMethod.isVarargs()) {
            final int paramLength = parameters.length;
            final int minArgLength = paramLength - 1;
            final int argLength = arguments.length;
            for (int i = 0; i < minArgLength; ++i) {
                parameters[i].collectSubstitutes(scope, arguments[i], inferenceContext, 1);
                if (inferenceContext.status == 1) {
                    return null;
                }
            }
            if (minArgLength < argLength) {
                TypeBinding varargType = parameters[minArgLength];
                final TypeBinding lastArgument = arguments[minArgLength];
                Label_0158: {
                    if (paramLength == argLength) {
                        if (lastArgument == TypeBinding.NULL) {
                            break Label_0158;
                        }
                        switch (lastArgument.dimensions()) {
                            case 0: {
                                break;
                            }
                            case 1: {
                                if (!lastArgument.leafComponentType().isBaseType()) {
                                    break Label_0158;
                                }
                                break;
                            }
                            default: {
                                break Label_0158;
                            }
                        }
                    }
                    varargType = ((ArrayBinding)varargType).elementsType();
                }
                for (int j = minArgLength; j < argLength; ++j) {
                    varargType.collectSubstitutes(scope, arguments[j], inferenceContext, 1);
                    if (inferenceContext.status == 1) {
                        return null;
                    }
                }
            }
        }
        else {
            for (int paramLength = parameters.length, k = 0; k < paramLength; ++k) {
                parameters[k].collectSubstitutes(scope, arguments[k], inferenceContext, 1);
                if (inferenceContext.status == 1) {
                    return null;
                }
            }
        }
        final TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
        if (!resolveSubstituteConstraints(scope, originalVariables, inferenceContext, false)) {
            return null;
        }
        TypeBinding[] actualSubstitutes;
        final TypeBinding[] inferredSustitutes = actualSubstitutes = inferenceContext.substitutes;
        for (int i = 0, varLength = originalVariables.length; i < varLength; ++i) {
            if (inferredSustitutes[i] == null) {
                if (actualSubstitutes == inferredSustitutes) {
                    System.arraycopy(inferredSustitutes, 0, actualSubstitutes = new TypeBinding[varLength], 0, i);
                }
                actualSubstitutes[i] = originalVariables[i];
            }
            else if (actualSubstitutes != inferredSustitutes) {
                actualSubstitutes[i] = inferredSustitutes[i];
            }
        }
        final ParameterizedGenericMethodBinding paramMethod = scope.environment().createParameterizedGenericMethod(originalMethod, actualSubstitutes);
        return paramMethod;
    }
    
    private static boolean resolveSubstituteConstraints(final Scope scope, final TypeVariableBinding[] typeVariables, final InferenceContext inferenceContext, final boolean considerEXTENDSConstraints) {
        final TypeBinding[] substitutes = inferenceContext.substitutes;
        final int varLength = typeVariables.length;
    Label_0168:
        for (int i = 0; i < varLength; ++i) {
            final TypeVariableBinding current = typeVariables[i];
            final TypeBinding substitute = substitutes[i];
            if (substitute == null) {
                final TypeBinding[] equalSubstitutes = inferenceContext.getSubstitutes(current, 0);
                if (equalSubstitutes != null) {
                    int j = 0;
                    final int equalLength = equalSubstitutes.length;
                    while (j < equalLength) {
                        TypeBinding equalSubstitute = equalSubstitutes[j];
                        if (equalSubstitute == null) {
                            ++j;
                        }
                        else {
                            if (TypeBinding.equalsEquals(equalSubstitute, current)) {
                                for (int k = j + 1; k < equalLength; ++k) {
                                    equalSubstitute = equalSubstitutes[k];
                                    if (TypeBinding.notEquals(equalSubstitute, current) && equalSubstitute != null) {
                                        substitutes[i] = equalSubstitute;
                                        continue Label_0168;
                                    }
                                }
                                substitutes[i] = current;
                                break;
                            }
                            substitutes[i] = equalSubstitute;
                            break;
                        }
                    }
                }
            }
        }
        if (inferenceContext.hasUnresolvedTypeArgument()) {
            for (int i = 0; i < varLength; ++i) {
                final TypeVariableBinding current = typeVariables[i];
                final TypeBinding substitute = substitutes[i];
                if (substitute == null) {
                    final TypeBinding[] bounds = inferenceContext.getSubstitutes(current, 2);
                    if (bounds != null) {
                        final TypeBinding mostSpecificSubstitute = scope.lowerUpperBound(bounds);
                        if (mostSpecificSubstitute == null) {
                            return false;
                        }
                        if (mostSpecificSubstitute != TypeBinding.VOID) {
                            substitutes[i] = mostSpecificSubstitute;
                        }
                    }
                }
            }
        }
        if (considerEXTENDSConstraints && inferenceContext.hasUnresolvedTypeArgument()) {
            for (int i = 0; i < varLength; ++i) {
                final TypeVariableBinding current = typeVariables[i];
                final TypeBinding substitute = substitutes[i];
                if (substitute == null) {
                    final TypeBinding[] bounds = inferenceContext.getSubstitutes(current, 1);
                    if (bounds != null) {
                        final TypeBinding[] glb = Scope.greaterLowerBound(bounds, scope, scope.environment());
                        TypeBinding mostSpecificSubstitute2 = null;
                        if (glb != null) {
                            if (glb.length == 1) {
                                mostSpecificSubstitute2 = glb[0];
                            }
                            else {
                                final TypeBinding[] otherBounds = new TypeBinding[glb.length - 1];
                                System.arraycopy(glb, 1, otherBounds, 0, glb.length - 1);
                                mostSpecificSubstitute2 = scope.environment().createWildcard(null, 0, glb[0], otherBounds, 1);
                            }
                        }
                        if (mostSpecificSubstitute2 != null) {
                            substitutes[i] = mostSpecificSubstitute2;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public ParameterizedGenericMethodBinding(final MethodBinding originalMethod, final RawTypeBinding rawType, final LookupEnvironment environment) {
        final TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
        final int length = originalVariables.length;
        final TypeBinding[] rawArguments = new TypeBinding[length];
        for (int i = 0; i < length; ++i) {
            rawArguments[i] = environment.convertToRawType(originalVariables[i].erasure(), false);
        }
        this.isRaw = true;
        this.tagBits = originalMethod.tagBits;
        this.environment = environment;
        this.modifiers = originalMethod.modifiers;
        this.selector = originalMethod.selector;
        this.declaringClass = ((rawType == null) ? originalMethod.declaringClass : rawType);
        this.typeVariables = Binding.NO_TYPE_VARIABLES;
        this.typeArguments = rawArguments;
        this.originalMethod = originalMethod;
        final boolean ignoreRawTypeSubstitution = rawType == null || originalMethod.isStatic();
        this.parameters = Scope.substitute(this, ignoreRawTypeSubstitution ? originalMethod.parameters : Scope.substitute(rawType, originalMethod.parameters));
        this.thrownExceptions = Scope.substitute(this, ignoreRawTypeSubstitution ? originalMethod.thrownExceptions : Scope.substitute(rawType, originalMethod.thrownExceptions));
        if (this.thrownExceptions == null) {
            this.thrownExceptions = Binding.NO_EXCEPTIONS;
        }
        this.returnType = Scope.substitute(this, ignoreRawTypeSubstitution ? originalMethod.returnType : Scope.substitute(rawType, originalMethod.returnType));
        this.wasInferred = false;
        this.parameterNonNullness = originalMethod.parameterNonNullness;
        this.defaultNullness = originalMethod.defaultNullness;
    }
    
    public ParameterizedGenericMethodBinding(final MethodBinding originalMethod, final TypeBinding[] typeArguments, final LookupEnvironment environment, final boolean inferredWithUncheckConversion, final boolean hasReturnProblem) {
        this.environment = environment;
        this.inferredWithUncheckedConversion = inferredWithUncheckConversion;
        this.modifiers = originalMethod.modifiers;
        this.selector = originalMethod.selector;
        this.declaringClass = originalMethod.declaringClass;
        if (inferredWithUncheckConversion && originalMethod.isConstructor() && this.declaringClass.isParameterizedType()) {
            this.declaringClass = (ReferenceBinding)environment.convertToRawType(this.declaringClass.erasure(), false);
        }
        this.typeVariables = Binding.NO_TYPE_VARIABLES;
        this.typeArguments = typeArguments;
        this.isRaw = false;
        this.tagBits = originalMethod.tagBits;
        this.originalMethod = originalMethod;
        this.parameters = Scope.substitute(this, originalMethod.parameters);
        if (inferredWithUncheckConversion) {
            this.returnType = this.getErasure18_5_2(originalMethod.returnType, environment, hasReturnProblem);
            this.thrownExceptions = new ReferenceBinding[originalMethod.thrownExceptions.length];
            for (int i = 0; i < originalMethod.thrownExceptions.length; ++i) {
                this.thrownExceptions[i] = (ReferenceBinding)this.getErasure18_5_2(originalMethod.thrownExceptions[i], environment, false);
            }
        }
        else {
            this.returnType = Scope.substitute(this, originalMethod.returnType);
            this.thrownExceptions = Scope.substitute(this, originalMethod.thrownExceptions);
        }
        if (this.thrownExceptions == null) {
            this.thrownExceptions = Binding.NO_EXCEPTIONS;
        }
        Label_0398: {
            if ((this.tagBits & 0x80L) == 0x0L) {
                if ((this.returnType.tagBits & 0x80L) != 0x0L) {
                    this.tagBits |= 0x80L;
                }
                else {
                    for (int i = 0, max = this.parameters.length; i < max; ++i) {
                        if ((this.parameters[i].tagBits & 0x80L) != 0x0L) {
                            this.tagBits |= 0x80L;
                            break Label_0398;
                        }
                    }
                    for (int i = 0, max = this.thrownExceptions.length; i < max; ++i) {
                        if ((this.thrownExceptions[i].tagBits & 0x80L) != 0x0L) {
                            this.tagBits |= 0x80L;
                            break;
                        }
                    }
                }
            }
        }
        this.wasInferred = true;
        this.parameterNonNullness = originalMethod.parameterNonNullness;
        this.defaultNullness = originalMethod.defaultNullness;
        for (int len = this.parameters.length, j = 0; j < len; ++j) {
            if (this.parameters[j] == TypeBinding.NULL) {
                final long nullBits = originalMethod.parameters[j].tagBits & 0x180000000000000L;
                if (nullBits == 72057594037927936L) {
                    if (this.parameterNonNullness == null) {
                        this.parameterNonNullness = new Boolean[len];
                    }
                    this.parameterNonNullness[j] = Boolean.TRUE;
                }
            }
        }
    }
    
    TypeBinding getErasure18_5_2(TypeBinding type, final LookupEnvironment env, final boolean substitute) {
        if (substitute) {
            type = Scope.substitute(this, type);
        }
        return env.convertToRawType(type.erasure(), true);
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.originalMethod.computeUniqueKey(false));
        buffer.append('%');
        buffer.append('<');
        if (!this.isRaw) {
            for (int length = this.typeArguments.length, i = 0; i < length; ++i) {
                final TypeBinding typeArgument = this.typeArguments[i];
                buffer.append(typeArgument.computeUniqueKey(false));
            }
        }
        buffer.append('>');
        final int resultLength = buffer.length();
        final char[] result = new char[resultLength];
        buffer.getChars(0, resultLength, result, 0);
        return result;
    }
    
    @Override
    public LookupEnvironment environment() {
        return this.environment;
    }
    
    @Override
    public boolean hasSubstitutedParameters() {
        if (this.wasInferred) {
            return this.originalMethod.hasSubstitutedParameters();
        }
        return super.hasSubstitutedParameters();
    }
    
    @Override
    public boolean hasSubstitutedReturnType() {
        if (this.inferredReturnType) {
            return this.originalMethod.hasSubstitutedReturnType();
        }
        return super.hasSubstitutedReturnType();
    }
    
    private ParameterizedGenericMethodBinding inferFromExpectedType(final Scope scope, final InferenceContext inferenceContext) {
        final TypeVariableBinding[] originalVariables = this.originalMethod.typeVariables;
        final int varLength = originalVariables.length;
        if (inferenceContext.expectedType != null) {
            this.returnType.collectSubstitutes(scope, inferenceContext.expectedType, inferenceContext, 2);
            if (inferenceContext.status == 1) {
                return null;
            }
        }
        for (int i = 0; i < varLength; ++i) {
            final TypeVariableBinding originalVariable = originalVariables[i];
            final TypeBinding argument = this.typeArguments[i];
            final boolean argAlreadyInferred = TypeBinding.notEquals(argument, originalVariable);
            if (TypeBinding.equalsEquals(originalVariable.firstBound, originalVariable.superclass)) {
                final TypeBinding substitutedBound = Scope.substitute(this, originalVariable.superclass);
                argument.collectSubstitutes(scope, substitutedBound, inferenceContext, 2);
                if (inferenceContext.status == 1) {
                    return null;
                }
                if (argAlreadyInferred) {
                    substitutedBound.collectSubstitutes(scope, argument, inferenceContext, 1);
                    if (inferenceContext.status == 1) {
                        return null;
                    }
                }
            }
            for (int j = 0, max = originalVariable.superInterfaces.length; j < max; ++j) {
                final TypeBinding substitutedBound2 = Scope.substitute(this, originalVariable.superInterfaces[j]);
                argument.collectSubstitutes(scope, substitutedBound2, inferenceContext, 2);
                if (inferenceContext.status == 1) {
                    return null;
                }
                if (argAlreadyInferred) {
                    substitutedBound2.collectSubstitutes(scope, argument, inferenceContext, 1);
                    if (inferenceContext.status == 1) {
                        return null;
                    }
                }
            }
        }
        if (!resolveSubstituteConstraints(scope, originalVariables, inferenceContext, true)) {
            return null;
        }
        for (int i = 0; i < varLength; ++i) {
            final TypeBinding substitute = inferenceContext.substitutes[i];
            if (substitute != null) {
                this.typeArguments[i] = substitute;
            }
            else {
                this.typeArguments[i] = (inferenceContext.substitutes[i] = originalVariables[i].upperBound());
            }
        }
        this.typeArguments = Scope.substitute(this, this.typeArguments);
        final TypeBinding oldReturnType = this.returnType;
        this.returnType = Scope.substitute(this, this.returnType);
        this.inferredReturnType = (inferenceContext.hasExplicitExpectedType && TypeBinding.notEquals(this.returnType, oldReturnType));
        this.parameters = Scope.substitute(this, this.parameters);
        this.thrownExceptions = Scope.substitute(this, this.thrownExceptions);
        if (this.thrownExceptions == null) {
            this.thrownExceptions = Binding.NO_EXCEPTIONS;
        }
        if ((this.tagBits & 0x80L) == 0x0L) {
            if ((this.returnType.tagBits & 0x80L) != 0x0L) {
                this.tagBits |= 0x80L;
            }
            else {
                for (int k = 0, max2 = this.parameters.length; k < max2; ++k) {
                    if ((this.parameters[k].tagBits & 0x80L) != 0x0L) {
                        this.tagBits |= 0x80L;
                        return this;
                    }
                }
                for (int k = 0, max2 = this.thrownExceptions.length; k < max2; ++k) {
                    if ((this.thrownExceptions[k].tagBits & 0x80L) != 0x0L) {
                        this.tagBits |= 0x80L;
                        break;
                    }
                }
            }
        }
        return this;
    }
    
    @Override
    public boolean isParameterizedGeneric() {
        return true;
    }
    
    @Override
    public boolean isRawSubstitution() {
        return this.isRaw;
    }
    
    @Override
    public TypeBinding substitute(final TypeVariableBinding originalVariable) {
        final TypeVariableBinding[] variables = this.originalMethod.typeVariables;
        final int length = variables.length;
        if (originalVariable.rank < length && TypeBinding.equalsEquals(variables[originalVariable.rank], originalVariable)) {
            final TypeBinding substitute = this.typeArguments[originalVariable.rank];
            return originalVariable.combineTypeAnnotations(substitute);
        }
        return originalVariable;
    }
    
    @Override
    public MethodBinding tiebreakMethod() {
        if (this.tiebreakMethod == null) {
            this.tiebreakMethod = this.originalMethod.asRawMethod(this.environment);
        }
        return this.tiebreakMethod;
    }
    
    @Override
    public MethodBinding genericMethod() {
        if (this.isRaw) {
            return this;
        }
        return this.originalMethod;
    }
    
    private static class LingeringTypeVariableEliminator implements Substitution
    {
        private final TypeVariableBinding[] variables;
        private final TypeBinding[] substitutes;
        private final Scope scope;
        
        public LingeringTypeVariableEliminator(final TypeVariableBinding[] variables, final TypeBinding[] substitutes, final Scope scope) {
            this.variables = variables;
            this.substitutes = substitutes;
            this.scope = scope;
        }
        
        @Override
        public TypeBinding substitute(final TypeVariableBinding typeVariable) {
            if (typeVariable.rank >= this.variables.length || TypeBinding.notEquals(this.variables[typeVariable.rank], typeVariable)) {
                return typeVariable;
            }
            if (this.substitutes != null) {
                return Scope.substitute(new LingeringTypeVariableEliminator(this.variables, null, this.scope), this.substitutes[typeVariable.rank]);
            }
            final ReferenceBinding genericType = (ReferenceBinding)((typeVariable.declaringElement instanceof ReferenceBinding) ? typeVariable.declaringElement : null);
            return this.scope.environment().createWildcard(genericType, typeVariable.rank, null, null, 0, typeVariable.getTypeAnnotations());
        }
        
        @Override
        public LookupEnvironment environment() {
            return this.scope.environment();
        }
        
        @Override
        public boolean isRawSubstitution() {
            return false;
        }
    }
}
