package com.sun.org.apache.xalan.internal.xsltc.runtime;

public final class Operators
{
    public static final int EQ = 0;
    public static final int NE = 1;
    public static final int GT = 2;
    public static final int LT = 3;
    public static final int GE = 4;
    public static final int LE = 5;
    private static final String[] names;
    private static final int[] swapOpArray;
    
    public static final String getOpNames(final int operator) {
        return Operators.names[operator];
    }
    
    public static final int swapOp(final int operator) {
        return Operators.swapOpArray[operator];
    }
    
    static {
        names = new String[] { "=", "!=", ">", "<", ">=", "<=" };
        swapOpArray = new int[] { 0, 1, 3, 2, 5, 4 };
    }
}
