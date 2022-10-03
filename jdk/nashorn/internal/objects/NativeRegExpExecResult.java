package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import jdk.nashorn.internal.runtime.AccessorProperty;
import java.util.ArrayList;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.arrays.ArrayData;
import jdk.nashorn.internal.runtime.regexp.RegExpResult;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.ScriptObject;

public final class NativeRegExpExecResult extends ScriptObject
{
    public Object index;
    public Object input;
    private static PropertyMap $nasgenmap$;
    
    NativeRegExpExecResult(final RegExpResult result, final Global global) {
        super(global.getArrayPrototype(), NativeRegExpExecResult.$nasgenmap$);
        this.setIsArray();
        this.setArray(ArrayData.allocate(result.getGroups().clone()));
        this.index = result.getIndex();
        this.input = result.getInput();
    }
    
    @Override
    public String getClassName() {
        return "Array";
    }
    
    public static Object length(final Object self) {
        if (self instanceof ScriptObject) {
            return JSType.toUint32((double)((ScriptObject)self).getArray().length());
        }
        return 0;
    }
    
    public static void length(final Object self, final Object length) {
        if (self instanceof ScriptObject) {
            ((ScriptObject)self).setLength(NativeArray.validLength(length));
        }
    }
    
    static {
        $clinit$();
    }
    
    public static void $clinit$() {
        final ArrayList properties = new ArrayList(3);
        properties.add(AccessorProperty.create("index", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_159.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_15a.HANDLE));
        properties.add(AccessorProperty.create("input", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_15b.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_15c.HANDLE));
        properties.add(AccessorProperty.create("length", 6, /* ldc_method_handle(!) */ProcyonConstantHelper_15d.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_15e.HANDLE));
        NativeRegExpExecResult.$nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    public Object G$index() {
        return this.index;
    }
    
    public void S$index(final Object index) {
        this.index = index;
    }
    
    public Object G$input() {
        return this.input;
    }
    
    public void S$input(final Object input) {
        this.input = input;
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_159__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_159
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeRegExpExecResult.__PROCYON__LOOKUP_159__.findVirtual(NativeRegExpExecResult.class, "G$index", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_159.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_15a__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_15a
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeRegExpExecResult.__PROCYON__LOOKUP_15a__.findVirtual(NativeRegExpExecResult.class, "S$index", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_15a.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_15b__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_15b
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeRegExpExecResult.__PROCYON__LOOKUP_15b__.findVirtual(NativeRegExpExecResult.class, "G$input", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_15b.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_15c__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_15c
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeRegExpExecResult.__PROCYON__LOOKUP_15c__.findVirtual(NativeRegExpExecResult.class, "S$input", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_15c.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_15d__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_15d
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class, Object.class);
            try {
                handle = NativeRegExpExecResult.__PROCYON__LOOKUP_15d__.findStatic(NativeRegExpExecResult.class, "length", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_15d.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_15e__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_15e
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class, Object.class);
            try {
                handle = NativeRegExpExecResult.__PROCYON__LOOKUP_15e__.findStatic(NativeRegExpExecResult.class, "length", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_15e.HANDLE = handle;
        }
    }
}