package java.lang.invoke;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

class Invokers
{
    private final MethodType targetType;
    @Stable
    private final MethodHandle[] invokers;
    static final int INV_EXACT = 0;
    static final int INV_GENERIC = 1;
    static final int INV_BASIC = 2;
    static final int INV_LIMIT = 3;
    private static final int MH_LINKER_ARG_APPENDED = 1;
    private static final LambdaForm.NamedFunction NF_checkExactType;
    private static final LambdaForm.NamedFunction NF_checkGenericType;
    private static final LambdaForm.NamedFunction NF_getCallSiteTarget;
    private static final LambdaForm.NamedFunction NF_checkCustomized;
    
    Invokers(final MethodType targetType) {
        this.invokers = new MethodHandle[3];
        this.targetType = targetType;
    }
    
    MethodHandle exactInvoker() {
        final MethodHandle cachedInvoker = this.cachedInvoker(0);
        if (cachedInvoker != null) {
            return cachedInvoker;
        }
        return this.setCachedInvoker(0, this.makeExactOrGeneralInvoker(true));
    }
    
    MethodHandle genericInvoker() {
        final MethodHandle cachedInvoker = this.cachedInvoker(1);
        if (cachedInvoker != null) {
            return cachedInvoker;
        }
        return this.setCachedInvoker(1, this.makeExactOrGeneralInvoker(false));
    }
    
    MethodHandle basicInvoker() {
        final MethodHandle cachedInvoker = this.cachedInvoker(2);
        if (cachedInvoker != null) {
            return cachedInvoker;
        }
        final MethodType basicType = this.targetType.basicType();
        if (basicType != this.targetType) {
            return this.setCachedInvoker(2, basicType.invokers().basicInvoker());
        }
        MethodHandle methodHandle = basicType.form().cachedMethodHandle(0);
        if (methodHandle == null) {
            final DirectMethodHandle make = DirectMethodHandle.make(invokeBasicMethod(basicType));
            assert this.checkInvoker(make);
            methodHandle = basicType.form().setCachedMethodHandle(0, make);
        }
        return this.setCachedInvoker(2, methodHandle);
    }
    
    private MethodHandle cachedInvoker(final int n) {
        return this.invokers[n];
    }
    
    private synchronized MethodHandle setCachedInvoker(final int n, final MethodHandle methodHandle) {
        final MethodHandle methodHandle2 = this.invokers[n];
        if (methodHandle2 != null) {
            return methodHandle2;
        }
        return this.invokers[n] = methodHandle;
    }
    
    private MethodHandle makeExactOrGeneralInvoker(final boolean b) {
        final MethodType targetType = this.targetType;
        final MethodHandle withInternalMemberName = BoundMethodHandle.bindSingle(targetType.invokerType(), invokeHandleForm(targetType, false, b ? 11 : 13), targetType).withInternalMemberName(MemberName.makeMethodHandleInvoke(b ? "invokeExact" : "invoke", targetType), false);
        assert this.checkInvoker(withInternalMemberName);
        this.maybeCompileToBytecode(withInternalMemberName);
        return withInternalMemberName;
    }
    
    private void maybeCompileToBytecode(final MethodHandle methodHandle) {
        if (this.targetType == this.targetType.erase() && this.targetType.parameterCount() < 10) {
            methodHandle.form.compileToBytecode();
        }
    }
    
    static MemberName invokeBasicMethod(final MethodType methodType) {
        assert methodType == methodType.basicType();
        try {
            return MethodHandles.Lookup.IMPL_LOOKUP.resolveOrFail((byte)5, MethodHandle.class, "invokeBasic", methodType);
        }
        catch (final ReflectiveOperationException ex) {
            throw MethodHandleStatics.newInternalError("JVM cannot find invoker for " + methodType, ex);
        }
    }
    
    private boolean checkInvoker(final MethodHandle methodHandle) {
        assert this.targetType.invokerType().equals((Object)methodHandle.type()) : Arrays.asList(this.targetType, this.targetType.invokerType(), methodHandle);
        assert !(!methodHandle.internalMemberName().getMethodType().equals((Object)this.targetType));
        assert !methodHandle.isVarargsCollector();
        return true;
    }
    
    MethodHandle spreadInvoker(final int n) {
        final int n2 = this.targetType.parameterCount() - n;
        final MethodType targetType = this.targetType;
        final Class<?> impliedRestargType = impliedRestargType(targetType, n);
        if (targetType.parameterSlotCount() <= 253) {
            return this.genericInvoker().asSpreader(impliedRestargType, n2);
        }
        return MethodHandles.filterArgument(MethodHandles.invoker(targetType.replaceParameterTypes(n, targetType.parameterCount(), impliedRestargType)), 0, MethodHandles.insertArguments(Lazy.MH_asSpreader, 1, impliedRestargType, n2));
    }
    
