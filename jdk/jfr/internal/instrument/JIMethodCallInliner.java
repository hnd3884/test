package jdk.jfr.internal.instrument;

import java.util.Iterator;
import jdk.internal.org.objectweb.asm.commons.Remapper;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.commons.SimpleRemapper;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import java.util.ArrayList;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.commons.LocalVariablesSorter;

@Deprecated
final class JIMethodCallInliner extends LocalVariablesSorter
{
    private final String oldClass;
    private final String newClass;
    private final MethodNode inlineTarget;
    private final List<CatchBlock> blocks;
    private boolean inlining;
    
    public JIMethodCallInliner(final int n, final String s, final MethodVisitor methodVisitor, final MethodNode inlineTarget, final String oldClass, final String newClass) {
        super(327680, n, s, methodVisitor);
        this.blocks = new ArrayList<CatchBlock>();
        this.oldClass = oldClass;
        this.newClass = newClass;
        this.inlineTarget = inlineTarget;
        Logger.log(LogTag.JFR_SYSTEM_BYTECODE, LogLevel.DEBUG, "MethodCallInliner: targetMethod=" + newClass + "." + inlineTarget.name + inlineTarget.desc);
    }
    
    @Override
    public void visitMethodInsn(final int n, final String s, final String s2, final String s3, final boolean b) {
        if (!this.shouldBeInlined(s, s2, s3)) {
            this.mv.visitMethodInsn(n, s, s2, s3, b);
            return;
        }
        Logger.log(LogTag.JFR_SYSTEM_BYTECODE, LogLevel.DEBUG, "Inlining call to " + s2 + s3);
        final SimpleRemapper simpleRemapper = new SimpleRemapper(this.oldClass, this.newClass);
        final Label label = new Label();
        this.inlining = true;
        this.inlineTarget.instructions.resetLabels();
        this.inlineTarget.accept(new JIMethodInliningAdapter(this, label, (n == 184) ? 8 : 0, s3, simpleRemapper));
        this.inlining = false;
        super.visitLabel(label);
    }
    
    private boolean shouldBeInlined(final String s, final String s2, final String s3) {
        return this.inlineTarget.desc.equals(s3) && this.inlineTarget.name.equals(s2) && s.equals(this.newClass.replace('.', '/'));
    }
    
    @Override
    public void visitTryCatchBlock(final Label label, final Label label2, final Label label3, final String s) {
        if (!this.inlining) {
            this.blocks.add(new CatchBlock(label, label2, label3, s));
        }
        else {
            super.visitTryCatchBlock(label, label2, label3, s);
        }
    }
    
    @Override
    public void visitMaxs(final int n, final int n2) {
        for (final CatchBlock catchBlock : this.blocks) {
            super.visitTryCatchBlock(catchBlock.start, catchBlock.end, catchBlock.handler, catchBlock.type);
        }
        super.visitMaxs(n, n2);
    }
    
    static final class CatchBlock
    {
        final Label start;
        final Label end;
        final Label handler;
        final String type;
        
        CatchBlock(final Label start, final Label end, final Label handler, final String type) {
            this.start = start;
            this.end = end;
            this.handler = handler;
            this.type = type;
        }
    }
}
