package java.lang.invoke;

import sun.misc.Unsafe;
import jdk.internal.org.objectweb.asm.Label;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import java.util.Iterator;
import java.util.Arrays;
import java.security.AccessController;
import java.io.IOException;
import java.io.FileOutputStream;
import java.security.PrivilegedAction;
import java.util.Map;
import java.io.File;
import java.util.HashMap;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;

class InvokerBytecodeGenerator
{
    private static final String MH = "java/lang/invoke/MethodHandle";
    private static final String MHI = "java/lang/invoke/MethodHandleImpl";
    private static final String LF = "java/lang/invoke/LambdaForm";
    private static final String LFN = "java/lang/invoke/LambdaForm$Name";
    private static final String CLS = "java/lang/Class";
    private static final String OBJ = "java/lang/Object";
    private static final String OBJARY = "[Ljava/lang/Object;";
    private static final String MH_SIG = "Ljava/lang/invoke/MethodHandle;";
    private static final String LF_SIG = "Ljava/lang/invoke/LambdaForm;";
    private static final String LFN_SIG = "Ljava/lang/invoke/LambdaForm$Name;";
    private static final String LL_SIG = "(Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String LLV_SIG = "(Ljava/lang/Object;Ljava/lang/Object;)V";
    private static final String CLL_SIG = "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String superName = "java/lang/Object";
    private final String className;
    private final String sourceFile;
    private final LambdaForm lambdaForm;
    private final String invokerName;
    private final MethodType invokerType;
    private final int[] localsMap;
    private final LambdaForm.BasicType[] localTypes;
    private final Class<?>[] localClasses;
    private ClassWriter cw;
    private MethodVisitor mv;
    private static final MemberName.Factory MEMBERNAME_FACTORY;
    private static final Class<?> HOST_CLASS;
    private static final HashMap<String, Integer> DUMP_CLASS_FILES_COUNTERS;
    private static final File DUMP_CLASS_FILES_DIR;
    Map<Object, CpPatch> cpPatches;
    int cph;
    private static Class<?>[] STATICALLY_INVOCABLE_PACKAGES;
    
    private InvokerBytecodeGenerator(final LambdaForm lambdaForm, final int n, String s, String substring, final MethodType invokerType) {
        this.cpPatches = new HashMap<Object, CpPatch>();
        this.cph = 0;
        if (substring.contains(".")) {
            final int index = substring.indexOf(".");
            s = substring.substring(0, index);
            substring = substring.substring(index + 1);
        }
        if (MethodHandleStatics.DUMP_CLASS_FILES) {
            s = makeDumpableClassName(s);
        }
        this.className = "java/lang/invoke/LambdaForm$" + s;
        this.sourceFile = "LambdaForm$" + s;
        this.lambdaForm = lambdaForm;
        this.invokerName = substring;
        this.invokerType = invokerType;
        this.localsMap = new int[n + 1];
        this.localTypes = new LambdaForm.BasicType[n + 1];
        this.localClasses = new Class[n + 1];
    }
    
    private InvokerBytecodeGenerator(final String s, final String s2, final MethodType methodType) {
        this(null, methodType.parameterCount(), s, s2, methodType);
        this.localTypes[this.localTypes.length - 1] = LambdaForm.BasicType.V_TYPE;
        for (int i = 0; i < this.localsMap.length; ++i) {
            this.localsMap[i] = methodType.parameterSlotCount() - methodType.parameterSlotDepth(i);
            if (i < methodType.parameterCount()) {
                this.localTypes[i] = LambdaForm.BasicType.basicType(methodType.parameterType(i));
            }
        }
    }
    
    private InvokerBytecodeGenerator(final String s, final LambdaForm lambdaForm, final MethodType methodType) {
        this(lambdaForm, lambdaForm.names.length, s, lambdaForm.debugName, methodType);
        final LambdaForm.Name[] names = lambdaForm.names;
        int i = 0;
        int n = 0;
        while (i < this.localsMap.length) {
            this.localsMap[i] = n;
            if (i < names.length) {
                final LambdaForm.BasicType type = names[i].type();
                n += type.basicTypeSlots();
                this.localTypes[i] = type;
            }
            ++i;
        }
    }
    
