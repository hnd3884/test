package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public abstract class SwitchForm extends VariableInstructionForm
{
    public SwitchForm(final int opcode, final String name) {
        super(opcode, name);
    }
    
    @Override
    public void fixUpByteCodeTargets(final ByteCode byteCode, final CodeAttribute codeAttribute) {
        final int[] originalTargets = byteCode.getByteCodeTargets();
        final int numberOfLabels = originalTargets.length;
        final int[] replacementTargets = new int[numberOfLabels];
        final int sourceIndex = byteCode.getByteCodeIndex();
        final int sourceValue = codeAttribute.byteCodeOffsets.get(sourceIndex);
        for (int index = 0; index < numberOfLabels; ++index) {
            final int absoluteInstructionTargetIndex = sourceIndex + originalTargets[index];
            final int targetValue = codeAttribute.byteCodeOffsets.get(absoluteInstructionTargetIndex);
            replacementTargets[index] = targetValue - sourceValue;
        }
        final int[] rewriteArray = byteCode.getRewrite();
        for (int index2 = 0; index2 < numberOfLabels; ++index2) {
            this.setRewrite4Bytes(replacementTargets[index2], rewriteArray);
        }
    }
}
