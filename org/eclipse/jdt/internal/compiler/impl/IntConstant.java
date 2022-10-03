package org.eclipse.jdt.internal.compiler.impl;

public class IntConstant extends Constant
{
    int value;
    private static final IntConstant MIN_VALUE;
    private static final IntConstant MINUS_FOUR;
    private static final IntConstant MINUS_THREE;
    private static final IntConstant MINUS_TWO;
    private static final IntConstant MINUS_ONE;
    private static final IntConstant ZERO;
    private static final IntConstant ONE;
    private static final IntConstant TWO;
    private static final IntConstant THREE;
    private static final IntConstant FOUR;
    private static final IntConstant FIVE;
    private static final IntConstant SIX;
    private static final IntConstant SEVEN;
    private static final IntConstant EIGHT;
    private static final IntConstant NINE;
    private static final IntConstant TEN;
    
    static {
        MIN_VALUE = new IntConstant(Integer.MIN_VALUE);
        MINUS_FOUR = new IntConstant(-4);
        MINUS_THREE = new IntConstant(-3);
        MINUS_TWO = new IntConstant(-2);
        MINUS_ONE = new IntConstant(-1);
        ZERO = new IntConstant(0);
        ONE = new IntConstant(1);
        TWO = new IntConstant(2);
        THREE = new IntConstant(3);
        FOUR = new IntConstant(4);
        FIVE = new IntConstant(5);
        SIX = new IntConstant(6);
        SEVEN = new IntConstant(7);
        EIGHT = new IntConstant(8);
        NINE = new IntConstant(9);
        TEN = new IntConstant(10);
    }
    
    public static Constant fromValue(final int value) {
        switch (value) {
            case Integer.MIN_VALUE: {
                return IntConstant.MIN_VALUE;
            }
            case -4: {
                return IntConstant.MINUS_FOUR;
            }
            case -3: {
                return IntConstant.MINUS_THREE;
            }
            case -2: {
                return IntConstant.MINUS_TWO;
            }
            case -1: {
                return IntConstant.MINUS_ONE;
            }
            case 0: {
                return IntConstant.ZERO;
            }
            case 1: {
                return IntConstant.ONE;
            }
            case 2: {
                return IntConstant.TWO;
            }
            case 3: {
                return IntConstant.THREE;
            }
            case 4: {
                return IntConstant.FOUR;
            }
            case 5: {
                return IntConstant.FIVE;
            }
            case 6: {
                return IntConstant.SIX;
            }
            case 7: {
                return IntConstant.SEVEN;
            }
            case 8: {
                return IntConstant.EIGHT;
            }
            case 9: {
                return IntConstant.NINE;
            }
            case 10: {
                return IntConstant.TEN;
            }
            default: {
                return new IntConstant(value);
            }
        }
    }
    
    private IntConstant(final int value) {
        this.value = value;
    }
    
    @Override
    public byte byteValue() {
        return (byte)this.value;
    }
    
    @Override
    public char charValue() {
        return (char)this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    @Override
    public float floatValue() {
        return (float)this.value;
    }
    
    @Override
    public int intValue() {
        return this.value;
    }
    
    @Override
    public long longValue() {
        return this.value;
    }
    
    @Override
    public short shortValue() {
        return (short)this.value;
    }
    
    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }
    
    @Override
    public String toString() {
        return "(int)" + this.value;
    }
    
    @Override
    public int typeID() {
        return 10;
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final IntConstant other = (IntConstant)obj;
        return this.value == other.value;
    }
}
