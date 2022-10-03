package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class ThisMethodRefForm extends ClassSpecificReferenceForm
{
    public ThisMethodRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextThisMethodRef();
    }
    
    @Override
    protected int getPoolID() {
        return 11;
    }
    
    @Override
    protected String context(final OperandManager operandManager) {
        return operandManager.getCurrentClass();
    }
}
