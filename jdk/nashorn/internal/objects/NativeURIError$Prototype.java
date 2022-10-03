package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import jdk.nashorn.internal.runtime.AccessorProperty;
import java.util.ArrayList;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.PrototypeObject;

final class NativeURIError$Prototype extends PrototypeObject
{
    private Object name;
    private Object message;
    private static final PropertyMap $nasgenmap$;
    
    public Object G$name() {
        return this.name;
    }
    
    public void S$name(final Object name) {
        this.name = name;
    }
    
    public Object G$message() {
        return this.message;
    }
    
    public void S$message(final Object message) {
        this.message = message;
    }
    
    static {
        final ArrayList properties = new ArrayList(2);
        properties.add(AccessorProperty.create("name", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_1a2.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_1a3.HANDLE));
        properties.add(AccessorProperty.create("message", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_1a4.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_1a5.HANDLE));
        $nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    NativeURIError$Prototype() {
        super(NativeURIError$Prototype.$nasgenmap$);
    }
    
    @Override
    public String getClassName() {
        return "Error";
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_1a2__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_1a2
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeURIError$Prototype.__PROCYON__LOOKUP_1a2__.findVirtual(NativeURIError$Prototype.class, "G$name", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_1a2.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_1a3__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_1a3
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeURIError$Prototype.__PROCYON__LOOKUP_1a3__.findVirtual(NativeURIError$Prototype.class, "S$name", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_1a3.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_1a4__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_1a4
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeURIError$Prototype.__PROCYON__LOOKUP_1a4__.findVirtual(NativeURIError$Prototype.class, "G$message", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_1a4.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_1a5__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_1a5
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeURIError$Prototype.__PROCYON__LOOKUP_1a5__.findVirtual(NativeURIError$Prototype.class, "S$message", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_1a5.HANDLE = handle;
        }
    }
}
