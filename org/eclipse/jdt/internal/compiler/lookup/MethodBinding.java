package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.List;

public class MethodBinding extends Binding
{
    public int modifiers;
    public char[] selector;
    public TypeBinding returnType;
    public TypeBinding[] parameters;
    public TypeBinding receiver;
    public ReferenceBinding[] thrownExceptions;
    public ReferenceBinding declaringClass;
    public TypeVariableBinding[] typeVariables;
    char[] signature;
    public long tagBits;
    protected AnnotationBinding[] typeAnnotations;
    public Boolean[] parameterNonNullness;
    public int defaultNullness;
    public char[][] parameterNames;
    
    protected MethodBinding() {
        this.typeVariables = Binding.NO_TYPE_VARIABLES;
        this.typeAnnotations = Binding.NO_ANNOTATIONS;
        this.parameterNames = Binding.NO_PARAMETER_NAMES;
    }
    
    public MethodBinding(final int modifiers, final char[] selector, final TypeBinding returnType, final TypeBinding[] parameters, final ReferenceBinding[] thrownExceptions, final ReferenceBinding declaringClass) {
        this.typeVariables = Binding.NO_TYPE_VARIABLES;
        this.typeAnnotations = Binding.NO_ANNOTATIONS;
        this.parameterNames = Binding.NO_PARAMETER_NAMES;
        this.modifiers = modifiers;
        this.selector = selector;
        this.returnType = returnType;
        this.parameters = ((parameters == null || parameters.length == 0) ? Binding.NO_PARAMETERS : parameters);
        this.thrownExceptions = ((thrownExceptions == null || thrownExceptions.length == 0) ? Binding.NO_EXCEPTIONS : thrownExceptions);
        this.declaringClass = declaringClass;
        if (this.declaringClass != null && this.declaringClass.isStrictfp() && !this.isNative() && !this.isAbstract()) {
            this.modifiers |= 0x800;
        }
    }
    
    public MethodBinding(final int modifiers, final TypeBinding[] parameters, final ReferenceBinding[] thrownExceptions, final ReferenceBinding declaringClass) {
        this(modifiers, TypeConstants.INIT, TypeBinding.VOID, parameters, thrownExceptions, declaringClass);
    }
    
    public MethodBinding(final MethodBinding initialMethodBinding, final ReferenceBinding declaringClass) {
        this.typeVariables = Binding.NO_TYPE_VARIABLES;
        this.typeAnnotations = Binding.NO_ANNOTATIONS;
        this.parameterNames = Binding.NO_PARAMETER_NAMES;
        this.modifiers = initialMethodBinding.modifiers;
        this.selector = initialMethodBinding.selector;
        this.returnType = initialMethodBinding.returnType;
        this.parameters = initialMethodBinding.parameters;
        this.thrownExceptions = initialMethodBinding.thrownExceptions;
        (this.declaringClass = declaringClass).storeAnnotationHolder(this, initialMethodBinding.declaringClass.retrieveAnnotationHolder(initialMethodBinding, true));
    }
    
    public final boolean areParameterErasuresEqual(final MethodBinding method) {
        final TypeBinding[] args = method.parameters;
        if (this.parameters == args) {
            return true;
        }
        final int length = this.parameters.length;
        if (length != args.length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (TypeBinding.notEquals(this.parameters[i], args[i]) && TypeBinding.notEquals(this.parameters[i].erasure(), args[i].erasure())) {
                return false;
            }
        }
        return true;
    }
    
    public final boolean areParametersCompatibleWith(final TypeBinding[] arguments) {
        final int paramLength = this.parameters.length;
        int lastIndex;
        final int argLength = lastIndex = arguments.length;
        if (this.isVarargs()) {
            lastIndex = paramLength - 1;
            if (paramLength == argLength) {
                final TypeBinding varArgType = this.parameters[lastIndex];
                final TypeBinding lastArgument = arguments[lastIndex];
                if (TypeBinding.notEquals(varArgType, lastArgument) && !lastArgument.isCompatibleWith(varArgType)) {
                    return false;
                }
            }
            else if (paramLength < argLength) {
                final TypeBinding varArgType = ((ArrayBinding)this.parameters[lastIndex]).elementsType();
                for (int i = lastIndex; i < argLength; ++i) {
                    if (TypeBinding.notEquals(varArgType, arguments[i]) && !arguments[i].isCompatibleWith(varArgType)) {
                        return false;
                    }
                }
            }
            else if (lastIndex != argLength) {
                return false;
            }
        }
        for (int j = 0; j < lastIndex; ++j) {
            if (TypeBinding.notEquals(this.parameters[j], arguments[j]) && !arguments[j].isCompatibleWith(this.parameters[j])) {
                return false;
            }
        }
        return true;
    }
    
    public final boolean areParametersEqual(final MethodBinding method) {
        final TypeBinding[] args = method.parameters;
        if (this.parameters == args) {
            return true;
        }
        final int length = this.parameters.length;
        if (length != args.length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (TypeBinding.notEquals(this.parameters[i], args[i])) {
                return false;
            }
        }
        return true;
    }
    
    public final boolean areTypeVariableErasuresEqual(final MethodBinding method) {
        final TypeVariableBinding[] vars = method.typeVariables;
        if (this.typeVariables == vars) {
            return true;
        }
        final int length = this.typeVariables.length;
        if (length != vars.length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (TypeBinding.notEquals(this.typeVariables[i], vars[i]) && TypeBinding.notEquals(this.typeVariables[i].erasure(), vars[i].erasure())) {
                return false;
            }
        }
        return true;
    }
    
