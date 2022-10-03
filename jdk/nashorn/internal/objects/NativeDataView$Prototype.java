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

final class NativeDataView$Prototype extends PrototypeObject
{
    private Object getInt8;
    private Object getUint8;
    private Object getInt16;
    private Object getUint16;
    private Object getInt32;
    private Object getUint32;
    private Object getFloat32;
    private Object getFloat64;
    private Object setInt8;
    private Object setUint8;
    private Object setInt16;
    private Object setUint16;
    private Object setInt32;
    private Object setUint32;
    private Object setFloat32;
    private Object setFloat64;
    private static final PropertyMap $nasgenmap$;
    
    public Object G$getInt8() {
        return this.getInt8;
    }
    
    public void S$getInt8(final Object getInt8) {
        this.getInt8 = getInt8;
    }
    
    public Object G$getUint8() {
        return this.getUint8;
    }
    
    public void S$getUint8(final Object getUint8) {
        this.getUint8 = getUint8;
    }
    
    public Object G$getInt16() {
        return this.getInt16;
    }
    
    public void S$getInt16(final Object getInt16) {
        this.getInt16 = getInt16;
    }
    
    public Object G$getUint16() {
        return this.getUint16;
    }
    
    public void S$getUint16(final Object getUint16) {
        this.getUint16 = getUint16;
    }
    
    public Object G$getInt32() {
        return this.getInt32;
    }
    
    public void S$getInt32(final Object getInt32) {
        this.getInt32 = getInt32;
    }
    
    public Object G$getUint32() {
        return this.getUint32;
    }
    
    public void S$getUint32(final Object getUint32) {
        this.getUint32 = getUint32;
    }
    
    public Object G$getFloat32() {
        return this.getFloat32;
    }
    
    public void S$getFloat32(final Object getFloat32) {
        this.getFloat32 = getFloat32;
    }
    
    public Object G$getFloat64() {
        return this.getFloat64;
    }
    
    public void S$getFloat64(final Object getFloat64) {
        this.getFloat64 = getFloat64;
    }
    
    public Object G$setInt8() {
        return this.setInt8;
    }
    
    public void S$setInt8(final Object setInt8) {
        this.setInt8 = setInt8;
    }
    
    public Object G$setUint8() {
        return this.setUint8;
    }
    
    public void S$setUint8(final Object setUint8) {
        this.setUint8 = setUint8;
    }
    
    public Object G$setInt16() {
        return this.setInt16;
    }
    
    public void S$setInt16(final Object setInt16) {
        this.setInt16 = setInt16;
    }
    
    public Object G$setUint16() {
        return this.setUint16;
    }
    
    public void S$setUint16(final Object setUint16) {
        this.setUint16 = setUint16;
    }
    
    public Object G$setInt32() {
        return this.setInt32;
    }
    
    public void S$setInt32(final Object setInt32) {
        this.setInt32 = setInt32;
    }
    
    public Object G$setUint32() {
        return this.setUint32;
    }
    
    public void S$setUint32(final Object setUint32) {
        this.setUint32 = setUint32;
    }
    
    public Object G$setFloat32() {
        return this.setFloat32;
    }
    
    public void S$setFloat32(final Object setFloat32) {
        this.setFloat32 = setFloat32;
    }
    
    public Object G$setFloat64() {
        return this.setFloat64;
    }
    
    public void S$setFloat64(final Object setFloat64) {
        this.setFloat64 = setFloat64;
    }
    
