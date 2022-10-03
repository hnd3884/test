package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class TableSwitchForm extends SwitchForm
{
    public TableSwitchForm(final int opcode, final String name) {
        super(opcode, name);
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        final int case_count = operandManager.nextCaseCount();
        final int default_pc = operandManager.nextLabel();
        int case_value = -1;
        case_value = operandManager.nextCaseValues();
        final int[] case_pcs = new int[case_count];
        for (int index = 0; index < case_count; ++index) {
            case_pcs[index] = operandManager.nextLabel();
        }
        final int[] labelsArray = new int[case_count + 1];
        labelsArray[0] = default_pc;
        for (int index2 = 1; index2 < case_count + 1; ++index2) {
            labelsArray[index2] = case_pcs[index2 - 1];
        }
        byteCode.setByteCodeTargets(labelsArray);
        final int lowValue = case_value;
        final int highValue = lowValue + case_count - 1;
        final int padLength = 3 - codeLength % 4;
        final int rewriteSize = 1 + padLength + 4 + 4 + 4 + 4 * case_pcs.length;
        final int[] newRewrite = new int[rewriteSize];
        int rewriteIndex = 0;
        newRewrite[rewriteIndex++] = byteCode.getOpcode();
        for (int index3 = 0; index3 < padLength; ++index3) {
            newRewrite[rewriteIndex++] = 0;
        }
        newRewrite[rewriteIndex++] = -1;
        newRewrite[rewriteIndex++] = -1;
        newRewrite[rewriteIndex++] = -1;
        newRewrite[rewriteIndex++] = -1;
        final int lowbyteIndex = rewriteIndex;
        this.setRewrite4Bytes(lowValue, lowbyteIndex, newRewrite);
        rewriteIndex += 4;
        final int highbyteIndex = rewriteIndex;
        this.setRewrite4Bytes(highValue, highbyteIndex, newRewrite);
        rewriteIndex += 4;
        for (int index4 = 0; index4 < case_count; ++index4) {
            newRewrite[rewriteIndex++] = -1;
            newRewrite[rewriteIndex++] = -1;
            newRewrite[rewriteIndex++] = -1;
            newRewrite[rewriteIndex++] = -1;
        }
        byteCode.setRewrite(newRewrite);
    }
}
