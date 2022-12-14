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

final class NativeFunction$Prototype extends PrototypeObject
{
    private Object toString;
    private Object apply;
    private Object call;
    private Object bind;
    private Object toSource;
    private static final PropertyMap $nasgenmap$;
    
    public Object G$toString() {
        return this.toString;
    }
    
    public void S$toString(final Object toString) {
        this.toString = toString;
    }
    
    public Object G$apply() {
        return this.apply;
    }
    
    public void S$apply(final Object apply) {
        this.apply = apply;
    }
    
    public Object G$call() {
        return this.call;
    }
    
    public void S$call(final Object call) {
        this.call = call;
    }
    
    public Object G$bind() {
        return this.bind;
    }
    
    public void S$bind(final Object bind) {
        this.bind = bind;
    }
    
    public Object G$toSource() {
        return this.toSource;
    }
    
    public void S$toSource(final Object toSource) {
        this.toSource = toSource;
    }
    
    static {
        final ArrayList properties = new ArrayList(5);
        properties.add(AccessorProperty.create("toString", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_8b.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_8c.HANDLE));
        properties.add(AccessorProperty.create("apply", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_8d.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_8e.HANDLE));
        properties.add(AccessorProperty.create("call", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_8f.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_90.HANDLE));
        properties.add(AccessorProperty.create("bind", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_91.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_92.HANDLE));
        properties.add(AccessorProperty.create("toSource", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_93.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_94.HANDLE));
        $nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    NativeFunction$Prototype() {
        super(NativeFunction$Prototype.$nasgenmap$);
        this.toString = ScriptFunction.createBuiltin("toString", /* ldc_method_handle(!) */ProcyonConstantHelper_95.HANDLE);
        this.apply = ScriptFunction.createBuiltin("apply", /* ldc_method_handle(!) */ProcyonConstantHelper_96.HANDLE);
        final ScriptFunction builtin = ScriptFunction.createBuiltin("call", /* ldc_method_handle(!) */ProcyonConstantHelper_97.HANDLE);
        builtin.setArity(1);
        this.call = builtin;
        final ScriptFunction builtin2 = ScriptFunction.createBuiltin("bind", /* ldc_method_handle(!) */ProcyonConstantHelper_98.HANDLE);
        builtin2.setArity(1);
        this.bind = builtin2;
        this.toSource = ScriptFunction.createBuiltin("toSource", /* ldc_method_handle(!) */ProcyonConstantHelper_99.HANDLE);
    }
    
    @Override
    public String getClassName() {
        return "Function";
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_8b__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_8b
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_8b__.findVirtual(NativeFunction$Prototype.class, "G$toString", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_8b.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_8c__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_8c
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_8c__.findVirtual(NativeFunction$Prototype.class, "S$toString", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_8c.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_8d__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_8d
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_8d__.findVirtual(NativeFunction$Prototype.class, "G$apply", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_8d.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_8e__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_8e
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_8e__.findVirtual(NativeFunction$Prototype.class, "S$apply", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_8e.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_8f__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_8f
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_8f__.findVirtual(NativeFunction$Prototype.class, "G$call", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_8f.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_90__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_90
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_90__.findVirtual(NativeFunction$Prototype.class, "S$call", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_90.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_91__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_91
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_91__.findVirtual(NativeFunction$Prototype.class, "G$bind", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_91.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_92__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_92
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_92__.findVirtual(NativeFunction$Prototype.class, "S$bind", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_92.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_93__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_93
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_93__.findVirtual(NativeFunction$Prototype.class, "G$toSource", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_93.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_94__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_94
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_94__.findVirtual(NativeFunction$Prototype.class, "S$toSource", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_94.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_95__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_95
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(String.class, Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_95__.findStatic(NativeFunction.class, "toString", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_95.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_96__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_96
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class, Object.class, Object.class, Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_96__.findStatic(NativeFunction.class, "apply", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_96.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_97__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_97
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class, Object.class, Object[].class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_97__.findStatic(NativeFunction.class, "call", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_97.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_98__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_98
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class, Object.class, Object[].class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_98__.findStatic(NativeFunction.class, "bind", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_98.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_99__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_99
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(String.class, Object.class);
            try {
                handle = NativeFunction$Prototype.__PROCYON__LOOKUP_99__.findStatic(NativeFunction.class, "toSource", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_99.HANDLE = handle;
        }
    }
}
