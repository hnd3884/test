package java.lang.invoke;

import java.util.Arrays;

abstract class DelegatingMethodHandle extends MethodHandle
{
    static final LambdaForm.NamedFunction NF_getTarget;
    
    protected DelegatingMethodHandle(final MethodHandle methodHandle) {
        this(methodHandle.type(), methodHandle);
    }
    
    protected DelegatingMethodHandle(final MethodType methodType, final MethodHandle methodHandle) {
        super(methodType, chooseDelegatingForm(methodHandle));
    }
    
    protected DelegatingMethodHandle(final MethodType methodType, final LambdaForm lambdaForm) {
        super(methodType, lambdaForm);
    }
    
    protected abstract MethodHandle getTarget();
    
    @Override
    abstract MethodHandle asTypeUncached(final MethodType p0);
    
    @Override
    MemberName internalMemberName() {
        return this.getTarget().internalMemberName();
    }
    
    @Override
    boolean isInvokeSpecial() {
        return this.getTarget().isInvokeSpecial();
    }
    
    @Override
    Class<?> internalCallerClass() {
        return this.getTarget().internalCallerClass();
    }
    
    @Override
    MethodHandle copyWith(final MethodType methodType, final LambdaForm lambdaForm) {
        throw MethodHandleStatics.newIllegalArgumentException("do not use this");
    }
    
    @Override
    String internalProperties() {
        return "\n& Class=" + this.getClass().getSimpleName() + "\n& Target=" + this.getTarget().debugString();
    }
    
    @Override
    BoundMethodHandle rebind() {
        return this.getTarget().rebind();
    }
    
    private static LambdaForm chooseDelegatingForm(final MethodHandle methodHandle) {
        if (methodHandle instanceof SimpleMethodHandle) {
            return methodHandle.internalForm();
        }
        return makeReinvokerForm(methodHandle, 8, DelegatingMethodHandle.class, DelegatingMethodHandle.NF_getTarget);
    }
    
    static LambdaForm makeReinvokerForm(final MethodHandle methodHandle, final int n, final Object o, final LambdaForm.NamedFunction namedFunction) {
        String s = null;
        switch (n) {
            case 7: {
                s = "BMH.reinvoke";
                break;
            }
            case 8: {
                s = "MH.delegate";
                break;
            }
            default: {
                s = "MH.reinvoke";
                break;
            }
        }
        return makeReinvokerForm(methodHandle, n, o, s, true, namedFunction, null);
    }
    
    static LambdaForm makeReinvokerForm(final MethodHandle methodHandle, final int n, final Object o, final String s, final boolean b, final LambdaForm.NamedFunction namedFunction, final LambdaForm.NamedFunction namedFunction2) {
        final MethodType basicType = methodHandle.type().basicType();
        final boolean b2 = n < 0 || basicType.parameterSlotCount() > 253;
        final boolean b3 = namedFunction2 != null;
        if (!b2) {
            final LambdaForm cachedLambdaForm = basicType.form().cachedLambdaForm(n);
            if (cachedLambdaForm != null) {
                return cachedLambdaForm;
            }
        }
        int n3;
        final int n2 = n3 = 1 + basicType.parameterCount();
        final int n4 = b3 ? n3++ : -1;
        final int n5 = b2 ? -1 : n3++;
        final int n6 = n3++;
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n3 - n2, basicType.invokerType());
        assert arguments.length == n3;
        arguments[0] = arguments[0].withConstraint(o);
        if (b3) {
            arguments[n4] = new LambdaForm.Name(namedFunction2, new Object[] { arguments[0] });
        }
        if (b2) {
            arguments[n6] = new LambdaForm.Name(methodHandle, Arrays.copyOfRange(arguments, 1, n2, (Class<? extends Object[]>)Object[].class));
        }
        else {
            arguments[n5] = new LambdaForm.Name(namedFunction, new Object[] { arguments[0] });
            final Object[] copyOfRange = Arrays.copyOfRange(arguments, 0, n2, (Class<? extends Object[]>)Object[].class);
            copyOfRange[0] = arguments[n5];
            arguments[n6] = new LambdaForm.Name(basicType, copyOfRange);
        }
        LambdaForm setCachedLambdaForm = new LambdaForm(s, n2, arguments, b);
        if (!b2) {
            setCachedLambdaForm = basicType.form().setCachedLambdaForm(n, setCachedLambdaForm);
        }
        return setCachedLambdaForm;
    }
    
    static {
        try {
            NF_getTarget = new LambdaForm.NamedFunction(DelegatingMethodHandle.class.getDeclaredMethod("getTarget", (Class<?>[])new Class[0]));
        }
        catch (final ReflectiveOperationException ex) {
            throw MethodHandleStatics.newInternalError(ex);
        }
    }
}
