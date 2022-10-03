package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.List;
import org.eclipse.jdt.internal.compiler.util.Sorting;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;

public abstract class MethodVerifier extends ImplicitNullAnnotationVerifier
{
    SourceTypeBinding type;
    HashtableOfObject inheritedMethods;
    HashtableOfObject currentMethods;
    HashtableOfObject inheritedOverriddenMethods;
    
    MethodVerifier(final LookupEnvironment environment) {
        super(environment);
        this.type = null;
        this.inheritedMethods = null;
        this.currentMethods = null;
        this.inheritedOverriddenMethods = null;
    }
    
    boolean areMethodsCompatible(final MethodBinding one, final MethodBinding two) {
        return areMethodsCompatible(one, two, this.environment);
    }
    
    static boolean areMethodsCompatible(MethodBinding one, MethodBinding two, final LookupEnvironment environment) {
        one = one.original();
        two = one.findOriginalInheritedMethod(two);
        return two != null && isParameterSubsignature(one, two, environment);
    }
    
    boolean areReturnTypesCompatible(final MethodBinding one, final MethodBinding two) {
        return areReturnTypesCompatible(one, two, this.type.scope.environment());
    }
    
    public static boolean areReturnTypesCompatible(final MethodBinding one, final MethodBinding two, final LookupEnvironment environment) {
        if (TypeBinding.equalsEquals(one.returnType, two.returnType)) {
            return true;
        }
        if (environment.globalOptions.sourceLevel < 3211264L) {
            return ImplicitNullAnnotationVerifier.areTypesEqual(one.returnType.erasure(), two.returnType.erasure());
        }
        if (one.returnType.isBaseType()) {
            return false;
        }
        if (!one.declaringClass.isInterface() && one.declaringClass.id == 1) {
            return two.returnType.isCompatibleWith(one.returnType);
        }
        return one.returnType.isCompatibleWith(two.returnType);
    }
    
    boolean canSkipInheritedMethods() {
        return (this.type.superclass() == null || !this.type.superclass().isAbstract()) && this.type.superInterfaces() == Binding.NO_SUPERINTERFACES;
    }
    
    boolean canSkipInheritedMethods(final MethodBinding one, final MethodBinding two) {
        return two == null || TypeBinding.equalsEquals(one.declaringClass, two.declaringClass);
    }
    
