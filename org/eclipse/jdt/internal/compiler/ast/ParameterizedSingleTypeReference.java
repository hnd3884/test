package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ParameterizedSingleTypeReference extends ArrayTypeReference
{
    public static final TypeBinding[] DIAMOND_TYPE_ARGUMENTS;
    public TypeReference[] typeArguments;
    
    static {
        DIAMOND_TYPE_ARGUMENTS = new TypeBinding[0];
    }
    
    public ParameterizedSingleTypeReference(final char[] name, final TypeReference[] typeArguments, final int dim, final long pos) {
        super(name, dim, pos);
        this.originalSourceEnd = this.sourceEnd;
        this.typeArguments = typeArguments;
        for (int i = 0, max = typeArguments.length; i < max; ++i) {
            if ((typeArguments[i].bits & 0x100000) != 0x0) {
                this.bits |= 0x100000;
                break;
            }
        }
    }
    
    public ParameterizedSingleTypeReference(final char[] name, final TypeReference[] typeArguments, final int dim, final Annotation[][] annotationsOnDimensions, final long pos) {
        this(name, typeArguments, dim, pos);
        this.setAnnotationsOnDimensions(annotationsOnDimensions);
        if (annotationsOnDimensions != null) {
            this.bits |= 0x100000;
        }
    }
    
    @Override
    public void checkBounds(final Scope scope) {
        if (this.resolvedType == null) {
            return;
        }
        if (this.resolvedType.leafComponentType() instanceof ParameterizedTypeBinding) {
            final ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)this.resolvedType.leafComponentType();
            final TypeBinding[] argTypes = parameterizedType.arguments;
            if (argTypes != null) {
                parameterizedType.boundCheck(scope, this.typeArguments);
            }
        }
    }
    
    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(final int additionalDimensions, final Annotation[][] additionalAnnotations, final boolean isVarargs) {
        final int totalDimensions = this.dimensions() + additionalDimensions;
        final Annotation[][] allAnnotations = this.getMergedAnnotationsOnDimensions(additionalDimensions, additionalAnnotations);
        final ParameterizedSingleTypeReference parameterizedSingleTypeReference = new ParameterizedSingleTypeReference(this.token, this.typeArguments, totalDimensions, allAnnotations, ((long)this.sourceStart << 32) + this.sourceEnd);
        parameterizedSingleTypeReference.annotations = this.annotations;
        final ParameterizedSingleTypeReference parameterizedSingleTypeReference2 = parameterizedSingleTypeReference;
        parameterizedSingleTypeReference2.bits |= (this.bits & 0x100000);
        if (!isVarargs) {
            parameterizedSingleTypeReference.extendedDimensions = additionalDimensions;
        }
        return parameterizedSingleTypeReference;
    }
    
    @Override
    public char[][] getParameterizedTypeName() {
        final StringBuffer buffer = new StringBuffer(5);
        buffer.append(this.token).append('<');
        for (int i = 0, length = this.typeArguments.length; i < length; ++i) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(CharOperation.concatWith(this.typeArguments[i].getParameterizedTypeName(), '.'));
        }
        buffer.append('>');
        final int nameLength = buffer.length();
        char[] name = new char[nameLength];
        buffer.getChars(0, nameLength, name, 0);
        final int dim = this.dimensions;
        if (dim > 0) {
            final char[] dimChars = new char[dim * 2];
            for (int j = 0; j < dim; ++j) {
                final int index = j * 2;
                dimChars[index] = '[';
                dimChars[index + 1] = ']';
            }
            name = CharOperation.concat(name, dimChars);
        }
        return new char[][] { name };
    }
    
    @Override
    public TypeReference[][] getTypeArguments() {
        return new TypeReference[][] { this.typeArguments };
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        return null;
    }
    
    @Override
    public boolean isParameterizedTypeReference() {
        return true;
    }
    
    @Override
    public boolean hasNullTypeAnnotation(final AnnotationPosition position) {
        if (super.hasNullTypeAnnotation(position)) {
            return true;
        }
        if (position == AnnotationPosition.ANY) {
            if (this.resolvedType != null && !this.resolvedType.hasNullTypeAnnotations()) {
                return false;
            }
            if (this.typeArguments != null) {
                for (int i = 0; i < this.typeArguments.length; ++i) {
                    if (this.typeArguments[i].hasNullTypeAnnotation(position)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private TypeBinding internalResolveType(final Scope scope, final ReferenceBinding enclosingType, final boolean checkBounds, final int location) {
        this.constant = Constant.NotAConstant;
        if ((this.bits & 0x40000) != 0x0 && this.resolvedType != null) {
            if (this.resolvedType.isValidBinding()) {
                return this.resolvedType;
            }
            switch (this.resolvedType.problemId()) {
                case 1:
                case 2:
                case 5: {
                    final TypeBinding type = this.resolvedType.closestMatch();
                    return type;
                }
                default: {
                    return null;
                }
            }
        }
        else {
            this.bits |= 0x40000;
            TypeBinding type = this.internalResolveLeafType(scope, enclosingType, checkBounds);
            if (type == null) {
                this.resolvedType = this.createArrayType(scope, this.resolvedType);
                this.resolveAnnotations(scope, 0);
                return null;
            }
            type = this.createArrayType(scope, type);
            if (!this.resolvedType.isValidBinding() && this.resolvedType.dimensions() == type.dimensions()) {
                this.resolveAnnotations(scope, 0);
                return type;
            }
            this.resolvedType = type;
            this.resolveAnnotations(scope, location);
            return this.resolvedType;
        }
    }
    
    private TypeBinding internalResolveLeafType(final Scope scope, ReferenceBinding enclosingType, final boolean checkBounds) {
        ReferenceBinding currentType = null;
        if (enclosingType == null) {
            this.resolvedType = scope.getType(this.token);
            Label_0189: {
                if (!this.resolvedType.isValidBinding()) {
                    this.reportInvalidType(scope);
                    switch (this.resolvedType.problemId()) {
                        case 1:
                        case 2:
                        case 5: {
                            final TypeBinding type = this.resolvedType.closestMatch();
                            if (type instanceof ReferenceBinding) {
                                currentType = (ReferenceBinding)type;
                                break Label_0189;
                            }
                            break;
                        }
                    }
                    final boolean isClassScope = scope.kind == 3;
                    for (int argLength = this.typeArguments.length, i = 0; i < argLength; ++i) {
                        final TypeReference typeArgument = this.typeArguments[i];
                        if (isClassScope) {
                            typeArgument.resolveType((ClassScope)scope);
                        }
                        else {
                            typeArgument.resolveType((BlockScope)scope, checkBounds);
                        }
                    }
                    return null;
                }
                currentType = (ReferenceBinding)this.resolvedType;
            }
            enclosingType = currentType.enclosingType();
            if (enclosingType != null) {
                enclosingType = (ReferenceBinding)(currentType.isStatic() ? scope.environment().convertToRawType(enclosingType, false) : scope.environment().convertToParameterizedType(enclosingType));
                currentType = scope.environment().createParameterizedType((ReferenceBinding)currentType.erasure(), null, enclosingType);
            }
        }
        else {
            currentType = (ReferenceBinding)(this.resolvedType = scope.getMemberType(this.token, enclosingType));
            if (!this.resolvedType.isValidBinding()) {
                scope.problemReporter().invalidEnclosingType(this, currentType, enclosingType);
                return null;
            }
            if (this.isTypeUseDeprecated(currentType, scope)) {
                scope.problemReporter().deprecatedType(currentType, this);
            }
            final ReferenceBinding currentEnclosing = currentType.enclosingType();
            if (currentEnclosing != null && TypeBinding.notEquals(currentEnclosing.erasure(), enclosingType.erasure())) {
                enclosingType = currentEnclosing;
            }
        }
        final boolean isClassScope2 = scope.kind == 3;
        TypeReference keep = null;
        if (isClassScope2) {
            keep = ((ClassScope)scope).superTypeReference;
            ((ClassScope)scope).superTypeReference = null;
        }
        final boolean isDiamond = (this.bits & 0x80000) != 0x0;
        final int argLength2 = this.typeArguments.length;
        final TypeBinding[] argTypes = new TypeBinding[argLength2];
        boolean argHasError = false;
        final ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
        for (int j = 0; j < argLength2; ++j) {
            final TypeReference typeArgument2 = this.typeArguments[j];
            final TypeBinding argType = isClassScope2 ? typeArgument2.resolveTypeArgument((ClassScope)scope, currentOriginal, j) : typeArgument2.resolveTypeArgument((BlockScope)scope, currentOriginal, j);
            this.bits |= (typeArgument2.bits & 0x100000);
            if (argType == null) {
                argHasError = true;
            }
            else {
                argTypes[j] = argType;
            }
        }
        if (argHasError) {
            return null;
        }
        if (isClassScope2) {
            ((ClassScope)scope).superTypeReference = keep;
            if (((ClassScope)scope).detectHierarchyCycle(currentOriginal, this)) {
                return null;
            }
        }
        final TypeVariableBinding[] typeVariables = currentOriginal.typeVariables();
        if (typeVariables == Binding.NO_TYPE_VARIABLES) {
            final boolean isCompliant15 = scope.compilerOptions().originalSourceLevel >= 3211264L;
            if ((currentOriginal.tagBits & 0x80L) == 0x0L && isCompliant15) {
                this.resolvedType = currentType;
                scope.problemReporter().nonGenericTypeCannotBeParameterized(0, this, currentType, argTypes);
                return null;
            }
            if (!isCompliant15) {
                if (!this.resolvedType.isValidBinding()) {
                    return currentType;
                }
                return this.resolvedType = currentType;
            }
        }
        else if (argLength2 != typeVariables.length) {
            if (!isDiamond) {
                scope.problemReporter().incorrectArityForParameterizedType(this, currentType, argTypes);
                return null;
            }
        }
        else if (!currentType.isStatic()) {
            final ReferenceBinding actualEnclosing = currentType.enclosingType();
            if (actualEnclosing != null && actualEnclosing.isRawType()) {
                scope.problemReporter().rawMemberTypeCannotBeParameterized(this, scope.environment().createRawType(currentOriginal, actualEnclosing), argTypes);
                return null;
            }
        }
        final ParameterizedTypeBinding parameterizedType = scope.environment().createParameterizedType(currentOriginal, argTypes, enclosingType);
        if (!isDiamond) {
            if (checkBounds) {
                parameterizedType.boundCheck(scope, this.typeArguments);
            }
            else {
                scope.deferBoundCheck(this);
            }
        }
        else {
            parameterizedType.arguments = ParameterizedSingleTypeReference.DIAMOND_TYPE_ARGUMENTS;
        }
        if (this.isTypeUseDeprecated(parameterizedType, scope)) {
            this.reportDeprecatedType(parameterizedType, scope);
        }
        this.checkIllegalNullAnnotations(scope, this.typeArguments);
        if (!this.resolvedType.isValidBinding()) {
            return parameterizedType;
        }
        return this.resolvedType = parameterizedType;
    }
    
    private TypeBinding createArrayType(final Scope scope, final TypeBinding type) {
        if (this.dimensions > 0) {
            if (this.dimensions > 255) {
                scope.problemReporter().tooManyDimensions(this);
            }
            return scope.createArrayType(type, this.dimensions);
        }
        return type;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        if (this.annotations != null && this.annotations[0] != null) {
            ASTNode.printAnnotations(this.annotations[0], output);
            output.append(' ');
        }
        output.append(this.token);
        output.append("<");
        final int length = this.typeArguments.length;
        if (length > 0) {
            final int max = length - 1;
            for (int i = 0; i < max; ++i) {
                this.typeArguments[i].print(0, output);
                output.append(", ");
            }
            this.typeArguments[max].print(0, output);
        }
        output.append(">");
        final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions();
        if ((this.bits & 0x4000) != 0x0) {
            for (int i = 0; i < this.dimensions - 1; ++i) {
                if (annotationsOnDimensions != null && annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ASTNode.printAnnotations(annotationsOnDimensions[i], output);
                    output.append(" ");
                }
                output.append("[]");
            }
            if (annotationsOnDimensions != null && annotationsOnDimensions[this.dimensions - 1] != null) {
                output.append(" ");
                ASTNode.printAnnotations(annotationsOnDimensions[this.dimensions - 1], output);
                output.append(" ");
            }
            output.append("...");
        }
        else {
            for (int i = 0; i < this.dimensions; ++i) {
                if (annotationsOnDimensions != null && annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ASTNode.printAnnotations(annotationsOnDimensions[i], output);
                    output.append(" ");
                }
                output.append("[]");
            }
        }
        return output;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope, final boolean checkBounds, final int location) {
        return this.internalResolveType(scope, null, checkBounds, location);
    }
    
    @Override
    public TypeBinding resolveType(final ClassScope scope, final int location) {
        return this.internalResolveType(scope, null, false, location);
    }
    
    @Override
    public TypeBinding resolveTypeEnclosing(final BlockScope scope, final ReferenceBinding enclosingType) {
        return this.internalResolveType(scope, enclosingType, true, 0);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                final Annotation[] typeAnnotations = this.annotations[0];
                for (int i = 0, length = (typeAnnotations == null) ? 0 : typeAnnotations.length; i < length; ++i) {
                    typeAnnotations[i].traverse(visitor, scope);
                }
            }
            final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(true);
            if (annotationsOnDimensions != null) {
                for (int i = 0, max = annotationsOnDimensions.length; i < max; ++i) {
                    final Annotation[] annotations2 = annotationsOnDimensions[i];
                    if (annotations2 != null) {
                        for (int j = 0, max2 = annotations2.length; j < max2; ++j) {
                            final Annotation annotation = annotations2[j];
                            annotation.traverse(visitor, scope);
                        }
                    }
                }
            }
            for (int i = 0, max = this.typeArguments.length; i < max; ++i) {
                this.typeArguments[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                final Annotation[] typeAnnotations = this.annotations[0];
                for (int i = 0, length = (typeAnnotations == null) ? 0 : typeAnnotations.length; i < length; ++i) {
                    typeAnnotations[i].traverse(visitor, scope);
                }
            }
            final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(true);
            if (annotationsOnDimensions != null) {
                for (int i = 0, max = annotationsOnDimensions.length; i < max; ++i) {
                    final Annotation[] annotations2 = annotationsOnDimensions[i];
                    for (int j = 0, max2 = annotations2.length; j < max2; ++j) {
                        final Annotation annotation = annotations2[j];
                        annotation.traverse(visitor, scope);
                    }
                }
            }
            for (int i = 0, max = this.typeArguments.length; i < max; ++i) {
                this.typeArguments[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
