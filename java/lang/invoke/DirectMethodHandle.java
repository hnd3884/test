package java.lang.invoke;

import sun.misc.Unsafe;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import java.lang.ref.WeakReference;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyAccess;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class DirectMethodHandle extends MethodHandle
{
    final MemberName member;
    private static final MemberName.Factory IMPL_NAMES;
    private static byte AF_GETFIELD;
    private static byte AF_PUTFIELD;
    private static byte AF_GETSTATIC;
    private static byte AF_PUTSTATIC;
    private static byte AF_GETSTATIC_INIT;
    private static byte AF_PUTSTATIC_INIT;
    private static byte AF_LIMIT;
    private static int FT_LAST_WRAPPER;
    private static int FT_UNCHECKED_REF;
    private static int FT_CHECKED_REF;
    private static int FT_LIMIT;
    private static final LambdaForm[] ACCESSOR_FORMS;
    
    private DirectMethodHandle(final MethodType methodType, final LambdaForm lambdaForm, MemberName member) {
        super(methodType, lambdaForm);
        if (!member.isResolved()) {
            throw new InternalError();
        }
        if (member.getDeclaringClass().isInterface() && member.isMethod() && !member.isAbstract()) {
            final MemberName memberName = new MemberName(Object.class, member.getName(), member.getMethodType(), member.getReferenceKind());
            final MemberName resolveOrNull = MemberName.getFactory().resolveOrNull(memberName.getReferenceKind(), memberName, null);
            if (resolveOrNull != null && resolveOrNull.isPublic()) {
                assert member.getReferenceKind() == resolveOrNull.getReferenceKind();
                member = resolveOrNull;
            }
        }
        this.member = member;
    }
    
    static DirectMethodHandle make(final byte b, final Class<?> clazz, MemberName special) {
        MethodType methodType = special.getMethodOrFieldType();
        if (!special.isStatic()) {
            if (!special.getDeclaringClass().isAssignableFrom(clazz) || special.isConstructor()) {
                throw new InternalError(special.toString());
            }
            methodType = methodType.insertParameterTypes(0, clazz);
        }
        if (!special.isField()) {
            switch (b) {
                case 7: {
                    special = special.asSpecial();
                    return new Special(methodType, preparedLambdaForm(special), special);
                }
                case 9: {
                    return new Interface(methodType, preparedLambdaForm(special), special, (Class)clazz);
                }
                default: {
                    return new DirectMethodHandle(methodType, preparedLambdaForm(special), special);
                }
            }
        }
        else {
            final LambdaForm preparedFieldLambdaForm = preparedFieldLambdaForm(special);
            if (special.isStatic()) {
                return new StaticAccessor(methodType, preparedFieldLambdaForm, special, MethodHandleNatives.staticFieldBase(special), MethodHandleNatives.staticFieldOffset(special));
            }
            final long objectFieldOffset = MethodHandleNatives.objectFieldOffset(special);
            assert objectFieldOffset == (int)objectFieldOffset;
            return new Accessor(methodType, preparedFieldLambdaForm, special, (int)objectFieldOffset);
        }
    }
    
    static DirectMethodHandle make(final Class<?> clazz, final MemberName memberName) {
        byte referenceKind = memberName.getReferenceKind();
        if (referenceKind == 7) {
            referenceKind = 5;
        }
        return make(referenceKind, clazz, memberName);
    }
    
    static DirectMethodHandle make(final MemberName memberName) {
        if (memberName.isConstructor()) {
            return makeAllocator(memberName);
        }
        return make(memberName.getDeclaringClass(), memberName);
    }
    
    static DirectMethodHandle make(final Method method) {
        return make(method.getDeclaringClass(), new MemberName(method));
    }
    
    static DirectMethodHandle make(final Field field) {
        return make(field.getDeclaringClass(), new MemberName(field));
    }
    
    private static DirectMethodHandle makeAllocator(MemberName constructor) {
        assert constructor.isConstructor() && constructor.getName().equals("<init>");
        final Class<?> declaringClass = constructor.getDeclaringClass();
        constructor = constructor.asConstructor();
        assert constructor.isConstructor() && constructor.getReferenceKind() == 8 : constructor;
        final MethodType changeReturnType = constructor.getMethodType().changeReturnType(declaringClass);
        final LambdaForm preparedLambdaForm = preparedLambdaForm(constructor);
        final MemberName special = constructor.asSpecial();
        assert special.getMethodType().returnType() == Void.TYPE;
        return new Constructor(changeReturnType, preparedLambdaForm, constructor, special, (Class)declaringClass);
    }
    
    @Override
    BoundMethodHandle rebind() {
        return BoundMethodHandle.makeReinvoker(this);
    }
    
    @Override
    MethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
        assert this.getClass() == DirectMethodHandle.class;
        return new DirectMethodHandle(methodType, lambdaForm, this.member);
    }
    
    @Override
    String internalProperties() {
        return "\n& DMH.MN=" + this.internalMemberName();
    }
    
    @ForceInline
    @Override
    MemberName internalMemberName() {
        return this.member;
    }
    
    private static LambdaForm preparedLambdaForm(final MemberName memberName) {
        assert memberName.isInvocable() : memberName;
        final MethodType basicType = memberName.getInvocationType().basicType();
        assert !memberName.isMethodHandleInvoke() : memberName;
        int n = 0;
        switch (memberName.getReferenceKind()) {
            case 5: {
                n = 0;
                break;
            }
            case 6: {
                n = 1;
                break;
            }
            case 7: {
                n = 2;
                break;
            }
            case 9: {
                n = 4;
                break;
            }
            case 8: {
                n = 3;
                break;
            }
            default: {
                throw new InternalError(memberName.toString());
            }
        }
        if (n == 1 && shouldBeInitialized(memberName)) {
            preparedLambdaForm(basicType, n);
            n = 5;
        }
        final LambdaForm preparedLambdaForm = preparedLambdaForm(basicType, n);
        maybeCompile(preparedLambdaForm, memberName);
        assert preparedLambdaForm.methodType().dropParameterTypes(0, 1).equals((Object)memberName.getInvocationType().basicType()) : Arrays.asList(memberName, memberName.getInvocationType().basicType(), preparedLambdaForm, preparedLambdaForm.methodType());
        return preparedLambdaForm;
    }
    
    private static LambdaForm preparedLambdaForm(final MethodType methodType, final int n) {
        final LambdaForm cachedLambdaForm = methodType.form().cachedLambdaForm(n);
        if (cachedLambdaForm != null) {
            return cachedLambdaForm;
        }
        return methodType.form().setCachedLambdaForm(n, makePreparedLambdaForm(methodType, n));
    }
    
    private static LambdaForm makePreparedLambdaForm(final MethodType methodType, final int n) {
        final boolean b = n == 5;
        final boolean b2 = n == 3;
        final boolean b3 = n == 4;
        String s = null;
        String s2 = null;
        switch (n) {
            case 0: {
                s = "linkToVirtual";
                s2 = "DMH.invokeVirtual";
                break;
            }
            case 1: {
                s = "linkToStatic";
                s2 = "DMH.invokeStatic";
                break;
            }
            case 5: {
                s = "linkToStatic";
                s2 = "DMH.invokeStaticInit";
                break;
            }
            case 2: {
                s = "linkToSpecial";
                s2 = "DMH.invokeSpecial";
                break;
            }
            case 4: {
                s = "linkToInterface";
                s2 = "DMH.invokeInterface";
                break;
            }
            case 3: {
                s = "linkToSpecial";
                s2 = "DMH.newInvokeSpecial";
                break;
            }
            default: {
                throw new InternalError("which=" + n);
            }
        }
        MethodType methodType2 = methodType.appendParameterTypes(MemberName.class);
        if (b2) {
            methodType2 = methodType2.insertParameterTypes(0, Object.class).changeReturnType(Void.TYPE);
        }
        final MemberName memberName = new MemberName(MethodHandle.class, s, methodType2, (byte)6);
        MemberName resolveOrFail;
        try {
            resolveOrFail = DirectMethodHandle.IMPL_NAMES.resolveOrFail((byte)6, memberName, null, NoSuchMethodException.class);
        }
        catch (final ReflectiveOperationException ex) {
            throw MethodHandleStatics.newInternalError(ex);
        }
        int n3;
        final int n2 = n3 = 1 + methodType.parameterCount();
        final int n4 = b2 ? n3++ : -1;
        final int n5 = n3++;
        final int n6 = b3 ? n3++ : -1;
        final int n7 = n3++;
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n3 - n2, methodType.invokerType());
        assert arguments.length == n3;
        if (b2) {
            arguments[n4] = new LambdaForm.Name(Lazy.NF_allocateInstance, new Object[] { arguments[0] });
            arguments[n5] = new LambdaForm.Name(Lazy.NF_constructorMethod, new Object[] { arguments[0] });
        }
        else if (b) {
            arguments[n5] = new LambdaForm.Name(Lazy.NF_internalMemberNameEnsureInit, new Object[] { arguments[0] });
        }
        else {
            arguments[n5] = new LambdaForm.Name(Lazy.NF_internalMemberName, new Object[] { arguments[0] });
        }
        assert findDirectMethodHandle(arguments[n5]) == arguments[0];
        final Object[] copyOfRange = Arrays.copyOfRange(arguments, 1, n5 + 1, (Class<? extends Object[]>)Object[].class);
        if (b3) {
            copyOfRange[0] = (arguments[n6] = new LambdaForm.Name(Lazy.NF_checkReceiver, new Object[] { arguments[0], arguments[1] }));
        }
        assert copyOfRange[copyOfRange.length - 1] == arguments[n5];
        int n8 = -2;
        if (b2) {
            assert copyOfRange[copyOfRange.length - 2] == arguments[n4];
            System.arraycopy(copyOfRange, 0, copyOfRange, 1, copyOfRange.length - 2);
            copyOfRange[0] = arguments[n4];
            n8 = n4;
        }
        arguments[n7] = new LambdaForm.Name(resolveOrFail, copyOfRange);
        final LambdaForm lambdaForm = new LambdaForm(s2 + "_" + LambdaForm.shortenSignature(LambdaForm.basicTypeSignature(methodType)), n2, arguments, n8);
        lambdaForm.compileToBytecode();
        return lambdaForm;
    }
    
    static Object findDirectMethodHandle(final LambdaForm.Name name) {
        if (name.function != Lazy.NF_internalMemberName && name.function != Lazy.NF_internalMemberNameEnsureInit && name.function != Lazy.NF_constructorMethod) {
            return null;
        }
        assert name.arguments.length == 1;
        return name.arguments[0];
    }
    
    private static void maybeCompile(final LambdaForm lambdaForm, final MemberName memberName) {
        if (VerifyAccess.isSamePackage(memberName.getDeclaringClass(), MethodHandle.class)) {
            lambdaForm.compileToBytecode();
        }
    }
    
    @ForceInline
    static Object internalMemberName(final Object o) {
        return ((DirectMethodHandle)o).member;
    }
    
    static Object internalMemberNameEnsureInit(final Object o) {
        final DirectMethodHandle directMethodHandle = (DirectMethodHandle)o;
        directMethodHandle.ensureInitialized();
        return directMethodHandle.member;
    }
    
    static boolean shouldBeInitialized(final MemberName memberName) {
        switch (memberName.getReferenceKind()) {
            case 2:
            case 4:
            case 6:
            case 8: {
                final Class<?> declaringClass = memberName.getDeclaringClass();
                if (declaringClass == ValueConversions.class || declaringClass == MethodHandleImpl.class || declaringClass == Invokers.class) {
                    return false;
                }
                if (VerifyAccess.isSamePackage(MethodHandle.class, declaringClass) || VerifyAccess.isSamePackage(ValueConversions.class, declaringClass)) {
                    if (MethodHandleStatics.UNSAFE.shouldBeInitialized(declaringClass)) {
                        MethodHandleStatics.UNSAFE.ensureClassInitialized(declaringClass);
                    }
                    return false;
                }
                return MethodHandleStatics.UNSAFE.shouldBeInitialized(declaringClass);
            }
            default: {
                return false;
            }
        }
    }
    
    private void ensureInitialized() {
        if (checkInitialized(this.member)) {
            if (this.member.isField()) {
                this.updateForm(preparedFieldLambdaForm(this.member));
            }
            else {
                this.updateForm(preparedLambdaForm(this.member));
            }
        }
    }
    
    private static boolean checkInitialized(final MemberName memberName) {
        final Class<?> declaringClass = memberName.getDeclaringClass();
        final WeakReference weakReference = ((ClassValue<WeakReference>)EnsureInitialized.INSTANCE).get(declaringClass);
        if (weakReference == null) {
            return true;
        }
        if (weakReference.get() == Thread.currentThread()) {
            if (MethodHandleStatics.UNSAFE.shouldBeInitialized(declaringClass)) {
                return false;
            }
        }
        else {
            MethodHandleStatics.UNSAFE.ensureClassInitialized(declaringClass);
        }
        assert !MethodHandleStatics.UNSAFE.shouldBeInitialized(declaringClass);
        EnsureInitialized.INSTANCE.remove(declaringClass);
        return true;
    }
    
    static void ensureInitialized(final Object o) {
        ((DirectMethodHandle)o).ensureInitialized();
    }
    
    static Object constructorMethod(final Object o) {
        return ((Constructor)o).initMethod;
    }
    
    static Object allocateInstance(final Object o) throws InstantiationException {
        return MethodHandleStatics.UNSAFE.allocateInstance(((Constructor)o).instanceClass);
    }
    
    @ForceInline
    static long fieldOffset(final Object o) {
        return ((Accessor)o).fieldOffset;
    }
    
    @ForceInline
    static Object checkBase(final Object o) {
        o.getClass();
        return o;
    }
    
    @ForceInline
    static Object nullCheck(final Object o) {
        o.getClass();
        return o;
    }
    
    @ForceInline
    static Object staticBase(final Object o) {
        return ((StaticAccessor)o).staticBase;
    }
    
    @ForceInline
    static long staticOffset(final Object o) {
        return ((StaticAccessor)o).staticOffset;
    }
    
    @ForceInline
    static Object checkCast(final Object o, final Object o2) {
        return ((DirectMethodHandle)o).checkCast(o2);
    }
    
    Object checkCast(final Object o) {
        return this.member.getReturnType().cast(o);
    }
    
    private static int afIndex(final byte b, final boolean b2, final int n) {
        return b * DirectMethodHandle.FT_LIMIT * 2 + (b2 ? DirectMethodHandle.FT_LIMIT : 0) + n;
    }
    
    private static int ftypeKind(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return Wrapper.forPrimitiveType(clazz).ordinal();
        }
        if (VerifyType.isNullReferenceConversion(Object.class, clazz)) {
            return DirectMethodHandle.FT_UNCHECKED_REF;
        }
        return DirectMethodHandle.FT_CHECKED_REF;
    }
    
    private static LambdaForm preparedFieldLambdaForm(final MemberName memberName) {
        final Class<?> fieldType = memberName.getFieldType();
        final boolean volatile1 = memberName.isVolatile();
        byte b = 0;
        switch (memberName.getReferenceKind()) {
            case 1: {
                b = DirectMethodHandle.AF_GETFIELD;
                break;
            }
            case 3: {
                b = DirectMethodHandle.AF_PUTFIELD;
                break;
            }
            case 2: {
                b = DirectMethodHandle.AF_GETSTATIC;
                break;
            }
            case 4: {
                b = DirectMethodHandle.AF_PUTSTATIC;
                break;
            }
            default: {
                throw new InternalError(memberName.toString());
            }
        }
        if (shouldBeInitialized(memberName)) {
            preparedFieldLambdaForm(b, volatile1, fieldType);
            assert DirectMethodHandle.AF_GETSTATIC_INIT - DirectMethodHandle.AF_GETSTATIC == DirectMethodHandle.AF_PUTSTATIC_INIT - DirectMethodHandle.AF_PUTSTATIC;
            b += (byte)(DirectMethodHandle.AF_GETSTATIC_INIT - DirectMethodHandle.AF_GETSTATIC);
        }
        final LambdaForm preparedFieldLambdaForm = preparedFieldLambdaForm(b, volatile1, fieldType);
        maybeCompile(preparedFieldLambdaForm, memberName);
        assert preparedFieldLambdaForm.methodType().dropParameterTypes(0, 1).equals((Object)memberName.getInvocationType().basicType()) : Arrays.asList(memberName, memberName.getInvocationType().basicType(), preparedFieldLambdaForm, preparedFieldLambdaForm.methodType());
        return preparedFieldLambdaForm;
    }
    
    private static LambdaForm preparedFieldLambdaForm(final byte b, final boolean b2, final Class<?> clazz) {
        final int afIndex = afIndex(b, b2, ftypeKind(clazz));
        final LambdaForm lambdaForm = DirectMethodHandle.ACCESSOR_FORMS[afIndex];
        if (lambdaForm != null) {
            return lambdaForm;
        }
        return DirectMethodHandle.ACCESSOR_FORMS[afIndex] = makePreparedFieldLambdaForm(b, b2, ftypeKind(clazz));
    }
    
    private static LambdaForm makePreparedFieldLambdaForm(final byte b, final boolean b2, final int n) {
        final boolean b3 = (b & 0x1) == (DirectMethodHandle.AF_GETFIELD & 0x1);
        final boolean b4 = b >= DirectMethodHandle.AF_GETSTATIC;
        final boolean b5 = b >= DirectMethodHandle.AF_GETSTATIC_INIT;
        final boolean b6 = n == DirectMethodHandle.FT_CHECKED_REF;
        final Wrapper wrapper = b6 ? Wrapper.OBJECT : Wrapper.values()[n];
        final Class<?> primitiveType = wrapper.primitiveType();
        assert ftypeKind(b6 ? String.class : primitiveType) == n;
        final String primitiveSimpleName = wrapper.primitiveSimpleName();
        String s = Character.toUpperCase(primitiveSimpleName.charAt(0)) + primitiveSimpleName.substring(1);
        if (b2) {
            s += "Volatile";
        }
        final String string = (b3 ? "get" : "put") + s;
        MethodType methodType;
        if (b3) {
            methodType = MethodType.methodType(primitiveType, Object.class, Long.TYPE);
        }
        else {
            methodType = MethodType.methodType(Void.TYPE, Object.class, Long.TYPE, primitiveType);
        }
        final MemberName memberName = new MemberName(Unsafe.class, string, methodType, (byte)5);
        MemberName resolveOrFail;
        try {
            resolveOrFail = DirectMethodHandle.IMPL_NAMES.resolveOrFail((byte)5, memberName, null, NoSuchMethodException.class);
        }
        catch (final ReflectiveOperationException ex) {
            throw MethodHandleStatics.newInternalError(ex);
        }
        MethodType methodType2;
        if (b3) {
            methodType2 = MethodType.methodType(primitiveType);
        }
        else {
            methodType2 = MethodType.methodType(Void.TYPE, primitiveType);
        }
        MethodType methodType3 = methodType2.basicType();
        if (!b4) {
            methodType3 = methodType3.insertParameterTypes(0, Object.class);
        }
        final int n2 = 1 + methodType3.parameterCount();
        final int n3 = b4 ? -1 : 1;
        final int n4 = b3 ? -1 : (n2 - 1);
        int n5 = n2;
        final int n6 = b4 ? n5++ : -1;
        final int n7 = n5++;
        final int n8 = (n3 >= 0) ? n5++ : -1;
        final int n9 = b5 ? n5++ : -1;
        final int n10 = (b6 && !b3) ? n5++ : -1;
        final int n11 = n5++;
        final int n12 = (b6 && b3) ? n5++ : -1;
        final int n13 = n5 - 1;
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n5 - n2, methodType3.invokerType());
        if (b5) {
            arguments[n9] = new LambdaForm.Name(Lazy.NF_ensureInitialized, new Object[] { arguments[0] });
        }
        if (b6 && !b3) {
            arguments[n10] = new LambdaForm.Name(Lazy.NF_checkCast, new Object[] { arguments[0], arguments[n4] });
        }
        final Object[] array = new Object[1 + methodType.parameterCount()];
        assert array.length == (b3 ? 3 : 4);
        array[0] = MethodHandleStatics.UNSAFE;
        if (b4) {
            array[1] = (arguments[n6] = new LambdaForm.Name(Lazy.NF_staticBase, new Object[] { arguments[0] }));
            array[2] = (arguments[n7] = new LambdaForm.Name(Lazy.NF_staticOffset, new Object[] { arguments[0] }));
        }
        else {
            array[1] = (arguments[n8] = new LambdaForm.Name(Lazy.NF_checkBase, new Object[] { arguments[n3] }));
            array[2] = (arguments[n7] = new LambdaForm.Name(Lazy.NF_fieldOffset, new Object[] { arguments[0] }));
        }
        if (!b3) {
            array[3] = (b6 ? arguments[n10] : arguments[n4]);
        }
        for (final Object o : array) {
            assert o != null;
        }
        arguments[n11] = new LambdaForm.Name(resolveOrFail, array);
        if (b6 && b3) {
            arguments[n12] = new LambdaForm.Name(Lazy.NF_checkCast, new Object[] { arguments[0], arguments[n11] });
        }
        for (final LambdaForm.Name name : arguments) {
            assert name != null;
        }
        String s2 = string + (b4 ? "Static" : "Field");
        if (b6) {
            s2 += "Cast";
        }
        if (b5) {
            s2 += "Init";
        }
        return new LambdaForm(s2, n2, arguments, n13);
    }
    
    static {
        IMPL_NAMES = MemberName.getFactory();
        DirectMethodHandle.AF_GETFIELD = 0;
        DirectMethodHandle.AF_PUTFIELD = 1;
        DirectMethodHandle.AF_GETSTATIC = 2;
        DirectMethodHandle.AF_PUTSTATIC = 3;
        DirectMethodHandle.AF_GETSTATIC_INIT = 4;
        DirectMethodHandle.AF_PUTSTATIC_INIT = 5;
        DirectMethodHandle.AF_LIMIT = 6;
        DirectMethodHandle.FT_LAST_WRAPPER = Wrapper.values().length - 1;
        DirectMethodHandle.FT_UNCHECKED_REF = Wrapper.OBJECT.ordinal();
        DirectMethodHandle.FT_CHECKED_REF = DirectMethodHandle.FT_LAST_WRAPPER + 1;
        DirectMethodHandle.FT_LIMIT = DirectMethodHandle.FT_LAST_WRAPPER + 2;
        ACCESSOR_FORMS = new LambdaForm[afIndex(DirectMethodHandle.AF_LIMIT, false, 0)];
    }
    
    private static class EnsureInitialized extends ClassValue<WeakReference<Thread>>
    {
        static final EnsureInitialized INSTANCE;
        
        @Override
        protected WeakReference<Thread> computeValue(final Class<?> clazz) {
            MethodHandleStatics.UNSAFE.ensureClassInitialized(clazz);
            if (MethodHandleStatics.UNSAFE.shouldBeInitialized(clazz)) {
                return new WeakReference<Thread>(Thread.currentThread());
            }
            return null;
        }
        
        static {
            INSTANCE = new EnsureInitialized();
        }
    }
    
    static class Special extends DirectMethodHandle
    {
        private Special(final MethodType methodType, final LambdaForm lambdaForm, final MemberName memberName) {
            super(methodType, lambdaForm, memberName, null);
        }
        
        @Override
        boolean isInvokeSpecial() {
            return true;
        }
        
        @Override
        MethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
            return new Special(methodType, lambdaForm, this.member);
        }
    }
    
    static class Interface extends DirectMethodHandle
    {
        private final Class<?> refc;
        
        private Interface(final MethodType methodType, final LambdaForm lambdaForm, final MemberName memberName, final Class<?> refc) {
            super(methodType, lambdaForm, memberName, null);
            assert refc.isInterface() : refc;
            this.refc = refc;
        }
        
        @Override
        MethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
            return new Interface(methodType, lambdaForm, this.member, this.refc);
        }
        
        Object checkReceiver(final Object o) {
            if (!this.refc.isInstance(o)) {
                throw new IncompatibleClassChangeError(String.format("Class %s does not implement the requested interface %s", o.getClass().getName(), this.refc.getName()));
            }
            return o;
        }
    }
    
    static class Constructor extends DirectMethodHandle
    {
        final MemberName initMethod;
        final Class<?> instanceClass;
        
        private Constructor(final MethodType methodType, final LambdaForm lambdaForm, final MemberName memberName, final MemberName initMethod, final Class<?> instanceClass) {
            super(methodType, lambdaForm, memberName, null);
            this.initMethod = initMethod;
            this.instanceClass = instanceClass;
            assert initMethod.isResolved();
        }
        
        @Override
        MethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
            return new Constructor(methodType, lambdaForm, this.member, this.initMethod, this.instanceClass);
        }
    }
    
    static class Accessor extends DirectMethodHandle
    {
        final Class<?> fieldType;
        final int fieldOffset;
        
        private Accessor(final MethodType methodType, final LambdaForm lambdaForm, final MemberName memberName, final int fieldOffset) {
            super(methodType, lambdaForm, memberName, null);
            this.fieldType = memberName.getFieldType();
            this.fieldOffset = fieldOffset;
        }
        
        @Override
        Object checkCast(final Object o) {
            return this.fieldType.cast(o);
        }
        
        @Override
        MethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
            return new Accessor(methodType, lambdaForm, this.member, this.fieldOffset);
        }
    }
    
    static class StaticAccessor extends DirectMethodHandle
    {
        private final Class<?> fieldType;
        private final Object staticBase;
        private final long staticOffset;
        
        private StaticAccessor(final MethodType methodType, final LambdaForm lambdaForm, final MemberName memberName, final Object staticBase, final long staticOffset) {
            super(methodType, lambdaForm, memberName, null);
            this.fieldType = memberName.getFieldType();
            this.staticBase = staticBase;
            this.staticOffset = staticOffset;
        }
        
        @Override
        Object checkCast(final Object o) {
            return this.fieldType.cast(o);
        }
        
        @Override
        MethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
            return new StaticAccessor(methodType, lambdaForm, this.member, this.staticBase, this.staticOffset);
        }
    }
    
    private static class Lazy
    {
        static final LambdaForm.NamedFunction NF_internalMemberName;
        static final LambdaForm.NamedFunction NF_internalMemberNameEnsureInit;
        static final LambdaForm.NamedFunction NF_ensureInitialized;
        static final LambdaForm.NamedFunction NF_fieldOffset;
        static final LambdaForm.NamedFunction NF_checkBase;
        static final LambdaForm.NamedFunction NF_staticBase;
        static final LambdaForm.NamedFunction NF_staticOffset;
        static final LambdaForm.NamedFunction NF_checkCast;
        static final LambdaForm.NamedFunction NF_allocateInstance;
        static final LambdaForm.NamedFunction NF_constructorMethod;
        static final LambdaForm.NamedFunction NF_checkReceiver;
        
        static {
            try {
                for (final LambdaForm.NamedFunction namedFunction : new LambdaForm.NamedFunction[] { NF_internalMemberName = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("internalMemberName", Object.class)), NF_internalMemberNameEnsureInit = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("internalMemberNameEnsureInit", Object.class)), NF_ensureInitialized = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("ensureInitialized", Object.class)), NF_fieldOffset = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("fieldOffset", Object.class)), NF_checkBase = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("checkBase", Object.class)), NF_staticBase = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("staticBase", Object.class)), NF_staticOffset = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("staticOffset", Object.class)), NF_checkCast = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("checkCast", Object.class, Object.class)), NF_allocateInstance = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("allocateInstance", Object.class)), NF_constructorMethod = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("constructorMethod", Object.class)), NF_checkReceiver = new LambdaForm.NamedFunction(new MemberName(Interface.class.getDeclaredMethod("checkReceiver", Object.class))) }) {
                    assert InvokerBytecodeGenerator.isStaticallyInvocable(namedFunction.member) : namedFunction;
                    namedFunction.resolve();
                }
            }
            catch (final ReflectiveOperationException ex) {
                throw MethodHandleStatics.newInternalError(ex);
            }
        }
    }
}
