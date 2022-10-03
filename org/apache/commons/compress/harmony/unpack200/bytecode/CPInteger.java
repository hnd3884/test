package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class CPInteger extends CPConstantNumber
{
    public CPInteger(final Integer value, final int globalIndex) {
        super((byte)3, value, globalIndex);
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.getNumber().intValue());
    }
    
    @Override
    public String toString() {
        return "Integer: " + this.getValue();
    }
}
