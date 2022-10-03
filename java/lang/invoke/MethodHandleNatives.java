package java.lang.invoke;

import java.util.Arrays;

class MethodHandleNatives
{
    static final boolean COUNT_GWT;
    
    private MethodHandleNatives() {
    }
    
    static native void init(final MemberName p0, final Object p1);
    
    static native void expand(final MemberName p0);
    
    static native MemberName resolve(final MemberName p0, final Class<?> p1) throws LinkageError, ClassNotFoundException;
    
    static native int getMembers(final Class<?> p0, final String p1, final String p2, final int p3, final Class<?> p4, final int p5, final MemberName[] p6);
    
    static native long objectFieldOffset(final MemberName p0);
    
    static native long staticFieldOffset(final MemberName p0);
    
    static native Object staticFieldBase(final MemberName p0);
    
    static native Object getMemberVMInfo(final MemberName p0);
    
    static native int getConstant(final int p0);
    
    static native void setCallSiteTargetNormal(final CallSite p0, final MethodHandle p1);
    
    static native void setCallSiteTargetVolatile(final CallSite p0, final MethodHandle p1);
    
    private static native void registerNatives();
    
    static boolean refKindIsValid(final int n) {
        return n > 0 && n < 10;
    }
    
    static boolean refKindIsField(final byte b) {
        assert refKindIsValid(b);
        return b <= 4;
    }
    
    static boolean refKindIsGetter(final byte b) {
        assert refKindIsValid(b);
        return b <= 2;
    }
    
    static boolean refKindIsSetter(final byte b) {
        return refKindIsField(b) && !refKindIsGetter(b);
    }
    
    static boolean refKindIsMethod(final byte b) {
        return !refKindIsField(b) && b != 8;
    }
    
    static boolean refKindIsConstructor(final byte b) {
        return b == 8;
    }
    
    static boolean refKindHasReceiver(final byte b) {
        assert refKindIsValid(b);
        return (b & 0x1) != 0x0;
    }
    
    static boolean refKindIsStatic(final byte b) {
        return !refKindHasReceiver(b) && b != 8;
    }
    
    static boolean refKindDoesDispatch(final byte b) {
        assert refKindIsValid(b);
        return b == 5 || b == 9;
    }
    
    static String refKindName(final byte b) {
        assert refKindIsValid(b);
        switch (b) {
            case 1: {
                return "getField";
            }
            case 2: {
                return "getStatic";
            }
            case 3: {
                return "putField";
            }
            case 4: {
                return "putStatic";
            }
            case 5: {
                return "invokeVirtual";
            }
            case 6: {
                return "invokeStatic";
            }
            case 7: {
                return "invokeSpecial";
            }
            case 8: {
                return "newInvokeSpecial";
            }
            case 9: {
                return "invokeInterface";
            }
            default: {
                return "REF_???";
            }
        }
    }
    
    private static native int getNamedCon(final int p0, final Object[] p1);
    
    static boolean verifyConstants() {
        final Object[] array = { null };
        int n = 0;
        while (true) {
            array[0] = null;
            final int namedCon = getNamedCon(n, array);
            if (array[0] == null) {
                break;
            }
            final String s = (String)array[0];
            try {
                final int int1 = Constants.class.getDeclaredField(s).getInt(null);
                if (int1 != namedCon) {
                    final String string = s + ": JVM has " + namedCon + " while Java has " + int1;
                    if (!s.equals("CONV_OP_LIMIT")) {
                        throw new InternalError(string);
                    }
                    System.err.println("warning: " + string);
                }
            }
            catch (final NoSuchFieldException | IllegalAccessException ex) {
                new StringBuilder().append(s).append(": JVM has ").append(namedCon).append(" which Java does not define").toString();
            }
            ++n;
        }
        return true;
    }
    
    static MemberName linkCallSite(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object[] array) {
        final MethodHandle methodHandle = (MethodHandle)o2;
        final Class clazz = (Class)o;
        final String intern = o3.toString().intern();
        final MethodType methodType = (MethodType)o4;
        if (!MethodHandleStatics.TRACE_METHOD_LINKAGE) {
            return linkCallSiteImpl(clazz, methodHandle, intern, methodType, o5, array);
        }
        return linkCallSiteTracing(clazz, methodHandle, intern, methodType, o5, array);
    }
    
    static MemberName linkCallSiteImpl(final Class<?> clazz, final MethodHandle methodHandle, final String s, final MethodType methodType, final Object o, final Object[] array) {
        final CallSite site = CallSite.makeSite(methodHandle, s, methodType, o, clazz);
        if (site instanceof ConstantCallSite) {
            array[0] = site.dynamicInvoker();
            return Invokers.linkToTargetMethod(methodType);
        }
        array[0] = site;
        return Invokers.linkToCallSiteMethod(methodType);
    }
    
