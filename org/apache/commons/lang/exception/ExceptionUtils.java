package org.apache.commons.lang.exception;

import java.util.LinkedList;
import java.util.StringTokenizer;
import org.apache.commons.lang.SystemUtils;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import org.apache.commons.lang.ArrayUtils;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import java.lang.reflect.Method;

public class ExceptionUtils
{
    static final String WRAPPED_MARKER = " [wrapped] ";
    private static String[] CAUSE_METHOD_NAMES;
    private static final Method THROWABLE_CAUSE_METHOD;
    
    public static void addCauseMethodName(final String methodName) {
        if (StringUtils.isNotEmpty(methodName)) {
            final List list = new ArrayList(Arrays.asList(ExceptionUtils.CAUSE_METHOD_NAMES));
            list.add(methodName);
            ExceptionUtils.CAUSE_METHOD_NAMES = list.toArray(new String[list.size()]);
        }
    }
    
    public static Throwable getCause(final Throwable throwable) {
        return getCause(throwable, ExceptionUtils.CAUSE_METHOD_NAMES);
    }
    
    public static Throwable getCause(final Throwable throwable, String[] methodNames) {
        if (throwable == null) {
            return null;
        }
        Throwable cause = getCauseUsingWellKnownTypes(throwable);
        if (cause == null) {
            if (methodNames == null) {
                methodNames = ExceptionUtils.CAUSE_METHOD_NAMES;
            }
            for (int i = 0; i < methodNames.length; ++i) {
                final String methodName = methodNames[i];
                if (methodName != null) {
                    cause = getCauseUsingMethodName(throwable, methodName);
                    if (cause != null) {
                        break;
                    }
                }
            }
            if (cause == null) {
                cause = getCauseUsingFieldName(throwable, "detail");
            }
        }
        return cause;
    }
    
    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = getCause(throwable);
        if (cause != null) {
            throwable = cause;
            while ((throwable = getCause(throwable)) != null) {
                cause = throwable;
            }
        }
        return cause;
    }
    
    private static Throwable getCauseUsingWellKnownTypes(final Throwable throwable) {
        if (throwable instanceof Nestable) {
            return ((Nestable)throwable).getCause();
        }
        if (throwable instanceof SQLException) {
            return ((SQLException)throwable).getNextException();
        }
        if (throwable instanceof InvocationTargetException) {
            return ((InvocationTargetException)throwable).getTargetException();
        }
        return null;
    }
    
    private static Throwable getCauseUsingMethodName(final Throwable throwable, final String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName, (Class<?>[])null);
        }
        catch (final NoSuchMethodException ignored) {}
        catch (final SecurityException ex) {}
        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable)method.invoke(throwable, ArrayUtils.EMPTY_OBJECT_ARRAY);
            }
            catch (final IllegalAccessException ignored2) {}
            catch (final IllegalArgumentException ignored3) {}
            catch (final InvocationTargetException ex2) {}
        }
        return null;
    }
    
    private static Throwable getCauseUsingFieldName(final Throwable throwable, final String fieldName) {
        Field field = null;
        try {
            field = throwable.getClass().getField(fieldName);
        }
        catch (final NoSuchFieldException ignored) {}
        catch (final SecurityException ex) {}
        if (field != null && Throwable.class.isAssignableFrom(field.getType())) {
            try {
                return (Throwable)field.get(throwable);
            }
            catch (final IllegalAccessException ignored2) {}
            catch (final IllegalArgumentException ex2) {}
        }
        return null;
    }
    
    public static boolean isThrowableNested() {
        return ExceptionUtils.THROWABLE_CAUSE_METHOD != null;
    }
    
    public static boolean isNestedThrowable(final Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        if (throwable instanceof Nestable) {
            return true;
        }
        if (throwable instanceof SQLException) {
            return true;
        }
        if (throwable instanceof InvocationTargetException) {
            return true;
        }
        if (isThrowableNested()) {
            return true;
        }
        final Class cls = throwable.getClass();
        for (int i = 0, isize = ExceptionUtils.CAUSE_METHOD_NAMES.length; i < isize; ++i) {
            try {
                final Method method = cls.getMethod(ExceptionUtils.CAUSE_METHOD_NAMES[i], (Class[])null);
                if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
                    return true;
                }
            }
            catch (final NoSuchMethodException ignored) {}
            catch (final SecurityException ex) {}
        }
        try {
            final Field field = cls.getField("detail");
            if (field != null) {
                return true;
            }
        }
        catch (final NoSuchFieldException ignored2) {}
        catch (final SecurityException ex2) {}
        return false;
    }
    
    public static int getThrowableCount(Throwable throwable) {
        int count = 0;
        while (throwable != null) {
            ++count;
            throwable = getCause(throwable);
        }
        return count;
    }
    
    public static Throwable[] getThrowables(Throwable throwable) {
        final List list = new ArrayList();
        while (throwable != null) {
            list.add(throwable);
            throwable = getCause(throwable);
        }
        return list.toArray(new Throwable[list.size()]);
    }
    
    public static int indexOfThrowable(final Throwable throwable, final Class type) {
        return indexOfThrowable(throwable, type, 0);
    }
    
    public static int indexOfThrowable(final Throwable throwable, final Class type, int fromIndex) {
        if (throwable == null) {
            return -1;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        final Throwable[] throwables = getThrowables(throwable);
        if (fromIndex >= throwables.length) {
            return -1;
        }
        for (int i = fromIndex; i < throwables.length; ++i) {
            if (throwables[i].getClass().equals(type)) {
                return i;
            }
        }
        return -1;
    }
    
    public static void printRootCauseStackTrace(final Throwable throwable) {
        printRootCauseStackTrace(throwable, System.err);
    }
    
    public static void printRootCauseStackTrace(final Throwable throwable, final PrintStream stream) {
        if (throwable == null) {
            return;
        }
        if (stream == null) {
            throw new IllegalArgumentException("The PrintStream must not be null");
        }
        final String[] trace = getRootCauseStackTrace(throwable);
        for (int i = 0; i < trace.length; ++i) {
            stream.println(trace[i]);
        }
        stream.flush();
    }
    
    public static void printRootCauseStackTrace(final Throwable throwable, final PrintWriter writer) {
        if (throwable == null) {
            return;
        }
        if (writer == null) {
            throw new IllegalArgumentException("The PrintWriter must not be null");
        }
        final String[] trace = getRootCauseStackTrace(throwable);
        for (int i = 0; i < trace.length; ++i) {
            writer.println(trace[i]);
        }
        writer.flush();
    }
    
    public static String[] getRootCauseStackTrace(final Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final Throwable[] throwables = getThrowables(throwable);
        final int count = throwables.length;
        final ArrayList frames = new ArrayList();
        List nextTrace = getStackFrameList(throwables[count - 1]);
        int i = count;
        while (--i >= 0) {
            final List trace = nextTrace;
            if (i != 0) {
                nextTrace = getStackFrameList(throwables[i - 1]);
                removeCommonFrames(trace, nextTrace);
            }
            if (i == count - 1) {
                frames.add(throwables[i].toString());
            }
            else {
                frames.add(" [wrapped] " + throwables[i].toString());
            }
            for (int j = 0; j < trace.size(); ++j) {
                frames.add(trace.get(j));
            }
        }
        return frames.toArray(new String[0]);
    }
    
    public static void removeCommonFrames(final List causeFrames, final List wrapperFrames) {
        if (causeFrames == null || wrapperFrames == null) {
            throw new IllegalArgumentException("The List must not be null");
        }
        for (int causeFrameIndex = causeFrames.size() - 1, wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; --causeFrameIndex, --wrapperFrameIndex) {
            final String causeFrame = causeFrames.get(causeFrameIndex);
            final String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
            if (causeFrame.equals(wrapperFrame)) {
                causeFrames.remove(causeFrameIndex);
            }
        }
    }
    
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    
    public static String getFullStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        final Throwable[] ts = getThrowables(throwable);
        for (int i = 0; i < ts.length; ++i) {
            ts[i].printStackTrace(pw);
            if (isNestedThrowable(ts[i])) {
                break;
            }
        }
        return sw.getBuffer().toString();
    }
    
    public static String[] getStackFrames(final Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return getStackFrames(getStackTrace(throwable));
    }
    
    static String[] getStackFrames(final String stackTrace) {
        final String linebreak = SystemUtils.LINE_SEPARATOR;
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List list = new LinkedList();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return list.toArray(new String[list.size()]);
    }
    
    static List getStackFrameList(final Throwable t) {
        final String stackTrace = getStackTrace(t);
        final String linebreak = SystemUtils.LINE_SEPARATOR;
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List list = new LinkedList();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            final String token = frames.nextToken();
            final int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().length() == 0) {
                traceStarted = true;
                list.add(token);
            }
            else {
                if (traceStarted) {
                    break;
                }
                continue;
            }
        }
        return list;
    }
    
    static {
        ExceptionUtils.CAUSE_METHOD_NAMES = new String[] { "getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested" };
        Method getCauseMethod;
        try {
            getCauseMethod = Throwable.class.getMethod("getCause", (Class[])null);
        }
        catch (final Exception e) {
            getCauseMethod = null;
        }
        THROWABLE_CAUSE_METHOD = getCauseMethod;
    }
}
