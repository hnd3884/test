package jdk.jfr.internal.instrument;

import jdk.internal.org.objectweb.asm.ClassWriter;
import java.lang.reflect.Method;
import java.util.List;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import java.util.ArrayList;
import java.io.InputStream;
import jdk.jfr.internal.SecuritySupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import jdk.jfr.internal.Utils;
import jdk.internal.org.objectweb.asm.ClassReader;

@Deprecated
final class JIClassInstrumentation
{
    private final Class<?> instrumentor;
    private final String targetName;
    private final String instrumentorName;
    private final byte[] newBytes;
    private final ClassReader targetClassReader;
    private final ClassReader instrClassReader;
    
    JIClassInstrumentation(final Class<?> instrumentor, final Class<?> clazz, final byte[] array) throws ClassNotFoundException, IOException {
        this.instrumentorName = instrumentor.getName();
        this.targetName = clazz.getName();
        this.instrumentor = instrumentor;
        this.targetClassReader = new ClassReader(array);
        this.instrClassReader = new ClassReader(getOriginalClassBytes(instrumentor));
        this.newBytes = this.makeBytecode();
        Utils.writeGeneratedASM(clazz.getName(), this.newBytes);
    }
    
    private static byte[] getOriginalClassBytes(final Class<?> clazz) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final InputStream resourceAsStream = SecuritySupport.getResourceAsStream("/" + clazz.getName().replace(".", "/") + ".class");
        final byte[] array = new byte[16384];
        int read;
        while ((read = resourceAsStream.read(array, 0, array.length)) != -1) {
            byteArrayOutputStream.write(array, 0, read);
        }
        byteArrayOutputStream.flush();
        resourceAsStream.close();
        return byteArrayOutputStream.toByteArray();
    }
    
    private byte[] makeBytecode() throws IOException, ClassNotFoundException {
        final ArrayList list = new ArrayList();
        for (final Method method : this.instrumentor.getDeclaredMethods()) {
            if (method.getAnnotation(JIInstrumentationMethod.class) != null) {
                list.add(method);
            }
        }
        final ClassNode classNode = new ClassNode();
        this.instrClassReader.accept(new JIInliner(327680, classNode, this.targetName, this.instrumentorName, this.targetClassReader, list), 8);
        final ClassWriter classWriter = new ClassWriter(2);
        this.targetClassReader.accept(new JIMethodMergeAdapter(classWriter, classNode, list, this.instrumentor.getAnnotationsByType(JITypeMapping.class)), 8);
        return classWriter.toByteArray();
    }
    
    public byte[] getNewBytes() {
        return this.newBytes.clone();
    }
}
