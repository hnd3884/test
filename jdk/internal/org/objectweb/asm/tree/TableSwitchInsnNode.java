package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class TableSwitchInsnNode extends AbstractInsnNode
{
    public int min;
    public int max;
    public LabelNode dflt;
    public List<LabelNode> labels;
    
    public TableSwitchInsnNode(final int min, final int max, final LabelNode dflt, final LabelNode... array) {
        super(170);
        this.min = min;
        this.max = max;
        this.dflt = dflt;
        this.labels = new ArrayList<LabelNode>();
        if (array != null) {
            this.labels.addAll(Arrays.asList(array));
        }
    }
    
    @Override
    public int getType() {
        return 11;
    }
    
    @Override
    public void accept(final MethodVisitor methodVisitor) {
        final Label[] array = new Label[this.labels.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.labels.get(i).getLabel();
        }
        methodVisitor.visitTableSwitchInsn(this.min, this.max, this.dflt.getLabel(), array);
        this.acceptAnnotations(methodVisitor);
    }
    
    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> map) {
        return new TableSwitchInsnNode(this.min, this.max, AbstractInsnNode.clone(this.dflt, map), AbstractInsnNode.clone(this.labels, map)).cloneAnnotations(this);
    }
}
