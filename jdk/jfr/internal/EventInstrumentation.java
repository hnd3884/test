package jdk.jfr.internal;

import jdk.jfr.SettingControl;
import jdk.jfr.Name;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import java.util.function.Consumer;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.ClassWriter;
import java.lang.reflect.Field;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import java.lang.reflect.Modifier;
import jdk.jfr.Event;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.jfr.SettingDefinition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import jdk.internal.org.objectweb.asm.tree.AnnotationNode;
import jdk.jfr.Enabled;
import jdk.jfr.Registered;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassReader;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.commons.Method;
import jdk.internal.org.objectweb.asm.Type;
import jdk.jfr.internal.handlers.EventHandler;

public final class EventInstrumentation
{
    public static final String FIELD_EVENT_THREAD = "eventThread";
    public static final String FIELD_STACK_TRACE = "stackTrace";
    public static final String FIELD_DURATION = "duration";
    static final String FIELD_EVENT_HANDLER = "eventHandler";
    static final String FIELD_START_TIME = "startTime";
    private static final Class<? extends EventHandler> eventHandlerProxy;
    private static final Type ANNOTATION_TYPE_NAME;
    private static final Type ANNOTATION_TYPE_REGISTERED;
    private static final Type ANNOTATION_TYPE_ENABLED;
    private static final Type TYPE_EVENT_HANDLER;
    private static final Type TYPE_SETTING_CONTROL;
    private static final Method METHOD_COMMIT;
    private static final Method METHOD_BEGIN;
    private static final Method METHOD_END;
    private static final Method METHOD_IS_ENABLED;
    private static final Method METHOD_TIME_STAMP;
    private static final Method METHOD_EVENT_SHOULD_COMMIT;
    private static final Method METHOD_EVENT_HANDLER_SHOULD_COMMIT;
    private static final Method METHOD_DURATION;
    private final ClassNode classNode;
    private final List<SettingInfo> settingInfos;
    private final List<FieldInfo> fieldInfos;
    private final Method writeMethod;
    private final String eventHandlerXInternalName;
    private final String eventName;
    private boolean guardHandlerReference;
    private Class<?> superClass;
    
    EventInstrumentation(final Class<?> superClass, final byte[] array, final long n) {
        this.superClass = superClass;
        this.classNode = this.createClassNode(array);
        this.settingInfos = buildSettingInfos(superClass, this.classNode);
        this.fieldInfos = buildFieldInfos(superClass, this.classNode);
        this.writeMethod = makeWriteMethod(this.fieldInfos);
        this.eventHandlerXInternalName = ASMToolkit.getInternalName(EventHandlerCreator.makeEventHandlerName(n));
        final String s = annotationValue(this.classNode, EventInstrumentation.ANNOTATION_TYPE_NAME.getDescriptor(), String.class);
        this.eventName = ((s == null) ? this.classNode.name.replace("/", ".") : s);
    }
    
    public String getClassName() {
        return this.classNode.name.replace("/", ".");
    }
    
    private ClassNode createClassNode(final byte[] array) {
        final ClassNode classNode = new ClassNode();
        new ClassReader(array).accept(classNode, 0);
        return classNode;
    }
    
    boolean isRegistered() {
        final Boolean b = annotationValue(this.classNode, EventInstrumentation.ANNOTATION_TYPE_REGISTERED.getDescriptor(), Boolean.class);
        if (b != null) {
            return b;
        }
        if (this.superClass != null) {
            final Registered registered = this.superClass.getAnnotation(Registered.class);
            if (registered != null) {
                return registered.value();
            }
        }
        return true;
    }
    
    boolean isEnabled() {
        final Boolean b = annotationValue(this.classNode, EventInstrumentation.ANNOTATION_TYPE_ENABLED.getDescriptor(), Boolean.class);
        if (b != null) {
            return b;
        }
        if (this.superClass != null) {
            final Enabled enabled = this.superClass.getAnnotation(Enabled.class);
            if (enabled != null) {
                return enabled.value();
            }
        }
        return true;
    }
    
    private static <T> T annotationValue(final ClassNode classNode, final String s, final Class<?> clazz) {
        if (classNode.visibleAnnotations != null) {
            for (final AnnotationNode annotationNode : classNode.visibleAnnotations) {
                if (s.equals(annotationNode.desc)) {
                    final List<Object> values = annotationNode.values;
                    if (values == null || values.size() != 2) {
                        continue;
                    }
                    final Object value = values.get(0);
                    final Object value2 = values.get(1);
                    if (value instanceof String && value2 != null && clazz == value2.getClass() && "value".equals(value)) {
                        return (T)value2;
                    }
                    continue;
                }
            }
        }
        return null;
    }
    
