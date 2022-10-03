package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.PrototypeObject;
import jdk.nashorn.internal.runtime.Specialization;
import jdk.nashorn.internal.runtime.ScriptFunction;

final class NativeURIError$Constructor extends ScriptFunction
{
    NativeURIError$Constructor() {
        super("URIError", /* ldc_method_handle(!) */ProcyonConstantHelper_1a1.HANDLE, (Specialization[])null);
        final NativeURIError$Prototype nativeURIError$Prototype = new NativeURIError$Prototype();
        PrototypeObject.setConstructor(nativeURIError$Prototype, this);
        this.setPrototype(nativeURIError$Prototype);
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_1a1__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_1a1
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(NativeURIError.class, boolean.class, Object.class, Object.class);
            try {
                handle = NativeURIError$Constructor.__PROCYON__LOOKUP_1a1__.findStatic(NativeURIError.class, "constructor", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_1a1.HANDLE = handle;
        }
    }
}
