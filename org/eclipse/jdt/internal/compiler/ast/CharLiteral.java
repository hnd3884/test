package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.compiler.impl.CharConstant;

public class CharLiteral extends NumberLiteral
{
    char value;
    
    public CharLiteral(final char[] token, final int s, final int e) {
        super(token, s, e);
        this.computeValue();
    }
    
    @Override
    public void computeConstant() {
        this.constant = CharConstant.fromValue(this.value);
    }
    
    private void computeValue() {
        final char value = this.source[1];
        this.value = value;
        if (value != '\\') {
            return;
        }
        char digit;
        switch (digit = this.source[2]) {
            case 'b': {
                this.value = '\b';
                break;
            }
            case 't': {
                this.value = '\t';
                break;
            }
            case 'n': {
                this.value = '\n';
                break;
            }
            case 'f': {
                this.value = '\f';
                break;
            }
            case 'r': {
                this.value = '\r';
                break;
            }
            case '\"': {
                this.value = '\"';
                break;
            }
            case '\'': {
                this.value = '\'';
                break;
            }
            case '\\': {
                this.value = '\\';
                break;
            }
            default: {
                int number = ScannerHelper.getNumericValue(digit);
                if ((digit = this.source[3]) != '\'') {
                    number = number * 8 + ScannerHelper.getNumericValue(digit);
                    if ((digit = this.source[4]) != '\'') {
                        number = number * 8 + ScannerHelper.getNumericValue(digit);
                    }
                    this.value = (char)number;
                    break;
                }
                final char c = (char)number;
                this.value = c;
                this.constant = CharConstant.fromValue(c);
                break;
            }
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (valueRequired) {
            codeStream.generateConstant(this.constant, this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public TypeBinding literalType(final BlockScope scope) {
        return TypeBinding.CHAR;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
}
