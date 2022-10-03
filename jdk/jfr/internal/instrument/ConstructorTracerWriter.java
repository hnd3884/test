package jdk.jfr.internal.instrument;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import java.io.IOException;
import jdk.internal.org.objectweb.asm.ClassWriter;
import java.io.InputStream;
import jdk.internal.org.objectweb.asm.ClassReader;
import java.io.ByteArrayInputStream;
import jdk.internal.org.objectweb.asm.ClassVisitor;

final class ConstructorTracerWriter extends ClassVisitor
{
    private ConstructorWriter useInputParameter;
    private ConstructorWriter noUseInputParameter;
    
    static byte[] generateBytes(final Class<?> clazz, final byte[] array) throws IOException {
        final ClassReader classReader = new ClassReader(new ByteArrayInputStream(array));
        final ClassWriter classWriter = new ClassWriter(1);
        classReader.accept(new ConstructorTracerWriter(classWriter, clazz), 0);
        return classWriter.toByteArray();
    }
    
    private ConstructorTracerWriter(final ClassVisitor classVisitor, final Class<?> clazz) {
        super(327680, classVisitor);
        this.useInputParameter = new ConstructorWriter(clazz, true);
        this.noUseInputParameter = new ConstructorWriter(clazz, false);
    }
    
    private boolean isConstructor(final String s) {
        return s.equals("<init>");
    }
    
    private boolean takesStringParameter(final String s) {
        final Type[] argumentTypes = Type.getArgumentTypes(s);
        return argumentTypes.length > 0 && argumentTypes[0].getClassName().equals(String.class.getName());
    }
    
    @Override
    public MethodVisitor visitMethod(final int n, final String s, final String s2, final String s3, final String[] array) {
        final MethodVisitor visitMethod = super.visitMethod(n, s, s2, s3, array);
        if (!this.isConstructor(s)) {
            return visitMethod;
        }
        if (this.takesStringParameter(s2)) {
            this.useInputParameter.setMethodVisitor(visitMethod);
            return this.useInputParameter;
        }
        this.noUseInputParameter.setMethodVisitor(visitMethod);
        return this.noUseInputParameter;
    }
}
