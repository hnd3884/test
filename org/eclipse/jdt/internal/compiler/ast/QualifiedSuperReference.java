package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class QualifiedSuperReference extends QualifiedThisReference
{
    public QualifiedSuperReference(final TypeReference name, final int pos, final int sourceEnd) {
        super(name, pos, sourceEnd);
    }
    
    @Override
    public boolean isSuper() {
        return true;
    }
    
    @Override
    public boolean isQualifiedSuper() {
        return true;
    }
    
    @Override
    public boolean isThis() {
        return false;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        return this.qualification.print(0, output).append(".super");
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        if ((this.bits & 0x1FE00000) != 0x0) {
            scope.problemReporter().invalidParenthesizedExpression(this);
            return null;
        }
        super.resolveType(scope);
        if (this.resolvedType != null && !this.resolvedType.isValidBinding()) {
            scope.problemReporter().illegalSuperAccess(this.qualification.resolvedType, this.resolvedType, this);
            return null;
        }
        if (this.currentCompatibleType == null) {
            return null;
        }
        if (this.currentCompatibleType.id == 1) {
            scope.problemReporter().cannotUseSuperInJavaLangObject(this);
            return null;
        }
        return this.resolvedType = (this.currentCompatibleType.isInterface() ? this.currentCompatibleType : this.currentCompatibleType.superclass());
    }
    
    @Override
    int findCompatibleEnclosing(final ReferenceBinding enclosingType, final TypeBinding type, final BlockScope scope) {
        if (type.isInterface()) {
            final CompilerOptions compilerOptions = scope.compilerOptions();
            final ReferenceBinding[] supers = enclosingType.superInterfaces();
            final int length = supers.length;
            final boolean isJava8 = compilerOptions.complianceLevel >= 3407872L;
            boolean isLegal = true;
            char[][] compoundName = null;
            ReferenceBinding closestMatch = null;
            for (int i = 0; i < length; ++i) {
                if (TypeBinding.equalsEquals(supers[i].erasure(), type)) {
                    closestMatch = (this.currentCompatibleType = supers[i]);
                }
                else if (supers[i].erasure().isCompatibleWith(type)) {
                    isLegal = false;
                    compoundName = supers[i].compoundName;
                    if (closestMatch == null) {
                        closestMatch = supers[i];
                    }
                }
            }
            if (!isLegal || !isJava8) {
                this.currentCompatibleType = null;
                this.resolvedType = new ProblemReferenceBinding(compoundName, closestMatch, isJava8 ? 21 : 29);
            }
            return 0;
        }
        return super.findCompatibleEnclosing(enclosingType, type, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.qualification.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.qualification.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}
