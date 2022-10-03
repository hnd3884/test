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

final class NativeSyntaxError$Prototype extends PrototypeObject
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
        properties.add(AccessorProperty.create("name", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_170.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_171.HANDLE));
        properties.add(AccessorProperty.create("message", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_172.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_173.HANDLE));
        $nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    NativeSyntaxError$Prototype() {
        super(NativeSyntaxError$Prototype.$nasgenmap$);
    }
    
    @Override
    public String getClassName() {
        return "Error";
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_170__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_170
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeSyntaxError$Prototype.__PROCYON__LOOKUP_170__.findVirtual(NativeSyntaxError$Prototype.class, "G$name", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_170.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_171__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_171
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeSyntaxError$Prototype.__PROCYON__LOOKUP_171__.findVirtual(NativeSyntaxError$Prototype.class, "S$name", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_171.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_172__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_172
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeSyntaxError$Prototype.__PROCYON__LOOKUP_172__.findVirtual(NativeSyntaxError$Prototype.class, "G$message", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_172.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_173__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_173
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeSyntaxError$Prototype.__PROCYON__LOOKUP_173__.findVirtual(NativeSyntaxError$Prototype.class, "S$message", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_173.HANDLE = handle;
        }
    }
}