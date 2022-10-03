package com.sun.org.apache.bcel.internal.generic;

import java.io.IOException;
import com.sun.org.apache.bcel.internal.util.ByteSequence;

public class LDC_W extends LDC
{
    LDC_W() {
    }
    
    public LDC_W(final int index) {
        super(index);
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.setIndex(bytes.readUnsignedShort());
        this.opcode = 19;
    }
}
