package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Label;

public class LabelNode extends AbstractInsnNode
{
    private Label label;
    
    public LabelNode() {
        super(-1);
    }
    
    public LabelNode(final Label label) {
        super(-1);
        this.label = label;
    }
    
    @Override
    public int getType() {
        return 8;
    }
    
    public Label getLabel() {
        if (this.label == null) {
            this.label = new Label();
        }
        return this.label;
    }
    
    @Override
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitLabel(this.getLabel());
    }
    
    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> map) {
        return map.get(this);
    }
    
    public void resetLabel() {
        this.label = null;
    }
}
