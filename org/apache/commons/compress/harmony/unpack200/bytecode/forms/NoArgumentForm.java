package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class NoArgumentForm extends ByteCodeForm
{
    public NoArgumentForm(final int opcode, final String name) {
        super(opcode, name);
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
    }
}
