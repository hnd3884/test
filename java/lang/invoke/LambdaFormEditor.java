package java.lang.invoke;

import java.lang.ref.SoftReference;
import java.util.Collections;
import sun.invoke.util.Wrapper;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

class LambdaFormEditor
{
    final LambdaForm lambdaForm;
    private static final int MIN_CACHE_ARRAY_SIZE = 4;
    private static final int MAX_CACHE_ARRAY_SIZE = 16;
    
    private LambdaFormEditor(final LambdaForm lambdaForm) {
        this.lambdaForm = lambdaForm;
    }
    
    static LambdaFormEditor lambdaFormEditor(final LambdaForm lambdaForm) {
        return new LambdaFormEditor(lambdaForm.uncustomize());
    }
    
    private LambdaForm getInCache(final Transform transform) {
        assert transform.get() == null;
        final Object transformCache = this.lambdaForm.transformCache;
        Transform transform2 = null;
        if (transformCache instanceof ConcurrentHashMap) {
            transform2 = (Transform)((ConcurrentHashMap)transformCache).get(transform);
        }
        else {
            if (transformCache == null) {
                return null;
            }
            if (transformCache instanceof Transform) {
                final Transform transform3 = (Transform)transformCache;
                if (transform3.equals(transform)) {
                    transform2 = transform3;
                }
            }
            else {
                final Transform[] array = (Transform[])transformCache;
                for (int i = 0; i < array.length; ++i) {
                    final Transform transform4 = array[i];
                    if (transform4 == null) {
                        break;
                    }
                    if (transform4.equals(transform)) {
                        transform2 = transform4;
                        break;
                    }
                }
            }
        }
        assert !(!transform.equals(transform2));
        return (transform2 != null) ? transform2.get() : null;
    }
    
