package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import jdk.nashorn.internal.runtime.PrototypeObject;
import jdk.nashorn.internal.runtime.Specialization;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.AccessorProperty;
import java.util.ArrayList;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.ScriptFunction;

final class NativeUint8ClampedArray$Constructor extends ScriptFunction
{
    private static final PropertyMap $nasgenmap$;
    
    public int G$BYTES_PER_ELEMENT() {
        return NativeUint8ClampedArray.BYTES_PER_ELEMENT;
    }
    
    static {
        final ArrayList properties = new ArrayList(1);
        properties.add(AccessorProperty.create("BYTES_PER_ELEMENT", 7, /* ldc_method_handle(!) */ProcyonConstantHelper_199.HANDLE, (MethodHandle)null));
        $nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    NativeUint8ClampedArray$Constructor() {
        super("Uint8ClampedArray", /* ldc_method_handle(!) */ProcyonConstantHelper_19a.HANDLE, NativeUint8ClampedArray$Constructor.$nasgenmap$, (Specialization[])null);
        final NativeUint8ClampedArray$Prototype nativeUint8ClampedArray$Prototype = new NativeUint8ClampedArray$Prototype();
        PrototypeObject.setConstructor(nativeUint8ClampedArray$Prototype, this);
        this.setPrototype(nativeUint8ClampedArray$Prototype);
        this.setArity(1);
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_199__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_199
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(int.class);
            try {
                handle = NativeUint8ClampedArray$Constructor.__PROCYON__LOOKUP_199__.findVirtual(NativeUint8ClampedArray$Constructor.class, "G$BYTES_PER_ELEMENT", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_199.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_19a__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_19a
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(NativeUint8ClampedArray.class, boolean.class, Object.class, Object[].class);
            try {
                handle = NativeUint8ClampedArray$Constructor.__PROCYON__LOOKUP_19a__.findStatic(NativeUint8ClampedArray.class, "constructor", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_19a.HANDLE = handle;
        }
    }
}
