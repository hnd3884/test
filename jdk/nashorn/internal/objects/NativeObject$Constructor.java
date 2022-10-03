package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import jdk.nashorn.internal.runtime.AccessorProperty;
import java.util.ArrayList;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.ScriptFunction;

final class NativeObject$Constructor extends ScriptFunction
{
    private Object setIndexedPropertiesToExternalArrayData;
    private Object getPrototypeOf;
    private Object setPrototypeOf;
    private Object getOwnPropertyDescriptor;
    private Object getOwnPropertyNames;
    private Object create;
    private Object defineProperty;
    private Object defineProperties;
    private Object seal;
    private Object freeze;
    private Object preventExtensions;
    private Object isSealed;
    private Object isFrozen;
    private Object isExtensible;
    private Object keys;
    private Object bindProperties;
    private static final PropertyMap $nasgenmap$;
    
    public Object G$setIndexedPropertiesToExternalArrayData() {
        return this.setIndexedPropertiesToExternalArrayData;
    }
    
    public void S$setIndexedPropertiesToExternalArrayData(final Object setIndexedPropertiesToExternalArrayData) {
        this.setIndexedPropertiesToExternalArrayData = setIndexedPropertiesToExternalArrayData;
    }
    
    public Object G$getPrototypeOf() {
        return this.getPrototypeOf;
    }
    
    public void S$getPrototypeOf(final Object getPrototypeOf) {
        this.getPrototypeOf = getPrototypeOf;
    }
    
    public Object G$setPrototypeOf() {
        return this.setPrototypeOf;
    }
    
    public void S$setPrototypeOf(final Object setPrototypeOf) {
        this.setPrototypeOf = setPrototypeOf;
    }
    
    public Object G$getOwnPropertyDescriptor() {
        return this.getOwnPropertyDescriptor;
    }
    
    public void S$getOwnPropertyDescriptor(final Object getOwnPropertyDescriptor) {
        this.getOwnPropertyDescriptor = getOwnPropertyDescriptor;
    }
    
    public Object G$getOwnPropertyNames() {
        return this.getOwnPropertyNames;
    }
    
    public void S$getOwnPropertyNames(final Object getOwnPropertyNames) {
        this.getOwnPropertyNames = getOwnPropertyNames;
    }
    
    public Object G$create() {
        return this.create;
    }
    
    public void S$create(final Object create) {
        this.create = create;
    }
    
    public Object G$defineProperty() {
        return this.defineProperty;
    }
    
    public void S$defineProperty(final Object defineProperty) {
        this.defineProperty = defineProperty;
    }
    
    public Object G$defineProperties() {
        return this.defineProperties;
    }
    
    public void S$defineProperties(final Object defineProperties) {
        this.defineProperties = defineProperties;
    }
    
    public Object G$seal() {
        return this.seal;
    }
    
    public void S$seal(final Object seal) {
        this.seal = seal;
    }
    
    public Object G$freeze() {
        return this.freeze;
    }
    
    public void S$freeze(final Object freeze) {
        this.freeze = freeze;
    }
    
    public Object G$preventExtensions() {
        return this.preventExtensions;
    }
    
    public void S$preventExtensions(final Object preventExtensions) {
        this.preventExtensions = preventExtensions;
    }
    
    public Object G$isSealed() {
        return this.isSealed;
    }
    
    public void S$isSealed(final Object isSealed) {
        this.isSealed = isSealed;
    }
    
    public Object G$isFrozen() {
        return this.isFrozen;
    }
    
    public void S$isFrozen(final Object isFrozen) {
        this.isFrozen = isFrozen;
    }
    
    public Object G$isExtensible() {
        return this.isExtensible;
    }
    
    public void S$isExtensible(final Object isExtensible) {
        this.isExtensible = isExtensible;
    }
    
    public Object G$keys() {
        return this.keys;
    }
    
    public void S$keys(final Object keys) {
        this.keys = keys;
    }
    
    public Object G$bindProperties() {
        return this.bindProperties;
    }
    
    public void S$bindProperties(final Object bindProperties) {
        this.bindProperties = bindProperties;
    }
    