    private static List<SettingInfo> buildSettingInfos(final Class<?> clazz, final ClassNode classNode) {
        final HashSet set = new HashSet();
        final ArrayList list = new ArrayList();
        final String descriptor = Type.getType(SettingDefinition.class).getDescriptor();
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.visibleAnnotations != null) {
                final Iterator<AnnotationNode> iterator2 = methodNode.visibleAnnotations.iterator();
                while (iterator2.hasNext()) {
                    if (descriptor.equals(iterator2.next().desc) && Type.getReturnType(methodNode.desc).equals(Type.getType(Boolean.TYPE))) {
                        final Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
                        if (argumentTypes.length != 1) {
                            continue;
                        }
                        final Type type = argumentTypes[0];
                        final SettingInfo settingInfo = new SettingInfo("setting" + list.size(), list.size());
                        settingInfo.methodName = methodNode.name;
                        settingInfo.settingDescriptor = type.getDescriptor();
                        settingInfo.internalSettingName = type.getInternalName();
                        set.add(methodNode.name);
                        list.add(settingInfo);
                    }
                }
            }
        }
        for (Class<Event> superclass = (Class<Event>)clazz; superclass != Event.class; superclass = superclass.getSuperclass()) {
            for (final java.lang.reflect.Method method : superclass.getDeclaredMethods()) {
                if (!set.contains(method.getName()) && !Modifier.isPrivate(method.getModifiers()) && method.getReturnType().equals(Boolean.TYPE) && method.getParameterCount() == 1) {
                    final Type type2 = Type.getType(method.getParameters()[0].getType());
                    final SettingInfo settingInfo2 = new SettingInfo("setting" + list.size(), list.size());
                    settingInfo2.methodName = method.getName();
                    settingInfo2.settingDescriptor = type2.getDescriptor();
                    settingInfo2.internalSettingName = type2.getInternalName();
                    set.add(method.getName());
                    list.add(settingInfo2);
                }
            }
        }
        return list;
    }
    
    private static List<FieldInfo> buildFieldInfos(final Class<?> clazz, final ClassNode classNode) {
        final HashSet set = new HashSet();
        final ArrayList list = new ArrayList(classNode.fields.size());
        list.add(new FieldInfo("startTime", Type.LONG_TYPE.getDescriptor(), classNode.name));
        list.add(new FieldInfo("duration", Type.LONG_TYPE.getDescriptor(), classNode.name));
        for (final FieldNode fieldNode : classNode.fields) {
            final String className = Type.getType(fieldNode.desc).getClassName();
            if (!set.contains(fieldNode.name) && isValidField(fieldNode.access, className)) {
                list.add(new FieldInfo(fieldNode.name, fieldNode.desc, classNode.name));
                set.add(fieldNode.name);
            }
        }
        for (Class<Event> superclass = (Class<Event>)clazz; superclass != Event.class; superclass = superclass.getSuperclass()) {
            for (final Field field : superclass.getDeclaredFields()) {
                if (!Modifier.isPrivate(field.getModifiers()) && isValidField(field.getModifiers(), field.getType().getName())) {
                    final String name = field.getName();
                    if (!set.contains(name)) {
                        list.add(new FieldInfo(name, Type.getType(field.getType()).getDescriptor(), ASMToolkit.getInternalName(superclass.getName())));
                        set.add(name);
                    }
                }
            }
        }
        return list;
    }
    
    public static boolean isValidField(final int n, final String s) {
        return !Modifier.isTransient(n) && !Modifier.isStatic(n) && jdk.jfr.internal.Type.isValidJavaFieldType(s);
    }
    
    public byte[] buildInstrumented() {
        this.makeInstrumented();
        return this.toByteArray();
    }
    
    private byte[] toByteArray() {
        final ClassWriter classWriter = new ClassWriter(2);
        this.classNode.accept(classWriter);
        classWriter.visitEnd();
        final byte[] byteArray = classWriter.toByteArray();
        Utils.writeGeneratedASM(this.classNode.name, byteArray);
        return byteArray;
    }
    
    public byte[] builUninstrumented() {
        this.makeUninstrumented();
        return this.toByteArray();
    }
    
    private void makeInstrumented() {
        this.updateMethod(EventInstrumentation.METHOD_IS_ENABLED, methodVisitor -> {
            final Label label = new Label();
            if (this.guardHandlerReference) {
                methodVisitor.visitFieldInsn(178, this.getInternalClassName(), "eventHandler", EventInstrumentation.TYPE_EVENT_HANDLER.getDescriptor());
                methodVisitor.visitJumpInsn(198, label);
            }
            methodVisitor.visitFieldInsn(178, this.getInternalClassName(), "eventHandler", EventInstrumentation.TYPE_EVENT_HANDLER.getDescriptor());
            ASMToolkit.invokeVirtual(methodVisitor, EventInstrumentation.TYPE_EVENT_HANDLER.getInternalName(), EventInstrumentation.METHOD_IS_ENABLED);
            methodVisitor.visitInsn(172);
            if (this.guardHandlerReference) {
                methodVisitor.visitLabel(label);
                methodVisitor.visitFrame(3, 0, null, 0, null);
                methodVisitor.visitInsn(3);
                methodVisitor.visitInsn(172);
            }
            return;
        });
        this.updateMethod(EventInstrumentation.METHOD_BEGIN, methodVisitor2 -> {
            methodVisitor2.visitIntInsn(25, 0);
            ASMToolkit.invokeStatic(methodVisitor2, EventInstrumentation.TYPE_EVENT_HANDLER.getInternalName(), EventInstrumentation.METHOD_TIME_STAMP);
            methodVisitor2.visitFieldInsn(181, this.getInternalClassName(), "startTime", "J");
            methodVisitor2.visitInsn(177);
            return;
        });
        this.updateMethod(EventInstrumentation.METHOD_END, methodVisitor3 -> {
            methodVisitor3.visitIntInsn(25, 0);
            methodVisitor3.visitIntInsn(25, 0);
            methodVisitor3.visitFieldInsn(180, this.getInternalClassName(), "startTime", "J");
            ASMToolkit.invokeStatic(methodVisitor3, EventInstrumentation.TYPE_EVENT_HANDLER.getInternalName(), EventInstrumentation.METHOD_DURATION);
            methodVisitor3.visitFieldInsn(181, this.getInternalClassName(), "duration", "J");
            methodVisitor3.visitInsn(177);
            methodVisitor3.visitMaxs(0, 0);
            return;
        });
        this.updateMethod(EventInstrumentation.METHOD_COMMIT, methodVisitor4 -> {
            methodVisitor4.visitCode();
            methodVisitor4.visitVarInsn(25, 0);
            methodVisitor4.visitMethodInsn(182, this.getInternalClassName(), EventInstrumentation.METHOD_IS_ENABLED.getName(), EventInstrumentation.METHOD_IS_ENABLED.getDescriptor(), false);
            final Label label2 = new Label();
            methodVisitor4.visitJumpInsn(154, label2);
            methodVisitor4.visitInsn(177);
            methodVisitor4.visitLabel(label2);
            methodVisitor4.visitFrame(3, 0, null, 0, null);
            methodVisitor4.visitVarInsn(25, 0);
            methodVisitor4.visitFieldInsn(180, this.getInternalClassName(), "startTime", "J");
            methodVisitor4.visitInsn(9);
            methodVisitor4.visitInsn(148);
            final Label label3 = new Label();
            methodVisitor4.visitJumpInsn(154, label3);
            methodVisitor4.visitVarInsn(25, 0);
            methodVisitor4.visitMethodInsn(184, EventInstrumentation.TYPE_EVENT_HANDLER.getInternalName(), EventInstrumentation.METHOD_TIME_STAMP.getName(), EventInstrumentation.METHOD_TIME_STAMP.getDescriptor(), false);
            methodVisitor4.visitFieldInsn(181, this.getInternalClassName(), "startTime", "J");
            final Label label4 = new Label();
            methodVisitor4.visitJumpInsn(167, label4);
            methodVisitor4.visitLabel(label3);
            methodVisitor4.visitFrame(3, 0, null, 0, null);
            methodVisitor4.visitVarInsn(25, 0);
            methodVisitor4.visitFieldInsn(180, this.getInternalClassName(), "duration", "J");
            methodVisitor4.visitInsn(9);
            methodVisitor4.visitInsn(148);
            methodVisitor4.visitJumpInsn(154, label4);
            methodVisitor4.visitVarInsn(25, 0);
            methodVisitor4.visitMethodInsn(184, EventInstrumentation.TYPE_EVENT_HANDLER.getInternalName(), EventInstrumentation.METHOD_TIME_STAMP.getName(), EventInstrumentation.METHOD_TIME_STAMP.getDescriptor(), false);
            methodVisitor4.visitVarInsn(25, 0);
            methodVisitor4.visitFieldInsn(180, this.getInternalClassName(), "startTime", "J");
            methodVisitor4.visitInsn(101);
            methodVisitor4.visitFieldInsn(181, this.getInternalClassName(), "duration", "J");
            methodVisitor4.visitLabel(label4);
            methodVisitor4.visitFrame(3, 0, null, 0, null);
            methodVisitor4.visitVarInsn(25, 0);
            methodVisitor4.visitMethodInsn(182, this.getInternalClassName(), EventInstrumentation.METHOD_EVENT_SHOULD_COMMIT.getName(), EventInstrumentation.METHOD_EVENT_SHOULD_COMMIT.getDescriptor(), false);
            final Label label5 = new Label();
            methodVisitor4.visitJumpInsn(153, label5);
            methodVisitor4.visitFieldInsn(178, this.getInternalClassName(), "eventHandler", Type.getDescriptor(EventInstrumentation.eventHandlerProxy));
            methodVisitor4.visitTypeInsn(192, this.eventHandlerXInternalName);
            this.fieldInfos.iterator();
            final Iterator iterator;
            while (iterator.hasNext()) {
                final FieldInfo fieldInfo = iterator.next();
                methodVisitor4.visitVarInsn(25, 0);
                methodVisitor4.visitFieldInsn(180, fieldInfo.internalClassName, fieldInfo.fieldName, fieldInfo.fieldDescriptor);
            }
            methodVisitor4.visitMethodInsn(182, this.eventHandlerXInternalName, this.writeMethod.getName(), this.writeMethod.getDescriptor(), false);
            methodVisitor4.visitLabel(label5);
            methodVisitor4.visitFrame(3, 0, null, 0, null);
            methodVisitor4.visitInsn(177);
            methodVisitor4.visitEnd();
            return;
        });
        this.updateMethod(EventInstrumentation.METHOD_EVENT_SHOULD_COMMIT, methodVisitor5 -> {
            final Label label6 = new Label();
            methodVisitor5.visitFieldInsn(178, this.getInternalClassName(), "eventHandler", Type.getDescriptor(EventInstrumentation.eventHandlerProxy));
            methodVisitor5.visitVarInsn(25, 0);
            methodVisitor5.visitFieldInsn(180, this.getInternalClassName(), "duration", "J");
            ASMToolkit.invokeVirtual(methodVisitor5, EventInstrumentation.TYPE_EVENT_HANDLER.getInternalName(), EventInstrumentation.METHOD_EVENT_HANDLER_SHOULD_COMMIT);
            methodVisitor5.visitJumpInsn(153, label6);
            this.settingInfos.iterator();
            final Iterator iterator2;
            while (iterator2.hasNext()) {
                final SettingInfo settingInfo = iterator2.next();
                methodVisitor5.visitIntInsn(25, 0);
                methodVisitor5.visitFieldInsn(178, this.getInternalClassName(), "eventHandler", Type.getDescriptor(EventInstrumentation.eventHandlerProxy));
                methodVisitor5.visitTypeInsn(192, this.eventHandlerXInternalName);
                methodVisitor5.visitFieldInsn(180, this.eventHandlerXInternalName, settingInfo.fieldName, EventInstrumentation.TYPE_SETTING_CONTROL.getDescriptor());
                methodVisitor5.visitTypeInsn(192, settingInfo.internalSettingName);
                methodVisitor5.visitMethodInsn(182, this.getInternalClassName(), settingInfo.methodName, "(" + settingInfo.settingDescriptor + ")Z", false);
                methodVisitor5.visitJumpInsn(153, label6);
            }
            methodVisitor5.visitInsn(4);
            methodVisitor5.visitInsn(172);
            methodVisitor5.visitLabel(label6);
            methodVisitor5.visitInsn(3);
            methodVisitor5.visitInsn(172);
        });
    }
    
    private void makeUninstrumented() {
        this.updateExistingWithReturnFalse(EventInstrumentation.METHOD_EVENT_SHOULD_COMMIT);
        this.updateExistingWithReturnFalse(EventInstrumentation.METHOD_IS_ENABLED);
        this.updateExistingWithEmptyVoidMethod(EventInstrumentation.METHOD_COMMIT);
        this.updateExistingWithEmptyVoidMethod(EventInstrumentation.METHOD_BEGIN);
        this.updateExistingWithEmptyVoidMethod(EventInstrumentation.METHOD_END);
    }
    
    private final void updateExistingWithEmptyVoidMethod(final Method method) {
        this.updateMethod(method, methodVisitor -> methodVisitor.visitInsn(177));
    }
    
    private final void updateExistingWithReturnFalse(final Method method) {
        this.updateMethod(method, methodVisitor -> {
            methodVisitor.visitInsn(3);
            methodVisitor.visitInsn(172);
        });
    }
    
    private MethodNode getMethodNode(final Method method) {
        for (final MethodNode methodNode : this.classNode.methods) {
            if (methodNode.name.equals(method.getName()) && methodNode.desc.equals(method.getDescriptor())) {
                return methodNode;
            }
        }
        return null;
    }
    
    private final void updateMethod(final Method method, final Consumer<MethodVisitor> consumer) {
        final MethodNode methodNode = this.getMethodNode(method);
        final int index = this.classNode.methods.indexOf(methodNode);
        this.classNode.methods.remove(methodNode);
        final MethodVisitor visitMethod = this.classNode.visitMethod(methodNode.access, methodNode.name, methodNode.desc, null, null);
        visitMethod.visitCode();
        consumer.accept(visitMethod);
        visitMethod.visitMaxs(0, 0);
        final MethodNode methodNode2 = this.getMethodNode(method);
        this.classNode.methods.remove(methodNode2);
        this.classNode.methods.add(index, methodNode2);
    }
    
    public static Method makeWriteMethod(final List<FieldInfo> list) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        final Iterator<FieldInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().fieldDescriptor);
        }
        sb.append(")V");
        return new Method("write", sb.toString());
    }
    
    private String getInternalClassName() {
        return this.classNode.name;
    }
    
    public List<SettingInfo> getSettingInfos() {
        return this.settingInfos;
    }
    
    public List<FieldInfo> getFieldInfos() {
        return this.fieldInfos;
    }
    
    public String getEventName() {
        return this.eventName;
    }
    
    public void setGuardHandler(final boolean guardHandlerReference) {
        this.guardHandlerReference = guardHandlerReference;
    }
    
    static {
        eventHandlerProxy = EventHandlerProxyCreator.proxyClass;
        ANNOTATION_TYPE_NAME = Type.getType(Name.class);
        ANNOTATION_TYPE_REGISTERED = Type.getType(Registered.class);
        ANNOTATION_TYPE_ENABLED = Type.getType(Enabled.class);
        TYPE_EVENT_HANDLER = Type.getType(EventInstrumentation.eventHandlerProxy);
        TYPE_SETTING_CONTROL = Type.getType(SettingControl.class);
        METHOD_COMMIT = new Method("commit", Type.VOID_TYPE, new Type[0]);
        METHOD_BEGIN = new Method("begin", Type.VOID_TYPE, new Type[0]);
        METHOD_END = new Method("end", Type.VOID_TYPE, new Type[0]);
        METHOD_IS_ENABLED = new Method("isEnabled", Type.BOOLEAN_TYPE, new Type[0]);
        METHOD_TIME_STAMP = new Method("timestamp", Type.LONG_TYPE, new Type[0]);
        METHOD_EVENT_SHOULD_COMMIT = new Method("shouldCommit", Type.BOOLEAN_TYPE, new Type[0]);
        METHOD_EVENT_HANDLER_SHOULD_COMMIT = new Method("shouldCommit", Type.BOOLEAN_TYPE, new Type[] { Type.LONG_TYPE });
        METHOD_DURATION = new Method("duration", Type.LONG_TYPE, new Type[] { Type.LONG_TYPE });
    }
    
    static final class SettingInfo
    {
        private String methodName;
        private String internalSettingName;
        private String settingDescriptor;
        final String fieldName;
        final int index;
        SettingControl settingControl;
        
        public SettingInfo(final String fieldName, final int index) {
            this.fieldName = fieldName;
            this.index = index;
        }
    }
    
    static final class FieldInfo
    {
        private static final Type STRING;
        final String fieldName;
        final String fieldDescriptor;
        final String internalClassName;
        
        public FieldInfo(final String fieldName, final String fieldDescriptor, final String internalClassName) {
            this.fieldName = fieldName;
            this.fieldDescriptor = fieldDescriptor;
            this.internalClassName = internalClassName;
        }
        
        public boolean isString() {
            return FieldInfo.STRING.getDescriptor().equals(this.fieldDescriptor);
        }
        
        static {
            STRING = Type.getType(String.class);
        }
    }
}
