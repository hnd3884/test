package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;

public class ExtendedStringLiteral extends StringLiteral
{
    public ExtendedStringLiteral(final StringLiteral str, final CharLiteral character) {
        super(str.source, str.sourceStart, str.sourceEnd, str.lineNumber);
        this.extendWith(character);
    }
    
    public ExtendedStringLiteral(final StringLiteral str1, final StringLiteral str2) {
        super(str1.source, str1.sourceStart, str1.sourceEnd, str1.lineNumber);
        this.extendWith(str2);
    }
    
    @Override
    public ExtendedStringLiteral extendWith(final CharLiteral lit) {
        final int length = this.source.length;
        System.arraycopy(this.source, 0, this.source = new char[length + 1], 0, length);
        this.source[length] = lit.value;
        this.sourceEnd = lit.sourceEnd;
        return this;
    }
    
    @Override
    public ExtendedStringLiteral extendWith(final StringLiteral lit) {
        final int length = this.source.length;
        System.arraycopy(this.source, 0, this.source = new char[length + lit.source.length], 0, length);
        System.arraycopy(lit.source, 0, this.source, length, lit.source.length);
        this.sourceEnd = lit.sourceEnd;
        return this;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        return output.append("ExtendedStringLiteral{").append(this.source).append('}');
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
