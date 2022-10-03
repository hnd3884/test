package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.impl.IntConstant;

public class IntLiteralMinValue extends IntLiteral
{
    static final char[] CharValue;
    
    static {
        CharValue = new char[] { '-', '2', '1', '4', '7', '4', '8', '3', '6', '4', '8' };
    }
    
    public IntLiteralMinValue(final char[] token, final char[] reducedToken, final int start, final int end) {
        super(token, reducedToken, start, end, Integer.MIN_VALUE, IntConstant.fromValue(Integer.MIN_VALUE));
    }
    
    @Override
    public void computeConstant() {
    }
}
