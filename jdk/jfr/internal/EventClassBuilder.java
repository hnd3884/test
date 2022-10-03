package jdk.jfr.internal;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import java.util.Iterator;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.commons.GeneratorAdapter;
import jdk.jfr.Event;
import jdk.jfr.AnnotationElement;
import jdk.jfr.ValueDescriptor;
import java.util.List;
import jdk.internal.org.objectweb.asm.ClassWriter;
import java.util.concurrent.atomic.AtomicLong;
import jdk.internal.org.objectweb.asm.commons.Method;
import jdk.internal.org.objectweb.asm.Type;

public final class EventClassBuilder
{
    private static final Type TYPE_EVENT;
    private static final Type TYPE_IOBE;
    private static final Method DEFAULT_CONSTRUCTOR;
    private static final Method SET_METHOD;
    private static final AtomicLong idCounter;
    private final ClassWriter classWriter;
    private final String fullClassName;
    private final Type type;
    private final List<ValueDescriptor> fields;
    private final List<AnnotationElement> annotationElements;
    
    public EventClassBuilder(final List<AnnotationElement> annotationElements, final List<ValueDescriptor> fields) {
        this.classWriter = new ClassWriter(3);
        this.fullClassName = "jdk.jfr.DynamicEvent" + EventClassBuilder.idCounter.incrementAndGet();
        this.type = Type.getType(this.fullClassName.replace(".", "/"));
        this.fields = fields;
        this.annotationElements = annotationElements;
    }
    
    public Class<? extends Event> build() {
        this.buildClassInfo();
        this.buildConstructor();
        this.buildFields();
        this.buildSetMethod();
        this.endClass();
        final byte[] byteArray = this.classWriter.toByteArray();
        ASMToolkit.logASM(this.fullClassName, byteArray);
        return SecuritySupport.defineClass(this.type.getInternalName(), byteArray, Event.class.getClassLoader()).asSubclass(Event.class);
    }
    
    private void endClass() {
        this.classWriter.visitEnd();
    }
    
    private void buildSetMethod() {
        final GeneratorAdapter generatorAdapter = new GeneratorAdapter(1, EventClassBuilder.SET_METHOD, null, null, this.classWriter);
        int n = 0;
        for (final ValueDescriptor valueDescriptor : this.fields) {
            generatorAdapter.loadArg(0);
            generatorAdapter.visitLdcInsn(n);
            final Label label = new Label();
            generatorAdapter.ifICmp(154, label);
            generatorAdapter.loadThis();
            generatorAdapter.loadArg(1);
            final Type type = ASMToolkit.toType(valueDescriptor);
            generatorAdapter.unbox(ASMToolkit.toType(valueDescriptor));
            generatorAdapter.putField(this.type, valueDescriptor.getName(), type);
            generatorAdapter.visitInsn(177);
            generatorAdapter.visitLabel(label);
            ++n;
        }
        generatorAdapter.throwException(EventClassBuilder.TYPE_IOBE, "Index must between 0 and " + this.fields.size());
        generatorAdapter.endMethod();
    }
    
    private void buildConstructor() {
        final MethodVisitor visitMethod = this.classWriter.visitMethod(1, EventClassBuilder.DEFAULT_CONSTRUCTOR.getName(), EventClassBuilder.DEFAULT_CONSTRUCTOR.getDescriptor(), null, null);
        visitMethod.visitIntInsn(25, 0);
        ASMToolkit.invokeSpecial(visitMethod, EventClassBuilder.TYPE_EVENT.getInternalName(), EventClassBuilder.DEFAULT_CONSTRUCTOR);
        visitMethod.visitInsn(177);
        visitMethod.visitMaxs(0, 0);
    }
    
    private void buildClassInfo() {
        this.classWriter.visit(52, 49, this.type.getInternalName(), null, ASMToolkit.getInternalName(Event.class.getName()), null);
        for (final AnnotationElement annotationElement : this.annotationElements) {
            final AnnotationVisitor visitAnnotation = this.classWriter.visitAnnotation(ASMToolkit.getDescriptor(annotationElement.getTypeName()), true);
            for (final ValueDescriptor valueDescriptor : annotationElement.getValueDescriptors()) {
                final Object value = annotationElement.getValue(valueDescriptor.getName());
                final String name = valueDescriptor.getName();
                if (valueDescriptor.isArray()) {
                    final AnnotationVisitor visitArray = visitAnnotation.visitArray(name);
                    final Object[] array = (Object[])value;
                    for (int i = 0; i < array.length; ++i) {
                        visitArray.visit(null, array[i]);
                    }
                    visitArray.visitEnd();
                }
                else {
                    visitAnnotation.visit(name, value);
                }
            }
            visitAnnotation.visitEnd();
        }
    }
    
    private void buildFields() {
        for (final ValueDescriptor valueDescriptor : this.fields) {
            this.classWriter.visitField(2, valueDescriptor.getName(), ASMToolkit.getDescriptor(valueDescriptor.getTypeName()), null, null);
        }
    }
    
    static {
        TYPE_EVENT = Type.getType(Event.class);
        TYPE_IOBE = Type.getType(IndexOutOfBoundsException.class);
        DEFAULT_CONSTRUCTOR = Method.getMethod("void <init> ()");
        SET_METHOD = Method.getMethod("void set (int, java.lang.Object)");
        idCounter = new AtomicLong();
    }
}