    public MethodBinding asRawMethod(final LookupEnvironment env) {
        if (this.typeVariables == Binding.NO_TYPE_VARIABLES) {
            return this;
        }
        final int length = this.typeVariables.length;
        final TypeBinding[] arguments = new TypeBinding[length];
        for (int i = 0; i < length; ++i) {
            final TypeVariableBinding var = this.typeVariables[i];
            if (var.boundsCount() <= 1) {
                arguments[i] = env.convertToRawType(var.upperBound(), false);
            }
            else {
                final TypeBinding[] itsSuperinterfaces = var.superInterfaces();
                final int superLength = itsSuperinterfaces.length;
                TypeBinding rawFirstBound = null;
                TypeBinding[] rawOtherBounds = null;
                if (var.boundsCount() == superLength) {
                    rawFirstBound = env.convertToRawType(itsSuperinterfaces[0], false);
                    rawOtherBounds = new TypeBinding[superLength - 1];
                    for (int s = 1; s < superLength; ++s) {
                        rawOtherBounds[s - 1] = env.convertToRawType(itsSuperinterfaces[s], false);
                    }
                }
                else {
                    rawFirstBound = env.convertToRawType(var.superclass(), false);
                    rawOtherBounds = new TypeBinding[superLength];
                    for (int s = 0; s < superLength; ++s) {
                        rawOtherBounds[s] = env.convertToRawType(itsSuperinterfaces[s], false);
                    }
                }
                arguments[i] = env.createWildcard(null, 0, rawFirstBound, rawOtherBounds, 1);
            }
        }
        return env.createParameterizedGenericMethod(this, arguments);
    }
    
    public final boolean canBeSeenBy(final InvocationSite invocationSite, final Scope scope) {
        if (this.isPublic()) {
            return true;
        }
        final SourceTypeBinding invocationType = scope.enclosingSourceType();
        if (TypeBinding.equalsEquals(invocationType, this.declaringClass)) {
            return true;
        }
        if (this.isProtected()) {
            return invocationType.fPackage == this.declaringClass.fPackage || invocationSite.isSuperAccess();
        }
        if (this.isPrivate()) {
            ReferenceBinding outerInvocationType = invocationType;
            for (ReferenceBinding temp = outerInvocationType.enclosingType(); temp != null; temp = temp.enclosingType()) {
                outerInvocationType = temp;
            }
            ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
            for (ReferenceBinding temp = outerDeclaringClass.enclosingType(); temp != null; temp = temp.enclosingType()) {
                outerDeclaringClass = temp;
            }
            return TypeBinding.equalsEquals(outerInvocationType, outerDeclaringClass);
        }
        return invocationType.fPackage == this.declaringClass.fPackage;
    }
    
    public final boolean canBeSeenBy(final PackageBinding invocationPackage) {
        return this.isPublic() || (!this.isPrivate() && invocationPackage == this.declaringClass.getPackage());
    }
    