    private static Class<?> impliedRestargType(final MethodType methodType, final int n) {
        if (methodType.isGeneric()) {
            return Object[].class;
        }
        final int parameterCount = methodType.parameterCount();
        if (n >= parameterCount) {
            return Object[].class;
        }
        final Class<?> parameterType = methodType.parameterType(n);
        for (int i = n + 1; i < parameterCount; ++i) {
            if (parameterType != methodType.parameterType(i)) {
                throw MethodHandleStatics.newIllegalArgumentException("need homogeneous rest arguments", methodType);
            }
        }
        if (parameterType == Object.class) {
            return Object[].class;
        }
        return Array.newInstance(parameterType, 0).getClass();
    }
    
    @Override
    public String toString() {
        return "Invokers" + this.targetType;
    }
    
    static MemberName methodHandleInvokeLinkerMethod(final String s, final MethodType methodType, final Object[] array) {
        int n2 = 0;
        switch (s) {
            case "invokeExact": {
                n2 = 10;
                break;
            }
            case "invoke": {
                n2 = 12;
                break;
            }
            default: {
                throw new InternalError("not invoker: " + s);
            }
        }
        LambdaForm lambdaForm;
        if (methodType.parameterSlotCount() <= 253) {
            lambdaForm = invokeHandleForm(methodType, false, n2);
            array[0] = methodType;
        }
        else {
            lambdaForm = invokeHandleForm(methodType, true, n2);
        }
        return lambdaForm.vmentry;
    }
    
