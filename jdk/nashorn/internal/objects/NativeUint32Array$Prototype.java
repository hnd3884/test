package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.ScriptFunction;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import jdk.nashorn.internal.runtime.AccessorProperty;
import java.util.ArrayList;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.PrototypeObject;

final class NativeUint32Array$Prototype extends PrototypeObject
{
    private Object set;
    private Object subarray;
    private static final PropertyMap $nasgenmap$;
    
    public Object G$set() {
        return this.set;
    }
    
    public void S$set(final Object set) {
        this.set = set;
    }
    
    public Object G$subarray() {
        return this.subarray;
    }
    
    public void S$subarray(final Object subarray) {
        this.subarray = subarray;
    }
    
    static {
        final ArrayList properties = new ArrayList(2);
        properties.add(AccessorProperty.create("set", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_18b.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_18c.HANDLE));
        properties.add(AccessorProperty.create("subarray", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_18d.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_18e.HANDLE));
        $nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    NativeUint32Array$Prototype() {
        super(NativeUint32Array$Prototype.$nasgenmap$);
        this.set = ScriptFunction.createBuiltin("set", /* ldc_method_handle(!) */ProcyonConstantHelper_18f.HANDLE);
        this.subarray = ScriptFunction.createBuiltin("subarray", /* ldc_method_handle(!) */ProcyonConstantHelper_190.HANDLE);
    }
    
    @Override
    public String getClassName() {
        return "Uint32Array";
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_18b__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_18b
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeUint32Array$Prototype.__PROCYON__LOOKUP_18b__.findVirtual(NativeUint32Array$Prototype.class, "G$set", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_18b.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_18c__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_18c
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeUint32Array$Prototype.__PROCYON__LOOKUP_18c__.findVirtual(NativeUint32Array$Prototype.class, "S$set", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_18c.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_18d__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_18d
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeUint32Array$Prototype.__PROCYON__LOOKUP_18d__.findVirtual(NativeUint32Array$Prototype.class, "G$subarray", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_18d.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_18e__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_18e
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeUint32Array$Prototype.__PROCYON__LOOKUP_18e__.findVirtual(NativeUint32Array$Prototype.class, "S$subarray", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_18e.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_18f__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_18f
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class, Object.class, Object.class, Object.class);
            try {
                handle = NativeUint32Array$Prototype.__PROCYON__LOOKUP_18f__.findStatic(NativeUint32Array.class, "set", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_18f.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_190__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_190
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(NativeUint32Array.class, Object.class, Object.class, Object.class);
            try {
                handle = NativeUint32Array$Prototype.__PROCYON__LOOKUP_190__.findStatic(NativeUint32Array.class, "subarray", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_190.HANDLE = handle;
        }
    }
}
