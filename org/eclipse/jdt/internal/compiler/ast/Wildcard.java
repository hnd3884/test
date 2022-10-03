package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.core.compiler.CharOperation;

public class Wildcard extends SingleTypeReference
{
    public static final int UNBOUND = 0;
    public static final int EXTENDS = 1;
    public static final int SUPER = 2;
    public TypeReference bound;
    public int kind;
    
    public Wildcard(final int kind) {
        super(Wildcard.WILDCARD_NAME, 0L);
        this.kind = kind;
    }
    
    @Override
    public char[][] getParameterizedTypeName() {
        switch (this.kind) {
            case 0: {
                return new char[][] { Wildcard.WILDCARD_NAME };
            }
            case 1: {
                return new char[][] { CharOperation.concat(Wildcard.WILDCARD_NAME, Wildcard.WILDCARD_EXTENDS, CharOperation.concatWith(this.bound.getParameterizedTypeName(), '.')) };
            }
            default: {
                return new char[][] { CharOperation.concat(Wildcard.WILDCARD_NAME, Wildcard.WILDCARD_SUPER, CharOperation.concatWith(this.bound.getParameterizedTypeName(), '.')) };
            }
        }
    }
    
    @Override
    public char[][] getTypeName() {
        switch (this.kind) {
            case 0: {
                return new char[][] { Wildcard.WILDCARD_NAME };
            }
            case 1: {
                return new char[][] { CharOperation.concat(Wildcard.WILDCARD_NAME, Wildcard.WILDCARD_EXTENDS, CharOperation.concatWith(this.bound.getTypeName(), '.')) };
            }
            default: {
                return new char[][] { CharOperation.concat(Wildcard.WILDCARD_NAME, Wildcard.WILDCARD_SUPER, CharOperation.concatWith(this.bound.getTypeName(), '.')) };
            }
        }
    }
    
    private TypeBinding internalResolveType(final Scope scope, final ReferenceBinding genericType, final int rank) {
        TypeBinding boundType = null;
        if (this.bound != null) {
            boundType = ((scope.kind == 3) ? this.bound.resolveType((ClassScope)scope, 256) : this.bound.resolveType((BlockScope)scope, true, 256));
            this.bits |= (this.bound.bits & 0x100000);
            if (boundType == null) {
                return null;
            }
        }
        this.resolvedType = scope.environment().createWildcard(genericType, rank, boundType, null, this.kind);
        this.resolveAnnotations(scope, 0);
        if (scope.environment().usesNullTypeAnnotations()) {
            ((WildcardBinding)this.resolvedType).evaluateNullAnnotations(scope, this);
        }
        return this.resolvedType;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        if (this.annotations != null && this.annotations[0] != null) {
            ASTNode.printAnnotations(this.annotations[0], output);
            output.append(' ');
        }
        switch (this.kind) {
            case 0: {
                output.append(Wildcard.WILDCARD_NAME);
                break;
            }
            case 1: {
                output.append(Wildcard.WILDCARD_NAME).append(Wildcard.WILDCARD_EXTENDS);
                this.bound.printExpression(0, output);
                break;
            }
            default: {
                output.append(Wildcard.WILDCARD_NAME).append(Wildcard.WILDCARD_SUPER);
                this.bound.printExpression(0, output);
                break;
            }
        }
        return output;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope, final boolean checkBounds, final int location) {
        if (this.bound != null) {
            this.bound.resolveType(scope, checkBounds, 256);
            this.bits |= (this.bound.bits & 0x100000);
        }
        return null;
    }
    
    @Override
    public TypeBinding resolveType(final ClassScope scope, final int location) {
        if (this.bound != null) {
            this.bound.resolveType(scope, 256);
            this.bits |= (this.bound.bits & 0x100000);
        }
        return null;
    }
    
    @Override
    public TypeBinding resolveTypeArgument(final BlockScope blockScope, final ReferenceBinding genericType, final int rank) {
        return this.internalResolveType(blockScope, genericType, rank);
    }
    
    @Override
    public TypeBinding resolveTypeArgument(final ClassScope classScope, final ReferenceBinding genericType, final int rank) {
        return this.internalResolveType(classScope, genericType, rank);
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
            if (this.bound != null) {
                this.bound.traverse(visitor, scope);
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
            if (this.bound != null) {
                this.bound.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public boolean isWildcard() {
        return true;
    }
}
