package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class IincInsnNode extends AbstractInsnNode
{
    public int var;
    public int incr;
    
    public IincInsnNode(final int var, final int incr) {
        super(132);
        this.var = var;
        this.incr = incr;
    }
    
    @Override
    public int getType() {
        return 10;
    }
    
    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitIincInsn(this.var, this.incr);
        this.acceptAnnotations(methodVisitor);
    }
    
    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> map) {
        return new IincInsnNode(this.var, this.incr).cloneAnnotations(this);
    }
}
