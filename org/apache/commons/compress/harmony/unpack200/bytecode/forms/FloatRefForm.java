package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class FloatRefForm extends SingleByteReferenceForm
{
    public FloatRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    public FloatRefForm(final int opcode, final String name, final int[] rewrite, final boolean widened) {
        this(opcode, name, rewrite);
        this.widened = widened;
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextFloatRef();
    }
    
    @Override
    protected int getPoolID() {
        return 3;
    }
}
