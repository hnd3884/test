package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

public class ImplicitNullAnnotationVerifier
{
    ImplicitNullAnnotationVerifier buddyImplicitNullAnnotationsVerifier;
    private boolean inheritNullAnnotations;
    protected LookupEnvironment environment;
    
    public ImplicitNullAnnotationVerifier(final LookupEnvironment environment, final boolean inheritNullAnnotations) {
        this.buddyImplicitNullAnnotationsVerifier = this;
        this.inheritNullAnnotations = inheritNullAnnotations;
        this.environment = environment;
    }
    
    ImplicitNullAnnotationVerifier(final LookupEnvironment environment) {
        final CompilerOptions options = environment.globalOptions;
        this.buddyImplicitNullAnnotationsVerifier = new ImplicitNullAnnotationVerifier(environment, options.inheritNullAnnotations);
        this.inheritNullAnnotations = options.inheritNullAnnotations;
        this.environment = environment;
    }
    
    public void checkImplicitNullAnnotations(final MethodBinding currentMethod, final AbstractMethodDeclaration srcMethod, boolean complain, final Scope scope) {
        try {
            final ReferenceBinding currentType = currentMethod.declaringClass;
            if (currentType.id == 1) {
                return;
            }
            final boolean usesTypeAnnotations = scope.environment().usesNullTypeAnnotations();
            final boolean needToApplyReturnNonNullDefault = currentMethod.hasNonNullDefaultFor(16, usesTypeAnnotations);
            final boolean needToApplyParameterNonNullDefault = currentMethod.hasNonNullDefaultFor(8, usesTypeAnnotations);
            boolean needToApplyNonNullDefault = needToApplyReturnNonNullDefault | needToApplyParameterNonNullDefault;
            final boolean isInstanceMethod = !currentMethod.isConstructor() && !currentMethod.isStatic();
            complain &= isInstanceMethod;
            if (!needToApplyNonNullDefault && !complain && (!this.inheritNullAnnotations || !isInstanceMethod)) {
                return;
            }
            if (isInstanceMethod) {
                final List superMethodList = new ArrayList();
                if (currentType instanceof SourceTypeBinding && !currentType.isHierarchyConnected() && !currentType.isAnonymousType()) {
                    ((SourceTypeBinding)currentType).scope.connectTypeHierarchy();
                }
                final int paramLen = currentMethod.parameters.length;
                this.findAllOverriddenMethods(currentMethod.original(), currentMethod.selector, paramLen, currentType, new HashSet(), superMethodList);
                final InheritedNonNullnessInfo[] inheritedNonNullnessInfos = new InheritedNonNullnessInfo[paramLen + 1];
                for (int i = 0; i < paramLen + 1; ++i) {
                    inheritedNonNullnessInfos[i] = new InheritedNonNullnessInfo();
                }
                int j;
                final int length = j = superMethodList.size();
                while (--j >= 0) {
                    final MethodBinding currentSuper = superMethodList.get(j);
                    if ((currentSuper.tagBits & 0x1000L) == 0x0L) {
                        this.checkImplicitNullAnnotations(currentSuper, null, false, scope);
                    }
                    this.checkNullSpecInheritance(currentMethod, srcMethod, needToApplyReturnNonNullDefault, needToApplyParameterNonNullDefault, complain, currentSuper, null, scope, inheritedNonNullnessInfos);
                    needToApplyNonNullDefault = false;
                }
                InheritedNonNullnessInfo info = inheritedNonNullnessInfos[0];
                if (!info.complained) {
                    long tagBits = 0L;
                    if (info.inheritedNonNullness == Boolean.TRUE) {
                        tagBits = 72057594037927936L;
                    }
                    else if (info.inheritedNonNullness == Boolean.FALSE) {
                        tagBits = 36028797018963968L;
                    }
                    if (tagBits != 0L) {
                        if (!usesTypeAnnotations) {
                            currentMethod.tagBits |= tagBits;
                        }
                        else if (!currentMethod.returnType.isBaseType()) {
                            final LookupEnvironment env = scope.environment();
                            currentMethod.returnType = env.createAnnotatedType(currentMethod.returnType, env.nullAnnotationsFromTagBits(tagBits));
                        }
                    }
                }
                for (int k = 0; k < paramLen; ++k) {
                    info = inheritedNonNullnessInfos[k + 1];
                    if (!info.complained && info.inheritedNonNullness != null) {
                        final Argument currentArg = (srcMethod == null) ? null : srcMethod.arguments[k];
                        if (!usesTypeAnnotations) {
                            this.recordArgNonNullness(currentMethod, paramLen, k, currentArg, info.inheritedNonNullness);
                        }
                        else {
                            this.recordArgNonNullness18(currentMethod, k, currentArg, info.inheritedNonNullness, scope.environment());
                        }
                    }
                }
            }
            if (needToApplyNonNullDefault) {
                if (!usesTypeAnnotations) {
                    currentMethod.fillInDefaultNonNullness(srcMethod);
                }
                else {
                    currentMethod.fillInDefaultNonNullness18(srcMethod, scope.environment());
                }
            }
        }
        finally {
            currentMethod.tagBits |= 0x1000L;
        }
        currentMethod.tagBits |= 0x1000L;
    }
    