    private LambdaForm putInCache(Transform withResult, final LambdaForm lambdaForm) {
        withResult = withResult.withResult(lambdaForm);
        int n = 0;
        while (true) {
            final Object transformCache = this.lambdaForm.transformCache;
            Label_0518: {
                if (transformCache instanceof ConcurrentHashMap) {
                    final ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap)transformCache;
                    final Transform transform = concurrentHashMap.putIfAbsent(withResult, withResult);
                    if (transform == null) {
                        return lambdaForm;
                    }
                    final LambdaForm lambdaForm2 = transform.get();
                    if (lambdaForm2 != null) {
                        return lambdaForm2;
                    }
                    if (concurrentHashMap.replace(withResult, transform, withResult)) {
                        return lambdaForm;
                    }
                }
                else {
                    assert n == 0;
                    synchronized (this.lambdaForm) {
                        final Object transformCache2 = this.lambdaForm.transformCache;
                        if (!(transformCache2 instanceof ConcurrentHashMap)) {
                            if (transformCache2 == null) {
                                this.lambdaForm.transformCache = withResult;
                                return lambdaForm;
                            }
                            Transform[] array;
                            if (transformCache2 instanceof Transform) {
                                final Transform transform2 = (Transform)transformCache2;
                                if (transform2.equals(withResult)) {
                                    final LambdaForm lambdaForm3 = transform2.get();
                                    if (lambdaForm3 == null) {
                                        this.lambdaForm.transformCache = withResult;
                                        return lambdaForm;
                                    }
                                    return lambdaForm3;
                                }
                                else {
                                    if (transform2.get() == null) {
                                        this.lambdaForm.transformCache = withResult;
                                        return lambdaForm;
                                    }
                                    array = new Transform[4];
                                    array[0] = transform2;
                                    this.lambdaForm.transformCache = array;
                                }
                            }
                            else {
                                array = (Transform[])transformCache2;
                            }
                            final int length = array.length;
                            int n2 = -1;
                            int i = 0;
                            while (i < length) {
                                final Transform transform3 = array[i];
                                if (transform3 == null) {
                                    break;
                                }
                                if (transform3.equals(withResult)) {
                                    final LambdaForm lambdaForm4 = transform3.get();
                                    if (lambdaForm4 == null) {
                                        array[i] = withResult;
                                        return lambdaForm;
                                    }
                                    return lambdaForm4;
                                }
                                else {
                                    if (n2 < 0 && transform3.get() == null) {
                                        n2 = i;
                                    }
                                    ++i;
                                }
                            }
                            if (i >= length) {
                                if (n2 < 0) {
                                    if (length >= 16) {
                                        final ConcurrentHashMap<Transform, Transform> transformCache3 = new ConcurrentHashMap<Transform, Transform>(32);
                                        for (final Transform transform4 : array) {
                                            transformCache3.put(transform4, transform4);
                                        }
                                        this.lambdaForm.transformCache = transformCache3;
                                        break Label_0518;
                                    }
                                    array = Arrays.copyOf(array, Math.min(length * 2, 16));
                                    this.lambdaForm.transformCache = array;
                                }
                            }
                            array[(n2 >= 0) ? n2 : i] = withResult;
                            return lambdaForm;
                        }
                    }
                }
            }
            ++n;
        }
    }
    
    private LambdaFormBuffer buffer() {
        return new LambdaFormBuffer(this.lambdaForm);
    }
    
    private BoundMethodHandle.SpeciesData oldSpeciesData() {
        return BoundMethodHandle.speciesData(this.lambdaForm);
    }
    
    private BoundMethodHandle.SpeciesData newSpeciesData(final LambdaForm.BasicType basicType) {
        return this.oldSpeciesData().extendWith(basicType);
    }
    
    BoundMethodHandle bindArgumentL(final BoundMethodHandle boundMethodHandle, final int n, final Object o) {
        assert boundMethodHandle.speciesData() == this.oldSpeciesData();
        return boundMethodHandle.copyWithExtendL(this.bindArgumentType(boundMethodHandle, n, LambdaForm.BasicType.L_TYPE), this.bindArgumentForm(1 + n), o);
    }
    
    BoundMethodHandle bindArgumentI(final BoundMethodHandle boundMethodHandle, final int n, final int n2) {
        assert boundMethodHandle.speciesData() == this.oldSpeciesData();
        return boundMethodHandle.copyWithExtendI(this.bindArgumentType(boundMethodHandle, n, LambdaForm.BasicType.I_TYPE), this.bindArgumentForm(1 + n), n2);
    }
    
    BoundMethodHandle bindArgumentJ(final BoundMethodHandle boundMethodHandle, final int n, final long n2) {
        assert boundMethodHandle.speciesData() == this.oldSpeciesData();
        return boundMethodHandle.copyWithExtendJ(this.bindArgumentType(boundMethodHandle, n, LambdaForm.BasicType.J_TYPE), this.bindArgumentForm(1 + n), n2);
    }
    
    BoundMethodHandle bindArgumentF(final BoundMethodHandle boundMethodHandle, final int n, final float n2) {
        assert boundMethodHandle.speciesData() == this.oldSpeciesData();
        return boundMethodHandle.copyWithExtendF(this.bindArgumentType(boundMethodHandle, n, LambdaForm.BasicType.F_TYPE), this.bindArgumentForm(1 + n), n2);
    }
    
    BoundMethodHandle bindArgumentD(final BoundMethodHandle boundMethodHandle, final int n, final double n2) {
        assert boundMethodHandle.speciesData() == this.oldSpeciesData();
        return boundMethodHandle.copyWithExtendD(this.bindArgumentType(boundMethodHandle, n, LambdaForm.BasicType.D_TYPE), this.bindArgumentForm(1 + n), n2);
    }
    
    private MethodType bindArgumentType(final BoundMethodHandle boundMethodHandle, final int n, final LambdaForm.BasicType basicType) {
        assert boundMethodHandle.form.uncustomize() == this.lambdaForm;
        assert boundMethodHandle.form.names[1 + n].type == basicType;
        assert LambdaForm.BasicType.basicType(boundMethodHandle.type().parameterType(n)) == basicType;
        return boundMethodHandle.type().dropParameterTypes(n, n + 1);
    }
    
    LambdaForm bindArgumentForm(final int n) {
        final Transform of = Transform.of(Transform.Kind.BIND_ARG, n);
        final LambdaForm inCache = this.getInCache(of);
        if (inCache == null) {
            final LambdaFormBuffer buffer = this.buffer();
            buffer.startEdit();
            final BoundMethodHandle.SpeciesData oldSpeciesData = this.oldSpeciesData();
            final BoundMethodHandle.SpeciesData speciesData = this.newSpeciesData(this.lambdaForm.parameterType(n));
            final LambdaForm.Name parameter = this.lambdaForm.parameter(0);
            final LambdaForm.NamedFunction getterFunction = speciesData.getterFunction(oldSpeciesData.fieldCount());
            if (n != 0) {
                buffer.replaceFunctions(oldSpeciesData.getterFunctions(), speciesData.getterFunctions(), parameter);
                final LambdaForm.Name withConstraint = parameter.withConstraint(speciesData);
                buffer.renameParameter(0, withConstraint);
                buffer.replaceParameterByNewExpression(n, new LambdaForm.Name(getterFunction, new Object[] { withConstraint }));
            }
            else {
                assert oldSpeciesData == BoundMethodHandle.SpeciesData.EMPTY;
                final LambdaForm.Name withConstraint2 = new LambdaForm.Name(LambdaForm.BasicType.L_TYPE).withConstraint(speciesData);
                buffer.replaceParameterByNewExpression(0, new LambdaForm.Name(getterFunction, new Object[] { withConstraint2 }));
                buffer.insertParameter(0, withConstraint2);
            }
            return this.putInCache(of, buffer.endEdit());
        }
        assert inCache.parameterConstraint(0) == this.newSpeciesData(this.lambdaForm.parameterType(n));
        return inCache;
    }
    
    LambdaForm addArgumentForm(final int n, final LambdaForm.BasicType basicType) {
        final Transform of = Transform.of(Transform.Kind.ADD_ARG, n, basicType.ordinal());
        final LambdaForm inCache = this.getInCache(of);
        if (inCache == null) {
            final LambdaFormBuffer buffer = this.buffer();
            buffer.startEdit();
            buffer.insertParameter(n, new LambdaForm.Name(basicType));
            return this.putInCache(of, buffer.endEdit());
        }
        assert inCache.arity == this.lambdaForm.arity + 1;
        assert inCache.parameterType(n) == basicType;
        return inCache;
    }
    
    LambdaForm dupArgumentForm(final int n, final int n2) {
        final Transform of = Transform.of(Transform.Kind.DUP_ARG, n, n2);
        final LambdaForm inCache = this.getInCache(of);
        if (inCache != null) {
            assert inCache.arity == this.lambdaForm.arity - 1;
            return inCache;
        }
        else {
            final LambdaFormBuffer buffer = this.buffer();
            buffer.startEdit();
            assert this.lambdaForm.parameter(n).constraint == null;
            assert this.lambdaForm.parameter(n2).constraint == null;
            buffer.replaceParameterByCopy(n2, n);
            return this.putInCache(of, buffer.endEdit());
        }
    }
    
    LambdaForm spreadArgumentsForm(final int n, final Class<?> clazz, final int n2) {
        final Class componentType = clazz.getComponentType();
        Class<Object[]> clazz2 = (Class<Object[]>)clazz;
        if (!componentType.isPrimitive()) {
            clazz2 = Object[].class;
        }
        final LambdaForm.BasicType basicType = LambdaForm.BasicType.basicType(componentType);
        int ordinal = basicType.ordinal();
        if (basicType.basicTypeClass() != componentType && componentType.isPrimitive()) {
            ordinal = LambdaForm.BasicType.TYPE_LIMIT + Wrapper.forPrimitiveType(componentType).ordinal();
        }
        final Transform of = Transform.of(Transform.Kind.SPREAD_ARGS, n, ordinal, n2);
        final LambdaForm inCache = this.getInCache(of);
        if (inCache != null) {
            assert inCache.arity == this.lambdaForm.arity - n2 + 1;
            return inCache;
        }
        else {
            final LambdaFormBuffer buffer = this.buffer();
            buffer.startEdit();
            assert n <= 255;
            assert n + n2 <= this.lambdaForm.arity;
            assert n > 0;
            final LambdaForm.Name name = new LambdaForm.Name(LambdaForm.BasicType.L_TYPE);
            final LambdaForm.Name name2 = new LambdaForm.Name(MethodHandleImpl.Lazy.NF_checkSpreadArgument, new Object[] { name, n2 });
            int arity = this.lambdaForm.arity();
            buffer.insertExpression(arity++, name2);
            final MethodHandle arrayElementGetter = MethodHandles.arrayElementGetter(clazz2);
            for (int i = 0; i < n2; ++i) {
                buffer.insertExpression(arity + i, new LambdaForm.Name(arrayElementGetter, new Object[] { name, i }));
                buffer.replaceParameterByCopy(n + i, arity + i);
            }
            buffer.insertParameter(n, name);
            return this.putInCache(of, buffer.endEdit());
        }
    }
    
    LambdaForm collectArgumentsForm(int n, final MethodType methodType) {
        final int parameterCount = methodType.parameterCount();
        final boolean b = methodType.returnType() == Void.TYPE;
        if (parameterCount == 1 && !b) {
            return this.filterArgumentForm(n, LambdaForm.BasicType.basicType(methodType.parameterType(0)));
        }
        final LambdaForm.BasicType[] basicTypes = LambdaForm.BasicType.basicTypes(methodType.parameterList());
        final Transform.Kind kind = b ? Transform.Kind.COLLECT_ARGS_TO_VOID : Transform.Kind.COLLECT_ARGS;
        if (b && parameterCount == 0) {
            n = 1;
        }
        final Transform of = Transform.of(kind, n, parameterCount, LambdaForm.BasicType.basicTypesOrd(basicTypes));
        final LambdaForm inCache = this.getInCache(of);
        if (inCache == null) {
            return this.putInCache(of, this.makeArgumentCombinationForm(n, methodType, false, b));
        }
        assert inCache.arity == this.lambdaForm.arity - (b ? 0 : 1) + parameterCount;
        return inCache;
    }
    
    LambdaForm collectArgumentArrayForm(final int n, final MethodHandle methodHandle) {
        final MethodType type = methodHandle.type();
        final int parameterCount = type.parameterCount();
        assert methodHandle.intrinsicName() == MethodHandleImpl.Intrinsic.NEW_ARRAY;
        final Class<?> componentType = type.returnType().getComponentType();
        final LambdaForm.BasicType basicType = LambdaForm.BasicType.basicType(componentType);
        int ordinal = basicType.ordinal();
        if (basicType.basicTypeClass() != componentType) {
            if (!componentType.isPrimitive()) {
                return null;
            }
            ordinal = LambdaForm.BasicType.TYPE_LIMIT + Wrapper.forPrimitiveType(componentType).ordinal();
        }
        assert type.parameterList().equals(Collections.nCopies(parameterCount, componentType));
        final Transform of = Transform.of(Transform.Kind.COLLECT_ARGS_TO_ARRAY, n, parameterCount, ordinal);
        final LambdaForm inCache = this.getInCache(of);
        if (inCache != null) {
            assert inCache.arity == this.lambdaForm.arity - 1 + parameterCount;
            return inCache;
        }
        else {
            final LambdaFormBuffer buffer = this.buffer();
            buffer.startEdit();
            assert n + 1 <= this.lambdaForm.arity;
            assert n > 0;
            final LambdaForm.Name[] array = new LambdaForm.Name[parameterCount];
            for (int i = 0; i < parameterCount; ++i) {
                array[i] = new LambdaForm.Name(n + i, basicType);
            }
            final LambdaForm.Name name = new LambdaForm.Name(methodHandle, (Object[])array);
            final int arity = this.lambdaForm.arity();
            buffer.insertExpression(arity, name);
            int n2 = n + 1;
            final LambdaForm.Name[] array2 = array;
            for (int length = array2.length, j = 0; j < length; ++j) {
                buffer.insertParameter(n2++, array2[j]);
            }
            assert buffer.lastIndexOf(name) == arity + array.length;
            buffer.replaceParameterByCopy(n, arity + array.length);
            return this.putInCache(of, buffer.endEdit());
        }
    }
    
    LambdaForm filterArgumentForm(final int n, final LambdaForm.BasicType basicType) {
        final Transform of = Transform.of(Transform.Kind.FILTER_ARG, n, basicType.ordinal());
        final LambdaForm inCache = this.getInCache(of);
        if (inCache == null) {
            return this.putInCache(of, this.makeArgumentCombinationForm(n, MethodType.methodType(this.lambdaForm.parameterType(n).basicTypeClass(), basicType.basicTypeClass()), false, false));
        }
        assert inCache.arity == this.lambdaForm.arity;
        assert inCache.parameterType(n) == basicType;
        return inCache;
    }
    
    private LambdaForm makeArgumentCombinationForm(final int n, final MethodType methodType, final boolean b, final boolean b2) {
        final LambdaFormBuffer buffer = this.buffer();
        buffer.startEdit();
        final int parameterCount = methodType.parameterCount();
        final int n2 = b2 ? 0 : 1;
        assert n <= 255;
        assert n + n2 + (b ? parameterCount : 0) <= this.lambdaForm.arity;
        assert n > 0;
        assert methodType == methodType.basicType();
        assert !(!b2);
        final BoundMethodHandle.SpeciesData oldSpeciesData = this.oldSpeciesData();
        final BoundMethodHandle.SpeciesData speciesData = this.newSpeciesData(LambdaForm.BasicType.L_TYPE);
        final LambdaForm.Name parameter = this.lambdaForm.parameter(0);
        buffer.replaceFunctions(oldSpeciesData.getterFunctions(), speciesData.getterFunctions(), parameter);
        final LambdaForm.Name withConstraint = parameter.withConstraint(speciesData);
        buffer.renameParameter(0, withConstraint);
        final LambdaForm.Name name = new LambdaForm.Name(speciesData.getterFunction(oldSpeciesData.fieldCount()), new Object[] { withConstraint });
        final Object[] array = new Object[1 + parameterCount];
        array[0] = name;
        LambdaForm.Name[] array2;
        if (b) {
            array2 = new LambdaForm.Name[0];
            System.arraycopy(this.lambdaForm.names, n + n2, array, 1, parameterCount);
        }
        else {
            array2 = new LambdaForm.Name[parameterCount];
            final LambdaForm.BasicType[] basicTypes = LambdaForm.BasicType.basicTypes(methodType.parameterList());
            for (int i = 0; i < basicTypes.length; ++i) {
                array2[i] = new LambdaForm.Name(n + i, basicTypes[i]);
            }
            System.arraycopy(array2, 0, array, 1, parameterCount);
        }
        final LambdaForm.Name name2 = new LambdaForm.Name(methodType, array);
        final int arity = this.lambdaForm.arity();
        buffer.insertExpression(arity + 0, name);
        buffer.insertExpression(arity + 1, name2);
        int n3 = n + n2;
        final LambdaForm.Name[] array3 = array2;
        for (int length = array3.length, j = 0; j < length; ++j) {
            buffer.insertParameter(n3++, array3[j]);
        }
        assert buffer.lastIndexOf(name2) == arity + 1 + array2.length;
        if (!b2) {
            buffer.replaceParameterByCopy(n, arity + 1 + array2.length);
        }
        return buffer.endEdit();
    }
    
    LambdaForm filterReturnForm(final LambdaForm.BasicType basicType, final boolean b) {
        final Transform of = Transform.of(b ? Transform.Kind.FILTER_RETURN_TO_ZERO : Transform.Kind.FILTER_RETURN, basicType.ordinal());
        final LambdaForm inCache = this.getInCache(of);
        if (inCache == null) {
            final LambdaFormBuffer buffer = this.buffer();
            buffer.startEdit();
            int length = this.lambdaForm.names.length;
            LambdaForm.Name result;
            if (b) {
                if (basicType == LambdaForm.BasicType.V_TYPE) {
                    result = null;
                }
                else {
                    result = new LambdaForm.Name(LambdaForm.constantZero(basicType), new Object[0]);
                }
            }
            else {
                final BoundMethodHandle.SpeciesData oldSpeciesData = this.oldSpeciesData();
                final BoundMethodHandle.SpeciesData speciesData = this.newSpeciesData(LambdaForm.BasicType.L_TYPE);
                final LambdaForm.Name parameter = this.lambdaForm.parameter(0);
                buffer.replaceFunctions(oldSpeciesData.getterFunctions(), speciesData.getterFunctions(), parameter);
                final LambdaForm.Name withConstraint = parameter.withConstraint(speciesData);
                buffer.renameParameter(0, withConstraint);
                final LambdaForm.Name name = new LambdaForm.Name(speciesData.getterFunction(oldSpeciesData.fieldCount()), new Object[] { withConstraint });
                buffer.insertExpression(length++, name);
                final LambdaForm.BasicType returnType = this.lambdaForm.returnType();
                if (returnType == LambdaForm.BasicType.V_TYPE) {
                    result = new LambdaForm.Name(MethodType.methodType(basicType.basicTypeClass()), new Object[] { name });
                }
                else {
                    result = new LambdaForm.Name(MethodType.methodType(basicType.basicTypeClass(), returnType.basicTypeClass()), new Object[] { name, this.lambdaForm.names[this.lambdaForm.result] });
                }
            }
            if (result != null) {
                buffer.insertExpression(length++, result);
            }
            buffer.setResult(result);
            return this.putInCache(of, buffer.endEdit());
        }
        assert inCache.arity == this.lambdaForm.arity;
        assert inCache.returnType() == basicType;
        return inCache;
    }
    
    LambdaForm foldArgumentsForm(final int n, final boolean b, final MethodType methodType) {
        final int parameterCount = methodType.parameterCount();
        final Transform.Kind kind = b ? Transform.Kind.FOLD_ARGS_TO_VOID : Transform.Kind.FOLD_ARGS;
        final Transform of = Transform.of(kind, n, parameterCount);
        final LambdaForm inCache = this.getInCache(of);
        if (inCache == null) {
            return this.putInCache(of, this.makeArgumentCombinationForm(n, methodType, true, b));
        }
        assert inCache.arity == this.lambdaForm.arity - ((kind == Transform.Kind.FOLD_ARGS) ? 1 : 0);
        return inCache;
    }
    
    LambdaForm permuteArgumentsForm(final int n, final int[] array) {
        assert n == 1;
        final int length = this.lambdaForm.names.length;
        final int length2 = array.length;
        int max = 0;
        boolean b = true;
        for (int i = 0; i < array.length; ++i) {
            final int n2 = array[i];
            if (n2 != i) {
                b = false;
            }
            max = Math.max(max, n2 + 1);
        }
        assert n + array.length == this.lambdaForm.arity;
        if (b) {
            return this.lambdaForm;
        }
        final Transform of = Transform.of(Transform.Kind.PERMUTE_ARGS, array);
        final LambdaForm inCache = this.getInCache(of);
        if (inCache != null) {
            assert inCache.arity == n + max : inCache;
            return inCache;
        }
        else {
            final LambdaForm.BasicType[] array2 = new LambdaForm.BasicType[max];
            for (int j = 0; j < length2; ++j) {
                array2[array[j]] = this.lambdaForm.names[n + j].type;
            }
            assert n + length2 == this.lambdaForm.arity;
            assert permutedTypesMatch(array, array2, this.lambdaForm.names, n);
            int n3;
            for (n3 = 0; n3 < length2 && array[n3] == n3; ++n3) {}
            final LambdaForm.Name[] array3 = new LambdaForm.Name[length - length2 + max];
            System.arraycopy(this.lambdaForm.names, 0, array3, 0, n + n3);
            final int n4 = length - this.lambdaForm.arity;
            System.arraycopy(this.lambdaForm.names, n + length2, array3, n + max, n4);
            final int n5 = array3.length - n4;
            int result = this.lambdaForm.result;
            if (result >= n) {
                if (result < n + length2) {
                    result = array[result - n] + n;
                }
                else {
                    result = result - length2 + max;
                }
            }
            for (int k = n3; k < length2; ++k) {
                final LambdaForm.Name name = this.lambdaForm.names[n + k];
                final int n6 = array[k];
                LambdaForm.Name name2 = array3[n + n6];
                if (name2 == null) {
                    name2 = (array3[n + n6] = new LambdaForm.Name(array2[n6]));
                }
                else {
                    assert name2.type == array2[n6];
                }
                for (int l = n5; l < array3.length; ++l) {
                    array3[l] = array3[l].replaceName(name, name2);
                }
            }
            for (int n7 = n + n3; n7 < n5; ++n7) {
                if (array3[n7] == null) {
                    array3[n7] = LambdaForm.argument(n7, array2[n7 - n]);
                }
            }
            for (int arity = this.lambdaForm.arity; arity < this.lambdaForm.names.length; ++arity) {
                final int n8 = arity - this.lambdaForm.arity + n5;
                final LambdaForm.Name name3 = this.lambdaForm.names[arity];
                final LambdaForm.Name name4 = array3[n8];
                if (name3 != name4) {
                    for (int n9 = n8 + 1; n9 < array3.length; ++n9) {
                        array3[n9] = array3[n9].replaceName(name3, name4);
                    }
                }
            }
            return this.putInCache(of, new LambdaForm(this.lambdaForm.debugName, n5, array3, result));
        }
    }
    
    static boolean permutedTypesMatch(final int[] array, final LambdaForm.BasicType[] array2, final LambdaForm.Name[] array3, final int n) {
        for (int i = 0; i < array.length; ++i) {
            assert array3[n + i].isParam();
            assert array3[n + i].type == array2[array[i]];
        }
        return true;
    }
    
    private static final class Transform extends SoftReference<LambdaForm>
    {
        final long packedBytes;
        final byte[] fullBytes;
        private static final boolean STRESS_TEST = false;
        private static final int PACKED_BYTE_SIZE = 4;
        private static final int PACKED_BYTE_MASK = 15;
        private static final int PACKED_BYTE_MAX_LENGTH = 16;
        private static final byte[] NO_BYTES;
        
        private static long packedBytes(final byte[] array) {
            if (array.length > 16) {
                return 0L;
            }
            long n = 0L;
            int n2 = 0;
            for (int i = 0; i < array.length; ++i) {
                final int n3 = array[i] & 0xFF;
                n2 |= n3;
                n |= (long)n3 << i * 4;
            }
            if (!inRange(n2)) {
                return 0L;
            }
            return n;
        }
        
        private static long packedBytes(final int n, final int n2) {
            assert inRange(n | n2);
            return n << 0 | n2 << 4;
        }
        
        private static long packedBytes(final int n, final int n2, final int n3) {
            assert inRange(n | n2 | n3);
            return n << 0 | n2 << 4 | n3 << 8;
        }
        
        private static long packedBytes(final int n, final int n2, final int n3, final int n4) {
            assert inRange(n | n2 | n3 | n4);
            return n << 0 | n2 << 4 | n3 << 8 | n4 << 12;
        }
        
        private static boolean inRange(final int n) {
            assert (n & 0xFF) == n;
            return (n & 0xFFFFFFF0) == 0x0;
        }
        
        private static byte[] fullBytes(final int... array) {
            final byte[] array2 = new byte[array.length];
            int n = 0;
            for (int length = array.length, i = 0; i < length; ++i) {
                array2[n++] = bval(array[i]);
            }
            assert packedBytes(array2) == 0L;
            return array2;
        }
        
        private byte byteAt(final int n) {
            final long packedBytes = this.packedBytes;
            if (packedBytes == 0L) {
                if (n >= this.fullBytes.length) {
                    return 0;
                }
                return this.fullBytes[n];
            }
            else {
                assert this.fullBytes == null;
                if (n > 16) {
                    return 0;
                }
                return (byte)(packedBytes >>> n * 4 & 0xFL);
            }
        }
        
        Kind kind() {
            return Kind.values()[this.byteAt(0)];
        }
        
        private Transform(final long packedBytes, final byte[] fullBytes, final LambdaForm lambdaForm) {
            super(lambdaForm);
            this.packedBytes = packedBytes;
            this.fullBytes = fullBytes;
        }
        
        private Transform(final long n) {
            this(n, null, null);
            assert n != 0L;
        }
        
        private Transform(final byte[] array) {
            this(0L, array, null);
        }
        
        private static byte bval(final int n) {
            assert (n & 0xFF) == n;
            return (byte)n;
        }
        
        private static byte bval(final Kind kind) {
            return bval(kind.ordinal());
        }
        
        static Transform of(final Kind kind, final int n) {
            final byte bval = bval(kind);
            if (inRange(bval | n)) {
                return new Transform(packedBytes(bval, n));
            }
            return new Transform(fullBytes(bval, n));
        }
        
        static Transform of(final Kind kind, final int n, final int n2) {
            final byte b = (byte)kind.ordinal();
            if (inRange(b | n | n2)) {
                return new Transform(packedBytes(b, n, n2));
            }
            return new Transform(fullBytes(b, n, n2));
        }
        
        static Transform of(final Kind kind, final int n, final int n2, final int n3) {
            final byte b = (byte)kind.ordinal();
            if (inRange(b | n | n2 | n3)) {
                return new Transform(packedBytes(b, n, n2, n3));
            }
            return new Transform(fullBytes(b, n, n2, n3));
        }
        
        static Transform of(final Kind kind, final int... array) {
            return ofBothArrays(kind, array, Transform.NO_BYTES);
        }
        
        static Transform of(final Kind kind, final int n, final byte[] array) {
            return ofBothArrays(kind, new int[] { n }, array);
        }
        
        static Transform of(final Kind kind, final int n, final int n2, final byte[] array) {
            return ofBothArrays(kind, new int[] { n, n2 }, array);
        }
        
        private static Transform ofBothArrays(final Kind kind, final int[] array, final byte[] array2) {
            final byte[] array3 = new byte[1 + array.length + array2.length];
            int n = 0;
            array3[n++] = bval(kind);
            for (int length = array.length, i = 0; i < length; ++i) {
                array3[n++] = bval(array[i]);
            }
            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                array3[n++] = array2[j];
            }
            final long packedBytes = packedBytes(array3);
            if (packedBytes != 0L) {
                return new Transform(packedBytes);
            }
            return new Transform(array3);
        }
        
        Transform withResult(final LambdaForm lambdaForm) {
            return new Transform(this.packedBytes, this.fullBytes, lambdaForm);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Transform && this.equals((Transform)o);
        }
        
        public boolean equals(final Transform transform) {
            return this.packedBytes == transform.packedBytes && Arrays.equals(this.fullBytes, transform.fullBytes);
        }
        
        @Override
        public int hashCode() {
            if (this.packedBytes == 0L) {
                return Arrays.hashCode(this.fullBytes);
            }
            assert this.fullBytes == null;
            return Long.hashCode(this.packedBytes);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            long packedBytes = this.packedBytes;
            if (packedBytes != 0L) {
                sb.append("(");
                while (packedBytes != 0L) {
                    sb.append(packedBytes & 0xFL);
                    packedBytes >>>= 4;
                    if (packedBytes != 0L) {
                        sb.append(",");
                    }
                }
                sb.append(")");
            }
            if (this.fullBytes != null) {
                sb.append("unpacked");
                sb.append(Arrays.toString(this.fullBytes));
            }
            final LambdaForm lambdaForm = this.get();
            if (lambdaForm != null) {
                sb.append(" result=");
                sb.append(lambdaForm);
            }
            return sb.toString();
        }
        
        static {
            NO_BYTES = new byte[0];
        }
        
        private enum Kind
        {
            NO_KIND, 
            BIND_ARG, 
            ADD_ARG, 
            DUP_ARG, 
            SPREAD_ARGS, 
            FILTER_ARG, 
            FILTER_RETURN, 
            FILTER_RETURN_TO_ZERO, 
            COLLECT_ARGS, 
            COLLECT_ARGS_TO_VOID, 
            COLLECT_ARGS_TO_ARRAY, 
            FOLD_ARGS, 
            FOLD_ARGS_TO_VOID, 
            PERMUTE_ARGS;
        }
    }
}