    static MemberName linkCallSiteTracing(final Class<?> clazz, final MethodHandle methodHandle, final String s, final MethodType methodType, final Object o, final Object[] array) {
        Object internalMemberName = methodHandle.internalMemberName();
        if (internalMemberName == null) {
            internalMemberName = methodHandle;
        }
        System.out.println("linkCallSite " + clazz.getName() + " " + internalMemberName + " " + s + methodType + "/" + ((o instanceof Object[]) ? Arrays.asList((Object[])o) : o));
        try {
            final MemberName linkCallSiteImpl = linkCallSiteImpl(clazz, methodHandle, s, methodType, o, array);
            System.out.println("linkCallSite => " + linkCallSiteImpl + " + " + array[0]);
            return linkCallSiteImpl;
        }
        catch (final Throwable t) {
            System.out.println("linkCallSite => throw " + t);
            throw t;
        }
    }
    
    static MethodType findMethodHandleType(final Class<?> clazz, final Class<?>[] array) {
        return MethodType.makeImpl(clazz, array, true);
    }
    
    static MemberName linkMethod(final Class<?> clazz, final int n, final Class<?> clazz2, final String s, final Object o, final Object[] array) {
        if (!MethodHandleStatics.TRACE_METHOD_LINKAGE) {
            return linkMethodImpl(clazz, n, clazz2, s, o, array);
        }
        return linkMethodTracing(clazz, n, clazz2, s, o, array);
    }
    
    static MemberName linkMethodImpl(final Class<?> clazz, final int n, final Class<?> clazz2, final String s, final Object o, final Object[] array) {
        try {
            if (clazz2 == MethodHandle.class && n == 5) {
                return Invokers.methodHandleInvokeLinkerMethod(s, fixMethodType(clazz, o), array);
            }
        }
        catch (final Throwable t) {
            if (t instanceof LinkageError) {
                throw (LinkageError)t;
            }
            throw new LinkageError(t.getMessage(), t);
        }
        throw new LinkageError("no such method " + clazz2.getName() + "." + s + o);
    }
    
    private static MethodType fixMethodType(final Class<?> clazz, final Object o) {
        if (o instanceof MethodType) {
            return (MethodType)o;
        }
        return MethodType.fromMethodDescriptorString((String)o, clazz.getClassLoader());
    }
    
    static MemberName linkMethodTracing(final Class<?> clazz, final int n, final Class<?> clazz2, final String s, final Object o, final Object[] array) {
        System.out.println("linkMethod " + clazz2.getName() + "." + s + o + "/" + Integer.toHexString(n));
        try {
            final MemberName linkMethodImpl = linkMethodImpl(clazz, n, clazz2, s, o, array);
            System.out.println("linkMethod => " + linkMethodImpl + " + " + array[0]);
            return linkMethodImpl;
        }
        catch (final Throwable t) {
            System.out.println("linkMethod => throw " + t);
            throw t;
        }
    }
    
