package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class NewInitMethodRefForm extends InitMethodReferenceForm
{
    public NewInitMethodRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    protected String context(final OperandManager operandManager) {
        final String result = operandManager.getNewClass();
        return result;
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
