package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public abstract class InitMethodReferenceForm extends ClassSpecificReferenceForm
{
    public InitMethodReferenceForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    protected abstract String context(final OperandManager p0);
    
    @Override
    protected int getPoolID() {
        return 11;
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextInitRef();
    }
    
    @Override
    protected void setNestedEntries(final ByteCode byteCode, final OperandManager operandManager, final int offset) throws Pack200Exception {
        final SegmentConstantPool globalPool = operandManager.globalConstantPool();
        ClassFileEntry[] nested = null;
        nested = new ClassFileEntry[] { globalPool.getInitMethodPoolEntry(11, offset, this.context(operandManager)) };
        byteCode.setNested(nested);
        byteCode.setNestedPositions(new int[][] { { 0, 2 } });
    }
}
