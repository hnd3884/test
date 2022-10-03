package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.List;
import java.io.IOException;
import java.io.DataOutputStream;

public class ExceptionTableEntry
{
    private final int startPC;
    private final int endPC;
    private final int handlerPC;
    private final CPClass catchType;
    private int startPcRenumbered;
    private int endPcRenumbered;
    private int handlerPcRenumbered;
    private int catchTypeIndex;
    
    public ExceptionTableEntry(final int startPC, final int endPC, final int handlerPC, final CPClass catchType) {
        this.startPC = startPC;
        this.endPC = endPC;
        this.handlerPC = handlerPC;
        this.catchType = catchType;
    }
    
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.startPcRenumbered);
        dos.writeShort(this.endPcRenumbered);
        dos.writeShort(this.handlerPcRenumbered);
        dos.writeShort(this.catchTypeIndex);
    }
    
    public void renumber(final List byteCodeOffsets) {
        this.startPcRenumbered = byteCodeOffsets.get(this.startPC);
        final int endPcIndex = this.startPC + this.endPC;
        this.endPcRenumbered = byteCodeOffsets.get(endPcIndex);
        final int handlerPcIndex = endPcIndex + this.handlerPC;
        this.handlerPcRenumbered = byteCodeOffsets.get(handlerPcIndex);
    }
    
    public CPClass getCatchType() {
        return this.catchType;
    }
    
    public void resolve(final ClassConstantPool pool) {
        if (this.catchType == null) {
            this.catchTypeIndex = 0;
            return;
        }
        this.catchType.resolve(pool);
        this.catchTypeIndex = pool.indexOf(this.catchType);
    }
}