    private static LambdaForm invokeHandleForm(MethodType basicType, final boolean b, final int n) {
        boolean b2;
        if (!b) {
            basicType = basicType.basicType();
            b2 = true;
        }
        else {
            b2 = false;
        }
        boolean b3 = false;
        boolean b4 = false;
        String s = null;
        switch (n) {
            case 10: {
                b3 = true;
                b4 = false;
                s = "invokeExact_MT";
                break;
            }
            case 11: {
                b3 = false;
                b4 = false;
                s = "exactInvoker";
                break;
            }
            case 12: {
                b3 = true;
                b4 = true;
                s = "invoke_MT";
                break;
            }
            case 13: {
                b3 = false;
                b4 = true;
                s = "invoker";
                break;
            }
            default: {
                throw new InternalError();
            }
        }
        if (b2) {
            final LambdaForm cachedLambdaForm = basicType.form().cachedLambdaForm(n);
            if (cachedLambdaForm != null) {
                return cachedLambdaForm;
            }
        }
        final int n2 = 0 + (b3 ? 0 : 1);
        final int n3 = n2 + 1 + basicType.parameterCount();
        final int n4 = n3 + ((b3 && !b) ? 1 : 0);
        int n5 = n3;
        final int n6 = b ? -1 : n5++;
        final int n7 = n5++;
        final int n8 = (MethodHandleStatics.CUSTOMIZE_THRESHOLD >= 0) ? n5++ : -1;
        final int n9 = n5++;
        MethodType methodType = basicType.invokerType();
        if (b3) {
            if (!b) {
                methodType = methodType.appendParameterTypes(MemberName.class);
            }
        }
        else {
            methodType = methodType.invokerType();
        }
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n5 - n4, methodType);
        assert arguments.length == n5 : Arrays.asList(basicType, b, n, n5, arguments.length);
        if (n6 >= n4) {
            assert arguments[n6] == null;
            final BoundMethodHandle.SpeciesData speciesData_L = BoundMethodHandle.speciesData_L();
            arguments[0] = arguments[0].withConstraint(speciesData_L);
            arguments[n6] = new LambdaForm.Name(speciesData_L.getterFunction(0), new Object[] { arguments[0] });
        }
        final MethodType basicType2 = basicType.basicType();
        final Object[] copyOfRange = Arrays.copyOfRange(arguments, n2, n3, (Class<? extends Object[]>)Object[].class);
        final Object o = b ? basicType : arguments[n6];
        if (!b4) {
            arguments[n7] = new LambdaForm.Name(Invokers.NF_checkExactType, new Object[] { arguments[n2], o });
        }
        else {
            copyOfRange[0] = (arguments[n7] = new LambdaForm.Name(Invokers.NF_checkGenericType, new Object[] { arguments[n2], o }));
        }
        if (n8 != -1) {
            arguments[n8] = new LambdaForm.Name(Invokers.NF_checkCustomized, new Object[] { copyOfRange[0] });
        }
        arguments[n9] = new LambdaForm.Name(basicType2, copyOfRange);
        LambdaForm setCachedLambdaForm = new LambdaForm(s, n4, arguments);
        if (b3) {
            setCachedLambdaForm.compileToBytecode();
        }
        if (b2) {
            setCachedLambdaForm = basicType.form().setCachedLambdaForm(n, setCachedLambdaForm);
        }
        return setCachedLambdaForm;
    }
    
    static WrongMethodTypeException newWrongMethodTypeException(final MethodType methodType, final MethodType methodType2) {
        return new WrongMethodTypeException("expected " + methodType2 + " but found " + methodType);
    }
    
    @ForceInline
    static void checkExactType(final Object o, final Object o2) {
        final MethodHandle methodHandle = (MethodHandle)o;
        final MethodType methodType = (MethodType)o2;
        final MethodType type = methodHandle.type();
        if (type != methodType) {
            throw newWrongMethodTypeException(methodType, type);
        }
    }
    
    @ForceInline
    static Object checkGenericType(final Object o, final Object o2) {
        return ((MethodHandle)o).asType((MethodType)o2);
    }
    
    static MemberName linkToCallSiteMethod(final MethodType methodType) {
        return callSiteForm(methodType, false).vmentry;
    }
    
    static MemberName linkToTargetMethod(final MethodType methodType) {
        return callSiteForm(methodType, true).vmentry;
    }
    
    private static LambdaForm callSiteForm(MethodType basicType, final boolean b) {
        basicType = basicType.basicType();
        final int n = b ? 15 : 14;
        final LambdaForm cachedLambdaForm = basicType.form().cachedLambdaForm(n);
        if (cachedLambdaForm != null) {
            return cachedLambdaForm;
        }
        final int n2 = 0 + basicType.parameterCount();
        final int n3 = n2 + 1;
        int n4 = n2;
        final int n5 = n4++;
        final int n6 = b ? -1 : n5;
        final int n7 = b ? n5 : n4++;
        final int n8 = n4++;
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n4 - n3, basicType.appendParameterTypes((Class)(b ? MethodHandle.class : CallSite.class)));
        assert arguments.length == n4;
        assert arguments[n5] != null;
        if (!b) {
            arguments[n7] = new LambdaForm.Name(Invokers.NF_getCallSiteTarget, new Object[] { arguments[n6] });
        }
        final Object[] copyOfRange = Arrays.copyOfRange(arguments, 0, n2 + 1, (Class<? extends Object[]>)Object[].class);
        System.arraycopy(copyOfRange, 0, copyOfRange, 1, copyOfRange.length - 1);
        copyOfRange[0] = arguments[n7];
        arguments[n8] = new LambdaForm.Name(basicType, copyOfRange);
        final LambdaForm lambdaForm = new LambdaForm(b ? "linkToTargetMethod" : "linkToCallSite", n3, arguments);
        lambdaForm.compileToBytecode();
        return basicType.form().setCachedLambdaForm(n, lambdaForm);
    }
    
    @ForceInline
    static Object getCallSiteTarget(final Object o) {
        return ((CallSite)o).getTarget();
    }
    
    @ForceInline
    static void checkCustomized(final Object o) {
        final MethodHandle methodHandle = (MethodHandle)o;
        if (methodHandle.form.customized == null) {
            maybeCustomize(methodHandle);
        }
    }
    
    @DontInline
    static void maybeCustomize(final MethodHandle methodHandle) {
        final byte customizationCount = methodHandle.customizationCount;
        if (customizationCount >= MethodHandleStatics.CUSTOMIZE_THRESHOLD) {
            methodHandle.customize();
        }
        else {
            methodHandle.customizationCount = (byte)(customizationCount + 1);
        }
    }
    
    static {
        try {
            for (final LambdaForm.NamedFunction namedFunction : new LambdaForm.NamedFunction[] { NF_checkExactType = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkExactType", Object.class, Object.class)), NF_checkGenericType = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkGenericType", Object.class, Object.class)), NF_getCallSiteTarget = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("getCallSiteTarget", Object.class)), NF_checkCustomized = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkCustomized", Object.class)) }) {
                assert InvokerBytecodeGenerator.isStaticallyInvocable(namedFunction.member) : namedFunction;
                namedFunction.resolve();
            }
        }
        catch (final ReflectiveOperationException ex) {
            throw MethodHandleStatics.newInternalError(ex);
        }
    }
    
    private static class Lazy
    {
        private static final MethodHandle MH_asSpreader;
        
        static {
            try {
                MH_asSpreader = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(MethodHandle.class, "asSpreader", MethodType.methodType(MethodHandle.class, Class.class, Integer.TYPE));
            }
            catch (final ReflectiveOperationException ex) {
                throw MethodHandleStatics.newInternalError(ex);
            }
        }
    }
}
