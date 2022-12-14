package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.ClassVisitor;

public class RemappingClassAdapter extends ClassVisitor
{
    protected final Remapper remapper;
    protected String className;
    
    public RemappingClassAdapter(final ClassVisitor classVisitor, final Remapper remapper) {
        this(327680, classVisitor, remapper);
    }
    
    protected RemappingClassAdapter(final int n, final ClassVisitor classVisitor, final Remapper remapper) {
        super(n, classVisitor);
        this.remapper = remapper;
    }
    
    @Override
    public void visit(final int n, final int n2, final String className, final String s, final String s2, final String[] array) {
        this.className = className;
        super.visit(n, n2, this.remapper.mapType(className), this.remapper.mapSignature(s, false), this.remapper.mapType(s2), (String[])((array == null) ? null : this.remapper.mapTypes(array)));
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        final AnnotationVisitor visitAnnotation = super.visitAnnotation(this.remapper.mapDesc(s), b);
        return (visitAnnotation == null) ? null : this.createRemappingAnnotationAdapter(visitAnnotation);
    }
    
    @Override
    public AnnotationVisitor visitTypeAnnotation(final int n, final TypePath typePath, final String s, final boolean b) {
        final AnnotationVisitor visitTypeAnnotation = super.visitTypeAnnotation(n, typePath, this.remapper.mapDesc(s), b);
        return (visitTypeAnnotation == null) ? null : this.createRemappingAnnotationAdapter(visitTypeAnnotation);
    }
    
    @Override
    public FieldVisitor visitField(final int n, final String s, final String s2, final String s3, final Object o) {
        final FieldVisitor visitField = super.visitField(n, this.remapper.mapFieldName(this.className, s, s2), this.remapper.mapDesc(s2), this.remapper.mapSignature(s3, true), this.remapper.mapValue(o));
        return (visitField == null) ? null : this.createRemappingFieldAdapter(visitField);
    }
    
    @Override
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        final String mapMethodDesc = this.remapper.mapMethodDesc(s2);
        final MethodVisitor visitMethod = super.visitMethod(n, this.remapper.mapMethodName(this.className, s, s2), mapMethodDesc, this.remapper.mapSignature(s3, false), (String[])((array == null) ? null : this.remapper.mapTypes(array)));
        return (visitMethod == null) ? null : this.createRemappingMethodAdapter(n, mapMethodDesc, visitMethod);
    }
    
    @Override
    public void visitInnerClass(final String s, final String s2, final String s3, final int n) {
        super.visitInnerClass(this.remapper.mapType(s), (s2 == null) ? null : this.remapper.mapType(s2), s3, n);
    }
    
    @Override
    public void visitOuterClass(final String s, final String s2, final String s3) {
        super.visitOuterClass(this.remapper.mapType(s), (s2 == null) ? null : this.remapper.mapMethodName(s, s2, s3), (s3 == null) ? null : this.remapper.mapMethodDesc(s3));
    }
    
    protected FieldVisitor createRemappingFieldAdapter(final FieldVisitor fieldVisitor) {
        return new RemappingFieldAdapter(fieldVisitor, this.remapper);
    }
    
    protected MethodVisitor createRemappingMethodAdapter(final int n, final String s, final MethodVisitor methodVisitor) {
        return new RemappingMethodAdapter(n, s, methodVisitor, this.remapper);
    }
    
    protected AnnotationVisitor createRemappingAnnotationAdapter(final AnnotationVisitor annotationVisitor) {
        return new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }
}
