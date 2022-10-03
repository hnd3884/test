package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class MultiANewArrayForm extends ClassRefForm
{
    public MultiANewArrayForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        super.setByteCodeOperands(byteCode, operandManager, codeLength);
        final int dimension = operandManager.nextByte();
        byteCode.setOperandByte(dimension, 2);
    }
}
