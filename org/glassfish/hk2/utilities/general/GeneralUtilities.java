package org.glassfish.hk2.utilities.general;

import org.glassfish.hk2.utilities.general.internal.WeakHashLRUImpl;
import org.glassfish.hk2.utilities.general.internal.WeakHashClockImpl;
import java.lang.reflect.Array;

public class GeneralUtilities
{
    public static boolean safeEquals(final Object a, final Object b) {
        return a == b || (a != null && b != null && a.equals(b));
    }
    
    private static Class<?> loadArrayClass(final ClassLoader cl, final String aName) {
        Class<?> componentType = null;
        int[] dimensions = null;
        int dot = 0;
        while (componentType == null) {
            final char dotChar = aName.charAt(dot);
            if (dotChar == '[') {
                ++dot;
            }
            else {
                dimensions = new int[dot];
                for (int lcv = 0; lcv < dot; ++lcv) {
                    dimensions[lcv] = 0;
                }
                if (dotChar == 'B') {
                    componentType = Byte.TYPE;
                }
                else if (dotChar == 'I') {
                    componentType = Integer.TYPE;
                }
                else if (dotChar == 'J') {
                    componentType = Long.TYPE;
                }
                else if (dotChar == 'Z') {
                    componentType = Boolean.TYPE;
                }
                else if (dotChar == 'S') {
                    componentType = Short.TYPE;
                }
                else if (dotChar == 'C') {
                    componentType = Character.TYPE;
                }
                else if (dotChar == 'D') {
                    componentType = Double.TYPE;
                }
                else if (dotChar == 'F') {
                    componentType = Float.TYPE;
                }
                else {
                    if (dotChar != 'L') {
                        throw new IllegalArgumentException("Unknown array type " + aName);
                    }
                    if (aName.charAt(aName.length() - 1) != ';') {
                        throw new IllegalArgumentException("Badly formed L array expresion: " + aName);
                    }
                    final String cName = aName.substring(dot + 1, aName.length() - 1);
                    componentType = loadClass(cl, cName);
                    if (componentType == null) {
                        return null;
                    }
                    continue;
                }
            }
        }
        final Object retArray = Array.newInstance(componentType, dimensions);
        return retArray.getClass();
    }
    
    public static Class<?> loadClass(final ClassLoader cl, final String cName) {
        if (cName.startsWith("[")) {
            return loadArrayClass(cl, cName);
        }
        try {
            return cl.loadClass(cName);
        }
        catch (final Throwable th) {
            return null;
        }
    }
    
    public static <K, V> WeakHashClock<K, V> getWeakHashClock(final boolean isWeak) {
        return new WeakHashClockImpl<K, V>(isWeak);
    }
    
    public static <K> WeakHashLRU<K> getWeakHashLRU(final boolean isWeak) {
        return new WeakHashLRUImpl<K>(isWeak);
    }
    
    public static String prettyPrintBytes(final byte[] bytes) {
        final StringBuffer sb = new StringBuffer("Total buffer length: " + bytes.length + "\n");
        int numEntered = 0;
        for (final byte b : bytes) {
            if (numEntered % 16 == 0) {
                if (numEntered != 0) {
                    sb.append("\n");
                }
                final String desc = String.format("%08X ", numEntered);
                sb.append(desc);
            }
            final String singleByte = String.format("%02X ", b);
            sb.append(singleByte);
            if (++numEntered % 8 == 0 && numEntered % 16 != 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
