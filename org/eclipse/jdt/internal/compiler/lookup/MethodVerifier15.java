package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.util.Sorting;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;

class MethodVerifier15 extends MethodVerifier
{
    MethodVerifier15(final LookupEnvironment environment) {
        super(environment);
    }
    
    @Override
    protected boolean canOverridingMethodDifferInErasure(final MethodBinding overridingMethod, final MethodBinding inheritedMethod) {
        return !overridingMethod.areParameterErasuresEqual(inheritedMethod) && !overridingMethod.declaringClass.isRawType();
    }
    
    @Override
    boolean canSkipInheritedMethods() {
        return (this.type.superclass() == null || (!this.type.superclass().isAbstract() && !this.type.superclass().isParameterizedType())) && this.type.superInterfaces() == Binding.NO_SUPERINTERFACES;
    }
    
    @Override
    boolean canSkipInheritedMethods(final MethodBinding one, final MethodBinding two) {
        return two == null || (TypeBinding.equalsEquals(one.declaringClass, two.declaringClass) && !one.declaringClass.isParameterizedType());
    }
    
    @Override
    void checkConcreteInheritedMethod(final MethodBinding concreteMethod, final MethodBinding[] abstractMethods) {
        super.checkConcreteInheritedMethod(concreteMethod, abstractMethods);
        final boolean analyseNullAnnotations = this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
        AbstractMethodDeclaration srcMethod = null;
        if (analyseNullAnnotations && this.type.equals(concreteMethod.declaringClass)) {
            srcMethod = concreteMethod.sourceMethod();
        }
        final boolean useTypeAnnotations = this.environment.usesNullTypeAnnotations();
        final boolean hasReturnNonNullDefault = analyseNullAnnotations && concreteMethod.hasNonNullDefaultFor(16, useTypeAnnotations);
        final boolean hasParameterNonNullDefault = analyseNullAnnotations && concreteMethod.hasNonNullDefaultFor(8, useTypeAnnotations);
        for (int i = 0, l = abstractMethods.length; i < l; ++i) {
            final MethodBinding abstractMethod = abstractMethods[i];
            if (concreteMethod.isVarargs() != abstractMethod.isVarargs()) {
                this.problemReporter().varargsConflict(concreteMethod, abstractMethod, this.type);
            }
            final MethodBinding originalInherited = abstractMethod.original();
            if (TypeBinding.notEquals(originalInherited.returnType, concreteMethod.returnType) && !this.isAcceptableReturnTypeOverride(concreteMethod, abstractMethod)) {
                this.problemReporter().unsafeReturnTypeOverride(concreteMethod, originalInherited, this.type);
            }
            if (originalInherited.declaringClass.isInterface() && ((TypeBinding.equalsEquals(concreteMethod.declaringClass, this.type.superclass) && this.type.superclass.isParameterizedType() && !this.areMethodsCompatible(concreteMethod, originalInherited)) || this.type.superclass.erasure().findSuperTypeOriginatingFrom(originalInherited.declaringClass) == null)) {
                this.type.addSyntheticBridgeMethod(originalInherited, concreteMethod.original());
            }
            if (analyseNullAnnotations && !concreteMethod.isStatic() && !abstractMethod.isStatic()) {
                this.checkNullSpecInheritance(concreteMethod, srcMethod, hasReturnNonNullDefault, hasParameterNonNullDefault, true, abstractMethod, abstractMethods, this.type.scope, null);
            }
        }
    }
    
    @Override
    void checkForBridgeMethod(final MethodBinding currentMethod, final MethodBinding inheritedMethod, final MethodBinding[] allInheritedMethods) {
        if (currentMethod.isVarargs() != inheritedMethod.isVarargs()) {
            this.problemReporter(currentMethod).varargsConflict(currentMethod, inheritedMethod, this.type);
        }
        final MethodBinding originalInherited = inheritedMethod.original();
        if (TypeBinding.notEquals(originalInherited.returnType, currentMethod.returnType) && !this.isAcceptableReturnTypeOverride(currentMethod, inheritedMethod)) {
            this.problemReporter(currentMethod).unsafeReturnTypeOverride(currentMethod, originalInherited, this.type);
        }
        final MethodBinding bridge = this.type.addSyntheticBridgeMethod(originalInherited, currentMethod.original());
        if (bridge != null) {
            for (int i = 0, l = (allInheritedMethods == null) ? 0 : allInheritedMethods.length; i < l; ++i) {
                if (allInheritedMethods[i] != null && this.detectInheritedNameClash(originalInherited, allInheritedMethods[i].original())) {
                    return;
                }
            }
            final MethodBinding[] current = (MethodBinding[])this.currentMethods.get(bridge.selector);
            for (int j = current.length - 1; j >= 0; --j) {
                final MethodBinding thisMethod = current[j];
                if (thisMethod.areParameterErasuresEqual(bridge) && TypeBinding.equalsEquals(thisMethod.returnType.erasure(), bridge.returnType.erasure())) {
                    this.problemReporter(thisMethod).methodNameClash(thisMethod, inheritedMethod.declaringClass.isRawType() ? inheritedMethod : inheritedMethod.original(), 1);
                    return;
                }
            }
        }
    }
    
