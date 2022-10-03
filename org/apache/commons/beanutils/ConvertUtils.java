package org.apache.commons.beanutils;

public class ConvertUtils
{
    @Deprecated
    public static boolean getDefaultBoolean() {
        return ConvertUtilsBean.getInstance().getDefaultBoolean();
    }
    
    @Deprecated
    public static void setDefaultBoolean(final boolean newDefaultBoolean) {
        ConvertUtilsBean.getInstance().setDefaultBoolean(newDefaultBoolean);
    }
    
    @Deprecated
    public static byte getDefaultByte() {
        return ConvertUtilsBean.getInstance().getDefaultByte();
    }
    
    @Deprecated
    public static void setDefaultByte(final byte newDefaultByte) {
        ConvertUtilsBean.getInstance().setDefaultByte(newDefaultByte);
    }
    
    @Deprecated
    public static char getDefaultCharacter() {
        return ConvertUtilsBean.getInstance().getDefaultCharacter();
    }
    
    @Deprecated
    public static void setDefaultCharacter(final char newDefaultCharacter) {
        ConvertUtilsBean.getInstance().setDefaultCharacter(newDefaultCharacter);
    }
    
    @Deprecated
    public static double getDefaultDouble() {
        return ConvertUtilsBean.getInstance().getDefaultDouble();
    }
    
    @Deprecated
    public static void setDefaultDouble(final double newDefaultDouble) {
        ConvertUtilsBean.getInstance().setDefaultDouble(newDefaultDouble);
    }
    
    @Deprecated
    public static float getDefaultFloat() {
        return ConvertUtilsBean.getInstance().getDefaultFloat();
    }
    
    @Deprecated
    public static void setDefaultFloat(final float newDefaultFloat) {
        ConvertUtilsBean.getInstance().setDefaultFloat(newDefaultFloat);
    }
    
    @Deprecated
    public static int getDefaultInteger() {
        return ConvertUtilsBean.getInstance().getDefaultInteger();
    }
    
    @Deprecated
    public static void setDefaultInteger(final int newDefaultInteger) {
        ConvertUtilsBean.getInstance().setDefaultInteger(newDefaultInteger);
    }
    
    @Deprecated
    public static long getDefaultLong() {
        return ConvertUtilsBean.getInstance().getDefaultLong();
    }
    
    @Deprecated
    public static void setDefaultLong(final long newDefaultLong) {
        ConvertUtilsBean.getInstance().setDefaultLong(newDefaultLong);
    }
    
    @Deprecated
    public static short getDefaultShort() {
        return ConvertUtilsBean.getInstance().getDefaultShort();
    }
    
    @Deprecated
    public static void setDefaultShort(final short newDefaultShort) {
        ConvertUtilsBean.getInstance().setDefaultShort(newDefaultShort);
    }
    
    public static String convert(final Object value) {
        return ConvertUtilsBean.getInstance().convert(value);
    }
    
    public static Object convert(final String value, final Class<?> clazz) {
        return ConvertUtilsBean.getInstance().convert(value, clazz);
    }
    
    public static Object convert(final String[] values, final Class<?> clazz) {
        return ConvertUtilsBean.getInstance().convert(values, clazz);
    }
    
    public static Object convert(final Object value, final Class<?> targetType) {
        return ConvertUtilsBean.getInstance().convert(value, targetType);
    }
    
    public static void deregister() {
        ConvertUtilsBean.getInstance().deregister();
    }
    
    public static void deregister(final Class<?> clazz) {
        ConvertUtilsBean.getInstance().deregister(clazz);
    }
    
    public static Converter lookup(final Class<?> clazz) {
        return ConvertUtilsBean.getInstance().lookup(clazz);
    }
    
    public static Converter lookup(final Class<?> sourceType, final Class<?> targetType) {
        return ConvertUtilsBean.getInstance().lookup(sourceType, targetType);
    }
    
    public static void register(final Converter converter, final Class<?> clazz) {
        ConvertUtilsBean.getInstance().register(converter, clazz);
    }
    
    public static <T> Class<T> primitiveToWrapper(final Class<T> type) {
        if (type == null || !type.isPrimitive()) {
            return type;
        }
        if (type == Integer.TYPE) {
            return (Class<T>)Integer.class;
        }
        if (type == Double.TYPE) {
            return (Class<T>)Double.class;
        }
        if (type == Long.TYPE) {
            return (Class<T>)Long.class;
        }
        if (type == Boolean.TYPE) {
            return (Class<T>)Boolean.class;
        }
        if (type == Float.TYPE) {
            return (Class<T>)Float.class;
        }
        if (type == Short.TYPE) {
            return (Class<T>)Short.class;
        }
        if (type == Byte.TYPE) {
            return (Class<T>)Byte.class;
        }
        if (type == Character.TYPE) {
            return (Class<T>)Character.class;
        }
        return type;
    }
}
