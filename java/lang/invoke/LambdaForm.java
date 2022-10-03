package java.lang.invoke;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import sun.invoke.util.Wrapper;
import java.util.Iterator;
import java.util.Arrays;
import java.util.HashMap;

class LambdaForm
{
    final int arity;
    final int result;
    final boolean forceInline;
    final MethodHandle customized;
    @Stable
    final Name[] names;
    final String debugName;
    MemberName vmentry;
    private boolean isCompiled;
    volatile Object transformCache;
    public static final int VOID_RESULT = -1;
    public static final int LAST_RESULT = -2;
    private static final boolean USE_PREDEFINED_INTERPRET_METHODS = true;
    private static final int COMPILE_THRESHOLD;
    private int invocationCounter;
    static final int INTERNED_ARGUMENT_LIMIT = 10;
    private static final Name[][] INTERNED_ARGUMENTS;
    private static final MemberName.Factory IMPL_NAMES;
    private static final LambdaForm[] LF_identityForm;
    private static final LambdaForm[] LF_zeroForm;
    private static final NamedFunction[] NF_identity;
    private static final NamedFunction[] NF_zero;
    private static final HashMap<String, Integer> DEBUG_NAME_COUNTERS;
    private static final boolean TRACE_INTERPRETER;
    
    LambdaForm(final String s, final int n, final Name[] array, final int n2) {
        this(s, n, array, n2, true, null);
    }
    
    LambdaForm(final String s, final int arity, final Name[] array, final int n, final boolean forceInline, final MethodHandle customized) {
        this.invocationCounter = 0;
        assert namesOK(arity, array);
        this.arity = arity;
        this.result = fixResult(n, array);
        this.names = array.clone();
        this.debugName = fixDebugName(s);
        this.forceInline = forceInline;
        this.customized = customized;
        final int normalize = this.normalize();
        if (normalize > 253) {
            assert normalize <= 255;
            this.compileToBytecode();
        }
    }
    
    LambdaForm(final String s, final int n, final Name[] array) {
        this(s, n, array, -2, true, null);
    }
    
    LambdaForm(final String s, final int n, final Name[] array, final boolean b) {
        this(s, n, array, -2, b, null);
    }
    
    LambdaForm(final String s, final Name[] array, final Name[] array2, final Name name) {
        this(s, array.length, buildNames(array, array2, name), -2, true, null);
    }
    
    LambdaForm(final String s, final Name[] array, final Name[] array2, final Name name, final boolean b) {
        this(s, array.length, buildNames(array, array2, name), -2, b, null);
    }
    
    private static Name[] buildNames(final Name[] array, final Name[] array2, final Name name) {
        final int length = array.length;
        final int n = length + array2.length + ((name != null) ? 1 : 0);
        final Name[] array3 = Arrays.copyOf(array, n);
        System.arraycopy(array2, 0, array3, length, array2.length);
        if (name != null) {
            array3[n - 1] = name;
        }
        return array3;
    }
    
    private LambdaForm(final String s) {
        this.invocationCounter = 0;
        assert isValidSignature(s);
        this.arity = signatureArity(s);
        this.result = ((signatureReturn(s) == BasicType.V_TYPE) ? -1 : this.arity);
        this.names = buildEmptyNames(this.arity, s);
        this.debugName = "LF.zero";
        this.forceInline = true;
        this.customized = null;
        assert this.nameRefsAreLegal();
        assert this.isEmpty();
        assert s.equals(this.basicTypeSignature()) : s + " != " + this.basicTypeSignature();
    }
    
    private static Name[] buildEmptyNames(final int n, final String s) {
        assert isValidSignature(s);
        final int n2 = n + 1;
        if (n < 0 || s.length() != n2 + 1) {
            throw new IllegalArgumentException("bad arity for " + s);
        }
        final int n3 = (BasicType.basicType(s.charAt(n2)) != BasicType.V_TYPE) ? 1 : 0;
        final Name[] arguments = arguments(n3, s.substring(0, n));
        for (int i = 0; i < n3; ++i) {
            arguments[n + i] = new Name(constantZero(BasicType.basicType(s.charAt(n2 + i))), new Object[0]).newIndex(n + i);
        }
        return arguments;
    }
    
    private static int fixResult(int n, final Name[] array) {
        if (n == -2) {
            n = array.length - 1;
        }
        if (n >= 0 && array[n].type == BasicType.V_TYPE) {
            n = -1;
        }
        return n;
    }
    
    private static String fixDebugName(final String s) {
        if (LambdaForm.DEBUG_NAME_COUNTERS != null) {
            int index = s.indexOf(95);
            final int length = s.length();
            if (index < 0) {
                index = length;
            }
            final String substring = s.substring(0, index);
            Integer value;
            synchronized (LambdaForm.DEBUG_NAME_COUNTERS) {
                value = LambdaForm.DEBUG_NAME_COUNTERS.get(substring);
                if (value == null) {
                    value = 0;
                }
                LambdaForm.DEBUG_NAME_COUNTERS.put(substring, value + 1);
            }
            final StringBuilder sb = new StringBuilder(substring);
            sb.append('_');
            final int length2 = sb.length();
            sb.append((int)value);
            for (int i = sb.length() - length2; i < 3; ++i) {
                sb.insert(length2, '0');
            }
            if (index < length) {
                ++index;
                while (index < length && Character.isDigit(s.charAt(index))) {
                    ++index;
                }
                if (index < length && s.charAt(index) == '_') {
                    ++index;
                }
                if (index < length) {
                    sb.append('_').append(s, index, length);
                }
            }
            return sb.toString();
        }
        return s;
    }
    
    private static boolean namesOK(final int n, final Name[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Name name = array[i];
            assert name != null : "n is null";
            if (i < n) {
                assert name.isParam() : name + " is not param at " + i;
            }
            else {
                assert !name.isParam() : name + " is param at " + i;
            }
        }
        return true;
    }
    
    LambdaForm customize(final MethodHandle methodHandle) {
        final LambdaForm lambdaForm = new LambdaForm(this.debugName, this.arity, this.names, this.result, this.forceInline, methodHandle);
        if (LambdaForm.COMPILE_THRESHOLD > 0 && this.isCompiled) {
            lambdaForm.compileToBytecode();
        }
        lambdaForm.transformCache = this;
        return lambdaForm;
    }
    
    LambdaForm uncustomize() {
        if (this.customized == null) {
            return this;
        }
        assert this.transformCache != null;
        final LambdaForm lambdaForm = (LambdaForm)this.transformCache;
        if (LambdaForm.COMPILE_THRESHOLD > 0 && this.isCompiled) {
            lambdaForm.compileToBytecode();
        }
        return lambdaForm;
    }
    
