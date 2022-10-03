package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;

public class IMethodRefForm extends ReferenceForm
{
    public IMethodRefForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    @Override
    protected int getOffset(final OperandManager operandManager) {
        return operandManager.nextIMethodRef();
    }
    
    @Override
    protected int getPoolID() {
        return 12;
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        super.setByteCodeOperands(byteCode, operandManager, codeLength);
        final int count = ((CPInterfaceMethodRef)byteCode.getNestedClassFileEntries()[0]).invokeInterfaceCount();
        byteCode.getRewrite()[3] = count;
    }
}
