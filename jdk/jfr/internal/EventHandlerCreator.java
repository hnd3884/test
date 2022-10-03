package jdk.jfr.internal;

import jdk.jfr.SettingControl;
import java.util.StringJoiner;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.lang.reflect.Modifier;
import jdk.jfr.ValueDescriptor;
import java.util.ArrayList;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import java.util.List;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.commons.Method;
import jdk.internal.org.objectweb.asm.Type;
import jdk.jfr.internal.handlers.EventHandler;

final class EventHandlerCreator
{
    private static final int CLASS_VERSION = 52;
    private static final String SUFFIX;
    private static final String FIELD_EVENT_TYPE = "platformEventType";
    private static final String FIELD_PREFIX_STRING_POOL = "stringPool";
    private static final Class<? extends EventHandler> eventHandlerProxy;
    private static final Type TYPE_STRING_POOL;
    private static final Type TYPE_EVENT_WRITER;
    private static final Type TYPE_PLATFORM_EVENT_TYPE;
    private static final Type TYPE_EVENT_HANDLER;
    private static final Type TYPE_SETTING_CONTROL;
    private static final Type TYPE_EVENT_TYPE;
    private static final Type TYPE_EVENT_CONTROL;
    private static final String DESCRIPTOR_EVENT_HANDLER;
    private static final Method METHOD_GET_EVENT_WRITER;
    private static final Method METHOD_EVENT_HANDLER_CONSTRUCTOR;
    private static final Method METHOD_RESET;
    private final ClassWriter classWriter;
    private final String className;
    private final String internalClassName;
    private final List<EventInstrumentation.SettingInfo> settingInfos;
    private final List<EventInstrumentation.FieldInfo> fields;
    
    public EventHandlerCreator(final long n, final List<EventInstrumentation.SettingInfo> settingInfos, final List<EventInstrumentation.FieldInfo> fields) {
        this.classWriter = new ClassWriter(3);
        this.className = makeEventHandlerName(n);
        this.internalClassName = ASMToolkit.getInternalName(this.className);
        this.settingInfos = settingInfos;
        this.fields = fields;
    }
    
    public static String makeEventHandlerName(final long n) {
        return EventHandlerCreator.eventHandlerProxy.getName() + n + EventHandlerCreator.SUFFIX;
    }
    
    public EventHandlerCreator(final long n, final List<EventInstrumentation.SettingInfo> list, final EventType eventType, final Class<? extends Event> clazz) {
        this(n, list, createFieldInfos(clazz, eventType));
    }
    
    private static List<EventInstrumentation.FieldInfo> createFieldInfos(final Class<? extends Event> clazz, final EventType eventType) throws Error {
        final ArrayList list = new ArrayList();
        for (final ValueDescriptor valueDescriptor : eventType.getFields()) {
            if (valueDescriptor != TypeLibrary.STACK_TRACE_FIELD && valueDescriptor != TypeLibrary.THREAD_FIELD) {
                final String fieldName = PrivateAccess.getInstance().getFieldName(valueDescriptor);
                final String descriptor = ASMToolkit.getDescriptor(valueDescriptor.getTypeName());
                Class<Event> superclass = (Class<Event>)clazz;
                String internalName = null;
                while (superclass != Event.class) {
                    try {
                        final Field declaredField = superclass.getDeclaredField(fieldName);
                        if (superclass == clazz || !Modifier.isPrivate(declaredField.getModifiers())) {
                            internalName = ASMToolkit.getInternalName(superclass.getName());
                            break;
                        }
                    }
                    catch (final NoSuchFieldException | SecurityException ex) {}
                    superclass = superclass.getSuperclass();
                }
                if (internalName == null) {
                    throw new InternalError("Could not locate field " + fieldName + " for event type" + eventType.getName());
                }
                list.add(new EventInstrumentation.FieldInfo(fieldName, descriptor, internalName));
            }
        }
        return list;
    }
    
