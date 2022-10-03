package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class LookupSwitchForm extends SwitchForm
{
    public LookupSwitchForm(final int opcode, final String name) {
        super(opcode, name);
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        final int case_count = operandManager.nextCaseCount();
        final int default_pc = operandManager.nextLabel();
        final int[] case_values = new int[case_count];
        for (int index = 0; index < case_count; ++index) {
            case_values[index] = operandManager.nextCaseValues();
        }
        final int[] case_pcs = new int[case_count];
        for (int index2 = 0; index2 < case_count; ++index2) {
            case_pcs[index2] = operandManager.nextLabel();
        }
        final int[] labelsArray = new int[case_count + 1];
        labelsArray[0] = default_pc;
        for (int index3 = 1; index3 < case_count + 1; ++index3) {
            labelsArray[index3] = case_pcs[index3 - 1];
        }
        byteCode.setByteCodeTargets(labelsArray);
        final int padLength = 3 - codeLength % 4;
        final int rewriteSize = 1 + padLength + 4 + 4 + 4 * case_values.length + 4 * case_pcs.length;
        final int[] newRewrite = new int[rewriteSize];
        int rewriteIndex = 0;
        newRewrite[rewriteIndex++] = byteCode.getOpcode();
        for (int index4 = 0; index4 < padLength; ++index4) {
            newRewrite[rewriteIndex++] = 0;
        }
        newRewrite[rewriteIndex++] = -1;
        newRewrite[rewriteIndex++] = -1;
        newRewrite[rewriteIndex++] = -1;
        newRewrite[rewriteIndex++] = -1;
        final int npairsIndex = rewriteIndex;
        this.setRewrite4Bytes(case_values.length, npairsIndex, newRewrite);
        rewriteIndex += 4;
        for (int index5 = 0; index5 < case_values.length; ++index5) {
            this.setRewrite4Bytes(case_values[index5], rewriteIndex, newRewrite);
            rewriteIndex += 4;
            newRewrite[rewriteIndex++] = -1;
            newRewrite[rewriteIndex++] = -1;
            newRewrite[rewriteIndex++] = -1;
            newRewrite[rewriteIndex++] = -1;
        }
        byteCode.setRewrite(newRewrite);
    }
}
