package java.lang.invoke;

import java.io.Serializable;
import sun.invoke.util.Wrapper;
import java.lang.ref.SoftReference;

final class MethodTypeForm
{
    final int[] argToSlotTable;
    final int[] slotToArgTable;
    final long argCounts;
    final long primCounts;
    final MethodType erasedType;
    final MethodType basicType;
    @Stable
    final SoftReference<MethodHandle>[] methodHandles;
    static final int MH_BASIC_INV = 0;
    static final int MH_NF_INV = 1;
    static final int MH_UNINIT_CS = 2;
    static final int MH_LIMIT = 3;
    @Stable
    final SoftReference<LambdaForm>[] lambdaForms;
    static final int LF_INVVIRTUAL = 0;
    static final int LF_INVSTATIC = 1;
    static final int LF_INVSPECIAL = 2;
    static final int LF_NEWINVSPECIAL = 3;
    static final int LF_INVINTERFACE = 4;
    static final int LF_INVSTATIC_INIT = 5;
    static final int LF_INTERPRET = 6;
    static final int LF_REBIND = 7;
    static final int LF_DELEGATE = 8;
    static final int LF_DELEGATE_BLOCK_INLINING = 9;
    static final int LF_EX_LINKER = 10;
    static final int LF_EX_INVOKER = 11;
    static final int LF_GEN_LINKER = 12;
    static final int LF_GEN_INVOKER = 13;
    static final int LF_CS_LINKER = 14;
    static final int LF_MH_LINKER = 15;
    static final int LF_GWC = 16;
    static final int LF_GWT = 17;
    static final int LF_LIMIT = 18;
    public static final int NO_CHANGE = 0;
    public static final int ERASE = 1;
    public static final int WRAP = 2;
    public static final int UNWRAP = 3;
    public static final int INTS = 4;
    public static final int LONGS = 5;
    public static final int RAW_RETURN = 6;
    
    public MethodType erasedType() {
        return this.erasedType;
    }
    
    public MethodType basicType() {
        return this.basicType;
    }
    
    private boolean assertIsBasicType() {
        assert this.erasedType == this.basicType : "erasedType: " + this.erasedType + " != basicType: " + this.basicType;
        return true;
    }
    
    public MethodHandle cachedMethodHandle(final int n) {
        assert this.assertIsBasicType();
        final SoftReference<MethodHandle> softReference = this.methodHandles[n];
        return (softReference != null) ? softReference.get() : null;
    }
    
    public synchronized MethodHandle setCachedMethodHandle(final int n, final MethodHandle methodHandle) {
        final SoftReference<MethodHandle> softReference = this.methodHandles[n];
        if (softReference != null) {
            final MethodHandle methodHandle2 = softReference.get();
            if (methodHandle2 != null) {
                return methodHandle2;
            }
        }
        this.methodHandles[n] = new SoftReference<MethodHandle>(methodHandle);
        return methodHandle;
    }
    
    public LambdaForm cachedLambdaForm(final int n) {
        assert this.assertIsBasicType();
        final SoftReference<LambdaForm> softReference = this.lambdaForms[n];
        return (softReference != null) ? softReference.get() : null;
    }
    
    public synchronized LambdaForm setCachedLambdaForm(final int n, final LambdaForm lambdaForm) {
        final SoftReference<LambdaForm> softReference = this.lambdaForms[n];
        if (softReference != null) {
            final LambdaForm lambdaForm2 = softReference.get();
            if (lambdaForm2 != null) {
                return lambdaForm2;
            }
        }
        this.lambdaForms[n] = new SoftReference<LambdaForm>(lambdaForm);
        return lambdaForm;
    }
    
