package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class WideForm extends VariableInstructionForm
{
    public WideForm(final int opcode, final String name) {
        super(opcode, name);
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        final int instruction = operandManager.nextWideByteCode();
        if (instruction == 132) {
            this.setByteCodeOperandsFormat2(instruction, byteCode, operandManager, codeLength);
        }
        else {
            this.setByteCodeOperandsFormat1(instruction, byteCode, operandManager, codeLength);
        }
    }
    
    protected void setByteCodeOperandsFormat1(final int instruction, final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        final int local = operandManager.nextLocal();
        final int[] newRewrite = new int[4];
        int rewriteIndex = 0;
        newRewrite[rewriteIndex++] = byteCode.getOpcode();
        newRewrite[rewriteIndex++] = instruction;
        this.setRewrite2Bytes(local, rewriteIndex, newRewrite);
        rewriteIndex += 2;
        byteCode.setRewrite(newRewrite);
    }
    
    protected void setByteCodeOperandsFormat2(final int instruction, final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        final int local = operandManager.nextLocal();
        final int constWord = operandManager.nextShort();
        final int[] newRewrite = new int[6];
        int rewriteIndex = 0;
        newRewrite[rewriteIndex++] = byteCode.getOpcode();
        newRewrite[rewriteIndex++] = instruction;
        this.setRewrite2Bytes(local, rewriteIndex, newRewrite);
        rewriteIndex += 2;
        this.setRewrite2Bytes(constWord, rewriteIndex, newRewrite);
        rewriteIndex += 2;
        byteCode.setRewrite(newRewrite);
    }
}
