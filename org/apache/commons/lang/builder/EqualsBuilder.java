package org.apache.commons.lang.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.AccessibleObject;

public class EqualsBuilder
{
    private boolean isEquals;
    
    public EqualsBuilder() {
        this.isEquals = true;
    }
    
    public static boolean reflectionEquals(final Object lhs, final Object rhs) {
        return reflectionEquals(lhs, rhs, false, null);
    }
    
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final boolean testTransients) {
        return reflectionEquals(lhs, rhs, testTransients, null);
    }
    
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final boolean testTransients, final Class reflectUpToClass) {
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        final Class lhsClass = lhs.getClass();
        final Class rhsClass = rhs.getClass();
        if (lhsClass.isInstance(rhs)) {
            Class testClass = lhsClass;
            if (!rhsClass.isInstance(lhs)) {
                testClass = rhsClass;
            }
        }
        else {
            if (!rhsClass.isInstance(lhs)) {
                return false;
            }
            Class testClass = rhsClass;
            if (!lhsClass.isInstance(rhs)) {
                testClass = lhsClass;
            }
        }
        final EqualsBuilder equalsBuilder = new EqualsBuilder();
        try {
            final Class clazz;
            reflectionAppend(lhs, rhs, clazz, equalsBuilder, testTransients);
            while (clazz.getSuperclass() != null && clazz != reflectUpToClass) {
                final Class testClass = clazz.getSuperclass();
                reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients);
            }
        }
        catch (final IllegalArgumentException e) {
            return false;
        }
        return equalsBuilder.isEquals();
    }
    
    private static void reflectionAppend(final Object lhs, final Object rhs, final Class clazz, final EqualsBuilder builder, final boolean useTransients) {
        final Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (int i = 0; i < fields.length && builder.isEquals; ++i) {
            final Field f = fields[i];
            if (f.getName().indexOf(36) == -1 && (useTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers())) {
                try {
                    builder.append(f.get(lhs), f.get(rhs));
                }
                catch (final IllegalAccessException e) {
                    throw new InternalError("Unexpected IllegalAccessException");
                }
            }
        }
    }
    
    public EqualsBuilder appendSuper(final boolean superEquals) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = superEquals;
        return this;
    }
    
    public EqualsBuilder append(final Object lhs, final Object rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        final Class lhsClass = lhs.getClass();
        if (!lhsClass.isArray()) {
            this.isEquals = lhs.equals(rhs);
        }
        else if (lhs instanceof long[]) {
            this.append((long[])lhs, (long[])rhs);
        }
        else if (lhs instanceof int[]) {
            this.append((int[])lhs, (int[])rhs);
        }
        else if (lhs instanceof short[]) {
            this.append((short[])lhs, (short[])rhs);
        }
        else if (lhs instanceof char[]) {
            this.append((char[])lhs, (char[])rhs);
        }
        else if (lhs instanceof byte[]) {
            this.append((byte[])lhs, (byte[])rhs);
        }
        else if (lhs instanceof double[]) {
            this.append((double[])lhs, (double[])rhs);
        }
        else if (lhs instanceof float[]) {
            this.append((float[])lhs, (float[])rhs);
        }
        else if (lhs instanceof boolean[]) {
            this.append((boolean[])lhs, (boolean[])rhs);
        }
        else {
            this.append((Object[])lhs, (Object[])rhs);
        }
        return this;
    }
    
    public EqualsBuilder append(final long lhs, final long rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final int lhs, final int rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final short lhs, final short rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final char lhs, final char rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final byte lhs, final byte rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final double lhs, final double rhs) {
        if (!this.isEquals) {
            return this;
        }
        return this.append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
    }
    
    public EqualsBuilder append(final float lhs, final float rhs) {
        if (!this.isEquals) {
            return this;
        }
        return this.append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
    }
    
    public EqualsBuilder append(final boolean lhs, final boolean rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final Object[] lhs, final Object[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            final Class lhsClass = lhs[i].getClass();
            if (!lhsClass.isInstance(rhs[i])) {
                this.isEquals = false;
                break;
            }
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final long[] lhs, final long[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final int[] lhs, final int[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final short[] lhs, final short[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final char[] lhs, final char[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final byte[] lhs, final byte[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final double[] lhs, final double[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final float[] lhs, final float[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final boolean[] lhs, final boolean[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        if (lhs.length != rhs.length) {
            this.isEquals = false;
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public boolean isEquals() {
        return this.isEquals;
    }
}