    void checkAbstractMethod(final MethodBinding abstractMethod) {
        if (this.mustImplementAbstractMethod(abstractMethod.declaringClass)) {
            final TypeDeclaration typeDeclaration = this.type.scope.referenceContext;
            if (typeDeclaration != null) {
                final MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(abstractMethod);
                missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, abstractMethod);
            }
            else {
                this.problemReporter().abstractMethodMustBeImplemented(this.type, abstractMethod);
            }
        }
    }
    
    void checkAgainstInheritedMethods(final MethodBinding currentMethod, final MethodBinding[] methods, final int length, final MethodBinding[] allInheritedMethods) {
        if (this.type.isAnnotationType()) {
            this.problemReporter().annotationCannotOverrideMethod(currentMethod, methods[length - 1]);
            return;
        }
        final CompilerOptions options = this.type.scope.compilerOptions();
        final int[] overriddenInheritedMethods = (int[])((length > 1) ? this.findOverriddenInheritedMethods(methods, length) : null);
        int i = length;
    Label_0492:
        while (--i >= 0) {
            final MethodBinding inheritedMethod = methods[i];
            if (overriddenInheritedMethods == null || overriddenInheritedMethods[i] == 0) {
                if (currentMethod.isStatic() != inheritedMethod.isStatic()) {
                    this.problemReporter(currentMethod).staticAndInstanceConflict(currentMethod, inheritedMethod);
                    continue;
                }
                if (inheritedMethod.isAbstract()) {
                    if (inheritedMethod.declaringClass.isInterface()) {
                        currentMethod.modifiers |= 0x20000000;
                    }
                    else {
                        currentMethod.modifiers |= 0x30000000;
                    }
                }
                else if (inheritedMethod.isPublic() || !this.type.isInterface()) {
                    if (currentMethod.isDefaultMethod() && !inheritedMethod.isFinal() && inheritedMethod.declaringClass.id == 1) {
                        this.problemReporter(currentMethod).defaultMethodOverridesObjectMethod(currentMethod);
                    }
                    else if (inheritedMethod.isDefaultMethod()) {
                        currentMethod.modifiers |= 0x20000000;
                    }
                    else {
                        currentMethod.modifiers |= 0x10000000;
                    }
                }
                if (!this.areReturnTypesCompatible(currentMethod, inheritedMethod) && (currentMethod.returnType.tagBits & 0x80L) == 0x0L && this.reportIncompatibleReturnTypeError(currentMethod, inheritedMethod)) {
                    continue;
                }
                this.reportRawReferences(currentMethod, inheritedMethod);
                if (currentMethod.thrownExceptions != Binding.NO_EXCEPTIONS) {
                    this.checkExceptions(currentMethod, inheritedMethod);
                }
                if (inheritedMethod.isFinal()) {
                    this.problemReporter(currentMethod).finalMethodCannotBeOverridden(currentMethod, inheritedMethod);
                }
                if (!this.isAsVisible(currentMethod, inheritedMethod)) {
                    this.problemReporter(currentMethod).visibilityConflict(currentMethod, inheritedMethod);
                }
                if (inheritedMethod.isSynchronized() && !currentMethod.isSynchronized()) {
                    this.problemReporter(currentMethod).missingSynchronizedOnInheritedMethod(currentMethod, inheritedMethod);
                }
                if (options.reportDeprecationWhenOverridingDeprecatedMethod && inheritedMethod.isViewedAsDeprecated() && (!currentMethod.isViewedAsDeprecated() || options.reportDeprecationInsideDeprecatedCode)) {
                    final ReferenceBinding declaringClass = inheritedMethod.declaringClass;
                    if (declaringClass.isInterface()) {
                        int j = length;
                        while (--j >= 0) {
                            if (i != j && methods[j].declaringClass.implementsInterface(declaringClass, false)) {
                                continue Label_0492;
                            }
                        }
                    }
                    this.problemReporter(currentMethod).overridesDeprecatedMethod(currentMethod, inheritedMethod);
                }
            }
            if (!inheritedMethod.isStatic() && !inheritedMethod.isFinal()) {
                this.checkForBridgeMethod(currentMethod, inheritedMethod, allInheritedMethods);
            }
        }
        final MethodBinding[] overridden = (MethodBinding[])this.inheritedOverriddenMethods.get(currentMethod.selector);
        if (overridden != null) {
            int k = overridden.length;
            while (--k >= 0) {
                final MethodBinding inheritedMethod2 = overridden[k];
                if (this.isParameterSubsignature(currentMethod, inheritedMethod2) && !inheritedMethod2.isStatic() && !inheritedMethod2.isFinal()) {
                    this.checkForBridgeMethod(currentMethod, inheritedMethod2, allInheritedMethods);
                }
            }
        }
    }
    
    void addBridgeMethodCandidate(final MethodBinding overriddenMethod) {
        MethodBinding[] existing = (MethodBinding[])this.inheritedOverriddenMethods.get(overriddenMethod.selector);
        if (existing == null) {
            existing = new MethodBinding[] { overriddenMethod };
        }
        else {
            final int length = existing.length;
            System.arraycopy(existing, 0, existing = new MethodBinding[length + 1], 0, length);
            existing[length] = overriddenMethod;
        }
        this.inheritedOverriddenMethods.put(overriddenMethod.selector, existing);
    }
    
    public void reportRawReferences(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
    }
    
    void checkConcreteInheritedMethod(final MethodBinding concreteMethod, final MethodBinding[] abstractMethods) {
        if (concreteMethod.isStatic()) {
            this.problemReporter().staticInheritedMethodConflicts(this.type, concreteMethod, abstractMethods);
        }
        if (!concreteMethod.isPublic()) {
            int index = 0;
            final int length = abstractMethods.length;
            if (concreteMethod.isProtected()) {
                while (index < length) {
                    if (abstractMethods[index].isPublic()) {
                        break;
                    }
                    ++index;
                }
            }
            else if (concreteMethod.isDefault()) {
                while (index < length) {
                    if (!abstractMethods[index].isDefault()) {
                        break;
                    }
                    ++index;
                }
            }
            if (index < length) {
                this.problemReporter().inheritedMethodReducesVisibility(this.type, concreteMethod, abstractMethods);
            }
        }
        if (concreteMethod.thrownExceptions != Binding.NO_EXCEPTIONS) {
            int i = abstractMethods.length;
            while (--i >= 0) {
                this.checkExceptions(concreteMethod, abstractMethods[i]);
            }
        }
        if (concreteMethod.isOrEnclosedByPrivateType()) {
            final MethodBinding original = concreteMethod.original();
            original.modifiers |= 0x8000000;
        }
    }
    
    void checkExceptions(final MethodBinding newMethod, final MethodBinding inheritedMethod) {
        final ReferenceBinding[] newExceptions = this.resolvedExceptionTypesFor(newMethod);
        final ReferenceBinding[] inheritedExceptions = this.resolvedExceptionTypesFor(inheritedMethod);
        int i = newExceptions.length;
        while (--i >= 0) {
            final ReferenceBinding newException = newExceptions[i];
            int j = inheritedExceptions.length;
            while (--j > -1 && !this.isSameClassOrSubclassOf(newException, inheritedExceptions[j])) {}
            if (j == -1 && !newException.isUncheckedException(false) && (newException.tagBits & 0x80L) == 0x0L) {
                this.problemReporter(newMethod).incompatibleExceptionInThrowsClause(this.type, newMethod, inheritedMethod, newException);
            }
        }
    }
    
    void checkForBridgeMethod(final MethodBinding currentMethod, final MethodBinding inheritedMethod, final MethodBinding[] allInheritedMethods) {
    }
    
    void checkForMissingHashCodeMethod() {
        final MethodBinding[] choices = this.type.getMethods(TypeConstants.EQUALS);
        boolean overridesEquals = false;
        for (int i = choices.length; !overridesEquals && --i >= 0; overridesEquals = (choices[i].parameters.length == 1 && choices[i].parameters[0].id == 1)) {}
        if (overridesEquals) {
            final MethodBinding hashCodeMethod = this.type.getExactMethod(TypeConstants.HASHCODE, Binding.NO_PARAMETERS, null);
            if (hashCodeMethod != null && hashCodeMethod.declaringClass.id == 1) {
                this.problemReporter().shouldImplementHashcode(this.type);
            }
        }
    }
    
    void checkForRedundantSuperinterfaces(final ReferenceBinding superclass, final ReferenceBinding[] superInterfaces) {
        if (superInterfaces == Binding.NO_SUPERINTERFACES) {
            return;
        }
        final SimpleSet interfacesToCheck = new SimpleSet(superInterfaces.length);
        SimpleSet redundantInterfaces = null;
        for (int i = 0, l = superInterfaces.length; i < l; ++i) {
            final ReferenceBinding toCheck = superInterfaces[i];
            for (int j = 0; j < l; ++j) {
                final ReferenceBinding implementedInterface = superInterfaces[j];
                if (i != j && toCheck.implementsInterface(implementedInterface, true)) {
                    if (redundantInterfaces == null) {
                        redundantInterfaces = new SimpleSet(3);
                    }
                    else if (redundantInterfaces.includes(implementedInterface)) {
                        continue;
                    }
                    redundantInterfaces.add(implementedInterface);
                    final TypeReference[] refs = this.type.scope.referenceContext.superInterfaces;
                    for (int r = 0, rl = refs.length; r < rl; ++r) {
                        if (TypeBinding.equalsEquals(refs[r].resolvedType, toCheck)) {
                            this.problemReporter().redundantSuperInterface(this.type, refs[j], implementedInterface, toCheck);
                            break;
                        }
                    }
                }
            }
            interfacesToCheck.add(toCheck);
        }
        ReferenceBinding[] itsInterfaces = null;
        final SimpleSet inheritedInterfaces = new SimpleSet(5);
        for (ReferenceBinding superType = superclass; superType != null && superType.isValidBinding(); superType = superType.superclass()) {
            if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
                for (int k = 0, m = itsInterfaces.length; k < m; ++k) {
                    final ReferenceBinding inheritedInterface = itsInterfaces[k];
                    if (!inheritedInterfaces.includes(inheritedInterface) && inheritedInterface.isValidBinding()) {
                        if (interfacesToCheck.includes(inheritedInterface)) {
                            if (redundantInterfaces == null) {
                                redundantInterfaces = new SimpleSet(3);
                            }
                            else if (redundantInterfaces.includes(inheritedInterface)) {
                                continue;
                            }
                            redundantInterfaces.add(inheritedInterface);
                            final TypeReference[] refs2 = this.type.scope.referenceContext.superInterfaces;
                            for (int r2 = 0, rl2 = refs2.length; r2 < rl2; ++r2) {
                                if (TypeBinding.equalsEquals(refs2[r2].resolvedType, inheritedInterface)) {
                                    this.problemReporter().redundantSuperInterface(this.type, refs2[r2], inheritedInterface, superType);
                                    break;
                                }
                            }
                        }
                        else {
                            inheritedInterfaces.add(inheritedInterface);
                        }
                    }
                }
            }
        }
        int nextPosition = inheritedInterfaces.elementSize;
        if (nextPosition == 0) {
            return;
        }
        ReferenceBinding[] interfacesToVisit = new ReferenceBinding[nextPosition];
        inheritedInterfaces.asArray(interfacesToVisit);
        for (int i2 = 0; i2 < nextPosition; ++i2) {
            final ReferenceBinding superType = interfacesToVisit[i2];
            if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
                final int itsLength = itsInterfaces.length;
                if (nextPosition + itsLength >= interfacesToVisit.length) {
                    System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                }
                for (final ReferenceBinding inheritedInterface2 : itsInterfaces) {
                    Label_0708: {
                        if (!inheritedInterfaces.includes(inheritedInterface2) && inheritedInterface2.isValidBinding()) {
                            if (interfacesToCheck.includes(inheritedInterface2)) {
                                if (redundantInterfaces == null) {
                                    redundantInterfaces = new SimpleSet(3);
                                }
                                else if (redundantInterfaces.includes(inheritedInterface2)) {
                                    break Label_0708;
                                }
                                redundantInterfaces.add(inheritedInterface2);
                                final TypeReference[] refs3 = this.type.scope.referenceContext.superInterfaces;
                                for (int r3 = 0, rl3 = refs3.length; r3 < rl3; ++r3) {
                                    if (TypeBinding.equalsEquals(refs3[r3].resolvedType, inheritedInterface2)) {
                                        this.problemReporter().redundantSuperInterface(this.type, refs3[r3], inheritedInterface2, superType);
                                        break;
                                    }
                                }
                            }
                            else {
                                inheritedInterfaces.add(inheritedInterface2);
                                interfacesToVisit[nextPosition++] = inheritedInterface2;
                            }
                        }
                    }
                }
            }
        }
    }
    
    void checkInheritedMethods(final MethodBinding[] methods, final int length, final boolean[] isOverridden, final boolean[] isInherited) {
        final MethodBinding concreteMethod = (this.type.isInterface() || methods[0].isAbstract()) ? null : methods[0];
        if (concreteMethod == null) {
            MethodBinding bestAbstractMethod = (length == 1) ? methods[0] : this.findBestInheritedAbstractOrDefaultMethod(methods, length);
            final boolean noMatch = bestAbstractMethod == null;
            if (noMatch) {
                bestAbstractMethod = methods[0];
            }
            if (this.mustImplementAbstractMethod(bestAbstractMethod.declaringClass)) {
                final TypeDeclaration typeDeclaration = this.type.scope.referenceContext;
                final MethodBinding superclassAbstractMethod = methods[0];
                if (superclassAbstractMethod == bestAbstractMethod || superclassAbstractMethod.declaringClass.isInterface()) {
                    if (typeDeclaration != null) {
                        final MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(bestAbstractMethod);
                        missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod);
                    }
                    else {
                        this.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod);
                    }
                }
                else if (typeDeclaration != null) {
                    final MethodDeclaration missingAbstractMethod = typeDeclaration.addMissingAbstractMethodFor(bestAbstractMethod);
                    missingAbstractMethod.scope.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod, superclassAbstractMethod);
                }
                else {
                    this.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod, superclassAbstractMethod);
                }
            }
            else if (noMatch) {
                this.problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(this.type, methods, length, isOverridden);
            }
            return;
        }
        if (length < 2) {
            return;
        }
        int index = length;
        while (--index > 0 && this.checkInheritedReturnTypes(concreteMethod, methods[index])) {}
        if (index > 0) {
            final MethodBinding bestAbstractMethod2 = this.findBestInheritedAbstractOrDefaultMethod(methods, length);
            if (bestAbstractMethod2 == null) {
                this.problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(this.type, methods, length, isOverridden);
            }
            else {
                this.problemReporter().abstractMethodMustBeImplemented(this.type, bestAbstractMethod2, concreteMethod);
            }
            return;
        }
        MethodBinding[] abstractMethods = new MethodBinding[length - 1];
        index = 0;
        for (int i = 0; i < length; ++i) {
            if (methods[i].isAbstract() || (methods[i] != concreteMethod && methods[i].isDefaultMethod())) {
                abstractMethods[index++] = methods[i];
            }
        }
        if (index == 0) {
            return;
        }
        if (index < abstractMethods.length) {
            System.arraycopy(abstractMethods, 0, abstractMethods = new MethodBinding[index], 0, index);
        }
        this.checkConcreteInheritedMethod(concreteMethod, abstractMethods);
    }
    
    boolean checkInheritedReturnTypes(final MethodBinding method, final MethodBinding otherMethod) {
        return this.areReturnTypesCompatible(method, otherMethod) || (!this.type.isInterface() && (method.declaringClass.isClass() || !this.type.implementsInterface(method.declaringClass, false)) && (otherMethod.declaringClass.isClass() || !this.type.implementsInterface(otherMethod.declaringClass, false)));
    }
    
    abstract void checkMethods();
    
    void checkPackagePrivateAbstractMethod(final MethodBinding abstractMethod) {
        final PackageBinding necessaryPackage = abstractMethod.declaringClass.fPackage;
        if (necessaryPackage == this.type.fPackage) {
            return;
        }
        ReferenceBinding superType = this.type.superclass();
        final char[] selector = abstractMethod.selector;
        while (superType.isValidBinding()) {
            if (!superType.isAbstract()) {
                return;
            }
            if (necessaryPackage == superType.fPackage) {
                final MethodBinding[] methods = superType.getMethods(selector);
                int m = methods.length;
                while (--m >= 0) {
                    final MethodBinding method = methods[m];
                    if (!method.isPrivate() && !method.isConstructor()) {
                        if (method.isDefaultAbstract()) {
                            continue;
                        }
                        if (this.areMethodsCompatible(method, abstractMethod)) {
                            return;
                        }
                        continue;
                    }
                }
            }
            if (!TypeBinding.notEquals(superType = superType.superclass(), abstractMethod.declaringClass)) {
                this.problemReporter().abstractMethodCannotBeOverridden(this.type, abstractMethod);
            }
        }
    }
    
    void computeInheritedMethods() {
        final ReferenceBinding superclass = this.type.isInterface() ? this.type.scope.getJavaLangObject() : this.type.superclass();
        this.computeInheritedMethods(superclass, this.type.superInterfaces());
        this.checkForRedundantSuperinterfaces(superclass, this.type.superInterfaces());
    }
    
    void computeInheritedMethods(final ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
        this.inheritedMethods = new HashtableOfObject(51);
        this.inheritedOverriddenMethods = new HashtableOfObject(11);
        ReferenceBinding superType = superclass;
        final HashtableOfObject nonVisibleDefaultMethods = new HashtableOfObject(3);
        while (superType != null && superType.isValidBinding()) {
            final MethodBinding[] methods = superType.unResolvedMethods();
            int m = methods.length;
        Label_0642:
            while (--m >= 0) {
                final MethodBinding inheritedMethod = methods[m];
                if (!inheritedMethod.isPrivate() && !inheritedMethod.isConstructor()) {
                    if (inheritedMethod.isDefaultAbstract()) {
                        continue;
                    }
                    MethodBinding[] existingMethods = (MethodBinding[])this.inheritedMethods.get(inheritedMethod.selector);
                    if (existingMethods != null) {
                        for (int i = 0, length = existingMethods.length; i < length; ++i) {
                            final MethodBinding existingMethod = existingMethods[i];
                            if (TypeBinding.notEquals(existingMethod.declaringClass, inheritedMethod.declaringClass) && this.areMethodsCompatible(existingMethod, inheritedMethod) && !this.canOverridingMethodDifferInErasure(existingMethod, inheritedMethod)) {
                                if (inheritedMethod.isDefault()) {
                                    if (inheritedMethod.isAbstract()) {
                                        this.checkPackagePrivateAbstractMethod(inheritedMethod);
                                    }
                                    else if (existingMethod.declaringClass.fPackage != inheritedMethod.declaringClass.fPackage && this.type.fPackage == inheritedMethod.declaringClass.fPackage && !this.areReturnTypesCompatible(inheritedMethod, existingMethod)) {
                                        continue;
                                    }
                                }
                                if (TypeBinding.notEquals(inheritedMethod.returnType.erasure(), existingMethod.returnType.erasure()) && this.areReturnTypesCompatible(existingMethod, inheritedMethod)) {
                                    this.addBridgeMethodCandidate(inheritedMethod);
                                    continue Label_0642;
                                }
                                continue Label_0642;
                            }
                        }
                    }
                    if (!inheritedMethod.isDefault() || inheritedMethod.declaringClass.fPackage == this.type.fPackage) {
                        if (existingMethods == null) {
                            existingMethods = new MethodBinding[] { inheritedMethod };
                        }
                        else {
                            final int length2 = existingMethods.length;
                            System.arraycopy(existingMethods, 0, existingMethods = new MethodBinding[length2 + 1], 0, length2);
                            existingMethods[length2] = inheritedMethod;
                        }
                        this.inheritedMethods.put(inheritedMethod.selector, existingMethods);
                    }
                    else {
                        MethodBinding[] nonVisible = (MethodBinding[])nonVisibleDefaultMethods.get(inheritedMethod.selector);
                        if (nonVisible != null && inheritedMethod.isAbstract()) {
                            for (int j = 0, l = nonVisible.length; j < l; ++j) {
                                if (this.areMethodsCompatible(nonVisible[j], inheritedMethod)) {
                                    continue Label_0642;
                                }
                            }
                        }
                        if (nonVisible == null) {
                            nonVisible = new MethodBinding[] { inheritedMethod };
                        }
                        else {
                            final int length = nonVisible.length;
                            System.arraycopy(nonVisible, 0, nonVisible = new MethodBinding[length + 1], 0, length);
                            nonVisible[length] = inheritedMethod;
                        }
                        nonVisibleDefaultMethods.put(inheritedMethod.selector, nonVisible);
                        if (inheritedMethod.isAbstract() && !this.type.isAbstract()) {
                            this.problemReporter().abstractMethodCannotBeOverridden(this.type, inheritedMethod);
                        }
                        final MethodBinding[] current = (MethodBinding[])this.currentMethods.get(inheritedMethod.selector);
                        if (current == null || inheritedMethod.isStatic()) {
                            continue;
                        }
                        for (int k = 0, length3 = current.length; k < length3; ++k) {
                            if (!current[k].isStatic() && this.areMethodsCompatible(current[k], inheritedMethod)) {
                                this.problemReporter().overridesPackageDefaultMethod(current[k], inheritedMethod);
                                break;
                            }
                        }
                    }
                }
            }
            superType = superType.superclass();
        }
        final List superIfcList = new ArrayList();
        final HashSet seenTypes = new HashSet();
        this.collectAllDistinctSuperInterfaces(superInterfaces, seenTypes, superIfcList);
        for (ReferenceBinding currentSuper = superclass; currentSuper != null && currentSuper.id != 1; currentSuper = currentSuper.superclass()) {
            this.collectAllDistinctSuperInterfaces(currentSuper.superInterfaces(), seenTypes, superIfcList);
        }
        if (superIfcList.size() == 0) {
            return;
        }
        if (superIfcList.size() == 1) {
            superInterfaces = new ReferenceBinding[] { superIfcList.get(0) };
        }
        else {
            superInterfaces = superIfcList.toArray(new ReferenceBinding[superIfcList.size()]);
            superInterfaces = Sorting.sortTypes(superInterfaces);
        }
        final SimpleSet skip = this.findSuperinterfaceCollisions(superclass, superInterfaces);
        final int len = superInterfaces.length;
        for (int j = len - 1; j >= 0; --j) {
            superType = superInterfaces[j];
            if (superType.isValidBinding()) {
                if (skip == null || !skip.includes(superType)) {
                    final MethodBinding[] methods2 = superType.unResolvedMethods();
                    int m2 = methods2.length;
                Label_1046:
                    while (--m2 >= 0) {
                        final MethodBinding inheritedMethod2 = methods2[m2];
                        if (inheritedMethod2.isStatic()) {
                            continue;
                        }
                        MethodBinding[] existingMethods2 = (MethodBinding[])this.inheritedMethods.get(inheritedMethod2.selector);
                        if (existingMethods2 == null) {
                            existingMethods2 = new MethodBinding[] { inheritedMethod2 };
                        }
                        else {
                            final int length4 = existingMethods2.length;
                            for (int e = 0; e < length4; ++e) {
                                if (this.isInterfaceMethodImplemented(inheritedMethod2, existingMethods2[e], superType)) {
                                    if (TypeBinding.notEquals(inheritedMethod2.returnType.erasure(), existingMethods2[e].returnType.erasure())) {
                                        this.addBridgeMethodCandidate(inheritedMethod2);
                                    }
                                    if (!this.canOverridingMethodDifferInErasure(existingMethods2[e], inheritedMethod2)) {
                                        continue Label_1046;
                                    }
                                }
                            }
                            System.arraycopy(existingMethods2, 0, existingMethods2 = new MethodBinding[length4 + 1], 0, length4);
                            existingMethods2[length4] = inheritedMethod2;
                        }
                        this.inheritedMethods.put(inheritedMethod2.selector, existingMethods2);
                    }
                }
            }
        }
    }
    
    void collectAllDistinctSuperInterfaces(final ReferenceBinding[] superInterfaces, final Set seen, final List result) {
        for (final ReferenceBinding superInterface : superInterfaces) {
            if (seen.add(superInterface)) {
                result.add(superInterface);
                this.collectAllDistinctSuperInterfaces(superInterface.superInterfaces(), seen, result);
            }
        }
    }
    
    protected boolean canOverridingMethodDifferInErasure(final MethodBinding overridingMethod, final MethodBinding inheritedMethod) {
        return false;
    }
    
    void computeMethods() {
        final MethodBinding[] methods = this.type.methods();
        final int size = methods.length;
        this.currentMethods = new HashtableOfObject((size == 0) ? 1 : size);
        int m = size;
        while (--m >= 0) {
            final MethodBinding method = methods[m];
            if (!method.isConstructor() && !method.isDefaultAbstract()) {
                MethodBinding[] existingMethods = (MethodBinding[])this.currentMethods.get(method.selector);
                if (existingMethods == null) {
                    existingMethods = new MethodBinding[] { null };
                }
                else {
                    System.arraycopy(existingMethods, 0, existingMethods = new MethodBinding[existingMethods.length + 1], 0, existingMethods.length - 1);
                }
                existingMethods[existingMethods.length - 1] = method;
                this.currentMethods.put(method.selector, existingMethods);
            }
        }
    }
    
    MethodBinding computeSubstituteMethod(final MethodBinding inheritedMethod, final MethodBinding currentMethod) {
        return computeSubstituteMethod(inheritedMethod, currentMethod, this.environment);
    }
    
    public static MethodBinding computeSubstituteMethod(final MethodBinding inheritedMethod, final MethodBinding currentMethod, final LookupEnvironment environment) {
        if (inheritedMethod == null) {
            return null;
        }
        if (currentMethod.parameters.length != inheritedMethod.parameters.length) {
            return null;
        }
        if (currentMethod.declaringClass instanceof BinaryTypeBinding) {
            ((BinaryTypeBinding)currentMethod.declaringClass).resolveTypesFor(currentMethod);
        }
        if (inheritedMethod.declaringClass instanceof BinaryTypeBinding) {
            ((BinaryTypeBinding)inheritedMethod.declaringClass).resolveTypesFor(inheritedMethod);
        }
        final TypeVariableBinding[] inheritedTypeVariables = inheritedMethod.typeVariables;
        final int inheritedLength = inheritedTypeVariables.length;
        if (inheritedLength == 0) {
            return inheritedMethod;
        }
        final TypeVariableBinding[] typeVariables = currentMethod.typeVariables;
        final int length = typeVariables.length;
        if (length == 0) {
            return inheritedMethod.asRawMethod(environment);
        }
        if (length != inheritedLength) {
            return inheritedMethod;
        }
        final TypeBinding[] arguments = new TypeBinding[length];
        System.arraycopy(typeVariables, 0, arguments, 0, length);
        final ParameterizedGenericMethodBinding substitute = environment.createParameterizedGenericMethod(inheritedMethod, arguments);
        for (int i = 0; i < inheritedLength; ++i) {
            final TypeVariableBinding inheritedTypeVariable = inheritedTypeVariables[i];
            final TypeVariableBinding typeVariable = (TypeVariableBinding)arguments[i];
            if (TypeBinding.equalsEquals(typeVariable.firstBound, inheritedTypeVariable.firstBound)) {
                if (typeVariable.firstBound == null) {
                    continue;
                }
            }
            else if (typeVariable.firstBound != null && inheritedTypeVariable.firstBound != null && typeVariable.firstBound.isClass() != inheritedTypeVariable.firstBound.isClass()) {
                return inheritedMethod;
            }
            if (TypeBinding.notEquals(Scope.substitute(substitute, inheritedTypeVariable.superclass), typeVariable.superclass)) {
                return inheritedMethod;
            }
            final int interfaceLength = inheritedTypeVariable.superInterfaces.length;
            final ReferenceBinding[] interfaces = typeVariable.superInterfaces;
            if (interfaceLength != interfaces.length) {
                return inheritedMethod;
            }
            int j = 0;
        Label_0331:
            while (j < interfaceLength) {
                final TypeBinding superType = Scope.substitute(substitute, inheritedTypeVariable.superInterfaces[j]);
                for (int k = 0; k < interfaceLength; ++k) {
                    if (TypeBinding.equalsEquals(superType, interfaces[k])) {
                        ++j;
                        continue Label_0331;
                    }
                }
                return inheritedMethod;
            }
        }
        return substitute;
    }
    
    static boolean couldMethodOverride(final MethodBinding method, final MethodBinding inheritedMethod) {
        if (!CharOperation.equals(method.selector, inheritedMethod.selector)) {
            return false;
        }
        if (method == inheritedMethod || method.isStatic() || inheritedMethod.isStatic()) {
            return false;
        }
        if (inheritedMethod.isPrivate()) {
            return false;
        }
        if (inheritedMethod.isDefault() && method.declaringClass.getPackage() != inheritedMethod.declaringClass.getPackage()) {
            return false;
        }
        if (!method.isPublic()) {
            if (inheritedMethod.isPublic()) {
                return false;
            }
            if (inheritedMethod.isProtected() && !method.isProtected()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean doesMethodOverride(final MethodBinding method, final MethodBinding inheritedMethod) {
        return doesMethodOverride(method, inheritedMethod, this.environment);
    }
    
    public static boolean doesMethodOverride(final MethodBinding method, final MethodBinding inheritedMethod, final LookupEnvironment environment) {
        return couldMethodOverride(method, inheritedMethod) && areMethodsCompatible(method, inheritedMethod, environment);
    }
    
    SimpleSet findSuperinterfaceCollisions(final ReferenceBinding superclass, final ReferenceBinding[] superInterfaces) {
        return null;
    }
    
    MethodBinding findBestInheritedAbstractOrDefaultMethod(final MethodBinding[] methods, final int length) {
    Label_0096:
        for (int i = 0; i < length; ++i) {
            final MethodBinding method = methods[i];
            if (method.isAbstract() || method.isDefaultMethod()) {
                for (int j = 0; j < length; ++j) {
                    if (i != j) {
                        if (!this.checkInheritedReturnTypes(method, methods[j])) {
                            if (this.type.isInterface() && methods[j].declaringClass.id == 1) {
                                return method;
                            }
                            continue Label_0096;
                        }
                    }
                }
                return method;
            }
        }
        return null;
    }
    
    int[] findOverriddenInheritedMethods(final MethodBinding[] methods, final int length) {
        int[] toSkip = null;
        int i = 0;
        ReferenceBinding declaringClass = methods[i].declaringClass;
        if (!declaringClass.isInterface()) {
            ReferenceBinding declaringClass2;
            for (declaringClass2 = methods[++i].declaringClass; TypeBinding.equalsEquals(declaringClass, declaringClass2); declaringClass2 = methods[i].declaringClass) {
                if (++i == length) {
                    return null;
                }
            }
            if (!declaringClass2.isInterface()) {
                if (declaringClass.fPackage != declaringClass2.fPackage && methods[i].isDefault()) {
                    return null;
                }
                toSkip = new int[length];
                do {
                    toSkip[i] = -1;
                    if (++i == length) {
                        return toSkip;
                    }
                    declaringClass2 = methods[i].declaringClass;
                } while (!declaringClass2.isInterface());
            }
        }
        while (i < length) {
            if (toSkip == null || toSkip[i] != -1) {
                declaringClass = methods[i].declaringClass;
                for (int j = i + 1; j < length; ++j) {
                    if (toSkip == null || toSkip[j] != -1) {
                        final ReferenceBinding declaringClass3 = methods[j].declaringClass;
                        if (!TypeBinding.equalsEquals(declaringClass, declaringClass3)) {
                            if (declaringClass.implementsInterface(declaringClass3, true)) {
                                if (toSkip == null) {
                                    toSkip = new int[length];
                                }
                                toSkip[j] = -1;
                            }
                            else if (declaringClass3.implementsInterface(declaringClass, true)) {
                                if (toSkip == null) {
                                    toSkip = new int[length];
                                }
                                toSkip[i] = -1;
                                break;
                            }
                        }
                    }
                }
            }
            ++i;
        }
        return toSkip;
    }
    
    boolean isAsVisible(final MethodBinding newMethod, final MethodBinding inheritedMethod) {
        return inheritedMethod.modifiers == newMethod.modifiers || newMethod.isPublic() || (!inheritedMethod.isPublic() && (newMethod.isProtected() || (!inheritedMethod.isProtected() && !newMethod.isPrivate())));
    }
    
    boolean isInterfaceMethodImplemented(final MethodBinding inheritedMethod, final MethodBinding existingMethod, final ReferenceBinding superType) {
        return ImplicitNullAnnotationVerifier.areParametersEqual(existingMethod, inheritedMethod) && existingMethod.declaringClass.implementsInterface(superType, true);
    }
    
    public boolean isMethodSubsignature(final MethodBinding method, final MethodBinding inheritedMethod) {
        return CharOperation.equals(method.selector, inheritedMethod.selector) && this.isParameterSubsignature(method, inheritedMethod);
    }
    
    boolean isParameterSubsignature(final MethodBinding method, final MethodBinding inheritedMethod) {
        return isParameterSubsignature(method, inheritedMethod, this.environment);
    }
    
    static boolean isParameterSubsignature(final MethodBinding method, final MethodBinding inheritedMethod, final LookupEnvironment environment) {
        final MethodBinding substitute = computeSubstituteMethod(inheritedMethod, method, environment);
        return substitute != null && isSubstituteParameterSubsignature(method, substitute, environment);
    }
    
    boolean isSubstituteParameterSubsignature(final MethodBinding method, final MethodBinding substituteMethod) {
        return isSubstituteParameterSubsignature(method, substituteMethod, this.environment);
    }
    
    public static boolean isSubstituteParameterSubsignature(final MethodBinding method, final MethodBinding substituteMethod, final LookupEnvironment environment) {
        if (!ImplicitNullAnnotationVerifier.areParametersEqual(method, substituteMethod)) {
            if (substituteMethod.hasSubstitutedParameters() && method.areParameterErasuresEqual(substituteMethod)) {
                return method.typeVariables == Binding.NO_TYPE_VARIABLES && !hasGenericParameter(method);
            }
            return method.declaringClass.isRawType() && substituteMethod.declaringClass.isRawType() && method.hasSubstitutedParameters() && substituteMethod.hasSubstitutedParameters() && areMethodsCompatible(method, substituteMethod, environment);
        }
        else {
            if (!(substituteMethod instanceof ParameterizedGenericMethodBinding)) {
                return method.typeVariables == Binding.NO_TYPE_VARIABLES;
            }
            if (method.typeVariables != Binding.NO_TYPE_VARIABLES) {
                return !((ParameterizedGenericMethodBinding)substituteMethod).isRaw;
            }
            return !hasGenericParameter(method);
        }
    }
    
    static boolean hasGenericParameter(final MethodBinding method) {
        if (method.genericSignature() == null) {
            return false;
        }
        final TypeBinding[] params = method.parameters;
        for (int i = 0, l = params.length; i < l; ++i) {
            final TypeBinding param = params[i].leafComponentType();
            if (param instanceof ReferenceBinding) {
                final int modifiers = ((ReferenceBinding)param).modifiers;
                if ((modifiers & 0x40000000) != 0x0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    boolean isSameClassOrSubclassOf(ReferenceBinding testClass, final ReferenceBinding superclass) {
        while (!TypeBinding.equalsEquals(testClass, superclass)) {
            if ((testClass = testClass.superclass()) == null) {
                return false;
            }
        }
        return true;
    }
    
    boolean mustImplementAbstractMethod(final ReferenceBinding declaringClass) {
        if (!this.mustImplementAbstractMethods()) {
            return false;
        }
        ReferenceBinding superclass = this.type.superclass();
        if (declaringClass.isClass()) {
            while (superclass.isAbstract()) {
                if (!TypeBinding.notEquals(superclass, declaringClass)) {
                    break;
                }
                superclass = superclass.superclass();
            }
        }
        else {
            if (this.type.implementsInterface(declaringClass, false) && !superclass.implementsInterface(declaringClass, true)) {
                return true;
            }
            while (superclass.isAbstract() && !superclass.implementsInterface(declaringClass, false)) {
                superclass = superclass.superclass();
            }
        }
        return superclass.isAbstract();
    }
    
    boolean mustImplementAbstractMethods() {
        return !this.type.isInterface() && !this.type.isAbstract();
    }
    
    ProblemReporter problemReporter() {
        return this.type.scope.problemReporter();
    }
    
    ProblemReporter problemReporter(final MethodBinding currentMethod) {
        final ProblemReporter reporter = this.problemReporter();
        if (TypeBinding.equalsEquals(currentMethod.declaringClass, this.type) && currentMethod.sourceMethod() != null) {
            reporter.referenceContext = currentMethod.sourceMethod();
        }
        return reporter;
    }
    
    boolean reportIncompatibleReturnTypeError(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        this.problemReporter(currentMethod).incompatibleReturnType(currentMethod, inheritedMethod);
        return true;
    }
    
    ReferenceBinding[] resolvedExceptionTypesFor(final MethodBinding method) {
        final ReferenceBinding[] exceptions = method.thrownExceptions;
        if ((method.modifiers & 0x2000000) == 0x0) {
            return exceptions;
        }
        if (!(method.declaringClass instanceof BinaryTypeBinding)) {
            return Binding.NO_EXCEPTIONS;
        }
        int i = exceptions.length;
        while (--i >= 0) {
            exceptions[i] = (ReferenceBinding)BinaryTypeBinding.resolveType(exceptions[i], this.environment, true);
        }
        return exceptions;
    }
    
    void verify() {
        this.computeMethods();
        this.computeInheritedMethods();
        this.checkMethods();
        if (this.type.isClass()) {
            this.checkForMissingHashCodeMethod();
        }
    }
    
    void verify(final SourceTypeBinding someType) {
        if (this.type == null) {
            try {
                this.type = someType;
                this.verify();
            }
            finally {
                this.type = null;
            }
            this.type = null;
        }
        else {
            this.environment.newMethodVerifier().verify(someType);
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(10);
        buffer.append("MethodVerifier for type: ");
        buffer.append(this.type.readableName());
        buffer.append('\n');
        buffer.append("\t-inherited methods: ");
        buffer.append(this.inheritedMethods);
        return buffer.toString();
    }
}
