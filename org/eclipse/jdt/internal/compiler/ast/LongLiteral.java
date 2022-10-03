package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.core.compiler.CharOperation;

public class LongLiteral extends NumberLiteral
{
    private static final char[] HEXA_MIN_VALUE;
    private static final char[] HEXA_MINUS_ONE_VALUE;
    private static final char[] OCTAL_MIN_VALUE;
    private static final char[] OCTAL_MINUS_ONE_VALUE;
    private static final char[] DECIMAL_MIN_VALUE;
    private static final char[] DECIMAL_MAX_VALUE;
    private char[] reducedForm;
    
    static {
        HEXA_MIN_VALUE = "0x8000000000000000L".toCharArray();
        HEXA_MINUS_ONE_VALUE = "0xffffffffffffffffL".toCharArray();
        OCTAL_MIN_VALUE = "01000000000000000000000L".toCharArray();
        OCTAL_MINUS_ONE_VALUE = "01777777777777777777777L".toCharArray();
        DECIMAL_MIN_VALUE = "9223372036854775808L".toCharArray();
        DECIMAL_MAX_VALUE = "9223372036854775807L".toCharArray();
    }
    
    public static LongLiteral buildLongLiteral(final char[] token, final int s, final int e) {
        final char[] longReducedToken = NumberLiteral.removePrefixZerosAndUnderscores(token, true);
        switch (longReducedToken.length) {
            case 19: {
                if (CharOperation.equals(longReducedToken, LongLiteral.HEXA_MIN_VALUE)) {
                    return new LongLiteralMinValue(token, (char[])((longReducedToken != token) ? longReducedToken : null), s, e);
                }
                break;
            }
            case 24: {
                if (CharOperation.equals(longReducedToken, LongLiteral.OCTAL_MIN_VALUE)) {
                    return new LongLiteralMinValue(token, (char[])((longReducedToken != token) ? longReducedToken : null), s, e);
                }
                break;
            }
        }
        return new LongLiteral(token, (char[])((longReducedToken != token) ? longReducedToken : null), s, e);
    }
    
    LongLiteral(final char[] token, final char[] reducedForm, final int start, final int end) {
        super(token, start, end);
        this.reducedForm = reducedForm;
    }
    
    public LongLiteral convertToMinValue() {
        if ((this.bits & 0x1FE00000) >> 21 != 0) {
            return this;
        }
        final char[] token = (this.reducedForm != null) ? this.reducedForm : this.source;
        switch (token.length) {
            case 20: {
                if (CharOperation.equals(token, LongLiteral.DECIMAL_MIN_VALUE, false)) {
                    return new LongLiteralMinValue(this.source, this.reducedForm, this.sourceStart, this.sourceEnd);
                }
                break;
            }
        }
        return this;
    }
    
    @Override
    public void computeConstant() {
        final char[] token = (this.reducedForm != null) ? this.reducedForm : this.source;
        final int tokenLength = token.length;
        final int length = tokenLength - 1;
        int radix = 10;
        int j = 0;
        if (token[0] == '0') {
            if (length == 1) {
                this.constant = LongConstant.fromValue(0L);
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
                if (length - 2 > 64) {
                    return;
                }
                this.computeValue(token, length, radix, j);
                break;
            }
            case 16: {
                if (tokenLength > 19) {
                    break;
                }
                if (CharOperation.equals(token, LongLiteral.HEXA_MINUS_ONE_VALUE)) {
                    this.constant = LongConstant.fromValue(-1L);
                    return;
                }
                this.computeValue(token, length, radix, j);
                break;
            }
            case 10: {
                if (tokenLength > LongLiteral.DECIMAL_MAX_VALUE.length || (tokenLength == LongLiteral.DECIMAL_MAX_VALUE.length && CharOperation.compareTo(token, LongLiteral.DECIMAL_MAX_VALUE, 0, length) > 0)) {
                    return;
                }
                this.computeValue(token, length, radix, j);
                break;
            }
            case 8: {
                if (tokenLength > 24) {
                    break;
                }
                if (tokenLength == 24 && token[j] > '1') {
                    return;
                }
                if (CharOperation.equals(token, LongLiteral.OCTAL_MINUS_ONE_VALUE)) {
                    this.constant = LongConstant.fromValue(-1L);
                    return;
                }
                this.computeValue(token, length, radix, j);
                break;
            }
        }
    }
    
    private void computeValue(final char[] token, final int tokenLength, final int radix, int j) {
        long computedValue = 0L;
        while (j < tokenLength) {
            final int digitValue;
            if ((digitValue = ScannerHelper.digit(token[j++], radix)) < 0) {
                return;
            }
            computedValue = computedValue * radix + digitValue;
        }
        this.constant = LongConstant.fromValue(computedValue);
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
        return TypeBinding.LONG;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
