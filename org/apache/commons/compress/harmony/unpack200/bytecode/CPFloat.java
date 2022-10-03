package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class CPFloat extends CPConstantNumber
{
    public CPFloat(final Float value, final int globalIndex) {
        super((byte)4, value, globalIndex);
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeFloat(this.getNumber().floatValue());
    }
    
    @Override
    public String toString() {
        return "Float: " + this.getValue();
    }
}
