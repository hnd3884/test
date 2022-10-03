package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class FieldRefForm extends ReferenceForm
{
    public FieldRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextFieldRef();
    }
    
    @Override
    protected int getPoolID() {
        return 10;
    }
}
