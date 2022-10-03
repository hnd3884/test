package jdk.jfr.internal;

import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.Method;

public enum EventWriterMethod
{
    BEGIN_EVENT("(" + Type.getType(PlatformEventType.class).getDescriptor() + ")Z", "???", "beginEvent"), 
    END_EVENT("()Z", "???", "endEvent"), 
    PUT_BYTE("(B)V", "byte", "putByte"), 
    PUT_SHORT("(S)V", "short", "putShort"), 
    PUT_INT("(I)V", "int", "putInt"), 
    PUT_LONG("(J)V", "long", "putLong"), 
    PUT_FLOAT("(F)V", "float", "putFloat"), 
    PUT_DOUBLE("(D)V", "double", "putDouble"), 
    PUT_CHAR("(C)V", "char", "putChar"), 
    PUT_BOOLEAN("(Z)V", "boolean", "putBoolean"), 
    PUT_THREAD("(Ljava/lang/Thread;)V", jdk.jfr.internal.Type.THREAD.getName(), "putThread"), 
    PUT_CLASS("(Ljava/lang/Class;)V", jdk.jfr.internal.Type.CLASS.getName(), "putClass"), 
    PUT_STRING("(Ljava/lang/String;Ljdk/jfr/internal/StringPool;)V", jdk.jfr.internal.Type.STRING.getName(), "putString"), 
    PUT_EVENT_THREAD("()V", jdk.jfr.internal.Type.THREAD.getName(), "putEventThread"), 
    PUT_STACK_TRACE("()V", "jdk.types.StackTrace", "putStackTrace");
    
    private final Method asmMethod;
    private final String typeDescriptor;
    
    private EventWriterMethod(final String s2, final String s3, final String s4) {
        this.typeDescriptor = ASMToolkit.getDescriptor(s3);
        this.asmMethod = new Method(s4, s2);
    }
    
    public Method asASM() {
        return this.asmMethod;
    }
    
    public static EventWriterMethod lookupMethod(final EventInstrumentation.FieldInfo fieldInfo) {
        if (fieldInfo.fieldName.equals("eventThread")) {
            return EventWriterMethod.PUT_EVENT_THREAD;
        }
        for (final EventWriterMethod eventWriterMethod : values()) {
            if (fieldInfo.fieldDescriptor.equals(eventWriterMethod.typeDescriptor)) {
                return eventWriterMethod;
            }
        }
        throw new Error("Unknown type " + fieldInfo.fieldDescriptor);
    }
}
