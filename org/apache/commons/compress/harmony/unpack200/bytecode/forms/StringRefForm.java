package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class StringRefForm extends SingleByteReferenceForm
{
    public StringRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    public StringRefForm(final int opcode, final String name, final int[] rewrite, final boolean widened) {
        this(opcode, name, rewrite);
        this.widened = widened;
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextStringRef();
    }
    
    @Override
    protected int getPoolID() {
        return 6;
    }
    
    @Override
    protected void setNestedEntries(final ByteCode byteCode, final OperandManager operandManager, final int offset) throws Pack200Exception {
        final SegmentConstantPool globalPool = operandManager.globalConstantPool();
        ClassFileEntry[] nested = null;
        nested = new ClassFileEntry[] { globalPool.getValue(this.getPoolID(), offset) };
        byteCode.setNested(nested);
        if (this.widened) {
            byteCode.setNestedPositions(new int[][] { { 0, 2 } });
        }
        else {
            byteCode.setNestedPositions(new int[][] { { 0, 1 } });
        }
    }
}