    static MethodHandle linkMethodHandleConstant(final Class<?> clazz, final int n, final Class<?> clazz2, final String s, final Object o) {
        try {
            final MethodHandles.Lookup in = MethodHandles.Lookup.IMPL_LOOKUP.in(clazz);
            assert refKindIsValid(n);
            return in.linkMethodHandleConstant((byte)n, clazz2, s, o);
        }
        catch (final IllegalAccessException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof AbstractMethodError) {
                throw (AbstractMethodError)cause;
            }
            throw initCauseFrom(new IllegalAccessError(ex.getMessage()), ex);
        }
        catch (final NoSuchMethodException ex2) {
            throw initCauseFrom(new NoSuchMethodError(ex2.getMessage()), ex2);
        }
        catch (final NoSuchFieldException ex3) {
            throw initCauseFrom(new NoSuchFieldError(ex3.getMessage()), ex3);
        }
        catch (final ReflectiveOperationException ex4) {
            throw initCauseFrom(new IncompatibleClassChangeError(), ex4);
        }
    }
    
    private static Error initCauseFrom(final Error error, final Exception ex) {
        final Throwable cause = ex.getCause();
        if (error.getClass().isInstance(cause)) {
            return (Error)cause;
        }
        error.initCause((cause == null) ? ex : cause);
        return error;
    }
    
    static boolean isCallerSensitive(final MemberName memberName) {
        return memberName.isInvocable() && (memberName.isCallerSensitive() || canBeCalledVirtual(memberName));
    }
    
    static boolean canBeCalledVirtual(final MemberName memberName) {
        assert memberName.isInvocable();
        memberName.getDeclaringClass();
        final String name = memberName.getName();
        switch (name) {
            case "checkMemberAccess": {
                return canBeCalledVirtual(memberName, SecurityManager.class);
            }
            case "getContextClassLoader": {
                return canBeCalledVirtual(memberName, Thread.class);
            }
            default: {
                return false;
            }
        }
    }
    
    static boolean canBeCalledVirtual(final MemberName memberName, final Class<?> clazz) {
        final Class<?> declaringClass = memberName.getDeclaringClass();
        return declaringClass == clazz || (!memberName.isStatic() && !memberName.isPrivate() && (clazz.isAssignableFrom(declaringClass) || declaringClass.isInterface()));
    }
    
    static {
        registerNatives();
        COUNT_GWT = (getConstant(4) != 0);
        MethodHandleImpl.initStatics();
        for (byte b = 1; b < 10; ++b) {
            assert refKindHasReceiver(b) == ((1 << b & 0x2AA) != 0x0) : b;
        }
        assert verifyConstants();
    }
    
    static class Constants
    {
        static final int GC_COUNT_GWT = 4;
        static final int GC_LAMBDA_SUPPORT = 5;
        static final int MN_IS_METHOD = 65536;
        static final int MN_IS_CONSTRUCTOR = 131072;
        static final int MN_IS_FIELD = 262144;
        static final int MN_IS_TYPE = 524288;
        static final int MN_CALLER_SENSITIVE = 1048576;
        static final int MN_REFERENCE_KIND_SHIFT = 24;
        static final int MN_REFERENCE_KIND_MASK = 15;
        static final int MN_SEARCH_SUPERCLASSES = 1048576;
        static final int MN_SEARCH_INTERFACES = 2097152;
        static final int T_BOOLEAN = 4;
        static final int T_CHAR = 5;
        static final int T_FLOAT = 6;
        static final int T_DOUBLE = 7;
        static final int T_BYTE = 8;
        static final int T_SHORT = 9;
        static final int T_INT = 10;
        static final int T_LONG = 11;
        static final int T_OBJECT = 12;
        static final int T_VOID = 14;
        static final int T_ILLEGAL = 99;
        static final byte CONSTANT_Utf8 = 1;
        static final byte CONSTANT_Integer = 3;
        static final byte CONSTANT_Float = 4;
        static final byte CONSTANT_Long = 5;
        static final byte CONSTANT_Double = 6;
        static final byte CONSTANT_Class = 7;
        static final byte CONSTANT_String = 8;
        static final byte CONSTANT_Fieldref = 9;
        static final byte CONSTANT_Methodref = 10;
        static final byte CONSTANT_InterfaceMethodref = 11;
        static final byte CONSTANT_NameAndType = 12;
        static final byte CONSTANT_MethodHandle = 15;
        static final byte CONSTANT_MethodType = 16;
        static final byte CONSTANT_InvokeDynamic = 18;
        static final byte CONSTANT_LIMIT = 19;
        static final char ACC_PUBLIC = '\u0001';
        static final char ACC_PRIVATE = '\u0002';
        static final char ACC_PROTECTED = '\u0004';
        static final char ACC_STATIC = '\b';
        static final char ACC_FINAL = '\u0010';
        static final char ACC_SYNCHRONIZED = ' ';
        static final char ACC_VOLATILE = '@';
        static final char ACC_TRANSIENT = '\u0080';
        static final char ACC_NATIVE = '\u0100';
        static final char ACC_INTERFACE = '\u0200';
        static final char ACC_ABSTRACT = '\u0400';
        static final char ACC_STRICT = '\u0800';
        static final char ACC_SYNTHETIC = '\u1000';
        static final char ACC_ANNOTATION = '\u2000';
        static final char ACC_ENUM = '\u4000';
        static final char ACC_SUPER = ' ';
        static final char ACC_BRIDGE = '@';
        static final char ACC_VARARGS = '\u0080';
        static final byte REF_NONE = 0;
        static final byte REF_getField = 1;
        static final byte REF_getStatic = 2;
        static final byte REF_putField = 3;
        static final byte REF_putStatic = 4;
        static final byte REF_invokeVirtual = 5;
        static final byte REF_invokeStatic = 6;
        static final byte REF_invokeSpecial = 7;
        static final byte REF_newInvokeSpecial = 8;
        static final byte REF_invokeInterface = 9;
        static final byte REF_LIMIT = 10;
    }
}
