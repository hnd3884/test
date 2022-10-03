package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class ThisFieldRefForm extends ClassSpecificReferenceForm
{
    public ThisFieldRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextThisFieldRef();
    }
    
    @Override
    protected int getPoolID() {
        return 10;
    }
    
    @Override
    protected String context(final OperandManager operandManager) {
        return operandManager.getCurrentClass();
    }
}