    static {
        final ArrayList properties = new ArrayList(16);
        properties.add(AccessorProperty.create("setIndexedPropertiesToExternalArrayData", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_f6.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_f7.HANDLE));
        properties.add(AccessorProperty.create("getPrototypeOf", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_f8.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_f9.HANDLE));
        properties.add(AccessorProperty.create("setPrototypeOf", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_fa.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_fb.HANDLE));
        properties.add(AccessorProperty.create("getOwnPropertyDescriptor", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_fc.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_fd.HANDLE));
        properties.add(AccessorProperty.create("getOwnPropertyNames", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_fe.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_ff.HANDLE));
        properties.add(AccessorProperty.create("create", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_100.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_101.HANDLE));
        properties.add(AccessorProperty.create("defineProperty", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_102.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_103.HANDLE));
        properties.add(AccessorProperty.create("defineProperties", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_104.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_105.HANDLE));
        properties.add(AccessorProperty.create("seal", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_106.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_107.HANDLE));
        properties.add(AccessorProperty.create("freeze", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_108.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_109.HANDLE));
        properties.add(AccessorProperty.create("preventExtensions", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_10a.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_10b.HANDLE));
        properties.add(AccessorProperty.create("isSealed", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_10c.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_10d.HANDLE));
        properties.add(AccessorProperty.create("isFrozen", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_10e.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_10f.HANDLE));
        properties.add(AccessorProperty.create("isExtensible", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_110.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_111.HANDLE));
        properties.add(AccessorProperty.create("keys", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_112.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_113.HANDLE));
        properties.add(AccessorProperty.create("bindProperties", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_114.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_115.HANDLE));
        $nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    NativeObject$Constructor() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'jdk/nashorn/internal/objects/NativeObject$Constructor.<init>:()V'.
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:67)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:729)
        //     at com.strobel.assembler.metadata.MethodDefinition.getBody(MethodDefinition.java:83)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:194)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$MethodHandleEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1590)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:299)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:63)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_f6__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_f6
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_f6__.findVirtual(NativeObject$Constructor.class, "G$setIndexedPropertiesToExternalArrayData", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_f6.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_f7__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_f7
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_f7__.findVirtual(NativeObject$Constructor.class, "S$setIndexedPropertiesToExternalArrayData", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_f7.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_f8__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_f8
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_f8__.findVirtual(NativeObject$Constructor.class, "G$getPrototypeOf", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_f8.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_f9__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_f9
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_f9__.findVirtual(NativeObject$Constructor.class, "S$getPrototypeOf", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_f9.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_fa__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_fa
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_fa__.findVirtual(NativeObject$Constructor.class, "G$setPrototypeOf", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_fa.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_fb__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_fb
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_fb__.findVirtual(NativeObject$Constructor.class, "S$setPrototypeOf", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_fb.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_fc__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_fc
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_fc__.findVirtual(NativeObject$Constructor.class, "G$getOwnPropertyDescriptor", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_fc.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_fd__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_fd
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_fd__.findVirtual(NativeObject$Constructor.class, "S$getOwnPropertyDescriptor", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_fd.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_fe__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_fe
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_fe__.findVirtual(NativeObject$Constructor.class, "G$getOwnPropertyNames", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_fe.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_ff__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_ff
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_ff__.findVirtual(NativeObject$Constructor.class, "S$getOwnPropertyNames", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_ff.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_100__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_100
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_100__.findVirtual(NativeObject$Constructor.class, "G$create", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_100.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_101__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_101
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_101__.findVirtual(NativeObject$Constructor.class, "S$create", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_101.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_102__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_102
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_102__.findVirtual(NativeObject$Constructor.class, "G$defineProperty", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_102.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_103__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_103
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_103__.findVirtual(NativeObject$Constructor.class, "S$defineProperty", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_103.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_104__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_104
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_104__.findVirtual(NativeObject$Constructor.class, "G$defineProperties", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_104.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_105__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_105
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_105__.findVirtual(NativeObject$Constructor.class, "S$defineProperties", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_105.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_106__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_106
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_106__.findVirtual(NativeObject$Constructor.class, "G$seal", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_106.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_107__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_107
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_107__.findVirtual(NativeObject$Constructor.class, "S$seal", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_107.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_108__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_108
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_108__.findVirtual(NativeObject$Constructor.class, "G$freeze", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_108.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_109__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_109
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_109__.findVirtual(NativeObject$Constructor.class, "S$freeze", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_109.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_10a__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_10a
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_10a__.findVirtual(NativeObject$Constructor.class, "G$preventExtensions", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_10a.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_10b__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_10b
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_10b__.findVirtual(NativeObject$Constructor.class, "S$preventExtensions", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_10b.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_10c__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_10c
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_10c__.findVirtual(NativeObject$Constructor.class, "G$isSealed", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_10c.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_10d__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_10d
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_10d__.findVirtual(NativeObject$Constructor.class, "S$isSealed", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_10d.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_10e__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_10e
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_10e__.findVirtual(NativeObject$Constructor.class, "G$isFrozen", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_10e.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_10f__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_10f
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_10f__.findVirtual(NativeObject$Constructor.class, "S$isFrozen", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_10f.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_110__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_110
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_110__.findVirtual(NativeObject$Constructor.class, "G$isExtensible", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_110.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_111__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_111
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_111__.findVirtual(NativeObject$Constructor.class, "S$isExtensible", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_111.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_112__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_112
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_112__.findVirtual(NativeObject$Constructor.class, "G$keys", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_112.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_113__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_113
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_113__.findVirtual(NativeObject$Constructor.class, "S$keys", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_113.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_114__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_114
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_114__.findVirtual(NativeObject$Constructor.class, "G$bindProperties", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_114.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_115__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_115
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeObject$Constructor.__PROCYON__LOOKUP_115__.findVirtual(NativeObject$Constructor.class, "S$bindProperties", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_115.HANDLE = handle;
        }
    }
}
