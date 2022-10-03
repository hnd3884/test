package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class CPLong extends CPConstantNumber
{
    public CPLong(final Long value, final int globalIndex) {
        super((byte)5, value, globalIndex);
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeLong(this.getNumber().longValue());
    }
    
    @Override
    public String toString() {
        return "Long: " + this.getValue();
    }
}