    private void findAllOverriddenMethods(final MethodBinding original, final char[] selector, final int suggestedParameterLength, final ReferenceBinding currentType, final Set ifcsSeen, final List result) {
        if (currentType.id == 1) {
            return;
        }
        final ReferenceBinding superclass = currentType.superclass();
        if (superclass == null) {
            return;
        }
        this.collectOverriddenMethods(original, selector, suggestedParameterLength, superclass, ifcsSeen, result);
        for (final ReferenceBinding currentIfc : currentType.superInterfaces()) {
            if (ifcsSeen.add(currentIfc.original())) {
                this.collectOverriddenMethods(original, selector, suggestedParameterLength, currentIfc, ifcsSeen, result);
            }
        }
    }
    
    private void collectOverriddenMethods(final MethodBinding original, final char[] selector, final int suggestedParameterLength, final ReferenceBinding superType, final Set ifcsSeen, final List result) {
        final MethodBinding[] ifcMethods = superType.getMethods(selector, suggestedParameterLength);
        final int length = ifcMethods.length;
        boolean added = false;
        for (final MethodBinding currentMethod : ifcMethods) {
            if (!currentMethod.isStatic()) {
                if (MethodVerifier.doesMethodOverride(original, currentMethod, this.environment)) {
                    result.add(currentMethod);
                    added = true;
                }
            }
        }
        if (!added) {
            this.findAllOverriddenMethods(original, selector, suggestedParameterLength, superType, ifcsSeen, result);
        }
    }
    
