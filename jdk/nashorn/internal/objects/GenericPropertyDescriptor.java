package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import jdk.nashorn.internal.runtime.AccessorProperty;
import java.util.ArrayList;
import java.util.Objects;
import jdk.nashorn.internal.runtime.ScriptRuntime;
import jdk.nashorn.internal.runtime.ScriptFunction;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.PropertyDescriptor;
import jdk.nashorn.internal.runtime.ScriptObject;

public final class GenericPropertyDescriptor extends ScriptObject implements PropertyDescriptor
{
    public Object configurable;
    public Object enumerable;
    private static PropertyMap $nasgenmap$;
    
    GenericPropertyDescriptor(final boolean configurable, final boolean enumerable, final Global global) {
        super(global.getObjectPrototype(), GenericPropertyDescriptor.$nasgenmap$);
        this.configurable = configurable;
        this.enumerable = enumerable;
    }
    
    @Override
    public boolean isConfigurable() {
        return JSType.toBoolean(this.configurable);
    }
    
    @Override
    public boolean isEnumerable() {
        return JSType.toBoolean(this.enumerable);
    }
    
    @Override
    public boolean isWritable() {
        return false;
    }
    
    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("value");
    }
    
    @Override
    public ScriptFunction getGetter() {
        throw new UnsupportedOperationException("get");
    }
    
    @Override
    public ScriptFunction getSetter() {
        throw new UnsupportedOperationException("set");
    }
    
    @Override
    public void setConfigurable(final boolean flag) {
        this.configurable = flag;
    }
    
    @Override
    public void setEnumerable(final boolean flag) {
        this.enumerable = flag;
    }
    
    @Override
    public void setWritable(final boolean flag) {
        throw new UnsupportedOperationException("set writable");
    }
    
    @Override
    public void setValue(final Object value) {
        throw new UnsupportedOperationException("set value");
    }
    
    @Override
    public void setGetter(final Object getter) {
        throw new UnsupportedOperationException("set getter");
    }
    
    @Override
    public void setSetter(final Object setter) {
        throw new UnsupportedOperationException("set setter");
    }
    
    @Override
    public PropertyDescriptor fillFrom(final ScriptObject sobj) {
        if (sobj.has("configurable")) {
            this.configurable = JSType.toBoolean(sobj.get("configurable"));
        }
        else {
            this.delete("configurable", false);
        }
        if (sobj.has("enumerable")) {
            this.enumerable = JSType.toBoolean(sobj.get("enumerable"));
        }
        else {
            this.delete("enumerable", false);
        }
        return this;
    }
    
    @Override
    public int type() {
        return 0;
    }
    
    @Override
    public boolean hasAndEquals(final PropertyDescriptor other) {
        return (!this.has("configurable") || !other.has("configurable") || this.isConfigurable() == other.isConfigurable()) && (!this.has("enumerable") || !other.has("enumerable") || this.isEnumerable() == other.isEnumerable());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GenericPropertyDescriptor)) {
            return false;
        }
        final GenericPropertyDescriptor other = (GenericPropertyDescriptor)obj;
        return ScriptRuntime.sameValue(this.configurable, other.configurable) && ScriptRuntime.sameValue(this.enumerable, other.enumerable);
    }
    
    @Override
    public String toString() {
        return '[' + this.getClass().getSimpleName() + " {configurable=" + this.configurable + " enumerable=" + this.enumerable + "}]";
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.configurable);
        hash = 97 * hash + Objects.hashCode(this.enumerable);
        return hash;
    }
    
    static {
        $clinit$();
    }
    
    public static void $clinit$() {
        final ArrayList properties = new ArrayList(2);
        properties.add(AccessorProperty.create("configurable", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_11.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_12.HANDLE));
        properties.add(AccessorProperty.create("enumerable", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_13.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_14.HANDLE));
        GenericPropertyDescriptor.$nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    public Object G$configurable() {
        return this.configurable;
    }
    
    public void S$configurable(final Object configurable) {
        this.configurable = configurable;
    }
    
    public Object G$enumerable() {
        return this.enumerable;
    }
    
    public void S$enumerable(final Object enumerable) {
        this.enumerable = enumerable;
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_11__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_11
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = GenericPropertyDescriptor.__PROCYON__LOOKUP_11__.findVirtual(GenericPropertyDescriptor.class, "G$configurable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_11.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_12__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_12
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = GenericPropertyDescriptor.__PROCYON__LOOKUP_12__.findVirtual(GenericPropertyDescriptor.class, "S$configurable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_12.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_13__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_13
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = GenericPropertyDescriptor.__PROCYON__LOOKUP_13__.findVirtual(GenericPropertyDescriptor.class, "G$enumerable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_13.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_14__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_14
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = GenericPropertyDescriptor.__PROCYON__LOOKUP_14__.findVirtual(GenericPropertyDescriptor.class, "S$enumerable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_14.HANDLE = handle;
        }
    }
}
