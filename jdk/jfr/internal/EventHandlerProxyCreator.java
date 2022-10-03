package jdk.jfr.internal;

import jdk.jfr.EventType;
import java.util.StringJoiner;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.jfr.Event;
import jdk.jfr.internal.handlers.EventHandler;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.commons.Method;
import jdk.internal.org.objectweb.asm.Type;

final class EventHandlerProxyCreator
{
    private static final int CLASS_VERSION = 52;
    private static final Type TYPE_EVENT_TYPE;
    private static final Type TYPE_EVENT_CONTROL;
    private static final String DESCRIPTOR_EVENT_HANDLER;
    private static final Method METHOD_EVENT_HANDLER_CONSTRUCTOR;
    private static final String DESCRIPTOR_TIME_STAMP;
    private static final Method METHOD_TIME_STAMP;
    private static final String DESCRIPTOR_DURATION;
    private static final Method METHOD_DURATION;
    private static final ClassWriter classWriter;
    private static final String className = "jdk.jfr.proxy.internal.EventHandlerProxy";
    private static final String internalClassName;
    static final Class<? extends EventHandler> proxyClass;
    
    static void ensureInitialized() {
    }
    
    public static Class<? extends EventHandler> makeEventHandlerProxyClass() {
        buildClassInfo();
        buildConstructor();
        buildTimestampMethod();
        buildDurationMethod();
        final byte[] byteArray = EventHandlerProxyCreator.classWriter.toByteArray();
        ASMToolkit.logASM("jdk.jfr.proxy.internal.EventHandlerProxy", byteArray);
        return SecuritySupport.defineClass("jdk.jfr.proxy.internal.EventHandlerProxy", byteArray, Event.class.getClassLoader()).asSubclass(EventHandler.class);
    }
    
    private static void buildConstructor() {
        final MethodVisitor visitMethod = EventHandlerProxyCreator.classWriter.visitMethod(0, EventHandlerProxyCreator.METHOD_EVENT_HANDLER_CONSTRUCTOR.getName(), makeConstructorDescriptor(), null, null);
        visitMethod.visitVarInsn(25, 0);
        visitMethod.visitVarInsn(21, 1);
        visitMethod.visitVarInsn(25, 2);
        visitMethod.visitVarInsn(25, 3);
        visitMethod.visitMethodInsn(183, Type.getInternalName(EventHandler.class), EventHandlerProxyCreator.METHOD_EVENT_HANDLER_CONSTRUCTOR.getName(), EventHandlerProxyCreator.METHOD_EVENT_HANDLER_CONSTRUCTOR.getDescriptor(), false);
        visitMethod.visitInsn(177);
        visitMethod.visitMaxs(0, 0);
        visitMethod.visitEnd();
    }
    
    private static void buildClassInfo() {
        EventHandlerProxyCreator.classWriter.visit(52, 1057, EventHandlerProxyCreator.internalClassName, null, ASMToolkit.getInternalName(EventHandler.class.getName()), null);
    }
    
    private static void buildTimestampMethod() {
        final MethodVisitor visitMethod = EventHandlerProxyCreator.classWriter.visitMethod(9, EventHandlerProxyCreator.METHOD_TIME_STAMP.getName(), EventHandlerProxyCreator.METHOD_TIME_STAMP.getDescriptor(), null, null);
        visitMethod.visitCode();
        visitMethod.visitMethodInsn(184, Type.getInternalName(EventHandler.class), EventHandlerProxyCreator.METHOD_TIME_STAMP.getName(), EventHandlerProxyCreator.METHOD_TIME_STAMP.getDescriptor(), false);
        visitMethod.visitInsn(173);
        visitMethod.visitMaxs(0, 0);
        visitMethod.visitEnd();
    }
    
    private static void buildDurationMethod() {
        final MethodVisitor visitMethod = EventHandlerProxyCreator.classWriter.visitMethod(9, EventHandlerProxyCreator.METHOD_DURATION.getName(), EventHandlerProxyCreator.METHOD_DURATION.getDescriptor(), null, null);
        visitMethod.visitCode();
        visitMethod.visitVarInsn(22, 0);
        visitMethod.visitMethodInsn(184, Type.getInternalName(EventHandler.class), EventHandlerProxyCreator.METHOD_DURATION.getName(), EventHandlerProxyCreator.METHOD_DURATION.getDescriptor(), false);
        visitMethod.visitInsn(173);
        visitMethod.visitMaxs(0, 0);
        visitMethod.visitEnd();
    }
    
    private static String makeConstructorDescriptor() {
        final StringJoiner stringJoiner = new StringJoiner("", "(", ")V");
        stringJoiner.add(Type.BOOLEAN_TYPE.getDescriptor());
        stringJoiner.add(Type.getType(EventType.class).getDescriptor());
        stringJoiner.add(Type.getType(EventControl.class).getDescriptor());
        return stringJoiner.toString();
    }
    
    static {
        TYPE_EVENT_TYPE = Type.getType(EventType.class);
        TYPE_EVENT_CONTROL = Type.getType(EventControl.class);
        DESCRIPTOR_EVENT_HANDLER = "(" + Type.BOOLEAN_TYPE.getDescriptor() + EventHandlerProxyCreator.TYPE_EVENT_TYPE.getDescriptor() + EventHandlerProxyCreator.TYPE_EVENT_CONTROL.getDescriptor() + ")V";
        METHOD_EVENT_HANDLER_CONSTRUCTOR = new Method("<init>", EventHandlerProxyCreator.DESCRIPTOR_EVENT_HANDLER);
        DESCRIPTOR_TIME_STAMP = "()" + Type.LONG_TYPE.getDescriptor();
        METHOD_TIME_STAMP = new Method("timestamp", EventHandlerProxyCreator.DESCRIPTOR_TIME_STAMP);
        DESCRIPTOR_DURATION = "(" + Type.LONG_TYPE.getDescriptor() + ")" + Type.LONG_TYPE.getDescriptor();
        METHOD_DURATION = new Method("duration", EventHandlerProxyCreator.DESCRIPTOR_DURATION);
        classWriter = new ClassWriter(3);
        internalClassName = ASMToolkit.getInternalName("jdk.jfr.proxy.internal.EventHandlerProxy");
        proxyClass = makeEventHandlerProxyClass();
    }
}
