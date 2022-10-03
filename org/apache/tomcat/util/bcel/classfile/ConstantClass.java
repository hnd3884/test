package org.apache.tomcat.util.bcel.classfile;

import java.io.IOException;
import java.io.DataInput;

public final class ConstantClass extends Constant
{
    private final int nameIndex;
    
    ConstantClass(final DataInput dataInput) throws IOException {
        super((byte)7);
        this.nameIndex = dataInput.readUnsignedShort();
    }
    
    public int getNameIndex() {
        return this.nameIndex;
    }
}
