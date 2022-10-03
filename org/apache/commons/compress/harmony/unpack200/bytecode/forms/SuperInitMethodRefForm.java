package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class SuperInitMethodRefForm extends InitMethodReferenceForm
{
    public SuperInitMethodRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    protected String context(final OperandManager operandManager) {
        return operandManager.getSuperClass();
    }
}
