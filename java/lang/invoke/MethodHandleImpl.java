package java.lang.invoke;

import java.io.InputStream;
import java.net.URLConnection;
import java.io.IOException;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import sun.invoke.empty.Empty;
import java.lang.reflect.Array;
import java.util.Arrays;
import sun.invoke.util.Wrapper;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyType;
import java.util.function.Function;

abstract class MethodHandleImpl
{
    private static final int MAX_ARITY;
    private static final Function<MethodHandle, LambdaForm> PRODUCE_BLOCK_INLINING_FORM;
    private static final Function<MethodHandle, LambdaForm> PRODUCE_REINVOKER_FORM;
    static MethodHandle[] FAKE_METHOD_HANDLE_INVOKE;
    private static final Object[] NO_ARGS_ARRAY;
    private static final int FILL_ARRAYS_COUNT = 11;
    private static final int LEFT_ARGS = 10;
    private static final MethodHandle[] FILL_ARRAY_TO_RIGHT;
    private static final ClassValue<MethodHandle[]> TYPED_COLLECTORS;
    static final int MAX_JVM_ARITY = 255;
    
    static void initStatics() {
        MemberName.Factory.INSTANCE.getClass();
    }
    
    static MethodHandle makeArrayElementAccessor(final Class<?> clazz, final boolean b) {
        if (clazz == Object[].class) {
            return b ? ArrayAccessor.OBJECT_ARRAY_SETTER : ArrayAccessor.OBJECT_ARRAY_GETTER;
        }
        if (!clazz.isArray()) {
            throw MethodHandleStatics.newIllegalArgumentException("not an array: " + clazz);
        }
        final MethodHandle[] array = ArrayAccessor.TYPED_ACCESSORS.get(clazz);
        final int n = b ? 1 : 0;
        final MethodHandle methodHandle = array[n];
        if (methodHandle != null) {
            return methodHandle;
        }
        MethodHandle methodHandle2 = ArrayAccessor.getAccessor(clazz, b);
        final MethodType correctType = ArrayAccessor.correctType(clazz, b);
        if (methodHandle2.type() != correctType) {
            assert methodHandle2.type().parameterType(0) == Object[].class;
            assert (b ? methodHandle2.type().parameterType(2) : methodHandle2.type().returnType()) == Object.class;
            assert correctType.parameterType(0).getComponentType() == correctType.returnType();
            methodHandle2 = methodHandle2.viewAsType(correctType, false);
        }
        MethodHandle intrinsic = makeIntrinsic(methodHandle2, b ? Intrinsic.ARRAY_STORE : Intrinsic.ARRAY_LOAD);
        synchronized (array) {
            if (array[n] == null) {
                array[n] = intrinsic;
            }
            else {
                intrinsic = array[n];
            }
        }
        return intrinsic;
    }
    
    static MethodHandle makePairwiseConvert(final MethodHandle methodHandle, final MethodType methodType, final boolean b, final boolean b2) {
        if (methodType == methodHandle.type()) {
            return methodHandle;
        }
        return makePairwiseConvertByEditor(methodHandle, methodType, b, b2);
    }
    
