package jdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import jdk.nashorn.internal.runtime.AccessorProperty;
import java.util.ArrayList;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.ScriptObject;

final class NativeJava$Constructor extends ScriptObject
{
    private Object isType;
    private Object synchronizedFunc;
    private Object isJavaMethod;
    private Object isJavaFunction;
    private Object isJavaObject;
    private Object isScriptObject;
    private Object isScriptFunction;
    private Object type;
    private Object typeName;
    private Object to;
    private Object from;
    private Object extend;
    private Object _super;
    private Object asJSONCompatible;
    private static final PropertyMap $nasgenmap$;
    
    public Object G$isType() {
        return this.isType;
    }
    
    public void S$isType(final Object isType) {
        this.isType = isType;
    }
    
    public Object G$synchronizedFunc() {
        return this.synchronizedFunc;
    }
    
    public void S$synchronizedFunc(final Object synchronizedFunc) {
        this.synchronizedFunc = synchronizedFunc;
    }
    
    public Object G$isJavaMethod() {
        return this.isJavaMethod;
    }
    
    public void S$isJavaMethod(final Object isJavaMethod) {
        this.isJavaMethod = isJavaMethod;
    }
    
    public Object G$isJavaFunction() {
        return this.isJavaFunction;
    }
    
    public void S$isJavaFunction(final Object isJavaFunction) {
        this.isJavaFunction = isJavaFunction;
    }
    
    public Object G$isJavaObject() {
        return this.isJavaObject;
    }
    
    public void S$isJavaObject(final Object isJavaObject) {
        this.isJavaObject = isJavaObject;
    }
    
    public Object G$isScriptObject() {
        return this.isScriptObject;
    }
    
    public void S$isScriptObject(final Object isScriptObject) {
        this.isScriptObject = isScriptObject;
    }
    
    public Object G$isScriptFunction() {
        return this.isScriptFunction;
    }
    
    public void S$isScriptFunction(final Object isScriptFunction) {
        this.isScriptFunction = isScriptFunction;
    }
    
    public Object G$type() {
        return this.type;
    }
    
    public void S$type(final Object type) {
        this.type = type;
    }
    
    public Object G$typeName() {
        return this.typeName;
    }
    
    public void S$typeName(final Object typeName) {
        this.typeName = typeName;
    }
    
    public Object G$to() {
        return this.to;
    }
    
    public void S$to(final Object to) {
        this.to = to;
    }
    
    public Object G$from() {
        return this.from;
    }
    
    public void S$from(final Object from) {
        this.from = from;
    }
    
    public Object G$extend() {
        return this.extend;
    }
    
    public void S$extend(final Object extend) {
        this.extend = extend;
    }
    
    public Object G$_super() {
        return this._super;
    }
    
    public void S$_super(final Object super1) {
        this._super = super1;
    }
    
    public Object G$asJSONCompatible() {
        return this.asJSONCompatible;
    }
    
    public void S$asJSONCompatible(final Object asJSONCompatible) {
        this.asJSONCompatible = asJSONCompatible;
    }
    
    static {
        final ArrayList properties = new ArrayList(14);
        properties.add(AccessorProperty.create("isType", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_b2.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_b3.HANDLE));
        properties.add(AccessorProperty.create("synchronized", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_b4.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_b5.HANDLE));
        properties.add(AccessorProperty.create("isJavaMethod", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_b6.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_b7.HANDLE));
        properties.add(AccessorProperty.create("isJavaFunction", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_b8.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_b9.HANDLE));
        properties.add(AccessorProperty.create("isJavaObject", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_ba.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_bb.HANDLE));
        properties.add(AccessorProperty.create("isScriptObject", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_bc.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_bd.HANDLE));
        properties.add(AccessorProperty.create("isScriptFunction", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_be.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_bf.HANDLE));
        properties.add(AccessorProperty.create("type", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_c0.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_c1.HANDLE));
        properties.add(AccessorProperty.create("typeName", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_c2.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_c3.HANDLE));
        properties.add(AccessorProperty.create("to", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_c4.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_c5.HANDLE));
        properties.add(AccessorProperty.create("from", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_c6.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_c7.HANDLE));
        properties.add(AccessorProperty.create("extend", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_c8.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_c9.HANDLE));
        properties.add(AccessorProperty.create("super", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_ca.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_cb.HANDLE));
        properties.add(AccessorProperty.create("asJSONCompatible", 2, /* ldc_method_handle(!) */ProcyonConstantHelper_cc.HANDLE, /* ldc_method_handle(!) */ProcyonConstantHelper_cd.HANDLE));
        $nasgenmap$ = PropertyMap.newMap(properties);
    }
    
