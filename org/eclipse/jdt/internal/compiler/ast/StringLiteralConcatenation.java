package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;

public class StringLiteralConcatenation extends StringLiteral
{
    private static final int INITIAL_SIZE = 5;
    public Expression[] literals;
    public int counter;
    
    public StringLiteralConcatenation(final StringLiteral str1, final StringLiteral str2) {
        super(str1.sourceStart, str1.sourceEnd);
        this.source = str1.source;
        this.literals = new StringLiteral[5];
        this.counter = 0;
        this.literals[this.counter++] = str1;
        this.extendsWith(str2);
    }
    
    @Override
    public StringLiteralConcatenation extendsWith(final StringLiteral lit) {
        this.sourceEnd = lit.sourceEnd;
        final int literalsLength = this.literals.length;
        if (this.counter == literalsLength) {
            System.arraycopy(this.literals, 0, this.literals = new StringLiteral[literalsLength + 5], 0, literalsLength);
        }
        final int length = this.source.length;
        System.arraycopy(this.source, 0, this.source = new char[length + lit.source.length], 0, length);
        System.arraycopy(lit.source, 0, this.source, length, lit.source.length);
        this.literals[this.counter++] = lit;
        return this;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        output.append("StringLiteralConcatenation{");
        for (int i = 0, max = this.counter; i < max; ++i) {
            this.literals[i].printExpression(indent, output);
            output.append("+\n");
        }
        return output.append('}');
    }
    
    @Override
    public char[] source() {
        return this.source;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            for (int i = 0, max = this.counter; i < max; ++i) {
                this.literals[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
