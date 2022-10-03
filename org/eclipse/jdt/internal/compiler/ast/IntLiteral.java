package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;

public class IntLiteral extends NumberLiteral
{
    private static final char[] HEXA_MIN_VALUE;
    private static final char[] HEXA_MINUS_ONE_VALUE;
    private static final char[] OCTAL_MIN_VALUE;
    private static final char[] OCTAL_MINUS_ONE_VALUE;
    private static final char[] DECIMAL_MIN_VALUE;
    private static final char[] DECIMAL_MAX_VALUE;
    private char[] reducedForm;
    public int value;
    public static final IntLiteral One;
    
    static {
        HEXA_MIN_VALUE = "0x80000000".toCharArray();
        HEXA_MINUS_ONE_VALUE = "0xffffffff".toCharArray();
        OCTAL_MIN_VALUE = "020000000000".toCharArray();
        OCTAL_MINUS_ONE_VALUE = "037777777777".toCharArray();
        DECIMAL_MIN_VALUE = "2147483648".toCharArray();
        DECIMAL_MAX_VALUE = "2147483647".toCharArray();
        One = new IntLiteral(new char[] { '1' }, null, 0, 0, 1, IntConstant.fromValue(1));
    }
    
    public static IntLiteral buildIntLiteral(final char[] token, final int s, final int e) {
        final char[] intReducedToken = NumberLiteral.removePrefixZerosAndUnderscores(token, false);
        switch (intReducedToken.length) {
            case 10: {
                if (CharOperation.equals(intReducedToken, IntLiteral.HEXA_MIN_VALUE)) {
                    return new IntLiteralMinValue(token, (char[])((intReducedToken != token) ? intReducedToken : null), s, e);
                }
                break;
            }
            case 12: {
                if (CharOperation.equals(intReducedToken, IntLiteral.OCTAL_MIN_VALUE)) {
                    return new IntLiteralMinValue(token, (char[])((intReducedToken != token) ? intReducedToken : null), s, e);
                }
                break;
            }
        }
        return new IntLiteral(token, (char[])((intReducedToken != token) ? intReducedToken : null), s, e);
    }
    
    IntLiteral(final char[] token, final char[] reducedForm, final int start, final int end) {
        super(token, start, end);
        this.reducedForm = reducedForm;
    }
    
    IntLiteral(final char[] token, final char[] reducedForm, final int start, final int end, final int value, final Constant constant) {
        super(token, start, end);
        this.reducedForm = reducedForm;
        this.value = value;
        this.constant = constant;
    }
    
    @Override
    public void computeConstant() {
        final char[] token = (this.reducedForm != null) ? this.reducedForm : this.source;
        final int tokenLength = token.length;
        int radix = 10;
        int j = 0;
        if (token[0] == '0') {
            if (tokenLength == 1) {
                this.constant = IntConstant.fromValue(0);
                return;
            }
            if (token[1] == 'x' || token[1] == 'X') {
                radix = 16;
                j = 2;
            }
            else if (token[1] == 'b' || token[1] == 'B') {
                radix = 2;
                j = 2;
            }
            else {
                radix = 8;
                j = 1;
            }
        }
        switch (radix) {
            case 2: {
                if (tokenLength - 2 > 32) {
                    return;
                }
                this.computeValue(token, tokenLength, radix, j);
                return;
            }
            case 16: {
                if (tokenLength > 10) {
                    break;
                }
                if (CharOperation.equals(token, IntLiteral.HEXA_MINUS_ONE_VALUE)) {
                    this.constant = IntConstant.fromValue(-1);
                    return;
                }
                this.computeValue(token, tokenLength, radix, j);
                return;
            }
            case 10: {
                if (tokenLength > IntLiteral.DECIMAL_MAX_VALUE.length || (tokenLength == IntLiteral.DECIMAL_MAX_VALUE.length && CharOperation.compareTo(token, IntLiteral.DECIMAL_MAX_VALUE) > 0)) {
                    return;
                }
                this.computeValue(token, tokenLength, radix, j);
                break;
            }
            case 8: {
                if (tokenLength > 12) {
                    break;
                }
                if (tokenLength == 12 && token[j] > '4') {
                    return;
                }
                if (CharOperation.equals(token, IntLiteral.OCTAL_MINUS_ONE_VALUE)) {
                    this.constant = IntConstant.fromValue(-1);
                    return;
                }
                this.computeValue(token, tokenLength, radix, j);
            }
        }
    }
    
    private void computeValue(final char[] token, final int tokenLength, final int radix, int j) {
        int computedValue = 0;
        while (j < tokenLength) {
            final int digitValue;
            if ((digitValue = ScannerHelper.digit(token[j++], radix)) < 0) {
                return;
            }
            computedValue = computedValue * radix + digitValue;
        }
        this.constant = IntConstant.fromValue(computedValue);
    }
    
    public IntLiteral convertToMinValue() {
        if ((this.bits & 0x1FE00000) >> 21 != 0) {
            return this;
        }
        final char[] token = (this.reducedForm != null) ? this.reducedForm : this.source;
        switch (token.length) {
            case 10: {
                if (CharOperation.equals(token, IntLiteral.DECIMAL_MIN_VALUE)) {
                    return new IntLiteralMinValue(this.source, this.reducedForm, this.sourceStart, this.sourceEnd);
                }
                break;
            }
        }
        return this;
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
        return TypeBinding.INT;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
