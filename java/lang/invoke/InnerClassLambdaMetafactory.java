package java.lang.invoke;

import sun.security.action.GetPropertyAction;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import java.security.AccessControlContext;
import java.util.PropertyPermission;
import java.io.FilePermission;
import java.security.Permission;
import java.util.LinkedHashSet;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Constructor;
import sun.invoke.util.BytecodeDescriptor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import java.util.concurrent.atomic.AtomicInteger;
import sun.misc.Unsafe;

final class InnerClassLambdaMetafactory extends AbstractValidatingLambdaMetafactory
{
    private static final Unsafe UNSAFE;
    private static final int CLASSFILE_VERSION = 52;
    private static final String METHOD_DESCRIPTOR_VOID;
    private static final String JAVA_LANG_OBJECT = "java/lang/Object";
    private static final String NAME_CTOR = "<init>";
    private static final String NAME_FACTORY = "get$Lambda";
    private static final String NAME_SERIALIZED_LAMBDA = "java/lang/invoke/SerializedLambda";
    private static final String NAME_NOT_SERIALIZABLE_EXCEPTION = "java/io/NotSerializableException";
    private static final String DESCR_METHOD_WRITE_REPLACE = "()Ljava/lang/Object;";
    private static final String DESCR_METHOD_WRITE_OBJECT = "(Ljava/io/ObjectOutputStream;)V";
    private static final String DESCR_METHOD_READ_OBJECT = "(Ljava/io/ObjectInputStream;)V";
    private static final String NAME_METHOD_WRITE_REPLACE = "writeReplace";
    private static final String NAME_METHOD_READ_OBJECT = "readObject";
    private static final String NAME_METHOD_WRITE_OBJECT = "writeObject";
    private static final String DESCR_CTOR_SERIALIZED_LAMBDA;
    private static final String DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION;
    private static final String[] SER_HOSTILE_EXCEPTIONS;
    private static final String[] EMPTY_STRING_ARRAY;
    private static final AtomicInteger counter;
    private static final ProxyClassesDumper dumper;
    private final String implMethodClassName;
    private final String implMethodName;
    private final String implMethodDesc;
    private final Class<?> implMethodReturnClass;
    private final MethodType constructorType;
    private final ClassWriter cw;
    private final String[] argNames;
    private final String[] argDescs;
    private final String lambdaClassName;
    
    public InnerClassLambdaMetafactory(final MethodHandles.Lookup lookup, final MethodType methodType, final String s, final MethodType methodType2, final MethodHandle methodHandle, final MethodType methodType3, final boolean b, final Class<?>[] array, final MethodType[] array2) throws LambdaConversionException {
        super(lookup, methodType, s, methodType2, methodHandle, methodType3, b, array, array2);
        this.implMethodClassName = this.implDefiningClass.getName().replace('.', '/');
        this.implMethodName = this.implInfo.getName();
        this.implMethodDesc = this.implMethodType.toMethodDescriptorString();
        this.implMethodReturnClass = ((this.implKind == 8) ? this.implDefiningClass : this.implMethodType.returnType());
        this.constructorType = methodType.changeReturnType(Void.TYPE);
        this.lambdaClassName = this.targetClass.getName().replace('.', '/') + "$$Lambda$" + InnerClassLambdaMetafactory.counter.incrementAndGet();
        this.cw = new ClassWriter(1);
        final int parameterCount = methodType.parameterCount();
        if (parameterCount > 0) {
            this.argNames = new String[parameterCount];
            this.argDescs = new String[parameterCount];
            for (int i = 0; i < parameterCount; ++i) {
                this.argNames[i] = "arg$" + (i + 1);
                this.argDescs[i] = BytecodeDescriptor.unparse(methodType.parameterType(i));
            }
        }
        else {
            final String[] empty_STRING_ARRAY = InnerClassLambdaMetafactory.EMPTY_STRING_ARRAY;
            this.argDescs = empty_STRING_ARRAY;
            this.argNames = empty_STRING_ARRAY;
        }
    }
    