    private int normalize() {
        Name[] array = null;
        int length = 0;
        int n = 0;
        for (int i = 0; i < this.names.length; ++i) {
            final Name name = this.names[i];
            if (!name.initIndex(i)) {
                if (array == null) {
                    array = this.names.clone();
                    n = i;
                }
                this.names[i] = name.cloneWithIndex(i);
            }
            if (name.arguments != null && length < name.arguments.length) {
                length = name.arguments.length;
            }
        }
        if (array != null) {
            int arity = this.arity;
            if (arity <= n) {
                arity = n + 1;
            }
            for (int j = arity; j < this.names.length; ++j) {
                this.names[j] = this.names[j].replaceNames(array, this.names, n, j).newIndex(j);
            }
        }
        assert this.nameRefsAreLegal();
        final int min = Math.min(this.arity, 10);
        boolean b = false;
        for (int k = 0; k < min; ++k) {
            final Name name2 = this.names[k];
            final Name internArgument = internArgument(name2);
            if (name2 != internArgument) {
                this.names[k] = internArgument;
                b = true;
            }
        }
        if (b) {
            for (int l = this.arity; l < this.names.length; ++l) {
                this.names[l].internArguments();
            }
        }
        assert this.nameRefsAreLegal();
        return length;
    }
    
    boolean nameRefsAreLegal() {
        assert this.arity >= 0 && this.arity <= this.names.length;
        assert this.result >= -1 && this.result < this.names.length;
        for (int i = 0; i < this.arity; ++i) {
            final Name name = this.names[i];
            assert name.index() == i : Arrays.asList(name.index(), i);
            assert name.isParam();
        }
        for (int j = this.arity; j < this.names.length; ++j) {
            final Name name2 = this.names[j];
            assert name2.index() == j;
            for (final Object o : name2.arguments) {
                if (o instanceof Name) {
                    final Name name3 = (Name)o;
                    final short access$000 = name3.index;
                    assert 0 <= access$000 && access$000 < this.names.length : name2.debugString() + ": 0 <= i2 && i2 < names.length: 0 <= " + access$000 + " < " + this.names.length;
                    assert this.names[access$000] == name3 : Arrays.asList("-1-", j, "-2-", name2.debugString(), "-3-", access$000, "-4-", name3.debugString(), "-5-", this.names[access$000].debugString(), "-6-", this);
                    assert access$000 < j;
                }
            }
        }
        return true;
    }
    
    BasicType returnType() {
        if (this.result < 0) {
            return BasicType.V_TYPE;
        }
        return this.names[this.result].type;
    }
    
    BasicType parameterType(final int n) {
        return this.parameter(n).type;
    }
    
    Name parameter(final int n) {
        assert n < this.arity;
        final Name name = this.names[n];
        assert name.isParam();
        return name;
    }
    
    Object parameterConstraint(final int n) {
        return this.parameter(n).constraint;
    }
    
    int arity() {
        return this.arity;
    }
    
    int expressionCount() {
        return this.names.length - this.arity;
    }
    
    MethodType methodType() {
        return signatureType(this.basicTypeSignature());
    }
    
    final String basicTypeSignature() {
        final StringBuilder sb = new StringBuilder(this.arity() + 3);
        for (int i = 0; i < this.arity(); ++i) {
            sb.append(this.parameterType(i).basicTypeChar());
        }
        return sb.append('_').append(this.returnType().basicTypeChar()).toString();
    }
    
    static int signatureArity(final String s) {
        assert isValidSignature(s);
        return s.indexOf(95);
    }
    
    static BasicType signatureReturn(final String s) {
        return BasicType.basicType(s.charAt(signatureArity(s) + 1));
    }
    
