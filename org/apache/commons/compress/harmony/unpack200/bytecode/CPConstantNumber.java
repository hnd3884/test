package org.apache.commons.compress.harmony.unpack200.bytecode;

public abstract class CPConstantNumber extends CPConstant
{
    public CPConstantNumber(final byte tag, final Object value, final int globalIndex) {
        super(tag, value, globalIndex);
    }
    
    protected Number getNumber() {
        return (Number)this.getValue();
    }
}