    @Override
    CallSite buildCallSite() throws LambdaConversionException {
        final Class<?> spinInnerClass = this.spinInnerClass();
        if (this.invokedType.parameterCount() == 0) {
            final Constructor[] array = AccessController.doPrivileged((PrivilegedAction<Constructor[]>)new PrivilegedAction<Constructor<?>[]>() {
                @Override
                public Constructor<?>[] run() {
                    final Constructor[] declaredConstructors = spinInnerClass.getDeclaredConstructors();
                    if (declaredConstructors.length == 1) {
                        declaredConstructors[0].setAccessible(true);
                    }
                    return declaredConstructors;
                }
            });
            if (array.length != 1) {
                throw new LambdaConversionException("Expected one lambda constructor for " + spinInnerClass.getCanonicalName() + ", got " + array.length);
            }
            try {
                return new ConstantCallSite(MethodHandles.constant(this.samBase, array[0].newInstance(new Object[0])));
            }
            catch (final ReflectiveOperationException ex) {
                throw new LambdaConversionException("Exception instantiating lambda object", ex);
            }
        }
        try {
            InnerClassLambdaMetafactory.UNSAFE.ensureClassInitialized(spinInnerClass);
            return new ConstantCallSite(MethodHandles.Lookup.IMPL_LOOKUP.findStatic(spinInnerClass, "get$Lambda", this.invokedType));
        }
        catch (final ReflectiveOperationException ex2) {
            throw new LambdaConversionException("Exception finding constructor", ex2);
        }
    }
    
