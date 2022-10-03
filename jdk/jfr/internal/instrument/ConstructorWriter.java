package jdk.jfr.internal.instrument;

import jdk.internal.org.objectweb.asm.MethodVisitor;

final class ConstructorWriter extends MethodVisitor
{
    private boolean useInputParameter;
    private String shortClassName;
    private String fullClassName;
    
    ConstructorWriter(final Class<?> clazz, final boolean useInputParameter) {
        super(327680);
        this.useInputParameter = useInputParameter;
        this.shortClassName = clazz.getSimpleName();
        this.fullClassName = clazz.getName().replace('.', '/');
    }
    
    @Override
    public void visitInsn(final int n) {
        if (n == 177) {
            if (this.useInputParameter) {
                this.useInput();
            }
            else {
                this.noInput();
            }
        }
        this.mv.visitInsn(n);
    }
    
    private void useInput() {
        this.mv.visitVarInsn(25, 0);
        this.mv.visitVarInsn(25, 1);
        this.mv.visitMethodInsn(184, "jdk/jfr/internal/instrument/ThrowableTracer", "trace" + this.shortClassName, "(L" + this.fullClassName + ";Ljava/lang/String;)V");
    }
    
    private void noInput() {
        this.mv.visitVarInsn(25, 0);
        this.mv.visitInsn(1);
        this.mv.visitMethodInsn(184, "jdk/jfr/internal/instrument/ThrowableTracer", "trace" + this.shortClassName, "(L" + this.fullClassName + ";Ljava/lang/String;)V");
    }
    
    public void setMethodVisitor(final MethodVisitor mv) {
        this.mv = mv;
    }
}
