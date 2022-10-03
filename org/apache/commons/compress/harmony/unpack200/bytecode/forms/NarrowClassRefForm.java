package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class NarrowClassRefForm extends ClassRefForm
{
    public NarrowClassRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    public NarrowClassRefForm(final int opcode, final String name, final int[] rewrite, final boolean widened) {
        super(opcode, name, rewrite, widened);
    }
    
    @Override
    protected void setNestedEntries(final ByteCode byteCode, final OperandManager operandManager, final int offset) throws Pack200Exception {
        super.setNestedEntries(byteCode, operandManager, offset);
        if (!this.widened) {
            byteCode.setNestedPositions(new int[][] { { 0, 1 } });
        }
    }
    
    @Override
    public boolean nestedMustStartClassPool() {
        return !this.widened;
    }
}
