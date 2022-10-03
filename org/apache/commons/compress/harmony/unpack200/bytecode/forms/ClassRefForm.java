package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class ClassRefForm extends ReferenceForm
{
    protected boolean widened;
    
    public ClassRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    public ClassRefForm(final int opcode, final String name, final int[] rewrite, final boolean widened) {
        this(opcode, name, rewrite);
        this.widened = widened;
    }
    
    @Override
    protected void setNestedEntries(final ByteCode byteCode, final OperandManager operandManager, final int offset) throws Pack200Exception {
        if (offset != 0) {
            super.setNestedEntries(byteCode, operandManager, offset - 1);
            return;
        }
        final SegmentConstantPool globalPool = operandManager.globalConstantPool();
        ClassFileEntry[] nested = null;
        nested = new ClassFileEntry[] { globalPool.getClassPoolEntry(operandManager.getCurrentClass()) };
        byteCode.setNested(nested);
        byteCode.setNestedPositions(new int[][] { { 0, 2 } });
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextClassRef();
    }
    
    @Override
    protected int getPoolID() {
        return 7;
    }
}
