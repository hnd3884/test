package jdk.jfr.internal.instrument;

import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.commons.Remapper;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.commons.LocalVariablesSorter;
import jdk.internal.org.objectweb.asm.commons.RemappingMethodAdapter;

@Deprecated
final class JIMethodInliningAdapter extends RemappingMethodAdapter
{
    private final LocalVariablesSorter lvs;
    private final Label end;
    
    public JIMethodInliningAdapter(final LocalVariablesSorter lvs, final Label end, final int n, final String s, final Remapper remapper) {
        super(n, s, lvs, remapper);
        this.lvs = lvs;
        this.end = end;
        final int n2 = this.isStatic(n) ? 0 : 1;
        final Type[] argumentTypes = Type.getArgumentTypes(s);
        for (int i = argumentTypes.length - 1; i >= 0; --i) {
            super.visitVarInsn(argumentTypes[i].getOpcode(54), i + n2);
        }
        if (n2 > 0) {
            super.visitVarInsn(58, 0);
        }
    }
    
    private boolean isStatic(final int n) {
        return (n & 0x8) != 0x0;
    }
    
    @Override
    public void visitInsn(final int n) {
        if (n == 177 || n == 172 || n == 176 || n == 173) {
            super.visitJumpInsn(167, this.end);
        }
        else {
            super.visitInsn(n);
        }
    }
    
    @Override
    public void visitMaxs(final int n, final int n2) {
    }
    
    @Override
    protected int newLocalMapping(final Type type) {
        return this.lvs.newLocal(type);
    }
}
