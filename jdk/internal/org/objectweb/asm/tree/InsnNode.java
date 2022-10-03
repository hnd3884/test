package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class InsnNode extends AbstractInsnNode
{
    public InsnNode(final int n) {
        super(n);
    }
    
    @Override
    public int getType() {
        return 0;
    }
    
    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitInsn(this.opcode);
        this.acceptAnnotations(methodVisitor);
    }
    
    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> map) {
        return new InsnNode(this.opcode).cloneAnnotations(this);
    }
}
