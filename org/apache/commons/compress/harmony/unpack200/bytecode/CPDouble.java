package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class CPDouble extends CPConstantNumber
{
    public CPDouble(final Double value, final int globalIndex) {
        super((byte)6, value, globalIndex);
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeDouble(this.getNumber().doubleValue());
    }
    
    @Override
    public String toString() {
        return "Double: " + this.getValue();
    }
}