    private static int countNonNull(final Object[] array) {
        int n = 0;
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] != null) {
                ++n;
            }
        }
        return n;
    }
    
    static MethodHandle makePairwiseConvertByEditor(final MethodHandle methodHandle, final MethodType methodType, final boolean b, final boolean b2) {
        final Object[] computeValueConversions = computeValueConversions(methodType, methodHandle.type(), b, b2);
        int countNonNull = countNonNull(computeValueConversions);
        if (countNonNull == 0) {
            return methodHandle.viewAsType(methodType, b);
        }
        final MethodType basicType = methodType.basicType();
        MethodType methodType2 = methodHandle.type().basicType();
        BoundMethodHandle boundMethodHandle = methodHandle.rebind();
        for (int i = 0; i < computeValueConversions.length - 1; ++i) {
            final Object o = computeValueConversions[i];
            if (o != null) {
                MethodHandle bindTo;
                if (o instanceof Class) {
                    bindTo = Lazy.MH_castReference.bindTo(o);
                }
                else {
                    bindTo = (MethodHandle)o;
                }
                final Class<?> parameterType = basicType.parameterType(i);
                if (--countNonNull == 0) {
                    methodType2 = methodType;
                }
                else {
                    methodType2 = methodType2.changeParameterType(i, parameterType);
                }
                boundMethodHandle = boundMethodHandle.copyWithExtendL(methodType2, boundMethodHandle.editor().filterArgumentForm(1 + i, LambdaForm.BasicType.basicType(parameterType)), bindTo).rebind();
            }
        }
        final Object o2 = computeValueConversions[computeValueConversions.length - 1];
        if (o2 != null) {
            Object bindTo2;
            if (o2 instanceof Class) {
                if (o2 == Void.TYPE) {
                    bindTo2 = null;
                }
                else {
                    bindTo2 = Lazy.MH_castReference.bindTo(o2);
                }
            }
            else {
                bindTo2 = o2;
            }
            final Class<?> returnType = basicType.returnType();
            assert --countNonNull == 0;
            if (bindTo2 != null) {
                final BoundMethodHandle rebind = boundMethodHandle.rebind();
                boundMethodHandle = rebind.copyWithExtendL(methodType, rebind.editor().filterReturnForm(LambdaForm.BasicType.basicType(returnType), false), bindTo2);
            }
            else {
                boundMethodHandle = boundMethodHandle.copyWith(methodType, boundMethodHandle.editor().filterReturnForm(LambdaForm.BasicType.basicType(returnType), true));
            }
        }
        assert countNonNull == 0;
        assert boundMethodHandle.type().equals((Object)methodType);
        return boundMethodHandle;
    }
    
    static MethodHandle makePairwiseConvertIndirect(final MethodHandle methodHandle, final MethodType methodType, final boolean b, final boolean b2) {
        assert methodHandle.type().parameterCount() == methodType.parameterCount();
        final Object[] computeValueConversions = computeValueConversions(methodType, methodHandle.type(), b, b2);
        final int parameterCount = methodType.parameterCount();
        int countNonNull = countNonNull(computeValueConversions);
        int n = (computeValueConversions[parameterCount] != null) ? 1 : 0;
        final boolean b3 = methodType.returnType() == Void.TYPE;
        if (n != 0 && b3) {
            --countNonNull;
            n = 0;
        }
        final int n2 = 1 + parameterCount;
        final int n3 = n2 + countNonNull + 1;
        final int n4 = (n == 0) ? -1 : (n3 - 1);
        final int n5 = ((n == 0) ? n3 : n4) - 1;
        final int n6 = b3 ? -1 : (n3 - 1);
        final MethodType invokerType = methodType.basicType().invokerType();
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n3 - n2, invokerType);
        final Object[] array = new Object[0 + parameterCount];
        int n7 = n2;
        for (int i = 0; i < parameterCount; ++i) {
            final Object o = computeValueConversions[i];
            if (o == null) {
                array[0 + i] = arguments[1 + i];
            }
            else {
                LambdaForm.Name name;
                if (o instanceof Class) {
                    name = new LambdaForm.Name(Lazy.MH_castReference, new Object[] { (Class<?>)o, arguments[1 + i] });
                }
                else {
                    name = new LambdaForm.Name((MethodHandle)o, new Object[] { arguments[1 + i] });
                }
                assert arguments[n7] == null;
                arguments[n7++] = name;
                assert array[0 + i] == null;
                array[0 + i] = name;
            }
        }
        assert n7 == n5;
        arguments[n5] = new LambdaForm.Name(methodHandle, array);
        final Object o2 = computeValueConversions[parameterCount];
        if (n == 0) {
            assert n5 == arguments.length - 1;
        }
        else {
            LambdaForm.Name name2;
            if (o2 == Void.TYPE) {
                name2 = new LambdaForm.Name(LambdaForm.constantZero(LambdaForm.BasicType.basicType(methodType.returnType())), new Object[0]);
            }
            else if (o2 instanceof Class) {
                name2 = new LambdaForm.Name(Lazy.MH_castReference, new Object[] { (Class<Void>)o2, arguments[n5] });
            }
            else {
                final MethodHandle methodHandle2 = (MethodHandle)o2;
                if (methodHandle2.type().parameterCount() == 0) {
                    name2 = new LambdaForm.Name(methodHandle2, new Object[0]);
                }
                else {
                    name2 = new LambdaForm.Name(methodHandle2, new Object[] { arguments[n5] });
                }
            }
            assert arguments[n4] == null;
            arguments[n4] = name2;
            assert n4 == arguments.length - 1;
        }
        return SimpleMethodHandle.make(methodType, new LambdaForm("convert", invokerType.parameterCount(), arguments, n6));
    }
    
    @ForceInline
    static <T, U> T castReference(final Class<? extends T> clazz, final U u) {
        if (u != null && !clazz.isInstance(u)) {
            throw newClassCastException(clazz, u);
        }
        return (T)u;
    }
    
    private static ClassCastException newClassCastException(final Class<?> clazz, final Object o) {
        return new ClassCastException("Cannot cast " + o.getClass().getName() + " to " + clazz.getName());
    }
    
    static Object[] computeValueConversions(final MethodType methodType, final MethodType methodType2, final boolean b, final boolean b2) {
        final int parameterCount = methodType.parameterCount();
        final Object[] array = new Object[parameterCount + 1];
        for (int i = 0; i <= parameterCount; ++i) {
            final boolean b3 = i == parameterCount;
            final Class<?> clazz = b3 ? methodType2.returnType() : methodType.parameterType(i);
            final Class<?> clazz2 = b3 ? methodType.returnType() : methodType2.parameterType(i);
            if (!VerifyType.isNullConversion(clazz, clazz2, b)) {
                array[i] = valueConversion(clazz, clazz2, b, b2);
            }
        }
        return array;
    }
    
    static MethodHandle makePairwiseConvert(final MethodHandle methodHandle, final MethodType methodType, final boolean b) {
        return makePairwiseConvert(methodHandle, methodType, b, false);
    }
    
    static Object valueConversion(final Class<?> clazz, final Class<?> clazz2, final boolean b, final boolean b2) {
        assert !VerifyType.isNullConversion(clazz, clazz2, b);
        if (clazz2 == Void.TYPE) {
            return clazz2;
        }
        MethodHandle methodHandle;
        if (clazz.isPrimitive()) {
            if (clazz == Void.TYPE) {
                return Void.TYPE;
            }
            if (clazz2.isPrimitive()) {
                methodHandle = ValueConversions.convertPrimitive(clazz, clazz2);
            }
            else {
                final Wrapper forPrimitiveType = Wrapper.forPrimitiveType(clazz);
                methodHandle = ValueConversions.boxExact(forPrimitiveType);
                assert methodHandle.type().parameterType(0) == forPrimitiveType.primitiveType();
                assert methodHandle.type().returnType() == forPrimitiveType.wrapperType();
                if (!VerifyType.isNullConversion(forPrimitiveType.wrapperType(), clazz2, b)) {
                    final MethodType methodType = MethodType.methodType(clazz2, clazz);
                    if (b) {
                        methodHandle = methodHandle.asType(methodType);
                    }
                    else {
                        methodHandle = makePairwiseConvert(methodHandle, methodType, false);
                    }
                }
            }
        }
        else {
            if (!clazz2.isPrimitive()) {
                return clazz2;
            }
            final Wrapper forPrimitiveType2 = Wrapper.forPrimitiveType(clazz2);
            if (b2 || clazz == forPrimitiveType2.wrapperType()) {
                methodHandle = ValueConversions.unboxExact(forPrimitiveType2, b);
            }
            else {
                methodHandle = (b ? ValueConversions.unboxWiden(forPrimitiveType2) : ValueConversions.unboxCast(forPrimitiveType2));
            }
        }
        assert methodHandle.type().parameterCount() <= 1 : "pc" + Arrays.asList(clazz.getSimpleName(), clazz2.getSimpleName(), methodHandle);
        return methodHandle;
    }
    
    static MethodHandle makeVarargsCollector(MethodHandle methodHandle, final Class<?> clazz) {
        final MethodType type = methodHandle.type();
        final int n = type.parameterCount() - 1;
        if (type.parameterType(n) != clazz) {
            methodHandle = methodHandle.asType(type.changeParameterType(n, clazz));
        }
        methodHandle = methodHandle.asFixedArity();
        return new AsVarargsCollector(methodHandle, clazz);
    }
    
    static MethodHandle makeSpreadArguments(MethodHandle type, final Class<?> clazz, final int n, final int n2) {
        MethodType methodType = type.type();
        for (int i = 0; i < n2; ++i) {
            Class<?> spreadArgElementType = VerifyType.spreadArgElementType(clazz, i);
            if (spreadArgElementType == null) {
                spreadArgElementType = Object.class;
            }
            methodType = methodType.changeParameterType(n + i, spreadArgElementType);
        }
        type = type.asType(methodType);
        final MethodType replaceParameterTypes = methodType.replaceParameterTypes(n, n + n2, clazz);
        final MethodType invokerType = replaceParameterTypes.invokerType();
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n2 + 2, invokerType);
        int parameterCount = invokerType.parameterCount();
        final int[] array = new int[methodType.parameterCount()];
        for (int j = 0, n3 = 1; j < methodType.parameterCount() + 1; ++j, ++n3) {
            invokerType.parameterType(j);
            if (j == n) {
                final MethodHandle arrayElementGetter = MethodHandles.arrayElementGetter(clazz);
                final LambdaForm.Name name = arguments[n3];
                arguments[parameterCount++] = new LambdaForm.Name(Lazy.NF_checkSpreadArgument, new Object[] { name, n2 });
                for (int k = 0; k < n2; ++k) {
                    array[j] = parameterCount;
                    arguments[parameterCount++] = new LambdaForm.Name(arrayElementGetter, new Object[] { name, k });
                    ++j;
                }
            }
            else if (j < array.length) {
                array[j] = n3;
            }
        }
        assert parameterCount == arguments.length - 1;
        final LambdaForm.Name[] array2 = new LambdaForm.Name[methodType.parameterCount()];
        for (int l = 0; l < methodType.parameterCount(); ++l) {
            array2[l] = arguments[array[l]];
        }
        arguments[arguments.length - 1] = new LambdaForm.Name(type, (Object[])array2);
        return SimpleMethodHandle.make(replaceParameterTypes, new LambdaForm("spread", invokerType.parameterCount(), arguments));
    }
    
    static void checkSpreadArgument(final Object o, final int n) {
        if (o == null) {
            if (n == 0) {
                return;
            }
        }
        else if (o instanceof Object[]) {
            if (((Object[])o).length == n) {
                return;
            }
        }
        else if (Array.getLength(o) == n) {
            return;
        }
        throw MethodHandleStatics.newIllegalArgumentException("array is not of length " + n);
    }
    
    static MethodHandle makeCollectArguments(final MethodHandle methodHandle, final MethodHandle methodHandle2, final int n, final boolean b) {
        final MethodType type = methodHandle.type();
        final MethodType type2 = methodHandle2.type();
        final int parameterCount = type2.parameterCount();
        final Class<?> returnType = type2.returnType();
        MethodType methodType = type.dropParameterTypes(n, n + ((returnType != Void.TYPE) ? 1 : 0));
        if (!b) {
            methodType = methodType.insertParameterTypes(n, type2.parameterList());
        }
        final MethodType invokerType = methodType.invokerType();
        final LambdaForm.Name[] arguments = LambdaForm.arguments(2, invokerType);
        final int n2 = arguments.length - 2;
        final int n3 = arguments.length - 1;
        arguments[n2] = new LambdaForm.Name(methodHandle2, (Object[])Arrays.copyOfRange(arguments, 1 + n, 1 + n + parameterCount));
        final LambdaForm.Name[] array = new LambdaForm.Name[type.parameterCount()];
        final int n4 = 1;
        final int n5 = 0;
        System.arraycopy(arguments, n4, array, n5, n);
        final int n6 = n4 + n;
        int n7 = n5 + n;
        if (returnType != Void.TYPE) {
            array[n7++] = arguments[n2];
        }
        final int n8 = parameterCount;
        if (b) {
            System.arraycopy(arguments, n6, array, n7, n8);
            n7 += n8;
        }
        final int n9 = n6 + n8;
        final int n10 = array.length - n7;
        System.arraycopy(arguments, n9, array, n7, n10);
        assert n9 + n10 == n2;
        arguments[n3] = new LambdaForm.Name(methodHandle, (Object[])array);
        return SimpleMethodHandle.make(methodType, new LambdaForm("collect", invokerType.parameterCount(), arguments));
    }
    
    @LambdaForm.Hidden
    static MethodHandle selectAlternative(final boolean b, final MethodHandle methodHandle, final MethodHandle methodHandle2) {
        if (b) {
            return methodHandle;
        }
        return methodHandle2;
    }
    
    @LambdaForm.Hidden
    static boolean profileBoolean(final boolean b, final int[] array) {
        final int n = b ? 1 : 0;
        try {
            array[n] = Math.addExact(array[n], 1);
        }
        catch (final ArithmeticException ex) {
            array[n] /= 2;
        }
        return b;
    }
    
    static MethodHandle makeGuardWithTest(final MethodHandle methodHandle, final MethodHandle methodHandle2, final MethodHandle methodHandle3) {
        final MethodType type = methodHandle2.type();
        assert methodHandle.type().equals((Object)type.changeReturnType(Boolean.TYPE)) && methodHandle3.type().equals((Object)type);
        final LambdaForm guardWithTestForm = makeGuardWithTestForm(type.basicType());
        BoundMethodHandle boundMethodHandle;
        try {
            if (MethodHandleStatics.PROFILE_GWT) {
                boundMethodHandle = BoundMethodHandle.speciesData_LLLL().constructor().invokeBasic(type, guardWithTestForm, (Object)methodHandle, (Object)profile(methodHandle2), (Object)profile(methodHandle3), new int[2]);
            }
            else {
                boundMethodHandle = BoundMethodHandle.speciesData_LLL().constructor().invokeBasic(type, guardWithTestForm, (Object)methodHandle, (Object)profile(methodHandle2), (Object)profile(methodHandle3));
            }
        }
        catch (final Throwable t) {
            throw MethodHandleStatics.uncaughtException(t);
        }
        assert boundMethodHandle.type() == type;
        return boundMethodHandle;
    }
    
    static MethodHandle profile(final MethodHandle methodHandle) {
        if (MethodHandleStatics.DONT_INLINE_THRESHOLD >= 0) {
            return makeBlockInlningWrapper(methodHandle);
        }
        return methodHandle;
    }
    
    static MethodHandle makeBlockInlningWrapper(final MethodHandle methodHandle) {
        return new CountingWrapper(methodHandle, (LambdaForm)MethodHandleImpl.PRODUCE_BLOCK_INLINING_FORM.apply(methodHandle), (Function)MethodHandleImpl.PRODUCE_BLOCK_INLINING_FORM, (Function)MethodHandleImpl.PRODUCE_REINVOKER_FORM, MethodHandleStatics.DONT_INLINE_THRESHOLD);
    }
    
    static LambdaForm makeGuardWithTestForm(final MethodType methodType) {
        final LambdaForm cachedLambdaForm = methodType.form().cachedLambdaForm(17);
        if (cachedLambdaForm != null) {
            return cachedLambdaForm;
        }
        int n2;
        final int n = n2 = 1 + methodType.parameterCount();
        final int n3 = n2++;
        final int n4 = n2++;
        final int n5 = n2++;
        final int n6 = MethodHandleStatics.PROFILE_GWT ? n2++ : -1;
        final int n7 = n2++;
        final int n8 = (n6 != -1) ? n2++ : -1;
        final int n9 = n2 - 1;
        final int n10 = n2++;
        final int n11 = n2++;
        assert n11 == n10 + 1;
        final MethodType invokerType = methodType.invokerType();
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n2 - n, invokerType);
        final BoundMethodHandle.SpeciesData speciesData = (n6 != -1) ? BoundMethodHandle.speciesData_LLLL() : BoundMethodHandle.speciesData_LLL();
        arguments[0] = arguments[0].withConstraint(speciesData);
        arguments[n3] = new LambdaForm.Name(speciesData.getterFunction(0), new Object[] { arguments[0] });
        arguments[n4] = new LambdaForm.Name(speciesData.getterFunction(1), new Object[] { arguments[0] });
        arguments[n5] = new LambdaForm.Name(speciesData.getterFunction(2), new Object[] { arguments[0] });
        if (n6 != -1) {
            arguments[n6] = new LambdaForm.Name(speciesData.getterFunction(3), new Object[] { arguments[0] });
        }
        final Object[] copyOfRange = Arrays.copyOfRange(arguments, 0, n, (Class<? extends Object[]>)Object[].class);
        final MethodType basicType = methodType.changeReturnType(Boolean.TYPE).basicType();
        copyOfRange[0] = arguments[n3];
        arguments[n7] = new LambdaForm.Name(basicType, copyOfRange);
        if (n8 != -1) {
            arguments[n8] = new LambdaForm.Name(Lazy.NF_profileBoolean, new Object[] { arguments[n7], arguments[n6] });
        }
        copyOfRange[0] = (arguments[n10] = new LambdaForm.Name(Lazy.MH_selectAlternative, new Object[] { arguments[n9], arguments[n4], arguments[n5] }));
        arguments[n11] = new LambdaForm.Name(methodType, copyOfRange);
        return methodType.form().setCachedLambdaForm(17, new LambdaForm("guard", invokerType.parameterCount(), arguments, true));
    }
    
    private static LambdaForm makeGuardWithCatchForm(final MethodType methodType) {
        final MethodType invokerType = methodType.invokerType();
        final LambdaForm cachedLambdaForm = methodType.form().cachedLambdaForm(16);
        if (cachedLambdaForm != null) {
            return cachedLambdaForm;
        }
        int n2;
        final int n = n2 = 1 + methodType.parameterCount();
        final int n3 = n2++;
        final int n4 = n2++;
        final int n5 = n2++;
        final int n6 = n2++;
        final int n7 = n2++;
        final int n8 = n2++;
        final int n9 = n2++;
        final int n10 = n2++;
        final LambdaForm.Name[] arguments = LambdaForm.arguments(n2 - n, invokerType);
        final BoundMethodHandle.SpeciesData speciesData_LLLLL = BoundMethodHandle.speciesData_LLLLL();
        arguments[0] = arguments[0].withConstraint(speciesData_LLLLL);
        arguments[n3] = new LambdaForm.Name(speciesData_LLLLL.getterFunction(0), new Object[] { arguments[0] });
        arguments[n4] = new LambdaForm.Name(speciesData_LLLLL.getterFunction(1), new Object[] { arguments[0] });
        arguments[n5] = new LambdaForm.Name(speciesData_LLLLL.getterFunction(2), new Object[] { arguments[0] });
        arguments[n6] = new LambdaForm.Name(speciesData_LLLLL.getterFunction(3), new Object[] { arguments[0] });
        arguments[n7] = new LambdaForm.Name(speciesData_LLLLL.getterFunction(4), new Object[] { arguments[0] });
        final MethodHandle basicInvoker = MethodHandles.basicInvoker(methodType.changeReturnType(Object.class));
        final Object[] array = new Object[basicInvoker.type().parameterCount()];
        array[0] = arguments[n6];
        System.arraycopy(arguments, 1, array, 1, n - 1);
        arguments[n8] = new LambdaForm.Name(makeIntrinsic(basicInvoker, Intrinsic.GUARD_WITH_CATCH), array);
        arguments[n9] = new LambdaForm.Name(Lazy.NF_guardWithCatch, new Object[] { arguments[n3], arguments[n4], arguments[n5], arguments[n8] });
        arguments[n10] = new LambdaForm.Name(MethodHandles.basicInvoker(MethodType.methodType(methodType.rtype(), Object.class)), new Object[] { arguments[n7], arguments[n9] });
        return methodType.form().setCachedLambdaForm(16, new LambdaForm("guardWithCatch", invokerType.parameterCount(), arguments));
    }
    
    static MethodHandle makeGuardWithCatch(final MethodHandle methodHandle, final Class<? extends Throwable> clazz, final MethodHandle methodHandle2) {
        final MethodType type = methodHandle.type();
        final LambdaForm guardWithCatchForm = makeGuardWithCatchForm(type.basicType());
        final MethodHandle type2 = varargsArray(type.parameterCount()).asType(type.changeReturnType(Object[].class));
        final Class<?> returnType = type.returnType();
        MethodHandle methodHandle3;
        if (returnType.isPrimitive()) {
            if (returnType == Void.TYPE) {
                methodHandle3 = ValueConversions.ignore();
            }
            else {
                methodHandle3 = ValueConversions.unboxExact(Wrapper.forPrimitiveType(type.returnType()));
            }
        }
        else {
            methodHandle3 = MethodHandles.identity(Object.class);
        }
        final BoundMethodHandle.SpeciesData speciesData_LLLLL = BoundMethodHandle.speciesData_LLLLL();
        BoundMethodHandle invokeBasic;
        try {
            invokeBasic = speciesData_LLLLL.constructor().invokeBasic(type, guardWithCatchForm, (Object)methodHandle, (Object)clazz, (Object)methodHandle2, (Object)type2, (Object)methodHandle3);
        }
        catch (final Throwable t) {
            throw MethodHandleStatics.uncaughtException(t);
        }
        assert invokeBasic.type() == type;
        return invokeBasic;
    }
    
    @LambdaForm.Hidden
    static Object guardWithCatch(final MethodHandle methodHandle, final Class<? extends Throwable> clazz, final MethodHandle methodHandle2, final Object... array) throws Throwable {
        try {
            return methodHandle.asFixedArity().invokeWithArguments(array);
        }
        catch (final Throwable t) {
            if (!clazz.isInstance(t)) {
                throw t;
            }
            return methodHandle2.asFixedArity().invokeWithArguments(prepend(t, array));
        }
    }
    
    @LambdaForm.Hidden
    private static Object[] prepend(final Object o, final Object[] array) {
        final Object[] array2 = new Object[array.length + 1];
        array2[0] = o;
        System.arraycopy(array, 0, array2, 1, array.length);
        return array2;
    }
    
    static MethodHandle throwException(final MethodType methodType) {
        assert Throwable.class.isAssignableFrom(methodType.parameterType(0));
        final int parameterCount = methodType.parameterCount();
        if (parameterCount > 1) {
            return MethodHandles.dropArguments(throwException(methodType.dropParameterTypes(1, parameterCount)), 1, methodType.parameterList().subList(1, parameterCount));
        }
        return makePairwiseConvert(Lazy.NF_throwException.resolvedHandle(), methodType, false, true);
    }
    
    static <T extends Throwable> Empty throwException(final T t) throws T, Throwable {
        throw t;
    }
    
    static MethodHandle fakeMethodHandleInvoke(final MemberName memberName) {
        assert memberName.isMethodHandleInvoke();
        final String name = memberName.getName();
        int n2 = 0;
        switch (name) {
            case "invoke": {
                n2 = 0;
                break;
            }
            case "invokeExact": {
                n2 = 1;
                break;
            }
            default: {
                throw new InternalError(memberName.getName());
            }
        }
        final MethodHandle methodHandle = MethodHandleImpl.FAKE_METHOD_HANDLE_INVOKE[n2];
        if (methodHandle != null) {
            return methodHandle;
        }
        final MethodHandle bindTo = throwException(MethodType.methodType(Object.class, UnsupportedOperationException.class, MethodHandle.class, Object[].class)).bindTo(new UnsupportedOperationException("cannot reflectively invoke MethodHandle"));
        if (!memberName.getInvocationType().equals((Object)bindTo.type())) {
            throw new InternalError(memberName.toString());
        }
        final MethodHandle varargsCollector = bindTo.withInternalMemberName(memberName, false).asVarargsCollector(Object[].class);
        assert memberName.isVarargs();
        return MethodHandleImpl.FAKE_METHOD_HANDLE_INVOKE[n2] = varargsCollector;
    }
    
    static MethodHandle bindCaller(final MethodHandle methodHandle, final Class<?> clazz) {
        return BindCaller.bindCaller(methodHandle, clazz);
    }
    
    static MethodHandle makeWrappedMember(final MethodHandle methodHandle, final MemberName memberName, final boolean b) {
        if (memberName.equals(methodHandle.internalMemberName()) && b == methodHandle.isInvokeSpecial()) {
            return methodHandle;
        }
        return new WrappedMember(methodHandle, methodHandle.type(), memberName, b, (Class)null);
    }
    
    static MethodHandle makeIntrinsic(final MethodHandle methodHandle, final Intrinsic intrinsic) {
        if (intrinsic == methodHandle.intrinsicName()) {
            return methodHandle;
        }
        return new IntrinsicMethodHandle(methodHandle, intrinsic);
    }
    
    static MethodHandle makeIntrinsic(final MethodType methodType, final LambdaForm lambdaForm, final Intrinsic intrinsic) {
        return new IntrinsicMethodHandle(SimpleMethodHandle.make(methodType, lambdaForm), intrinsic);
    }
    
    private static MethodHandle findCollector(final String s, final int n, final Class<?> clazz, final Class<?>... array) {
        final MethodType insertParameterTypes = MethodType.genericMethodType(n).changeReturnType(clazz).insertParameterTypes(0, array);
        try {
            return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MethodHandleImpl.class, s, insertParameterTypes);
        }
        catch (final ReflectiveOperationException ex) {
            return null;
        }
    }
    
    private static Object[] makeArray(final Object... array) {
        return array;
    }
    
    private static Object[] array() {
        return MethodHandleImpl.NO_ARGS_ARRAY;
    }
    
    private static Object[] array(final Object o) {
        return makeArray(o);
    }
    
    private static Object[] array(final Object o, final Object o2) {
        return makeArray(o, o2);
    }
    
    private static Object[] array(final Object o, final Object o2, final Object o3) {
        return makeArray(o, o2, o3);
    }
    
    private static Object[] array(final Object o, final Object o2, final Object o3, final Object o4) {
        return makeArray(o, o2, o3, o4);
    }
    
    private static Object[] array(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return makeArray(o, o2, o3, o4, o5);
    }
    
    private static Object[] array(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return makeArray(o, o2, o3, o4, o5, o6);
    }
    
    private static Object[] array(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        return makeArray(o, o2, o3, o4, o5, o6, o7);
    }
    
    private static Object[] array(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        return makeArray(o, o2, o3, o4, o5, o6, o7, o8);
    }
    
    private static Object[] array(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        return makeArray(o, o2, o3, o4, o5, o6, o7, o8, o9);
    }
    
    private static Object[] array(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10) {
        return makeArray(o, o2, o3, o4, o5, o6, o7, o8, o9, o10);
    }
    
    private static MethodHandle[] makeArrays() {
        final ArrayList list = new ArrayList();
        while (true) {
            final MethodHandle collector = findCollector("array", list.size(), Object[].class, (Class<?>[])new Class[0]);
            if (collector == null) {
                break;
            }
            list.add(makeIntrinsic(collector, Intrinsic.NEW_ARRAY));
        }
        assert list.size() == 11;
        return list.toArray(new MethodHandle[MethodHandleImpl.MAX_ARITY + 1]);
    }
    
    private static Object[] fillNewArray(final Integer n, final Object[] array) {
        final Object[] array2 = new Object[(int)n];
        fillWithArguments(array2, 0, array);
        return array2;
    }
    
    private static Object[] fillNewTypedArray(final Object[] array, final Integer n, final Object[] array2) {
        final Object[] copy = Arrays.copyOf(array, n);
        assert copy.getClass() != Object[].class;
        fillWithArguments(copy, 0, array2);
        return copy;
    }
    
    private static void fillWithArguments(final Object[] array, final int n, final Object... array2) {
        System.arraycopy(array2, 0, array, n, array2.length);
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o) {
        fillWithArguments(array, n, o);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2) {
        fillWithArguments(array, n, o, o2);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2, final Object o3) {
        fillWithArguments(array, n, o, o2, o3);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2, final Object o3, final Object o4) {
        fillWithArguments(array, n, o, o2, o3, o4);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        fillWithArguments(array, n, o, o2, o3, o4, o5);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        fillWithArguments(array, n, o, o2, o3, o4, o5, o6);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        fillWithArguments(array, n, o, o2, o3, o4, o5, o6, o7);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        fillWithArguments(array, n, o, o2, o3, o4, o5, o6, o7, o8);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        fillWithArguments(array, n, o, o2, o3, o4, o5, o6, o7, o8, o9);
        return array;
    }
    
    private static Object[] fillArray(final Integer n, final Object[] array, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10) {
        fillWithArguments(array, n, o, o2, o3, o4, o5, o6, o7, o8, o9, o10);
        return array;
    }
    
    private static MethodHandle[] makeFillArrays() {
        final ArrayList list = new ArrayList();
        list.add(null);
        while (true) {
            final MethodHandle collector = findCollector("fillArray", list.size(), Object[].class, Integer.class, Object[].class);
            if (collector == null) {
                break;
            }
            list.add(collector);
        }
        assert list.size() == 11;
        return list.toArray(new MethodHandle[0]);
    }
    
    private static Object copyAsPrimitiveArray(final Wrapper wrapper, final Object... array) {
        final Object array2 = wrapper.makeArray(array.length);
        wrapper.copyArrayUnboxing(array, 0, array2, 0, array.length);
        return array2;
    }
    
    static MethodHandle varargsArray(final int n) {
        final MethodHandle methodHandle = Lazy.ARRAYS[n];
        if (methodHandle != null) {
            return methodHandle;
        }
        MethodHandle methodHandle2 = findCollector("array", n, Object[].class, (Class<?>[])new Class[0]);
        if (methodHandle2 != null) {
            methodHandle2 = makeIntrinsic(methodHandle2, Intrinsic.NEW_ARRAY);
        }
        if (methodHandle2 != null) {
            return Lazy.ARRAYS[n] = methodHandle2;
        }
        final MethodHandle buildVarargsArray = buildVarargsArray(Lazy.MH_fillNewArray, Lazy.MH_arrayIdentity, n);
        assert assertCorrectArity(buildVarargsArray, n);
        return Lazy.ARRAYS[n] = makeIntrinsic(buildVarargsArray, Intrinsic.NEW_ARRAY);
    }
    
    private static boolean assertCorrectArity(final MethodHandle methodHandle, final int n) {
        assert methodHandle.type().parameterCount() == n : "arity != " + n + ": " + methodHandle;
        return true;
    }
    
    static <T> T[] identity(final T[] array) {
        return array;
    }
    
    private static MethodHandle buildVarargsArray(final MethodHandle methodHandle, final MethodHandle methodHandle2, final int n) {
        final int min = Math.min(n, 10);
        final int n2 = n - min;
        final MethodHandle collector = methodHandle.bindTo(n).asCollector(Object[].class, min);
        MethodHandle collectArguments = methodHandle2;
        if (n2 > 0) {
            final MethodHandle fillToRight = fillToRight(10 + n2);
            if (collectArguments == Lazy.MH_arrayIdentity) {
                collectArguments = fillToRight;
            }
            else {
                collectArguments = MethodHandles.collectArguments(collectArguments, 0, fillToRight);
            }
        }
        MethodHandle collectArguments2;
        if (collectArguments == Lazy.MH_arrayIdentity) {
            collectArguments2 = collector;
        }
        else {
            collectArguments2 = MethodHandles.collectArguments(collectArguments, 0, collector);
        }
        return collectArguments2;
    }
    
    private static MethodHandle fillToRight(final int n) {
        final MethodHandle methodHandle = MethodHandleImpl.FILL_ARRAY_TO_RIGHT[n];
        if (methodHandle != null) {
            return methodHandle;
        }
        final MethodHandle buildFiller = buildFiller(n);
        assert assertCorrectArity(buildFiller, n - 10 + 1);
        return MethodHandleImpl.FILL_ARRAY_TO_RIGHT[n] = buildFiller;
    }
    
    private static MethodHandle buildFiller(final int n) {
        if (n <= 10) {
            return Lazy.MH_arrayIdentity;
        }
        int n2 = n % 10;
        int n3 = n - n2;
        if (n2 == 0) {
            n3 = n - (n2 = 10);
            if (MethodHandleImpl.FILL_ARRAY_TO_RIGHT[n3] == null) {
                for (int i = 0; i < n3; i += 10) {
                    if (i > 10) {
                        fillToRight(i);
                    }
                }
            }
        }
        if (n3 < 10) {
            n2 = n - (n3 = 10);
        }
        assert n2 > 0;
        final MethodHandle fillToRight = fillToRight(n3);
        final MethodHandle bindTo = Lazy.FILL_ARRAYS[n2].bindTo(n3);
        assert fillToRight.type().parameterCount() == 1 + n3 - 10;
        assert bindTo.type().parameterCount() == 1 + n2;
        if (n3 == 10) {
            return bindTo;
        }
        return MethodHandles.collectArguments(bindTo, 0, fillToRight);
    }
    
    static MethodHandle varargsArray(final Class<?> clazz, final int n) {
        final Class componentType = clazz.getComponentType();
        if (componentType == null) {
            throw new IllegalArgumentException("not an array: " + clazz);
        }
        if (n >= 126) {
            int n2 = n;
            if (n2 <= 254 && componentType.isPrimitive()) {
                n2 *= Wrapper.forPrimitiveType(componentType).stackSlots();
            }
            if (n2 > 254) {
                throw new IllegalArgumentException("too many arguments: " + clazz.getSimpleName() + ", length " + n);
            }
        }
        if (componentType == Object.class) {
            return varargsArray(n);
        }
        final MethodHandle[] array = MethodHandleImpl.TYPED_COLLECTORS.get(componentType);
        final MethodHandle methodHandle = (n < array.length) ? array[n] : null;
        if (methodHandle != null) {
            return methodHandle;
        }
        MethodHandle methodHandle2;
        if (n == 0) {
            methodHandle2 = MethodHandles.constant(clazz, Array.newInstance(clazz.getComponentType(), 0));
        }
        else if (componentType.isPrimitive()) {
            methodHandle2 = buildVarargsArray(Lazy.MH_fillNewArray, buildArrayProducer(clazz), n);
        }
        else {
            methodHandle2 = buildVarargsArray(Lazy.MH_fillNewTypedArray.bindTo(Arrays.copyOf(MethodHandleImpl.NO_ARGS_ARRAY, 0, clazz.asSubclass(Object[].class))), Lazy.MH_arrayIdentity, n);
        }
        final MethodHandle intrinsic = makeIntrinsic(methodHandle2.asType(MethodType.methodType(clazz, (List<Class<?>>)Collections.nCopies(n, componentType))), Intrinsic.NEW_ARRAY);
        assert assertCorrectArity(intrinsic, n);
        if (n < array.length) {
            array[n] = intrinsic;
        }
        return intrinsic;
    }
    
    private static MethodHandle buildArrayProducer(final Class<?> clazz) {
        final Class<?> componentType = clazz.getComponentType();
        assert componentType.isPrimitive();
        return Lazy.MH_copyAsPrimitiveArray.bindTo(Wrapper.forPrimitiveType(componentType));
    }
    
    static void assertSame(final Object o, final Object o2) {
        if (o != o2) {
            throw MethodHandleStatics.newInternalError(String.format("mh1 != mh2: mh1 = %s (form: %s); mh2 = %s (form: %s)", o, ((MethodHandle)o).form, o2, ((MethodHandle)o2).form));
        }
    }
    
    static {
        final Object[] array = { 255 };
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                array[0] = Integer.getInteger(MethodHandleImpl.class.getName() + ".MAX_ARITY", 255);
                return null;
            }
        });
        MAX_ARITY = (int)array[0];
        PRODUCE_BLOCK_INLINING_FORM = new Function<MethodHandle, LambdaForm>() {
            @Override
            public LambdaForm apply(final MethodHandle methodHandle) {
                return DelegatingMethodHandle.makeReinvokerForm(methodHandle, 9, CountingWrapper.class, "reinvoker.dontInline", false, DelegatingMethodHandle.NF_getTarget, CountingWrapper.NF_maybeStopCounting);
            }
        };
        PRODUCE_REINVOKER_FORM = new Function<MethodHandle, LambdaForm>() {
            @Override
            public LambdaForm apply(final MethodHandle methodHandle) {
                return DelegatingMethodHandle.makeReinvokerForm(methodHandle, 8, DelegatingMethodHandle.class, DelegatingMethodHandle.NF_getTarget);
            }
        };
        MethodHandleImpl.FAKE_METHOD_HANDLE_INVOKE = new MethodHandle[2];
        NO_ARGS_ARRAY = new Object[0];
        FILL_ARRAY_TO_RIGHT = new MethodHandle[MethodHandleImpl.MAX_ARITY + 1];
        TYPED_COLLECTORS = new ClassValue<MethodHandle[]>() {
            @Override
            protected MethodHandle[] computeValue(final Class<?> clazz) {
                return new MethodHandle[256];
            }
        };
    }
    
    static final class ArrayAccessor
    {
        static final int GETTER_INDEX = 0;
        static final int SETTER_INDEX = 1;
        static final int INDEX_LIMIT = 2;
        static final ClassValue<MethodHandle[]> TYPED_ACCESSORS;
        static final MethodHandle OBJECT_ARRAY_GETTER;
        static final MethodHandle OBJECT_ARRAY_SETTER;
        
        static int getElementI(final int[] array, final int n) {
            return array[n];
        }
        
        static long getElementJ(final long[] array, final int n) {
            return array[n];
        }
        
        static float getElementF(final float[] array, final int n) {
            return array[n];
        }
        
        static double getElementD(final double[] array, final int n) {
            return array[n];
        }
        
        static boolean getElementZ(final boolean[] array, final int n) {
            return array[n];
        }
        
        static byte getElementB(final byte[] array, final int n) {
            return array[n];
        }
        
        static short getElementS(final short[] array, final int n) {
            return array[n];
        }
        
        static char getElementC(final char[] array, final int n) {
            return array[n];
        }
        
        static Object getElementL(final Object[] array, final int n) {
            return array[n];
        }
        
        static void setElementI(final int[] array, final int n, final int n2) {
            array[n] = n2;
        }
        
        static void setElementJ(final long[] array, final int n, final long n2) {
            array[n] = n2;
        }
        
        static void setElementF(final float[] array, final int n, final float n2) {
            array[n] = n2;
        }
        
        static void setElementD(final double[] array, final int n, final double n2) {
            array[n] = n2;
        }
        
        static void setElementZ(final boolean[] array, final int n, final boolean b) {
            array[n] = b;
        }
        
        static void setElementB(final byte[] array, final int n, final byte b) {
            array[n] = b;
        }
        
        static void setElementS(final short[] array, final int n, final short n2) {
            array[n] = n2;
        }
        
        static void setElementC(final char[] array, final int n, final char c) {
            array[n] = c;
        }
        
        static void setElementL(final Object[] array, final int n, final Object o) {
            array[n] = o;
        }
        
        static String name(final Class<?> clazz, final boolean b) {
            final Class<?> componentType = clazz.getComponentType();
            if (componentType == null) {
                throw MethodHandleStatics.newIllegalArgumentException("not an array", clazz);
            }
            return (b ? "setElement" : "getElement") + Wrapper.basicTypeChar(componentType);
        }
        
        static MethodType type(final Class<?> clazz, final boolean b) {
            Class<?> componentType = clazz.getComponentType();
            Class<Object[]> clazz2 = (Class<Object[]>)clazz;
            if (!componentType.isPrimitive()) {
                clazz2 = Object[].class;
                componentType = Object.class;
            }
            return b ? MethodType.methodType(Void.TYPE, clazz2, Integer.TYPE, componentType) : MethodType.methodType(componentType, clazz2, Integer.TYPE);
        }
        
        static MethodType correctType(final Class<?> clazz, final boolean b) {
            final Class<?> componentType = clazz.getComponentType();
            return b ? MethodType.methodType(Void.TYPE, clazz, Integer.TYPE, componentType) : MethodType.methodType(componentType, clazz, Integer.TYPE);
        }
        
        static MethodHandle getAccessor(final Class<?> clazz, final boolean b) {
            final String name = name(clazz, b);
            final MethodType type = type(clazz, b);
            try {
                return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(ArrayAccessor.class, name, type);
            }
            catch (final ReflectiveOperationException ex) {
                throw MethodHandleStatics.uncaughtException(ex);
            }
        }
        
        static {
            TYPED_ACCESSORS = new ClassValue<MethodHandle[]>() {
                @Override
                protected MethodHandle[] computeValue(final Class<?> clazz) {
                    return new MethodHandle[2];
                }
            };
            final MethodHandle[] array = ArrayAccessor.TYPED_ACCESSORS.get(Object[].class);
            array[0] = (OBJECT_ARRAY_GETTER = MethodHandleImpl.makeIntrinsic(getAccessor(Object[].class, false), Intrinsic.ARRAY_LOAD));
            array[1] = (OBJECT_ARRAY_SETTER = MethodHandleImpl.makeIntrinsic(getAccessor(Object[].class, true), Intrinsic.ARRAY_STORE));
            assert InvokerBytecodeGenerator.isStaticallyInvocable(ArrayAccessor.OBJECT_ARRAY_GETTER.internalMemberName());
            assert InvokerBytecodeGenerator.isStaticallyInvocable(ArrayAccessor.OBJECT_ARRAY_SETTER.internalMemberName());
        }
    }
    
    private static final class AsVarargsCollector extends DelegatingMethodHandle
    {
        private final MethodHandle target;
        private final Class<?> arrayType;
        @Stable
        private MethodHandle asCollectorCache;
        
        AsVarargsCollector(final MethodHandle methodHandle, final Class<?> clazz) {
            this(methodHandle.type(), methodHandle, clazz);
        }
        
        AsVarargsCollector(final MethodType methodType, final MethodHandle target, final Class<?> arrayType) {
            super(methodType, target);
            this.target = target;
            this.arrayType = arrayType;
            this.asCollectorCache = target.asCollector(arrayType, 0);
        }
        
        @Override
        public boolean isVarargsCollector() {
            return true;
        }
        
        @Override
        protected MethodHandle getTarget() {
            return this.target;
        }
        
        @Override
        public MethodHandle asFixedArity() {
            return this.target;
        }
        
        @Override
        MethodHandle setVarargs(final MemberName memberName) {
            if (memberName.isVarargs()) {
                return this;
            }
            return this.asFixedArity();
        }
        
        public MethodHandle asTypeUncached(final MethodType methodType) {
            final MethodType type = this.type();
            final int n = type.parameterCount() - 1;
            final int parameterCount = methodType.parameterCount();
            if (parameterCount == n + 1 && type.parameterType(n).isAssignableFrom(methodType.parameterType(n))) {
                return this.asTypeCache = this.asFixedArity().asType(methodType);
            }
            final MethodHandle asCollectorCache = this.asCollectorCache;
            if (asCollectorCache != null && asCollectorCache.type().parameterCount() == parameterCount) {
                return this.asTypeCache = asCollectorCache.asType(methodType);
            }
            final int n2 = parameterCount - n;
            MethodHandle collector;
            try {
                collector = this.asFixedArity().asCollector(this.arrayType, n2);
                assert collector.type().parameterCount() == parameterCount : "newArity=" + parameterCount + " but collector=" + collector;
            }
            catch (final IllegalArgumentException ex) {
                throw new WrongMethodTypeException("cannot build collector", ex);
            }
            this.asCollectorCache = collector;
            return this.asTypeCache = collector.asType(methodType);
        }
        
        @Override
        boolean viewAsTypeChecks(final MethodType methodType, final boolean b) {
            super.viewAsTypeChecks(methodType, true);
            if (b) {
                return true;
            }
            assert this.type().lastParameterType().getComponentType().isAssignableFrom(methodType.lastParameterType().getComponentType()) : Arrays.asList(this, methodType);
            return true;
        }
    }
    
    static class Lazy
    {
        private static final Class<?> MHI;
        private static final MethodHandle[] ARRAYS;
        private static final MethodHandle[] FILL_ARRAYS;
        static final LambdaForm.NamedFunction NF_checkSpreadArgument;
        static final LambdaForm.NamedFunction NF_guardWithCatch;
        static final LambdaForm.NamedFunction NF_throwException;
        static final LambdaForm.NamedFunction NF_profileBoolean;
        static final MethodHandle MH_castReference;
        static final MethodHandle MH_selectAlternative;
        static final MethodHandle MH_copyAsPrimitiveArray;
        static final MethodHandle MH_fillNewTypedArray;
        static final MethodHandle MH_fillNewArray;
        static final MethodHandle MH_arrayIdentity;
        
        static {
            MHI = MethodHandleImpl.class;
            ARRAYS = makeArrays();
            FILL_ARRAYS = makeFillArrays();
            try {
                NF_checkSpreadArgument = new LambdaForm.NamedFunction(Lazy.MHI.getDeclaredMethod("checkSpreadArgument", Object.class, Integer.TYPE));
                NF_guardWithCatch = new LambdaForm.NamedFunction(Lazy.MHI.getDeclaredMethod("guardWithCatch", MethodHandle.class, Class.class, MethodHandle.class, Object[].class));
                NF_throwException = new LambdaForm.NamedFunction(Lazy.MHI.getDeclaredMethod("throwException", Throwable.class));
                NF_profileBoolean = new LambdaForm.NamedFunction(Lazy.MHI.getDeclaredMethod("profileBoolean", Boolean.TYPE, int[].class));
                Lazy.NF_checkSpreadArgument.resolve();
                Lazy.NF_guardWithCatch.resolve();
                Lazy.NF_throwException.resolve();
                Lazy.NF_profileBoolean.resolve();
                MH_castReference = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(Lazy.MHI, "castReference", MethodType.methodType(Object.class, Class.class, Object.class));
                MH_copyAsPrimitiveArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(Lazy.MHI, "copyAsPrimitiveArray", MethodType.methodType(Object.class, Wrapper.class, Object[].class));
                MH_arrayIdentity = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(Lazy.MHI, "identity", MethodType.methodType(Object[].class, Object[].class));
                MH_fillNewArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(Lazy.MHI, "fillNewArray", MethodType.methodType(Object[].class, Integer.class, Object[].class));
                MH_fillNewTypedArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(Lazy.MHI, "fillNewTypedArray", MethodType.methodType(Object[].class, Object[].class, Integer.class, Object[].class));
                MH_selectAlternative = MethodHandleImpl.makeIntrinsic(MethodHandles.Lookup.IMPL_LOOKUP.findStatic(Lazy.MHI, "selectAlternative", MethodType.methodType(MethodHandle.class, Boolean.TYPE, MethodHandle.class, MethodHandle.class)), Intrinsic.SELECT_ALTERNATIVE);
            }
            catch (final ReflectiveOperationException ex) {
                throw MethodHandleStatics.newInternalError(ex);
            }
        }
    }
    
    static class CountingWrapper extends DelegatingMethodHandle
    {
        private final MethodHandle target;
        private int count;
        private Function<MethodHandle, LambdaForm> countingFormProducer;
        private Function<MethodHandle, LambdaForm> nonCountingFormProducer;
        private volatile boolean isCounting;
        static final LambdaForm.NamedFunction NF_maybeStopCounting;
        
        private CountingWrapper(final MethodHandle target, final LambdaForm lambdaForm, final Function<MethodHandle, LambdaForm> countingFormProducer, final Function<MethodHandle, LambdaForm> nonCountingFormProducer, final int count) {
            super(target.type(), lambdaForm);
            this.target = target;
            this.count = count;
            this.countingFormProducer = countingFormProducer;
            this.nonCountingFormProducer = nonCountingFormProducer;
            this.isCounting = (count > 0);
        }
        
        @LambdaForm.Hidden
        @Override
        protected MethodHandle getTarget() {
            return this.target;
        }
        
        public MethodHandle asTypeUncached(final MethodType methodType) {
            final MethodHandle type = this.target.asType(methodType);
            MethodHandle asTypeCache;
            if (this.isCounting) {
                asTypeCache = new CountingWrapper(type, this.countingFormProducer.apply(type), this.countingFormProducer, this.nonCountingFormProducer, MethodHandleStatics.DONT_INLINE_THRESHOLD);
            }
            else {
                asTypeCache = type;
            }
            return this.asTypeCache = asTypeCache;
        }
        
        boolean countDown() {
            if (this.count > 0) {
                --this.count;
                return false;
            }
            if (this.isCounting) {
                this.isCounting = false;
                return true;
            }
            return false;
        }
        
        @LambdaForm.Hidden
        static void maybeStopCounting(final Object o) {
            final CountingWrapper countingWrapper = (CountingWrapper)o;
            if (countingWrapper.countDown()) {
                final LambdaForm lambdaForm = countingWrapper.nonCountingFormProducer.apply(countingWrapper.target);
                lambdaForm.compileToBytecode();
                countingWrapper.updateForm(lambdaForm);
            }
        }
        
        static {
            final Class<CountingWrapper> clazz = CountingWrapper.class;
            try {
                NF_maybeStopCounting = new LambdaForm.NamedFunction(clazz.getDeclaredMethod("maybeStopCounting", Object.class));
            }
            catch (final ReflectiveOperationException ex) {
                throw MethodHandleStatics.newInternalError(ex);
            }
        }
    }
    
    private static class BindCaller
    {
        private static ClassValue<MethodHandle> CV_makeInjectedInvoker;
        private static final MethodHandle MH_checkCallerClass;
        private static final byte[] T_BYTES;
        
        static MethodHandle bindCaller(final MethodHandle methodHandle, final Class<?> clazz) {
            if (clazz == null || clazz.isArray() || clazz.isPrimitive() || clazz.getName().startsWith("java.") || clazz.getName().startsWith("sun.")) {
                throw new InternalError();
            }
            return restoreToType(BindCaller.CV_makeInjectedInvoker.get(clazz).bindTo(prepareForInvoker(methodHandle)), methodHandle, clazz);
        }
        
        private static MethodHandle makeInjectedInvoker(final Class<?> clazz) {
            final Class<?> defineAnonymousClass = MethodHandleStatics.UNSAFE.defineAnonymousClass(clazz, BindCaller.T_BYTES, null);
            if (clazz.getClassLoader() != defineAnonymousClass.getClassLoader()) {
                throw new InternalError(clazz.getName() + " (CL)");
            }
            try {
                if (clazz.getProtectionDomain() != defineAnonymousClass.getProtectionDomain()) {
                    throw new InternalError(clazz.getName() + " (PD)");
                }
            }
            catch (final SecurityException ex) {}
            try {
                MethodHandles.Lookup.IMPL_LOOKUP.findStatic(defineAnonymousClass, "init", MethodType.methodType(Void.TYPE)).invokeExact();
            }
            catch (final Throwable t) {
                throw MethodHandleStatics.uncaughtException(t);
            }
            MethodHandle static1;
            try {
                static1 = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(defineAnonymousClass, "invoke_V", MethodType.methodType(Object.class, MethodHandle.class, Object[].class));
            }
            catch (final ReflectiveOperationException ex2) {
                throw MethodHandleStatics.uncaughtException(ex2);
            }
            try {
                static1.invokeExact(prepareForInvoker(BindCaller.MH_checkCallerClass), new Object[] { clazz, defineAnonymousClass });
            }
            catch (final Throwable t2) {
                throw new InternalError(t2);
            }
            return static1;
        }
        
        private static MethodHandle prepareForInvoker(MethodHandle fixedArity) {
            fixedArity = fixedArity.asFixedArity();
            final MethodType type = fixedArity.type();
            final int parameterCount = type.parameterCount();
            final MethodHandle type2 = fixedArity.asType(type.generic());
            type2.internalForm().compileToBytecode();
            final MethodHandle spreader = type2.asSpreader(Object[].class, parameterCount);
            spreader.internalForm().compileToBytecode();
            return spreader;
        }
        
        private static MethodHandle restoreToType(final MethodHandle methodHandle, final MethodHandle methodHandle2, final Class<?> clazz) {
            final MethodType type = methodHandle2.type();
            return new WrappedMember(methodHandle.asCollector(Object[].class, type.parameterCount()).asType(type), type, methodHandle2.internalMemberName(), methodHandle2.isInvokeSpecial(), (Class)clazz);
        }
        
        @CallerSensitive
        private static boolean checkCallerClass(final Class<?> clazz, final Class<?> clazz2) {
            final Class<?> callerClass = Reflection.getCallerClass();
            if (callerClass != clazz && callerClass != clazz2) {
                throw new InternalError("found " + callerClass.getName() + ", expected " + clazz.getName() + ((clazz == clazz2) ? "" : (", or else " + clazz2.getName())));
            }
            return true;
        }
        
        static {
            BindCaller.CV_makeInjectedInvoker = new ClassValue<MethodHandle>() {
                @Override
                protected MethodHandle computeValue(final Class<?> clazz) {
                    return makeInjectedInvoker(clazz);
                }
            };
            final Class<BindCaller> clazz = BindCaller.class;
            assert checkCallerClass(clazz, clazz);
            try {
                MH_checkCallerClass = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(clazz, "checkCallerClass", MethodType.methodType(Boolean.TYPE, Class.class, Class.class));
                assert BindCaller.MH_checkCallerClass.invokeExact((Class)clazz, (Class)clazz);
            }
            catch (final Throwable t) {
                throw new InternalError(t);
            }
            final Object[] array = { null };
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    try {
                        final Class<T> clazz = T.class;
                        final String name = clazz.getName();
                        final String string = name.substring(name.lastIndexOf(46) + 1) + ".class";
                        final URLConnection openConnection = clazz.getResource(string).openConnection();
                        final int contentLength = openConnection.getContentLength();
                        final byte[] array = new byte[contentLength];
                        try (final InputStream inputStream = openConnection.getInputStream()) {
                            if (inputStream.read(array) != contentLength) {
                                throw new IOException(string);
                            }
                        }
                        array[0] = array;
                    }
                    catch (final IOException ex) {
                        throw new InternalError(ex);
                    }
                    return null;
                }
            });
            T_BYTES = (byte[])array[0];
        }
        
        private static class T
        {
            static void init() {
            }
            
            static Object invoke_V(final MethodHandle methodHandle, final Object[] array) throws Throwable {
                return methodHandle.invokeExact(array);
            }
        }
    }
    
    private static final class WrappedMember extends DelegatingMethodHandle
    {
        private final MethodHandle target;
        private final MemberName member;
        private final Class<?> callerClass;
        private final boolean isInvokeSpecial;
        
        private WrappedMember(final MethodHandle target, final MethodType methodType, final MemberName member, final boolean isInvokeSpecial, final Class<?> callerClass) {
            super(methodType, target);
            this.target = target;
            this.member = member;
            this.callerClass = callerClass;
            this.isInvokeSpecial = isInvokeSpecial;
        }
        
        @Override
        MemberName internalMemberName() {
            return this.member;
        }
        
        @Override
        Class<?> internalCallerClass() {
            return this.callerClass;
        }
        
        @Override
        boolean isInvokeSpecial() {
            return this.isInvokeSpecial;
        }
        
        @Override
        protected MethodHandle getTarget() {
            return this.target;
        }
        
        public MethodHandle asTypeUncached(final MethodType methodType) {
            return this.asTypeCache = this.target.asType(methodType);
        }
    }
    
    enum Intrinsic
    {
        SELECT_ALTERNATIVE, 
        GUARD_WITH_CATCH, 
        NEW_ARRAY, 
        ARRAY_LOAD, 
        ARRAY_STORE, 
        IDENTITY, 
        ZERO, 
        NONE;
    }
    
    private static final class IntrinsicMethodHandle extends DelegatingMethodHandle
    {
        private final MethodHandle target;
        private final Intrinsic intrinsicName;
        
        IntrinsicMethodHandle(final MethodHandle target, final Intrinsic intrinsicName) {
            super(target.type(), target);
            this.target = target;
            this.intrinsicName = intrinsicName;
        }
        
        @Override
        protected MethodHandle getTarget() {
            return this.target;
        }
        
        @Override
        Intrinsic intrinsicName() {
            return this.intrinsicName;
        }
        
        public MethodHandle asTypeUncached(final MethodType methodType) {
            return this.asTypeCache = this.target.asType(methodType);
        }
        
        @Override
        String internalProperties() {
            return super.internalProperties() + "\n& Intrinsic=" + this.intrinsicName;
        }
        
        @Override
        public MethodHandle asCollector(final Class<?> clazz, final int n) {
            if (this.intrinsicName == Intrinsic.IDENTITY) {
                return MethodHandleImpl.varargsArray(clazz, n).asType(this.type().asCollectorType(clazz, n));
            }
            return super.asCollector(clazz, n);
        }
    }
}