    static void maybeDump(final String s, final byte[] array) {
        if (MethodHandleStatics.DUMP_CLASS_FILES) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    try {
                        final File file = new File(InvokerBytecodeGenerator.DUMP_CLASS_FILES_DIR, s + ".class");
                        System.out.println("dump: " + file);
                        file.getParentFile().mkdirs();
                        final FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(array);
                        fileOutputStream.close();
                        return null;
                    }
                    catch (final IOException ex) {
                        throw MethodHandleStatics.newInternalError(ex);
                    }
                }
            });
        }
    }
    
    private static String makeDumpableClassName(String string) {
        Integer value;
        synchronized (InvokerBytecodeGenerator.DUMP_CLASS_FILES_COUNTERS) {
            value = InvokerBytecodeGenerator.DUMP_CLASS_FILES_COUNTERS.get(string);
            if (value == null) {
                value = 0;
            }
            InvokerBytecodeGenerator.DUMP_CLASS_FILES_COUNTERS.put(string, value + 1);
        }
        String s;
        for (s = value.toString(); s.length() < 3; s = "0" + s) {}
        string += s;
        return string;
    }
    
    String constantPlaceholder(final Object o) {
        String s = "CONSTANT_PLACEHOLDER_" + this.cph++;
        if (MethodHandleStatics.DUMP_CLASS_FILES) {
            s = s + " <<" + debugString(o) + ">>";
        }
        if (this.cpPatches.containsKey(s)) {
            throw new InternalError("observed CP placeholder twice: " + s);
        }
        this.cpPatches.put(s, new CpPatch(this.cw.newConst(s), s, o));
        return s;
    }
    
    Object[] cpPatches(final byte[] array) {
        final int constantPoolSize = getConstantPoolSize(array);
        final Object[] array2 = new Object[constantPoolSize];
        for (final CpPatch cpPatch : this.cpPatches.values()) {
            if (cpPatch.index >= constantPoolSize) {
                throw new InternalError("in cpool[" + constantPoolSize + "]: " + cpPatch + "\n" + Arrays.toString(Arrays.copyOf(array, 20)));
            }
            array2[cpPatch.index] = cpPatch.value;
        }
        return array2;
    }
    
    private static String debugString(final Object o) {
        if (!(o instanceof MethodHandle)) {
            return o.toString();
        }
        final MethodHandle methodHandle = (MethodHandle)o;
        final MemberName internalMemberName = methodHandle.internalMemberName();
        if (internalMemberName != null) {
            return internalMemberName.toString();
        }
        return methodHandle.debugString();
    }
    
    private static int getConstantPoolSize(final byte[] array) {
        return (array[8] & 0xFF) << 8 | (array[9] & 0xFF);
    }
    
    private MemberName loadMethod(final byte[] array) {
        return resolveInvokerMember(loadAndInitializeInvokerClass(array, this.cpPatches(array)), this.invokerName, this.invokerType);
    }
    
    private static Class<?> loadAndInitializeInvokerClass(final byte[] array, final Object[] array2) {
        final Class<?> defineAnonymousClass = MethodHandleStatics.UNSAFE.defineAnonymousClass(InvokerBytecodeGenerator.HOST_CLASS, array, array2);
        MethodHandleStatics.UNSAFE.ensureClassInitialized(defineAnonymousClass);
        return defineAnonymousClass;
    }
    
    private static MemberName resolveInvokerMember(final Class<?> clazz, final String s, final MethodType methodType) {
        final MemberName memberName = new MemberName(clazz, s, methodType, (byte)6);
        MemberName resolveOrFail;
        try {
            resolveOrFail = InvokerBytecodeGenerator.MEMBERNAME_FACTORY.resolveOrFail((byte)6, memberName, InvokerBytecodeGenerator.HOST_CLASS, ReflectiveOperationException.class);
        }
        catch (final ReflectiveOperationException ex) {
            throw MethodHandleStatics.newInternalError(ex);
        }
        return resolveOrFail;
    }
    
    private void classFilePrologue() {
        (this.cw = new ClassWriter(3)).visit(52, 48, this.className, null, "java/lang/Object", null);
        this.cw.visitSource(this.sourceFile, null);
        this.mv = this.cw.visitMethod(8, this.invokerName, this.invokerType.toMethodDescriptorString(), null, null);
    }
    
    private void classFileEpilogue() {
        this.mv.visitMaxs(0, 0);
        this.mv.visitEnd();
    }
    
    private void emitConst(final Object o) {
        if (o == null) {
            this.mv.visitInsn(1);
            return;
        }
        if (o instanceof Integer) {
            this.emitIconstInsn((int)o);
            return;
        }
        if (o instanceof Long) {
            final long longValue = (long)o;
            if (longValue == (short)longValue) {
                this.emitIconstInsn((int)longValue);
                this.mv.visitInsn(133);
                return;
            }
        }
        if (o instanceof Float) {
            final float floatValue = (float)o;
            if (floatValue == (short)floatValue) {
                this.emitIconstInsn((int)floatValue);
                this.mv.visitInsn(134);
                return;
            }
        }
        if (o instanceof Double) {
            final double doubleValue = (double)o;
            if (doubleValue == (short)doubleValue) {
                this.emitIconstInsn((int)doubleValue);
                this.mv.visitInsn(135);
                return;
            }
        }
        if (o instanceof Boolean) {
            this.emitIconstInsn(((boolean)o) ? 1 : 0);
            return;
        }
        this.mv.visitLdcInsn(o);
    }
    
    private void emitIconstInsn(final int n) {
        int n2 = 0;
        switch (n) {
            case 0: {
                n2 = 3;
                break;
            }
            case 1: {
                n2 = 4;
                break;
            }
            case 2: {
                n2 = 5;
                break;
            }
            case 3: {
                n2 = 6;
                break;
            }
            case 4: {
                n2 = 7;
                break;
            }
            case 5: {
                n2 = 8;
                break;
            }
            default: {
                if (n == (byte)n) {
                    this.mv.visitIntInsn(16, n & 0xFF);
                }
                else if (n == (short)n) {
                    this.mv.visitIntInsn(17, (char)n);
                }
                else {
                    this.mv.visitLdcInsn(n);
                }
                return;
            }
        }
        this.mv.visitInsn(n2);
    }
    
    private void emitLoadInsn(final LambdaForm.BasicType basicType, final int n) {
        this.mv.visitVarInsn(this.loadInsnOpcode(basicType), this.localsMap[n]);
    }
    
    private int loadInsnOpcode(final LambdaForm.BasicType basicType) throws InternalError {
        switch (basicType) {
            case I_TYPE: {
                return 21;
            }
            case J_TYPE: {
                return 22;
            }
            case F_TYPE: {
                return 23;
            }
            case D_TYPE: {
                return 24;
            }
            case L_TYPE: {
                return 25;
            }
            default: {
                throw new InternalError("unknown type: " + basicType);
            }
        }
    }
    
    private void emitAloadInsn(final int n) {
        this.emitLoadInsn(LambdaForm.BasicType.L_TYPE, n);
    }
    
    private void emitStoreInsn(final LambdaForm.BasicType basicType, final int n) {
        this.mv.visitVarInsn(this.storeInsnOpcode(basicType), this.localsMap[n]);
    }
    
    private int storeInsnOpcode(final LambdaForm.BasicType basicType) throws InternalError {
        switch (basicType) {
            case I_TYPE: {
                return 54;
            }
            case J_TYPE: {
                return 55;
            }
            case F_TYPE: {
                return 56;
            }
            case D_TYPE: {
                return 57;
            }
            case L_TYPE: {
                return 58;
            }
            default: {
                throw new InternalError("unknown type: " + basicType);
            }
        }
    }
    
    private void emitAstoreInsn(final int n) {
        this.emitStoreInsn(LambdaForm.BasicType.L_TYPE, n);
    }
    
    private byte arrayTypeCode(final Wrapper wrapper) {
        switch (wrapper) {
            case BOOLEAN: {
                return 4;
            }
            case BYTE: {
                return 8;
            }
            case CHAR: {
                return 5;
            }
            case SHORT: {
                return 9;
            }
            case INT: {
                return 10;
            }
            case LONG: {
                return 11;
            }
            case FLOAT: {
                return 6;
            }
            case DOUBLE: {
                return 7;
            }
            case OBJECT: {
                return 0;
            }
            default: {
                throw new InternalError();
            }
        }
    }
    
    private int arrayInsnOpcode(final byte b, final int n) throws InternalError {
        assert n == 50;
        int n2 = 0;
        switch (b) {
            case 4: {
                n2 = 84;
                break;
            }
            case 8: {
                n2 = 84;
                break;
            }
            case 5: {
                n2 = 85;
                break;
            }
            case 9: {
                n2 = 86;
                break;
            }
            case 10: {
                n2 = 79;
                break;
            }
            case 11: {
                n2 = 80;
                break;
            }
            case 6: {
                n2 = 81;
                break;
            }
            case 7: {
                n2 = 82;
                break;
            }
            case 0: {
                n2 = 83;
                break;
            }
            default: {
                throw new InternalError();
            }
        }
        return n2 - 83 + n;
    }
    
    private void freeFrameLocal(final int n) {
        final int indexForFrameLocal = this.indexForFrameLocal(n);
        if (indexForFrameLocal < 0) {
            return;
        }
        final LambdaForm.BasicType basicType = this.localTypes[indexForFrameLocal];
        final int localTemp = this.makeLocalTemp(basicType);
        this.mv.visitVarInsn(this.loadInsnOpcode(basicType), n);
        this.mv.visitVarInsn(this.storeInsnOpcode(basicType), localTemp);
        assert this.localsMap[indexForFrameLocal] == n;
        this.localsMap[indexForFrameLocal] = localTemp;
        assert this.indexForFrameLocal(n) < 0;
    }
    
    private int indexForFrameLocal(final int n) {
        for (int i = 0; i < this.localsMap.length; ++i) {
            if (this.localsMap[i] == n && this.localTypes[i] != LambdaForm.BasicType.V_TYPE) {
                return i;
            }
        }
        return -1;
    }
    
    private int makeLocalTemp(final LambdaForm.BasicType basicType) {
        final int n = this.localsMap[this.localsMap.length - 1];
        this.localsMap[this.localsMap.length - 1] = n + basicType.basicTypeSlots();
        return n;
    }
    
    private void emitBoxing(final Wrapper wrapper) {
        final String string = "java/lang/" + wrapper.wrapperType().getSimpleName();
        this.mv.visitMethodInsn(184, string, "valueOf", "(" + wrapper.basicTypeChar() + ")L" + string + ";", false);
    }
    
    private void emitUnboxing(final Wrapper wrapper) {
        final String string = "java/lang/" + wrapper.wrapperType().getSimpleName();
        final String string2 = wrapper.primitiveSimpleName() + "Value";
        final String string3 = "()" + wrapper.basicTypeChar();
        this.emitReferenceCast(wrapper.wrapperType(), null);
        this.mv.visitMethodInsn(182, string, string2, string3, false);
    }
    
    private void emitImplicitConversion(final LambdaForm.BasicType basicType, final Class<?> clazz, final Object o) {
        assert LambdaForm.BasicType.basicType(clazz) == basicType;
        if (clazz == basicType.basicTypeClass() && basicType != LambdaForm.BasicType.L_TYPE) {
            return;
        }
        switch (basicType) {
            case L_TYPE: {
                if (VerifyType.isNullConversion(Object.class, clazz, false)) {
                    if (MethodHandleStatics.PROFILE_LEVEL > 0) {
                        this.emitReferenceCast(Object.class, o);
                    }
                    return;
                }
                this.emitReferenceCast(clazz, o);
                return;
            }
            case I_TYPE: {
                if (!VerifyType.isNullConversion(Integer.TYPE, clazz, false)) {
                    this.emitPrimCast(basicType.basicTypeWrapper(), Wrapper.forPrimitiveType(clazz));
                }
                return;
            }
            default: {
                throw MethodHandleStatics.newInternalError("bad implicit conversion: tc=" + basicType + ": " + clazz);
            }
        }
    }
    
    private boolean assertStaticType(final Class<?> clazz, final LambdaForm.Name name) {
        final int index = name.index();
        final Class<?> clazz2 = this.localClasses[index];
        if (clazz2 != null && (clazz2 == clazz || clazz.isAssignableFrom(clazz2))) {
            return true;
        }
        if (clazz2 == null || clazz2.isAssignableFrom(clazz)) {
            this.localClasses[index] = clazz;
        }
        return false;
    }
    
    private void emitReferenceCast(final Class<?> clazz, final Object o) {
        LambdaForm.Name name = null;
        if (o instanceof LambdaForm.Name) {
            final LambdaForm.Name name2 = (LambdaForm.Name)o;
            if (this.assertStaticType(clazz, name2)) {
                return;
            }
            if (this.lambdaForm.useCount(name2) > 1) {
                name = name2;
            }
        }
        if (isStaticallyNameable(clazz)) {
            this.mv.visitTypeInsn(192, getInternalName(clazz));
        }
        else {
            this.mv.visitLdcInsn(this.constantPlaceholder(clazz));
            this.mv.visitTypeInsn(192, "java/lang/Class");
            this.mv.visitInsn(95);
            this.mv.visitMethodInsn(184, "java/lang/invoke/MethodHandleImpl", "castReference", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;", false);
            if (Object[].class.isAssignableFrom(clazz)) {
                this.mv.visitTypeInsn(192, "[Ljava/lang/Object;");
            }
            else if (MethodHandleStatics.PROFILE_LEVEL > 0) {
                this.mv.visitTypeInsn(192, "java/lang/Object");
            }
        }
        if (name != null) {
            this.mv.visitInsn(89);
            this.emitAstoreInsn(name.index());
        }
    }
    
    private void emitReturnInsn(final LambdaForm.BasicType basicType) {
        int n = 0;
        switch (basicType) {
            case I_TYPE: {
                n = 172;
                break;
            }
            case J_TYPE: {
                n = 173;
                break;
            }
            case F_TYPE: {
                n = 174;
                break;
            }
            case D_TYPE: {
                n = 175;
                break;
            }
            case L_TYPE: {
                n = 176;
                break;
            }
            case V_TYPE: {
                n = 177;
                break;
            }
            default: {
                throw new InternalError("unknown return type: " + basicType);
            }
        }
        this.mv.visitInsn(n);
    }
    
    private static String getInternalName(final Class<?> clazz) {
        if (clazz == Object.class) {
            return "java/lang/Object";
        }
        if (clazz == Object[].class) {
            return "[Ljava/lang/Object;";
        }
        if (clazz == Class.class) {
            return "java/lang/Class";
        }
        if (clazz == MethodHandle.class) {
            return "java/lang/invoke/MethodHandle";
        }
        assert VerifyAccess.isTypeVisible(clazz, Object.class) : clazz.getName();
        return clazz.getName().replace('.', '/');
    }
    
    static MemberName generateCustomizedCode(final LambdaForm lambdaForm, final MethodType methodType) {
        final InvokerBytecodeGenerator invokerBytecodeGenerator = new InvokerBytecodeGenerator("MH", lambdaForm, methodType);
        return invokerBytecodeGenerator.loadMethod(invokerBytecodeGenerator.generateCustomizedCodeBytes());
    }
    
    private boolean checkActualReceiver() {
        this.mv.visitInsn(89);
        this.mv.visitVarInsn(25, this.localsMap[0]);
        this.mv.visitMethodInsn(184, "java/lang/invoke/MethodHandleImpl", "assertSame", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
        return true;
    }
    
    private byte[] generateCustomizedCodeBytes() {
        this.classFilePrologue();
        this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Compiled;", true);
        if (this.lambdaForm.forceInline) {
            this.mv.visitAnnotation("Ljava/lang/invoke/ForceInline;", true);
        }
        else {
            this.mv.visitAnnotation("Ljava/lang/invoke/DontInline;", true);
        }
        if (this.lambdaForm.customized != null) {
            this.mv.visitLdcInsn(this.constantPlaceholder(this.lambdaForm.customized));
            this.mv.visitTypeInsn(192, "java/lang/invoke/MethodHandle");
            assert this.checkActualReceiver();
            this.mv.visitVarInsn(58, this.localsMap[0]);
        }
        LambdaForm.Name name = null;
        for (int i = this.lambdaForm.arity; i < this.lambdaForm.names.length; ++i) {
            final LambdaForm.Name name2 = this.lambdaForm.names[i];
            this.emitStoreResult(name);
            name = name2;
            final MethodHandleImpl.Intrinsic intrinsicName = name2.function.intrinsicName();
            switch (intrinsicName) {
                case SELECT_ALTERNATIVE: {
                    assert this.isSelectAlternative(i);
                    if (MethodHandleStatics.PROFILE_GWT) {
                        assert name2.arguments[0] instanceof LambdaForm.Name && this.nameRefersTo((LambdaForm.Name)name2.arguments[0], MethodHandleImpl.class, "profileBoolean");
                        this.mv.visitAnnotation("Ljava/lang/invoke/InjectedProfile;", true);
                    }
                    name = this.emitSelectAlternative(name2, this.lambdaForm.names[i + 1]);
                    ++i;
                    continue;
                }
                case GUARD_WITH_CATCH: {
                    assert this.isGuardWithCatch(i);
                    name = this.emitGuardWithCatch(i);
                    i += 2;
                    continue;
                }
                case NEW_ARRAY: {
                    if (isStaticallyNameable(name2.function.methodType().returnType())) {
                        this.emitNewArray(name2);
                        continue;
                    }
                    break;
                }
                case ARRAY_LOAD: {
                    this.emitArrayLoad(name2);
                    continue;
                }
                case ARRAY_STORE: {
                    this.emitArrayStore(name2);
                    continue;
                }
                case IDENTITY: {
                    assert name2.arguments.length == 1;
                    this.emitPushArguments(name2);
                    continue;
                }
                case ZERO: {
                    assert name2.arguments.length == 0;
                    this.emitConst(name2.type.basicTypeWrapper().zero());
                    continue;
                }
                case NONE: {
                    break;
                }
                default: {
                    throw MethodHandleStatics.newInternalError("Unknown intrinsic: " + intrinsicName);
                }
            }
            final MemberName member = name2.function.member();
            if (isStaticallyInvocable(member)) {
                this.emitStaticInvoke(member, name2);
            }
            else {
                this.emitInvoke(name2);
            }
        }
        this.emitReturn(name);
        this.classFileEpilogue();
        this.bogusMethod(this.lambdaForm);
        final byte[] byteArray = this.cw.toByteArray();
        maybeDump(this.className, byteArray);
        return byteArray;
    }
    
    void emitArrayLoad(final LambdaForm.Name name) {
        this.emitArrayOp(name, 50);
    }
    
    void emitArrayStore(final LambdaForm.Name name) {
        this.emitArrayOp(name, 83);
    }
    
    void emitArrayOp(final LambdaForm.Name name, int arrayInsnOpcode) {
        assert arrayInsnOpcode == 83;
        final Class<?> componentType = name.function.methodType().parameterType(0).getComponentType();
        assert componentType != null;
        this.emitPushArguments(name);
        if (componentType.isPrimitive()) {
            arrayInsnOpcode = this.arrayInsnOpcode(this.arrayTypeCode(Wrapper.forPrimitiveType(componentType)), arrayInsnOpcode);
        }
        this.mv.visitInsn(arrayInsnOpcode);
    }
    
    void emitInvoke(final LambdaForm.Name name) {
        assert !this.isLinkerMethodInvoke(name);
        final MethodHandle resolvedHandle = name.function.resolvedHandle;
        assert resolvedHandle != null : name.exprString();
        this.mv.visitLdcInsn(this.constantPlaceholder(resolvedHandle));
        this.emitReferenceCast(MethodHandle.class, resolvedHandle);
        this.emitPushArguments(name);
        this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", name.function.methodType().basicType().toMethodDescriptorString(), false);
    }
    
    static boolean isStaticallyInvocable(final LambdaForm.Name name) {
        return isStaticallyInvocable(name.function.member());
    }
    
    static boolean isStaticallyInvocable(final MemberName memberName) {
        if (memberName == null) {
            return false;
        }
        if (memberName.isConstructor()) {
            return false;
        }
        final Class<?> declaringClass = memberName.getDeclaringClass();
        if (declaringClass.isArray() || declaringClass.isPrimitive()) {
            return false;
        }
        if (declaringClass.isAnonymousClass() || declaringClass.isLocalClass()) {
            return false;
        }
        if (declaringClass.getClassLoader() != MethodHandle.class.getClassLoader()) {
            return false;
        }
        if (ReflectUtil.isVMAnonymousClass(declaringClass)) {
            return false;
        }
        final MethodType methodOrFieldType = memberName.getMethodOrFieldType();
        if (!isStaticallyNameable(methodOrFieldType.returnType())) {
            return false;
        }
        final Class<?>[] parameterArray = methodOrFieldType.parameterArray();
        for (int length = parameterArray.length, i = 0; i < length; ++i) {
            if (!isStaticallyNameable(parameterArray[i])) {
                return false;
            }
        }
        return (!memberName.isPrivate() && VerifyAccess.isSamePackage(MethodHandle.class, declaringClass)) || (memberName.isPublic() && isStaticallyNameable(declaringClass));
    }
    
    static boolean isStaticallyNameable(Class<?> componentType) {
        if (componentType == Object.class) {
            return true;
        }
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }
        if (componentType.isPrimitive()) {
            return true;
        }
        if (ReflectUtil.isVMAnonymousClass(componentType)) {
            return false;
        }
        if (componentType.getClassLoader() != Object.class.getClassLoader()) {
            return false;
        }
        if (VerifyAccess.isSamePackage(MethodHandle.class, componentType)) {
            return true;
        }
        if (!Modifier.isPublic(componentType.getModifiers())) {
            return false;
        }
        final Class<?>[] statically_INVOCABLE_PACKAGES = InvokerBytecodeGenerator.STATICALLY_INVOCABLE_PACKAGES;
        for (int length = statically_INVOCABLE_PACKAGES.length, i = 0; i < length; ++i) {
            if (VerifyAccess.isSamePackage(statically_INVOCABLE_PACKAGES[i], componentType)) {
                return true;
            }
        }
        return false;
    }
    
    void emitStaticInvoke(final LambdaForm.Name name) {
        this.emitStaticInvoke(name.function.member(), name);
    }
    
    void emitStaticInvoke(final MemberName memberName, final LambdaForm.Name name) {
        assert memberName.equals(name.function.member());
        final String internalName = getInternalName(memberName.getDeclaringClass());
        final String name2 = memberName.getName();
        byte referenceKind = memberName.getReferenceKind();
        if (referenceKind == 7) {
            assert memberName.canBeStaticallyBound() : memberName;
            referenceKind = 5;
        }
        if (memberName.getDeclaringClass().isInterface() && referenceKind == 5) {
            referenceKind = 9;
        }
        this.emitPushArguments(name);
        if (memberName.isMethod()) {
            this.mv.visitMethodInsn(this.refKindOpcode(referenceKind), internalName, name2, memberName.getMethodType().toMethodDescriptorString(), memberName.getDeclaringClass().isInterface());
        }
        else {
            this.mv.visitFieldInsn(this.refKindOpcode(referenceKind), internalName, name2, MethodType.toFieldDescriptorString(memberName.getFieldType()));
        }
        if (name.type == LambdaForm.BasicType.L_TYPE) {
            final Class<?> returnType = memberName.getInvocationType().returnType();
            assert !returnType.isPrimitive();
            if (returnType != Object.class && !returnType.isInterface()) {
                this.assertStaticType(returnType, name);
            }
        }
    }
    
    void emitNewArray(final LambdaForm.Name name) throws InternalError {
        final Class<?> returnType = name.function.methodType().returnType();
        if (name.arguments.length == 0) {
            Object invoke;
            try {
                invoke = name.function.resolvedHandle.invoke();
            }
            catch (final Throwable t) {
                throw MethodHandleStatics.newInternalError(t);
            }
            assert Array.getLength(invoke) == 0;
            assert invoke.getClass() == returnType;
            this.mv.visitLdcInsn(this.constantPlaceholder(invoke));
            this.emitReferenceCast(returnType, invoke);
        }
        else {
            final Class componentType = returnType.getComponentType();
            assert componentType != null;
            this.emitIconstInsn(name.arguments.length);
            int arrayInsnOpcode = 83;
            if (!componentType.isPrimitive()) {
                this.mv.visitTypeInsn(189, getInternalName(componentType));
            }
            else {
                final byte arrayTypeCode = this.arrayTypeCode(Wrapper.forPrimitiveType(componentType));
                arrayInsnOpcode = this.arrayInsnOpcode(arrayTypeCode, arrayInsnOpcode);
                this.mv.visitIntInsn(188, arrayTypeCode);
            }
            for (int i = 0; i < name.arguments.length; ++i) {
                this.mv.visitInsn(89);
                this.emitIconstInsn(i);
                this.emitPushArgument(name, i);
                this.mv.visitInsn(arrayInsnOpcode);
            }
            this.assertStaticType(returnType, name);
        }
    }
    
    int refKindOpcode(final byte b) {
        switch (b) {
            case 5: {
                return 182;
            }
            case 6: {
                return 184;
            }
            case 7: {
                return 183;
            }
            case 9: {
                return 185;
            }
            case 1: {
                return 180;
            }
            case 3: {
                return 181;
            }
            case 2: {
                return 178;
            }
            case 4: {
                return 179;
            }
            default: {
                throw new InternalError("refKind=" + b);
            }
        }
    }
    
    private boolean memberRefersTo(final MemberName memberName, final Class<?> clazz, final String s) {
        return memberName != null && memberName.getDeclaringClass() == clazz && memberName.getName().equals(s);
    }
    
    private boolean nameRefersTo(final LambdaForm.Name name, final Class<?> clazz, final String s) {
        return name.function != null && this.memberRefersTo(name.function.member(), clazz, s);
    }
    
    private boolean isInvokeBasic(final LambdaForm.Name name) {
        if (name.function == null) {
            return false;
        }
        if (name.arguments.length < 1) {
            return false;
        }
        final MemberName member = name.function.member();
        return this.memberRefersTo(member, MethodHandle.class, "invokeBasic") && !member.isPublic() && !member.isStatic();
    }
    
    private boolean isLinkerMethodInvoke(final LambdaForm.Name name) {
        if (name.function == null) {
            return false;
        }
        if (name.arguments.length < 1) {
            return false;
        }
        final MemberName member = name.function.member();
        return member != null && member.getDeclaringClass() == MethodHandle.class && !member.isPublic() && member.isStatic() && member.getName().startsWith("linkTo");
    }
    
    private boolean isSelectAlternative(final int n) {
        if (n + 1 >= this.lambdaForm.names.length) {
            return false;
        }
        final LambdaForm.Name name = this.lambdaForm.names[n];
        final LambdaForm.Name name2 = this.lambdaForm.names[n + 1];
        return this.nameRefersTo(name, MethodHandleImpl.class, "selectAlternative") && this.isInvokeBasic(name2) && name2.lastUseIndex(name) == 0 && this.lambdaForm.lastUseIndex(name) == n + 1;
    }
    
    private boolean isGuardWithCatch(final int n) {
        if (n + 2 >= this.lambdaForm.names.length) {
            return false;
        }
        final LambdaForm.Name name = this.lambdaForm.names[n];
        final LambdaForm.Name name2 = this.lambdaForm.names[n + 1];
        final LambdaForm.Name name3 = this.lambdaForm.names[n + 2];
        return this.nameRefersTo(name2, MethodHandleImpl.class, "guardWithCatch") && this.isInvokeBasic(name) && this.isInvokeBasic(name3) && name2.lastUseIndex(name) == 3 && this.lambdaForm.lastUseIndex(name) == n + 1 && name3.lastUseIndex(name2) == 1 && this.lambdaForm.lastUseIndex(name2) == n + 2;
    }
    
    private LambdaForm.Name emitSelectAlternative(final LambdaForm.Name name, final LambdaForm.Name name2) {
        assert isStaticallyInvocable(name2);
        final LambdaForm.Name name3 = (LambdaForm.Name)name2.arguments[0];
        final Label label = new Label();
        final Label label2 = new Label();
        this.emitPushArgument(name, 0);
        this.mv.visitJumpInsn(153, label);
        final Class[] array = this.localClasses.clone();
        this.emitPushArgument(name, 1);
        this.emitAstoreInsn(name3.index());
        this.emitStaticInvoke(name2);
        this.mv.visitJumpInsn(167, label2);
        this.mv.visitLabel(label);
        System.arraycopy(array, 0, this.localClasses, 0, array.length);
        this.emitPushArgument(name, 2);
        this.emitAstoreInsn(name3.index());
        this.emitStaticInvoke(name2);
        this.mv.visitLabel(label2);
        System.arraycopy(array, 0, this.localClasses, 0, array.length);
        return name2;
    }
    
    private LambdaForm.Name emitGuardWithCatch(final int n) {
        final LambdaForm.Name name = this.lambdaForm.names[n];
        final LambdaForm.Name name2 = this.lambdaForm.names[n + 1];
        final LambdaForm.Name name3 = this.lambdaForm.names[n + 2];
        final Label label = new Label();
        final Label label2 = new Label();
        final Label label3 = new Label();
        final Label label4 = new Label();
        final MethodType changeReturnType = name.function.resolvedHandle.type().dropParameterTypes(0, 1).changeReturnType(name3.function.resolvedHandle.type().returnType());
        this.mv.visitTryCatchBlock(label, label2, label3, "java/lang/Throwable");
        this.mv.visitLabel(label);
        this.emitPushArgument(name2, 0);
        this.emitPushArguments(name, 1);
        this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", changeReturnType.basicType().toMethodDescriptorString(), false);
        this.mv.visitLabel(label2);
        this.mv.visitJumpInsn(167, label4);
        this.mv.visitLabel(label3);
        this.mv.visitInsn(89);
        this.emitPushArgument(name2, 1);
        this.mv.visitInsn(95);
        this.mv.visitMethodInsn(182, "java/lang/Class", "isInstance", "(Ljava/lang/Object;)Z", false);
        final Label label5 = new Label();
        this.mv.visitJumpInsn(153, label5);
        this.emitPushArgument(name2, 2);
        this.mv.visitInsn(95);
        this.emitPushArguments(name, 1);
        this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", changeReturnType.insertParameterTypes(0, Throwable.class).basicType().toMethodDescriptorString(), false);
        this.mv.visitJumpInsn(167, label4);
        this.mv.visitLabel(label5);
        this.mv.visitInsn(191);
        this.mv.visitLabel(label4);
        return name3;
    }
    
    private void emitPushArguments(final LambdaForm.Name name) {
        this.emitPushArguments(name, 0);
    }
    
    private void emitPushArguments(final LambdaForm.Name name, final int n) {
        for (int i = n; i < name.arguments.length; ++i) {
            this.emitPushArgument(name, i);
        }
    }
    
    private void emitPushArgument(final LambdaForm.Name name, final int n) {
        this.emitPushArgument(name.function.methodType().parameterType(n), name.arguments[n]);
    }
    
    private void emitPushArgument(final Class<?> clazz, final Object o) {
        final LambdaForm.BasicType basicType = LambdaForm.BasicType.basicType(clazz);
        if (o instanceof LambdaForm.Name) {
            final LambdaForm.Name name = (LambdaForm.Name)o;
            this.emitLoadInsn(name.type, name.index());
            this.emitImplicitConversion(name.type, clazz, name);
        }
        else if ((o == null || o instanceof String) && basicType == LambdaForm.BasicType.L_TYPE) {
            this.emitConst(o);
        }
        else if (Wrapper.isWrapperType(o.getClass()) && basicType != LambdaForm.BasicType.L_TYPE) {
            this.emitConst(o);
        }
        else {
            this.mv.visitLdcInsn(this.constantPlaceholder(o));
            this.emitImplicitConversion(LambdaForm.BasicType.L_TYPE, clazz, o);
        }
    }
    
    private void emitStoreResult(final LambdaForm.Name name) {
        if (name != null && name.type != LambdaForm.BasicType.V_TYPE) {
            this.emitStoreInsn(name.type, name.index());
        }
    }
    
    private void emitReturn(final LambdaForm.Name name) {
        final Class<?> returnType = this.invokerType.returnType();
        final LambdaForm.BasicType returnType2 = this.lambdaForm.returnType();
        assert returnType2 == LambdaForm.BasicType.basicType(returnType);
        if (returnType2 == LambdaForm.BasicType.V_TYPE) {
            this.mv.visitInsn(177);
        }
        else {
            final LambdaForm.Name name2 = this.lambdaForm.names[this.lambdaForm.result];
            if (name2 != name) {
                this.emitLoadInsn(returnType2, this.lambdaForm.result);
            }
            this.emitImplicitConversion(returnType2, returnType, name2);
            this.emitReturnInsn(returnType2);
        }
    }
    
    private void emitPrimCast(final Wrapper wrapper, final Wrapper wrapper2) {
        if (wrapper == wrapper2) {
            return;
        }
        if (wrapper.isSubwordOrInt()) {
            this.emitI2X(wrapper2);
        }
        else if (wrapper2.isSubwordOrInt()) {
            this.emitX2I(wrapper);
            if (wrapper2.bitWidth() < 32) {
                this.emitI2X(wrapper2);
            }
        }
        else {
            boolean b = false;
            Label_0285: {
                switch (wrapper) {
                    case LONG: {
                        switch (wrapper2) {
                            case FLOAT: {
                                this.mv.visitInsn(137);
                                break Label_0285;
                            }
                            case DOUBLE: {
                                this.mv.visitInsn(138);
                                break Label_0285;
                            }
                            default: {
                                b = true;
                                break Label_0285;
                            }
                        }
                        break;
                    }
                    case FLOAT: {
                        switch (wrapper2) {
                            case LONG: {
                                this.mv.visitInsn(140);
                                break Label_0285;
                            }
                            case DOUBLE: {
                                this.mv.visitInsn(141);
                                break Label_0285;
                            }
                            default: {
                                b = true;
                                break Label_0285;
                            }
                        }
                        break;
                    }
                    case DOUBLE: {
                        switch (wrapper2) {
                            case LONG: {
                                this.mv.visitInsn(143);
                                break Label_0285;
                            }
                            case FLOAT: {
                                this.mv.visitInsn(144);
                                break Label_0285;
                            }
                            default: {
                                b = true;
                                break Label_0285;
                            }
                        }
                        break;
                    }
                    default: {
                        b = true;
                        break;
                    }
                }
            }
            if (b) {
                throw new IllegalStateException("unhandled prim cast: " + wrapper + "2" + wrapper2);
            }
        }
    }
    
    private void emitI2X(final Wrapper wrapper) {
        switch (wrapper) {
            case BYTE: {
                this.mv.visitInsn(145);
                break;
            }
            case SHORT: {
                this.mv.visitInsn(147);
                break;
            }
            case CHAR: {
                this.mv.visitInsn(146);
                break;
            }
            case INT: {
                break;
            }
            case LONG: {
                this.mv.visitInsn(133);
                break;
            }
            case FLOAT: {
                this.mv.visitInsn(134);
                break;
            }
            case DOUBLE: {
                this.mv.visitInsn(135);
                break;
            }
            case BOOLEAN: {
                this.mv.visitInsn(4);
                this.mv.visitInsn(126);
                break;
            }
            default: {
                throw new InternalError("unknown type: " + wrapper);
            }
        }
    }
    
    private void emitX2I(final Wrapper wrapper) {
        switch (wrapper) {
            case LONG: {
                this.mv.visitInsn(136);
                break;
            }
            case FLOAT: {
                this.mv.visitInsn(139);
                break;
            }
            case DOUBLE: {
                this.mv.visitInsn(142);
                break;
            }
            default: {
                throw new InternalError("unknown type: " + wrapper);
            }
        }
    }
    
    static MemberName generateLambdaFormInterpreterEntryPoint(final String s) {
        assert LambdaForm.isValidSignature(s);
        final InvokerBytecodeGenerator invokerBytecodeGenerator = new InvokerBytecodeGenerator("LFI", "interpret_" + LambdaForm.signatureReturn(s).basicTypeChar(), LambdaForm.signatureType(s).changeParameterType(0, MethodHandle.class));
        return invokerBytecodeGenerator.loadMethod(invokerBytecodeGenerator.generateLambdaFormInterpreterEntryPointBytes());
    }
    
    private byte[] generateLambdaFormInterpreterEntryPointBytes() {
        this.classFilePrologue();
        this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        this.mv.visitAnnotation("Ljava/lang/invoke/DontInline;", true);
        this.emitIconstInsn(this.invokerType.parameterCount());
        this.mv.visitTypeInsn(189, "java/lang/Object");
        for (int i = 0; i < this.invokerType.parameterCount(); ++i) {
            final Class<?> parameterType = this.invokerType.parameterType(i);
            this.mv.visitInsn(89);
            this.emitIconstInsn(i);
            this.emitLoadInsn(LambdaForm.BasicType.basicType(parameterType), i);
            if (parameterType.isPrimitive()) {
                this.emitBoxing(Wrapper.forPrimitiveType(parameterType));
            }
            this.mv.visitInsn(83);
        }
        this.emitAloadInsn(0);
        this.mv.visitFieldInsn(180, "java/lang/invoke/MethodHandle", "form", "Ljava/lang/invoke/LambdaForm;");
        this.mv.visitInsn(95);
        this.mv.visitMethodInsn(182, "java/lang/invoke/LambdaForm", "interpretWithArguments", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
        final Class<?> returnType = this.invokerType.returnType();
        if (returnType.isPrimitive() && returnType != Void.TYPE) {
            this.emitUnboxing(Wrapper.forPrimitiveType(returnType));
        }
        this.emitReturnInsn(LambdaForm.BasicType.basicType(returnType));
        this.classFileEpilogue();
        this.bogusMethod(this.invokerType);
        final byte[] byteArray = this.cw.toByteArray();
        maybeDump(this.className, byteArray);
        return byteArray;
    }
    
    static MemberName generateNamedFunctionInvoker(final MethodTypeForm methodTypeForm) {
        final InvokerBytecodeGenerator invokerBytecodeGenerator = new InvokerBytecodeGenerator("NFI", "invoke_" + LambdaForm.shortenSignature(LambdaForm.basicTypeSignature(methodTypeForm.erasedType())), LambdaForm.NamedFunction.INVOKER_METHOD_TYPE);
        return invokerBytecodeGenerator.loadMethod(invokerBytecodeGenerator.generateNamedFunctionInvokerImpl(methodTypeForm));
    }
    
    private byte[] generateNamedFunctionInvokerImpl(final MethodTypeForm methodTypeForm) {
        final MethodType erasedType = methodTypeForm.erasedType();
        this.classFilePrologue();
        this.mv.visitAnnotation("Ljava/lang/invoke/LambdaForm$Hidden;", true);
        this.mv.visitAnnotation("Ljava/lang/invoke/ForceInline;", true);
        this.emitAloadInsn(0);
        for (int i = 0; i < erasedType.parameterCount(); ++i) {
            this.emitAloadInsn(1);
            this.emitIconstInsn(i);
            this.mv.visitInsn(50);
            final Class<?> parameterType = erasedType.parameterType(i);
            if (parameterType.isPrimitive()) {
                erasedType.basicType().wrap().parameterType(i);
                final Wrapper forBasicType = Wrapper.forBasicType(parameterType);
                final Wrapper wrapper = forBasicType.isSubwordOrInt() ? Wrapper.INT : forBasicType;
                this.emitUnboxing(wrapper);
                this.emitPrimCast(wrapper, forBasicType);
            }
        }
        this.mv.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", erasedType.basicType().toMethodDescriptorString(), false);
        final Class<?> returnType = erasedType.returnType();
        if (returnType != Void.TYPE && returnType.isPrimitive()) {
            final Wrapper forBasicType2 = Wrapper.forBasicType(returnType);
            final Wrapper wrapper2 = forBasicType2.isSubwordOrInt() ? Wrapper.INT : forBasicType2;
            this.emitPrimCast(forBasicType2, wrapper2);
            this.emitBoxing(wrapper2);
        }
        if (returnType == Void.TYPE) {
            this.mv.visitInsn(1);
        }
        this.emitReturnInsn(LambdaForm.BasicType.L_TYPE);
        this.classFileEpilogue();
        this.bogusMethod(erasedType);
        final byte[] byteArray = this.cw.toByteArray();
        maybeDump(this.className, byteArray);
        return byteArray;
    }
    
    private void bogusMethod(final Object... array) {
        if (MethodHandleStatics.DUMP_CLASS_FILES) {
            this.mv = this.cw.visitMethod(8, "dummy", "()V", null, null);
            for (int length = array.length, i = 0; i < length; ++i) {
                this.mv.visitLdcInsn(array[i].toString());
                this.mv.visitInsn(87);
            }
            this.mv.visitInsn(177);
            this.mv.visitMaxs(0, 0);
            this.mv.visitEnd();
        }
    }
    
    static {
        MEMBERNAME_FACTORY = MemberName.getFactory();
        HOST_CLASS = LambdaForm.class;
        Label_0122: {
            if (MethodHandleStatics.DUMP_CLASS_FILES) {
                DUMP_CLASS_FILES_COUNTERS = new HashMap<String, Integer>();
                try {
                    final File dump_CLASS_FILES_DIR = new File("DUMP_CLASS_FILES");
                    if (!dump_CLASS_FILES_DIR.exists()) {
                        dump_CLASS_FILES_DIR.mkdirs();
                    }
                    DUMP_CLASS_FILES_DIR = dump_CLASS_FILES_DIR;
                    System.out.println("Dumping class files to " + InvokerBytecodeGenerator.DUMP_CLASS_FILES_DIR + "/...");
                    break Label_0122;
                }
                catch (final Exception ex) {
                    throw MethodHandleStatics.newInternalError(ex);
                }
            }
            DUMP_CLASS_FILES_COUNTERS = null;
            DUMP_CLASS_FILES_DIR = null;
        }
        InvokerBytecodeGenerator.STATICALLY_INVOCABLE_PACKAGES = new Class[] { Object.class, Arrays.class, Unsafe.class };
    }
    
    class CpPatch
    {
        final int index;
        final String placeholder;
        final Object value;
        
        CpPatch(final int index, final String placeholder, final Object value) {
            this.index = index;
            this.placeholder = placeholder;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return "CpPatch/index=" + this.index + ",placeholder=" + this.placeholder + ",value=" + this.value;
        }
    }
}