    static boolean isValidSignature(final String s) {
        final int index = s.indexOf(95);
        if (index < 0) {
            return false;
        }
        final int length = s.length();
        if (length != index + 2) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (i != index) {
                final char char1 = s.charAt(i);
                if (char1 == 'V') {
                    return i == length - 1 && index == length - 2;
                }
                if (!BasicType.isArgBasicTypeChar(char1)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    static MethodType signatureType(final String s) {
        final Class[] array = new Class[signatureArity(s)];
        for (int i = 0; i < array.length; ++i) {
            array[i] = BasicType.basicType(s.charAt(i)).btClass;
        }
        return MethodType.methodType(signatureReturn(s).btClass, array);
    }
    
    public void prepare() {
        if (LambdaForm.COMPILE_THRESHOLD == 0 && !this.isCompiled) {
            this.compileToBytecode();
        }
        if (this.vmentry != null) {
            return;
        }
        this.vmentry = getPreparedForm(this.basicTypeSignature()).vmentry;
    }
    
    MemberName compileToBytecode() {
        if (this.vmentry != null && this.isCompiled) {
            return this.vmentry;
        }
        final MethodType methodType = this.methodType();
        assert !(!this.vmentry.getMethodType().basicType().equals((Object)methodType));
        try {
            this.vmentry = InvokerBytecodeGenerator.generateCustomizedCode(this, methodType);
            if (LambdaForm.TRACE_INTERPRETER) {
                traceInterpreter("compileToBytecode", this);
            }
            this.isCompiled = true;
            return this.vmentry;
        }
        catch (final Error | Exception ex) {
            throw MethodHandleStatics.newInternalError(this.toString(), (Throwable)ex);
        }
    }
    
    private static void computeInitialPreparedForms() {
        for (final MemberName vmentry : MemberName.getFactory().getMethods(LambdaForm.class, false, null, null, null)) {
            if (vmentry.isStatic()) {
                if (!vmentry.isPackage()) {
                    continue;
                }
                final MethodType methodType = vmentry.getMethodType();
                if (methodType.parameterCount() <= 0 || methodType.parameterType(0) != MethodHandle.class || !vmentry.getName().startsWith("interpret_")) {
                    continue;
                }
                final String basicTypeSignature = basicTypeSignature(methodType);
                assert vmentry.getName().equals("interpret" + basicTypeSignature.substring(basicTypeSignature.indexOf(95)));
                final LambdaForm lambdaForm = new LambdaForm(basicTypeSignature);
                lambdaForm.vmentry = vmentry;
                methodType.form().setCachedLambdaForm(6, lambdaForm);
            }
        }
    }
    
    static Object interpret_L(final MethodHandle methodHandle) throws Throwable {
        final Object[] array = { methodHandle };
        String s = null;
        assert argumentTypesMatch(s = "L_L", array);
        final Object interpretWithArguments = methodHandle.form.interpretWithArguments(array);
        assert returnTypesMatch(s, array, interpretWithArguments);
        return interpretWithArguments;
    }
    
    static Object interpret_L(final MethodHandle methodHandle, final Object o) throws Throwable {
        final Object[] array = { methodHandle, o };
        String s = null;
        assert argumentTypesMatch(s = "LL_L", array);
        final Object interpretWithArguments = methodHandle.form.interpretWithArguments(array);
        assert returnTypesMatch(s, array, interpretWithArguments);
        return interpretWithArguments;
    }
    
    static Object interpret_L(final MethodHandle methodHandle, final Object o, final Object o2) throws Throwable {
        final Object[] array = { methodHandle, o, o2 };
        String s = null;
        assert argumentTypesMatch(s = "LLL_L", array);
        final Object interpretWithArguments = methodHandle.form.interpretWithArguments(array);
        assert returnTypesMatch(s, array, interpretWithArguments);
        return interpretWithArguments;
    }
    
    private static LambdaForm getPreparedForm(final String s) {
        final MethodType signatureType = signatureType(s);
        final LambdaForm cachedLambdaForm = signatureType.form().cachedLambdaForm(6);
        if (cachedLambdaForm != null) {
            return cachedLambdaForm;
        }
        assert isValidSignature(s);
        final LambdaForm lambdaForm = new LambdaForm(s);
        lambdaForm.vmentry = InvokerBytecodeGenerator.generateLambdaFormInterpreterEntryPoint(s);
        return signatureType.form().setCachedLambdaForm(6, lambdaForm);
    }
    
    private static boolean argumentTypesMatch(final String s, final Object[] array) {
        final int signatureArity = signatureArity(s);
        assert array.length == signatureArity : "av.length == arity: av.length=" + array.length + ", arity=" + signatureArity;
        assert array[0] instanceof MethodHandle : "av[0] not instace of MethodHandle: " + array[0];
        final MethodType type = ((MethodHandle)array[0]).type();
        assert type.parameterCount() == signatureArity - 1;
        for (int i = 0; i < array.length; ++i) {
            final Class<?> clazz = (i == 0) ? MethodHandle.class : type.parameterType(i - 1);
            assert valueMatches(BasicType.basicType(s.charAt(i)), clazz, array[i]);
        }
        return true;
    }
    
    private static boolean valueMatches(BasicType v_TYPE, final Class<?> clazz, final Object o) {
        if (clazz == Void.TYPE) {
            v_TYPE = BasicType.V_TYPE;
        }
        assert v_TYPE == BasicType.basicType(clazz) : v_TYPE + " == basicType(" + clazz + ")=" + BasicType.basicType(clazz);
        switch (v_TYPE) {
            case I_TYPE: {
                assert checkInt(clazz, o) : "checkInt(" + clazz + "," + o + ")";
                break;
            }
            case J_TYPE: {
                assert o instanceof Long : "instanceof Long: " + o;
                break;
            }
            case F_TYPE: {
                assert o instanceof Float : "instanceof Float: " + o;
                break;
            }
            case D_TYPE: {
                assert o instanceof Double : "instanceof Double: " + o;
                break;
            }
            case L_TYPE: {
                assert checkRef(clazz, o) : "checkRef(" + clazz + "," + o + ")";
                break;
            }
            case V_TYPE: {
                break;
            }
            default: {
                assert false;
                break;
            }
        }
        return true;
    }
    
    private static boolean returnTypesMatch(final String s, final Object[] array, final Object o) {
        return valueMatches(signatureReturn(s), ((MethodHandle)array[0]).type().returnType(), o);
    }
    
    private static boolean checkInt(final Class<?> clazz, final Object o) {
        assert o instanceof Integer;
        if (clazz == Integer.TYPE) {
            return true;
        }
        final Wrapper forBasicType = Wrapper.forBasicType(clazz);
        assert forBasicType.isSubwordOrInt();
        return o.equals(Wrapper.INT.wrap(forBasicType.wrap(o)));
    }
    
    private static boolean checkRef(final Class<?> clazz, final Object o) {
        assert !clazz.isPrimitive();
        return o == null || clazz.isInterface() || clazz.isInstance(o);
    }
    
    @Hidden
    @DontInline
    Object interpretWithArguments(final Object... array) throws Throwable {
        if (LambdaForm.TRACE_INTERPRETER) {
            return this.interpretWithArgumentsTracing(array);
        }
        this.checkInvocationCounter();
        assert this.arityCheck(array);
        final Object[] copy = Arrays.copyOf(array, this.names.length);
        for (int i = array.length; i < copy.length; ++i) {
            copy[i] = this.interpretName(this.names[i], copy);
        }
        final Object o = (this.result < 0) ? null : copy[this.result];
        assert this.resultCheck(array, o);
        return o;
    }
    
    @Hidden
    @DontInline
    Object interpretName(final Name name, final Object[] array) throws Throwable {
        if (LambdaForm.TRACE_INTERPRETER) {
            traceInterpreter("| interpretName", name.debugString(), (Object[])null);
        }
        final Object[] copy = Arrays.copyOf(name.arguments, name.arguments.length, (Class<? extends Object[]>)Object[].class);
        for (int i = 0; i < copy.length; ++i) {
            final Object o = copy[i];
            if (o instanceof Name) {
                final int index = ((Name)o).index();
                assert this.names[index] == o;
                copy[i] = array[index];
            }
        }
        return name.function.invokeWithArguments(copy);
    }
    
    private void checkInvocationCounter() {
        if (LambdaForm.COMPILE_THRESHOLD != 0 && this.invocationCounter < LambdaForm.COMPILE_THRESHOLD) {
            ++this.invocationCounter;
            if (this.invocationCounter >= LambdaForm.COMPILE_THRESHOLD) {
                this.compileToBytecode();
            }
        }
    }
    
    Object interpretWithArgumentsTracing(final Object... array) throws Throwable {
        traceInterpreter("[ interpretWithArguments", this, array);
        if (this.invocationCounter < LambdaForm.COMPILE_THRESHOLD) {
            traceInterpreter("| invocationCounter", this.invocationCounter++);
            if (this.invocationCounter >= LambdaForm.COMPILE_THRESHOLD) {
                this.compileToBytecode();
            }
        }
        Object o;
        try {
            assert this.arityCheck(array);
            final Object[] copy = Arrays.copyOf(array, this.names.length);
            for (int i = array.length; i < copy.length; ++i) {
                copy[i] = this.interpretName(this.names[i], copy);
            }
            o = ((this.result < 0) ? null : copy[this.result]);
        }
        catch (final Throwable t) {
            traceInterpreter("] throw =>", t);
            throw t;
        }
        traceInterpreter("] return =>", o);
        return o;
    }
    
    static void traceInterpreter(final String s, final Object o, final Object... array) {
        if (LambdaForm.TRACE_INTERPRETER) {
            System.out.println("LFI: " + s + " " + ((o != null) ? o : "") + ((array != null && array.length != 0) ? Arrays.asList(array) : ""));
        }
    }
    
    static void traceInterpreter(final String s, final Object o) {
        traceInterpreter(s, o, (Object[])null);
    }
    
    private boolean arityCheck(final Object[] array) {
        assert array.length == this.arity : this.arity + "!=" + Arrays.asList(array) + ".length";
        assert array[0] instanceof MethodHandle : "not MH: " + array[0];
        final MethodHandle methodHandle = (MethodHandle)array[0];
        assert methodHandle.internalForm() == this;
        argumentTypesMatch(this.basicTypeSignature(), array);
        return true;
    }
    
    private boolean resultCheck(final Object[] array, final Object o) {
        final MethodType type = ((MethodHandle)array[0]).type();
        assert valueMatches(this.returnType(), type.returnType(), o);
        return true;
    }
    
    private boolean isEmpty() {
        if (this.result < 0) {
            return this.names.length == this.arity;
        }
        return this.result == this.arity && this.names.length == this.arity + 1 && this.names[this.arity].isConstantZero();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.debugName + "=Lambda(");
        for (int i = 0; i < this.names.length; ++i) {
            if (i == this.arity) {
                sb.append(")=>{");
            }
            final Name name = this.names[i];
            if (i >= this.arity) {
                sb.append("\n    ");
            }
            sb.append(name.paramString());
            if (i < this.arity) {
                if (i + 1 < this.arity) {
                    sb.append(",");
                }
            }
            else {
                sb.append("=").append(name.exprString());
                sb.append(";");
            }
        }
        if (this.arity == this.names.length) {
            sb.append(")=>{");
        }
        sb.append((this.result < 0) ? "void" : this.names[this.result]).append("}");
        if (LambdaForm.TRACE_INTERPRETER) {
            sb.append(":").append(this.basicTypeSignature());
            sb.append("/").append(this.vmentry);
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof LambdaForm && this.equals((LambdaForm)o);
    }
    
    public boolean equals(final LambdaForm lambdaForm) {
        return this.result == lambdaForm.result && Arrays.equals(this.names, lambdaForm.names);
    }
    
    @Override
    public int hashCode() {
        return this.result + 31 * Arrays.hashCode(this.names);
    }
    
    LambdaFormEditor editor() {
        return LambdaFormEditor.lambdaFormEditor(this);
    }
    
    boolean contains(final Name name) {
        final int index = name.index();
        if (index >= 0) {
            return index < this.names.length && name.equals(this.names[index]);
        }
        for (int i = this.arity; i < this.names.length; ++i) {
            if (name.equals(this.names[i])) {
                return true;
            }
        }
        return false;
    }
    
    LambdaForm addArguments(final int n, final BasicType... array) {
        final int n2 = n + 1;
        assert n2 <= this.arity;
        final int length = this.names.length;
        final int length2 = array.length;
        final Name[] array2 = Arrays.copyOf(this.names, length + length2);
        final int n3 = this.arity + length2;
        int result = this.result;
        if (result >= n2) {
            result += length2;
        }
        System.arraycopy(this.names, n2, array2, n2 + length2, length - n2);
        for (int i = 0; i < length2; ++i) {
            array2[n2 + i] = new Name(array[i]);
        }
        return new LambdaForm(this.debugName, n3, array2, result);
    }
    
    LambdaForm addArguments(final int n, final List<Class<?>> list) {
        return this.addArguments(n, BasicType.basicTypes(list));
    }
    
    LambdaForm permuteArguments(final int n, final int[] array, final BasicType[] array2) {
        final int length = this.names.length;
        final int length2 = array2.length;
        final int length3 = array.length;
        assert n + length3 == this.arity;
        assert permutedTypesMatch(array, array2, this.names, n);
        int n2;
        for (n2 = 0; n2 < length3 && array[n2] == n2; ++n2) {}
        final Name[] array3 = new Name[length - length3 + length2];
        System.arraycopy(this.names, 0, array3, 0, n + n2);
        final int n3 = length - this.arity;
        System.arraycopy(this.names, n + length3, array3, n + length2, n3);
        final int n4 = array3.length - n3;
        int result = this.result;
        if (result >= 0) {
            if (result < n + length3) {
                result = array[result - n];
            }
            else {
                result = result - length3 + length2;
            }
        }
        for (int i = n2; i < length3; ++i) {
            final Name name = this.names[n + i];
            final int n5 = array[i];
            Name name2 = array3[n + n5];
            if (name2 == null) {
                name2 = (array3[n + n5] = new Name(array2[n5]));
            }
            else {
                assert name2.type == array2[n5];
            }
            for (int j = n4; j < array3.length; ++j) {
                array3[j] = array3[j].replaceName(name, name2);
            }
        }
        for (int k = n + n2; k < n4; ++k) {
            if (array3[k] == null) {
                array3[k] = argument(k, array2[k - n]);
            }
        }
        for (int l = this.arity; l < this.names.length; ++l) {
            final int n6 = l - this.arity + n4;
            final Name name3 = this.names[l];
            final Name name4 = array3[n6];
            if (name3 != name4) {
                for (int n7 = n6 + 1; n7 < array3.length; ++n7) {
                    array3[n7] = array3[n7].replaceName(name3, name4);
                }
            }
        }
        return new LambdaForm(this.debugName, n4, array3, result);
    }
    
    static boolean permutedTypesMatch(final int[] array, final BasicType[] array2, final Name[] array3, final int n) {
        final int length = array2.length;
        for (int length2 = array.length, i = 0; i < length2; ++i) {
            assert array3[n + i].isParam();
            assert array3[n + i].type == array2[array[i]];
        }
        return true;
    }
    
    public static String basicTypeSignature(final MethodType methodType) {
        final char[] array = new char[methodType.parameterCount() + 2];
        int n = 0;
        final Iterator<Class<?>> iterator = methodType.parameterList().iterator();
        while (iterator.hasNext()) {
            array[n++] = BasicType.basicTypeChar(iterator.next());
        }
        array[n++] = '_';
        array[n++] = BasicType.basicTypeChar(methodType.returnType());
        assert n == array.length;
        return String.valueOf(array);
    }
    
    public static String shortenSignature(final String s) {
        int n = -1;
        int n2 = 0;
        StringBuilder append = null;
        final int length = s.length();
        if (length < 3) {
            return s;
        }
        for (int i = 0; i <= length; ++i) {
            final int n3 = n;
            n = ((i == length) ? -1 : s.charAt(i));
            if (n == n3) {
                ++n2;
            }
            else {
                int n4 = n2;
                n2 = 1;
                if (n4 < 3) {
                    if (append != null) {
                        while (--n4 >= 0) {
                            append.append((char)n3);
                        }
                    }
                }
                else {
                    if (append == null) {
                        append = new StringBuilder().append(s, 0, i - n4);
                    }
                    append.append((char)n3).append(n4);
                }
            }
        }
        return (append == null) ? s : append.toString();
    }
    
    int lastUseIndex(final Name name) {
        final short access$000 = name.index;
        final int length = this.names.length;
        assert this.names[access$000] == name;
        if (this.result == access$000) {
            return length;
        }
        int n = length;
        while (--n > access$000) {
            if (this.names[n].lastUseIndex(name) >= 0) {
                return n;
            }
        }
        return -1;
    }
    
    int useCount(final Name name) {
        name.index;
        final int length = this.names.length;
        int lastUseIndex = this.lastUseIndex(name);
        if (lastUseIndex < 0) {
            return 0;
        }
        int n = 0;
        if (lastUseIndex == length) {
            ++n;
            --lastUseIndex;
        }
        int arity = name.index() + 1;
        if (arity < this.arity) {
            arity = this.arity;
        }
        for (int i = arity; i <= lastUseIndex; ++i) {
            n += this.names[i].useCount(name);
        }
        return n;
    }
    
    static Name argument(final int n, final char c) {
        return argument(n, BasicType.basicType(c));
    }
    
    static Name argument(final int n, final BasicType basicType) {
        if (n >= 10) {
            return new Name(n, basicType);
        }
        return LambdaForm.INTERNED_ARGUMENTS[basicType.ordinal()][n];
    }
    
    static Name internArgument(final Name name) {
        assert name.isParam() : "not param: " + name;
        assert name.index < 10;
        if (name.constraint != null) {
            return name;
        }
        return argument(name.index, name.type);
    }
    
    static Name[] arguments(final int n, final String s) {
        final int length = s.length();
        final Name[] array = new Name[length + n];
        for (int i = 0; i < length; ++i) {
            array[i] = argument(i, s.charAt(i));
        }
        return array;
    }
    
    static Name[] arguments(final int n, final char... array) {
        final int length = array.length;
        final Name[] array2 = new Name[length + n];
        for (int i = 0; i < length; ++i) {
            array2[i] = argument(i, array[i]);
        }
        return array2;
    }
    
    static Name[] arguments(final int n, final List<Class<?>> list) {
        final int size = list.size();
        final Name[] array = new Name[size + n];
        for (int i = 0; i < size; ++i) {
            array[i] = argument(i, BasicType.basicType((Class<?>)list.get(i)));
        }
        return array;
    }
    
    static Name[] arguments(final int n, final Class<?>... array) {
        final int length = array.length;
        final Name[] array2 = new Name[length + n];
        for (int i = 0; i < length; ++i) {
            array2[i] = argument(i, BasicType.basicType(array[i]));
        }
        return array2;
    }
    
    static Name[] arguments(final int n, final MethodType methodType) {
        final int parameterCount = methodType.parameterCount();
        final Name[] array = new Name[parameterCount + n];
        for (int i = 0; i < parameterCount; ++i) {
            array[i] = argument(i, BasicType.basicType(methodType.parameterType(i)));
        }
        return array;
    }
    
    static LambdaForm identityForm(final BasicType basicType) {
        return LambdaForm.LF_identityForm[basicType.ordinal()];
    }
    
    static LambdaForm zeroForm(final BasicType basicType) {
        return LambdaForm.LF_zeroForm[basicType.ordinal()];
    }
    
    static NamedFunction identity(final BasicType basicType) {
        return LambdaForm.NF_identity[basicType.ordinal()];
    }
    
    static NamedFunction constantZero(final BasicType basicType) {
        return LambdaForm.NF_zero[basicType.ordinal()];
    }
    
    private static void createIdentityForms() {
        for (final BasicType basicType : BasicType.ALL_TYPES) {
            final int ordinal = basicType.ordinal();
            final char basicTypeChar = basicType.basicTypeChar();
            final boolean b = basicType == BasicType.V_TYPE;
            final Class access$100 = basicType.btClass;
            final MethodType methodType = MethodType.methodType(access$100);
            final MemberName memberName = new MemberName(LambdaForm.class, "identity_" + basicTypeChar, b ? methodType : methodType.appendParameterTypes(access$100), (byte)6);
            final MemberName memberName2 = new MemberName(LambdaForm.class, "zero_" + basicTypeChar, methodType, (byte)6);
            MemberName resolveOrFail;
            MemberName resolveOrFail2;
            try {
                resolveOrFail = LambdaForm.IMPL_NAMES.resolveOrFail((byte)6, memberName2, null, NoSuchMethodException.class);
                resolveOrFail2 = LambdaForm.IMPL_NAMES.resolveOrFail((byte)6, memberName, null, NoSuchMethodException.class);
            }
            catch (final IllegalAccessException | NoSuchMethodException ex) {
                throw MethodHandleStatics.newInternalError((Throwable)ex);
            }
            final NamedFunction namedFunction = new NamedFunction(resolveOrFail2);
            LambdaForm lambdaForm;
            if (b) {
                lambdaForm = new LambdaForm(resolveOrFail2.getName(), 1, new Name[] { argument(0, BasicType.L_TYPE) }, -1);
            }
            else {
                lambdaForm = new LambdaForm(resolveOrFail2.getName(), 2, new Name[] { argument(0, BasicType.L_TYPE), argument(1, basicType) }, 1);
            }
            LambdaForm.LF_identityForm[ordinal] = lambdaForm;
            LambdaForm.NF_identity[ordinal] = namedFunction;
            final NamedFunction namedFunction2 = new NamedFunction(resolveOrFail);
            LambdaForm lambdaForm2;
            if (b) {
                lambdaForm2 = lambdaForm;
            }
            else {
                lambdaForm2 = new LambdaForm(resolveOrFail.getName(), 1, new Name[] { argument(0, BasicType.L_TYPE), new Name(namedFunction, new Object[] { Wrapper.forBasicType(basicTypeChar).zero() }) }, 1);
            }
            LambdaForm.LF_zeroForm[ordinal] = lambdaForm2;
            LambdaForm.NF_zero[ordinal] = namedFunction2;
            assert namedFunction.isIdentity();
            assert namedFunction2.isConstantZero();
            assert new Name(namedFunction2, new Object[0]).isConstantZero();
        }
        final BasicType[] all_TYPES2 = BasicType.ALL_TYPES;
        for (int length2 = all_TYPES2.length, j = 0; j < length2; ++j) {
            final int ordinal2 = all_TYPES2[j].ordinal();
            final NamedFunction namedFunction3 = LambdaForm.NF_identity[ordinal2];
            namedFunction3.resolvedHandle = SimpleMethodHandle.make(namedFunction3.member.getInvocationType(), LambdaForm.LF_identityForm[ordinal2]);
            final NamedFunction namedFunction4 = LambdaForm.NF_zero[ordinal2];
            namedFunction4.resolvedHandle = SimpleMethodHandle.make(namedFunction4.member.getInvocationType(), LambdaForm.LF_zeroForm[ordinal2]);
            assert namedFunction3.isIdentity();
            assert namedFunction4.isConstantZero();
            assert new Name(namedFunction4, new Object[0]).isConstantZero();
        }
    }
    
    private static int identity_I(final int n) {
        return n;
    }
    
    private static long identity_J(final long n) {
        return n;
    }
    
    private static float identity_F(final float n) {
        return n;
    }
    
    private static double identity_D(final double n) {
        return n;
    }
    
    private static Object identity_L(final Object o) {
        return o;
    }
    
    private static void identity_V() {
    }
    
    private static int zero_I() {
        return 0;
    }
    
    private static long zero_J() {
        return 0L;
    }
    
    private static float zero_F() {
        return 0.0f;
    }
    
    private static double zero_D() {
        return 0.0;
    }
    
    private static Object zero_L() {
        return null;
    }
    
    private static void zero_V() {
    }
    
    static {
        COMPILE_THRESHOLD = Math.max(-1, MethodHandleStatics.COMPILE_THRESHOLD);
        INTERNED_ARGUMENTS = new Name[BasicType.ARG_TYPE_LIMIT][10];
        for (final BasicType basicType : BasicType.ARG_TYPES) {
            for (int ordinal = basicType.ordinal(), j = 0; j < LambdaForm.INTERNED_ARGUMENTS[ordinal].length; ++j) {
                LambdaForm.INTERNED_ARGUMENTS[ordinal][j] = new Name(j, basicType);
            }
        }
        IMPL_NAMES = MemberName.getFactory();
        LF_identityForm = new LambdaForm[BasicType.TYPE_LIMIT];
        LF_zeroForm = new LambdaForm[BasicType.TYPE_LIMIT];
        NF_identity = new NamedFunction[BasicType.TYPE_LIMIT];
        NF_zero = new NamedFunction[BasicType.TYPE_LIMIT];
        if (MethodHandleStatics.debugEnabled()) {
            DEBUG_NAME_COUNTERS = new HashMap<String, Integer>();
        }
        else {
            DEBUG_NAME_COUNTERS = null;
        }
        createIdentityForms();
        computeInitialPreparedForms();
        NamedFunction.initializeInvokers();
        TRACE_INTERPRETER = MethodHandleStatics.TRACE_INTERPRETER;
    }
    
    enum BasicType
    {
        L_TYPE('L', (Class<?>)Object.class, Wrapper.OBJECT), 
        I_TYPE('I', (Class<?>)Integer.TYPE, Wrapper.INT), 
        J_TYPE('J', (Class<?>)Long.TYPE, Wrapper.LONG), 
        F_TYPE('F', (Class<?>)Float.TYPE, Wrapper.FLOAT), 
        D_TYPE('D', (Class<?>)Double.TYPE, Wrapper.DOUBLE), 
        V_TYPE('V', (Class<?>)Void.TYPE, Wrapper.VOID);
        
        static final BasicType[] ALL_TYPES;
        static final BasicType[] ARG_TYPES;
        static final int ARG_TYPE_LIMIT;
        static final int TYPE_LIMIT;
        private final char btChar;
        private final Class<?> btClass;
        private final Wrapper btWrapper;
        
        private BasicType(final char btChar, final Class<?> btClass, final Wrapper btWrapper) {
            this.btChar = btChar;
            this.btClass = btClass;
            this.btWrapper = btWrapper;
        }
        
        char basicTypeChar() {
            return this.btChar;
        }
        
        Class<?> basicTypeClass() {
            return this.btClass;
        }
        
        Wrapper basicTypeWrapper() {
            return this.btWrapper;
        }
        
        int basicTypeSlots() {
            return this.btWrapper.stackSlots();
        }
        
        static BasicType basicType(final byte b) {
            return BasicType.ALL_TYPES[b];
        }
        
        static BasicType basicType(final char c) {
            switch (c) {
                case 'L': {
                    return BasicType.L_TYPE;
                }
                case 'I': {
                    return BasicType.I_TYPE;
                }
                case 'J': {
                    return BasicType.J_TYPE;
                }
                case 'F': {
                    return BasicType.F_TYPE;
                }
                case 'D': {
                    return BasicType.D_TYPE;
                }
                case 'V': {
                    return BasicType.V_TYPE;
                }
                case 'B':
                case 'C':
                case 'S':
                case 'Z': {
                    return BasicType.I_TYPE;
                }
                default: {
                    throw MethodHandleStatics.newInternalError("Unknown type char: '" + c + "'");
                }
            }
        }
        
        static BasicType basicType(final Wrapper wrapper) {
            return basicType(wrapper.basicTypeChar());
        }
        
        static BasicType basicType(final Class<?> clazz) {
            if (!clazz.isPrimitive()) {
                return BasicType.L_TYPE;
            }
            return basicType(Wrapper.forPrimitiveType(clazz));
        }
        
        static char basicTypeChar(final Class<?> clazz) {
            return basicType(clazz).btChar;
        }
        
        static BasicType[] basicTypes(final List<Class<?>> list) {
            final BasicType[] array = new BasicType[list.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = basicType((Class<?>)list.get(i));
            }
            return array;
        }
        
        static BasicType[] basicTypes(final String s) {
            final BasicType[] array = new BasicType[s.length()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = basicType(s.charAt(i));
            }
            return array;
        }
        
        static byte[] basicTypesOrd(final BasicType[] array) {
            final byte[] array2 = new byte[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = (byte)array[i].ordinal();
            }
            return array2;
        }
        
        static boolean isBasicTypeChar(final char c) {
            return "LIJFDV".indexOf(c) >= 0;
        }
        
        static boolean isArgBasicTypeChar(final char c) {
            return "LIJFD".indexOf(c) >= 0;
        }
        
        private static boolean checkBasicType() {
            for (int i = 0; i < BasicType.ARG_TYPE_LIMIT; ++i) {
                assert BasicType.ARG_TYPES[i].ordinal() == i;
                assert BasicType.ARG_TYPES[i] == BasicType.ALL_TYPES[i];
            }
            for (int j = 0; j < BasicType.TYPE_LIMIT; ++j) {
                assert BasicType.ALL_TYPES[j].ordinal() == j;
            }
            assert BasicType.ALL_TYPES[BasicType.TYPE_LIMIT - 1] == BasicType.V_TYPE;
            assert !Arrays.asList(BasicType.ARG_TYPES).contains(BasicType.V_TYPE);
            return true;
        }
        
        static {
            ALL_TYPES = values();
            ARG_TYPES = Arrays.copyOf(BasicType.ALL_TYPES, BasicType.ALL_TYPES.length - 1);
            ARG_TYPE_LIMIT = BasicType.ARG_TYPES.length;
            TYPE_LIMIT = BasicType.ALL_TYPES.length;
            assert checkBasicType();
        }
    }
    
    static class NamedFunction
    {
        final MemberName member;
        @Stable
        MethodHandle resolvedHandle;
        @Stable
        MethodHandle invoker;
        static final MethodType INVOKER_METHOD_TYPE;
        
        NamedFunction(final MethodHandle methodHandle) {
            this(methodHandle.internalMemberName(), methodHandle);
        }
        
        NamedFunction(final MemberName member, final MethodHandle resolvedHandle) {
            this.member = member;
            this.resolvedHandle = resolvedHandle;
        }
        
        NamedFunction(final MethodType methodType) {
            assert methodType == methodType.basicType() : methodType;
            if (methodType.parameterSlotCount() < 253) {
                this.resolvedHandle = methodType.invokers().basicInvoker();
                this.member = this.resolvedHandle.internalMemberName();
            }
            else {
                this.member = Invokers.invokeBasicMethod(methodType);
            }
            assert isInvokeBasic(this.member);
        }
        
        private static boolean isInvokeBasic(final MemberName memberName) {
            return memberName != null && memberName.getDeclaringClass() == MethodHandle.class && "invokeBasic".equals(memberName.getName());
        }
        
        NamedFunction(final Method method) {
            this(new MemberName(method));
        }
        
        NamedFunction(final Field field) {
            this(new MemberName(field));
        }
        
        NamedFunction(final MemberName member) {
            this.member = member;
            this.resolvedHandle = null;
        }
        
        MethodHandle resolvedHandle() {
            if (this.resolvedHandle == null) {
                this.resolve();
            }
            return this.resolvedHandle;
        }
        
        void resolve() {
            this.resolvedHandle = DirectMethodHandle.make(this.member);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!(o instanceof NamedFunction)) {
                return false;
            }
            final NamedFunction namedFunction = (NamedFunction)o;
            return this.member != null && this.member.equals(namedFunction.member);
        }
        
        @Override
        public int hashCode() {
            if (this.member != null) {
                return this.member.hashCode();
            }
            return super.hashCode();
        }
        
        static void initializeInvokers() {
            for (final MemberName memberName : MemberName.getFactory().getMethods(NamedFunction.class, false, null, null, null)) {
                if (memberName.isStatic()) {
                    if (!memberName.isPackage()) {
                        continue;
                    }
                    if (!memberName.getMethodType().equals((Object)NamedFunction.INVOKER_METHOD_TYPE) || !memberName.getName().startsWith("invoke_")) {
                        continue;
                    }
                    final String substring = memberName.getName().substring("invoke_".length());
                    MethodType methodType = MethodType.genericMethodType(LambdaForm.signatureArity(substring));
                    if (LambdaForm.signatureReturn(substring) == BasicType.V_TYPE) {
                        methodType = methodType.changeReturnType(Void.TYPE);
                    }
                    methodType.form().setCachedMethodHandle(1, DirectMethodHandle.make(memberName));
                }
            }
        }
        
        @Hidden
        static Object invoke__V(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(0, Void.TYPE, methodHandle, array);
            methodHandle.invokeBasic();
            return null;
        }
        
        @Hidden
        static Object invoke_L_V(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(1, Void.TYPE, methodHandle, array);
            methodHandle.invokeBasic(array[0]);
            return null;
        }
        
        @Hidden
        static Object invoke_LL_V(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(2, Void.TYPE, methodHandle, array);
            methodHandle.invokeBasic(array[0], array[1]);
            return null;
        }
        
        @Hidden
        static Object invoke_LLL_V(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(3, Void.TYPE, methodHandle, array);
            methodHandle.invokeBasic(array[0], array[1], array[2]);
            return null;
        }
        
        @Hidden
        static Object invoke_LLLL_V(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(4, Void.TYPE, methodHandle, array);
            methodHandle.invokeBasic(array[0], array[1], array[2], array[3]);
            return null;
        }
        
        @Hidden
        static Object invoke_LLLLL_V(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(5, Void.TYPE, methodHandle, array);
            methodHandle.invokeBasic(array[0], array[1], array[2], array[3], array[4]);
            return null;
        }
        
        @Hidden
        static Object invoke__L(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(0, methodHandle, array);
            return methodHandle.invokeBasic();
        }
        
        @Hidden
        static Object invoke_L_L(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(1, methodHandle, array);
            return methodHandle.invokeBasic(array[0]);
        }
        
        @Hidden
        static Object invoke_LL_L(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(2, methodHandle, array);
            return methodHandle.invokeBasic(array[0], array[1]);
        }
        
        @Hidden
        static Object invoke_LLL_L(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(3, methodHandle, array);
            return methodHandle.invokeBasic(array[0], array[1], array[2]);
        }
        
        @Hidden
        static Object invoke_LLLL_L(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(4, methodHandle, array);
            return methodHandle.invokeBasic(array[0], array[1], array[2], array[3]);
        }
        
        @Hidden
        static Object invoke_LLLLL_L(final MethodHandle methodHandle, final Object[] array) throws Throwable {
            assert arityCheck(5, methodHandle, array);
            return methodHandle.invokeBasic(array[0], array[1], array[2], array[3], array[4]);
        }
        
        private static boolean arityCheck(final int n, final MethodHandle methodHandle, final Object[] array) {
            return arityCheck(n, Object.class, methodHandle, array);
        }
        
        private static boolean arityCheck(final int n, final Class<?> clazz, final MethodHandle methodHandle, final Object[] array) {
            assert array.length == n : Arrays.asList(array.length, n);
            assert methodHandle.type().basicType() == MethodType.genericMethodType(n).changeReturnType(clazz) : Arrays.asList(methodHandle, clazz, n);
            final MemberName internalMemberName = methodHandle.internalMemberName();
            if (isInvokeBasic(internalMemberName)) {
                assert n > 0;
                assert array[0] instanceof MethodHandle;
                final MethodHandle methodHandle2 = (MethodHandle)array[0];
                assert methodHandle2.type().basicType() == MethodType.genericMethodType(n - 1).changeReturnType(clazz) : Arrays.asList(internalMemberName, methodHandle2, clazz, n);
            }
            return true;
        }
        
        private static MethodHandle computeInvoker(MethodTypeForm form) {
            form = form.basicType().form();
            final MethodHandle cachedMethodHandle = form.cachedMethodHandle(1);
            if (cachedMethodHandle != null) {
                return cachedMethodHandle;
            }
            final DirectMethodHandle make = DirectMethodHandle.make(InvokerBytecodeGenerator.generateNamedFunctionInvoker(form));
            final MethodHandle cachedMethodHandle2 = form.cachedMethodHandle(1);
            if (cachedMethodHandle2 != null) {
                return cachedMethodHandle2;
            }
            if (!make.type().equals((Object)NamedFunction.INVOKER_METHOD_TYPE)) {
                throw MethodHandleStatics.newInternalError(make.debugString());
            }
            return form.setCachedMethodHandle(1, make);
        }
        
        @Hidden
        Object invokeWithArguments(final Object... array) throws Throwable {
            if (LambdaForm.TRACE_INTERPRETER) {
                return this.invokeWithArgumentsTracing(array);
            }
            assert checkArgumentTypes(array, this.methodType());
            return this.invoker().invokeBasic(this.resolvedHandle(), array);
        }
        
        @Hidden
        Object invokeWithArgumentsTracing(final Object[] array) throws Throwable {
            Object invokeBasic;
            try {
                LambdaForm.traceInterpreter("[ call", this, array);
                if (this.invoker == null) {
                    LambdaForm.traceInterpreter("| getInvoker", this);
                    this.invoker();
                }
                if (this.resolvedHandle == null) {
                    LambdaForm.traceInterpreter("| resolve", this);
                    this.resolvedHandle();
                }
                assert checkArgumentTypes(array, this.methodType());
                invokeBasic = this.invoker().invokeBasic(this.resolvedHandle(), array);
            }
            catch (final Throwable t) {
                LambdaForm.traceInterpreter("] throw =>", t);
                throw t;
            }
            LambdaForm.traceInterpreter("] return =>", invokeBasic);
            return invokeBasic;
        }
        
        private MethodHandle invoker() {
            if (this.invoker != null) {
                return this.invoker;
            }
            return this.invoker = computeInvoker(this.methodType().form());
        }
        
        private static boolean checkArgumentTypes(final Object[] array, final MethodType methodType) {
            return true;
        }
        
        MethodType methodType() {
            if (this.resolvedHandle != null) {
                return this.resolvedHandle.type();
            }
            return this.member.getInvocationType();
        }
        
        MemberName member() {
            assert this.assertMemberIsConsistent();
            return this.member;
        }
        
        private boolean assertMemberIsConsistent() {
            if (this.resolvedHandle instanceof DirectMethodHandle) {
                final MemberName internalMemberName = this.resolvedHandle.internalMemberName();
                assert internalMemberName.equals(this.member);
            }
            return true;
        }
        
        Class<?> memberDeclaringClassOrNull() {
            return (this.member == null) ? null : this.member.getDeclaringClass();
        }
        
        BasicType returnType() {
            return BasicType.basicType(this.methodType().returnType());
        }
        
        BasicType parameterType(final int n) {
            return BasicType.basicType(this.methodType().parameterType(n));
        }
        
        int arity() {
            return this.methodType().parameterCount();
        }
        
        @Override
        public String toString() {
            if (this.member == null) {
                return String.valueOf(this.resolvedHandle);
            }
            return this.member.getDeclaringClass().getSimpleName() + "." + this.member.getName();
        }
        
        public boolean isIdentity() {
            return this.equals(LambdaForm.identity(this.returnType()));
        }
        
        public boolean isConstantZero() {
            return this.equals(LambdaForm.constantZero(this.returnType()));
        }
        
        public MethodHandleImpl.Intrinsic intrinsicName() {
            return (this.resolvedHandle == null) ? MethodHandleImpl.Intrinsic.NONE : this.resolvedHandle.intrinsicName();
        }
        
        static {
            INVOKER_METHOD_TYPE = MethodType.methodType(Object.class, MethodHandle.class, Object[].class);
        }
    }
    
    static final class Name
    {
        final BasicType type;
        private short index;
        final NamedFunction function;
        final Object constraint;
        @Stable
        final Object[] arguments;
        
        private Name(final int n, final BasicType type, final NamedFunction function, final Object[] arguments) {
            this.index = (short)n;
            this.type = type;
            this.function = function;
            this.arguments = arguments;
            this.constraint = null;
            assert this.index == n;
        }
        
        private Name(final Name name, final Object constraint) {
            this.index = name.index;
            this.type = name.type;
            this.function = name.function;
            this.arguments = name.arguments;
            this.constraint = constraint;
            assert !(!this.isParam());
            assert !(!(constraint instanceof Class));
        }
        
        Name(final MethodHandle methodHandle, final Object... array) {
            this(new NamedFunction(methodHandle), array);
        }
        
        Name(final MethodType methodType, final Object... array) {
            this(new NamedFunction(methodType), array);
            assert array[0] instanceof Name && ((Name)array[0]).type == BasicType.L_TYPE;
        }
        
        Name(final MemberName memberName, final Object... array) {
            this(new NamedFunction(memberName), array);
        }
        
        Name(final NamedFunction namedFunction, Object... copy) {
            this(-1, namedFunction.returnType(), namedFunction, copy = Arrays.copyOf(copy, copy.length, (Class<? extends Object[]>)Object[].class));
            assert copy.length == namedFunction.arity() : "arity mismatch: arguments.length=" + copy.length + " == function.arity()=" + namedFunction.arity() + " in " + this.debugString();
            for (int i = 0; i < copy.length; ++i) {
                assert typesMatch(namedFunction.parameterType(i), copy[i]) : "types don't match: function.parameterType(" + i + ")=" + namedFunction.parameterType(i) + ", arguments[" + i + "]=" + copy[i] + " in " + this.debugString();
            }
        }
        
        Name(final int n, final BasicType basicType) {
            this(n, basicType, null, null);
        }
        
        Name(final BasicType basicType) {
            this(-1, basicType);
        }
        
        BasicType type() {
            return this.type;
        }
        
        int index() {
            return this.index;
        }
        
        boolean initIndex(final int n) {
            if (this.index != n) {
                if (this.index != -1) {
                    return false;
                }
                this.index = (short)n;
            }
            return true;
        }
        
        char typeChar() {
            return this.type.btChar;
        }
        
        void resolve() {
            if (this.function != null) {
                this.function.resolve();
            }
        }
        
        Name newIndex(final int n) {
            if (this.initIndex(n)) {
                return this;
            }
            return this.cloneWithIndex(n);
        }
        
        Name cloneWithIndex(final int n) {
            return new Name(n, this.type, this.function, (Object[])((this.arguments == null) ? null : ((Object[])this.arguments.clone()))).withConstraint(this.constraint);
        }
        
        Name withConstraint(final Object o) {
            if (o == this.constraint) {
                return this;
            }
            return new Name(this, o);
        }
        
        Name replaceName(final Name name, final Name name2) {
            if (name == name2) {
                return this;
            }
            Object[] arguments = this.arguments;
            if (arguments == null) {
                return this;
            }
            int n = 0;
            for (int i = 0; i < arguments.length; ++i) {
                if (arguments[i] == name) {
                    if (n == 0) {
                        n = 1;
                        arguments = arguments.clone();
                    }
                    arguments[i] = name2;
                }
            }
            if (n == 0) {
                return this;
            }
            return new Name(this.function, arguments);
        }
        
        Name replaceNames(final Name[] array, final Name[] array2, final int n, final int n2) {
            if (n >= n2) {
                return this;
            }
            Object[] arguments = this.arguments;
            int n3 = 0;
            for (int i = 0; i < arguments.length; ++i) {
                if (arguments[i] instanceof Name) {
                    final Name name = (Name)arguments[i];
                    final short index = name.index;
                    if (index < 0 || index >= array2.length || name != array2[index]) {
                        int j = n;
                        while (j < n2) {
                            if (name == array[j]) {
                                if (name == array2[j]) {
                                    break;
                                }
                                if (n3 == 0) {
                                    n3 = 1;
                                    arguments = arguments.clone();
                                }
                                arguments[i] = array2[j];
                                break;
                            }
                            else {
                                ++j;
                            }
                        }
                    }
                }
            }
            if (n3 == 0) {
                return this;
            }
            return new Name(this.function, arguments);
        }
        
        void internArguments() {
            final Object[] arguments = this.arguments;
            for (int i = 0; i < arguments.length; ++i) {
                if (arguments[i] instanceof Name) {
                    final Name name = (Name)arguments[i];
                    if (name.isParam() && name.index < 10) {
                        arguments[i] = LambdaForm.internArgument(name);
                    }
                }
            }
        }
        
        boolean isParam() {
            return this.function == null;
        }
        
        boolean isConstantZero() {
            return !this.isParam() && this.arguments.length == 0 && this.function.isConstantZero();
        }
        
        @Override
        public String toString() {
            return (this.isParam() ? "a" : "t") + ((this.index >= 0) ? this.index : System.identityHashCode(this)) + ":" + this.typeChar();
        }
        
        public String debugString() {
            final String paramString = this.paramString();
            return (this.function == null) ? paramString : (paramString + "=" + this.exprString());
        }
        
        public String paramString() {
            final String string = this.toString();
            Object o = this.constraint;
            if (o == null) {
                return string;
            }
            if (o instanceof Class) {
                o = ((Class)o).getSimpleName();
            }
            return string + "/" + o;
        }
        
        public String exprString() {
            if (this.function == null) {
                return this.toString();
            }
            final StringBuilder sb = new StringBuilder(this.function.toString());
            sb.append("(");
            String s = "";
            for (final Object o : this.arguments) {
                sb.append(s);
                s = ",";
                if (o instanceof Name || o instanceof Integer) {
                    sb.append(o);
                }
                else {
                    sb.append("(").append(o).append(")");
                }
            }
            sb.append(")");
            return sb.toString();
        }
        
        static boolean typesMatch(final BasicType basicType, final Object o) {
            if (o instanceof Name) {
                return ((Name)o).type == basicType;
            }
            switch (basicType) {
                case I_TYPE: {
                    return o instanceof Integer;
                }
                case J_TYPE: {
                    return o instanceof Long;
                }
                case F_TYPE: {
                    return o instanceof Float;
                }
                case D_TYPE: {
                    return o instanceof Double;
                }
                default: {
                    assert basicType == BasicType.L_TYPE;
                    return true;
                }
            }
        }
        
        int lastUseIndex(final Name name) {
            if (this.arguments == null) {
                return -1;
            }
            int length = this.arguments.length;
            while (--length >= 0) {
                if (this.arguments[length] == name) {
                    return length;
                }
            }
            return -1;
        }
        
        int useCount(final Name name) {
            if (this.arguments == null) {
                return 0;
            }
            int n = 0;
            int length = this.arguments.length;
            while (--length >= 0) {
                if (this.arguments[length] == name) {
                    ++n;
                }
            }
            return n;
        }
        
        boolean contains(final Name name) {
            return this == name || this.lastUseIndex(name) >= 0;
        }
        
        public boolean equals(final Name name) {
            return this == name || (!this.isParam() && this.type == name.type && this.function.equals(name.function) && Arrays.equals(this.arguments, name.arguments));
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Name && this.equals((Name)o);
        }
        
        @Override
        public int hashCode() {
            if (this.isParam()) {
                return this.index | this.type.ordinal() << 8;
            }
            return this.function.hashCode() ^ Arrays.hashCode(this.arguments);
        }
    }
    
    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @interface Hidden {
    }
    
    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @interface Compiled {
    }
}
