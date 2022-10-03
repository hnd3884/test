package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class LocalForm extends ByteCodeForm
{
    public LocalForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        byteCode.setOperandBytes(new int[] { operandManager.nextLocal() });
    }
}
