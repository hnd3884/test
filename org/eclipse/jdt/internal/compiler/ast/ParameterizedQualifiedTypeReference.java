package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class ParameterizedQualifiedTypeReference extends ArrayQualifiedTypeReference
{
    public TypeReference[][] typeArguments;
    ReferenceBinding[] typesPerToken;
    
    public ParameterizedQualifiedTypeReference(final char[][] tokens, final TypeReference[][] typeArguments, final int dim, final long[] positions) {
        super(tokens, dim, positions);
        this.typeArguments = typeArguments;
    Label_0093:
        for (int i = 0, max = typeArguments.length; i < max; ++i) {
            final TypeReference[] typeArgumentsOnTypeComponent = typeArguments[i];
            if (typeArgumentsOnTypeComponent != null) {
                for (int j = 0, max2 = typeArgumentsOnTypeComponent.length; j < max2; ++j) {
                    if ((typeArgumentsOnTypeComponent[j].bits & 0x100000) != 0x0) {
                        this.bits |= 0x100000;
                        break Label_0093;
                    }
                }
            }
        }
    }
    
    public ParameterizedQualifiedTypeReference(final char[][] tokens, final TypeReference[][] typeArguments, final int dim, final Annotation[][] annotationsOnDimensions, final long[] positions) {
        this(tokens, typeArguments, dim, positions);
        this.setAnnotationsOnDimensions(annotationsOnDimensions);
        if (annotationsOnDimensions != null) {
            this.bits |= 0x100000;
        }
    }
    
    @Override
    public void checkBounds(final Scope scope) {
        if (this.resolvedType == null || !this.resolvedType.isValidBinding()) {
            return;
        }
        this.checkBounds((ReferenceBinding)this.resolvedType.leafComponentType(), scope, this.typeArguments.length - 1);
    }
    
    public void checkBounds(final ReferenceBinding type, final Scope scope, final int index) {
        if (index > 0) {
            final ReferenceBinding enclosingType = this.typesPerToken[index - 1];
            if (enclosingType != null) {
                this.checkBounds(enclosingType, scope, index - 1);
            }
        }
        if (type.isParameterizedTypeWithActualArguments()) {
            final ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)type;
            final ReferenceBinding currentType = parameterizedType.genericType();
            final TypeVariableBinding[] typeVariables = currentType.typeVariables();
            if (typeVariables != null) {
                parameterizedType.boundCheck(scope, this.typeArguments[index]);
            }
        }
    }
    
    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(final int additionalDimensions, final Annotation[][] additionalAnnotations, final boolean isVarargs) {
        final int totalDimensions = this.dimensions() + additionalDimensions;
        final Annotation[][] allAnnotations = this.getMergedAnnotationsOnDimensions(additionalDimensions, additionalAnnotations);
        final ParameterizedQualifiedTypeReference pqtr = new ParameterizedQualifiedTypeReference(this.tokens, this.typeArguments, totalDimensions, allAnnotations, this.sourcePositions);
        pqtr.annotations = this.annotations;
        final ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference = pqtr;
        parameterizedQualifiedTypeReference.bits |= (this.bits & 0x100000);
        if (!isVarargs) {
            pqtr.extendedDimensions = additionalDimensions;
        }
        return pqtr;
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
                    final TypeReference[] arguments = this.typeArguments[i];
                    if (arguments != null) {
                        for (int j = 0; j < arguments.length; ++j) {
                            if (arguments[j].hasNullTypeAnnotation(position)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public char[][] getParameterizedTypeName() {
        final int length = this.tokens.length;
        final char[][] qParamName = new char[length][];
        for (int i = 0; i < length; ++i) {
            final TypeReference[] arguments = this.typeArguments[i];
            if (arguments == null) {
                qParamName[i] = this.tokens[i];
            }
            else {
                final StringBuffer buffer = new StringBuffer(5);
                buffer.append(this.tokens[i]);
                buffer.append('<');
                for (int j = 0, argLength = arguments.length; j < argLength; ++j) {
                    if (j > 0) {
                        buffer.append(',');
                    }
                    buffer.append(CharOperation.concatWith(arguments[j].getParameterizedTypeName(), '.'));
                }
                buffer.append('>');
                final int nameLength = buffer.length();
                buffer.getChars(0, nameLength, qParamName[i] = new char[nameLength], 0);
            }
        }
        final int dim = this.dimensions;
        if (dim > 0) {
            final char[] dimChars = new char[dim * 2];
            for (int k = 0; k < dim; ++k) {
                final int index = k * 2;
                dimChars[index] = '[';
                dimChars[index + 1] = ']';
            }
            qParamName[length - 1] = CharOperation.concat(qParamName[length - 1], dimChars);
        }
        return qParamName;
    }
    
    @Override
    public TypeReference[][] getTypeArguments() {
        return this.typeArguments;
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        return null;
    }
    
    private TypeBinding internalResolveType(final Scope scope, final boolean checkBounds, final int location) {
        this.constant = Constant.NotAConstant;
        if ((this.bits & 0x40000) == 0x0 || this.resolvedType == null) {
            this.bits |= 0x40000;
            final TypeBinding type = this.internalResolveLeafType(scope, checkBounds);
            this.createArrayType(scope);
            this.resolveAnnotations(scope, location);
            if (this.typeArguments != null) {
                this.checkIllegalNullAnnotations(scope, this.typeArguments[this.typeArguments.length - 1]);
            }
            return (type == null) ? type : this.resolvedType;
        }
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
    
    private TypeBinding internalResolveLeafType(final Scope scope, final boolean checkBounds) {
        final boolean isClassScope = scope.kind == 3;
        final Binding binding = scope.getPackage(this.tokens);
        if (binding != null && !binding.isValidBinding()) {
            this.resolvedType = (ReferenceBinding)binding;
            this.reportInvalidType(scope);
            for (int i = 0, max = this.tokens.length; i < max; ++i) {
                final TypeReference[] args = this.typeArguments[i];
                if (args != null) {
                    for (final TypeReference typeArgument : args) {
                        if (isClassScope) {
                            typeArgument.resolveType((ClassScope)scope);
                        }
                        else {
                            typeArgument.resolveType((BlockScope)scope, checkBounds);
                        }
                    }
                }
            }
            return null;
        }
        final PackageBinding packageBinding = (binding == null) ? null : ((PackageBinding)binding);
        this.rejectAnnotationsOnPackageQualifiers(scope, packageBinding);
        boolean typeIsConsistent = true;
        ReferenceBinding qualifyingType = null;
        final int max2 = this.tokens.length;
        this.typesPerToken = new ReferenceBinding[max2];
        for (int k = (packageBinding == null) ? 0 : packageBinding.compoundName.length; k < max2; ++k) {
            this.findNextTypeBinding(k, scope, packageBinding);
            if (!this.resolvedType.isValidBinding()) {
                this.reportInvalidType(scope);
                for (int l = k; l < max2; ++l) {
                    final TypeReference[] args2 = this.typeArguments[l];
                    if (args2 != null) {
                        for (final TypeReference typeArgument2 : args2) {
                            if (isClassScope) {
                                typeArgument2.resolveType((ClassScope)scope);
                            }
                            else {
                                typeArgument2.resolveType((BlockScope)scope);
                            }
                        }
                    }
                }
                return null;
            }
            final ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
            if (qualifyingType == null) {
                qualifyingType = currentType.enclosingType();
                if (qualifyingType != null) {
                    qualifyingType = (ReferenceBinding)(currentType.isStatic() ? scope.environment().convertToRawType(qualifyingType, false) : scope.environment().convertToParameterizedType(qualifyingType));
                }
            }
            else {
                if (this.annotations != null) {
                    QualifiedTypeReference.rejectAnnotationsOnStaticMemberQualififer(scope, currentType, this.annotations[k - 1]);
                }
                if (typeIsConsistent && currentType.isStatic() && (qualifyingType.isParameterizedTypeWithActualArguments() || qualifyingType.isGenericType())) {
                    scope.problemReporter().staticMemberOfParameterizedType(this, scope.environment().createParameterizedType((ReferenceBinding)currentType.erasure(), null, qualifyingType), k);
                    typeIsConsistent = false;
                }
                final ReferenceBinding enclosingType = currentType.enclosingType();
                if (enclosingType != null && TypeBinding.notEquals(enclosingType.erasure(), qualifyingType.erasure())) {
                    qualifyingType = enclosingType;
                }
            }
            final TypeReference[] args2 = this.typeArguments[k];
            if (args2 != null) {
                TypeReference keep = null;
                if (isClassScope) {
                    keep = ((ClassScope)scope).superTypeReference;
                    ((ClassScope)scope).superTypeReference = null;
                }
                final int argLength3 = args2.length;
                final boolean isDiamond = argLength3 == 0 && k == max2 - 1 && (this.bits & 0x80000) != 0x0;
                final TypeBinding[] argTypes = new TypeBinding[argLength3];
                boolean argHasError = false;
                final ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
                for (int j2 = 0; j2 < argLength3; ++j2) {
                    final TypeReference arg = args2[j2];
                    final TypeBinding argType = isClassScope ? arg.resolveTypeArgument((ClassScope)scope, currentOriginal, j2) : arg.resolveTypeArgument((BlockScope)scope, currentOriginal, j2);
                    if (argType == null) {
                        argHasError = true;
                    }
                    else {
                        argTypes[j2] = argType;
                    }
                }
                if (argHasError) {
                    return null;
                }
                if (isClassScope) {
                    ((ClassScope)scope).superTypeReference = keep;
                    if (((ClassScope)scope).detectHierarchyCycle(currentOriginal, this)) {
                        return null;
                    }
                }
                final TypeVariableBinding[] typeVariables = currentOriginal.typeVariables();
                if (typeVariables == Binding.NO_TYPE_VARIABLES) {
                    if (scope.compilerOptions().originalSourceLevel >= 3211264L) {
                        scope.problemReporter().nonGenericTypeCannotBeParameterized(k, this, currentType, argTypes);
                        return null;
                    }
                    return this.resolvedType = ((qualifyingType != null && qualifyingType.isParameterizedType()) ? scope.environment().createParameterizedType(currentOriginal, null, qualifyingType) : currentType);
                }
                else {
                    if (argLength3 != typeVariables.length && !isDiamond) {
                        scope.problemReporter().incorrectArityForParameterizedType(this, currentType, argTypes, k);
                        return null;
                    }
                    if (typeIsConsistent && !currentType.isStatic()) {
                        final ReferenceBinding actualEnclosing = currentType.enclosingType();
                        if (actualEnclosing != null && actualEnclosing.isRawType()) {
                            scope.problemReporter().rawMemberTypeCannotBeParameterized(this, scope.environment().createRawType(currentOriginal, actualEnclosing), argTypes);
                            typeIsConsistent = false;
                        }
                    }
                    final ParameterizedTypeBinding parameterizedType = scope.environment().createParameterizedType(currentOriginal, argTypes, qualifyingType);
                    if (!isDiamond) {
                        if (checkBounds) {
                            parameterizedType.boundCheck(scope, args2);
                        }
                        else {
                            scope.deferBoundCheck(this);
                        }
                    }
                    else {
                        parameterizedType.arguments = ParameterizedSingleTypeReference.DIAMOND_TYPE_ARGUMENTS;
                    }
                    qualifyingType = parameterizedType;
                }
            }
            else {
                final ReferenceBinding currentOriginal2 = (ReferenceBinding)currentType.original();
                if (isClassScope && ((ClassScope)scope).detectHierarchyCycle(currentOriginal2, this)) {
                    return null;
                }
                if (currentOriginal2.isGenericType()) {
                    if (typeIsConsistent && qualifyingType != null && qualifyingType.isParameterizedType() && !currentOriginal2.isStatic()) {
                        scope.problemReporter().parameterizedMemberTypeMissingArguments(this, scope.environment().createParameterizedType(currentOriginal2, null, qualifyingType), k);
                        typeIsConsistent = false;
                    }
                    qualifyingType = scope.environment().createRawType(currentOriginal2, qualifyingType);
                }
                else {
                    qualifyingType = ((qualifyingType != null && qualifyingType.isParameterizedType()) ? scope.environment().createParameterizedType(currentOriginal2, null, qualifyingType) : currentType);
                }
            }
            if (this.isTypeUseDeprecated(qualifyingType, scope)) {
                this.reportDeprecatedType(qualifyingType, scope, k);
            }
            this.resolvedType = qualifyingType;
            this.typesPerToken[k] = qualifyingType;
            this.recordResolution(scope.environment(), this.resolvedType);
        }
        return this.resolvedType;
    }
    
    private void createArrayType(final Scope scope) {
        if (this.dimensions > 0) {
            if (this.dimensions > 255) {
                scope.problemReporter().tooManyDimensions(this);
            }
            this.resolvedType = scope.createArrayType(this.resolvedType, this.dimensions);
        }
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        final int length = this.tokens.length;
        for (int i = 0; i < length - 1; ++i) {
            if (this.annotations != null && this.annotations[i] != null) {
                ASTNode.printAnnotations(this.annotations[i], output);
                output.append(' ');
            }
            output.append(this.tokens[i]);
            final TypeReference[] typeArgument = this.typeArguments[i];
            if (typeArgument != null) {
                output.append('<');
                final int typeArgumentLength = typeArgument.length;
                if (typeArgumentLength > 0) {
                    final int max = typeArgumentLength - 1;
                    for (int j = 0; j < max; ++j) {
                        typeArgument[j].print(0, output);
                        output.append(", ");
                    }
                    typeArgument[max].print(0, output);
                }
                output.append('>');
            }
            output.append('.');
        }
        if (this.annotations != null && this.annotations[length - 1] != null) {
            output.append(" ");
            ASTNode.printAnnotations(this.annotations[length - 1], output);
            output.append(' ');
        }
        output.append(this.tokens[length - 1]);
        final TypeReference[] typeArgument2 = this.typeArguments[length - 1];
        if (typeArgument2 != null) {
            output.append('<');
            final int typeArgumentLength2 = typeArgument2.length;
            if (typeArgumentLength2 > 0) {
                final int max2 = typeArgumentLength2 - 1;
                for (int k = 0; k < max2; ++k) {
                    typeArgument2[k].print(0, output);
                    output.append(", ");
                }
                typeArgument2[max2].print(0, output);
            }
            output.append('>');
        }
        final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions();
        if ((this.bits & 0x4000) != 0x0) {
            for (int l = 0; l < this.dimensions - 1; ++l) {
                if (annotationsOnDimensions != null && annotationsOnDimensions[l] != null) {
                    output.append(" ");
                    ASTNode.printAnnotations(annotationsOnDimensions[l], output);
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
            for (int l = 0; l < this.dimensions; ++l) {
                if (annotationsOnDimensions != null && annotationsOnDimensions[l] != null) {
                    output.append(" ");
                    ASTNode.printAnnotations(annotationsOnDimensions[l], output);
                    output.append(" ");
                }
                output.append("[]");
            }
        }
        return output;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope, final boolean checkBounds, final int location) {
        return this.internalResolveType(scope, checkBounds, location);
    }
    
    @Override
    public TypeBinding resolveType(final ClassScope scope, final int location) {
        return this.internalResolveType(scope, false, location);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                for (int annotationsLevels = this.annotations.length, i = 0; i < annotationsLevels; ++i) {
                    for (int annotationsLength = (this.annotations[i] == null) ? 0 : this.annotations[i].length, j = 0; j < annotationsLength; ++j) {
                        this.annotations[i][j].traverse(visitor, scope);
                    }
                }
            }
            final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(true);
            if (annotationsOnDimensions != null) {
                for (int i = 0, max = annotationsOnDimensions.length; i < max; ++i) {
                    final Annotation[] annotations2 = annotationsOnDimensions[i];
                    for (int k = 0, max2 = (annotations2 == null) ? 0 : annotations2.length; k < max2; ++k) {
                        final Annotation annotation = annotations2[k];
                        annotation.traverse(visitor, scope);
                    }
                }
            }
            for (int i = 0, max = this.typeArguments.length; i < max; ++i) {
                if (this.typeArguments[i] != null) {
                    for (int j = 0, max3 = this.typeArguments[i].length; j < max3; ++j) {
                        this.typeArguments[i][j].traverse(visitor, scope);
                    }
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                for (int annotationsLevels = this.annotations.length, i = 0; i < annotationsLevels; ++i) {
                    for (int annotationsLength = (this.annotations[i] == null) ? 0 : this.annotations[i].length, j = 0; j < annotationsLength; ++j) {
                        this.annotations[i][j].traverse(visitor, scope);
                    }
                }
            }
            final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(true);
            if (annotationsOnDimensions != null) {
                for (int i = 0, max = annotationsOnDimensions.length; i < max; ++i) {
                    final Annotation[] annotations2 = annotationsOnDimensions[i];
                    for (int k = 0, max2 = (annotations2 == null) ? 0 : annotations2.length; k < max2; ++k) {
                        final Annotation annotation = annotations2[k];
                        annotation.traverse(visitor, scope);
                    }
                }
            }
            for (int i = 0, max = this.typeArguments.length; i < max; ++i) {
                if (this.typeArguments[i] != null) {
                    for (int j = 0, max3 = this.typeArguments[i].length; j < max3; ++j) {
                        this.typeArguments[i][j].traverse(visitor, scope);
                    }
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}