    NativeJava$Constructor() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'jdk/nashorn/internal/objects/NativeJava$Constructor.<init>:()V'.
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
    
    @Override
    public String getClassName() {
        return "Java";
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b2__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b2
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_b2__.findVirtual(NativeJava$Constructor.class, "G$isType", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b2.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b3__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b3
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_b3__.findVirtual(NativeJava$Constructor.class, "S$isType", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b3.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b4__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b4
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_b4__.findVirtual(NativeJava$Constructor.class, "G$synchronizedFunc", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b4.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b5__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b5
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_b5__.findVirtual(NativeJava$Constructor.class, "S$synchronizedFunc", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b5.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b6__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b6
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_b6__.findVirtual(NativeJava$Constructor.class, "G$isJavaMethod", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b6.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b7__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b7
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_b7__.findVirtual(NativeJava$Constructor.class, "S$isJavaMethod", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b7.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b8__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b8
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_b8__.findVirtual(NativeJava$Constructor.class, "G$isJavaFunction", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b8.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_b9__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_b9
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_b9__.findVirtual(NativeJava$Constructor.class, "S$isJavaFunction", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_b9.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_ba__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_ba
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_ba__.findVirtual(NativeJava$Constructor.class, "G$isJavaObject", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_ba.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_bb__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_bb
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_bb__.findVirtual(NativeJava$Constructor.class, "S$isJavaObject", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_bb.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_bc__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_bc
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_bc__.findVirtual(NativeJava$Constructor.class, "G$isScriptObject", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_bc.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_bd__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_bd
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_bd__.findVirtual(NativeJava$Constructor.class, "S$isScriptObject", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_bd.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_be__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_be
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_be__.findVirtual(NativeJava$Constructor.class, "G$isScriptFunction", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_be.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_bf__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_bf
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_bf__.findVirtual(NativeJava$Constructor.class, "S$isScriptFunction", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_bf.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c0__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c0
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c0__.findVirtual(NativeJava$Constructor.class, "G$type", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c0.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c1__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c1
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c1__.findVirtual(NativeJava$Constructor.class, "S$type", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c1.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c2__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c2
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c2__.findVirtual(NativeJava$Constructor.class, "G$typeName", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c2.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c3__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c3
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c3__.findVirtual(NativeJava$Constructor.class, "S$typeName", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c3.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c4__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c4
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c4__.findVirtual(NativeJava$Constructor.class, "G$to", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c4.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c5__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c5
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c5__.findVirtual(NativeJava$Constructor.class, "S$to", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c5.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c6__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c6
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c6__.findVirtual(NativeJava$Constructor.class, "G$from", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c6.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c7__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c7
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c7__.findVirtual(NativeJava$Constructor.class, "S$from", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c7.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c8__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c8
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c8__.findVirtual(NativeJava$Constructor.class, "G$extend", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c8.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_c9__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_c9
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_c9__.findVirtual(NativeJava$Constructor.class, "S$extend", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_c9.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_ca__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_ca
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_ca__.findVirtual(NativeJava$Constructor.class, "G$_super", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_ca.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_cb__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_cb
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_cb__.findVirtual(NativeJava$Constructor.class, "S$_super", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_cb.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_cc__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_cc
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_cc__.findVirtual(NativeJava$Constructor.class, "G$asJSONCompatible", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_cc.HANDLE = handle;
        }
    }
    
    private static final MethodHandles.Lookup __PROCYON__LOOKUP_cd__ = MethodHandles.lookup();
    
    // This helper class was generated by Procyon to approximate the behavior of a
    // MethodHandle constant that cannot (currently) be represented in Java code.
    private static final class ProcyonConstantHelper_cd
    {
        static final MethodHandle HANDLE;
        
        static {
            MethodHandle handle;
            final MethodType type = MethodType.methodType(void.class, Object.class);
            try {
                handle = NativeJava$Constructor.__PROCYON__LOOKUP_cd__.findVirtual(NativeJava$Constructor.class, "S$asJSONCompatible", type);
            }
            catch (final ReflectiveOperationException e) {
                handle = MethodHandles.permuteArguments(MethodHandles.insertArguments(MethodHandles.throwException(type.returnType(), e.getClass()), 0, e), type);
            }
            ProcyonConstantHelper_cd.HANDLE = handle;
        }
    }
}
