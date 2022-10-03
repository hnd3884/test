package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;

class MethodAccessorGenerator extends AccessorGenerator
{
    private static final short NUM_BASE_CPOOL_ENTRIES = 12;
    private static final short NUM_METHODS = 2;
    private static final short NUM_SERIALIZATION_CPOOL_ENTRIES = 2;
    private static volatile int methodSymnum;
    private static volatile int constructorSymnum;
    private static volatile int serializationConstructorSymnum;
    private Class<?> declaringClass;
    private Class<?>[] parameterTypes;
    private Class<?> returnType;
    private boolean isConstructor;
    private boolean forSerialization;
    private short targetMethodRef;
    private short invokeIdx;
    private short invokeDescriptorIdx;
    private short nonPrimitiveParametersBaseIdx;
    
    public MethodAccessor generateMethod(final Class<?> clazz, final String s, final Class<?>[] array, final Class<?> clazz2, final Class<?>[] array2, final int n) {
        return (MethodAccessor)this.generate(clazz, s, array, clazz2, array2, n, false, false, null);
    }
    
    public ConstructorAccessor generateConstructor(final Class<?> clazz, final Class<?>[] array, final Class<?>[] array2, final int n) {
        return (ConstructorAccessor)this.generate(clazz, "<init>", array, Void.TYPE, array2, n, true, false, null);
    }
    
    public SerializationConstructorAccessorImpl generateSerializationConstructor(final Class<?> clazz, final Class<?>[] array, final Class<?>[] array2, final int n, final Class<?> clazz2) {
        return (SerializationConstructorAccessorImpl)this.generate(clazz, "<init>", array, Void.TYPE, array2, n, true, true, clazz2);
    }
    