    protected MethodTypeForm(final MethodType methodType) {
        this.erasedType = methodType;
        final Class<?>[] ptypes = methodType.ptypes();
        final int length;
        final int n = length = ptypes.length;
        int n2 = 1;
        int n3 = 1;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        Class<?>[] array2;
        final Class<?>[] array = array2 = ptypes;
        for (int i = 0; i < array.length; ++i) {
            final Class<?> clazz = array[i];
            if (clazz != Object.class) {
                ++n4;
                final Wrapper forPrimitiveType = Wrapper.forPrimitiveType(clazz);
                if (forPrimitiveType.isDoubleWord()) {
                    ++n5;
                }
                if (forPrimitiveType.isSubwordOrInt() && clazz != Integer.TYPE) {
                    if (array2 == array) {
                        array2 = array2.clone();
                    }
                    array2[i] = Integer.TYPE;
                }
            }
        }
        final int n8 = length + n5;
        final Class<?> returnType = methodType.returnType();
        Serializable type;
        if ((type = returnType) != Object.class) {
            ++n6;
            final Wrapper forPrimitiveType2 = Wrapper.forPrimitiveType(returnType);
            if (forPrimitiveType2.isDoubleWord()) {
                ++n7;
            }
            if (forPrimitiveType2.isSubwordOrInt() && returnType != Integer.TYPE) {
                type = Integer.TYPE;
            }
            if (returnType == Void.TYPE) {
                n3 = (n2 = 0);
            }
            else {
                n3 += n7;
            }
        }
        if (array == array2 && type == returnType) {
            this.basicType = methodType;
            int[] slotToArgTable;
            int[] argToSlotTable;
            if (n5 != 0) {
                int n9 = n + n5;
                slotToArgTable = new int[n9 + 1];
                argToSlotTable = new int[1 + n];
                argToSlotTable[0] = n9;
                for (int j = 0; j < array.length; ++j) {
                    if (Wrapper.forBasicType(array[j]).isDoubleWord()) {
                        --n9;
                    }
                    --n9;
                    slotToArgTable[n9] = j + 1;
                    argToSlotTable[1 + j] = n9;
                }
                assert n9 == 0;
            }
            else if (n4 != 0) {
                assert n == n8;
                final MethodTypeForm form = MethodType.genericMethodType(n).form();
                assert this != form;
                slotToArgTable = form.slotToArgTable;
                argToSlotTable = form.argToSlotTable;
            }
            else {
                int n10 = n;
                slotToArgTable = new int[n10 + 1];
                argToSlotTable = new int[1 + n];
                argToSlotTable[0] = n10;
                for (int k = 0; k < n; ++k) {
                    --n10;
                    slotToArgTable[n10] = k + 1;
                    argToSlotTable[1 + k] = n10;
                }
            }
            this.primCounts = pack(n7, n6, n5, n4);
            this.argCounts = pack(n3, n2, n8, n);
            this.argToSlotTable = argToSlotTable;
            this.slotToArgTable = slotToArgTable;
            if (n8 >= 256) {
                throw MethodHandleStatics.newIllegalArgumentException("too many arguments");
            }
            assert this.basicType == methodType;
            this.lambdaForms = new SoftReference[18];
            this.methodHandles = new SoftReference[3];
        }
        else {
            this.basicType = MethodType.makeImpl((Class<?>)type, array2, true);
            final MethodTypeForm form2 = this.basicType.form();
            assert this != form2;
            this.primCounts = form2.primCounts;
            this.argCounts = form2.argCounts;
            this.argToSlotTable = form2.argToSlotTable;
            this.slotToArgTable = form2.slotToArgTable;
            this.methodHandles = null;
            this.lambdaForms = null;
        }
    }
    
    private static long pack(final int n, final int n2, final int n3, final int n4) {
        assert ((n | n2 | n3 | n4) & 0xFFFF0000) == 0x0;
        return (long)(n << 16 | n2) << 32 | (long)(n3 << 16 | n4);
    }
    
    private static char unpack(final long n, final int n2) {
        assert n2 <= 3;
        return (char)(n >> (3 - n2) * 16);
    }
    
    public int parameterCount() {
        return unpack(this.argCounts, 3);
    }
    
    public int parameterSlotCount() {
        return unpack(this.argCounts, 2);
    }
    
    public int returnCount() {
        return unpack(this.argCounts, 1);
    }
    
    public int returnSlotCount() {
        return unpack(this.argCounts, 0);
    }
    
