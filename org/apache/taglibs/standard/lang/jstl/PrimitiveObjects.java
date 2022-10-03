package org.apache.taglibs.standard.lang.jstl;

class PrimitiveObjects
{
    static int BYTE_LOWER_BOUND;
    static int BYTE_UPPER_BOUND;
    static int CHARACTER_LOWER_BOUND;
    static int CHARACTER_UPPER_BOUND;
    static int SHORT_LOWER_BOUND;
    static int SHORT_UPPER_BOUND;
    static int INTEGER_LOWER_BOUND;
    static int INTEGER_UPPER_BOUND;
    static int LONG_LOWER_BOUND;
    static int LONG_UPPER_BOUND;
    static Byte[] mBytes;
    static Character[] mCharacters;
    static Short[] mShorts;
    static Integer[] mIntegers;
    static Long[] mLongs;
    
    public static Boolean getBoolean(final boolean pValue) {
        return pValue ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public static Byte getByte(final byte pValue) {
        if (pValue >= PrimitiveObjects.BYTE_LOWER_BOUND && pValue <= PrimitiveObjects.BYTE_UPPER_BOUND) {
            return PrimitiveObjects.mBytes[pValue - PrimitiveObjects.BYTE_LOWER_BOUND];
        }
        return new Byte(pValue);
    }
    
    public static Character getCharacter(final char pValue) {
        if (pValue >= PrimitiveObjects.CHARACTER_LOWER_BOUND && pValue <= PrimitiveObjects.CHARACTER_UPPER_BOUND) {
            return PrimitiveObjects.mCharacters[pValue - PrimitiveObjects.CHARACTER_LOWER_BOUND];
        }
        return new Character(pValue);
    }
    
    public static Short getShort(final short pValue) {
        if (pValue >= PrimitiveObjects.SHORT_LOWER_BOUND && pValue <= PrimitiveObjects.SHORT_UPPER_BOUND) {
            return PrimitiveObjects.mShorts[pValue - PrimitiveObjects.SHORT_LOWER_BOUND];
        }
        return new Short(pValue);
    }
    
    public static Integer getInteger(final int pValue) {
        if (pValue >= PrimitiveObjects.INTEGER_LOWER_BOUND && pValue <= PrimitiveObjects.INTEGER_UPPER_BOUND) {
            return PrimitiveObjects.mIntegers[pValue - PrimitiveObjects.INTEGER_LOWER_BOUND];
        }
        return new Integer(pValue);
    }
    
    public static Long getLong(final long pValue) {
        if (pValue >= PrimitiveObjects.LONG_LOWER_BOUND && pValue <= PrimitiveObjects.LONG_UPPER_BOUND) {
            return PrimitiveObjects.mLongs[(int)pValue - PrimitiveObjects.LONG_LOWER_BOUND];
        }
        return new Long(pValue);
    }
    
    public static Float getFloat(final float pValue) {
        return new Float(pValue);
    }
    
    public static Double getDouble(final double pValue) {
        return new Double(pValue);
    }
    
    public static Class getPrimitiveObjectClass(final Class pClass) {
        if (pClass == Boolean.TYPE) {
            return Boolean.class;
        }
        if (pClass == Byte.TYPE) {
            return Byte.class;
        }
        if (pClass == Short.TYPE) {
            return Short.class;
        }
        if (pClass == Character.TYPE) {
            return Character.class;
        }
        if (pClass == Integer.TYPE) {
            return Integer.class;
        }
        if (pClass == Long.TYPE) {
            return Long.class;
        }
        if (pClass == Float.TYPE) {
            return Float.class;
        }
        if (pClass == Double.TYPE) {
            return Double.class;
        }
        return pClass;
    }
    
    static Byte[] createBytes() {
        final int len = PrimitiveObjects.BYTE_UPPER_BOUND - PrimitiveObjects.BYTE_LOWER_BOUND + 1;
        final Byte[] ret = new Byte[len];
        byte val = (byte)PrimitiveObjects.BYTE_LOWER_BOUND;
        for (int i = 0; i < len; ++i, ++val) {
            ret[i] = new Byte(val);
        }
        return ret;
    }
    
    static Character[] createCharacters() {
        final int len = PrimitiveObjects.CHARACTER_UPPER_BOUND - PrimitiveObjects.CHARACTER_LOWER_BOUND + 1;
        final Character[] ret = new Character[len];
        char val = (char)PrimitiveObjects.CHARACTER_LOWER_BOUND;
        for (int i = 0; i < len; ++i, ++val) {
            ret[i] = new Character(val);
        }
        return ret;
    }
    
    static Short[] createShorts() {
        final int len = PrimitiveObjects.SHORT_UPPER_BOUND - PrimitiveObjects.SHORT_LOWER_BOUND + 1;
        final Short[] ret = new Short[len];
        short val = (short)PrimitiveObjects.SHORT_LOWER_BOUND;
        for (int i = 0; i < len; ++i, ++val) {
            ret[i] = new Short(val);
        }
        return ret;
    }
    
    static Integer[] createIntegers() {
        final int len = PrimitiveObjects.INTEGER_UPPER_BOUND - PrimitiveObjects.INTEGER_LOWER_BOUND + 1;
        final Integer[] ret = new Integer[len];
        for (int val = PrimitiveObjects.INTEGER_LOWER_BOUND, i = 0; i < len; ++i, ++val) {
            ret[i] = new Integer(val);
        }
        return ret;
    }
    
    static Long[] createLongs() {
        final int len = PrimitiveObjects.LONG_UPPER_BOUND - PrimitiveObjects.LONG_LOWER_BOUND + 1;
        final Long[] ret = new Long[len];
        long val = PrimitiveObjects.LONG_LOWER_BOUND;
        for (int i = 0; i < len; ++i, ++val) {
            ret[i] = new Long(val);
        }
        return ret;
    }
    
    static {
        PrimitiveObjects.BYTE_LOWER_BOUND = 0;
        PrimitiveObjects.BYTE_UPPER_BOUND = 255;
        PrimitiveObjects.CHARACTER_LOWER_BOUND = 0;
        PrimitiveObjects.CHARACTER_UPPER_BOUND = 255;
        PrimitiveObjects.SHORT_LOWER_BOUND = -1000;
        PrimitiveObjects.SHORT_UPPER_BOUND = 1000;
        PrimitiveObjects.INTEGER_LOWER_BOUND = -1000;
        PrimitiveObjects.INTEGER_UPPER_BOUND = 1000;
        PrimitiveObjects.LONG_LOWER_BOUND = -1000;
        PrimitiveObjects.LONG_UPPER_BOUND = 1000;
        PrimitiveObjects.mBytes = createBytes();
        PrimitiveObjects.mCharacters = createCharacters();
        PrimitiveObjects.mShorts = createShorts();
        PrimitiveObjects.mIntegers = createIntegers();
        PrimitiveObjects.mLongs = createLongs();
    }
}