    public final boolean canBeSeenBy(final TypeBinding receiverType, final InvocationSite invocationSite, final Scope scope) {
        final SourceTypeBinding invocationType = scope.enclosingSourceType();
        if (this.declaringClass.isInterface() && this.isStatic()) {
            return scope.compilerOptions().sourceLevel >= 3407872L && ((invocationSite.isTypeAccess() || invocationSite.receiverIsImplicitThis()) && TypeBinding.equalsEquals(receiverType, this.declaringClass));
        }
        if (this.isPublic()) {
            return true;
        }
        if (TypeBinding.equalsEquals(invocationType, this.declaringClass) && TypeBinding.equalsEquals(invocationType, receiverType)) {
            return true;
        }
        if (invocationType == null) {
            return !this.isPrivate() && scope.getCurrentPackage() == this.declaringClass.fPackage;
        }
        if (this.isProtected()) {
            if (TypeBinding.equalsEquals(invocationType, this.declaringClass)) {
                return true;
            }
            if (invocationType.fPackage == this.declaringClass.fPackage) {
                return true;
            }
            ReferenceBinding currentType = invocationType;
            final TypeBinding receiverErasure = receiverType.erasure();
            final ReferenceBinding declaringErasure = (ReferenceBinding)this.declaringClass.erasure();
            int depth = 0;
            do {
                if (currentType.findSuperTypeOriginatingFrom(declaringErasure) != null) {
                    if (invocationSite.isSuperAccess()) {
                        return true;
                    }
                    if (receiverType instanceof ArrayBinding) {
                        return false;
                    }
                    if (this.isStatic()) {
                        if (depth > 0) {
                            invocationSite.setDepth(depth);
                        }
                        return true;
                    }
                    if (TypeBinding.equalsEquals(currentType, receiverErasure) || receiverErasure.findSuperTypeOriginatingFrom(currentType) != null) {
                        if (depth > 0) {
                            invocationSite.setDepth(depth);
                        }
                        return true;
                    }
                }
                ++depth;
                currentType = currentType.enclosingType();
            } while (currentType != null);
            return false;
        }
        else if (this.isPrivate()) {
            if (TypeBinding.notEquals(receiverType, this.declaringClass) && (scope.compilerOptions().complianceLevel > 3276800L || !receiverType.isTypeVariable() || !((TypeVariableBinding)receiverType).isErasureBoundTo(this.declaringClass.erasure()))) {
                return false;
            }
            if (TypeBinding.notEquals(invocationType, this.declaringClass)) {
                ReferenceBinding outerInvocationType = invocationType;
                for (ReferenceBinding temp = outerInvocationType.enclosingType(); temp != null; temp = temp.enclosingType()) {
                    outerInvocationType = temp;
                }
                ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
                for (ReferenceBinding temp = outerDeclaringClass.enclosingType(); temp != null; temp = temp.enclosingType()) {
                    outerDeclaringClass = temp;
                }
                if (TypeBinding.notEquals(outerInvocationType, outerDeclaringClass)) {
                    return false;
                }
            }
            return true;
        }
        else {
            final PackageBinding declaringPackage = this.declaringClass.fPackage;
            if (invocationType.fPackage != declaringPackage) {
                return false;
            }
            if (receiverType instanceof ArrayBinding) {
                return false;
            }
            final TypeBinding originalDeclaringClass = this.declaringClass.original();
            ReferenceBinding currentType2 = (ReferenceBinding)receiverType;
            do {
                if (currentType2.isCapture()) {
                    if (TypeBinding.equalsEquals(originalDeclaringClass, currentType2.erasure().original())) {
                        return true;
                    }
                }
                else if (TypeBinding.equalsEquals(originalDeclaringClass, currentType2.original())) {
                    return true;
                }
                final PackageBinding currentPackage = currentType2.fPackage;
                if (!currentType2.isCapture() && currentPackage != null && currentPackage != declaringPackage) {
                    return false;
                }
            } while ((currentType2 = currentType2.superclass()) != null);
            return false;
        }
    }
    
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & 0x80L) != 0x0L) {
            missingTypes = this.returnType.collectMissingTypes(missingTypes);
            for (int i = 0, max = this.parameters.length; i < max; ++i) {
                missingTypes = this.parameters[i].collectMissingTypes(missingTypes);
            }
            for (int i = 0, max = this.thrownExceptions.length; i < max; ++i) {
                missingTypes = this.thrownExceptions[i].collectMissingTypes(missingTypes);
            }
            for (int i = 0, max = this.typeVariables.length; i < max; ++i) {
                final TypeVariableBinding variable = this.typeVariables[i];
                missingTypes = variable.superclass().collectMissingTypes(missingTypes);
                final ReferenceBinding[] interfaces = variable.superInterfaces();
                for (int j = 0, length = interfaces.length; j < length; ++j) {
                    missingTypes = interfaces[j].collectMissingTypes(missingTypes);
                }
            }
        }
        return missingTypes;
    }
    
    MethodBinding computeSubstitutedMethod(final MethodBinding method, final LookupEnvironment env) {
        final int length = this.typeVariables.length;
        final TypeVariableBinding[] vars = method.typeVariables;
        if (length != vars.length) {
            return null;
        }
        final ParameterizedGenericMethodBinding substitute = env.createParameterizedGenericMethod(method, this.typeVariables);
        for (int i = 0; i < length; ++i) {
            if (!this.typeVariables[i].isInterchangeableWith(vars[i], substitute)) {
                return null;
            }
        }
        return substitute;
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final char[] declaringKey = this.declaringClass.computeUniqueKey(false);
        final int declaringLength = declaringKey.length;
        final int selectorLength = (this.selector == TypeConstants.INIT) ? 0 : this.selector.length;
        char[] sig = this.genericSignature();
        final boolean isGeneric = sig != null;
        if (!isGeneric) {
            sig = this.signature();
        }
        final int signatureLength = sig.length;
        final int thrownExceptionsLength = this.thrownExceptions.length;
        int thrownExceptionsSignatureLength = 0;
        char[][] thrownExceptionsSignatures = null;
        final boolean addThrownExceptions = thrownExceptionsLength > 0 && (!isGeneric || CharOperation.lastIndexOf('^', sig) < 0);
        if (addThrownExceptions) {
            thrownExceptionsSignatures = new char[thrownExceptionsLength][];
            for (int i = 0; i < thrownExceptionsLength; ++i) {
                if (this.thrownExceptions[i] != null) {
                    thrownExceptionsSignatures[i] = this.thrownExceptions[i].signature();
                    thrownExceptionsSignatureLength += thrownExceptionsSignatures[i].length + 1;
                }
            }
        }
        final char[] uniqueKey = new char[declaringLength + 1 + selectorLength + signatureLength + thrownExceptionsSignatureLength];
        int index = 0;
        System.arraycopy(declaringKey, 0, uniqueKey, index, declaringLength);
        index = declaringLength;
        uniqueKey[index++] = '.';
        System.arraycopy(this.selector, 0, uniqueKey, index, selectorLength);
        index += selectorLength;
        System.arraycopy(sig, 0, uniqueKey, index, signatureLength);
        if (thrownExceptionsSignatureLength > 0) {
            index += signatureLength;
            for (final char[] thrownExceptionSignature : thrownExceptionsSignatures) {
                if (thrownExceptionSignature != null) {
                    uniqueKey[index++] = '|';
                    final int length = thrownExceptionSignature.length;
                    System.arraycopy(thrownExceptionSignature, 0, uniqueKey, index, length);
                    index += length;
                }
            }
        }
        return uniqueKey;
    }
    
    public final char[] constantPoolName() {
        return this.selector;
    }
    
    protected void fillInDefaultNonNullness(final AbstractMethodDeclaration sourceMethod) {
        if (this.parameterNonNullness == null) {
            this.parameterNonNullness = new Boolean[this.parameters.length];
        }
        boolean added = false;
        for (int length = this.parameterNonNullness.length, i = 0; i < length; ++i) {
            if (!this.parameters[i].isBaseType()) {
                if (this.parameterNonNullness[i] == null) {
                    added = true;
                    this.parameterNonNullness[i] = Boolean.TRUE;
                    if (sourceMethod != null) {
                        final LocalVariableBinding binding = sourceMethod.arguments[i].binding;
                        binding.tagBits |= 0x100000000000000L;
                    }
                }
                else if (sourceMethod != null && this.parameterNonNullness[i]) {
                    sourceMethod.scope.problemReporter().nullAnnotationIsRedundant(sourceMethod, i);
                }
            }
        }
        if (added) {
            this.tagBits |= 0x400L;
        }
        if (this.returnType != null && !this.returnType.isBaseType() && (this.tagBits & 0x180000000000000L) == 0x0L) {
            this.tagBits |= 0x100000000000000L;
        }
        else if (sourceMethod != null && (this.tagBits & 0x100000000000000L) != 0x0L) {
            sourceMethod.scope.problemReporter().nullAnnotationIsRedundant(sourceMethod, -1);
        }
    }
    
    protected void fillInDefaultNonNullness18(final AbstractMethodDeclaration sourceMethod, final LookupEnvironment env) {
        if (this.hasNonNullDefaultFor(8, true)) {
            boolean added = false;
            for (int length = this.parameters.length, i = 0; i < length; ++i) {
                final TypeBinding parameter = this.parameters[i];
                if (parameter.acceptsNonNullDefault()) {
                    final long existing = parameter.tagBits & 0x180000000000000L;
                    if (existing == 0L) {
                        added = true;
                        if (!parameter.isBaseType()) {
                            this.parameters[i] = env.createAnnotatedType(parameter, new AnnotationBinding[] { env.getNonNullAnnotation() });
                            if (sourceMethod != null) {
                                sourceMethod.arguments[i].binding.type = this.parameters[i];
                            }
                        }
                    }
                    else if (sourceMethod != null && (parameter.tagBits & 0x100000000000000L) != 0x0L && sourceMethod.arguments[i].hasNullTypeAnnotation(TypeReference.AnnotationPosition.MAIN_TYPE)) {
                        sourceMethod.scope.problemReporter().nullAnnotationIsRedundant(sourceMethod, i);
                    }
                }
            }
            if (added) {
                this.tagBits |= 0x400L;
            }
        }
        if (this.returnType != null && this.hasNonNullDefaultFor(16, true) && this.returnType.acceptsNonNullDefault()) {
            if ((this.returnType.tagBits & 0x180000000000000L) == 0x0L) {
                this.returnType = env.createAnnotatedType(this.returnType, new AnnotationBinding[] { env.getNonNullAnnotation() });
            }
            else if (sourceMethod instanceof MethodDeclaration && (this.returnType.tagBits & 0x100000000000000L) != 0x0L && ((MethodDeclaration)sourceMethod).hasNullTypeAnnotation(TypeReference.AnnotationPosition.MAIN_TYPE)) {
                sourceMethod.scope.problemReporter().nullAnnotationIsRedundant(sourceMethod, -1);
            }
        }
    }
    
    public MethodBinding findOriginalInheritedMethod(final MethodBinding inheritedMethod) {
        final MethodBinding inheritedOriginal = inheritedMethod.original();
        final TypeBinding superType = this.declaringClass.findSuperTypeOriginatingFrom(inheritedOriginal.declaringClass);
        if (superType == null || !(superType instanceof ReferenceBinding)) {
            return null;
        }
        if (TypeBinding.notEquals(inheritedOriginal.declaringClass, superType)) {
            final MethodBinding[] superMethods = ((ReferenceBinding)superType).getMethods(inheritedOriginal.selector, inheritedOriginal.parameters.length);
            for (int m = 0, l = superMethods.length; m < l; ++m) {
                if (superMethods[m].original() == inheritedOriginal) {
                    return superMethods[m];
                }
            }
        }
        return inheritedOriginal;
    }
    
    public char[] genericSignature() {
        if ((this.modifiers & 0x40000000) == 0x0) {
            return null;
        }
        final StringBuffer sig = new StringBuffer(10);
        if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            sig.append('<');
            for (int i = 0, length = this.typeVariables.length; i < length; ++i) {
                sig.append(this.typeVariables[i].genericSignature());
            }
            sig.append('>');
        }
        sig.append('(');
        for (int i = 0, length = this.parameters.length; i < length; ++i) {
            sig.append(this.parameters[i].genericTypeSignature());
        }
        sig.append(')');
        if (this.returnType != null) {
            sig.append(this.returnType.genericTypeSignature());
        }
        boolean needExceptionSignatures = false;
        final int length = this.thrownExceptions.length;
        for (int j = 0; j < length; ++j) {
            if ((this.thrownExceptions[j].modifiers & 0x40000000) != 0x0) {
                needExceptionSignatures = true;
                break;
            }
        }
        if (needExceptionSignatures) {
            for (int j = 0; j < length; ++j) {
                sig.append('^');
                sig.append(this.thrownExceptions[j].genericTypeSignature());
            }
        }
        final int sigLength = sig.length();
        final char[] genericSignature = new char[sigLength];
        sig.getChars(0, sigLength, genericSignature, 0);
        return genericSignature;
    }
    
    public final int getAccessFlags() {
        return this.modifiers & 0x1FFFF;
    }
    
    @Override
    public AnnotationBinding[] getAnnotations() {
        final MethodBinding originalMethod = this.original();
        return originalMethod.declaringClass.retrieveAnnotations(originalMethod);
    }
    
    @Override
    public long getAnnotationTagBits() {
        final MethodBinding originalMethod = this.original();
        if ((originalMethod.tagBits & 0x200000000L) == 0x0L && originalMethod.declaringClass instanceof SourceTypeBinding) {
            final ClassScope scope = ((SourceTypeBinding)originalMethod.declaringClass).scope;
            if (scope != null) {
                final TypeDeclaration typeDecl = scope.referenceContext;
                final AbstractMethodDeclaration methodDecl = typeDecl.declarationOf(originalMethod);
                if (methodDecl != null) {
                    ASTNode.resolveAnnotations(methodDecl.scope, methodDecl.annotations, originalMethod);
                }
                final CompilerOptions options = scope.compilerOptions();
                if (options.isAnnotationBasedNullAnalysisEnabled) {
                    final boolean usesNullTypeAnnotations = scope.environment().usesNullTypeAnnotations();
                    final long nullDefaultBits = usesNullTypeAnnotations ? this.defaultNullness : (this.tagBits & 0x600000000000000L);
                    if (nullDefaultBits != 0L && this.declaringClass instanceof SourceTypeBinding) {
                        final SourceTypeBinding declaringSourceType = (SourceTypeBinding)this.declaringClass;
                        if (declaringSourceType.checkRedundantNullnessDefaultOne(methodDecl, methodDecl.annotations, nullDefaultBits, usesNullTypeAnnotations)) {
                            declaringSourceType.checkRedundantNullnessDefaultRecurse(methodDecl, methodDecl.annotations, nullDefaultBits, usesNullTypeAnnotations);
                        }
                    }
                }
            }
        }
        return originalMethod.tagBits;
    }
    
    public Object getDefaultValue() {
        final MethodBinding originalMethod = this.original();
        if ((originalMethod.tagBits & 0x800000000000000L) == 0x0L) {
            if (originalMethod.declaringClass instanceof SourceTypeBinding) {
                final SourceTypeBinding sourceType = (SourceTypeBinding)originalMethod.declaringClass;
                if (sourceType.scope != null) {
                    final AbstractMethodDeclaration methodDeclaration = originalMethod.sourceMethod();
                    if (methodDeclaration != null && methodDeclaration.isAnnotationMethod()) {
                        methodDeclaration.resolve(sourceType.scope);
                    }
                }
            }
            final MethodBinding methodBinding = originalMethod;
            methodBinding.tagBits |= 0x800000000000000L;
        }
        final AnnotationHolder holder = originalMethod.declaringClass.retrieveAnnotationHolder(originalMethod, true);
        return (holder == null) ? null : holder.getDefaultValue();
    }
    
    public AnnotationBinding[][] getParameterAnnotations() {
        final int length;
        if ((length = this.parameters.length) == 0) {
            return null;
        }
        final MethodBinding originalMethod = this.original();
        final AnnotationHolder holder = originalMethod.declaringClass.retrieveAnnotationHolder(originalMethod, true);
        AnnotationBinding[][] allParameterAnnotations = (AnnotationBinding[][])((holder == null) ? null : holder.getParameterAnnotations());
        if (allParameterAnnotations == null && (this.tagBits & 0x400L) != 0x0L) {
            allParameterAnnotations = new AnnotationBinding[length][];
            if (this.declaringClass instanceof SourceTypeBinding) {
                final SourceTypeBinding sourceType = (SourceTypeBinding)this.declaringClass;
                if (sourceType.scope != null) {
                    final AbstractMethodDeclaration methodDecl = sourceType.scope.referenceType().declarationOf(originalMethod);
                    for (int i = 0; i < length; ++i) {
                        final Argument argument = methodDecl.arguments[i];
                        if (argument.annotations != null) {
                            ASTNode.resolveAnnotations(methodDecl.scope, argument.annotations, argument.binding);
                            allParameterAnnotations[i] = argument.binding.getAnnotations();
                        }
                        else {
                            allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
                        }
                    }
                }
                else {
                    for (int j = 0; j < length; ++j) {
                        allParameterAnnotations[j] = Binding.NO_ANNOTATIONS;
                    }
                }
            }
            else {
                for (int k = 0; k < length; ++k) {
                    allParameterAnnotations[k] = Binding.NO_ANNOTATIONS;
                }
            }
            this.setParameterAnnotations(allParameterAnnotations);
        }
        return allParameterAnnotations;
    }
    
    public TypeVariableBinding getTypeVariable(final char[] variableName) {
        int i = this.typeVariables.length;
        while (--i >= 0) {
            if (CharOperation.equals(this.typeVariables[i].sourceName, variableName)) {
                return this.typeVariables[i];
            }
        }
        return null;
    }
    
    public TypeVariableBinding[] getAllTypeVariables(final boolean isDiamond) {
        TypeVariableBinding[] allTypeVariables = this.typeVariables;
        if (isDiamond) {
            final TypeVariableBinding[] classTypeVariables = this.declaringClass.typeVariables();
            final int l1 = allTypeVariables.length;
            final int l2 = classTypeVariables.length;
            if (l1 == 0) {
                allTypeVariables = classTypeVariables;
            }
            else if (l2 != 0) {
                System.arraycopy(allTypeVariables, 0, allTypeVariables = new TypeVariableBinding[l1 + l2], 0, l1);
                System.arraycopy(classTypeVariables, 0, allTypeVariables, l1, l2);
            }
        }
        return allTypeVariables;
    }
    
    public boolean hasSubstitutedParameters() {
        return false;
    }
    
    public boolean hasSubstitutedReturnType() {
        return false;
    }
    
    public final boolean isAbstract() {
        return (this.modifiers & 0x400) != 0x0;
    }
    
    public final boolean isBridge() {
        return (this.modifiers & 0x40) != 0x0;
    }
    
    public final boolean isConstructor() {
        return this.selector == TypeConstants.INIT;
    }
    
    public final boolean isDefault() {
        return !this.isPublic() && !this.isProtected() && !this.isPrivate();
    }
    
    public final boolean isDefaultAbstract() {
        return (this.modifiers & 0x80000) != 0x0;
    }
    
    public boolean isDefaultMethod() {
        return (this.modifiers & 0x10000) != 0x0;
    }
    
    public final boolean isDeprecated() {
        return (this.modifiers & 0x100000) != 0x0;
    }
    
    public final boolean isFinal() {
        return (this.modifiers & 0x10) != 0x0;
    }
    
    public final boolean isImplementing() {
        return (this.modifiers & 0x20000000) != 0x0;
    }
    
    public final boolean isMain() {
        if (this.selector.length == 4 && CharOperation.equals(this.selector, TypeConstants.MAIN) && (this.modifiers & 0x9) != 0x0 && TypeBinding.VOID == this.returnType && this.parameters.length == 1) {
            final TypeBinding paramType = this.parameters[0];
            if (paramType.dimensions() == 1 && paramType.leafComponentType().id == 11) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean isNative() {
        return (this.modifiers & 0x100) != 0x0;
    }
    
    public final boolean isOverriding() {
        return (this.modifiers & 0x10000000) != 0x0;
    }
    
    public final boolean isPrivate() {
        return (this.modifiers & 0x2) != 0x0;
    }
    
    public final boolean isOrEnclosedByPrivateType() {
        return (this.modifiers & 0x2) != 0x0 || (this.declaringClass != null && this.declaringClass.isOrEnclosedByPrivateType());
    }
    
    public final boolean isProtected() {
        return (this.modifiers & 0x4) != 0x0;
    }
    
    public final boolean isPublic() {
        return (this.modifiers & 0x1) != 0x0;
    }
    
    public final boolean isStatic() {
        return (this.modifiers & 0x8) != 0x0;
    }
    
    public final boolean isStrictfp() {
        return (this.modifiers & 0x800) != 0x0;
    }
    
    public final boolean isSynchronized() {
        return (this.modifiers & 0x20) != 0x0;
    }
    
    public final boolean isSynthetic() {
        return (this.modifiers & 0x1000) != 0x0;
    }
    
    public final boolean isUsed() {
        return (this.modifiers & 0x8000000) != 0x0;
    }
    
    public boolean isVarargs() {
        return (this.modifiers & 0x80) != 0x0;
    }
    
    public boolean isParameterizedGeneric() {
        return false;
    }
    
    public boolean isPolymorphic() {
        return false;
    }
    
    public final boolean isViewedAsDeprecated() {
        return (this.modifiers & 0x300000) != 0x0;
    }
    
    @Override
    public final int kind() {
        return 8;
    }
    
    public MethodBinding original() {
        return this;
    }
    
    public MethodBinding shallowOriginal() {
        return this.original();
    }
    
    public MethodBinding genericMethod() {
        return this;
    }
    
    @Override
    public char[] readableName() {
        final StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
        if (this.isConstructor()) {
            buffer.append(this.declaringClass.sourceName());
        }
        else {
            buffer.append(this.selector);
        }
        buffer.append('(');
        if (this.parameters != Binding.NO_PARAMETERS) {
            for (int i = 0, length = this.parameters.length; i < length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(this.parameters[i].sourceName());
            }
        }
        buffer.append(')');
        return buffer.toString().toCharArray();
    }
    
    public final AnnotationBinding[] getTypeAnnotations() {
        return this.typeAnnotations;
    }
    
    public void setTypeAnnotations(final AnnotationBinding[] annotations) {
        this.typeAnnotations = annotations;
    }
    
    @Override
    public void setAnnotations(final AnnotationBinding[] annotations) {
        this.declaringClass.storeAnnotations(this, annotations);
    }
    
    public void setAnnotations(final AnnotationBinding[] annotations, final AnnotationBinding[][] parameterAnnotations, final Object defaultValue, final LookupEnvironment optionalEnv) {
        this.declaringClass.storeAnnotationHolder(this, AnnotationHolder.storeAnnotations(annotations, parameterAnnotations, defaultValue, optionalEnv));
    }
    
    public void setDefaultValue(final Object defaultValue) {
        final MethodBinding original;
        final MethodBinding originalMethod = original = this.original();
        original.tagBits |= 0x800000000000000L;
        final AnnotationHolder holder = this.declaringClass.retrieveAnnotationHolder(this, false);
        if (holder == null) {
            this.setAnnotations(null, null, defaultValue, null);
        }
        else {
            this.setAnnotations(holder.getAnnotations(), holder.getParameterAnnotations(), defaultValue, null);
        }
    }
    
    public void setParameterAnnotations(final AnnotationBinding[][] parameterAnnotations) {
        final AnnotationHolder holder = this.declaringClass.retrieveAnnotationHolder(this, false);
        if (holder == null) {
            this.setAnnotations(null, parameterAnnotations, null, null);
        }
        else {
            this.setAnnotations(holder.getAnnotations(), parameterAnnotations, holder.getDefaultValue(), null);
        }
    }
    
    protected final void setSelector(final char[] selector) {
        this.selector = selector;
        this.signature = null;
    }
    
    @Override
    public char[] shortReadableName() {
        final StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
        if (this.isConstructor()) {
            buffer.append(this.declaringClass.shortReadableName());
        }
        else {
            buffer.append(this.selector);
        }
        buffer.append('(');
        if (this.parameters != Binding.NO_PARAMETERS) {
            for (int i = 0, length = this.parameters.length; i < length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(this.parameters[i].shortReadableName());
            }
        }
        buffer.append(')');
        final int nameLength = buffer.length();
        final char[] shortReadableName = new char[nameLength];
        buffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }
    
    public final char[] signature() {
        if (this.signature != null) {
            return this.signature;
        }
        final StringBuffer buffer = new StringBuffer(this.parameters.length + 20);
        buffer.append('(');
        TypeBinding[] targetParameters = this.parameters;
        final boolean isConstructor = this.isConstructor();
        if (isConstructor && this.declaringClass.isEnum()) {
            buffer.append(ConstantPool.JavaLangStringSignature);
            buffer.append(TypeBinding.INT.signature());
        }
        final boolean needSynthetics = isConstructor && this.declaringClass.isNestedType();
        if (needSynthetics) {
            final ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
            if (syntheticArgumentTypes != null) {
                for (int i = 0, count = syntheticArgumentTypes.length; i < count; ++i) {
                    buffer.append(syntheticArgumentTypes[i].signature());
                }
            }
            if (this instanceof SyntheticMethodBinding) {
                targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
            }
        }
        if (targetParameters != Binding.NO_PARAMETERS) {
            for (int j = 0; j < targetParameters.length; ++j) {
                buffer.append(targetParameters[j].signature());
            }
        }
        if (needSynthetics) {
            final SyntheticArgumentBinding[] syntheticOuterArguments = this.declaringClass.syntheticOuterLocalVariables();
            for (int count2 = (syntheticOuterArguments == null) ? 0 : syntheticOuterArguments.length, k = 0; k < count2; ++k) {
                buffer.append(syntheticOuterArguments[k].type.signature());
            }
            for (int k = targetParameters.length, extraLength = this.parameters.length; k < extraLength; ++k) {
                buffer.append(this.parameters[k].signature());
            }
        }
        buffer.append(')');
        if (this.returnType != null) {
            buffer.append(this.returnType.signature());
        }
        final int nameLength = buffer.length();
        buffer.getChars(0, nameLength, this.signature = new char[nameLength], 0);
        return this.signature;
    }
    
    public final char[] signature(final ClassFile classFile) {
        if (this.signature != null) {
            if ((this.tagBits & 0x800L) != 0x0L) {
                final boolean isConstructor = this.isConstructor();
                TypeBinding[] targetParameters = this.parameters;
                final boolean needSynthetics = isConstructor && this.declaringClass.isNestedType();
                if (needSynthetics) {
                    final ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
                    if (syntheticArgumentTypes != null) {
                        for (int i = 0, count = syntheticArgumentTypes.length; i < count; ++i) {
                            final ReferenceBinding syntheticArgumentType = syntheticArgumentTypes[i];
                            if ((syntheticArgumentType.tagBits & 0x800L) != 0x0L) {
                                Util.recordNestedType(classFile, syntheticArgumentType);
                            }
                        }
                    }
                    if (this instanceof SyntheticMethodBinding) {
                        targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
                    }
                }
                if (targetParameters != Binding.NO_PARAMETERS) {
                    for (int j = 0, max = targetParameters.length; j < max; ++j) {
                        final TypeBinding targetParameter = targetParameters[j];
                        final TypeBinding leafTargetParameterType = targetParameter.leafComponentType();
                        if ((leafTargetParameterType.tagBits & 0x800L) != 0x0L) {
                            Util.recordNestedType(classFile, leafTargetParameterType);
                        }
                    }
                }
                if (needSynthetics) {
                    for (int j = targetParameters.length, extraLength = this.parameters.length; j < extraLength; ++j) {
                        final TypeBinding parameter = this.parameters[j];
                        final TypeBinding leafParameterType = parameter.leafComponentType();
                        if ((leafParameterType.tagBits & 0x800L) != 0x0L) {
                            Util.recordNestedType(classFile, leafParameterType);
                        }
                    }
                }
                if (this.returnType != null) {
                    final TypeBinding ret = this.returnType.leafComponentType();
                    if ((ret.tagBits & 0x800L) != 0x0L) {
                        Util.recordNestedType(classFile, ret);
                    }
                }
            }
            return this.signature;
        }
        final StringBuffer buffer = new StringBuffer((this.parameters.length + 1) * 20);
        buffer.append('(');
        TypeBinding[] targetParameters = this.parameters;
        final boolean isConstructor2 = this.isConstructor();
        if (isConstructor2 && this.declaringClass.isEnum()) {
            buffer.append(ConstantPool.JavaLangStringSignature);
            buffer.append(TypeBinding.INT.signature());
        }
        final boolean needSynthetics2 = isConstructor2 && this.declaringClass.isNestedType();
        if (needSynthetics2) {
            final ReferenceBinding[] syntheticArgumentTypes2 = this.declaringClass.syntheticEnclosingInstanceTypes();
            if (syntheticArgumentTypes2 != null) {
                for (int k = 0, count2 = syntheticArgumentTypes2.length; k < count2; ++k) {
                    final ReferenceBinding syntheticArgumentType2 = syntheticArgumentTypes2[k];
                    if ((syntheticArgumentType2.tagBits & 0x800L) != 0x0L) {
                        this.tagBits |= 0x800L;
                        Util.recordNestedType(classFile, syntheticArgumentType2);
                    }
                    buffer.append(syntheticArgumentType2.signature());
                }
            }
            if (this instanceof SyntheticMethodBinding) {
                targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
            }
        }
        if (targetParameters != Binding.NO_PARAMETERS) {
            for (int i = 0, max2 = targetParameters.length; i < max2; ++i) {
                final TypeBinding targetParameter2 = targetParameters[i];
                final TypeBinding leafTargetParameterType2 = targetParameter2.leafComponentType();
                if ((leafTargetParameterType2.tagBits & 0x800L) != 0x0L) {
                    this.tagBits |= 0x800L;
                    Util.recordNestedType(classFile, leafTargetParameterType2);
                }
                buffer.append(targetParameter2.signature());
            }
        }
        if (needSynthetics2) {
            final SyntheticArgumentBinding[] syntheticOuterArguments = this.declaringClass.syntheticOuterLocalVariables();
            for (int count = (syntheticOuterArguments == null) ? 0 : syntheticOuterArguments.length, l = 0; l < count; ++l) {
                buffer.append(syntheticOuterArguments[l].type.signature());
            }
            for (int l = targetParameters.length, extraLength2 = this.parameters.length; l < extraLength2; ++l) {
                final TypeBinding parameter2 = this.parameters[l];
                final TypeBinding leafParameterType2 = parameter2.leafComponentType();
                if ((leafParameterType2.tagBits & 0x800L) != 0x0L) {
                    this.tagBits |= 0x800L;
                    Util.recordNestedType(classFile, leafParameterType2);
                }
                buffer.append(parameter2.signature());
            }
        }
        buffer.append(')');
        if (this.returnType != null) {
            final TypeBinding ret2 = this.returnType.leafComponentType();
            if ((ret2.tagBits & 0x800L) != 0x0L) {
                this.tagBits |= 0x800L;
                Util.recordNestedType(classFile, ret2);
            }
            buffer.append(this.returnType.signature());
        }
        final int nameLength = buffer.length();
        buffer.getChars(0, nameLength, this.signature = new char[nameLength], 0);
        return this.signature;
    }
    
    public final int sourceEnd() {
        final AbstractMethodDeclaration method = this.sourceMethod();
        if (method != null) {
            return method.sourceEnd;
        }
        if (this.declaringClass instanceof SourceTypeBinding) {
            return ((SourceTypeBinding)this.declaringClass).sourceEnd();
        }
        return 0;
    }
    
    public AbstractMethodDeclaration sourceMethod() {
        if (this.isSynthetic()) {
            return null;
        }
        SourceTypeBinding sourceType;
        try {
            sourceType = (SourceTypeBinding)this.declaringClass;
        }
        catch (final ClassCastException ex) {
            return null;
        }
        final AbstractMethodDeclaration[] methods = (AbstractMethodDeclaration[])((sourceType.scope != null) ? sourceType.scope.referenceContext.methods : null);
        if (methods != null) {
            int i = methods.length;
            while (--i >= 0) {
                if (this == methods[i].binding) {
                    return methods[i];
                }
            }
        }
        return null;
    }
    
    public LambdaExpression sourceLambda() {
        return null;
    }
    
    public final int sourceStart() {
        final AbstractMethodDeclaration method = this.sourceMethod();
        if (method != null) {
            return method.sourceStart;
        }
        if (this.declaringClass instanceof SourceTypeBinding) {
            return ((SourceTypeBinding)this.declaringClass).sourceStart();
        }
        return 0;
    }
    
    public MethodBinding tiebreakMethod() {
        return this;
    }
    
    @Override
    public String toString() {
        final StringBuffer output = new StringBuffer(10);
        if ((this.modifiers & 0x2000000) != 0x0) {
            output.append("[unresolved] ");
        }
        ASTNode.printModifiers(this.modifiers, output);
        output.append((this.returnType != null) ? this.returnType.debugName() : "<no type>");
        output.append(" ");
        output.append((this.selector != null) ? new String(this.selector) : "<no selector>");
        output.append("(");
        if (this.parameters != null) {
            if (this.parameters != Binding.NO_PARAMETERS) {
                for (int i = 0, length = this.parameters.length; i < length; ++i) {
                    if (i > 0) {
                        output.append(", ");
                    }
                    output.append((this.parameters[i] != null) ? this.parameters[i].debugName() : "<no argument type>");
                }
            }
        }
        else {
            output.append("<no argument types>");
        }
        output.append(") ");
        if (this.thrownExceptions != null) {
            if (this.thrownExceptions != Binding.NO_EXCEPTIONS) {
                output.append("throws ");
                for (int i = 0, length = this.thrownExceptions.length; i < length; ++i) {
                    if (i > 0) {
                        output.append(", ");
                    }
                    output.append((this.thrownExceptions[i] != null) ? this.thrownExceptions[i].debugName() : "<no exception type>");
                }
            }
        }
        else {
            output.append("<no exception types>");
        }
        return output.toString();
    }
    
    public TypeVariableBinding[] typeVariables() {
        return this.typeVariables;
    }
    
    public boolean hasNonNullDefaultFor(final int location, final boolean useTypeAnnotations) {
        if ((this.modifiers & 0x4000000) != 0x0) {
            return false;
        }
        if (useTypeAnnotations) {
            if (this.defaultNullness != 0) {
                return (this.defaultNullness & location) != 0x0;
            }
        }
        else {
            if ((this.tagBits & 0x200000000000000L) != 0x0L) {
                return true;
            }
            if ((this.tagBits & 0x400000000000000L) != 0x0L) {
                return false;
            }
        }
        return this.declaringClass.hasNonNullDefaultFor(location, useTypeAnnotations);
    }
    
    public boolean redeclaresPublicObjectMethod(final Scope scope) {
        final ReferenceBinding javaLangObject = scope.getJavaLangObject();
        final MethodBinding[] methods = javaLangObject.getMethods(this.selector);
        for (int i = 0, length = (methods == null) ? 0 : methods.length; i < length; ++i) {
            final MethodBinding method = methods[i];
            if (method.isPublic() && !method.isStatic()) {
                if (method.parameters.length == this.parameters.length) {
                    if (MethodVerifier.doesMethodOverride(this, method, scope.environment())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isVoidMethod() {
        return this.returnType == TypeBinding.VOID;
    }
}
