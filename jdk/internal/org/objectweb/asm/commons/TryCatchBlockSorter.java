package jdk.internal.org.objectweb.asm.commons;

import java.util.Collections;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import java.util.Comparator;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

public class TryCatchBlockSorter extends MethodNode
{
    public TryCatchBlockSorter(final MethodVisitor methodVisitor, final int n, final String s, final String s2, final String s3, final String[] array) {
        this(327680, methodVisitor, n, s, s2, s3, array);
    }
    
    protected TryCatchBlockSorter(final int n, final MethodVisitor mv, final int n2, final String s, final String s2, final String s3, final String[] array) {
        super(n, n2, s, s2, s3, array);
        this.mv = mv;
    }
    
    @Override
    public void visitEnd() {
        Collections.sort(this.tryCatchBlocks, new Comparator<TryCatchBlockNode>() {
            @Override
            public int compare(final TryCatchBlockNode tryCatchBlockNode, final TryCatchBlockNode tryCatchBlockNode2) {
                return this.blockLength(tryCatchBlockNode) - this.blockLength(tryCatchBlockNode2);
            }
            
            private int blockLength(final TryCatchBlockNode tryCatchBlockNode) {
                return TryCatchBlockSorter.this.instructions.indexOf(tryCatchBlockNode.end) - TryCatchBlockSorter.this.instructions.indexOf(tryCatchBlockNode.start);
            }
        });
        for (int i = 0; i < this.tryCatchBlocks.size(); ++i) {
            this.tryCatchBlocks.get(i).updateIndex(i);
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
}