    private Class<?> spinInnerClass() throws LambdaConversionException {
        final String replace = this.samBase.getName().replace('.', '/');
        boolean b = !this.isSerializable && Serializable.class.isAssignableFrom(this.samBase);
        String[] array;
        if (this.markerInterfaces.length == 0) {
            array = new String[] { replace };
        }
        else {
            final LinkedHashSet set = new LinkedHashSet(this.markerInterfaces.length + 1);
            set.add(replace);
            for (final Class<?> clazz : this.markerInterfaces) {
                set.add(clazz.getName().replace('.', '/'));
                b |= (!this.isSerializable && Serializable.class.isAssignableFrom(clazz));
            }
            array = (String[])set.toArray(new String[set.size()]);
        }
        this.cw.visit(52, 4144, this.lambdaClassName, null, "java/lang/Object", array);
        for (int j = 0; j < this.argDescs.length; ++j) {
            this.cw.visitField(18, this.argNames[j], this.argDescs[j], null, null).visitEnd();
        }
        this.generateConstructor();
        if (this.invokedType.parameterCount() != 0) {
            this.generateFactory();
        }
        final MethodVisitor visitMethod = this.cw.visitMethod(1, this.samMethodName, this.samMethodType.toMethodDescriptorString(), null, null);
        visitMethod.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        new ForwardingMethodGenerator(visitMethod).generate(this.samMethodType);
        if (this.additionalBridges != null) {
            for (final MethodType methodType : this.additionalBridges) {
                final MethodVisitor visitMethod2 = this.cw.visitMethod(65, this.samMethodName, methodType.toMethodDescriptorString(), null, null);
                visitMethod2.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
                new ForwardingMethodGenerator(visitMethod2).generate(methodType);
            }
        }
        if (this.isSerializable) {
            this.generateSerializationFriendlyMethods();
        }
        else if (b) {
            this.generateSerializationHostileMethods();
        }
        this.cw.visitEnd();
        final byte[] byteArray = this.cw.toByteArray();
        if (InnerClassLambdaMetafactory.dumper != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    InnerClassLambdaMetafactory.dumper.dumpClass(InnerClassLambdaMetafactory.this.lambdaClassName, byteArray);
                    return null;
                }
            }, null, new FilePermission("<<ALL FILES>>", "read, write"), new PropertyPermission("user.dir", "read"));
        }
        return InnerClassLambdaMetafactory.UNSAFE.defineAnonymousClass(this.targetClass, byteArray, null);
    }
    
    private void generateFactory() {
        final MethodVisitor visitMethod = this.cw.visitMethod(10, "get$Lambda", this.invokedType.toMethodDescriptorString(), null, null);
        visitMethod.visitCode();
        visitMethod.visitTypeInsn(187, this.lambdaClassName);
        visitMethod.visitInsn(89);
        final int parameterCount = this.invokedType.parameterCount();
        int i = 0;
        int n = 0;
        while (i < parameterCount) {
            final Class<?> parameterType = this.invokedType.parameterType(i);
            visitMethod.visitVarInsn(getLoadOpcode(parameterType), n);
            n += getParameterSize(parameterType);
            ++i;
        }
        visitMethod.visitMethodInsn(183, this.lambdaClassName, "<init>", this.constructorType.toMethodDescriptorString(), false);
        visitMethod.visitInsn(176);
        visitMethod.visitMaxs(-1, -1);
        visitMethod.visitEnd();
    }
    
    private void generateConstructor() {
        final MethodVisitor visitMethod = this.cw.visitMethod(2, "<init>", this.constructorType.toMethodDescriptorString(), null, null);
        visitMethod.visitCode();
        visitMethod.visitVarInsn(25, 0);
        visitMethod.visitMethodInsn(183, "java/lang/Object", "<init>", InnerClassLambdaMetafactory.METHOD_DESCRIPTOR_VOID, false);
        final int parameterCount = this.invokedType.parameterCount();
        int i = 0;
        int n = 0;
        while (i < parameterCount) {
            visitMethod.visitVarInsn(25, 0);
            final Class<?> parameterType = this.invokedType.parameterType(i);
            visitMethod.visitVarInsn(getLoadOpcode(parameterType), n + 1);
            n += getParameterSize(parameterType);
            visitMethod.visitFieldInsn(181, this.lambdaClassName, this.argNames[i], this.argDescs[i]);
            ++i;
        }
        visitMethod.visitInsn(177);
        visitMethod.visitMaxs(-1, -1);
        visitMethod.visitEnd();
    }
    
    private void generateSerializationFriendlyMethods() {
        final TypeConvertingMethodAdapter typeConvertingMethodAdapter = new TypeConvertingMethodAdapter(this.cw.visitMethod(18, "writeReplace", "()Ljava/lang/Object;", null, null));
        typeConvertingMethodAdapter.visitCode();
        typeConvertingMethodAdapter.visitTypeInsn(187, "java/lang/invoke/SerializedLambda");
        typeConvertingMethodAdapter.visitInsn(89);
        typeConvertingMethodAdapter.visitLdcInsn(Type.getType(this.targetClass));
        typeConvertingMethodAdapter.visitLdcInsn(this.invokedType.returnType().getName().replace('.', '/'));
        typeConvertingMethodAdapter.visitLdcInsn(this.samMethodName);
        typeConvertingMethodAdapter.visitLdcInsn(this.samMethodType.toMethodDescriptorString());
        typeConvertingMethodAdapter.visitLdcInsn(this.implInfo.getReferenceKind());
        typeConvertingMethodAdapter.visitLdcInsn(this.implInfo.getDeclaringClass().getName().replace('.', '/'));
        typeConvertingMethodAdapter.visitLdcInsn(this.implInfo.getName());
        typeConvertingMethodAdapter.visitLdcInsn(this.implInfo.getMethodType().toMethodDescriptorString());
        typeConvertingMethodAdapter.visitLdcInsn(this.instantiatedMethodType.toMethodDescriptorString());
        typeConvertingMethodAdapter.iconst(this.argDescs.length);
        typeConvertingMethodAdapter.visitTypeInsn(189, "java/lang/Object");
        for (int i = 0; i < this.argDescs.length; ++i) {
            typeConvertingMethodAdapter.visitInsn(89);
            typeConvertingMethodAdapter.iconst(i);
            typeConvertingMethodAdapter.visitVarInsn(25, 0);
            typeConvertingMethodAdapter.visitFieldInsn(180, this.lambdaClassName, this.argNames[i], this.argDescs[i]);
            typeConvertingMethodAdapter.boxIfTypePrimitive(Type.getType(this.argDescs[i]));
            typeConvertingMethodAdapter.visitInsn(83);
        }
        typeConvertingMethodAdapter.visitMethodInsn(183, "java/lang/invoke/SerializedLambda", "<init>", InnerClassLambdaMetafactory.DESCR_CTOR_SERIALIZED_LAMBDA, false);
        typeConvertingMethodAdapter.visitInsn(176);
        typeConvertingMethodAdapter.visitMaxs(-1, -1);
        typeConvertingMethodAdapter.visitEnd();
    }
    
    private void generateSerializationHostileMethods() {
        final MethodVisitor visitMethod = this.cw.visitMethod(18, "writeObject", "(Ljava/io/ObjectOutputStream;)V", null, InnerClassLambdaMetafactory.SER_HOSTILE_EXCEPTIONS);
        visitMethod.visitCode();
        visitMethod.visitTypeInsn(187, "java/io/NotSerializableException");
        visitMethod.visitInsn(89);
        visitMethod.visitLdcInsn("Non-serializable lambda");
        visitMethod.visitMethodInsn(183, "java/io/NotSerializableException", "<init>", InnerClassLambdaMetafactory.DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
        visitMethod.visitInsn(191);
        visitMethod.visitMaxs(-1, -1);
        visitMethod.visitEnd();
        final MethodVisitor visitMethod2 = this.cw.visitMethod(18, "readObject", "(Ljava/io/ObjectInputStream;)V", null, InnerClassLambdaMetafactory.SER_HOSTILE_EXCEPTIONS);
        visitMethod2.visitCode();
        visitMethod2.visitTypeInsn(187, "java/io/NotSerializableException");
        visitMethod2.visitInsn(89);
        visitMethod2.visitLdcInsn("Non-serializable lambda");
        visitMethod2.visitMethodInsn(183, "java/io/NotSerializableException", "<init>", InnerClassLambdaMetafactory.DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION, false);
        visitMethod2.visitInsn(191);
        visitMethod2.visitMaxs(-1, -1);
        visitMethod2.visitEnd();
    }
    
    static int getParameterSize(final Class<?> clazz) {
        if (clazz == Void.TYPE) {
            return 0;
        }
        if (clazz == Long.TYPE || clazz == Double.TYPE) {
            return 2;
        }
        return 1;
    }
    
    static int getLoadOpcode(final Class<?> clazz) {
        if (clazz == Void.TYPE) {
            throw new InternalError("Unexpected void type of load opcode");
        }
        return 21 + getOpcodeOffset(clazz);
    }
    
    static int getReturnOpcode(final Class<?> clazz) {
        if (clazz == Void.TYPE) {
            return 177;
        }
        return 172 + getOpcodeOffset(clazz);
    }
    
    private static int getOpcodeOffset(final Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return 4;
        }
        if (clazz == Long.TYPE) {
            return 1;
        }
        if (clazz == Float.TYPE) {
            return 2;
        }
        if (clazz == Double.TYPE) {
            return 3;
        }
        return 0;
    }
    
    static {
        UNSAFE = Unsafe.getUnsafe();
        METHOD_DESCRIPTOR_VOID = Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0]);
        DESCR_CTOR_SERIALIZED_LAMBDA = MethodType.methodType(Void.TYPE, Class.class, String.class, String.class, String.class, Integer.TYPE, String.class, String.class, String.class, String.class, Object[].class).toMethodDescriptorString();
        DESCR_CTOR_NOT_SERIALIZABLE_EXCEPTION = MethodType.methodType(Void.TYPE, String.class).toMethodDescriptorString();
        SER_HOSTILE_EXCEPTIONS = new String[] { "java/io/NotSerializableException" };
        EMPTY_STRING_ARRAY = new String[0];
        counter = new AtomicInteger(0);
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.internal.lambda.dumpProxyClasses"), null, new PropertyPermission("jdk.internal.lambda.dumpProxyClasses", "read"));
        dumper = ((null == s) ? null : ProxyClassesDumper.getInstance(s));
    }
    
    private class ForwardingMethodGenerator extends TypeConvertingMethodAdapter
    {
        ForwardingMethodGenerator(final MethodVisitor methodVisitor) {
            super(methodVisitor);
        }
        
        void generate(final MethodType methodType) {
            this.visitCode();
            if (InnerClassLambdaMetafactory.this.implKind == 8) {
                this.visitTypeInsn(187, InnerClassLambdaMetafactory.this.implMethodClassName);
                this.visitInsn(89);
            }
            for (int i = 0; i < InnerClassLambdaMetafactory.this.argNames.length; ++i) {
                this.visitVarInsn(25, 0);
                this.visitFieldInsn(180, InnerClassLambdaMetafactory.this.lambdaClassName, InnerClassLambdaMetafactory.this.argNames[i], InnerClassLambdaMetafactory.this.argDescs[i]);
            }
            this.convertArgumentTypes(methodType);
            this.visitMethodInsn(this.invocationOpcode(), InnerClassLambdaMetafactory.this.implMethodClassName, InnerClassLambdaMetafactory.this.implMethodName, InnerClassLambdaMetafactory.this.implMethodDesc, InnerClassLambdaMetafactory.this.implDefiningClass.isInterface());
            final Class<?> returnType = methodType.returnType();
            this.convertType(InnerClassLambdaMetafactory.this.implMethodReturnClass, returnType, returnType);
            this.visitInsn(InnerClassLambdaMetafactory.getReturnOpcode(returnType));
            this.visitMaxs(-1, -1);
            this.visitEnd();
        }
        
        private void convertArgumentTypes(final MethodType methodType) {
            int n = 0;
            final int n2;
            if ((n2 = ((InnerClassLambdaMetafactory.this.implIsInstanceMethod && InnerClassLambdaMetafactory.this.invokedType.parameterCount() == 0) ? 1 : 0)) != 0) {
                final Class<?> parameterType = methodType.parameterType(0);
                this.visitVarInsn(InnerClassLambdaMetafactory.getLoadOpcode(parameterType), n + 1);
                n += InnerClassLambdaMetafactory.getParameterSize(parameterType);
                this.convertType(parameterType, InnerClassLambdaMetafactory.this.implDefiningClass, InnerClassLambdaMetafactory.this.instantiatedMethodType.parameterType(0));
            }
            final int parameterCount = methodType.parameterCount();
            final int n3 = InnerClassLambdaMetafactory.this.implMethodType.parameterCount() - parameterCount;
            for (int i = n2; i < parameterCount; ++i) {
                final Class<?> parameterType2 = methodType.parameterType(i);
                this.visitVarInsn(InnerClassLambdaMetafactory.getLoadOpcode(parameterType2), n + 1);
                n += InnerClassLambdaMetafactory.getParameterSize(parameterType2);
                this.convertType(parameterType2, InnerClassLambdaMetafactory.this.implMethodType.parameterType(n3 + i), InnerClassLambdaMetafactory.this.instantiatedMethodType.parameterType(i));
            }
        }
        
        private int invocationOpcode() throws InternalError {
            switch (InnerClassLambdaMetafactory.this.implKind) {
                case 6: {
                    return 184;
                }
                case 8: {
                    return 183;
                }
                case 5: {
                    return 182;
                }
                case 9: {
                    return 185;
                }
                case 7: {
                    return 183;
                }
                default: {
                    throw new InternalError("Unexpected invocation kind: " + InnerClassLambdaMetafactory.this.implKind);
                }
            }
        }
    }
}
