package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LdcInsnNode extends AbstractInsnNode
{
    public Object cst;
    
    public LdcInsnNode(final Object cst) {
        super(18);
        this.cst = cst;
    }
    
    @Override
    public int getType() {
        return 9;
    }
    
    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(this.cst);
        this.acceptAnnotations(methodVisitor);
    }
    
    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> map) {
        return new LdcInsnNode(this.cst).cloneAnnotations(this);
    }
}
