package jdk.internal.org.objectweb.asm.tree.analysis;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import java.util.Set;

public class SourceValue implements Value
{
    public final int size;
    public final Set<AbstractInsnNode> insns;
    
    public SourceValue(final int n) {
        this(n, SmallSet.emptySet());
    }
    
    public SourceValue(final int size, final AbstractInsnNode abstractInsnNode) {
        this.size = size;
        this.insns = new SmallSet<AbstractInsnNode>(abstractInsnNode, null);
    }
    
    public SourceValue(final int size, final Set<AbstractInsnNode> insns) {
        this.size = size;
        this.insns = insns;
    }
    
    @Override
    public int getSize() {
        return this.size;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof SourceValue)) {
            return false;
        }
        final SourceValue sourceValue = (SourceValue)o;
        return this.size == sourceValue.size && this.insns.equals(sourceValue.insns);
    }
    
    @Override
    public int hashCode() {
        return this.insns.hashCode();
    }
}
