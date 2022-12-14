package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.PrototypeObject;
import jdk.nashorn.internal.runtime.Specialization;
import jdk.nashorn.internal.runtime.ScriptFunction;

final class NativeDataView$Constructor extends ScriptFunction
{
    NativeDataView$Constructor() {
        super("DataView", /* ldc_method_handle(!) */ProcyonConstantHelper_2f.HANDLE, new Specialization[] { new Specialization(/* ldc_method_handle(!) */ProcyonConstantHelper_30.HANDLE, false), new Specialization(/* ldc_method_handle(!) */ProcyonConstantHelper_31.HANDLE, false) });
        final NativeDataView$Prototype nativeDataView$Prototype = new NativeDataView$Prototype();
        PrototypeObject.setConstructor(nativeDataView$Prototype, this);
        this.setPrototype(nativeDataView$Prototype);
        this.setArity(1);
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_2f__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_2f
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(NativeDataView.class, boolean.class, Object.class, Object[].class);
            try {
                handle = NativeDataView$Constructor.__PROCYON__LOOKUP_2f__.findStatic(NativeDataView.class, "constructor", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_2f.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_30__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_30
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(NativeDataView.class, boolean.class, Object.class, Object.class, int.class);
            try {
                handle = NativeDataView$Constructor.__PROCYON__LOOKUP_30__.findStatic(NativeDataView.class, "constructor", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_30.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_31__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_31
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(NativeDataView.class, boolean.class, Object.class, Object.class, int.class, int.class);
            try {
                handle = NativeDataView$Constructor.__PROCYON__LOOKUP_31__.findStatic(NativeDataView.class, "constructor", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_31.HANDLE = handle;
        }
    }
}
