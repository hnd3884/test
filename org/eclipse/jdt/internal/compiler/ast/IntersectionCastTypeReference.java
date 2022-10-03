package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class IntersectionCastTypeReference extends TypeReference
{
    public TypeReference[] typeReferences;
    
    public IntersectionCastTypeReference(final TypeReference[] typeReferences) {
        this.typeReferences = typeReferences;
        this.sourceStart = typeReferences[0].sourceStart;
        final int length = typeReferences.length;
        this.sourceEnd = typeReferences[length - 1].sourceEnd;
        for (int i = 0, max = typeReferences.length; i < max; ++i) {
            if ((typeReferences[i].bits & 0x100000) != 0x0) {
                this.bits |= 0x100000;
                break;
            }
        }
    }
    
    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(final int additionalDimensions, final Annotation[][] additionalAnnotations, final boolean isVarargs) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public char[] getLastToken() {
        return null;
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        return null;
    }
    
    @Override
    public TypeReference[] getTypeReferences() {
        return this.typeReferences;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope, final boolean checkBounds, final int location) {
        final int length = this.typeReferences.length;
        ReferenceBinding[] intersectingTypes = new ReferenceBinding[length];
        boolean hasError = false;
        int typeCount = 0;
    Label_0264:
        for (int i = 0; i < length; ++i) {
            final TypeReference typeReference = this.typeReferences[i];
            final TypeBinding type = typeReference.resolveType(scope, checkBounds, location);
            if (type == null || (type.tagBits & 0x80L) != 0x0L) {
                hasError = true;
            }
            else {
                if (i == 0) {
                    if (type.isBaseType()) {
                        scope.problemReporter().onlyReferenceTypesInIntersectionCast(typeReference);
                        hasError = true;
                        continue;
                    }
                    if (type.isArrayType()) {
                        scope.problemReporter().illegalArrayTypeInIntersectionCast(typeReference);
                        hasError = true;
                        continue;
                    }
                }
                else if (!type.isInterface()) {
                    scope.problemReporter().boundMustBeAnInterface(typeReference, type);
                    hasError = true;
                    continue;
                }
                for (int j = 0; j < typeCount; ++j) {
                    final ReferenceBinding priorType = intersectingTypes[j];
                    if (TypeBinding.equalsEquals(priorType, type)) {
                        scope.problemReporter().duplicateBoundInIntersectionCast(typeReference);
                        hasError = true;
                    }
                    else if (priorType.isInterface()) {
                        if (TypeBinding.equalsEquals(type.findSuperTypeOriginatingFrom(priorType), priorType)) {
                            intersectingTypes[j] = (ReferenceBinding)type;
                            continue Label_0264;
                        }
                        if (TypeBinding.equalsEquals(priorType.findSuperTypeOriginatingFrom(type), type)) {
                            continue Label_0264;
                        }
                    }
                }
                intersectingTypes[typeCount++] = (ReferenceBinding)type;
            }
        }
        if (hasError) {
            return null;
        }
        if (typeCount != length) {
            if (typeCount == 1) {
                return this.resolvedType = intersectingTypes[0];
            }
            System.arraycopy(intersectingTypes, 0, intersectingTypes = new ReferenceBinding[typeCount], 0, typeCount);
        }
        final IntersectionTypeBinding18 intersectionType = (IntersectionTypeBinding18)scope.environment().createIntersectionType18(intersectingTypes);
        ReferenceBinding itsSuperclass = null;
        ReferenceBinding[] interfaces = intersectingTypes;
        final ReferenceBinding firstType = intersectingTypes[0];
        if (firstType.isClass()) {
            itsSuperclass = firstType.superclass();
            System.arraycopy(intersectingTypes, 1, interfaces = new ReferenceBinding[typeCount - 1], 0, typeCount - 1);
        }
        final Map invocations = new HashMap(2);
        for (int k = 0, interfaceCount = interfaces.length; k < interfaceCount; ++k) {
            final ReferenceBinding one = interfaces[k];
            if (one != null) {
                if (itsSuperclass == null || !scope.hasErasedCandidatesCollisions(itsSuperclass, one, invocations, intersectionType, this)) {
                    for (final ReferenceBinding two : interfaces) {
                        if (two != null) {
                            if (scope.hasErasedCandidatesCollisions(one, two, invocations, intersectionType, this)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        if ((intersectionType.tagBits & 0x20000L) != 0x0L) {
            return null;
        }
        return this.resolvedType = intersectionType;
    }
    
    @Override
    public char[][] getTypeName() {
        return this.typeReferences[0].getTypeName();
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            for (int length = (this.typeReferences == null) ? 0 : this.typeReferences.length, i = 0; i < length; ++i) {
                this.typeReferences[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        throw new UnsupportedOperationException("Unexpected traversal request: IntersectionTypeReference in class scope");
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        final int length = (this.typeReferences == null) ? 0 : this.typeReferences.length;
        ASTNode.printIndent(indent, output);
        for (int i = 0; i < length; ++i) {
            this.typeReferences[i].printExpression(0, output);
            if (i != length - 1) {
                output.append(" & ");
            }
        }
        return output;
    }
}
