package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class IincForm extends ByteCodeForm
{
    public IincForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        final int local = operandManager.nextLocal();
        final int constant = operandManager.nextByte();
        byteCode.setOperandBytes(new int[] { local, constant });
    }
}
