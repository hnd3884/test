package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class DoubleForm extends ReferenceForm
{
    public DoubleForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextDoubleRef();
    }
    
    @Override
    protected int getPoolID() {
        return 5;
    }
}