    public int primitiveParameterCount() {
        return unpack(this.primCounts, 3);
    }
    
    public int longPrimitiveParameterCount() {
        return unpack(this.primCounts, 2);
    }
    
    public int primitiveReturnCount() {
        return unpack(this.primCounts, 1);
    }
    
    public int longPrimitiveReturnCount() {
        return unpack(this.primCounts, 0);
    }
    
    public boolean hasPrimitives() {
        return this.primCounts != 0L;
    }
    
    public boolean hasNonVoidPrimitives() {
        return this.primCounts != 0L && (this.primitiveParameterCount() != 0 || (this.primitiveReturnCount() != 0 && this.returnCount() != 0));
    }
    
    public boolean hasLongPrimitives() {
        return (this.longPrimitiveParameterCount() | this.longPrimitiveReturnCount()) != 0x0;
    }
    
    public int parameterToArgSlot(final int n) {
        return this.argToSlotTable[1 + n];
    }
    
    public int argSlotToParameter(final int n) {
        return this.slotToArgTable[n] - 1;
    }
    
    static MethodTypeForm findForm(final MethodType methodType) {
        final MethodType canonicalize = canonicalize(methodType, 1, 1);
        if (canonicalize == null) {
            return new MethodTypeForm(methodType);
        }
        return canonicalize.form();
    }
    
    public static MethodType canonicalize(final MethodType methodType, final int n, final int n2) {
        final Class<?>[] ptypes = methodType.ptypes();
        Class<?>[] canonicalizeAll = canonicalizeAll(ptypes, n2);
        final Class<?> returnType = methodType.returnType();
        Class<?> canonicalize = canonicalize(returnType, n);
        if (canonicalizeAll == null && canonicalize == null) {
            return null;
        }
        if (canonicalize == null) {
            canonicalize = returnType;
        }
        if (canonicalizeAll == null) {
            canonicalizeAll = ptypes;
        }
        return MethodType.makeImpl(canonicalize, canonicalizeAll, true);
    }
    
    static Class<?> canonicalize(final Class<?> clazz, final int n) {
        if (clazz != Object.class) {
            if (!clazz.isPrimitive()) {
                switch (n) {
                    case 3: {
                        final Class<Object> primitiveType = Wrapper.asPrimitiveType(clazz);
                        if (primitiveType != clazz) {
                            return primitiveType;
                        }
                        break;
                    }
                    case 1:
                    case 6: {
                        return Object.class;
                    }
                }
            }
            else if (clazz == Void.TYPE) {
                switch (n) {
                    case 6: {
                        return Integer.TYPE;
                    }
                    case 2: {
                        return Void.class;
                    }
                }
            }
            else {
                switch (n) {
                    case 2: {
                        return Wrapper.asWrapperType(clazz);
                    }
                    case 4: {
                        if (clazz == Integer.TYPE || clazz == Long.TYPE) {
                            return null;
                        }
                        if (clazz == Double.TYPE) {
                            return Long.TYPE;
                        }
                        return Integer.TYPE;
                    }
                    case 5: {
                        if (clazz == Long.TYPE) {
                            return null;
                        }
                        return Long.TYPE;
                    }
                    case 6: {
                        if (clazz == Integer.TYPE || clazz == Long.TYPE || clazz == Float.TYPE || clazz == Double.TYPE) {
                            return null;
                        }
                        return Integer.TYPE;
                    }
                }
            }
        }
        return null;
    }
    
    static Class<?>[] canonicalizeAll(final Class<?>[] array, final int n) {
        Class<?>[] array2 = null;
        for (int length = array.length, i = 0; i < length; ++i) {
            Class<?> canonicalize = canonicalize(array[i], n);
            if (canonicalize == Void.TYPE) {
                canonicalize = null;
            }
            if (canonicalize != null) {
                if (array2 == null) {
                    array2 = array.clone();
                }
                array2[i] = canonicalize;
            }
        }
        return array2;
    }
    
    @Override
    public String toString() {
        return "Form" + this.erasedType;
    }
}
