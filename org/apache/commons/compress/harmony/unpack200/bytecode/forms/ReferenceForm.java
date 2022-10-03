package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public abstract class ReferenceForm extends ByteCodeForm
{
    public ReferenceForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    protected abstract int getPoolID();
    
    protected abstract int getOffset(final OperandManager p0);
    
    protected void setNestedEntries(final ByteCode byteCode, final OperandManager operandManager, final int offset) throws Pack200Exception {
        final SegmentConstantPool globalPool = operandManager.globalConstantPool();
        ClassFileEntry[] nested = null;
        nested = new ClassFileEntry[] { globalPool.getConstantPoolEntry(this.getPoolID(), offset) };
        if (nested[0] == null) {
            throw new NullPointerException("Null nested entries are not allowed");
        }
        byteCode.setNested(nested);
        byteCode.setNestedPositions(new int[][] { { 0, 2 } });
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        final int offset = this.getOffset(operandManager);
        try {
            this.setNestedEntries(byteCode, operandManager, offset);
        }
        catch (final Pack200Exception ex) {
            throw new Error("Got a pack200 exception. What to do?");
        }
    }
}