    void checkNullSpecInheritance(final MethodBinding currentMethod, final AbstractMethodDeclaration srcMethod, final boolean hasReturnNonNullDefault, final boolean hasParameterNonNullDefault, final boolean shouldComplain, final MethodBinding inheritedMethod, final MethodBinding[] allInheritedMethods, final Scope scope, final InheritedNonNullnessInfo[] inheritedNonNullnessInfos) {
        if ((inheritedMethod.tagBits & 0x1000L) == 0x0L) {
            this.buddyImplicitNullAnnotationsVerifier.checkImplicitNullAnnotations(inheritedMethod, null, false, scope);
        }
        final boolean useTypeAnnotations = this.environment.usesNullTypeAnnotations();
        final long inheritedNullnessBits = this.getReturnTypeNullnessTagBits(inheritedMethod, useTypeAnnotations);
        long currentNullnessBits = this.getReturnTypeNullnessTagBits(currentMethod, useTypeAnnotations);
        final boolean shouldInherit = this.inheritNullAnnotations;
        Label_0408: {
            if (currentMethod.returnType != null) {
                if (!currentMethod.returnType.isBaseType()) {
                    if (currentNullnessBits == 0L) {
                        if (shouldInherit && inheritedNullnessBits != 0L) {
                            if (hasReturnNonNullDefault && shouldComplain && inheritedNullnessBits == 36028797018963968L) {
                                scope.problemReporter().conflictingNullAnnotations(currentMethod, ((MethodDeclaration)srcMethod).returnType, inheritedMethod);
                            }
                            if (inheritedNonNullnessInfos != null && srcMethod != null) {
                                this.recordDeferredInheritedNullness(scope, ((MethodDeclaration)srcMethod).returnType, inheritedMethod, inheritedNullnessBits == 72057594037927936L, inheritedNonNullnessInfos[0]);
                                break Label_0408;
                            }
                            this.applyReturnNullBits(currentMethod, inheritedNullnessBits);
                            break Label_0408;
                        }
                        else if (hasReturnNonNullDefault && (!useTypeAnnotations || currentMethod.returnType.acceptsNonNullDefault())) {
                            currentNullnessBits = 72057594037927936L;
                            this.applyReturnNullBits(currentMethod, currentNullnessBits);
                        }
                    }
                    if (shouldComplain) {
                        if ((inheritedNullnessBits & 0x100000000000000L) != 0x0L && currentNullnessBits != 72057594037927936L) {
                            if (srcMethod == null) {
                                scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod, useTypeAnnotations);
                                return;
                            }
                            scope.problemReporter().illegalReturnRedefinition(srcMethod, inheritedMethod, this.environment.getNonNullAnnotationName());
                        }
                        else if (useTypeAnnotations) {
                            TypeBinding substituteReturnType = null;
                            final TypeVariableBinding[] typeVariables = inheritedMethod.original().typeVariables;
                            if (typeVariables != null && currentMethod.returnType.id != 6) {
                                final ParameterizedGenericMethodBinding substitute = this.environment.createParameterizedGenericMethod(currentMethod, typeVariables);
                                substituteReturnType = substitute.returnType;
                            }
                            if (NullAnnotationMatching.analyse(inheritedMethod.returnType, currentMethod.returnType, substituteReturnType, null, 0, null, NullAnnotationMatching.CheckMode.OVERRIDE_RETURN).isAnyMismatch()) {
                                if (srcMethod != null) {
                                    scope.problemReporter().illegalReturnRedefinition(srcMethod, inheritedMethod, this.environment.getNonNullAnnotationName());
                                }
                                else {
                                    scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod, useTypeAnnotations);
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
        TypeBinding[] substituteParameters = null;
        if (shouldComplain) {
            final TypeVariableBinding[] typeVariables = currentMethod.original().typeVariables;
            if (typeVariables != Binding.NO_TYPE_VARIABLES) {
                final ParameterizedGenericMethodBinding substitute = this.environment.createParameterizedGenericMethod(inheritedMethod, typeVariables);
                substituteParameters = substitute.parameters;
            }
        }
        final Argument[] currentArguments = (Argument[])((srcMethod == null) ? null : srcMethod.arguments);
        int length = 0;
        if (currentArguments != null) {
            length = currentArguments.length;
        }
        if (useTypeAnnotations) {
            length = currentMethod.parameters.length;
        }
        else if (inheritedMethod.parameterNonNullness != null) {
            length = inheritedMethod.parameterNonNullness.length;
        }
        else if (currentMethod.parameterNonNullness != null) {
            length = currentMethod.parameterNonNullness.length;
        }
    Label_1135:
        for (int i = 0; i < length; ++i) {
            if (!currentMethod.parameters[i].isBaseType()) {
                final Argument currentArgument = (currentArguments == null) ? null : currentArguments[i];
                final Boolean inheritedNonNullNess = this.getParameterNonNullness(inheritedMethod, i, useTypeAnnotations);
                Boolean currentNonNullNess = this.getParameterNonNullness(currentMethod, i, useTypeAnnotations);
                if (currentNonNullNess == null) {
                    if (inheritedNonNullNess != null && shouldInherit) {
                        if (hasParameterNonNullDefault && shouldComplain && inheritedNonNullNess == Boolean.FALSE && currentArgument != null) {
                            scope.problemReporter().conflictingNullAnnotations(currentMethod, currentArgument, inheritedMethod);
                        }
                        if (inheritedNonNullnessInfos != null && srcMethod != null) {
                            this.recordDeferredInheritedNullness(scope, srcMethod.arguments[i].type, inheritedMethod, inheritedNonNullNess, inheritedNonNullnessInfos[i + 1]);
                            continue;
                        }
                        if (!useTypeAnnotations) {
                            this.recordArgNonNullness(currentMethod, length, i, currentArgument, inheritedNonNullNess);
                            continue;
                        }
                        this.recordArgNonNullness18(currentMethod, i, currentArgument, inheritedNonNullNess, this.environment);
                        continue;
                    }
                    else if (hasParameterNonNullDefault) {
                        currentNonNullNess = Boolean.TRUE;
                        if (!useTypeAnnotations) {
                            this.recordArgNonNullness(currentMethod, length, i, currentArgument, Boolean.TRUE);
                        }
                        else if (currentMethod.parameters[i].acceptsNonNullDefault()) {
                            this.recordArgNonNullness18(currentMethod, i, currentArgument, Boolean.TRUE, this.environment);
                        }
                        else {
                            currentNonNullNess = null;
                        }
                    }
                }
                if (shouldComplain) {
                    char[][] annotationName;
                    if (inheritedNonNullNess == Boolean.TRUE) {
                        annotationName = this.environment.getNonNullAnnotationName();
                    }
                    else {
                        annotationName = this.environment.getNullableAnnotationName();
                    }
                    if (inheritedNonNullNess != Boolean.TRUE && currentNonNullNess == Boolean.TRUE) {
                        if (currentArgument != null) {
                            scope.problemReporter().illegalRedefinitionToNonNullParameter(currentArgument, inheritedMethod.declaringClass, (char[][])((inheritedNonNullNess == null) ? null : this.environment.getNullableAnnotationName()));
                        }
                        else {
                            scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod, false);
                        }
                    }
                    else {
                        if (currentNonNullNess == null) {
                            if (inheritedNonNullNess == Boolean.FALSE) {
                                if (currentArgument != null) {
                                    scope.problemReporter().parameterLackingNullableAnnotation(currentArgument, inheritedMethod.declaringClass, annotationName);
                                    continue;
                                }
                                scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod, false);
                                continue;
                            }
                            else if (inheritedNonNullNess == Boolean.TRUE) {
                                if (allInheritedMethods != null) {
                                    for (final MethodBinding one : allInheritedMethods) {
                                        if (TypeBinding.equalsEquals(inheritedMethod.declaringClass, one.declaringClass) && this.getParameterNonNullness(one, i, useTypeAnnotations) != Boolean.TRUE) {
                                            continue Label_1135;
                                        }
                                    }
                                }
                                scope.problemReporter().parameterLackingNonnullAnnotation(currentArgument, inheritedMethod.declaringClass, annotationName);
                                continue;
                            }
                        }
                        if (useTypeAnnotations) {
                            final TypeBinding inheritedParameter = inheritedMethod.parameters[i];
                            final TypeBinding substituteParameter = (substituteParameters != null) ? substituteParameters[i] : null;
                            if (NullAnnotationMatching.analyse(currentMethod.parameters[i], inheritedParameter, substituteParameter, null, 0, null, NullAnnotationMatching.CheckMode.OVERRIDE).isAnyMismatch()) {
                                if (currentArgument != null) {
                                    scope.problemReporter().illegalParameterRedefinition(currentArgument, inheritedMethod.declaringClass, inheritedParameter);
                                }
                                else {
                                    scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod, false);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (shouldComplain && useTypeAnnotations && srcMethod != null) {
            final TypeVariableBinding[] currentTypeVariables = currentMethod.typeVariables();
            final TypeVariableBinding[] inheritedTypeVariables = inheritedMethod.typeVariables();
            if (currentTypeVariables != Binding.NO_TYPE_VARIABLES && currentTypeVariables.length == inheritedTypeVariables.length) {
                for (int j = 0; j < currentTypeVariables.length; ++j) {
                    final TypeVariableBinding inheritedVariable = inheritedTypeVariables[j];
                    if (NullAnnotationMatching.analyse(inheritedVariable, currentTypeVariables[j], null, null, -1, null, NullAnnotationMatching.CheckMode.BOUND_CHECK).isAnyMismatch()) {
                        scope.problemReporter().cannotRedefineTypeArgumentNullity(inheritedVariable, inheritedMethod, srcMethod.typeParameters()[j]);
                    }
                }
            }
        }
    }
    
    void applyReturnNullBits(final MethodBinding method, final long nullnessBits) {
        if (this.environment.usesNullTypeAnnotations()) {
            if (!method.returnType.isBaseType()) {
                method.returnType = this.environment.createAnnotatedType(method.returnType, this.environment.nullAnnotationsFromTagBits(nullnessBits));
            }
        }
        else {
            method.tagBits |= nullnessBits;
        }
    }
    
    private Boolean getParameterNonNullness(final MethodBinding method, final int i, final boolean useTypeAnnotations) {
        if (useTypeAnnotations) {
            final TypeBinding parameter = method.parameters[i];
            if (parameter != null) {
                final long nullBits = NullAnnotationMatching.validNullTagBits(parameter.tagBits);
                if (nullBits != 0L) {
                    return nullBits == 72057594037927936L;
                }
            }
            return null;
        }
        return (method.parameterNonNullness == null) ? null : method.parameterNonNullness[i];
    }
    
    private long getReturnTypeNullnessTagBits(final MethodBinding method, final boolean useTypeAnnotations) {
        if (!useTypeAnnotations) {
            return method.tagBits & 0x180000000000000L;
        }
        if (method.returnType == null) {
            return 0L;
        }
        return NullAnnotationMatching.validNullTagBits(method.returnType.tagBits);
    }
    
    protected void recordDeferredInheritedNullness(final Scope scope, final ASTNode location, final MethodBinding inheritedMethod, final Boolean inheritedNonNullness, final InheritedNonNullnessInfo nullnessInfo) {
        if (nullnessInfo.inheritedNonNullness != null && nullnessInfo.inheritedNonNullness != inheritedNonNullness) {
            scope.problemReporter().conflictingInheritedNullAnnotations(location, nullnessInfo.inheritedNonNullness, nullnessInfo.annotationOrigin, inheritedNonNullness, inheritedMethod);
            nullnessInfo.complained = true;
        }
        else {
            nullnessInfo.inheritedNonNullness = inheritedNonNullness;
            nullnessInfo.annotationOrigin = inheritedMethod;
        }
    }
    
    void recordArgNonNullness(final MethodBinding method, final int paramCount, final int paramIdx, final Argument currentArgument, final Boolean nonNullNess) {
        if (method.parameterNonNullness == null) {
            method.parameterNonNullness = new Boolean[paramCount];
        }
        method.parameterNonNullness[paramIdx] = nonNullNess;
        if (currentArgument != null) {
            final LocalVariableBinding binding = currentArgument.binding;
            binding.tagBits |= (nonNullNess ? 72057594037927936L : 36028797018963968L);
        }
    }
    
    void recordArgNonNullness18(final MethodBinding method, final int paramIdx, final Argument currentArgument, final Boolean nonNullNess, final LookupEnvironment env) {
        final AnnotationBinding annotationBinding = nonNullNess ? env.getNonNullAnnotation() : env.getNullableAnnotation();
        method.parameters[paramIdx] = env.createAnnotatedType(method.parameters[paramIdx], new AnnotationBinding[] { annotationBinding });
        if (currentArgument != null) {
            currentArgument.binding.type = method.parameters[paramIdx];
        }
    }
    
    static boolean areParametersEqual(final MethodBinding one, final MethodBinding two) {
        final TypeBinding[] oneArgs = one.parameters;
        final TypeBinding[] twoArgs = two.parameters;
        if (oneArgs == twoArgs) {
            return true;
        }
        final int length = oneArgs.length;
        if (length != twoArgs.length) {
            return false;
        }
        int i = 0;
        while (i < length) {
            if (!areTypesEqual(oneArgs[i], twoArgs[i])) {
                if (!oneArgs[i].leafComponentType().isRawType() || oneArgs[i].dimensions() != twoArgs[i].dimensions() || !oneArgs[i].leafComponentType().isEquivalentTo(twoArgs[i].leafComponentType())) {
                    return false;
                }
                if (one.typeVariables != Binding.NO_TYPE_VARIABLES) {
                    return false;
                }
                for (int j = 0; j < i; ++j) {
                    if (oneArgs[j].leafComponentType().isParameterizedTypeWithActualArguments()) {
                        return false;
                    }
                }
                break;
            }
            else {
                ++i;
            }
        }
        ++i;
        while (i < length) {
            if (!areTypesEqual(oneArgs[i], twoArgs[i])) {
                if (!oneArgs[i].leafComponentType().isRawType() || oneArgs[i].dimensions() != twoArgs[i].dimensions() || !oneArgs[i].leafComponentType().isEquivalentTo(twoArgs[i].leafComponentType())) {
                    return false;
                }
            }
            else if (oneArgs[i].leafComponentType().isParameterizedTypeWithActualArguments()) {
                return false;
            }
            ++i;
        }
        return true;
    }
    
    static boolean areTypesEqual(final TypeBinding one, final TypeBinding two) {
        if (TypeBinding.equalsEquals(one, two)) {
            return true;
        }
        Label_0133: {
            switch (one.kind()) {
                case 4: {
                    switch (two.kind()) {
                        case 260:
                        case 1028: {
                            if (TypeBinding.equalsEquals(one, two.erasure())) {
                                return true;
                            }
                            break Label_0133;
                        }
                        default: {
                            break Label_0133;
                        }
                    }
                    break;
                }
                case 260:
                case 1028: {
                    switch (two.kind()) {
                        case 4: {
                            if (TypeBinding.equalsEquals(one.erasure(), two)) {
                                return true;
                            }
                            break Label_0133;
                        }
                    }
                    break;
                }
            }
        }
        return one.isParameterizedType() && two.isParameterizedType() && (one.isEquivalentTo(two) && two.isEquivalentTo(one));
    }
    
    static class InheritedNonNullnessInfo
    {
        Boolean inheritedNonNullness;
        MethodBinding annotationOrigin;
        boolean complained;
    }
}