    static {
        final ArrayList properties = new ArrayList(16);
        properties.add(AccessorProperty.create("getInt8", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_32.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_33.HANDLE));
        properties.add(AccessorProperty.create("getUint8", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_34.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_35.HANDLE));
        properties.add(AccessorProperty.create("getInt16", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_36.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_37.HANDLE));
        properties.add(AccessorProperty.create("getUint16", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_38.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_39.HANDLE));
        properties.add(AccessorProperty.create("getInt32", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_3a.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_3b.HANDLE));
        properties.add(AccessorProperty.create("getUint32", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_3c.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_3d.HANDLE));
        properties.add(AccessorProperty.create("getFloat32", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_3e.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_3f.HANDLE));
        properties.add(AccessorProperty.create("getFloat64", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_40.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_41.HANDLE));
        properties.add(AccessorProperty.create("setInt8", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_42.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_43.HANDLE));
        properties.add(AccessorProperty.create("setUint8", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_44.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_45.HANDLE));
        properties.add(AccessorProperty.create("setInt16", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_46.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_47.HANDLE));
        properties.add(AccessorProperty.create("setUint16", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_48.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_49.HANDLE));
        properties.add(AccessorProperty.create("setInt32", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_4a.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_4b.HANDLE));
        properties.add(AccessorProperty.create("setUint32", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_4c.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_4d.HANDLE));
        properties.add(AccessorProperty.create("setFloat32", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_4e.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_4f.HANDLE));
        properties.add(AccessorProperty.create("setFloat64", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_50.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_51.HANDLE));
        $nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    NativeDataView$Prototype() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'jdk/nashorn/internal/objects/NativeDataView$Prototype.<init>:()V'.
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
        // Caused by: java.lang.ClassCastException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public String getClassName() {
        return "DataView";
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_32__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_32
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_32__.findVirtual(NativeDataView$Prototype.class, "G$getInt8", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_32.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_33__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_33
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_33__.findVirtual(NativeDataView$Prototype.class, "S$getInt8", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_33.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_34__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_34
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_34__.findVirtual(NativeDataView$Prototype.class, "G$getUint8", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_34.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_35__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_35
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_35__.findVirtual(NativeDataView$Prototype.class, "S$getUint8", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_35.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_36__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_36
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_36__.findVirtual(NativeDataView$Prototype.class, "G$getInt16", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_36.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_37__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_37
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_37__.findVirtual(NativeDataView$Prototype.class, "S$getInt16", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_37.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_38__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_38
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_38__.findVirtual(NativeDataView$Prototype.class, "G$getUint16", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_38.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_39__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_39
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_39__.findVirtual(NativeDataView$Prototype.class, "S$getUint16", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_39.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_3a__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_3a
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_3a__.findVirtual(NativeDataView$Prototype.class, "G$getInt32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_3a.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_3b__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_3b
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_3b__.findVirtual(NativeDataView$Prototype.class, "S$getInt32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_3b.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_3c__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_3c
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_3c__.findVirtual(NativeDataView$Prototype.class, "G$getUint32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_3c.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_3d__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_3d
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_3d__.findVirtual(NativeDataView$Prototype.class, "S$getUint32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_3d.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_3e__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_3e
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_3e__.findVirtual(NativeDataView$Prototype.class, "G$getFloat32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_3e.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_3f__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_3f
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_3f__.findVirtual(NativeDataView$Prototype.class, "S$getFloat32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_3f.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_40__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_40
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_40__.findVirtual(NativeDataView$Prototype.class, "G$getFloat64", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_40.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_41__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_41
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_41__.findVirtual(NativeDataView$Prototype.class, "S$getFloat64", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_41.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_42__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_42
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_42__.findVirtual(NativeDataView$Prototype.class, "G$setInt8", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_42.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_43__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_43
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_43__.findVirtual(NativeDataView$Prototype.class, "S$setInt8", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_43.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_44__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_44
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_44__.findVirtual(NativeDataView$Prototype.class, "G$setUint8", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_44.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_45__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_45
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_45__.findVirtual(NativeDataView$Prototype.class, "S$setUint8", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_45.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_46__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_46
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_46__.findVirtual(NativeDataView$Prototype.class, "G$setInt16", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_46.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_47__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_47
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_47__.findVirtual(NativeDataView$Prototype.class, "S$setInt16", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_47.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_48__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_48
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_48__.findVirtual(NativeDataView$Prototype.class, "G$setUint16", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_48.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_49__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_49
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_49__.findVirtual(NativeDataView$Prototype.class, "S$setUint16", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_49.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_4a__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_4a
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_4a__.findVirtual(NativeDataView$Prototype.class, "G$setInt32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_4a.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_4b__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_4b
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_4b__.findVirtual(NativeDataView$Prototype.class, "S$setInt32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_4b.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_4c__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_4c
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_4c__.findVirtual(NativeDataView$Prototype.class, "G$setUint32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_4c.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_4d__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_4d
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_4d__.findVirtual(NativeDataView$Prototype.class, "S$setUint32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_4d.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_4e__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_4e
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_4e__.findVirtual(NativeDataView$Prototype.class, "G$setFloat32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_4e.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_4f__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_4f
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_4f__.findVirtual(NativeDataView$Prototype.class, "S$setFloat32", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_4f.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_50__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_50
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_50__.findVirtual(NativeDataView$Prototype.class, "G$setFloat64", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_50.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_51__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_51
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeDataView$Prototype.__PROCYON__LOOKUP_51__.findVirtual(NativeDataView$Prototype.class, "S$setFloat64", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_51.HANDLE = handle;
        }
    }
}
