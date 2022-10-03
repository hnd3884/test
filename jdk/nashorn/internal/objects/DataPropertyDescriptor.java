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

public final class DataPropertyDescriptor extends ScriptObject implements PropertyDescriptor
{
    public Object configurable;
    public Object enumerable;
    public Object writable;
    public Object value;
    private static PropertyMap $nasgenmap$;
    
    DataPropertyDescriptor(final boolean configurable, final boolean enumerable, final boolean writable, final Object value, final Global global) {
        super(global.getObjectPrototype(), DataPropertyDescriptor.$nasgenmap$);
        this.configurable = configurable;
        this.enumerable = enumerable;
        this.writable = writable;
        this.value = value;
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
        return JSType.toBoolean(this.writable);
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public ScriptFunction getGetter() {
        throw new UnsupportedOperationException("getter");
    }
    
    @Override
    public ScriptFunction getSetter() {
        throw new UnsupportedOperationException("setter");
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
        this.writable = flag;
    }
    
    @Override
    public void setValue(final Object value) {
        this.value = value;
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
        if (sobj.has("writable")) {
            this.writable = JSType.toBoolean(sobj.get("writable"));
        }
        else {
            this.delete("writable", false);
        }
        if (sobj.has("value")) {
            this.value = sobj.get("value");
        }
        else {
            this.delete("value", false);
        }
        return this;
    }
    
    @Override
    public int type() {
        return 1;
    }
    
    @Override
    public boolean hasAndEquals(final PropertyDescriptor otherDesc) {
        if (!(otherDesc instanceof DataPropertyDescriptor)) {
            return false;
        }
        final DataPropertyDescriptor other = (DataPropertyDescriptor)otherDesc;
        return (!this.has("configurable") || ScriptRuntime.sameValue(this.configurable, other.configurable)) && (!this.has("enumerable") || ScriptRuntime.sameValue(this.enumerable, other.enumerable)) && (!this.has("writable") || ScriptRuntime.sameValue(this.writable, other.writable)) && (!this.has("value") || ScriptRuntime.sameValue(this.value, other.value));
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DataPropertyDescriptor)) {
            return false;
        }
        final DataPropertyDescriptor other = (DataPropertyDescriptor)obj;
        return ScriptRuntime.sameValue(this.configurable, other.configurable) && ScriptRuntime.sameValue(this.enumerable, other.enumerable) && ScriptRuntime.sameValue(this.writable, other.writable) && ScriptRuntime.sameValue(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return '[' + this.getClass().getSimpleName() + " {configurable=" + this.configurable + " enumerable=" + this.enumerable + " writable=" + this.writable + " value=" + this.value + "}]";
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.configurable);
        hash = 43 * hash + Objects.hashCode(this.enumerable);
        hash = 43 * hash + Objects.hashCode(this.writable);
        hash = 43 * hash + Objects.hashCode(this.value);
        return hash;
    }
    
    static {
        $clinit$();
    }
    
    public static void $clinit$() {
        final ArrayList properties = new ArrayList(4);
        properties.add(AccessorProperty.create("configurable", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_9.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_a.HANDLE));
        properties.add(AccessorProperty.create("enumerable", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_b.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_c.HANDLE));
        properties.add(AccessorProperty.create("writable", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_d.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_e.HANDLE));
        properties.add(AccessorProperty.create("value", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_f.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_10.HANDLE));
        DataPropertyDescriptor.$nasgenmap$ = PropertyMap.newMap(properties);
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
    
    public Object G$writable() {
        return this.writable;
    }
    
    public void S$writable(final Object writable) {
        this.writable = writable;
    }
    
    public Object G$value() {
        return this.value;
    }
    
    public void S$value(final Object value) {
        this.value = value;
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_9__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_9
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = DataPropertyDescriptor.__PROCYON__LOOKUP_9__.findVirtual(DataPropertyDescriptor.class, "G$configurable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_9.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_a__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_a
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = DataPropertyDescriptor.__PROCYON__LOOKUP_a__.findVirtual(DataPropertyDescriptor.class, "S$configurable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_a.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = DataPropertyDescriptor.__PROCYON__LOOKUP_b__.findVirtual(DataPropertyDescriptor.class, "G$enumerable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = DataPropertyDescriptor.__PROCYON__LOOKUP_c__.findVirtual(DataPropertyDescriptor.class, "S$enumerable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_d__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_d
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = DataPropertyDescriptor.__PROCYON__LOOKUP_d__.findVirtual(DataPropertyDescriptor.class, "G$writable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_d.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_e__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_e
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = DataPropertyDescriptor.__PROCYON__LOOKUP_e__.findVirtual(DataPropertyDescriptor.class, "S$writable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_e.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_f__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_f
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = DataPropertyDescriptor.__PROCYON__LOOKUP_f__.findVirtual(DataPropertyDescriptor.class, "G$value", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_f.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_10__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_10
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = DataPropertyDescriptor.__PROCYON__LOOKUP_10__.findVirtual(DataPropertyDescriptor.class, "S$value", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_10.HANDLE = handle;
        }
    }
}
