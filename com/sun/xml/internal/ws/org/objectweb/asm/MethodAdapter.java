package com.sun.xml.internal.ws.org.objectweb.asm;

public class MethodAdapter implements MethodVisitor
{
    protected MethodVisitor mv;
    
    public MethodAdapter(final MethodVisitor mv) {
        this.mv = mv;
    }
    
    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return this.mv.visitAnnotationDefault();
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return this.mv.visitAnnotation(desc, visible);
    }
    
    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
        return this.mv.visitParameterAnnotation(parameter, desc, visible);
    }
    
    @Override
    public void visitAttribute(final Attribute attr) {
        this.mv.visitAttribute(attr);
    }
    
    @Override
    public void visitCode() {
        this.mv.visitCode();
    }
    
    @Override
    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
        this.mv.visitFrame(type, nLocal, local, nStack, stack);
    }
    
    @Override
    public void visitInsn(final int opcode) {
        this.mv.visitInsn(opcode);
    }
    
    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        this.mv.visitIntInsn(opcode, operand);
    }
    
    @Override
    public void visitVarInsn(final int opcode, final int var) {
        this.mv.visitVarInsn(opcode, var);
    }
    
    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        this.mv.visitTypeInsn(opcode, type);
    }
    
    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        this.mv.visitFieldInsn(opcode, owner, name, desc);
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        this.mv.visitMethodInsn(opcode, owner, name, desc);
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.mv.visitJumpInsn(opcode, label);
    }
    
    @Override
    public void visitLabel(final Label label) {
        this.mv.visitLabel(label);
    }
    
    @Override
    public void visitLdcInsn(final Object cst) {
        this.mv.visitLdcInsn(cst);
    }
    
    @Override
    public void visitIincInsn(final int var, final int increment) {
        this.mv.visitIincInsn(var, increment);
    }
    
    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
        this.mv.visitTableSwitchInsn(min, max, dflt, labels);
    }
    
    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        this.mv.visitLookupSwitchInsn(dflt, keys, labels);
    }
    
    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        this.mv.visitMultiANewArrayInsn(desc, dims);
    }
    
    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        this.mv.visitTryCatchBlock(start, end, handler, type);
    }
    
    @Override
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
        this.mv.visitLocalVariable(name, desc, signature, start, end, index);
    }
    
    @Override
    public void visitLineNumber(final int line, final Label start) {
        this.mv.visitLineNumber(line, start);
    }
    
    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.mv.visitMaxs(maxStack, maxLocals);
    }
    
    @Override
    public void visitEnd() {
        this.mv.visitEnd();
    }
}
