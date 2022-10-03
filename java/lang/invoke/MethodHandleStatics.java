package java.lang.invoke;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class MethodHandleStatics
{
    static final Unsafe UNSAFE;
    static final boolean DEBUG_METHOD_HANDLE_NAMES;
    static final boolean DUMP_CLASS_FILES;
    static final boolean TRACE_INTERPRETER;
    static final boolean TRACE_METHOD_LINKAGE;
    static final int COMPILE_THRESHOLD;
    static final int DONT_INLINE_THRESHOLD;
    static final int PROFILE_LEVEL;
    static final boolean PROFILE_GWT;
    static final int CUSTOMIZE_THRESHOLD;
    
    private MethodHandleStatics() {
    }
    
    static boolean debugEnabled() {
        return MethodHandleStatics.DEBUG_METHOD_HANDLE_NAMES | MethodHandleStatics.DUMP_CLASS_FILES | MethodHandleStatics.TRACE_INTERPRETER | MethodHandleStatics.TRACE_METHOD_LINKAGE;
    }
    
    static String getNameString(final MethodHandle methodHandle, MethodType type) {
        if (type == null) {
            type = methodHandle.type();
        }
        MemberName internalMemberName = null;
        if (methodHandle != null) {
            internalMemberName = methodHandle.internalMemberName();
        }
        if (internalMemberName == null) {
            return "invoke" + type;
        }
        return internalMemberName.getName() + type;
    }
    
    static String getNameString(final MethodHandle methodHandle, final MethodHandle methodHandle2) {
        return getNameString(methodHandle, (methodHandle2 == null) ? ((MethodType)null) : methodHandle2.type());
    }
    
    static String getNameString(final MethodHandle methodHandle) {
        return getNameString(methodHandle, (MethodType)null);
    }
    
    static String addTypeString(final Object o, final MethodHandle methodHandle) {
        String s = String.valueOf(o);
        if (methodHandle == null) {
            return s;
        }
        final int index = s.indexOf(40);
        if (index >= 0) {
            s = s.substring(0, index);
        }
        return s + methodHandle.type();
    }
    
    static InternalError newInternalError(final String s) {
        return new InternalError(s);
    }
    
    static InternalError newInternalError(final String s, final Throwable t) {
        return new InternalError(s, t);
    }
    
    static InternalError newInternalError(final Throwable t) {
        return new InternalError(t);
    }
    
    static RuntimeException newIllegalStateException(final String s) {
        return new IllegalStateException(s);
    }
    
    static RuntimeException newIllegalStateException(final String s, final Object o) {
        return new IllegalStateException(message(s, o));
    }
    
    static RuntimeException newIllegalArgumentException(final String s) {
        return new IllegalArgumentException(s);
    }
    
    static RuntimeException newIllegalArgumentException(final String s, final Object o) {
        return new IllegalArgumentException(message(s, o));
    }
    
    static RuntimeException newIllegalArgumentException(final String s, final Object o, final Object o2) {
        return new IllegalArgumentException(message(s, o, o2));
    }
    
    static Error uncaughtException(final Throwable t) {
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw newInternalError("uncaught exception", t);
    }
    
    static Error NYI() {
        throw new AssertionError((Object)"NYI");
    }
    
    private static String message(String string, final Object o) {
        if (o != null) {
            string = string + ": " + o;
        }
        return string;
    }
    
    private static String message(String string, final Object o, final Object o2) {
        if (o != null || o2 != null) {
            string = string + ": " + o + ", " + o2;
        }
        return string;
    }
    
    static {
        UNSAFE = Unsafe.getUnsafe();
        final Object[] array = new Object[9];
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                array[0] = Boolean.getBoolean("java.lang.invoke.MethodHandle.DEBUG_NAMES");
                array[1] = Boolean.getBoolean("java.lang.invoke.MethodHandle.DUMP_CLASS_FILES");
                array[2] = Boolean.getBoolean("java.lang.invoke.MethodHandle.TRACE_INTERPRETER");
                array[3] = Boolean.getBoolean("java.lang.invoke.MethodHandle.TRACE_METHOD_LINKAGE");
                array[4] = Integer.getInteger("java.lang.invoke.MethodHandle.COMPILE_THRESHOLD", 0);
                array[5] = Integer.getInteger("java.lang.invoke.MethodHandle.DONT_INLINE_THRESHOLD", 30);
                array[6] = Integer.getInteger("java.lang.invoke.MethodHandle.PROFILE_LEVEL", 0);
                array[7] = Boolean.parseBoolean(System.getProperty("java.lang.invoke.MethodHandle.PROFILE_GWT", "true"));
                array[8] = Integer.getInteger("java.lang.invoke.MethodHandle.CUSTOMIZE_THRESHOLD", 127);
                return null;
            }
        });
        DEBUG_METHOD_HANDLE_NAMES = (boolean)array[0];
        DUMP_CLASS_FILES = (boolean)array[1];
        TRACE_INTERPRETER = (boolean)array[2];
        TRACE_METHOD_LINKAGE = (boolean)array[3];
        COMPILE_THRESHOLD = (int)array[4];
        DONT_INLINE_THRESHOLD = (int)array[5];
        PROFILE_LEVEL = (int)array[6];
        PROFILE_GWT = (boolean)array[7];
        CUSTOMIZE_THRESHOLD = (int)array[8];
        if (MethodHandleStatics.CUSTOMIZE_THRESHOLD < -1 || MethodHandleStatics.CUSTOMIZE_THRESHOLD > 127) {
            throw newInternalError("CUSTOMIZE_THRESHOLD should be in [-1...127] range");
        }
    }
}
