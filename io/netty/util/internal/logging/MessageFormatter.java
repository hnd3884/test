package io.netty.util.internal.logging;

import java.util.HashSet;
import java.util.Set;

final class MessageFormatter
{
    private static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';
    
    static FormattingTuple format(final String messagePattern, final Object arg) {
        return arrayFormat(messagePattern, new Object[] { arg });
    }
    
    static FormattingTuple format(final String messagePattern, final Object argA, final Object argB) {
        return arrayFormat(messagePattern, new Object[] { argA, argB });
    }
    
    static FormattingTuple arrayFormat(final String messagePattern, final Object[] argArray) {
        if (argArray == null || argArray.length == 0) {
            return new FormattingTuple(messagePattern, null);
        }
        final int lastArrIdx = argArray.length - 1;
        final Object lastEntry = argArray[lastArrIdx];
        final Throwable throwable = (lastEntry instanceof Throwable) ? ((Throwable)lastEntry) : null;
        if (messagePattern == null) {
            return new FormattingTuple(null, throwable);
        }
        int j = messagePattern.indexOf("{}");
        if (j == -1) {
            return new FormattingTuple(messagePattern, throwable);
        }
        final StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int i = 0;
        int L = 0;
        do {
            boolean notEscaped = j == 0 || messagePattern.charAt(j - 1) != '\\';
            if (notEscaped) {
                sbuf.append(messagePattern, i, j);
            }
            else {
                sbuf.append(messagePattern, i, j - 1);
                notEscaped = (j >= 2 && messagePattern.charAt(j - 2) == '\\');
            }
            i = j + 2;
            if (notEscaped) {
                deeplyAppendParameter(sbuf, argArray[L], null);
                if (++L > lastArrIdx) {
                    break;
                }
            }
            else {
                sbuf.append("{}");
            }
            j = messagePattern.indexOf("{}", i);
        } while (j != -1);
        sbuf.append(messagePattern, i, messagePattern.length());
        return new FormattingTuple(sbuf.toString(), (L <= lastArrIdx) ? throwable : null);
    }
    
    private static void deeplyAppendParameter(final StringBuilder sbuf, final Object o, final Set<Object[]> seenSet) {
        if (o == null) {
            sbuf.append("null");
            return;
        }
        final Class<?> objClass = o.getClass();
        if (!objClass.isArray()) {
            if (Number.class.isAssignableFrom(objClass)) {
                if (objClass == Long.class) {
                    sbuf.append((long)o);
                }
                else if (objClass == Integer.class || objClass == Short.class || objClass == Byte.class) {
                    sbuf.append(((Number)o).intValue());
                }
                else if (objClass == Double.class) {
                    sbuf.append((double)o);
                }
                else if (objClass == Float.class) {
                    sbuf.append((float)o);
                }
                else {
                    safeObjectAppend(sbuf, o);
                }
            }
            else {
                safeObjectAppend(sbuf, o);
            }
        }
        else {
            sbuf.append('[');
            if (objClass == boolean[].class) {
                booleanArrayAppend(sbuf, (boolean[])o);
            }
            else if (objClass == byte[].class) {
                byteArrayAppend(sbuf, (byte[])o);
            }
            else if (objClass == char[].class) {
                charArrayAppend(sbuf, (char[])o);
            }
            else if (objClass == short[].class) {
                shortArrayAppend(sbuf, (short[])o);
            }
            else if (objClass == int[].class) {
                intArrayAppend(sbuf, (int[])o);
            }
            else if (objClass == long[].class) {
                longArrayAppend(sbuf, (long[])o);
            }
            else if (objClass == float[].class) {
                floatArrayAppend(sbuf, (float[])o);
            }
            else if (objClass == double[].class) {
                doubleArrayAppend(sbuf, (double[])o);
            }
            else {
                objectArrayAppend(sbuf, (Object[])o, seenSet);
            }
            sbuf.append(']');
        }
    }
    
    private static void safeObjectAppend(final StringBuilder sbuf, final Object o) {
        try {
            final String oAsString = o.toString();
            sbuf.append(oAsString);
        }
        catch (final Throwable t) {
            System.err.println("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + ']');
            t.printStackTrace();
            sbuf.append("[FAILED toString()]");
        }
    }
    
    private static void objectArrayAppend(final StringBuilder sbuf, final Object[] a, Set<Object[]> seenSet) {
        if (a.length == 0) {
            return;
        }
        if (seenSet == null) {
            seenSet = new HashSet<Object[]>(a.length);
        }
        if (seenSet.add(a)) {
            deeplyAppendParameter(sbuf, a[0], seenSet);
            for (int i = 1; i < a.length; ++i) {
                sbuf.append(", ");
                deeplyAppendParameter(sbuf, a[i], seenSet);
            }
            seenSet.remove(a);
        }
        else {
            sbuf.append("...");
        }
    }
    
    private static void booleanArrayAppend(final StringBuilder sbuf, final boolean[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }
    
    private static void byteArrayAppend(final StringBuilder sbuf, final byte[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }
    
    private static void charArrayAppend(final StringBuilder sbuf, final char[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }
    
    private static void shortArrayAppend(final StringBuilder sbuf, final short[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }
    
    private static void intArrayAppend(final StringBuilder sbuf, final int[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }
    
    private static void longArrayAppend(final StringBuilder sbuf, final long[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }
    
    private static void floatArrayAppend(final StringBuilder sbuf, final float[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }
    
    private static void doubleArrayAppend(final StringBuilder sbuf, final double[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }
    
    private MessageFormatter() {
    }
}
