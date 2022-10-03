package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class SingleTypeReference extends TypeReference
{
    public char[] token;
    
    public SingleTypeReference(final char[] source, final long pos) {
        this.token = source;
        this.sourceStart = (int)(pos >>> 32);
        this.sourceEnd = (int)(pos & 0xFFFFFFFFL);
    }
    
    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(final int additionalDimensions, final Annotation[][] additionalAnnotations, final boolean isVarargs) {
        final int totalDimensions = this.dimensions() + additionalDimensions;
        final Annotation[][] allAnnotations = this.getMergedAnnotationsOnDimensions(additionalDimensions, additionalAnnotations);
        final ArrayTypeReference arrayTypeReference = new ArrayTypeReference(this.token, totalDimensions, allAnnotations, ((long)this.sourceStart << 32) + this.sourceEnd);
        arrayTypeReference.annotations = this.annotations;
        final ArrayTypeReference arrayTypeReference2 = arrayTypeReference;
        arrayTypeReference2.bits |= (this.bits & 0x100000);
        if (!isVarargs) {
            arrayTypeReference.extendedDimensions = additionalDimensions;
        }
        return arrayTypeReference;
    }
    
    @Override
    public char[] getLastToken() {
        return this.token;
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        this.resolvedType = scope.getType(this.token);
        if (this.resolvedType instanceof TypeVariableBinding) {
            final TypeVariableBinding typeVariable = (TypeVariableBinding)this.resolvedType;
            if (typeVariable.declaringElement instanceof SourceTypeBinding) {
                scope.tagAsAccessingEnclosingInstanceStateOf((ReferenceBinding)typeVariable.declaringElement, true);
            }
        }
        else if (this.resolvedType instanceof LocalTypeBinding) {
            final LocalTypeBinding localType = (LocalTypeBinding)this.resolvedType;
            final MethodScope methodScope = scope.methodScope();
            if (methodScope != null && !methodScope.isStatic) {
                methodScope.tagAsAccessingEnclosingInstanceStateOf(localType, false);
            }
        }
        if (scope.kind == 3 && this.resolvedType.isValidBinding() && ((ClassScope)scope).detectHierarchyCycle(this.resolvedType, this)) {
            return null;
        }
        return this.resolvedType;
    }
    
    @Override
    public char[][] getTypeName() {
        return new char[][] { this.token };
    }
    
    @Override
    public boolean isBaseTypeReference() {
        return this.token == SingleTypeReference.BYTE || this.token == SingleTypeReference.SHORT || this.token == SingleTypeReference.INT || this.token == SingleTypeReference.LONG || this.token == SingleTypeReference.FLOAT || this.token == SingleTypeReference.DOUBLE || this.token == SingleTypeReference.CHAR || this.token == SingleTypeReference.BOOLEAN || this.token == SingleTypeReference.NULL || this.token == SingleTypeReference.VOID;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        if (this.annotations != null && this.annotations[0] != null) {
            ASTNode.printAnnotations(this.annotations[0], output);
            output.append(' ');
        }
        return output.append(this.token);
    }
    
    public TypeBinding resolveTypeEnclosing(final BlockScope scope, final ReferenceBinding enclosingType) {
        this.resolvedType = scope.getMemberType(this.token, enclosingType);
        boolean hasError = false;
        this.resolveAnnotations(scope, 0);
        TypeBinding memberType = this.resolvedType;
        if (!memberType.isValidBinding()) {
            hasError = true;
            scope.problemReporter().invalidEnclosingType(this, memberType, enclosingType);
            memberType = memberType.closestMatch();
            if (memberType == null) {
                return null;
            }
        }
        if (this.isTypeUseDeprecated(memberType, scope)) {
            this.reportDeprecatedType(memberType, scope);
        }
        memberType = scope.environment().convertToRawType(memberType, false);
        if (memberType.isRawType() && (this.bits & 0x40000000) == 0x0 && scope.compilerOptions().getSeverity(536936448) != 256) {
            scope.problemReporter().rawTypeReference(this, memberType);
        }
        if (hasError) {
            return memberType;
        }
        return this.resolvedType = memberType;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope) && this.annotations != null) {
            final Annotation[] typeAnnotations = this.annotations[0];
            for (int i = 0, length = (typeAnnotations == null) ? 0 : typeAnnotations.length; i < length; ++i) {
                typeAnnotations[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope) && this.annotations != null) {
            final Annotation[] typeAnnotations = this.annotations[0];
            for (int i = 0, length = (typeAnnotations == null) ? 0 : typeAnnotations.length; i < length; ++i) {
                typeAnnotations[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