    public Class<? extends EventHandler> makeEventHandlerClass() {
        this.buildClassInfo();
        this.buildConstructor();
        this.buildWriteMethod();
        final byte[] byteArray = this.classWriter.toByteArray();
        ASMToolkit.logASM(this.className, byteArray);
        return SecuritySupport.defineClass(this.className, byteArray, Event.class.getClassLoader()).asSubclass(EventHandler.class);
    }
    
    public static EventHandler instantiateEventHandler(final Class<? extends EventHandler> clazz, final boolean b, final EventType eventType, final EventControl eventControl) throws Error {
        Constructor<?> accessible;
        try {
            accessible = clazz.getDeclaredConstructors()[0];
        }
        catch (final Exception ex) {
            throw (Error)new InternalError("Could not get handler constructor for " + eventType.getName()).initCause(ex);
        }
        SecuritySupport.setAccessible(accessible);
        try {
            final List<EventInstrumentation.SettingInfo> settingInfos = eventControl.getSettingInfos();
            final Object[] array = new Object[3 + settingInfos.size()];
            array[0] = b;
            array[1] = eventType;
            array[2] = eventControl;
            for (final EventInstrumentation.SettingInfo settingInfo : settingInfos) {
                array[settingInfo.index + 3] = settingInfo.settingControl;
            }
            return (EventHandler)accessible.newInstance(array);
        }
        catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex2) {
            throw (Error)new InternalError("Could not instantiate event handler for " + eventType.getName() + ". " + ((Throwable)ex2).getMessage()).initCause((Throwable)ex2);
        }
    }
    
    private void buildConstructor() {
        final MethodVisitor visitMethod = this.classWriter.visitMethod(2, EventHandlerCreator.METHOD_EVENT_HANDLER_CONSTRUCTOR.getName(), makeConstructorDescriptor(this.settingInfos), null, null);
        visitMethod.visitVarInsn(25, 0);
        visitMethod.visitVarInsn(21, 1);
        visitMethod.visitVarInsn(25, 2);
        visitMethod.visitVarInsn(25, 3);
        visitMethod.visitMethodInsn(183, Type.getInternalName(EventHandlerCreator.eventHandlerProxy), EventHandlerCreator.METHOD_EVENT_HANDLER_CONSTRUCTOR.getName(), EventHandlerCreator.METHOD_EVENT_HANDLER_CONSTRUCTOR.getDescriptor(), false);
        for (final EventInstrumentation.SettingInfo settingInfo : this.settingInfos) {
            visitMethod.visitVarInsn(25, 0);
            visitMethod.visitVarInsn(25, settingInfo.index + 4);
            visitMethod.visitFieldInsn(181, this.internalClassName, settingInfo.fieldName, EventHandlerCreator.TYPE_SETTING_CONTROL.getDescriptor());
        }
        int n = 0;
        final Iterator<EventInstrumentation.FieldInfo> iterator2 = this.fields.iterator();
        while (iterator2.hasNext()) {
            if (iterator2.next().isString()) {
                visitMethod.visitVarInsn(25, 0);
                visitMethod.visitVarInsn(25, 0);
                visitMethod.visitMethodInsn(182, Type.getInternalName(EventHandlerCreator.eventHandlerProxy), "createStringFieldWriter", "()" + EventHandlerCreator.TYPE_STRING_POOL.getDescriptor(), false);
                visitMethod.visitFieldInsn(181, this.internalClassName, "stringPool" + n, EventHandlerCreator.TYPE_STRING_POOL.getDescriptor());
            }
            ++n;
        }
        visitMethod.visitInsn(177);
        visitMethod.visitMaxs(0, 0);
        visitMethod.visitEnd();
    }
    
    private void buildClassInfo() {
        this.classWriter.visit(52, 49, this.internalClassName, null, ASMToolkit.getInternalName(EventHandlerCreator.eventHandlerProxy.getName()), null);
        final Iterator<EventInstrumentation.SettingInfo> iterator = this.settingInfos.iterator();
        while (iterator.hasNext()) {
            this.classWriter.visitField(17, iterator.next().fieldName, EventHandlerCreator.TYPE_SETTING_CONTROL.getDescriptor(), null, null);
        }
        int n = 0;
        final Iterator<EventInstrumentation.FieldInfo> iterator2 = this.fields.iterator();
        while (iterator2.hasNext()) {
            if (iterator2.next().isString()) {
                this.classWriter.visitField(18, "stringPool" + n, EventHandlerCreator.TYPE_STRING_POOL.getDescriptor(), null, null);
            }
            ++n;
        }
    }
    
    private void visitMethod(final MethodVisitor methodVisitor, final int n, final Type type, final Method method) {
        methodVisitor.visitMethodInsn(n, type.getInternalName(), method.getName(), method.getDescriptor(), false);
    }
    
    private void buildWriteMethod() {
        int n = 0;
        final int n2 = 1;
        int i = 0;
        final Method writeMethod = ASMToolkit.makeWriteMethod(this.fields);
        final Type[] argumentTypes = Type.getArgumentTypes(writeMethod.getDescriptor());
        final MethodVisitor visitMethod = this.classWriter.visitMethod(1, writeMethod.getName(), writeMethod.getDescriptor(), null, null);
        visitMethod.visitCode();
        final Label label = new Label();
        final Label label2 = new Label();
        final Label label3 = new Label();
        visitMethod.visitTryCatchBlock(label, label2, label3, "java/lang/Throwable");
        visitMethod.visitLabel(label);
        this.visitMethod(visitMethod, 184, EventHandlerCreator.TYPE_EVENT_WRITER, EventHandlerCreator.METHOD_GET_EVENT_WRITER);
        visitMethod.visitInsn(89);
        visitMethod.visitVarInsn(25, 0);
        visitMethod.visitFieldInsn(180, EventHandlerCreator.TYPE_EVENT_HANDLER.getInternalName(), "platformEventType", EventHandlerCreator.TYPE_PLATFORM_EVENT_TYPE.getDescriptor());
        this.visitMethod(visitMethod, 182, EventHandlerCreator.TYPE_EVENT_WRITER, EventWriterMethod.BEGIN_EVENT.asASM());
        final Label label4 = new Label();
        visitMethod.visitJumpInsn(153, label4);
        visitMethod.visitInsn(89);
        visitMethod.visitVarInsn(argumentTypes[n].getOpcode(21), n2);
        final int n3 = n2 + argumentTypes[n++].getSize();
        this.visitMethod(visitMethod, 182, EventHandlerCreator.TYPE_EVENT_WRITER, EventWriterMethod.PUT_LONG.asASM());
        ++i;
        visitMethod.visitInsn(89);
        visitMethod.visitVarInsn(argumentTypes[n].getOpcode(21), n3);
        int n4 = n3 + argumentTypes[n++].getSize();
        this.visitMethod(visitMethod, 182, EventHandlerCreator.TYPE_EVENT_WRITER, EventWriterMethod.PUT_LONG.asASM());
        ++i;
        visitMethod.visitInsn(89);
        this.visitMethod(visitMethod, 182, EventHandlerCreator.TYPE_EVENT_WRITER, EventWriterMethod.PUT_EVENT_THREAD.asASM());
        visitMethod.visitInsn(89);
        this.visitMethod(visitMethod, 182, EventHandlerCreator.TYPE_EVENT_WRITER, EventWriterMethod.PUT_STACK_TRACE.asASM());
        while (i < this.fields.size()) {
            visitMethod.visitInsn(89);
            visitMethod.visitVarInsn(argumentTypes[n].getOpcode(21), n4);
            n4 += argumentTypes[n++].getSize();
            final EventInstrumentation.FieldInfo fieldInfo = this.fields.get(i);
            if (fieldInfo.isString()) {
                visitMethod.visitVarInsn(25, 0);
                visitMethod.visitFieldInsn(180, this.internalClassName, "stringPool" + i, EventHandlerCreator.TYPE_STRING_POOL.getDescriptor());
            }
            this.visitMethod(visitMethod, 182, EventHandlerCreator.TYPE_EVENT_WRITER, EventWriterMethod.lookupMethod(fieldInfo).asASM());
            ++i;
        }
        this.visitMethod(visitMethod, 182, EventHandlerCreator.TYPE_EVENT_WRITER, EventWriterMethod.END_EVENT.asASM());
        visitMethod.visitJumpInsn(153, label);
        visitMethod.visitLabel(label2);
        final Label label5 = new Label();
        visitMethod.visitJumpInsn(167, label5);
        visitMethod.visitLabel(label3);
        visitMethod.visitFrame(4, 0, null, 1, new Object[] { "java/lang/Throwable" });
        this.visitMethod(visitMethod, 184, EventHandlerCreator.TYPE_EVENT_WRITER, EventHandlerCreator.METHOD_GET_EVENT_WRITER);
        visitMethod.visitInsn(89);
        final Label label6 = new Label();
        visitMethod.visitJumpInsn(198, label6);
        visitMethod.visitInsn(89);
        this.visitMethod(visitMethod, 182, EventHandlerCreator.TYPE_EVENT_WRITER, EventHandlerCreator.METHOD_RESET);
        visitMethod.visitLabel(label6);
        visitMethod.visitFrame(3, 0, null, 2, new Object[] { "java/lang/Throwable", EventHandlerCreator.TYPE_EVENT_WRITER.getInternalName() });
        visitMethod.visitInsn(87);
        visitMethod.visitInsn(191);
        visitMethod.visitLabel(label4);
        visitMethod.visitFrame(3, 0, null, 1, new Object[] { EventHandlerCreator.TYPE_EVENT_WRITER.getInternalName() });
        visitMethod.visitInsn(87);
        visitMethod.visitLabel(label5);
        visitMethod.visitFrame(3, 0, null, 0, null);
        visitMethod.visitInsn(177);
        visitMethod.visitMaxs(0, 0);
        visitMethod.visitEnd();
    }
    
    private static String makeConstructorDescriptor(final List<EventInstrumentation.SettingInfo> list) {
        final StringJoiner stringJoiner = new StringJoiner("", "(", ")V");
        stringJoiner.add(Type.BOOLEAN_TYPE.getDescriptor());
        stringJoiner.add(Type.getType(EventType.class).getDescriptor());
        stringJoiner.add(Type.getType(EventControl.class).getDescriptor());
        for (int i = 0; i < list.size(); ++i) {
            stringJoiner.add(EventHandlerCreator.TYPE_SETTING_CONTROL.getDescriptor());
        }
        return stringJoiner.toString();
    }
    
    static {
        SUFFIX = "_" + System.currentTimeMillis() + "-" + JVM.getJVM().getPid();
        eventHandlerProxy = EventHandlerProxyCreator.proxyClass;
        TYPE_STRING_POOL = Type.getType(StringPool.class);
        TYPE_EVENT_WRITER = Type.getType(EventWriter.class);
        TYPE_PLATFORM_EVENT_TYPE = Type.getType(PlatformEventType.class);
        TYPE_EVENT_HANDLER = Type.getType(EventHandlerCreator.eventHandlerProxy);
        TYPE_SETTING_CONTROL = Type.getType(SettingControl.class);
        TYPE_EVENT_TYPE = Type.getType(EventType.class);
        TYPE_EVENT_CONTROL = Type.getType(EventControl.class);
        DESCRIPTOR_EVENT_HANDLER = "(" + Type.BOOLEAN_TYPE.getDescriptor() + EventHandlerCreator.TYPE_EVENT_TYPE.getDescriptor() + EventHandlerCreator.TYPE_EVENT_CONTROL.getDescriptor() + ")V";
        METHOD_GET_EVENT_WRITER = new Method("getEventWriter", "()" + EventHandlerCreator.TYPE_EVENT_WRITER.getDescriptor());
        METHOD_EVENT_HANDLER_CONSTRUCTOR = new Method("<init>", EventHandlerCreator.DESCRIPTOR_EVENT_HANDLER);
        METHOD_RESET = new Method("reset", "()V");
    }
}
