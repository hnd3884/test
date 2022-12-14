package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.PrototypeObject;
import jdk.nashorn.internal.runtime.Specialization;
import jdk.nashorn.internal.runtime.ScriptFunction;

final class NativeJSAdapter$Constructor extends ScriptFunction
{
    NativeJSAdapter$Constructor() {
        super("JSAdapter", /* ldc_method_handle(!) */ProcyonConstantHelper_d5.HANDLE, (Specialization[])null);
        final NativeJSAdapter$Prototype nativeJSAdapter$Prototype = new NativeJSAdapter$Prototype();
        PrototypeObject.setConstructor(nativeJSAdapter$Prototype, this);
        this.setPrototype(nativeJSAdapter$Prototype);
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_d5__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_d5
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(NativeJSAdapter.class, boolean.class, Object.class, Object[].class);
            try {
                handle = NativeJSAdapter$Constructor.__PROCYON__LOOKUP_d5__.findStatic(NativeJSAdapter.class, "construct", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_d5.HANDLE = handle;
        }
    }
}