    private MagicAccessorImpl generate(final Class<?> declaringClass, final String s, final Class<?>[] parameterTypes, final Class<?> returnType, final Class<?>[] array, final int modifiers, final boolean isConstructor, final boolean forSerialization, final Class<?> clazz) {
        final ByteVector create = ByteVectorFactory.create();
        this.asm = new ClassFileAssembler(create);
        this.declaringClass = declaringClass;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.modifiers = modifiers;
        this.isConstructor = isConstructor;
        this.forSerialization = forSerialization;
        this.asm.emitMagicAndVersion();
        short n = 42;
        final boolean usesPrimitiveTypes = this.usesPrimitiveTypes();
        if (usesPrimitiveTypes) {
            n += 72;
        }
        if (forSerialization) {
            n += 2;
        }
        final short n2 = (short)(n + (short)(2 * this.numNonPrimitiveParameterTypes()));
        this.asm.emitShort(AccessorGenerator.add(n2, (short)1));
        final String generateName = generateName(isConstructor, forSerialization);
        this.asm.emitConstantPoolUTF8(generateName);
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.thisClass = this.asm.cpi();
        if (isConstructor) {
            if (forSerialization) {
                this.asm.emitConstantPoolUTF8("sun/reflect/SerializationConstructorAccessorImpl");
            }
            else {
                this.asm.emitConstantPoolUTF8("sun/reflect/ConstructorAccessorImpl");
            }
        }
        else {
            this.asm.emitConstantPoolUTF8("sun/reflect/MethodAccessorImpl");
        }
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.superClass = this.asm.cpi();
        this.asm.emitConstantPoolUTF8(AccessorGenerator.getClassName(declaringClass, false));
        this.asm.emitConstantPoolClass(this.asm.cpi());
        this.targetClass = this.asm.cpi();
        short cpi = 0;
        if (forSerialization) {
            this.asm.emitConstantPoolUTF8(AccessorGenerator.getClassName(clazz, false));
            this.asm.emitConstantPoolClass(this.asm.cpi());
            cpi = this.asm.cpi();
        }
        this.asm.emitConstantPoolUTF8(s);
        this.asm.emitConstantPoolUTF8(this.buildInternalSignature());
        this.asm.emitConstantPoolNameAndType(AccessorGenerator.sub(this.asm.cpi(), (short)1), this.asm.cpi());
        if (this.isInterface()) {
            this.asm.emitConstantPoolInterfaceMethodref(this.targetClass, this.asm.cpi());
        }
        else if (forSerialization) {
            this.asm.emitConstantPoolMethodref(cpi, this.asm.cpi());
        }
        else {
            this.asm.emitConstantPoolMethodref(this.targetClass, this.asm.cpi());
        }
        this.targetMethodRef = this.asm.cpi();
        if (isConstructor) {
            this.asm.emitConstantPoolUTF8("newInstance");
        }
        else {
            this.asm.emitConstantPoolUTF8("invoke");
        }
        this.invokeIdx = this.asm.cpi();
        if (isConstructor) {
            this.asm.emitConstantPoolUTF8("([Ljava/lang/Object;)Ljava/lang/Object;");
        }
        else {
            this.asm.emitConstantPoolUTF8("(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        }
        this.invokeDescriptorIdx = this.asm.cpi();
        this.nonPrimitiveParametersBaseIdx = AccessorGenerator.add(this.asm.cpi(), (short)2);
        for (int i = 0; i < parameterTypes.length; ++i) {
            final Class<?> clazz2 = parameterTypes[i];
            if (!AccessorGenerator.isPrimitive(clazz2)) {
                this.asm.emitConstantPoolUTF8(AccessorGenerator.getClassName(clazz2, false));
                this.asm.emitConstantPoolClass(this.asm.cpi());
            }
        }
        this.emitCommonConstantPoolEntries();
        if (usesPrimitiveTypes) {
            this.emitBoxingContantPoolEntries();
        }
        if (this.asm.cpi() != n2) {
            throw new InternalError("Adjust this code (cpi = " + this.asm.cpi() + ", numCPEntries = " + n2 + ")");
        }
        this.asm.emitShort((short)1);
        this.asm.emitShort(this.thisClass);
        this.asm.emitShort(this.superClass);
        this.asm.emitShort((short)0);
        this.asm.emitShort((short)0);
        this.asm.emitShort((short)2);
        this.emitConstructor();
        this.emitInvoke();
        this.asm.emitShort((short)0);
        create.trim();
        return AccessController.doPrivileged((PrivilegedAction<MagicAccessorImpl>)new PrivilegedAction<MagicAccessorImpl>() {
            final /* synthetic */ byte[] val$bytes = create.getData();
            
            @Override
            public MagicAccessorImpl run() {
                try {
                    return (MagicAccessorImpl)ClassDefiner.defineClass(generateName, this.val$bytes, 0, this.val$bytes.length, declaringClass.getClassLoader()).newInstance();
                }
                catch (final InstantiationException | IllegalAccessException ex) {
                    throw new InternalError((Throwable)ex);
                }
            }
        });
    }
    
    private void emitInvoke() {
        if (this.parameterTypes.length > 65535) {
            throw new InternalError("Can't handle more than 65535 parameters");
        }
        final ClassFileAssembler classFileAssembler = new ClassFileAssembler();
        if (this.isConstructor) {
            classFileAssembler.setMaxLocals(2);
        }
        else {
            classFileAssembler.setMaxLocals(3);
        }
        short length = 0;
        if (this.isConstructor) {
            classFileAssembler.opc_new(this.targetClass);
            classFileAssembler.opc_dup();
        }
        else {
            if (AccessorGenerator.isPrimitive(this.returnType)) {
                classFileAssembler.opc_new(this.indexForPrimitiveType(this.returnType));
                classFileAssembler.opc_dup();
            }
            if (!this.isStatic()) {
                classFileAssembler.opc_aload_1();
                final Label label = new Label();
                classFileAssembler.opc_ifnonnull(label);
                classFileAssembler.opc_new(this.nullPointerClass);
                classFileAssembler.opc_dup();
                classFileAssembler.opc_invokespecial(this.nullPointerCtorIdx, 0, 0);
                classFileAssembler.opc_athrow();
                label.bind();
                length = classFileAssembler.getLength();
                classFileAssembler.opc_aload_1();
                classFileAssembler.opc_checkcast(this.targetClass);
            }
        }
        final Label label2 = new Label();
        if (this.parameterTypes.length == 0) {
            if (this.isConstructor) {
                classFileAssembler.opc_aload_1();
            }
            else {
                classFileAssembler.opc_aload_2();
            }
            classFileAssembler.opc_ifnull(label2);
        }
        if (this.isConstructor) {
            classFileAssembler.opc_aload_1();
        }
        else {
            classFileAssembler.opc_aload_2();
        }
        classFileAssembler.opc_arraylength();
        classFileAssembler.opc_sipush((short)this.parameterTypes.length);
        classFileAssembler.opc_if_icmpeq(label2);
        classFileAssembler.opc_new(this.illegalArgumentClass);
        classFileAssembler.opc_dup();
        classFileAssembler.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
        classFileAssembler.opc_athrow();
        label2.bind();
        short n = this.nonPrimitiveParametersBaseIdx;
        Label label3 = null;
        byte b = 1;
        for (int i = 0; i < this.parameterTypes.length; ++i) {
            final Class<?> clazz = this.parameterTypes[i];
            b += (byte)this.typeSizeInStackSlots(clazz);
            if (label3 != null) {
                label3.bind();
                label3 = null;
            }
            if (this.isConstructor) {
                classFileAssembler.opc_aload_1();
            }
            else {
                classFileAssembler.opc_aload_2();
            }
            classFileAssembler.opc_sipush((short)i);
            classFileAssembler.opc_aaload();
            if (AccessorGenerator.isPrimitive(clazz)) {
                if (this.isConstructor) {
                    classFileAssembler.opc_astore_2();
                }
                else {
                    classFileAssembler.opc_astore_3();
                }
                Label label4 = null;
                label3 = new Label();
                for (int j = 0; j < MethodAccessorGenerator.primitiveTypes.length; ++j) {
                    final Class<?> clazz2 = MethodAccessorGenerator.primitiveTypes[j];
                    if (AccessorGenerator.canWidenTo(clazz2, clazz)) {
                        if (label4 != null) {
                            label4.bind();
                        }
                        if (this.isConstructor) {
                            classFileAssembler.opc_aload_2();
                        }
                        else {
                            classFileAssembler.opc_aload_3();
                        }
                        classFileAssembler.opc_instanceof(this.indexForPrimitiveType(clazz2));
                        label4 = new Label();
                        classFileAssembler.opc_ifeq(label4);
                        if (this.isConstructor) {
                            classFileAssembler.opc_aload_2();
                        }
                        else {
                            classFileAssembler.opc_aload_3();
                        }
                        classFileAssembler.opc_checkcast(this.indexForPrimitiveType(clazz2));
                        classFileAssembler.opc_invokevirtual(this.unboxingMethodForPrimitiveType(clazz2), 0, this.typeSizeInStackSlots(clazz2));
                        AccessorGenerator.emitWideningBytecodeForPrimitiveConversion(classFileAssembler, clazz2, clazz);
                        classFileAssembler.opc_goto(label3);
                    }
                }
                if (label4 == null) {
                    throw new InternalError("Must have found at least identity conversion");
                }
                label4.bind();
                classFileAssembler.opc_new(this.illegalArgumentClass);
                classFileAssembler.opc_dup();
                classFileAssembler.opc_invokespecial(this.illegalArgumentCtorIdx, 0, 0);
                classFileAssembler.opc_athrow();
            }
            else {
                classFileAssembler.opc_checkcast(n);
                n = AccessorGenerator.add(n, (short)2);
            }
        }
        if (label3 != null) {
            label3.bind();
        }
        final short length2 = classFileAssembler.getLength();
        if (this.isConstructor) {
            classFileAssembler.opc_invokespecial(this.targetMethodRef, b, 0);
        }
        else if (this.isStatic()) {
            classFileAssembler.opc_invokestatic(this.targetMethodRef, b, this.typeSizeInStackSlots(this.returnType));
        }
        else if (this.isInterface()) {
            if (this.isPrivate()) {
                classFileAssembler.opc_invokespecial(this.targetMethodRef, b, 0);
            }
            else {
                classFileAssembler.opc_invokeinterface(this.targetMethodRef, b, b, this.typeSizeInStackSlots(this.returnType));
            }
        }
        else {
            classFileAssembler.opc_invokevirtual(this.targetMethodRef, b, this.typeSizeInStackSlots(this.returnType));
        }
        final short length3 = classFileAssembler.getLength();
        if (!this.isConstructor) {
            if (AccessorGenerator.isPrimitive(this.returnType)) {
                classFileAssembler.opc_invokespecial(this.ctorIndexForPrimitiveType(this.returnType), this.typeSizeInStackSlots(this.returnType), 0);
            }
            else if (this.returnType == Void.TYPE) {
                classFileAssembler.opc_aconst_null();
            }
        }
        classFileAssembler.opc_areturn();
        final short length4 = classFileAssembler.getLength();
        classFileAssembler.setStack(1);
        classFileAssembler.opc_invokespecial(this.toStringIdx, 0, 1);
        classFileAssembler.opc_new(this.illegalArgumentClass);
        classFileAssembler.opc_dup_x1();
        classFileAssembler.opc_swap();
        classFileAssembler.opc_invokespecial(this.illegalArgumentStringCtorIdx, 1, 0);
        classFileAssembler.opc_athrow();
        final short length5 = classFileAssembler.getLength();
        classFileAssembler.setStack(1);
        classFileAssembler.opc_new(this.invocationTargetClass);
        classFileAssembler.opc_dup_x1();
        classFileAssembler.opc_swap();
        classFileAssembler.opc_invokespecial(this.invocationTargetCtorIdx, 1, 0);
        classFileAssembler.opc_athrow();
        final ClassFileAssembler classFileAssembler2 = new ClassFileAssembler();
        classFileAssembler2.emitShort(length);
        classFileAssembler2.emitShort(length2);
        classFileAssembler2.emitShort(length4);
        classFileAssembler2.emitShort(this.classCastClass);
        classFileAssembler2.emitShort(length);
        classFileAssembler2.emitShort(length2);
        classFileAssembler2.emitShort(length4);
        classFileAssembler2.emitShort(this.nullPointerClass);
        classFileAssembler2.emitShort(length2);
        classFileAssembler2.emitShort(length3);
        classFileAssembler2.emitShort(length5);
        classFileAssembler2.emitShort(this.throwableClass);
        this.emitMethod(this.invokeIdx, classFileAssembler.getMaxLocals(), classFileAssembler, classFileAssembler2, new short[] { this.invocationTargetClass });
    }
    
    private boolean usesPrimitiveTypes() {
        if (this.returnType.isPrimitive()) {
            return true;
        }
        for (int i = 0; i < this.parameterTypes.length; ++i) {
            if (this.parameterTypes[i].isPrimitive()) {
                return true;
            }
        }
        return false;
    }
    
    private int numNonPrimitiveParameterTypes() {
        int n = 0;
        for (int i = 0; i < this.parameterTypes.length; ++i) {
            if (!this.parameterTypes[i].isPrimitive()) {
                ++n;
            }
        }
        return n;
    }
    
    private boolean isInterface() {
        return this.declaringClass.isInterface();
    }
    
    private String buildInternalSignature() {
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (int i = 0; i < this.parameterTypes.length; ++i) {
            sb.append(AccessorGenerator.getClassName(this.parameterTypes[i], true));
        }
        sb.append(")");
        sb.append(AccessorGenerator.getClassName(this.returnType, true));
        return sb.toString();
    }
    
    private static synchronized String generateName(final boolean b, final boolean b2) {
        if (!b) {
            return "sun/reflect/GeneratedMethodAccessor" + ++MethodAccessorGenerator.methodSymnum;
        }
        if (b2) {
            return "sun/reflect/GeneratedSerializationConstructorAccessor" + ++MethodAccessorGenerator.serializationConstructorSymnum;
        }
        return "sun/reflect/GeneratedConstructorAccessor" + ++MethodAccessorGenerator.constructorSymnum;
    }
    
    static {
        MethodAccessorGenerator.methodSymnum = 0;
        MethodAccessorGenerator.constructorSymnum = 0;
        MethodAccessorGenerator.serializationConstructorSymnum = 0;
    }
}