    void checkForNameClash(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        if (inheritedMethod.isStatic() || currentMethod.isStatic()) {
            final MethodBinding original = inheritedMethod.original();
            if (this.type.scope.compilerOptions().complianceLevel >= 3342336L && currentMethod.areParameterErasuresEqual(original)) {
                this.problemReporter(currentMethod).methodNameClashHidden(currentMethod, inheritedMethod.declaringClass.isRawType() ? inheritedMethod : original);
            }
            return;
        }
        if (!this.detectNameClash(currentMethod, inheritedMethod, false)) {
            final TypeBinding[] currentParams = currentMethod.parameters;
            final TypeBinding[] inheritedParams = inheritedMethod.parameters;
            final int length = currentParams.length;
            if (length != inheritedParams.length) {
                return;
            }
            for (int i = 0; i < length; ++i) {
                if (TypeBinding.notEquals(currentParams[i], inheritedParams[i]) && (currentParams[i].isBaseType() != inheritedParams[i].isBaseType() || !inheritedParams[i].isCompatibleWith(currentParams[i]))) {
                    return;
                }
            }
            ReferenceBinding[] interfacesToVisit = null;
            int nextPosition = 0;
            ReferenceBinding superType = inheritedMethod.declaringClass;
            ReferenceBinding[] itsInterfaces = superType.superInterfaces();
            if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
                nextPosition = itsInterfaces.length;
                interfacesToVisit = itsInterfaces;
            }
            for (superType = superType.superclass(); superType != null && superType.isValidBinding(); superType = superType.superclass()) {
                final MethodBinding[] methods = superType.getMethods(currentMethod.selector);
                for (int m = 0, n = methods.length; m < n; ++m) {
                    final MethodBinding substitute = this.computeSubstituteMethod(methods[m], currentMethod);
                    if (substitute != null && !this.isSubstituteParameterSubsignature(currentMethod, substitute) && this.detectNameClash(currentMethod, substitute, true)) {
                        return;
                    }
                }
                if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
                    if (interfacesToVisit == null) {
                        interfacesToVisit = itsInterfaces;
                        nextPosition = interfacesToVisit.length;
                    }
                    else {
                        final int itsLength = itsInterfaces.length;
                        if (nextPosition + itsLength >= interfacesToVisit.length) {
                            System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                        }
                        int a = 0;
                    Label_0415:
                        while (a < itsLength) {
                            final ReferenceBinding next = itsInterfaces[a];
                            while (true) {
                                for (int b = 0; b < nextPosition; ++b) {
                                    if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                        ++a;
                                        continue Label_0415;
                                    }
                                }
                                interfacesToVisit[nextPosition++] = next;
                                continue;
                            }
                        }
                    }
                }
            }
            for (int j = 0; j < nextPosition; ++j) {
                superType = interfacesToVisit[j];
                if (superType.isValidBinding()) {
                    final MethodBinding[] methods2 = superType.getMethods(currentMethod.selector);
                    for (int k = 0, n2 = methods2.length; k < n2; ++k) {
                        final MethodBinding substitute2 = this.computeSubstituteMethod(methods2[k], currentMethod);
                        if (substitute2 != null && !this.isSubstituteParameterSubsignature(currentMethod, substitute2) && this.detectNameClash(currentMethod, substitute2, true)) {
                            return;
                        }
                    }
                    if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
                        final int itsLength2 = itsInterfaces.length;
                        if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                            System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                        }
                        int a2 = 0;
                    Label_0644:
                        while (a2 < itsLength2) {
                            final ReferenceBinding next2 = itsInterfaces[a2];
                            while (true) {
                                for (int b2 = 0; b2 < nextPosition; ++b2) {
                                    if (TypeBinding.equalsEquals(next2, interfacesToVisit[b2])) {
                                        ++a2;
                                        continue Label_0644;
                                    }
                                }
                                interfacesToVisit[nextPosition++] = next2;
                                continue;
                            }
                        }
                    }
                }
            }
        }
    }
    
    void checkInheritedMethods(final MethodBinding inheritedMethod, final MethodBinding otherInheritedMethod) {
        if (inheritedMethod.isStatic()) {
            return;
        }
        if (this.environment.globalOptions.complianceLevel < 3342336L && inheritedMethod.declaringClass.isInterface()) {
            return;
        }
        this.detectInheritedNameClash(inheritedMethod.original(), otherInheritedMethod.original());
    }
    
    @Override
    void checkInheritedMethods(final MethodBinding[] methods, final int length, final boolean[] isOverridden, final boolean[] isInherited) {
        boolean continueInvestigation = true;
        MethodBinding concreteMethod = null;
        MethodBinding abstractSuperClassMethod = null;
        boolean playingTrump = false;
        for (int i = 0; i < length; ++i) {
            if (!methods[i].declaringClass.isInterface() && TypeBinding.notEquals(methods[i].declaringClass, this.type) && methods[i].isAbstract()) {
                abstractSuperClassMethod = methods[i];
                break;
            }
        }
        for (int i = 0; i < length; ++i) {
            if (isInherited[i] && !methods[i].isAbstract()) {
                if (methods[i].isDefaultMethod() && abstractSuperClassMethod != null && ImplicitNullAnnotationVerifier.areParametersEqual(abstractSuperClassMethod, methods[i]) && concreteMethod == null) {
                    playingTrump = true;
                }
                else {
                    playingTrump = false;
                    if (concreteMethod != null) {
                        if (isOverridden[i] && this.areMethodsCompatible(concreteMethod, methods[i])) {
                            continue;
                        }
                        if (TypeBinding.equalsEquals(concreteMethod.declaringClass, methods[i].declaringClass) && concreteMethod.typeVariables.length != methods[i].typeVariables.length) {
                            if (concreteMethod.typeVariables == Binding.NO_TYPE_VARIABLES && concreteMethod.original() == methods[i]) {
                                continue;
                            }
                            if (methods[i].typeVariables == Binding.NO_TYPE_VARIABLES && methods[i].original() == concreteMethod) {
                                continue;
                            }
                        }
                        this.problemReporter().duplicateInheritedMethods(this.type, concreteMethod, methods[i], this.environment.globalOptions.sourceLevel >= 3407872L);
                        continueInvestigation = false;
                    }
                    concreteMethod = methods[i];
                }
            }
        }
        if (continueInvestigation) {
            if (playingTrump) {
                if (!this.type.isAbstract()) {
                    this.problemReporter().abstractMethodMustBeImplemented(this.type, abstractSuperClassMethod);
                    return;
                }
            }
            else if (concreteMethod != null && concreteMethod.isDefaultMethod() && this.environment.globalOptions.complianceLevel >= 3407872L && !this.checkInheritedDefaultMethods(methods, isOverridden, length)) {
                return;
            }
            super.checkInheritedMethods(methods, length, isOverridden, isInherited);
        }
    }
    
    boolean checkInheritedDefaultMethods(final MethodBinding[] methods, final boolean[] isOverridden, final int length) {
        if (length < 2) {
            return true;
        }
        boolean ok = true;
        for (int i = 0; i < length; ++i) {
            if (methods[i].isDefaultMethod() && !isOverridden[i]) {
                for (int j = 0; j < length; ++j) {
                    if (j != i) {
                        if (!isOverridden[j]) {
                            if (this.isMethodSubsignature(methods[i], methods[j]) && !this.doesMethodOverride(methods[i], methods[j]) && !this.doesMethodOverride(methods[j], methods[i])) {
                                this.problemReporter().inheritedDefaultMethodConflictsWithOtherInherited(this.type, methods[i], methods[j]);
                                ok = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return ok;
    }
    
    @Override
    boolean checkInheritedReturnTypes(final MethodBinding method, final MethodBinding otherMethod) {
        if (this.areReturnTypesCompatible(method, otherMethod)) {
            return true;
        }
        if (this.isUnsafeReturnTypeOverride(method, otherMethod)) {
            if (!method.declaringClass.implementsInterface(otherMethod.declaringClass, false)) {
                this.problemReporter(method).unsafeReturnTypeOverride(method, otherMethod, this.type);
            }
            return true;
        }
        return false;
    }
    
    @Override
    void checkAgainstInheritedMethods(final MethodBinding currentMethod, final MethodBinding[] methods, final int length, final MethodBinding[] allInheritedMethods) {
        super.checkAgainstInheritedMethods(currentMethod, methods, length, allInheritedMethods);
        final CompilerOptions options = this.environment.globalOptions;
        if (options.isAnnotationBasedNullAnalysisEnabled && (currentMethod.tagBits & 0x1000L) == 0x0L) {
            AbstractMethodDeclaration srcMethod = null;
            if (this.type.equals(currentMethod.declaringClass)) {
                srcMethod = currentMethod.sourceMethod();
            }
            final boolean useTypeAnnotations = this.environment.usesNullTypeAnnotations();
            final boolean hasReturnNonNullDefault = currentMethod.hasNonNullDefaultFor(16, useTypeAnnotations);
            final boolean hasParameterNonNullDefault = currentMethod.hasNonNullDefaultFor(8, useTypeAnnotations);
            int i = length;
            while (--i >= 0) {
                if (!currentMethod.isStatic() && !methods[i].isStatic()) {
                    this.checkNullSpecInheritance(currentMethod, srcMethod, hasReturnNonNullDefault, hasParameterNonNullDefault, true, methods[i], methods, this.type.scope, null);
                }
            }
        }
    }
    
    @Override
    void checkNullSpecInheritance(final MethodBinding currentMethod, final AbstractMethodDeclaration srcMethod, final boolean hasReturnNonNullDefault, final boolean hasParameterNonNullDefault, boolean complain, final MethodBinding inheritedMethod, final MethodBinding[] allInherited, final Scope scope, final InheritedNonNullnessInfo[] inheritedNonNullnessInfos) {
        complain &= !currentMethod.isConstructor();
        if (!hasReturnNonNullDefault && !hasParameterNonNullDefault && !complain && !this.environment.globalOptions.inheritNullAnnotations) {
            currentMethod.tagBits |= 0x1000L;
            return;
        }
        if (TypeBinding.notEquals(currentMethod.declaringClass, this.type) && (currentMethod.tagBits & 0x1000L) == 0x0L) {
            this.buddyImplicitNullAnnotationsVerifier.checkImplicitNullAnnotations(currentMethod, srcMethod, complain, scope);
        }
        super.checkNullSpecInheritance(currentMethod, srcMethod, hasReturnNonNullDefault, hasParameterNonNullDefault, complain, inheritedMethod, allInherited, scope, inheritedNonNullnessInfos);
    }
    
    void reportRawReferences() {
        final CompilerOptions compilerOptions = this.type.scope.compilerOptions();
        if (compilerOptions.sourceLevel < 3211264L || compilerOptions.reportUnavoidableGenericTypeProblems) {
            return;
        }
        final Object[] methodArray = this.currentMethods.valueTable;
        int s = methodArray.length;
        while (--s >= 0) {
            if (methodArray[s] == null) {
                continue;
            }
            final MethodBinding[] current = (MethodBinding[])methodArray[s];
            for (int i = 0, length = current.length; i < length; ++i) {
                final MethodBinding currentMethod = current[i];
                if ((currentMethod.modifiers & 0x30000000) == 0x0) {
                    final AbstractMethodDeclaration methodDecl = currentMethod.sourceMethod();
                    if (methodDecl == null) {
                        return;
                    }
                    final TypeBinding[] parameterTypes = currentMethod.parameters;
                    final Argument[] arguments = methodDecl.arguments;
                    for (int j = 0, size = currentMethod.parameters.length; j < size; ++j) {
                        final TypeBinding parameterType = parameterTypes[j];
                        final Argument arg = arguments[j];
                        if (parameterType.leafComponentType().isRawType() && compilerOptions.getSeverity(536936448) != 256 && (arg.type.bits & 0x40000000) == 0x0) {
                            methodDecl.scope.problemReporter().rawTypeReference(arg.type, parameterType);
                        }
                    }
                    if (!methodDecl.isConstructor() && methodDecl instanceof MethodDeclaration) {
                        final TypeReference returnType = ((MethodDeclaration)methodDecl).returnType;
                        final TypeBinding methodType = currentMethod.returnType;
                        if (returnType != null && methodType.leafComponentType().isRawType() && compilerOptions.getSeverity(536936448) != 256 && (returnType.bits & 0x40000000) == 0x0) {
                            methodDecl.scope.problemReporter().rawTypeReference(returnType, methodType);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void reportRawReferences(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        final CompilerOptions compilerOptions = this.type.scope.compilerOptions();
        if (compilerOptions.sourceLevel < 3211264L || compilerOptions.reportUnavoidableGenericTypeProblems) {
            return;
        }
        final AbstractMethodDeclaration methodDecl = currentMethod.sourceMethod();
        if (methodDecl == null) {
            return;
        }
        final TypeBinding[] parameterTypes = currentMethod.parameters;
        final TypeBinding[] inheritedParameterTypes = inheritedMethod.parameters;
        final Argument[] arguments = methodDecl.arguments;
        for (int j = 0, size = currentMethod.parameters.length; j < size; ++j) {
            final TypeBinding parameterType = parameterTypes[j];
            final TypeBinding inheritedParameterType = inheritedParameterTypes[j];
            final Argument arg = arguments[j];
            if (parameterType.leafComponentType().isRawType()) {
                if (inheritedParameterType.leafComponentType().isRawType()) {
                    final LocalVariableBinding binding = arg.binding;
                    binding.tagBits |= 0x200L;
                }
                else if (compilerOptions.getSeverity(536936448) != 256 && (arg.type.bits & 0x40000000) == 0x0) {
                    methodDecl.scope.problemReporter().rawTypeReference(arg.type, parameterType);
                }
            }
        }
        TypeReference returnType = null;
        if (!methodDecl.isConstructor() && methodDecl instanceof MethodDeclaration && (returnType = ((MethodDeclaration)methodDecl).returnType) != null) {
            final TypeBinding inheritedMethodType = inheritedMethod.returnType;
            final TypeBinding methodType = currentMethod.returnType;
            if (methodType.leafComponentType().isRawType() && !inheritedMethodType.leafComponentType().isRawType() && (returnType.bits & 0x40000000) == 0x0 && compilerOptions.getSeverity(536936448) != 256) {
                methodDecl.scope.problemReporter().rawTypeReference(returnType, methodType);
            }
        }
    }
    
    @Override
    void checkMethods() {
        final boolean mustImplementAbstractMethods = this.mustImplementAbstractMethods();
        final boolean skipInheritedMethods = mustImplementAbstractMethods && this.canSkipInheritedMethods();
        final boolean isOrEnclosedByPrivateType = this.type.isOrEnclosedByPrivateType();
        final char[][] methodSelectors = this.inheritedMethods.keyTable;
        int s = methodSelectors.length;
        while (--s >= 0) {
            if (methodSelectors[s] == null) {
                continue;
            }
            final MethodBinding[] current = (MethodBinding[])this.currentMethods.get(methodSelectors[s]);
            MethodBinding[] inherited = (MethodBinding[])this.inheritedMethods.valueTable[s];
            inherited = Sorting.concreteFirst(inherited, inherited.length);
            if (current == null && !isOrEnclosedByPrivateType) {
                for (int length = inherited.length, i = 0; i < length; ++i) {
                    final MethodBinding original = inherited[i].original();
                    original.modifiers |= 0x8000000;
                }
            }
            if (current == null && this.type.isPublic()) {
                for (final MethodBinding inheritedMethod : inherited) {
                    if (inheritedMethod.isPublic() && !inheritedMethod.declaringClass.isInterface() && !inheritedMethod.declaringClass.isPublic()) {
                        this.type.addSyntheticBridgeMethod(inheritedMethod.original());
                    }
                }
            }
            if (current == null && skipInheritedMethods) {
                continue;
            }
            if (inherited.length == 1 && current == null) {
                if (!mustImplementAbstractMethods || !inherited[0].isAbstract()) {
                    continue;
                }
                this.checkAbstractMethod(inherited[0]);
            }
            else {
                int index = -1;
                final int inheritedLength = inherited.length;
                final MethodBinding[] matchingInherited = new MethodBinding[inheritedLength];
                final MethodBinding[] foundMatch = new MethodBinding[inheritedLength];
                final boolean[] skip = new boolean[inheritedLength];
                final boolean[] isOverridden = new boolean[inheritedLength];
                final boolean[] isInherited = new boolean[inheritedLength];
                Arrays.fill(isInherited, true);
                if (current != null) {
                    for (int j = 0, length2 = current.length; j < length2; ++j) {
                        final MethodBinding currentMethod = current[j];
                        MethodBinding[] nonMatchingInherited = null;
                        for (int k = 0; k < inheritedLength; ++k) {
                            final MethodBinding inheritedMethod2 = this.computeSubstituteMethod(inherited[k], currentMethod);
                            if (inheritedMethod2 != null) {
                                if (foundMatch[k] == null && this.isSubstituteParameterSubsignature(currentMethod, inheritedMethod2)) {
                                    isOverridden[k] = (skip[k] = MethodVerifier.couldMethodOverride(currentMethod, inheritedMethod2));
                                    matchingInherited[++index] = inheritedMethod2;
                                    foundMatch[k] = currentMethod;
                                }
                                else {
                                    this.checkForNameClash(currentMethod, inheritedMethod2);
                                    if (inheritedLength > 1) {
                                        if (nonMatchingInherited == null) {
                                            nonMatchingInherited = new MethodBinding[inheritedLength];
                                        }
                                        nonMatchingInherited[k] = inheritedMethod2;
                                    }
                                }
                            }
                        }
                        if (index >= 0) {
                            this.checkAgainstInheritedMethods(currentMethod, matchingInherited, index + 1, nonMatchingInherited);
                            while (index >= 0) {
                                matchingInherited[index--] = null;
                            }
                        }
                    }
                }
                for (int j = 0; j < inheritedLength; ++j) {
                    final MethodBinding matchMethod = foundMatch[j];
                    if (matchMethod == null && current != null && this.type.isPublic()) {
                        final MethodBinding inheritedMethod3 = inherited[j];
                        if (inheritedMethod3.isPublic() && !inheritedMethod3.declaringClass.isInterface() && !inheritedMethod3.declaringClass.isPublic()) {
                            this.type.addSyntheticBridgeMethod(inheritedMethod3.original());
                        }
                    }
                    if (!isOrEnclosedByPrivateType && matchMethod == null && current != null) {
                        final MethodBinding original2 = inherited[j].original();
                        original2.modifiers |= 0x8000000;
                    }
                    final MethodBinding inheritedMethod3 = inherited[j];
                    for (int l = j + 1; l < inheritedLength; ++l) {
                        final MethodBinding otherInheritedMethod = inherited[l];
                        if (matchMethod != foundMatch[l] || matchMethod == null) {
                            if (!this.canSkipInheritedMethods(inheritedMethod3, otherInheritedMethod)) {
                                if (TypeBinding.notEquals(inheritedMethod3.declaringClass, otherInheritedMethod.declaringClass)) {
                                    if (!this.isSkippableOrOverridden(inheritedMethod3, otherInheritedMethod, skip, isOverridden, isInherited, l)) {
                                        if (this.isSkippableOrOverridden(otherInheritedMethod, inheritedMethod3, skip, isOverridden, isInherited, j)) {}
                                    }
                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < inheritedLength; ++j) {
                    final MethodBinding matchMethod = foundMatch[j];
                    if (!skip[j]) {
                        final MethodBinding inheritedMethod3 = inherited[j];
                        if (matchMethod == null) {
                            matchingInherited[++index] = inheritedMethod3;
                        }
                        for (int l = j + 1; l < inheritedLength; ++l) {
                            if (foundMatch[l] == null) {
                                final MethodBinding otherInheritedMethod = inherited[l];
                                if (matchMethod != foundMatch[l] || matchMethod == null) {
                                    if (!this.canSkipInheritedMethods(inheritedMethod3, otherInheritedMethod)) {
                                        MethodBinding replaceMatch;
                                        if ((replaceMatch = this.findReplacedMethod(inheritedMethod3, otherInheritedMethod)) != null) {
                                            matchingInherited[++index] = replaceMatch;
                                            skip[l] = true;
                                        }
                                        else if ((replaceMatch = this.findReplacedMethod(otherInheritedMethod, inheritedMethod3)) != null) {
                                            matchingInherited[++index] = replaceMatch;
                                            skip[l] = true;
                                        }
                                        else if (matchMethod == null) {
                                            this.checkInheritedMethods(inheritedMethod3, otherInheritedMethod);
                                        }
                                    }
                                }
                            }
                        }
                        if (index != -1) {
                            if (index > 0) {
                                final int length3 = index + 1;
                                boolean[] matchingIsOverridden;
                                boolean[] matchingIsInherited;
                                if (length3 != inheritedLength) {
                                    matchingIsOverridden = new boolean[length3];
                                    matchingIsInherited = new boolean[length3];
                                    for (int m = 0; m < length3; ++m) {
                                        for (int k2 = 0; k2 < inheritedLength; ++k2) {
                                            if (matchingInherited[m] == inherited[k2]) {
                                                matchingIsOverridden[m] = isOverridden[k2];
                                                matchingIsInherited[m] = isInherited[k2];
                                                break;
                                            }
                                        }
                                    }
                                }
                                else {
                                    matchingIsOverridden = isOverridden;
                                    matchingIsInherited = isInherited;
                                }
                                this.checkInheritedMethods(matchingInherited, length3, matchingIsOverridden, matchingIsInherited);
                            }
                            else if (mustImplementAbstractMethods && matchingInherited[0].isAbstract() && matchMethod == null) {
                                this.checkAbstractMethod(matchingInherited[0]);
                            }
                            while (index >= 0) {
                                matchingInherited[index--] = null;
                            }
                        }
                    }
                }
            }
        }
    }
    
    boolean isSkippableOrOverridden(final MethodBinding specific, final MethodBinding general, final boolean[] skip, final boolean[] isOverridden, final boolean[] isInherited, final int idx) {
        final boolean specificIsInterface = specific.declaringClass.isInterface();
        final boolean generalIsInterface = general.declaringClass.isInterface();
        if (!specificIsInterface && generalIsInterface) {
            if (!specific.isAbstract() && this.isParameterSubsignature(specific, general)) {
                isInherited[idx] = false;
                return true;
            }
            if (this.isInterfaceMethodImplemented(general, specific, general.declaringClass)) {
                skip[idx] = true;
                return isOverridden[idx] = true;
            }
        }
        else if (specificIsInterface == generalIsInterface && specific.declaringClass.isCompatibleWith(general.declaringClass) && this.isMethodSubsignature(specific, general)) {
            skip[idx] = true;
            return isOverridden[idx] = true;
        }
        return false;
    }
    
    MethodBinding findReplacedMethod(final MethodBinding specific, final MethodBinding general) {
        final MethodBinding generalSubstitute = this.computeSubstituteMethod(general, specific);
        if (generalSubstitute != null && (!specific.isAbstract() || general.isAbstract() || (general.isDefaultMethod() && specific.declaringClass.isClass())) && this.isSubstituteParameterSubsignature(specific, generalSubstitute)) {
            return generalSubstitute;
        }
        return null;
    }
    
    void checkTypeVariableMethods(final TypeParameter typeParameter) {
        final char[][] methodSelectors = this.inheritedMethods.keyTable;
        int s = methodSelectors.length;
        while (--s >= 0) {
            if (methodSelectors[s] == null) {
                continue;
            }
            final MethodBinding[] inherited = (MethodBinding[])this.inheritedMethods.valueTable[s];
            if (inherited.length == 1) {
                continue;
            }
            int index = -1;
            final MethodBinding[] matchingInherited = new MethodBinding[inherited.length];
            for (int i = 0, length = inherited.length; i < length; ++i) {
                while (index >= 0) {
                    matchingInherited[index--] = null;
                }
                final MethodBinding inheritedMethod = inherited[i];
                if (inheritedMethod != null) {
                    matchingInherited[++index] = inheritedMethod;
                    for (int j = i + 1; j < length; ++j) {
                        MethodBinding otherInheritedMethod = inherited[j];
                        if (!this.canSkipInheritedMethods(inheritedMethod, otherInheritedMethod)) {
                            otherInheritedMethod = this.computeSubstituteMethod(otherInheritedMethod, inheritedMethod);
                            if (otherInheritedMethod != null && this.isSubstituteParameterSubsignature(inheritedMethod, otherInheritedMethod)) {
                                matchingInherited[++index] = otherInheritedMethod;
                                inherited[j] = null;
                            }
                        }
                    }
                }
                if (index > 0) {
                    final MethodBinding first = matchingInherited[0];
                    int count = index + 1;
                    while (--count > 0) {
                        final MethodBinding match = matchingInherited[count];
                        MethodBinding interfaceMethod = null;
                        MethodBinding implementation = null;
                        if (first.declaringClass.isInterface()) {
                            interfaceMethod = first;
                        }
                        else if (first.declaringClass.isClass()) {
                            implementation = first;
                        }
                        if (match.declaringClass.isInterface()) {
                            interfaceMethod = match;
                        }
                        else if (match.declaringClass.isClass()) {
                            implementation = match;
                        }
                        if (interfaceMethod != null && implementation != null && !this.isAsVisible(implementation, interfaceMethod)) {
                            this.problemReporter().inheritedMethodReducesVisibility(typeParameter, implementation, new MethodBinding[] { interfaceMethod });
                        }
                        if (this.areReturnTypesCompatible(first, match)) {
                            continue;
                        }
                        if (first.declaringClass.isInterface() && match.declaringClass.isInterface() && this.areReturnTypesCompatible(match, first)) {
                            continue;
                        }
                        break;
                    }
                    if (count > 0) {
                        this.problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(typeParameter, matchingInherited, index + 1);
                        break;
                    }
                }
            }
        }
    }
    
    boolean detectInheritedNameClash(final MethodBinding inherited, final MethodBinding otherInherited) {
        if (!inherited.areParameterErasuresEqual(otherInherited)) {
            return false;
        }
        if (TypeBinding.notEquals(inherited.returnType.erasure(), otherInherited.returnType.erasure())) {
            return false;
        }
        if (TypeBinding.notEquals(inherited.declaringClass.erasure(), otherInherited.declaringClass.erasure())) {
            if (inherited.declaringClass.findSuperTypeOriginatingFrom(otherInherited.declaringClass) != null) {
                return false;
            }
            if (otherInherited.declaringClass.findSuperTypeOriginatingFrom(inherited.declaringClass) != null) {
                return false;
            }
        }
        this.problemReporter().inheritedMethodsHaveNameClash(this.type, inherited, otherInherited);
        return true;
    }
    
    boolean detectNameClash(final MethodBinding current, final MethodBinding inherited, final boolean treatAsSynthetic) {
        MethodBinding methodToCheck = inherited;
        MethodBinding original = methodToCheck.original();
        if (!current.areParameterErasuresEqual(original)) {
            return false;
        }
        int severity = 1;
        if (this.environment.globalOptions.complianceLevel == 3276800L && TypeBinding.notEquals(current.returnType.erasure(), original.returnType.erasure())) {
            severity = 0;
        }
        if (!treatAsSynthetic) {
            final MethodBinding[] currentNamesakes = (MethodBinding[])this.currentMethods.get(inherited.selector);
            if (currentNamesakes.length > 1) {
                for (int i = 0, length = currentNamesakes.length; i < length; ++i) {
                    final MethodBinding currentMethod = currentNamesakes[i];
                    if (currentMethod != current && this.doesMethodOverride(currentMethod, inherited)) {
                        methodToCheck = currentMethod;
                        break;
                    }
                }
            }
        }
        original = methodToCheck.original();
        if (!current.areParameterErasuresEqual(original)) {
            return false;
        }
        original = inherited.original();
        this.problemReporter(current).methodNameClash(current, inherited.declaringClass.isRawType() ? inherited : original, severity);
        return severity != 0;
    }
    
    boolean doTypeVariablesClash(final MethodBinding one, final MethodBinding substituteTwo) {
        return one.typeVariables != Binding.NO_TYPE_VARIABLES && !(substituteTwo instanceof ParameterizedGenericMethodBinding);
    }
    
    @Override
    SimpleSet findSuperinterfaceCollisions(final ReferenceBinding superclass, final ReferenceBinding[] superInterfaces) {
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        ReferenceBinding[] itsInterfaces = superInterfaces;
        if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
            nextPosition = itsInterfaces.length;
            interfacesToVisit = itsInterfaces;
        }
        boolean isInconsistent = this.type.isHierarchyInconsistent();
        for (ReferenceBinding superType = superclass; superType != null && superType.isValidBinding(); superType = superType.superclass()) {
            isInconsistent |= superType.isHierarchyInconsistent();
            if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                }
                else {
                    final int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                    }
                    int a = 0;
                Label_0168:
                    while (a < itsLength) {
                        final ReferenceBinding next = itsInterfaces[a];
                        while (true) {
                            for (int b = 0; b < nextPosition; ++b) {
                                if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++a;
                                    continue Label_0168;
                                }
                            }
                            interfacesToVisit[nextPosition++] = next;
                            continue;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < nextPosition; ++i) {
            final ReferenceBinding superType = interfacesToVisit[i];
            if (superType.isValidBinding()) {
                isInconsistent |= superType.isHierarchyInconsistent();
                if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
                    final int itsLength2 = itsInterfaces.length;
                    if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                        System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                    }
                    int a2 = 0;
                Label_0330:
                    while (a2 < itsLength2) {
                        final ReferenceBinding next2 = itsInterfaces[a2];
                        while (true) {
                            for (int b2 = 0; b2 < nextPosition; ++b2) {
                                if (TypeBinding.equalsEquals(next2, interfacesToVisit[b2])) {
                                    ++a2;
                                    continue Label_0330;
                                }
                            }
                            interfacesToVisit[nextPosition++] = next2;
                            continue;
                        }
                    }
                }
            }
        }
        if (!isInconsistent) {
            return null;
        }
        SimpleSet copy = null;
        for (int j = 0; j < nextPosition; ++j) {
            final ReferenceBinding current = interfacesToVisit[j];
            if (current.isValidBinding()) {
                final TypeBinding erasure = current.erasure();
                for (int k = j + 1; k < nextPosition; ++k) {
                    final ReferenceBinding next3 = interfacesToVisit[k];
                    if (next3.isValidBinding() && TypeBinding.equalsEquals(next3.erasure(), erasure)) {
                        if (copy == null) {
                            copy = new SimpleSet(nextPosition);
                        }
                        copy.add(interfacesToVisit[j]);
                        copy.add(interfacesToVisit[k]);
                    }
                }
            }
        }
        return copy;
    }
    
    boolean isAcceptableReturnTypeOverride(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        if (inheritedMethod.declaringClass.isRawType()) {
            return true;
        }
        final MethodBinding originalInherited = inheritedMethod.original();
        final TypeBinding originalInheritedReturnType = originalInherited.returnType.leafComponentType();
        if (originalInheritedReturnType.isParameterizedTypeWithActualArguments()) {
            return !currentMethod.returnType.leafComponentType().isRawType();
        }
        final TypeBinding currentReturnType = currentMethod.returnType.leafComponentType();
        switch (currentReturnType.kind()) {
            case 4100: {
                if (TypeBinding.equalsEquals(currentReturnType, inheritedMethod.returnType.leafComponentType())) {
                    return true;
                }
                break;
            }
        }
        return !originalInheritedReturnType.isTypeVariable() || ((TypeVariableBinding)originalInheritedReturnType).declaringElement != originalInherited;
    }
    
    @Override
    boolean isInterfaceMethodImplemented(MethodBinding inheritedMethod, final MethodBinding existingMethod, final ReferenceBinding superType) {
        if (inheritedMethod.original() != inheritedMethod && existingMethod.declaringClass.isInterface()) {
            return false;
        }
        inheritedMethod = this.computeSubstituteMethod(inheritedMethod, existingMethod);
        return inheritedMethod != null && this.doesMethodOverride(existingMethod, inheritedMethod) && (TypeBinding.equalsEquals(inheritedMethod.returnType, existingMethod.returnType) || (TypeBinding.notEquals(this.type, existingMethod.declaringClass) && !existingMethod.declaringClass.isInterface() && this.areReturnTypesCompatible(existingMethod, inheritedMethod)));
    }
    
    @Override
    public boolean isMethodSubsignature(MethodBinding method, final MethodBinding inheritedMethod) {
        if (!CharOperation.equals(method.selector, inheritedMethod.selector)) {
            return false;
        }
        if (method.declaringClass.isParameterizedType()) {
            method = method.original();
        }
        final MethodBinding inheritedOriginal = method.findOriginalInheritedMethod(inheritedMethod);
        return this.isParameterSubsignature(method, (inheritedOriginal == null) ? inheritedMethod : inheritedOriginal);
    }
    
    boolean isUnsafeReturnTypeOverride(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        if (TypeBinding.equalsEquals(currentMethod.returnType, inheritedMethod.returnType.erasure())) {
            final TypeBinding[] currentParams = currentMethod.parameters;
            final TypeBinding[] inheritedParams = inheritedMethod.parameters;
            for (int i = 0, l = currentParams.length; i < l; ++i) {
                if (!ImplicitNullAnnotationVerifier.areTypesEqual(currentParams[i], inheritedParams[i])) {
                    return true;
                }
            }
        }
        return currentMethod.typeVariables == Binding.NO_TYPE_VARIABLES && inheritedMethod.original().typeVariables != Binding.NO_TYPE_VARIABLES && currentMethod.returnType.erasure().findSuperTypeOriginatingFrom(inheritedMethod.returnType.erasure()) != null;
    }
    
    @Override
    boolean reportIncompatibleReturnTypeError(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        if (this.isUnsafeReturnTypeOverride(currentMethod, inheritedMethod)) {
            this.problemReporter(currentMethod).unsafeReturnTypeOverride(currentMethod, inheritedMethod, this.type);
            return false;
        }
        return super.reportIncompatibleReturnTypeError(currentMethod, inheritedMethod);
    }
    
    @Override
    void verify() {
        if (this.type.isAnnotationType()) {
            this.type.detectAnnotationCycle();
        }
        super.verify();
        this.reportRawReferences();
        int i = this.type.typeVariables.length;
        while (--i >= 0) {
            final TypeVariableBinding var = this.type.typeVariables[i];
            if (var.superInterfaces == Binding.NO_SUPERINTERFACES) {
                continue;
            }
            if (var.superInterfaces.length == 1 && var.superclass.id == 1) {
                continue;
            }
            this.currentMethods = new HashtableOfObject(0);
            ReferenceBinding superclass = var.superclass();
            if (superclass.kind() == 4100) {
                superclass = (ReferenceBinding)superclass.erasure();
            }
            final ReferenceBinding[] itsInterfaces = var.superInterfaces();
            final ReferenceBinding[] superInterfaces = new ReferenceBinding[itsInterfaces.length];
            int j = itsInterfaces.length;
            while (--j >= 0) {
                superInterfaces[j] = (ReferenceBinding)((itsInterfaces[j].kind() == 4100) ? itsInterfaces[j].erasure() : itsInterfaces[j]);
            }
            this.computeInheritedMethods(superclass, superInterfaces);
            this.checkTypeVariableMethods(this.type.scope.referenceContext.typeParameters[i]);
        }
    }
}
