package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public class FieldBinding extends VariableBinding
{
    public ReferenceBinding declaringClass;
    public int compoundUseFlag;
    
    protected FieldBinding() {
        super(null, null, 0, null);
        this.compoundUseFlag = 0;
    }
    
    public FieldBinding(final char[] name, final TypeBinding type, final int modifiers, final ReferenceBinding declaringClass, final Constant constant) {
        super(name, type, modifiers, constant);
        this.compoundUseFlag = 0;
        this.declaringClass = declaringClass;
    }
    
    public FieldBinding(final FieldBinding initialFieldBinding, final ReferenceBinding declaringClass) {
        super(initialFieldBinding.name, initialFieldBinding.type, initialFieldBinding.modifiers, initialFieldBinding.constant());
        this.compoundUseFlag = 0;
        this.declaringClass = declaringClass;
        this.id = initialFieldBinding.id;
        this.setAnnotations(initialFieldBinding.getAnnotations());
    }
    
    public FieldBinding(final FieldDeclaration field, final TypeBinding type, final int modifiers, final ReferenceBinding declaringClass) {
        this(field.name, type, modifiers, declaringClass, null);
        field.binding = this;
    }
    
    public final boolean canBeSeenBy(final PackageBinding invocationPackage) {
        return this.isPublic() || (!this.isPrivate() && invocationPackage == this.declaringClass.getPackage());
    }
    
    public final boolean canBeSeenBy(final TypeBinding receiverType, final InvocationSite invocationSite, final Scope scope) {
        if (this.isPublic()) {
            return true;
        }
        final SourceTypeBinding invocationType = scope.enclosingSourceType();
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
            int depth = 0;
            final ReferenceBinding receiverErasure = (ReferenceBinding)receiverType.erasure();
            final ReferenceBinding declaringErasure = (ReferenceBinding)this.declaringClass.erasure();
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
                if (currentPackage != null && currentPackage != declaringPackage) {
                    return false;
                }
            } while ((currentType2 = currentType2.superclass()) != null);
            return false;
        }
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final char[] declaringKey = (this.declaringClass == null) ? CharOperation.NO_CHAR : this.declaringClass.computeUniqueKey(false);
        final int declaringLength = declaringKey.length;
        final int nameLength = this.name.length;
        final char[] returnTypeKey = (this.type == null) ? new char[] { 'V' } : this.type.computeUniqueKey(false);
        final int returnTypeLength = returnTypeKey.length;
        final char[] uniqueKey = new char[declaringLength + 1 + nameLength + 1 + returnTypeLength];
        int index = 0;
        System.arraycopy(declaringKey, 0, uniqueKey, index, declaringLength);
        index += declaringLength;
        uniqueKey[index++] = '.';
        System.arraycopy(this.name, 0, uniqueKey, index, nameLength);
        index += nameLength;
        uniqueKey[index++] = ')';
        System.arraycopy(returnTypeKey, 0, uniqueKey, index, returnTypeLength);
        return uniqueKey;
    }
    
    @Override
    public Constant constant() {
        Constant fieldConstant = this.constant;
        if (fieldConstant == null) {
            if (this.isFinal()) {
                final FieldBinding originalField = this.original();
                if (originalField.declaringClass instanceof SourceTypeBinding) {
                    final SourceTypeBinding sourceType = (SourceTypeBinding)originalField.declaringClass;
                    if (sourceType.scope != null) {
                        final TypeDeclaration typeDecl = sourceType.scope.referenceContext;
                        final FieldDeclaration fieldDecl = typeDecl.declarationOf(originalField);
                        final MethodScope initScope = originalField.isStatic() ? typeDecl.staticInitializerScope : typeDecl.initializerScope;
                        final boolean old = initScope.insideTypeAnnotation;
                        try {
                            initScope.insideTypeAnnotation = false;
                            fieldDecl.resolve(initScope);
                        }
                        finally {
                            initScope.insideTypeAnnotation = old;
                        }
                        initScope.insideTypeAnnotation = old;
                        fieldConstant = ((originalField.constant == null) ? Constant.NotAConstant : originalField.constant);
                    }
                    else {
                        fieldConstant = Constant.NotAConstant;
                    }
                }
                else {
                    fieldConstant = Constant.NotAConstant;
                }
            }
            else {
                fieldConstant = Constant.NotAConstant;
            }
            this.constant = fieldConstant;
        }
        return fieldConstant;
    }
    
    @Override
    public Constant constant(final Scope scope) {
        if (this.constant != null) {
            return this.constant;
        }
        final ProblemReporter problemReporter = scope.problemReporter();
        final IErrorHandlingPolicy suspendedPolicy = problemReporter.suspendTempErrorHandlingPolicy();
        try {
            return this.constant();
        }
        finally {
            problemReporter.resumeTempErrorHandlingPolicy(suspendedPolicy);
        }
    }
    
    public void fillInDefaultNonNullness(final FieldDeclaration sourceField, final Scope scope) {
        if (this.type == null || this.type.isBaseType()) {
            return;
        }
        final LookupEnvironment environment = scope.environment();
        if (environment.usesNullTypeAnnotations()) {
            if (!this.type.acceptsNonNullDefault()) {
                return;
            }
            if ((this.type.tagBits & 0x180000000000000L) == 0x0L) {
                this.type = environment.createAnnotatedType(this.type, new AnnotationBinding[] { environment.getNonNullAnnotation() });
            }
            else if ((this.type.tagBits & 0x100000000000000L) != 0x0L) {
                scope.problemReporter().nullAnnotationIsRedundant(sourceField);
            }
        }
        else if ((this.tagBits & 0x180000000000000L) == 0x0L) {
            this.tagBits |= 0x100000000000000L;
        }
        else if ((this.tagBits & 0x100000000000000L) != 0x0L) {
            scope.problemReporter().nullAnnotationIsRedundant(sourceField);
        }
    }
    
    public char[] genericSignature() {
        if ((this.modifiers & 0x40000000) == 0x0) {
            return null;
        }
        return this.type.genericTypeSignature();
    }
    
    public final int getAccessFlags() {
        return this.modifiers & 0xFFFF;
    }
    
    @Override
    public AnnotationBinding[] getAnnotations() {
        final FieldBinding originalField = this.original();
        final ReferenceBinding declaringClassBinding = originalField.declaringClass;
        if (declaringClassBinding == null) {
            return Binding.NO_ANNOTATIONS;
        }
        return declaringClassBinding.retrieveAnnotations(originalField);
    }
    
    @Override
    public long getAnnotationTagBits() {
        final FieldBinding originalField = this.original();
        if ((originalField.tagBits & 0x200000000L) == 0x0L && originalField.declaringClass instanceof SourceTypeBinding) {
            final ClassScope scope = ((SourceTypeBinding)originalField.declaringClass).scope;
            if (scope == null) {
                this.tagBits |= 0x600000000L;
                return 0L;
            }
            final TypeDeclaration typeDecl = scope.referenceContext;
            final FieldDeclaration fieldDecl = typeDecl.declarationOf(originalField);
            if (fieldDecl != null) {
                final MethodScope initializationScope = this.isStatic() ? typeDecl.staticInitializerScope : typeDecl.initializerScope;
                final FieldBinding previousField = initializationScope.initializedField;
                final int previousFieldID = initializationScope.lastVisibleFieldID;
                try {
                    initializationScope.initializedField = originalField;
                    initializationScope.lastVisibleFieldID = originalField.id;
                    ASTNode.resolveAnnotations(initializationScope, fieldDecl.annotations, originalField);
                }
                finally {
                    initializationScope.initializedField = previousField;
                    initializationScope.lastVisibleFieldID = previousFieldID;
                }
                initializationScope.initializedField = previousField;
                initializationScope.lastVisibleFieldID = previousFieldID;
            }
        }
        return originalField.tagBits;
    }
    
    public final boolean isDefault() {
        return !this.isPublic() && !this.isProtected() && !this.isPrivate();
    }
    
    public final boolean isDeprecated() {
        return (this.modifiers & 0x100000) != 0x0;
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
    
    public final boolean isSynthetic() {
        return (this.modifiers & 0x1000) != 0x0;
    }
    
    public final boolean isTransient() {
        return (this.modifiers & 0x80) != 0x0;
    }
    
    public final boolean isUsed() {
        return (this.modifiers & 0x8000000) != 0x0 || this.compoundUseFlag > 0;
    }
    
    public final boolean isUsedOnlyInCompound() {
        return (this.modifiers & 0x8000000) == 0x0 && this.compoundUseFlag > 0;
    }
    
    public final boolean isViewedAsDeprecated() {
        return (this.modifiers & 0x300000) != 0x0;
    }
    
    @Override
    public final boolean isVolatile() {
        return (this.modifiers & 0x40) != 0x0;
    }
    
    @Override
    public final int kind() {
        return 1;
    }
    
    public FieldBinding original() {
        return this;
    }
    
    @Override
    public void setAnnotations(final AnnotationBinding[] annotations) {
        this.declaringClass.storeAnnotations(this, annotations);
    }
    
    public FieldDeclaration sourceField() {
        SourceTypeBinding sourceType;
        try {
            sourceType = (SourceTypeBinding)this.declaringClass;
        }
        catch (final ClassCastException ex) {
            return null;
        }
        final FieldDeclaration[] fields = sourceType.scope.referenceContext.fields;
        if (fields != null) {
            int i = fields.length;
            while (--i >= 0) {
                if (this == fields[i].binding) {
                    return fields[i];
                }
            }
        }
        return null;
    }
}
