package com.sun.xml.internal.ws.org.objectweb.asm;

public class ClassAdapter implements ClassVisitor
{
    protected ClassVisitor cv;
    
    public ClassAdapter(final ClassVisitor cv) {
        this.cv = cv;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.cv.visit(version, access, name, signature, superName, interfaces);
    }
    
    @Override
    public void visitSource(final String source, final String debug) {
        this.cv.visitSource(source, debug);
    }
    
    @Override
    public void visitOuterClass(final String owner, final String name, final String desc) {
        this.cv.visitOuterClass(owner, name, desc);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return this.cv.visitAnnotation(desc, visible);
    }
    
    @Override
    public void visitAttribute(final Attribute attr) {
        this.cv.visitAttribute(attr);
    }
    
    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        this.cv.visitInnerClass(name, outerName, innerName, access);
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        return this.cv.visitField(access, name, desc, signature, value);
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return this.cv.visitMethod(access, name, desc, signature, exceptions);
    }
    
    @Override
    public void visitEnd() {
        this.cv.visitEnd();
    }
}
