package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public class JSR_W extends JsrInstruction
{
    JSR_W() {
    }
    
    public JSR_W(final InstructionHandle target) {
        super((short)201, target);
        this.length = 5;
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        this.index = this.getTargetOffset();
        out.writeByte(this.opcode);
        out.writeInt(this.index);
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.index = bytes.readInt();
        this.length = 5;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackProducer(this);
        v.visitBranchInstruction(this);
        v.visitJsrInstruction(this);
        v.visitJSR_W(this);
    }
}
