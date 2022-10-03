package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;

public class Javadoc extends ASTNode
{
    public JavadocSingleNameReference[] paramReferences;
    public JavadocSingleTypeReference[] paramTypeParameters;
    public TypeReference[] exceptionReferences;
    public JavadocReturnStatement returnStatement;
    public Expression[] seeReferences;
    public long[] inheritedPositions;
    public JavadocSingleNameReference[] invalidParameters;
    public long valuePositions;
    
    public Javadoc(final int sourceStart, final int sourceEnd) {
        this.inheritedPositions = null;
        this.valuePositions = -1L;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.bits |= 0x10000;
    }
    
    boolean canBeSeen(final int visibility, final int modifiers) {
        if (modifiers < 0) {
            return true;
        }
        switch (modifiers & 0x7) {
            case 1: {
                return true;
            }
            case 4: {
                return visibility != 1;
            }
            case 0: {
                return visibility == 0 || visibility == 2;
            }
            case 2: {
                return visibility == 2;
            }
            default: {
                return true;
            }
        }
    }
    
    public ASTNode getNodeStartingAt(final int start) {
        int length = 0;
        if (this.paramReferences != null) {
            length = this.paramReferences.length;
            for (int i = 0; i < length; ++i) {
                final JavadocSingleNameReference param = this.paramReferences[i];
                if (param.sourceStart == start) {
                    return param;
                }
            }
        }
        if (this.invalidParameters != null) {
            length = this.invalidParameters.length;
            for (int i = 0; i < length; ++i) {
                final JavadocSingleNameReference param = this.invalidParameters[i];
                if (param.sourceStart == start) {
                    return param;
                }
            }
        }
        if (this.paramTypeParameters != null) {
            length = this.paramTypeParameters.length;
            for (int i = 0; i < length; ++i) {
                final JavadocSingleTypeReference param2 = this.paramTypeParameters[i];
                if (param2.sourceStart == start) {
                    return param2;
                }
            }
        }
        if (this.exceptionReferences != null) {
            length = this.exceptionReferences.length;
            for (int i = 0; i < length; ++i) {
                final TypeReference typeRef = this.exceptionReferences[i];
                if (typeRef.sourceStart == start) {
                    return typeRef;
                }
            }
        }
        if (this.seeReferences != null) {
            length = this.seeReferences.length;
            for (int i = 0; i < length; ++i) {
                final Expression expression = this.seeReferences[i];
                if (expression.sourceStart == start) {
                    return expression;
                }
                if (expression instanceof JavadocAllocationExpression) {
                    final JavadocAllocationExpression allocationExpr = (JavadocAllocationExpression)this.seeReferences[i];
                    if (allocationExpr.binding != null && allocationExpr.binding.isValidBinding() && allocationExpr.arguments != null) {
                        for (int j = 0, l = allocationExpr.arguments.length; j < l; ++j) {
                            if (allocationExpr.arguments[j].sourceStart == start) {
                                return allocationExpr.arguments[j];
                            }
                        }
                    }
                }
                else if (expression instanceof JavadocMessageSend) {
                    final JavadocMessageSend messageSend = (JavadocMessageSend)this.seeReferences[i];
                    if (messageSend.binding != null && messageSend.binding.isValidBinding() && messageSend.arguments != null) {
                        for (int j = 0, l = messageSend.arguments.length; j < l; ++j) {
                            if (messageSend.arguments[j].sourceStart == start) {
                                return messageSend.arguments[j];
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public StringBuffer print(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output).append("/**\n");
        if (this.paramReferences != null) {
            for (int i = 0, length = this.paramReferences.length; i < length; ++i) {
                ASTNode.printIndent(indent + 1, output).append(" * @param ");
                this.paramReferences[i].print(indent, output).append('\n');
            }
        }
        if (this.paramTypeParameters != null) {
            for (int i = 0, length = this.paramTypeParameters.length; i < length; ++i) {
                ASTNode.printIndent(indent + 1, output).append(" * @param <");
                this.paramTypeParameters[i].print(indent, output).append(">\n");
            }
        }
        if (this.returnStatement != null) {
            ASTNode.printIndent(indent + 1, output).append(" * @");
            this.returnStatement.print(indent, output).append('\n');
        }
        if (this.exceptionReferences != null) {
            for (int i = 0, length = this.exceptionReferences.length; i < length; ++i) {
                ASTNode.printIndent(indent + 1, output).append(" * @throws ");
                this.exceptionReferences[i].print(indent, output).append('\n');
            }
        }
        if (this.seeReferences != null) {
            for (int i = 0, length = this.seeReferences.length; i < length; ++i) {
                ASTNode.printIndent(indent + 1, output).append(" * @see ");
                this.seeReferences[i].print(indent, output).append('\n');
            }
        }
        ASTNode.printIndent(indent, output).append(" */\n");
        return output;
    }
    
    public void resolve(final ClassScope scope) {
        if ((this.bits & 0x10000) == 0x0) {
            return;
        }
        if (this.inheritedPositions != null) {
            for (int length = this.inheritedPositions.length, i = 0; i < length; ++i) {
                final int start = (int)(this.inheritedPositions[i] >>> 32);
                final int end = (int)this.inheritedPositions[i];
                scope.problemReporter().javadocUnexpectedTag(start, end);
            }
        }
        for (int paramTagsSize = (this.paramReferences == null) ? 0 : this.paramReferences.length, i = 0; i < paramTagsSize; ++i) {
            final JavadocSingleNameReference param = this.paramReferences[i];
            scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
        }
        this.resolveTypeParameterTags(scope, true);
        if (this.returnStatement != null) {
            scope.problemReporter().javadocUnexpectedTag(this.returnStatement.sourceStart, this.returnStatement.sourceEnd);
        }
        for (int throwsTagsLength = (this.exceptionReferences == null) ? 0 : this.exceptionReferences.length, j = 0; j < throwsTagsLength; ++j) {
            final TypeReference typeRef = this.exceptionReferences[j];
            int start2;
            int end2;
            if (typeRef instanceof JavadocSingleTypeReference) {
                final JavadocSingleTypeReference singleRef = (JavadocSingleTypeReference)typeRef;
                start2 = singleRef.tagSourceStart;
                end2 = singleRef.tagSourceEnd;
            }
            else if (typeRef instanceof JavadocQualifiedTypeReference) {
                final JavadocQualifiedTypeReference qualifiedRef = (JavadocQualifiedTypeReference)typeRef;
                start2 = qualifiedRef.tagSourceStart;
                end2 = qualifiedRef.tagSourceEnd;
            }
            else {
                start2 = typeRef.sourceStart;
                end2 = typeRef.sourceEnd;
            }
            scope.problemReporter().javadocUnexpectedTag(start2, end2);
        }
        for (int seeTagsLength = (this.seeReferences == null) ? 0 : this.seeReferences.length, k = 0; k < seeTagsLength; ++k) {
            this.resolveReference(this.seeReferences[k], scope);
        }
        final boolean source15 = scope.compilerOptions().sourceLevel >= 3211264L;
        if (!source15 && this.valuePositions != -1L) {
            scope.problemReporter().javadocUnexpectedTag((int)(this.valuePositions >>> 32), (int)this.valuePositions);
        }
    }
    
    public void resolve(final CompilationUnitScope unitScope) {
        if ((this.bits & 0x10000) == 0x0) {
            return;
        }
    }
    
    public void resolve(final MethodScope methScope) {
        if ((this.bits & 0x10000) == 0x0) {
            return;
        }
        final AbstractMethodDeclaration methDecl = methScope.referenceMethod();
        final boolean overriding = methDecl != null && methDecl.binding != null && (!methDecl.binding.isStatic() && (methDecl.binding.modifiers & 0x30000000) != 0x0);
        final int seeTagsLength = (this.seeReferences == null) ? 0 : this.seeReferences.length;
        boolean superRef = false;
        for (int i = 0; i < seeTagsLength; ++i) {
            this.resolveReference(this.seeReferences[i], methScope);
            if (methDecl != null && !superRef) {
                if (!methDecl.isConstructor()) {
                    if (overriding && this.seeReferences[i] instanceof JavadocMessageSend) {
                        final JavadocMessageSend messageSend = (JavadocMessageSend)this.seeReferences[i];
                        if (messageSend.binding != null && messageSend.binding.isValidBinding() && messageSend.actualReceiverType instanceof ReferenceBinding) {
                            final ReferenceBinding methodReceiverType = (ReferenceBinding)messageSend.actualReceiverType;
                            final TypeBinding superType = methDecl.binding.declaringClass.findSuperTypeOriginatingFrom(methodReceiverType);
                            if (superType != null && TypeBinding.notEquals(superType.original(), methDecl.binding.declaringClass) && CharOperation.equals(messageSend.selector, methDecl.selector) && methScope.environment().methodVerifier().doesMethodOverride(methDecl.binding, messageSend.binding.original())) {
                                superRef = true;
                            }
                        }
                    }
                }
                else if (this.seeReferences[i] instanceof JavadocAllocationExpression) {
                    final JavadocAllocationExpression allocationExpr = (JavadocAllocationExpression)this.seeReferences[i];
                    if (allocationExpr.binding != null && allocationExpr.binding.isValidBinding()) {
                        final ReferenceBinding allocType = (ReferenceBinding)allocationExpr.resolvedType.original();
                        final ReferenceBinding superType2 = (ReferenceBinding)methDecl.binding.declaringClass.findSuperTypeOriginatingFrom(allocType);
                        if (superType2 != null && TypeBinding.notEquals(superType2.original(), methDecl.binding.declaringClass)) {
                            final MethodBinding superConstructor = methScope.getConstructor(superType2, methDecl.binding.parameters, allocationExpr);
                            if (superConstructor.isValidBinding() && superConstructor.original() == allocationExpr.binding.original()) {
                                MethodBinding current = methDecl.binding;
                                if (methScope.compilerOptions().sourceLevel >= 3407872L && current.typeVariables != Binding.NO_TYPE_VARIABLES) {
                                    current = current.asRawMethod(methScope.environment());
                                }
                                if (superConstructor.areParametersEqual(current)) {
                                    superRef = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!superRef && methDecl != null && methDecl.annotations != null) {
            for (int length = methDecl.annotations.length, j = 0; j < length && !superRef; superRef = ((methDecl.binding.tagBits & 0x2000000000000L) != 0x0L), ++j) {}
        }
        final boolean reportMissing = methDecl == null || ((!overriding || this.inheritedPositions == null) && !superRef && (methDecl.binding.declaringClass == null || !methDecl.binding.declaringClass.isLocalType()));
        if (!overriding && this.inheritedPositions != null) {
            for (int length2 = this.inheritedPositions.length, k = 0; k < length2; ++k) {
                final int start = (int)(this.inheritedPositions[k] >>> 32);
                final int end = (int)this.inheritedPositions[k];
                methScope.problemReporter().javadocUnexpectedTag(start, end);
            }
        }
        final CompilerOptions compilerOptions = methScope.compilerOptions();
        this.resolveParamTags(methScope, reportMissing, compilerOptions.reportUnusedParameterIncludeDocCommentReference);
        this.resolveTypeParameterTags(methScope, reportMissing && compilerOptions.reportMissingJavadocTagsMethodTypeParameters);
        if (this.returnStatement == null) {
            if (reportMissing && methDecl != null && methDecl.isMethod()) {
                final MethodDeclaration meth = (MethodDeclaration)methDecl;
                if (meth.binding.returnType != TypeBinding.VOID) {
                    methScope.problemReporter().javadocMissingReturnTag(meth.returnType.sourceStart, meth.returnType.sourceEnd, methDecl.binding.modifiers);
                }
            }
        }
        else {
            this.returnStatement.resolve(methScope);
        }
        this.resolveThrowsTags(methScope, reportMissing);
        final boolean source15 = compilerOptions.sourceLevel >= 3211264L;
        if (!source15 && methDecl != null && this.valuePositions != -1L) {
            methScope.problemReporter().javadocUnexpectedTag((int)(this.valuePositions >>> 32), (int)this.valuePositions);
        }
        for (int length3 = (this.invalidParameters == null) ? 0 : this.invalidParameters.length, l = 0; l < length3; ++l) {
            this.invalidParameters[l].resolve(methScope, false, false);
        }
    }
    
    private void resolveReference(final Expression reference, final Scope scope) {
        final int problemCount = scope.referenceContext().compilationResult().problemCount;
        switch (scope.kind) {
            case 2: {
                reference.resolveType((BlockScope)scope);
                break;
            }
            case 3: {
                reference.resolveType((ClassScope)scope);
                break;
            }
        }
        final boolean hasProblems = scope.referenceContext().compilationResult().problemCount > problemCount;
        final boolean source15 = scope.compilerOptions().sourceLevel >= 3211264L;
        int scopeModifiers = -1;
        if (reference instanceof JavadocFieldReference) {
            final JavadocFieldReference fieldRef = (JavadocFieldReference)reference;
            if (fieldRef.methodBinding != null) {
                if (fieldRef.tagValue == 10) {
                    if (scopeModifiers == -1) {
                        scopeModifiers = scope.getDeclarationModifiers();
                    }
                    scope.problemReporter().javadocInvalidValueReference(fieldRef.sourceStart, fieldRef.sourceEnd, scopeModifiers);
                }
                else if (fieldRef.actualReceiverType != null) {
                    if (scope.enclosingSourceType().isCompatibleWith(fieldRef.actualReceiverType)) {
                        final JavadocFieldReference javadocFieldReference = fieldRef;
                        javadocFieldReference.bits |= 0x4000;
                    }
                    final ReferenceBinding resolvedType = (ReferenceBinding)fieldRef.actualReceiverType;
                    if (CharOperation.equals(resolvedType.sourceName(), fieldRef.token)) {
                        fieldRef.methodBinding = scope.getConstructor(resolvedType, Binding.NO_TYPES, fieldRef);
                    }
                    else {
                        fieldRef.methodBinding = scope.findMethod(resolvedType, fieldRef.token, Binding.NO_TYPES, fieldRef, false);
                    }
                }
            }
            else if (source15 && fieldRef.binding != null && fieldRef.binding.isValidBinding() && fieldRef.tagValue == 10 && !fieldRef.binding.isStatic()) {
                if (scopeModifiers == -1) {
                    scopeModifiers = scope.getDeclarationModifiers();
                }
                scope.problemReporter().javadocInvalidValueReference(fieldRef.sourceStart, fieldRef.sourceEnd, scopeModifiers);
            }
            if (!hasProblems && fieldRef.binding != null && fieldRef.binding.isValidBinding() && fieldRef.actualReceiverType instanceof ReferenceBinding) {
                final ReferenceBinding resolvedType = (ReferenceBinding)fieldRef.actualReceiverType;
                this.verifyTypeReference(fieldRef, fieldRef.receiver, scope, source15, resolvedType, fieldRef.binding.modifiers);
            }
            return;
        }
        if (!hasProblems && (reference instanceof JavadocSingleTypeReference || reference instanceof JavadocQualifiedTypeReference) && reference.resolvedType instanceof ReferenceBinding) {
            final ReferenceBinding resolvedType2 = (ReferenceBinding)reference.resolvedType;
            this.verifyTypeReference(reference, reference, scope, source15, resolvedType2, resolvedType2.modifiers);
        }
        if (reference instanceof JavadocMessageSend) {
            final JavadocMessageSend msgSend = (JavadocMessageSend)reference;
            if (source15 && msgSend.tagValue == 10) {
                if (scopeModifiers == -1) {
                    scopeModifiers = scope.getDeclarationModifiers();
                }
                scope.problemReporter().javadocInvalidValueReference(msgSend.sourceStart, msgSend.sourceEnd, scopeModifiers);
            }
            if (!hasProblems && msgSend.binding != null && msgSend.binding.isValidBinding() && msgSend.actualReceiverType instanceof ReferenceBinding) {
                final ReferenceBinding resolvedType = (ReferenceBinding)msgSend.actualReceiverType;
                this.verifyTypeReference(msgSend, msgSend.receiver, scope, source15, resolvedType, msgSend.binding.modifiers);
            }
        }
        else if (reference instanceof JavadocAllocationExpression) {
            final JavadocAllocationExpression alloc = (JavadocAllocationExpression)reference;
            if (source15 && alloc.tagValue == 10) {
                if (scopeModifiers == -1) {
                    scopeModifiers = scope.getDeclarationModifiers();
                }
                scope.problemReporter().javadocInvalidValueReference(alloc.sourceStart, alloc.sourceEnd, scopeModifiers);
            }
            if (!hasProblems && alloc.binding != null && alloc.binding.isValidBinding() && alloc.resolvedType instanceof ReferenceBinding) {
                final ReferenceBinding resolvedType = (ReferenceBinding)alloc.resolvedType;
                this.verifyTypeReference(alloc, alloc.type, scope, source15, resolvedType, alloc.binding.modifiers);
            }
        }
        else if (reference instanceof JavadocSingleTypeReference && reference.resolvedType != null && reference.resolvedType.isTypeVariable()) {
            scope.problemReporter().javadocInvalidReference(reference.sourceStart, reference.sourceEnd);
        }
    }
    
    private void resolveParamTags(final MethodScope scope, final boolean reportMissing, final boolean considerParamRefAsUsage) {
        final AbstractMethodDeclaration methodDecl = scope.referenceMethod();
        final int paramTagsSize = (this.paramReferences == null) ? 0 : this.paramReferences.length;
        if (methodDecl == null) {
            for (int i = 0; i < paramTagsSize; ++i) {
                final JavadocSingleNameReference param = this.paramReferences[i];
                scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
            }
            return;
        }
        final int argumentsSize = (methodDecl.arguments == null) ? 0 : methodDecl.arguments.length;
        if (paramTagsSize == 0) {
            if (reportMissing) {
                for (int j = 0; j < argumentsSize; ++j) {
                    final Argument arg = methodDecl.arguments[j];
                    scope.problemReporter().javadocMissingParamTag(arg.name, arg.sourceStart, arg.sourceEnd, methodDecl.binding.modifiers);
                }
            }
        }
        else {
            final LocalVariableBinding[] bindings = new LocalVariableBinding[paramTagsSize];
            int maxBindings = 0;
            for (int k = 0; k < paramTagsSize; ++k) {
                final JavadocSingleNameReference param2 = this.paramReferences[k];
                param2.resolve(scope, true, considerParamRefAsUsage);
                if (param2.binding != null && param2.binding.isValidBinding()) {
                    boolean found = false;
                    for (int l = 0; l < maxBindings && !found; ++l) {
                        if (bindings[l] == param2.binding) {
                            scope.problemReporter().javadocDuplicatedParamTag(param2.token, param2.sourceStart, param2.sourceEnd, methodDecl.binding.modifiers);
                            found = true;
                        }
                    }
                    if (!found) {
                        bindings[maxBindings++] = (LocalVariableBinding)param2.binding;
                    }
                }
            }
            if (reportMissing) {
                for (int k = 0; k < argumentsSize; ++k) {
                    final Argument arg2 = methodDecl.arguments[k];
                    boolean found = false;
                    for (int l = 0; l < maxBindings && !found; ++l) {
                        final LocalVariableBinding binding = bindings[l];
                        if (arg2.binding == binding) {
                            found = true;
                        }
                    }
                    if (!found) {
                        scope.problemReporter().javadocMissingParamTag(arg2.name, arg2.sourceStart, arg2.sourceEnd, methodDecl.binding.modifiers);
                    }
                }
            }
        }
    }
    
    private void resolveTypeParameterTags(final Scope scope, boolean reportMissing) {
        final int paramTypeParamLength = (this.paramTypeParameters == null) ? 0 : this.paramTypeParameters.length;
        TypeParameter[] parameters = null;
        TypeVariableBinding[] typeVariables = null;
        int modifiers = -1;
        switch (scope.kind) {
            case 2: {
                final AbstractMethodDeclaration methodDeclaration = ((MethodScope)scope).referenceMethod();
                if (methodDeclaration == null) {
                    for (int i = 0; i < paramTypeParamLength; ++i) {
                        final JavadocSingleTypeReference param = this.paramTypeParameters[i];
                        scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
                    }
                    return;
                }
                parameters = methodDeclaration.typeParameters();
                typeVariables = methodDeclaration.binding.typeVariables;
                modifiers = methodDeclaration.binding.modifiers;
                break;
            }
            case 3: {
                final TypeDeclaration typeDeclaration = ((ClassScope)scope).referenceContext;
                parameters = typeDeclaration.typeParameters;
                typeVariables = typeDeclaration.binding.typeVariables;
                modifiers = typeDeclaration.binding.modifiers;
                break;
            }
        }
        if (typeVariables == null || typeVariables.length == 0) {
            for (int j = 0; j < paramTypeParamLength; ++j) {
                final JavadocSingleTypeReference param2 = this.paramTypeParameters[j];
                scope.problemReporter().javadocUnexpectedTag(param2.tagSourceStart, param2.tagSourceEnd);
            }
            return;
        }
        if (parameters != null) {
            reportMissing = (reportMissing && scope.compilerOptions().sourceLevel >= 3211264L);
            final int typeParametersLength = parameters.length;
            if (paramTypeParamLength == 0) {
                if (reportMissing) {
                    for (int i = 0, l = typeParametersLength; i < l; ++i) {
                        scope.problemReporter().javadocMissingParamTag(parameters[i].name, parameters[i].sourceStart, parameters[i].sourceEnd, modifiers);
                    }
                }
            }
            else if (typeVariables.length == typeParametersLength) {
                final TypeVariableBinding[] bindings = new TypeVariableBinding[paramTypeParamLength];
                for (int k = 0; k < paramTypeParamLength; ++k) {
                    final JavadocSingleTypeReference param3 = this.paramTypeParameters[k];
                    final TypeBinding paramBindind = param3.internalResolveType(scope, 0);
                    if (paramBindind != null && paramBindind.isValidBinding()) {
                        if (paramBindind.isTypeVariable()) {
                            if (scope.compilerOptions().reportUnusedParameterIncludeDocCommentReference) {
                                final TypeVariableBinding typeVariableBinding2;
                                final TypeVariableBinding typeVariableBinding = typeVariableBinding2 = (TypeVariableBinding)paramBindind;
                                typeVariableBinding2.modifiers |= 0x8000000;
                            }
                            boolean duplicate = false;
                            for (int m = 0; m < k && !duplicate; ++m) {
                                if (TypeBinding.equalsEquals(bindings[m], param3.resolvedType)) {
                                    scope.problemReporter().javadocDuplicatedParamTag(param3.token, param3.sourceStart, param3.sourceEnd, modifiers);
                                    duplicate = true;
                                }
                            }
                            if (!duplicate) {
                                bindings[k] = (TypeVariableBinding)param3.resolvedType;
                            }
                        }
                        else {
                            scope.problemReporter().javadocUndeclaredParamTagName(param3.token, param3.sourceStart, param3.sourceEnd, modifiers);
                        }
                    }
                }
                for (final TypeParameter parameter : parameters) {
                    boolean found = false;
                    for (int j2 = 0; j2 < paramTypeParamLength && !found; ++j2) {
                        if (TypeBinding.equalsEquals(parameter.binding, bindings[j2])) {
                            found = true;
                            bindings[j2] = null;
                        }
                    }
                    if (!found && reportMissing) {
                        scope.problemReporter().javadocMissingParamTag(parameter.name, parameter.sourceStart, parameter.sourceEnd, modifiers);
                    }
                }
                for (int k = 0; k < paramTypeParamLength; ++k) {
                    if (bindings[k] != null) {
                        final JavadocSingleTypeReference param3 = this.paramTypeParameters[k];
                        scope.problemReporter().javadocUndeclaredParamTagName(param3.token, param3.sourceStart, param3.sourceEnd, modifiers);
                    }
                }
            }
        }
    }
    
    private void resolveThrowsTags(final MethodScope methScope, final boolean reportMissing) {
        final AbstractMethodDeclaration md = methScope.referenceMethod();
        final int throwsTagsLength = (this.exceptionReferences == null) ? 0 : this.exceptionReferences.length;
        if (md == null) {
            for (int i = 0; i < throwsTagsLength; ++i) {
                final TypeReference typeRef = this.exceptionReferences[i];
                int start = typeRef.sourceStart;
                int end = typeRef.sourceEnd;
                if (typeRef instanceof JavadocQualifiedTypeReference) {
                    start = ((JavadocQualifiedTypeReference)typeRef).tagSourceStart;
                    end = ((JavadocQualifiedTypeReference)typeRef).tagSourceEnd;
                }
                else if (typeRef instanceof JavadocSingleTypeReference) {
                    start = ((JavadocSingleTypeReference)typeRef).tagSourceStart;
                    end = ((JavadocSingleTypeReference)typeRef).tagSourceEnd;
                }
                methScope.problemReporter().javadocUnexpectedTag(start, end);
            }
            return;
        }
        final int boundExceptionLength = (md.binding == null) ? 0 : md.binding.thrownExceptions.length;
        final int thrownExceptionLength = (md.thrownExceptions == null) ? 0 : md.thrownExceptions.length;
        if (throwsTagsLength == 0) {
            if (reportMissing) {
                for (int j = 0; j < boundExceptionLength; ++j) {
                    final ReferenceBinding exceptionBinding = md.binding.thrownExceptions[j];
                    if (exceptionBinding != null && exceptionBinding.isValidBinding()) {
                        int k;
                        for (k = j; k < thrownExceptionLength && TypeBinding.notEquals(exceptionBinding, md.thrownExceptions[k].resolvedType); ++k) {}
                        if (k < thrownExceptionLength) {
                            methScope.problemReporter().javadocMissingThrowsTag(md.thrownExceptions[k], md.binding.modifiers);
                        }
                    }
                }
            }
        }
        else {
            int maxRef = 0;
            final TypeReference[] typeReferences = new TypeReference[throwsTagsLength];
            for (int l = 0; l < throwsTagsLength; ++l) {
                final TypeReference typeRef2 = this.exceptionReferences[l];
                typeRef2.resolve(methScope);
                final TypeBinding typeBinding = typeRef2.resolvedType;
                if (typeBinding != null && typeBinding.isValidBinding() && typeBinding.isClass()) {
                    typeReferences[maxRef++] = typeRef2;
                }
            }
            for (int l = 0; l < boundExceptionLength; ++l) {
                ReferenceBinding exceptionBinding2 = md.binding.thrownExceptions[l];
                if (exceptionBinding2 != null) {
                    exceptionBinding2 = (ReferenceBinding)exceptionBinding2.erasure();
                }
                boolean found = false;
                for (int m = 0; m < maxRef && !found; ++m) {
                    if (typeReferences[m] != null) {
                        final TypeBinding typeBinding2 = typeReferences[m].resolvedType;
                        if (TypeBinding.equalsEquals(exceptionBinding2, typeBinding2)) {
                            found = true;
                            typeReferences[m] = null;
                        }
                    }
                }
                if (!found && reportMissing && exceptionBinding2 != null && exceptionBinding2.isValidBinding()) {
                    int k2;
                    for (k2 = l; k2 < thrownExceptionLength && TypeBinding.notEquals(exceptionBinding2, md.thrownExceptions[k2].resolvedType); ++k2) {}
                    if (k2 < thrownExceptionLength) {
                        methScope.problemReporter().javadocMissingThrowsTag(md.thrownExceptions[k2], md.binding.modifiers);
                    }
                }
            }
            for (int l = 0; l < maxRef; ++l) {
                final TypeReference typeRef2 = typeReferences[l];
                if (typeRef2 != null) {
                    boolean compatible = false;
                    for (int m = 0; m < thrownExceptionLength && !compatible; ++m) {
                        final TypeBinding exceptionBinding3 = md.thrownExceptions[m].resolvedType;
                        if (exceptionBinding3 != null) {
                            compatible = typeRef2.resolvedType.isCompatibleWith(exceptionBinding3);
                        }
                    }
                    if (!compatible && !typeRef2.resolvedType.isUncheckedException(false)) {
                        methScope.problemReporter().javadocInvalidThrowsClassName(typeRef2, md.binding.modifiers);
                    }
                }
            }
        }
    }
    
    private void verifyTypeReference(final Expression reference, final Expression typeReference, final Scope scope, final boolean source15, final ReferenceBinding resolvedType, final int modifiers) {
        if (resolvedType.isValidBinding()) {
            int scopeModifiers = -1;
            if (!this.canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, modifiers)) {
                scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, reference.sourceEnd, scope, modifiers);
                return;
            }
            if (reference != typeReference && !this.canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, resolvedType.modifiers)) {
                scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, typeReference.sourceEnd, scope, resolvedType.modifiers);
                return;
            }
            if (resolvedType.isMemberType()) {
                ReferenceBinding topLevelType = resolvedType;
                final int packageLength = topLevelType.fPackage.compoundName.length;
                final int depth = resolvedType.depth();
                int idx = depth + packageLength;
                final char[][] computedCompoundName = new char[idx + 1][];
                computedCompoundName[idx] = topLevelType.sourceName;
                while (topLevelType.enclosingType() != null) {
                    topLevelType = topLevelType.enclosingType();
                    computedCompoundName[--idx] = topLevelType.sourceName;
                }
                int i = packageLength;
                while (--i >= 0) {
                    computedCompoundName[--idx] = topLevelType.fPackage.compoundName[i];
                }
                ClassScope topLevelScope = scope.classScope();
                if (topLevelScope.parent.kind != 4 || !CharOperation.equals(topLevelType.sourceName, topLevelScope.referenceContext.name)) {
                    topLevelScope = topLevelScope.outerMostClassScope();
                    if (typeReference instanceof JavadocSingleTypeReference && ((!source15 && depth == 1) || TypeBinding.notEquals(topLevelType, topLevelScope.referenceContext.binding))) {
                        boolean hasValidImport = false;
                        if (!source15) {
                            if (scopeModifiers == -1) {
                                scopeModifiers = scope.getDeclarationModifiers();
                            }
                            scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
                            return;
                        }
                        final CompilationUnitScope unitScope = topLevelScope.compilationUnitScope();
                        final ImportBinding[] imports = unitScope.imports;
                    Label_0467:
                        for (int length = (imports == null) ? 0 : imports.length, j = 0; j < length; ++j) {
                            final char[][] compoundName = imports[j].compoundName;
                            final int compoundNameLength = compoundName.length;
                            if ((imports[j].onDemand && compoundNameLength == computedCompoundName.length - 1) || compoundNameLength == computedCompoundName.length) {
                                int k = compoundNameLength;
                                while (--k >= 0 && CharOperation.equals(imports[j].compoundName[k], computedCompoundName[k])) {
                                    if (k == 0) {
                                        hasValidImport = true;
                                        final ImportReference importReference = imports[j].reference;
                                        if (importReference != null) {
                                            final ImportReference importReference2 = importReference;
                                            importReference2.bits |= 0x2;
                                            break Label_0467;
                                        }
                                        break Label_0467;
                                    }
                                }
                            }
                        }
                        if (!hasValidImport) {
                            if (scopeModifiers == -1) {
                                scopeModifiers = scope.getDeclarationModifiers();
                            }
                            scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
                        }
                    }
                }
                if (typeReference instanceof JavadocQualifiedTypeReference && !scope.isDefinedInSameUnit(resolvedType)) {
                    final char[][] typeRefName = ((JavadocQualifiedTypeReference)typeReference).getTypeName();
                    int skipLength = 0;
                    if (topLevelScope.getCurrentPackage() == resolvedType.getPackage() && typeRefName.length < computedCompoundName.length) {
                        skipLength = resolvedType.fPackage.compoundName.length;
                    }
                    boolean valid = true;
                    if (typeRefName.length == computedCompoundName.length - skipLength) {
                        for (int l = 0; l < typeRefName.length; ++l) {
                            if (!CharOperation.equals(typeRefName[l], computedCompoundName[l + skipLength])) {
                                valid = false;
                                break;
                            }
                        }
                    }
                    else {
                        valid = false;
                    }
                    if (!valid) {
                        if (scopeModifiers == -1) {
                            scopeModifiers = scope.getDeclarationModifiers();
                        }
                        scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
                        return;
                    }
                }
            }
            if (scope.referenceCompilationUnit().isPackageInfo() && typeReference instanceof JavadocSingleTypeReference && resolvedType.fPackage.compoundName.length > 0) {
                scope.problemReporter().javadocInvalidReference(typeReference.sourceStart, typeReference.sourceEnd);
            }
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.paramReferences != null) {
                for (int i = 0, length = this.paramReferences.length; i < length; ++i) {
                    this.paramReferences[i].traverse(visitor, scope);
                }
            }
            if (this.paramTypeParameters != null) {
                for (int i = 0, length = this.paramTypeParameters.length; i < length; ++i) {
                    this.paramTypeParameters[i].traverse(visitor, scope);
                }
            }
            if (this.returnStatement != null) {
                this.returnStatement.traverse(visitor, scope);
            }
            if (this.exceptionReferences != null) {
                for (int i = 0, length = this.exceptionReferences.length; i < length; ++i) {
                    this.exceptionReferences[i].traverse(visitor, scope);
                }
            }
            if (this.seeReferences != null) {
                for (int i = 0, length = this.seeReferences.length; i < length; ++i) {
                    this.seeReferences[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.paramReferences != null) {
                for (int i = 0, length = this.paramReferences.length; i < length; ++i) {
                    this.paramReferences[i].traverse(visitor, scope);
                }
            }
            if (this.paramTypeParameters != null) {
                for (int i = 0, length = this.paramTypeParameters.length; i < length; ++i) {
                    this.paramTypeParameters[i].traverse(visitor, scope);
                }
            }
            if (this.returnStatement != null) {
                this.returnStatement.traverse(visitor, scope);
            }
            if (this.exceptionReferences != null) {
                for (int i = 0, length = this.exceptionReferences.length; i < length; ++i) {
                    this.exceptionReferences[i].traverse(visitor, scope);
                }
            }
            if (this.seeReferences != null) {
                for (int i = 0, length = this.seeReferences.length; i < length; ++i) {
                    this.seeReferences[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}
