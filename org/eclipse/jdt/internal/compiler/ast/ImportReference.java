package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;

public class ImportReference extends ASTNode
{
    public char[][] tokens;
    public long[] sourcePositions;
    public int declarationEnd;
    public int declarationSourceStart;
    public int declarationSourceEnd;
    public int modifiers;
    public Annotation[] annotations;
    public int trailingStarPosition;
    
    public ImportReference(final char[][] tokens, final long[] sourcePositions, final boolean onDemand, final int modifiers) {
        this.tokens = tokens;
        this.sourcePositions = sourcePositions;
        if (onDemand) {
            this.bits |= 0x20000;
        }
        this.sourceEnd = (int)(sourcePositions[sourcePositions.length - 1] & -1L);
        this.sourceStart = (int)(sourcePositions[0] >>> 32);
        this.modifiers = modifiers;
    }
    
    public boolean isStatic() {
        return (this.modifiers & 0x8) != 0x0;
    }
    
    public char[][] getImportName() {
        return this.tokens;
    }
    
    @Override
    public StringBuffer print(final int indent, final StringBuffer output) {
        return this.print(indent, output, true);
    }
    
    public StringBuffer print(final int tab, final StringBuffer output, final boolean withOnDemand) {
        for (int i = 0; i < this.tokens.length; ++i) {
            if (i > 0) {
                output.append('.');
            }
            output.append(this.tokens[i]);
        }
        if (withOnDemand && (this.bits & 0x20000) != 0x0) {
            output.append(".*");
        }
        return output;
    }
    
    public void traverse(final ASTVisitor visitor, final CompilationUnitScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
