package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import jdk.nashorn.internal.runtime.AccessorProperty;
import java.util.ArrayList;
import java.util.Objects;
import jdk.nashorn.internal.runtime.ECMAErrors;
import jdk.nashorn.internal.runtime.ScriptRuntime;
import jdk.nashorn.internal.runtime.ScriptFunction;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.PropertyDescriptor;
import jdk.nashorn.internal.runtime.ScriptObject;

public final class AccessorPropertyDescriptor extends ScriptObject implements PropertyDescriptor
{
    public Object configurable;
    public Object enumerable;
    public Object get;
    public Object set;
    private static PropertyMap $nasgenmap$;
    
    AccessorPropertyDescriptor(final boolean configurable, final boolean enumerable, final Object get, final Object set, final Global global) {
        super(global.getObjectPrototype(), AccessorPropertyDescriptor.$nasgenmap$);
        this.configurable = configurable;
        this.enumerable = enumerable;
        this.get = get;
        this.set = set;
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
        return true;
    }
    
    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("value");
    }
    
    @Override
    public ScriptFunction getGetter() {
        return (this.get instanceof ScriptFunction) ? ((ScriptFunction)this.get) : null;
    }
    
    @Override
    public ScriptFunction getSetter() {
        return (this.set instanceof ScriptFunction) ? ((ScriptFunction)this.set) : null;
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
        this.get = getter;
    }
    
    @Override
    public void setSetter(final Object setter) {
        this.set = setter;
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
        if (sobj.has("get")) {
            final Object getter = sobj.get("get");
            if (getter != ScriptRuntime.UNDEFINED && !(getter instanceof ScriptFunction)) {
                throw ECMAErrors.typeError("not.a.function", ScriptRuntime.safeToString(getter));
            }
            this.get = getter;
        }
        else {
            this.delete("get", false);
        }
        if (sobj.has("set")) {
            final Object setter = sobj.get("set");
            if (setter != ScriptRuntime.UNDEFINED && !(setter instanceof ScriptFunction)) {
                throw ECMAErrors.typeError("not.a.function", ScriptRuntime.safeToString(setter));
            }
            this.set = setter;
        }
        else {
            this.delete("set", false);
        }
        return this;
    }
    
    @Override
    public int type() {
        return 2;
    }
    
    @Override
    public boolean hasAndEquals(final PropertyDescriptor otherDesc) {
        if (!(otherDesc instanceof AccessorPropertyDescriptor)) {
            return false;
        }
        final AccessorPropertyDescriptor other = (AccessorPropertyDescriptor)otherDesc;
        return (!this.has("configurable") || ScriptRuntime.sameValue(this.configurable, other.configurable)) && (!this.has("enumerable") || ScriptRuntime.sameValue(this.enumerable, other.enumerable)) && (!this.has("get") || ScriptRuntime.sameValue(this.get, other.get)) && (!this.has("set") || ScriptRuntime.sameValue(this.set, other.set));
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AccessorPropertyDescriptor)) {
            return false;
        }
        final AccessorPropertyDescriptor other = (AccessorPropertyDescriptor)obj;
        return ScriptRuntime.sameValue(this.configurable, other.configurable) && ScriptRuntime.sameValue(this.enumerable, other.enumerable) && ScriptRuntime.sameValue(this.get, other.get) && ScriptRuntime.sameValue(this.set, other.set);
    }
    
    @Override
    public String toString() {
        return '[' + this.getClass().getSimpleName() + " {configurable=" + this.configurable + " enumerable=" + this.enumerable + " getter=" + this.get + " setter=" + this.set + "}]";
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.configurable);
        hash = 41 * hash + Objects.hashCode(this.enumerable);
        hash = 41 * hash + Objects.hashCode(this.get);
        hash = 41 * hash + Objects.hashCode(this.set);
        return hash;
    }
    
    static {
        $clinit$();
    }
    
    public static void $clinit$() {
        final ArrayList properties = new ArrayList(4);
        properties.add(AccessorProperty.create("configurable", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_1.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_2.HANDLE));
        properties.add(AccessorProperty.create("enumerable", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_3.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_4.HANDLE));
        properties.add(AccessorProperty.create("get", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_5.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_6.HANDLE));
        properties.add(AccessorProperty.create("set", 0, /* ldc_method_handle(!) */ProcyonConstantHelper_7.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_8.HANDLE));
        AccessorPropertyDescriptor.$nasgenmap$ = PropertyMap.newMap(properties);
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
    
    public Object G$get() {
        return this.get;
    }
    
    public void S$get(final Object get) {
        this.get = get;
    }
    
    public Object G$set() {
        return this.set;
    }
    
    public void S$set(final Object set) {
        this.set = set;
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_1__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_1
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = AccessorPropertyDescriptor.__PROCYON__LOOKUP_1__.findVirtual(AccessorPropertyDescriptor.class, "G$configurable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_1.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_2__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_2
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = AccessorPropertyDescriptor.__PROCYON__LOOKUP_2__.findVirtual(AccessorPropertyDescriptor.class, "S$configurable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_2.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_3__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_3
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = AccessorPropertyDescriptor.__PROCYON__LOOKUP_3__.findVirtual(AccessorPropertyDescriptor.class, "G$enumerable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_3.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_4__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_4
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = AccessorPropertyDescriptor.__PROCYON__LOOKUP_4__.findVirtual(AccessorPropertyDescriptor.class, "S$enumerable", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_4.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_5__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_5
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = AccessorPropertyDescriptor.__PROCYON__LOOKUP_5__.findVirtual(AccessorPropertyDescriptor.class, "G$get", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_5.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_6__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_6
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = AccessorPropertyDescriptor.__PROCYON__LOOKUP_6__.findVirtual(AccessorPropertyDescriptor.class, "S$get", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_6.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_7__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_7
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = AccessorPropertyDescriptor.__PROCYON__LOOKUP_7__.findVirtual(AccessorPropertyDescriptor.class, "G$set", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_7.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_8__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_8
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = AccessorPropertyDescriptor.__PROCYON__LOOKUP_8__.findVirtual(AccessorPropertyDescriptor.class, "S$set", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_8.HANDLE = handle;
        }
    }
}
