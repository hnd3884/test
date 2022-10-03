package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.SegmentConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class NewClassRefForm extends ClassRefForm
{
    public NewClassRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        ClassFileEntry[] nested = null;
        final int offset = this.getOffset(operandManager);
        if (offset == 0) {
            final SegmentConstantPool globalPool = operandManager.globalConstantPool();
            nested = new ClassFileEntry[] { globalPool.getClassPoolEntry(operandManager.getCurrentClass()) };
            byteCode.setNested(nested);
            byteCode.setNestedPositions(new int[][] { { 0, 2 } });
        }
        else {
            try {
                this.setNestedEntries(byteCode, operandManager, offset);
            }
            catch (final Pack200Exception ex) {
                throw new Error("Got a pack200 exception. What to do?");
            }
        }
        operandManager.setNewClass(((CPClass)byteCode.getNestedClassFileEntries()[0]).getName());
    }
}
