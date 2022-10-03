package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;

public class StringLiteral extends Literal
{
    char[] source;
    int lineNumber;
    
    public StringLiteral(final char[] token, final int start, final int end, final int lineNumber) {
        this(start, end);
        this.source = token;
        this.lineNumber = lineNumber - 1;
    }
    
    public StringLiteral(final int s, final int e) {
        super(s, e);
    }
    
    @Override
    public void computeConstant() {
        this.constant = StringConstant.fromValue(String.valueOf(this.source));
    }
    
    public ExtendedStringLiteral extendWith(final CharLiteral lit) {
        return new ExtendedStringLiteral(this, lit);
    }
    
    public ExtendedStringLiteral extendWith(final StringLiteral lit) {
        return new ExtendedStringLiteral(this, lit);
    }
    
    public StringLiteralConcatenation extendsWith(final StringLiteral lit) {
        return new StringLiteralConcatenation(this, lit);
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (valueRequired) {
            codeStream.ldc(this.constant.stringValue());
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public TypeBinding literalType(final BlockScope scope) {
        return scope.getJavaLangString();
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        output.append('\"');
        for (int i = 0; i < this.source.length; ++i) {
            Util.appendEscapedChar(output, this.source[i], true);
        }
        output.append('\"');
        return output;
    }
    
    @Override
    public char[] source() {
        return this.source;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
