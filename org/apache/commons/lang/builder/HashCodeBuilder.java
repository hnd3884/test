package org.apache.commons.lang.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.AccessibleObject;

public class HashCodeBuilder
{
    private final int iConstant;
    private int iTotal;
    
    public HashCodeBuilder() {
        this.iTotal = 0;
        this.iConstant = 37;
        this.iTotal = 17;
    }
    
    public HashCodeBuilder(final int initialNonZeroOddNumber, final int multiplierNonZeroOddNumber) {
        this.iTotal = 0;
        if (initialNonZeroOddNumber == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires a non zero initial value");
        }
        if (initialNonZeroOddNumber % 2 == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires an odd initial value");
        }
        if (multiplierNonZeroOddNumber == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires a non zero multiplier");
        }
        if (multiplierNonZeroOddNumber % 2 == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires an odd multiplier");
        }
        this.iConstant = multiplierNonZeroOddNumber;
        this.iTotal = initialNonZeroOddNumber;
    }
    
    public static int reflectionHashCode(final Object object) {
        return reflectionHashCode(17, 37, object, false, null);
    }
    
    public static int reflectionHashCode(final Object object, final boolean testTransients) {
        return reflectionHashCode(17, 37, object, testTransients, null);
    }
    
    public static int reflectionHashCode(final int initialNonZeroOddNumber, final int multiplierNonZeroOddNumber, final Object object) {
        return reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, false, null);
    }
    
    public static int reflectionHashCode(final int initialNonZeroOddNumber, final int multiplierNonZeroOddNumber, final Object object, final boolean testTransients) {
        return reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, testTransients, null);
    }
    
    public static int reflectionHashCode(final int initialNonZeroOddNumber, final int multiplierNonZeroOddNumber, final Object object, final boolean testTransients, final Class reflectUpToClass) {
        if (object == null) {
            throw new IllegalArgumentException("The object to build a hash code for must not be null");
        }
        final HashCodeBuilder builder = new HashCodeBuilder(initialNonZeroOddNumber, multiplierNonZeroOddNumber);
        Class clazz = object.getClass();
        reflectionAppend(object, clazz, builder, testTransients);
        while (clazz.getSuperclass() != null && clazz != reflectUpToClass) {
            clazz = clazz.getSuperclass();
            reflectionAppend(object, clazz, builder, testTransients);
        }
        return builder.toHashCode();
    }
    
    private static void reflectionAppend(final Object object, final Class clazz, final HashCodeBuilder builder, final boolean useTransients) {
        final Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (int i = 0; i < fields.length; ++i) {
            final Field f = fields[i];
            if (f.getName().indexOf(36) == -1 && (useTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers())) {
                try {
                    builder.append(f.get(object));
                }
                catch (final IllegalAccessException e) {
                    throw new InternalError("Unexpected IllegalAccessException");
                }
            }
        }
    }
    
    public HashCodeBuilder appendSuper(final int superHashCode) {
        this.iTotal = this.iTotal * this.iConstant + superHashCode;
        return this;
    }
    
    public HashCodeBuilder append(final Object object) {
        if (object == null) {
            this.iTotal *= this.iConstant;
        }
        else if (!object.getClass().isArray()) {
            this.iTotal = this.iTotal * this.iConstant + object.hashCode();
        }
        else if (object instanceof long[]) {
            this.append((long[])object);
        }
        else if (object instanceof int[]) {
            this.append((int[])object);
        }
        else if (object instanceof short[]) {
            this.append((short[])object);
        }
        else if (object instanceof char[]) {
            this.append((char[])object);
        }
        else if (object instanceof byte[]) {
            this.append((byte[])object);
        }
        else if (object instanceof double[]) {
            this.append((double[])object);
        }
        else if (object instanceof float[]) {
            this.append((float[])object);
        }
        else if (object instanceof boolean[]) {
            this.append((boolean[])object);
        }
        else {
            this.append((Object[])object);
        }
        return this;
    }
    
    public HashCodeBuilder append(final long value) {
        this.iTotal = this.iTotal * this.iConstant + (int)(value ^ value >> 32);
        return this;
    }
    
    public HashCodeBuilder append(final int value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final short value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final char value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final byte value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final double value) {
        return this.append(Double.doubleToLongBits(value));
    }
    
    public HashCodeBuilder append(final float value) {
        this.iTotal = this.iTotal * this.iConstant + Float.floatToIntBits(value);
        return this;
    }
    
    public HashCodeBuilder append(final boolean value) {
        this.iTotal = this.iTotal * this.iConstant + (value ? 0 : 1);
        return this;
    }
    
    public HashCodeBuilder append(final Object[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final long[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final int[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final short[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final char[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final byte[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final double[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final float[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final boolean[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (int i = 0; i < array.length; ++i) {
                this.append(array[i]);
            }
        }
        return this;
    }
    
    public int toHashCode() {
        return this.iTotal;
    }
}
