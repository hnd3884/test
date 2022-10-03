package org.eclipse.jdt.internal.compiler.impl;

public class BooleanConstant extends Constant
{
    private boolean value;
    private static final BooleanConstant TRUE;
    private static final BooleanConstant FALSE;
    
    static {
        TRUE = new BooleanConstant(true);
        FALSE = new BooleanConstant(false);
    }
    
    public static Constant fromValue(final boolean value) {
        return value ? BooleanConstant.TRUE : BooleanConstant.FALSE;
    }
    
    private BooleanConstant(final boolean value) {
        this.value = value;
    }
    
    @Override
    public boolean booleanValue() {
        return this.value;
    }
    
    @Override
    public String stringValue() {
        return String.valueOf(this.value);
    }
    
    @Override
    public String toString() {
        return "(boolean)" + this.value;
    }
    
    @Override
    public int typeID() {
        return 5;
    }
    
    @Override
    public int hashCode() {
        return this.value ? 1231 : 1237;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && this.getClass() != obj.getClass() && false);
    }
}
