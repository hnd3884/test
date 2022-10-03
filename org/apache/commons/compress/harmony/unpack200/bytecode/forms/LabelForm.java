package org.apache.commons.compress.harmony.unpack200.bytecode.forms;

import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;

public class LabelForm extends ByteCodeForm
{
    protected boolean widened;
    
    public LabelForm(final int opcode, final String name, final int[] rewrite) {
        super(opcode, name, rewrite);
    }
    
    public LabelForm(final int opcode, final String name, final int[] rewrite, final boolean widened) {
        this(opcode, name, rewrite);
        this.widened = widened;
    }
    
    @Override
    public void fixUpByteCodeTargets(final ByteCode byteCode, final CodeAttribute codeAttribute) {
        final int originalTarget = byteCode.getByteCodeTargets()[0];
        final int sourceIndex = byteCode.getByteCodeIndex();
        final int absoluteInstructionTargetIndex = sourceIndex + originalTarget;
        final int targetValue = codeAttribute.byteCodeOffsets.get(absoluteInstructionTargetIndex);
        final int sourceValue = codeAttribute.byteCodeOffsets.get(sourceIndex);
        byteCode.setOperandSigned2Bytes(targetValue - sourceValue, 0);
        if (this.widened) {
            byteCode.setNestedPositions(new int[][] { { 0, 4 } });
        }
        else {
            byteCode.setNestedPositions(new int[][] { { 0, 2 } });
        }
    }
    
    @Override
    public void setByteCodeOperands(final ByteCode byteCode, final OperandManager operandManager, final int codeLength) {
        byteCode.setByteCodeTargets(new int[] { operandManager.nextLabel() });
    }
}
